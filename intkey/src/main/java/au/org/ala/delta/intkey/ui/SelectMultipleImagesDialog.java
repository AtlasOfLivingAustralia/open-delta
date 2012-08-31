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

import java.awt.Dialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;

public class SelectMultipleImagesDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 3089705977409496966L;
    private JPanel _pnlButtons;
    private JPanel _pnlMain;
    private JPanel _pnlOptions;
    private JPanel _pnlSubjectList;

    public SelectMultipleImagesDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        
        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);
        
        JButton btnOk = new JButton("OK");
        _pnlButtons.add(btnOk);
        
        JButton btnCancel = new JButton("Cancel");
        _pnlButtons.add(btnCancel);
        
        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _pnlOptions = new JPanel();
        _pnlOptions.setBorder(new EmptyBorder(20, 20, 0, 0));
        _pnlMain.add(_pnlOptions, BorderLayout.WEST);
        GridBagLayout gbl__pnlOptions = new GridBagLayout();
        gbl__pnlOptions.columnWidths = new int[]{327, 0};
        gbl__pnlOptions.rowHeights = new int[]{40, 40, 40, 60, 0};
        gbl__pnlOptions.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl__pnlOptions.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        _pnlOptions.setLayout(gbl__pnlOptions);
        
        JRadioButton rdbtnAllImagesOf = new JRadioButton("All images of the current taxon");
        GridBagConstraints gbc_rdbtnAllImagesOf = new GridBagConstraints();
        gbc_rdbtnAllImagesOf.fill = GridBagConstraints.BOTH;
        gbc_rdbtnAllImagesOf.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnAllImagesOf.gridx = 0;
        gbc_rdbtnAllImagesOf.gridy = 0;
        _pnlOptions.add(rdbtnAllImagesOf, gbc_rdbtnAllImagesOf);
        
        JRadioButton rdbtnNewRadioButton = new JRadioButton("First image of all selected taxa");
        GridBagConstraints gbc_rdbtnNewRadioButton = new GridBagConstraints();
        gbc_rdbtnNewRadioButton.fill = GridBagConstraints.BOTH;
        gbc_rdbtnNewRadioButton.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnNewRadioButton.gridx = 0;
        gbc_rdbtnNewRadioButton.gridy = 1;
        _pnlOptions.add(rdbtnNewRadioButton, gbc_rdbtnNewRadioButton);
        
        JRadioButton rdbtnAllImagesOf_1 = new JRadioButton("All images of selected taxa");
        GridBagConstraints gbc_rdbtnAllImagesOf_1 = new GridBagConstraints();
        gbc_rdbtnAllImagesOf_1.fill = GridBagConstraints.BOTH;
        gbc_rdbtnAllImagesOf_1.insets = new Insets(0, 0, 5, 0);
        gbc_rdbtnAllImagesOf_1.gridx = 0;
        gbc_rdbtnAllImagesOf_1.gridy = 2;
        _pnlOptions.add(rdbtnAllImagesOf_1, gbc_rdbtnAllImagesOf_1);
        
        JCheckBox chckbxCloseAllOpen = new JCheckBox("Close all open windows first");
        GridBagConstraints gbc_chckbxCloseAllOpen = new GridBagConstraints();
        gbc_chckbxCloseAllOpen.anchor = GridBagConstraints.SOUTH;
        gbc_chckbxCloseAllOpen.fill = GridBagConstraints.HORIZONTAL;
        gbc_chckbxCloseAllOpen.gridx = 0;
        gbc_chckbxCloseAllOpen.gridy = 3;
        _pnlOptions.add(chckbxCloseAllOpen, gbc_chckbxCloseAllOpen);
        
        _pnlSubjectList = new JPanel();
        _pnlSubjectList.setBorder(new EmptyBorder(20, 0, 0, 20));
        _pnlMain.add(_pnlSubjectList);
        GridBagLayout gbl__pnlSubjectList = new GridBagLayout();
        gbl__pnlSubjectList.columnWidths = new int[]{238, 0};
        gbl__pnlSubjectList.rowHeights = new int[]{40, 114, 0};
        gbl__pnlSubjectList.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl__pnlSubjectList.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
        _pnlSubjectList.setLayout(gbl__pnlSubjectList);
        
        JLabel lblSelectBySubject = new JLabel("Select by subject");
        GridBagConstraints gbc_lblSelectBySubject = new GridBagConstraints();
        gbc_lblSelectBySubject.fill = GridBagConstraints.HORIZONTAL;
        gbc_lblSelectBySubject.insets = new Insets(0, 0, 5, 0);
        gbc_lblSelectBySubject.gridx = 0;
        gbc_lblSelectBySubject.gridy = 0;
        _pnlSubjectList.add(lblSelectBySubject, gbc_lblSelectBySubject);
        
        JList list = new JList();
        GridBagConstraints gbc_list = new GridBagConstraints();
        gbc_list.fill = GridBagConstraints.BOTH;
        gbc_list.gridx = 0;
        gbc_list.gridy = 1;
        _pnlSubjectList.add(list, gbc_list);
        // TODO Auto-generated constructor stub
    }

}
