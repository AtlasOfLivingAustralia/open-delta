package au.org.ala.delta.intkey.ui;

import java.awt.Frame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import javax.swing.border.EmptyBorder;
import javax.swing.JCheckBox;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JLabel;

public class SetMatchPromptDialog extends IntkeyDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 9154669439641806443L;
    private JPanel _pnlMain;
    private JPanel _pnlButtons;

    public SetMatchPromptDialog(Frame owner, boolean modal) {
        super(owner, modal);
        setPreferredSize(new Dimension(480, 250));
        setTitle("Set Match");
        
        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 10, 20, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        GridBagLayout gbl__pnlButtons = new GridBagLayout();
        gbl__pnlButtons.columnWidths = new int[]{65, 0};
        gbl__pnlButtons.rowHeights = new int[]{23, 23, 23, 0};
        gbl__pnlButtons.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl__pnlButtons.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        _pnlButtons.setLayout(gbl__pnlButtons);
        
        JButton btnOk = new JButton("Ok");
        GridBagConstraints gbc_btnOk = new GridBagConstraints();
        gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnOk.insets = new Insets(0, 0, 5, 0);
        gbc_btnOk.gridx = 0;
        gbc_btnOk.gridy = 0;
        _pnlButtons.add(btnOk, gbc_btnOk);
        
        JButton btnCancel = new JButton("Cancel");
        GridBagConstraints gbc_btnCancel = new GridBagConstraints();
        gbc_btnCancel.anchor = GridBagConstraints.WEST;
        gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
        gbc_btnCancel.gridx = 0;
        gbc_btnCancel.gridy = 1;
        _pnlButtons.add(btnCancel, gbc_btnCancel);
        
        JButton btnHelp = new JButton("Help");
        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnHelp.gridx = 0;
        gbc_btnHelp.gridy = 2;
        _pnlButtons.add(btnHelp, gbc_btnHelp);
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 20, 0, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new GridLayout(0, 1, 0, 0));
        
        JCheckBox chckbxInapplicables = new JCheckBox("Inapplicables");
        _pnlMain.add(chckbxInapplicables);
        
        JCheckBox chckbxUnknowns = new JCheckBox("Unknowns");
        _pnlMain.add(chckbxUnknowns);
        
        JCheckBox chckbxSubset = new JCheckBox("Subset");
        _pnlMain.add(chckbxSubset);
        
        JCheckBox chckbxOverlap = new JCheckBox("Overlap");
        _pnlMain.add(chckbxOverlap);
        
        JCheckBox chckbxExact = new JCheckBox("Exact");
        _pnlMain.add(chckbxExact);
        
        JPanel panel = new JPanel();
        FlowLayout flowLayout = (FlowLayout) panel.getLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        _pnlMain.add(panel);
        
        JButton button = new JButton("");
        button.setPreferredSize(new Dimension(20, 20));
        panel.add(button);
        
        JLabel lblSetValuesFor = new JLabel("Set values for identification");
        panel.add(lblSetValuesFor);
        
        JPanel panel_1 = new JPanel();
        FlowLayout flowLayout_1 = (FlowLayout) panel_1.getLayout();
        flowLayout_1.setAlignment(FlowLayout.LEFT);
        _pnlMain.add(panel_1);
        
        JButton button_1 = new JButton("");
        button_1.setPreferredSize(new Dimension(20, 20));
        panel_1.add(button_1);
        
        JLabel lblSetValuesFor_1 = new JLabel("Set values for information retrieval");
        panel_1.add(lblSetValuesFor_1);
    }

}
