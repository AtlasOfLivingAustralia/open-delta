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
import java.io.StringWriter;

/**
 * Like a StringBuilder for writing RTF content. Utility methods for document
 * start and end, fonts, coloring. Each time text is appended it is put into a
 * separate paragraph.
 * 
 * @author ChrisF
 * 
 */
public class RTFBuilder extends RTFWriter {

    public RTFBuilder() {
        super(new StringWriter());
    }

    public String toString() {
        return ((StringWriter) _writer).toString();
    }

    public void startDocument() {
        try {
            super.startDocument();
        } catch (IOException ex) {
            // We are using a StringWriter which will never throw an IOException
            // - do nothing.
        }
    }

    public void endDocument() {
        try {
            super.endDocument();
        } catch (IOException ex) {
            // We are using a StringWriter which will never throw an IOException
            // - do nothing.
        }
    }

    public void setTextColor(Color color) {
        try {
            super.setTextColor(color);
        } catch (IOException ex) {
            // We are using a StringWriter which will never throw an IOException
            // - do nothing.
        }
    }

    public void setFont(int fontNumber) {
        try {
            super.setFont(fontNumber);
        } catch (IOException ex) {
            // We are using a StringWriter which will never throw an IOException
            // - do nothing.
        }
    }

    public void appendText(String str) {
        try {
            super.writeText(str);
        } catch (IOException ex) {
            // We are using a StringWriter which will never throw an IOException
            // - do nothing.
        }
    }

}
