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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public abstract class CharacterValueInputDialog extends JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 361631735179369565L;

    public static final String USE_DIRECTIVE_HELP_TOPIC_ID = "directive_use";

    protected JPanel _buttonPanel;
    protected JButton _btnImages;
    protected JButton _btnFullText;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnNotes;
    protected JButton _btnHelp;
    protected JPanel _pnlMain;
    protected JLabel _lblCharacterDescription;
    protected Character _ch;
    protected CharacterFormatter _formatter;
    protected ImageSettings _imageSettings;

    protected boolean _imagesStartScaled;

    protected String _fullCharacterTextCaption;
    protected String _notesCaption;

    /**
     * True if the dialog's OK button has been pressed
     */
    protected boolean _okPressed;

    /**
     * True if the dialog's cancel button has been pressed
     */
    protected boolean _cancelPressed;

    public CharacterValueInputDialog(Frame owner, Character ch, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled, boolean advancedMode) {
        super(owner, true);
        
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(CharacterValueInputDialog.class, this);

        // Have to pull these resource strings out manually as BSAF does not
        // play nicely with
        // class hierarchies.
        _fullCharacterTextCaption = UIUtils.getResourceString("CharacterValueInputDialog.fullCharacterTextCaption");
        _notesCaption = UIUtils.getResourceString("CharacterValueInputDialog.notesCaption");

        getContentPane().setLayout(new BorderLayout(0, 0));

        setResizable(false);
        setPreferredSize(new Dimension(600, 200));

        _imagesStartScaled = imagesStartScaled;
        _ch = ch;
        _imageSettings = imageSettings;

        _buttonPanel = new JPanel();
        _buttonPanel.setBorder(new EmptyBorder(0, 20, 10, 20));
        getContentPane().add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.setLayout(new GridLayout(0, 4, 5, 5));

        JButton _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("characterValueInputDialog_OK"));

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("characterValueInputDialog_Images"));
        if (ch.getImageCount() == 0 || !enableImagesButton) {
            _btnImages.setEnabled(false);
        }

        _btnFullText = new JButton();
        _btnFullText.setAction(actionMap.get("characterValueInputDialog_FullText"));
        _btnFullText.setEnabled(true);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("characterValueInputDialog_Search"));
        _btnSearch.setEnabled(true);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("characterValueInputDialog_Cancel"));

        _btnNotes = new JButton();
        _btnNotes.setAction(actionMap.get("characterValueInputDialog_Notes"));
        _btnNotes.setEnabled(true);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("characterValueInputDialog_Help"));
        
        //Some of the buttons should not be displayed if not in advanced mode
        if (advancedMode) {
            _buttonPanel.add(_btnOk);
            _buttonPanel.add(_btnImages);
            _buttonPanel.add(_btnFullText);
            _buttonPanel.add(_btnSearch);
            _buttonPanel.add(_btnCancel);
            _buttonPanel.add(_btnNotes);
            _buttonPanel.add(_btnHelp);
        } else {
            _buttonPanel.add(_btnOk);
            _buttonPanel.add(_btnCancel);
            _buttonPanel.add(_btnNotes);
            _buttonPanel.add(_btnImages);
        }
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));

        _lblCharacterDescription = new JLabel();
        _lblCharacterDescription.setBorder(new EmptyBorder(0, 0, 5, 0));
        _formatter = new CharacterFormatter(displayNumbering, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);
        _lblCharacterDescription.setText(_formatter.formatCharacterDescription(_ch));
        _pnlMain.add(_lblCharacterDescription, BorderLayout.NORTH);

        _btnImages.setEnabled(!_ch.getImages().isEmpty());

        _btnNotes.setEnabled(StringUtils.isNotBlank(_ch.getNotes()));

        setLocationRelativeTo(owner);
    }

    abstract void handleBtnOKClicked();

    abstract void handleBtnCancelClicked();

    abstract void handleBtnImagesClicked();

    abstract void handleBtnSearchClicked();

    private void displayRTFWindow(String rtfContent, String title) {
        RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(this, new SimpleRtfEditorKit(null), rtfContent, title);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
    }

    // Button action handlers

    @Action
    public void characterValueInputDialog_OK() {
        _okPressed = true;
        handleBtnOKClicked();
    }

    @Action
    public void characterValueInputDialog_Images() {
        try {
            handleBtnImagesClicked();
        } catch (IllegalArgumentException ex) {
            // Display error message if unable to display
            ((Intkey) Application.getInstance()).displayErrorMessage(UIUtils.getResourceString("CouldNotDisplayImage.error", ex.getMessage()));
        }
    }

    @Action
    public void characterValueInputDialog_FullText() {
        String rtfFullText = ReportUtils.generateFullCharacterTextRTF(_ch);
        displayRTFWindow(rtfFullText, _fullCharacterTextCaption);
    }

    @Action
    public void characterValueInputDialog_Search() {
        handleBtnSearchClicked();
    }

    @Action
    public void characterValueInputDialog_Cancel() {
        _cancelPressed = true;
        handleBtnCancelClicked();
    }

    @Action
    public void characterValueInputDialog_Notes() {
        RTFBuilder builder = new RTFBuilder();
        builder.startDocument();
        builder.appendText(_ch.getNotes());
        builder.endDocument();
        displayRTFWindow(builder.toString(), _notesCaption);
    }

    @Action
    public void characterValueInputDialog_Help(ActionEvent e) {
        UIUtils.displayHelpTopic(USE_DIRECTIVE_HELP_TOPIC_ID, this, e);
    }

    public boolean okPressed() {
        return _okPressed;
    }

    public boolean cancelPressed() {
        return _cancelPressed;
    }
}
