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

package au.org.ala.delta.gui;

import java.io.ByteArrayOutputStream;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import au.org.ala.delta.rtf.MyRTFEditorKit;


/**
 * A JTextPane that works with RTF text by default.
 *
 */
public class RtfEditorPane extends JTextPane {

	private static final long serialVersionUID = -7907959747266618098L;


	/**
	 * Creates a new RtfEditorPane.
	 */
	public RtfEditorPane() {
		setEditorKit(new MyRTFEditorKit());
	}
	
	/**
	 * @return a String containing the text in this RtfEditorPane's document, inclusive of RTF formatting
	 * characters.  Note that this method strips the RTF header and attributes (such as font table etc).
	 * Note that the String will be ANSI encoded as that is what the editor kit supports.  Unicode characters
	 * will be encoded using the RTF unicode control characters.
	 */
	public String getRtfTextBody() {
		String rtfText = null;
		Document doc = getDocument();
		MyRTFEditorKit kit = (MyRTFEditorKit)getEditorKit();				
		ByteArrayOutputStream bos = new ByteArrayOutputStream(doc.getLength());
		try {
			kit.writeBody(bos, doc);
			rtfText = new String(bos.toByteArray()).trim();
			
		} catch (Exception ex) {
			ex.printStackTrace();
			return getPlainText();
		}
		return rtfText;
	}
	
	
	public String getPlainText() {
		Document doc = getDocument();
		try {
			return doc.getText(0, doc.getLength());
		} catch (BadLocationException e) {
			return null;
		}
	}
	
}