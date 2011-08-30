package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.SelectableOverlay;

/**
 * Used to Display images for a single character
 * 
 * @author ChrisF
 * 
 */
public class CharacterImageInputDialog extends ImageDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6274578091708982240L;

    private Character _character;

    // Title used when the dialog is used to display images for an integer
    // character
    @Resource
    String integerTitle;

    // Title used when the dialog is used to display images for a real character
    @Resource
    String realTitle;

    // Title used when the dialog is used to display images for an multistate
    // character
    @Resource
    String multistateTitle;

    public CharacterImageInputDialog(Frame owner, Character character, ImageSettings imageSettings) {
        super(owner, imageSettings);
        init(character);
    }

    /**
     * @wbp.parser.constructor
     */
    public CharacterImageInputDialog(Dialog owner, Character character, ImageSettings imageSettings) {
        super(owner, imageSettings);
        init(character);
    }

    private void init(Character character) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterImageInputDialog.class);
        resourceMap.injectFields(this);

        _character = character;
        getContentPane().setLayout(new BorderLayout(0, 0));

        buildMenu();

        List<Image> images = _character.getImages();
        setImages(images);

        getContentPane().add(_multipleImageViewer, BorderLayout.CENTER);

        if (character instanceof IntegerCharacter) {
            setTitle(integerTitle);
        } else if (character instanceof RealCharacter) {
            setTitle(realTitle);
        } else if (character instanceof MultiStateCharacter) {
            setTitle(multistateTitle);
        }

        this.pack();
    }

    private void buildMenu() {
        // add character specific stuff here
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        ImageOverlay imageOverlay = overlay.getImageOverlay();
        if (imageOverlay.isType(OverlayType.OLNOTES)) {
            displayRTFWindow(_character.getNotes(), "Notes");
        } else {
            super.overlaySelected(overlay);
        }
    }

}
