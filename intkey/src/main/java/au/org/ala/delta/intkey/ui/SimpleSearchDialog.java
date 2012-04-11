package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.text.MessageFormat;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

public class SimpleSearchDialog extends IntkeyDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -3058018736938884082L;

    private JTextField _txtFldSearch;
    private JButton _btnCancel;
    private JButton _btnSearch;
    private JPanel _pnlButtons;
    private JLabel _lblEnterSearchString;
    private JPanel _pnlMain;

    private JDialog _owner;

    // The dialog to highlight search results in. Could be different from the
    // "owner" of the dialog.
    private SearchableListDialog _dialogToSearch;

    private int _lastMatchedListIndex;

    @Resource
    String title;

    @Resource
    String enterStringCaption;

    @Resource
    String noMoreOccurrencesCaption;

    @Resource
    String informationDialogTitle;

    public SimpleSearchDialog(JDialog owner, SearchableListDialog dialogToSearch) {
        super(owner, false);

        _owner = owner;
        _dialogToSearch = dialogToSearch;
        _lastMatchedListIndex = -1;

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

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("SimpleSearchDialog_Search"));
        _pnlButtons.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("SimpleSearchDialog_Cancel"));
        _pnlButtons.add(_btnCancel);

        _txtFldSearch.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void removeUpdate(DocumentEvent e) {
                _lastMatchedListIndex = -1;
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                _lastMatchedListIndex = -1;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                _lastMatchedListIndex = -1;
            }
        });
    }

    @Action
    public void SimpleSearchDialog_Search() {
        String searchText = _txtFldSearch.getText().trim();
        if (!searchText.isEmpty()) {
            int startingIndex = 0;
            if (_lastMatchedListIndex != -1) {
                startingIndex = _lastMatchedListIndex + 1;
            }
            int matchedIndex = _dialogToSearch.searchForText(searchText, startingIndex);
            if (matchedIndex == -1) {
                String message = MessageFormat.format(noMoreOccurrencesCaption, searchText);
                JOptionPane.showMessageDialog(this, message, informationDialogTitle, JOptionPane.INFORMATION_MESSAGE);
            } else {
                _lastMatchedListIndex = matchedIndex;
            }
        }
    }

    @Action
    public void SimpleSearchDialog_Cancel() {
        this.setVisible(false);
    }

}
