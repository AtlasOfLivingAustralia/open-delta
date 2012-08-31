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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.SystemColor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ActionMap;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.Resource;
import org.jdesktop.application.ResourceMap;

import au.org.ala.delta.intkey.directives.ParsingUtils;
import au.org.ala.delta.intkey.model.FormattingUtils;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.format.CharacterFormatter;
import au.org.ala.delta.model.format.Formatter.AngleBracketHandlingMode;
import au.org.ala.delta.model.format.Formatter.CommentStrippingMode;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.rtf.RTFBuilder;
import au.org.ala.delta.ui.image.ImageViewer;
import au.org.ala.delta.ui.image.SelectableOverlay;
import au.org.ala.delta.ui.image.overlay.HotSpotGroup;
import au.org.ala.delta.ui.image.overlay.SelectableTextOverlay;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

/**
 * Used to Display images for a single character
 * 
 * @author ChrisF
 * 
 */
public class CharacterImageDialog extends ImageDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 6274578091708982240L;

    private List<Character> _characters;

    private JMenuItem _mnuItNextCharacter;
    private JMenuItem _mnuItPreviousCharacter;

    private int _selectedCharacterIndex;

    protected Set<Integer> _initialSelectedStates;
    protected Set<Integer> _initialIntegerValues;
    protected FloatRange _initialRealValues;
    protected List<String> _initialTextValues;

    private JPanel _pnlControllingCharacterMessage;
    private JLabel _lblWarningIcon;
    private JTextArea _txtControllingCharacterMessage;

    /**
     * True if the states or values for the characters can be edited.
     */
    private boolean _valuesEditable;

    /**
     * True if the initial values (values for the character set in the specimen
     * prior to the opening of the dialog) have been displayed in the dialog.
     */
    private boolean _initialValuesDisplayed = false;

    // Title used when the dialog is used to enter values for an integer
    // character
    @Resource
    String integerTitle;

    // Title used when the dialog is used to enter values for a real character
    @Resource
    String realTitle;

    // Title used when the dialog is used to enter values for an multistate
    // character
    @Resource
    String multistateTitle;

    // Title used when the dialog is used only for display purposes - character
    // values are not
    // editable.
    @Resource
    String notEditableTitle;

    // Message shown when users attempts to set character values when the dialog
    // is being used by the ILLUSTRATE CHARACTERS directive.
    @Resource
    String imageForViewingOnlyMessage;

    /**
     * ctor
     * 
     * @param owner
     *            Frame owner of the dialog
     * @param characters
     *            the character being viewed in this dialog
     * @param dependentCharacter
     *            if this dialog is being used to set the value of a (single)
     *            controlling character before its dependent character is set,
     *            this argument should be a reference to the dependent
     *            character. In all other cases it should be null.
     * @param imageSettings
     *            Image settings
     * @param modal
     *            true if the dialog should be modal
     * @param valuesEditable
     *            true if the overlays should be editable
     * @param initScalingMode
     *            true if images should start scaled.
     */
    public CharacterImageDialog(Frame owner, List<Character> characters, Character dependentCharacter, ImageSettings imageSettings, boolean modal, boolean valuesEditable, boolean initScalingMode) {
        super(owner, imageSettings, modal, initScalingMode);
        init(characters, dependentCharacter, valuesEditable);
    }

    /**
     * ctor
     * 
     * @param owner
     *            Dialog owner of the dialog
     * @param characters
     *            the character being viewed in this dialog
     * @param dependentCharacter
     *            if this dialog is being used to set the value of a (single)
     *            controlling character before its dependent character is set,
     *            this argument should be a reference to the dependent
     *            character. In all other cases it should be null.
     * @param imageSettings
     *            Image settings
     * @param modal
     *            true if the dialog should be modal
     * @param valuesEditable
     *            true if the overlays should be editable
     * @param initScalingMode
     *            true if images should start scaled.
     */
    public CharacterImageDialog(Dialog owner, List<Character> characters, Character dependentCharacter, ImageSettings imageSettings, boolean modal, boolean valuesEditable, boolean initScalingMode) {
        super(owner, imageSettings, modal, initScalingMode);
        init(characters, dependentCharacter, valuesEditable);
    }

    private void init(List<Character> characters, Character dependentCharacter, boolean valuesEditable) {
        ResourceMap resourceMap = Application.getInstance().getContext().getResourceMap(CharacterImageDialog.class);
        resourceMap.injectFields(this);

        _characters = characters;
        _valuesEditable = valuesEditable;
        getContentPane().setLayout(new BorderLayout(0, 0));

        buildMenuItems();

        if (dependentCharacter != null) {
            if (characters.size() != 1) {
                throw new IllegalArgumentException("Dependent character should only be supplied if there is a single character being viewed in the dialog");
            }

            Character ch = characters.get(0);

            _pnlControllingCharacterMessage = new JPanel();
            _pnlControllingCharacterMessage.setFocusable(false);
            _pnlControllingCharacterMessage.setBorder(new EmptyBorder(5, 0, 0, 0));
            getContentPane().add(_pnlControllingCharacterMessage, BorderLayout.NORTH);
            _pnlControllingCharacterMessage.setLayout(new BorderLayout(0, 0));

            _lblWarningIcon = new JLabel("");
            _lblWarningIcon.setFocusable(false);
            _lblWarningIcon.setIcon(UIManager.getIcon("OptionPane.warningIcon"));
            _pnlControllingCharacterMessage.add(_lblWarningIcon, BorderLayout.WEST);

            _txtControllingCharacterMessage = new JTextArea();
            CharacterFormatter formatter = new CharacterFormatter(true, CommentStrippingMode.RETAIN, AngleBracketHandlingMode.REMOVE_SURROUNDING_REPLACE_INNER, true, false);
            String setControllingCharacterMessage = UIUtils.getResourceString("MultiStateInputDialog.setControllingCharacterMessage", formatter.formatCharacterDescription(dependentCharacter),
                    formatter.formatCharacterDescription(ch));
            _txtControllingCharacterMessage.setText(setControllingCharacterMessage);
            _txtControllingCharacterMessage.setFocusable(false);
            _txtControllingCharacterMessage.setBorder(new EmptyBorder(0, 5, 0, 0));
            _txtControllingCharacterMessage.setEditable(false);
            _pnlControllingCharacterMessage.add(_txtControllingCharacterMessage);
            _txtControllingCharacterMessage.setWrapStyleWord(true);
            _txtControllingCharacterMessage.setFont(UIManager.getFont("Button.font"));
            _txtControllingCharacterMessage.setLineWrap(true);
            _txtControllingCharacterMessage.setBackground(SystemColor.control);
        }
    }

    private void buildMenuItems() {
        ActionMap actionMap = Application.getInstance().getContext().getActionMap(CharacterImageDialog.class, this);

        _mnuItNextCharacter = new JMenuItem();
        _mnuItNextCharacter.setAction(actionMap.get("viewNextCharacter"));
        _mnuControl.add(_mnuItNextCharacter);

        _mnuItPreviousCharacter = new JMenuItem();
        _mnuItPreviousCharacter.setAction(actionMap.get("viewPreviousCharacter"));
        _mnuControl.add(_mnuItPreviousCharacter);
    }

    private void displayImagesForCharacter(int characterIndex, int imageToShow) {
        _selectedCharacterIndex = characterIndex;
        Character selectedCharacter = _characters.get(characterIndex);

        setImages(selectedCharacter.getImages());
        showImage(imageToShow);

        _mnuItNextCharacter.setEnabled(_selectedCharacterIndex < _characters.size() - 1);
        _mnuItPreviousCharacter.setEnabled(_selectedCharacterIndex > 0);
    }

    private void updateTitle() {
        Character selectedCharacter = _characters.get(_selectedCharacterIndex);

        if (_valuesEditable) {
            if (selectedCharacter instanceof IntegerCharacter) {
                setTitle(integerTitle);
            } else if (selectedCharacter instanceof RealCharacter) {
                setTitle(realTitle);
            } else if (selectedCharacter instanceof MultiStateCharacter) {
                setTitle(multistateTitle);
            }
        } else {
            String strCharacterNumber = Integer.toString(selectedCharacter.getCharacterId());
            String formattedImageName = _imageDescriptionFormatter.defaultFormat(_multipleImageViewer.getVisibleImage().getSubjectTextOrFileName());
            setTitle(MessageFormat.format(notEditableTitle, strCharacterNumber, formattedImageName));
        }
    }

    @Override
    protected void handleNewImageSelected() {
        super.handleNewImageSelected();
        if (_initialValuesDisplayed) {
            ImageViewer currentVisibleViewer = _multipleImageViewer.getVisibleViewer();
            ImageViewer previouslyVisibleViewer = _multipleImageViewer.getPreviouslyVisibleViewer();

            selectStatesInViewer(currentVisibleViewer, _selectedStates);

            if (previouslyVisibleViewer != null) {
                currentVisibleViewer.setInputText(previouslyVisibleViewer.getInputText());
            }
        } else {
            populateWithInitialValues();
        }
        updateTitle();
    }

    public void displayImagesForCharacter(Character ch) {
        int characterIndex = _characters.indexOf(ch);
        if (characterIndex > -1) {
            displayImagesForCharacter(characterIndex, 0);
        }
    }

    @Action
    public void viewNextCharacter() {
        if (_selectedCharacterIndex < _characters.size() - 1) {
            displayImagesForCharacter(_selectedCharacterIndex + 1, 0);
        }
    }

    @Action
    public void viewPreviousCharacter() {
        if (_selectedCharacterIndex > 0) {
            displayImagesForCharacter(_selectedCharacterIndex - 1, 0);
        }
    }

    @Override
    public void overlaySelected(SelectableOverlay overlay) {
        ImageOverlay imageOverlay = overlay.getImageOverlay();
        if (imageOverlay.isType(OverlayType.OLNOTES)) {
            RTFBuilder builder = new RTFBuilder();
            builder.startDocument();
            builder.appendText(_characters.get(_selectedCharacterIndex).getNotes());
            builder.endDocument();
            displayRTFWindow(builder.toString(), "Notes");
        } else {
            if (_valuesEditable) {
                super.overlaySelected(overlay);
            } else {
                if (imageOverlay.isType(OverlayType.OLOK) || imageOverlay.isType(OverlayType.OLCANCEL)) {
                    super.overlaySelected(overlay);
                } else {
                    JOptionPane.showMessageDialog(this, imageForViewingOnlyMessage, "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    public Set<Integer> getSelectedStates() {
        return _selectedStates;
    }

    /**
     * Set the states that should initially be selected when a multistate
     * character is shown in the image dialog. If set to null, no states will be
     * selected.
     * 
     * @param selectedStates
     */
    public void setInitialSelectedStates(Set<Integer> selectedStates) {
        _initialSelectedStates = selectedStates;

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

    /**
     * Set the values that should initially be input in the input field when an
     * integer character is shown in the image dialog. If set to null, the input
     * field will be empty.
     * 
     * @param selectedStates
     */
    public void setInitialIntegerValues(Set<Integer> intValues) {
        _initialIntegerValues = intValues;
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

        // if the range is still null, return a float range with negative
        // infinity. This represents "no values selected".
        if (retRange == null) {
            retRange = new FloatRange(Float.NEGATIVE_INFINITY);
        }

        return retRange;
    }

    /**
     * Set the values that should initially be input in the input field when a
     * real character is shown in the image dialog. If set to null, the input
     * field will be empty.
     * 
     * @param realRange
     */
    public void setInitialRealValues(FloatRange realRange) {
        _initialRealValues = realRange;
    }

    public List<String> getInputTextValues() {
        String inputText = _multipleImageViewer.getVisibleViewer().getInputText();
        if (!inputText.isEmpty()) {
            return ParsingUtils.parseTextCharacterValue(inputText);
        } else {
            // empty list represents no values input
            return new ArrayList<String>();
        }
    }

    /**
     * Set the values that should initially be input in the input field when a
     * text character is shown in the image dialog. If set to null, the input
     * field will be empty.
     * 
     * @param textValues
     */
    public void setInitialTextValues(List<String> textValues) {
        _initialTextValues = textValues;
    }

    private void populateWithInitialValues() {
        if (_characters.size() != 1) {
            throw new IllegalStateException("Can only populate character viewer with initial values if a single character is being viewed.");
        }

        Character ch = _characters.get(0);

        if (ch instanceof MultiStateCharacter) {
            if (_initialSelectedStates != null) {
                selectStatesInViewer(_multipleImageViewer.getVisibleViewer(), _initialSelectedStates);
                _selectedStates.addAll(_initialSelectedStates);
            }
        } else if (ch instanceof IntegerCharacter) {
            IntegerCharacter intChar = (IntegerCharacter) ch;
            if (_initialIntegerValues != null) {
                _multipleImageViewer.getVisibleViewer().setInputText(FormattingUtils.formatIntegerValuesAsString(_initialIntegerValues, intChar.getMinimumValue(), intChar.getMaximumValue()));
            }
        } else if (ch instanceof RealCharacter) {
            if (_initialRealValues != null) {
                _multipleImageViewer.getVisibleViewer().setInputText(Utils.formatFloatRangeAsString(_initialRealValues));
            }
        } else if (ch instanceof TextCharacter) {
            if (_initialTextValues != null) {
                _multipleImageViewer.getVisibleViewer().setInputText(StringUtils.join(_initialTextValues, "/"));
            }
        } else {
            throw new IllegalArgumentException("Unrecognized character type");
        }

        _initialValuesDisplayed = true;
    }

    /**
     * Select the supplied states in the supplied image viewer.
     * 
     * @param viewer
     *            The viewer
     * @param statesToSelect
     *            The states to select
     */
    private void selectStatesInViewer(ImageViewer viewer, Set<Integer> statesToSelect) {
        List<ImageOverlay> overlays = viewer.getOverlays();
        for (ImageOverlay overlay : overlays) {
            if (overlay.isType(OverlayType.OLSTATE)) {
                int stateId = overlay.stateId;

                SelectableTextOverlay selectableText = viewer.getSelectableTextForOverlay(overlay);
                if (selectableText != null) {
                    selectableText.setSelected(statesToSelect.contains(stateId));
                }

                HotSpotGroup hotSpotGroup = viewer.getHotSpotGroupForOverlay(overlay);
                if (hotSpotGroup != null) {
                    hotSpotGroup.setSelected(statesToSelect.contains(stateId));
                }
            }
        }
    }

}
