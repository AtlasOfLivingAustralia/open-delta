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
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.net.URL;
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
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.Intkey;
import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.model.format.Formatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.ui.image.AboutImageDialog;
import au.org.ala.delta.ui.image.ImagePanel.ScalingMode;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.MultipleImageViewer;
import au.org.ala.delta.ui.image.OverlaySelectionObserver;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;
import au.org.ala.delta.ui.rtf.SimpleRtfEditorKit;
import au.org.ala.delta.ui.util.UIUtils;
import au.org.ala.delta.util.Pair;

public class ImageDialog extends IntkeyDialog implements OverlaySelectionObserver {

    /**
     * 
     */
    private static final long serialVersionUID = -8012068171243224330L;

    protected ImageSettings _imageSettings;
    protected MultipleImageViewer _multipleImageViewer;
    protected JMenuBar _menuBar;
    protected JMenu _mnuControl;
    protected JMenu _mnuSubject;
    protected JMenu _mnuWindow;
    protected JCheckBoxMenuItem _mnuItScaled;
    protected JCheckBoxMenuItem _mnuItHideText;
    protected JMenuItem _mnuItReplaySound;
    protected JMenuItem _mnuItFitToImage;
    protected JMenuItem _mnuItFullScreen;
    protected JMenuItem _mnuItCascade;
    protected JMenuItem _mnuItTile;
    protected JMenuItem _mnuItCloseAll;
    protected JMenuItem _mnuItAboutImage;
    protected JMenuItem _mnuItReplayVideo;

    protected Set<Integer> _selectedStates;
    protected Set<String> _selectedKeywords;
    protected Set<Pair<String, String>> _selectedValues;
    protected boolean _okButtonPressed;
    protected JMenuItem _mnuItNextImage;
    protected JMenuItem _mnuItPreviousImage;

    protected Window _fullScreenWindow;

    protected Formatter _imageDescriptionFormatter;

    protected List<Image> _images;

    protected boolean _scaleImages;

    /**
     * ctor
     * 
     * @param owner
     *            parent window
     * @param imageSettings
     *            Intkey image settings - contains lookup paths etc.
     * @param modal
     *            if true, dialog is modal
     * @param imagesStartScaled
     *            initial value of scaling mode (can be changed using the menu).
     *            If true, images will be scaled, if false, they will not be
     *            scaled.
     */
    public ImageDialog(Frame owner, ImageSettings imageSettings, boolean modal, boolean imagesStartScaled) {
        super(owner, modal, true);
        init(imageSettings, imagesStartScaled);
    }

    /**
     * ctor
     * 
     * @param owner
     *            parent window
     * @param imageSettings
     *            Intkey image settings - contains lookup paths etc.
     * @param modal
     *            if true, dialog is modal
     * @param imagesStartScaled
     *            initial value of scaling mode (can be changed using the menu).
     *            If true, images will be scaled, if false, they will not be
     *            scaled.
     */
    public ImageDialog(Dialog owner, ImageSettings imageSettings, boolean modal, boolean imagesStartScaled) {
        super(owner, modal, true);
        init(imageSettings, imagesStartScaled);
    }

    private void init(ImageSettings imageSettings, boolean imagesStartScaled) {
        _imageSettings = imageSettings;

        _okButtonPressed = false;

        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();

        _imageDescriptionFormatter = new Formatter(CommentStrippingMode.RETAIN, AngleBracketHandlingMode.RETAIN, true, false);

        _scaleImages = imagesStartScaled;

        setMinimumSize(new Dimension(500, 500));

        buildMenu();
        getContentPane().setLayout(new GridLayout(0, 1, 0, 0));
    }

    private void buildMenu() {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(ImageDialog.class);
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(ImageDialog.class, this);

        _menuBar = new JMenuBar();
        setJMenuBar(_menuBar);

        // Have to read directly from the resource map - injectFields does not
        // work for subclasses
        // of ImageDialog
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

        _mnuWindow = new JMenu(resourceMap.getString("ImageDialog.mnuWindowCaption"));
        _menuBar.add(_mnuWindow);

        _mnuItScaled = new JCheckBoxMenuItem();
        _mnuItScaled.setAction(actionMap.get("toggleScaling"));
        _mnuItScaled.getAction().putValue(javax.swing.Action.SELECTED_KEY, true);
        _mnuWindow.add(_mnuItScaled);
        _mnuItScaled.setSelected(_scaleImages);

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
        _images = images;

        // remove the old multiple image viewer if there is one.
        if (_multipleImageViewer != null) {
            getContentPane().remove(_multipleImageViewer);
        }

        _multipleImageViewer = new MultipleImageViewer(_imageSettings);
        _multipleImageViewer.setObserver(this);
        getContentPane().add(_multipleImageViewer);

        populateSubjectMenu(_images);

        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();

        _mnuItNextImage.setEnabled(_images.size() > 1);
        _mnuItPreviousImage.setEnabled(false);

        _multipleImageViewer.setScaleImages(_scaleImages);

        this.pack();
    }

    private void populateSubjectMenu(List<Image> images) {
        _mnuSubject.removeAll();

        ButtonGroup group = new ButtonGroup();

        for (final Image image : images) {
            String imageDescription = _imageDescriptionFormatter.defaultFormat(image.getSubjectTextOrFileName());
            JRadioButtonMenuItem rdBtnMnuIt = new JRadioButtonMenuItem(imageDescription);
            rdBtnMnuIt.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    showImage(_images.indexOf(image));
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
            setVisible(false);
        } else if (imageOverlay.isType(OverlayType.OLCANCEL)) {
            _okButtonPressed = false;
            setVisible(false);
        } else if (imageOverlay.isType(OverlayType.OLIMAGENOTES)) {
            Image image = _multipleImageViewer.getVisibleViewer().getViewedImage();
            RTFBuilder builder = new RTFBuilder();
            builder.startDocument();
            builder.appendText(image.getNotes());
            builder.endDocument();
            displayRTFWindow(builder.toString(), "Image Notes");
        } else if (imageOverlay.isType(OverlayType.OLNOTES)) {
            displayRTFWindow("Character notes go here", "Notes");
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

    public void showImage(int imageIndex) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        new ImageLoadWorker(imageIndex).execute();
    }

    @Action
    public void nextImage() {
        showImage(getIndexCurrentViewedImage() + 1);
    }

    @Action
    public void previousImage() {
        showImage(getIndexCurrentViewedImage() - 1);
    }

    @Action
    public void toggleScaling() {
        _scaleImages = !_scaleImages;
        _multipleImageViewer.setScaleImages(_scaleImages);
    }

    @Action
    public void toggleHideText() {
        _multipleImageViewer.toggleHideText();
    }

    @Action
    public void replaySound() {
        try {
            _multipleImageViewer.replaySound();
        } catch (Exception ex) {
            Logger.error(ex);
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ImageDialog.this), "Error occurred playing sound: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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
        Window applicationWindow = ((SingleFrameApplication) Application.getInstance()).getMainFrame();

        final JDialog dlg = new JDialog(this);
        dlg.setLayout(new BorderLayout());
        dlg.setUndecorated(true);

        Image image = _multipleImageViewer.getVisibleImage();
        BufferedImage bufferedImage = _multipleImageViewer.getVisibleViewer().getImage();
        String imageType = _multipleImageViewer.getVisibleViewer().getImageFormatName();
        URL imageLocation = _multipleImageViewer.getVisibleViewer().getImageFileLocation();

        ImageViewer imageViewer = new ImageViewer(image, _imageSettings, bufferedImage, imageLocation, imageType);
        imageViewer.addOverlaySelectionObserver(this);
        imageViewer.setScalingMode(ScalingMode.NO_SCALING);

        dlg.add(imageViewer, BorderLayout.CENTER);

        Rectangle r = applicationWindow.getGraphicsConfiguration().getBounds();
        dlg.setLocation(r.x, r.y);
        //
        // //set this dialog to have the same location - we don't want it still
        // visible on a different monitor
        this.setLocation(r.x, r.y);
        dlg.setSize(new Dimension(r.width, r.height));

        dlg.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dlg.setVisible(false);
            }
        });

        dlg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    dlg.setVisible(false);
                }
            }
        });

        dlg.setFocusable(true);
        dlg.setVisible(true);
    }

    @Action
    public void cascade() {
        IntKeyDialogController.cascadeWindows();
    }

    @Action
    public void tile() {
        IntKeyDialogController.tileWindows();
    }

    @Action
    public void closeAll() {
        IntKeyDialogController.closeWindows();
    }

    @Action
    public void aboutImage() {
        ImageViewer visibleViewer = _multipleImageViewer.getVisibleViewer();
        AboutImageDialog dlg = new AboutImageDialog(this, visibleViewer.getViewedImage().getSubjectTextOrFileName(), visibleViewer.getImageFileLocation(), visibleViewer.getImage(),
                visibleViewer.getImageFormatName());
        dlg.pack();
        dlg.setVisible(true);
    }

    protected void handleNewImageSelected() {
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
        _mnuItNextImage.setEnabled(!(_multipleImageViewer.getVisibleImage() == _images.get(_images.size() - 1)));
        _mnuItPreviousImage.setEnabled(!(_multipleImageViewer.getVisibleImage() == _images.get(0)));

        int viewedIndex = getIndexCurrentViewedImage();
        JMenuItem mnuIt = (JMenuItem) _mnuSubject.getMenuComponent(viewedIndex);
        mnuIt.setSelected(true);

        fitToImage();
        replaySound();
    }

    private int getIndexCurrentViewedImage() {
        return _images.indexOf(_multipleImageViewer.getVisibleImage());
    }

    protected void displayRTFWindow(String rtfContent, String title) {
        RtfReportDisplayDialog dlg = new RtfReportDisplayDialog(this, new SimpleRtfEditorKit(null), rtfContent, title);
        ((SingleFrameApplication) Application.getInstance()).show(dlg);
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

    /**
     * Loads images from their URLs on a background thread. Used because large
     * remotely hosted images lock up the UI if they are loaded on the event
     * dispatch thread.
     * 
     * @author ChrisF
     * 
     */
    private class ImageLoadWorker extends SwingWorker<List<Object>, Void> {

        private int _imageIndex;

        public ImageLoadWorker(int imageIndex) {
            _imageIndex = imageIndex;
        }

        @Override
        protected List<Object> doInBackground() throws Exception {
            Image img = _images.get(_imageIndex);
            URL imgURL = UIUtils.findImageFile(img.getFileName(), _imageSettings);
            Pair<BufferedImage, String> imageAndType = UIUtils.readImage(imgURL);
            List<Object> retList = new ArrayList<Object>();
            retList.add(img);
            retList.add(imageAndType.getFirst());
            retList.add(imageAndType.getSecond());
            retList.add(imgURL);
            return retList;
        }

        @Override
        protected void done() {
            ImageDialog.this.setCursor(Cursor.getDefaultCursor());
            try {
                List<Object> imageDataList = get();
                _multipleImageViewer.addImage(((Image) imageDataList.get(0)).getFileName(), (Image) imageDataList.get(0), (BufferedImage) imageDataList.get(1), (URL) imageDataList.get(3),
                        (String) imageDataList.get(2));
                _multipleImageViewer.showImage(((Image) imageDataList.get(0)).getFileName());
                handleNewImageSelected();
            } catch (Exception ex) {
                Logger.error(ex);
                JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(ImageDialog.this), "Error occurred loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
