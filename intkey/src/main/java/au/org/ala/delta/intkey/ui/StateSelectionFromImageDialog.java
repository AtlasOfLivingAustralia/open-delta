package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;

public class StateSelectionFromImageDialog extends JDialog implements OverlaySelectionObserver {

    private ImageSettings _imageSettings;
    private Set<Integer> _selectedStates;
    private Character _character;
    private MultipleImageViewer _multipleImageViewer;

    public StateSelectionFromImageDialog(Frame owner, Character character, ImageSettings imageSettings) {
        super(owner, true);
        init(character, imageSettings);
    }

    /**
     * @wbp.parser.constructor
     */
    public StateSelectionFromImageDialog(Dialog owner, Character character, ImageSettings imageSettings) {
        super(owner, true);
        init(character, imageSettings);
    }

    private void init(Character character, ImageSettings imageSettings) {
        getContentPane().setLayout(new BorderLayout(0, 0));

        _imageSettings = imageSettings;
        _character = character;
        _selectedStates = new HashSet<Integer>();

        buildMenu();
        
        List<Image> images = _character.getImages();
        _multipleImageViewer = new MultipleImageViewer(_imageSettings);
        for (Image image: images) {
            ImageViewer viewer = new ImageViewer(image, _imageSettings);
            _multipleImageViewer.addImageViewer(viewer);
            viewer.addOverlaySelectionObserver(this);
        }

        getContentPane().add(_multipleImageViewer, BorderLayout.CENTER);     
        
        this.pack();
    }
    
    private void buildMenu() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);
        
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnControl = new JMenu("Control");
        menuBar.add(mnControl);

        JMenuItem mnuItNextImage = new JMenuItem();
        mnuItNextImage.setAction(actionMap.get("StateSelectionFromImageDialog_mnuItNextImage"));
        mnControl.add(mnuItNextImage);

        JMenuItem mnuItPreviousImage = new JMenuItem();
        mnuItPreviousImage.setAction(actionMap.get("StateSelectionFromImageDialog_mnuItPreviousImage"));
        mnControl.add(mnuItPreviousImage);        
    }

    public Set<Integer> getSelectedStates() {
        return _selectedStates;
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        overlay.setSelected(!overlay.isSelected());
        ImageOverlay imageOverlay = overlay.getImageOverlay();
        if (imageOverlay.isType(OverlayType.OLOK) || imageOverlay.isType(OverlayType.OLCANCEL)) {
            this.setVisible(false);
        } else if (imageOverlay.isType(OverlayType.OLNOTES)) {
            // showCharacterNotes();
        } else if (imageOverlay.isType(OverlayType.OLIMAGENOTES)) {
            // showImageNotes();
        } else if (imageOverlay.isType(OverlayType.OLSTATE)) {
            int stateId = imageOverlay.stateId;
            if (overlay.isSelected()) {
                _selectedStates.add(stateId);
            } else {
                _selectedStates.remove(stateId);
            }
        }

    }

    @Action
    public void StateSelectionFromImageDialog_mnuItNextImage() {
        _multipleImageViewer.nextImage();
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
    }
    
    @Action
    public void StateSelectionFromImageDialog_mnuItPreviousImage() {
        _multipleImageViewer.previousImage();
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
    }
    
    private void reSelectStatesInNewViewer(ImageViewer viewer) {
        List<ImageOverlay> overlays = viewer.getOverlays();
        for (ImageOverlay overlay : overlays) {
            if (overlay.isType(OverlayType.OLSTATE)) {
                int stateId = overlay.stateId;

                HotSpotGroup hotSpotGroup = viewer.getHotSpotGroupForOverlay(overlay);
                hotSpotGroup.setSelected(_selectedStates.contains(stateId));
            }
        }
        
    }
}
