package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;

import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.GridBagLayout;
import javax.swing.JButton;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.model.IntkeyContext;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FindInCharactersDialog extends JDialog {
    private JPanel _pnlMain;
    private JPanel _pnlMainTop;
    private JButton _btnDone;
    private JButton _btnPrevious;
    private JButton _btnFindNext;
    private JPanel _pnlInnerButtons;
    private JPanel _pnlButtons;
    private JCheckBox _chckbxSearchUsedCharacters;
    private JCheckBox _chckbxSearchStates;
    private JPanel _pnlMainBottom;
    private JTextField _textField;
    private JLabel lblEnterSearchString;

    private javax.swing.Action _findAction;
    private javax.swing.Action _nextAction;

    private Intkey _intkeyApp;
    private IntkeyContext _context;

    private int _numMatchedCharacters;
    private int _currentMatchedCharacter;

    public FindInCharactersDialog(Intkey intkeyApp, IntkeyContext context) {
        super(intkeyApp.getMainFrame(), false);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(FindInCharactersDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(FindInCharactersDialog.class, this);

        _intkeyApp = intkeyApp;

        _numMatchedCharacters = 0;
        _currentMatchedCharacter = -1;

        _findAction = actionMap.get("findCharacters");
        _nextAction = actionMap.get("nextCharacter");

        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));

        _pnlMainTop = new JPanel();
        _pnlMain.add(_pnlMainTop, BorderLayout.NORTH);
        _pnlMainTop.setLayout(new BoxLayout(_pnlMainTop, BoxLayout.Y_AXIS));

        lblEnterSearchString = new JLabel("Enter search string:");
        lblEnterSearchString.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMainTop.add(lblEnterSearchString);

        _textField = new JTextField();
        _pnlMainTop.add(_textField);
        _textField.setColumns(10);
        _textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                reset();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                reset();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reset();
            }
        });

        _pnlMainBottom = new JPanel();
        _pnlMainBottom.setBorder(new EmptyBorder(20, 0, 0, 0));
        _pnlMain.add(_pnlMainBottom, BorderLayout.CENTER);
        _pnlMainBottom.setLayout(new BoxLayout(_pnlMainBottom, BoxLayout.Y_AXIS));

        _chckbxSearchStates = new JCheckBox("Search states");
        _pnlMainBottom.add(_chckbxSearchStates);

        _chckbxSearchUsedCharacters = new JCheckBox("Search used characters");
        _pnlMainBottom.add(_chckbxSearchUsedCharacters);

        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 0, 0, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        _pnlButtons.setLayout(new BorderLayout(0, 0));

        _pnlInnerButtons = new JPanel();
        _pnlButtons.add(_pnlInnerButtons, BorderLayout.NORTH);
        GridBagLayout gbl__pnlInnerButtons = new GridBagLayout();
        gbl__pnlInnerButtons.columnWidths = new int[] { 0, 0 };
        gbl__pnlInnerButtons.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl__pnlInnerButtons.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl__pnlInnerButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlInnerButtons.setLayout(gbl__pnlInnerButtons);

        _btnFindNext = new JButton();
        _btnFindNext.setAction(_findAction);

        GridBagConstraints gbc__btnFindNext = new GridBagConstraints();
        gbc__btnFindNext.fill = GridBagConstraints.HORIZONTAL;
        gbc__btnFindNext.insets = new Insets(0, 0, 5, 0);
        gbc__btnFindNext.gridx = 0;
        gbc__btnFindNext.gridy = 0;
        _pnlInnerButtons.add(_btnFindNext, gbc__btnFindNext);

        _btnPrevious = new JButton();
        _btnPrevious.setAction(actionMap.get("previousCharacter"));
        _btnPrevious.setEnabled(false);

        GridBagConstraints gbc__btnPrevious = new GridBagConstraints();
        gbc__btnPrevious.insets = new Insets(0, 0, 5, 0);
        gbc__btnPrevious.gridx = 0;
        gbc__btnPrevious.gridy = 1;
        _pnlInnerButtons.add(_btnPrevious, gbc__btnPrevious);

        _btnDone = new JButton("Done");
        _btnDone.setAction(actionMap.get("findCharactersDone"));

        GridBagConstraints gbc__btnDone = new GridBagConstraints();
        gbc__btnDone.fill = GridBagConstraints.HORIZONTAL;
        gbc__btnDone.gridx = 0;
        gbc__btnDone.gridy = 2;
        _pnlInnerButtons.add(_btnDone, gbc__btnDone);

        this.pack();
    }

    @Action
    public void findCharacters() {

        String searchText = _textField.getText();
        boolean searchStates = _chckbxSearchStates.isSelected();
        boolean searchUsedCharacters = _chckbxSearchUsedCharacters.isSelected();
        if (!StringUtils.isEmpty(searchText)) {
            _numMatchedCharacters = _intkeyApp.findCharacters(searchText, searchStates, searchUsedCharacters);

            if (_numMatchedCharacters > 0) {
                _currentMatchedCharacter = 0;
                characterSelectionUpdated();
            } else {
                JOptionPane.showMessageDialog(this, "No characters found");
            }
        }
    }

    @Action
    public void nextCharacter() {
        if (_currentMatchedCharacter < (_numMatchedCharacters - 1)) {
            _currentMatchedCharacter++;
            characterSelectionUpdated();
        }
    }

    @Action
    public void previousCharacter() {
        if (_currentMatchedCharacter > 0) {
            _currentMatchedCharacter--;
            characterSelectionUpdated();
        }
    }

    private void characterSelectionUpdated() {
        _intkeyApp.selectCurrentMatchedCharacter(_currentMatchedCharacter);
        _btnFindNext.setAction(_nextAction);

        _btnPrevious.setEnabled(_currentMatchedCharacter > 0);
        _btnFindNext.setEnabled(_currentMatchedCharacter < (_numMatchedCharacters - 1));
    }

    @Action
    public void findCharactersDone() {
        this.setVisible(false);
    }

    private void reset() {
        System.out.println("calling reset!");
        if (_numMatchedCharacters > 0) {
            _btnFindNext.setAction(_findAction);
            _btnFindNext.setEnabled(true);
            _btnPrevious.setEnabled(false);
            
            _numMatchedCharacters = 0;
            _currentMatchedCharacter = -1;
        }
    }
}
