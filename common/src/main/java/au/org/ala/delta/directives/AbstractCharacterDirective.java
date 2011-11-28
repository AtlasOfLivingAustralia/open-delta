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
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractCharacterDirective extends AbstractDirective<DeltaContext> {

	private int _characterNum = -1;

	protected AbstractCharacterDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		DirectiveArguments args = new DirectiveArguments();
		args.addDirectiveArgument(_characterNum);
		return args;
	}

	public void process(DeltaContext context, String data) throws Exception {
		_characterNum = Integer.parseInt(data);

		processCharacter(context, _characterNum);
	}

	public abstract void processCharacter(DeltaContext context, int character) throws Exception;

}
