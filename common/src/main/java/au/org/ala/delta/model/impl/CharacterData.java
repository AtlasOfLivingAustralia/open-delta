package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.Item;

public interface CharacterData extends Illustratable {
	
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

	void validateAttributeText(String text);
	
	ControllingInfo checkApplicability(Item item);
	
    float getReliability();

    void setReliability(float reliability);
    
    int getMaximumValue();

    void setMaximumValue(int max);    

    int getMinimumValue();

    void setMinimumValue(int min);
    
    String getImageData();
    
    void setImageData(String imageData);
    
    String getItemSubheading();
    
    void setItemSubheading(String charItemSubheading);
    
    List<Float> getKeyStateBoundaries();
    
    void setKeyStateBoundaries(List<Float> keyStateBoundaries);
    
    boolean getContainsSynonmyInformation();
    
    void setContainsSynonmyInformation(boolean containsSynonmyInfo);
    
    boolean getOmitOr();
    
    void setOmitOr(boolean omitOr);
    
    boolean getUseCc();
    
    void setUseCc(boolean useCc);
    
    boolean getOmitPeriod();
    
    void setOmitPeriod(boolean omitPeriod);
    
    boolean getNewParagraph();
    
    void setNewParagraph(boolean newParagraph);
    
    boolean getNonAutoCc();
    
    void setNonAutoCc(boolean nonAutoCc);
}
