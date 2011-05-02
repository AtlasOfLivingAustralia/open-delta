package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.border.EmptyBorder;

public class NumberInputDialog extends CharacterValueInputDialog {
    private JTextField txtFo;

    public NumberInputDialog(Frame owner) {
        super(owner);
        setTitle("Enter value or range of values");
        
        JLabel lblEnterNumberValue = new JLabel("Enter number value");
        lblEnterNumberValue.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMain.add(lblEnterNumberValue, BorderLayout.NORTH);
        
        JPanel panel = new JPanel();
        _pnlMain.add(panel, BorderLayout.CENTER);
        panel.setLayout(new GridLayout(0, 2, 5, 0));
        
        txtFo = new JTextField();
        txtFo.setText("fo");
        panel.add(txtFo);
        txtFo.setColumns(10);
        
        JLabel lblUnits = new JLabel("units");
        panel.add(lblUnits);
        // TODO Auto-generated constructor stub
    }

}
