package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.NumericCharacter;

public abstract class NumberInputDialog extends CharacterValueInputDialog {
    protected JTextField _txtInput;
    private JLabel _lblUnits;
    
    public NumberInputDialog(Frame owner, NumericCharacter ch) {
        super(owner, ch);
        
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(NumberInputDialog.class);
        resourceMap.injectFields(this);
        
        JPanel panel = new JPanel();
        _pnlMain.add(panel, BorderLayout.CENTER);
        
        _txtInput = new JTextField();
        _txtInput.addActionListener(new ActionListener() {
            
            @Override
            public void actionPerformed(ActionEvent e) {
                NumberInputDialog.this.handleBtnOKClicked();
            }
        });
        panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        panel.add(_txtInput);
        _txtInput.setColumns(30);
        
        _lblUnits = new JLabel();
        panel.add(_lblUnits);
        
        if (ch.getUnits() != null) {
            _lblUnits.setText(ch.getUnits());
        } else {
            _lblUnits.setVisible(false);
        }
    }

}
