package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.MatchType;
import javax.swing.JRadioButton;

public class SetMatchPromptDialog extends IntkeyDialog {

    @Resource
    String title;

    @Resource
    String inapplicablesCaption;

    @Resource
    String unknownsCaption;

    @Resource
    String subsetCaption;

    @Resource
    String overlapCaption;

    @Resource
    String exactCaption;

    @Resource
    String setValuesForIdentificationCaption;

    @Resource
    String setValuesForInformationRetrievalCaption;

    /**
     * 
     */
    private static final long serialVersionUID = 9154669439641806443L;
    private JPanel _pnlMain;
    private JPanel _pnlButtons;
    private JCheckBox _chckbxInapplicables;
    private JCheckBox _chckbxUnknowns;
    private JPanel _pnlSetValuesForIdentification;
    private JButton _btnSetValuesForIdentification;
    private JLabel _lblSetValuesForIdentification;
    private JPanel _pnlSetValuesForInfoRetrieval;
    private JButton _btnSetValuesForInfoRetrieval;
    private JLabel _lblSetValuesForInfoRetrieval;

    private boolean _okButtonPressed;
    private JRadioButton _rdbtnSubset;
    private JRadioButton _rdbtnOverlap;
    private JRadioButton _rdbtnExact;

    public SetMatchPromptDialog(Frame owner, boolean modal, boolean currentMatchInapplicables, boolean currentMatchUnknowns, MatchType currentMatchType) {
        super(owner, modal);

        _okButtonPressed = false;

        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(SetMatchPromptDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(SetMatchPromptDialog.class, this);

        setPreferredSize(new Dimension(480, 250));
        setTitle(title);

        _pnlButtons = new JPanel();
        _pnlButtons.setBorder(new EmptyBorder(20, 10, 20, 10));
        getContentPane().add(_pnlButtons, BorderLayout.EAST);
        GridBagLayout gbl__pnlButtons = new GridBagLayout();
        gbl__pnlButtons.columnWidths = new int[] { 65, 0 };
        gbl__pnlButtons.rowHeights = new int[] { 23, 23, 23, 0 };
        gbl__pnlButtons.columnWeights = new double[] { 0.0, Double.MIN_VALUE };
        gbl__pnlButtons.rowWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
        _pnlButtons.setLayout(gbl__pnlButtons);

        JButton btnOk = new JButton();
        btnOk.setAction(actionMap.get("setMatchPromptDialog_okAction"));
        GridBagConstraints gbc_btnOk = new GridBagConstraints();
        gbc_btnOk.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnOk.insets = new Insets(0, 0, 5, 0);
        gbc_btnOk.gridx = 0;
        gbc_btnOk.gridy = 0;
        _pnlButtons.add(btnOk, gbc_btnOk);

        JButton btnCancel = new JButton();
        btnCancel.setAction(actionMap.get("setMatchPromptDialog_cancelAction"));
        GridBagConstraints gbc_btnCancel = new GridBagConstraints();
        gbc_btnCancel.anchor = GridBagConstraints.WEST;
        gbc_btnCancel.insets = new Insets(0, 0, 5, 0);
        gbc_btnCancel.gridx = 0;
        gbc_btnCancel.gridy = 1;
        _pnlButtons.add(btnCancel, gbc_btnCancel);

        JButton btnHelp = new JButton();
        btnHelp.setAction(actionMap.get("setMatchPromptDialog_helpAction"));
        GridBagConstraints gbc_btnHelp = new GridBagConstraints();
        gbc_btnHelp.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnHelp.gridx = 0;
        gbc_btnHelp.gridy = 2;
        _pnlButtons.add(btnHelp, gbc_btnHelp);

        _pnlMain = new JPanel();
        _pnlMain.setBorder(new EmptyBorder(10, 20, 0, 10));
        getContentPane().add(_pnlMain, BorderLayout.CENTER);
        _pnlMain.setLayout(new GridLayout(0, 1, 0, 0));

        _chckbxInapplicables = new JCheckBox(inapplicablesCaption);
        _chckbxInapplicables.setMnemonic('i');
        _pnlMain.add(_chckbxInapplicables);

        _chckbxUnknowns = new JCheckBox(unknownsCaption);
        _chckbxUnknowns.setMnemonic('u');
        _pnlMain.add(_chckbxUnknowns);

        _rdbtnSubset = new JRadioButton(subsetCaption);
        _rdbtnSubset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _chckbxInapplicables.setEnabled(true);
                _chckbxUnknowns.setEnabled(true);
            }
        });
        _rdbtnSubset.setMnemonic('s');
        _pnlMain.add(_rdbtnSubset);

        _rdbtnOverlap = new JRadioButton(overlapCaption);
        _rdbtnOverlap.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _chckbxInapplicables.setEnabled(true);
                _chckbxUnknowns.setEnabled(true);
            }
        });

        _rdbtnOverlap.setMnemonic('o');
        _pnlMain.add(_rdbtnOverlap);

        _rdbtnExact = new JRadioButton(exactCaption);
        _rdbtnExact.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                _chckbxInapplicables.setSelected(false);
                _chckbxInapplicables.setEnabled(false);
                _chckbxUnknowns.setSelected(false);
                _chckbxUnknowns.setEnabled(false);
            }
        });

        _rdbtnExact.setMnemonic('e');
        _pnlMain.add(_rdbtnExact);

        ButtonGroup btnGrp = new ButtonGroup();
        btnGrp.add(_rdbtnSubset);
        btnGrp.add(_rdbtnOverlap);
        btnGrp.add(_rdbtnExact);

        _pnlSetValuesForIdentification = new JPanel();
        FlowLayout fl_panel = (FlowLayout) _pnlSetValuesForIdentification.getLayout();
        fl_panel.setAlignment(FlowLayout.LEFT);
        _pnlMain.add(_pnlSetValuesForIdentification);

        _btnSetValuesForIdentification = new JButton();
        _btnSetValuesForIdentification.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setValuesForIdentification();
            }
        });

        _btnSetValuesForIdentification.setPreferredSize(new Dimension(20, 20));
        _pnlSetValuesForIdentification.add(_btnSetValuesForIdentification);

        _lblSetValuesForIdentification = new JLabel(setValuesForIdentificationCaption);
        _pnlSetValuesForIdentification.add(_lblSetValuesForIdentification);

        _pnlSetValuesForInfoRetrieval = new JPanel();
        FlowLayout fl_panel_1 = (FlowLayout) _pnlSetValuesForInfoRetrieval.getLayout();
        fl_panel_1.setAlignment(FlowLayout.LEFT);
        _pnlMain.add(_pnlSetValuesForInfoRetrieval);

        _btnSetValuesForInfoRetrieval = new JButton();
        _btnSetValuesForInfoRetrieval.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setValuesForInformationRetrieval();
            }
        });

        _btnSetValuesForInfoRetrieval.setPreferredSize(new Dimension(20, 20));
        _pnlSetValuesForInfoRetrieval.add(_btnSetValuesForInfoRetrieval);

        _lblSetValuesForInfoRetrieval = new JLabel(setValuesForInformationRetrievalCaption);
        _pnlSetValuesForInfoRetrieval.add(_lblSetValuesForInfoRetrieval);

        _chckbxInapplicables.setSelected(currentMatchInapplicables);
        _chckbxUnknowns.setSelected(currentMatchUnknowns);

        switch (currentMatchType) {
        case SUBSET:
            _rdbtnSubset.setSelected(true);
            break;
        case OVERLAP:
            _rdbtnOverlap.setSelected(true);
            break;
        case EXACT:
            _rdbtnExact.setSelected(true);
            _chckbxInapplicables.setSelected(false);
            _chckbxInapplicables.setEnabled(false);
            _chckbxUnknowns.setSelected(false);
            _chckbxUnknowns.setEnabled(false);
            break;
        default:
            throw new IllegalArgumentException("Unrecognized match type");
        }

        this.pack();
    }

    @Action
    public void setMatchPromptDialog_okAction() {
        _okButtonPressed = true;
        this.setVisible(false);
    }

    @Action
    public void setMatchPromptDialog_cancelAction() {
        this.setVisible(false);
    }

    @Action
    public void setMatchPromptDialog_helpAction() {

    }

    public boolean wasOkButtonPressed() {
        return _okButtonPressed;
    }

    public boolean getMatchInapplicables() {
        return _chckbxInapplicables.isSelected();
    }

    public boolean getMatchUnknowns() {
        return _chckbxUnknowns.isSelected();
    }

    public MatchType getMatchType() {
        if (_rdbtnSubset.isSelected()) {
            return MatchType.SUBSET;
        } else if (_rdbtnOverlap.isSelected()) {
            return MatchType.OVERLAP;
        } else {
            return MatchType.EXACT;
        }
    }

    private void setValuesForIdentification() {
        _chckbxInapplicables.setEnabled(true);
        _chckbxUnknowns.setEnabled(true);
        _chckbxInapplicables.setSelected(true);
        _chckbxUnknowns.setSelected(true);
        _rdbtnOverlap.setSelected(true);
    }

    private void setValuesForInformationRetrieval() {
        _chckbxInapplicables.setEnabled(true);
        _chckbxUnknowns.setEnabled(true);
        _chckbxInapplicables.setSelected(false);
        _chckbxUnknowns.setSelected(false);
        _rdbtnOverlap.setSelected(true);
    }

}
