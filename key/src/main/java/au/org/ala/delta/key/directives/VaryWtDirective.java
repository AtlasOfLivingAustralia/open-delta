package au.org.ala.delta.key.directives;

import au.org.ala.delta.key.KeyContext;

/**
 * Processes the VARYWT directive.
 * 
 */
public class VaryWtDirective extends AbstractRealDirective {

    public VaryWtDirective() {
        super("varywt");
    }

    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        context.setVaryWt(value);
    }
}
