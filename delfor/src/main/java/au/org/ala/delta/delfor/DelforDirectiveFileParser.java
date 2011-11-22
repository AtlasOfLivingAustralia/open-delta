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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.delfor.directives.ControlPhrases;
import au.org.ala.delta.delfor.directives.InputFile;
import au.org.ala.delta.delfor.directives.NewCharacterOrder;
import au.org.ala.delta.delfor.directives.NewLineForAttributes;
import au.org.ala.delta.delfor.directives.NewStateOrders;
import au.org.ala.delta.delfor.directives.OutputFile;
import au.org.ala.delta.delfor.directives.Reformat;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.CharacterTypes;
import au.org.ala.delta.directives.ChineseFormat;
import au.org.ala.delta.directives.Comment;
import au.org.ala.delta.directives.DataBufferSize;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.ErrorFile;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.InputDeltaFile;
import au.org.ala.delta.directives.ItemDescriptions;
import au.org.ala.delta.directives.ListingFile;
import au.org.ala.delta.directives.MandatoryCharacters;
import au.org.ala.delta.directives.MaximumNumberOfItems;
import au.org.ala.delta.directives.MaximumNumberOfStates;
import au.org.ala.delta.directives.NumberOfCharacters;
import au.org.ala.delta.directives.NumbersOfStates;
import au.org.ala.delta.directives.OutputWidth;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.directives.Show;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * A directive file is a text file containing one or more directives. Directives
 * start with an '*' followed by up to four alphanumeric components of the
 * directive name delimited by a space,followed by the data (if any) of the
 * directive, and is terminated either by the beginning of a new directive, or
 * the end of the file.
 * 
 * @author baird
 * 
 */
public class DelforDirectiveFileParser extends DirectiveParser<DeltaContext> {

    //Private constructor, must use factory method to get an instance
    private DelforDirectiveFileParser() {
        
    }
    
    public static DelforDirectiveFileParser createInstance() {
        DelforDirectiveFileParser instance = new DelforDirectiveFileParser();
        
        instance.registerDirective(new CharacterTypes());
        instance.registerDirective(new ChineseFormat());
        instance.registerDirective(new Comment());
        instance.registerDirective(new ControlPhrases());
        instance.registerDirective(new DataBufferSize());
       
        instance.registerDirective(new ErrorFile());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new ExcludeItems());
        
        instance.registerDirective(new IncludeCharacters());
        instance.registerDirective(new IncludeItems());     
        instance.registerDirective(new InputDeltaFile());
        instance.registerDirective(new InputFile());
        instance.registerDirective(new ItemDescriptions());
       
        instance.registerDirective(new ListingFile());
        
        instance.registerDirective(new MandatoryCharacters());
        instance.registerDirective(new MaximumNumberOfItems());
        instance.registerDirective(new MaximumNumberOfStates());
      
        instance.registerDirective(new NewCharacterOrder());
        instance.registerDirective(new NewLineForAttributes());
        instance.registerDirective(new NewStateOrders());
        instance.registerDirective(new NumberOfCharacters());
        instance.registerDirective(new NumbersOfStates());
        
        instance.registerDirective(new OutputFile());
        instance.registerDirective(new OutputWidth());       
        
        instance.registerDirective(new Reformat());
        
        instance.registerDirective(new Show());
        
        
        return instance;
    }
    
    /** Tracks the order of the most recently processed directive. */
    private int _currentOrder;

    @Override
    protected void handleUnrecognizedDirective(DeltaContext context, List<String> controlWords) {
        ParsingContext pc = context.getCurrentParsingContext();
        if (pc.getFile() != null) {
            Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
                    pc.getCurrentDirectiveStartOffset());
        } else {
            Logger.log("Unrecognized Directive: %s at offset %d:%d", StringUtils.join(controlWords, " "), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset());
        }
    }

	@Override
	protected void executeDirective(AbstractDirective<DeltaContext> directive, String data, DeltaContext context)
			throws DirectiveException {
		
		int order = directive.getOrder();
		// Directives with order 0 can appear anywhere
		if (order > 0 && order < _currentOrder) {
			throw DirectiveError.asException(DirectiveError.Error.DIRECTIVE_OUT_OF_ORDER, 0);
		}
		_currentOrder = order;
		super.executeDirective(directive, data, context);
	}
    
    
    
}
