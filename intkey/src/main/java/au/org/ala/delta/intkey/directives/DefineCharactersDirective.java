package au.org.ala.delta.intkey.directives;

//TODO need error if no dataset loaded
//TODO need to prompt if characters/keyword not supplied
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DefineCharactersDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineCharactersDirective extends NewIntkeyDirective {

    public DefineCharactersDirective() {
        super("define", "characters");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new StringArgument("keyword", "Enter keyword", null));
        arguments.add(new CharacterListArgument("characters", null, SelectionMode.KEYWORD, false, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DefineCharactersDirectiveInvocation();
    }

}
