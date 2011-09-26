package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.intkey.directives.invocation.DisplayCharacterOrderBestDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

/**
 * Identical to the "DISPLAY CHARACTERORDER BEST" directive.
 * Included for backwards compatibility reasons.
 * @author ChrisF
 *
 */
public class BestDirective extends IntkeyDirective {

    public BestDirective() {
        super("best");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        return new DisplayCharacterOrderBestDirectiveInvocation();
    }

}
