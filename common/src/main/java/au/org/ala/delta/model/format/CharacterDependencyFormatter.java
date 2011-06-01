package au.org.ala.delta.model.format;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Formats CharacterDependencies in a consistent manner.
 */
public class CharacterDependencyFormatter {

	private DeltaDataSet _dataSet;
	private CharacterFormatter _characterFormatter;
	
	public CharacterDependencyFormatter(DeltaDataSet dataSet) {
		_dataSet = dataSet;
		_characterFormatter = new CharacterFormatter(false, true, false, true);
	}
	
	public String formatCharacterDependency(CharacterDependency characterDependency) {
		
		MultiStateCharacter controllingCharacter = (MultiStateCharacter)_dataSet.getCharacter(characterDependency.getControllingCharacterId());
		
		StringBuilder description = new StringBuilder();
		
		appendSummary(characterDependency, controllingCharacter, description);
		
		appendText(characterDependency, controllingCharacter, description);
		
		return description.toString();
	}
	
	private void appendSummary(CharacterDependency characterDependency,
			MultiStateCharacter controllingCharacter, StringBuilder description) {
		description.append("[").append(controllingCharacter.getCharacterId());
		description.append(",");
		List<Integer> stateNumbers = characterDependency.getStatesAsList();
		for (int i=0; i<stateNumbers.size()-1; i++) {
			description.append(stateNumbers.get(i));
			description.append("/");
		}
		description.append(stateNumbers.get(stateNumbers.size()-1));
		description.append("] ");
	}

	private void appendText(CharacterDependency characterDependency,
			MultiStateCharacter controllingCharacter, StringBuilder description) {
		
		String charDescription = _characterFormatter.formatCharacterDescription(controllingCharacter);
		
		if (StringUtils.isEmpty(charDescription)) {
			charDescription = _characterFormatter.formatCharacterDescription(controllingCharacter, false);
		}
		description.append(charDescription);
		description.append(": ");
		
		List<Integer> stateNumbers = characterDependency.getStatesAsList();
		for (int i=0; i<stateNumbers.size()-1; i++) {
			description.append(_characterFormatter.formatState(controllingCharacter, stateNumbers.get(i)));
			description.append(", or ");
		}
		description.append(_characterFormatter.formatState(controllingCharacter, stateNumbers.get(stateNumbers.size()-1)));
	}
	
}
