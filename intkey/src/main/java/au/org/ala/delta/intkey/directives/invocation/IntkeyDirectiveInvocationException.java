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

import java.text.MessageFormat;

import au.org.ala.delta.intkey.ui.UIUtils;

public class IntkeyDirectiveInvocationException extends Exception {
    public IntkeyDirectiveInvocationException(String messageKey, Throwable cause, Object... messageArguments) {
        super(MessageFormat.format(UIUtils.getResourceString(messageKey), messageArguments), cause);
    }

    public IntkeyDirectiveInvocationException(String messageKey, Object... messageArguments) {
        super(MessageFormat.format(UIUtils.getResourceString(messageKey), messageArguments));
    }
}
