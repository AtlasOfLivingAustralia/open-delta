package au.org.ala.delta.ui.image;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.application.Action;

import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.ui.image.ImagePanel.ScalingMode;
import java.awt.Color;

public class MultipleImageViewer extends JPanel {

    private static final long serialVersionUID = 6901754518169951771L;
    private int _selectedIndex;
    private CardLayout _layout;
    private ScalingMode _scalingMode;
    private List<ImageViewer> _imageViewers;
    private Map<String, ImageViewer> _imageViewerMap;
    private ImageSettings _imageSettings;
    private JPanel _contentPanel;
    private boolean _hideHotSpots;
    private boolean _hideTextOverlays;

    public MultipleImageViewer(ImageSettings imageSettings) {
        _imageSettings = imageSettings;
        _layout = new CardLayout();
        _contentPanel = new JPanel();
        _contentPanel.setLayout(_layout);
        this.setLayout(new BorderLayout());
        this.add(_contentPanel, BorderLayout.CENTER);
        _imageViewers = new ArrayList<ImageViewer>();
        _imageViewerMap = new HashMap<String, ImageViewer>();
       
        _selectedIndex = 0;
        _scalingMode = ScalingMode.FIXED_ASPECT_RATIO;
    }

    public void addImageViewer(ImageViewer viewer) {
        _imageViewers.add(viewer);
        String imageId = ImageUtils.getSubjectTextOrFileName(viewer.getViewedImage());
        _imageViewerMap.put(imageId, viewer);
        _contentPanel.add(viewer, imageId);
    }

    /**
     * Displays the next image of the current subject (Character or Item)
     */
    public void nextImage() {
        int nextIndex = _selectedIndex + 1;
        if (nextIndex < _imageViewers.size()) {
            _layout.next(_contentPanel);
            _selectedIndex = nextIndex;
        }
    }

    /**
     * Displays the previous image of the current subject (Character or Item)
     */
    public void previousImage() {
        int prevIndex = _selectedIndex - 1;
        if (prevIndex >= 0) {
            _layout.previous(_contentPanel);
            _selectedIndex = prevIndex;
        }
    }

    public void showImage(String imageId) {
        ImageViewer viewer = _imageViewerMap.get(imageId);
        _selectedIndex = _imageViewers.indexOf(viewer);
        _layout.show(_contentPanel, imageId);
    }

    public int getNumberImages() {
        return _imageViewers.size();
    }

    public int getIndexCurrentlyViewedImage() {
        return _selectedIndex;
    }

    public boolean atFirstImage() {
        return _selectedIndex == 0;
    }

    public boolean atLastImage() {
        return _selectedIndex == _imageViewers.size() - 1;
    }

    public void replaySound() {
        List<ImageOverlay> sounds = getVisibleViewer().getViewedImage().getSounds();
        for (ImageOverlay sound : sounds) {

            try {
                URL soundUrl = getVisibleViewer().getViewedImage().soundToURL(sound, _imageSettings.getImagePath());
                AudioPlayer.playClip(soundUrl);
            } catch (Exception e) {
                // TODO _messageHelper.errorPlayingSound(sound.overlayText);
                e.printStackTrace();
            }
        }
    }

    public void replayVideo() {
    }

    public void toggleScaling() {
        if (_scalingMode == ScalingMode.NO_SCALING) {
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
        if (_selectedIndex < _imageViewers.size()) {
            return _imageViewers.get(_selectedIndex);
        } else {
            return null;
        }
    }

}
