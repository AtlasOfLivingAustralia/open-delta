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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the PRINT HEADING directive.
 * @link http://delta-intkey.com/www/uguide.htm#_*PRINT_HEADING_
 */
public class PrintHeading extends AbstractNoArgDirective {

	public PrintHeading() {
		super("print", "heading");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String heading = context.getHeading(HeadingType.HEADING);
		au.org.ala.delta.translation.PrintFile printFile = context.getOutputFileSelector().getPrintFile();
		if (heading == null) {
            heading = "";
        }
		if (StringUtils.isNotBlank(heading)) {
			printFile.writeBlankLines(2, 0);
			printFile.outputLine(heading);
			printFile.writeBlankLines(2, 0);
		}
	}

}
