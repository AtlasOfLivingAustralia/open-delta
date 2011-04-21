package au.org.ala.delta.model.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.Item;

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
	private String _imageData;
	private String _itemSubheading;
	private List<Float> _keyStateBoundaries;
	private boolean _containsSynonmyInfo;
	private boolean _omitOr;
	private boolean _useCc;
	private boolean _omitPeriod;
	private boolean _newParagraph;
	private boolean _nonAutoCc;

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
		return _states[stateNumber-1];
	}
	
	@Override
	public void setStateText(int stateNumber, String text) {
		
		_states[stateNumber-1] = text;
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
	 * @param notes the notes to set
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
    public String getImageData() {
        return _imageData;
    }

    @Override
    public void setImageData(String imageData) {
        _imageData = imageData;
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


	
}
