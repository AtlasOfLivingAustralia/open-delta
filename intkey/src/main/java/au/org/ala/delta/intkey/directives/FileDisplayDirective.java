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
package au.org.ala.delta.intkey.directives;

import java.io.File;
import java.net.URL;
import java.util.Arrays;

import au.org.ala.delta.intkey.directives.invocation.FileDisplayDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.util.Utils;

public class FileDisplayDirective extends IntkeyDirective {

    public FileDisplayDirective() {
        super(false, "file", "display");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String filePath = data;
        FileDisplayDirectiveInvocation invoc = null;

        if (filePath == null || filePath.startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            File file = context.getDirectivePopulator().promptForFile(Arrays.asList(new String[] { "rtf", "doc", "htm", "html", "wav", "ink" }), "Files (*.rtf, *.doc, *.htm, *.wav, *.ink)", false);
            invoc = new FileDisplayDirectiveInvocation(file.toURI().toURL(), file.getName());
        } else if (filePath.startsWith("http://")) {
            try {
                URL url = new URL(filePath);
                invoc = new FileDisplayDirectiveInvocation(url, filePath);
            } catch (Exception ex) {

            }
        } else {
            File file = Utils.createFileFromPath(filePath, context.getDatasetDirectory());
            invoc = new FileDisplayDirectiveInvocation(file.toURI().toURL(), file.getName());
        }

        return invoc;
    }

}
