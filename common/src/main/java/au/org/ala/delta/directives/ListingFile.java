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

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class ListingFile extends AbstractTextDirective {
	
	public ListingFile() {
		super("listing", "file");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		String data = args.getFirstArgumentText();
		String filename = data.trim();
		File file = new File(context.getCurrentParsingContext().getFile().getParentFile(), filename);		
		PrintStream stream = new PrintStream(file);
		startFile(context, stream);
		context.setListingStream(stream);
		context.ListMessage(data);
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

}
