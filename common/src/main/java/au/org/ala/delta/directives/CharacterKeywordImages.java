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
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.model.image.ImageType;

import java.io.StringReader;

/**
 * Processes the CHARACTER KEYWORD IMAGES directive.
 */
public class CharacterKeywordImages extends AbstractImageDirective {

	public CharacterKeywordImages() {
		super(ImageType.IMAGE_CHARACTER_KEYWORD, "character", "keyword", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_CHARACTER_KEYWORD, null);
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
}
