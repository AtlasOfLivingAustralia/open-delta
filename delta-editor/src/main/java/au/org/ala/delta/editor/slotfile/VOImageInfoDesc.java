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

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.util.Pair;

public class VOImageInfoDesc extends VOAnyDesc {

	private ImageInfoFixedData _fixedData;

	public VOImageInfoDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		synchronized (getVOP()) {
			_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
			short diskFixedSize = _slotFile.readShort();
			assert diskFixedSize == ImageInfoFixedData.SIZE;
			_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
			_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
			_fixedData = new ImageInfoFixedData();
			_fixedData.read(_slotFile);

			// Logger.debug("ImageInfoDesc: pathLen=%d, nFonts=%d, alignment=%d, overlayDefs=%x, hotspotDefs=%x", _fixedData.pathLen, _fixedData.nFonts, _fixedData.alignment, _fixedData.overlayDefs,
			// _fixedData.hotspotDefs);

			dataSeek(0);
		}
	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOImageInfoDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Image information";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void StoreQData() {
		throw new NotImplementedException();
	}

	public short getButtonAlignment() {
		return _fixedData.alignment;
	}

	public void setButtonAlignment(short align) {
		_fixedData.alignment = align;
		setDirty();
	}

	public int getHotspotDefaults() {
		return _fixedData.hotspotDefs;
	}

	public void setHotspotDefaults(int value) {
		_fixedData.hotspotDefs = value;
		setDirty();
	}

	public short getOverlayDefaults() {
		return _fixedData.overlayDefs;
	}

	public void setOverlayDefaults(short value) {
		_fixedData.overlayDefs = value;
		setDirty();
	}

	public short getNFonts() {
		return _fixedData.nFonts;
	}

	public String readImagePath() {
		dataSeek(0);
		return readString(_fixedData.pathLen);
	}

	public Pair<LOGFONT, String> readOverlayFont(OverlayFontType fontType) {
		synchronized (getVOP()) {
			dataSeek(_fixedData.pathLen);
			String comment = null;
			LOGFONT font = null;
			if (_fixedData.nFonts > fontType.ordinal()) {
				// Skip preceding fonts...
				for (int i = 0; i < fontType.ordinal(); ++i) {
					int commentLen = readShort();
					comment = readString(commentLen);
					readBytes(LOGFONT.SIZE);
					// dataSeek(commentLen + LOGFONT.SIZE, SeekDirection.FROM_CUR);
				}
				int commentLen = readShort();
				if (commentLen != 0) {
					comment = readString(commentLen);
				} else {
					comment = "";
				}
				font = new LOGFONT();
				font.read(_slotFile);
			}

			return new Pair<LOGFONT, String>(font, comment);
		}
	}

	public void writeImagePath(String imagePath) {
		throw new NotImplementedException();
	}

	public void writeOverlayFont(OverlayFontType fontType, String comment, LOGFONT logFont) {
		throw new NotImplementedException();
	}

	// TFont* GetOverlayFontObject(const OverlayFontType fontType);
	// protected:
	// TFont* overlayFont[FontTypeCount];

	// Fixed data stuff
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int pathLenOffs = fixedSizeOffs + 2;
	public static final int nFontsOffs = pathLenOffs + 2;
	public static final int alignmentOffs = nFontsOffs + 2;
	public static final int overlayDefsOffs = alignmentOffs + 2;
	public static final int hotspotDefsOffs = overlayDefsOffs + 2;

	public class ImageInfoFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 2 + 2 + 2 + 2 + 4;

		public ImageInfoFixedData() {
			super("Img inf Desc");
		}

		public short fixedSize = SIZE;
		public short pathLen; // Length of the IMAGEPATH
		public short nFonts; // Number of overlay fonts
		public short alignment; // Default button alignment
		public short overlayDefs; // Default flags for new overlays
		public int hotspotDefs; // Default settings for new hotspots

		@Override
		public void read(BinFile file) {
			super.read(file);
			fixedSize = file.readShort();
			pathLen = file.readShort();
			nFonts = file.readShort();
			alignment = file.readShort();
			overlayDefs = file.readShort();
			hotspotDefs = file.readInt();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(pathLen);
			file.write(nFonts);
			file.write(alignment);
			file.write(overlayDefs);
			file.write(hotspotDefs);
		}

	}

	public enum OverlayFontType {
		OF_DEFAULT, OF_BUTTON, OF_FEATURE;
		// OL_FONT_LIST_END;

		public static OverlayFontType fromOrdinal(int ord) {
			return values()[ord];
		}

	};

	public class LOGFONT implements IOObject {

		public static final int SIZE = 4 + 4 + 4 + 4 + 4 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 32;

		public int lfHeight;
		public int lfWidth;
		public int lfEscapement;
		public int lfOrientation;
		public int lfWeight;
		public byte lfItalic;
		public byte lfUnderline;
		public byte lfStrikeOut;
		public byte lfCharSet;
		public byte lfOutPrecision;
		public byte lfClipPrecision;
		public byte lfQuality;
		public byte lfPitchAndFamily;
		public byte[] lfFaceName = new byte[32];

		@Override
		public void read(BinFile file) {
			lfHeight = file.readInt();
			lfWidth = file.readInt();
			lfEscapement = file.readInt();
			lfOrientation = file.readInt();
			lfWeight = file.readInt();

			lfItalic = file.readByte();
			lfUnderline = file.readByte();
			lfStrikeOut = file.readByte();
			lfCharSet = file.readByte();
			lfOutPrecision = file.readByte();
			lfClipPrecision = file.readByte();
			lfQuality = file.readByte();
			lfPitchAndFamily = file.readByte();
			lfFaceName = file.readBytes(32);
		}

		@Override
		public void write(BinFile file) {
			file.write(lfHeight);
			file.write(lfWidth);
			file.write(lfEscapement);
			file.write(lfOrientation);
			file.write(lfWeight);
			file.write(lfItalic);
			file.write(lfUnderline);
			file.write(lfStrikeOut);
			file.write(lfCharSet);
			file.write(lfOutPrecision);
			file.write(lfClipPrecision);
			file.write(lfQuality);
			file.write(lfPitchAndFamily);
			file.write(lfFaceName);
		}
		
		@Override
		public int size() {
			return SIZE;
		}

		@Override
		public String toString() {
			return String.format("LOGFONT: %s", new String(lfFaceName));
		}

	}

}
