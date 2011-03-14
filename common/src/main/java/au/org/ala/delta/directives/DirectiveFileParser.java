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
package au.org.ala.delta.directives;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.Tree;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;

/**
 * A directive file is a text file containing one or more directives. Directives
 * start with an '*' followed by up to four alphanumeric components of the
 * directive name delimited by a space,followed by the data (if any) of the
 * directive, and is terminated either by the beginning of a new directive, or
 * the end of the file.
 * 
 * @author baird
 * 
 */
public class DirectiveFileParser {

	public static char DIRECTIVE_DELIMITER = '*';
	private final static String _blank = " \n\r";
	public static Tree DIRECTIVE_TREE = new Tree();

	static {
		register(new Show());
		register(new Heading());
		register(new TranslateInto());
		register(new CharacterReliabilities());
		register(new CharacterWeights());
		register(new InputFile());
		register(new ListingFile());
		register(new PrintFile());
		register(new NumberOfCharacters());
		register(new MaximumNumberOfStates());
		register(new MaximumNumberOfItems());
		register(new DataBufferSize());
		register(new CharacterTypes());
		register(new NumbersOfStates());
		register(new ImplicitValues());
		register(new DependentCharacters());
		register(new MandatoryCharacters());
		register(new PrintWidth());
		register(new Comment());
		register(new ReplaceAngleBrackets());
		register(new OmitCharacterNumbers());
		register(new OmitInapplicables());
		register(new OmitInnerComments());
		register(new OmitTypeSettingMarks());
		register(new CharacterForTaxonImages());
		register(new ExcludeCharacters());
		register(new NewParagraphAtCharacters());
		register(new CharacterList());
		register(new ItemDescriptions());
	}

	public static void register(Directive d) {
		DIRECTIVE_TREE.addDirective(d);
	}

	public DirectiveFileParser() {
	}

	public void parse(File file, DeltaContext context) throws IOException {

		ParsingContext pc = context.newParsingContext(file);

		FileReader reader = new FileReader(file);

		StringBuilder currentData = new StringBuilder();
		int ch = reader.read();
		int prev = ' ';
		pc.setCurrentLine(1);
		StringBuilder line = new StringBuilder();
		while (ch >= 0) {
			if (ch == DIRECTIVE_DELIMITER && _blank.indexOf(prev) >= 0) {
				// Finish off any existing directive
				processDirective(currentData, context);
				// Start a new directive
				currentData = new StringBuilder();
				pc.setCurrentDirectiveStartLine(pc.getCurrentLine());
				long offset = pc.getCurrentOffset() - 1;
				pc.setCurrentDirectiveStartOffset(offset < 0 ? 0 : offset);
			} else {
				currentData.append((char) ch);
			}
			line.append((char) ch);
			prev = ch;
			ch = reader.read();
			if (ch == '\n') {
				context.ListMessage(line.toString().trim());
				pc.incrementCurrentLine();
				pc.setCurrentOffset(0);								
				line.setLength(0);
			}

			pc.incrementCurrentOffset();
		}

		processDirective(currentData, context);
		Logger.log("Finished!");
		context.endCurrentParsingContext();
	}

	protected void processDirective(StringBuilder data, DeltaContext context) {
		if (data.length() > 0) {

			// Try and find the directive handler for this data...
			int i = 0;
			List<String> controlWords = new ArrayList<String>();
			ParsingContext pc = context.getCurrentParsingContext();
			while (i < data.length()) {
				String word = readWord(data, i);
				controlWords.add(word);
				DirectiveSearchResult result = DIRECTIVE_TREE.findDirective(controlWords);
				if (result.getResultType() == ResultType.Found) {
					Directive d = result.getDirective();
					// do something with the directive...
					try {
						String dd = data.substring(i + word.length() + 1).trim();
						// context.ListMessage(d, "%s", dd);
						d.process(context, dd);
					} catch (Exception ex) {

						throw new RuntimeException(String.format("Exception occured trying to process directive: %s (%s %d:%d)", d.getName(), pc.getFile().getName(),
								pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()), ex);
					}
					return;

				} else if (result.getResultType() == ResultType.NotFound) {
					Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
							pc.getCurrentDirectiveStartOffset());
					return;
					// throw new
					// RuntimeException(String.format("Unrecognized directive: %s",
					// StringUtils.join(controlWords, " ")));
				}
				i += word.length() + 1;
			}

		}
	}

	private String readWord(StringBuilder buf, int start) {
		int i = start;
		StringBuilder b = new StringBuilder();
		while (i < buf.length()) {
			char ch = buf.charAt(i++);
			if (_blank.indexOf(ch) >= 0) {
				return b.toString();
			} else {
				b.append(ch);
			}
		}
		return b.toString();
	}

}
