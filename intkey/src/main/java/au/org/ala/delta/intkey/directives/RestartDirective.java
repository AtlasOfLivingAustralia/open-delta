package au.org.ala.delta.intkey.directives;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class RestartDirective extends IntkeyDirective {
    
    public RestartDirective() {
        super("restart");
    }

    @Override
    public IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        // TODO this is a stub, need to handle optional switches here.
        
        return new RestartDirectiveInvocation();
    }

    class RestartDirectiveInvocation implements IntkeyDirectiveInvocation {

        @Override
        public boolean execute(IntkeyContext context) {
            context.restartIdentification();
            return true;
        }

        @Override
        public String toString() {
            return StringUtils.join(_controlWords, " ").toUpperCase();
        }
    }
}


