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
package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

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
import au.org.ala.delta.util.Pair;

public class ImageUtils {

    // Display the list of images one by one in a full screen window. Clicking
    // the window will
    // cause the next image in the list to be displayed, or will close the
    // window if the last image
    // is currently being displayed.
    public static void displayStartupScreen(final List<Image> images, ImageSettings imageSettings, Window applicationWindow) {

        final JDialog w = new JDialog(applicationWindow);
        w.setUndecorated(true);
        w.setLayout(new BorderLayout());

        final MultipleImageViewer viewer = new MultipleImageViewer(imageSettings);

        applicationWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new ImageLoadWorker(images, imageSettings, applicationWindow, viewer, w).execute();
    }

    /**
     * Loads images from their URLs on a background thread. Used because large
     * remotely hosted images lock up the UI if they are loaded on the event
     * dispatch thread.
     * 
     * @author ChrisF
     * 
     */
    private static class ImageLoadWorker extends SwingWorker<List<List<Object>>, Void> {

        private List<Image> _images;
        private ImageSettings _imageSettings;
        private Window _applicationWindow;
        private MultipleImageViewer _viewer;
        private JDialog _imageDisplayDialog;

        public ImageLoadWorker(List<Image> images, ImageSettings imageSettings, Window applicationWindow, MultipleImageViewer viewer, JDialog imageDisplayDialog) {
            _images = images;
            _imageSettings = imageSettings;
            _applicationWindow = applicationWindow;
            _imageDisplayDialog = imageDisplayDialog;
            _viewer = viewer;
        }

        @Override
        protected List<List<Object>> doInBackground() throws Exception {
            List<List<Object>> retList = new ArrayList<List<Object>>();
            for (Image img : _images) {
                List<Object> imageData = new ArrayList<Object>();
                URL imgURL = au.org.ala.delta.ui.util.UIUtils.findImageFile(img.getFileName(), _imageSettings);
                Pair<BufferedImage, String> imageAndType = au.org.ala.delta.ui.util.UIUtils.readImage(imgURL);

                imageData.add(img);
                imageData.add(imageAndType.getFirst());
                imageData.add(imageAndType.getSecond());
                imageData.add(imgURL);
                retList.add(imageData);
            }
            return retList;
        }

        @Override
        protected void done() {
            _applicationWindow.setCursor(Cursor.getDefaultCursor());
            try {
                OverlaySelectionObserver observer = new OverlaySelectionObserver() {
                    @Override
                    public void overlaySelected(SelectableOverlay overlay) {
                        ImageOverlay imageOverlay = overlay.getImageOverlay();
                        if (imageOverlay.isType(OverlayType.OLOK)) {
                            _imageDisplayDialog.setVisible(false);
                        } else if (imageOverlay.isType(OverlayType.OLCANCEL)) {
                            _imageDisplayDialog.setVisible(false);
                        } else if (imageOverlay.isType(OverlayType.OLIMAGENOTES)) {
                            String rtfContent = _viewer.getVisibleImage().getNotes();
                            String title = "Image Notes";
                            RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(_imageDisplayDialog, new SimpleRtfEditorKit(null), rtfContent, title);
                            ((SingleFrameApplication) Application.getInstance()).show(dlg);
                        }
                    }
                };

                _viewer.setObserver(observer);

                List<List<Object>> allImageDataList = get();

                for (List<Object> imageDataList : allImageDataList) {
                    _viewer.addImage(((Image) imageDataList.get(0)).getFileName(), (Image) imageDataList.get(0), (BufferedImage) imageDataList.get(1), (URL) imageDataList.get(3),
                            (String) imageDataList.get(2));
                }

                _viewer.setBackground(Color.BLACK);
                _viewer.showImage(_images.get(0).getFileName());

                GridBagLayout panelLayout = new GridBagLayout();

                JPanel panel = new JPanel(panelLayout);
                panel.setBackground(Color.BLACK);
                panel.add(_viewer);
                _imageDisplayDialog.add(panel, BorderLayout.CENTER);

                _imageDisplayDialog.addMouseListener(new MouseAdapter() {

                    private int _visibleImageIndex = 0;

                    public void mouseClicked(MouseEvent e) {
                        if (_visibleImageIndex == _images.size() - 1) {
                            _imageDisplayDialog.setVisible(false);
                        } else {
                            _visibleImageIndex++;
                            _viewer.showImage(_images.get(_visibleImageIndex).getFileName());
                        }
                    }
                });

                _imageDisplayDialog.addKeyListener(new KeyListener() {

                    @Override
                    public void keyTyped(KeyEvent e) {
                        _imageDisplayDialog.setVisible(false);
                        _imageDisplayDialog.dispose();
                    }

                    @Override
                    public void keyReleased(KeyEvent e) {
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {
                    }
                });

                _imageDisplayDialog.setFocusable(true);

                Rectangle r = _applicationWindow.getGraphicsConfiguration().getBounds();
                _imageDisplayDialog.setLocation(r.x, r.y);

                _imageDisplayDialog.setSize(new Dimension(r.width, r.height));
                _imageDisplayDialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(_applicationWindow, "Error occurred loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

}
