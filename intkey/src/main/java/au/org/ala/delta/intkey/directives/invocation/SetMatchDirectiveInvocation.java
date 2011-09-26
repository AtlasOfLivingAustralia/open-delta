package au.org.ala.delta.intkey.directives.invocation;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.MatchType;

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
        context.setMatchInapplicables(_matchInapplicables);
        context.setMatchUnknowns(_matchUnknowns);
        context.setMatchType(_matchType);
        return true;
    }

}
