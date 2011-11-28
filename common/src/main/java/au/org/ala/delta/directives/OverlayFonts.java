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

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;


public class OverlayFonts extends AbstractInternalDirective {

	public OverlayFonts() {
		super("overlay", "fonts");
	}
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args.addTextArgument(data);
	}

	@Override
	public void process(DeltaContext context,
			DirectiveArguments directiveArguments) throws Exception {
		String args = directiveArguments.getFirstArgumentText();
		FontInfoParser parser = new FontInfoParser(context, new StringReader(args));
		parser.parse();
		
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

	
	class FontInfoParser extends IntegerTextListParser {
		
		private FontInfo _fontInfo;
		
		public FontInfoParser(DeltaContext context, Reader reader) {
			super(context, reader);
		}

		@Override
		protected void readSingle() throws ParseException {
			
			IntRange ids = readId();
			String comment = readOptionalComment();
			
			_fontInfo = new FontInfo();
			_fontInfo.comment = comment;
			
			readText();
			
			ImageSettings settings = getDeltaContext().getDataSet().getImageSettings();
			if (settings == null) {
				settings = new ImageSettings();
				getDeltaContext().getDataSet().setImageSettings(settings);
			}
			
			int id = ids.getMinimumInteger();
			addFont(settings, id);
		}

		private void addFont(ImageSettings settings, int id) {
			OverlayFontType fontType = OverlayFontType.fromOrdinal(id-1);
			
			switch (fontType) {
			case OF_BUTTON:
				settings.setDefaultButtonFontInfo(_fontInfo);
				break;
			case OF_DEFAULT:
				settings.setDefaultFontInfo(_fontInfo);
				break;
			case OF_FEATURE:
				settings.setDefaultFeatureFontInfo(_fontInfo);
				break;
			}
		}
	
		@Override
		protected String readText() throws ParseException {
			
			skipWhitespace();
			
			// Any of the digits are optional.
			if (Character.isDigit(_currentChar)) {
				_fontInfo.size = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				_fontInfo.weight = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				_fontInfo.italic = readInteger() != 0;
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				_fontInfo.pitch = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				_fontInfo.family = readInteger();
				skipWhitespace();
			}
			if (Character.isDigit(_currentChar)) {
				_fontInfo.charSet = readInteger();
				skipWhitespace();
			}
			_fontInfo.name = readToNext('#').trim();
			
			return "";
		}
		
		private DeltaContext getDeltaContext() {
			return (DeltaContext)_context;
		}
		
	}
	
}
