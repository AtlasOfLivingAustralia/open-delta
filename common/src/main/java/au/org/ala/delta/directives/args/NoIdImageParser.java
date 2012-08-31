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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.image.ImageInfo;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Parses the arguments to the STARTUP IMAGES, CHARACTER KEY IMAGES and
 * TAXON KEY IMAGES directives.
 */
public class NoIdImageParser extends ImageParser {
	
	public NoIdImageParser(DeltaContext context, Reader reader, int imageType) {
		super(context, reader, imageType, null);
	}
	
	@Override
	public void parse() throws ParseException {
		
		_args = new DirectiveArguments();
		_imageInfo = new ArrayList<ImageInfo>();
		readNext();
		skipWhitespace();
		
		int id = 0;
		while (_currentInt > 0) {
	
			readImage(id);
			skipWhitespace();
		}
		
	}
}
