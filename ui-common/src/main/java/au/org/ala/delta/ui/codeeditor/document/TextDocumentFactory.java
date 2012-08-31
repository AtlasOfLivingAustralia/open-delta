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

import java.util.HashMap;
import java.util.Map;

public class TextDocumentFactory {

	/** Holds the document type map. */

	@SuppressWarnings("rawtypes")
	private static final Map<String, Class> documentTypes = new HashMap<String, Class>();

	/**
	 * No one is allowed to instanciate this class!!
	 */
	private TextDocumentFactory() {
	}

	/**
	 * Registers the given document type.
	 * 
	 * @param documentClass
	 *            The document class.
	 */
	@SuppressWarnings("rawtypes")
	public static final void registerDocumentType(Class documentClass) {
		try {
			TextDocument doc = (TextDocument) documentClass.newInstance();
			documentTypes.put(doc.getMimeType(), documentClass);
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Unregisters the given document type.
	 * 
	 * @param mimeType
	 *            The document type.
	 */
	public static final void unregisterDocumentType(String mimeType) {
		documentTypes.remove(mimeType);
	}

	/**
	 * Unregisters the given document type.
	 * 
	 * @param documentClass
	 *            The document class.
	 */
	@SuppressWarnings("rawtypes")
	public static final void unregisterDocumentType(Class documentClass) {
		try {
			TextDocument doc = (TextDocument) documentClass.newInstance();
			documentTypes.remove(doc.getMimeType());
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Generates an instance for the given mimeType.
	 * 
	 * @param mimeType
	 *            The document type.
	 * @return The generated instance.
	 */
	@SuppressWarnings("rawtypes")
	public static final TextDocument createDocument(String mimeType) {
		try {
			Class cls = (Class) documentTypes.get(mimeType);
			return (TextDocument) cls.newInstance();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	static {
		// registering default document types
		registerDocumentType(PlainTextDocument.class);
		registerDocumentType(XmlTextDocument.class);
		registerDocumentType(ConforTextDocument.class);
	}
}
