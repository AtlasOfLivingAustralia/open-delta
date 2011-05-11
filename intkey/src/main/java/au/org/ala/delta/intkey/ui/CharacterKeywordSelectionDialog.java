package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import au.org.ala.delta.intkey.model.CharacterComparator;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class CharacterKeywordSelectionDialog extends KeywordSelectionDialog {
    
    private List<Character> _selectedCharacters;
    private IntkeyContext _context;
    
    public CharacterKeywordSelectionDialog(Frame owner, IntkeyContext context) {
        super(owner);
        setTitle("Select Character Keywords");
        List<String> characterKeywords = context.getCharacterKeywords();
        
        DefaultListModel model = new DefaultListModel();
        for(String keyword: characterKeywords) {
            model.addElement(keyword);
        }
        _list.setModel(model);
        
        _selectedCharacters = new ArrayList<Character>();
        _context = context;
    }

    @Override
    protected void okBtnPressed() {
        for (Object o: _list.getSelectedValues()) {
            String keyword = (String)o;
            _selectedCharacters.addAll(_context.getCharactersForKeyword(keyword));
        }
        Collections.sort(_selectedCharacters, new CharacterComparator());
    }

    @Override
    protected void listBtnPressed() {
        // TODO Auto-generated method stub
        
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
