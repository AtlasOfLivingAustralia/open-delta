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

import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.util.IntegerFunctor;

public abstract class Directive {
	
	private static Pattern VAR_PATTERN = Pattern.compile("[#]([A-Z]+)");

	private String _data;
	private String _controlWords[];

	// **

	protected Directive(String... controlWords) {
		assert controlWords.length <= 4;
		_controlWords = controlWords;
	}
	
	public String getData() {
		return _data;
	}

	public String[] getControlWords() {
		return _controlWords;
	}

	@Override
	public String toString() {
		return String.format("Directive: %s", StringUtils.join(_controlWords, " "));
	}
	
	/**
	 * Directives must override this, otherwise they will fail!
	 * @param context
	 * @param data
	 */
	public void process(DeltaContext context, String data) throws Exception {
		throw new NotImplementedException();
	}
	
	protected String replaceVariables(DeltaContext context, String str) {
		String result = str;
		Matcher m = VAR_PATTERN.matcher(str);	
		while (m.find()) {
			String varname = m.group(1);
			String value = context.getVariable(varname, "#" + varname).toString();
			result = result.replaceAll("[#]" + varname, value);			
		}
		return result;		
	}
	
	protected void startFile(DeltaContext context, PrintStream stream) {
		if (stream != null) {
			String credits = context.getCredits(); 
			if (StringUtils.isNotEmpty(credits)) {
				stream.println(credits);
			}
			String heading = (String) context.getVariable("HEADING", null);
			if (heading != null) {
				stream.println(heading);
			}
			
			for (String message : context.getErrorMessages()) {
				stream.println(message);
			}
			stream.println();
		}
	}

	public Object getName() {
		return StringUtils.join(_controlWords, " ").toUpperCase();
	}
	
	private static Pattern RANGE_PATTERN = Pattern.compile("^([-]*\\d+)[-](\\d+)$");
	
	protected IntRange parseRange(String str) {
		Matcher m = RANGE_PATTERN.matcher(str);
		if (m.matches()) {
			int lhs = Integer.parseInt(m.group(1));
			int rhs = Integer.parseInt(m.group(2));
			return new IntRange(lhs, rhs);
		} else {
			return new IntRange(Integer.parseInt(str));
		}
	}
	
	protected void forEach(IntRange range, DeltaContext context, IntegerFunctor func) {
		for (int i = range.getMinimumInteger(); i <= range.getMaximumInteger(); ++i) {
			if (func != null) {
				func.invoke(context, i);
			}
		}
	}

}
