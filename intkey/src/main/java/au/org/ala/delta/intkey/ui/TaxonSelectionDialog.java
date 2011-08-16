package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.border.EmptyBorder;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Item;

public class TaxonSelectionDialog extends ListSelectionDialog {
    private List<Item> _selectedTaxa;
    private JButton _btnOk;
    private JButton _btnSelectAll;
    private JButton _btnKeywords;
    private JButton _btnImages;
    private JButton _btnSearch;
    private JButton _btnCancel;
    private JButton _btnDeselectAll;
    private JButton _btnFullText;
    private JButton _btnHelp;

    private DefaultListModel _listModel;

    // The name of the directive being processed
    private String _directiveName;

    // The name of the keyword that these characters belong to
    private String _keyword;

    @Resource
    String title;

    @Resource
    String titleFromKeyword;

    public TaxonSelectionDialog(Dialog owner, List<Item> taxa, String directiveName, String keyword) {
        this(owner, taxa, directiveName);
        _keyword = keyword;
        setTitle(String.format(titleFromKeyword, _directiveName, _keyword));
    }

    public TaxonSelectionDialog(Frame owner, List<Item> taxa, String directiveName, String keyword) {
        this(owner, taxa, directiveName);
        _keyword = keyword;
        setTitle(String.format(titleFromKeyword, _directiveName, _keyword));
    }

    public TaxonSelectionDialog(Dialog owner, List<Item> taxa, String directiveName) {
        super(owner);
        _directiveName = directiveName;
        init(taxa);
    }

    public TaxonSelectionDialog(Frame owner, List<Item> taxa, String directiveName) {
        super(owner);
        _directiveName = directiveName;
        init(taxa);
    }

    private void init(List<Item> taxa) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonSelectionDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(TaxonSelectionDialog.class, this);

        setTitle(String.format(title, _directiveName));

        _panelButtons.setBorder(new EmptyBorder(0, 20, 10, 20));
        _panelButtons.setLayout(new GridLayout(0, 5, 5, 2));

        _btnOk = new JButton();
        _btnOk.setAction(actionMap.get("taxonSelectionDialog_OK"));
        _panelButtons.add(_btnOk);

        _btnSelectAll = new JButton();
        _btnSelectAll.setAction(actionMap.get("taxonSelectionDialog_SelectAll"));
        _panelButtons.add(_btnSelectAll);

        _btnKeywords = new JButton();
        _btnKeywords.setAction(actionMap.get("taxonSelectionDialog_Keywords"));
        _btnKeywords.setEnabled(false);
        _panelButtons.add(_btnKeywords);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("taxonSelectionDialog_Images"));
        _btnImages.setEnabled(false);
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("taxonSelectionDialog_Search"));
        _btnSearch.setEnabled(false);
        _panelButtons.add(_btnSearch);

        _btnCancel = new JButton();
        _btnCancel.setAction(actionMap.get("taxonSelectionDialog_Cancel"));
        _panelButtons.add(_btnCancel);

        _btnDeselectAll = new JButton();
        _btnDeselectAll.setAction(actionMap.get("taxonSelectionDialog_DeselectAll"));
        _panelButtons.add(_btnDeselectAll);

        _btnFullText = new JButton();
        _btnFullText.setAction(actionMap.get("taxonSelectionDialog_FullText"));
        _btnFullText.setEnabled(false);
        _panelButtons.add(_btnFullText);

        _btnHelp = new JButton();
        _btnHelp.setAction(actionMap.get("taxonSelectionDialog_Help"));
        _btnHelp.setEnabled(false);
        _panelButtons.add(_btnHelp);

        _selectedTaxa = new ArrayList<Item>();

        if (taxa != null) {
            _listModel = new DefaultListModel();
            for (Item taxon : taxa) {
                _listModel.addElement(taxon);
            }
            _list.setCellRenderer(new TaxonCellRenderer());
            _list.setModel(_listModel);
        }
    }

    @Action
    public void taxonSelectionDialog_OK() {
        for (int i : _list.getSelectedIndices()) {
            _selectedTaxa.add((Item) _listModel.getElementAt(i));
        }

        this.setVisible(false);
    }

    @Action
    public void taxonSelectionDialog_SelectAll() {
        _list.setSelectionInterval(0, _listModel.getSize());
    }

    @Action
    public void taxonSelectionDialog_Keywords() {

    }

    @Action
    public void taxonSelectionDialog_Images() {

    }

    @Action
    public void taxonSelectionDialog_Search() {

    }

    @Action
    public void taxonSelectionDialog_Cancel() {
        this.setVisible(false);
    }

    @Action
    public void taxonSelectionDialog_DeselectAll() {
        _list.clearSelection();
    }

    @Action
    public void taxonSelectionDialog_FullText() {

    }

    @Action
    public void taxonSelectionDialog_Notes() {

    }

    @Action
    public void taxonSelectionDialog_Help() {

    }

    public List<Item> getSelectedTaxa() {
        return new ArrayList<Item>(_selectedTaxa);
    }
}
