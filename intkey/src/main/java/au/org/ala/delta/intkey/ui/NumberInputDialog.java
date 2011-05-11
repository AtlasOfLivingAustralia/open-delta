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
    private JLabel _lblUnits;
    
    public NumberInputDialog(Frame owner, NumericCharacter ch) {
        super(owner, ch);
        setTitle("Enter value or range of values");
        
        JPanel panel = new JPanel();
        _pnlMain.add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 2, 5, 0));
        
        _txtInput = new JTextField();
        panel.add(_txtInput);
        _txtInput.setColumns(10);
        
        _lblUnits = new JLabel();
        panel.add(_lblUnits);
        
        if (ch.getUnits() != null) {
            _lblUnits.setText(ch.getUnits());
        } else {
            _lblUnits.setVisible(false);
        }
    }

}
