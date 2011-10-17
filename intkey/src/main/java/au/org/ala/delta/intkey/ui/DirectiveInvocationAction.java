package au.org.ala.delta.intkey.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DirectiveInvocationAction extends AbstractAction {

    private IntkeyDirectiveInvocation _invoc;
    private IntkeyContext _context;

    public DirectiveInvocationAction(IntkeyDirectiveInvocation invoc, IntkeyContext context) {
        _invoc = invoc;
        _context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        _context.executeDirective(_invoc);
    }

}
