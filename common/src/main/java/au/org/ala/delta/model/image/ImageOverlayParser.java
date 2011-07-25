package au.org.ala.delta.model.image;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;
import au.org.ala.delta.util.Utils;

/**
 * Parses the overlay arguments to the CHARACTER IMAGES, TAXON IMAGES and STARTUP IMAGES
 * directives.
 */
public class ImageOverlayParser {

	enum ParseState {
		TEXT, PATH, // Same as text, but without RTF
		MODIFIER, NOWHERE
	};
	
	public static final int HAS_X = 1;
	public static final int HAS_Y = 2;
	public static final int HAS_W = 4;
	public static final int HAS_H = 8;
	public static final int HAS_ALL_DIMS = (HAS_X | HAS_Y | HAS_W | HAS_H);
	
	private Set<Integer> usedIds = new HashSet<Integer>();

	// Parse a string containing overlay information, and build up the
	// corresponding list of TImageOverlay objects
	// It is assumed that the string includes the surroundings brackets,
	// and any non-space text outside of brackets is considered an error.
	public List<ImageOverlay> parseOverlays(String buffer, int imageType) throws ParseException {
		List<ImageOverlay> overlayList = new ArrayList<ImageOverlay>();
		usedIds.clear();
		String modifiers = "XYWHTNCMPFE";

		int commentLevel = 0;
		int textStart = -1;
		int nHidden = 0;
		// bool inQuote = false;
		ParseState parseState = ParseState.NOWHERE;
		char dims = 0;
		boolean inRTF = false;
		boolean inParam = false;
		boolean inHotspot = false;
		int bracketLevel = 0;
		ImageOverlay anOverlay = new ImageOverlay();
		OverlayLocation olLocation = new OverlayLocation();
		OverlayLocation hsLocation = new OverlayLocation();

		for (int i = 0; i < buffer.length(); ++i) {
			boolean evaluated = false;
			boolean saveIt = false;
			char ch = buffer.charAt(i);

			// If accumulating text, keep track of RTF markup
			if (commentLevel == 1 && parseState == ParseState.TEXT) {
				if (inRTF) {
					++nHidden;
					if (Character.isDigit(ch) || (!inParam && ch == '-')) {
						inParam = true;
					} else if (inParam || !(Character.isLetter(ch))) {
						inParam = inRTF = false;
						if (ch == '\'' && buffer.charAt(i - i) == '\\')
							++nHidden;
						else if (ch != ' ')
							--nHidden;
					}
				} else if (ch == '{') {
					++bracketLevel;
					++nHidden;
				} else if (ch == '}') {
					--bracketLevel;
					++nHidden;
				} else if (ch == '\\') {
					++nHidden;
					inRTF = true;
					inParam = false;
				}
			}
			if (Character.isSpaceChar(ch))
				continue;
			else if (ch == '<' && (i == 0 || buffer.charAt(i - 1) != '|')) {
				if (++commentLevel == 2) {
					if (parseState == ParseState.TEXT && textStart != -1) {
						int textLen = i - textStart - 1;
						if (textLen > 0)
							anOverlay.overlayText += buffer.substring(textStart + 1, textStart + 1 + textLen);
					}
					textStart = i; // Start saving comment string....
				}
				evaluated = true;
			} else if (ch == '>' && (i == 0 || buffer.charAt(i - 1) != '|')) {
				if (--commentLevel < 0)
					throw new ParseException("EIP_UNMATCHED_CLOSEBRACK", i - nHidden);
				if (commentLevel == 0)
					saveIt = true; // used to be goto SaveIt:
				else if (commentLevel == 1)
				// We've finished a nested comment (that is, an overlay comment,
				// not an image comment)
				// so append its text to the current overlay's comment field.
				{
					int textLen = i - textStart - 1;
					if (textLen > 0) {
						if (anOverlay.comment.length() > 0)
							anOverlay.comment += ' ';
						anOverlay.comment += buffer.substring(textStart + 1, textStart + textLen);
					}
					textStart = (parseState == ParseState.TEXT) ? i : -1;
				}
				evaluated = true;
			} else if (commentLevel > 1)
				continue;
			// This bit of code is to simulate the behaviour of a goto label
			// statement
			// in the c++...
			if (evaluated && !saveIt) {
				continue;
			} else if (saveIt || (ch == '@' && (i == 0 || buffer.charAt(i - 1) != '@'))) {
				// SaveIt:
				// Should check and save whatever we have built up
				if ((parseState == ParseState.TEXT || parseState == ParseState.PATH) && textStart != -1) {
					int textLen = i - textStart - 1;
					if (textLen > 0)
						anOverlay.overlayText += buffer.substring(textStart + 1, textStart + textLen);
				}
				textStart = -1;
				if (anOverlay.type != OverlayType.OLNONE) {
					// Only comments lack positioning information...
					// But I think we should perhaps insert an "empty" location
					// for
					// them anyway, just to provide a space to old an ID, so
					// that
					// we can identify them readily
					anOverlay.location.add(0, olLocation);
					if (anOverlay.type != OverlayType.OLCOMMENT && anOverlay.type != OverlayType.OLSOUND
							&& anOverlay.type != OverlayType.OLSUBJECT) {
						if (dims != HAS_ALL_DIMS)
							throw new ParseException("EIP_MISSING_DIMENSIONS", i - nHidden);
						if (inHotspot) {
							anOverlay.location.add(hsLocation);
							inHotspot = false;
						}
					}
					// Assign an ID to every location
					for (OverlayLocation loc : anOverlay.location) {
						loc.ID = getNextId(anOverlay.type);
					}
					overlayList.add(anOverlay);
				}
				anOverlay.clearAll();
				// hsLocation.ClearAll(true);
				olLocation.clearAll();
				dims = 0;
				inHotspot = false;
				// If we jumped here because we hit a closing bracket, tidy up
				// and move on
				if (ch != '@') {
					parseState = ParseState.NOWHERE;
					continue;
				}
				// Read and match the keyword.
				parseState = ParseState.MODIFIER;
				int oldi = i;
				int j = i + 1;
				while (j < buffer.length() && Character.isLetter(buffer.charAt(j)))
					++j;
				String keyWord = buffer.substring(i + 1, --j);
				i = j;
				anOverlay.type = OverlayType.typeFromKeyword(keyWord);

				switch (anOverlay.type) {
				case OverlayType.OLCOMMENT:
				case OverlayType.OLSUBJECT:
				case OverlayType.OLSOUND:
					parseState = ParseState.TEXT;
					if (anOverlay.type == OverlayType.OLSOUND)
						parseState = ParseState.PATH;
					while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
						;
					if (i >= buffer.length())
						throw new ParseException("EIP_DATA_MISSING", i - nHidden);
					textStart = --i;
					dims = HAS_ALL_DIMS;
					break;

				case OverlayType.OLSTATE: {
					if (imageType != ImageType.IMAGE_CHARACTER)
						throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
					int stateNo = readIntegerValue(buffer, ++i, false, false, 10);
					anOverlay.stateId = stateNo;
					break;
				}

				case OverlayType.OLVALUE:
					// In brackets to allow declaration of local variables
				{
					if (imageType != ImageType.IMAGE_CHARACTER)
						throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
					while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
						;
					if (i >= buffer.length())
						throw new ParseException("EIP_DATA_MISSING", i - nHidden);
					String stringStart = buffer.substring(i);
					int pos = 0;

					int[] endPos = new int[1];
					BigDecimal number = Utils.stringToBigDecimal(stringStart, endPos);
					
					int endPtr = endPos[0];
					anOverlay.minVal = number.toPlainString();

					if (endPtr != pos && stringStart.charAt(endPtr) == '-') {
						pos = endPtr + 1;
						number = Utils.stringToBigDecimal(stringStart.substring(pos), endPos);
						endPtr = endPos[0];
						anOverlay.maxVal = number.toPlainString();
					}
					if (endPtr != 0)
						i += --endPtr;
					break;
				}

				case OverlayType.OLKEYWORD: {
					while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
						;
					if (i >= buffer.length())
						throw new ParseException("EIP_DATA_MISSING", i - nHidden);
					boolean quoted = buffer.charAt(i) == '\"';
					int l;
					if (quoted) {
						l = ++i;
						while (l < buffer.length() && buffer.charAt(l) != '\"')
							++l;
					} else {
						l = i;
						while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
							;
					}
					anOverlay.keywords = buffer.substring(i, l);
					i = l;
					break;
				}

				case OverlayType.OLNOTES:
					// Legacy stuff - silently convert NOTES to IMAGENOTES if we
					// are not looking at a character image.
					// Then drop through
					if (imageType != ImageType.IMAGE_CHARACTER)
						anOverlay.type = OverlayType.OLIMAGENOTES;
				case OverlayType.OLOK:
				case OverlayType.OLCANCEL:
				case OverlayType.OLIMAGENOTES:
					olLocation.W = olLocation.H = Short.MIN_VALUE;
					dims = HAS_W | HAS_H;
					break;

				case OverlayType.OLITEM:
					if (imageType != ImageType.IMAGE_TAXON)
						throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
					break;

				case OverlayType.OLFEATURE:
				case OverlayType.OLUNITS:
				case OverlayType.OLENTER:
					if (imageType != ImageType.IMAGE_CHARACTER)
						throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
					break;

				case OverlayType.OLHEADING:
					if (imageType != ImageType.IMAGE_STARTUP)
						throw new ParseException("EIP_BAD_OVERLAY_TYPE", i - nHidden);
					break;

				case OverlayType.OLTEXT:
					break;

				default: // Unmatched "keyword"
					// Should report this condition, but for now, just save it
					// all as a comment.
					anOverlay.type = OverlayType.OLCOMMENT;
					parseState = ParseState.TEXT;
					i = oldi;
					textStart = i - 1;
					dims = HAS_ALL_DIMS;
					break;
				}
			} else if (parseState == ParseState.MODIFIER && modifiers.indexOf(Character.toUpperCase(ch)) >= 0) {
				ch = Character.toUpperCase(ch);

				int val = 0;
				if (ch == 'X' || ch == 'Y' || ch == 'W' || ch == 'H') {
					val = readIntegerValue(buffer, ++i, anOverlay.type == OverlayType.OLUNITS
							&& (ch == 'X' || ch == 'Y'), true, 10);
					if (dims == HAS_ALL_DIMS
							&& (anOverlay.type == OverlayType.OLSTATE || anOverlay.type == OverlayType.OLVALUE || anOverlay.type == OverlayType.OLKEYWORD)) {
						if (inHotspot)
							anOverlay.location.add(hsLocation);
						hsLocation.clearAll();
						inHotspot = true;
						dims = 0;
					}
				}
				switch (ch) {
				case 'X':
					if ((dims & HAS_X) != 0)
						throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
					dims |= HAS_X;
					if (inHotspot)
						hsLocation.X = (short) val;
					else
						olLocation.X = (short) val;
					break;

				case 'Y':
					if ((dims & HAS_Y) != 0)
						throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
					dims |= HAS_Y;
					if (inHotspot)
						hsLocation.Y = (short) val;
					else
						olLocation.Y = (short) val;
					break;

				case 'W':
					if ((dims & HAS_W) != 0)
						throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
					dims |= HAS_W;
					if (inHotspot)
						hsLocation.W = (short) val;
					else
						olLocation.W = (short) val;
					break;

				case 'H':
					if ((dims & HAS_H) != 0)
						throw new ParseException("EIP_DUPLICATE_DIMENSION", i - nHidden);
					dims |= HAS_H;
					if (inHotspot)
						hsLocation.H = (short) val;
					else {
						olLocation.H = (short) val;
						if (val <= 0)
							anOverlay.setIntegeralHeight(true);
					}
					break;

				case 'C':
					anOverlay.setIncludeComments(true);
					break;

				case 'M':
					anOverlay.setCentreText(true);
					break;

				case 'N':
					anOverlay.setOmitDescription(true);
					break;

				case 'P':
					if (!inHotspot)
						throw new ParseException("EIP_NOT_HOTSPOT", i - nHidden);
					hsLocation.setPopup(true);
					break;

				case 'E':
					if (!inHotspot)
						throw new ParseException("EIP_NOT_HOTSPOT", i - nHidden);
					hsLocation.drawType = OLDrawType.ellipse;
					break;

				case 'F':
					val = readIntegerValue(buffer, ++i, false, true, 16);
					hsLocation.setColor(val);
					
					break;

				case 'T':
					// Skip over
					while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
						;

					// Check for '=' sign
					if (i < buffer.length() && buffer.charAt(i) == '=')
						while (++i < buffer.length() && Character.isSpaceChar(buffer.charAt(i)))
							;
					textStart = --i;
					parseState = ParseState.TEXT;
					break;

				default:
					break; // Should never be reached
				}
			} else if (parseState == ParseState.MODIFIER && ch != ',')
				throw new ParseException("EIP_BAD_SYMBOL", i - nHidden);
			else if (commentLevel == 0)
				throw new ParseException("EIP_BAD_SYMBOL", i - nHidden);
			else if (parseState == ParseState.NOWHERE) {
				anOverlay.type = OverlayType.OLCOMMENT;
				parseState = ParseState.TEXT;
				textStart = i - 1;
				dims = HAS_ALL_DIMS;
			}
		}
		return overlayList;
	}
	
	public int getNextId() {
		return getNextId(OverlayType.OLTEXT, true);
	}

	public int getNextId(int overlayType) {
		return getNextId(overlayType, true);
	}

	public int getNextId(int olType, boolean reserve) {
		int i = ImageOverlay.ID_OVERLAY_FIRST;
		if (olType == OverlayType.OLOK)
			i = ImageOverlay.ID_OK;
		else if (olType == OverlayType.OLCANCEL)
			i = ImageOverlay.ID_CANCEL;
		else if (olType == OverlayType.OLNOTES)
			i = ImageOverlay.ID_NOTES;
		else if (olType == OverlayType.OLIMAGENOTES)
			i = ImageOverlay.ID_IMAGE_NOTES;
		else
			while (usedIds.contains(i)) {
				++i;
			}
		if (reserve) {
			usedIds.add(i);
		}

		return i;
	}
	
	protected int readIntegerValue(String buffer, int pos) {
		return readIntegerValue(buffer, pos, false, true, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde) {
		return readIntegerValue(buffer, pos, allowTilde, true, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde, boolean allowEquals) {
		return readIntegerValue(buffer, pos, allowTilde, allowEquals, 10);
	}

	protected int readIntegerValue(String buffer, int pos, boolean allowTilde, boolean allowEquals, int base) {
		// Eat up any leading white-space
		while (pos < buffer.length() && Character.isWhitespace(buffer.charAt(pos))) {
			++pos;
		}

		// Check for '=' sign
		if (allowEquals && pos < buffer.length() && buffer.charAt(pos) == '=')
			while (++pos < buffer.length() && Character.isWhitespace(buffer.charAt(pos)))
				;

		if (pos >= buffer.length()) {
			throw new RuntimeException("EIP_DATA_MISSING:" + pos);
		}

		if (allowTilde && buffer.charAt(pos) == '~')
			return Short.MIN_VALUE;

		String candidate = buffer.substring(pos, buffer.length() - 1);
		int[] endPtr = new int[] { 0 };
		int retVal = Utils.strtol(candidate, endPtr);
		if (endPtr[0] == 0) {
			throw new RuntimeException("Bad symbol: " + candidate);
		} else {
			// pos += --endPtr - string;
		}
		return retVal;
	}
}
