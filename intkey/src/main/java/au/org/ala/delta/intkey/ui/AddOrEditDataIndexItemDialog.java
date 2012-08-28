package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Pair;
import java.awt.Dimension;

public class AddOrEditDataIndexItemDialog extends IntkeyDialog {
    private JTextField _txtFldDescription;
    private JTextField _txtFldFilePath;
    private JPanel _pnlMain;
    private JLabel _lblFilePath;
    private JButton _btnBrowse;
    private JLabel _lblDescription;
    private JButton _btnOk;
    private JButton _btnCancel;
    private JPanel _pnlButtons;
    private Pair<String, String> _descriptionPathPair;

    @Resource
    String descriptionCaption;

    @Resource
    String pathCaption;

    @Resource
    String missingDataMessage;

    @Resource
    String fileChooserDescription;

    /**
     * ctor
     * 
     * @param owner
     *            owner of the dialog
     * @param title
     *            the title to use for the dialog
     */
    public AddOrEditDataIndexItemDialog(Dialog owner, String title) {
        super(owner, true);
        setPreferredSize(new Dimension(450, 150));
        init();
        setTitle(title);
    }

    /**
     * Use this constructor when prepopulating the dialog with values
     * 
     * @param owner
     *            owner of the dialog
     * @param title
     *            the title to use for the dialog
     * @param description
     *            description of the dataset being edited
     * @param path
     *            path of the dataset being edited
     */
    public AddOrEditDataIndexItemDialog(Dialog owner, String title, String description, String path) {
        super(owner, true);
        init();
        setTitle(title);
        _txtFldDescription.setText(description);
        _txtFldDescription.selectAll();
        _txtFldFilePath.setText(path);
    }

    private void init() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(AddOrEditDataIndexItemDialog.class);
        resourceMap.injectFields(this);

        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);

        _lblDescription = new JLabel(descriptionCaption);

        _txtFldDescription = new JTextField();
        _txtFldDescription.setColumns(10);

        _btnBrowse = new JButton();
        _btnBrowse.setAction(actionMap.get("AddOrEditDataIndexItemDialog_Browse"));

        _lblFilePath = new JLabel(pathCaption);

        _txtFldFilePath = new JTextField();
        _txtFldFilePath.setColumns(10);
        GroupLayout gl__pnlMain = new GroupLayout(_pnlMain);
        gl__pnlMain.setHorizontalGroup(gl__pnlMain.createParallelGroup(Alignment.LEADING).addGroup(
                gl__pnlMain
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl__pnlMain.createParallelGroup(Alignment.LEADING, false).addComponent(_lblFilePath, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(_lblDescription, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(
                                gl__pnlMain
                                        .createParallelGroup(Alignment.TRAILING)
                                        .addGroup(
                                                gl__pnlMain.createSequentialGroup().addComponent(_txtFldFilePath, GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                                                        .addPreferredGap(ComponentPlacement.RELATED).addComponent(_btnBrowse))
                                        .addComponent(_txtFldDescription, GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)).addContainerGap()));
        gl__pnlMain.setVerticalGroup(gl__pnlMain.createParallelGroup(Alignment.LEADING).addGroup(
                gl__pnlMain
                        .createSequentialGroup()
                        .addContainerGap()
                        .addGroup(
                                gl__pnlMain.createParallelGroup(Alignment.BASELINE).addComponent(_lblDescription)
                                        .addComponent(_txtFldDescription, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                        .addGap(18)
                        .addGroup(
                                gl__pnlMain.createParallelGroup(Alignment.BASELINE).addComponent(_btnBrowse, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(_txtFldFilePath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(_lblFilePath, GroupLayout.PREFERRED_SIZE, 20, GroupLayout.PREFERRED_SIZE)).addContainerGap(25, Short.MAX_VALUE)));
        _pnlMain.setLayout(gl__pnlMain);

        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("AddOrEditDataIndexItemDialog_OK"));
        _pnlButtons.add(_btnOk);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("AddOrEditDataIndexItemDialog_Cancel"));
        _pnlButtons.add(_btnCancel);
    }

    @Action
    public void AddOrEditDataIndexItemDialog_Browse() {
        String filePath = _txtFldFilePath.getText().trim();
        File file = new File(filePath);
        File startBrowseDirectory = null;
        if (file.exists()) {
            startBrowseDirectory = file.getParentFile();
        }

        List<String> fileExtensions = Arrays.asList(new String[] { "ini", "ink" });
        try {
            File selectedFile = UIUtils.promptForFile(fileExtensions, fileChooserDescription, false, startBrowseDirectory, this);
            if (selectedFile != null) {
                _txtFldFilePath.setText(selectedFile.getAbsolutePath());
            }
        } catch (IOException ex) {
            // do nothing, promptForFile will only throw an IOException if
            // attempting to create a file fails. As we are passing in
            // createFileIfNonExistant as false, this will never occur.
        }
    }

    @Action
    public void AddOrEditDataIndexItemDialog_OK() {
        String description = _txtFldDescription.getText().trim();
        String filePath = _txtFldFilePath.getText().trim();

        if (StringUtils.isEmpty(description) || StringUtils.isEmpty(filePath)) {
            JOptionPane.showMessageDialog(this, missingDataMessage, "", JOptionPane.ERROR_MESSAGE);
        } else {
            _descriptionPathPair = new Pair<String, String>(description, filePath);
            this.setVisible(false);
        }
    }

    @Action
    public void AddOrEditDataIndexItemDialog_Cancel() {
        _descriptionPathPair = null;
        this.setVisible(false);
    }

    public Pair<String, String> getDescriptionPathPair() {
        return _descriptionPathPair;
    }

}
