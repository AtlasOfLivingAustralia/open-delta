/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.editor.ui;

import au.org.ala.delta.editor.ui.dnd.SimpleTransferHandler;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.ui.GenericSearchController;
import au.org.ala.delta.ui.GenericSearchPredicate;
import au.org.ala.delta.ui.SearchDialog;
import au.org.ala.delta.ui.SearchOptions;
import au.org.ala.delta.util.SearchableModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.jdesktop.application.Application;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.Position;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * The CharacterTree extends JTree to provide 1-click cell editing and to implement the ReorderableList interface to help our Drag and Drop implementation.
 * 
 * A CharacterTree can behave as both a list of Characters and a list of States depending on the selection and what is being dragged and dropped.
 */
public class CharacterTree extends JTree {

	private static final long serialVersionUID = 1462521823171738637L;

    /** Used as the Key in the inputMap and actionMap for the selection action */
    private static final String SELECTION_ACTION_KEY = "SelectionAction";

    private boolean _doubleProcessingMouseEvent = false;

	/** Handles drag and drop of states */
	private TransferHandler _stateTransferHandler;

	/** Handles drag and drop of characters */
	private TransferHandler _characterTransferHandler;

	private ReorderableList _characterListBehaviour;

	private ReorderableList _stateListBehaviour;

	private class StateListBehaviour implements ReorderableList {

		private int _selectedCharacter;

		@Override
		public int getSelectedIndex() {
			TreePath selectedPath = getSelectionPath();
			// We record the selected character to help us when dragging
			// states.
			_selectedCharacter = pathToCharacterNumber(selectedPath);

			if (selectedPath.getPathCount() >= 3) {

                Object node = selectedPath.getPathComponent(2);
                if (node instanceof MultistateStateNode) {
                    int stateNum = ((MultistateStateNode) selectedPath.getPathComponent(2)).getStateNo();
                    return stateNum - 1;
                }
				return 0;
			}

			return -1;
		}

		@Override
		public void setSelectedIndex(int index) {

		}

		@Override
		public int getDropLocationIndex(javax.swing.TransferHandler.DropLocation dropLocation) {
			if (dropLocation == null) {
				return -1;
			}

			return dropLocationToStateNumber((DropLocation) dropLocation) - 1;
		}

		private int dropLocationToStateNumber(DropLocation location) {
			TreePath path = location.getPath();
			if (path.getPathCount() == 2 && pathToCharacterNumber(path) == _selectedCharacter) {

				if (location.getChildIndex() >= 0) {
					return location.getChildIndex() + 1;
				}
			} else if (path.getPathCount() == 1 && location.getChildIndex() == _selectedCharacter) {
				// get character number. move to last state position.
				TreeNode node = (TreeNode) getModel().getChild(path.getLastPathComponent(), _selectedCharacter - 1);
				return node.getChildCount() + 1;
			}

			return -1;
		}

		@Override
		public void setSelectionAction(Action action) {
		    getActionMap().put(SELECTION_ACTION_KEY, action);
	    }

        @Override
        public JComponent getListViewComponent() {
            return CharacterTree.this;
        }
    }

	private class CharacterListBehaviour implements ReorderableList {
		@Override
		public int getSelectedIndex() {
			TreePath selectedPath = getSelectionPath();
			return pathToCharacterNumber(selectedPath) - 1;
		}

		@Override
		public void setSelectedIndex(int index) {
			Object[] nodes = new Object[2];
			DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
			nodes[0] = root;
			nodes[1] = root.getChildAt(index);
			TreePath path = new TreePath(nodes);

			setSelectionPath(path);
		}

		@Override
		public int getDropLocationIndex(javax.swing.TransferHandler.DropLocation dropLocation) {
			if (dropLocation == null) {
				return -1;
			}
			return dropLocationToCharacterNumber((DropLocation) dropLocation) - 1;
		}

		/**
		 * Turns a DropLocation into the character number of the character at the location.
		 * 
		 * @param location
		 *            the current drop location.
		 * @return the character number representing the drop position.
		 */
		private int dropLocationToCharacterNumber(DropLocation location) {
			TreePath path = location.getPath();

			if (path.getPathCount() == 2) {
				return pathToCharacterNumber(path);
			} else {
				return location.getChildIndex() + 1;
			}
		}

		@Override
		public void setSelectionAction(Action action) {
            getActionMap().put(SELECTION_ACTION_KEY, action);
        }

        @Override
        public JComponent getListViewComponent() {
            return CharacterTree.this;
        }
	}

	public CharacterTree() {

		setToggleClickCount(0);
		_characterListBehaviour = new CharacterListBehaviour();
		_stateListBehaviour = new StateListBehaviour();

		ToolTipManager.sharedInstance().registerComponent(this);

		addMouseListener(new MouseAdapter() {

			public void mousePressed(MouseEvent e) {
				if (_doubleProcessingMouseEvent) {
					return;
				}
				if (!isEditing()) {
					int selectedRow = getClosestRowForLocation(e.getX(), e.getY());

					if ((selectedRow >= 0) && (e.getClickCount() == 2) && SwingUtilities.isLeftMouseButton(e)) {
						Action action = getActionMap().get(SELECTION_ACTION_KEY);
						if (action != null) {
							action.actionPerformed(new ActionEvent(this, -1, ""));
						}
					}
				}
			}
		});

		addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {

				TreePath selection = e.getNewLeadSelectionPath();
				if (selection != null) {
					Object lastComponent = selection.getLastPathComponent();
					if (lastComponent instanceof DefaultMutableTreeNode) {
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastComponent;

						if (node.isLeaf()) {
							CharacterTree.super.setTransferHandler(_stateTransferHandler);
						} else {
							CharacterTree.super.setTransferHandler(_characterTransferHandler);
						}
					}
				}
			}
		});

		ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

		javax.swing.Action find = actionMap.get("find");
		if (find != null) {
			getActionMap().put("find", find);
		}

		javax.swing.Action findNext = actionMap.get("findNext");
		if (findNext != null) {
			getActionMap().put("findNext", findNext);
		}

        getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, KeyEvent.CTRL_MASK), SELECTION_ACTION_KEY);

	}

    /**
     * Overriding the default getNextMatch method to disable list item selection based on first character matching.
     * @param prefix unused.
     * @param startIndex unused.
     * @param bias unused.
     * @return always returns -1.
     */
    @Override
    public TreePath getNextMatch(String prefix, int startIndex, Position.Bias bias) {
        return null;
    }
	
	@Override
	public String getToolTipText(MouseEvent event) {
		TreePath path = getClosestPathForLocation(event.getX(), event.getY());
		if (path != null) {
			Object node = path.getLastPathComponent();
			if (node instanceof CharacterTreeNode) {
				CharacterTreeNode chNode = (CharacterTreeNode) node;
				String desc = chNode.getCharacter().getDescription();				
				int width = getGraphics().getFontMetrics(getFont()).stringWidth(desc);
				if (width > getParent().getWidth() - 70) {					
					if (width > 400) {
						return String.format("<html><body style=\"width: %dpx;\">%s</body></html>", 400, StringEscapeUtils.escapeHtml(desc));						
					} else {
						return desc;
					}				
				}
			}
		}
		return null;		
	}

	private SearchDialog _search;

	@org.jdesktop.application.Action
	public void find() {
		if (_search == null) {
			_search = new SearchDialog(new CharacterSearchController());
		}
		_search.setVisible(true);
	}

	@org.jdesktop.application.Action
	public void findNext() {
		if (_search == null) {
			find();
			return;
		}

		_search.findNext();
	}

	/**
	 * This is a done to initiate a cell edit from a single click with drag and drop enabled.
	 */
	protected void processMouseEvent(MouseEvent e) {

		super.processMouseEvent(e);
		_doubleProcessingMouseEvent = false;
		TreePath selectedPath = getSelectionPath();
		if (selectedPath != null) {
			Object lastComponent = selectedPath.getLastPathComponent();
			if (lastComponent instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) lastComponent;

				if (node.isLeaf()) {
					int row = getRowForPath(selectedPath);
					Rectangle bounds = getRowBounds(row);
					int pos = e.getX() - bounds.x;
					if (pos >= 0 && pos < 20) {
						_doubleProcessingMouseEvent = true;
						super.processMouseEvent(e);
					}
				}
			}
		}
	}

    public ReorderableList getCharacterListView() {
        return _characterListBehaviour;
    }

    public ReorderableList getStateListView() {
        return _stateListBehaviour;
    }

	/**
	 * Returns the character number of the Character at the supplied TreePath.
	 * 
	 * @param path the TreePath of interest.
	 * @return the character number representing the drop position.
	 */
	private int pathToCharacterNumber(TreePath path) {
		if (path != null) {
			Object[] nodes = path.getPath();
			if (nodes[1] instanceof CharacterTreeNode) {
				return ((CharacterTreeNode) nodes[1]).getCharacter().getCharacterId();
			}
		}
		return -1;
	}

	public void setCharacterTransferHandler(TransferHandler handler) {
		_characterTransferHandler = handler;
	}

	public void setStateTransferHandler(TransferHandler handler) {
		_stateTransferHandler = handler;
	}

	@Override
	public void setTransferHandler(TransferHandler handler) {
		if (handler instanceof SimpleTransferHandler<?>) {
			Class<?> transferClass = ((SimpleTransferHandler<?>) handler).getTransferClass();
			if (au.org.ala.delta.model.Character.class.equals(transferClass)) {
				setCharacterTransferHandler(handler);
			} else if (Integer.class.equals(transferClass)) {
				setStateTransferHandler(handler);
			} else {
				super.setTransferHandler(handler);
			}
		}
	}

	class CharacterSearchController extends GenericSearchController<Character> {

		public CharacterSearchController() {
			super("findCharacter.title");
		}

		@Override
		public JComponent getOwningComponent() {
			return CharacterTree.this;
		}

		@Override
		protected void selectItem(Character character) {
			int index = character.getCharacterId() - 1;
			_characterListBehaviour.setSelectedIndex(index);
			TreePath path = getPathForRow(index);
			makeVisible(path);
			scrollPathToVisible(path);
		}

		@Override
		protected void clearSelection() {
			CharacterTree.this.clearSelection();
		}

		@Override
		protected SearchableModel<Character> getSearchableModel() {
			return (CharacterTreeModel) getModel();
		}

		@Override
		protected int getSelectedIndex() {
			return _characterListBehaviour.getSelectedIndex();
		}

		@Override
		protected int getIndexOf(Character item) {
			return item.getCharacterId() - 1;
		}

		@Override
		protected GenericSearchPredicate<Character> createPredicate(SearchOptions options) {
			return new SearchCharacterPredicate(options);
		}

	}

	class SearchCharacterPredicate extends GenericSearchPredicate<Character> {

		public SearchCharacterPredicate(SearchOptions options) {
			super(options);
		}

		@Override
		public boolean test(Character character) {
			String desc = character.getDescription();
			if (!getOptions().isCaseSensitive()) {
				return desc.toLowerCase().contains(getTerm());
			}

			return desc.contains(getTerm());
		}

	}

	public void expandAll() {

		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			for (int i = 0; i < getRowCount(); i++) {
				expandRow(i);
			}
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	public void collapseAll() {
		try {
			setCursor(new Cursor(Cursor.WAIT_CURSOR));
			for (int i = 0; i < getRowCount(); i++) {
				collapseRow(i);
			}
		} finally {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

}
