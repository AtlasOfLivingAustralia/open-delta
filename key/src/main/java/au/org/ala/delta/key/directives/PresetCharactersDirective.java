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

import java.io.StringReader;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.PresetCharactersParser;
import au.org.ala.delta.key.KeyContext;

public class PresetCharactersDirective extends AbstractTextDirective {

    public PresetCharactersDirective() {
        super("preset", "characters");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        String data = directiveArguments.getFirstArgumentText().trim();
        StringReader reader = new StringReader(data);
        PresetCharactersParser parser = new PresetCharactersParser(context, reader);
        parser.parse();
        addPresetCharacters((KeyContext) context, parser.getDirectiveArgs());
    }

    protected void addPresetCharacters(KeyContext context, DirectiveArguments args) {
        for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
            List<Integer> argValues = arg.getDataList();
            int characterNumber = (Integer) arg.getId();
            int columnNumber = argValues.get(0);
            int groupNumber = argValues.get(1);
            context.setPresetCharacter(characterNumber, columnNumber, groupNumber);
        }
    }

}
