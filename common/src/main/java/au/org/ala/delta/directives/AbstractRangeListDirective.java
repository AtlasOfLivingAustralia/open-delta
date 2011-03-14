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

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.util.IntegerFunctor;

public abstract class AbstractRangeListDirective extends Directive {
	
	protected AbstractRangeListDirective(String ...controlWords) {
		super(controlWords);
	}

	@Override
	public void process(DeltaContext context, String data) throws Exception {
		// data is a space separate list of ranges...
		String[] ranges = data.split(" ");
		for (String range : ranges) {
			IntRange r = parseRange(range);
			forEach(r, context, new IntegerFunctor() {
				@Override
				public void invoke(DeltaContext context, int number) {
					processNumber(context, number);
				}
			});
		}

	}

	protected abstract void processNumber(DeltaContext context, int number);

}
