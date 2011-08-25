package au.org.ala.delta.ui.codeeditor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;


public class SearchReplaceDialog extends BaseDialog {
    
    private javax.swing.JRadioButton backwardRadioButton;
    private JCheckBox caseSensitiveCheck;
    private JButton closeButton;
    private javax.swing.ButtonGroup directionButtonGroup;
    private JPanel directionPanel;
    private JButton findButton;
    private JComboBox findCombo;
    private JLabel findLabel;
    private javax.swing.JRadioButton forwardRadioButton;
    private JPanel aaPanel1;
    private JPanel mainPanel;
    private JPanel optionsPanel;
    private JButton replaceAllButton;
    private JButton replaceButton;
    private JButton replaceFindButton;
    private JLabel replaceLabel;
    private JComboBox replaceWithCombo;
    private JCheckBox wrapSearchCheck;

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** The text area. */
    private CodeTextArea textArea;

    /**
     * Gets the text to find.
     *
     * @return The text to find.
     */
    public String getFindText() {
        String findText = findCombo.getEditor().getItem().toString();
        return findText;
    }

    /**
     * Sets the text to find.
     *
     * @param findText
     *            The new text to find.
     */
    public void setFindText(String findText) {
        updateComboBox(findCombo, findText);
    }

    /**
     * Gets the find direction.
     *
     * @return The current find direction.
     */
    public int getFindDirection() {
        if (backwardRadioButton.isSelected()) {
            return CodeTextArea.DIRECTION_BACKWARD;
        }
        return CodeTextArea.DIRECTION_FORWARD;
    }

    /**
     * Gets the case sensitive search flag.
     *
     * @return The case sensitive flag specified through the find dialog.
     */
    public boolean isCaseSensitiveSearch() {
        return caseSensitiveCheck.isSelected();
    }

    /**
     * Gets the wrapped search flag.
     *
     * @return The wrapped search flag specified through the find dialog.
     */
    public boolean isWrappedSearch() {
        return wrapSearchCheck.isSelected();
    }

    /**
     * @see java.awt.Dialog#show()
     */
    @SuppressWarnings("deprecation")
    public void show() {
        super.show();
        findCombo.requestFocus();
    }

    /**
     * Constructs a SearchReplaceDialog instance with specific attributes.
     *
     * @param textArea
     *            The text area.
     */
    public SearchReplaceDialog(CodeTextArea textArea) {
        super(textArea.getFrame(), false);
        setTitle("Find/Replace");
        this.textArea = textArea;
        initComponents();
        forwardRadioButton.setSelected(true);
        wrapSearchCheck.setSelected(true);

        // defining key bindings
        InputMap inputMap = this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enter");

        ActionMap actionMap = this.getRootPane().getActionMap();
        actionMap.put("escape", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        actionMap.put("enter", new AbstractAction() {

            private static final long serialVersionUID = 1L;

            public void actionPerformed(ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });

        // centering dialog over text area
        centerDialog();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;
        directionButtonGroup = new javax.swing.ButtonGroup();
        aaPanel1 = new JPanel();
        findButton = new JButton();
        replaceFindButton = new JButton();
        replaceButton = new JButton();
        replaceAllButton = new JButton();
        closeButton = new JButton();
        mainPanel = new JPanel();
        findLabel = new JLabel();
        replaceLabel = new JLabel();
        findCombo = new JComboBox();
        replaceWithCombo = new JComboBox();
        directionPanel = new JPanel();
        forwardRadioButton = new javax.swing.JRadioButton();
        backwardRadioButton = new javax.swing.JRadioButton();
        optionsPanel = new JPanel();
        caseSensitiveCheck = new JCheckBox();
        wrapSearchCheck = new JCheckBox();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        aaPanel1.setLayout(new java.awt.GridBagLayout());

        findButton.setMnemonic('n');
        findButton.setText("Find");
        findButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        aaPanel1.add(findButton, gridBagConstraints);

        replaceFindButton.setMnemonic('d');
        replaceFindButton.setText("Replace/Find");
        replaceFindButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceFindButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        aaPanel1.add(replaceFindButton, gridBagConstraints);

        replaceButton.setMnemonic('R');
        replaceButton.setText("Replace");
        replaceButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        aaPanel1.add(replaceButton, gridBagConstraints);

        replaceAllButton.setMnemonic('A');
        replaceAllButton.setText("Replace All");
        replaceAllButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceAllButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        aaPanel1.add(replaceAllButton, gridBagConstraints);

        closeButton.setText("Close");
        closeButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        aaPanel1.add(closeButton, gridBagConstraints);

        getContentPane().add(aaPanel1, java.awt.BorderLayout.SOUTH);

        mainPanel.setLayout(new java.awt.GridBagLayout());

        findLabel.setText("Find:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 2);
        mainPanel.add(findLabel, gridBagConstraints);

        replaceLabel.setText("Replace with:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 2);
        mainPanel.add(replaceLabel, gridBagConstraints);

        findCombo.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        mainPanel.add(findCombo, gridBagConstraints);

        replaceWithCombo.setEditable(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 5);
        mainPanel.add(replaceWithCombo, gridBagConstraints);

        directionPanel.setLayout(new java.awt.GridBagLayout());

        directionPanel.setBorder(new javax.swing.border.TitledBorder("Direction"));
        forwardRadioButton.setText("Forward");
        directionButtonGroup.add(forwardRadioButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        directionPanel.add(forwardRadioButton, gridBagConstraints);

        backwardRadioButton.setText("Backward");
        directionButtonGroup.add(backwardRadioButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        directionPanel.add(backwardRadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        mainPanel.add(directionPanel, gridBagConstraints);

        optionsPanel.setLayout(new java.awt.GridBagLayout());

        optionsPanel.setBorder(new javax.swing.border.TitledBorder("Options"));
        caseSensitiveCheck.setText("Case Sensitive");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        optionsPanel.add(caseSensitiveCheck, gridBagConstraints);

        wrapSearchCheck.setText("Wrap Search");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        optionsPanel.add(wrapSearchCheck, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 2, 5);
        mainPanel.add(optionsPanel, gridBagConstraints);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        getRootPane().setDefaultButton(this.findButton);
        pack();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        this.setVisible(false);
    }

    private void replaceAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String textToFind = getFindText();
        updateComboBox(findCombo, textToFind);
        String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
        updateComboBox(replaceWithCombo, textToReplaceWith);
        textArea.replaceAll(textToFind, textToReplaceWith);
    }

    private void replaceButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
        updateComboBox(replaceWithCombo, textToReplaceWith);
        textArea.replace(textToReplaceWith);
    }

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String textToFind = getFindText();
        updateComboBox(findCombo, textToFind);
        textArea.find(textToFind);
    }

    private void replaceFindButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // replace step
        String textToReplaceWith = (String) replaceWithCombo.getSelectedItem();
        updateComboBox(replaceWithCombo, textToReplaceWith);
        textArea.replace(textToReplaceWith);
        // find step
        String textToFind = getFindText();
        updateComboBox(findCombo, textToFind);
        textArea.find(textToFind);
    }

    /**
     * Updates the list of the given combo box, puts the given latestEntry item on top of the list.
     *
     * @param combo
     *            The combo box to update.
     * @param latestEntry
     *            The lates entry to put on top of the selection list.
     */
    private void updateComboBox(JComboBox combo, String latestEntry) {
        int numItems = combo.getItemCount();
        List<String> items = new ArrayList<String>();
        for (int i = 0; i < numItems; i++) {
            String current = (String) combo.getItemAt(i);
            if ((current != null) && !current.equals(latestEntry)) {
                items.add(current);
            }
        }
        numItems = items.size();
        combo.removeAllItems();
        combo.addItem(latestEntry);
        for (int i = 0; i < numItems; i++) {
            combo.addItem(items.get(i));
        }
    }


}
