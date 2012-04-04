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
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;

public class CharacterKeywordSelectionDialog extends KeywordSelectionDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1417718500077724253L;

    private List<Character> _includedCharacters;
    private List<Character> _selectedCharacters;
    private boolean _displayCharacterNumbering;

    @Resource
    String title;

    @Resource
    String selectFromAllCharactersCaption;

    @Resource
    String selectFromIncludedCharactersCaption;

    @Resource
    String allCharactersInSelectedSetExcludedCaption;

    public CharacterKeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedCharactersOnly) {
        super(owner, context, directiveName);
        init(context, permitSelectionFromIncludedCharactersOnly);
    }

    public CharacterKeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedCharactersOnly) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        init(context, permitSelectionFromIncludedCharactersOnly);
    }

    private void init(IntkeyContext context, boolean permitSelectionFromIncludedCharactersOnly) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterKeywordSelectionDialog.class);
        resourceMap.injectFields(this);

        setTitle(MessageFormat.format(title, _directiveName));
        _selectedCharacters = null;

        List<String> characterKeywords = context.getCharacterKeywords();

        DefaultListModel model = new DefaultListModel();
        for (String keyword : characterKeywords) {
            model.addElement(keyword);
        }
        _list.setModel(model);

        _includedCharacters = context.getIncludedCharacters();

        _rdbtnSelectFromAll.setText(selectFromAllCharactersCaption);
        _rdbtnSelectFromIncluded.setText(selectFromIncludedCharactersCaption);

        if (!permitSelectionFromIncludedCharactersOnly || _includedCharacters.size() == context.getDataset().getNumberOfCharacters()) {
            _panelRadioButtons.setVisible(false);
            _selectFromIncluded = false;
        } else {
            _rdbtnSelectFromIncluded.setSelected(true);
            _selectFromIncluded = true;
        }

        _displayCharacterNumbering = context.displayNumbering();
    }

    @Override
    protected void okBtnPressed() {
        _selectedCharacters = new ArrayList<Character>();
        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;

            List<Character> characters = _context.getCharactersForKeyword(keyword);

            if (_selectFromIncluded) {
                characters.retainAll(_includedCharacters);
            }

            _selectedCharacters.addAll(characters);
        }
        Collections.sort(_selectedCharacters);
        this.setVisible(false);
    }

    @Override
    protected void cancelBtnPressed() {
        this.setVisible(false);
    }

    @Override
    protected void listBtnPressed() {
        if (_list.getSelectedValue() != null) {

            List<Character> characters = new ArrayList<Character>();
            String selectedKeyword = (String) _list.getSelectedValue();
            characters.addAll(_context.getCharactersForKeyword(selectedKeyword));

            if (_selectFromIncluded) {
                characters.retainAll(_includedCharacters);
            }

            if (characters.isEmpty()) {
                JOptionPane.showMessageDialog(this, allCharactersInSelectedSetExcludedCaption, title, JOptionPane.ERROR_MESSAGE);
            } else {
                CharacterSelectionDialog charDlg = new CharacterSelectionDialog(this, characters, _directiveName, selectedKeyword, _context.getImageSettings(), _displayCharacterNumbering);
                charDlg.setVisible(true);

                List<Character> charsSelectedInDlg = charDlg.getSelectedCharacters();
                if (charsSelectedInDlg != null && charsSelectedInDlg.size() > 0) {
                    _selectedCharacters.clear();
                    _selectedCharacters.addAll(charsSelectedInDlg);
                    this.setVisible(false);
                }
            }
        }
    }

    @Override
    protected void imagesBtnPressed() {
        List<Image> characterKeywordImages = _context.getDataset().getCharacterKeywordImages();
        if (characterKeywordImages != null && !characterKeywordImages.isEmpty()) {
            ImageDialog dlg = new ImageDialog(this, _context.getImageSettings(), true, _context.displayScaled());
            dlg.setImages(characterKeywordImages);
            dlg.setVisible(true);

            if (dlg.okButtonPressed() && !dlg.getSelectedKeywords().isEmpty()) {
                Set<String> selectedKeywords = dlg.getSelectedKeywords();
                for (String keyword : selectedKeywords) {
                    List<Character> characters = _context.getCharactersForKeyword(keyword);

                    if (_selectFromIncluded) {
                        characters.retainAll(_includedCharacters);
                    }

                    _selectedCharacters.addAll(characters);
                }
                Collections.sort(_selectedCharacters);
                this.setVisible(false);
            }
        }
    }

    @Override
    protected void searchBtnPressed() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void helpBtnPressed() {
        // TODO Auto-generated method stub

    }

    public List<Character> getSelectedCharacters() {
        return _selectedCharacters;
    }

    @Override
    public int searchForText(String searchText, int startingIndex) {
        // TODO Auto-generated method stub
        return -1;
    }
}
