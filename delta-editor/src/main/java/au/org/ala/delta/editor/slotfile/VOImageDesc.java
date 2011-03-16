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
package au.org.ala.delta.editor.slotfile;

import java.awt.Point;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.util.Utils;

public class VOImageDesc extends VOAnyDesc {

	private ImageFixedData _fixedData;

	public static String[] OLKeywords = new String[] { "text", "item", "feature", "state", "value", "units", "enter", "subject", "sound", "heading", "keyword", "ok", "cancel", "notes", "imagenotes",
			"comment" };

	public VOImageDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();

		assert diskFixedSize == ImageFixedData.SIZE;

		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData = new ImageFixedData();
		_fixedData.read(_slotFile);

		dataSeek(0);

		// Logger.debug("ImageDesc: OwnerID=%d imageType=%d nameLen=%d nOverlays=%d, alignment=%d", _fixedData.ownerId, _fixedData.imageType, _fixedData.nameLen, _fixedData.nOverlays,
		// _fixedData.alignment);
	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOImageDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Image description";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void StoreQData() {
		throw new NotImplementedException();
	}

	public void setOwnerId(int id) {
		_fixedData.ownerId = id;
		setDirty();
	}

	public int getOwnerId() {
		return _fixedData.ownerId;
	}

	public void setImageType(int type) {
		_fixedData.imageType = type;
		setDirty();
	}

	int getImageType() {
		return _fixedData.imageType;
	}

	public void setButtonAlignment(int align) {
		_fixedData.alignment = (short) align;
		setDirty();
	}

	int GetButtonAlignment() {
		return _fixedData.alignment;
	}

	public void parseOverlays(String buffer) {
		throw new NotImplementedException();
	}

	public String readFileName() {
		dataSeek(0);
		return readString(_fixedData.nameLen);
	}

	public void writeFileName(String fileName) {
		throw new NotImplementedException();
	}

	public String getSubjectText() {
		return getSubjectText(true);
	}

	public String getSubjectText(boolean useFilename) {
		List<ImageOverlay> overlays = readAllOverlays();
		String subject = "";
		for (ImageOverlay overlay : overlays) {
			if (overlay.type == OverlayType.OLSUBJECT) {
				subject = overlay.overlayText;
				break;
			}
		}

		if ((subject == null || subject.length() == 0) && useFilename) {
			subject = readFileName();
		}

		return subject;
	}

	public VOImageDesc clone(int newOwnerId) {
		throw new NotImplementedException();
	}

	public List<ImageOverlay> readAllOverlays() {
		List<ImageOverlay> dest = new ArrayList<VOImageDesc.ImageOverlay>();
		usedIds = new HashSet<Integer>();
		if (_fixedData.nOverlays > 0) {
			// Skip over name...
			dataSeek(_fixedData.nameLen);

			for (int i = 0; i < _fixedData.nOverlays; ++i) {
				ImageOverlay anOverlay = readSingleOverlay(true);
				dest.add(anOverlay);
			}
			// Code to cope with legacy situation, where IDs were not stored. This could
			// eventually be discarded.
			boolean newIds = false;
			for (ImageOverlay overlay : dest) {

				if (overlay.location.size() == 0) {
					overlay.location.add(new OverlayLoc());
				}
				for (OverlayLoc loc : overlay.location) {
					if (loc.ID == 0) {
						newIds = true;
						loc.ID = getNextId(overlay.type);
					}
				}
			}
			if (newIds)
				writeAllOverlays(dest);

		}

		return dest;
	}

	public ImageOverlay readOverlay(int Id) {
		int startLoc = getOverlayStart(Id);
		if (startLoc != -1) {
			dataSeek(startLoc);
			return readSingleOverlay();
		}
		return null;
	}

	public OverlayLoc readLocation(int Id) {
		ImageOverlay overlay = readOverlay(Id);

		if (overlay != null) {
			for (OverlayLoc loc : overlay.location) {
				if (loc.ID == Id) {
					return loc;
				}
			}
		}
		return null;
	}

	public void writeAllOverlays(List<ImageOverlay> overlays) {
		throw new NotImplementedException();
	}

	public boolean hasId(int Id) {
		// If we haven't initialized the list of IDs, do so now
		if (usedIds.isEmpty() && _fixedData.nOverlays > 0) {
			readAllOverlays();
		}
		return usedIds.contains(Id);

	}

	public boolean replaceOverlay(ImageOverlay src, boolean frontOnly) {
		throw new NotImplementedException();
	}

	public boolean replaceLocation(OverlayLoc src) {
		throw new NotImplementedException();
	}

	public int insertOverlay(ImageOverlay src) {
		return insertOverlay(src, 0);
	}

	public int insertOverlay(ImageOverlay src, int placeId) {
		throw new NotImplementedException();
	}

	public int insertLocation(OverlayLoc src, int placeId) {
		throw new NotImplementedException();
	}

	public boolean removeOverlay(int Id) {
		throw new NotImplementedException();
	}

	public boolean removeLocation(int Id) {
		throw new NotImplementedException();
	}

	public int getBaseId(int Id) {
		throw new NotImplementedException();
	}

	protected int getOverlayStart(int Id) {
		return getOverlayStart(Id, null);
	}

	protected int getOverlayStart(int Id, int[] basePtr) {
		if (hasId(Id)) {

			dataSeek(_fixedData.nameLen);
			for (int i = 0; i < _fixedData.nOverlays; ++i) {
				int curPos = dataTell();
				// First part gives type of the overlay,
				// followed by the lengths of remaining info
				short[] valBuf = readShortArray(4);
				// Reads in:
				// 0 : overlay type
				// 1 : length of location list
				// 2 : length of overlay text
				// 3 : length of (image level) comment (usually 0)
				// Next part is the location of this overlay, and its hotspots
				// Starts with a 4-byte value. Lowest byte indicates the draw type
				// Highest three bytes contain the ID
				for (int j = 0; j < valBuf[1]; ++j) {
					int aValue = readInt();

					int curId = aValue >> 8;
					if (basePtr != null && basePtr.length > 0 && j == 0) {
						basePtr[0] = curId;
					}
					if (curId == Id) {
						// Found what we were looking for...
						return curPos;
					}
					// Skip over rest of "location" information...
					dataSeek(4 + 2 + 2 + 2 + 2, SeekDirection.FROM_CUR);
				}
				int olType = valBuf[0];
				dataSeek(valBuf[2] + valBuf[3], SeekDirection.FROM_CUR);
				if (olType == OverlayType.OLSTATE)
					dataSeek(4, SeekDirection.FROM_CUR);
				else if (olType == OverlayType.OLVALUE)
					dataSeek(DeltaNumber.size(), SeekDirection.FROM_CUR);
				else if (olType == OverlayType.OLKEYWORD) {
					short textLen = readShort();
					dataSeek(textLen, SeekDirection.FROM_CUR);
				}
			}
		}
		return -1;
	}

	protected ImageOverlay readSingleOverlay() {
		return readSingleOverlay(false);
	}

	protected ImageOverlay readSingleOverlay(boolean isNew) {
		// First part gives type of the overlay,
		// followed by the lengths of remaining info
		short[] valBuf = new short[4];
		for (int i = 0; i < valBuf.length; ++i) {
			valBuf[i] = _slotFile.readShort();
		}
		// Reads in:
		// 0 : overlay type
		// 1 : length of location list
		// 2 : length of overlay text
		// 3 : length of (image level) comment (usually 0)

		ImageOverlay dest = new ImageOverlay();
		dest.type = valBuf[0];

		for (int j = 0; j < valBuf[1]; ++j) {
			OverlayLoc olLoc = new OverlayLoc();

			// Starts with a 4-byte value. Lowest byte indicates the draw type
			// Highest three bytes contain the ID
			// These should perhaps be made separate, but would "break" the
			// original design, which stored only the drawType and not the ID
			int aValue = readInt();
			olLoc.drawType = OLDrawType.fromOrdinal(aValue & 0xFF);
			olLoc.ID = aValue >> 8;

			if (olLoc.ID != 0) {
				// Paranoia about duplicate IDs
				if (!usedIds.contains(olLoc.ID)) {
					usedIds.add(olLoc.ID);
				} else if (isNew) {
					olLoc.ID = 0;
				}
			}
			// DataRead(&olLoc.drawType, sizeof(olLoc.drawType));
			olLoc.flags = readInt();
			olLoc.X = readShort();
			olLoc.Y = readShort();
			olLoc.W = readShort();
			olLoc.H = readShort();

			if (olLoc.H <= 0) {
				olLoc.flags |= ImageOverlay.OL_INTEGRAL_HEIGHT;
			}
			dest.location.add(olLoc);
		}

		dest.overlayText = readString(valBuf[2]);
		dest.comment = readString(valBuf[3]);

		if (dest.type == OverlayType.OLSTATE) {
			dest.stateId = readInt();
		} else if (dest.type == OverlayType.OLVALUE) {
			dest.minVal = readNumber();
			dest.maxVal = readNumber();
		} else if (dest.type == OverlayType.OLKEYWORD) {
			short textLen = readShort();
			dest.keywords = readString(textLen);
		}

		return dest;
	}

	protected int writeSingleOverlay(ImageOverlay src) {
		return writeSingleOverlay(src, false);
	}

	protected int writeSingleOverlay(ImageOverlay src, boolean reuseIds) {
		throw new NotImplementedException();
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

	protected Set<Integer> usedIds;

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

	// Fixed data...

	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int ownerIdOffs = fixedSizeOffs + 2;
	public static final int imageTypeOffs = ownerIdOffs + 4;
	public static final int nameLenOffs = imageTypeOffs + 4;
	public static final int nOverlaysOffs = nameLenOffs + 2;
	public static final int alignmentOffs = nOverlaysOffs + 2;

	public class ImageFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 2 + 2 + 2;

		public ImageFixedData() {
			super("Image Desc");
		}

		short fixedSize = SIZE;
		int ownerId;
		int imageType;
		short nameLen;
		short nOverlays; // Number of overlay blocks; will an unsigned short be large enough?
		short alignment;

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);
			
			fixedSize = b.getShort();
			ownerId = b.getInt();
			imageType = b.getInt();
			nameLen = b.getShort();
			nOverlays = b.getShort();
			alignment = b.getShort();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(ownerId);
			file.write(imageType);
			file.write(nameLen);
			file.write(nOverlays);
			file.write(alignment);
		}

	}

	public class ButtonAlignment {
		public static final int ALIGN_DEFAULT = -1;
		public static final int ALIGN_NONE = 0;
		public static final int ALIGN_VERTICAL = 1;
		public static final int ALIGN_HORIZONTAL = 2;
	}

	public class ImageOverlay {

		public static final int OVERLAY_TYPE_COUNT = OverlayType.LIST_END; // Total number of Overlay types we know about (+ 1)

		public static final int OLOC_FLAG_COLOUR_MASK = 0x00ffffff;
		public static final int OLOC_FLAG_HOTSPOT = 0x01000000;
		public static final int OLOC_FLAG_POPUP = 0x02000000;
		public static final int OLOC_FLAG_COLOUR = 0x04000000;

		public static final byte OL_OMIT_DESCRIPTION = 0x1;
		public static final byte OL_INCLUDE_COMMENTS = 0x2;
		public static final byte OL_CENTER_TEXT = 0x4;
		public static final byte OL_INTEGRAL_HEIGHT = 0x8;

		public static final int OLSHOW_CONTROL = 0x80000000;
		public static final int OLSHOW_HOTSPOT = 0x40000000;
		public static final int OLSHOW_POPUPS = 0x20000000;
		public static final int OLSHOW_ALL = (OLSHOW_CONTROL | OLSHOW_HOTSPOT | OLSHOW_POPUPS);
		public static final int OLSHOW_IDMASK = ~(OLSHOW_CONTROL | OLSHOW_HOTSPOT | OLSHOW_POPUPS);
		public static final int ID_OVERLAY_FIRST = 0x100;

		public static final int ID_OK = 1;
		public static final int ID_CANCEL = 2;
		public static final int ID_NOTES = 8;
		public static final int ID_OUTLINE = 9;
		public static final int ID_BUTTON_BLOCK = 0xA;
		public static final int ID_IMAGE_NOTES = 0XB;

		public int type;
		public String overlayText;
		public String comment;

		public List<OverlayLoc> location; // List of locations - first is that of the "main" overlay
		// object, (required for all overlay objects except comments)
		// then (optionally) those of any associated "hotspots"

		public int stateId; // Might be appropriate to use a "union" here, but
		public DeltaNumber minVal; // since TDeltaNumber has a constructor, it is not allowable
		public DeltaNumber maxVal; // But for any given overlay, depending on type, we really
		public String keywords; // need only 1 of stateId, (minVal && maxVal), or keywords
		public String displayText; // Buffer for constructing entire text string

		public ImageOverlay() {
			this(OverlayType.OLNONE);
		}

		public String toString() {
			return String.format("Overlay: Type=%d, overlayText=%s, Comment=%s, StateID=%d, minVal=%d, maxVal=%d, KeyWords=%s, displayText=%s\nLocations: %s", type, overlayText, comment, stateId,
					minVal, maxVal, keywords, displayText, location);
		}

		public ImageOverlay(int aType) {
			type = aType;
			location = new ArrayList<VOImageDesc.OverlayLoc>();
		}

		// Should "keywords" be a string? eventually I think this information will be a set of
		// group IDs. But this "group" mechanism doesn't yet exist...

		// These next members are used for drawing purposes, rather than for
		// storing actual information about the overlay.
		// TImageWindow* parent; // Pointer to "owner" window
		// void SetParent(TImageWindow* newParent) { Destroy(); parent = newParent; }

		// void Hide(unsigned long id = OLSHOW_ALL);
		// void Show(unsigned long id = OLSHOW_ALL, HDC hDCPaint = 0, TOverlayLoc::visibility how=TOverlayLoc::yes, bool isErasure = false);
		// void Destroy(unsigned long id = OLSHOW_ALL);
		// void Recreate(unsigned long id = OLSHOW_ALL, bool doCreate=true);
		// //void Refresh();
		// void Rescale(unsigned long id = OLSHOW_ALL, unsigned int flags = 0);
		// void Deselect();
		// void ChangeFont();
		// void ValidateControl(TControl* control);
		// OverlayFontType GetFontType();
		// bool HasVisibleElement() const { return !location.empty(); }
		public boolean hasVisibleElement() {
			return !(type == OverlayType.OLCOMMENT || type == OverlayType.OLSUBJECT || type == OverlayType.OLSOUND || location.isEmpty());
		}

		public boolean hasTextBox() {
			throw new NotImplementedException();
		}

		public int getNHotSpots() {
			return Math.max(0, (int) location.size() - 1);
		}

		public boolean IsButton() {
			return type == OverlayType.OLOK || type == OverlayType.OLCANCEL || type == OverlayType.OLNOTES || type == OverlayType.OLIMAGENOTES;
		}

		public int getX() {
			return getX(OLSHOW_CONTROL);
		}

		public int getX(int id) {
			throw new NotImplementedException();
		}

		public int getY() {
			return getY(OLSHOW_CONTROL);
		}

		public int getY(int id) {
			throw new NotImplementedException();
		}

		public int getHeight(int id, double yscale) {
			throw new NotImplementedException();
		}

		public int getWidth(int id) {
			throw new NotImplementedException();
		}

		public OverlayLoc getLocation(int Id) {
			throw new NotImplementedException();
		}

		// TControl* GetBaseControl() const;
		public int containsPointInHotspot(Point testPt) {
			throw new NotImplementedException();
		}

		public boolean containsId(int id) {
			throw new NotImplementedException();
		}

		public boolean canSelect() {
			return type == OverlayType.OLSTATE || type == OverlayType.OLVALUE;
		}

		public String getValueString() {
			throw new NotImplementedException();
		}

		public String getDisplayText() {
			throw new NotImplementedException();
		}

		public void updateText() {
			throw new NotImplementedException();
		}

		// static const char const * punct;

		public void clearAll() {
			overlayText = "";
			comment = "";
			location.clear();
			keywords = "";
			minVal.setFromValue(0);
			maxVal.setFromValue(0);

			stateId = 0;
			type = OverlayType.OLNONE;
		}

	}

	public class OverlayLoc {

		public OLDrawType drawType;
		public int flags;
		public int ID;
		short X;
		short Y;
		short W;
		short H;

		public OverlayLoc() {
		}

		public void clearAll() {
			drawType = OLDrawType.Unknown;
			ID = flags = 0;
			X = Y = W = H = 0;
		}

		@Override
		public String toString() {
			return String.format("OverlayLoc: drawType=%s, flags=%d, ID=%d, X=%d, Y=%d, W=%d, H=%d", drawType, flags, ID, X, Y, W, H);
		}

	}

	public enum OLDrawType {
		Unknown, frame, rectangle, line, arrow, ellipse;

		public static OLDrawType fromOrdinal(int ord) {
			return values()[ord];
		}
	}

	public class OverlayType {
		// NOTE! Changes here must be made to both array OLKeywords and enum OverlayType
		// They MUST have entries "in parallel".
		// The negative values are used for "unnamed" overlay types, that are used
		// internally for editing.
		public static final int OLBUTTONBLOCK = -3; // Used only when modifying aligned push-buttons
		public static final int OLHOTSPOT = -2; // Not a "real" overlay type; used for convenience in editing
		public static final int OLNONE = -1; // Undefined; the remaining values MUST correspond
												// with array OLKeywords.
		public static final int OLTEXT = 0; // Use a literal text string
		public static final int OLITEM = 1; // Use name of the item
		public static final int OLFEATURE = 2; // Use name of the character
		public static final int OLSTATE = 3; // Use name of the state (selectable)
		public static final int OLVALUE = 4; // Use specified values or ranges (selectable)
		public static final int OLUNITS = 5; // Use units (for numeric characters)
		public static final int OLENTER = 6; // Create edit box for data entry
		public static final int OLSUBJECT = 7; // Has text for menu entry
		public static final int OLSOUND = 8; // Has name of .WAV sound file
		public static final int OLHEADING = 9; // Using heading string for the data-set
		public static final int OLKEYWORD = 10; // Use specified keyword(s)
		public static final int OLOK = 11; // Create OK pushbutton
		public static final int OLCANCEL = 12; // Create Cancel pushbutton
		public static final int OLNOTES = 13; // Create Notes pushbutton (for character notes)
		public static final int OLIMAGENOTES = 14; // Create Notes pushbutton (for notes about the image)
		public static final int OLCOMMENT = 15; // Not a "real" overlay type, but used to save comments addressed
		// to images rather than overlays
		public static final int LIST_END = 16; // Insert new overlay types just BEFORE this!

	}

}
