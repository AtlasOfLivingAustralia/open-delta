package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;

/**
 * Used to Display images for a single character
 * @author ChrisF
 *
 */
public class CharacterImageInputDialog extends ImageDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6274578091708982240L;

    private Character _character;

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
        _character = character;
        getContentPane().setLayout(new BorderLayout(0, 0));

        buildMenu();

        List<Image> images = _character.getImages();
        setImages(images);

        getContentPane().add(_multipleImageViewer, BorderLayout.CENTER);

        this.pack();
    }

    private void buildMenu() {
        // add character specific stuff here
    }

}
