package au.org.ala.delta.directives;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.DirectiveTreeNode;
import au.org.ala.delta.Logger;
import au.org.ala.delta.Tree;
import au.org.ala.delta.TreeNode;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Parser for directive files and input streams
 * 
 * A directive file is a text file containing one or more directives. Directives start with an '*' followed by up to four alphanumeric components of the directive name delimited by a space,followed by
 * the data (if any) of the directive, and is terminated either by the beginning of a new directive, or the end of the file.
 * 
 * @author baird
 * 
 */
public abstract class DirectiveParser<C extends AbstractDeltaContext> {

	public static char DIRECTIVE_DELIMITER = '*';
	private final static String _blank = " \n\r";
	protected Tree directiveTree = new Tree();

	private List<DirectiveParserObserver> _observers = new ArrayList<DirectiveParserObserver>();

	public void registerObserver(DirectiveParserObserver o) {
		_observers.add(o);
	}

	public void deregisterObserver(DirectiveParserObserver o) {
		_observers.remove(o);
	}

	public Tree getDirectiveTree() {
		return directiveTree;
	}

	protected void registerDirective(AbstractDirective<?> dir) {
		directiveTree.addDirective(dir);
	}

	public void parse(File file, C context) throws IOException {
		
		FileInputStream fileIn = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(fileIn, context.getFileEncoding());
		ParsingContext pc = context.newParsingContext();
		pc.setFile(file);
		doParse(reader, context, pc);
	}

	public void parse(Reader reader, C context) throws IOException {
		ParsingContext pc = context.newParsingContext();
		doParse(reader, context, pc);
	}

	protected void doParse(Reader reader, C context, ParsingContext pc) throws IOException {

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
				for (DirectiveParserObserver o : _observers) {
					o.preProcess(line.toString().trim());
				}

				pc.incrementCurrentLine();
				pc.setCurrentOffset(0);
				line.setLength(0);
			}

			pc.incrementCurrentOffset();
		}

		AbstractDirective<?> directive = processDirective(currentData, context);

		for (DirectiveParserObserver o : _observers) {
			o.postProcess(directive);
		}

		Logger.log("Finished!");
		context.endCurrentParsingContext();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected AbstractDirective processDirective(StringBuilder data, C context) {
		if (data.length() > 0) {

			// Try and find the directive handler for this data...
			int i = 0;
			List<String> controlWords = new ArrayList<String>();

			while (i < data.length()) {
				String word = readWord(data, i);
				controlWords.add(word);
				DirectiveSearchResult result = directiveTree.findDirective(controlWords);
				if (result.getResultType() == ResultType.Found) {
					AbstractDirective d = result.getDirective();
					// do something with the directive...
					try {
						String dd;
						if (data.length() < i + word.length() + 1) {
							dd = null;
						} else {
							dd = data.substring(i + word.length() + 1).trim();
						}
						// String dd = data.substring(i + word.length() +
						// 1).trim();
						doProcess(context, d, dd);
					} catch (Exception ex) {
						handleDirectiveProcessingException(context, d, ex);
					}
					return d;

				} else if (result.getResultType() == ResultType.NotFound) {
					handleUnrecognizedDirective(context, controlWords);
					return null;
				}
				i += word.length() + 1;
			}
		}
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected void doProcess(C context, AbstractDirective d, String dd) throws ParseException, Exception {
		d.parse(context, dd);
		DirectiveArguments args = d.getDirectiveArgs();
		d.process(context, args);
	}

	protected abstract void handleUnrecognizedDirective(C context, List<String> controlWords);

	protected abstract void handleDirectiveProcessingException(C context, AbstractDirective<C> d, Exception ex);

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

	public void visitDirectives(final DirectiveVisitor<C> visitor) {

		if (visitor != null) {

			this.directiveTree.visit(new Tree.TreeVisitor() {
				@SuppressWarnings("unchecked")
				@Override
				public void visit(TreeNode node) {					
					if (node instanceof DirectiveTreeNode) {
						DirectiveTreeNode dnode = (DirectiveTreeNode) node;						
						visitor.visit((AbstractDirective<C>) dnode.getDirective());
					}
				}
			});
		}

	}
}
