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
package au.org.ala.delta.directives.args;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DirectiveArguments {

	private List<DirectiveArgument<?>> _args;
	
	public DirectiveArguments() {
		_args = new ArrayList<DirectiveArgument<?>>();
	}
	
	public void add(DirectiveArgument<?> arg) {
		_args.add(arg);
	}
	
	public int size() {
		return _args.size();
	}
	
	public List<DirectiveArgument<?>> getDirectiveArguments() {
		return _args;
	}
	
	public DirectiveArgument<?> get(int index) {
		return _args.get(index);
	}
	
	public <T> void addDirectiveArgument(T id, String comment, String text) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setComment(comment);
		arg.setText(text);
		
		_args.add(arg);
	}
	
	public <T> void addTextArgument(T id, String text) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setText(text);
		
		_args.add(arg);
	}
	
	public DirectiveArgument<?> addTextArgument(String text) {
		DirectiveArgument<Integer> arg = new DirectiveArgument<Integer>();
		arg.setText(text);
		
		_args.add(arg);
		return arg;
	}
	
	public <T,V> void addNumericArgument(T id, String value) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setValue(new BigDecimal(value));
		_args.add(arg);
	}
	
	public <T> void addDirectiveArgument(List<Integer> ids) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>();
		
		for (int id : ids) {
			arg.add(id);
		}
		_args.add(arg);
	}

	public <T> DirectiveArgument<T> addDirectiveArgument(T id) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		_args.add(arg);
		return arg;
	}
	
	/**
	 * A convenience method for directives that have a single text argument.
	 * @return the text of the first argument.
	 */
	public String getFirstArgumentText() {
		return _args.get(0).getText();
	}
	
	/**
	 * A convenience method for directives that have a single numeric argument.
	 * @return the text of the first argument.
	 */
	public int getFirstArgumentValue() {
		return _args.get(0).getValue().intValue();
	}
	
	public int getFirstArgumentIdAsInt() {
		return (Integer)_args.get(0).getId();
	}
	
	
	
	public static DirectiveArguments textArgument(String text) {
		DirectiveArguments args = new DirectiveArguments();
		args.addTextArgument(text);
		return args;
	}

	public String getFirstArgumentValueAsString() {
		return _args.get(0).getValue().toString();
		
	}

	public void addValueArgument(BigDecimal value) {
		DirectiveArgument<Integer> arg = new DirectiveArgument<Integer>();
		arg.setValue(value);
		_args.add(arg);
	}

	public void addDirectiveArgument(int id, BigDecimal value) {
		DirectiveArgument<Integer> arg = new DirectiveArgument<Integer>(id);
		arg.setValue(value);
		_args.add(arg);
	}
}
