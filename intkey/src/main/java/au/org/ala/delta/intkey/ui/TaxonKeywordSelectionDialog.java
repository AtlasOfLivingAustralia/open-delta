/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.apache.commons.lang.mutable.MutableBoolean;
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

    // names of keywords;
    private List<String> _selectedKeywords;

    /**
     * Used to return whether or not, the specimen was chosen as one of the
     * options. Mutable boolean used here as java does not allow pass by
     * reference.
     */
    private MutableBoolean _specimenSelectedReturnValue;

    private boolean _includeSpecimenAsOption;

    public TaxonKeywordSelectionDialog(Dialog owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean includeSpecimenAsOption,
            MutableBoolean specimenSelectedReturnValue) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        _specimenSelectedReturnValue = specimenSelectedReturnValue;
        init(context, permitSelectionFromIncludedTaxaOnly, includeSpecimenAsOption);
    }

    public TaxonKeywordSelectionDialog(Frame owner, IntkeyContext context, String directiveName, boolean permitSelectionFromIncludedTaxaOnly, boolean includeSpecimenAsOption,
            MutableBoolean specimenSelectedReturnValue) {
        super(owner, context, directiveName);
        _directiveName = directiveName;
        _specimenSelectedReturnValue = specimenSelectedReturnValue;
        init(context, permitSelectionFromIncludedTaxaOnly, includeSpecimenAsOption);
    }

    private void init(IntkeyContext context, boolean permitSelectionFromIncludedTaxaOnly, boolean includeSpecimenAsOption) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonKeywordSelectionDialog.class);
        resourceMap.injectFields(this);

        setTitle(MessageFormat.format(title, _directiveName));
        _includeSpecimenAsOption = includeSpecimenAsOption;
        List<String> taxonKeywords = context.getTaxaKeywords(_includeSpecimenAsOption);

        _listModel = new DefaultListModel();
        for (String keyword : taxonKeywords) {
            _listModel.addElement(keyword);
        }
        _list.setModel(_listModel);

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

        List<Image> taxonKeywordImages = _context.getDataset().getTaxonKeywordImages();
        _btnImages.setEnabled(taxonKeywordImages != null && !taxonKeywordImages.isEmpty());

        _selectedKeywords = new ArrayList<String>();
    }

    @Override
    protected void okBtnPressed() {
        _selectedTaxa = new ArrayList<Item>();
        HashSet<Item> selectedTaxaSet = new HashSet<Item>();
        for (Object o : _list.getSelectedValues()) {
            String keyword = (String) o;

            _selectedKeywords.add(keyword);

            if (keyword.equals(IntkeyContext.SPECIMEN_KEYWORD)) {
                _specimenSelectedReturnValue.setValue(true);
            } else {
                List<Item> taxa = _context.getTaxaForKeyword(keyword);

                if (_selectFromIncluded) {
                    taxa.retainAll(_includedTaxa);
                }

                selectedTaxaSet.addAll(taxa);
            }
        }
        _selectedTaxa.addAll(selectedTaxaSet);
        Collections.sort(_selectedTaxa);
        this.setVisible(false);
    }

    @Override
    protected void cancelBtnPressed() {
        this.setVisible(false);
    }

    @Override
    protected void listBtnPressed() {
        // List button will only be enabled if a single taxon is selected.
        if (_list.getSelectedValue() != null) {

            List<Item> taxa = new ArrayList<Item>();
            String selectedKeyword = (String) _list.getSelectedValue();

            // do nothing if the "specimen" keyword is selected - this is not a
            // real taxon keyword.
            if (selectedKeyword.equals(IntkeyContext.SPECIMEN_KEYWORD)) {
                return;
            }

            taxa.addAll(_context.getTaxaForKeyword(selectedKeyword));

            if (_selectFromIncluded) {
                taxa.retainAll(_includedTaxa);
            }

            if (taxa.isEmpty()) {
                JOptionPane.showMessageDialog(this, allTaxaInSelectedSetExcludedCaption, title, JOptionPane.ERROR_MESSAGE);
            } else {
                TaxonSelectionDialog taxonDlg = new TaxonSelectionDialog(this, taxa, _directiveName, selectedKeyword, _context.displayNumbering(), false, _context, _includeSpecimenAsOption,
                        _specimenSelectedReturnValue);
                taxonDlg.setVisible(true);

                List<Item> taxaSelectedInDlg = taxonDlg.getSelectedTaxa();
                if (taxaSelectedInDlg != null && taxaSelectedInDlg.size() > 0) {
                    if (_selectedTaxa == null) {
                        _selectedTaxa = new ArrayList<Item>();
                    }
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
            ImageDialog dlg = new ImageDialog(this, _context.getImageSettings(), true, _context.displayScaled());
            dlg.setImages(taxonKeywordImages);
            dlg.showImage(0);
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

    public List<Item> getSelectedTaxa() {
        return _selectedTaxa;
    }

    public List<String> getSelectedKeywords() {
        return _selectedKeywords;
    }

}
