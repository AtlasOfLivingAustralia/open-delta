package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.model.MatchType;

public class SetMatchDirectiveInvocation extends IntkeyDirectiveInvocation {

    private boolean _matchInapplicables;
    private boolean _matchUnknowns;
    private MatchType _matchType;

    public SetMatchDirectiveInvocation(boolean matchInapplicables, boolean matchUnknowns, MatchType matchType) {
        _matchInapplicables = matchInapplicables;
        _matchUnknowns = matchUnknowns;
        _matchType = matchType;
    }

    @Override
    public boolean execute(IntkeyContext context) {
        context.setMatchSettings(_matchUnknowns, _matchInapplicables, _matchType);
        return true;
    }

}
