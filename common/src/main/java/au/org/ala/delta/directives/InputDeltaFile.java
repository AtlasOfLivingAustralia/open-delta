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

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Similar to INPUT FILE but if the file doesn't exist in the current directory
 * it checks an environment variable then a folder.
 */
public class InputDeltaFile extends InputFileDirective {

	public static final String DELTA_ENV_VARIABLE = "DELTA";
	
	public InputDeltaFile() {
		super("input", "delta", "file");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		String fileName = args.getFirstArgumentText().trim();
		String parent = context.getCurrentParsingContext().getFile().getParent();
		File file = new File(parent, fileName);
		
		if (!file.exists()) {
			file = lookInDELTADirectory(fileName);
		}
		 
		parseFile(context, file);
	}
	
	private File lookInDELTADirectory(String fileName) {
		String deltaDir = System.getenv(DELTA_ENV_VARIABLE);
		if (StringUtils.isBlank(deltaDir)) {
			deltaDir = "DELTA";
		}
		
		return new File(FilenameUtils.concat(deltaDir, fileName));
	}

}
