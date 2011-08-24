package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.Intkey;

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

    private javax.swing.Action _findAction;
    private javax.swing.Action _nextAction;

    private Intkey _intkeyApp;

    private int _numMatchedTaxa;
    private int _currentMatchedTaxon;

    @Resource
    String enterSearchStringCaption;

    @Resource
    String selectOneCaption;

    @Resource
    String selectAllCaption;

    @Resource
    String searchSynonymsCaption;

    @Resource
    String searchEliminatedTaxaCaption;

    @Resource
    String windowTitle;

    @Resource
    String windowTitleNumberFound;

    @Resource
    String noTaxaFoundMessage;

    public FindInTaxaDialog(Intkey intkeyApp) {
        super(intkeyApp.getMainFrame(), false);
        setResizable(false);

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(FindInTaxaDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

        _intkeyApp = intkeyApp;

        _numMatchedTaxa = 0;
        _currentMatchedTaxon = -1;

        _findAction = actionMap.get("findTaxa");
        _nextAction = actionMap.get("nextTaxon");

        this.setTitle(windowTitle);

        getContentPane().setLayout(new BorderLayout(0, 0));

        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(20, 20, 20, 20));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new BorderLayout(0, 0));

        _pnlMainTop = new JPanel();
        _pnlMain.add(_pnlMainTop, BorderLayout.NORTH);
        _pnlMainTop.setLayout(new BoxLayout(_pnlMainTop, BoxLayout.Y_AXIS));

        _lblEnterSearchString = new JLabel(enterSearchStringCaption);
        _lblEnterSearchString.setBorder(new EmptyBorder(0, 0, 5, 0));
        _lblEnterSearchString.setHorizontalAlignment(SwingConstants.LEFT);
        _lblEnterSearchString.setVerticalAlignment(SwingConstants.TOP);
        _lblEnterSearchString.setAlignmentY(Component.TOP_ALIGNMENT);
        _pnlMainTop.add(_lblEnterSearchString);

        _textField = new JTextField();
        _textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                reset();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                reset();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                reset();
            }
        });

        _pnlMainTop.add(_textField);
        _textField.setColumns(10);

        _pnlMainMiddle = new JPanel();
        _pnlMainMiddle.setBorder(new EmptyBorder(10, 0, 0, 0));
        _pnlMain.add(_pnlMainMiddle, BorderLayout.CENTER);
        _pnlMainMiddle.setLayout(new BoxLayout(_pnlMainMiddle, BoxLayout.Y_AXIS));

        _rdbtnSelectOne = new JRadioButton(selectOneCaption);
        _rdbtnSelectOne.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        _pnlMainMiddle.add(_rdbtnSelectOne);

        _rdbtnSelectAll = new JRadioButton(selectAllCaption);
        _rdbtnSelectAll.setSelected(true);
        _rdbtnSelectAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });
        _pnlMainMiddle.add(_rdbtnSelectAll);

        ButtonGroup radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(_rdbtnSelectOne);
        radioButtonGroup.add(_rdbtnSelectAll);

        _pnlMainBottom = new JPanel();
        _pnlMain.add(_pnlMainBottom, BorderLayout.SOUTH);
        _pnlMainBottom.setLayout(new BoxLayout(_pnlMainBottom, BoxLayout.Y_AXIS));

        _chckbxSearchSynonyms = new JCheckBox(searchSynonymsCaption);
        _chckbxSearchSynonyms.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        _pnlMainBottom.add(_chckbxSearchSynonyms);

        _chckbxSearchEliminatedTaxa = new JCheckBox(searchEliminatedTaxaCaption);
        _chckbxSearchEliminatedTaxa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reset();
            }
        });

        _pnlMainBottom.add(_chckbxSearchEliminatedTaxa);

        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 0, 0, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        _pnlButtons.setLayout(new BorderLayout(0, 0));

        _pnlInnerButtons = new JPanel();
        _pnlButtons.add(_pnlInnerButtons, BorderLayout.NORTH);
        GridBagLayout gbl_pnlInnerButtons = new GridBagLayout();
        gbl_pnlInnerButtons.columnWidths = new int[] { 0, 0 };
        gbl_pnlInnerButtons.rowHeights = new int[] { 0, 0, 0, 0 };
        gbl_pnlInnerButtons.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl_pnlInnerButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlInnerButtons.setLayout(gbl_pnlInnerButtons);

        _btnFindNext = new JButton();
        _btnFindNext.setAction(_findAction);
        GridBagConstraints gbc_btnFind = new GridBagConstraints();
        gbc_btnFind.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnFind.insets = new Insets(0, 0, 5, 0);
        gbc_btnFind.gridx = 0;
        gbc_btnFind.gridy = 0;
        _pnlInnerButtons.add(_btnFindNext, gbc_btnFind);

        _btnPrevious = new JButton("Previous");
        _btnPrevious.setAction(actionMap.get("previousTaxon"));
        _btnPrevious.setEnabled(false);
        GridBagConstraints gbc_btnPrevious = new GridBagConstraints();
        gbc_btnPrevious.insets = new Insets(0, 0, 5, 0);
        gbc_btnPrevious.gridx = 0;
        gbc_btnPrevious.gridy = 1;
        _pnlInnerButtons.add(_btnPrevious, gbc_btnPrevious);

        _btnDone = new JButton("Done");
        _btnDone.setAction(actionMap.get("findTaxaDone"));
        GridBagConstraints gbc_btnDone = new GridBagConstraints();
        gbc_btnDone.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnDone.gridx = 0;
        gbc_btnDone.gridy = 2;
        _pnlInnerButtons.add(_btnDone, gbc_btnDone);

        this.pack();
        this.setLocationRelativeTo(_intkeyApp.getMainFrame());
    }

    @Action
    public void findTaxa() {
        String searchText = _textField.getText();
        boolean searchSynonyms = _chckbxSearchSynonyms.isSelected();
        boolean searchEliminatedTaxa = _chckbxSearchEliminatedTaxa.isSelected();
        if (!StringUtils.isEmpty(searchText)) {
            _numMatchedTaxa = _intkeyApp.findTaxa(searchText, searchSynonyms, searchEliminatedTaxa);
            this.setTitle(String.format(windowTitleNumberFound, _numMatchedTaxa));

            if (_numMatchedTaxa > 0) {
                _currentMatchedTaxon = 0;
                taxonSelectionUpdated();
            } else {
                JOptionPane.showMessageDialog(this, noTaxaFoundMessage, "", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    @Action
    public void nextFoundTaxon() {
        if (_currentMatchedTaxon < (_numMatchedTaxa - 1)) {
            _currentMatchedTaxon++;
            taxonSelectionUpdated();
        }
    }

    @Action
    public void previousFoundTaxon() {
        if (_currentMatchedTaxon > 0) {
            _currentMatchedTaxon--;
            taxonSelectionUpdated();
        }
    }

    @Action
    public void findTaxaDone() {
        this.setVisible(false);
    }

    private void taxonSelectionUpdated() {
        if (_rdbtnSelectAll.isSelected()) {
            _intkeyApp.selectAllMatchedTaxa();
        } else {
            _intkeyApp.selectCurrentMatchedTaxon(_currentMatchedTaxon);
            _btnFindNext.setAction(_nextAction);

            _btnPrevious.setEnabled(_currentMatchedTaxon > 0);
            _btnFindNext.setEnabled(_currentMatchedTaxon < (_numMatchedTaxa - 1));
        }
    }

    private void reset() {
        if (_numMatchedTaxa > 0) {
            this.setTitle(windowTitle);
            _btnFindNext.setAction(_findAction);
            _btnFindNext.setEnabled(true);
            _btnPrevious.setEnabled(false);

            _numMatchedTaxa = 0;
            _currentMatchedTaxon = -1;
        }
    }

}
