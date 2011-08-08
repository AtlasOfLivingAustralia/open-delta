package au.org.ala.delta.intkey.ui;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class FindInCharactersDialog extends JDialog {
    private JPanel _pnlMain;
    private JPanel _pnlMainTop;
    private JTextField textField;
    public FindInCharactersDialog() {
        
        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));
        
        _pnlMainTop = new JPanel();
        _pnlMain.add(_pnlMainTop, BorderLayout.NORTH);
        _pnlMainTop.setLayout(new BoxLayout(_pnlMainTop, BoxLayout.Y_AXIS));
        
        JLabel lblEnterSearchString = new JLabel("Enter search string:");
        lblEnterSearchString.setBorder(new EmptyBorder(0, 0, 5, 0));
        _pnlMainTop.add(lblEnterSearchString);
        
        textField = new JTextField();
        _pnlMainTop.add(textField);
        textField.setColumns(10);
        
        JPanel _pnlMainBottom = new JPanel();
        _pnlMainBottom.setBorder(new EmptyBorder(20, 0, 0, 0));
        _pnlMain.add(_pnlMainBottom, BorderLayout.CENTER);
        _pnlMainBottom.setLayout(new BoxLayout(_pnlMainBottom, BoxLayout.Y_AXIS));
        
        JCheckBox chckbxSearchStates = new JCheckBox("Search states");
        _pnlMainBottom.add(chckbxSearchStates);
        
        JCheckBox chckbxSearchUsedCharacters = new JCheckBox("Search used characters");
        _pnlMainBottom.add(chckbxSearchUsedCharacters);
        
        JPanel _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 0, 0, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        _pnlButtons.setLayout(new BorderLayout(0, 0));
        
        JPanel _pnlInnerButtons = new JPanel();
        _pnlButtons.add(_pnlInnerButtons, BorderLayout.NORTH);
        GridBagLayout gbl__pnlInnerButtons = new GridBagLayout();
        gbl__pnlInnerButtons.columnWidths = new int[]{0, 0};
        gbl__pnlInnerButtons.rowHeights = new int[]{0, 0, 0, 0};
        gbl__pnlInnerButtons.columnWeights = new double[]{0.0, Double.MIN_VALUE};
        gbl__pnlInnerButtons.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
        _pnlInnerButtons.setLayout(gbl__pnlInnerButtons);
        
        JButton _btnFindNext = new JButton("Find");
        GridBagConstraints gbc__btnFindNext = new GridBagConstraints();
        gbc__btnFindNext.fill = GridBagConstraints.HORIZONTAL;
        gbc__btnFindNext.insets = new Insets(0, 0, 5, 0);
        gbc__btnFindNext.gridx = 0;
        gbc__btnFindNext.gridy = 0;
        _pnlInnerButtons.add(_btnFindNext, gbc__btnFindNext);
        
        JButton _btnPrevious = new JButton("Previous");
        GridBagConstraints gbc__btnPrevious = new GridBagConstraints();
        gbc__btnPrevious.insets = new Insets(0, 0, 5, 0);
        gbc__btnPrevious.gridx = 0;
        gbc__btnPrevious.gridy = 1;
        _pnlInnerButtons.add(_btnPrevious, gbc__btnPrevious);
        
        JButton _btnDone = new JButton("Done");
        GridBagConstraints gbc__btnDone = new GridBagConstraints();
        gbc__btnDone.fill = GridBagConstraints.HORIZONTAL;
        gbc__btnDone.gridx = 0;
        gbc__btnDone.gridy = 2;
        _pnlInnerButtons.add(_btnDone, gbc__btnDone);
    }

}
