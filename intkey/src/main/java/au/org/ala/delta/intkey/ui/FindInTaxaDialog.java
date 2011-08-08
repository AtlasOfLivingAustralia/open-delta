package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.model.IntkeyDataset;

public class FindInTaxaDialog extends JDialog {
    
    private static final long serialVersionUID = -1533904722860799151L;
    
    private JTextField _textField;
    private JPanel _pnlMain;
    private JPanel _pnlMainTop;
    private JLabel _lblEnterSearchString;
    private JPanel _pnlMainMiddle;
    private JRadioButton _rdbtnSelectOne;
    private JRadioButton _rdbtnSelectAll;
    private JPanel _pnlMainBottom;
    private JCheckBox _chckbxSearchSynonyms;
    private JCheckBox _chckbxSearchEliminatedTaxa;
    private JPanel _pnlButtons;
    private JPanel _pnlInnerButtons;
    private JButton _btnFindNext;
    private JButton _btnPrevious;
    private JButton _btnDone;
    
    public FindInTaxaDialog(Intkey intkey) {
        super(intkey.getMainFrame(), false);
        getContentPane().setLayout(new BorderLayout(0, 0));
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _pnlMainTop = new JPanel();
        _pnlMain.add(_pnlMainTop, BorderLayout.NORTH);
        _pnlMainTop.setLayout(new BoxLayout(_pnlMainTop, BoxLayout.Y_AXIS));
        
        _lblEnterSearchString = new JLabel("Enter search string:");
        _lblEnterSearchString.setBorder(new EmptyBorder(0, 0, 5, 0));
        _lblEnterSearchString.setHorizontalAlignment(SwingConstants.LEFT);
        _lblEnterSearchString.setVerticalAlignment(SwingConstants.TOP);
        _lblEnterSearchString.setAlignmentY(Component.TOP_ALIGNMENT);
        _pnlMainTop.add(_lblEnterSearchString);
        
        _textField = new JTextField();
        _pnlMainTop.add(_textField);
        _textField.setColumns(10);
        
        _pnlMainMiddle = new JPanel();
        _pnlMainMiddle.setBorder(new EmptyBorder(10, 0, 0, 0));
        _pnlMain.add(_pnlMainMiddle, BorderLayout.CENTER);
        _pnlMainMiddle.setLayout(new BoxLayout(_pnlMainMiddle, BoxLayout.Y_AXIS));
        
        _rdbtnSelectOne = new JRadioButton("Select one");
        _pnlMainMiddle.add(_rdbtnSelectOne);
        
        _rdbtnSelectAll = new JRadioButton("Select all");
        _pnlMainMiddle.add(_rdbtnSelectAll);
        
        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(_rdbtnSelectOne);
        radioButtonGroup.add(_rdbtnSelectAll);
        
        _pnlMainBottom = new JPanel();
        _pnlMain.add(_pnlMainBottom, BorderLayout.SOUTH);
        _pnlMainBottom.setLayout(new BoxLayout(_pnlMainBottom, BoxLayout.Y_AXIS));
        
        _chckbxSearchSynonyms = new JCheckBox("Search synonyms");
        _pnlMainBottom.add(_chckbxSearchSynonyms);
        
        _chckbxSearchEliminatedTaxa = new JCheckBox("Search eliminated taxa");
        _pnlMainBottom.add(_chckbxSearchEliminatedTaxa);
        
        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 0, 0, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        _pnlButtons.setLayout(new BorderLayout(0, 0));
        
        _pnlInnerButtons = new JPanel();
        _pnlButtons.add(_pnlInnerButtons, BorderLayout.NORTH);
        GridBagLayout gbl_pnlInnerButtons = new GridBagLayout();
        gbl_pnlInnerButtons.columnWidths = new int[]{0, 0};
        gbl_pnlInnerButtons.rowHeights = new int[]{0, 0, 0, 0};
        gbl_pnlInnerButtons.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl_pnlInnerButtons.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        _pnlInnerButtons.setLayout(gbl_pnlInnerButtons);
        
        _btnFindNext = new JButton("Find");
        GridBagConstraints gbc_btnFind = new GridBagConstraints();
        gbc_btnFind.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnFind.insets = new Insets(0, 0, 5, 0);
        gbc_btnFind.gridx = 0;
        gbc_btnFind.gridy = 0;
        _pnlInnerButtons.add(_btnFindNext, gbc_btnFind);
        
        _btnPrevious = new JButton("Previous");
        GridBagConstraints gbc_btnPrevious = new GridBagConstraints();
        gbc_btnPrevious.insets = new Insets(0, 0, 5, 0);
        gbc_btnPrevious.gridx = 0;
        gbc_btnPrevious.gridy = 1;
        _pnlInnerButtons.add(_btnPrevious, gbc_btnPrevious);
        
        _btnDone = new JButton("Done");
        GridBagConstraints gbc_btnDone = new GridBagConstraints();
        gbc_btnDone.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnDone.gridx = 0;
        gbc_btnDone.gridy = 2;
        _pnlInnerButtons.add(_btnDone, gbc_btnDone);
    }

}
