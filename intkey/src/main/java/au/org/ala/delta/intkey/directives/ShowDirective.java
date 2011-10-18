package au.org.ala.delta.intkey.directives;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.ShowDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class ShowDirective extends IntkeyDirective {

    public ShowDirective() {
        super("show");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        if (StringUtils.isEmpty(data)) {
            data = context.getDirectivePopulator().promptForString("Enter text", null, StringUtils.join(getControlWords(), " ").toUpperCase());
        }

        ShowDirectiveInvocation invoc = new ShowDirectiveInvocation();
        invoc.setText(data);
        return invoc;
    }

}
