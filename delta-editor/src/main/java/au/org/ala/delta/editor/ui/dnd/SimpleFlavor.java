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
package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * Overrides DataFlavor to allow comparisons to succeed if one flavor is a parent class of
 * the other.
 * (This is necessary because our TransferHandler deals with the Character class but specific
 * dragged Characters are subclasses of Character).
 */
public class SimpleFlavor extends DataFlavor {

	public SimpleFlavor(Class<?> flavourClass, String className) {
		super(flavourClass, className);
	}
	
	public boolean equals(DataFlavor flavour) {
		return getRepresentationClass().isAssignableFrom(flavour.getRepresentationClass());
	}
}
