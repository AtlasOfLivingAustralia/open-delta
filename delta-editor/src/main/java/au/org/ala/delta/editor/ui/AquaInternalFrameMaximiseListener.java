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
package au.org.ala.delta.editor.ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;

/**
 * This class exists to work around a problem with JInternalFrame in the Aqua look and feel.
 * When a frame is maximised, the drop shadow border is not removed, leaving a largish gap between the sides of the
 * maximised internal frame and the desktop pane.
 * This class listens for changes to the JInternalFrame maximised state and adds and removes the drop shadow as
 * appropriate.
 */
public class AquaInternalFrameMaximiseListener implements PropertyChangeListener {

    /** Stores the drop shadow so it can be restored later */
    private Border dropShadow;

    /** An empty border to replace the drop shadow border when the frame is maximised */
    private Border emptyBorder = BorderFactory.createEmptyBorder();

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        JInternalFrame frame = (JInternalFrame)evt.getSource();
        if ("com.apple.laf.AquaInternalFrameUI".equals(frame.getUI().getClass().getName())) {
            if (JInternalFrame.IS_MAXIMUM_PROPERTY.equals(evt.getPropertyName())) {

                Border border = frame.getBorder();
                if (border instanceof CompoundBorder) {
                    Border outerBorder = ((CompoundBorder)border).getOutsideBorder();
                    if (outerBorder != emptyBorder) {
                        dropShadow = outerBorder;
                    }

                    boolean maximised = (Boolean)evt.getNewValue();
                    Border newOuterBorder = maximised ? emptyBorder : dropShadow;

                    // We are directly updating the field here rather than creating a new compound
                    // border because the UI relies on various listeners being installed on the current
                    // instance.
                    if (newOuterBorder != outerBorder) {
                        try {
                            Field field = CompoundBorder.class.getDeclaredField("outsideBorder");
                            field.setAccessible(true);
                            field.set(border, newOuterBorder);
                            frame.revalidate();
                        }
                        catch (Exception e) {

                        }
                    }
                }
            }
        }

    }

}
