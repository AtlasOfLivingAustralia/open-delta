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

import javax.swing.JOptionPane;

import org.apache.commons.lang.math.FloatRange;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.util.Utils;

public class RealInputDialog extends NumberInputDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -3565062911844305863L;

    private FloatRange _inputData;

    @Resource
    String title;

    @Resource
    String validationErrorMessage;

    @Resource
    String validationErrorTitle;

    public RealInputDialog(Frame owner, RealCharacter ch, FloatRange initialValues, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton, imagesStartScaled);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(RealInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);
        
        // Fill the input text box with any previously set values for the
        // character.
        if (initialValues != null) {
            _txtInput.setText(Utils.formatFloatRangeAsString(initialValues));
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
                _inputData = ParsingUtils.parseRealCharacterValue(inputTxt);
                setVisible(false);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
            }
        } else {
            setVisible(false);
        }
    }

    @Override
    void handleBtnCancelClicked() {
        _inputData = null;
        this.setVisible(false);
    }

    @Override
    void handleBtnImagesClicked() {
        CharacterImageDialog dlg = new CharacterImageDialog(this, Arrays.asList(new au.org.ala.delta.model.Character[] { _ch }), _imageSettings, true, true, _imagesStartScaled);
        dlg.displayImagesForCharacter(_ch);
        dlg.showImage(0);
        dlg.setVisible(true);

        try {
            FloatRange rangeFromImageDialog = dlg.getInputRealValues();
            if (rangeFromImageDialog != null) {
                _inputData = rangeFromImageDialog;
                this.setVisible(false);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, validationErrorMessage, validationErrorTitle, JOptionPane.ERROR_MESSAGE);
        }
    }

    public FloatRange getInputData() {
        return _inputData;
    }

}
