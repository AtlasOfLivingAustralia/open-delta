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
package au.org.ala.delta.dist;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.dist.directives.DistDirectiveFileParser;
import au.org.ala.delta.dist.io.DistItemsFile;
import au.org.ala.delta.dist.io.DistOutputWriter;

public class DIST implements DirectiveParserObserver {

	private DistContext _context;
	private boolean _itemsFileRead;
	
	/**
	 * @param args specifies the name of the input file to use.
	 */
	public static void main(String[] args) throws Exception {

	    
		StringBuilder credits = new StringBuilder("DIST version 2.05 (Java)");
		credits.append("\n\nM. J. Dallwitz, T.A. Paine");
		credits.append("\n\nCSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia\nPhone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
		credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2010.\n");
		
		
		System.out.println(credits);
		
		File f = handleArgs(args);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", f.getName());
			return;
		}
		
		new DIST(f);
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
	
	public DIST(File input) throws Exception {
		_context = new DistContext();
		_itemsFileRead = false;
		DistDirectiveFileParser parser = DistDirectiveFileParser.createInstance();
		parser.registerObserver(this);
		parser.parse(input, _context);
		
		computeAndOutputDistanceMatrix();
	}
	

	public File getOutputFile() {
		return _context.getOutputFileSelector().getPrintFileAsFile();
	}
	
	public File getNamesFile() {
		return _context.getOutputFileSelector().getIndexFileAsFile();
	}
	
	public File getListingFile() {
		return null;
	}
	
	public File getErrorFile() {
		return null;
	}

	public void computeAndOutputDistanceMatrix() throws Exception {
		readInputFile();
		
		DistanceMatrixCalculator calculator = new DistanceMatrixCalculator(_context);
		DistanceMatrix matrix = calculator.calculateDistanceMatrix();
		
		DistOutputWriter outputWriter = new DistOutputWriter(_context);
		outputWriter.writeOutput(matrix);
	}

	protected void readInputFile() {
		if (!_itemsFileRead) {
			DistItemsFile itemsFile = getInputFile();
			DistItemsFileReader reader = new DistItemsFileReader(_context.getDataSet(), itemsFile, _context);
			reader.readAll();
			_itemsFileRead = true;
		}
	}
	
	private DistItemsFile getInputFile() {
		
		String itemsFileName = _context.getItemsFileName();
		itemsFileName = _context.getOutputFileManager().makeAbsolute(itemsFileName);
		DistItemsFile itemsFile = new DistItemsFile(itemsFileName);
		
		return itemsFile;
	}

	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		if (directive instanceof IncludeCharacters) {
			readInputFile();
		}
	}

	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {}

	public void finishedProcessing() {}
	
}
