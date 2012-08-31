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
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.naming.directory.InitialDirContext;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;

public class DisplayImagesDialog extends IntkeyDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 4419701511123184423L;
    private JPanel _pnlButtons;
    private JPanel _pnlMain;
    private JPanel _pnlImageDisplayMode;
    private JPanel _pnlReportType;

    private boolean _okButtonPressed;

    @Resource
    String title;

    @Resource
    String autoCaption;

    @Resource
    String manualCaption;

    @Resource
    String offCaption;

    @Resource
    String missingImageListCaption;

    @Resource
    String characterImageListCaption;

    @Resource
    String taxonImageListCaption;

    @Resource
    String imageDisplayModeCaption;

    @Resource
    String generateReportCaption;
    private JLabel _lblImageDisplayMode;
    private JRadioButton _rdbtnAuto;
    private JRadioButton _rdbtnManual;
    private JRadioButton _rdbtnOff;
    private JLabel _lblGenerateReport;
    private JRadioButton _rdbtnMissingImageList;
    private JRadioButton _rdbtnCharacterImageList;
    private JRadioButton _rdbtnTaxonImageList;

    public DisplayImagesDialog(Frame owner, ImageDisplayMode initialImageDisplayMode) {
        super(owner, true);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(DisplayImagesDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(DisplayImagesDialog.class, this);

        setTitle(title);

        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(10, 0, 0, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        GridBagLayout gbl__pnlButtons = new GridBagLayout();
        gbl__pnlButtons.columnWidths = new int[] { 65, 0 };
        gbl__pnlButtons.rowHeights = new int[] { 10, 10, 10, 0 };
        gbl__pnlButtons.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl__pnlButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlButtons.setLayout(gbl__pnlButtons);

        JButton btnOk = new JButton();
        btnOk.setAction(actionMap.get("DisplayImagesDialog_OK"));
        GridBagConstraints gbc_btnOk = new GridBagConstraints();
        gbc_btnOk.anchor = GridBagConstraints.SOUTH;
        gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnOk.insets = new Insets(0, 0, 5, 0);
        gbc_btnOk.gridx = 0;
        gbc_btnOk.gridy = 0;
        _pnlButtons.add(btnOk, gbc_btnOk);

        JButton btnCancel = new JButton();
        btnCancel.setAction(actionMap.get("DisplayImagesDialog_Cancel"));
        GridBagConstraints gbc_btnCancel = new GridBagConstraints();
        gbc_btnCancel.anchor = GridBagConstraints.NORTH;
        gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
        gbc_btnCancel.gridx = 0;
        gbc_btnCancel.gridy = 1;
        _pnlButtons.add(btnCancel, gbc_btnCancel);

        JButton btnHelp = new JButton();
        btnHelp.setAction(actionMap.get("DisplayImagesDialog_Help"));
        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.anchor = GridBagConstraints.NORTH;
        gbc_btnHelp.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnHelp.gridx = 0;
        gbc_btnHelp.gridy = 2;
        _pnlButtons.add(btnHelp, gbc_btnHelp);

        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BoxLayout(_pnlMain, BoxLayout.Y_AXIS));

        _pnlImageDisplayMode = new JPanel();
        _pnlImageDisplayMode.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        _pnlMain.add(_pnlImageDisplayMode);
        GridBagLayout gbl__pnlImageDisplayMode = new GridBagLayout();
        gbl__pnlImageDisplayMode.columnWidths = new int[] { 0, 0 };
        gbl__pnlImageDisplayMode.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl__pnlImageDisplayMode.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl__pnlImageDisplayMode.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlImageDisplayMode.setLayout(gbl__pnlImageDisplayMode);

        _lblImageDisplayMode = new JLabel(imageDisplayModeCaption);
        GridBagConstraints gbc__lblImageDisplayMode = new GridBagConstraints();
        gbc__lblImageDisplayMode.insets = new Insets(0, 0, 5, 0);
        gbc__lblImageDisplayMode.gridx = 0;
        gbc__lblImageDisplayMode.gridy = 0;
        _pnlImageDisplayMode.add(_lblImageDisplayMode, gbc__lblImageDisplayMode);

        _rdbtnAuto = new JRadioButton(autoCaption);
        _rdbtnAuto.setMnemonic('A');
        GridBagConstraints gbc__rdbtnAuto = new GridBagConstraints();
        gbc__rdbtnAuto.anchor = GridBagConstraints.WEST;
        gbc__rdbtnAuto.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnAuto.gridx = 0;
        gbc__rdbtnAuto.gridy = 1;
        _pnlImageDisplayMode.add(_rdbtnAuto, gbc__rdbtnAuto);

        _rdbtnManual = new JRadioButton(manualCaption);
        _rdbtnManual.setMnemonic('M');
        GridBagConstraints gbc__rdbtnManual = new GridBagConstraints();
        gbc__rdbtnManual.anchor = GridBagConstraints.WEST;
        gbc__rdbtnManual.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnManual.gridx = 0;
        gbc__rdbtnManual.gridy = 2;
        _pnlImageDisplayMode.add(_rdbtnManual, gbc__rdbtnManual);

        _rdbtnOff = new JRadioButton(offCaption);
        _rdbtnOff.setMnemonic('f');
        GridBagConstraints gbc__rdbtnOff = new GridBagConstraints();
        gbc__rdbtnOff.anchor = GridBagConstraints.WEST;
        gbc__rdbtnOff.gridx = 0;
        gbc__rdbtnOff.gridy = 3;
        _pnlImageDisplayMode.add(_rdbtnOff, gbc__rdbtnOff);

        _pnlReportType = new JPanel();
        _pnlReportType.setBorder(new CompoundBorder(new EmptyBorder(0, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        _pnlMain.add(_pnlReportType);
        GridBagLayout gbl__pnlReportType = new GridBagLayout();
        gbl__pnlReportType.columnWidths = new int[] { 0, 0 };
        gbl__pnlReportType.rowHeights = new int[] { 0, 0, 0, 0, 0 };
        gbl__pnlReportType.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl__pnlReportType.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlReportType.setLayout(gbl__pnlReportType);

        _lblGenerateReport = new JLabel(generateReportCaption);
        GridBagConstraints gbc__lblGenerateReport = new GridBagConstraints();
        gbc__lblGenerateReport.anchor = GridBagConstraints.WEST;
        gbc__lblGenerateReport.insets = new Insets(0, 0, 5, 0);
        gbc__lblGenerateReport.gridx = 0;
        gbc__lblGenerateReport.gridy = 0;
        _pnlReportType.add(_lblGenerateReport, gbc__lblGenerateReport);

        _rdbtnMissingImageList = new JRadioButton(missingImageListCaption);
        _rdbtnMissingImageList.setMnemonic('M');
        GridBagConstraints gbc__rdbtnMissingImageList = new GridBagConstraints();
        gbc__rdbtnMissingImageList.anchor = GridBagConstraints.WEST;
        gbc__rdbtnMissingImageList.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnMissingImageList.gridx = 0;
        gbc__rdbtnMissingImageList.gridy = 1;
        _pnlReportType.add(_rdbtnMissingImageList, gbc__rdbtnMissingImageList);

        _rdbtnCharacterImageList = new JRadioButton(characterImageListCaption);
        _rdbtnCharacterImageList.setMnemonic('h');
        GridBagConstraints gbc__rdbtnCharacterImageList = new GridBagConstraints();
        gbc__rdbtnCharacterImageList.anchor = GridBagConstraints.WEST;
        gbc__rdbtnCharacterImageList.insets = new Insets(0, 0, 5, 0);
        gbc__rdbtnCharacterImageList.gridx = 0;
        gbc__rdbtnCharacterImageList.gridy = 2;
        _pnlReportType.add(_rdbtnCharacterImageList, gbc__rdbtnCharacterImageList);

        _rdbtnTaxonImageList = new JRadioButton(taxonImageListCaption);
        _rdbtnTaxonImageList.setMnemonic('T');
        GridBagConstraints gbc__rdbtnTaxonImageList = new GridBagConstraints();
        gbc__rdbtnTaxonImageList.anchor = GridBagConstraints.WEST;
        gbc__rdbtnTaxonImageList.gridx = 0;
        gbc__rdbtnTaxonImageList.gridy = 3;
        _pnlReportType.add(_rdbtnTaxonImageList, gbc__rdbtnTaxonImageList);

        ButtonGroup group1 = new ButtonGroup();
        group1.add(_rdbtnAuto);
        group1.add(_rdbtnManual);
        group1.add(_rdbtnOff);

        ButtonGroup group2 = new ButtonGroup();
        group2.add(_rdbtnMissingImageList);
        group2.add(_rdbtnCharacterImageList);
        group2.add(_rdbtnTaxonImageList);

        if (initialImageDisplayMode != null) {
            switch (initialImageDisplayMode) {
            case AUTO:
                _rdbtnAuto.setSelected(true);
                break;
            case MANUAL:
                _rdbtnManual.setSelected(true);
                break;
            case OFF:
                _rdbtnOff.setSelected(true);
                break;
            }
        }
    }

    @Action
    public void DisplayImagesDialog_OK() {
        _okButtonPressed = true;
        setVisible(false);
    }

    @Action
    public void DisplayImagesDialog_Cancel() {
        _okButtonPressed = false;
        setVisible(false);
    }

    @Action
    public void DisplayImagesDialog_Help() {

    }

    public boolean wasOkButtonPressed() {
        return _okButtonPressed;
    }

    public ImageDisplayMode getSelectedImageDisplayMode() {
        if (_rdbtnAuto.isSelected()) {
            return ImageDisplayMode.AUTO;
        } else if (_rdbtnManual.isSelected()) {
            return ImageDisplayMode.MANUAL;
        } else {
            return ImageDisplayMode.OFF;
        }
    }

    public DisplayImagesReportType getSelectedReportType() {
        if (_rdbtnMissingImageList.isSelected()) {
            return DisplayImagesReportType.MISSING_IMAGE_LIST;
        } else if (_rdbtnCharacterImageList.isSelected()) {
            return DisplayImagesReportType.CHARACTER_IMAGE_LIST;
        } else if (_rdbtnTaxonImageList.isSelected()) {
            return DisplayImagesReportType.TAXON_IMAGE_LIST;
        } else {
            return null;
        }
    }

}
