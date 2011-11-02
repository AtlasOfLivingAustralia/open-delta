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
package au.org.ala.delta.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JFrame;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.rtf.RTFUtils;

public class Utils {

    public static void centreWindow(Window c, JFrame frame) {
        Dimension app = frame.getSize();
        int x = frame.getX() + (app.width - c.getWidth()) / 2;
        int y = frame.getY() + (app.height - c.getHeight()) / 3;
        if (y < frame.getY()) {
            y = frame.getY();
        }
        c.setLocation(x, y);
    }

    public static String truncate(String str, int length) {
        if (str == null) {
            return "";
        }

        if (str.length() > length) {
            return ".." + str.substring(str.length() - (length - 2));
        } else {
            return str;
        }
    }

    public static short LOWORD(int dword) {
        return (short) (dword & 0x0000ffff);
    }

    public static short HIWORD(int dword) {
        return (short) ((dword & 0xffff0000) >> 16);
    }

    public static long dateToFILETIME(Date d) {
        return (d.getTime() + 11644473600000L) * 10000L;
    }

    public static Date FILETIMEToDate(long FILETIME) {
        return new Date((FILETIME / 10000L) - 11644473600000L);
    }

    public static int strtol(String buf) {
        return strtol(buf, null, 10);
    }

    public static int strtol(String buf, int[] endpos) {
        return strtol(buf, endpos, 10);
    }

    public static int strtol(String buf, int[] endpos, int radix) {
        StringBuffer digits = new StringBuffer();
        int i = 0;
        for (; i < buf.length(); ++i) {
            char ch = buf.charAt(i);
            if (Character.isDigit(ch) || (i == 0 && ch == '-') || (ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122)) {
                digits.append(ch);
            } else {
                break;
            }
        }

        if (endpos != null && endpos.length > 0) {
            endpos[0] = i;
        }

        if (digits.length() > 0) {
            return Integer.parseInt(digits.toString(), radix);
        } else {
            return 0;
        }

    }

    /**
     * A slightly more tolerant version of the BigDecimal constructor - we allow
     * the valid number to be followed by non-numeric characters at the end of
     * the string.
     * 
     * @param src
     *            the String to parse into a BigDecimal.
     * @return the length of the portion of the string containing a parsable
     *         number.
     */
    public static BigDecimal stringToBigDecimal(String src, int[] endPos) {

        int endIndex = src.length();
        while (!Character.isDigit(src.charAt(endIndex - 1))) {
            endIndex--;
        }
        BigDecimal value = new BigDecimal(src.substring(0, endIndex));

        endPos[0] = endIndex;
        return value;
    }

    /**
     * @deprecated Use {@link RTFUtils#stripFormatting(String)}
     * @param text
     * @return
     */
    @Deprecated
    public static byte[] RTFToUTF8(String text) {
        // Same as RTFToANSI, overall. But returns a string of UTF8 encoded
        // Unicode,
        // rather than ANSI.
        // We first build up a UCS2 "wide" string, then convert it to UTF8
        boolean hadControl = false;

        char[] wideBuf = new char[text.length()];

        int outPos = 0;
        if (text.length() > 0) {
            int[] RTFstart = new int[] { 0 };
            int[] RTFnext = new int[] { 0 };
            int prevStart;
            do {
                prevStart = RTFstart[0] = RTFnext[0];
                char aChar = GetNextChar(text, RTFstart, RTFnext);
                if (aChar != 0) {
                    wideBuf[outPos++] = aChar;
                }
                if (!hadControl && RTFstart[0] != prevStart) {
                    hadControl = true;
                }
            } while (RTFnext[0] < (int) text.length());
        }

        StringBuilder b = new StringBuilder();
        for (char ch : wideBuf) {
            if (ch == 0) {
                break; // simulate null terminated
            }
            b.append(ch);
        }
        String str = b.toString();
        try {
            return str.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String RTFToANSI(String text) {

        boolean hadControl = false;

        StringBuffer out = new StringBuffer();

        if (text.length() > 0) {
            int[] RTFstart = new int[] { 0 };
            int[] RTFnext = new int[] { 0 };
            // int outPos = 0;
            int prevStart;
            do {
                prevStart = RTFstart[0] = RTFnext[0];
                char aChar = (char) LOWORD(GetNextChar(text, RTFstart, RTFnext));
                if (aChar != 0) {
                    out.append(aChar);
                }
                if (!hadControl && RTFstart[0] != prevStart)
                    hadControl = true;
            } while (RTFnext[0] < (int) text.length());
        }
        return out.toString();
    }

    public static class NextCharResult {
        public int retVal;
        public int startPos;
        public int endPos;
    }

    public static class RTFcmdReplace {
        public RTFcmdReplace(String a, String b, char c) {
            cmdString = a;
            repString = b;
            unicodeValue = c;
        }

        public String cmdString;
        public String repString;
        public char unicodeValue;
    }

    static RTFcmdReplace[] RTFreps = new RTFcmdReplace[] { new RTFcmdReplace("par", "\r\n", (char) 0x0d), new RTFcmdReplace("line", "\r\n", (char) 0x0b), new RTFcmdReplace("tab", "\t", (char) 0x09),
            new RTFcmdReplace("page", "\f", (char) 0x0c), new RTFcmdReplace("lquote", "\221", (char) 0x2018), // 145
                                                                                                              // ANSI
            new RTFcmdReplace("rquote", "\222", (char) 0x2019), // 146
            new RTFcmdReplace("ldblquote", "\223", (char) 0x201c), // 147
            new RTFcmdReplace("rdblquote", "\224", (char) 0x201d), // 148
            new RTFcmdReplace("bullet", "\225", (char) 0x2022), // 149
            new RTFcmdReplace("endash", "\226", (char) 0x2013), // 150 UGH!
                                                                // Microsoft got
                                                                // it
            new RTFcmdReplace("emdash", "\227", (char) 0x2014), // 151 wrong
                                                                // again! When
                                                                // Vers. 2
            new RTFcmdReplace("enspace", " ", (char) 0x2002), // RichEdit
                                                              // controls
                                                              // stream these
                                                              // in with this
                                                              // form,
            new RTFcmdReplace("emspace", " ", (char) 0x2003) // they're
                                                             // converted to
                                                             // plain ol'
                                                             // dashes and
                                                             // spaces
    };

    static int nRTFCmds = RTFreps.length;

    static String[] skipWords = new String[] { "author",
            // "buptim",
            "colortbl",
            // "comment",
            // "company",
            // "creatim",
            // "doccomm",
            "fonttbl",
            // "footer",
            // "footerf",
            // "footerl",
            // "footerr",
            // "footnote",
            // "ftncn",
            // "ftnsep",
            // "header",
            // "headerf",
            // "headerl",
            // "heaerr",
            // "info",
            // "keywords",
            // "listtable",
            // "operator",
            // "pict",
            // "printim",
            // "private1",
            // "revtim",
            // "rxe",
            // "stylesheet",
            "subject",
            // "tc",
            "title",
    // "txe",
    // "xe"
    };

    static int nSkipWords = skipWords.length;

    // Obtain the next single printable character encoded in an RTF string
    // Begins the search at the position passed in "startPos".
    // Returns the value of the character. If the character was in Unicode,
    // the low word of the result gives its Unicode value, and the high word
    // its "substitution" value.
    // On return, "startPos" points to the position at which the encoding of
    // the character began (which might be on an RTF \), and "endPos" returns
    // the position immediately after the end of the character's encoding.
    // This will be trivial for most text, (where "startPos" will point to the
    // character, and "endPos" to the next position), but for Unicode characters
    // or other
    // "special" characters (like \lquote), it gets more complicated.
    // Note that the calling function must be careful about setting startPos
    // correctly.
    // If it gets things wrong, parts of RTF command strings might look like
    // text.
    public static char GetNextChar(String RTFString, int[] startPos, int[] endPos) {
        char result = 0;
        int skipLevel = 0;
        endPos[0] = RTFString.length();
        while (result == 0 && startPos[0] < endPos[0]) {
            char ch = RTFString.charAt(startPos[0]);
            if (ch == '{' || ch == '}') {
                ++startPos[0];
                if (skipLevel != 0) {
                    if (ch == '{') {
                        ++skipLevel;
                    } else {
                        --skipLevel;
                    }
                }
            } else if (skipLevel != 0) {
                ++startPos[0];
            } else if (ch == '\\') {
                int cmdStart = startPos[0] + 1;
                if (cmdStart >= endPos[0]) {
                    // A pathological case - not actually good RTF

                    result = ch;
                } else {
                    ch = RTFString.charAt(cmdStart);
                    if (Character.isLetter(ch)) {
                        int[] curPos = new int[] { cmdStart };
                        while (++curPos[0] < endPos[0] && Character.isLetter(RTFString.charAt(curPos[0]))) {
                        }

                        String test = RTFString.substring(cmdStart, cmdStart + curPos[0] - cmdStart);

                        int numStart = curPos[0];
                        boolean hasParam = false;
                        if (curPos[0] < endPos[0] && (RTFString.charAt(curPos[0]) == '-' || Character.isDigit(RTFString.charAt(curPos[0])))) {
                            hasParam = true;
                            while (++curPos[0] < endPos[0] && Character.isDigit(RTFString.charAt(curPos[0]))) {
                            }
                        }

                        if (curPos[0] < endPos[0] && RTFString.charAt(curPos[0]) == ' ') {
                            ++curPos[0];
                        }

                        for (int i = 0; i < nSkipWords; ++i) {
                            if (skipWords[i] == test) {
                                skipLevel = 1;
                                break;
                            }
                        }
                        if (skipLevel != 0) {

                        } else if (test == "u") {
                            // Actually had RTF unicode...
                            result = (char) Integer.parseInt(RTFString.substring(numStart, curPos[0] - numStart));
                            char ansiVal = GetNextChar(RTFString, curPos, endPos);
                            curPos[0] = endPos[0];
                            result |= ansiVal << 16;
                        } else if (!hasParam) {
                            // Currently match only parameter-less commands
                            for (int i = 0; i < nRTFCmds; ++i) {
                                if (RTFreps[i].cmdString == test) {
                                    result = RTFreps[i].unicodeValue;
                                    if (result > 0x100)
                                        result |= (char) RTFreps[i].repString.charAt(0) << 16;
                                }
                            }
                        }
                        if (result != 0) {
                            // && endPos == RTFString.size())

                            endPos[0] = curPos[0];
                        } else {
                            startPos[0] = curPos[0];
                        }
                    } else if (ch == '{' || ch == '}' || ch == '\\') {
                        result = ch;
                        endPos[0] = cmdStart + 1;
                    } else if (ch == '~') {
                        result = 0xa0;
                        endPos[0] = cmdStart + 1;
                    } else if (ch == '-') {
                        result = 0xad;
                        endPos[0] = cmdStart + 1;
                    } else if (ch == '\'' && cmdStart + 2 < endPos[0]) {
                        char[] buff = new char[2];
                        buff[0] = RTFString.charAt(cmdStart + 1);
                        buff[1] = RTFString.charAt(cmdStart + 2);

                        result = (char) Integer.parseInt(new String(buff), 16);
                        endPos[0] = cmdStart + 1 + 2;
                    } else {
                        result = ch;
                        endPos[0] = cmdStart + 1;
                    }
                }
            } else if (!Character.isISOControl(ch) || ch >= 0x80) {
                if (ch >= 0x80 && ch < 0xa0) {
                    result = (char) (winANSIChars[ch - 0x80] | ch << 16);
                } else {
                    result = ch;
                }
                endPos[0] = startPos[0] + 1;
            } else
                ++startPos[0];
        }

        if ((result >> 16) == 0)
            result |= (result << 16);

        return result;
    }

    static char[] winANSIChars = new char[] { 0x20AC, // €
            0x81, // �?
            0x201A, // ‚
            0x192, // ƒ
            0x201E, // „
            0x2026, // …
            0x2020, // †
            0x2021, // ‡
            0x2C6, // ˆ
            0x2030, // ‰
            0x160, // Š
            0x2039, // ‹
            0x152, // Œ
            0x8D, // �?
            0x17D, // Ž
            0x8F, // �?
            0x90, // �?
            0x2018, // ‘
            0x2019, // ’
            0x201C, // “
            0x201D, // �?
            0x2022, // •
            0x2013, // –
            0x2014, // —
            0x2DC, // ˜
            0x2122, // ™
            0x161, // š
            0x203A, // ›
            0x153, // œ
            0x9D, // �?
            0x17E, // ž
            0x178 // Ÿ
    };

    /**
     * Calls removeComments with the supplied level and all other parameters =
     * false.
     * 
     * @param text
     *            the text to remove comments from.
     * @param level
     *            identifies the extent of the removal operation.
     * @return the supplied text without comments
     */
    public static String removeComments(String text, int level) {
        return removeComments(text, level, false, false, false, false);
    }

    /**
     * Removes DELTA style <> comments from the supplied string.
     * 
     * @param src
     *            the string to remove comments from.
     * @param level
     *            0 = don't remove, 1 = remove all, 2 = remove only if other
     *            text, 3 = same as 2, but outer brackets are removed if
     *            commented text is used.
     * @return the string with comments removed
     */
    public static String removeComments(String text, int level, boolean convertCommentsToBrackets, boolean removeInnerComments, boolean stripSpaces, boolean removeBrackets) {

        int mode = level;

        int commentLevel = 0;
        boolean hasText = mode == 1;
        boolean hadInner = false;
        char ch;
        int i, curStart = -1, start = -1, end = -1;
        int innerStart = -1;
        boolean wasSpace = true;
        boolean wasBrace = false;
        // TODO despaceRTF(text);
        if (stripSpaces) {
            text = stripExtraSpaces(text);
        }
        StringBuilder result = new StringBuilder(text);

        for (i = 0; i < result.length(); ++i) { // Work through string
            // Is character an opening bracket?
            if (result.charAt(i) == '<' && (wasSpace || wasBrace || (ch = result.charAt(i - 1)) == ' ' || ch == '<' || ch == '>')) {
                wasBrace = true;
                if (convertCommentsToBrackets) {
                    result.setCharAt(i, ')');
                }
                if (removeBrackets || (mode == 3 && commentLevel == 0)) {
                    result.deleteCharAt(i--);
                }
                if (commentLevel == 0) {
                    curStart = i;
                    if (start == -1)
                        start = i;
                } else if (commentLevel == 1) {
                    innerStart = i;
                    hadInner = true;
                }
                // Keep track of nesting level
                commentLevel++;
            }
            // Was it a closing bracket?
            else if (result.charAt(i) == '>' && commentLevel > 0 && result.charAt(i - 1) != '|' && (i + 1 == result.length() || (ch = result.charAt(i + 1)) == ' ' || ch == '<' || ch == '>')) {
                // Keep track of nesting level
                commentLevel--;
                wasBrace = true;
                if (convertCommentsToBrackets)
                    result.setCharAt(i, ')');
                if (removeBrackets || (mode == 3 && commentLevel == 0))
                    result.deleteCharAt(i--);
                if (commentLevel == 0) {
                    if (start != -1) {
                        end = i;
                        if (removeInnerComments && hadInner) // In this case,
                                                             // check for
                        // and remove an empty
                        // comment...
                        {
                            int leng = end - curStart - 1;
                            String contents = result.substring(curStart + 1, end - 1);
                            contents = stripExtraSpaces(contents);
                            if (contents.isEmpty() || contents == " ") {
                                result.delete(curStart, end - 1);
                                i = curStart;
                            } else if (stripSpaces && contents.length() != leng) {
                                result.replace(curStart + 1, curStart + leng, contents);
                                i -= leng - contents.length();
                            }
                        }
                    }
                    hadInner = false;
                } else if (commentLevel == 1 && removeInnerComments) {
                    // If we're removing inner comments, get rid of this
                    // part of the string, and any space before it.
                    int leng = i - innerStart + 1;
                    result.delete(innerStart, innerStart + leng);
                    i = innerStart - 1;
                    while (result.length() > i && result.charAt(i) == ' ')
                        result.deleteCharAt(i--);
                }
            } else if (commentLevel == 0 && (hasText || result.charAt(i) != ' ')) {
                hasText = true;
                wasBrace = false;
                wasSpace = (end == i - 1 && i > 0);
                if (end != -1 && mode > 0) {
                    result.delete(start, end + 1);
                    i -= end - start + 2;
                    // Hmm. How SHOULD spaces around the removed comments
                    // be treated? This erases the spaces BEFORE the comment
                    while (i >= 0 && result.length() > i && result.charAt(i) == ' ')
                        result.deleteCharAt(i--);
                    start = -1;
                    end = -1;
                }
            } else
                wasBrace = false;
        }
        if (end != -1 && hasText && mode > 0) {
            result.delete(start, end + 1);
            for (i = result.length() - 1; i >= 0 && result.charAt(i) == ' '; --i)
                result.deleteCharAt(i);
        }
        return result.toString();
    }

    // Strip extra spaces from a string. This means reducing multiple spaces to
    // a
    // single space AND stripping leading and trailing spaces from comments
    public static String stripExtraSpaces(String str) {
        // TODO Needs to be done properly!
        String tmp = str.replaceAll("  ", " ");
        return tmp.trim();

    }

    public static String getVersionFromManifest() {
        String versionString = Utils.class.getPackage().getImplementationVersion();
        return versionString;
    }

    private static final int BYTES_IN_MEGABTYE = 1048576;

    public static String generateSystemInfo() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();

        // Free, max and total memory should be written out in megabytes
        long freeMemory = Runtime.getRuntime().freeMemory() / BYTES_IN_MEGABTYE;
        long maxMemory = Runtime.getRuntime().maxMemory() / BYTES_IN_MEGABTYE;
        long totalMemory = Runtime.getRuntime().totalMemory() / BYTES_IN_MEGABTYE;

        StringBuilder versionInfo = new StringBuilder();
        versionInfo.append("DELTA Editor " + getVersionFromManifest());
        versionInfo.append("\n");
        versionInfo.append("date: ");
        versionInfo.append(df.format(currentTime));
        versionInfo.append("\n");
        versionInfo.append("free memory: ");
        versionInfo.append(freeMemory);
        versionInfo.append(" MB \n");
        versionInfo.append("total memory: ");
        versionInfo.append(totalMemory);
        versionInfo.append(" MB \n");
        versionInfo.append("max memory: ");
        versionInfo.append(maxMemory);
        versionInfo.append(" MB\n");
        versionInfo.append("java.version: ");
        versionInfo.append(System.getProperty("java.version"));
        versionInfo.append("\n");
        versionInfo.append("java.vendor: ");
        versionInfo.append(System.getProperty("java.vendor"));
        versionInfo.append("\n");
        versionInfo.append("os.name: ");
        versionInfo.append(System.getProperty("os.name"));
        versionInfo.append("\n");
        versionInfo.append("os.arch: ");
        versionInfo.append(System.getProperty("os.arch"));
        versionInfo.append("\n");
        versionInfo.append("os.version: ");
        versionInfo.append(System.getProperty("os.version"));
        versionInfo.append("\n");
        versionInfo.append("user.language: ");
        versionInfo.append(System.getProperty("user.language"));
        versionInfo.append("\n");
        versionInfo.append("user.region: ");
        versionInfo.append(System.getProperty("user.region"));

        return versionInfo.toString();
    }

    /**
     * The main job of this method is to terminate RTF control words with {}
     * instead of a space.
     */
    // Not all cases are handled correctly in the current code.
    // For example, text with \bin might not always give correct results
    // A few other things, such as \'xx, should perhaps also be given
    // explicit treatment, but should not substantially affect the outcome.
    public static String despaceRtf(String text, boolean quoteDelims) {
        if (StringUtils.isEmpty(text)) {
            return "";
        }
        int srcPos;
        boolean inRTF = false;
        boolean inParam = false;
        boolean inUnicode = false;
        boolean bracketed = text.charAt(0) == '<' && text.charAt(text.length() - 1) == '>';

        StringBuilder outputText = new StringBuilder(text);
        if (bracketed) // If a "comment", temporarily chop off the terminating
                       // bracket
            outputText.setLength(outputText.length() - 1);
        for (srcPos = 0; srcPos < outputText.length(); ++srcPos) {
            char ch = outputText.charAt(srcPos);
            // Always convert a tab character into a \tab control word
            if (ch == '\t') {
                outputText.replace(srcPos, 1, "\\tab{}");
                ch = '\\';
            }
            if (inRTF) {
                if (Character.isDigit(ch) || (!inParam && ch == '-')) {
                    if (!inParam && outputText.charAt(srcPos - 1) == 'u' && outputText.charAt(srcPos - 2) == '\\')
                        inUnicode = true;
                    inParam = true;
                } else if (inParam || !Character.isLetter(ch)) {
                    boolean wasInUnicode = inUnicode;
                    inUnicode = inParam = inRTF = false;
                    if (Character.isSpaceChar(ch)) {
                        // Check for the absence of a control; when this
                        // happens,
                        // the terminating character IS the control word!
                        if (srcPos > 0 && outputText.charAt(srcPos - 1) == '\\') {
                            // \<NEWLINE> is treated as a \par control. We make
                            // this
                            // change here explicitly, to make it more apparent.
                            // But should we keep the <NEWLINE> character around
                            // as well,
                            // as a clue for breaking lines during output?
                            if (ch == '\n' || ch == '\r') {
                                // text.replace(--srcPos, 2, "\\par{}");
                                outputText.insert(srcPos, "par{}");
                                srcPos += 5;
                            }
                            // (Note that if we don't catch this here, replacing
                            // "\ " could yield
                            // "\{}" which is WRONG. But rather than just get
                            // rid of this, it
                            // is probably better to replace with {} to ensure
                            // that any preceding
                            // RTF is terminated)
                            else if (ch == ' ') {
                                outputText.replace(srcPos - 1, 2, "{}");
                            }
                        }
                        // This is the chief condition we are trying to fix.
                        // Terminate the RTF
                        // control phrase with {} instead of white space...
                        // But if the terminator is a new line, we keep it
                        // around
                        // for assistance in wrapping output lines.
                        // else if (ch == '\n')
                        // {
                        // text.insert(srcPos, "{}");
                        // srcPos += 2;
                        // }
                        else if (ch != '\n') {
                            outputText.setCharAt(srcPos, '{');
                            outputText.insert(++srcPos, '}');
                        }
                    }
                    // No reason to do the following. Probably better to leave
                    // the
                    // character quoted.
                    // Reinstated 8 December 1999 because we need to be sure
                    // all text is in a consistent state when linking characters
                    // One exception - if the quoted character is a Unicode
                    // "replacement"
                    // character, we'd better leave it quoted.
                    else if (ch == '\'' && !wasInUnicode && srcPos + 2 < outputText.length()) {
                        char[] buff = new char[3];
                        buff[0] = outputText.charAt(srcPos + 1);
                        buff[1] = outputText.charAt(srcPos + 2);
                        buff[2] = 0;

                        int[] endPos = new int[1];
                        int value = strtol(new String(buff), endPos, 16);
                        if ((endPos[0] == 2) && value > 127 && outputText.charAt(srcPos - 1) == '\\') {

                            srcPos--;
                            outputText.replace(srcPos, srcPos + 4, new String(new char[] { (char) value }));

                        }
                    } else if (ch == '\\' && outputText.charAt(srcPos - 1) != '\\') // Terminates
                                                                                    // RTF,
                                                                                    // but
                                                                                    // starts
                                                                                    // new
                                                                                    // RTF
                    {
                        inRTF = true;
                        if (wasInUnicode && srcPos + 1 < outputText.length() && outputText.charAt(srcPos + 1) == '\'')
                            inUnicode = true;
                    } else if (ch == '>') {
                        // Append a space after the RTF (it was probably
                        // stripped by the attribute parsing)
                        outputText.insert(srcPos, "{}");
                    }
                }
            } else if (ch == '\\')
                inRTF = true;
            // TEST - to allow outputting of a "*" or "#" character in arbitrary
            // text...
            else if (quoteDelims && (ch == '*' || ch == '#') && (srcPos == 0 || Character.isSpaceChar(outputText.charAt(srcPos - 1)))) {
                // //char buffer[5];
                // Always build a 4-character replacement string, like:
                // \'20
                // //sprintf(buffer, "\\\'%2.2x", (int)ch);
                // //text.replace(srcPos, buffer, 4);
                // //srcPos += 3;
                outputText.insert(srcPos, "{}");
                srcPos += 2;
            }
        }
        if (inRTF)
            outputText.append("{}");
        if (bracketed)
            outputText.append('>');
        return outputText.toString();
    }

    /**
     * Capitalises the first word in the supplied text (which may contain RTF
     * markup) the first letter of the word is preceded by a '|'.
     * 
     * @param text
     *            the text to capitalise.
     * @return the text with the first word capitalised.
     */
    public static String capitaliseFirstWord(String text) {
        if (StringUtils.isEmpty(text)) {
            return text;
        }
        //
        StringBuilder tmp = new StringBuilder();
        tmp.append(text);
        int index = 0;
        if (tmp.charAt(0) == '\\') {

            if (tmp.length() > 1) {
                index++;
                char next = tmp.charAt(index);
                if (next != '\\' && next != '-') {

                    while (index < text.length() && Character.isLetterOrDigit(tmp.charAt(index))) {
                        index++;
                    }
                }
            }
        }
        while (index < text.length() && !Character.isLetterOrDigit(tmp.charAt(index))) {
            index++;
        }
        if (index < text.length() && Character.isLetter(tmp.charAt(index))) {
            if ((index == 0) || (tmp.charAt(index - 1) != '|')) {
                tmp.setCharAt(index, Character.toUpperCase(tmp.charAt(index)));
            } else if (tmp.charAt(index - 1) == '|') {
                tmp.deleteCharAt(index - 1);
            }
        }
        return tmp.toString();
    }

    /**
     * Unzips the supplied zip file
     * 
     * @param zip
     *            The zip file to extract
     * @param destinationDir
     *            the directory to extract the zip to
     * @throws IOException
     *             if an error occurred while extracting the zip file.
     */
    public static void extractZipFile(File zip, File destinationDir) throws IOException {
        if (!zip.exists()) {
            throw new IllegalArgumentException("zip file does not exist");
        }

        if (!zip.isFile()) {
            throw new IllegalArgumentException("supplied zip file is not a file");
        }

        if (!destinationDir.exists()) {
            throw new IllegalArgumentException("destination does not exist");
        }

        if (!destinationDir.isDirectory()) {
            throw new IllegalArgumentException("destination is not a directory");
        }

        ZipFile zipFile = new ZipFile(zip);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();

        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            String entryName = entry.getName();

            File fileForEntry = new File(destinationDir, entryName);
            if (entry.isDirectory() && !fileForEntry.exists()) {
                fileForEntry.mkdirs();
            } else {
                InputStream is = new BufferedInputStream(zipFile.getInputStream(entry));
                FileUtils.copyInputStreamToFile(is, fileForEntry);
            }
        }
    }

    public static int adjustFontSizeForDPI(int fontSize) {
        /**
         * Need to adjust the font size as Java 2D assumes 72 dpi. From the Java
         * 2D FAQ:
         * 
         * Q: Why does (eg) a 10 pt font in Java applications appear to have a
         * different size from the same font at 10pt in a native application?
         * 
         * A: Conversion from the size in points into device pixels depends on
         * device resolution as reported by the platform APIs. Java 2D defaults
         * to assuming 72 dpi. Platform defaults vary. Mac OS also uses 72 dpi.
         * Linux desktops based on GTK (Gnome) or Qt (KDE) typically default to
         * 96 dpi and let the end-user customise what they want to use. Windows
         * defaults to 96 dpi (VGA resolution) and also offers 120 dpi (large
         * fonts size) and lets users further specify a custom resolution. So a
         * couple of things can now be seen
         * 
         * The DPI reported by platform APIs likely has no correspondence to the
         * true DPI of the display device Its unlikely that Java 2D's default
         * matches the platform default. So a typical results is that for
         * Window's default 96 DPI that a 10 pt font in a Java application is
         * 72/96 of the size of the native counterpart.
         * 
         * Note that Swing's Windows and GTK L&Fs do scale fonts based on the
         * system DPI to match the desktop. If you want to do the same in your
         * application you can call java.awt.Toolkit.getScreenResolution() and
         * use this to apply a simple scale to the size you specify for fonts.
         */
        int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        int adjustedFontSize = (int) Math.round(Math.abs(fontSize) * screenRes / 72.0);

        return adjustedFontSize;
    }

    public static String formatIntegersAsListOfRanges(List<Integer> ints) {
        StringBuilder builder = new StringBuilder();

        int startRange = 0;
        int previousValue = 0;

        if (ints.size() == 0) {
            return StringUtils.EMPTY;
        } else if (ints.size() == 1) {
            return Integer.toString(ints.get(0));
        } else {
            for (int i = 0; i < ints.size(); i++) {
                int val = ints.get(i);

                if (i == 0) {
                    startRange = val;
                } else {
                    if (previousValue < val - 1) {
                        builder.append(" ");
                        builder.append(startRange);

                        if (previousValue != startRange) {
                            builder.append("-");
                            builder.append(previousValue);
                        }

                        startRange = val;

                    }

                    if (i == ints.size() - 1) {
                        builder.append(" ");
                        builder.append(startRange);

                        if (val != startRange) {
                            builder.append("-");
                            builder.append(val);
                        }

                        startRange = val;
                    }
                }

                previousValue = val;
            }

            return builder.toString().trim();
        }
    }

    /**
     * Return a file object for the file at the supplied path (may be a relative
     * path)
     * 
     * @param filePath
     *            the path of the file - may be a relative path
     * @param defaultDirectory
     *            the default parent directory - this directory will be used as
     *            the parent directory if the filePath is not absolute
     * @return
     */
    public static File createFileFromPath(String filePath, File defaultDirectory) {
        File file = null;
        // If the supplied file path starts with one of the file system
        // roots, then it is absolute. Otherwise, assume that
        // it is relative to the directory in which the dataset is located.
        boolean fileAbsolute = false;
        for (File root : File.listRoots()) {
            if (filePath.toLowerCase().startsWith(root.getAbsolutePath().toLowerCase())) {
                fileAbsolute = true;
                break;
            }
        }

        if (fileAbsolute) {
            file = new File(filePath);
        } else {
            file = new File(defaultDirectory, filePath);
        }

        return file;
    }
}
