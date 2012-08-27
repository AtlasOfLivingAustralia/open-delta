package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import java.io.File;
import java.util.LinkedHashMap;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.ActionMap;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.GridLayout;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import javax.swing.ListSelectionModel;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class EditDatasetDialog extends IntkeyDialog {
    private JTable _tableDatasetIndex;
    private JScrollPane _sclPnTable;
    private JPanel _pnlModificationButtons;

    @Resource
    String title;

    @Resource
    String descriptionColumnHeader;

    @Resource
    String pathColumnHeader;

    public EditDatasetDialog(Frame owner, LinkedHashMap<String, String> datasetIndexMap, File startBrowseDirectory) {
        super(owner, true);

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(OpenDataSetDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _sclPnTable = new JScrollPane();
        _sclPnTable.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new BevelBorder(BevelBorder.LOWERED, null, null, null, null)));
        getContentPane().add(_sclPnTable, BorderLayout.CENTER);

        _tableDatasetIndex = new JTable();
        _tableDatasetIndex.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _tableDatasetIndex.setModel(new DefaultTableModel(new Object[][] { { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null },
                { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, { null, null }, },
                new String[] { "Description", "Path" }) {
            boolean[] columnEditables = new boolean[] { true, false };

            public boolean isCellEditable(int row, int column) {
                return columnEditables[column];
            }
        });

        _sclPnTable.setViewportView(_tableDatasetIndex);

        _pnlModificationButtons = new JPanel();
        _pnlModificationButtons.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlModificationButtons, BorderLayout.EAST);
        _pnlModificationButtons.setLayout(new GridLayout(0, 1, 0, 5));

        JButton btnEdit = new JButton();
        btnEdit.setAction(actionMap.get("EditDatasetDialog_Edit"));
        _pnlModificationButtons.add(btnEdit);

        JButton btnAdd = new JButton();
        btnAdd.setAction(actionMap.get("EditDatasetDialog_Add"));
        _pnlModificationButtons.add(btnAdd);

        JButton btnDelete = new JButton();
        btnDelete.setAction(actionMap.get("EditDatasetDialog_Delete"));
        _pnlModificationButtons.add(btnDelete);

        JButton btnMoveUp = new JButton();
        btnMoveUp.setAction(actionMap.get("EditDatasetDialog_MoveUp"));
        _pnlModificationButtons.add(btnMoveUp);

        JButton btnMoveDown = new JButton();
        btnMoveDown.setAction(actionMap.get("EditDatasetDialog_MoveDown"));
        _pnlModificationButtons.add(btnMoveDown);

        JPanel panel = new JPanel();
        getContentPane().add(panel, BorderLayout.SOUTH);

        JButton btnOk = new JButton();
        btnOk.setAction(actionMap.get("EditDatasetDialog_OK"));
        panel.add(btnOk);

        JButton btnCancel = new JButton();
        btnCancel.setAction(actionMap.get("EditDatasetDialog_Cancel"));
        panel.add(btnCancel);
    }

    @Action
    public void EditDatasetDialog_OK() {
        this.setVisible(false);
    }

    @Action
    public void EditDatasetDialog_Cancel() {
        this.setVisible(false);
    }

    @Action
    public void EditDatasetDialog_Edit() {
    }

    @Action
    public void EditDatasetDialog_Delete() {
    }

    @Action
    public void EditDatasetDialog_MoveUp() {
    }

    @Action
    public void EditDatasetDialog_MoveDown() {
    }

}
