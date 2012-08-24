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

import java.awt.Frame;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.intkey.model.FormattingUtils;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.image.ImageSettings;

public class IntegerInputDialog extends NumberInputDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1248724422772112737L;

    private Set<Integer> _inputData;

    @Resource
    String title;

    @Resource
    String validationErrorMessage;

    @Resource
    String validationErrorTitle;

    public IntegerInputDialog(Frame owner, IntegerCharacter ch, Set<Integer> initialValues, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton, imagesStartScaled);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(IntegerInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        // Fill the input text box with any previously set values for the
        // character.
        if (initialValues != null) {
            _txtInput.setText(FormattingUtils.formatIntegerValuesAsString(initialValues, ch.getMinimumValue(), ch.getMaximumValue()));
            _txtInput.setSelectionStart(0);
            _txtInput.requestFocusInWindow();
            _txtInput.setSelectionStart(0);
            _txtInput.setSelectionEnd(_txtInput.getText().length());
        }

        _inputData = null;
    }

    @Override
    void handleBtnOKClicked() {
        String inputTxt = _txtInput.getText();
        if (inputTxt.length() > 0) {
            try {
                _inputData = ParsingUtils.parseMultistateOrIntegerCharacterValue(inputTxt);
                this.setVisible(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            // No value supplied, return empty set.
            _inputData = new HashSet<Integer>();
            _okPressed = true;
            this.setVisible(false);
        }
    }

    @Override
    void handleBtnImagesClicked() {
        CharacterImageDialog dlg = new CharacterImageDialog(this, Arrays.asList(new au.org.ala.delta.model.Character[] { _ch }), null, _imageSettings, true, true, _imagesStartScaled);
        dlg.displayImagesForCharacter(_ch);
        dlg.showImage(0);
        dlg.setVisible(true);

        try {
            Set<Integer> inputValuesFromImageDialog = dlg.getInputIntegerValues();
            if (inputValuesFromImageDialog != null && !inputValuesFromImageDialog.isEmpty()) {
                _inputData = inputValuesFromImageDialog;
                this.setVisible(false);
            }
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns the integers specified with the dialog, or null if the dialog was
     * closed with the cancel button.
     * 
     * @return
     */
    public Set<Integer> getInputData() {
        return _inputData;
    }

    @Override
    void handleBtnCancelClicked() {
        _inputData = null;
        this.setVisible(false);
    }

}
