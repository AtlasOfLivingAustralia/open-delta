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
import java.util.List;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;

public class VOControllingDesc extends VOAnyDesc {

	private ControllingFixedData _fixedData;

	public VOControllingDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		synchronized (getVOP()) {
			_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
			short diskFixedSize = _slotFile.readShort();

			assert diskFixedSize == ControllingFixedData.SIZE;

			_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
			_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
			_fixedData = new ControllingFixedData();
			_fixedData.read(_slotFile);

			// Logger.debug("ControllingDesc: controlChar=%d, nStates=%d, labelLeng=%d, nControlled=%d", _fixedData.controlChar, _fixedData.nStates, _fixedData.labelLeng, _fixedData.nControlled);
			//
			// // SNIP >>>>
			//
			// Logger.debug("Label: %s States: %s Controlled: %s", readLabel(), readStateIds(), readControlledChars());

			// SNIP <<<<

			dataSeek(0);
		}
	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOControllingDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Controlling attributes";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	@Override
	public void storeQData() {
		makeTemp();
		byte[] trailerBuf = null;
		int trailerLeng = 0;
        
		// If the size of TFixedData has been increased (due to a newer program version)
		// re-write the whole slot, using the new size.
		if (_fixedData.fixedSize < ControllingFixedData.SIZE) {
		      // Save a copy of all "variable" data
		      trailerBuf = dupTrailingData(0);
		      if (trailerBuf != null) {
		    	  trailerLeng = trailerBuf.length;
		      }
		      _dataOffs = SlotFile.SlotHeader.SIZE + ControllingFixedData.SIZE; ///// Adjust DataOffs accordingly
		      _fixedData.fixedSize = ControllingFixedData.SIZE;
		      // Do seek to force allocation of large enough slot
		      dataSeek(trailerLeng);
		}

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.write(_slotFile);

		if (trailerBuf != null) {// If fixedData was resized, re-write the saved, variable-length data
		    dataSeek(0);
		    dataWrite(trailerBuf);
		    dataTruncate();
		}
	}

	public int getCharId() {
		return _fixedData.controlChar;
	}

	public int getNStates() {
		return _fixedData.nStates;
	}

	public int getNControlled() {
		return _fixedData.nControlled;
	}

	public int getLabelLeng() {
		return _fixedData.labelLeng;
	}

	public List<Integer> readStateIds() {
		synchronized (getVOP()) {
			dataSeek(0);
			return readIntArrayToList(_fixedData.nStates);
		}

	}

	public String readLabel() {
		synchronized (getVOP()) {
			if (_fixedData.labelLeng > 0) {
				dataSeek(_fixedData.nStates * 4);
				return readString(_fixedData.labelLeng);
			} else {
				return "";
			}
		}
	}

	public List<Integer> readControlledChars() {
		synchronized (getVOP()) {
			List<Integer> dest = new ArrayList<Integer>();
			if (_fixedData.nControlled > 0) {
				dataSeek((_fixedData.nStates * 4) + _fixedData.labelLeng);
				dest = readIntArrayToList(_fixedData.nControlled);
			}
			return dest;
		}
	}

	public void writeLabel(String aLabel) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int seekPos = _fixedData.nStates * SIZE_OF_INT_IN_BYTES;

		if (aLabel.length() != _fixedData.labelLeng) {// Save a copy of any following data!
		    trailerBuf = dupTrailingData(seekPos + _fixedData.labelLeng);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		}
		dataSeek(seekPos + aLabel.length() + trailerLeng);
		dataSeek(seekPos);
		dataWrite(BinFileEncoding.encode(aLabel));
		if (aLabel.length() != _fixedData.labelLeng) {
		    _fixedData.labelLeng = aLabel.length();
		    setDirty();
		    if (trailerBuf != null) {
		        dataWrite(trailerBuf);
		        dataTruncate();
		    }
		}
	}

	public void writeStateIds(List<Integer> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = 0;
		if (src.size() != _fixedData.nStates) { // Save a copy of any following data!
		    trailerBuf = dupTrailingData(_fixedData.nStates * SIZE_OF_INT_IN_BYTES);
		    if (trailerBuf != null) {
		    	trailerLeng = trailerBuf.length;
		    }
		
		}

		// Seek to force allocation of large enough slot
		dataSeek(SIZE_OF_INT_IN_BYTES * src.size() + trailerLeng);
		dataSeek(startPos);

		Collections.sort(src);
		for (int id : src) {
		    dataWrite(id);
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

	public void writeControlledChars(List<Integer> src) {
		int seekPos = _fixedData.nStates * SIZE_OF_INT_IN_BYTES + _fixedData.labelLeng;
		dataSeek(seekPos + src.size() * SIZE_OF_INT_IN_BYTES);
        dataSeek(seekPos);

        for (int id : src) {
        	dataWrite(id);
        }
        if (src.size() != _fixedData.nControlled) {
		    dataTruncate();
		    _fixedData.nControlled = src.size();
		    setDirty();
		}
	}

	public boolean addControlledChar(int charId) {
		boolean retVal = false;
		List<Integer> contVect = readControlledChars();
		// Disallow duplicate entries...
		if (!contVect.contains(charId)) {
		    contVect.add(charId);
		    writeControlledChars(contVect);
		    retVal = true;
		}
		return retVal;
	}

	public boolean removeControlledChar(int charId) {
		boolean retVal = false;
		List<Integer> contVect= readControlledChars();
		if (contVect.contains(charId)) {
		    contVect.remove((Integer)charId);
		    writeControlledChars(contVect);
		    retVal = true;
		}
		return retVal;
	}

	public void setControllingInfo(int charId, List<Integer> stateIdVect, String aLabel) {
		_fixedData.controlChar = charId;
		writeStateIds(stateIdVect);
		writeLabel(aLabel);
		setDirty();
	}

	// Fixed data
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int controlCharOffs = fixedSizeOffs + 2;
	public static final int nStatesOffs = controlCharOffs + 4;
	public static final int labelLengOffs = nStatesOffs + 4;
	public static final int nControlledOffs = labelLengOffs + 4;

	public static class ControllingFixedData extends FixedData {

		
		private static final int CONTROLLING_FIXED_DATA_SIZE = 2 + 4 + 4 + 4 + 4;
		public static final int SIZE = FixedData.SIZE + CONTROLLING_FIXED_DATA_SIZE;
		
		public ControllingFixedData() {
			super("Cont Attr");
			TypeID = VODescFactory.VOControllingDesc_TypeId;
			fixedSize = SIZE;
		}

		public short fixedSize;
		public int controlChar; // ID of char base descriptor that's doing the controlling
		public int nStates; // Number of controlling states within the character
		public int labelLeng; // Length of our label
		public int nControlled; // No. of characters directly controlled

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(CONTROLLING_FIXED_DATA_SIZE);
			fixedSize = b.getShort();
			controlChar = b.getInt();
			nStates = b.getInt();
			labelLeng = b.getInt();
			nControlled = b.getInt();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(controlChar);
			file.write(nStates);
			file.write(labelLeng);
			file.write(nControlled);
		}

	}

}
