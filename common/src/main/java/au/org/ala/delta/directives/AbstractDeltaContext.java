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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import au.org.ala.delta.directives.validation.DirectiveError;

public abstract class AbstractDeltaContext {
    
    protected Stack<ParsingContext> _parsingContexts = new Stack<ParsingContext>();
    
    private List<DirectiveError> _errors = new ArrayList<DirectiveError>();
    
    private List<Class<? extends AbstractDirective<?>>> _processedDirectives = new ArrayList<Class<? extends AbstractDirective<?>>>();
    
    public ParsingContext getCurrentParsingContext() {
        if (_parsingContexts.size() > 0) {
            return _parsingContexts.peek();
        }
        return null;
    }

    public ParsingContext newParsingContext() {
        ParsingContext context = new ParsingContext();
        _parsingContexts.push(context);
        return context;
    }

    public ParsingContext endCurrentParsingContext() {
        if (_parsingContexts.size() > 0) {
            return _parsingContexts.pop();
        }
        return null;
    }
    
    public Charset getFileEncoding() {
    	return Charset.forName("Cp1252");
    }
    
    public Charset getOutputFileEncoding() {
    	return Charset.forName("utf-8");
    }

	public void addError(DirectiveError error) {
		_errors.add(error);
	}
	
	public void addError(DirectiveError.Error error, int position, Object... args) {
		_errors.add(new DirectiveError(error, position, args));
	}
	
	public List<DirectiveError> getErrors() {
		return new ArrayList<DirectiveError>(_errors);
	}
	
	public void clearErrors() {
		_errors.clear();
	}
	
	public void addProcessedDirective(Class<? extends AbstractDirective<?>> dirClass) {
		_processedDirectives.add(dirClass);
	}
	
	public boolean hasProcessedDirective(Class<? extends AbstractDirective<?>> dirClass) {
		return _processedDirectives.contains(dirClass);
	}

}
