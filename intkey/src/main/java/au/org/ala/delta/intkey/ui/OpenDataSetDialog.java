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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.util.Pair;

public class OpenDataSetDialog extends IntkeyDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 1594437898859286262L;

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
    private JScrollPane _sclPnList;
    private JList _listDatasetIndex;
    private JPanel _pnlBottom;
    private JLabel _lblSelectByFileName;
    private JPanel _pnlButtons;
    private JButton _btnOK;
    private JButton _btnCancel;
    private JButton _btnHelp;
    private JPanel _pnlFile;
    private JButton _btnBrowse;

    public OpenDataSetDialog(Frame owner, List<Pair<String, String>> datasetIndexData, File startBrowseDirectory) {
        super(owner, true);
        setPreferredSize(new Dimension(450, 300));

        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(OpenDataSetDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _selectedDatasetPath = null;

        _pnlList = new JPanel();
        _pnlList.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_pnlList, BorderLayout.CENTER);
        _pnlList.setLayout(new BorderLayout(0, 0));

        _lblSelectByTitle = new JLabel(selectByTitleCaption);
        _pnlList.add(_lblSelectByTitle, BorderLayout.NORTH);

        _sclPnList = new JScrollPane();
        _pnlList.add(_sclPnList, BorderLayout.CENTER);

        _listDatasetIndex = new JList();
        _sclPnList.setViewportView(_listDatasetIndex);

        _pnlBottom = new JPanel();
        _pnlBottom.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_pnlBottom, BorderLayout.SOUTH);
        _pnlBottom.setLayout(new BorderLayout(0, 0));

        _lblSelectByFileName = new JLabel(selectByFileCaption);
        _pnlBottom.add(_lblSelectByFileName, BorderLayout.NORTH);

        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(10, 0, 0, 0));
        _pnlBottom.add(_pnlButtons, BorderLayout.SOUTH);

        _btnOK = new JButton();
        _btnOK.setAction(actionMap.get("OpenDataSetDialog_OK"));
        _pnlButtons.add(_btnOK);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("OpenDataSetDialog_Cancel"));
        _pnlButtons.add(_btnCancel);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("OpenDataSetDialog_Help"));
        _pnlButtons.add(_btnHelp);

        _pnlFile = new JPanel();
        _pnlBottom.add(_pnlFile, BorderLayout.CENTER);
        _pnlFile.setLayout(new BorderLayout(0, 0));

        _txtFldFileName = new JTextField();
        _pnlFile.add(_txtFldFileName, BorderLayout.CENTER);
        _txtFldFileName.setColumns(10);
        _txtFldFileName.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // Clear any selected item in the list of the text field is
                // modified.
                _listDatasetIndex.clearSelection();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // do nothing
            }

            @Override
            public void keyPressed(KeyEvent e) {
                // do nothing

            }
        });

        _btnBrowse = new JButton();
        _btnBrowse.setAction(actionMap.get("OpenDataSetDialog_Browse"));
        _pnlFile.add(_btnBrowse, BorderLayout.EAST);

        DefaultListModel model = new DefaultListModel();

        for (Pair<String, String> datasetInfo : datasetIndexData) {
            model.addElement(datasetInfo);
        }

        _listDatasetIndex.setModel(model);
        _listDatasetIndex.setCellRenderer(new DatasetIndexCellRenderer());

        _listDatasetIndex.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Clear any filename in the text field if an item is selected
                // from the text box.
                _txtFldFileName.setText(null);
            }
        });
        
        _startBrowseDirectory = startBrowseDirectory;
    }

    @Action
    public void OpenDataSetDialog_Browse() {
        List<String> fileExtensions = Arrays.asList(new String[] { "ini", "ink" });
        try {
            File selectedFile = UIUtils.promptForFile(fileExtensions, fileChooserDescription, false, _startBrowseDirectory, this);
            if (selectedFile != null) {
                // Clear any selection in the list if a file is chosen using the
                // Browse button
                _listDatasetIndex.clearSelection();

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
        if (_listDatasetIndex.getSelectedIndex() != -1) {
            _selectedDatasetPath = ((Pair<String, String>) _listDatasetIndex.getSelectedValue()).getSecond();
        } else if (!StringUtils.isEmpty(_txtFldFileName.getText())) {
            _selectedDatasetPath = _txtFldFileName.getText();
        } else {
            _selectedDatasetPath = null;
        }
        this.setVisible(false);
    }

    @Action
    public void OpenDataSetDialog_Cancel() {
        _selectedDatasetPath = null;
        this.setVisible(false);
    }

    @Action
    public void OpenDataSetDialog_Help(ActionEvent e) {
        UIUtils.displayHelpTopic("data_sets_index", this, e);
    }

    public String getSelectedDatasetPath() {
        return _selectedDatasetPath;
    }

    private class DatasetIndexCellRenderer extends JLabel implements ListCellRenderer {

        /**
         * 
         */
        private static final long serialVersionUID = 3061984673980004867L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Pair<String, String> namePathPair = (Pair<String, String>) value;

            // Display the name for the dataset from the dataset index.
            setText(namePathPair.getFirst());
            setToolTipText(namePathPair.getSecond());

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;

        }

    }
}
