package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;

public class TaxonKeywordSelectionDialog extends KeywordSelectionDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -8462278458050948803L;

    @Resource
    String title;

    @Resource
    String selectFromAllTaxaCaption;

    @Resource
    String selectFromIncludedTaxaCaption;

    @Resource
    String allTaxaInSelectedSetExcludedCaption;

    private List<Item> _includedTaxa;
    private List<Item> _selectedTaxa;
    
    public TaxonKeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedTaxaOnly) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        init(context, permitSelectionFromIncludedTaxaOnly);
    }

    public TaxonKeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedTaxaOnly) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        init(context, permitSelectionFromIncludedTaxaOnly);
    }

    private void init(IntkeyContext context, boolean permitSelectionFromIncludedTaxaOnly) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonKeywordSelectionDialog.class);
        resourceMap.injectFields(this);

        setTitle(MessageFormat.format(title, _directiveName));
        List<String> taxonKeywords = context.getTaxaKeywords();

        DefaultListModel model = new DefaultListModel();
        for (String keyword : taxonKeywords) {
            model.addElement(keyword);
        }
        _list.setModel(model);

        _selectedTaxa = null;
        _context = context;

        _includedTaxa = context.getIncludedTaxa();

        _rdbtnSelectFromAll.setText(selectFromAllTaxaCaption);
        _rdbtnSelectFromIncluded.setText(selectFromIncludedTaxaCaption);

        if (!permitSelectionFromIncludedTaxaOnly || _includedTaxa.size() == context.getDataset().getNumberOfTaxa()) {
            _panelRadioButtons.setVisible(false);
            _selectFromIncluded = false;
        } else {
            _rdbtnSelectFromIncluded.setSelected(true);
            _selectFromIncluded = true;
        }
    }

    @Override
    protected void okBtnPressed() {
        _selectedTaxa = new ArrayList<Item>();
        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;
            
            List<Item> taxa =  _context.getTaxaForKeyword(keyword);
            
            if (_selectFromIncluded) {
                taxa.retainAll(_includedTaxa);
            }
            
            _selectedTaxa.addAll(taxa);
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
            
            if (_selectFromIncluded) {
                taxa.retainAll(_includedTaxa);
            }

            if (taxa.isEmpty()) {
                JOptionPane.showMessageDialog(this, allTaxaInSelectedSetExcludedCaption, title, JOptionPane.ERROR_MESSAGE);
            } else {
                TaxonSelectionDialog taxonDlg = new TaxonSelectionDialog(this, taxa, _directiveName, selectedKeyword, _context.displayNumbering(), false);
                taxonDlg.setVisible(true);

                List<Item> taxaSelectedInDlg = taxonDlg.getSelectedTaxa();
                if (taxaSelectedInDlg.size() > 0) {
                    _selectedTaxa.clear();
                    _selectedTaxa.addAll(taxaSelectedInDlg);
                    this.setVisible(false);
                }
            }
        }
    }

    @Override
    protected void imagesBtnPressed() {
        List<Image> taxonKeywordImages = _context.getDataset().getTaxonKeywordImages();
        if (taxonKeywordImages != null && !taxonKeywordImages.isEmpty()) {
            ImageDialog dlg = new ImageDialog(this, _context.getImageSettings(), true);
            dlg.setImages(taxonKeywordImages);
            dlg.setVisible(true);

            if (dlg.okButtonPressed() && !dlg.getSelectedKeywords().isEmpty()) {
                Set<String> selectedKeywords = dlg.getSelectedKeywords();
                for (String keyword : selectedKeywords) {
                    List<Item> taxa = _context.getTaxaForKeyword(keyword);

                    if (_selectFromIncluded) {
                        taxa.retainAll(_includedTaxa);
                    }

                    _selectedTaxa.addAll(taxa);
                }
                Collections.sort(_selectedTaxa);
                this.setVisible(false);
            }
        }
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
