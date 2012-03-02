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
package au.org.ala.delta.key.directives;

import java.util.List;

import au.org.ala.delta.directives.CharacterReliabilities;
import au.org.ala.delta.directives.Comment;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.ExcludeCharacters;
import au.org.ala.delta.directives.ExcludeItems;
import au.org.ala.delta.directives.Heading;
import au.org.ala.delta.directives.IncludeCharacters;
import au.org.ala.delta.directives.IncludeItems;
import au.org.ala.delta.directives.ItemAbundances;
import au.org.ala.delta.directives.OutputFormatHtml;
import au.org.ala.delta.directives.PrintWidth;
import au.org.ala.delta.directives.TypeSettingMarks;
import au.org.ala.delta.key.KeyContext;

public class KeyDirectiveParser extends DirectiveParser<KeyContext> {

    // Private constructor, must use factory method to get an instance
    private KeyDirectiveParser() {

    }

    public static KeyDirectiveParser createInstance() {
        KeyDirectiveParser instance = new KeyDirectiveParser();
        instance.registerDirective(new ABaseDirective());
        instance.registerDirective(new AddCharacterNumbersDirective());
        instance.registerDirective(new AllowImproperSubgroupsDirective());
        instance.registerDirective(new CharacterReliabilities());
        instance.registerDirective(new CharactersFileDirective());
        instance.registerDirective(new Comment());
        
        // No-op directive implemented to allow backward compatibility of old
        // scripts
        instance.registerDirective(new DumpDirective());
        instance.registerDirective(new ExcludeCharacters());
        instance.registerDirective(new ExcludeItems());
        instance.registerDirective(new Heading());
        instance.registerDirective(new IncludeCharacters());
        instance.registerDirective(new IncludeItems());
        instance.registerDirective(new KeyInputFileDirective());
        instance.registerDirective(new ItemAbundances());
        instance.registerDirective(new ItemsFileDirective());
        instance.registerDirective(new KeyOutputFileDirective());
        instance.registerDirective(new KeyTypesettingFileDirective());
        instance.registerDirective(new ListingFileDirective());

        // No-op directive implemented to allow backward compatibility of old
        // scripts
        instance.registerDirective(new MatrixDumpDirective());
        instance.registerDirective(new NoBracketedKeyDirective());
        instance.registerDirective(new NoTabularKeyDirective());
        instance.registerDirective(new NumberOfConfirmatoryCharactersDirective());
        instance.registerDirective(new KeyOutputDirectoryDirective());
        instance.registerDirective(new OutputFormatHtml());
        //TODO page length
        //TODO pagination of tabular key
        
        instance.registerDirective(new PresetCharactersDirective());
        instance.registerDirective(new KeyPrintCommentDirective());
        instance.registerDirective(new KeyPrintWidthDirective());
        
        instance.registerDirective(new RBaseDirective());
        instance.registerDirective(new ReuseDirective());
        instance.registerDirective(new StopAfterColumnDirective());
        
        // No-op directive implemented to allow backward compatibility of old
        // scripts
        instance.registerDirective(new StorageFactorDirective());
        instance.registerDirective(new TreatCharactersAsVariableDirective());
        instance.registerDirective(new TreatUnknownAsInapplicableDirective());
        instance.registerDirective(new TruncateTabularKeyAtDirective());
        instance.registerDirective(new TypeSettingMarks());
        instance.registerDirective(new VaryWtDirective());


        return instance;
    }

    @Override
    protected void handleUnrecognizedDirective(KeyContext context, List<String> controlWords) {
        // System.err.println("Unrecognized directive " +
        // StringUtils.join(controlWords, " "));
    }

}
