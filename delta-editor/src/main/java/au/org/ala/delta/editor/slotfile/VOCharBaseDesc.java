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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import au.org.ala.delta.editor.slotfile.VOCharTextDesc.CharTextFixedData;
import au.org.ala.delta.io.BinFile;

public class VOCharBaseDesc extends VOImageHolderDesc {

	public static final int STATEID_NULL = -1;

	public static final byte CHAR_EXCLUSIVE = 0x1; // Character states are
													// exclusive
	public static final byte CHAR_MANDATORY = 0x2; // Character is mandatory

	private CharBaseFixedData _fixedData;
	protected CharTextInfo _charDescript;
	private List<Integer> _stateNumberMappingVector;

	public VOCharBaseDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		synchronized (getVOP()) {
			_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
			short diskFixedSize = _slotFile.readShort();
			_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
			_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
			_fixedData = new CharBaseFixedData();
			_fixedData.read(_slotFile);
			dataSeek(0);
			_stateNumberMappingVector = readStateNumberMap();
			cacheCharTextInfo(0, (short) 0);
		}

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
		synchronized (getVOP()) {
			List<Integer> dest = new ArrayList<Integer>();
			dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE + _fixedData.nControlling * 4 + _fixedData.nControls * 4);

			ByteBuffer b = readBuffer(_fixedData.nImages * 4);
			for (int i = 0; i < _fixedData.nImages; ++i) {
				dest.add(b.getInt());
			}
			return dest;
		}
	}

	@Override
	public boolean writeImageList(List<Integer> imageList) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = _fixedData.nStates * SIZE_OF_INT_IN_BYTES +
		               _fixedData.nDescriptors * CharTextInfo.SIZE +
		               _fixedData.nControlling * SIZE_OF_INT_IN_BYTES +
		               _fixedData.nControls * SIZE_OF_INT_IN_BYTES;

		if (imageList.size() != _fixedData.nImages) { // Save a copy of any following data!
		    trailerBuf = dupTrailingData(startPos + _fixedData.nImages * SIZE_OF_INT_IN_BYTES);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}
		
		// Seek to force allocation of large enough slot
		dataSeek(startPos + SIZE_OF_INT_IN_BYTES * imageList.size() + trailerLeng);
		dataSeek(startPos);

		for (int imageId : imageList) {
		    dataWrite(imageId);
		}
		if (imageList.size() != _fixedData.nImages) {
		    _fixedData.nImages = imageList.size();
		    setDirty();
		    if (trailerBuf != null) {
		        dataWrite(trailerBuf);
		        dataTruncate();
		    }
		}
		return true;
	}
	
	public void deleteImage(int imageId) {
		List<Integer> imageList = readImageList();
		imageList.remove(new Integer(imageId));
		writeImageList(imageList);
	}

	public void storeQData() {
		synchronized (getVOP()) {
			makeTemp();
			writeStateNumberMap(_stateNumberMappingVector);

			byte[] trailerBuf = null;
			int trailerLeng = 0;

			// If the size of TFixedData has been increased (due to a newer program
			// version)
			// re-write the whole slot, using the new size.
			if (_fixedData.fixedSize < CharBaseFixedData.SIZE) {
				// Save a copy of all "variable" data
				trailerBuf = dupTrailingData(0);
				if (trailerBuf != null) {
					trailerLeng = trailerBuf.length;
				}
				_dataOffs = SlotFile.SlotHeader.SIZE + CharBaseFixedData.SIZE;
				_fixedData.fixedSize = CharBaseFixedData.SIZE;
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

	public void setCharType(int charType) {
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

	public void setUncodedImplicit(int stateNo) {
		_fixedData.uncodedImplicit = stateNo;
		setDirty();
	}

	public void setCodedImplicit(int stateNo) {
		_fixedData.codedImplicit = stateNo;
		setDirty();
	}

	public void setInitialStateNumber(int nStates) {
		_stateNumberMappingVector = new ArrayList<Integer>(nStates);
		for (int i = 0; i < nStates; ++i) {
			_stateNumberMappingVector.add(i);
		}
		_fixedData.nStatesUsed = nStates;
		setDirty();
	}

	public boolean addControllingInfo(List<Integer> src) {
		boolean retVal = false;
		if (src.size() > 0)  {
		    List<Integer> oldContVect = readControllingInfo();
		    List<Integer> newContVect = new ArrayList<Integer>(oldContVect.size()+src.size());
		    newContVect.addAll(oldContVect);
		    for (int i : src) {
		    	if (!oldContVect.contains(i)) {
		    		newContVect.add(i);
		    	}
		    }
		  
		    if (newContVect.size() > oldContVect.size()) { // Wasn't a duplicate...
		        writeControllingInfo(newContVect);
		        retVal = true;
		    }
		}
		return retVal;
	}

	public boolean AddControllingInfo(int oneId) {
		List<Integer> unitVector = new ArrayList<Integer>();
		unitVector.add(oneId);
		return addControllingInfo(unitVector);
	}

	public boolean removeControllingInfo(List<Integer> src) {
		boolean retVal = false;
		if (src.size() > 0) {
		      
		    List<Integer> oldContVect = readControllingInfo();
		    
		    List<Integer> newContVect = new ArrayList<Integer>(oldContVect);
		    for (int id : src) {
		    	if (newContVect.contains(id)) {
		    		newContVect.remove((Integer)id);
		    	}
		    }
		     
		    if (newContVect.size() < oldContVect.size()) { // Was able to remove something
		        writeControllingInfo(newContVect);
		        retVal = true;
		    }
		}
		return retVal;
	}

	public boolean removeControllingInfo(int oneId) {
		List<Integer> unitVector = new ArrayList<Integer>();
		unitVector.add(oneId);
		return removeControllingInfo(unitVector);
	}

	public boolean addDependentContAttr(int attrId) {

		boolean retVal = false;
		  
		List<Integer> contVect = readDependentContAttrs();
		if (!contVect.contains(attrId)) {
		    contVect.add(attrId);
		    writeDependentContAttrs(contVect);
		    retVal = true;
		}
		return retVal;
	}

	public boolean removeDependentContAttr(int attrId) {
		boolean retVal = false;
		  
		List<Integer> contVect = readDependentContAttrs();
		if (contVect.contains(attrId)) {
		    contVect.remove((Integer)attrId);
		    writeDependentContAttrs(contVect);
		    retVal = true;
		}
		return retVal;
	}

	public VOCharTextDesc cacheCharTextInfo(int langDesc, short variantNo) {
		return readCharTextInfo(langDesc, variantNo, true);
	}

	public List<Integer> readStateNumberMap() {
		dataSeek(0);
		return readIntArrayToList(_fixedData.nStates);

	}

	public VOCharTextDesc readCharTextInfo(int langDesc, short variantNo) {
		return readCharTextInfo(langDesc, variantNo, false);
	}

	protected VOCharTextDesc readCharTextInfo(int langDesc, short variantNo, boolean cacheCharTextInfo) {
		synchronized (getVOP()) {
			CharTextInfo someInfo = new CharTextInfo();

			dataSeek(_fixedData.nStates * 4);

			int nLangMatches = 0;
			for (int i = 0; i < _fixedData.nDescriptors; ++i) {
				someInfo.read(_slotFile);
				if (langDesc == someInfo.langDesc) {
					if (variantNo == nLangMatches++) {
						if (cacheCharTextInfo) {
							_charDescript = someInfo;
						}
						return (VOCharTextDesc) getVOP().getDescFromId(someInfo.charDesc);
					}
				}
			}

			VOCharTextDesc.CharTextFixedData charTextFixedData = new VOCharTextDesc.CharTextFixedData();
			List<CharTextInfo> existingText = readCharTextInfo();
			charTextFixedData.charBaseId = getUniId();

			VOCharTextDesc charTextDesc = (VOCharTextDesc) getVOP().insertObject(charTextFixedData, CharTextFixedData.SIZE, null, 0, 0);
			someInfo.langDesc = 0;
			someInfo.charDesc = charTextDesc.getUniId();
			if (cacheCharTextInfo) {
				_charDescript = someInfo;
			}
			existingText.add(someInfo);
			writeCharTextInfo(existingText);

			return charTextDesc;
		}
	}

	public List<CharTextInfo> readCharTextInfo() {
		synchronized (getVOP()) {
			List<CharTextInfo> dest = new ArrayList<VOCharBaseDesc.CharTextInfo>();
			dataSeek(_fixedData.nStates * 4);
			for (int i = 0; i < _fixedData.nDescriptors; ++i) {
				CharTextInfo textInfo = new CharTextInfo();
				textInfo.read(_slotFile);
				dest.add(textInfo);
			}
			return dest;
		}
	}

	public List<Integer> readControllingInfo() {
		synchronized (getVOP()) {
			List<Integer> dest = new ArrayList<Integer>();

			dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE);
			ByteBuffer b = readBuffer(_fixedData.nControlling * 4);
			for (int i = 0; i < _fixedData.nControlling; ++i) {
				dest.add(b.getInt());
			}

			return dest;
		}
	}

	public List<Integer> readDependentContAttrs() {
		synchronized (getVOP()) {
			List<Integer> dest = new ArrayList<Integer>();
			dataSeek(_fixedData.nStates * 4 + _fixedData.nDescriptors * CharTextInfo.SIZE + _fixedData.nControlling * 4);
			ByteBuffer b = readBuffer(_fixedData.nControls * 4);
			for (int i = 0; i < _fixedData.nControls; ++i) {
				dest.add(b.getInt());
			}

			return dest;
		}

	}

	public void writeStateNumberMap(List<Integer> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = 0;
		if (src.size() != _fixedData.nStates) { // Save a copy of any following
												// data!
			trailerBuf = dupTrailingData(_fixedData.nStates * 4);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		// Seek to force allocation of large enough slot
		dataSeek(4 * src.size() + trailerLeng);
		dataSeek(startPos);

		for (int i : src) {
			dataWrite(i);
		}
		if (src.size() != _fixedData.nStates) {
			_fixedData.nStates = src.size();
			setDirty();
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}
	}

	public void writeCharTextInfo(List<CharTextInfo> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = _fixedData.nStates * 4;
		if (src.size() != _fixedData.nDescriptors) {// Save a copy of any
													// following data!
			trailerBuf = dupTrailingData(startPos + _fixedData.nDescriptors * CharTextInfo.SIZE);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		// Seek to force allocation of large enough slot
		dataSeek(startPos + CharTextInfo.SIZE * src.size() + trailerLeng);
		dataSeek(startPos);

		for (CharTextInfo charText : src) {
			dataWrite(charText);
		}
		if (src.size() != _fixedData.nDescriptors) {
			_fixedData.nDescriptors = src.size();
			setDirty();
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}
	}

	public void writeControllingInfo(List<Integer> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = _fixedData.nStates * SIZE_OF_INT_IN_BYTES +
		               _fixedData.nDescriptors * CharTextInfo.SIZE;

		if (src.size() != _fixedData.nControlling) {// Save a copy of any following data!
		    trailerBuf = dupTrailingData(startPos + _fixedData.nControlling * SIZE_OF_INT_IN_BYTES);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}

		// Seek to force allocation of large enough slot
		dataSeek(startPos + SIZE_OF_INT_IN_BYTES * src.size() + trailerLeng);
		dataSeek(startPos);

		Collections.sort(src);
		for (int id: src) {
			dataWrite(id);
		}
		 
		if (src.size() != _fixedData.nControlling) {
		    _fixedData.nControlling = src.size();
		    setDirty();
		    if (trailerBuf != null) {
		        dataWrite(trailerBuf);
		        dataTruncate();
		    }
		}
	}

	public void writeDependentContAttrs(List<Integer> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = _fixedData.nStates * SIZE_OF_INT_IN_BYTES +
		               _fixedData.nDescriptors * CharTextInfo.SIZE +
		               _fixedData.nControlling * SIZE_OF_INT_IN_BYTES;

		// Sort, and remove duplicates before storing.
		// Overkill, actually. For a while, a was storing the IDs of controlled
		// _characters_, not _controlling attributes_, and duplicates were more
		// likely to be a problem.
		Set<Integer> ids = new HashSet<Integer>(src);
		src = new ArrayList<Integer>(ids);
		Collections.sort(src);

		if (src.size() != _fixedData.nControls) { // Save a copy of any following data!
		    trailerBuf = dupTrailingData(startPos + _fixedData.nControls * SIZE_OF_INT_IN_BYTES);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}

		// Seek to force allocation of large enough slot
		dataSeek(startPos + SIZE_OF_INT_IN_BYTES * src.size() + trailerLeng);
		dataSeek(startPos);

		for (int id : src) {
		    dataWrite(id);
		}
		if (src.size() != _fixedData.nControls) {
		    _fixedData.nControls = src.size();
		    setDirty();
		    if (trailerBuf != null) {
		        dataWrite(trailerBuf);
		        dataTruncate();
		    }
		}
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

	// Receives a list of state numbers (external representation), in
	// src and places the complement of that list in dest
	public void invertStateNos(List<Integer> src, List<Integer> dest) {
		int nStates = getNStatesUsed();
		  
	    for (int i = 1; i <= nStates; ++i) {
		    if (!src.contains(i)) {
		    	dest.add(i);
		    }
	    }
	}

	public boolean moveState(int oldNo, int newNo) {
		if (oldNo == 0 || newNo == 0 ||
			oldNo > _fixedData.nStatesUsed || newNo > _fixedData.nStatesUsed ||
			oldNo == newNo) {
		    return false;
		}
		oldNo--;  // Convert to zero-based, rather than 1-based, numbering
		newNo--;
		int oldId = _stateNumberMappingVector.get(oldNo);
		_stateNumberMappingVector.remove(oldNo);	  
		_stateNumberMappingVector.add(newNo, oldId);
	     setDirty();
		return true;
	}

	
	// Deleting a state is actually a fairly complicated operation.
	// First we should check to be sure the state is not in use, either
	// in an item description or in a "controlling attribute" and allow for
	// user interaction to correct potential problems. For now, however, we assume this
	// will be handled elsewhere. We can then set the corresponding
	// text for the state to an empty string. Finally, because of the way state ids
	// are handled, we can't just "delete" an id; we first move it to
	// the list of "available" ids, kept at the end of the state id vector.
	// The "available" ids are kept in sorted order. Only state ids that have been
	// made "available" AND lie at the end of the list of all state ids can actually
	// be deleted (that is, the id list can be shortened). If we do this, the text
	// descriptor's list should also be shortened. (And remember also that there
	// may be more than one text descriptor; all must be changed appropriately).
	// Return true if deletion concludes successfully.
	public boolean deleteState(int stateId, DeltaVOP Vop) {
		if (stateId == STATEID_NULL || stateId > _stateNumberMappingVector.size()) {
		    return false;
		}

		int pos = _stateNumberMappingVector.indexOf(stateId);
		
		if (pos == -1 || pos >= _fixedData.nStatesUsed) {
		    return false;  // probably better to throw something ... internal error.
		}
		// Remove the stateId from the list
		_stateNumberMappingVector.remove(pos);
		--_fixedData.nStatesUsed;

		// Then re-insert it in the (sorted) "available IDs" list at the end
		for (int i=_fixedData.nStatesUsed; i<_stateNumberMappingVector.size(); i++) {
			if (stateId < _stateNumberMappingVector.get(i)) {
				_stateNumberMappingVector.add(i, stateId);
			}
		}
		
		// Check the "available IDs" at the end of the list to see which can truly
		// be removed. This will actually only happen when the state ID currently being
		// deleted is the largest of the entire set.
		int nRemoved = 0;
		int i = _stateNumberMappingVector.size() - 1;
		while (i >= _fixedData.nStatesUsed && _stateNumberMappingVector.get(i) == i) {
		    _stateNumberMappingVector.remove(i);
		    ++nRemoved;
		    --i;
		}

		List<CharTextInfo> allText = readCharTextInfo(); // NOTE: Must do this BEFORE changing fixedData.nStates !

		for (CharTextInfo info : allText) {
		      
		    VOCharTextDesc charText = (VOCharTextDesc)Vop.getDescFromId(info.charDesc);
		    if (charText != null && charText.getCharBaseId() == getUniId()) {
		         
		        if (nRemoved > 0) {
		            charText.resize(_stateNumberMappingVector.size());
		        }
		        else {
		            charText.writeStateText("", stateId);
		        }
		    }
		}
		if (getCodedImplicit() == stateId) {
		    setCodedImplicit(STATEID_NULL);
		}
		if (getUncodedImplicit() == stateId) {
		    setUncodedImplicit(STATEID_NULL);
		}
		setDirty();
		return true;
	}

	// Append a new state, then move it to the indicated stateNo (or just leave it
	// at the end if stateNo == 0)
	public int insertState(int stateNo, VOP vop) {
		int stateId;
		if (_fixedData.nStatesUsed < _stateNumberMappingVector.size()) {
		    stateId = _stateNumberMappingVector.get(_fixedData.nStatesUsed);
		}
		else {
		    List<CharTextInfo> allText = readCharTextInfo();
		    stateId = _fixedData.nStatesUsed;
		    _stateNumberMappingVector.add(stateId);
		    for (CharTextInfo info : allText) {
		        VOCharTextDesc charText = (VOCharTextDesc)vop.getDescFromId(info.charDesc);
		        if (charText != null && charText.getCharBaseId() == getUniId()) {
		            charText.resize(_stateNumberMappingVector.size());
		        }
		    }
		}
		++_fixedData.nStatesUsed;
		moveState(_fixedData.nStatesUsed, stateNo);
		setDirty();
		return stateId;
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

	public static class CharBaseFixedData extends FixedData {

		private static final int CHAR_BASE_SIZE = 2 + 4 + 4 + 4 + 1 + 4 + 4 + 4 + 4 + 4 + 4;
		public static final int SIZE = FixedData.SIZE + CHAR_BASE_SIZE;

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
			ByteBuffer b = file.readByteBuffer(CHAR_BASE_SIZE);

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
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("charType: "+charType).append(",");
			sb.append("nStates: "+nStates).append(",");
			sb.append("nStatesUsed: "+nStatesUsed).append(",");
			sb.append("Char flags: "+charFlags).append(",");
			sb.append("nDescriptors: "+nDescriptors).append(",");
			sb.append("Uncoded implicit: "+uncodedImplicit).append(",");
			sb.append("Coded implicit: "+codedImplicit).append(",");
			sb.append("nControlling: "+nControlling).append(",");
			sb.append("nControls: "+nControls).append(",");
			sb.append("nImages: "+nImages).append(",");
			
			return sb.toString();
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see au.org.ala.delta.editor.slotfile.IOObject#size()
		 */
		@Override
		public int size() {
			return SIZE;
		}

	}

}
