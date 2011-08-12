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

    /**
     * @param image
     *            the image to get the text for.
     * @return the subject text of an image, or the filename if none has been
     *         specified.
     */
    public static String getSubjectTextOrFileName(Image image) {
        String text = image.getSubjectText();
        if (StringUtils.isEmpty(text)) {
            text = image.getFileName();
        }
        return text;
    }

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
    
    public static Pair<BufferedImage, String> read(URL imageFileLocation) throws Exception {
        InputStream inputStream = imageFileLocation.openStream();
        String imageType;
       
        ImageInputStream stream = ImageIO.createImageInputStream(inputStream);
        BufferedImage image;
        try {
            Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
            if (!iter.hasNext()) {
                return null;
            }

            ImageReader reader = (ImageReader)iter.next();
            ImageReadParam param = reader.getDefaultReadParam();
            reader.setInput(stream, true, true);
            imageType = reader.getFormatName();
            try {
                image = reader.read(0, param);
            } finally {
                reader.dispose();
                stream.close();
            }
           
            if (image == null) {
                stream.close();
            }
        } finally {
            inputStream.close();
        }
        
        Pair<BufferedImage, String> pair = new Pair<BufferedImage, String>(image, imageType);
        return pair;
    }

}
