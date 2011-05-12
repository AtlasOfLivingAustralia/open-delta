package au.org.ala.delta.intkey.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import au.org.ala.delta.Logger;
import au.org.ala.delta.intkey.directives.IntkeyDirective;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DirectiveAction extends AbstractAction {

    private IntkeyDirective _dir;
    private IntkeyContext _context;

    public DirectiveAction(IntkeyDirective dir, IntkeyContext context, String caption) {
        this(dir, context, caption, null);
    }
    
    public DirectiveAction(IntkeyDirective dir, IntkeyContext context, String caption, Icon icon) {
        super(caption, icon);
        _dir = dir;
        _context = context;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            _dir.process(_context, null);
        } catch (Exception ex) {
            Logger.log("Error while running directive from action - %s %s", _dir.toString(), ex.getMessage());
            ex.printStackTrace();
        }
    }

}
