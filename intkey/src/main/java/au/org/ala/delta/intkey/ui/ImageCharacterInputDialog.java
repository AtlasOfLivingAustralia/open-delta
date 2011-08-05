package au.org.ala.delta.intkey.ui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.text.ParseException;
import java.util.ArrayList;
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
import au.org.ala.delta.model.Character;
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

public class ImageCharacterInputDialog extends JDialog implements OverlaySelectionObserver {

    private ImageSettings _imageSettings;
    private Set<Integer> _selectedStates;
    private Set<String> _selectedKeywords;
    private Set<Pair<String, String>> _selectedValues;

    private Character _character;
    private MultipleImageViewer _multipleImageViewer;

    public ImageCharacterInputDialog(Frame owner, Character character, ImageSettings imageSettings) {
        super(owner, true);
        init(character, imageSettings);
    }

    /**
     * @wbp.parser.constructor
     */
    public ImageCharacterInputDialog(Dialog owner, Character character, ImageSettings imageSettings) {
        super(owner, true);
        init(character, imageSettings);
    }

    private void init(Character character, ImageSettings imageSettings) {
        getContentPane().setLayout(new BorderLayout(0, 0));

        _imageSettings = imageSettings;
        _character = character;
        _selectedStates = new HashSet<Integer>();
        _selectedKeywords = new HashSet<String>();
        _selectedValues = new HashSet<Pair<String, String>>();

        buildMenu();

        List<Image> images = _character.getImages();
        _multipleImageViewer = new MultipleImageViewer(_imageSettings);
        for (Image image : images) {
            ImageViewer viewer = new ImageViewer(image, _imageSettings);
            _multipleImageViewer.addImageViewer(viewer);
            viewer.addOverlaySelectionObserver(this);
        }

        getContentPane().add(_multipleImageViewer, BorderLayout.CENTER);

        this.pack();
    }

    private void buildMenu() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(this);

        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu mnControl = new JMenu("Control");
        menuBar.add(mnControl);

        JMenuItem mnuItNextImage = new JMenuItem();
        mnuItNextImage.setAction(actionMap.get("StateSelectionFromImageDialog_mnuItNextImage"));
        mnControl.add(mnuItNextImage);

        JMenuItem mnuItPreviousImage = new JMenuItem();
        mnuItPreviousImage.setAction(actionMap.get("StateSelectionFromImageDialog_mnuItPreviousImage"));
        mnControl.add(mnuItPreviousImage);
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        overlay.setSelected(!overlay.isSelected());
        ImageOverlay imageOverlay = overlay.getImageOverlay();
        if (imageOverlay.isType(OverlayType.OLOK) || imageOverlay.isType(OverlayType.OLCANCEL)) {
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
            _selectedValues.add(new Pair(minimumValString, maximumValString));
        }
    }

    @Action
    public void StateSelectionFromImageDialog_mnuItNextImage() {
        _multipleImageViewer.nextImage();
        reSelectStatesInNewViewer(_multipleImageViewer.getVisibleViewer());
    }

    @Action
    public void StateSelectionFromImageDialog_mnuItPreviousImage() {
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

                // Second value in the pair will be null if the value field represents a
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

                    // Second value in the pair will be null if the value field represents a
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

}
