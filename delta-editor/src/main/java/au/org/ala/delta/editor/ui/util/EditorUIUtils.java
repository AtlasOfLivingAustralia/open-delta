package au.org.ala.delta.editor.ui.util;

import javax.swing.ImageIcon;

import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.OrderedMultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.UnorderedMultiStateCharacter;
import au.org.ala.delta.ui.util.IconHelper;
import au.org.ala.delta.util.Platform;

public class EditorUIUtils {

    private static final String ICON_PATH = "/au/org/ala/delta/editor/resources/icons";
    private static final ImageIcon _textIcon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/textchar.png");
    private static final ImageIcon _realIcon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/realchar.png");
    private static final ImageIcon _intIcon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/intchar.png");
    private static final ImageIcon _omIcon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/omchar.png");
    private static final ImageIcon _umIcon = IconHelper.createImageIconFromAbsolutePath(ICON_PATH + "/umchar.png");
    
    public static ImageIcon createLargeIcon() {
        return IconHelper.createBlue32ImageIcon();
    }

    public static ImageIcon createDeltaImageIcon() {
        return IconHelper.createBlue16ImageIcon();
    }

    public static ImageIcon createInternalFrameNormalIcon() {

        if (Platform.isWindowsAero()) {
            return IconHelper.createBlue14ImageIcon();
        } else {
            return IconHelper.createBlue16ImageIcon();
        }

    }

    public static ImageIcon createInternalFrameMaximizedIcon() {
        if (Platform.isWindowsAero()) {
            return IconHelper.createBlue12ImageIcon();
        } else {
            return IconHelper.createBlue14ImageIcon();
        }
    }
    
    /**
     * Returns the appropriate icon for the supplied character.
     * 
     * @param ch
     *            the character to get the icon for.
     * @return an icon representing the type of the supplied Character
     */
    public static ImageIcon iconForCharacter(au.org.ala.delta.model.Character ch) {
        ImageIcon icon = null;
        if (ch instanceof TextCharacter) {
            icon = _textIcon;
        } else if (ch instanceof RealCharacter) {
            icon = _realIcon;
        } else if (ch instanceof IntegerCharacter) {
            icon = _intIcon;
        } else if (ch instanceof OrderedMultiStateCharacter) {
            icon = _omIcon;
        } else if (ch instanceof UnorderedMultiStateCharacter) {
            icon = _umIcon;
        }
        return icon;
    }
}
