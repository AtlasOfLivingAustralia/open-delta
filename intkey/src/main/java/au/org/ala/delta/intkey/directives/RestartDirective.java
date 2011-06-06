package au.org.ala.delta.intkey.directives;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.TextArg;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class RestartDirective extends IntkeyDirective {
    
    public RestartDirective() {
        super("restart");
    }
    
    @Override
	public DirectiveArgs getDirectiveArgs() {
		return new TextArg(_data);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXT;
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


