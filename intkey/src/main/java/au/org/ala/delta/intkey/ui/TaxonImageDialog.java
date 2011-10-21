package au.org.ala.delta.intkey.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.JMenuItem;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

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

    private int _selectedTaxonIndex;

    private ItemFormatter _itemFormatter;

    public TaxonImageDialog(Dialog owner, ImageSettings imageSettings, List<Item> taxa, boolean modal) {
        super(owner, imageSettings, modal);
        init(taxa);
    }

    public TaxonImageDialog(Frame owner, ImageSettings imageSettings, List<Item> taxa, boolean modal) {
        super(owner, imageSettings, modal);
        init(taxa);
    }

    private void init(List<Item> taxa) {
        _taxa = new ArrayList<Item>(taxa);

        _itemFormatter = new ItemFormatter(false, CommentStrippingMode.STRIP_ALL, AngleBracketHandlingMode.RETAIN, true, false, false);

        _selectedTaxonIndex = 0;
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(TaxonImageDialog.class);
        resourceMap.injectFields(this);
        buildMenu();

        if (!_taxa.isEmpty()) {
            displayImagesForTaxon(0);
        }
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

    private void displayImagesForTaxon(int taxonIndex) {
        _selectedTaxonIndex = taxonIndex;
        Item selectedTaxon = _taxa.get(_selectedTaxonIndex);

        setImages(selectedTaxon.getImages());

        _mnuItNextTaxon.setEnabled(_selectedTaxonIndex < _taxa.size() - 1);
        _mnuItPreviousTaxon.setEnabled(_selectedTaxonIndex > 0);
        _mnuItMultipleImages.setEnabled(selectedTaxon.getImageCount() > 1);

        updateTitle();
        fitToImage();
        replaySound();
    }

    private void updateTitle() {
        Item selectedTaxon = _taxa.get(_selectedTaxonIndex);

        String formattedTaxonName = _itemFormatter.formatItemDescription(selectedTaxon);
        String formattedImageName = _imageDescriptionFormatter.defaultFormat(_multipleImageViewer.getVisibleViewer().getViewedImage().getSubjectTextOrFileName());

        setTitle(String.format("%s: %s", formattedTaxonName, formattedImageName));
    }

    @Override
    protected void handleNewImageSelected() {
        super.handleNewImageSelected();
        updateTitle();
    }

    public void displayImagesForTaxon(Item taxon) {
        int taxonIndex = _taxa.indexOf(taxon);
        if (taxonIndex > -1) {
            displayImagesForTaxon(taxonIndex);
        }
    }

    @Action
    public void viewNextTaxon() {
        if (_selectedTaxonIndex < _taxa.size() - 1) {
            displayImagesForTaxon(_selectedTaxonIndex + 1);
        }
    }

    @Action
    public void viewPreviousTaxon() {
        if (_selectedTaxonIndex > 0) {
            displayImagesForTaxon(_selectedTaxonIndex - 1);
        }
    }

    @Action
    public void displayMultipleImages() {

    }

}
