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

import au.org.ala.delta.rtf.RTFUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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

    public static String fixedWidth(String str, int length) {
        if (str == null) {
            str = "";
        }

        if (str.length() > length) {
            return ".." + str.substring(str.length() - (length - 2));
        } else {
            // Pad out to 15 chars.
            StringBuilder result = new StringBuilder();
            result.append(str);
            for (int i = result.length(); i < length; i++) {
                result.append(' ');
            }
            return result.toString();
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
            if (Character.isDigit(ch) || (i == 0 && ch == '-') || ((radix > 10) && ((ch >= 65 && ch <= 90) || (ch >= 97 && ch <= 122)))) {
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

    public static <T extends Comparable<T>> int lowerBound(List<T> list, int startPos, int endPos, T n) {
        if (startPos >= list.size()) {
            return list.size();
        }

        if (endPos > list.size() - 1) {
            endPos = list.size() - 1;
        }

        for (int i = startPos; i <= endPos; ++i) {
            if (list.get(i).compareTo(n) >= 0) {
                return i;
            }
        }

        return list.size();
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
     * @param text
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

    public static String generateSystemInfo(String applicationTitle) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz", Locale.ENGLISH);
        Calendar cal = Calendar.getInstance();
        Date currentTime = cal.getTime();

        // Free, max and total memory should be written out in megabytes
        long freeMemory = Runtime.getRuntime().freeMemory() / BYTES_IN_MEGABTYE;
        long maxMemory = Runtime.getRuntime().maxMemory() / BYTES_IN_MEGABTYE;
        long totalMemory = Runtime.getRuntime().totalMemory() / BYTES_IN_MEGABTYE;

        StringBuilder versionInfo = new StringBuilder();
        versionInfo.append(applicationTitle + " " + getVersionFromManifest());
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
        versionInfo.append("\n");
        versionInfo.append("user.dir: ");
        versionInfo.append(System.getProperty("user.dir"));

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
                outputText.replace(srcPos, srcPos+1, "\\tab{}");
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

        StringBuilder tmp = new StringBuilder();
        tmp.append(text);
        int index = 0;
        while (index >= 0 && index < text.length() && !Character.isLetterOrDigit(tmp.charAt(index))) {
            if (tmp.charAt(index) == '\\') {
                index = RTFUtils.skipKeyword(text, index);

                if (index < 0 || index >= tmp.length() || Character.isLetterOrDigit(tmp.charAt(index))) {
                    break;
                }
            }
            index++;
        }

        if (index >= 0 && index < text.length() && Character.isLetter(tmp.charAt(index))) {
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

    /**
     * Adjust the supplied font size. Apply scaling based on the 72 dpi assumed
     * by java and the current screen resolution
     * 
     * Need to adjust the font size as Java 2D assumes 72 dpi. From the Java 2D
     * FAQ:
     * 
     * Q: Why does (eg) a 10 pt font in Java applications appear to have a
     * different size from the same font at 10pt in a native application?
     * 
     * A: Conversion from the size in points into device pixels depends on
     * device resolution as reported by the platform APIs. Java 2D defaults to
     * assuming 72 dpi. Platform defaults vary. Mac OS also uses 72 dpi. Linux
     * desktops based on GTK (Gnome) or Qt (KDE) typically default to 96 dpi and
     * let the end-user customise what they want to use. Windows defaults to 96
     * dpi (VGA resolution) and also offers 120 dpi (large fonts size) and lets
     * users further specify a custom resolution. So a couple of things can now
     * be seen
     * 
     * The DPI reported by platform APIs likely has no correspondence to the
     * true DPI of the display device Its unlikely that Java 2D's default
     * matches the platform default. So a typical results is that for Window's
     * default 96 DPI that a 10 pt font in a Java application is 72/96 of the
     * size of the native counterpart.
     * 
     * Note that Swing's Windows and GTK L&Fs do scale fonts based on the system
     * DPI to match the desktop. If you want to do the same in your application
     * you can call java.awt.Toolkit.getScreenResolution() and use this to apply
     * a simple scale to the size you specify for fonts.
     * 
     * 
     * @param fontSize
     *            the font size
     * @return the font size, adjusted from the default 72 dpi assumed by java
     *         2d to the screen resolution. See comment above.
     */
    public static int adjustFontSize(int fontSize) {
        int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        return adjustFontSize(fontSize, screenRes);
    }

    /**
     * Adjust the supplied font size. Apply scaling based on the 72 dpi assumed
     * by java and the current screen resolution
     * 
     * Need to adjust the font size as Java 2D assumes 72 dpi. From the Java 2D
     * FAQ:
     * 
     * Q: Why does (eg) a 10 pt font in Java applications appear to have a
     * different size from the same font at 10pt in a native application?
     * 
     * A: Conversion from the size in points into device pixels depends on
     * device resolution as reported by the platform APIs. Java 2D defaults to
     * assuming 72 dpi. Platform defaults vary. Mac OS also uses 72 dpi. Linux
     * desktops based on GTK (Gnome) or Qt (KDE) typically default to 96 dpi and
     * let the end-user customise what they want to use. Windows defaults to 96
     * dpi (VGA resolution) and also offers 120 dpi (large fonts size) and lets
     * users further specify a custom resolution. So a couple of things can now
     * be seen
     * 
     * The DPI reported by platform APIs likely has no correspondence to the
     * true DPI of the display device Its unlikely that Java 2D's default
     * matches the platform default. So a typical results is that for Window's
     * default 96 DPI that a 10 pt font in a Java application is 72/96 of the
     * size of the native counterpart.
     * 
     * Note that Swing's Windows and GTK L&Fs do scale fonts based on the system
     * DPI to match the desktop. If you want to do the same in your application
     * you can call java.awt.Toolkit.getScreenResolution() and use this to apply
     * a simple scale to the size you specify for fonts.
     * 
     * 
     * @param fontSize
     *            the font size
     * @param targetDPI the resolution at which the font will be displayed           
     * @return the font size, adjusted from the default 72 dpi assumed by java
     *         2d to the target DPI. See comment above.
     */
    public static int adjustFontSize(int fontSize, int targetDPI) {
        int adjustedFontSize = (int) Math.round(Math.abs(fontSize) * (double)targetDPI / 72.0);
        return adjustedFontSize;
    }

    /**
     * Performs the opposite operation to adjustFontSize.
     */
    public static int adjustFontInfoSize(int fontInfoSize, int targetDPI) {
        return (int)Math.round(Math.abs(fontInfoSize) * 72.0 / (double)targetDPI);
    }



    /**
     * Format a list of integers as a set of ranges, with spaces as the list
     * item separator and "-" as the range symbol
     * 
     * @param ints
     *            the list of integers, this is assumed to be already sorted.
     * @return The formated list of ranges
     */
    public static String formatIntegersAsListOfRanges(List<Integer> ints) {
        return formatIntegersAsListOfRanges(ints, " ", "-");
    }

    /**
     * Format a list of integers as a set of ranges, with spaces as the list
     * item separator and the supplied string as the range symbol
     * 
     * @param ints
     *            the list of integers, this is assumed to be already sorted.
     * @param rangeSymbol
     *            the symbol to use as the range symbol
     * @return the formatted list of ranges
     */
    public static String formatIntegersAsListOfRanges(List<Integer> ints, String rangeSymbol) {
        return formatIntegersAsListOfRanges(ints, " ", rangeSymbol);
    }

    /**
     * Format a list of integers as a set of ranges, with supplied strings used
     * for the list item separator and range symbol
     * 
     * @param ints
     *            the list of integers, this is assumed to be already sorted.
     * @param itemSeparator
     *            the symbol to use as the list item separator.
     * @param rangeSymbol
     *            the symbol to use as the range symbol
     * @return the formatted list of ranges
     */
    public static String formatIntegersAsListOfRanges(List<Integer> ints, String itemSeparator, String rangeSymbol) {
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
                        builder.append(itemSeparator);
                        builder.append(startRange);

                        if (previousValue != startRange) {
                            builder.append(rangeSymbol);
                            builder.append(previousValue);
                        }

                        startRange = val;

                    }

                    if (i == ints.size() - 1) {
                        builder.append(itemSeparator);
                        builder.append(startRange);

                        if (val != startRange) {
                            builder.append(rangeSymbol);
                            builder.append(val);
                        }

                        startRange = val;
                    }
                }

                previousValue = val;
            }

            String retStr = builder.toString();

            // Remove any leading or trailing space symbol
            if (retStr.startsWith(itemSeparator)) {
                retStr = retStr.substring(1);
            }

            if (retStr.endsWith(itemSeparator)) {
                retStr = retStr.substring(0, retStr.length() - 1);
            }

            return retStr;
        }
    }

    public static String formatFloatRangeAsString(FloatRange range) {
        StringBuilder builder = new StringBuilder();
        float minimumValue = range.getMinimumFloat();
        float maximumValue = range.getMaximumFloat();
        if (minimumValue == maximumValue) {
            if (minimumValue == Math.round(minimumValue)) {
                builder.append((int) minimumValue);
            } else {
                builder.append(minimumValue);
            }
        } else {
            if (minimumValue == Math.round(minimumValue)) {
                builder.append((int) minimumValue);
            } else {
                builder.append(minimumValue);
            }
            builder.append("-");
            if (maximumValue == Math.round(maximumValue)) {
                builder.append((int) maximumValue);
            } else {
                builder.append(maximumValue);
            }
        }

        return builder.toString();
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
        filePath = FilenameUtils.separatorsToSystem(filePath);
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

    /**
     * Use the values of the bits in the supplied array of bytes to create a
     * single array of boolean values
     */
    public static boolean[] byteArrayToBooleanArray(byte[] bArray) {
        boolean[] boolArray = new boolean[bArray.length * Byte.SIZE];

        for (int i = 0; i < bArray.length; i++) {
            byte b = bArray[i];
            for (int j = 0; j < Byte.SIZE; j++) {
                if ((b & (1 << j)) > 0) {
                    boolArray[i * Byte.SIZE + j] = true;
                } else {
                    boolArray[i * Byte.SIZE + j] = false;
                }
            }
        }

        return boolArray;
    }

    public static void launchIntkeyInSeparateProcess(String startupDirectory, String inputFile) throws Exception {
        if (!launchIntkeyViaExe(startupDirectory, inputFile)) {
            if (!launchIntkeyViaScript(startupDirectory, inputFile)) {
                launchIntkeyViaClassLoader(inputFile);
            }
        }
    }

    private static boolean launchIntkeyViaExe(String startupDirectory, String inputFile) {
        if (Platform.isWindows()) {
            String exeFile = "Intkey.exe";
            String fullpath = String.format("%s%s%s", startupDirectory, File.separator, exeFile);
            if (launchFile(fullpath, inputFile)) {
                return true;
            }
        }
        return false;
    }

    private static boolean launchIntkeyViaScript(String startupDirectory, String inputFile) {
        String scriptFile = (Platform.isWindows() ? "Intkey.bat" : "Intkey.sh");
        // The DELTA scripts set a System property "basedir" - this is more
        // reliable than the current directory
        // as if the script is on the path the current directory won't be the
        // script directory.
        if (StringUtils.isNotBlank(System.getProperty("basedir"))) {
            String scriptDir = System.getProperty("basedir") + File.separator + "bin" + File.separator + scriptFile;
            if (launchFile(scriptDir, inputFile)) {
                return true;
            }
        }
        String fullpath = String.format("%s%s%s", startupDirectory, File.separator, scriptFile);
        if (!launchFile(fullpath, inputFile)) {
            String path = System.getenv("PATH");
            String[] elements = path.split("\\Q" + File.pathSeparator + "\\E");
            for (String element : elements) {
                fullpath = String.format("%s%s%s", element, File.separator, scriptFile);
                if (launchFile(fullpath, inputFile)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private static boolean launchFile(String path, String args) {
        File f = new File(path);
        if (f.exists()) {
            try {
                Runtime.getRuntime().exec(new String[] { path, args });
                return true;
            } catch (Exception ex) {
            }
        }
        return false;
    }

    private static void launchIntkeyViaClassLoader(String inputFile) throws Exception {
        // Gah.... this is a horrible work around for the fact that
        // the swing application framework relies on a static
        // Application instance so we can't have the Editor and
        // Intkey playing together nicely in the same JVM.
        // It doesn't really work properly anyway, the swing application
        // framework generates exceptions during loading and saving
        // state due to failing instanceof checks.
        String classPath = System.getProperty("java.class.path");
        String[] path = classPath.split(File.pathSeparator);
        List<URL> urls = new ArrayList<URL>();
        for (String pathEntry : path) {
            urls.add(new File(pathEntry).toURI().toURL());
        }
        ClassLoader intkeyLoader = new URLClassLoader(urls.toArray(new URL[0]), ClassLoader.getSystemClassLoader().getParent());
        Class<?> intkey = intkeyLoader.loadClass("au.org.ala.delta.intkey.Intkey");
        Method main = intkey.getMethod("main", String[].class);
        main.invoke(null, (Object) new String[] { inputFile });
    }

    /**
     * Parse a string containing a URL or a file path and return a URL object.
     * 
     * @param input
     *            - the URL or file path
     * @return a URL object. If a file path was supplied, this will be a file
     *         protocol URL pointing to the file.
     * @throws IOException
     */
    public static URL parseURLOrFilePath(String input) throws IOException {
        try {
            URL url = new URL(input);
            return url;
        } catch (MalformedURLException ex) {
            // do nothing - assume this is a regular system file path
        }

        File file = new File(input);
        if (!(file.exists() && file.isFile())) {
            throw new IllegalArgumentException("Invalid input or file does not exist");
        }
        return file.toURI().toURL();
    }

    /**
     * Saves the content of the supplied URL to a temporary file. If the url is
     * a file url, the underlying file is simply returned without creating a
     * temporary file.
     * 
     * @param url
     *            the url
     * @param tempFilePrefix
     *            the prefix to use for the temporary file
     * @param timeout
     *            timeout to use when saving the URL's content to the temporary
     *            file
     * @return If the supplied url is a file url, the underlying file is
     *         returned. Otherwise, a temporary file containing the content is
     *         returned
     * @throws IOException
     */
    public static File saveURLToTempFile(URL url, String tempFilePrefix, int timeout) throws IOException {
        // If the URL is a file protocol url, just return the underlying
        // file.
        if (isFileURL(url)) {
            try {
                return new File(url.toURI());
            } catch (URISyntaxException ex) {
                throw new IllegalArgumentException("Invalid URL", ex);
            }
        }

        // Save the file to a temporary file
        File tempFile = File.createTempFile(tempFilePrefix, null);
        FileUtils.copyURLToFile(url, tempFile, timeout, timeout);
        return tempFile;
    }

    /**
     * Returns true if the supplied URL is using one of the formats supported by
     * open-delta - the supported formats are http, ftp and file.
     * 
     * @param url
     *            the url
     * @return true if the url is one of the supported formats
     */
    public static boolean checkURLValidProtocol(URL url) {
        return (url.getProtocol().equalsIgnoreCase("http") || (url.getProtocol().equalsIgnoreCase("ftp") || (url.getProtocol().equalsIgnoreCase("file"))));
    }

    /**
     * Returns true if the URL is a file URL
     * 
     * @param url
     *            the file URL
     * @return true if the supplied url is a file url
     */
    public static boolean isFileURL(URL url) {
        return url.getProtocol().equalsIgnoreCase("file");
    }
    
    /**
     * Use this method when you want to save a file to a directory but do not want to overwrite an existing file with the same name.
     * @param saveDir The directory that you want to save to
     * @param saveFileName The name that you want to use for the file
     * @return If no file with the specified name exists in the directory, a file with the exact name in the specified directory will be returned. Otherwise,
     * a file with the name specified, but with a number in brackets between the base file name and the extension.
     */
    public static File getSaveFileForDirectory(File saveDir, String saveFileName) {
        if (!saveDir.exists() || !saveDir.isDirectory()) {
            throw new IllegalArgumentException("Save directory does not exist or is not a directory");
        }
        
        // If a file with the exact name specified does not exist in the directory, use a file with the exact name
        File fileWithExactName = new File(saveDir, saveFileName);
        if (!fileWithExactName.exists()) {
            return fileWithExactName;
        }
        
        // Otherwise, look for existing files with the name specified, but with a number in brackets between the
        // base file name and the extension. Use a file with a number in brackets one higher than the highest number appended to 
        // existing files with the same base file name/extension.
        
        String fileExtension = FilenameUtils.getExtension(saveFileName);
        String filenameWithoutExtension = FilenameUtils.getBaseName(saveFileName);
        
        Pattern pattern = Pattern.compile(".*" + filenameWithoutExtension + "\\((\\d+)\\)\\." + fileExtension + "$");
        
        int highestAppendedNumber = 0;
        
        for (File f: saveDir.listFiles()) {
            if (f.isFile()) {
                Matcher matcher = pattern.matcher(f.getAbsolutePath());
                if (matcher.matches()) {
                    String strAppendedNumber = matcher.group(1);
                    highestAppendedNumber = Integer.parseInt(strAppendedNumber);
                }
            }
        }
        
        String modifiedSaveFileName = String.format("%s(%s).%s", filenameWithoutExtension, highestAppendedNumber + 1, fileExtension);
        
        return new File(saveDir, modifiedSaveFileName);
    }
    
}
