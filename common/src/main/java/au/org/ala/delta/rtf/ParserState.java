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

public class ParserState {
	
	public CharacterAttributes CharacterAttributes;
	public ParagraphAttributes ParagraphAttributes;
	public SectionAttributes SectionAttributes;
	public DocumentAttributes DocumentAttributes;
	public DestinationState rds;
	public ParserInternalState ris;
	
	public ParserState() {
		CharacterAttributes = new CharacterAttributes();
		ParagraphAttributes = new ParagraphAttributes();
		SectionAttributes = new SectionAttributes();
		DocumentAttributes = new DocumentAttributes();
		rds = DestinationState.Normal;
		ris = ParserInternalState.Normal;
	}
	
	public ParserState(ParserState other) {
		rds = other.rds;
		ris = other.ris;
		CharacterAttributes = new CharacterAttributes(other.CharacterAttributes);
		ParagraphAttributes = new ParagraphAttributes(other.ParagraphAttributes);
		SectionAttributes = new SectionAttributes(other.SectionAttributes);
		DocumentAttributes = new DocumentAttributes(other.DocumentAttributes);
	}
	
}
