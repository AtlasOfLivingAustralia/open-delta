package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.intkey.model.IntkeyContext;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import java.awt.FlowLayout;
import javax.swing.ButtonGroup;

public abstract class KeywordSelectionDialog extends ListSelectionDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -5586414259535222597L;
    
    protected JButton _btnOk;
    protected JButton _btnDeselectAll;
    protected JButton _btnList;
    protected JButton _btnImages;
    protected JButton _btnSearch;
    protected JButton _btnCancel;
    protected JButton _btnHelp;

    protected IntkeyContext _context;

    // The name of the directive being processed
    protected String _directiveName;
    protected JPanel _panelInnerButtons;
    protected JPanel _panelRadioButtons;
    protected JRadioButton _rdbtnSelectFromAll;
    protected JLabel lblSelectFrom;
    protected JRadioButton _rdbtnSelectFromIncluded;
    protected final ButtonGroup buttonGroup = new ButtonGroup();
    
    protected boolean _selectFromIncluded = false;

    public KeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName) {
        super(owner);
        _context = context;
        _directiveName = directiveName;
        init(_context);
    }

    public KeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName) {
        super(owner);
        _context = context;
        _directiveName = directiveName;
        init(_context);
    }

    private void init(IntkeyContext context) {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(KeywordSelectionDialog.class, this);

        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        _panelButtons.setLayout(new BorderLayout(0, 0));

        _panelInnerButtons = new JPanel();
        _panelButtons.add(_panelInnerButtons, BorderLayout.CENTER);
        _panelInnerButtons.setLayout(new GridLayout(0, 5, 0, 5));

        _btnOk = new JButton();
        _panelInnerButtons.add(_btnOk);
        _btnOk.setAction(actionMap.get("keywordSelectionDialog_OK"));

        _btnDeselectAll = new JButton("Deselect All");
        _panelInnerButtons.add(_btnDeselectAll);
        _btnDeselectAll.setAction(actionMap.get("keywordSelectionDialog_DeselectAll"));

        _btnList = new JButton();
        _panelInnerButtons.add(_btnList);
        _btnList.setEnabled(false);
        _btnList.setAction(actionMap.get("keywordSelectionDialog_List"));

        _btnImages = new JButton();
        _panelInnerButtons.add(_btnImages);
        _btnImages.setAction(actionMap.get("keywordSelectionDialog_Images"));

        _btnSearch = new JButton();
        _panelInnerButtons.add(_btnSearch);
        _btnSearch.setAction(actionMap.get("keywordSelectionDialog_Search"));
        _btnSearch.setEnabled(false);

        _btnCancel = new JButton();
        _panelInnerButtons.add(_btnCancel);
        _btnCancel.setAction(actionMap.get("keywordSelectionDialog_Cancel"));

        _btnHelp = new JButton();
        _panelInnerButtons.add(_btnHelp);
        _btnHelp.setAction(actionMap.get("keywordSelectionDialog_Help"));
        _btnHelp.setEnabled(false);

        _panelRadioButtons = new JPanel();
        FlowLayout flowLayout = (FlowLayout) _panelRadioButtons.getLayout();
        flowLayout.setHgap(20);
        _panelButtons.add(_panelRadioButtons, BorderLayout.SOUTH);

        lblSelectFrom = new JLabel("Select from:");
        _panelRadioButtons.add(lblSelectFrom);

        _rdbtnSelectFromAll = new JRadioButton("All characters");
        _rdbtnSelectFromAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _selectFromIncluded = false;
            }
        });
        buttonGroup.add(_rdbtnSelectFromAll);
        _panelRadioButtons.add(_rdbtnSelectFromAll);

        _rdbtnSelectFromIncluded = new JRadioButton("Included characters");
        _rdbtnSelectFromIncluded.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                _selectFromIncluded = true;
            }
        });
        buttonGroup.add(_rdbtnSelectFromIncluded);
        _panelRadioButtons.add(_rdbtnSelectFromIncluded);

        _context = context;
        
        
    }

    @Action
    public void keywordSelectionDialog_OK() {
        okBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_Cancel() {
        cancelBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_List() {
        listBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_Images() {
        imagesBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_Search() {
        searchBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_Help() {
        helpBtnPressed();
    }

    @Action
    public void keywordSelectionDialog_DeselectAll() {
        _list.clearSelection();
    }

    abstract protected void okBtnPressed();

    abstract protected void cancelBtnPressed();

    abstract protected void listBtnPressed();

    abstract protected void imagesBtnPressed();

    abstract protected void searchBtnPressed();

    abstract protected void helpBtnPressed();

}
