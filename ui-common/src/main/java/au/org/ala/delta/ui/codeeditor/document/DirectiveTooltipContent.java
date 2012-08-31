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
package au.org.ala.delta.ui.codeeditor.document;


public class DirectiveTooltipContent {
	
	private String _name;
	private String _description;
	private int _id;
	
	public DirectiveTooltipContent(String name, String description, int id) {
		_name = name;
		_description = description;
		_id = id;
	}
	
	@Override
	public String toString() {
		return String.format("<html><h1>%s</h1><p>%s</p></html>", _name, _description ); 
	}
	
	public String getName() {
		return _name;
	}
	
	public String getDescription() {		
		return _description;
	}
	
	public int getId() {
		return _id;
	}
	
}
