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

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.EditorPreferences;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.ui.util.EditorUIUtils;
import au.org.ala.delta.editor.ui.validator.AttributeValidator;
import au.org.ala.delta.editor.ui.validator.TextComponentValidator;
import au.org.ala.delta.editor.ui.validator.ValidationListener;
import au.org.ala.delta.editor.ui.validator.ValidationResult;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.SearchDirection;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.impl.ControllingInfo;
import au.org.ala.delta.model.observer.AbstractDataSetObserver;
import au.org.ala.delta.model.observer.DeltaDataSetChangeEvent;
import au.org.ala.delta.ui.AboutBox;
import au.org.ala.delta.util.Predicate;
import au.org.ala.delta.util.SearchableModel;
import au.org.ala.delta.util.Visitor;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.EventObject;
import java.util.HashSet;
import java.util.Set;

/**
 * The TreeViewer presents the data model as a list of items and a tree
 * containing the character attributes of the item selected from the list.
 */
public class TreeViewer extends AbstractDeltaView {

    private static final long serialVersionUID = 1L;

    private EditorViewModel _dataModel;
    private AttributeEditor _stateEditor;
    private CharacterTree _tree;
    private ItemList _itemList;

    @Resource
    String windowTitle;

    public TreeViewer(EditorViewModel dataModel) {
        super();

        setName(dataModel.getShortName() + "-tree");
        ApplicationContext context = Application.getInstance().getContext();
        ResourceMap resourceMap = context.getResourceMap(AboutBox.class);
        resourceMap.injectFields(this);

        this.setSize(new Dimension(800, 500));

        _dataModel = dataModel;
        _dataModel.addDeltaDataSetObserver(new AbstractDataSetObserver() {
            @Override
            public void characterAdded(DeltaDataSetChangeEvent event) {
                TreePath path = new TreePath(_tree.getModel().getRoot());
                if (!_tree.isExpanded(path)) {
                    _tree.expandPath(path);
                }
            }

        });

        _itemList = new ItemList(_dataModel);
        _itemList.setDragEnabled(true);
        _itemList.setDropMode(DropMode.INSERT);

        _tree = new CharacterTree();
        final CharacterTreeModel treeModel = new CharacterTreeModel(_dataModel);

        _tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        _tree.setModel(treeModel);
        _tree.setRootVisible(false);
        _tree.expandPath(new TreePath(treeModel.getRoot()));
        _tree.setEditable(true);
        _tree.setShowsRootHandles(true);
        DeltaTreeCellRenderer renderer = new DeltaTreeCellRenderer(_dataModel);
        _tree.setCellRenderer(renderer);
        _tree.setDragEnabled(true);
        _tree.setDropMode(DropMode.INSERT);
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
                    if (node instanceof MultistateStateNode) {
                        _dataModel.setSelectedState(((MultistateStateNode) node).getStateNo());
                    }
                } else {
                    _dataModel.setSelectedCharacter(null);
                }
                _stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
            }
        });

        _itemList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {

                int selectedItemNum = _itemList.getSelectedIndex() + 1;
                if (selectedItemNum <= 0) {
                    return;
                }
                _dataModel.setSelectedItem(_dataModel.getItem(selectedItemNum));

                treeModel.itemChanged();
                _tree.repaint();
                _stateEditor.bind(_dataModel.getSelectedCharacter(), _dataModel.getSelectedItem());
            }
        });

        JSplitPane content = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        content.setDividerSize(4);
        content.setDividerLocation(180);

        content.setRightComponent(new JScrollPane(_tree));
        content.setLeftComponent(new JScrollPane(_itemList));

        _stateEditor = new AttributeEditor(_dataModel, this);

        final JSplitPane divider = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        divider.setResizeWeight(1);

        divider.setTopComponent(content);
        divider.setBottomComponent(_stateEditor);
        divider.setBorder(null);

        _stateEditor.add(new AttributeEditorListener() {
            @Override
            public void advance() {
                updateSelection(1);
            }

            @Override
            public void reverse() {
                updateSelection(-1);
            }

            @Override
            public void focusOnViewer() {
                _tree.requestFocus();
            }
        });

        this.getContentPane().setLayout(new BorderLayout());
        getContentPane().add(divider);

        _itemList.setSelectedIndex(0);
        _tree.setSelectionRow(0);

        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentShown(ComponentEvent e) {
                divider.setDividerLocation(getHeight() - EditorPreferences.getViewerDividerOffset());
            }

        });

    }

    @Override
    public ReorderableList getCharacterListView() {
        return _tree.getCharacterListView();
    }

    @Override
    public ReorderableList getItemListView() {
        return _itemList;
    }

    @Override
    public ReorderableList getStateListView() {
        return _tree.getStateListView();
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

    /**
     * Attempts to commit any current edits in the tree or attribute editor and then returns whether those
     * edits are valid or not.
     * @return true if all edits are valid, false otherwise.
     */
    @Override
    public boolean editsValid() {
        boolean valid = true;
        // Commit any uncommited edit in the tree view.
        if (_tree.isEditing()) {
            valid = _tree.stopEditing();
        }
        if (valid) {
            _stateEditor.commitChanges();
            valid = _stateEditor.isAttributeValid();
        }
        return valid;
    }

    @Override
    public String getViewTitle() {
        return windowTitle;
    }

    /**
     * Provides the mechanism for editing multistate attributes.
     */
    class MultiStateAttributeCellEditor extends AttributeCellEditor {

        private static final long serialVersionUID = 2155436614741771060L;

        private CharacterFormatter _formatter;

        public MultiStateAttributeCellEditor() {
            super(new MultiStateCheckbox());
            _formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
            editorComponent.setOpaque(false);
        }

        @Override
        protected Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode node) {
            MultiStateAttribute multiStateAttr = (MultiStateAttribute) attribute;
            MultistateStateNode nodeValue = (MultistateStateNode) node;

            MultiStateCharacter character = (MultiStateCharacter) multiStateAttr.getCharacter();
            int stateNum = nodeValue.getStateNo();
            getEditingCheckBox().setText(_formatter.formatState(character, stateNum));

            getEditingCheckBox().setSelected(multiStateAttr.isStateCoded(stateNum));
            return editorComponent;
        }

        private JCheckBox getEditingCheckBox() {
            return (JCheckBox) editorComponent;
        }
    }

    /**
     * Provides the mechanism for editing numeric or text attributes.
     */
    class NumericTextAttributeCellEditor extends AttributeCellEditor implements ValidationListener {

        private static final long serialVersionUID = -6054961593128043309L;

        private JPanel editor;
        private JTextField textField;
        private JLabel unitsLabel;

        private boolean _valid;
        private TextComponentValidator _validator;
        private CharacterFormatter _formatter;
        private boolean _readyForEdits;

        class EditorPanel extends JPanel {

            private static final long serialVersionUID = -18749588943959204L;

            /**
             * Lays out this <code>Container</code>. If editing, the editor will
             * be placed at <code>offset</code> in the x direction and 0 for y.
             */
            @Override
            public void doLayout() {
                Dimension size = getSize();

                int textFieldWidth = textField.getPreferredSize().width;
                textField.setLocation(0, 0);
                textField.setBounds(0, 0, textFieldWidth, size.height);
                unitsLabel.setBounds(textFieldWidth, 0, size.width - textFieldWidth, size.height);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension preferredSize = textField.getPreferredSize();
                preferredSize.width += unitsLabel.getPreferredSize().width;

                return preferredSize;
            }

            @Override
            public Component getComponentAt(int x, int y) {
                Component comp = super.getComponentAt(x, y);
                if ((comp == textField) || (comp == unitsLabel)) {
                    return textField;
                }
                return comp;
            }

            @Override
            public boolean isFocusCycleRoot() {
                return false;
            }

        }

        public NumericTextAttributeCellEditor() {
            super(new JTextField());
            _formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
            
            createEditorComponent();

            delegate = new EditorDelegate() {
                private static final long serialVersionUID = 2456039012400902726L;

                public void setValue(Object value) {
                    _readyForEdits = false;
                    textField.setText((value != null) ? value.toString() : "");
                    _readyForEdits = true;
                }

                public Object getCellEditorValue() {
                    return textField.getText();
                }
            };
            textField.addActionListener(delegate);
            // The purpose of this is to enable the dataset save function (even though the edit has not
            // yet committed which would normally trigger the enabling of the save menu items).  The save function itself
            // will commit the edit if it is invoked while this editor is still active.
            textField.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    maybeSetModified();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    maybeSetModified();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    maybeSetModified();
                }

                private void maybeSetModified() {
                    if (_readyForEdits) {
                        _dataModel.setModified(true);
                    }
                }
            });
            editorComponent = editor;
        }

        public void requestFocus() {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    textField.requestFocusInWindow();
                }
            });

        }

        private void createEditorComponent() {
            editor = new EditorPanel();
            editor.setOpaque(false);
            editor.setFocusCycleRoot(true);
            editor.setFocusable(false);
            editor.setLayout(null);
            textField = new JTextField();

            editor.add(textField);
            unitsLabel = new JLabel();
            unitsLabel.setOpaque(false);
            unitsLabel.setFont(_tree.getFont());

            editor.add(unitsLabel);
        }

        @Override
        protected Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode nodeUserObject) {
            String attributeText = _dataModel.displayTextFromAttributeValue(attribute, attribute.getValueAsString());
        	delegate.setValue(attributeText);
            Character character = attribute.getCharacter();

            if ((character != null) && (character instanceof NumericCharacter<?>)) {
                unitsLabel.setText(_formatter.formatUnits((NumericCharacter<?>) character));
                textField.setColumns(10);
            } else {
                unitsLabel.setText("");
                textField.setColumns(30);
            }

            _validator = new TextComponentValidator(new AttributeValidator(_dataModel, attribute, new ControllingInfo()), this);
            getTextField().setInputVerifier(_validator);
            _valid = true;

            return editorComponent;
        }

        private JTextField getTextField() {
            return textField;
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
     * Provides support for editors by determining which node has been selected
     * and whether it may be edited
     */
    abstract class AttributeCellEditor extends DefaultCellEditor {

        private static final long serialVersionUID = -792901638653087259L;

        public AttributeCellEditor(JTextField textField) {
            super(textField);
        }

        public AttributeCellEditor(JCheckBox checkBox) {
            super(checkBox);
        }


        public void requestFocus() {}

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
            super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            if (leaf) {
                Character ch = getCharacterFor(node);
                if (ch != null) {
                    Attribute attribute = _dataModel.getSelectedItem().getAttribute(ch);
                    JComponent editor = (JComponent) configureEditingComponent(attribute, node);
                    return editor;
                }
            }

            return null;
        }

        private Character getCharacterFor(DefaultMutableTreeNode node) {
            Character ch = null;
            if (node.getParent() instanceof CharacterTreeNode) {
                CharacterTreeNode parentNode = (CharacterTreeNode) node.getParent();
                ch = parentNode.getCharacter();
            }
            return ch;
        }

        /**
         * Subclasses should override this method to configure the editor
         * appropriately for the supplied node to be edited.
         * 
         * @param attribute
         *            the attribute represented by the node.
         * @param node
         *            the node to be edited.
         * @return the component that will be used as the editor.
         */
        protected abstract Component configureEditingComponent(Attribute attribute, DefaultMutableTreeNode node);

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            if (anEvent == null) {
                return false;
            }

            if (anEvent instanceof MouseEvent) {
                MouseEvent event = (MouseEvent) anEvent;

                TreePath path = _tree.getPathForLocation(event.getX(), event.getY());
                if (path != null) {
                    Object value = path.getLastPathComponent();
                    if (_tree.getModel().isLeaf(value)) {
                        Character ch = getCharacterFor((DefaultMutableTreeNode) value);
                        Item item = _dataModel.getSelectedItem();
                        if (item != null && !ch.checkApplicability(item).isInapplicable()) {
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
     * Overrides the editing behaviour of the DefaultTreeCellEditor to allow
     * editing to occur with a single click. (The default is three).
     */
    class DeltaTreeEditor extends DefaultTreeCellEditor {

        private MultiStateAttributeCellEditor _multistateEditor;
        private NumericTextAttributeCellEditor _numericTextEditor;
        private DefaultTreeCellRenderer _renderer;

        public DeltaTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
            super(tree, renderer, new NumericTextAttributeCellEditor());
            _multistateEditor = new MultiStateAttributeCellEditor();
            _numericTextEditor = (NumericTextAttributeCellEditor) realEditor;
            _renderer = renderer;
            tree.setInvokesStopCellEditing(true);
        }

        @Override
        public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {

            if (value instanceof MultistateStateNode) {
                realEditor = _multistateEditor;
                // We do this to prevent the leaf icon being displayed before
                // the editor.
                renderer = null;

            } else {
                realEditor = _numericTextEditor;
                renderer = _renderer;
            }
            return super.getTreeCellEditorComponent(tree, value, isSelected, expanded, leaf, row);
        }

        @Override
        protected boolean canEditImmediately(EventObject e) {
            if (e instanceof MouseEvent) {
                MouseEvent me = (MouseEvent) e;
                return inHitRegion(me.getX(), me.getY());
            }
            return false;
        }

        @Override
        protected void prepareForEditing() {
            super.prepareForEditing();
            if (realEditor instanceof AttributeCellEditor) {
                ((AttributeCellEditor)realEditor).requestFocus();
            }
        }

        @Override
        protected boolean inHitRegion(int x, int y) {
            if (lastRow != -1 && tree != null) {
                Rectangle bounds = tree.getRowBounds(lastRow);

                if (bounds != null && x <= (bounds.x + 20) && x > bounds.x) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Adds the <code>CellEditorListener</code>.
         * 
         * @param l
         *            the listener to be added
         */
        public void addCellEditorListener(CellEditorListener l) {
            _multistateEditor.addCellEditorListener(l);
            _numericTextEditor.addCellEditorListener(l);
        }

        /**
         * Removes the previously added <code>CellEditorListener</code>.
         * 
         * @param l
         *            the listener to be removed
         */
        public void removeCellEditorListener(CellEditorListener l) {
            _multistateEditor.removeCellEditorListener(l);
            _numericTextEditor.addCellEditorListener(l);
        }
    }

}

class CharacterTreeModel extends DefaultTreeModel implements SearchableModel<Character> {

    private static final long serialVersionUID = 1L;
    private EditorViewModel _dataModel;

    private Set<Integer> _variableLengthCharacterIndicies;

    public CharacterTreeModel(EditorViewModel dataModel) {
        super(new ContextRootNode(dataModel), false);

        _dataModel = dataModel;
        _dataModel.addDeltaDataSetObserver(new TreeModelCharacterListener());
        _variableLengthCharacterIndicies = new HashSet<Integer>();
        for (int i = 1; i <= dataModel.getNumberOfCharacters(); i++) {
            Character character = dataModel.getCharacter(i);
            if ((character instanceof TextCharacter) || (character instanceof NumericCharacter)) {
                _variableLengthCharacterIndicies.add(i - 1);
            }
        }
    }

    public void visitCharacters(Visitor<Character> visitor) {
        _dataModel.visitCharacters(visitor);
    }

    public Collection<Character> selectCharacters(Predicate<Character> predicate) {
        return _dataModel.selectCharacters(predicate);
    }

    @Override
    public Character first(Predicate<Character> predicate, int startIndex, SearchDirection direction) {
        return _dataModel.firstCharacter(predicate, startIndex, direction);
    }

    /**
     * Handles the different values that will be supplied by the different
     * editing components.
     */
    public void valueForPathChanged(TreePath path, Object newValue) {
        DefaultMutableTreeNode aNode = (DefaultMutableTreeNode) path.getLastPathComponent();

        Item item = _dataModel.getSelectedItem();
        if (aNode instanceof MultistateStateNode) {
            MultistateStateNode node = (MultistateStateNode) aNode;

            MultiStateAttribute attribute = (MultiStateAttribute) item.getAttribute(node.getCharacter());
            attribute.setStatePresent(node.getStateNo(), (Boolean) newValue);
        } else {
            CharStateHolder holder = (CharStateHolder) aNode.getUserObject();
            Attribute attribute = item.getAttribute(holder.getCharacter());
            try {
            	String value = _dataModel.attributeValueFromDisplayText(attribute, (String)newValue);
            	attribute.setValueFromString(value);
            }
            catch (DirectiveException e) {
            	// The slot file attribute won't actually throw this exception.
            }
        }

        nodeChanged(aNode);
    }

    public int getNumberOfCharacters() {
        return _dataModel.getNumberOfCharacters();
    }

    /**
     * We notify about changes to text & numeric characters as differing text
     * lengths will invalidate the preferred size of the label that renders the
     * text and this value is cached by the tree UI delegate. Multistate
     * characters will maintain the same text as the text is specified by the
     * character.
     */
    public void itemChanged() {
        for (int i : _variableLengthCharacterIndicies) {
            nodeChanged(((ContextRootNode) getRoot()).getChildAt(i).getChildAt(0));
        }
    }

    class TreeModelCharacterListener extends AbstractDataSetObserver {

        @Override
        public void characterAdded(DeltaDataSetChangeEvent event) {
            ContextRootNode root = (ContextRootNode) getRoot();
            int charNumber = event.getCharacter().getCharacterId();
            CharacterTreeNode node = root.add(charNumber);
            fireTreeNodesInserted(this, new Object[] { root }, new int[] { charNumber - 1 }, new Object[] { node });

            for (int i = charNumber; i < root.getChildCount(); i++) {
                node = (CharacterTreeNode) root.getChildAt(i);
                updateNode(node, i + 1);
            }
        }

        @Override
        public void characterEdited(DeltaDataSetChangeEvent event) {
            ContextRootNode root = (ContextRootNode) getRoot();
            int charNumber = event.getCharacter().getCharacterId();
            fireTreeNodesChanged(this, new Object[] { root }, new int[] { charNumber - 1 }, new Object[] { root.getChildAt(charNumber - 1) });
            CharacterTreeNode charNode = (CharacterTreeNode) root.getChildAt(charNumber - 1);
            updateNode(charNode, charNumber);

            // The event was because of a change to a character state.
            if (event.getExtraInformation() != null) {
                // Force recreation of the children.
                charNode.setCharacterNumber(charNode.getCharacterNumber());

                int childCount = charNode.getChildCount();
                int[] childIndicies = new int[childCount];
                Object[] children = new Object[childCount];
                for (int i = 0; i < childCount; i++) {
                    childIndicies[i] = i;
                    children[i] = charNode.getChildAt(i);
                }
                charNode.getChildCount();
                fireTreeStructureChanged(this, pathToNode(charNode), childIndicies, children);

            }
        }

        @Override
        public void characterDeleted(DeltaDataSetChangeEvent event) {
            ContextRootNode root = (ContextRootNode) getRoot();
            int charNumber = (Integer) event.getExtraInformation();
            CharacterTreeNode node = root.removeCharacter(charNumber);
            fireTreeNodesRemoved(this, new Object[] { root }, new int[] { charNumber - 1 }, new Object[] { node });

            for (int i = charNumber; i <= root.getChildCount(); i++) {
                node = (CharacterTreeNode) root.getChildAt(i - 1);

                updateNode(node, i);
            }
        }

        @Override
        public void characterMoved(DeltaDataSetChangeEvent event) {
            ContextRootNode root = (ContextRootNode) getRoot();
            int charNumber = event.getCharacter().getCharacterId();
            int oldNumber = (Integer) event.getExtraInformation();

            CharacterTreeNode toMove = (CharacterTreeNode) root.getChildAt(oldNumber - 1);
            removeNodeFromParent(toMove);
            insertNodeInto(toMove, root, charNumber - 1);
        }

        @Override
        public void itemEdited(DeltaDataSetChangeEvent event) {
            if (event.getItem() != null) {
                itemChanged();
            }
        }


        private void updateNode(CharacterTreeNode node, int newCharacterNumber) {

            int[] childIndicies = new int[node.getChildCount()];
            Object[] children = new Object[node.getChildCount()];
            for (int i = 0; i < childIndicies.length; i++) {
                childIndicies[i] = i;
                children[i] = node.getChildAt(i);
            }

            fireTreeStructureChanged(this, pathToNode(node), childIndicies, children);
        }

        private Object[] pathToNode(CharacterTreeNode node) {
            return new Object[] { getRoot(), node };
        }
    }

    @Override
    public int size() {
        return getNumberOfCharacters();
    }

}

class ContextRootNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    private EditorViewModel _dataModel;

    public ContextRootNode(EditorViewModel dataModel) {
        _dataModel = dataModel;
        for (int i = 0; i < _dataModel.getNumberOfCharacters(); ++i) {
            add(i + 1);
        }
    }

    /**
     * Adds a new node that represents the character identified by the supplied
     * character number.
     * 
     * @param characterNumber
     *            the number of the character to add.
     * @return the new node that was added.
     */
    public CharacterTreeNode add(int characterNumber) {
        CharacterTreeNode node = new CharacterTreeNode(_dataModel, characterNumber);
        insert(node, characterNumber - 1);

        return node;
    }

    /**
     * Removes the node that represents the character identified by the supplied
     * character number.
     * 
     * @param characterNumber
     *            the number of the character to remove.
     * @return the node that was removed.
     */
    public CharacterTreeNode removeCharacter(int characterNumber) {
        CharacterTreeNode node = (CharacterTreeNode) getChildAt(characterNumber - 1);
        remove(characterNumber - 1);
        return node;
    }

    public void moveCharacter(int characterNumber, int newNumber) {
        CharacterTreeNode node = removeCharacter(characterNumber);
        insert(node, newNumber - 1);
    }

}

class CharStateHolder {

    private EditorViewModel _dataModel;
    private CharacterTreeNode _parent;

    public CharStateHolder(EditorViewModel dataModel, CharacterTreeNode parent) {
        _dataModel = dataModel;
        _parent = parent;
    }

    @Override
    public String toString() {
        if (_dataModel.getSelectedItem() != null) {
            return _dataModel.getAttributeAsString(_dataModel.getSelectedItem().getItemNumber(), _parent.getCharacterNumber());
        } else {
            return "---";
        }
    }

    public Character getCharacter() {
        int characterNumber = _parent.getCharacterNumber();
        return _dataModel.getCharacter(characterNumber);
    }

}

class DeltaTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 1L;

    private EditorViewModel _dataModel;
    private MultiStateCheckbox stateValueRenderer = new MultiStateCheckbox();
    private CharacterFormatter _formatter;

    public DeltaTreeCellRenderer(EditorViewModel dataModel) {
        _dataModel = dataModel;
        _formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);
    }

    @SuppressWarnings("rawtypes")
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        Item item = _dataModel.getSelectedItem();
        if (value instanceof CharacterTreeNode) {
            CharacterTreeNode node = (CharacterTreeNode) value;
            Character ch = node.getCharacter();
            // The selected item can be null when adding characters to a new
            // dataset.
            boolean inapplicable = false;
            if (item != null) {
                ControllingInfo info = ch.checkApplicability(item);
                inapplicable = info.isInapplicable();
            }

            node.setInapplicable(inapplicable);
            setIcon(EditorUIUtils.iconForCharacter(ch, inapplicable));

            setText(_formatter.formatCharacterDescription(ch));

        } else if (leaf) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

            Object userObject = node.getUserObject();
            if (userObject == null) {
                return this;
            }
            String name = userObject.toString();

            if (node.getParent() instanceof CharacterTreeNode) {
                CharacterTreeNode parentNode = (CharacterTreeNode) node.getParent();
                Character ch = parentNode.getCharacter();
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
                    stateValueRenderer.bind(msnode.getCharacter(), item, msnode.getStateNo(), parentNode.isInapplicable());
                    if (!parentNode.isInapplicable() && item != null) {
                        Attribute attribute = item.getAttribute(ch);
                        stateValueRenderer.setEnabled(attribute.isSimple());
                    }
                    return stateValueRenderer;
                } else if (ch instanceof NumericCharacter) {
                    setText(getText() + " " + _formatter.formatUnits((NumericCharacter) ch));
                }
            }
        }
        return this;
    }

    @Override
    public void updateUI() {
        setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
        setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
        setOpenIcon(UIManager.getIcon("Tree.openIcon"));
        setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
        setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
        setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
        setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
        setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
        super.updateUI();
    }

}

class CharacterTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    private EditorViewModel _dataModel;
    private boolean _inapplicable;

    public CharacterTreeNode(EditorViewModel dataModel, int characterNumber) {
        super(characterNumber);
        _dataModel = dataModel;
        setCharacterNumber(characterNumber);
    }

    public void setCharacterNumber(int characterNumber) {

        removeAllChildren();

        Character character = _dataModel.getCharacter(characterNumber);
        if (character instanceof MultiStateCharacter) {
            MultiStateCharacter ms = (MultiStateCharacter) character;
            for (int i = 0; i < ms.getNumberOfStates(); ++i) {
                add(new MultistateStateNode(ms, i + 1));
            }
        } else {
            add(new DefaultMutableTreeNode(new CharStateHolder(_dataModel, this)));
        }
    }

    public int getCharacterNumber() {
        return getParent().getIndex(this) + 1;
    }

    public Character getCharacter() {
        return _dataModel.getCharacter(getCharacterNumber());
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
        return Integer.toString(getCharacterNumber());
    }

}

class MultistateStateNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;
    private static CharacterFormatter _characterFormatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);

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
