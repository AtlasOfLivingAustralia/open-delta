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
package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.GridLayout;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.util.Pair;
import java.awt.Dimension;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

public class EditDatasetIndexDialog extends IntkeyDialog {
    private JTable _tableDatasetIndex;
    private JScrollPane _sclPnTable;
    private JPanel _pnlModificationButtons;
    private NonEditableTableModel _tableModel;

    private List<Pair<String, String>> _modifiedDatasetIndex;

    @Resource
    String title;

    @Resource
    String descriptionColumnHeader;

    @Resource
    String pathColumnHeader;

    @Resource
    String descriptionCaption;

    @Resource
    String pathCaption;

    @Resource
    String deleteItemPromptCaptionTemplate;

    @Resource
    String deleteItemPromptTitle;

    @Resource
    String addItemPromptTitle;

    @Resource
    String editItemPromptTitle;

    public EditDatasetIndexDialog(Frame owner, List<Pair<String, String>> datasetIndexData, final String newDatasetDescription, final String newDatasetPath) {
        this(owner, datasetIndexData);
        SwingUtilities.invokeLater(new Runnable() {
            
            @Override
            public void run() {
                promptToAddSpecifiedDataset(newDatasetDescription, newDatasetPath);
            }
        });
    }

    private void promptToAddSpecifiedDataset(String newDatasetDescription, String newDatasetPath) {
        AddOrEditDataIndexItemDialog addDialog = new AddOrEditDataIndexItemDialog(this, addItemPromptTitle, newDatasetDescription, newDatasetPath);
        ((SingleFrameApplication) Application.getInstance()).show(addDialog);
        Pair<String, String> descriptionPathPair = addDialog.getDescriptionPathPair();
        if (descriptionPathPair != null) {
            _tableModel.addRow(new Object[] { descriptionPathPair.getFirst(), descriptionPathPair.getSecond() });
            _tableDatasetIndex.setRowSelectionInterval(_tableDatasetIndex.getRowCount() - 1, _tableDatasetIndex.getRowCount() - 1);
        }
    }

    public EditDatasetIndexDialog(Frame owner, List<Pair<String, String>> datasetIndexData) {
        super(owner, true);
        setPreferredSize(new Dimension(500, 250));

        _modifiedDatasetIndex = null;

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(EditDatasetIndexDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _sclPnTable = new JScrollPane();
        _sclPnTable.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)));
        _sclPnTable.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        _sclPnTable.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(_sclPnTable, BorderLayout.CENTER);

        _tableDatasetIndex = new ToolTipTable();
        _tableDatasetIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _tableModel = new NonEditableTableModel();
        _tableModel.setColumnIdentifiers(new Object[] { descriptionCaption, pathCaption });

        for (Pair<String, String> dataInfoPair : datasetIndexData) {
            _tableModel.addRow(new Object[] { dataInfoPair.getFirst(), dataInfoPair.getSecond() });
        }

        _tableDatasetIndex.setModel(_tableModel);

        _sclPnTable.setViewportView(_tableDatasetIndex);

        _pnlModificationButtons = new JPanel();
        _pnlModificationButtons.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlModificationButtons, BorderLayout.EAST);

        JButton btnEdit = new JButton();
        btnEdit.setAction(actionMap.get("EditDatasetIndexDialog_Edit"));

        JButton btnAdd = new JButton();
        btnAdd.setAction(actionMap.get("EditDatasetIndexDialog_Add"));

        JButton btnDelete = new JButton();
        btnDelete.setAction(actionMap.get("EditDatasetIndexDialog_Delete"));

        JButton btnMoveUp = new JButton();
        btnMoveUp.setAction(actionMap.get("EditDatasetIndexDialog_MoveUp"));

        JButton btnMoveDown = new JButton();
        btnMoveDown.setAction(actionMap.get("EditDatasetIndexDialog_MoveDown"));
        GroupLayout gl__pnlModificationButtons = new GroupLayout(_pnlModificationButtons);
        gl__pnlModificationButtons.setHorizontalGroup(gl__pnlModificationButtons.createParallelGroup(Alignment.TRAILING)
                .addGroup(
                        gl__pnlModificationButtons
                                .createSequentialGroup()
                                .addContainerGap()
                                .addGroup(
                                        gl__pnlModificationButtons.createParallelGroup(Alignment.LEADING, false).addComponent(btnDelete, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                                                .addComponent(btnMoveUp, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE).addComponent(btnMoveDown, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                                                .addComponent(btnEdit, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
                                                .addComponent(btnAdd, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));
        gl__pnlModificationButtons.setVerticalGroup(gl__pnlModificationButtons.createParallelGroup(Alignment.LEADING).addGroup(
                gl__pnlModificationButtons.createSequentialGroup().addComponent(btnEdit, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addGap(4)
                        .addComponent(btnAdd, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addGap(4).addComponent(btnDelete, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(btnMoveUp, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE).addPreferredGap(ComponentPlacement.RELATED)
                        .addComponent(btnMoveDown, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE).addContainerGap(31, Short.MAX_VALUE)));
        _pnlModificationButtons.setLayout(gl__pnlModificationButtons);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btnOk = new JButton();
        btnOk.setAction(actionMap.get("EditDatasetIndexDialog_OK"));
        panel.add(btnOk);

        JButton btnCancel = new JButton();
        btnCancel.setAction(actionMap.get("EditDatasetIndexDialog_Cancel"));
        panel.add(btnCancel);
    }

    @Action
    public void EditDatasetIndexDialog_OK() {
        _modifiedDatasetIndex = new ArrayList<Pair<String, String>>();
        for (int i = 0; i < _tableDatasetIndex.getRowCount(); i++) {
            String description = (String) _tableDatasetIndex.getValueAt(i, 0);
            String path = (String) _tableDatasetIndex.getValueAt(i, 1);
            _modifiedDatasetIndex.add(new Pair<String, String>(description, path));
        }

        this.setVisible(false);
    }

    @Action
    public void EditDatasetIndexDialog_Cancel() {
        _modifiedDatasetIndex = null;
        this.setVisible(false);
    }

    @Action
    public void EditDatasetIndexDialog_Edit() {
        int selectedRow = _tableDatasetIndex.getSelectedRow();
        if (selectedRow != -1) {
            String description = (String) _tableDatasetIndex.getValueAt(selectedRow, 0);
            String path = (String) _tableDatasetIndex.getValueAt(selectedRow, 1);
            AddOrEditDataIndexItemDialog editDialog = new AddOrEditDataIndexItemDialog(this, editItemPromptTitle, description, path);
            ((SingleFrameApplication) Application.getInstance()).show(editDialog);
            Pair<String, String> descriptionPathPair = editDialog.getDescriptionPathPair();
            if (descriptionPathPair != null) {
                _tableModel.removeRow(selectedRow);
                _tableModel.insertRow(selectedRow, new Object[] { descriptionPathPair.getFirst(), descriptionPathPair.getSecond() });
                _tableDatasetIndex.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
    }

    @Action
    public void EditDatasetIndexDialog_Delete() {
        int selectedRow = _tableDatasetIndex.getSelectedRow();
        if (selectedRow != -1) {
            String selectedRowDatasetName = (String) _tableModel.getValueAt(selectedRow, 0);
            String promptCaption = MessageFormat.format(deleteItemPromptCaptionTemplate, selectedRowDatasetName);
            int returnValue = JOptionPane.showConfirmDialog(this, promptCaption, deleteItemPromptTitle, JOptionPane.YES_NO_OPTION);
            if (returnValue == JOptionPane.OK_OPTION) {
                _tableModel.removeRow(selectedRow);
            }
        }
    }

    @Action
    public void EditDatasetIndexDialog_MoveUp() {
        int selectedRow = _tableDatasetIndex.getSelectedRow();
        if (selectedRow != -1 && selectedRow != 0) {
            _tableModel.moveRow(selectedRow, selectedRow, selectedRow - 1);
            _tableDatasetIndex.setRowSelectionInterval(selectedRow - 1, selectedRow - 1);
        }
    }

    @Action
    public void EditDatasetIndexDialog_MoveDown() {
        int selectedRow = _tableDatasetIndex.getSelectedRow();
        if (selectedRow != -1 && selectedRow < _tableModel.getRowCount() - 1) {
            _tableModel.moveRow(selectedRow, selectedRow, selectedRow + 1);
            _tableDatasetIndex.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
        }
    }

    @Action
    public void EditDatasetIndexDialog_Add() {
        AddOrEditDataIndexItemDialog addDialog = new AddOrEditDataIndexItemDialog(this, addItemPromptTitle);
        ((SingleFrameApplication) Application.getInstance()).show(addDialog);
        Pair<String, String> descriptionPathPair = addDialog.getDescriptionPathPair();
        if (descriptionPathPair != null) {
            int selectedRow = _tableDatasetIndex.getSelectedRow();
            if (selectedRow != -1) {
                _tableModel.insertRow(selectedRow + 1, new Object[] { descriptionPathPair.getFirst(), descriptionPathPair.getSecond() });
                _tableDatasetIndex.setRowSelectionInterval(selectedRow + 1, selectedRow + 1);
            } else {
                _tableModel.addRow(new Object[] { descriptionPathPair.getFirst(), descriptionPathPair.getSecond() });
                _tableDatasetIndex.setRowSelectionInterval(_tableDatasetIndex.getRowCount() - 1, _tableDatasetIndex.getRowCount() - 1);
            }
        }
    }

    public List<Pair<String, String>> getModifiedDatasetIndex() {
        return _modifiedDatasetIndex;
    }

    private static class NonEditableTableModel extends DefaultTableModel {

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

    }

    private static class ToolTipTable extends JTable {
        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (c instanceof JComponent) {
                JComponent jc = (JComponent) c;
                jc.setToolTipText((String) getValueAt(row, column));
            }
            return c;
        }
    }

}
