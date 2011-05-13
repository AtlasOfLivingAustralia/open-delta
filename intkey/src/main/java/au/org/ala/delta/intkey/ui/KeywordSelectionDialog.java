package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public abstract class KeywordSelectionDialog extends ListSelectionDialog {
    protected boolean _okPressed = false;
    protected JButton _btnOk;
    protected JButton _btnDeselectAll;
    protected JButton _btnList;
    protected JButton _btnImages;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnHelp;
    
    public KeywordSelectionDialog(Dialog owner) {
        super(owner);
        init();
    }
    
    public KeywordSelectionDialog(Frame owner) {
        super(owner);
        init();
    }
     
    private void init() {
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
                listBtnPressed();
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
                cancelBtnPressed();
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
    }
    
    abstract protected void okBtnPressed();
    abstract protected void cancelBtnPressed();
    abstract protected void listBtnPressed();
    abstract protected void imagesBtnPressed();
    abstract protected void searchBtnPressed();
    abstract protected void helpBtnPressed();

}
