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
package au.org.ala.delta.editor.slotfile;

import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/** 
 * Represents a specific instance of a stored directive, complete with
 * arguments.
 */
public class DirectiveInstance {

	private Directive _directive;
	private DirectiveArguments _args;
	private boolean _commented;
	private DirectiveType _type;
	
	public DirectiveInstance(Directive directive, DirectiveArguments args) {
		_directive = directive;
		_args = args;
	}
	
	public Directive getDirective() {
		return _directive;
	}
	
	public DirectiveArguments getDirectiveArguments() {
		return _args;
	}
	
	public void setCommented(boolean commented) {
		_commented = commented;
	}
	
	public boolean isCommented() {
		return _commented;
	}
	
	public DirectiveType getType() {
		return _type;
	}
	
	public void setDirectiveType(DirectiveType type) {
		_type = type;
	}
}
