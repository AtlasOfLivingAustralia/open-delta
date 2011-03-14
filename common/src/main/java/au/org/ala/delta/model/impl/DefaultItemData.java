package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;

public class DefaultItemData implements ItemData {

	private String _description;
	private int _itemId;
	
	@Override
	public String getDescription() {
		return _description;
	}

	@Override
	public void setDescription(String description) {
		_description = description;
	}

	@Override
	public int getItemId() {
		return _itemId;
	}

	@Override
	public List<Attribute> getAttributes() {
		
		return null;
	}

	@Override
	public Attribute getAttribute(Character character) {
		return null;
	}

}
