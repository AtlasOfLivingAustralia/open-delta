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

import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineEndIdentifyDirectiveInvocation extends BasicIntkeyDirectiveInvocation {

    private String _commands;

    public void setCommands(String commands) {
        this._commands = commands;
    }

    public void setNumWindowsToTile(String numWindowsToTile) {
        // Ignore the value specified for this flag. All resulting windows that
        // are capable of being
        // tiled will be tiled. The number is not needed.
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        String[] splitCommands = _commands.split(";");
        List<String> commandsList = Arrays.asList(splitCommands);
        context.setEndIdentifyCommands(commandsList);
        return true;
    }

}
