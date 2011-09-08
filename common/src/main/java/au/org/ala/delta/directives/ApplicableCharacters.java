package au.org.ala.delta.directives;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;


/**
 * Processes the APPLICABLE CHARACTERS directive.
 */
public class ApplicableCharacters extends AbstractCharacterDependencyDirective {


	public ApplicableCharacters() {
		super("applicable", "characters");
	}
	
	@Override
	protected void addCharacterDependencies(DeltaContext context, List<CharacterDependency> dependencies) {
		DeltaDataSet dataSet = context.getDataSet();
		
		for (CharacterDependency dependency : dependencies) {
			MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(dependency.getControllingCharacterId());
			Set<Integer> states = invertStates(character, dependency.getStates());
			dataSet.addCharacterDependency(character, states, dependency.getDependentCharacterIds());
		}
	}
	
	private Set<Integer> invertStates(MultiStateCharacter character, Set<Integer> states) {
		Set<Integer> inverted = new HashSet<Integer>();
		for (int i=1; i<character.getNumberOfStates(); i++) {
			if (!states.contains(i)) {
				inverted.add(i);
			}
		}
		return inverted;
	}
	
}
