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
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ImageParser;
import au.org.ala.delta.directives.validation.CharacterNumberValidator;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.model.image.ImageType;

import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

/**
 * Processes the CHARACTER IMAGES directive.
 */
public class CharacterImages extends AbstractImageDirective {

	public CharacterImages() {
		super(ImageType.IMAGE_CHARACTER, "character", "images");
	}
	
	@Override
	protected ImageParser createParser(DeltaContext context, StringReader reader) {
		return new ImageParser(context, reader, ImageType.IMAGE_CHARACTER, new CharacterNumberValidator(context));
	}
	
	
	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws ParseException {
		super.process(context, directiveArguments);
		
		List<ImageInfo> images = context.getImages(ImageType.IMAGE_CHARACTER);
		
		for (ImageInfo imageInfo : images) {
			int charNum = (Integer)imageInfo.getId();
			Character character = context.getCharacter(charNum);
			imageInfo.addOrUpdate(character);
		}
	}
	
	@Override
	public int getOrder() {
		return 4;
	}
	
}
