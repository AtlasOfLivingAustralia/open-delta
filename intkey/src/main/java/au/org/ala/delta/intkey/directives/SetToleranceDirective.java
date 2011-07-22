package au.org.ala.delta.intkey.directives;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SetToleranceDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class SetToleranceDirective extends IntkeyDirective {

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        int toleranceValue = 0;

        int currentTolerance = context.getTolerance();

        if (StringUtils.isBlank(data)) {
            String inputText = context.getDirectivePopulator().promptForString("Input tolerance value", Integer.toString(currentTolerance));

            if (StringUtils.isBlank(inputText)) {
                // Cancel hit or blank text entered
                return null;
            } else {
                data = inputText;
            }
        }

        try {
            toleranceValue = Integer.parseInt(data);
        } catch (NumberFormatException ex) {
            throw new IntkeyDirectiveParseException("Invalid integer value", ex);
        }

        return new SetToleranceDirectiveInvocation(toleranceValue);
    }

}
