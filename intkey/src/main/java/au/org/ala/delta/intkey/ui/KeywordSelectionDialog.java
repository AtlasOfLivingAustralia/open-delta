package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
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

public abstract class KeywordSelectionDialog extends JDialog {
    private boolean _okPressed = false;
    private JPanel _panelButtons;
    private JButton _btnOk;
    private JButton _btnDeselectAll;
    private JButton _btnList;
    private JButton _btnImages;
    private JButton _btnSearch;
    private JButton _btnCancel;
    private JButton _btnHelp;
    private JScrollPane _scrollPane;
    private JList _list;
    public KeywordSelectionDialog() {
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
        _btnImages.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        _panelButtons.add(_btnImages);
        
        _btnSearch = new JButton("Search");
        _btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        _panelButtons.add(_btnSearch);
        
        _btnCancel = new JButton("Cancel");
        _btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                KeywordSelectionDialog.this.setVisible(false);
            }
        });
        _panelButtons.add(_btnCancel);
        
        _btnHelp = new JButton("Help");
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
    }
    
    public boolean getOkButtonPressed() {
        return _okPressed;
    }
    
    abstract void okBtnPressed();
    abstract void listBtnPressed();
    abstract void imagesBtnPressed();
    abstract void searchBtnPressed();
    abstract void helpBtnPressed();

}
