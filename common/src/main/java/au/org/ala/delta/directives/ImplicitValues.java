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
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.ImplicitValue;
import au.org.ala.delta.model.MultiStateCharacter;

public class ImplicitValues extends AbstractCharacterListDirective<DeltaContext, ImplicitValue> {
	
	public ImplicitValues() {
		super("implicit", "values");
	}

	@Override
	protected ImplicitValue interpretRHS(DeltaContext context, String rhs) {
		ImplicitValue ret = new ImplicitValue();
		if (rhs.contains(":")) {
			String bits[] = rhs.split(":");
			ret.setUncoded(Integer.parseInt(bits[0]));
			ret.setCoded(Integer.parseInt(bits[1]));
		} else {
			ret.setUncoded(Integer.parseInt(rhs));
		}
		return ret;
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_INTERNAL;
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, ImplicitValue rhs) {
		Character c = context.getCharacter(charIndex);
		if (c instanceof MultiStateCharacter) {
			Logger.debug("Setting implicit value for character %d: %s", charIndex, rhs);
			MultiStateCharacter msc = (MultiStateCharacter) c;
			if (rhs.getCoded() != null) {
				msc.setCodedImplicitState(rhs.getCoded());
			}
			
			msc.setUncodedImplicitState(rhs.getUncoded());
		} else {
			throw new RuntimeException("Attempted to set implicit values for non-multistate character: " + charIndex);
		}
	}

	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		args.addTextArgument(charIndex, value);
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
	
}
