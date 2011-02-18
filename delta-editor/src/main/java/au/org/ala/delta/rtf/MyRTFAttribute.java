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
package au.org.ala.delta.rtf;

import java.io.IOException;

import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;

/**
 * This interface describes a class which defines a 1-1 mapping between an RTF keyword and a SwingText attribute.
 */
interface MyRTFAttribute {
	static final int D_CHARACTER = 0;
	static final int D_PARAGRAPH = 1;
	static final int D_SECTION = 2;
	static final int D_DOCUMENT = 3;
	static final int D_META = 4;

	/*
	 * These next three should really be public variables, but you can't declare public variables in an interface...
	 */
	/* int domain; */
	public int domain();

	/* String swingName; */
	public Object swingName();

	/* String rtfName; */
	public String rtfName();

	public boolean set(MutableAttributeSet target);

	public boolean set(MutableAttributeSet target, int parameter);

	public boolean setDefault(MutableAttributeSet target);

	/* TODO: This method is poorly thought out */
	public boolean write(AttributeSet source, RTFGenerator target, boolean force) throws IOException;

	public boolean writeValue(Object value, RTFGenerator target, boolean force) throws IOException;
}
