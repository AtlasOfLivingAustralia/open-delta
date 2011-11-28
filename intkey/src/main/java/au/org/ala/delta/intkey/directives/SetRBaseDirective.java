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

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetRBaseDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * The SET RBASE directive - sets the base of the logarithmic
 * character-reliability scale which is used in determining the 'best'
 * characters during an identification. The default value is 1.1, valid values
 * are real numbers in the range 1 to 5.
 * 
 * @author ChrisF
 * 
 */
public class SetRBaseDirective extends NewIntkeyDirective {

    public SetRBaseDirective() {
        super(false, "set", "rbase");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new RealArgument("rbase", "Enter value of RBASE", context.getRBase()));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SetRBaseDirectiveInvocation();
    }

}
