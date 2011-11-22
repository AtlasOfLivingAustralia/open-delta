package au.org.ala.delta.directives;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveException;

/**
 * Parser for directive files and input streams
 * 
 * A directive file is a text file containing one or more directives. Directives
 * start with an '*' followed by up to four alphanumeric components of the
 * directive name delimited by a space,followed by the data (if any) of the
 * directive, and is terminated either by the beginning of a new directive, or
 * the end of the file.
 * 
 * @author baird
 * 
 */
public abstract class DirectiveParser<C extends AbstractDeltaContext> {

    public static char DIRECTIVE_DELIMITER = '*';

    public static String ILLEGAL_TEXT_DELIMITERS = "*#<>";

    private final static String _blank = " \n\r";
    // protected Tree directiveTree = new Tree();

    protected DirectiveRegistry<C> _registry = new DirectiveRegistry<C>();

    private List<DirectiveParserObserver> _observers = new ArrayList<DirectiveParserObserver>();

    public void registerObserver(DirectiveParserObserver o) {
        _observers.add(o);
    }

    public void deregisterObserver(DirectiveParserObserver o) {
        _observers.remove(o);
    }

    @SuppressWarnings("unchecked")
    protected void registerDirective(AbstractDirective<?> dir) {
        _registry.registerDirective((AbstractDirective<C>) dir);
    }

    public DirectiveRegistry<C> getDirectiveRegistry() {
        return _registry;
    }

    public void parse(File file, C context) throws IOException, DirectiveException {

        FileInputStream fileIn = new FileInputStream(file);
        InputStreamReader reader = new InputStreamReader(fileIn, context.getFileEncoding());
        ParsingContext pc = context.newParsingContext();
        pc.setFile(file);
        doParse(reader, context, pc);
        IOUtils.closeQuietly(reader);
    }

    public void parse(Reader reader, C context) throws IOException, DirectiveException {
        ParsingContext pc = context.newParsingContext();
        doParse(reader, context, pc);
    }

    @SuppressWarnings("unchecked")
    protected void doParse(Reader reader, C context, ParsingContext pc) throws IOException, DirectiveException {

        StringBuilder currentData = new StringBuilder();
        int prev = ' ';
        pc.setCurrentLine(1);
        StringBuilder line = new StringBuilder();
        StringBuilder directiveText = new StringBuilder();
        pc.setCurrentDirectiveText(directiveText);
        StringBuilder currentWord = new StringBuilder();
        List<String> currentWords = new ArrayList<String>();

        AbstractDirective<C> currentDirective = null;
        boolean foundDirectiveDelimiter = false;

        int ch = reader.read();

        while (ch >= 0) {

            if (ch == DIRECTIVE_DELIMITER && _blank.indexOf(prev) >= 0) {

                // First test to see if this text is delimited...
                if (currentDirective != null && isDelimited(currentDirective, currentData)) {
                    currentData.append((char) ch);
                } else {
                    // Finish off any existing directive
                    if (currentDirective != null) {
                        executeDirective(currentDirective, currentData.toString(), context);
                    }

                    directiveText.setLength(0);
                    foundDirectiveDelimiter = true;
                    // Start a potentially new directive
                    currentWords.clear();
                    currentWord = new StringBuilder();
                    currentDirective = null;
                    pc.setCurrentDirectiveStartLine(pc.getCurrentLine());
                    long offset = pc.getCurrentOffset() - 1;
                    pc.setCurrentDirectiveStartOffset(offset < 0 ? 0 : offset);
                }
            } else if (_blank.indexOf(ch) >= 0 && currentDirective == null && foundDirectiveDelimiter) {
                if (currentWord.length() > 0) {
                    currentWords.add(currentWord.toString());
                    currentData.append((char) ch);
                    currentWord = new StringBuilder();
                    DirectiveSearchResult result = _registry.findDirective(currentWords);
                    if (result.getResultType() == ResultType.Found) {
                        currentData = new StringBuilder();
                        pc.markDirectiveEnd();
                        currentDirective = (AbstractDirective<C>) result.getMatches().get(0);
                    }
                }
            } else {
                currentWord.append((char) ch);
                currentData.append((char) ch);
            }
            line.append((char) ch);
            prev = ch;
            ch = reader.read();

            // Handle end of line if the previous character was a newline, or
            // there are no more characters to read
            boolean endOfLine = (prev == '\n' || ch < 0);
            updateParsingContext(pc, line, directiveText, endOfLine);
        }

        if (currentDirective != null) {
            executeDirective(currentDirective, currentData.toString(), context);
        } else {
            if (currentData.length() > 0) {
                processTrailing(currentData, context);
            }
        }

        Logger.log("Finished!");
        context.endCurrentParsingContext();
    }

    protected void updateParsingContext(ParsingContext pc, StringBuilder line, StringBuilder directiveText, boolean endOfLine) {
        if (endOfLine) {
            pc.incrementCurrentLine();
            pc.setCurrentOffset(0);
            directiveText.append(line);
            line.setLength(0);
        }
        pc.incrementCurrentOffset();
    }

    private boolean isDelimited(AbstractDirective<C> directive, StringBuilder data) {

        if (!directive.canSpecifyTextDelimiter()) {
            return false;
        }

        // Read the first 'word'
        String word = readWord(data, 0, true);
        if (word.length() == 1 && ILLEGAL_TEXT_DELIMITERS.indexOf(word.charAt(0)) < 0) {
            char delim = word.charAt(0);
            boolean inDelim = false;
            for (int i = data.indexOf(word) + 1; i < data.length(); ++i) {
                if (data.charAt(i) == delim) {
                    inDelim = !inDelim;
                }
            }
            return inDelim;
        }

        return false;
    }

    protected void executeDirective(AbstractDirective<C> directive, String data, C context) throws DirectiveException {
        try {
            for (DirectiveParserObserver o : _observers) {
                o.preProcess(directive, data);
            }
            doProcess(context, directive, data);
            for (DirectiveParserObserver o : _observers) {
                o.postProcess(directive);
            }
        } catch (Exception ex) {
            handleDirectiveProcessingException(context, directive, ex);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void processTrailing(StringBuilder data, C context) throws DirectiveException {
        if (data.length() > 0) {

            // Try and find the directive handler for this data...
            int i = 0;
            List<String> controlWords = new ArrayList<String>();

            while (i < data.length()) {
                String word = readWord(data, i, true);
                controlWords.add(word);
                DirectiveSearchResult result = _registry.findDirective(controlWords);
                if (result.getResultType() == ResultType.Found) {
                    AbstractDirective d = result.getMatches().get(0);

                    // do something with the directive...
                    try {
                        String dd;
                        if (data.length() < i + word.length() + 1) {
                            dd = null;
                        } else {
                            dd = data.substring(i + word.length() + 1);
                        }

                        executeDirective(d, dd, context);
                    } catch (Exception ex) {
                        handleDirectiveProcessingException(context, d, ex);
                    }
                } else if (result.getResultType() == ResultType.NotFound) {
                    handleUnrecognizedDirective(context, controlWords);
                }
                i += word.length() + 1;
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void doProcess(C context, AbstractDirective d, String dd) throws ParseException, Exception {

        d.parse(context, dd);
        DirectiveArguments args = d.getDirectiveArgs();
        d.process(context, args);
    }

    protected abstract void handleUnrecognizedDirective(C context, List<String> controlWords);

    /**
     * The observers are given the opportunity to handle the exception. Any
     * observer can chose to terminate directive processing by rethrowing the
     * exception.
     * 
     * @throws DirectiveException
     *             if an observer decides to terminate directive processing.
     */
    protected void handleDirectiveProcessingException(C context, AbstractDirective<C> directive, Exception ex) throws DirectiveException {
        for (DirectiveParserObserver observer : _observers) {
            observer.handleDirectiveProcessingException(context, directive, ex);
        }
    }

    private String readWord(StringBuilder buf, int start, boolean ignoreLeadingSpace) {
        int i = start;
        StringBuilder b = new StringBuilder();

        if (ignoreLeadingSpace) {
            while (i < buf.length()) {
                char ch = buf.charAt(i++);
                if (_blank.indexOf(ch) < 0) {
                    --i;
                    break;
                }
            }
        }

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
            this._registry.visitDirectives(visitor);
        }

    }
}
