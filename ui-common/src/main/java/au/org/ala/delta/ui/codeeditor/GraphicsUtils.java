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
package au.org.ala.delta.ui.codeeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.PrintGraphics;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.print.PrinterGraphics;

import javax.swing.JComponent;

public class GraphicsUtils {
	
    /**
     * Returns the FontMetrics for the current Font of the passed in Graphics. This method is used when a Graphics is available, typically when painting. If a
     * Graphics is not available the JComponent method of the same name should be used.
     * <p>
     * Callers should pass in a non-null JComponent, the exception to this is if a JComponent is not readily available at the time of painting.
     * <p>
     * This does not necessarily return the FontMetrics from the Graphics.
     *
     * @param c
     *            JComponent requesting FontMetrics, may be null
     * @param g
     *            Graphics Graphics
     */
    public static FontMetrics getFontMetrics(JComponent c, Graphics g) {
        return getFontMetrics(c, g, g.getFont());
    }

    /**
     * Returns the FontMetrics for the specified Font. This method is used when a Graphics is available, typically when painting. If a Graphics is not available
     * the JComponent method of the same name should be used.
     * <p>
     * Callers should pass in a non-null JComonent, the exception to this is if a JComponent is not readily available at the time of painting.
     * <p>
     * This does not necessarily return the FontMetrics from the Graphics.
     *
     * @param c
     *            JComponent requesting FontMetrics, may be null
     * @param c
     *            Graphics Graphics
     * @param font
     *            Font to get FontMetrics for
     */
    @SuppressWarnings("deprecation")
    public static FontMetrics getFontMetrics(JComponent c, Graphics g, Font font) {
        if (c != null) {
            // Note: We assume that we're using the FontMetrics
            // from the widget to layout out text, otherwise we can get
            // mismatches when printing.
            return c.getFontMetrics(font);
        }
        return Toolkit.getDefaultToolkit().getFontMetrics(font);
    }
    
    private static boolean isPrinting(Graphics g) {
        return (g instanceof PrinterGraphics || g instanceof PrintGraphics);
    }
    
    public static Graphics2D getGraphics2D(Graphics g) {
        if (g instanceof Graphics2D) {
            return (Graphics2D) g;
        } else {
            return null;
        }
    }
    
    /**
     * Returns the FontRenderContext for the passed in FontMetrics or for the passed in JComponent if FontMetrics is null
     */
    private static FontRenderContext getFRC(JComponent c, FontMetrics fm) {
        if (fm == null && c != null) {
            // we do it this way because we need first case to
            // work as fast as possible
            return getFRC(c, c.getFontMetrics(c.getFont()));
        }
        return null;
    }

    private static boolean isFontRenderContextCompatible(FontRenderContext frc1, FontRenderContext frc2) {
        return (frc1 != null) ? frc1.equals(frc2) : frc2 == null;
    }

    
    /**
     * The following draw functions have the same semantic as the Graphics methods with the same names.
     *
     * this is used for printing
     */
    public static int drawChars(JComponent c, Graphics g, char[] data, int offset, int length, int x, int y) {
        if (length <= 0) { // no need to paint empty strings
            return x;
        }
        int nextX = x + getFontMetrics(c, g).charsWidth(data, offset, length);
        if (isPrinting(g)) {
            Graphics2D g2d = getGraphics2D(g);
            if (g2d != null) {
                FontRenderContext deviceFontRenderContext = g2d.getFontRenderContext();
                FontRenderContext frc = getFRC(c, null);
                if (frc.isAntiAliased() || frc.usesFractionalMetrics()) {
                    frc = new FontRenderContext(frc.getTransform(), false, false);
                }
                if (frc != null && !isFontRenderContextCompatible(deviceFontRenderContext, frc)) {
                    TextLayout layout = new TextLayout(new String(data, offset, length), g2d.getFont(), frc);

                    /* Use alternate print color if specified */
                    Color col = g2d.getColor();

                    layout.draw(g2d, x, y);

                    g2d.setColor(col);

                    return nextX;
                }
            }
        }
        // Assume we're not printing if we get here.
        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            Object oldAAValue = g2.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.drawChars(data, offset, length, x, y);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, oldAAValue);
        } else {
            g.drawChars(data, offset, length, x, y);
        }
        return nextX;
    }

}
