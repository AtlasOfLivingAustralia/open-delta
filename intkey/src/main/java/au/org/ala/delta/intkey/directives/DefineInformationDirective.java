package au.org.ala.delta.intkey.directives;

import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DefineInformationDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineInformationDirective extends IntkeyDirective {

    public DefineInformationDirective() {
        super("define", "information");
    }

    @Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {

        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);
        
        //TODO prompt if information not provided
        
        String subject = ParsingUtils.removeEnclosingQuotes(tokens.get(0));
        String command = ParsingUtils.removeEnclosingQuotes(tokens.get(1));
        
        return new DefineInformationDirectiveInvocation(subject, command);
    }

}
