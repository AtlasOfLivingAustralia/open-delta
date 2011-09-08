package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DependentCharactersParser;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Base class for Dependent Characters, Inapplicable Characters and
 * Applicable Characters.
 */
public abstract class AbstractCharacterDependencyDirective extends AbstractTextDirective {

	public AbstractCharacterDependencyDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		String data = args.getFirstArgumentText();
		
		StringReader reader = new StringReader(data);
		DependentCharactersParser parser = new DependentCharactersParser(context, reader);
		parser.parse();
		
		//addCharacterDependencies(context, parser.getCharacterDependencies());
	}

	protected void addCharacterDependencies(DeltaContext context, List<CharacterDependency> dependencies) {
		DeltaDataSet dataSet = context.getDataSet();
		
		for (CharacterDependency dependency : dependencies) {
			MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(dependency.getControllingCharacterId());
			dataSet.addCharacterDependency(character, dependency.getStates(), dependency.getDependentCharacterIds());
		}
	}
}