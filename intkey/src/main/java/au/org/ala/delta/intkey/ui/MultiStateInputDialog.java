package au.org.ala.delta.intkey.ui;

import java.awt.Frame;

import javax.swing.JDialog;
import java.awt.Dimension;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;

public class MultiStateInputDialog extends CharacterValueInputDialog {

    public MultiStateInputDialog(Frame owner) {
        super(owner);
        setTitle("Select state or states");
        setSize(new Dimension(500, 300));
        
        JLabel lblSelectValue = new JLabel("Select value");
        lblSelectValue.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMain.add(lblSelectValue, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane();
        _pnlMain.add(scrollPane, BorderLayout.CENTER);
        
        JList list = new JList();
        scrollPane.setViewportView(list);
        // TODO Auto-generated constructor stub
    }

}
