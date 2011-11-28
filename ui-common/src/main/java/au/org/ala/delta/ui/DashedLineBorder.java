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
package au.org.ala.delta.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Stroke;

import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.plaf.basic.BasicGraphicsUtils;

public class DashedLineBorder implements Border {

    /**
     * 
     */
    private static final long serialVersionUID = -7959521855099650590L;

    private Color _foregroundColor;
    private Color _backgroundColor;

    public DashedLineBorder(Color foregroundColor, Color backgroundColor) {
        _foregroundColor = foregroundColor;
        _backgroundColor = backgroundColor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        // We need an instance of Graphics2D to be able to paint the dashed line
        // border.
        // Simply do nothing if the graphics object is not an instance of
        // Graphics2D - the only other
        // subtype of Graphics is DebugGraphics which we don't need to worry
        // about supporting.
        if (g instanceof Graphics2D) {
            Graphics2D g2d = (Graphics2D) g;

            Color oldColor = g2d.getColor();
            Stroke oldStroke = g2d.getStroke();

            // First up, draw a solid rectangle in the desired background color.
            g2d.setColor(_backgroundColor);
            g2d.drawRect(x, y, width - 1, height - 1);

            // Now draw a dashed rectangle in the desired foreground color
            g2d.setColor(_foregroundColor);

            if (g2d instanceof Graphics2D) {
                g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, new float[] { 3, 3 }, 0));
            }

            g2d.drawRect(x, y, width - 1, height - 1);

            g2d.setColor(oldColor);
            g2d.setStroke(oldStroke);
        }
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(1, 1, 1, 1);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

}
