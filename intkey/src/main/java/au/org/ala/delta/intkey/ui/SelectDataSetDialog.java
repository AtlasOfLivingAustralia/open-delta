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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

public class SelectDataSetDialog extends JDialog {
    
	private static final long serialVersionUID = 1L;
	
	private String _selectedFilePath;
    private boolean _fileSelected;
    
    private JTextField txtFldFilePath;
    
    public SelectDataSetDialog(Frame owner) {
        super(owner, true);
        setMinimumSize(new Dimension(650, 500));
        
        _selectedFilePath = null;
        _fileSelected = false;
        
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        
        setResizable(false);
        setTitle("Select Data Set");
        setSize(new Dimension(650, 500));
        setName("SelectDataSetDialog");
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        JLabel lblNewLabel = new JLabel("Select by title:");
        lblNewLabel.setBorder(new EmptyBorder(10, 10, 0, 0));
        lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblNewLabel.setName("lblSelectByTitle");
        getContentPane().add(lblNewLabel, BorderLayout.NORTH);
        
        JPanel pnlList = new JPanel();
        pnlList.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(pnlList, BorderLayout.CENTER);
        pnlList.setLayout(new BorderLayout(0, 0));
        
        JScrollPane scrollPane = new JScrollPane();
        pnlList.add(scrollPane, BorderLayout.CENTER);
        
        JList listDataSets = new JList();
        scrollPane.setViewportView(listDataSets);
        
        JPanel pnlListButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) pnlListButtons.getLayout();
        flowLayout.setHgap(20);
        pnlList.add(pnlListButtons, BorderLayout.SOUTH);
        
        JButton btnOk = new JButton("Ok");
        pnlListButtons.add(btnOk);
        
        JButton btnCancel = new JButton("Cancel");
        pnlListButtons.add(btnCancel);
        
        JButton btnHelp = new JButton("Help");
        pnlListButtons.add(btnHelp);
        
        JPanel pnlSelectFile = new JPanel();
        pnlSelectFile.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSelectFile.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(pnlSelectFile, BorderLayout.SOUTH);
        pnlSelectFile.setLayout(new BorderLayout(0, 0));
        
        JLabel lblSelectByName = new JLabel("Select by name of initialization file:");
        lblSelectByName.setBorder(new EmptyBorder(0, 0, 10, 0));
        lblSelectByName.setHorizontalAlignment(SwingConstants.LEFT);
        lblSelectByName.setFont(new Font("Tahoma", Font.PLAIN, 15));
        pnlSelectFile.add(lblSelectByName, BorderLayout.NORTH);
        
        txtFldFilePath = new JTextField();
        pnlSelectFile.add(txtFldFilePath, BorderLayout.CENTER);
        txtFldFilePath.setColumns(10);
        
        JPanel pnlBrowseButton = new JPanel();
        pnlSelectFile.add(pnlBrowseButton, BorderLayout.SOUTH);
        
        JButton btnBrowse = new JButton("Browse...");
        btnBrowse.setAction(actionMap.get("browseForFile"));
        pnlBrowseButton.add(btnBrowse);
    }

    public String getSelectedFilePath() {
        return _selectedFilePath;
    }

    public boolean isFileSelected() {
        return _fileSelected;
    }
    
    @Action
    public void browseForFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data Initialization Files (*.ini, *.ink)", "ini", "ink");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(SelectDataSetDialog.this);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           _selectedFilePath = chooser.getSelectedFile().getAbsolutePath();
           _fileSelected =  true;
           //SelectDataSetDialog.this.
           SelectDataSetDialog.this.setVisible(false);
           //SelectDataSetDialog.this.dispose();
        }
    }

}
