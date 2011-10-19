package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.DifferencesDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;

public class DifferencesDirective extends NewIntkeyDirective {

    public DifferencesDirective() {
        super(true, "differences");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new BracketedTaxonListArgument("selectedTaxaSpecimen", null, SelectionMode.KEYWORD, false, false));
        arguments.add(new CharacterListArgument("characters", null, SelectionMode.KEYWORD, false, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList() {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('O', "matchOverlap", false));
        flags.add(new IntkeyDirectiveFlag('S', "matchSubset", false));
        flags.add(new IntkeyDirectiveFlag('E', "matchExact", false));
        flags.add(new IntkeyDirectiveFlag('U', "matchUnknowns", false));
        flags.add(new IntkeyDirectiveFlag('I', "matchInapplicables", false));
        flags.add(new IntkeyDirectiveFlag('X', "omitTextCharacters", false));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new DifferencesDirectiveInvocation();
    }

}