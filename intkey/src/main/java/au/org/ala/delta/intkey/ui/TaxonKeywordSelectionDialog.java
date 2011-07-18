package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;

public class TaxonKeywordSelectionDialog extends KeywordSelectionDialog {
    
    @Resource
    String title;
    
    private List<Item> _selectedTaxa;

    public TaxonKeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        init(context);
    }

    public TaxonKeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        init(context);
    }
    
    private void init(IntkeyContext context) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonKeywordSelectionDialog.class);
        resourceMap.injectFields(this);

        setTitle(String.format(title, _directiveName));
        List<String> taxonKeywords = context.getTaxaKeywords();

        DefaultListModel model = new DefaultListModel();
        for (String keyword : taxonKeywords) {
            model.addElement(keyword);
        }
        _list.setModel(model);

        _selectedTaxa = new ArrayList<Item>();
        _context = context;
    }

    @Override
    protected void okBtnPressed() {
        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;
            _selectedTaxa.addAll(_context.getTaxaForKeyword(keyword));
        }
        Collections.sort(_selectedTaxa);
        this.setVisible(false);
    }

    @Override
    protected void cancelBtnPressed() {
        this.setVisible(false);
    }

    @Override
    protected void listBtnPressed() {
        if (_list.getSelectedValue() != null) {
            
            List<Item> taxa = new ArrayList<Item>();
            String selectedKeyword = (String) _list.getSelectedValue();
            taxa.addAll(_context.getTaxaForKeyword(selectedKeyword));

            TaxonSelectionDialog taxonDlg = new TaxonSelectionDialog(this, taxa, _directiveName, selectedKeyword);
            taxonDlg.setVisible(true);

            List<Item> taxaSelectedInDlg = taxonDlg.getSelectedTaxa();
            if (taxaSelectedInDlg.size() > 0) {
                _selectedTaxa.clear();
                _selectedTaxa.addAll(taxaSelectedInDlg);
                this.setVisible(false);
            }
        }
    }

    @Override
    protected void imagesBtnPressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void searchBtnPressed() {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void helpBtnPressed() {
        // TODO Auto-generated method stub
        
    }
    
    public List<Item> getSelectedTaxa() {
        return _selectedTaxa;
    }

}
