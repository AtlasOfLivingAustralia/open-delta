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

	protected void addTokenPattern(byte token, boolean ignorecase, String... regexes) {
		_patterns.add(new TokenPattern(token, ignorecase, regexes));
	}

	protected void addKeyWords(byte token, boolean ignorecase, String... keywords) {
		StringBuilder strRegex = new StringBuilder();
		int i = 0;
		for (String k : keywords) {
			strRegex.append("\\b").append(k).append("\\b");
			if (i++ < keywords.length - 1) {
				strRegex.append("|");
			}
		}
		addTokenPattern(token, ignorecase, strRegex.toString());
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
	protected byte markTokens(byte token, Segment line, int lineIndex) {
		String full = new String(line.array, line.getBeginIndex(), line.getEndIndex() - line.getBeginIndex());

		String input = full;
		int bcidx = -1;

		if (token == Token.COMMENT2) {
			if (lineContainsBlockCommentEnd(full)) {
				int idx = full.indexOf(getBlockCommentEnd()) + getBlockCommentEnd().length();
				addToken(idx, Token.COMMENT2);
				input = full.substring(idx);
				token = Token.NULL;
			} else {
				addToken(full.length(), token);
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
				matches.add(new TokenMatch(m.start(), m.end() - m.start(), tp.getToken()));
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
						addToken(match.getStart() - lastindex, token);
					}
					addToken(match.getLength(), match.getToken());
					lastindex = match.getStart() + match.getLength();
					token = Token.NULL;
					lastmatch = match;
				}
			}
		}

		if (bcidx >= 0) {
			if (lastindex < bcidx) {
				addToken(bcidx - lastindex, token);
			}
			addToken(full.length() - bcidx, Token.COMMENT2);
			return Token.COMMENT2;
		} else if (lastindex < input.length()) {
			addToken(input.length() - lastindex, token);
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

	public TokenPattern(byte token, boolean ignorecase, String... regexes) {
		StringBuilder strBuf = new StringBuilder();
		for (int i = 0; i < regexes.length; ++i) {
			strBuf.append(regexes[i]);
			if (i < (regexes.length - 1)) {
				strBuf.append("|");
			}
		}
		_token = token;
		_regex = strBuf.toString();
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
}

class TokenMatch {

	private int _start;
	private int _length;
	private byte _token;

	public TokenMatch(int start, int length, byte token) {
		_start = start;
		_length = length;
		_token = token;
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
}
