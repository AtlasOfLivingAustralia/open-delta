package au.org.ala.delta.model.impl;

public interface CharacterData {
	
	String getDescription();
	
	boolean isExclusive();
	
	boolean isMandatory();
	
	String getUnits();
	
	String getStateText(int stateNumber);
	
	void setStateText(int stateNumber, String text);
	
	int getNumberOfStates();
	
	void setNumberOfStates(int numStates);
	
	void setMandatory(boolean b);

	void setDescription(String desc);

	void setUnits(String units);

	void setExclusive(boolean exclusive);
	
	String getNotes();

	void setNotes(String note);
	
	int getCodedImplicitState();
	
	void setCodedImplicitState(int stateId);
	
	int getUncodedImplicitState();
	
	void setUncodedImplicitState(int stateId);
	
}
