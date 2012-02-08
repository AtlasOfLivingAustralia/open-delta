package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractNoArgDirective;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.key.KeyContext;

public class AllowImproperSubgroupsDirective extends AbstractNoArgDirective {

    public AllowImproperSubgroupsDirective() {
        super("allow", "improper", "subgroups");
    }

    @Override
    public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
        ((KeyContext)context).setAllowImproperSubgroups(true);
    }

}
