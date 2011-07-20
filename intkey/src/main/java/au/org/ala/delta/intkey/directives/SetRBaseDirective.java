package au.org.ala.delta.intkey.directives;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

/**
 * The SET RBASE directive - sets the base of the logarithmic
 * character-reliability scale which is used in determining the 'best'
 * characters during an identification. The default value is 1.1, valid values
 * are real numbers in the range 1 to 5.
 * 
 * @author ChrisF
 * 
 */
public class SetRBaseDirective extends IntkeyDirective {
    
    //TODO needs to provide a prompt window if no value is supplied
    
    public SetRBaseDirective() {
        super("set", "rbase");
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_REAL;
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        String errMsg = UIUtils.getResourceString("SetRBaseDirective.InvalidValue");

        try {
            double value = Double.parseDouble(data);

            if (value >= 1.0 && value <= 5.0) {
                return new SetRBaseDirectiveInvocation(value);
            } else {
                throw new IntkeyDirectiveParseException(errMsg);
            }
        } catch (NumberFormatException ex) {
            throw new IntkeyDirectiveParseException(errMsg, ex);
        }
    }

    private class SetRBaseDirectiveInvocation implements IntkeyDirectiveInvocation {

        private double _value;

        public SetRBaseDirectiveInvocation(double value) {
            _value = value;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            context.setRBase(_value);
            return true;
        }

    }

}
