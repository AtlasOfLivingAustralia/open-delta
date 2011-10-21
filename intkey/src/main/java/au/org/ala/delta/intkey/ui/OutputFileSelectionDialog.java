package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class OutputFileSelectionDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -5672718959507221507L;

    private JPanel _btnPanel;
    private JScrollPane _scrollPane;
    private JList _list;

    private DefaultListModel _listModel;
    private boolean _okButtonPressed;

    @Resource
    String title;

    public OutputFileSelectionDialog(Frame parent, List<File> outputFiles) {
        super(parent, true);
        setPreferredSize(new Dimension(450, 250));

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(OutputFileSelectionDialog.class, this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(OutputFileSelectionDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _btnPanel = new JPanel();
        getContentPane().add(_btnPanel, BorderLayout.SOUTH);

        JButton _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("outputFileSelectionDialog_OkPressed"));
        _btnPanel.add(_btnOk);

        JButton _btnCancel = new JButton("Cancel");
        _btnCancel.setAction(actionMap.get("outputFileSelectionDialog_CancelPressed"));
        _btnPanel.add(_btnCancel);

        JPanel _listPnl = new JPanel();
        _listPnl.setBorder(new EmptyBorder(5, 5, 0, 5));
        getContentPane().add(_listPnl, BorderLayout.CENTER);
        _listPnl.setLayout(new BorderLayout(0, 0));

        _scrollPane = new JScrollPane();
        _listPnl.add(_scrollPane);

        _list = new JList();
        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        _scrollPane.setViewportView(_list);

        _listModel = new DefaultListModel();
        for (File file : outputFiles) {
            _listModel.addElement(file);
        }

        _list.setModel(_listModel);

        if (_listModel.size() > 0) {
            _list.setSelectedIndex(0);
        }

        _okButtonPressed = false;
    }

    public File getSelectedFile() {
        return (File) _list.getSelectedValue();
    }

    public boolean isOkButtonPressed() {
        return _okButtonPressed;
    }

    @Action
    public void outputFileSelectionDialog_OkPressed() {
        _okButtonPressed = true;
        this.setVisible(false);
    }

    @Action
    public void outputFileSelectionDialog_CancelPressed() {
        _okButtonPressed = false;
        this.setVisible(false);
    }
}
