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

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.DisplayImagesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.util.Pair;

public class DisplayImagesDirective extends IntkeyDirective {

    public DisplayImagesDirective() {
        super(true, "display", "images");
    }

    @Override
    protected BasicIntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        StringBuilder stringRepresentationBuilder = new StringBuilder();
        stringRepresentationBuilder.append(getControlWordsAsString());
        stringRepresentationBuilder.append(" ");
        
        DisplayImagesDirectiveInvocation invoc = new DisplayImagesDirectiveInvocation();

        if (StringUtils.isEmpty(data) || data.toUpperCase().startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            Pair<ImageDisplayMode, DisplayImagesReportType> settings = context.getDirectivePopulator().promptForImageDisplaySettings();

            if (settings == null) {
                // user cancelled
                return null;
            } else {
                invoc.setDisplayMode(settings.getFirst());
                invoc.setReportType(settings.getSecond());
                stringRepresentationBuilder.append(settings.getFirst().toString());
                stringRepresentationBuilder.append(" ");
                stringRepresentationBuilder.append(settings.getSecond().toString());
            }
        } else {
            List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

            for (int i=0; i < tokens.size(); i++) {
                if (i != 0) {
                    stringRepresentationBuilder.append(" ");
                }
                processToken(tokens.get(i), invoc, stringRepresentationBuilder);                
            }
        }
        
        invoc.setStringRepresentation(stringRepresentationBuilder.toString());
        
        return invoc;
    }

    private void processToken(String token, DisplayImagesDirectiveInvocation invoc, StringBuilder stringRepresentationBuilder) {
        if (token.equalsIgnoreCase(ImageDisplayMode.AUTO.name())) {
            invoc.setDisplayMode(ImageDisplayMode.AUTO);
            stringRepresentationBuilder.append(ImageDisplayMode.AUTO.name());
        } else if (token.equalsIgnoreCase(ImageDisplayMode.MANUAL.name())) {
            invoc.setDisplayMode(ImageDisplayMode.MANUAL);
            stringRepresentationBuilder.append(ImageDisplayMode.MANUAL.name());
        } else if (token.equalsIgnoreCase(ImageDisplayMode.OFF.name())) {
            invoc.setDisplayMode(ImageDisplayMode.OFF);
            stringRepresentationBuilder.append(ImageDisplayMode.OFF.name());
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.MISSING_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.MISSING_IMAGE_LIST);
            stringRepresentationBuilder.append(DisplayImagesReportType.MISSING_IMAGE_LIST.name());
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.CHARACTER_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.CHARACTER_IMAGE_LIST);
            stringRepresentationBuilder.append(DisplayImagesReportType.MISSING_IMAGE_LIST.name());
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.TAXON_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.TAXON_IMAGE_LIST);
            stringRepresentationBuilder.append(DisplayImagesReportType.TAXON_IMAGE_LIST.name());
        }
    }
}
