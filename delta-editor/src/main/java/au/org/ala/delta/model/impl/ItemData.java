package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;

public interface ItemData {
	
	public int getItemId();
	
	public List<Attribute> getAttributes();
	
	public Attribute getAttribute(Character character);
}
