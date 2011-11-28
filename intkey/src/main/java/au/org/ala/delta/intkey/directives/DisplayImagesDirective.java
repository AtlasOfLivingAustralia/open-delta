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
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.DisplayImagesReportType;
import au.org.ala.delta.intkey.model.ImageDisplayMode;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.util.Pair;

public class DisplayImagesDirective extends IntkeyDirective {

    public DisplayImagesDirective() {
        super(true, "display", "images");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        DisplayImagesDirectiveInvocation invoc = new DisplayImagesDirectiveInvocation();

        if (StringUtils.isEmpty(data) || data.startsWith(IntkeyDirectiveArgument.DEFAULT_DIALOG_WILDCARD)) {
            Pair<ImageDisplayMode, DisplayImagesReportType> settings = context.getDirectivePopulator().promptForImageDisplaySettings();

            if (settings == null) {
                // user cancelled
                return null;
            } else {
                invoc.setDisplayMode(settings.getFirst());
                invoc.setReportType(settings.getSecond());
            }
        } else {
            List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

            String firstToken = tokens.get(0);
            processToken(firstToken, invoc);

            if (tokens.size() > 1) {
                String secondToken = tokens.get(0);
                processToken(secondToken, invoc);
            }
        }

        return invoc;
    }

    private void processToken(String token, DisplayImagesDirectiveInvocation invoc) {
        if (token.equalsIgnoreCase(ImageDisplayMode.AUTO.name())) {
            invoc.setDisplayMode(ImageDisplayMode.AUTO);
        } else if (token.equalsIgnoreCase(ImageDisplayMode.MANUAL.name())) {
            invoc.setDisplayMode(ImageDisplayMode.MANUAL);
        } else if (token.equalsIgnoreCase(ImageDisplayMode.OFF.name())) {
            invoc.setDisplayMode(ImageDisplayMode.OFF);
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.MISSING_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.MISSING_IMAGE_LIST);
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.CHARACTER_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.CHARACTER_IMAGE_LIST);
        } else if (token.equalsIgnoreCase(DisplayImagesReportType.TAXON_IMAGE_LIST.name())) {
            invoc.setReportType(DisplayImagesReportType.TAXON_IMAGE_LIST);
        }
    }
}
