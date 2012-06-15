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

import java.awt.Font;
import java.awt.font.TextAttribute;
import java.util.Arrays;
import java.util.Map;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;
import au.org.ala.delta.util.Pair;

/**
 * Maintains settings related to the creation of image and image overlays.
 */
public class VOImageInfoDesc extends VOAnyDesc {

	private ImageInfoFixedData _fixedData;
	private Font[] _overlayFont = new Font[OverlayFontType.values().length];

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

	public void storeQData() {
		makeTemp();
		byte[] trailerBuf = null;
		int trailerLeng = 0;

		// If the size of TFixedData has been increased (due to a newer program version)
		// re-write the whole slot, using the new size.
		if (_fixedData.fixedSize < ImageInfoFixedData.SIZE) {
		      // Save a copy of all "variable" data
		      trailerBuf = dupTrailingData(0);
		      if (trailerBuf != null) {
		    	  trailerLeng = trailerBuf.length;
		      }
		      _dataOffs = SlotFile.SlotHeader.SIZE + ImageInfoFixedData.SIZE; ///// Adjust DataOffs accordingly
		      _fixedData.fixedSize = ImageInfoFixedData.SIZE;
		      // Do seek to force allocation of large enough slot
		      dataSeek(trailerLeng);
		}

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		dataWrite(_fixedData);
		
		if (trailerBuf != null) { // If fixedData was resized, re-write the saved, variable-length data
		    dataSeek(0);
		    dataWrite(trailerBuf);
		    dataTruncate();
		}
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
		synchronized (getVOP()) {
			
			byte[] trailerBuf = null;
			int trailerLeng = 0;
	
			if (imagePath.length() != _fixedData.pathLen) { // Save a copy of any following data!
			    trailerBuf = dupTrailingData(_fixedData.pathLen);
			    if (trailerBuf != null) {
			    	trailerLeng = trailerBuf.length;
			    }
			}
	
			// Seek to force allocation of large enough slot
			dataSeek(imagePath.length() + trailerLeng);
			dataSeek(0);
			dataWrite(stringToBytes(imagePath));
			if (imagePath.length() != _fixedData.pathLen) {
			    setDirty();
			    _fixedData.pathLen = (short)imagePath.length();
			    if (trailerBuf != null) {
			        dataWrite(trailerBuf);
			        dataTruncate();
			    }
			}
		}
	}
	
	public Font getOverlayFontObject(OverlayFontType fontType) {
		if (_overlayFont[fontType.ordinal()] == null) {
		   
		    Pair<LOGFONT, String> fontInfo = readOverlayFont(fontType);
		    if (fontInfo == null && fontType.equals(OverlayFontType.OF_FEATURE)) {
		    	fontInfo = readOverlayFont(OverlayFontType.OF_DEFAULT);
		    }
		    if (fontInfo.getFirst() != null) {
		    	_overlayFont[fontType.ordinal()] = fontInfo.getFirst().toFont();
		    }
		    
		}
        return _overlayFont[fontType.ordinal()];
	}

	public void writeOverlayFont(OverlayFontType fontType, String comment, LOGFONT logFont) {
		synchronized (getVOP()) {
			
		dataSeek(_fixedData.pathLen);
		short commentLen = 0;
		for (int i = 0; i < fontType.ordinal(); ++i) {
		    if (i + 1 > _fixedData.nFonts) {
		        // Fill in "empty" font spaces with zeroes
		        dataWrite(commentLen);
		        
		        LOGFONT nullFont = new LOGFONT();
		        nullFont.write(_slotFile);
		    }
		    else {
		        commentLen = dataReadShort();
		        dataSeek(commentLen + LOGFONT.SIZE, SeekDirection.FROM_CUR);
		    }
		}
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = dataTell();
		// May need to store any "trailing" information
		if (fontType.ordinal() + 1 < _fixedData.nFonts) {
		    commentLen = dataReadShort();
		    dataSeek(commentLen + LOGFONT.SIZE, SeekDirection.FROM_CUR);
		    trailerBuf = dupTrailingData(0, SeekDirection.FROM_CUR);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}
		// Seek to force allocation of large enough slot
		if (comment == null) {
			comment = "";
		}
		commentLen = (short)comment.length();
		dataSeek(startPos + trailerLeng + 2 + commentLen + LOGFONT.SIZE);
		dataSeek(startPos);
		
		dataWrite(commentLen);
		dataWrite(stringToBytes(comment));
		dataWrite(logFont);
		if (trailerBuf != null) {
		    dataWrite(trailerBuf);
		}
		dataTruncate();
		if (_fixedData.nFonts < fontType.ordinal() + 1)  {
		    _fixedData.nFonts = (short)(fontType.ordinal() + 1);
		    setDirty();
		}
		_overlayFont[fontType.ordinal()] = logFont.lfHeight != 0 ? logFont.toFont() : null;
		}
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

	public static class ImageInfoFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 2 + 2 + 2 + 2 + 4;

		public ImageInfoFixedData() {
			super("Img inf Desc");
			this.TypeID = VODescFactory.VOImageInfoDesc_TypeId;
			
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

	public static class LOGFONT implements IOObject {

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
		byte[] lfFaceName = new byte[32];

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
			lfFaceName = file.read(32);
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

		public Font toFont() {
			
			if (lfHeight == 0) {
				return null;
			}
			int style = (lfItalic != 0) ? Font.ITALIC : 0;
			style = style | (lfWeight > 500 ? Font.BOLD : 0); 
			return new Font(BinFileEncoding.decode(lfFaceName), style, Math.abs(lfHeight));
		}
		
		public void fromFont(Font font) {
			
			Map<TextAttribute, ?> attributes = font.getAttributes();
			lfFaceName = BinFileEncoding.encode((String)attributes.get(TextAttribute.FAMILY));
			int style = font.getStyle();
			lfItalic = (style & Font.ITALIC) > 0 ? (byte)1 : (byte)0;
			lfWeight = (Integer)attributes.get(TextAttribute.WEIGHT);
			
		}
		
		public void setLfFaceName(String faceName) {
			if (faceName.length() > 32) {
				throw new IllegalArgumentException("Font family length must be <= 32 bytes");
			}
			lfFaceName = new byte[32];
			Arrays.fill(lfFaceName, (byte)0);
			byte[] tmp = BinFileEncoding.encode(faceName);
			System.arraycopy(tmp, 0, lfFaceName, 0, Math.min(tmp.length, lfFaceName.length));
		}

		public String getLfFaceName() {
			return BinFileEncoding.decode(lfFaceName).trim();
		}
	}

}
