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
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

public class VOCharBaseDesc extends VOImageHolderDesc {
	
	public static final int STATEID_NULL = -1;
	
	public static final byte CHAR_EXCLUSIVE = 0x1;  // Character states are exclusive
	public static final byte CHAR_MANDATORY = 0x2;  // Character is mandatory
	

	private CharBaseFixedData _fixedData;
	protected CharTextInfo _charDescript;
	private List<Integer> _stateNumberMappingVector;

	public VOCharBaseDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();

		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData = new CharBaseFixedData();
		_fixedData.read(_slotFile);

//		Logger.debug("UniId: %d charType: %d nStates: %d nStatesUsed: %d charFlags: %x uncodedImplict: %d codedImplict: %d nControlling: %d nControls: %d nImages: %d", _fixedData.UniId,
//				_fixedData.charType, _fixedData.nStates, _fixedData.nStatesUsed, _fixedData.charFlags, _fixedData.uncodedImplicit, _fixedData.codedImplicit, _fixedData.nControlling,
//				_fixedData.nControls, _fixedData.nImages);

		dataSeek(0);

		_stateNumberMappingVector = readStateNumberMap();
//		Logger.debug("StateNumberMapping: %s", _stateNumberMappingVector);

		cacheCharTextInfo(0, (short) 0);

		// Logger.debug("CharTextInfo: LangDesc = %d, CharTextDesc =%d", _charDescript.langDesc, _charDescript.charDesc);

	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOCharBaseDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Character Base";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	@Override
	public List<Integer> readImageList() {
		List<Integer> dest = new ArrayList<Integer>();
		dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE + _fixedData.nControlling * 4 + _fixedData.nControls * 4);

		ByteBuffer b = readBuffer(_fixedData.nImages * 4);
		for (int i = 0; i < _fixedData.nImages; ++i) {
			dest.add(b.getInt());
		}
		return dest;
	}

	@Override
	public boolean writeImageList(List<Integer> imagelist) {
		throw new NotImplementedException();
	}

	public void storeQData() {
		throw new NotImplementedException();
	}

	public int getCharType() {
		return _fixedData.charType;
	}

	public CharTextInfo getCharDescript() {
		return _charDescript;
	}

	public int getNDescriptors() {
		return _fixedData.nDescriptors;
	}

	public int getNStates() {
		return _stateNumberMappingVector.size();
	}

	public int getNStatesUsed() {
		return _fixedData.nStatesUsed;
	}

	public int getNControlling() {
		return _fixedData.nControlling;
	}

	public int getNDependentContAttrs() {
		return _fixedData.nControls;
	}

	public int getNImages() {
		return _fixedData.nImages;
	}

	public int getImageType() {
		return ImageType.IMAGE_CHARACTER;
	}

	public byte getCharFlags() {
		return _fixedData.charFlags;
	}

	public int getUncodedImplicit() {
		return _fixedData.uncodedImplicit;
	}

	public int getCodedImplicit() {
		return _fixedData.codedImplicit;
	}

	public void setCharType(short charType) {
		_fixedData.charType = charType;
		setDirty();
	}

	public void setCharFlags(byte flags) {
		_fixedData.charFlags = flags;
		setDirty();
	}

	public void setCharFlag(byte flag) {
		_fixedData.charFlags |= flag;
		setDirty();
	}

	public void setNStatesUsed(int nStates) {
		_fixedData.nStatesUsed = nStates;
	}

	public void clearCharFlag(byte flag) {
		_fixedData.charFlags &= ~flag;
		setDirty();
	}

	public boolean testCharFlag(byte flag) {
		return (_fixedData.charFlags & flag) != 0;
	}

	public void setUncodedImplicit(short stateNo) {
		_fixedData.uncodedImplicit = stateNo;
		setDirty();
	}

	public void setCodedImplicit(short stateNo) {
		_fixedData.codedImplicit = stateNo;
		setDirty();
	}

	public void setInitialStateNumber(int nStates) {
		throw new NotImplementedException();
	}

	public boolean addControllingInfo(List<Integer> src) {
		throw new NotImplementedException();
	}

	public boolean AddControllingInfo(int oneId) {
		List<Integer> unitVector = new ArrayList<Integer>();
		unitVector.add(oneId);
		return addControllingInfo(unitVector);
	}

	public boolean removeControllingInfo(List<Integer> src) {
		throw new NotImplementedException();
	}

	public boolean removeControllingInfo(int oneId) {
		List<Integer> unitVector = new ArrayList<Integer>();
		unitVector.add(oneId);
		return removeControllingInfo(unitVector);
	}

	public boolean addDependentContAttr(int attrId) {
		throw new NotImplementedException();
	}

	public boolean RemoveDependentContAttr(int attrId) {
		throw new NotImplementedException();
	}

	public CharTextInfo cacheCharTextInfo(int langDesc, short variantNo) {
		if (_charDescript == null) {
			_charDescript = readCharTextInfo(langDesc, variantNo);
		}
		return _charDescript;
	}

	public List<Integer> readStateNumberMap() {
		dataSeek(0);
		return readIntArrayToList(_fixedData.nStates);

	}

	public CharTextInfo readCharTextInfo(int langDesc, short variantNo) {
		CharTextInfo someInfo = new CharTextInfo();
		
		dataSeek(_fixedData.nStates * 4);

		int nLangMatches = 0;
		for (int i = 0; i < _fixedData.nDescriptors; ++i) {
			someInfo.read(_slotFile);
			if (langDesc == someInfo.langDesc) {
				if (variantNo == nLangMatches++) {					
					return someInfo;
				}
			}
		}

		// otherwise throw something?

		return null;
	}

	public List<CharTextInfo> readCharTextInfo() {
		List<CharTextInfo> dest = new ArrayList<VOCharBaseDesc.CharTextInfo>();
		dataSeek(_fixedData.nStates * 4);
		for (int i = 0; i < _fixedData.nDescriptors; ++i) {
			CharTextInfo textInfo = new CharTextInfo();
			textInfo.read(_slotFile);
			dest.add(textInfo);
		}
		return dest;
	}

	public List<Integer> readControllingInfo() {
		List<Integer> dest = new ArrayList<Integer>();

		dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE);
		ByteBuffer b = readBuffer(_fixedData.nControlling * 4);
		for (int i = 0; i < _fixedData.nControlling; ++i) {
			dest.add(b.getInt());
		}

		return dest;
	}

	public List<Integer> readDependentContAttrs() {
		List<Integer> dest = new ArrayList<Integer>();

		dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE + _fixedData.nControlling * 4);
		ByteBuffer b = readBuffer(_fixedData.nControls * 4);		
		for (int i = 0; i < _fixedData.nControls; ++i) {
			dest.add(b.getInt());
		}

		return dest;

	}

	public void writeStateNumberMap(List<Short> src) {
		throw new NotImplementedException();
	}

	public void writeCharTextInfo(List<CharTextInfo> src) {
		throw new NotImplementedException();
	}

	public void writeControllingInfo(List<Integer> src) {
		throw new NotImplementedException();
	}

	public void writeDependentContAttrs(List<Integer> src) {
		throw new NotImplementedException();
	}

	public int uniIdFromStateNo(int stateNo) {
		if (stateNo <= 0 || stateNo > _fixedData.nStatesUsed) {
			return STATEID_NULL;
		}
		return _stateNumberMappingVector.get(stateNo - 1);
	}

	public int stateNoFromUniId(int uniId) {
		if (_stateNumberMappingVector.contains(uniId)) {
			return _stateNumberMappingVector.indexOf(uniId) + 1;
		}
		return 0;
	}

	public void invertStateNos(List<Integer> src, List<Integer> dest) {
		throw new NotImplementedException();
	}

	public boolean moveState(int oldNo, int newNo) {
		throw new NotImplementedException();
	}

	public boolean deleteState(short stateId, DeltaVOP Vop) {
		throw new NotImplementedException();
	}

	public short insertState(int stateNo, Object vopDoc) {
		throw new NotImplementedException();
	}

	void deleteImage(int imageId) {
		throw new NotImplementedException();
	}

	// Fixed data offsets etc...

	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 0;
	public static final int charTypeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 2;
	public static final int nStatesOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 6;
	public static final int nStatesUsedOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 10;
	public static final int charFlagsOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 14;
	public static final int nDescriptorsOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 15;
	public static final int uncodedImplicitOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 19;
	public static final int codedImplicitOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 23;
	public static final int nControllingOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 25;
	public static final int nControlsOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 29;
	public static final int nImagesOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 33;

	public class CharBaseFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 4 + 1 + 4 + 4 + 4 + 4 + 4 + 4;

		public CharBaseFixedData() {
			super("Char Base");
			this.TypeID = VODescFactory.VOCharBaseDesc_TypeId;
			this.fixedSize = SIZE;
		}

		public short fixedSize;
		public int charType; // Character type
		public int nStates; // Length of character state mapping array AS
							// WRITTEN ON DISK (for length of the in-memory
							// structure, use its size())
		public int nStatesUsed; // Number of states in state array actually in
								// use
		public byte charFlags; // Initially only "exclusive" and "mandatory"
		public int nDescriptors; // Number of associated character data
									// descriptors
		public int uncodedImplicit; // State number of uncoded implicit value
		public int codedImplicit; // State number of coded implicit value
		public int nControlling; // Number of controlling characters
		public int nControls; // Number of other characters controlled directly
								// by this one
		public int nImages; // Number of character images

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);
			
			fixedSize = b.getShort();
			charType = b.getInt();
			nStates = b.getInt();
			nStatesUsed = b.getInt();
			charFlags = b.get();
			nDescriptors = b.getInt();
			uncodedImplicit = b.getInt();
			codedImplicit = b.getInt();
			nControlling = b.getInt();
			nControls = b.getInt();
			if (fixedSize == SIZE) {
				nImages = b.getInt();
			}
			
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(charType);
			file.write(nStates);
			file.write(nStatesUsed);
			file.write(charFlags);
			file.write(nDescriptors);
			file.write(uncodedImplicit);
			file.write(codedImplicit);
			file.write(nControlling);
			file.write(nControls);
			file.write(nImages);
		}

	}

	public class CharTextInfo implements IOObject {

		public static final int SIZE = 8;

		public int langDesc = 0;
		public int charDesc = 0;

		@Override
		public void read(BinFile file) {
			ByteBuffer b = file.readByteBuffer(8);
			
			langDesc = b.getInt();
			charDesc = b.getInt();
		}

		@Override
		public void write(BinFile file) {
			file.writeInt(langDesc);
			file.writeInt(charDesc);
		}
	}

}
