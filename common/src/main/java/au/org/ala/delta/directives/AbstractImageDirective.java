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

import java.io.StringReader;
import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.ImageParser;

/**
 * Base class for parsing directives that specify images and image overlays.
 */
public abstract class AbstractImageDirective extends AbstractInternalDirective {

	private int _imageType;
	
	public AbstractImageDirective(int imageType, String... controlWords) {
		super(controlWords);
		_imageType = imageType;
		_args = new DirectiveArguments();
	}
	

	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args.addTextArgument(data);
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws ParseException {
		
		String data = directiveArguments.getFirstArgumentText();
		
		ImageParser parser = createParser(context, new StringReader(data));
		parser.parse();
		
		context.setImages(_imageType, parser.getImageInfo());
	}
	
	protected abstract ImageParser createParser(DeltaContext context, StringReader reader);
	
}
