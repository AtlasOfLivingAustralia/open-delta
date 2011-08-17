package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.ui.image.AboutImageDialog;
import au.org.ala.delta.ui.image.ImagePanel.ScalingMode;
import au.org.ala.delta.ui.image.ImageUtils;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;
import au.org.ala.delta.util.Pair;

public class ImageDialog extends JDialog implements OverlaySelectionObserver {

    protected ImageSettings _imageSettings;
    protected MultipleImageViewer _multipleImageViewer;
    protected JMenuBar _menuBar;
    protected JMenu _mnuControl;
    private JMenu _mnuSubject;
    private JMenu _mnuWindow;
    private JCheckBoxMenuItem _mnuItScaled;
    private JCheckBoxMenuItem _mnuItHideText;
    private JMenuItem _mnuItReplaySound;
    private JMenuItem _mnuItFitToImage;
    private JMenuItem _mnuItFullScreen;
    private JMenuItem _mnuItCascade;
    private JMenuItem _mnuItTile;
    private JMenuItem _mnuItCloseAll;
    private JMenuItem _mnuItAboutImage;
    private JMenuItem _mnuItReplayVideo;

    private Set<Integer> _selectedStates;
    private Set<String> _selectedKeywords;
    private Set<Pair<String, String>> _selectedValues;
    private boolean _okButtonPressed;
    private JMenuItem _mnuItNextImage;
    private JMenuItem _mnuItPreviousImage;

    private Window _fullScreenWindow;

    /**
     * @wbp.parser.constructor
     */
    public ImageDialog(Frame owner, ImageSettings imageSettings) {
        super(owner, true);
        init(imageSettings);
        setLocationRelativeTo(owner);
    }

    public ImageDialog(Dialog owner, ImageSettings imageSettings) {
        super(owner, true);
        init(imageSettings);
        setLocationRelativeTo(owner);
    }

    private void init(ImageSettings imageSettings) {
        _imageSettings = imageSettings;

        _okButtonPressed = false;

        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();

        buildMenu();
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

        _multipleImageViewer = new MultipleImageViewer(_imageSettings);
        getContentPane().add(_multipleImageViewer);

        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
                fitToImage();
                replaySound();
            }

        });

    }

    private void buildMenu() {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(ImageDialog.class);
        resourceMap.injectFields(this);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(ImageDialog.class, this);

        _menuBar = new JMenuBar();
        setJMenuBar(_menuBar);

        _mnuSubject = new JMenu(resourceMap.getString("ImageDialog.mnuSubjectCaption"));
        _menuBar.add(_mnuSubject);

        _mnuControl = new JMenu(resourceMap.getString("ImageDialog.mnuControlCaption"));
        _menuBar.add(_mnuControl);

        _mnuItNextImage = new JMenuItem();
        _mnuItNextImage.setAction(actionMap.get("nextImage"));
        _mnuControl.add(_mnuItNextImage);

        _mnuItPreviousImage = new JMenuItem();
        _mnuItPreviousImage.setAction(actionMap.get("previousImage"));
        _mnuControl.add(_mnuItPreviousImage);

        _mnuWindow = new JMenu(resourceMap.getString("ImageDialog.mnuSubjectCaption"));
        _menuBar.add(_mnuWindow);

        _mnuItScaled = new JCheckBoxMenuItem();
        _mnuItScaled.setAction(actionMap.get("toggleScaling"));
        _mnuItScaled.getAction().putValue(javax.swing.Action.SELECTED_KEY, true);
        _mnuWindow.add(_mnuItScaled);

        _mnuItHideText = new JCheckBoxMenuItem();
        _mnuItHideText.setAction(actionMap.get("toggleHideText"));
        _mnuItHideText.getAction().putValue(javax.swing.Action.SELECTED_KEY, false);
        _mnuWindow.add(_mnuItHideText);

        _mnuItReplaySound = new JMenuItem();
        _mnuItReplaySound.setAction(actionMap.get("replaySound"));
        _mnuWindow.add(_mnuItReplaySound);

        _mnuItReplayVideo = new JMenuItem();
        _mnuItReplayVideo.setAction(actionMap.get("replayVideo"));
        _mnuItReplayVideo.setEnabled(false);
        _mnuWindow.add(_mnuItReplayVideo);

        _mnuWindow.addSeparator();

        _mnuItFitToImage = new JMenuItem();
        _mnuItFitToImage.setAction(actionMap.get("fitToImage"));
        _mnuWindow.add(_mnuItFitToImage);

        _mnuItFullScreen = new JMenuItem();
        _mnuItFullScreen.setAction(actionMap.get("fullScreen"));
        _mnuWindow.add(_mnuItFullScreen);

        _mnuWindow.addSeparator();

        _mnuItCascade = new JMenuItem();
        _mnuItCascade.setAction(actionMap.get("cascade"));
        _mnuWindow.add(_mnuItCascade);

        _mnuItTile = new JMenuItem();
        _mnuItTile.setAction(actionMap.get("tile"));
        _mnuWindow.add(_mnuItTile);

        _mnuItCloseAll = new JMenuItem();
        _mnuItCloseAll.setAction(actionMap.get("closeAll"));
        _mnuWindow.add(_mnuItCloseAll);

        _mnuWindow.addSeparator();

        _mnuItAboutImage = new JMenuItem();
        _mnuItAboutImage.setAction(actionMap.get("aboutImage"));
        _mnuWindow.add(_mnuItAboutImage);
    }

    public void setImages(List<Image> images) {

        List<String> imageNames = new ArrayList<String>();
        for (Image image : images) {
            ImageViewer viewer = new ImageViewer(image, _imageSettings);
            _multipleImageViewer.addImageViewer(viewer);
            viewer.addOverlaySelectionObserver(this);

            String imageName = image.getSubjectTextOrFileName();
            imageNames.add(imageName);
            populateSubjectMenu(imageNames);
        }

        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();

        _mnuItNextImage.setEnabled(images.size() > 1);
        _mnuItPreviousImage.setEnabled(false);

        this.pack();
    }

    private void populateSubjectMenu(List<String> imageNames) {
        _mnuSubject.removeAll();

        ButtonGroup group = new ButtonGroup();

        for (final String imageName : imageNames) {
            final JRadioButtonMenuItem rdBtnMnuIt = new JRadioButtonMenuItem(imageName);
            rdBtnMnuIt.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    _multipleImageViewer.showImage(imageName);
                    handleNewImageSelected();
                }
            });

            group.add(rdBtnMnuIt);
            _mnuSubject.add(rdBtnMnuIt);
        }

        ((JRadioButtonMenuItem) _mnuSubject.getMenuComponent(0)).setSelected(true);
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
    public void nextImage() {
        _multipleImageViewer.nextImage();
        handleNewImageSelected();
    }

    @Action
    public void previousImage() {
        _multipleImageViewer.previousImage();
        handleNewImageSelected();
    }

    @Action
    public void toggleScaling() {
        _multipleImageViewer.toggleScaling();
    }

    @Action
    public void toggleHideText() {
        _multipleImageViewer.toggleHideText();
    }

    @Action
    public void replaySound() {
        _multipleImageViewer.replaySound();
    }

    @Action
    public void replayVideo() {
        _multipleImageViewer.replayVideo();
    }

    @Action
    public void fitToImage() {
        _multipleImageViewer.fitToImage();
    }

    @Action
    public void fullScreen() {

        // Image image =
        // _multipleImageViewer.getVisibleViewer().getViewedImage();
        // ImageViewer copyViewer = new ImageViewer(image, _imageSettings);
        // copyViewer.setScalingMode(ScalingMode.NO_SCALING);
        // copyViewer.addOverlaySelectionObserver(this);
        //
        // final Window w = new Window(this);
        // w.setLayout(new BorderLayout());
        // w.add(copyViewer, BorderLayout.CENTER);
        //
        // final GraphicsDevice gd =
        // this.getGraphicsConfiguration().getDevice();
        //
        // copyViewer.addOverlaySelectionObserver(new OverlaySelectionObserver()
        // {
        //
        // @Override
        // public void overlaySelected(SelectableOverlay overlay) {
        // ImageOverlay imageOverlay = overlay.getImageOverlay();
        // if (imageOverlay.isType(OverlayType.OLOK) ||
        // imageOverlay.isType(OverlayType.OLCANCEL)) {
        // w.setVisible(false);
        // w.dispose();
        // gd.setFullScreenWindow(null);
        // }
        // }
        // });
        //
        // gd.setFullScreenWindow(w);

        Image image = _multipleImageViewer.getVisibleViewer().getViewedImage();
        List<Image> images = new ArrayList<Image>();
        images.add(image);
        Window applicationWindow = ((SingleFrameApplication) Application.getInstance()).getMainFrame();
        this.setVisible(false);
        ImageUtils.displayImagesFullScreen(images, _imageSettings, applicationWindow);
    }

    @Action
    public void cascade() {

    }

    @Action
    public void tile() {

    }

    @Action
    public void closeAll() {

    }

    @Action
    public void aboutImage() {
        ImageViewer visibleViewer = _multipleImageViewer.getVisibleViewer();
        AboutImageDialog dlg = new AboutImageDialog(this, visibleViewer.getViewedImage().getSubjectTextOrFileName(), visibleViewer.getImageFileLocation(), visibleViewer.getImage(),
                visibleViewer.getImageFormatName());
        dlg.setVisible(true);
    }

    private void handleNewImageSelected() {
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
        _mnuItNextImage.setEnabled(!_multipleImageViewer.atLastImage());
        _mnuItPreviousImage.setEnabled(!_multipleImageViewer.atFirstImage());
        
        int viewedIndex = _multipleImageViewer.getIndexCurrentlyViewedImage();
        JMenuItem mnuIt = (JMenuItem) _mnuSubject.getMenuComponent(viewedIndex);
        mnuIt.setSelected(true);
        
        fitToImage();
        replaySound();
    }

    private void reSelectStatesInNewViewer(ImageViewer viewer) {
        List<ImageOverlay> overlays = viewer.getOverlays();
        for (ImageOverlay overlay : overlays) {
            if (overlay.isType(OverlayType.OLSTATE)) {
                int stateId = overlay.stateId;

                SelectableTextOverlay selectableText = viewer.getSelectableTextForOverlay(overlay);
                if (selectableText != null) {
                    selectableText.setSelected(_selectedStates.contains(stateId));
                }

                HotSpotGroup hotSpotGroup = viewer.getHotSpotGroupForOverlay(overlay);
                if (hotSpotGroup != null) {
                    hotSpotGroup.setSelected(_selectedStates.contains(stateId));
                }
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
