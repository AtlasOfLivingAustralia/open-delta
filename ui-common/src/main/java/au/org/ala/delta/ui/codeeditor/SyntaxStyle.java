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
import java.awt.Toolkit;

public class SyntaxStyle {

    /** The style color. */
    private Color color;
    /** The italic flag. */
    private boolean italic;
    /** The bold flag. */
    private boolean bold;
    /** The last font. */
    private Font lastFont;
    /** The last styled font. */
    private Font lastStyledFont;
    /** The font metrics of the last styled font. */
    private FontMetrics fontMetrics;

    /**
     * Creates a new SyntaxStyle.
     *
     * @param color
     *            The text color
     * @param italic
     *            True if the text should be italics
     * @param bold
     *            True if the text should be bold
     */
    public SyntaxStyle(Color color, boolean italic, boolean bold) {
        this.color = color;
        this.italic = italic;
        this.bold = bold;
    }

    /**
     * Returns the color specified in this style.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Returns true if no font styles are enabled.
     */
    public boolean isPlain() {
        return !(bold || italic);
    }

    /**
     * Returns true if italics is enabled for this style.
     */
    public boolean isItalic() {
        return italic;
    }

    /**
     * Returns true if boldface is enabled for this style.
     */
    public boolean isBold() {
        return bold;
    }

    /**
     * Returns the specified font, but with the style's bold and italic flags applied.
     */
    public Font getStyledFont(Font font) {
        if (font == null) {
            throw new NullPointerException("font param must not" + " be null");
        }
        if (font.equals(lastFont)) {
            return lastStyledFont;
        }
        lastFont = font;
        lastStyledFont = new Font(font.getFamily(), (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0), font.getSize());
        return lastStyledFont;
    }

    /**
     * Returns the font metrics for the styled font.
     */
    @SuppressWarnings("deprecation")
    public FontMetrics getFontMetrics(Font font) {
        if (font == null) {
            throw new NullPointerException("font param must not" + " be null");
        }
        if (font.equals(lastFont) && fontMetrics != null) {
            return fontMetrics;
        }
        lastFont = font;
        lastStyledFont = new Font(font.getFamily(), (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0), font.getSize());
        fontMetrics = Toolkit.getDefaultToolkit().getFontMetrics(lastStyledFont);
        return fontMetrics;
    }

    /**
     * Sets the foreground color and font of the specified graphics context to that specified in this style.
     *
     * @param gfx
     *            The graphics context
     * @param font
     *            The font to add the styles to
     */
    public void setGraphicsFlags(Graphics gfx, Font font) {
        Font _font = getStyledFont(font);
        gfx.setFont(_font);
        gfx.setColor(color);
    }

    /**
     * Returns a string representation of this object.
     */
    public String toString() {
        return getClass().getName() + "[color=" + color + (italic ? ",italic" : "") + (bold ? ",bold" : "") + "]";
    }
}
