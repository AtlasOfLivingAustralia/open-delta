package au.org.ala.delta.ui.codeeditor.document;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.DirectiveVisitor;
import au.org.ala.delta.ui.codeeditor.Token;

public abstract class DirectiveTextDocument<C extends AbstractDeltaContext> extends RegExDocument {

	private static final long serialVersionUID = 1L;

	public DirectiveTextDocument() {

		addTokenPattern(Token.COMMENT2, true, "\\*COMMENT\\s.*$");

		DirectiveParser<C> parser = getDirectiveParser();

		parser.visitDirectives(new DirectiveVisitor<C>() {
			@Override
			public void visit(AbstractDirective<C> directive) {
				String[] words = directive.getControlWords();
				if (!words[0].equalsIgnoreCase("comment")) {
					StringBuilder sb = new StringBuilder("[*]");
					for (int i = 0; i < words.length; ++i) {
						if (i > 0) {
							sb.append("\\s");
						}

						if (words[i].length() > 3) {
							sb.append(words[i].substring(0, 3));
							sb.append("\\w*");
						} else {
							sb.append(words[i]);
						}
					}
					addTokenPattern(Token.KEYWORD1, true, sb.toString());
				}
			}
		});
	}

	protected abstract DirectiveParser<C> getDirectiveParser();

	@Override
	public String getBlockCommentStart() {
		return "*COMMENT ";
	}

	@Override
	public String getBlockCommentEnd() {
		return "\n";
	}

	@Override
	public String getLineComment() {
		return "*COMMENT ";
	}

}
