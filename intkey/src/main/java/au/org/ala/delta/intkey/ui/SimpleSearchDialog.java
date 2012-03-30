package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;


public class SimpleSearchDialog extends IntkeyDialog {
    private JTextField _txtFldSearch;
    private JButton _btnCancel;
    private JButton _btnSearch;
    private JPanel _pnlButtons;
    private JLabel _lblEnterSearchString;
    private JPanel _pnlMain;
    
    @Resource
    String title;

    @Resource
    String enterStringCaption;
    
    public SimpleSearchDialog(Dialog owner) {
        super(owner, false);
        
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(SimpleSearchDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        
        setTitle(title);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 10, 10, 10));
        getContentPane().add(_pnlMain, BorderLayout.NORTH);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _lblEnterSearchString = new JLabel(enterStringCaption);
        _lblEnterSearchString.setBorder(new EmptyBorder(0, 0, 10, 0));
        _pnlMain.add(_lblEnterSearchString, BorderLayout.NORTH);
        
        _txtFldSearch = new JTextField();
        _pnlMain.add(_txtFldSearch);
        _txtFldSearch.setColumns(10);
        
        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);
        
        _btnSearch = new JButton("Search");
        _pnlButtons.add(_btnSearch);
        
        _btnCancel = new JButton("Cancel");
        _pnlButtons.add(_btnCancel);
    }
    
    @Action
    public void SimpleSearchDialog_Search() {
        
    }
    
    @Action
    public void SimpleSearchDialog_Cancel() {
        
    }

}
