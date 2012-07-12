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
package au.org.ala.delta.ui.util;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JInternalFrame.JDesktopIcon;
import javax.swing.LookAndFeel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.NumericCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.RichTextDialog;
import au.org.ala.delta.util.Pair;

public class UIUtils {

    public static RichTextDialog createCharacterDetailsDialog(Window owner, Character character) {
        CharacterFormatter formatter = new CharacterFormatter();
        StringBuilder text = new StringBuilder();
        text.append(formatter.formatCharacterDescription(character));

        if (character instanceof MultiStateCharacter) {
            MultiStateCharacter multiStateChar = (MultiStateCharacter) character;
            for (int i = 1; i <= multiStateChar.getNumberOfStates(); i++) {
                text.append("\\par ");
                text.append(formatter.formatState(multiStateChar, i));
            }
        } else if (character instanceof NumericCharacter<?>) {
            NumericCharacter<?> numericChar = (NumericCharacter<?>) character;
            text.append("\\par ");
            text.append(numericChar.getUnits());
        }
        RichTextDialog dialog = new RichTextDialog(owner, text.toString());
        return dialog;
    }

    public static void cascade(JComponent[] frames, Rectangle dBounds, int separation) {
        int margin = 10 * separation;
        int width = dBounds.width - margin;
        int height = dBounds.height - margin;
        for (int i = 0; i < frames.length; i++) {
            int offset = (frames.length - i - 1) * separation;
            int xOffset = dBounds.x + offset;
            if (xOffset > (dBounds.x + dBounds.width) - width) {
                xOffset -= margin;
            }

            int yOffset = dBounds.y + offset;
            if (yOffset > (dBounds.y + dBounds.height) - height) {
                yOffset -= margin;
            }

            frames[i].setBounds(xOffset, yOffset, width, height);
        }
    }

    public static void cascade(Window[] windows, Rectangle dBounds, int separation) {
        int margin = 10 * separation;
        int width = dBounds.width - margin;
        int height = dBounds.height - margin;
        for (int i = 0; i < windows.length; i++) {
            int offset = (windows.length - i - 1) * separation;
            int xOffset = dBounds.x + offset;
            if (xOffset > (dBounds.x + dBounds.width) - width) {
                xOffset -= margin;
            }

            int yOffset = dBounds.y + offset;
            if (yOffset > (dBounds.y + dBounds.height) - height) {
                yOffset -= margin;
            }

            windows[i].setBounds(xOffset, yOffset, width, height);
        }
    }

    public static void cascade(JDesktopPane desktopPane, int layer) {
        JInternalFrame[] frames = desktopPane.getAllFramesInLayer(layer);
        if (frames.length == 0) {
            return;
        }

        cascade(frames, desktopPane.getBounds(), 24);
    }

    public static void cascade(JDesktopPane desktopPane) {
        JInternalFrame[] frames = desktopPane.getAllFrames();
        if (frames.length == 0) {
            return;
        }

        cascade(frames, desktopPane.getBounds(), 24);
    }

    public static void arrangeMinifiedWindows(JDesktopPane desktop) {
        List<JInternalFrame> minified = new ArrayList<JInternalFrame>();
        JInternalFrame[] frames = desktop.getAllFrames();
        for (JInternalFrame frame : frames) {
            if (frame.isIcon()) {
                minified.add(frame);
            }
        }

        if (minified.size() > 0) {

            Rectangle desktopBounds = desktop.getBounds();

            JDesktopIcon i = minified.get(0).getDesktopIcon();
            Rectangle bounds = i.getBounds();
            int x = 0;
            int y = desktopBounds.height - bounds.height;

            for (JInternalFrame f : minified) {
                JDesktopIcon icon = f.getDesktopIcon();
                icon.setLocation(new Point(x, y));
                x += bounds.width;
                if (x + bounds.width > desktopBounds.width) {
                    x = 0;
                    y -= bounds.height;
                }
            }
        }

    }

    public static Frame getParentFrame(JComponent comp) {
        Container p = comp;
        while (p != null && !(p instanceof Frame)) {
            p = p.getParent();
        }
        return p == null ? null : (Frame) p;
    }

    public static JInternalFrame getParentInternalFrame(JComponent comp) {
        Container p = comp;
        while (p != null && !(p instanceof JInternalFrame)) {
            p = p.getParent();
        }
        return p == null ? null : (JInternalFrame) p;
    }

    public static void centerDialog(JDialog dialog, Container parent) {
        Dimension parentSize = parent.getSize();
        Dimension dialogSize = dialog.getSize();
        Point parentLocn = parent.getLocationOnScreen();

        int locnX = parentLocn.x + (parentSize.width - dialogSize.width) / 2;
        int locnY = parentLocn.y + (parentSize.height - dialogSize.height) / 2;

        dialog.setLocation(locnX, locnY);
    }

    public static void tileWindows(Window[] windows, Rectangle bounds, boolean horizontal) {

        if (windows == null) {
            return;
        }

        // How many frames do we have?
        int count = windows.length;
        if (count == 0)
            return;

        // Determine the necessary grid size
        int sqrt = (int) Math.sqrt(count);
        int rows = sqrt;
        int cols = sqrt;

        if (horizontal) {
            if (rows * cols < count) {
                rows++;
                if (rows * cols < count) {
                    cols++;
                }
            }
        } else {
            if (rows * cols < count) {
                cols++;
                if (rows * cols < count) {
                    rows++;
                }
            }
        }

        // Define some initial values for size & location.
        int w = bounds.width / cols;
        int h = bounds.height / rows;
        int x = bounds.x;
        int y = bounds.y;

        // Iterate over the frames, deiconifying any iconified frames and then
        // relocating & resizing each.
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols && ((i * cols) + j < count); j++) {
                Window f = windows[(i * cols) + j];

                f.setSize(w, h);
                f.setLocation(x, y);
                x += w;
            }
            y += h; // start the next row
            x = bounds.x;
        }
    }

    public static void systemLookAndFeel(JFrame appFrame) {
        try {
            Class<?> c = Class.forName(UIManager.getSystemLookAndFeelClassName());
            LookAndFeel sysLaf = (LookAndFeel) c.newInstance();
            changeLookAndFeel(sysLaf, appFrame);
        } catch (Exception e) {
        }
    }

    public static void metalLookAndFeel(JFrame appFrame) {
        changeLookAndFeel(new MetalLookAndFeel(), appFrame);
    }

    public static void nimbusLookAndFeel(JFrame appFrame) {
        // Nimbus L&F was added in update java 6 update 10.
        LookAndFeel nimbusLaF;
        try {
            nimbusLaF = (LookAndFeel) Class.forName("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel").newInstance();
            changeLookAndFeel(nimbusLaF, appFrame);
        } catch (Exception e) {
        }
    }

    private static void changeLookAndFeel(LookAndFeel laf, JFrame appFrame) {
        try {
            UIManager.setLookAndFeel(laf);
            SwingUtilities.updateComponentTreeUI(appFrame);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }

    /**
     * Search the resource path in the supplied ImageSettings instance for a
     * file with the supplied name
     * 
     * @param fileName
     *            Name of the desired file
     * @param imageSettings
     *            the ImageSettings instance
     * @return URL to the file location
     */
    public static URL findImageFile(String fileName, ImageSettings imageSettings) {
        URL imageLocation = imageSettings.findFileOnResourcePath(fileName, false);

        if (imageLocation == null) {
            throw new IllegalArgumentException("Could not open image file " + fileName);
        }

        return imageLocation;
    }
    
    /**
     * Read the image at the suppiled URL, and return it, along with a String describing the image type
     * @param imageFileLocation URL for image
     * @return image, type of image
     * @throws Exception
     */
    public static Pair<BufferedImage, String> readImage(URL imageFileLocation) throws Exception {
        InputStream inputStream = imageFileLocation.openStream();

        ImageInputStream stream = ImageIO.createImageInputStream(inputStream);
        String imageType;
        BufferedImage image;
        try {
            Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
            if (!iter.hasNext()) {
                return null;
            }

            ImageReader reader = (ImageReader) iter.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(stream, true, true);
            imageType = reader.getFormatName();
            try {
                image = reader.read(0, param);
            } finally {
                reader.dispose();
                stream.close();
            }

            if (image == null) {
                stream.close();
            }
        } finally {
            inputStream.close();
        }
        return new Pair<BufferedImage, String>(image, imageType);
    }

}
