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
package au.org.ala.delta.dist.directives;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.CharacterWeights;
import au.org.ala.delta.directives.Comment;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.ListingFile;
import au.org.ala.delta.directives.OutputFile;
import au.org.ala.delta.directives.ParsingContext;
import au.org.ala.delta.dist.DistContext;

/**
 * A directive file is a text file containing one or more directives. Directives
 * start with an '*' followed by up to four alphanumeric components of the
 * directive name delimited by a space,followed by the data (if any) of the
 * directive, and is terminated either by the beginning of a new directive, or
 * the end of the file.
 */
public class DistDirectiveFileParser extends DirectiveParser<DistContext> {

    //Private constructor, must use factory method to get an instance
    private DistDirectiveFileParser() {
        
    }
    
    public static DistDirectiveFileParser createInstance() {
        DistDirectiveFileParser instance = new DistDirectiveFileParser();
        
        instance.registerDirective(new CharacterWeights());
        instance.registerDirective(new Comment());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new ExcludeItems());
        instance.registerDirective(new IncludeCharacters());
        instance.registerDirective(new IncludeItems());
        instance.registerDirective(new ItemsFile());
        instance.registerDirective(new ListingFile());
        instance.registerDirective(new Log());
        instance.registerDirective(new MatchOverlap());
        instance.registerDirective(new MaximumItemsInMemory());
        instance.registerDirective(new MinimumNumberOfComparisons());
        instance.registerDirective(new NamesFile());
        instance.registerDirective(new OutputFile());
        instance.registerDirective(new PhylipFormat());
        
        return instance;
    }

    @Override
    protected void handleUnrecognizedDirective(DistContext context, List<String> controlWords) {
        ParsingContext pc = context.getCurrentParsingContext();
        if (pc.getFile() != null) {
            Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
                    pc.getCurrentDirectiveStartOffset());
        } else {
            Logger.log("Unrecognized Directive: %s at offset %d:%d", StringUtils.join(controlWords, " "), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset());
        }
    }

}
