package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.CharactersDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;

public class CharactersDirective extends NewIntkeyDirective {
    
    public CharactersDirective() {
        super("characters");
    }

    @Override
    protected List<IntkeyDirectiveArgument> buildArguments() {
        List<IntkeyDirectiveArgument> arguments = new ArrayList<IntkeyDirectiveArgument>();
        arguments.add(new CharacterListArgument("characters", null, SelectionMode.KEYWORD, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlags() {
        return null;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new CharactersDirectiveInvocation();
    }

}
