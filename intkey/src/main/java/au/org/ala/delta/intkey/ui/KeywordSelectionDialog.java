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

public abstract class KeywordSelectionDialog extends ListSelectionDialog {
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

        _panelButtons.setLayout(new GridLayout(0, 5, 5, 5));

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("keywordSelectionDialog_OK"));
        _panelButtons.add(_btnOk);

        _btnDeselectAll = new JButton("Deselect All");
        _btnDeselectAll.setAction(actionMap.get("keywordSelectionDialog_DeselectAll"));
        _panelButtons.add(_btnDeselectAll);

        _btnList = new JButton();
        _btnList.setEnabled(false);
        _btnList.setAction(actionMap.get("keywordSelectionDialog_List"));
        _panelButtons.add(_btnList);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("keywordSelectionDialog_Images"));
        _btnImages.setEnabled(false);
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("keywordSelectionDialog_Search"));
        _btnSearch.setEnabled(false);
        _panelButtons.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("keywordSelectionDialog_Cancel"));
        _panelButtons.add(_btnCancel);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("keywordSelectionDialog_Help"));
        _btnHelp.setEnabled(false);
        _panelButtons.add(_btnHelp);

        _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        GridLayout gridLayout = (GridLayout) _panelButtons.getLayout();
        gridLayout.setVgap(2);
        
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
