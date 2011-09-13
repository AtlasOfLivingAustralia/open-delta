package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JPanel;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;

public class ImageUtils {

    // Display the list of images one by one in a full screen window. Clicking
    // the window will
    // cause the next image in the list to be displayed, or will close the
    // window if the last image
    // is currently being displayed.
    public static void displayStartupScreen(List<Image> images, ImageSettings imageSettings, Window applicationWindow) {
        final JDialog w = new JDialog(applicationWindow);
        w.setUndecorated(true);
        w.setLayout(new BorderLayout());

        final MultipleImageViewer viewer = new MultipleImageViewer(imageSettings);
        viewer.setBackground(Color.BLACK);
        for (final Image image : images) {
            ImageViewer imageViewer = new ImageViewer(image, imageSettings);
            imageViewer.setBackground(Color.BLACK);
            imageViewer.addOverlaySelectionObserver(new OverlaySelectionObserver() {
                @Override
                public void overlaySelected(SelectableOverlay overlay) {
                    ImageOverlay imageOverlay = overlay.getImageOverlay();
                    if (imageOverlay.isType(OverlayType.OLOK)) {
                        w.setVisible(false);
                    } else if (imageOverlay.isType(OverlayType.OLCANCEL)) {
                        w.setVisible(false);
                    } else if (imageOverlay.isType(OverlayType.OLIMAGENOTES)) {
                        String rtfContent = image.getNotes();
                        String title = "Image Notes";
                        RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(w, new SimpleRtfEditorKit(null), rtfContent, title);
                        ((SingleFrameApplication) Application.getInstance()).show(dlg);
                    }                    
                }
            });
            
            viewer.addImageViewer(imageViewer);
        }

        GridBagLayout panelLayout = new GridBagLayout();

        JPanel panel = new JPanel(panelLayout);
        panel.setBackground(Color.BLACK);
        panel.add(viewer);
        w.add(panel, BorderLayout.CENTER);

        w.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (viewer.atLastImage()) {
                    w.setVisible(false);
                } else {
                    viewer.nextImage();
                }
            }
        });
        
        w.addKeyListener(new KeyListener() {
            
            @Override
            public void keyTyped(KeyEvent e) {
                w.setVisible(false);
                w.dispose();
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
            }
            
            @Override
            public void keyPressed(KeyEvent e) {
            }
        });

        w.setFocusable(true);

        Rectangle r = applicationWindow.getGraphicsConfiguration().getBounds();
        w.setLocation(r.x, r.y);
                       
        w.setSize(new Dimension(r.width, r.height));        
        w.setVisible(true);
    }

}
