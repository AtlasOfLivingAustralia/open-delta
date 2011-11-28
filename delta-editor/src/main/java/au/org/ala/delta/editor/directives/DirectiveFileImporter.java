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
package au.org.ala.delta.editor.directives;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;


/**
 * The DirectiveFileImporter is responsible for parsing a directives file
 * and importing each of the directives in a single file into a DeltaDataSet.
 */
public class DirectiveFileImporter extends DirectiveParser<ImportContext> {

	private DirectiveImportHandler _handler;
	private Directive[] _directives; 
	private boolean _importFailed;
	
	public DirectiveFileImporter(DirectiveImportHandler handler, Directive[] directives) {
		getDirectiveRegistry().setNumberOfSignificantCharacters(-1);
		_handler = handler;
		_directives = directives;
		_importFailed = false;
		registerDirectives(directives);
		registerObserver(handler);
	}
	
	
	@Override
	protected void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
		_importFailed = true;
		
		_handler.handleUnrecognizedDirective(context, controlWords);
	}
	
	

	@Override
	protected void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d, Exception ex) {
		_importFailed = true;
		ex.printStackTrace();
		_handler.handleDirectiveProcessingException(context, d, ex);
	}
	
	@SuppressWarnings({"rawtypes", "unchecked"})
    protected void doProcess(ImportContext context, AbstractDirective d, String dd)
			throws ParseException, Exception {
		
		Directive directive = typeOf(d);
		
    	d.parse(context, dd);
    	DirectiveInstance instance = new DirectiveInstance(directive, d.getDirectiveArgs());
		context.getDirectiveFile().add(instance);
		
		if (d.getArgType() == DirectiveArgType.DIRARG_INTERNAL) {
			d.process(context, d.getDirectiveArgs());
		}
	}

	/**
	 * Most directive imports can be handled by simply parsing and adding to
	 * the dataset (which is the responsibility of the ImportDirective).
	 * Directives of type DIRARG_INTERNAL are actually stored as part of the
	 * data model so these directives are parsed and processed. (the 
	 * processing step updates the data set).
	 * @param directives the directives supported by this DirectiveFileImporter.
	 * Depending of the type of the directives file (CONFOR/INTKEY/KEY/DIST)
	 * a different set of directives will be registered.
	 */
	private void registerDirectives(Directive[] directives) {
		Directive directive = null;
    	try {
    		
	    	for (int i=0; i<directives.length; i++) {
	    		directive = directives[i];
	    		if (directive.getArgType() == DirectiveArgType.DIRARG_INTERNAL) {
	    			
	    			registerInternalDirective(directive);
	    		}
	    		else {
	    			registerDirective(new ImportDirective(directive));
	    		}
	    	}
    	}
    	catch (Exception e) {
    		throw new RuntimeException("Failed to find directive for: "+directive.joinNameComponents(), e);
    	}
	}

	/**
	 * Instantiates an instance of the CONFOR directive class to handle the
	 * parsing and processing of the directive.  The CHARACTER LIST and
	 * ITEM DESCRIPTIONS directives are special cases as the processing of
	 * these directives normally result in a CONFOR action to be taken
	 * (e.g. a translation).
	 * @param directive the directive to register.
	 */
	private void registerInternalDirective(Directive directive) throws InstantiationException, IllegalAccessException {
		
		Class<? extends AbstractDirective<?>> dirClass = directive.getImplementationClass();
		registerDirective(dirClass.newInstance());
	}
	
	public Directive typeOf(AbstractDirective<?> directive) {
		String[] directiveName = directive.getControlWords();
		for (Directive dir : _directives) {
			if (directiveName.length == dir.getName().length) {
				
				boolean match = true;
				for (int i=0; i<directiveName.length; i++) {
					if (!directiveName[i].toUpperCase().equals(dir.getName()[i].toUpperCase())) {
						match = false;
					}
				}
				if (match) {
					return dir;
				}
			}
		}
		throw new RuntimeException("Cannot find a directive matching: "+Arrays.asList(directiveName));
	}
	
	public boolean success() {
		return !_importFailed;
	}
}
