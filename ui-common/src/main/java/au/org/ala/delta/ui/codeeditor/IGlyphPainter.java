package au.org.ala.delta.ui.codeeditor;

import java.awt.Graphics;


public interface IGlyphPainter {
    
    void paintGlyph(char c, Graphics g, int x, int y, int width, int height);

}
