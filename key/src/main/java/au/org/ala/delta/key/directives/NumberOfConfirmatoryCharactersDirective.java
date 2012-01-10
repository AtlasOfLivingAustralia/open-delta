package au.org.ala.delta.key.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractIntegerDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.key.KeyContext;

public class NumberOfConfirmatoryCharactersDirective extends AbstractIntegerDirective {

    public NumberOfConfirmatoryCharactersDirective() {
        super("number", "of", "confirmatory", "characters");
    }

    @Override
    protected void processInteger(DeltaContext context, int numberOfConfirmatoryCharacters) throws Exception {
        KeyContext keyContext = (KeyContext) context;
        keyContext.setNumberOfConfirmatoryCharacters(numberOfConfirmatoryCharacters);
    }

    @Override
    public int getArgType() {
        return DirectiveArgType.DIRARG_INTEGER;
    }

}
