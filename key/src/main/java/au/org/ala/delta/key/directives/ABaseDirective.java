package au.org.ala.delta.key.directives;

import au.org.ala.delta.key.KeyContext;

/**
 * Processes the ABASE directive.
 * 
 */
public class ABaseDirective extends AbstractRealDirective {

    public ABaseDirective() {
        super("abase");
    }

    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        context.setABase(value);
    }
}
