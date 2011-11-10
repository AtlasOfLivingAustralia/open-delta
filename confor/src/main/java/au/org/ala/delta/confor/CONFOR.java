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
import au.org.ala.delta.directives.ConforDirectiveParserObserver;
import au.org.ala.delta.directives.validation.DirectiveException;

public class CONFOR {

	private DeltaContext _context;
	
	/**
	 * @param args specifies the name of the input file to use.
	 */
	public static void main(String[] args) throws Exception {

		System.out.println(credits());
		
		File f = handleArgs(args);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", f.getName());
			return;
		}
		
		new CONFOR(f);
	}
	
	private static String credits() {
		String eol = System.getProperty("line.separator");
		StringBuilder credits = new StringBuilder("CONFOR version 3.00 (Java)");
		credits.append(eol).append(eol).append("M. J. Dallwitz, T.A. Paine and E.J. Zurcher");
		credits.append(eol).append(eol).append("CSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia");
		credits.append(eol).append("Phone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
		credits.append(eol).append(eol).append("Java edition ported by the Atlas of Living Australia, 2010.").append(eol);
		return credits.toString();
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
		_context = new DeltaContext();
		_context.setCredits(credits());
		ConforDirectiveFileParser p = ConforDirectiveFileParser.createInstance();
		ConforDirectiveParserObserver observer = new ConforDirectiveParserObserver(_context); 
		p.registerObserver(observer);
		try {
			p.parse(input, _context);
		}
		catch (DirectiveException e) {
			// Ignore, this just allows us to terminate parsing early.
		}
		observer.finishedProcessing();
	}
	
	public File getPrintFile() {
		return _context.getOutputFileSelector().getPrintFileAsFile();
	}
	
	public File getIndexFile() {
		return _context.getOutputFileSelector().getIndexFileAsFile();
	}
	
	public File getListingFile() {
		return null;
	}
	
	public File getErrorFile() {
		return null;
	}

}
