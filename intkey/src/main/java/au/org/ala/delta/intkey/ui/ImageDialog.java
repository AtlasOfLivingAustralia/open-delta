package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.util.Pair;

public class ImageDialog extends JDialog implements OverlaySelectionObserver {

    protected ImageSettings _imageSettings;
    protected MultipleImageViewer _multipleImageViewer;
    protected JMenuBar _menuBar;
    protected JMenu _mnControl;
    private JMenu _mnSubject;
    private JMenu mnWindow;
    private JMenuItem mntmScaled;
    private JMenuItem mntmHideText;
    private JMenuItem mntmReplaySound;
    private JMenuItem mntmFitToImage;
    private JMenuItem mntmFullScreen;
    private JMenuItem mntmCascade;
    private JMenuItem mntmTile;
    private JMenuItem mntmCloseAll;
    private JMenuItem mntmAboutImage;
    private JMenuItem mntmReplayVideo;

    private Set<Integer> _selectedStates;
    private Set<String> _selectedKeywords;
    private Set<Pair<String, String>> _selectedValues;
    private boolean _okButtonPressed;

    /**
     * @wbp.parser.constructor
     */
    public ImageDialog(Frame owner, ImageSettings imageSettings) {
        super(owner, true);
        init(imageSettings);
    }

    public ImageDialog(Dialog owner, ImageSettings imageSettings) {
        super(owner, true);
        init(imageSettings);
    }

    private void init(ImageSettings imageSettings) {
        _imageSettings = imageSettings;
        
        _okButtonPressed = false;
        
        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();
        
        getContentPane().setLayout(new BorderLayout(0, 0));

        buildMenu();

        _multipleImageViewer = new MultipleImageViewer(_imageSettings);
        getContentPane().add(_multipleImageViewer, BorderLayout.CENTER);

        this.pack();
    }

    private void buildMenu() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

        _menuBar = new JMenuBar();
        setJMenuBar(_menuBar);

        _mnSubject = new JMenu("Subject");
        _menuBar.add(_mnSubject);

        _mnControl = new JMenu("Control");
        _menuBar.add(_mnControl);

        JMenuItem mnuItNextImage = new JMenuItem();
        mnuItNextImage.setAction(actionMap.get("ImageDialog_mnuItNextImage"));
        _mnControl.add(mnuItNextImage);

        JMenuItem mnuItPreviousImage = new JMenuItem();
        mnuItPreviousImage.setAction(actionMap.get("ImageDialog_mnuItPreviousImage"));
        _mnControl.add(mnuItPreviousImage);

        mnWindow = new JMenu("Window");
        _menuBar.add(mnWindow);

        mntmScaled = new JMenuItem("Scaled");
        mnWindow.add(mntmScaled);

        mntmHideText = new JMenuItem("Hide text");
        mnWindow.add(mntmHideText);

        mntmReplaySound = new JMenuItem("Replay Sound");
        mnWindow.add(mntmReplaySound);

        mntmReplayVideo = new JMenuItem("Replay Video");
        mnWindow.add(mntmReplayVideo);

        mnWindow.addSeparator();

        mntmFitToImage = new JMenuItem("Fit to image");
        mnWindow.add(mntmFitToImage);

        mntmFullScreen = new JMenuItem("Full Screen");
        mnWindow.add(mntmFullScreen);

        mnWindow.addSeparator();

        mntmCascade = new JMenuItem("Cascade");
        mnWindow.add(mntmCascade);

        mntmTile = new JMenuItem("Tile");
        mnWindow.add(mntmTile);

        mntmCloseAll = new JMenuItem("Close All");
        mnWindow.add(mntmCloseAll);

        mnWindow.addSeparator();

        mntmAboutImage = new JMenuItem("About Image...");
        mnWindow.add(mntmAboutImage);
    }
    
    protected void setImages(List<Image> images) {
        for (Image image : images) {
            ImageViewer viewer = new ImageViewer(image, _imageSettings);
            _multipleImageViewer.addImageViewer(viewer);
            viewer.addOverlaySelectionObserver(this);
        }
        
        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();
        
        this.pack();
    }
    
    public void setFullScreen() {
        _multipleImageViewer.fullScreen();
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        overlay.setSelected(!overlay.isSelected());
        ImageOverlay imageOverlay = overlay.getImageOverlay();
        if (imageOverlay.isType(OverlayType.OLOK)) {
            _okButtonPressed = true;
            this.setVisible(false);
        } else if (imageOverlay.isType(OverlayType.OLCANCEL)) {
            _okButtonPressed = false;
            this.setVisible(false);
        } else if (imageOverlay.isType(OverlayType.OLNOTES)) {
            // showCharacterNotes();
        } else if (imageOverlay.isType(OverlayType.OLIMAGENOTES)) {
            // showImageNotes();
        } else if (imageOverlay.isType(OverlayType.OLSTATE)) {
            int stateId = imageOverlay.stateId;
            if (overlay.isSelected()) {
                _selectedStates.add(stateId);
            } else {
                _selectedStates.remove(stateId);
            }
        } else if (imageOverlay.isType(OverlayType.OLKEYWORD)) {
            String keywords = imageOverlay.keywords;
            for (String keyword : keywords.split(" ")) {
                _selectedKeywords.add(keyword);
            }
        } else if (imageOverlay.isType(OverlayType.OLVALUE)) {
            String minimumValString = imageOverlay.minVal;
            String maximumValString = imageOverlay.maxVal;
            _selectedValues.add(new Pair<String, String>(minimumValString, maximumValString));
        }
    }

    @Action
    public void ImageDialog_mnuItNextImage() {
        _multipleImageViewer.nextImage();
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
    }

    @Action
    public void ImageDialog_mnuItPreviousImage() {
        _multipleImageViewer.previousImage();
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
    }

    private void reSelectStatesInNewViewer(ImageViewer viewer) {
        List<ImageOverlay> overlays = viewer.getOverlays();
        for (ImageOverlay overlay : overlays) {
            if (overlay.isType(OverlayType.OLSTATE)) {
                int stateId = overlay.stateId;

                HotSpotGroup hotSpotGroup = viewer.getHotSpotGroupForOverlay(overlay);
                hotSpotGroup.setSelected(_selectedStates.contains(stateId));
            }
        }
    }

    public Set<Integer> getSelectedStates() {
        return _selectedStates;
    }

    public Set<String> getSelectedKeywords() {
        return _selectedKeywords;
    }

    public Set<Integer> getInputIntegerValues() {
        Set<Integer> retSet = null;

        String inputText = _multipleImageViewer.getVisibleViewer().getInputText();
        if (!StringUtils.isEmpty(inputText)) {
            // Use value from input field
            retSet = ParsingUtils.parseMultistateOrIntegerCharacterValue(inputText);
        } else {
            // Use values from selected value fields
            retSet = new HashSet<Integer>();

            for (Pair<String, String> selectedValue : _selectedValues) {
                int minVal = Integer.parseInt(selectedValue.getFirst());

                // Second value in the pair will be null if the value field
                // represents a
                // single real value rather than a range.
                if (selectedValue.getSecond() != null) {
                    int maxVal = Integer.parseInt(selectedValue.getSecond());

                    IntRange intRange = new IntRange(minVal, maxVal);
                    for (int i : intRange.toArray()) {
                        retSet.add(i);
                    }
                } else {
                    retSet.add(minVal);
                }
            }
        }

        return retSet;
    }

    public FloatRange getInputRealValues() {
        FloatRange retRange = null;

        String inputText = _multipleImageViewer.getVisibleViewer().getInputText();

        if (!StringUtils.isEmpty(inputText)) {
            // Use value supplied in input field
            retRange = ParsingUtils.parseRealCharacterValue(inputText);
        } else {
            // Use values for selected value fields
            if (!_selectedValues.isEmpty()) {
                Set<Float> boundsSet = new HashSet<Float>();
                for (Pair<String, String> selectedValue : _selectedValues) {
                    float minVal = Float.parseFloat(selectedValue.getFirst());
                    boundsSet.add(minVal);

                    // Second value in the pair will be null if the value field
                    // represents a
                    // single real value rather than a range.
                    if (selectedValue.getSecond() != null) {
                        float maxVal = Float.parseFloat(selectedValue.getSecond());
                        boundsSet.add(maxVal);
                    }
                }

                float overallMin = Collections.min(boundsSet);
                float overallMax = Collections.max(boundsSet);
                retRange = new FloatRange(overallMin, overallMax);
            }
        }

        return retRange;
    }

    public List<String> getInputTextValues() {
        String inputText = _multipleImageViewer.getVisibleViewer().getInputText();
        return ParsingUtils.parseTextCharacterValue(inputText);
    }
    
    /**
     * @return was the dialog closed using the ok button?
     */
    public boolean okButtonPressed() {
        return _okButtonPressed;
    }
}
