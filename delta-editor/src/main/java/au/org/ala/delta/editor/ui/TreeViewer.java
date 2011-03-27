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

import javax.swing.ActionMap;
import javax.swing.DropMode;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.ui.AboutBox;

public class TreeViewer extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;
	private AttributeEditor _stateEditor;

	@Resource
	String windowTitle;

	public TreeViewer(EditorDataModel dataModel) {
		super();

		ApplicationContext context = Application.getInstance().getContext();
		ResourceMap resourceMap = context.getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);

		this.setSize(new Dimension(500, 400));

		_dataModel = dataModel;
		new InternalFrameDataModelListener(this, dataModel, windowTitle);

		final ItemList lst = new ItemList(_dataModel);
		lst.setDragEnabled(true);
		lst.setDropMode(DropMode.ON);
		ActionMap actionMap = context.getActionMap();
		lst.setSelectionAction(actionMap.get("viewTaxonEditor"));
		

		final JTree tree = new JTree();
		final CharacterTreeModel treeModel = new CharacterTreeModel(_dataModel);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.setModel(treeModel);
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.setCellRenderer(new DeltaTreeCellRenderer(_dataModel));
		tree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {

			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

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

		lst.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {

				_dataModel.setSelectedItem(lst.getSelectedItem());
				tree.repaint();
				_stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
			}
		});

		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		content.setDividerSize(4);
		content.setDividerLocation(180);

		content.setRightComponent(new JScrollPane(tree));
		content.setLeftComponent(new JScrollPane(lst));

		_stateEditor = new AttributeEditor(_dataModel);

		JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);

		divider.setTopComponent(content);
		divider.setBottomComponent(_stateEditor);

		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(divider);
		
		lst.setSelectedIndex(0);
		tree.setSelectionRow(0);

	}
}

class CharacterTreeModel extends DefaultTreeModel {

	private static final long serialVersionUID = 1L;

	public CharacterTreeModel(EditorDataModel dataModel) {
		super(new ContextRootNode(dataModel), false);
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

}


class DeltaTreeCellRenderer extends DefaultTreeCellRenderer {

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
			ControllingInfo info = ch.checkApplicability(item);
			node.setInapplicable(info.isInapplicable());
			setIcon(EditorUIUtils.iconForCharacter(ch, info.isInapplicable()));
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
					if (selected) {
						stateValueRenderer.setBackground(getBackgroundSelectionColor());
					} else {
						stateValueRenderer.setBackground(getBackgroundNonSelectionColor());
					}
					stateValueRenderer.setEnabled(parentNode.isInapplicable());
					stateValueRenderer.bind( msnode.getCharacter(), item, msnode.getStateNo(), parentNode.isInapplicable());
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
	
	private MultiStateCharacter _character;
	private int _stateNo;
	
	public MultistateStateNode(MultiStateCharacter ch, int stateNo) {
		super(ch.getState(stateNo));
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
