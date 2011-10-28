package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DisplayContinuousDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.DisplayKeywordsDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DisplayKeywordsDirective extends OnOffDirective {
    public DisplayKeywordsDirective() {
        super(false, "display", "keywords");
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DisplayKeywordsDirectiveInvocation();
    }
}
