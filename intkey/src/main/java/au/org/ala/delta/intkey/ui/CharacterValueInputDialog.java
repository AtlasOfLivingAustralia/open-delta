package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.format.CharacterFormatter;

public abstract class CharacterValueInputDialog extends JDialog {
    protected JPanel _buttonPanel;
    protected JButton _btnImages;
    protected JButton _btnFullText;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnNotes;
    protected JButton _btnHelp;
    protected JPanel _pnlMain;
    protected JLabel _lblCharacterDescription;
    protected Character _ch;
    protected CharacterFormatter _formatter;

    public CharacterValueInputDialog(Frame owner, Character ch) {
        super(owner, true);
        setSize(new Dimension(500, 150));
        setResizable(false);
        setLocationRelativeTo(owner);

        getContentPane().setLayout(new BorderLayout(0, 0));
        
        _ch = ch;
        
        _buttonPanel = new JPanel();
        _buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.setLayout(new GridLayout(0, 4, 5, 5));

        JButton _btnOk = new JButton("OK");
        _btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CharacterValueInputDialog.this.handleBtnOKClicked();
            }
        });
        _buttonPanel.add(_btnOk);

        _btnImages = new JButton("Images");
        _btnImages.setEnabled(false);
        _buttonPanel.add(_btnImages);

        _btnFullText = new JButton("Full Text");
        _btnFullText.setEnabled(false);
        _buttonPanel.add(_btnFullText);

        _btnSearch = new JButton("Search");
        _btnSearch.setEnabled(false);
        _buttonPanel.add(_btnSearch);

        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                CharacterValueInputDialog.this.handleBtnCancelClicked();
            }
        });
        
        _buttonPanel.add(_btnCancel);

        _btnNotes = new JButton("Notes");
        _btnNotes.setEnabled(false);
        _buttonPanel.add(_btnNotes);

        _btnHelp = new JButton("Help");
        _btnHelp.setEnabled(false);
        _buttonPanel.add(_btnHelp);

        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _lblCharacterDescription = new JLabel();
        _lblCharacterDescription.setBorder(new EmptyBorder(0, 0, 5, 0));
        _formatter = new CharacterFormatter(false, false, true, true);
        _lblCharacterDescription.setText(_formatter.formatCharacterDescription(_ch));
        _pnlMain.add(_lblCharacterDescription, BorderLayout.NORTH);
    }

    abstract void handleBtnOKClicked();
    abstract void handleBtnCancelClicked();
}
