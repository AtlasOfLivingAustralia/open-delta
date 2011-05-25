package au.org.ala.delta.model.impl;

import java.util.List;

public interface AttributeData {
	
	public String getValue();
	
	public void setValue(String value);
	
	public boolean isStatePresent(int stateNumber);
	
	public boolean isSimple();

	public void setStatePresent(int stateNumber, boolean present);

	public List<Integer> getPresentStates();

	public boolean isVariable();
}
