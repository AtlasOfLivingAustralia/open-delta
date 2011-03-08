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
package au.org.ala.delta.slotfile;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.util.Utils;

public class VOItemDesc extends VOImageHolderDesc implements INameHolder {

	public static final byte ITEM_VARIANT = 0x1; // Character states are
													// exclusive

	public static final int CHUNK_STOP = 0; // Marker for end of list
	public static final int CHUNK_TEXT = 1; // Text (a "comment")
	public static final int CHUNK_STATE = 2; // A state ID
	public static final int CHUNK_NUMBER = 3; // A "normal" numeric value
	public static final int CHUNK_EXLO_NUMBER = 4; // An extreme low numeric
													// value
	public static final int CHUNK_EXHI_NUMBER = 5; // An extreme high numeric
													// value
	public static final int CHUNK_VARIABLE = 6; // The "Variable" pseudo-value
	public static final int CHUNK_UNKNOWN = 7; // The "Unknown" pseudo-value
	public static final int CHUNK_INAPPLICABLE = 8; // The "Inapplicable"
													// pseudo-value
	public static final int CHUNK_OR = 9; // Connective "or"
	public static final int CHUNK_AND = 10; // Connective "and"
	public static final int CHUNK_TO = 11; // Connective "to"
	public static final int CHUNK_LONGTEXT = 12; // Text longer than 0xffff in
													// length.

	/**
	 * Holds the offset (in bytes) into the attribute data for each character.
	 * Key: character uid Value: the offset into the attribute data.
	 * 
	 * Note that there are two "special" character uids: VOUID_NAME: the
	 * attribute name VOUID_DELETED: marks a block as deleted. (the original
	 * code used a multimap which allowed multiple deleted blocks in the same
	 * map, here we track deleted attributes using the _deletedAttributes
	 * field.).
	 */
	private Map<Integer, Integer> _attributeMap = new TreeMap<Integer, Integer>();

	/**
	 * Holds a list of offsets that have been deleted TODO work out the
	 * implications of splitting this structure.
	 */
	private List<Integer> _deletedAttributes = new ArrayList<Integer>();

	private ItemFixedData _fixedData;
	private String _ansiName;

	protected int _attrOffset;

	public VOItemDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);

		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();

		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);

		_fixedData = new ItemFixedData();
		if (diskFixedSize < ItemFixedData.SIZE) {
			_fixedData.legacyRead(_slotFile);
		}
		else {
			_fixedData.read(_slotFile);
		}
		
		// Logger.debug("ItemDesc: UniId=%d nBlocks=%d, itemFlags=%x, attribEnd=%d, nImages=%d",
		// _fixedData.UniId, _fixedData.nBlocks, _fixedData.itemFlags,
		// _fixedData.attribEnd, _fixedData.nImages);

		// For old data sets, the Fixed Data portion of an item is shorter (50 bytes) than the newer
		// format (59 bytes) - this corresponds to attribEnd, nImages itemFlags.  Hence we need to correct
		// these values.
		if ((_fixedData.attribEnd == 0) && _fixedData.nBlocks > 0) {
			_fixedData.attribEnd = getDataSize();
		}

		int mapStart = 0;
		dataSeek(mapStart);

		_attrOffset = mapStart + (_fixedData.nBlocks * 8);
		
		ByteBuffer b = _slotFile.readByteBuffer(_fixedData.nBlocks * (4 + 4));
		
		for (int i = 0; i < _fixedData.nBlocks; ++i) {
			
			int uid = b.getInt();
			int attr = b.getInt();

			if (uid == VOUID_DELETED) {
				_deletedAttributes.add(attr);
			} else {
				_attributeMap.put(uid, attr);
			}
		}

		_ansiName = readAttributeAsText(VOUID_NAME, TextType.RTF, 0);

		if (_ansiName != null) {
			adjustAnsiName();
		}

	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOItemDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Item description";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void adjustAnsiName() {
		_ansiName = Utils.RTFToANSI(_ansiName);
	}

	@Override
	public String getAnsiName() {
		return _ansiName;
	}

	// Write the cached data
	public void storeQData() {
		makeTemp();
		List<Attribute> attribList = readAllAttributes();
		writeAllAttributes(attribList, false);

		byte[] trailerBuf = null;
		int trailerLeng = 0;

		// If the size of TFixedData has been increased (due to a newer program
		// version)
		// re-write the whole slot, using the new size.
		if (_fixedData.fixedSize < FixedData.SIZE) {
			// Save a copy of all "variable" data
			trailerBuf = dupTrailingData(0);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
			_dataOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE; // /// Adjust
																	// DataOffs
																	// accordingly
			_fixedData.fixedSize = FixedData.SIZE;
			// Do seek to force allocation of large enough slot
			dataSeek(trailerLeng);
		}

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.write(_slotFile);

		if (trailerBuf != null) { // If fixedData was resized, re-write the
									// saved, variable-length data

			dataSeek(0);
			dataWrite(trailerBuf);
			dataTruncate();

		}
	}

	public int getImageType() {
		return ImageType.IMAGE_TAXON;
	}

	public int getNDefinedAttributes() {
		return _attributeMap.size();
	}

	public int getNImages() {
		return _fixedData.nImages;
	}

	public boolean isVariant() {
		return (_fixedData.itemFlags & ITEM_VARIANT) != 0;
	}

	public void setVariant(boolean isVariant) {
		if (isVariant) {
			_fixedData.itemFlags |= ITEM_VARIANT;
		} else {
			_fixedData.itemFlags &= ~ITEM_VARIANT;
			setDirty();
		}
	}

	public Set<Integer> getEncodedChars() {
		Set<Integer> charSet = new HashSet<Integer>();
		for (int charId : _attributeMap.keySet()) {
			charSet.add(charId);
		}
		return charSet;
	}

	public boolean hasAttribute(int charId) {
		return _attributeMap.containsKey(charId);
	}

	public boolean hasAttributeData(VOCharBaseDesc charBase) {
		int charId = charBase.getUniId();

		Attribute attr = readAttribute(charId);
		if (attr == null)
			return false;
		else if (CharType.isText(charBase.getCharType()))
			return true;
		else
			return (!attr.isTextOnly());

	}

	public String readAttributeAsText(int charId, TextType textType) {
		return readAttributeAsText(charId, textType, 0);
	}

	public String readAttributeAsText(int charId, TextType textType,
			int showComments) {

		StringBuffer dest = new StringBuffer();
		Attribute attr = readAttribute(charId);
		if (attr != null) {
			if (charId == VOUID_NAME) {
				// if (deltaDoc && showComments)
				dest.append(Utils.removeComments(
						attr.begin().get().getString(), showComments));
			} else {

				VOCharBaseDesc charBase = (VOCharBaseDesc) getVOP()
						.getDescFromId(charId);
				for (AttrChunk chunk : attr) {
					if (showComments == 0 || !chunk.isTextChunk()
							|| CharType.isText(charBase.getCharType())) {
						dest.append(chunk.getAsText(charBase));
					}
				}

			}

			if (textType == TextType.ANSI) {
				return Utils.RTFToANSI(dest.toString());
			} else if (textType == TextType.UTF8) {
				return new String(Utils.RTFToUTF8(dest.toString()));
			}

		}

		return dest.toString();
	}

	public Attribute readAttribute(int charId) {

		if (!_attributeMap.containsKey(charId)) {
			return null;
		}

		Attribute attr = new Attribute();

		dataSeek(_attrOffset + _attributeMap.get(charId));
		int blockLeng = _slotFile.readInt();
		if (blockLeng > 0) {
			byte[] data = _slotFile.read(blockLeng);
			attr.setCharId(charId);
			attr.setData(data);
			attr.initReadData();
		}

		return attr;

	}

	public List<Attribute> readAllAttributes() {
		List<Attribute> list = new ArrayList<Attribute>(_attributeMap.size());

		for (int charId : _attributeMap.keySet()) {
			Attribute attr = new Attribute(charId);
			dataSeek(_attrOffset + _attributeMap.get(charId));
			int blockLeng = _slotFile.readInt();
			byte[] data = _slotFile.read(blockLeng);
			attr.setData(data);
			attr.initReadData();
			list.add(attr);
		}

		return list;
	}

	public List<Integer> readImageList() {
		List<Integer> dest = new ArrayList<Integer>();

		dataSeek(_fixedData.attribEnd /* 0 */);

		ByteBuffer b = readBuffer(_fixedData.nImages * 4);
		for (int i = 0; i < _fixedData.nImages; ++i) {
			dest.add(b.getInt());
		}
		return dest;

	}

	public void writeAttribute(Attribute attrib) {
		int characterId = attrib.getCharId();
		if (attrib.size() == 0) {
			deleteAttribute(characterId);
			return;
		}

		Integer offset = _attributeMap.get(characterId);
		int needSize = attrib.getDataLength();
		if (offset != null) {

			dataSeek(_attrOffset + offset);
			int oldBlockLength = dataReadInt();
			if (needSize <= oldBlockLength) { // Just use the old block if the
												// data will fit.
				dataWrite(attrib.getData());
				setDirty();
				if (characterId == VOUID_NAME) {
					_ansiName = attrib.begin().get().getString();
					adjustAnsiName();
				}
				return;
			} else { // The old block won't fit the new data, mark it as
						// deleted.
				_attributeMap.remove(characterId);
				_deletedAttributes.add(offset);
			}
		}

		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int curSize = Integer.MAX_VALUE;
		int bestOffset = -1;
		for (int tmpOffset : _deletedAttributes) {
			dataSeek(_attrOffset + tmpOffset);
			int blockLength = dataReadInt();
			if ((blockLength >= needSize) && (blockLength < curSize)) {
				curSize = blockLength;
				bestOffset = tmpOffset;
			}
		}
		if (bestOffset != -1) {
			dataSeek(_attrOffset + bestOffset + SIZE_OF_INT_IN_BYTES);
			_deletedAttributes.remove(bestOffset);
		} else { // Couldn't find a suitable free block.
			trailerBuf = dupTrailingData(_fixedData.attribEnd);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
			dataSeek(_fixedData.attribEnd + needSize + SIZE_OF_INT_IN_BYTES
					+ trailerLeng);
			dataSeek(_fixedData.attribEnd);
			_fixedData.attribEnd += needSize + SIZE_OF_INT_IN_BYTES;
			dataWrite(needSize);
		}
		int startPos = dataTell() - _attrOffset - SIZE_OF_INT_IN_BYTES;
		dataWrite(attrib.getData());
		_attributeMap.put(characterId, startPos);
		setDirty();
		if (trailerBuf != null) {
			dataWrite(trailerBuf);
			dataTruncate();
		}

		if (characterId == VOUID_NAME) {
			_ansiName = attrib.begin().get().getString();
			adjustAnsiName();
		}
	}

	public void deleteAttribute(int charId) {
		Integer offset = _attributeMap.get(charId);
		if (offset != null) {
			_attributeMap.remove(charId);
			_deletedAttributes.add(offset);

			if (charId == VOUID_NAME) {
				_ansiName = "";
			}
			setDirty();
		}
	}

	/**
	 * TODO right now our data structure won't support duplicate attributes.
	 * It's a bit of a worry that this gets called with removeDups = false. need
	 * to track down why and try & handle it.
	 * 
	 * @param attribList
	 * @param removeDups
	 */
	public void writeAllAttributes(List<Attribute> attribList,
			boolean removeDups) {
		makeTemp();
		Map<Integer, Integer> newAttribMap = new LinkedHashMap<Integer, Integer>();

		int startPos = 0;
		// if (removeDups) {
		// // When there are "duplicates" (multiple encodings for the
		// // same character), we want to keep the LAST entry, so first we
		// // reverse the list, so that the last become the first.
		// std::reverse(attribList.begin(), attribList.end());
		// // Then use "stable_sort" to bring the "duplicates" together
		// std::stable_sort(attribList.begin(), attribList.end());
		// // Finally use "unique" to discard the "duplicates"
		// i = std::unique(attribList.begin(), attribList.end(),
		// CompareAttribChar);
		// attribList.erase(i, attribList.end());
		// }
		for (Attribute attribute : attribList) {
			if (attribute.size() > 0) { // Don't add empty attributes
				newAttribMap.put(attribute.getCharId(), startPos);
				startPos += 4 /* size of int */+ attribute.getDataLength();
			}
		}
		_attrOffset = newAttribMap.size() * 8 /*
											 * one int for the key, one for the
											 * value
											 */;

		byte[] trailerBuf = null;
		int trailerLeng = 0;

		boolean sizeChange = _attrOffset + startPos != _fixedData.attribEnd;
		if (sizeChange) {
			trailerBuf = dupTrailingData(_fixedData.attribEnd);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		// Seek to force allocation of large enough slot
		dataSeek(_attrOffset + startPos + trailerLeng);
		_fixedData.attribEnd = _attrOffset + startPos;

		// // These steps ensure that "fixedData" is written as well,
		// // allowing us to clear the 'dirty' flag.
		_fixedData.nBlocks = newAttribMap.size();
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.write(_slotFile);

		dataSeek(0);
		for (int id : newAttribMap.keySet()) {
			dataWrite(id);
			dataWrite(newAttribMap.get(id));
		}

		for (Attribute attribute : attribList) {
			if (attribute.size() > 0) // Don't add empty attributes
			{
				int blockLeng = attribute.getDataLength();
				dataWrite(blockLeng);
				dataWrite(attribute.getData());
			}
		}
		_attributeMap = newAttribMap;

		if (trailerBuf != null) {
			dataWrite(trailerBuf);
		}

		if (sizeChange) {
			dataTruncate();
		}

		_ansiName = readAttributeAsText(VOUID_NAME, TextType.RTF);
		if (_ansiName != null) {
			adjustAnsiName();
		}

		setDirty(false);

	}

	public boolean writeImageList(List<Integer> imageList) {
		throw new NotImplementedException();
	}

	public void deleteImage(int imageId) {
		throw new NotImplementedException();
	}

	// Fixed data and offsets
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE
			+ FixedData.SIZE + 0;
	public static final int nBlocksOffs = SlotFile.SlotHeader.SIZE
			+ FixedData.SIZE + 2;
	public static final int attribEndOffs = SlotFile.SlotHeader.SIZE
			+ FixedData.SIZE + 4;
	public static final int nImagesOffs = SlotFile.SlotHeader.SIZE
			+ FixedData.SIZE + 4;
	public static final int nFlagsOffs = SlotFile.SlotHeader.SIZE
			+ FixedData.SIZE + 4;

	public class ItemFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 4 + 1;
		
		public ItemFixedData() {
			super("Item Desc");
			this.TypeID = VODescFactory.VOItemDesc_TypeId;
		}

		public short fixedSize;
		public int nBlocks;
		public int attribEnd;
		public int nImages;
		public byte itemFlags;

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);
			
			fixedSize = b.getShort();
			nBlocks = b.getInt();
			attribEnd = b.getInt();
			nImages = b.getInt();
			itemFlags = b.get();			
		}
		
		// To support older DELTA files that did not have attribEnd, nImages or itemFlags fields.
		public void legacyRead(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(6);
			fixedSize = b.getShort();
			nBlocks = b.getInt();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(nBlocks);
			file.write(attribEnd);
			file.write(nImages);
			file.write(itemFlags);
		}

	}

}
