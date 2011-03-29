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

import au.org.ala.delta.DeltaContext;

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
public class ConforDirectiveFileParser extends DirectiveParser<DeltaContext> {

    //Private constructor, must use factory method to get an instance
    private ConforDirectiveFileParser() {
        
    }
    
    public static ConforDirectiveFileParser createInstance() {
        ConforDirectiveFileParser instance = new ConforDirectiveFileParser();
        
        instance.registerDirective(new Show());
        instance.registerDirective(new Heading());
        instance.registerDirective(new TranslateInto());
        instance.registerDirective(new CharacterReliabilities());
        instance.registerDirective(new CharacterWeights());
        instance.registerDirective(new InputFile());
        instance.registerDirective(new ListingFile());
        instance.registerDirective(new PrintFile());
        instance.registerDirective(new NumberOfCharacters());
        instance.registerDirective(new MaximumNumberOfStates());
        instance.registerDirective(new MaximumNumberOfItems());
        instance.registerDirective(new DataBufferSize());
        instance.registerDirective(new CharacterTypes());
        instance.registerDirective(new NumbersOfStates());
        instance.registerDirective(new ImplicitValues());
        instance.registerDirective(new DependentCharacters());
        instance.registerDirective(new MandatoryCharacters());
        instance.registerDirective(new PrintWidth());
        instance.registerDirective(new Comment());
        instance.registerDirective(new ReplaceAngleBrackets());
        instance.registerDirective(new OmitCharacterNumbers());
        instance.registerDirective(new OmitInapplicables());
        instance.registerDirective(new OmitInnerComments());
        instance.registerDirective(new OmitTypeSettingMarks());
        instance.registerDirective(new CharacterForTaxonImages());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new NewParagraphAtCharacters());
        instance.registerDirective(new CharacterList());
        instance.registerDirective(new ItemDescriptions());
        
        return instance;
    }

}
