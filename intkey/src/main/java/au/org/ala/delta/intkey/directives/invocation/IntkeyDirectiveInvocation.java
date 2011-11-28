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

import au.org.ala.delta.intkey.model.IntkeyContext;



/**
 * Command pattern. Represents a call to one of the Intkey
 * directives with specific arguments.
 * @author Chris
 *
 */
public abstract class IntkeyDirectiveInvocation {
    
    private String stringRepresentation;
    
    public void setStringRepresentation(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }
    
    @Override
    public String toString() {
        return stringRepresentation;
    }

    /**
     * Perform execution
     * @param context State object to set values on
     * @return success
     */
    public abstract boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException;
    
}
