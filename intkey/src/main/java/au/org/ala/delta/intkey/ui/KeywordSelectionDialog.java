package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.Dimension;

public abstract class KeywordSelectionDialog extends JDialog {
    protected boolean _okPressed = false;
    protected JPanel _panelButtons;
    protected JButton _btnOk;
    protected JButton _btnDeselectAll;
    protected JButton _btnList;
    protected JButton _btnImages;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnHelp;
    protected JScrollPane _scrollPane;
    protected JList _list;
    
    public KeywordSelectionDialog(Frame owner) {
        super(owner, true);
        setResizable(false);
        setSize(new Dimension(700, 500));
        
        _panelButtons = new JPanel();
        _panelButtons.setBorder(new EmptyBorder(0, 100, 10, 100));
        getContentPane().add(_panelButtons, BorderLayout.SOUTH);
        _panelButtons.setLayout(new GridLayout(0, 5, 5, 5));
        
        _btnOk = new JButton("OK");
        _btnOk.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                KeywordSelectionDialog.this._okPressed = true;
                KeywordSelectionDialog.this.okBtnPressed();
                KeywordSelectionDialog.this.setVisible(false);
            }
        });
        _panelButtons.add(_btnOk);
        
        _btnDeselectAll = new JButton("Deselect All");
        _btnDeselectAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _list.clearSelection();
            }
        });
        _panelButtons.add(_btnDeselectAll);
        
        _btnList = new JButton("List");
        _btnList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        _panelButtons.add(_btnList);
        
        _btnImages = new JButton("Images");
        _btnImages.setEnabled(false);
        _btnImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        _panelButtons.add(_btnImages);
        
        _btnSearch = new JButton("Search");
        _btnSearch.setEnabled(false);
        _btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        _panelButtons.add(_btnSearch);
        
        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _okPressed = false;
                KeywordSelectionDialog.this.setVisible(false);
            }
        });
        _panelButtons.add(_btnCancel);
        
        _btnHelp = new JButton("Help");
        _btnHelp.setEnabled(false);
        _btnHelp.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                KeywordSelectionDialog.this._okPressed = false;
                KeywordSelectionDialog.this.setVisible(false);
            }
        });
        _panelButtons.add(_btnHelp);
        
        _scrollPane = new JScrollPane();
        _scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), new EtchedBorder(EtchedBorder.LOWERED, null, null)));
        getContentPane().add(_scrollPane, BorderLayout.CENTER);
        
        _list = new JList();
        _scrollPane.setViewportView(_list);
        
        setLocationRelativeTo(owner);
    }
    
    public boolean getOkButtonPressed() {
        return _okPressed;
    }
    
    abstract protected void okBtnPressed();
    abstract protected void listBtnPressed();
    abstract protected void imagesBtnPressed();
    abstract protected void searchBtnPressed();
    abstract protected void helpBtnPressed();

}
