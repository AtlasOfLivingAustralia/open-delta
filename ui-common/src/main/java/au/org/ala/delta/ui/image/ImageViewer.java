package au.org.ala.delta.ui.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.overlay.HotSpot;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.ui.image.overlay.OverlayButton;
import au.org.ala.delta.ui.image.overlay.OverlayLocation;
import au.org.ala.delta.ui.image.overlay.OverlayLocationProvider;
import au.org.ala.delta.ui.image.overlay.RelativePositionedTextOverlay;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;
import au.org.ala.delta.ui.image.overlay.TextFieldOverlay;

/**
 * Displays a single DELTA Image.
 */
public class ImageViewer extends ImagePanel implements LayoutManager2, ActionListener, OverlaySelectionObserver {

    protected static final String IMAGE_OVERLAY_PROPERTY = "ImageOverlay";

    private static final long serialVersionUID = -6735023009826819178L;

    /** The Image we are displaying */
    protected Image _image;

    /** Creates image overlays */
    private OverlayComponentFactory _factory;

    protected List<ImageOverlay> _overlays;

    protected List<JComponent> _components;

    private List<OverlaySelectionObserver> _observers;

    /** Kept for convenience when toggling the display of hotspots */
    private Map<ImageOverlay, HotSpotGroup> _hotSpotGroups;

    private Map<ImageOverlay, SelectableTextOverlay> _selectableTextOverlays;

    private Map<ImageOverlay, OverlayButton> _overlayButtons;

    private TextFieldOverlay _inputField;

    /**
     * Creates a new ImageViewer for the supplied Image.
     * 
     * @param image
     *            the image to view.
     * @param imageSettings
     *            application-wide settings for the display of images
     */
    public ImageViewer(Image image, ImageSettings imageSettings) {
        _image = image;

        this.setBackground(Color.BLACK);

        ResourceMap resources = Application.getInstance().getContext().getResourceMap();

        _factory = new OverlayComponentFactory(resources, imageSettings);
        setLayout(this);

        URL imageLocation = findImageFile(image.getFileName(), imageSettings);

        displayImage(imageLocation);
        _components = new ArrayList<JComponent>();
        _observers = new ArrayList<OverlaySelectionObserver>();
        addOverlays();
    }

    protected URL findImageFile(String fileName, ImageSettings imageSettings) {
        URL imageLocation = imageSettings.findFileOnResourcePath(fileName);

        if (imageLocation == null) {
            throw new IllegalArgumentException("Could not open image file " + fileName);
        }

        return imageLocation;
    }

    public void addOverlays() {
        _components.clear();
        removeAll();
        _overlays = _image.getOverlays();

        _hotSpotGroups = new HashMap<ImageOverlay, HotSpotGroup>();
        _selectableTextOverlays = new HashMap<ImageOverlay, SelectableTextOverlay>();
        _overlayButtons = new HashMap<ImageOverlay, OverlayButton>();

        int maxButtonHeight = 0;
        int maxButtonWidth = 0;

        for (ImageOverlay overlay : _overlays) {
            JComponent overlayComp = _factory.createOverlayComponent(overlay, _image);

            if (overlayComp == null) {
                continue;
            }
            overlayComp.putClientProperty(IMAGE_OVERLAY_PROPERTY, overlay);
            add(overlayComp, overlay.getLocation(0));

            if (overlayComp instanceof OverlayButton) {
                OverlayButton button = (OverlayButton) overlayComp;
                _overlayButtons.put(overlay, button);
                button.addActionListener(this);

                // Determine a maximum width and height to set as the preferred
                // size
                // on all buttons.
                Dimension buttonPreferredSize = button.getPreferredSize();

                if (maxButtonHeight < buttonPreferredSize.height) {
                    maxButtonHeight = buttonPreferredSize.height;
                }

                if (maxButtonWidth < buttonPreferredSize.width) {
                    maxButtonWidth = buttonPreferredSize.width;
                }
            }

            if (overlayComp instanceof SelectableTextOverlay) {
                SelectableTextOverlay selectable = (SelectableTextOverlay) overlayComp;
                selectable.addOverlaySelectionObserver(this);
                // If the overlay has associated hotspots, add them also.
                addHotSpots(overlay, selectable);
                _selectableTextOverlays.put(overlay, selectable);
            }

            if (overlayComp instanceof TextFieldOverlay) {
                _inputField = (TextFieldOverlay) overlayComp;
            }
        }

        Dimension buttonPreferredSize = new Dimension(maxButtonWidth, maxButtonHeight);
        for (OverlayButton button : _overlayButtons.values()) {
            button.setPreferredSize(buttonPreferredSize);
        }

        assignRelativeComponents();
        addMouseListeners();
    }

    protected void addMouseListeners() {
        // Need to display the hand cursor when mousing over a selectable
        // overlay or hot spot
        for (final SelectableTextOverlay selectable : _selectableTextOverlays.values()) {
            selectable.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseExited(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                }
            });
        }

        for (HotSpotGroup hotSpotGroup : _hotSpotGroups.values()) {
            for (HotSpot hotSpot : hotSpotGroup.getHotSpots()) {
                hotSpot.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseExited(MouseEvent e) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    }
                });
            }
        }

    }

    /**
     * The Units overlay component is by default positioned relative to the
     * Enter overlay component. To make the layout work, the relatively
     * positioned components need access to the position of the Enter component
     * but since the order the layouts are created in is arbitrary it's easier
     * to do the assignment after they are all created.
     */
    private void assignRelativeComponents() {
        OverlayLocationProvider parent = null;
        // Assign the parent to the relative overlay components
        for (JComponent overlayComp : _components) {
            ImageOverlay overlay = (ImageOverlay) overlayComp.getClientProperty(IMAGE_OVERLAY_PROPERTY);
            if (overlay != null && overlay.isType(OverlayType.OLENTER)) {
                parent = (OverlayLocationProvider) overlayComp;
                break;
            }
        }

        for (JComponent overlayComp : _components) {
            if (overlayComp instanceof RelativePositionedTextOverlay) {
                ((RelativePositionedTextOverlay) overlayComp).makeRelativeTo(parent);
            }
        }
    }

    private void addHotSpots(ImageOverlay overlay, SelectableTextOverlay selectable) {
        int hotSpotCount = overlay.getNHotSpots();
        if (hotSpotCount > 0) {
            HotSpotGroup group = new HotSpotGroup(selectable);
            group.addOverlaySelectionObserver(this);
            _hotSpotGroups.put(overlay, group);

            for (int i = 1; i <= hotSpotCount; i++) {
                overlay.getLocation(i);
                HotSpot hotSpot = _factory.createHotSpot(overlay, i);
                hotSpot.putClientProperty(IMAGE_OVERLAY_PROPERTY, overlay);
                group.add(hotSpot);
                add(hotSpot, overlay.getLocation(i));
            }

            selectable.setHotspotGroup(group);
        }
    }

    public void setDisplayHotSpots(boolean displayHotSpots) {
        for (HotSpotGroup group : _hotSpotGroups.values()) {
            group.setDisplayHotSpots(displayHotSpots);
        }
        repaint();
    }

    public void setDisplayTextOverlays(boolean displayText) {
        for (JComponent overlayComp : _components) {
            if (isTextOverlay(overlayComp)) {
                overlayComp.setVisible(displayText);
            }
        }
    }

    private boolean isTextOverlay(JComponent overlayComp) {
        boolean isText = false;
        ImageOverlay overlay = (ImageOverlay) overlayComp.getClientProperty(IMAGE_OVERLAY_PROPERTY);
        if (overlay != null) {
            isText = OverlayType.isTextOverlay(overlay);
        }
        return isText;
    }

    /**
     * Lays out the Image overlays in this container.
     */
    protected void layoutOverlays() {
        for (JComponent overlayComp : _components) {

            OverlayLocationProvider locationProvider = (OverlayLocationProvider) overlayComp;

            OverlayLocation location = locationProvider.location(this);

            Rectangle bounds = new Rectangle(location.getX(), location.getY(), location.getWidth(), location.getHeight());
            overlayComp.setBounds(bounds);
        }
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints == null) {
            throw new IllegalArgumentException("Cannot use null constraints");
        }
        _components.add((JComponent) comp);
    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(2000, 2000);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {
        _components.remove((JComponent) comp);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Dimension d = new Dimension();
        d.width = getPreferredImageWidth();
        d.height = getPreferredImageHeight();

        return d;
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return new Dimension(100, 100);
    }

    @Override
    public void layoutContainer(Container parent) {
        layoutOverlays();
    }

    protected void fireOverlaySelected(SelectableOverlay overlay) {
        for (int i = _observers.size() - 1; i >= 0; i--) {
            _observers.get(i).overlaySelected(overlay);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SelectableOverlay comp = (SelectableOverlay) e.getSource();
        fireOverlaySelected(comp);
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        fireOverlaySelected(overlay);
    }

    public void addOverlaySelectionObserver(OverlaySelectionObserver observer) {
        _observers.add(observer);
    }

    public void removeOverlaySelectionObserver(OverlaySelectionObserver observer) {
        _observers.remove(observer);
    }

    public Image getViewedImage() {
        return _image;
    }

    public List<ImageOverlay> getOverlays() {
        // defensive copy
        return new ArrayList<ImageOverlay>(_overlays);
    }

    public HotSpotGroup getHotSpotGroupForOverlay(ImageOverlay overlay) {
        return _hotSpotGroups.get(overlay);
    }

    public SelectableTextOverlay getSelectableTextForOverlay(ImageOverlay overlay) {
        return _selectableTextOverlays.get(overlay);
    }

    public String getInputText() {
        if (_inputField != null) {
            return _inputField.getText();
        } else {
            return null;
        }
    }

}
