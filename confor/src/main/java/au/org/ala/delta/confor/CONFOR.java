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
package au.org.ala.delta.confor;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.translation.AbstractDataSetTranslator;
import au.org.ala.delta.translation.DataSetTranslatorFactory;

public class CONFOR {

	/**
	 * @param args specifies the name of the input file to use.
	 */
	public static void main(String[] args) throws Exception {

	    
		StringBuilder credits = new StringBuilder("CONFOR version 3.00 (Java)");
		credits.append("\n\nM. J. Dallwitz, T.A. Paine and E.J. Zurcher");
		credits.append("\n\nCSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia\nPhone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
		credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2010.\n");
		
		
		System.out.println(credits);
		
		File f = handleArgs(args);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", f.getName());
			return;
		}
		
		new CONFOR(f);
	}
	
	private static File handleArgs(String[] args) throws Exception {
		String fileName;
		if (args.length == 0) {
			fileName = askForFileName();
		}
		else {
			fileName = args[0];
		}
		
		return new File(fileName);
	}
	
	private static String askForFileName() throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		System.out.println();
		System.out.print("Enter the full pathname of the directives file: ");
		String fileName = in.readLine();
		
		return fileName;
	}
	
	public CONFOR(File input) throws Exception {
		DeltaContext context = new DeltaContext();
		ConforDirectiveFileParser p = ConforDirectiveFileParser.createInstance();
		
		p.parse(input, context);
		
		DataSetTranslatorFactory factory = new DataSetTranslatorFactory();
		AbstractDataSetTranslator translator = factory.createTranslator(context);
		
		translator.translate();
		
	}

}
