package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * Parent class for directives that write to the output file. Directive handling
 * will fail immediately if no output file is open.
 * 
 * @author ChrisF
 * 
 */
public abstract class OutputDirective extends StandardIntkeyDirective {

    public OutputDirective(boolean errorIfNoDatasetLoaded, String... controlWords) {
        super(errorIfNoDatasetLoaded, controlWords);
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        if (context.getOutputFile() == null) {
            throw new IntkeyDirectiveParseException("No output file is open. Use FILE OPEN to open an output file.");
        }

        return super.doProcess(context, data);
    }

}
