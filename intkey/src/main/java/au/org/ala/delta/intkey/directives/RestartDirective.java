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

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.RestartDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The RESTART directive - prepares the program for a new identification or
 * query.
 * 
 * @author ChrisF
 * 
 */
public class RestartDirective extends StandardIntkeyDirective {

    public RestartDirective() {
        super(true, "restart");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        return null;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('I', "identificationParameters", false));
        flags.add(new IntkeyDirectiveFlag('Q', "queryParameters", false));
        flags.add(new IntkeyDirectiveFlag('T', "zeroTolerance", false));
        return flags;
    }

    @Override
    protected BasicIntkeyDirectiveInvocation buildCommandObject() {
        return new RestartDirectiveInvocation();
    }
}
