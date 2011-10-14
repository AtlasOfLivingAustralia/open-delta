package au.org.ala.delta.intkey.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirective;
import au.org.ala.delta.intkey.directives.IntkeyDirectiveParseException;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DirectiveAction extends AbstractAction {

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
            String msg = String.format("Error occurred while processing '%s' command: %s", StringUtils.join(_directive.getControlWords(), " ").toUpperCase(), ex.getMessage());
            JOptionPane.showMessageDialog(UIUtils.getMainFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
            Logger.error(msg);
            Logger.error(ex);
        }
    }
}
