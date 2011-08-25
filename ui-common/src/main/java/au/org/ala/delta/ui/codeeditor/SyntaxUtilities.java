package au.org.ala.delta.ui.codeeditor;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.text.Segment;
import javax.swing.text.TabExpander;

public class SyntaxUtilities implements IGlyphPainter {

    /**
     * Checks if a subregion of a <code>Segment</code> is equal to a string.
     *
     * @param ignoreCase
     *            True if case should be ignored, false otherwise
     * @param text
     *            The segment
     * @param offset
     *            The offset into the segment
     * @param match
     *            The string to match
     * @return true if the region matches the String match, else false.
     */
    public static boolean regionMatches(boolean ignoreCase, Segment text, int offset, String match) {
        int length = offset + match.length();
        char[] textArray = text.array;
        if (length > text.offset + text.count)
            return false;
        for (int i = offset, j = 0; i < length; i++, j++) {
            char c1 = textArray[i];
            char c2 = match.charAt(j);
            if (ignoreCase) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
            }
            if (c1 != c2)
                return false;
        }
        return true;
    }

    /**
     * Checks if a subregion of a <code>Segment</code> is equal to a character array.
     *
     * @param ignoreCase
     *            True if case should be ignored, false otherwise
     * @param text
     *            The segment
     * @param offset
     *            The offset into the segment
     * @param match
     *            The character array to match
     * @return true if the region matches the String match, else false.
     */
    public static boolean regionMatches(boolean ignoreCase, Segment text, int offset, char[] match) {
        int length = offset + match.length;
        char[] textArray = text.array;
        if (length > text.offset + text.count)
            return false;
        for (int i = offset, j = 0; i < length; i++, j++) {
            char c1 = textArray[i];
            char c2 = match[j];
            if (ignoreCase) {
                c1 = Character.toUpperCase(c1);
                c2 = Character.toUpperCase(c2);
            }
            if (c1 != c2)
                return false;
        }
        return true;
    }

    private static SyntaxUtilities _instance = new SyntaxUtilities();

    /**
     * Returns the default style table. This can be passed to the <code>setStyles()</code> method of <code>SyntaxDocument</code> to use the default syntax styles.
     *
     * @return The default syntax styles.
     */
    public static SyntaxStyle[] getDefaultSyntaxStyles() {
        SyntaxStyle[] styles = new SyntaxStyle[Token.ID_COUNT];

        styles[Token.COMMENT1] = new SyntaxStyle(new Color(0x009900), true, false);
        styles[Token.COMMENT2] = new SyntaxStyle(new Color(0x000099), true, false);
        styles[Token.KEYWORD1] = new SyntaxStyle(Color.blue, false, false);
        styles[Token.KEYWORD2] = new SyntaxStyle(Color.blue, false, false);
        styles[Token.KEYWORD3] = new SyntaxStyle(new Color(0x009600), false, false);
        styles[Token.LITERAL1] = new SyntaxStyle(Color.blue, false, false);
        styles[Token.LITERAL2] = new SyntaxStyle(Color.blue, false, false);
        styles[Token.LABEL] = new SyntaxStyle(new Color(0x990033), false, true);
        styles[Token.OPERATOR] = new SyntaxStyle(Color.black, false, false);
        styles[Token.INVALID] = new SyntaxStyle(Color.red, false, true);

        return styles;
    }

    /**
     * Paints the specified line onto the graphics context. Note that this method munges the offset and count values of the segment.
     *
     * @param line
     *            The line segment
     * @param tokens
     *            The token list for the line
     * @param styles
     *            The syntax style list
     * @param expander
     *            The tab expander used to determine tab stops. May be null
     * @param gfx
     *            The graphics context
     * @param x
     *            The x co-ordinate
     * @param y
     *            The y co-ordinate
     * @return The x co-ordinate, plus the width of the painted string
     */
    public static int paintSyntaxLine(Segment line, Token tokens, SyntaxStyle[] styles, TabExpander expander, Graphics gfx, int x, int y, boolean showwhitespace) {
        Font defaultFont = gfx.getFont();
        Color defaultColor = gfx.getColor();

        int offset = 0;
        for (;;) {
            byte id = tokens.id;
            if (id == Token.END)
                break;

            int length = tokens.length;
            if (id == Token.NULL) {
                if (!defaultColor.equals(gfx.getColor()))
                    gfx.setColor(defaultColor);
                if (!defaultFont.equals(gfx.getFont()))
                    gfx.setFont(defaultFont);
            } else
                styles[id].setGraphicsFlags(gfx, defaultFont);

            line.count = length;
            x = drawTabbedText(line, x, y, gfx, expander, 0, (showwhitespace ? _instance : null));
            line.offset += length;
            offset += length;

            tokens = tokens.next;
        }

        return x;
    }

    static final int drawTabbedText(Segment s, int x, int y, Graphics g, TabExpander e, int startOffset, IGlyphPainter glyphpainter) {
        JComponent component = null;
        FontMetrics metrics = GraphicsUtils.getFontMetrics(component, g);
        int nextX = x;
        char[] txt = s.array;
        int txtOffset = s.offset;
        int flushLen = 0;
        int flushIndex = s.offset;
        int spaceAddon = 0;
        int startJustifiableContent = 0;
        int endJustifiableContent = 0;
        int n = s.offset + s.count;
        int spacewidth = metrics.charWidth(' ');
        int lineheight = 10;
        Color textcolor = g.getColor();
        for (int i = txtOffset; i < n; i++) {
            if (txt[i] == '\t' || ((spaceAddon != 0) && (txt[i] == ' ') && startJustifiableContent <= i && i <= endJustifiableContent)) {
                if (flushLen > 0) {
                    g.setColor(textcolor);
                    nextX = GraphicsUtils.drawChars(component, g, txt, flushIndex, flushLen, x, y);
                    flushLen = 0;
                }
                flushIndex = i + 1;
                if (txt[i] == '\t') {
                    if (e != null) {
                        int oldx = nextX;
                        nextX = (int) e.nextTabStop((float) nextX, startOffset + i - txtOffset);
                        if (glyphpainter != null) {
                            glyphpainter.paintGlyph('\t', g, oldx, y, nextX - oldx, lineheight);
                        }
                    } else {
                        nextX += spacewidth;
                    }
                } else if (txt[i] == ' ') {
                    if (glyphpainter != null) {
                        glyphpainter.paintGlyph(' ', g, nextX, y, spacewidth, lineheight);
                    }
                    nextX += spacewidth + spaceAddon;
                }
                x = nextX;
            } else if ((txt[i] == '\n') || (txt[i] == '\r')) {
                if (flushLen > 0) {
                    g.setColor(textcolor);
                    nextX = GraphicsUtils.drawChars(component, g, txt, flushIndex, flushLen, x, y);
                    flushLen = 0;
                }
                if (glyphpainter != null) {
                    glyphpainter.paintGlyph('\n', g, nextX, y, spacewidth, lineheight);
                }
                flushIndex = i + 1;
                x = nextX;
            } else if (txt[i] == ' ') {
                if (flushLen > 0) {
                    g.setColor(textcolor);
                    nextX = GraphicsUtils.drawChars(component, g, txt, flushIndex, flushLen, x, y);
                    flushLen = 0;
                }
                if (glyphpainter != null) {
                    glyphpainter.paintGlyph(' ', g, nextX, y, spacewidth, lineheight);
                }
                nextX += spacewidth;
                flushIndex = i + 1;
                x = nextX;
            } else {
                flushLen += 1;
            }
        }
        if (flushLen > 0) {
            g.setColor(textcolor);
            nextX = GraphicsUtils.drawChars(component, g, txt, flushIndex, flushLen, x, y);
        }
        return nextX;
    }

    public void paintGlyph(char c, Graphics g, int x, int y, int width, int height) {
        g.setColor(Color.gray);
        switch (c) {
            case ' ':
                g.fillRoundRect(x + (width / 2), y - 2, 2, 2, 2, 2);
                break;
            case '\n':
                g.drawString("¶", x, y);
                break;
            case '\t':
                g.drawString("»", x + (width/2), y);
                break;
            default:
                g.drawRect(x, y - height, width, height);
        }
    }
    
    

}
