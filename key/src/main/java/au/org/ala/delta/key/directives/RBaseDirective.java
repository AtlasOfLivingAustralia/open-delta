package au.org.ala.delta.key.directives;

import au.org.ala.delta.key.KeyContext;

/**
 * Processes the RBASE directive.
 * 
 */
public class RBaseDirective extends AbstractRealDirective {

    public RBaseDirective() {
        super("rbase");
    }

    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        context.setRBase(value);
    }
}
