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
package au.org.ala.delta.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.JCheckBox;
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

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.ui.util.IconHelper;

public class TreeViewer extends JInternalFrame {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;
	private StateEditor _stateEditor;

	@Resource
	String windowTitle;

	public TreeViewer(EditorDataModel dataModel) {
		super();

		ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AboutBox.class);
		resourceMap.injectFields(this);

		this.setSize(new Dimension(500, 400));

		_dataModel = dataModel;
		new InternalFrameDataModelListener(this, dataModel, windowTitle);

		final JList lst = new JList();
		lst.setModel(new ItemListModel(_dataModel));
		lst.setDragEnabled(true);
		lst.setDropMode(DropMode.ON);

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

				_dataModel.setSelectedItem(((ItemViewModel) lst.getSelectedValue()).getItem());
				tree.updateUI();
				_stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
			}
		});

		JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		content.setDividerSize(4);
		content.setDividerLocation(180);

		content.setRightComponent(new JScrollPane(tree));
		content.setLeftComponent(new JScrollPane(lst));

		_stateEditor = new StateEditor(_dataModel);

		JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		divider.setDividerLocation(getHeight() - 200);
		divider.setResizeWeight(1);

		divider.setTopComponent(content);
		divider.setBottomComponent(_stateEditor);

		this.getContentPane().setLayout(new BorderLayout());
		getContentPane().add(divider);

	}
}

class ItemListModel extends DefaultListModel implements ListModel {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;

	public ItemListModel(EditorDataModel dataModel) {
		_dataModel = dataModel;
	}

	@Override
	public int getSize() {
		return _dataModel.getMaximumNumberOfItems();
	}

	@Override
	public Object getElementAt(int index) {
		return new ItemViewModel(_dataModel.getItem(index + 1));
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
			return _dataModel.getAttributeAsString(_dataModel.getSelectedItem().getItemId(), _character.getCharacterId());
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
		return _model.getItemId() + ". " + RTFUtils.stripFormatting(_model.getDescription());
	}

	public Item getItem() {
		return _model;
	}
}

class DeltaTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private EditorDataModel _dataModel;
	private JCheckBox stateValueRenderer = new JCheckBox();

	public DeltaTreeCellRenderer(EditorDataModel dataModel) {
		_dataModel = dataModel;
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		if (value instanceof CharacterTreeNode) {
			Character ch = (Character) ((CharacterTreeNode) value).getUserObject();
			setIcon(IconHelper.iconForCharacter(ch));
		} else if (leaf) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			String name = node.getUserObject().toString();
			if (node.getParent() instanceof CharacterTreeNode) {
				Character ch = (Character) ((CharacterTreeNode) node.getParent()).getUserObject();

				if (ch instanceof MultiStateCharacter) {

					stateValueRenderer.setText(name);
					stateValueRenderer.setForeground(getForeground());
					if (selected) {
						stateValueRenderer.setBackground(getBackgroundSelectionColor());
					} else {
						stateValueRenderer.setBackground(getBackgroundNonSelectionColor());
					}
					stateValueRenderer.setSelected(false);
					if (_dataModel.getSelectedItem() != null) {
						Item item = _dataModel.getSelectedItem();
						Attribute attribute = item.getAttribute(ch);

						if (attribute != null) {
							try {

								MultiStateCharacter multiStateChar = (MultiStateCharacter) ch;
								int numStates = multiStateChar.getNumberOfStates();

								for (int stateNumber = 1; stateNumber <= numStates; stateNumber++) {
									if (multiStateChar.getState(stateNumber).equals(name)) {
										stateValueRenderer.setSelected(attribute.isPresent(stateNumber));
										break;
									}
								}
							} catch (Exception e) {
								// We don't handle multiple selection right now...
								e.printStackTrace();
							}
						}
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

	public CharacterTreeNode(EditorDataModel dataModel, Character ch) {
		super(ch);
		_dataModel = dataModel;
		_character = ch;
		if (_character instanceof MultiStateCharacter) {
			MultiStateCharacter ms = (MultiStateCharacter) _character;
			for (int i = 0; i < ms.getNumberOfStates(); ++i) {
				add(new DefaultMutableTreeNode(ms.getState(i + 1)));
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

	@Override
	public String toString() {
		return RTFUtils.stripFormatting(_character.getDescription());
	}

}
