package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.key.KeyContext;

public class NoTabularKeyDirective extends AbstractNoArgDirective {

    public NoTabularKeyDirective() {
        super("no", "bracketed", "key");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        ((KeyContext)context).setDisplayTabularKey(false);
    }

}
