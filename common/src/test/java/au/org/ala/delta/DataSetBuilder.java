package au.org.ala.delta;

import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;

public class DataSetBuilder {

	public static void buildSimpleDataSet(MutableDeltaDataSet dataSet) throws Exception {
		MultiStateCharacter char1 = (MultiStateCharacter)dataSet.addCharacter(CharacterType.UnorderedMultiState);
		char1.setDescription("character 1 description");
		char1.setNumberOfStates(3);
		char1.setState(1, "state 1");
		char1.setState(2, "This is state 2");
		char1.setState(3, "3");
		
		TextCharacter char2 = (TextCharacter)dataSet.addCharacter(CharacterType.Text);
		char2.setDescription("this is character 2 description");
		
		IntegerCharacter char3 = (IntegerCharacter)dataSet.addCharacter(CharacterType.IntegerNumeric);
		char3.setDescription("Char 3 is an integer character");
		char3.setUnits("mm");
		
		RealCharacter char4 = (RealCharacter)dataSet.addCharacter(CharacterType.RealNumeric);
		char4.setDescription("Char 4 is a real character");
		
		
		
		Item item1 = dataSet.addItem();
		item1.setDescription("Item 1 description");
		dataSet.addAttribute(1, 1).setValueFromString("<attribute 1,1 comment>1&3");
		dataSet.addAttribute(1, 2).setValueFromString("<text character>");
		dataSet.addAttribute(1, 3).setValueFromString("(1-)2-3/6-8");
		dataSet.addAttribute(1, 4).setValueFromString("<text character>4.4");
		
		
		Item item2 = dataSet.addItem();
		item2.setDescription("Description of item 2");
		dataSet.addAttribute(2, 1).setValueFromString("<attribute 2,1 comment>1-2");
		dataSet.addAttribute(2, 2).setValueFromString("<attribute 2,2 text character>");
		dataSet.addAttribute(2, 3).setValueFromString("4");
		dataSet.addAttribute(2, 4).setValueFromString("5.1-7.9");
		
		Item item3 = dataSet.addItem();
		item3.setDescription("Item 3 has a great description");
	}
}
