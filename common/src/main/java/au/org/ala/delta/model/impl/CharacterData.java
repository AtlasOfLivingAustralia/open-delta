package au.org.ala.delta.model.impl;

public interface CharacterData {
	
	public String getDescription();
	
	public boolean isExclusive();
	
	public boolean isMandatory();
	
	public String getUnits();
	
	public String getStateText(int stateNumber);
	
	public void setStateText(int stateNumber, String text);
	
	public int getNumberOfStates();
	
	public void setNumberOfStates(int numStates);
	
	public void setMandatory(boolean b);

	public void setDescription(String desc);

	public void setUnits(String units);

	public void setExclusive(boolean exclusive);
	
	public String getNotes();

	void setNotes(String note);
}
