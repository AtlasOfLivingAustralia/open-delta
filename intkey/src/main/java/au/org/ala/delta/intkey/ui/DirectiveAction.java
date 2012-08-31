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
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DirectiveAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	
	private AbstractDirective<IntkeyContext> _directive;
    private IntkeyContext _context;

    public DirectiveAction(AbstractDirective<IntkeyContext> directive, IntkeyContext context) {
        _directive = directive;
        _context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            _directive.parseAndProcess(_context, null);
        } catch (IntkeyDirectiveParseException ex) {
            ex.printStackTrace();
            String msg = ex.getMessage();
            JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            Logger.error(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
            String msg = MessageFormat.format(UIUtils.getResourceString("ErrorWhileProcessingCommand.error"), StringUtils.join(_directive.getControlWords(), " ").toUpperCase(), ex.getMessage());
            JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            Logger.error(msg);
            Logger.error(ex);
        }
    }
}
