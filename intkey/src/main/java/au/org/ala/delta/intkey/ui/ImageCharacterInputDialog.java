package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.util.Pair;

public class ImageCharacterInputDialog extends ImageDialog {



    private Character _character;

    public ImageCharacterInputDialog(Frame owner, Character character, ImageSettings imageSettings) {
        super(owner, imageSettings);
        init(character);
    }

    /**
     * @wbp.parser.constructor
     */
    public ImageCharacterInputDialog(Dialog owner, Character character, ImageSettings imageSettings) {
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
