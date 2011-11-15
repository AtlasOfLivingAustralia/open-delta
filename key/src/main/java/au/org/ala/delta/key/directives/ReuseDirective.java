package au.org.ala.delta.key.directives;

import au.org.ala.delta.key.KeyContext;

/**
 * Processes the RBASE directive.
 * 
 */
public class ReuseDirective extends AbstractRealDirective {

    public ReuseDirective() {
        super("reuse");
    }

    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        context.setReuse(value);
    }
}
