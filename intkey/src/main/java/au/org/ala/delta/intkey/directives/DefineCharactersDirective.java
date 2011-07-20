package au.org.ala.delta.intkey.directives;

//TODO need error if no dataset loaded
//TODO need to prompt if characters/keyword not supplied
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.intkey.IntkeyUI;
import au.org.ala.delta.intkey.directives.invocation.IntkeyDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.ui.UIUtils;

public class DefineCharactersDirective extends IntkeyDirective {

    public DefineCharactersDirective() {
        super("define", "characters");
    }

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_KEYWORD_CHARLIST;
	}

	@Override
    protected IntkeyDirectiveInvocation doProcess(IntkeyContext context, String data) throws Exception {
        String keyword = null;
        Set<Integer> characterNumbers = new HashSet<Integer>();
        List<String> tokens = ParsingUtils.tokenizeDirectiveCall(data);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);

            if (i == 0) {
                keyword = ParsingUtils.removeEnclosingQuotes(token);
            } else {
                IntRange r = ParsingUtils.parseIntRange(token);
                if (r != null)
                    for (int charNum : r.toArray()) {
                        characterNumbers.add(charNum);
                    }
                else {
                    try {
                        List<au.org.ala.delta.model.Character> charList = context.getCharactersForKeyword(token);
                        for (au.org.ala.delta.model.Character c : charList) {
                            characterNumbers.add(c.getCharacterId());
                        }
                    } catch (IllegalArgumentException ex) {
                        JOptionPane.showMessageDialog(UIUtils.getMainFrame(), ex.getMessage());
                        return null;
                    }
                }
            }
        }

        return new DefineCharactersDirectiveInvocation(keyword, characterNumbers);
    }

    class DefineCharactersDirectiveInvocation implements IntkeyDirectiveInvocation {

        String _keyword;
        Set<Integer> _characterNumbers;

        public DefineCharactersDirectiveInvocation(String keyword, Set<Integer> characterNumbers) {
            _keyword = keyword;
            _characterNumbers = characterNumbers;
        }

        @Override
        public boolean execute(IntkeyContext context) {
            context.addCharacterKeyword(_keyword, _characterNumbers);
            return true;
        }

        @Override
        public String toString() {
            return String.format("%s %s %s", StringUtils.join(_controlWords, " ").toUpperCase(), _keyword, StringUtils.join(_characterNumbers, " "));
        }

    }

}
