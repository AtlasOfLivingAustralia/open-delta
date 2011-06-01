package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Character;

public class CharacterKeywordSelectionDialog extends KeywordSelectionDialog {

    private List<Character> _selectedCharacters;
    private IntkeyContext _context;
    
    // The name of the directive being processed
    private String _directiveName;
    
    @Resource
    String title;

    public CharacterKeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName) {
        super(owner);
        _directiveName = directiveName;
        init(context);
    }

    public CharacterKeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName) {
        super(owner);
        _directiveName = directiveName;
        init(context);
    }

    private void init(IntkeyContext context) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterKeywordSelectionDialog.class);
        resourceMap.injectFields(this);
        
        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        GridLayout gridLayout = (GridLayout) _panelButtons.getLayout();
        gridLayout.setVgap(2);

        setTitle(String.format(title, _directiveName));
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

            CharacterSelectionDialog charDlg = new CharacterSelectionDialog(this, characters, _directiveName, selectedKeyword);
            charDlg.setVisible(true);

            List<Character> charsSelectedInDlg = charDlg.getSelectedCharacters();
            if (charsSelectedInDlg.size() > 0) {
                _selectedCharacters.clear();
                _selectedCharacters.addAll(charsSelectedInDlg);
                this.setVisible(false);
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
