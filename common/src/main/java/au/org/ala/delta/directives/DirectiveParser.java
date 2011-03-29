package au.org.ala.delta.directives;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.Tree;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;

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
    private final static String _blank = " \n\r";
    private Tree directiveTree = new Tree();

    public Tree getDirectiveTree() {
        return directiveTree;
    }

    protected void register(AbstractDirective<C> dir) {
        directiveTree.addDirective(dir);
    }

    public void parse(File file, C context) throws IOException {
        FileInputStream fileStream = new FileInputStream(file);
        ParsingContext pc = context.newParsingContext();
        pc.setFile(file);
        doParse(fileStream, context, pc);
    }

    public void parse(InputStream stream, C context) throws IOException {
        ParsingContext pc = context.newParsingContext();
        doParse(stream, context, pc);
    }

    protected void doParse(InputStream stream, C context, ParsingContext pc) throws IOException {
        InputStreamReader reader = new InputStreamReader(stream);

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
                // TODO - fix this
                // !context.ListMessage(line.toString().trim());
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

    protected void processDirective(StringBuilder data, C context) {
        if (data.length() > 0) {

            // Try and find the directive handler for this data...
            int i = 0;
            List<String> controlWords = new ArrayList<String>();
            ParsingContext pc = context.getCurrentParsingContext();
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
                        // context.ListMessage(d, "%s", dd);
                        d.process(context, dd);
                    } catch (Exception ex) {
                        if (pc.getFile() != null) {
                            throw new RuntimeException(String.format("Exception occured trying to process directive: %s (%s %d:%d)", d.getName(), pc.getFile().getName(),
                                    pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset()), ex);
                        } else {
                            throw new RuntimeException(String.format("Exception occured trying to process directive: %s (%d:%d)", d.getName(), pc.getCurrentDirectiveStartLine(),
                                    pc.getCurrentDirectiveStartOffset()), ex);
                        }
                    }
                    return;

                } else if (result.getResultType() == ResultType.NotFound) {
                    if (pc.getFile() != null) {
                        Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
                                pc.getCurrentDirectiveStartOffset());
                    } else {
                        Logger.log("Unrecognized Directive: %s at offset %d:%d", StringUtils.join(controlWords, " "), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset());
                    }
                    return;
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
