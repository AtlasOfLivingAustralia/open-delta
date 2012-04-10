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
import java.awt.GridLayout;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class TaxonSelectionDialog extends ListSelectionDialog {
    /**
     * 
     */
    private static final long serialVersionUID = -147794343857823234L;

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
    
    private IntkeyContext _context;
    
    private ItemFormatter _fullTextTaxonFormatter;
    
    @Resource
    String title;

    @Resource
    String titleFromKeyword;
    
    @Resource
    String fullTextOfTaxonNameCaption;

    public TaxonSelectionDialog(Dialog owner, List<Item> taxa, String directiveName, String keyword, boolean displayNumbering, boolean singleSelect, IntkeyContext context) {
        this(owner, taxa, directiveName, displayNumbering, singleSelect, context);
        _keyword = keyword;
        setTitle(MessageFormat.format(titleFromKeyword, _directiveName, _keyword));
    }

    public TaxonSelectionDialog(Frame owner, List<Item> taxa, String directiveName, String keyword, boolean displayNumbering, boolean singleSelect, IntkeyContext context) {
        this(owner, taxa, directiveName, displayNumbering, singleSelect, context);
        _keyword = keyword;
        setTitle(MessageFormat.format(titleFromKeyword, _directiveName, _keyword));
    }

    public TaxonSelectionDialog(Dialog owner, List<Item> taxa, String directiveName, boolean displayNumbering, boolean singleSelect, IntkeyContext context) {
        super(owner);
        _directiveName = directiveName;
        _context = context;
        init(taxa, displayNumbering, singleSelect);
    }

    public TaxonSelectionDialog(Frame owner, List<Item> taxa, String directiveName, boolean displayNumbering, boolean singleSelect, IntkeyContext context) {
        super(owner);
        _directiveName = directiveName;
        _context = context;
        init(taxa, displayNumbering, singleSelect);
    }

    private void init(List<Item> taxa, boolean displayNumbering, boolean singleSelect) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonSelectionDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(TaxonSelectionDialog.class, this);

        setTitle(MessageFormat.format(title, _directiveName));
        
        _fullTextTaxonFormatter = new ItemFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REPLACE, true, false, false);

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
        _panelButtons.add(_btnKeywords);

        _btnImages = new JButton();
        _btnImages.setAction(actionMap.get("taxonSelectionDialog_Images"));
        _btnImages.setEnabled(false);
        _panelButtons.add(_btnImages);

        _btnSearch = new JButton();
        _btnSearch.setAction(actionMap.get("taxonSelectionDialog_Search"));
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

        _selectedTaxa = null;

        if (taxa != null) {
            _listModel = new DefaultListModel();
            for (Item taxon : taxa) {
                _listModel.addElement(taxon);
            }
            _list.setCellRenderer(new TaxonCellRenderer(displayNumbering, false));
            _list.setModel(_listModel);
        }

        if (singleSelect) {
            _list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        
        _list.addListSelectionListener(new ListSelectionListener() {
            
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (_list.getSelectedIndices().length == 1) {
                    Item taxon = (Item) _list.getSelectedValue();
                    _btnImages.setEnabled(taxon.getImages().size() > 0);
                    _btnFullText.setEnabled(true);
                } else {
                    _btnImages.setEnabled(false);
                    _btnFullText.setEnabled(false);
                }
            }
        });
    }

    @Action
    public void taxonSelectionDialog_OK() {
        _selectedTaxa = new ArrayList<Item>();
        for (int i : _list.getSelectedIndices()) {
            _selectedTaxa.add((Item) _listModel.getElementAt(i));
        }

        this.setVisible(false);
    }

    @Action
    public void taxonSelectionDialog_SelectAll() {
        _list.setSelectionInterval(0, _listModel.getSize() - 1);
    }

    @Action
    public void taxonSelectionDialog_Keywords() {
        if (this.getOwner() instanceof TaxonKeywordSelectionDialog) {
            // If this dialog was spawned using the "List" button in a taxon keyword selection dialog, just 
            // close this window and bring the parent into focus
            _selectedTaxa = null;
            this.setVisible(false);
        } else {
            TaxonKeywordSelectionDialog dlg = new TaxonKeywordSelectionDialog(this, _context, _directiveName, false);
            ((SingleFrameApplication) Application.getInstance()).show(dlg);
            if (dlg.getSelectedTaxa() != null) {
                _selectedTaxa = dlg.getSelectedTaxa();
                this.setVisible(false);
            }
        }
    }

    @Action
    public void taxonSelectionDialog_Images() {
        // images button will only be enabled if a single taxon is selected
        Item taxon = (Item) _list.getSelectedValue();
        List<Item> taxonInList = new ArrayList<Item>();
        taxonInList.add(taxon);
        TaxonImageDialog dlg = new TaxonImageDialog(this, _context.getImageSettings(), taxonInList, false, !_context.displayContinuous(), _context.displayScaled());
        dlg.displayImagesForTaxon(taxon);
        dlg.showImage(0);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
    }

    @Action
    public void taxonSelectionDialog_Search() {
        SimpleSearchDialog dlg = new SimpleSearchDialog(this);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
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
        // full text button will only be enabled if a single taxon is selected
        Item taxon = (Item) _list.getSelectedValue();
        RTFBuilder rtfBuilder = new RTFBuilder();
        rtfBuilder.startDocument();
        rtfBuilder.appendText(_fullTextTaxonFormatter.formatItemDescription(taxon));
        rtfBuilder.endDocument();
        
        RtfReportDisplayDialog rtfDlg = new RtfReportDisplayDialog(this, new SimpleRtfEditorKit(null), rtfBuilder.toString(), fullTextOfTaxonNameCaption);
        ((SingleFrameApplication) Application.getInstance()).show(rtfDlg);
    }

    @Action
    public void taxonSelectionDialog_Help() {

    }

    public List<Item> getSelectedTaxa() {
        return _selectedTaxa;
    }

    @Override
    public int searchForText(String searchText, int startingIndex) {
        int matchedIndex = -1;
        
        ItemFormatter formatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.REMOVE, true, false, false);
        
        for (int i=startingIndex; i < _listModel.size(); i++) {
            Item taxon = (Item) _listModel.getElementAt(i);
            String taxonText = formatter.formatItemDescription(taxon);
            if (taxonText.trim().toLowerCase().contains(searchText.trim().toLowerCase())) {
                matchedIndex = i;
                _list.setSelectedIndex(i);
                _list.ensureIndexIsVisible(i);
                break;
            }
        }
        
        return matchedIndex;
    }

}
