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
package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.Item;

public interface CharacterData extends Illustratable {
	
	int getNumber();
	
	void setNumber(int number);
	
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

	void validateAttributeText(String text, ControllingInfo controlled);
	
	ControllingInfo checkApplicability(Item item);
	
    float getReliability();

    void setReliability(float reliability);
    
    int getMaximumValue();

    void setMaximumValue(int max);    

    int getMinimumValue();

    void setMinimumValue(int min);
    
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

	void addState(int index);

	void moveState(int stateNumber, int newNumber);

	void addDependentCharacters(CharacterDependency dependency);

    List<CharacterDependency> getDependentCharacters();
    
    void addControllingCharacters(CharacterDependency dependency);
    
    void removeControllingCharacter(CharacterDependency dependency);

    List<CharacterDependency> getControllingCharacters();

	List<Integer> getControlledCharacterNumbers(boolean indirect);
	
    boolean isIntegerRepresentedAsReal();

    public void setIntegerRepresentedAsReal(boolean isIntegerRepresentedAsReal);
}
