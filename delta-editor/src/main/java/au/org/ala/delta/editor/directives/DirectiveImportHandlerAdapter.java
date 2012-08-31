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

import java.util.List;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;

/**
 * Provides an empty implementation of the DirectiveImportHandler.
 */
public class DirectiveImportHandlerAdapter implements DirectiveImportHandler {

	@Override
	public void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) {
	}

	@Override
	public void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) {
	}

	@Override
	public void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
	}

	@Override
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> d,
			Exception ex) {
	}

	
	
	@Override
	public void handleDirectiveProcessingException(ImportContext context, AbstractDirective<ImportContext> d,
			Exception ex) {
		
	}

	@Override
	public void finishedProcessing() {}
}
