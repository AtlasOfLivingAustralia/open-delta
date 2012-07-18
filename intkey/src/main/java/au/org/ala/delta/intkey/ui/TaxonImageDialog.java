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
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.model.image.ImageSettings;

public class TaxonImageDialog extends ImageDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -358445604269517364L;

    private List<Item> _taxa;

    private JMenuItem _mnuItNextTaxon;
    private JMenuItem _mnuItPreviousTaxon;
    private JMenuItem _mnuItMultipleImages;

    private boolean _multipleImagesMenuEnabled;

    private int _selectedTaxonIndex;

    private ItemFormatter _itemFormatter;

    private List<String> _imageSubjects;
    private IntkeyUI _mainUI;

    public TaxonImageDialog(Dialog owner, ImageSettings imageSettings, List<Item> taxa, boolean modal, boolean multipleImagesMenuEnabled, boolean initScalingMode, List<String> imageSubjects,
            IntkeyUI appUI) {
        super(owner, imageSettings, modal, initScalingMode);
        init(taxa, multipleImagesMenuEnabled, imageSubjects, appUI);
    }

    public TaxonImageDialog(Frame owner, ImageSettings imageSettings, List<Item> taxa, boolean modal, boolean multipleImagesMenuEnabled, boolean initScalingMode, List<String> imageSubjects,
            IntkeyUI appUI) {
        super(owner, imageSettings, modal, initScalingMode);
        init(taxa, multipleImagesMenuEnabled, imageSubjects, appUI);
    }

    private void init(List<Item> taxa, boolean multipleImagesMenuEnabled, List<String> imageSubjects, IntkeyUI appUI) {
        _multipleImagesMenuEnabled = multipleImagesMenuEnabled;
        _imageSubjects = new ArrayList<String>(imageSubjects);
        _mainUI = appUI;
        _taxa = new ArrayList<Item>(taxa);

        _itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false, false);

        _selectedTaxonIndex = 0;
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonImageDialog.class);
        resourceMap.injectFields(this);
        buildMenu();
    }

    private void buildMenu() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(TaxonImageDialog.class, this);

        _mnuItNextTaxon = new JMenuItem();
        _mnuItNextTaxon.setAction(actionMap.get("viewNextTaxon"));
        _mnuControl.add(_mnuItNextTaxon);

        _mnuItPreviousTaxon = new JMenuItem();
        _mnuItPreviousTaxon.setAction(actionMap.get("viewPreviousTaxon"));
        _mnuControl.add(_mnuItPreviousTaxon);

        _mnuControl.addSeparator();

        _mnuItMultipleImages = new JMenuItem();
        _mnuItMultipleImages.setAction(actionMap.get("displayMultipleImages"));
        _mnuControl.add(_mnuItMultipleImages);
    }

    private void displayImagesForTaxon(int taxonIndex, int imageToShow) {
        _selectedTaxonIndex = taxonIndex;
        Item selectedTaxon = _taxa.get(_selectedTaxonIndex);

        setImages(selectedTaxon.getImages());
        showImage(imageToShow);

        _mnuItNextTaxon.setEnabled(_selectedTaxonIndex < _taxa.size() - 1);
        _mnuItPreviousTaxon.setEnabled(_selectedTaxonIndex > 0);
        _mnuItMultipleImages.setEnabled(_multipleImagesMenuEnabled && selectedTaxon.getImageCount() > 1);
    }

    private void updateTitle() {
        Item selectedTaxon = _taxa.get(_selectedTaxonIndex);

        String formattedTaxonName = _itemFormatter.formatItemDescription(selectedTaxon);
        String formattedImageName = _imageDescriptionFormatter.defaultFormat(_multipleImageViewer.getVisibleImage().getSubjectTextOrFileName());

        setTitle(String.format("%s: %s", formattedTaxonName, formattedImageName));
    }

    @Override
    protected void handleNewImageSelected() {
        super.handleNewImageSelected();
        updateTitle();
    }

    public void displayImagesForTaxon(Item taxon, int imageToShow) {
        int taxonIndex = _taxa.indexOf(taxon);
        if (taxonIndex > -1) {
            displayImagesForTaxon(taxonIndex, imageToShow);
        }
    }

    @Action
    public void viewNextTaxon() {
        if (_selectedTaxonIndex < _taxa.size() - 1) {
            displayImagesForTaxon(_selectedTaxonIndex + 1, 0);
        }
    }

    @Action
    public void viewPreviousTaxon() {
        if (_selectedTaxonIndex > 0) {
            displayImagesForTaxon(_selectedTaxonIndex - 1, 0);
        }
    }

    @Action
    public void displayMultipleImages() {
        MultipleImagesDialog dlg = new MultipleImagesDialog(UIUtils.getMainFrame(), true, _taxa.get(_selectedTaxonIndex), _taxa, _imageSubjects, _imageSettings, _multipleImagesMenuEnabled,
                _scaleImages, _mainUI);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
    }

}
