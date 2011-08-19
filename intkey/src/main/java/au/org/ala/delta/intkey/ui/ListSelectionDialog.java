package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

public class ListSelectionDialog extends JDialog {
    
    /**
     * 
     */
    private static final long serialVersionUID = 8010652632956537773L;
    
    protected JScrollPane _scrollPane;
    protected JList _list;
    protected JPanel _panelButtons;
    
    public ListSelectionDialog(Dialog owner) {
        super(owner, true);
        init();
        setLocationRelativeTo(owner);
    }
    
    public ListSelectionDialog(Frame owner) {
        super(owner, true);
        init();
        setLocationRelativeTo(owner);
    }

    private void init() {
        setResizable(false);
        setSize(new Dimension(600, 350));

        _panelButtons = new JPanel();
        getContentPane().add(_panelButtons, BorderLayout.SOUTH);
        
        _scrollPane = new JScrollPane();
        _scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(_scrollPane, BorderLayout.CENTER);
        
        _list = new JList();
        _scrollPane.setViewportView(_list);
    }
}
