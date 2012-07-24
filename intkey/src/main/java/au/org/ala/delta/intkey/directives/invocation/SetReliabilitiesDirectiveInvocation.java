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

import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Character;

public class SetReliabilitiesDirectiveInvocation extends BasicIntkeyDirectiveInvocation {
    private Map<Character, Float> _reliabilitiesMap;

    public SetReliabilitiesDirectiveInvocation(Map<Character, Float> reliabilitiesMap) {
        _reliabilitiesMap = reliabilitiesMap;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        for (Character ch : _reliabilitiesMap.keySet()) {
            ch.setReliability(_reliabilitiesMap.get(ch));
        }

        // Need dataset to re-calculate which characters should be ignored when
        // doing a BEST ordering.
        context.getDataset().determineCharactersToIgnoreForBest();

        // Clear the cached best characters then force the UI to update itself,
        // calculating the best
        // characters in the process
        if (!context.isProcessingDirectivesFile()) {
            context.clearBestOrSeparateCharacters();
            context.getUI().handleUpdateAll();
        }

        // Write a message to the log
        context.appendToLog(UIUtils.getResourceString("ReliabilitiesSet.log"));
        
        return true;
    }
}
