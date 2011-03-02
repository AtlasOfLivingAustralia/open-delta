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

import java.util.Collection;
import java.util.List;

import au.org.ala.delta.slotfile.SlotFile.SlotHeader;

public class VODeltaMasterDesc extends VONoteDesc {

	public static final short DELTA_MAJOR_VERSION = 0;
	public static final short DELTA_MINOR_VERSION = 1;
	
	private MasterFixedData _fixedData;

	private List<Integer> _charVector;
	private List<Integer> _itemVector;
	private List<Integer> _langVector;
	private List<Integer> _contAttrVector;
	private List<Integer> _dirFileVector;

	public VODeltaMasterDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		_fixedData = new MasterFixedData("DELTA MASTER");

		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();
		assert diskFixedSize == MasterFixedData.SIZE;

		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.read(_slotFile);

		if (_fixedData.majorVersion > 0) {
			throw new RuntimeException("Bad major version number: " + _fixedData.majorVersion);
		}

		if (_fixedData.minorVersion > 1) {
			throw new RuntimeException("Bad minor version number:" + _fixedData.minorVersion);
		}

		// Logger.debug("nChars: %d", _fixedData.nChars);
		// Logger.debug("nItems: %d", _fixedData.nItems);
		// Logger.debug("nLangs: %d", _fixedData.nLangs);
		// Logger.debug("nDirFiles: %d", _fixedData.nDirFiles);
		// Logger.debug("nContAttrs: %d", _fixedData.nContAttrs);

		_charVector = readIntArrayToList(_fixedData.nChars);
		// Logger.debug("charVector: %s", _charVector);

		_itemVector = readIntArrayToList(_fixedData.nItems);
		// Logger.debug("itemVector: %s", _itemVector);

		_langVector = readIntArrayToList(_fixedData.nLangs);
		// Logger.debug("langVector: %s", _langVector);

		_contAttrVector = readIntArrayToList(_fixedData.nContAttrs);
		// Logger.debug("contAttrVector: %s", _contAttrVector);

		_dirFileVector = readIntArrayToList(_fixedData.nDirFiles);
		// Logger.debug("dirFileVector: %s", _dirFileVector);

	}

	public void storeQData() {
		makeTemp();
		_fixedData.nChars = _charVector.size();
		_fixedData.nItems = _itemVector.size();
		_fixedData.nLangs = _langVector.size();
		_fixedData.nContAttrs = _contAttrVector.size();
		_fixedData.nDirFiles = _dirFileVector.size();
		_fixedData.majorVersion = DELTA_MAJOR_VERSION;
		_fixedData.minorVersion = DELTA_MINOR_VERSION;
		if (_fixedData.fixedSize < FixedData.SIZE) {
			_dataOffs = SlotHeader.SIZE + FixedData.SIZE; // /// Adjust DataOffs accordingly
			_fixedData.fixedSize = FixedData.SIZE;
		}

		_slotFile.seek(_slotHdrPtr + SlotHeader.SIZE);
		_fixedData.write(_slotFile);

		// This seek forces allocation of a sufficiently large slot...
		final int VO_UNI_ID_SIZE = 4; /* a VO unique id is an int */
		dataSeek(VO_UNI_ID_SIZE * _charVector.size() + VO_UNI_ID_SIZE
				* _itemVector.size() + VO_UNI_ID_SIZE * _langVector.size()
				+ VO_UNI_ID_SIZE * _contAttrVector.size() + VO_UNI_ID_SIZE
				* _dirFileVector.size());
		dataSeek(0);

		writeIntCollection(_charVector);
		writeIntCollection(_itemVector);
		writeIntCollection(_langVector);
		writeIntCollection(_contAttrVector);
		writeIntCollection(_dirFileVector);
		dataTruncate();
	}

	private void writeIntCollection(Collection<Integer> ints) {
		for (int i : ints) {
			dataWrite(i);
		}
	}

	public short getFixedSize() {
		return _fixedData.fixedSize;
	}

	public short getMajorVersion() {
		return _fixedData.majorVersion;
	}

	public short getMinorVersion() {
		return _fixedData.minorVersion;
	}

	public int getNItems() {
		return _itemVector.size();
	}

	public int getNChars() {
		return _charVector.size();
	}

	public int getNLangs() {
		return _langVector.size();
	}

	public int getNContAttrs() {
		return _contAttrVector.size();
	}

	public int getNDirFiles() {
		return _dirFileVector.size();
	}

	@Override
	public int getTypeId() {
		return VOAnyDesc.VOTID_DELTA_BASE;
	}

	private int getNoFromId(List<Integer> vector, int uid) {
		return vector.indexOf(uid) + 1;
	}

	private int getIdFromNo(List<Integer> vector, int num) {
		if (num <= 0 || num > vector.size()) {
			return 0;
		}
		return vector.get(num - 1);
	}

	private boolean moveElement(List<Integer> vector, int oldindex, int newindex) {
		if (oldindex == 0 || newindex == 0 || oldindex > vector.size() || newindex > vector.size() || oldindex == newindex) {
			return false;
		}
		oldindex--;
		newindex--;
		int temp = vector.get(oldindex);
		vector.remove(oldindex);
		vector.set(newindex, temp);
		return true;
	}

	private boolean removeElement(List<Integer> vector, int uid) {
		if (vector.contains(uid)) {
			vector.remove((Integer) uid);
			return true;
		}
		return false;

	}

	private boolean insertElement(List<Integer> vector, int uid, int index) {
		if (index == 0 || index > vector.size()) {
			index = vector.size();
		}
		vector.add(index, (Integer) uid);

		return true;
	}

	public int uniIdFromItemNo(int itemNo) {
		return getIdFromNo(_itemVector, itemNo);
	}

	public int itemNoFromUniId(int uniId) {
		return getNoFromId(_itemVector, uniId);
	}

	public boolean moveItem(int oldNo, int newNo) {
		return moveElement(_itemVector, oldNo, newNo);
	}

	public boolean removeItem(int itemId) {
		return removeElement(_itemVector, itemId);
	}

	public boolean insertItem(int itemId, int itemNo) {
		return insertElement(_itemVector, itemId, itemNo);
	}

	public int uniIdFromCharNo(int charNo) {
		return getIdFromNo(_charVector, charNo);
	}

	public int charNoFromUniId(int uniId) {
		return getNoFromId(_charVector, uniId);
	}

	public boolean moveCharacter(int oldNo, int newNo) {
		return moveElement(_charVector, oldNo, newNo);
	}

	public boolean removeCharacter(int charId) {
		return removeElement(_charVector, charId);
	}

	public boolean insertCharacter(int charId, int charNo) {
		return insertElement(_charVector, charId, charNo);
	}

	public int uniIdFromAttrNo(int attrNo) {
		return getIdFromNo(_contAttrVector, attrNo);
	}

	public int attrNoFromUniId(int uniId) {
		return getNoFromId(_contAttrVector, uniId);
	}

	public boolean moveContAttr(int oldNo, int newNo) {
		return moveElement(_contAttrVector, oldNo, newNo);
	}

	public boolean removeContAttr(int attrId) {
		return removeElement(_contAttrVector, attrId);
	}

	public boolean insertContAttr(int attrId, int attrNo) {
		return insertElement(_contAttrVector, attrId, attrNo);
	}

	public int uniIdFromDirFileNo(int dirFileNo) {
		return getIdFromNo(_dirFileVector, dirFileNo);
	}

	public int dirFileNoFromUniId(int uniId) {
		return getNoFromId(_dirFileVector, uniId);
	}

	public boolean moveDirFile(int oldNo, int newNo) {
		return moveElement(_dirFileVector, oldNo, newNo);
	}

	public boolean removeDirFile(int dirFileId) {
		return removeElement(_dirFileVector, dirFileId);
	}

	public boolean InsertDirFile(int dirFileId, int dirFileNo) {
		return insertElement(_dirFileVector, dirFileId, dirFileNo);
	}

	@Override
	public String getStringId() {
		return "DELTA Master";
	}

	@Override
	public int getNumberOfItems() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getFixedDataSize() {
		return MasterFixedData.SIZE;
	}

	// Offsets...

	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + NoteFixedData.SIZE + 0;

	public static final int majorVersionOffs = NoteFixedData.SIZE + 2;
	public static final int minorVersionOffs = NoteFixedData.SIZE + 4;
	public static final int nCharsOffs = NoteFixedData.SIZE + 6;
	public static final int nItemsOffs = NoteFixedData.SIZE + 10;
	public static final int nLangsOffs = NoteFixedData.SIZE + 14;
	public static final int nContAttrsOffs = NoteFixedData.SIZE + 18;
	public static final int nDirFileOffs = NoteFixedData.SIZE + 22;

	public class MasterFixedData extends NoteFixedData implements IOObject {

		public static final int SIZE = NoteFixedData.SIZE + 2 + 2 + 2 + 4 + 4 + 4 + 4 + 4;

		public MasterFixedData(String acronym) {
			super(acronym);
			this.TypeID = VODescFactory.VODeltaMasterDesc_TypeId;
			fixedSize = SIZE;
			majorVersion = 0;
			minorVersion = 1;
		}

		public short fixedSize = 0;
		public short majorVersion = 0;
		public short minorVersion = 0;
		public int nChars = 0;
		public int nItems = 0;
		public int nLangs = 0;
		public int nContAttrs = 0;
		public int nDirFiles = 0;

		@Override
		public void read(BinFile f) {
			// read the super fixed data...
			super.read(f);
			// now read my specific data...
			fixedSize = f.readShort();
			majorVersion = f.readShort();
			minorVersion = f.readShort();
			nChars = f.readInt();
			nItems = f.readInt();
			nLangs = f.readInt();
			nContAttrs = f.readInt();
			nDirFiles = f.readInt();
		}

		@Override
		public void write(BinFile f) {		
			super.write(f);
			f.writeShort(fixedSize);
			f.writeShort(majorVersion);
			f.writeShort(minorVersion);
			f.writeInt(nChars);
			f.writeInt(nItems);
			f.writeInt(nLangs);
			f.writeInt(nContAttrs);
			f.writeInt(nDirFiles);
		}

	}

}
