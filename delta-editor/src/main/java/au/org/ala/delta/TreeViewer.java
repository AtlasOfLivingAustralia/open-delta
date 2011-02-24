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
package au.org.ala.delta;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JInternalFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.StateValue;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;

public class TreeViewer extends JInternalFrame implements IContextHolder {

	private static final long serialVersionUID = 1L;

	private DeltaContext _context;
	private StateEditor _stateEditor;

	public TreeViewer(DeltaContext context) {
		super("Tree Viewer - " + context.getVariable("HEADING", ""));
		this.setSize(new Dimension(500, 400));

		_context = context;

		final JList lst = new JList();
		lst.setModel(new ItemListModel(_context));
		lst.setDragEnabled(true);
		lst.setDropMode(DropMode.ON);

		final JTree tree = new JTree();
		final CharacterTreeModel treeModel = new CharacterTreeModel(_context);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setModel(treeModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new DeltaTreeCellRenderer(_context));
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				if (node instanceof CharacterTreeNode) {
					_context.selectedCharacter = ((CharacterTreeNode) node).getCharacter();
				} else if (node.getParent() instanceof CharacterTreeNode) {
					_context.selectedCharacter = ((CharacterTreeNode) node.getParent()).getCharacter();
				} else {
					_context.selectedCharacter = null;
				}
				_stateEditor.bind(_context.selectedCharacter, _context.selectedItem);
			}
		});

		lst.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				_context.selectedItem = ((ItemViewModel) lst.getSelectedValue()).getItem();
				tree.updateUI();
				_stateEditor.bind(_context.selectedCharacter, _context.selectedItem);
			}
		});

		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		content.setDividerSize(4);
		content.setDividerLocation(180);

		content.setRightComponent(new JScrollPane(tree));
		content.setLeftComponent(new JScrollPane(lst));
		
		_stateEditor = new StateEditor(context);
		
		JSplitPane divider =new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);
		
		divider.setTopComponent(content);
		divider.setBottomComponent(_stateEditor);

		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(divider);

	}

	@Override
	public DeltaContext getContext() {
		return _context;
	}

}

class ItemListModel extends DefaultListModel implements ListModel {

	private static final long serialVersionUID = 1L;

	private DeltaContext _context;

	public ItemListModel(DeltaContext context) {
		_context = context;
	}

	@Override
	public int getSize() {
		return _context.getMaximumNumberOfItems();
	}

	@Override
	public Object getElementAt(int index) {
		return new ItemViewModel(_context.getItem(index + 1));
	}

}

class CharacterTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	public CharacterTreeModel(DeltaContext context) {
		super(new ContextRootNode(context), false);
	}

}

class ContextRootNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private DeltaContext _context;

	public ContextRootNode(DeltaContext context) {
		_context = context;
		for (int i = 0; i < _context.getNumberOfCharacters(); ++i) {
			au.org.ala.delta.model.Character ch = _context.getCharacter(i + 1);
			add(new CharacterTreeNode(context, ch));
		}

	}

}

class CharStateHolder {

	private DeltaContext _context;
	private Character _character;

	public CharStateHolder(DeltaContext context, Character character) {
		_context = context;
		_character = character;
	}

	@Override
	public String toString() {
		if (_context.selectedItem != null) {
			StateValue stateval = _context.getMatrix().getValue(_character.getCharacterId(), _context.selectedItem.getItemId());
			return String.format("%s", stateval == null ? "No value" : stateval.getValue());
		} else {
			return "---";
		}
	}

}

class ItemViewModel {

	private Item _model;

	public ItemViewModel(Item item) {
		_model = item;
	}

	@Override
	public String toString() {
		return _model.getDescription();
	}

	public Item getItem() {
		return _model;
	}
}

class DeltaTreeCellRenderer extends DefaultTreeCellRenderer {
	
	private static final long serialVersionUID = 1L;
	
	private DeltaContext _context;
	private ImageIcon _textIcon;
	private ImageIcon _realIcon;
	private ImageIcon _intIcon;
	private ImageIcon _omIcon;
	private ImageIcon _umIcon;
	
	
	public DeltaTreeCellRenderer(DeltaContext context) {
		_context = context;
		_textIcon = createImageIcon("/icons/textchar.png");
		_realIcon = createImageIcon("/icons/realchar.png");
		_intIcon = createImageIcon("/icons/intchar.png");
		_omIcon = createImageIcon("/icons/omchar.png");
		_umIcon = createImageIcon("/icons/umchar.png");
	}
	
	 /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path) {
        java.net.URL imgURL = TreeViewer.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }	
    
    public DeltaContext getContext() {
    	return _context;
    }

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof CharacterTreeNode) {
			Character ch = (Character) ((CharacterTreeNode) value).getUserObject();
			if (ch instanceof TextCharacter) {
				setIcon(_textIcon);
			} else if (ch instanceof RealCharacter) {
				setIcon(_realIcon);			
			} else if (ch instanceof IntegerCharacter) {
				setIcon(_intIcon);				
			} else if (ch instanceof OrderedMultiStateCharacter) {
				setIcon(_omIcon);
			} else if (ch instanceof UnorderedMultiStateCharacter) {
				setIcon(_umIcon);
			}
			
		}  
		return this;
	}
}

class CharacterTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private Character _character;
	private DeltaContext _context;

	public CharacterTreeNode(DeltaContext context, Character ch) {
		super(ch);
		_context = context;
		_character = ch;
		if (_character instanceof MultiStateCharacter) {
			MultiStateCharacter ms = (MultiStateCharacter) _character;
			for (int i = 0; i < ms.getNumberOfStates(); ++i) {
				add(new DefaultMutableTreeNode(ms.getState(i + 1)));
			}
		} else {
			add(new DefaultMutableTreeNode(new CharStateHolder(_context, ch)));
		}
	}
	
	public Character getCharacter() {
		return _character;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}

}
