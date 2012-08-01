package au.org.ala.delta.editor.ui;

import com.apple.laf.AquaInternalFrameUI;

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
        if (frame.getUI() instanceof AquaInternalFrameUI) {
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
