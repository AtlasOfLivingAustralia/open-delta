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
package au.org.ala.delta.rtf;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;

/**
 * For writing RTF content. Utility methods for document start and end, fonts,
 * coloring.
 * 
 * @author ChrisF
 * 
 */
public class RTFWriter {

    protected Writer _writer;

    // by default, the indent width is 340 twips, which is the same as in the
    // legacy version
    // if Intkey
    private int _indentWidth = 340;
    private int _currentIndent = 0;

    protected RTFAlignment _alignment = RTFAlignment.LEFT;

    public RTFWriter(Writer writer) {
        _writer = writer;
    }

    public void setIdentWidth(int twips) {
        _indentWidth = twips;
    }

    public void startDocument() throws IOException {
        _writer.write("{\\rtf1\\ansi\\deff0 {\\fonttbl{\\f0\\froman Times New Roman;}{\\f1\\fswiss MS Sans Serif;}}");
        _writer.write("\n");
        _writer.write("{\\colortbl;\\red255\\green0\\blue0;\\red0\\green0\\blue255}");
        _writer.write("\n");
        _writer.write("\\fs24");
        _writer.write("\n");
        _writer.flush();
    }

    public void endDocument() throws IOException {
        _writer.write("}");
        _writer.write("\n");
        _writer.flush();
        _writer.close();
    }

    public void increaseIndent() {
        _currentIndent++;
    }

    public void decreaseIndent() {
        _currentIndent--;
    }

    public void setAlignment(RTFAlignment alignment) {
        _alignment = alignment;
    }

    public void setTextColor(Color color) throws IOException {
        if (color.equals(Color.BLACK)) {
            _writer.write("\\cf0");
        } else if (color.equals(Color.RED)) {
            _writer.write("\\cf1");
        } else if (color.equals(Color.BLUE)) {
            _writer.write("\\cf2");
        } else {
            throw new IllegalArgumentException("Unsupported color");
        }
        _writer.flush();
    }

    public void setFont(int fontNumber) throws IOException {
        if (fontNumber == 0) {
            _writer.write("\\f0");
        } else if (fontNumber == 1) {
            _writer.write("\\f1");
        } else {
            throw new IllegalArgumentException("Unrecognised font number");
        }
        _writer.flush();
    }

    /**
     * Add freeform text. This will be wrapped in a new paragraph
     * 
     * @param str
     * @throws IOException
     */
    public void writeText(String str) throws IOException {
        // newline characters are new significant in RTF. convert them to spaces
        // to avoid
        // words getting concatenated etc. in the output.
        str = str.replaceAll("\\n", " ");

        _writer.write("\\pard ");

        if (_alignment != null) {
            switch (_alignment) {
            case LEFT:
                _writer.write("\\ql ");
                break;
            case RIGHT:
                _writer.write("\\qr ");
                break;
            case CENTER:
                _writer.write("\\qc ");
                break;
            case JUSTIFY:
                _writer.write("\\qj ");
                break;
            }
        }

        int indentTwips = _currentIndent * _indentWidth;
        _writer.write(String.format("\\li%s ", indentTwips));
        _writer.write(str);
        _writer.write("\\par ");
        _writer.write("\n");
        _writer.flush();
    }

    /**
     * Append text that has already been formatted with RTF control sequences.
     * This method should not be used to start on end the document. Use the
     * dedicated methods for this.
     * 
     * @param str
     */
    public void appendPreformattedRTF(String str) throws IOException {
        _writer.write(str);
        _writer.flush();
    }

}
