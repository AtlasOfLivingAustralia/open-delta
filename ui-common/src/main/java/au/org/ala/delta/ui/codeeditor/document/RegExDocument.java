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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.Segment;

import au.org.ala.delta.ui.codeeditor.Token;

public abstract class RegExDocument extends TextDocument {

	private static final long serialVersionUID = 1L;

	private List<TokenPattern> _patterns = new ArrayList<TokenPattern>();

	protected void addTokenPattern(byte token, Object tag, boolean ignorecase, String... regexes) {
		_patterns.add(new TokenPattern(token, tag, ignorecase, regexes));
	}

	protected void addKeyWords(byte token, Object tag, boolean ignorecase, String... keywords) {
		StringBuilder strRegex = new StringBuilder();
		int i = 0;
		for (String k : keywords) {
			strRegex.append("\\b").append(k).append("\\b");
			if (i++ < keywords.length - 1) {
				strRegex.append("|");
			}
		}
		addTokenPattern(token, tag, ignorecase, strRegex.toString());
	}

	public abstract String getBlockCommentStart();

	public abstract String getBlockCommentEnd();

	private boolean lineContainsBlockCommentEnd(String line) {
		String end = getBlockCommentEnd();
		if (line.contains(end)) {
			return true;
		}

		if ("\n".equals(end)) {
			if (line.trim().length() == 0) {
				return true;
			}
		}

		return false;
	}
	
	@Override
	protected byte markTokens(byte token, Segment line, int lineIndex, ITokenAccumulator tokAcc) {
		String full = new String(line.array, line.getBeginIndex(), line.getEndIndex() - line.getBeginIndex());

		String input = full;
		int bcidx = -1;

		if (token == Token.COMMENT2) {
			if (lineContainsBlockCommentEnd(full)) {
				int idx = full.indexOf(getBlockCommentEnd()) + getBlockCommentEnd().length();
				tokAcc.addToken(bcidx, idx, Token.COMMENT2, null);
				input = full.substring(idx);
				token = Token.NULL;
			} else {
				tokAcc.addToken(bcidx, full.length(), token, null);
				return token;
			}
		} else {
			if (full.contains(getBlockCommentStart()) && !full.contains(getBlockCommentEnd())) {

				if (full.indexOf(getLineComment()) < 0 || (full.indexOf(getLineComment()) >= full.indexOf(getBlockCommentStart()))) {
					bcidx = full.indexOf(getBlockCommentStart());
					input = full.substring(0, bcidx);
				}
			}
		}

		ArrayList<TokenMatch> matches = new ArrayList<TokenMatch>();
		for (TokenPattern tp : _patterns) {
			Matcher m = tp.getPattern().matcher(input);
			while (m.find()) {
				matches.add(new TokenMatch(m.start(), m.end() - m.start(), tp.getToken(), tp.getTag()));
			}
		}
		TokenMatch[] sorted = matches.toArray(new TokenMatch[matches.size()]);
		Arrays.sort(sorted, TokenMatchComparator.getInstance());

		int lastindex = 0;
		// Now add the tokens to the editor control - also removing any intersecting tokens
		if (sorted.length > 0) {
			TokenMatch lastmatch = null;
			for (int i = 0; i < sorted.length; ++i) {
				TokenMatch match = sorted[i];
				if (lastmatch != null && (match.getStart() < lastmatch.getStart() + lastmatch.getLength() || match.getStart() == lastmatch.getStart())) {
				} else {
					if (match.getStart() != lastindex) {						
						tokAcc.addToken(match.getStart(), match.getStart() - lastindex, token, match.getTag());
					}
					tokAcc.addToken(match.getStart(), match.getLength(), match.getToken(), match.getTag());
					lastindex = match.getStart() + match.getLength();
					token = Token.NULL;
					lastmatch = match;
				}
			}
		}

		if (bcidx >= 0) {
			if (lastindex < bcidx) {
				tokAcc.addToken(bcidx, bcidx - lastindex, token, null);
			}
			tokAcc.addToken(bcidx, full.length() - bcidx, Token.COMMENT2, null);
			return Token.COMMENT2;
		} else if (lastindex < input.length()) {
			tokAcc.addToken(bcidx, input.length() - lastindex, token, null);
		}
		return token;
	}

}

class TokenMatchComparator implements Comparator<TokenMatch> {

	private static TokenMatchComparator _instance = new TokenMatchComparator();

	public static TokenMatchComparator getInstance() {
		return _instance;
	}

	public int compare(TokenMatch o1, TokenMatch o2) {
		int ret = o1.getStart() - o2.getStart();
		if (ret == 0) {
			return o2.getLength() - o1.getLength();
		}
		return ret;
	}

}

class TokenPattern {

	private String _regex;
	private byte _token;
	private Pattern _pattern;
	private Object _tag;

	public TokenPattern(byte token, Object tag, boolean ignorecase, String... regexes) {
		StringBuilder strBuf = new StringBuilder();
		for (int i = 0; i < regexes.length; ++i) {
			strBuf.append(regexes[i]);
			if (i < (regexes.length - 1)) {
				strBuf.append("|");
			}
		}
		_token = token;
		_regex = strBuf.toString();
		_tag = tag;
		if (ignorecase) {
			_pattern = Pattern.compile(_regex, Pattern.CASE_INSENSITIVE);
		} else {
			_pattern = Pattern.compile(_regex);
		}
	}

	public String getRegex() {
		return _regex;
	}

	public byte getToken() {
		return _token;
	}

	public Pattern getPattern() {
		return _pattern;
	}
	
	public Object getTag() {
		return _tag;
	}
		
}

class TokenMatch {

	private int _start;
	private int _length;
	private byte _token;
	private Object _tag;

	public TokenMatch(int start, int length, byte token, Object tag) {
		_start = start;
		_length = length;
		_token = token;
		_tag = tag;
	}

	public int getStart() {
		return _start;
	}

	public int getLength() {
		return _length;
	}

	public byte getToken() {
		return _token;
	}
	
	public Object getTag() {
		return _tag;
	}
}


