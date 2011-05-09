package au.org.ala.delta.intkey.ui;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Frame;

import javax.swing.JButton;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.intkey.directives.IntkeyDirectiveInvocation;
import java.awt.Dimension;
import javax.swing.border.EtchedBorder;
import javax.swing.border.CompoundBorder;

public class ReExecuteDialog extends JDialog {
    private JPanel _pnlButtons;
    private JButton _btnExecute;
    private JButton _btnCancel;
    private JButton _btnEdit;
    private JScrollPane _scrollPane;
    private JList _listCmds;
    
    public ReExecuteDialog(Frame owner, List<IntkeyDirectiveInvocation> cmds) {
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
        _pnlButtons.add(_btnExecute);
        
        _btnCancel = new JButton("Cancel");
        _pnlButtons.add(_btnCancel);
        
        _btnEdit = new JButton("Edit");
        _pnlButtons.add(_btnEdit);
        
        _scrollPane = new JScrollPane();
        _scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(_scrollPane, BorderLayout.CENTER);
        
        _listCmds = new JList();
        _scrollPane.setViewportView(_listCmds);
        
        ExecutedDirectiveListModel listModel = new ExecutedDirectiveListModel(cmds);
        _listCmds.setModel(listModel);
    }
    
    private class ExecutedDirectiveListModel extends AbstractListModel {

        List<IntkeyDirectiveInvocation> _directives;

        public ExecutedDirectiveListModel(List<IntkeyDirectiveInvocation> directives) {
            _directives = directives;
        }

        @Override
        public int getSize() {
            return _directives.size();
        }

        @Override
        public Object getElementAt(int index) {
            return _directives.get(index).toString();
        }
    }

}
