package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.model.NumericCharacter;

public abstract class NumberInputDialog extends CharacterValueInputDialog {
    protected JTextField _txtInput;
    private JLabel _lblCharacterDescription;
    private JLabel _lblUnits;
    protected NumericCharacter _char;
    
    public NumberInputDialog(Frame owner, NumericCharacter ch) {
        super(owner);
        setTitle("Enter value or range of values");
        
        _lblCharacterDescription = new JLabel();
        _lblCharacterDescription.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMain.add(_lblCharacterDescription, BorderLayout.NORTH);
        
        JPanel panel = new JPanel();
        _pnlMain.add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 2, 5, 0));
        
        _txtInput = new JTextField();
        _txtInput.setText("fo");
        panel.add(_txtInput);
        _txtInput.setColumns(10);
        
        _lblUnits = new JLabel();
        panel.add(_lblUnits);
        
        _char = ch;
        _lblCharacterDescription.setText(_char.getDescription());
        
        if (_char.getUnits() != null) {
            _lblUnits.setText(_char.getUnits());
        } else {
            _lblUnits.setVisible(false);
        }
    }

}
