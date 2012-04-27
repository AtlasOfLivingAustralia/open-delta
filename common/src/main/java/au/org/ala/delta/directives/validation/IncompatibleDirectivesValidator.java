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
package au.org.ala.delta.directives.validation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.AddCharacters;
import au.org.ala.delta.directives.ApplicableCharacters;
import au.org.ala.delta.directives.CharacterList;
import au.org.ala.delta.directives.CharacterReliabilities;
import au.org.ala.delta.directives.CharacterWeights;
import au.org.ala.delta.directives.DependentCharacters;
import au.org.ala.delta.directives.EmphasizeCharacters;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.InapplicableCharacters;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.ItemAbundances;
import au.org.ala.delta.directives.ItemWeights;
import au.org.ala.delta.directives.KeyCharacterList;

/**
 * Some directives must not appear together during a CONFOR run.  The
 * IncompatibleDirectivesValidator is responsible for enforcing this behaviour.
 * It also enforces that each directive only appears once during the run 
 * (with the exception of type 0 directives).
 */
public class IncompatibleDirectivesValidator {

	class IncompatibleDirectives {
		boolean _encountered;
		Set<String> _incompatibleDirectives;
		boolean _warning;
		
		public IncompatibleDirectives(Set<String> directives) {
			this(directives, false);
		}
		
		public IncompatibleDirectives(Set<String> directives, boolean warning) {
			_incompatibleDirectives = directives;
			_encountered = false;
			_warning = warning;
		}
	}
	
	private Map<String, IncompatibleDirectives> _incompatibleDirectives;
	
	private Set<String> _encounteredDirectives; 
	
	private Set<String> _encounteredDirectivesExceptions;
	
	private DeltaContext _context;
	
	public IncompatibleDirectivesValidator(DeltaContext context) {
		
		_context = context;
		_incompatibleDirectives = new HashMap<String, IncompatibleDirectives>();
		_encounteredDirectives = new HashSet<String>();
		
		add(IncludeItems.CONTROL_WORDS, ExcludeItems.CONTROL_WORDS);
		
		add(IncludeCharacters.CONTROL_WORDS, ExcludeCharacters.CONTROL_WORDS);
		
		add(ApplicableCharacters.CONTROL_WORDS, InapplicableCharacters.CONTROL_WORDS, DependentCharacters.CONTROL_WORDS, true);
	
		add(CharacterWeights.CONTROL_WORDS, CharacterReliabilities.CONTROL_WORDS);
		
		add(ItemWeights.CONTROL_WORDS, ItemAbundances.CONTROL_WORDS);
		
		add(CharacterList.CONTROL_WORDS, KeyCharacterList.CONTROL_WORDS);
		
		_encounteredDirectivesExceptions = new HashSet<String>();
		_encounteredDirectivesExceptions.add(join(AddCharacters.CONTROL_WORDS));
		_encounteredDirectivesExceptions.add(join(EmphasizeCharacters.CONTROL_WORDS));
		
	}
	
	private void add(String[] directive1, String[] directive2) {
		Set<String> incompatible = new HashSet<String>();
		incompatible.add(join(directive1));
		incompatible.add(join(directive2));
		addToIncompatibleDirectives(incompatible);
	}
	
	private void add(String[] directive1, String[] directive2, String[] directive3, boolean warning) {
		Set<String> incompatible = new HashSet<String>();
		incompatible.add(join(directive1));
		incompatible.add(join(directive2));
		incompatible.add(join(directive3));
		addToIncompatibleDirectives(incompatible, warning);
	}
	
	private void addToIncompatibleDirectives(Set<String> directives) {
		addToIncompatibleDirectives(directives, false);
	}
	private void addToIncompatibleDirectives(Set<String> directives, boolean warning) {
		IncompatibleDirectives incompatibleDirectives = new IncompatibleDirectives(directives, warning);
		for (String directive : directives) {
			_incompatibleDirectives.put(directive, incompatibleDirectives);
		}
	}
	
	public void validate(AbstractDirective<DeltaContext> directive) throws DirectiveException {
		String name = join(directive.getControlWords());
		if (directive.getOrder() > 0 && _encounteredDirectives.contains(name) &&
				!_encounteredDirectivesExceptions.contains(name)) {
			throw DirectiveError.asException(DirectiveError.Error.EQUIVALENT_DIRECTIVE_USED, 0);
		}
		_encounteredDirectives.add(name);
		
		IncompatibleDirectives incompatibleDirectives = _incompatibleDirectives.get(name);
		if (incompatibleDirectives != null) {
			if (incompatibleDirectives._encountered) {
				if (incompatibleDirectives._warning) {
					_context.addError(new DirectiveError(DirectiveError.Warning.EQUIVALENT_DIRECTIVE_USED, 0));
				} else {
					throw DirectiveError.asException(DirectiveError.Error.EQUIVALENT_DIRECTIVE_USED, 0);
				}
			}
			incompatibleDirectives._encountered = true;
		}
	}
	
	private String join(String[] controlWords) {
		return StringUtils.join(controlWords).toUpperCase();
	}
}
