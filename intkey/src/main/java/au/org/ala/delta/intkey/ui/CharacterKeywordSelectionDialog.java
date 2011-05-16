package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import au.org.ala.delta.intkey.model.CharacterComparator;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;

public class CharacterKeywordSelectionDialog extends KeywordSelectionDialog {

    private List<Character> _selectedCharacters;
    private IntkeyContext _context;

    public CharacterKeywordSelectionDialog(Dialog owner, IntkeyContext context) {
        super(owner);
        init(context);
    }

    public CharacterKeywordSelectionDialog(Frame owner, IntkeyContext context) {
        super(owner);
        init(context);
    }

    private void init(IntkeyContext context) {
        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        GridLayout gridLayout = (GridLayout) _panelButtons.getLayout();
        gridLayout.setVgap(2);
        
        setTitle("Select Character Keywords");
        List<String> characterKeywords = context.getCharacterKeywords();

        DefaultListModel model = new DefaultListModel();
        for (String keyword : characterKeywords) {
            model.addElement(keyword);
        }
        _list.setModel(model);

        _selectedCharacters = new ArrayList<Character>();
        _context = context;
    }

    @Override
    protected void okBtnPressed() {
        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;
            _selectedCharacters.addAll(_context.getCharactersForKeyword(keyword));
        }
        Collections.sort(_selectedCharacters, new CharacterComparator());
    }

    @Override
    protected void cancelBtnPressed() {
        this.setVisible(false);
    }

    @Override
    protected void listBtnPressed() {
        List<String> selectedKeywords = new ArrayList<String>();
        List<Character> characters = new ArrayList<Character>();

        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;
            characters.addAll(_context.getCharactersForKeyword(keyword));
            selectedKeywords.add(keyword);
        }

        CharacterSelectionDialog charDlg = new CharacterSelectionDialog(this, characters);
        charDlg.setVisible(true);

        List<Character> charsSelectedInDlg = charDlg.getSelectedCharacters();
        if (charsSelectedInDlg.size() > 0) {
            _selectedCharacters.clear();
            _selectedCharacters.addAll(charsSelectedInDlg);
            this.setVisible(false);
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
