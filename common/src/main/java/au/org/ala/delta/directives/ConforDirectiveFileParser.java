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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.Tree;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;

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
        
        instance.register(new Show());
        instance.register(new Heading());
        instance.register(new TranslateInto());
        instance.register(new CharacterReliabilities());
        instance.register(new CharacterWeights());
        instance.register(new InputFile());
        instance.register(new ListingFile());
        instance.register(new PrintFile());
        instance.register(new NumberOfCharacters());
        instance.register(new MaximumNumberOfStates());
        instance.register(new MaximumNumberOfItems());
        instance.register(new DataBufferSize());
        instance.register(new CharacterTypes());
        instance.register(new NumbersOfStates());
        instance.register(new ImplicitValues());
        instance.register(new DependentCharacters());
        instance.register(new MandatoryCharacters());
        instance.register(new PrintWidth());
        instance.register(new Comment());
        instance.register(new ReplaceAngleBrackets());
        instance.register(new OmitCharacterNumbers());
        instance.register(new OmitInapplicables());
        instance.register(new OmitInnerComments());
        instance.register(new OmitTypeSettingMarks());
        instance.register(new CharacterForTaxonImages());
        instance.register(new ExcludeCharacters());
        instance.register(new NewParagraphAtCharacters());
        instance.register(new CharacterList());
        instance.register(new ItemDescriptions());
        
        return instance;
    }

}
