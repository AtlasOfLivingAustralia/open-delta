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

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.slotfile.directive.DirectiveFunctor;

public class Directive {
	
	private String[] _name;
	private int _level;
	private int _number;
	private int _argType;
	private DirectiveFunctor _inFunc;
	private DirectiveFunctor _outFunc;
	private Class<? extends AbstractDirective<?>> _implementationClass;
	
	public Directive(String[] name, int level, int number, int argType, Class<? extends AbstractDirective<?>> implClass, DirectiveFunctor inFunc, DirectiveFunctor outFunc) {
		setName(name);
		_level = level;
		_number = number;
		_argType = argType;
		_inFunc = inFunc;
		_outFunc = outFunc;
		_implementationClass = implClass;
	}
	
	public void setName(String[] name) {
		int i = 0;
		for (i=0; i<name.length; i++) {
			if (StringUtils.isEmpty(name[i])) {
				break;
			}
		}
		_name = Arrays.copyOfRange(name, 0, i);
	}
	
	public Directive(String[] name, int level, int number, int argType, DirectiveFunctor inFunc, DirectiveFunctor outFunc) {
		this(name, level, number, argType, null, inFunc, outFunc);
	}
	
	
	public String[] getName() {
		return _name;
	}
	
	public String joinNameComponents() {
		StringBuilder name = new StringBuilder();
		for (int i=0; i<_name.length; i++) {
			
			if (StringUtils.isNotEmpty(_name[i])) {
				if (name.length() > 0) {
					name.append(" ");
				}
				name.append(_name[i]);
			}
		}
		return name.toString();
	}
	
	public int getLevel() {
		return _level;
	}
	
	public int getNumber() {
		return _number;
	}
	
	public int getArgType() {
		return _argType;
	}
	
	public DirectiveFunctor getInFunc() {
		return _inFunc;
	}
	
	public DirectiveFunctor getOutFunc() {
		return _outFunc;
	}

	public Class<? extends AbstractDirective<?>> getImplementationClass() {
		return _implementationClass;
	}
}
