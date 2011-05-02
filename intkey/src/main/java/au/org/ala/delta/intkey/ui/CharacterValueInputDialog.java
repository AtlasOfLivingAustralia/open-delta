package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;
import java.awt.Dimension;
import javax.swing.JLabel;

public abstract class CharacterValueInputDialog extends JDialog {
    protected JPanel _buttonPanel;
    protected JButton _btnImages;
    protected JButton _btnFullText;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnNotes;
    protected JButton _btnHelp;
    protected JPanel _pnlMain;
    public CharacterValueInputDialog(Frame owner) {
        super(owner, true);
        setSize(new Dimension(500, 150));
        setResizable(false);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        _buttonPanel = new JPanel();
        _buttonPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(_buttonPanel, BorderLayout.SOUTH);
        _buttonPanel.setLayout(new GridLayout(0, 4, 5, 5));
        
        JButton _btnOk = new JButton("OK");
        _buttonPanel.add(_btnOk);
        
        _btnImages = new JButton("Images");
        _buttonPanel.add(_btnImages);
        
        _btnFullText = new JButton("Full Text");
        _buttonPanel.add(_btnFullText);
        
        _btnSearch = new JButton("Search");
        _buttonPanel.add(_btnSearch);
        
        _btnCancel = new JButton("Cancel");
        _buttonPanel.add(_btnCancel);
        
        _btnNotes = new JButton("Notes");
        _buttonPanel.add(_btnNotes);
        
        _btnHelp = new JButton("Help");
        _buttonPanel.add(_btnHelp);
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
    }

}
