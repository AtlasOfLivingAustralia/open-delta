package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.directives.invocation.SimilaritiesDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;

public class SimilaritiesDirective extends NewIntkeyDirective {

    public SimilaritiesDirective() {
        super("similarities");
    }

    @Override
    protected List<IntkeyDirectiveArgument<?>> generateArgumentsList(IntkeyContext context) {
        List<IntkeyDirectiveArgument<?>> arguments = new ArrayList<IntkeyDirectiveArgument<?>>();
        arguments.add(new BracketedTaxonListArgument("selectedTaxaSpecimen", null, SelectionMode.KEYWORD, false));
        arguments.add(new CharacterListArgument("characters", null, SelectionMode.KEYWORD, false));
        return arguments;
    }

    @Override
    protected List<IntkeyDirectiveFlag> buildFlagsList(IntkeyContext context) {
        List<IntkeyDirectiveFlag> flags = new ArrayList<IntkeyDirectiveFlag>();
        flags.add(new IntkeyDirectiveFlag('O', "matchOverlap", context.getMatchType() == MatchType.OVERLAP));
        flags.add(new IntkeyDirectiveFlag('S', "matchSubset", context.getMatchType() == MatchType.SUBSET));
        flags.add(new IntkeyDirectiveFlag('E', "matchExact", context.getMatchType() == MatchType.EXACT));
        flags.add(new IntkeyDirectiveFlag('U', "matchUnknowns", context.getMatchUnkowns()));
        flags.add(new IntkeyDirectiveFlag('I', "matchInapplicables", context.getMatchInapplicables()));
        return flags;
    }

    @Override
    protected IntkeyDirectiveInvocation buildCommandObject() {
        return new SimilaritiesDirectiveInvocation();
    }

}
