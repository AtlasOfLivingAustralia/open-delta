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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;

public abstract class ConforDirective extends AbstractDirective<DeltaContext> {
	
	private static Pattern VAR_PATTERN = Pattern.compile("[#]([A-Z]+)");
	
    protected ConforDirective(String... controlWords) {
        super(controlWords);
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
	
	/**
	 * Overrides process to allow empty data to be handled in a consistent manner.
	 */
	@Override
	public void process(DeltaContext context, String data) throws Exception {
		if ((data == null) || (data.trim().length() == 0) && ignoreEmptyArguments()) {
			return;
		}
		doProcess(context, data);
	}
	
	/**
	 * Subclasses of ConforDirective should override this method instead of 
	 * process.
	 * @param context the context in which the processing in being done.
	 * @param data the arguments supplied to the directive.
	 */
	protected abstract void doProcess(DeltaContext context, String data) throws Exception;
	
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
	
	/**
	 * The default behaviour of a CONFOR directive is to accept the use of a directive
	 * with no arguments.  In this case no processing in done and the result is the
	 * same as if the directive had not been used.
	 * Subclasses can override this method to return true in which case the doProcess
	 * method will be invoked.
	 */
	protected boolean ignoreEmptyArguments() {
		return true;
	}
	
}
