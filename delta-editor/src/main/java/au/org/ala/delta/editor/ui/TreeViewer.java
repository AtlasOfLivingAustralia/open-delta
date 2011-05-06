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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.DeltaView;
import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.ItemController;
import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.editor.ui.validator.AttributeValidator;
import au.org.ala.delta.editor.ui.validator.TextComponentValidator;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.ui.AboutBox;

public class TreeViewer extends JInternalFrame implements DeltaView {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;
	private AttributeEditor _stateEditor; 
	private JTree _tree;
	private ItemList _itemList;
	

	@Resource
	String windowTitle;

	public TreeViewer(EditorDataModel dataModel) {
		super();

		setName(dataModel.getShortName()+ "-tree");
		ApplicationContext context = Application.getInstance().getContext();
		ResourceMap resourceMap = context.getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);

		this.setSize(new Dimension(800, 500));

		_dataModel = dataModel;

		_itemList = new ItemList(_dataModel);
		_itemList.setDragEnabled(true);
		_itemList.setDropMode(DropMode.INSERT);
		final ActionMap actionMap = context.getActionMap();

		_tree = new JTree();
		final CharacterTreeModel treeModel = new CharacterTreeModel(_dataModel);
		_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		_tree.setModel(treeModel);
		_tree.setRootVisible(false);
		_tree.setEditable(true);
		_tree.setShowsRootHandles(true);
		DeltaTreeCellRenderer renderer = new DeltaTreeCellRenderer(_dataModel);
		_tree.setCellRenderer(renderer);
		_tree.setCellEditor(new DeltaTreeEditor(_tree, renderer));
		_tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) _tree.getLastSelectedPathComponent();

				if (node == null) {
					return;
				}

				if (node instanceof CharacterTreeNode) {
					_dataModel.setSelectedCharacter(((CharacterTreeNode) node).getCharacter());
				} else if (node.getParent() instanceof CharacterTreeNode) {
					_dataModel.setSelectedCharacter(((CharacterTreeNode) node.getParent()).getCharacter());
				} else {
					_dataModel.setSelectedCharacter(null);
				}
				_stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
			}
		});
		_tree.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selectedRow = _tree.getClosestRowForLocation(e.getX(), e.getY());
				if ((selectedRow >= 0) && (e.getClickCount() == 2)) {
					actionMap.get("viewCharacterEditor").actionPerformed(new ActionEvent(_tree, -1, ""));
				}
			}
		});

		_itemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				_dataModel.setSelectedItem(_itemList.getSelectedItem());
				
				treeModel.itemChanged();
				_tree.repaint();
				_stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
			}
		});
		
		new ItemController(_itemList, _dataModel);
		
		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		content.setDividerSize(4);
		content.setDividerLocation(180);

		content.setRightComponent(new JScrollPane(_tree));
		content.setLeftComponent(new JScrollPane(_itemList));

		_stateEditor = new AttributeEditor(_dataModel);

		JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);

		divider.setTopComponent(content);
		divider.setBottomComponent(_stateEditor);
		
		_stateEditor.add(new AttributeEditorListener() {					
			@Override
			public void advance() {
				updateSelection(1);
			}
			@Override
			public void reverse() {
				updateSelection(-1);
			}
		});

		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(divider);
		
		_itemList.setSelectedIndex(0);
		_tree.setSelectionRow(0);
		
		_dataModel.addDeltaDataSetObserver(new NewCharacterListener(_tree));

	}
	
	private void updateSelection(int increment) {
			switch (EditorPreferences.getEditorAdvanceMode()) {
				case Character:
					// find the next character in the tree and select it...
					int nextCharRow = findCharacterRow(increment);
					_tree.setSelectionRow(nextCharRow);
					_tree.scrollRowToVisible(nextCharRow);										
					break;
				case Item:
					int candidateIndex = _itemList.getSelectedIndex() + increment;
					if (candidateIndex >= 0 && candidateIndex < _itemList.getModel().getSize()) {
						_itemList.setSelectedIndex(candidateIndex);
						_itemList.ensureIndexIsVisible(candidateIndex);
					}
					break;
			}
	}
	
	private int findCharacterRow(int searchIncrement) {
		if (_tree.getSelectionCount() > 0) {  
			int current = _tree.getSelectionRows()[0];
			int row = current;
			row += searchIncrement;
			while (row >= 0 && row < _tree.getRowCount()) {
				Object obj = _tree.getPathForRow(row).getLastPathComponent();
				if (obj instanceof CharacterTreeNode) {
					return row;
				}
				row += searchIncrement;
			}
			return current;
		}
		
		return 0;
	}
	
	@Override
	public void open() {}

	@Override
	public void close() {}

	@Override
	public String getViewTitle() {
		return windowTitle;
	}
	
	class NewCharacterListener extends AbstractDataSetObserver {

		private JTree tree;
		public NewCharacterListener(JTree tree) {
			this.tree = tree;
		}
		@Override
		public void characterAdded(DeltaDataSetChangeEvent event) {
			updateTree();
		}
		@Override
		public void characterEdited(DeltaDataSetChangeEvent event) {
			updateTree();
		}
		
		private void updateTree() {
			// This is a bit lazy and will probably need to be fixed when we can do edit's directly
			// on the tree.
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					tree.setModel(new CharacterTreeModel(_dataModel));
				}
			});
		}
	}
	
	/**
	 * Provides the mechanism for editing multistate attributes.
	 */
	class MultiStateAttributeCellEditor extends AttributeCellEditor {
		
		private static final long serialVersionUID = 2155436614741771060L;
		
		public MultiStateAttributeCellEditor() {
			super(new JCheckBox());
			
			editorComponent.setOpaque(false);
		}
		@Override
		protected Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode node) {
			MultistateStateNode nodeValue = (MultistateStateNode)node;
			
			MultiStateCharacter character = (MultiStateCharacter)attribute.getCharacter();
			int stateNum = nodeValue.getStateNo();
			getEditingCheckBox().setText(character.getState(stateNum));
			getEditingCheckBox().setSelected(attribute.isPresent(stateNum));
			return editorComponent;
		}	
		
		private JCheckBox getEditingCheckBox() {
			return (JCheckBox)editorComponent;
		}
	}
	
	/**
	 * Provides the mechanism for editing numeric or text attributes.
	 */
	class NumericTextAttributeCellEditor extends AttributeCellEditor implements ValidationListener {
		
		private static final long serialVersionUID = -6054961593128043309L;

		private boolean _valid;
		private TextComponentValidator _validator;
		
		public NumericTextAttributeCellEditor() {
			super(new JTextField());	
			_valid = true;
			
		}

		@Override
		protected Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode nodeUserObject) {
			getTextField().setText(attribute.getValue());
			_validator = new TextComponentValidator(new AttributeValidator(_dataModel, attribute.getCharacter()), this);
			//getTextField().setInputVerifier(_validator);
			_valid = true;
			return editorComponent;
		}
		
		private JTextField getTextField() {
			return (JTextField)editorComponent;
		}

		@Override
		public void validationSuceeded(ValidationResult results) {
			_valid = true;
			
		}

		@Override
		public void validationFailed(ValidationResult results) {
			_valid = false;
		}

		@Override
		public boolean stopCellEditing() {
			_validator.verify(getTextField());
			if (_valid) {
				fireEditingStopped();
			}
			return _valid;
		}
		
		
	}
	
	/**
	 * Provides support for editors by determining which node has been selected and whether it 
	 * may be edited
	 */
	abstract class AttributeCellEditor extends DefaultCellEditor {
		
		private static final long serialVersionUID = -792901638653087259L;

		public AttributeCellEditor(JTextField textField) {
			super(textField);
		}
		
		public AttributeCellEditor(JCheckBox checkBox) {
			super(checkBox);
		}
		
		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			
			if (leaf) {
				Character ch = getCharacterFor(node);
				if (ch != null) {
					Attribute attribute = _dataModel.getSelectedItem().getAttribute(ch);
					return configureEditingComponent(attribute, node);
				}
			}
			
			return null;
		}
		
		private Character getCharacterFor(DefaultMutableTreeNode node) {
			Character ch = null;
			if (node.getParent() instanceof CharacterTreeNode) {
				CharacterTreeNode parentNode = (CharacterTreeNode) node.getParent();
				ch = (Character) parentNode.getUserObject();				
			}
			return ch;
		}
		
		/**
		 * Subclasses should override this method to configure the editor appropriately for the
		 * supplied node to be edited.
		 * @param attribute the attribute represented by the node.
		 * @param node the node to be edited.
		 * @return the component that will be used as the editor.
		 */
		protected abstract Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode node);
		
		@Override
		public boolean isCellEditable(EventObject anEvent) {
			if (anEvent == null) {
			    return false;
			}
		                   
            if (anEvent instanceof MouseEvent) {
            	MouseEvent event = (MouseEvent)anEvent;
            
            	TreePath path = _tree.getPathForLocation(event.getX(),event.getY());
		        if (path!=null) {					
					Object value = path.getLastPathComponent();
					if ( _tree.getModel().isLeaf(value)) {
						Character ch = getCharacterFor((DefaultMutableTreeNode)value);
						Item item = _dataModel.getSelectedItem();
						if (!ch.checkApplicability(item).isInapplicable()) {
							Attribute attribute = _dataModel.getSelectedItem().getAttribute(ch);
							return attribute.isSimple();
						}
					}
		        }
            }
            return false;
		}
	}
	
	/**
	 * Overrides the editing behavior of the DefaultTreeCellEditor to allow editing to
	 * occur with a single click.  (The default is three).
	 */
	class DeltaTreeEditor extends DefaultTreeCellEditor {
		
		private static final long serialVersionUID = 8431473832073654661L;

		private MultiStateAttributeCellEditor _multistateEditor;
		private NumericTextAttributeCellEditor _numericTextEditor;
		private DefaultTreeCellRenderer _renderer;
		
		public DeltaTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
			super(tree, renderer, new NumericTextAttributeCellEditor());
			_multistateEditor = new MultiStateAttributeCellEditor();
			_numericTextEditor = (NumericTextAttributeCellEditor)realEditor;
			_renderer = renderer;
		}
		
		@Override
		public Component getTreeCellEditorComponent(JTree tree, Object value,
				boolean isSelected, boolean expanded, boolean leaf, int row) {
			
			if (value instanceof MultistateStateNode) {
				realEditor = _multistateEditor;
				// We do this to prevent the leaf icon being displayed before the editor.
				renderer = null;
				
			}
			else {
				realEditor = _numericTextEditor;
				renderer = _renderer;
			}
			return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
		}

		@Override
		protected boolean canEditImmediately(EventObject e) {
			return true;
		}
		
		/**
	     * Adds the <code>CellEditorListener</code>.
	     * @param l the listener to be added
	     */
	    public void addCellEditorListener(CellEditorListener l) {
	    	_multistateEditor.addCellEditorListener(l);
	    	_numericTextEditor.addCellEditorListener(l);
	    }

	    /**
	      * Removes the previously added <code>CellEditorListener</code>.
	      * @param l the listener to be removed
	      */
	    public void removeCellEditorListener(CellEditorListener l) {
	    	_multistateEditor.removeCellEditorListener(l);
	    	_numericTextEditor.addCellEditorListener(l);
	    }
	}

}

class CharacterTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;
	private EditorDataModel _dataModel;
	
	private Set<Integer> _variableLengthCharacterIndicies;
	public CharacterTreeModel(EditorDataModel dataModel) {
		super(new ContextRootNode(dataModel), false);
		_dataModel = dataModel;
		_variableLengthCharacterIndicies = new HashSet<Integer>();
		for (int i=1; i<=dataModel.getNumberOfCharacters(); i++) {
			Character character = dataModel.getCharacter(i);
			if ((character instanceof TextCharacter) || (character instanceof NumericCharacter)) {
				_variableLengthCharacterIndicies.add(i-1);
			}
		}
	}	
	
	/**
     * Handles the different values that will be supplied by the different editing
     * components.
     */
   public void valueForPathChanged(TreePath path, Object newValue) {
	   DefaultMutableTreeNode  aNode = (DefaultMutableTreeNode)path.getLastPathComponent();
	   
	   Item item = _dataModel.getSelectedItem();
	   if (aNode instanceof MultistateStateNode) {
		   MultistateStateNode node = (MultistateStateNode)aNode;
		   
		   Attribute attribute = item.getAttribute(node.getCharacter());
		   attribute.setStatePresent(node.getStateNo(), (Boolean)newValue);
	   }
	   else {
		   CharStateHolder holder = (CharStateHolder)aNode.getUserObject();
		   Attribute attribute = item.getAttribute(holder.getCharacter());
		   attribute.setValue((String)newValue);
	   }
      
       nodeChanged(aNode);
   }
	
	/**
	 * We notify about changes to text & numeric characters as differing text lengths will invalidate
	 * the preferred size of the label that renders the text and this value is cached by the tree UI delegate.
	 * Multistate characters will maintain the same text as the text is specified by the character.
	 */
	public void itemChanged() {
		for (int i : _variableLengthCharacterIndicies) {
			nodeChanged(((ContextRootNode)getRoot()).getChildAt(i).getChildAt(0));
		}
	}
}


class ContextRootNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;

	public ContextRootNode(EditorDataModel dataModel) {
		_dataModel = dataModel;
		for (int i = 0; i < _dataModel.getNumberOfCharacters(); ++i) {
			au.org.ala.delta.model.Character ch = _dataModel.getCharacter(i + 1);
			add(new CharacterTreeNode(_dataModel, ch));
		}

	}

}

class CharStateHolder {

	private EditorDataModel _dataModel;
	private Character _character;

	public CharStateHolder(EditorDataModel dataModel, Character character) {
		_dataModel = dataModel;
		_character = character;
	}

	@Override
	public String toString() {
		if (_dataModel.getSelectedItem() != null) {
			return _dataModel.getAttributeAsString(_dataModel.getSelectedItem().getItemNumber(), _character.getCharacterId());
		} else {
			return "---";
		}
	}
	
	public Character getCharacter() {
		return _character;
	}

}


class DeltaTreeCellRenderer extends DefaultTreeCellRenderer  {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;
	private MultiStateCheckbox stateValueRenderer = new MultiStateCheckbox();

	public DeltaTreeCellRenderer(EditorDataModel dataModel) {
		_dataModel = dataModel;
	}

	@SuppressWarnings("rawtypes")
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		Item item = _dataModel.getSelectedItem();
		if (value instanceof CharacterTreeNode) {
			CharacterTreeNode node = (CharacterTreeNode) value;
			Character ch = (Character) node.getUserObject();
			// The selected item can be null when adding characters to a new dataset.
			boolean inapplicable = false;
			if (item != null) {
				ControllingInfo info = ch.checkApplicability(item);
				inapplicable = info.isInapplicable();
			}
				
			node.setInapplicable(inapplicable);
			setIcon(EditorUIUtils.iconForCharacter(ch, inapplicable));
			
		} else if (leaf) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			
			Object userObject = node.getUserObject();
			if (userObject == null) {
				return this;
			}
			String name = userObject.toString();
			
			if (node.getParent() instanceof CharacterTreeNode) {
				CharacterTreeNode parentNode = (CharacterTreeNode) node.getParent();
				Character ch = (Character) parentNode.getUserObject();				
				if (node instanceof MultistateStateNode) {
					MultistateStateNode msnode = (MultistateStateNode) node;						
					stateValueRenderer.setText(name);
					stateValueRenderer.setForeground(getForeground());
					if (sel) {
						stateValueRenderer.setBackground(getBackgroundSelectionColor());
					} else {
						stateValueRenderer.setBackground(getBackgroundNonSelectionColor());
					}
					stateValueRenderer.setEnabled(parentNode.isInapplicable());
					stateValueRenderer.bind( msnode.getCharacter(), item, msnode.getStateNo(), parentNode.isInapplicable());
					if (!parentNode.isInapplicable()) {
						Attribute attribute = item.getAttribute(ch);
						stateValueRenderer.setEnabled(attribute.isSimple());
					}
					return stateValueRenderer;
				} else if (ch instanceof NumericCharacter) {
					setText(getText() + " " + ((NumericCharacter) ch).getUnits());
				}
			}
		}
		return this;
	}
	
}

class CharacterTreeNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;

	private Character _character;
	private EditorDataModel _dataModel;
	private boolean _inapplicable;

	public CharacterTreeNode(EditorDataModel dataModel, Character ch) {
		super(ch);
		_dataModel = dataModel;
		_character = ch;
		if (_character instanceof MultiStateCharacter) {
			MultiStateCharacter ms = (MultiStateCharacter) _character;
			for (int i = 0; i < ms.getNumberOfStates(); ++i) {
				add(new MultistateStateNode(ms, i + 1));
			}
		} else {
			add(new DefaultMutableTreeNode(new CharStateHolder(_dataModel, ch)));
		}
	}

	public Character getCharacter() {
		return _character;
	}

	@Override
	public boolean isLeaf() {
		return false;
	}
	
	public void setInapplicable(boolean inapplicable) {
		_inapplicable = inapplicable;
	}
	
	public boolean isInapplicable() {
		return _inapplicable;
	}

	@Override
	public String toString() {
		return String.format("%d. %s", _character.getCharacterId(), RTFUtils.stripFormatting(_character.getDescription()));
	}

}

class MultistateStateNode extends DefaultMutableTreeNode {

	private static final long serialVersionUID = 1L;
	private static CharacterFormatter _characterFormatter = new CharacterFormatter();
	
	private MultiStateCharacter _character;
	private int _stateNo;
	
	public MultistateStateNode(MultiStateCharacter ch, int stateNo) {
		super(_characterFormatter.formatState(ch, stateNo));
		_character = ch;
		_stateNo = stateNo;
	}
	
	@Override
	public boolean isLeaf() {
		return true;
	}
	
	public MultiStateCharacter getCharacter() {
		return _character;
	}
	
	public int getStateNo() {
		return _stateNo;
	}
	
}
