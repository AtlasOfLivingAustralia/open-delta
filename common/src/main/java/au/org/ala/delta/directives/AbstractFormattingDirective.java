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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.TextListParser;
import au.org.ala.delta.model.TypeSettingMark;

/**
 * Base class for handling the TYPESETTING MARKS and FORMATTING MARKS directives.
 */
public abstract class AbstractFormattingDirective extends AbstractCustomDirective {

	public AbstractFormattingDirective(String... controlWords) {
		super(controlWords);
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXTLIST;
	}
	
	
	@Override
	protected TextListParser<?> createParser(DeltaContext context, StringReader reader) {
		return new IntegerTextListParser(context, reader, false);
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
	
		boolean hasDelimiter = !StringUtils.isEmpty(args.get(0).getText());
		
		for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
			
			int id = ((Integer)arg.getId());
			String markText = arg.getText();
			
			boolean allowWhiteSpace = hasDelimiter && (markText.startsWith(" ") || markText.startsWith("\r") || markText.startsWith("\n"));
			TypeSettingMark mark = new TypeSettingMark(id, markText, allowWhiteSpace);
			processMark(context, mark);
		}
		
	}
	
	protected abstract void processMark(DeltaContext context, TypeSettingMark mark);
	
	protected String cleanWhiteSpace(String input) {
	
		Pattern p = Pattern.compile("(\\W)\\s(\\W)");
		Matcher m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		p = Pattern.compile("(\\W)\\s(\\w)");
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		p = Pattern.compile("(\\w)\\s(\\W)");
		m = p.matcher(input);
		input = m.replaceAll("$1$2");
		
		return  input;
	}
}
