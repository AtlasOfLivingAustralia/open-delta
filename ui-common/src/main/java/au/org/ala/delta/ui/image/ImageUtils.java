package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;

public class ImageUtils {

    // Display the list of images one by one in a full screen window. Clicking
    // the window will
    // cause the next image in the list to be displayed, or will close the
    // window if the last image
    // is currently being displayed.
    public static void displayImagesFullScreen(List<Image> images, ImageSettings imageSettings, Window applicationWindow) {
        final JFrame w = new JFrame(applicationWindow.getGraphicsConfiguration());
        w.setUndecorated(true);
        w.setLayout(new BorderLayout());

        final MultipleImageViewer viewer = new MultipleImageViewer(imageSettings);
        viewer.setBackground(Color.BLACK);
        for (Image image : images) {
            ImageViewer imageViewer = new ImageViewer(image, imageSettings);
            imageViewer.setBackground(Color.BLACK);
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

        w.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    w.setVisible(false);
                }
            }
        });

        Rectangle r = applicationWindow.getGraphicsConfiguration().getBounds();
        w.setLocation(r.x, r.y);
                       
        w.setSize(new Dimension(r.width, r.height));        
        w.setVisible(true);
    }

}
