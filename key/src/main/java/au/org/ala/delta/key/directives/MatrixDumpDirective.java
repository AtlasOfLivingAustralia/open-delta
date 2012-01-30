package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractTextDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class MatrixDumpDirective extends AbstractTextDirective {

    public MatrixDumpDirective() {
        super("matrix", "dump");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        // do nothing, this is a no-op directive, implemented only for backwards
        // compatibility
    }

}
