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

import java.io.File;
import java.util.List;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.dist.directives.DistDirectiveFileParser;
import au.org.ala.delta.dist.io.DistItemsFile;
import au.org.ala.delta.dist.io.DistOutputWriter;
import au.org.ala.delta.dist.io.PhylipFormatOutputWriter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.IncludeExcludeDataSetFilter;

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
			fileName = "dist";
		}
		else {
			fileName = args[0];
		}
		
		return new File(fileName);
	}
	
	public DIST(File input) throws Exception {
		this(new DistContext(), input);
	}
	
	public DIST(DistContext context, File input) throws Exception {
		_context = new DistContext();
		_itemsFileRead = false;
		DistDirectiveFileParser parser = DistDirectiveFileParser.createInstance();
		parser.registerObserver(this);
		parser.parse(input, _context);
		
		computeAndOutputDistanceMatrix();
	}
	

	public File getOutputFile() {
		return _context.getOutputFileSelector().getOutputFileAsFile();
	}
	
	public File getListingFile() {
		return null;
	}
	
	public File getErrorFile() {
		return null;
	}

	public void computeAndOutputDistanceMatrix() throws Exception {
		readInputFile();
		
		FilteredDataSet dataSet = new FilteredDataSet(_context, new IncludeExcludeDataSetFilter(_context));
		
		DistanceMatrixCalculator calculator = new DistanceMatrixCalculator(_context, dataSet);
		DistanceMatrix matrix = calculator.calculateDistanceMatrix();
		
		DistOutputWriter outputWriter = getOutputWriter(dataSet);
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

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
		
		if (directive instanceof IncludeCharacters ||
			directive instanceof ExcludeCharacters ||
			directive instanceof IncludeItems ||
			directive instanceof ExcludeItems) {
			readInputFile();
		}
	}

	private DistOutputWriter getOutputWriter(FilteredDataSet dataSet) {
		if (_context.isPhylipFormat()) {
			return new PhylipFormatOutputWriter(_context, dataSet);
		}
		else {
			return new DistOutputWriter(_context, dataSet);
		}
	}
	
	
	
	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> d, Exception ex) {
		 ParsingContext pc = context.getCurrentParsingContext();
	        if (pc.getFile() != null) {
	            Logger.error(String.format("Exception occured trying to process directive: %s (%s %d:%d)", d.getName(), pc.getFile().getName(),
	                    pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()));
	            Logger.error(ex);
	        } else {
	            Logger.error(String.format("Exception occured trying to process directive: %s (%d:%d)", d.getName(), pc.getCurrentDirectiveStartLine(),
	                    pc.getCurrentDirectiveStartOffset()));
	            Logger.error(ex);
	        }
	        
		
	}

	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {}

	public void finishedProcessing() {}
	
	public List<File> getOutputFiles() {
		return _context.getOutputFileSelector().getOutputFiles();
	}
}
