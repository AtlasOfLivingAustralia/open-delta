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
package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.image.ImagePanel.ScalingMode;

public class MultipleImageViewer extends JPanel {

    private static final long serialVersionUID = 6901754518169951771L;
    private ScalingMode _scalingMode;
    private List<ImageViewer> _imageViewers;
    private Map<String, ImageViewer> _idToViewerMap;
    private Map<String, Image> _idToImageMap;
    private ImageSettings _imageSettings;
    private JPanel _contentPanel;
    private boolean _hideHotSpots;
    private boolean _hideTextOverlays;

    private OverlaySelectionObserver _observer;

    /**
     * The image viewer that is currently visible
     */
    private ImageViewer _visibleViewer;

    /**
     * The image viewer that was previously visible
     */
    private ImageViewer _previouslyVisibleViewer;

    private Image _visibleImage;

    public MultipleImageViewer(ImageSettings imageSettings) {
        _imageSettings = imageSettings;
        _contentPanel = new JPanel();
        _contentPanel.setLayout(new BorderLayout());
        this.setLayout(new BorderLayout());
        this.add(_contentPanel, BorderLayout.CENTER);
        _imageViewers = new ArrayList<ImageViewer>();
        _idToViewerMap = new HashMap<String, ImageViewer>();
        _idToImageMap = new HashMap<String, Image>();

        _scalingMode = ScalingMode.FIXED_ASPECT_RATIO;
    }

    /**
     * Set the observer that will be added to each instance of ImageViewer that
     * is created by this MultipleImageViewer. This must be set before any
     * images are shown by the MultipleImageViewer.
     * 
     * @param observer
     */
    public void setObserver(OverlaySelectionObserver observer) {
        _observer = observer;
    }

    public void addImage(String imageId, Image image, BufferedImage bufferedImage, URL imageFileLocation, String imageType) {
        ImageViewer viewer = new ImageViewer(image, _imageSettings, bufferedImage, imageFileLocation, imageType);
        viewer.setScalingMode(_scalingMode);
        if (_observer != null) {
            viewer.addOverlaySelectionObserver(_observer);
        }

        _idToViewerMap.put(imageId, viewer);
        _idToImageMap.put(imageId, image);

        _imageViewers.add(viewer);
    }

    public void showImage(String imageId) {
        if (!_idToViewerMap.containsKey(imageId)) {
            throw new IllegalArgumentException("Image " + imageId + " not present in MultipleImageViewer");
        }

        if (_visibleViewer != null) {
            _previouslyVisibleViewer = _visibleViewer;
        }

        _visibleViewer = _idToViewerMap.get(imageId);
        _visibleImage = _idToImageMap.get(imageId);

        _contentPanel.removeAll();
        _contentPanel.add(_visibleViewer, BorderLayout.CENTER);
        revalidate();
    }

    public boolean hasImage(String imageId) {
        return _idToViewerMap.containsKey(imageId);
    }

    public void replaySound() throws Exception {
        List<ImageOverlay> sounds = getVisibleImage().getSounds();
        for (ImageOverlay sound : sounds) {
            URL soundUrl = _imageSettings.findFileOnResourcePath(sound.overlayText, false);
            AudioPlayer.playClip(soundUrl);
        }
    }

    public void replayVideo() {
    }

    public void setScaleImages(boolean scale) {
        if (scale) {
            setScalingMode(ScalingMode.FIXED_ASPECT_RATIO);
        } else {
            setScalingMode(ScalingMode.NO_SCALING);
        }
    }

    private void setScalingMode(ScalingMode mode) {
        if (mode == ScalingMode.NO_SCALING) {
            this.remove(_contentPanel);
            this.add(new JScrollPane(_contentPanel), BorderLayout.CENTER);
        } else if (_scalingMode == ScalingMode.NO_SCALING) {
            this.removeAll();
            this.add(_contentPanel, BorderLayout.CENTER);
        }
        _scalingMode = mode;
        for (ImageViewer viewer : _imageViewers) {
            viewer.setScalingMode(mode);
        }
        revalidate();
    }

    /**
     * Resizes this JInternalFrame so that the image is displayed at it's
     * natural size.
     */
    @Action
    public void fitToImage() {
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        parentWindow.pack();
    }

    public void toggleHideHotSpots() {
        setHideHotSpots(!_hideHotSpots);
    }

    public void setHideHotSpots(boolean hideHotSpots) {
        if (_hideHotSpots != hideHotSpots) {
            _hideHotSpots = hideHotSpots;
            for (ImageViewer viewer : _imageViewers) {
                viewer.setDisplayHotSpots(!hideHotSpots);
            }
        }
    }

    public void toggleHideText() {
        setHideTextOverlays(!_hideTextOverlays);
    }

    private void setHideTextOverlays(boolean hideTextOverlays) {
        if (_hideTextOverlays != hideTextOverlays) {
            _hideTextOverlays = hideTextOverlays;
            for (ImageViewer viewer : _imageViewers) {
                viewer.setDisplayTextOverlays(!hideTextOverlays);
            }
        }
    }

    public ImageViewer getVisibleViewer() {
        return _visibleViewer;
    }

    public ImageViewer getPreviouslyVisibleViewer() {
        return _previouslyVisibleViewer;
    }

    public Image getVisibleImage() {
        return _visibleImage;
    }

    public List<ImageViewer> getViewersList() {
        return _imageViewers;
    }

}
