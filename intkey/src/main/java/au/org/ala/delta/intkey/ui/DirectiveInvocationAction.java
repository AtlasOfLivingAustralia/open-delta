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
package au.org.ala.delta.intkey.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import au.org.ala.delta.intkey.directives.invocation.BasicIntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DirectiveInvocationAction extends AbstractAction {

    private BasicIntkeyDirectiveInvocation _invoc;
    private IntkeyContext _context;

    public DirectiveInvocationAction(BasicIntkeyDirectiveInvocation invoc, IntkeyContext context) {
        _invoc = invoc;
        _context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _context.executeDirective(_invoc);
    }

}
