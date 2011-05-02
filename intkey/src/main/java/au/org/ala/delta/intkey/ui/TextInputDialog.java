package au.org.ala.delta.intkey.ui;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Dimension;
import javax.swing.UIManager;

public class TextInputDialog extends CharacterValueInputDialog {
    private JPanel _pnlTxtFld;
    private JTextField _txtInput;
    public TextInputDialog(Frame owner) {
        super(owner);
        setTitle("Enter text");
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _pnlTxtFld = new JPanel();
        _pnlMain.add(_pnlTxtFld, BorderLayout.CENTER);
        _pnlTxtFld.setLayout(new BorderLayout(0, 0));
        
        _txtInput = new JTextField();
        _pnlTxtFld.add(_txtInput, BorderLayout.NORTH);
        _txtInput.setText("foo");
        _txtInput.setColumns(10);
        
        JLabel lblEnterTextThere = new JLabel("Enter text there");
        _pnlMain.add(lblEnterTextThere, BorderLayout.NORTH);
        lblEnterTextThere.setBorder(new EmptyBorder(0, 0, 5, 0));
    }

}
