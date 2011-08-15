package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.util.Pair;

public class ImageUtils {

    // Display the list of images one by one in a full screen window. Clicking
    // the window will
    // cause the next image in the list to be displayed, or will close the
    // window if the last image
    // is currently being displayed.
    public static void displayImagesFullScreen(List<Image> images, ImageSettings imageSettings, Window applicationWindow) {
        final Window w = new Window(applicationWindow);
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

        final GraphicsDevice gd = applicationWindow.getGraphicsConfiguration().getDevice();

        w.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (viewer.atLastImage()) {
                    w.setVisible(false);
                    w.dispose();
                    gd.setFullScreenWindow(null);
                } else {
                    viewer.nextImage();
                }
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
                    w.dispose();
                    gd.setFullScreenWindow(null);
                }
            }
        });

        gd.setFullScreenWindow(w);
    }

}
