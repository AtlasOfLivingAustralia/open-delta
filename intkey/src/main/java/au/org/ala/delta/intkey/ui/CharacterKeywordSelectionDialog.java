package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
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

public class CharacterKeywordSelectionDialog extends KeywordSelectionDialog {

    private Set<Character> _includedCharacters;
    private List<Character> _selectedCharacters;

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

        setTitle(String.format(title, _directiveName));
        _selectedCharacters = new ArrayList<Character>();

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
    }

    @Override
    protected void okBtnPressed() {
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
                CharacterSelectionDialog charDlg = new CharacterSelectionDialog(this, characters, _directiveName, selectedKeyword, _context.getImageSettings());
                charDlg.setVisible(true);

                List<Character> charsSelectedInDlg = charDlg.getSelectedCharacters();
                if (charsSelectedInDlg.size() > 0) {
                    _selectedCharacters.clear();
                    _selectedCharacters.addAll(charsSelectedInDlg);
                    this.setVisible(false);
                }
            }
        }
    }

    @Override
    protected void imagesBtnPressed() {
        // TODO Auto-generated method stub

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
}
