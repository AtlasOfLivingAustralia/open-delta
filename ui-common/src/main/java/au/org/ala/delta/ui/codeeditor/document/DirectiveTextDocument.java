/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.ui.codeeditor.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.DirectiveVisitor;
import au.org.ala.delta.ui.codeeditor.Token;

public abstract class DirectiveTextDocument<C extends AbstractDeltaContext> extends RegExDocument {

	private static final long serialVersionUID = 1L;
	private static String _helpFile;

	static {
		try {

			List<String> lines = IOUtils.readLines(DirectiveTextDocument.class.getResourceAsStream("/help/delta_editor/pages/delta-user-guide.htm"));
			_helpFile = StringUtils.join(lines, " ");
			// IOUtils.write(_helpFile, new FileOutputStream("c:/zz/helpfile.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public DirectiveTextDocument() {

		addTokenPattern(Token.COMMENT2, null, true, "\\*COMMENT\\s.*$");

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
					
					List<String> wordList = new ArrayList<String>();
					int i = 0;
					while (i < words.length && wordList.size() < 3) {
						wordList.add(words[i]);
						i++;
					}

					String pattern = String.format("<h5><a name=\"_[*]%s_*\"></a>(.*?)</h5>(.*?)<h5>", StringUtils.join(wordList, "_").toUpperCase());

					Pattern regex = Pattern.compile(pattern, Pattern.DOTALL);

					Matcher m = regex.matcher(_helpFile);
					
					String desc = directive.getClass().getCanonicalName();
					if (m.find()) {
						desc = m.group(2);
					}

					DirectiveTooltipContent tooltip = new DirectiveTooltipContent(StringUtils.join(words, " ").toUpperCase(), desc, 0);
					addTokenPattern(Token.KEYWORD1, tooltip, true, sb.toString());
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
