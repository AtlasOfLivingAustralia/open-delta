package au.org.ala.delta.ui.codeeditor.document;

import javax.swing.text.Segment;


public class PlainTextDocument extends TextDocument {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Gets the document's mime type
     *
     * @return The document's mime type.
     */
    public String getMimeType() {
        return "text/plain";
    }

    /**
     * Marks the tokens.
     *
     * @param token
     *            The current token.
     * @param line
     *            The current line.
     * @param lineIndex
     *            The index of the current line.
     * @return The new index.
     */
    protected byte markTokens(byte token, Segment line, int lineIndex, ITokenAccumulator acc) {
        int offset = line.offset;
        int lastOffset = offset;
        int length = line.count + offset;
        acc.addToken(lastOffset, length - lastOffset, token, null);
        return token;
    }

    @Override
    public String getLineComment() {
        return "#";
    }

}
