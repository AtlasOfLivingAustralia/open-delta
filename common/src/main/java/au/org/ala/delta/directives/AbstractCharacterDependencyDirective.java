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
package au.org.ala.delta.directives;

import java.io.StringReader;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DependentCharactersParser;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.MutableDeltaDataSet;
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
		
		String data = args.getFirstArgumentText().trim();
		
		StringReader reader = new StringReader(data);
		DependentCharactersParser parser = new DependentCharactersParser(context, reader);
		parser.parse();
		
		addCharacterDependencies(context, parser.getCharacterDependencies());
	}

	protected void addCharacterDependencies(DeltaContext context, List<CharacterDependency> dependencies) {
		MutableDeltaDataSet dataSet = context.getDataSet();
		
		for (CharacterDependency dependency : dependencies) {
			MultiStateCharacter character = (MultiStateCharacter)dataSet.getCharacter(dependency.getControllingCharacterId());
			dataSet.addCharacterDependency(character, dependency.getStates(), dependency.getDependentCharacterIds());
		}
	}
}
