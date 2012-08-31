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

import au.org.ala.delta.directives.validation.DirectiveException;

public interface DirectiveParserObserver {

    void preProcess(AbstractDirective<? extends AbstractDeltaContext> directive, String data) throws DirectiveException;
    void postProcess(AbstractDirective<? extends AbstractDeltaContext> directive) throws DirectiveException;
	void finishedProcessing();
	public void handleDirectiveProcessingException(AbstractDeltaContext context, AbstractDirective<? extends AbstractDeltaContext> directive,Exception ex) throws DirectiveException;
    
}
