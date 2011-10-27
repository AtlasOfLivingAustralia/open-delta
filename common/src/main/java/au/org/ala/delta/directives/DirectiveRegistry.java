package au.org.ala.delta.directives;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;

public class DirectiveRegistry<C extends AbstractDeltaContext> {

	private int _numberOfSignificantCharacters = 3;

	private List<AbstractDirective<C>> _list = new ArrayList<AbstractDirective<C>>();

	public void registerDirective(AbstractDirective<C> directive) {
		DirectiveSearchResult result = findDirective(directive.getControlWords());
		if (result.getResultType() != DirectiveSearchResult.ResultType.NotFound) {
			throw new RuntimeException("Directive already exists!" + directive.toString());
		}

		_list.add(directive);
	}

	public DirectiveSearchResult findDirective(List<String> controlWords) {
		String[] words = controlWords.toArray(new String[] {});
		return findDirective(words);
	}

	public DirectiveSearchResult findDirective(String... controlwords) {

		List<AbstractDirective<?>> candidates = new ArrayList<AbstractDirective<?>>();
		for (AbstractDirective<C> directive : _list) {

			if (controlwords.length <= directive.getControlWords().length) {
				boolean match = true;
				for (int i = 0; i < controlwords.length; ++i) {
					String input = controlwords[i].toUpperCase();
					String candidateword = directive.getControlWords()[i].toUpperCase();

					if (_numberOfSignificantCharacters > 0 && input.length() > _numberOfSignificantCharacters) {
						input = input.substring(0, _numberOfSignificantCharacters);
					}

					if (!candidateword.startsWith(input)) {
						match = false;
						break;
					}
				}

				if (match) {
					candidates.add(directive);
				}
			}
		}

		// Sometimes directives can be uniquely identified by just there first control word. This
		// is a problem for the parser because it then doesn't property consume the rest of the control words...
		if (candidates.size() == 1) {
			if (candidates.get(0).getControlWords().length != controlwords.length) {
				candidates.clear();
			}
		}

		if (candidates.size() == 1) {
			return new DirectiveSearchResult(ResultType.Found, candidates);
		}

		if (candidates.size() > 1) {
			return new DirectiveSearchResult(ResultType.MoreSpecificityRequired, candidates);
		}

		return new DirectiveSearchResult(ResultType.NotFound, candidates);
	}

	public void setNumberOfSignificantCharacters(int numberOfSignifcantCharacters) {
		this._numberOfSignificantCharacters = numberOfSignifcantCharacters;
	}

	public void dump(PrintWriter writer) {
		for (AbstractDirective<C> d : _list) {
			writer.write(String.format("%s\n", StringUtils.join(d.getControlWords(), " ")));
		}
		writer.flush();
	}

	public void visitDirectives(DirectiveVisitor<C> visitor) {
		for (AbstractDirective<C> dir : _list) {
			visitor.visit(dir);
		}
	}

}
