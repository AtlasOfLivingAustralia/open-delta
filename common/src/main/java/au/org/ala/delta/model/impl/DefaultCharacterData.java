package au.org.ala.delta.model.impl;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageType;

/**
 * An implementation of CharacterData that maintains the data in-memory.
 */
public class DefaultCharacterData implements CharacterData {

    private String _notes;
    private String _description;
    private boolean _exclusive;
    private boolean _mandatory;
    private String _units;
    private String[] _states = new String[0];
    private int _codedImplicitStateId;
    private int _uncodedImplicitStateId;
    private float _reliability;
    private int _maximumValue;
    private int _minimumValue;
    private String _itemSubheading;
    private List<Float> _keyStateBoundaries;
    private boolean _containsSynonmyInfo;
    private boolean _omitOr;
    private boolean _useCc;
    private boolean _omitPeriod;
    private boolean _newParagraph;
    private boolean _nonAutoCc;
    private List<CharacterDependency> _dependentCharacters = new ArrayList<CharacterDependency>();
    private List<CharacterDependency> _controllingCharacters = new ArrayList<CharacterDependency>();

    // For INTKEY only - in intkey datasets some integer characters are
    // represented as real characters
    private boolean isIntegerRepresentedAsReal;

    private List<Image> _images = new ArrayList<Image>();

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public void setDescription(String description) {
        _description = description;
    }

    @Override
    public boolean isExclusive() {
        return _exclusive;
    }

    @Override
    public boolean isMandatory() {
        return _mandatory;
    }

    @Override
    public String getUnits() {
        return _units;
    }

    @Override
    public void setUnits(String units) {
        _units = units;
    }

    @Override
    public String getStateText(int stateNumber) {
        return _states[stateNumber - 1];
    }

    @Override
    public void setStateText(int stateNumber, String text) {

        _states[stateNumber - 1] = text;
    }

    @Override
    public void setNumberOfStates(int numStates) {
        _states = new String[numStates];

    }

    @Override
    public int getNumberOfStates() {
        return _states.length;
    }

    @Override
    public void setMandatory(boolean mandatory) {
        _mandatory = mandatory;
    }

    /**
     * @return the notes about this character
     */
    public String getNotes() {
        return _notes;
    }

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        _notes = notes;
    }

    @Override
    public void setExclusive(boolean exclusive) {
        _exclusive = exclusive;

    }

    @Override
    public int getCodedImplicitState() {
        return _codedImplicitStateId;
    }

    @Override
    public void setCodedImplicitState(int stateId) {
        _codedImplicitStateId = stateId;
    }

    @Override
    public int getUncodedImplicitState() {
        return _uncodedImplicitStateId;
    }

    @Override
    public void setUncodedImplicitState(int stateId) {
        _uncodedImplicitStateId = stateId;
    }

    @Override
    public void validateAttributeText(String text) {
        throw new NotImplementedException();
    }

    @Override
    public ControllingInfo checkApplicability(Item item) {
        throw new NotImplementedException();
    }

    @Override
    public float getReliability() {
        return _reliability;
    }

    @Override
    public void setReliability(float reliability) {
        _reliability = reliability;
    }

    @Override
    public int getMaximumValue() {
        return _maximumValue;
    }

    @Override
    public void setMaximumValue(int max) {
        _maximumValue = max;
    }

    @Override
    public int getMinimumValue() {
        return _minimumValue;
    }

    @Override
    public void setMinimumValue(int min) {
        _minimumValue = min;
    }

    @Override
    public String getItemSubheading() {
        return _itemSubheading;
    }

    @Override
    public void setItemSubheading(String charItemSubheading) {
        _itemSubheading = charItemSubheading;
    }

    @Override
    public List<Float> getKeyStateBoundaries() {
        return new ArrayList<Float>(_keyStateBoundaries);
    }

    @Override
    public void setKeyStateBoundaries(List<Float> keyStateBoundaries) {
        _keyStateBoundaries = new ArrayList<Float>(keyStateBoundaries);
    }

    @Override
    public boolean getContainsSynonmyInformation() {
        return _containsSynonmyInfo;
    }

    @Override
    public void setContainsSynonmyInformation(boolean containsSynonmyInfo) {
        _containsSynonmyInfo = containsSynonmyInfo;
    }

    @Override
    public boolean getOmitOr() {
        return _omitOr;
    }

    @Override
    public void setOmitOr(boolean omitOr) {
        _omitOr = omitOr;
    }

    @Override
    public boolean getUseCc() {
        return _useCc;
    }

    @Override
    public void setUseCc(boolean useCc) {
        _useCc = useCc;
    }

    @Override
    public boolean getOmitPeriod() {
        return _omitPeriod;
    }

    @Override
    public void setOmitPeriod(boolean omitPeriod) {
        _omitPeriod = omitPeriod;
    }

    @Override
    public boolean getNewParagraph() {
        return _newParagraph;
    }

    @Override
    public void setNewParagraph(boolean newParagraph) {
        _newParagraph = newParagraph;
    }

    @Override
    public boolean getNonAutoCc() {
        return _nonAutoCc;
    }

    @Override
    public void setNonAutoCc(boolean nonAutoCc) {
        _nonAutoCc = nonAutoCc;
    }

    @Override
    public void addState() {
        List<String> states = Arrays.asList(_states);
        states.add("");
        _states = states.toArray(new String[states.size()]);
    }

    @Override
    public void moveState(int stateNumber, int newStateNumber) {
        List<String> states = Arrays.asList(_states);
        String text = states.remove(stateNumber - 1);
        states.add(newStateNumber - 1, text);
    }

    @Override
    public Image addImage(String fileName, String comments) {
        DefaultImageData imageData = new DefaultImageData(fileName);
        Image image = new Image(imageData);
        try {
            if (comments != null) {
                List<ImageOverlay> overlayList = new ImageOverlayParser().parseOverlays(comments, ImageType.IMAGE_CHARACTER);
                imageData.setOverlays(overlayList);
            }
            _images.add(image);
            return image;
        } catch (ParseException ex) {
            throw new RuntimeException("Error parsing character image overlay data");
        }
    }

    @Override
    public void addImage(Image image) {
        _images.add(image);
    }

    @Override
    public List<Image> getImages() {
        return _images;
    }

    @Override
    public int getImageCount() {
        return _images.size();
    }

    @Override
    public void deleteImage(Image image) {
        _images.remove(image);
    }

    @Override
    public void moveImage(Image image, int position) {
        int imageIndex = _images.indexOf(image);
        _images.remove(imageIndex);
        _images.add(position, image);
    }

    public void addDependentCharacters(CharacterDependency dependency) {
        _dependentCharacters.add(dependency);
    }

    public List<CharacterDependency> getDependentCharacters() {
        return _dependentCharacters;
    }

    public void addControllingCharacters(CharacterDependency dependency) {
        _controllingCharacters.add(dependency);
    }

    public List<CharacterDependency> getControllingCharacters() {
        return _controllingCharacters;
    }

    @Override
    public List<Integer> getControlledCharacterNumbers(boolean indirect) {
        throw new NotImplementedException();
    }

    @Override
    public void removeControllingCharacter(CharacterDependency dependency) {
        _controllingCharacters.remove(dependency);

    }
    
    @Override
    public boolean isIntegerRepresentedAsReal() {
        return isIntegerRepresentedAsReal;
    }

    @Override
    public void setIntegerRepresentedAsReal(boolean isIntegerRepresentedAsReal) {
        this.isIntegerRepresentedAsReal = isIntegerRepresentedAsReal;
    }

}
