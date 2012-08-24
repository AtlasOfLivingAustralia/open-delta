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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.image.ImageSettings;

public class TextInputDialog extends CharacterValueInputDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 1140659731710865780L;

    private JPanel _pnlTxtFld;
    private JTextField _txtInput;
    private List<String> _inputData;

    @Resource
    String title;

    public TextInputDialog(Frame owner, TextCharacter ch, List<String> initialValues, ImageSettings imageSettings, boolean displayNumbering, boolean enableImagesButton, boolean imagesStartScaled, boolean advancedMode) {
        super(owner, ch, imageSettings, displayNumbering, enableImagesButton, imagesStartScaled, advancedMode);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TextInputDialog.class);
        resourceMap.injectFields(this);

        setTitle(title);

        _pnlTxtFld = new JPanel();
        _pnlMain.add(_pnlTxtFld, BorderLayout.CENTER);
        _pnlTxtFld.setLayout(new BorderLayout(0, 0));

        _txtInput = new JTextField();
        _pnlTxtFld.add(_txtInput, BorderLayout.NORTH);
        _txtInput.setColumns(10);
        _txtInput.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TextInputDialog.this.handleBtnOKClicked();
            }
        });

        _btnSearch.setEnabled(false);

        // Fill the input text box with any previously set values for the
        // character.
        if (initialValues != null) {
            _txtInput.setText(StringUtils.join(initialValues, "/"));
            _txtInput.setSelectionStart(0);
            _txtInput.requestFocusInWindow();
            _txtInput.setSelectionStart(0);
            _txtInput.setSelectionEnd(_txtInput.getText().length());
        }

        _inputData = new ArrayList<String>();
    }

    @Override
    void handleBtnOKClicked() {
        String data = _txtInput.getText();

        for (String str : data.split("/")) {
            if (str.length() > 0) {
                _inputData.add(str);
            }
        }

        setVisible(false);
    }

    /**
     * @return A list of input text values, or null if the dialog was closed
     *         using the cancel button.
     */
    public List<String> getInputData() {
        return _inputData;
    }

    @Override
    void handleBtnCancelClicked() {
        _inputData = null;
        this.setVisible(false);
    }

    @Override
    void handleBtnImagesClicked() {
        CharacterImageDialog dlg = new CharacterImageDialog(this, Arrays.asList(new au.org.ala.delta.model.Character[] { _ch }), null, _imageSettings, true, true, _imagesStartScaled);
        dlg.displayImagesForCharacter(_ch);
        dlg.showImage(0);
        dlg.setVisible(true);

        _inputData = dlg.getInputTextValues();
        if (_inputData != null && !_inputData.isEmpty()) {
            _okPressed = true;
            this.setVisible(false);
        }
    }

    @Override
    void handleBtnSearchClicked() {
        // Text input dialog cannot be searched - do nothing
    }

}
