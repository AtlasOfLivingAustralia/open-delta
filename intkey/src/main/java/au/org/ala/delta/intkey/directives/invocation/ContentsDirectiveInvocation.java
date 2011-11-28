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
package au.org.ala.delta.intkey.directives.invocation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class ContentsDirectiveInvocation extends IntkeyDirectiveInvocation {

    private File file;

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // use LinkedHashMap to maintain insertion order of keys
            LinkedHashMap<String, String> contentsMap = new LinkedHashMap<String, String>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("*")) {
                    String[] tokens = line.split("\\*");
                    if (tokens.length != 2) {
                        context.getUI().displayErrorMessage("Badly formed contents file.");
                        return false;
                    }

                    contentsMap.put(tokens[0].trim(), tokens[1].trim());
                } else {
                    String[] tokens = line.split(" ");
                    if (tokens.length > 1) {
                        String description = StringUtils.join(Arrays.copyOf(tokens, tokens.length - 1), " ");
                        String fileName = tokens[tokens.length - 1];

                        // TODO massive hack here. Really should be building
                        // IntkeyDirectiveInvocation objects
                        // from both line formats and passing them to the
                        // contents directive, rather than
                        // getting the contents directive to do directive
                        // parsing.
                        String command = "FILE DISPLAY " + fileName.trim();
                        contentsMap.put(description.trim(), command);
                    } else {

                    }
                }
            }
            context.getUI().displayContents(contentsMap);
        } catch (IOException ex) {

        }

        return true;
    }
}
