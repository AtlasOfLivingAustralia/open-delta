package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;

public class ReExecuteDialog extends JDialog {
    private JPanel _pnlButtons;
    private JButton _btnExecute;
    private JButton _btnCancel;
    private JButton _btnEdit;
    private JScrollPane _scrollPane;
    private JList _listDirectives;

    private IntkeyDirectiveInvocation _directiveToExecute = null;

    public ReExecuteDialog(Frame owner, List<IntkeyDirectiveInvocation> directives) {
        super(owner, true);
        setResizable(false);
        setSize(new Dimension(450, 300));
        setLocationRelativeTo(owner);

        setTitle("Select command to re-execute");

        _pnlButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) _pnlButtons.getLayout();
        flowLayout.setHgap(10);
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);

        _btnExecute = new JButton("Execute");
        _btnExecute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                IntkeyDirectiveInvocation selectedDirective = (IntkeyDirectiveInvocation)_listDirectives.getSelectedValue();
                _directiveToExecute = selectedDirective;
                ReExecuteDialog.this.setVisible(false);
            }
        });
        _pnlButtons.add(_btnExecute);

        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ReExecuteDialog.this.setVisible(false);
            }
        });
        _pnlButtons.add(_btnCancel);

        _btnEdit = new JButton("Edit");
        _pnlButtons.add(_btnEdit);

        _scrollPane = new JScrollPane();
        _scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(_scrollPane, BorderLayout.CENTER);

        _listDirectives = new JList();
        _scrollPane.setViewportView(_listDirectives);

        //Most recently executed directive should appear at the top of the list
        Collections.reverse(directives);
        
        DefaultListModel listModel = new DefaultListModel();
        for (IntkeyDirectiveInvocation dir : directives) {
            listModel.addElement(dir);
        }

        _listDirectives.setModel(listModel);
    }
    
    public IntkeyDirectiveInvocation getDirectiveToExecute() {
        return _directiveToExecute;
    }

}
