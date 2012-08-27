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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.NewDatasetDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.util.Utils;

/**
 * The NEWDATASET directive - tells intkey to open the specified dataset -
 * identified by its initialization file (an ini or ink file). All the commands
 * listed in this initalization file will be executed.
 * 
 * @author ChrisF
 * 
 */
public class NewDatasetDirective extends IntkeyDirective {

    public NewDatasetDirective() {
        super(false, "newdataset");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        NewDatasetDirectiveInvocation invoc = new NewDatasetDirectiveInvocation();

        URL url;

        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        String filePath = tokens.isEmpty() ? null : tokens.get(0);

        StringBuilder stringRepresentationBuilder = new StringBuilder();
        stringRepresentationBuilder.append(getControlWordsAsString());
        stringRepresentationBuilder.append(" ");

        // If not URL is provided, prompt for a file
        if (StringUtils.isEmpty(filePath)) {
            filePath = context.getDirectivePopulator().promptForDataset();

            if (filePath == null) {
                // User cancelled, end the operation
                return null;
            }
        }

        try {
            url = Utils.parseURLOrFilePath(filePath);
            if (!Utils.checkURLValidProtocol(url)) {
                throw new IntkeyDirectiveParseException("InvalidDatasetURL.error");
            }

            stringRepresentationBuilder.append(filePath);
        } catch (MalformedURLException ex) {
            throw new IntkeyDirectiveParseException("InvalidDatasetURL.error");
        }

        invoc.setURL(url);
        invoc.setStringRepresentation(stringRepresentationBuilder.toString());

        return invoc;
    }

}
