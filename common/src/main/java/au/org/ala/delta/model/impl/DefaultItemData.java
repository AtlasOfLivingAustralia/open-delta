package au.org.ala.delta.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Implements ItemData and stores the data in memory.
 */
public class DefaultItemData implements ItemData {

	private String _description;
	private boolean _variant;
	private String _imageData;
	
	private Map<Character, Attribute> _attributes = new HashMap<Character, Attribute>();
	
	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public List<Attribute> getAttributes() {
		
		return null;
	}

	@Override
	public Attribute getAttribute(Character character) {
		
		Attribute attribute = _attributes.get(character);
		if (attribute == null) {
			attribute = new Attribute(character, new DefaultAttributeData());
		}
		
		return attribute; 
	}

	@Override
	public void addAttribute(Character character, String value) {
		DefaultAttributeData attributeData = new DefaultAttributeData();
		attributeData.setValue(value);
		Attribute attribute = new Attribute(character, attributeData);
		_attributes.put(character, attribute);
	}

	@Override
	public boolean isVariant() {
		return _variant;
	}

	@Override
	public void setVariant(boolean variant) {
		_variant = variant;	
	}

    @Override
    public String getImageData() {
        return _imageData;
    }

    @Override
    public void setImageData(String imageData) {
        _imageData = imageData;
    }
	
	
	

}
