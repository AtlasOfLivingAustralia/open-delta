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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class DefineButtonDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -4796092485980089441L;

    @Resource
    String title;
    @Resource
    String enterFileNameCaption;
    @Resource
    String enterCommandsCaption;
    @Resource
    String enterBriefHelpCaption;
    @Resource
    String enterDetailedHelpCaption;
    @Resource
    String enableOnlyIfUsedCaption;
    @Resource
    String enableInAllModesCaption;
    @Resource
    String enableInNormalModeCaption;
    @Resource
    String enableInAdvancedModeCaption;
    @Resource
    String insertSpaceCaption;
    @Resource
    String removeAllCaption;
    @Resource
    String inputValidationError;
    @Resource
    String fileFilterDescription;

    private JPanel _pnlButtons;
    private JTextField _txtFldFileName;
    private JTextField _txtFldCommands;
    private JTextField _txtFldBriefHelp;
    private JTextField _txtFldDetailedHelp;
    private JButton _btnOk;
    private JButton _btnCancel;
    private JButton _btnHelp;
    private JPanel _pnlMain;
    private JPanel _pnlButtonProperties;
    private JLabel _lblEnterNameOf;
    private JPanel _pnlFile;
    private JButton _btnBrowse;
    private JLabel _lblEnterTheCommands;
    private JLabel _lblEnterBriefHelp;
    private JLabel _lblEnterMoreDetailed;
    private JCheckBox _chckbxEnableOnlyIfUsedCharacters;
    private JRadioButton _rdbtnEnableInAll;
    private JRadioButton _rdbtnEnableInNormal;
    private JRadioButton _rdbtnEnableInAdvanced;
    private JPanel _pnlSpaceRemoveAll;
    private JCheckBox _chckbxInsertASpace;
    private JCheckBox _chckbxRemoveAllButtons;

    private boolean _okButtonPressed;
    private boolean _insertSpace;
    private boolean _removeAllButtons;

    public DefineButtonDialog(Frame owner, boolean modal) {
        super(owner, modal);
        setPreferredSize(new Dimension(500, 430));

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(DefineButtonDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(DefineButtonDialog.class, this);

        setTitle(title);

        _okButtonPressed = false;

        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("DefineButtonDialog_OK"));
        _pnlButtons.add(_btnOk);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("DefineButtonDialog_Cancel"));
        _pnlButtons.add(_btnCancel);

        _btnHelp = new JButton("Help");
        _btnHelp.setAction(actionMap.get("DefineButtonDialog_Help"));
        _btnHelp.setEnabled(false);
        _pnlButtons.add(_btnHelp);

        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(5, 0));

        _pnlButtonProperties = new JPanel();
        _pnlButtonProperties.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new CompoundBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), new EmptyBorder(5, 5, 5, 5))));
        _pnlMain.add(_pnlButtonProperties, BorderLayout.NORTH);
        GridBagLayout gbl__pnlButtonProperties = new GridBagLayout();
        gbl__pnlButtonProperties.columnWidths = new int[] { 475, 0 };
        gbl__pnlButtonProperties.rowHeights = new int[] { 14, 23, 14, 20, 14, 20, 14, 0, 23, 23, 23, 23, 0 };
        gbl__pnlButtonProperties.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gbl__pnlButtonProperties.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlButtonProperties.setLayout(gbl__pnlButtonProperties);

        _lblEnterNameOf = new JLabel(enterFileNameCaption);
        _lblEnterNameOf.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc__lblEnterNameOf = new GridBagConstraints();
        gbc__lblEnterNameOf.anchor = GridBagConstraints.WEST;
        gbc__lblEnterNameOf.insets = new Insets(0, 0, 5, 0);
        gbc__lblEnterNameOf.gridx = 0;
        gbc__lblEnterNameOf.gridy = 0;
        _pnlButtonProperties.add(_lblEnterNameOf, gbc__lblEnterNameOf);

        _pnlFile = new JPanel();
        GridBagConstraints gbc__pnlFile = new GridBagConstraints();
        gbc__pnlFile.fill = GridBagConstraints.HORIZONTAL;
        gbc__pnlFile.insets = new Insets(0, 0, 5, 0);
        gbc__pnlFile.gridx = 0;
        gbc__pnlFile.gridy = 1;
        _pnlButtonProperties.add(_pnlFile, gbc__pnlFile);
        _pnlFile.setLayout(new BorderLayout(0, 0));

        _txtFldFileName = new JTextField();
        _pnlFile.add(_txtFldFileName, BorderLayout.CENTER);
        _txtFldFileName.setColumns(10);

        _btnBrowse = new JButton();
        _btnBrowse.setAction(actionMap.get("DefineButtonDialog_Browse"));
        _pnlFile.add(_btnBrowse, BorderLayout.EAST);

        _lblEnterTheCommands = new JLabel(enterCommandsCaption);
        _lblEnterTheCommands.setHorizontalAlignment(SwingConstants.LEFT);
        GridBagConstraints gbc__lblEnterTheCommands = new GridBagConstraints();
        gbc__lblEnterTheCommands.anchor = GridBagConstraints.WEST;
        gbc__lblEnterTheCommands.insets = new Insets(0, 0, 5, 0);
        gbc__lblEnterTheCommands.gridx = 0;
        gbc__lblEnterTheCommands.gridy = 2;
        _pnlButtonProperties.add(_lblEnterTheCommands, gbc__lblEnterTheCommands);

        _txtFldCommands = new JTextField();
        GridBagConstraints gbc__txtFldCommands = new GridBagConstraints();
        gbc__txtFldCommands.fill = GridBagConstraints.HORIZONTAL;
        gbc__txtFldCommands.insets = new Insets(0, 0, 5, 0);
        gbc__txtFldCommands.gridx = 0;
        gbc__txtFldCommands.gridy = 3;
        _pnlButtonProperties.add(_txtFldCommands, gbc__txtFldCommands);
        _txtFldCommands.setColumns(10);

        _lblEnterBriefHelp = new JLabel(enterBriefHelpCaption);
        _lblEnterBriefHelp.setAlignmentY(Component.TOP_ALIGNMENT);
        GridBagConstraints gbc__lblEnterBriefHelp = new GridBagConstraints();
        gbc__lblEnterBriefHelp.anchor = GridBagConstraints.NORTHWEST;
        gbc__lblEnterBriefHelp.insets = new Insets(0, 0, 5, 0);
        gbc__lblEnterBriefHelp.gridx = 0;
        gbc__lblEnterBriefHelp.gridy = 4;
        _pnlButtonProperties.add(_lblEnterBriefHelp, gbc__lblEnterBriefHelp);

        _txtFldBriefHelp = new JTextField();
        GridBagConstraints gbc__txtFldBriefHelp = new GridBagConstraints();
        gbc__txtFldBriefHelp.fill = GridBagConstraints.HORIZONTAL;
        gbc__txtFldBriefHelp.insets = new Insets(0, 0, 5, 0);
        gbc__txtFldBriefHelp.gridx = 0;
        gbc__txtFldBriefHelp.gridy = 5;
        _pnlButtonProperties.add(_txtFldBriefHelp, gbc__txtFldBriefHelp);
        _txtFldBriefHelp.setColumns(10);

        _lblEnterMoreDetailed = new JLabel(enterDetailedHelpCaption);
        GridBagConstraints gbc__lblEnterMoreDetailed = new GridBagConstraints();
        gbc__lblEnterMoreDetailed.anchor = GridBagConstraints.WEST;
        gbc__lblEnterMoreDetailed.insets = new Insets(0, 0, 5, 0);
        gbc__lblEnterMoreDetailed.gridx = 0;
        gbc__lblEnterMoreDetailed.gridy = 6;
        _pnlButtonProperties.add(_lblEnterMoreDetailed, gbc__lblEnterMoreDetailed);

        _txtFldDetailedHelp = new JTextField();
        GridBagConstraints gbc__txtFldDetailedHelp = new GridBagConstraints();
        gbc__txtFldDetailedHelp.insets = new Insets(0, 0, 5, 0);
        gbc__txtFldDetailedHelp.fill = GridBagConstraints.HORIZONTAL;
        gbc__txtFldDetailedHelp.gridx = 0;
        gbc__txtFldDetailedHelp.gridy = 7;
        _pnlButtonProperties.add(_txtFldDetailedHelp, gbc__txtFldDetailedHelp);
        _txtFldDetailedHelp.setColumns(10);

        _chckbxEnableOnlyIfUsedCharacters = new JCheckBox(enableOnlyIfUsedCaption);
        GridBagConstraints gbc__chckbxEnableOnlyIf = new GridBagConstraints();
        gbc__chckbxEnableOnlyIf.anchor = GridBagConstraints.WEST;
        gbc__chckbxEnableOnlyIf.insets = new Insets(0, 0, 5, 0);
        gbc__chckbxEnableOnlyIf.gridx = 0;
        gbc__chckbxEnableOnlyIf.gridy = 8;
        _pnlButtonProperties.add(_chckbxEnableOnlyIfUsedCharacters, gbc__chckbxEnableOnlyIf);

        _rdbtnEnableInAll = new JRadioButton(enableInAllModesCaption);
        GridBagConstraints gbc__rdbtnEnableInAll = new GridBagConstraints();
        gbc__rdbtnEnableInAll.anchor = GridBagConstraints.WEST;
        gbc__rdbtnEnableInAll.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnEnableInAll.gridx = 0;
        gbc__rdbtnEnableInAll.gridy = 9;
        _pnlButtonProperties.add(_rdbtnEnableInAll, gbc__rdbtnEnableInAll);

        _rdbtnEnableInNormal = new JRadioButton(enableInNormalModeCaption);
        GridBagConstraints gbc__rdbtnEnableInNormal = new GridBagConstraints();
        gbc__rdbtnEnableInNormal.anchor = GridBagConstraints.WEST;
        gbc__rdbtnEnableInNormal.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnEnableInNormal.gridx = 0;
        gbc__rdbtnEnableInNormal.gridy = 10;
        _pnlButtonProperties.add(_rdbtnEnableInNormal, gbc__rdbtnEnableInNormal);

        _rdbtnEnableInAdvanced = new JRadioButton(enableInAdvancedModeCaption);
        GridBagConstraints gbc__rdbtnEnableInAdvanced = new GridBagConstraints();
        gbc__rdbtnEnableInAdvanced.anchor = GridBagConstraints.WEST;
        gbc__rdbtnEnableInAdvanced.gridx = 0;
        gbc__rdbtnEnableInAdvanced.gridy = 11;
        _pnlButtonProperties.add(_rdbtnEnableInAdvanced, gbc__rdbtnEnableInAdvanced);

        _pnlSpaceRemoveAll = new JPanel();
        _pnlSpaceRemoveAll.setBorder(new EmptyBorder(0, 10, 0, 0));
        _pnlMain.add(_pnlSpaceRemoveAll, BorderLayout.SOUTH);
        _pnlSpaceRemoveAll.setLayout(new BoxLayout(_pnlSpaceRemoveAll, BoxLayout.Y_AXIS));

        _chckbxInsertASpace = new JCheckBox(insertSpaceCaption);
        _chckbxInsertASpace.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _insertSpace = !_insertSpace;

                if (_insertSpace) {
                    _removeAllButtons = false;
                    _chckbxRemoveAllButtons.setSelected(false);
                }

                updateButtonPropertyControls();
            }
        });
        _pnlSpaceRemoveAll.add(_chckbxInsertASpace);

        _chckbxRemoveAllButtons = new JCheckBox(removeAllCaption);
        _chckbxRemoveAllButtons.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _removeAllButtons = !_removeAllButtons;

                if (_removeAllButtons) {
                    _insertSpace = false;
                    _chckbxInsertASpace.setSelected(false);
                }

                updateButtonPropertyControls();
            }
        });
        _pnlSpaceRemoveAll.add(_chckbxRemoveAllButtons);

        _pnlButtonProperties.setEnabled(false);

        ButtonGroup btnGroup = new ButtonGroup();
        btnGroup.add(_rdbtnEnableInAll);
        btnGroup.add(_rdbtnEnableInNormal);
        btnGroup.add(_rdbtnEnableInAdvanced);

        _rdbtnEnableInAll.setSelected(true);
    }

    @Action
    public void DefineButtonDialog_OK() {
        if (!_insertSpace && !_removeAllButtons) {
            if (StringUtils.isEmpty(_txtFldFileName.getText()) || StringUtils.isEmpty(_txtFldCommands.getText()) || StringUtils.isEmpty(_txtFldCommands.getText())) {
                JOptionPane.showMessageDialog(this, inputValidationError, null, JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        _okButtonPressed = true;
        this.setVisible(false);
    }

    @Action
    public void DefineButtonDialog_Cancel() {
        _okButtonPressed = false;
        this.setVisible(false);
    }

    @Action
    public void DefineButtonDialog_Help() {
        // TODO
    }

    @Action
    public void DefineButtonDialog_Browse() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(fileFilterDescription, new String[] { "bmp", "png" });
        chooser.setFileFilter(filter);

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            _txtFldFileName.setText(selectedFile.getAbsolutePath());
        }
    }

    public boolean wasOkButtonPressed() {
        return _okButtonPressed;
    }

    public boolean isRemoveAllButtons() {
        return _removeAllButtons;
    }

    public boolean isInsertSpace() {
        return _insertSpace;
    }

    public String getImageFilePath() {
        return _txtFldFileName.getText();
    }

    public String getCommands() {
        return _txtFldCommands.getText();
    }

    public String getBriefHelp() {
        return _txtFldBriefHelp.getText();
    }

    public String getDetailedHelp() {
        return _txtFldDetailedHelp.getText();
    }

    public boolean enableIfUsedCharactersOnly() {
        return _chckbxEnableOnlyIfUsedCharacters.isSelected();
    }

    public boolean enableInNormalModeOnly() {
        return _rdbtnEnableInNormal.isSelected();
    }

    public boolean enableInAdvancedModeOnly() {
        return _rdbtnEnableInAdvanced.isSelected();
    }

    private void updateButtonPropertyControls() {
        boolean enabled = !(_insertSpace || _removeAllButtons);

        _lblEnterNameOf.setEnabled(enabled);
        _btnBrowse.setEnabled(enabled);
        _lblEnterTheCommands.setEnabled(enabled);
        _txtFldCommands.setEnabled(enabled);
        _lblEnterBriefHelp.setEnabled(enabled);
        _txtFldBriefHelp.setEnabled(enabled);
        _lblEnterMoreDetailed.setEnabled(enabled);
        _txtFldDetailedHelp.setEnabled(enabled);
        _chckbxEnableOnlyIfUsedCharacters.setEnabled(enabled);
        _rdbtnEnableInAll.setEnabled(enabled);
        _rdbtnEnableInNormal.setEnabled(enabled);
        _rdbtnEnableInAdvanced.setEnabled(enabled);
    }

}
