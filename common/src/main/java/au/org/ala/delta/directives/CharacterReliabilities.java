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

import au.org.ala.delta.DeltaContext;



/**
 * Processes the CHARACTER RELIABILITES directive.
 */
public class CharacterReliabilities extends CharacterWeightDirective {

	/** The default weight for any characters not included in this directive */
	private static final double DEFAULT_WEIGHT = 5.0d;
	
	/** The minimum allowed weight for a character */
	private static final double MIN_WEIGHT = 0d;
	
	/** The maximum allowed weight for a character */
	private static final double MAX_WEIGHT = 10d;
	
	public CharacterReliabilities() {
		super(MIN_WEIGHT, MAX_WEIGHT, DEFAULT_WEIGHT, "character", "reliabilities");
	}
	

	@Override
	protected void setDefaults(DeltaContext context) {
		for (int i=1; i<=context.getNumberOfCharacters(); i++) {
			context.setCharacterReliability(i, DEFAULT_WEIGHT);
		}
	}
	
	@Override
	protected void processCharacter(DeltaContext context, int charIndex, Double reliability) {
		
		if (reliability < MIN_WEIGHT) {
			throw new IllegalArgumentException("The weight must be greater than "+ MIN_WEIGHT);
		}
		if (reliability > MAX_WEIGHT) {
			throw new IllegalArgumentException("The weight must be less than "+ MAX_WEIGHT);
		}
		context.setCharacterReliability(charIndex, reliability);
	}
	
}
