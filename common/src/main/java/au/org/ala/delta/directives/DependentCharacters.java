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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class DependentCharacters extends AbstractCharacterListDirective<DeltaContext, String> {

    public DependentCharacters() {
        super("dependent", "characters");
    }
    
    public DependentCharacters(String... controlWords) {
		super(controlWords);
	}

    @Override
    protected String interpretRHS(DeltaContext context, String rhs) {
        return rhs;
    }
    
    @Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

    @Override
    protected void processCharacter(DeltaContext context, final int charIndex, String rhs) {
        // Dependencies take the form <c>,<s>:<d>. rhs == <s>:<d>
        // <s> = a set of state ids separated by "/"
        // <d> is a set of character ids separated by : and can include ranges
        // (n-n)

        final au.org.ala.delta.model.Character ch = context.getCharacter(charIndex);
        final Set<Integer> states = new HashSet<Integer>();
        String stateSet = rhs.substring(0, rhs.indexOf(":"));
        String[] stateIds = stateSet.split("/");
        for (String stateId : stateIds) {
            states.add(Integer.parseInt(stateId));
        }

        String charSet = rhs.substring(rhs.indexOf(":") + 1);
        String[] charBits = charSet.split(":");
        for (String charBit : charBits) {
            IntRange r = parseRange(charBit);

            Set<Integer> dependentCharacterIds = new HashSet<Integer>();
            for (int dependentChar : r.toArray()) {
                dependentCharacterIds.add(dependentChar);
            }

            //CharacterDependency d = new CharacterDependency(charIndex, states, dependentCharacterIds);
            
            /*
             * forEach(r, context, new IntegerFunctor<DeltaContext>() {
             * 
             * @Override public void invoke(DeltaContext context, int arg) {
             * CharacterDependency d = new CharacterDependency(charIndex,
             * states, arg); Logger.debug("Character dependency: %s", d);
             * ch.addDependentCharacter(d); } });
             */
        }

    }

	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {}
    
    

}
