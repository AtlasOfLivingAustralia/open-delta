package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import java.awt.Dimension;

public class OpenDataSetDialog extends IntkeyDialog {
    private JTextField _txtFldFileName;
    private JPanel _pnlList;
    private JLabel _lblSelectByTitle;

    private File _startBrowseDirectory;

    @Resource
    String title;

    @Resource
    String selectByTitleCaption;

    @Resource
    String selectByFileCaption;

    @Resource
    String fileChooserDescription;
    
    private String _selectedDatasetPath;

    public OpenDataSetDialog(Frame owner, LinkedHashMap<String, String> datasetIndexMap, File startBrowseDirectory) {
        super(owner, true);
        setPreferredSize(new Dimension(450, 300));

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(OpenDataSetDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _pnlList = new JPanel();
        _pnlList.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_pnlList, BorderLayout.CENTER);
        _pnlList.setLayout(new BorderLayout(0, 0));

        _lblSelectByTitle = new JLabel(selectByTitleCaption);
        _pnlList.add(_lblSelectByTitle, BorderLayout.NORTH);

        JScrollPane _sclPnList = new JScrollPane();
        _pnlList.add(_sclPnList, BorderLayout.CENTER);

        JList _listDatasetIndex = new JList();
        _sclPnList.setViewportView(_listDatasetIndex);

        JPanel _pnlBottom = new JPanel();
        _pnlBottom.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_pnlBottom, BorderLayout.SOUTH);
        _pnlBottom.setLayout(new BorderLayout(0, 0));

        JLabel _lblSelectByFileName = new JLabel(selectByFileCaption);
        _pnlBottom.add(_lblSelectByFileName, BorderLayout.NORTH);

        JPanel _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(10, 0, 0, 0));
        _pnlBottom.add(_pnlButtons, BorderLayout.SOUTH);

        JButton _btnOK = new JButton();
        _btnOK.setAction(actionMap.get("OpenDataSetDialog_OK"));
        _pnlButtons.add(_btnOK);

        JButton _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("OpenDataSetDialog_Cancel"));
        _pnlButtons.add(_btnCancel);

        JButton _btnHelp = new JButton("Help");
        _btnHelp.setAction(actionMap.get("OpenDataSetDialog_Help"));
        _pnlButtons.add(_btnHelp);

        JPanel _pnlFile = new JPanel();
        _pnlBottom.add(_pnlFile, BorderLayout.CENTER);
        _pnlFile.setLayout(new BorderLayout(0, 0));

        _txtFldFileName = new JTextField();
        _pnlFile.add(_txtFldFileName, BorderLayout.CENTER);
        _txtFldFileName.setColumns(10);

        JButton _btnBrowse = new JButton();
        _btnBrowse.setAction(actionMap.get("OpenDataSetDialog_Browse"));
        _pnlFile.add(_btnBrowse, BorderLayout.EAST);

    }

    @Action
    public void OpenDataSetDialog_Browse() {
        List<String> fileExtensions = Arrays.asList(new String[] { "ini", "ink" });
        try {
            File selectedFile = UIUtils.promptForFile(fileExtensions, fileChooserDescription, false, _startBrowseDirectory, this);
            if (selectedFile != null) {
                _txtFldFileName.setText(selectedFile.getAbsolutePath());
            }
        } catch (IOException ex) {
            // do nothing, promptForFile will only throw an IOException if
            // attempting to create a file fails. As we are passing in
            // createFileIfNonExistant as false, this will never occur.
        }
    }

    @Action
    public void OpenDataSetDialog_OK() {
        _selectedDatasetPath = _txtFldFileName.getText();
        this.setVisible(false);
    }

    @Action
    public void OpenDataSetDialog_Cancel() {
        _selectedDatasetPath = null;
        this.setVisible(false);
    }

    @Action
    public void OpenDataSetDialog_Help() {

    }

    public String getSelectedDatasetPath() {
        return _selectedDatasetPath;
    }
}
