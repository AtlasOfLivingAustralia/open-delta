package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.Character;
import java.awt.Dimension;

public class CharacterSelectionDialog extends ListSelectionDialog {

    private List<Character> _allCharacters;
    private List<Character> _selectedCharacters;
    private JButton _btnOk;
    private JButton _btnSelectAll;
    private JButton _btnKeywords;
    private JButton _btnImages;
    private JButton _btnSearch;
    private JButton _btnCancel;
    private JButton _btnDeselectAll;
    private JButton _btnFullText;
    private JButton _btnNotes;
    private JButton _btnHelp;

    private CharacterListModel _listModel;

    /**
     * @wbp.parser.constructor
     */
    public CharacterSelectionDialog(Dialog owner, List<Character> characters) {
        super(owner);
        setResizable(false);
        setSize(new Dimension(500, 300));
        init(characters);
    }

    public CharacterSelectionDialog(Frame owner, List<Character> characters) {
        super(owner);
        init(characters);
    }

    private void init(List<Character> characters) {
        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        _panelButtons.setLayout(new GridLayout(0, 5, 5, 0));

        _btnOk = new JButton("OK");
        _btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleOkPressed();
            }
        });
        _panelButtons.add(_btnOk);

        _btnSelectAll = new JButton("Select All");
        _btnSelectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleSelectAll();
            }
        });
        _panelButtons.add(_btnSelectAll);

        _btnKeywords = new JButton("Keywords");
        _btnKeywords.setEnabled(false);
        _panelButtons.add(_btnKeywords);

        _btnImages = new JButton("Images");
        _btnImages.setEnabled(false);
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton("Search");
        _btnSearch.setEnabled(false);
        _panelButtons.add(_btnSearch);

        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleCancelPressed();
            }
        });
        _panelButtons.add(_btnCancel);

        _btnDeselectAll = new JButton("Deselect All");
        _btnDeselectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                handleDeselectAll();
            }
        });
        _panelButtons.add(_btnDeselectAll);

        _btnFullText = new JButton("Full Text");
        _btnFullText.setEnabled(false);
        _panelButtons.add(_btnFullText);

        _btnNotes = new JButton("Notes");
        _btnNotes.setEnabled(false);
        _panelButtons.add(_btnNotes);

        _btnHelp = new JButton("Help");
        _btnHelp.setEnabled(false);
        _panelButtons.add(_btnHelp);

        _selectedCharacters = new ArrayList<Character>();

        if (characters != null) {
            _listModel = new CharacterListModel(characters);
            _list.setModel(_listModel);
        }
    }

    private void handleOkPressed() {
        for (int i: _list.getSelectedIndices()) {
            _selectedCharacters.add(_listModel.getCharacterAt(i));
        }

        this.setVisible(false);
    }

    private void handleCancelPressed() {
        this.setVisible(false);
    }

    private void handleSelectAll() {
        _list.setSelectionInterval(0, _listModel.getSize());
    }

    private void handleDeselectAll() {
        _list.clearSelection();
    }

    public List<Character> getSelectedCharacters() {
        return new ArrayList<Character>(_selectedCharacters);
    }
}
