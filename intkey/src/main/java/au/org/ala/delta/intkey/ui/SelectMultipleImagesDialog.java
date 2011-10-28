package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import javax.swing.JRadioButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;

public class SelectMultipleImagesDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 3089705977409496966L;
    private JPanel _pnlButtons;
    private JPanel _pnlMain;
    private JPanel _pnlOptions;
    private JPanel _pnlSubjectList;

    public SelectMultipleImagesDialog(Dialog owner, boolean modal) {
        super(owner, modal);
        
        _pnlButtons = new JPanel();
        getContentPane().add(_pnlButtons, BorderLayout.SOUTH);
        
        JButton btnOk = new JButton("OK");
        _pnlButtons.add(btnOk);
        
        JButton btnCancel = new JButton("Cancel");
        _pnlButtons.add(btnCancel);
        
        _pnlMain = new JPanel();
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BoxLayout(_pnlMain, BoxLayout.X_AXIS));
        
        _pnlOptions = new JPanel();
        _pnlMain.add(_pnlOptions);
        _pnlOptions.setLayout(new GridLayout(0, 1, 0, 0));
        
        JRadioButton rdbtnAllImagesOf = new JRadioButton("All images of the current taxon");
        _pnlOptions.add(rdbtnAllImagesOf);
        
        JRadioButton rdbtnNewRadioButton = new JRadioButton("First image of all selected taxa");
        _pnlOptions.add(rdbtnNewRadioButton);
        
        JRadioButton rdbtnAllImagesOf_1 = new JRadioButton("All images of selected taxa");
        _pnlOptions.add(rdbtnAllImagesOf_1);
        
        JCheckBox chckbxCloseAllOpen = new JCheckBox("Close all open windows first");
        _pnlOptions.add(chckbxCloseAllOpen);
        
        _pnlSubjectList = new JPanel();
        _pnlMain.add(_pnlSubjectList);
        _pnlSubjectList.setLayout(new GridLayout(0, 1, 0, 0));
        
        JLabel lblSelectBySubject = new JLabel("Select by subject");
        _pnlSubjectList.add(lblSelectBySubject);
        
        JList list = new JList();
        _pnlSubjectList.add(list);
        // TODO Auto-generated constructor stub
    }

}
