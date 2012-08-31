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
package au.org.ala.delta.delfor;

import java.io.File;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParserObserver;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.slotfile.model.SlotFileDataSet;
import au.org.ala.delta.editor.slotfile.model.SlotFileRepository;

public class DELFOR implements DirectiveParserObserver {

	private DelforContext _context;
	private SlotFileDataSet _dataSet;
	
	/**
	 * @param args specifies the name of the input file to use.
	 */
	public static void main(String[] args) throws Exception {

	    
		StringBuilder credits = new StringBuilder("DELFOR version 2.15 (Java)");
		credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2010.\n");
		
		
		System.out.println(credits);
		
		File f = handleArgs(args);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", f.getName());
			return;
		}
		
		new DELFOR(f);
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
	
	public DELFOR(File input) throws Exception {
		
		SlotFileRepository dataSetRepository = new SlotFileRepository();
		_dataSet = (SlotFileDataSet) dataSetRepository.newDataSet();
		
		_context = new DelforContext(_dataSet);
		
		DelforDirectiveFileParser parser = DelforDirectiveFileParser.createInstance();
		parser.registerObserver(this);
		parser.parse(input, _context);
		
		finishedProcessing();
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

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {}
	
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

	public void finishedProcessing() {
		
		
		DirectivesFileFormatter formatter = new DirectivesFileFormatter(_context);
		try {
			formatter.reformat();
		}
		catch (DirectiveException e) {
			e.printStackTrace();
		}
	
	}
	
	
}
