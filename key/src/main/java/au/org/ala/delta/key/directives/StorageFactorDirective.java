package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.validation.RealRangeValidator;
import au.org.ala.delta.directives.validation.RealValidator;
import au.org.ala.delta.key.KeyContext;

public class StorageFactorDirective extends AbstractRealDirective {

    public StorageFactorDirective() {
        super("storage", "factor");
    }
    
    @Override
    protected void processReal(KeyContext context, double value) throws Exception {
        // do nothing, this is a no-op directive, implemented only for backwards
        // compatibility
    }
    
    @Override
    protected RealValidator createValidator(DeltaContext context) {
        return null;
    }

}
