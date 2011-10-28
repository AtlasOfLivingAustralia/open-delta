package au.org.ala.delta.intkey.directives.invocation;

import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.intkey.model.IntkeyContext;

public class DefineEndIdentifyDirectiveInvocation extends IntkeyDirectiveInvocation {

    private String _commands;

    public void setCommands(String commands) {
        this._commands = commands;
    }

    public void setNumWindowsToTile(String numWindowsToTile) {
        // Ignore the value specified for this flag. All resulting windows that
        // are capable of being
        // tiled will be tiled. The number is not needed.
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {
        String[] splitCommands = _commands.split(";");
        List<String> commandsList = Arrays.asList(splitCommands);
        context.setEndIdentifyCommands(commandsList);
        return true;
    }

}
