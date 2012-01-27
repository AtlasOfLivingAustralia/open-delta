package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.key.KeyContext;

public class StopAfterColumnDirective extends AbstractIntegerDirective {

    @Override
    protected void processInteger(DeltaContext context, int stopAtColumn) throws Exception {
        KeyContext keyContext = (KeyContext) context;
        keyContext.setStopAfterColumn(stopAtColumn);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

}
