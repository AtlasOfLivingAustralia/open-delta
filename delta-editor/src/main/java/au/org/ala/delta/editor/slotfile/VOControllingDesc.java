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
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

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

	public void StoreQData() {
		// Write the cached data
		throw new NotImplementedException();
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
		throw new NotImplementedException();
	}

	public void writeStateIds(List<Integer> src) {
		throw new NotImplementedException();
	}

	public void writeControlledChars(List<Integer> src) {
		throw new NotImplementedException();
	}

	public boolean AddControlledChar(int charId) {
		throw new NotImplementedException();
	}

	public boolean removeControlledChar(int charId) {
		throw new NotImplementedException();
	}

	public void setControllingInfo(int charId, List<Integer> stateIdVect, String aLabel) {
		throw new NotImplementedException();
	}

	// Fixed data
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int controlCharOffs = fixedSizeOffs + 2;
	public static final int nStatesOffs = controlCharOffs + 4;
	public static final int labelLengOffs = nStatesOffs + 4;
	public static final int nControlledOffs = labelLengOffs + 4;

	public class ControllingFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 4 + 4;

		public ControllingFixedData() {
			super("Cont Attr");
		}

		public short fixedSize;
		public int controlChar; // ID of char base descriptor that's doing the controlling
		public int nStates; // Number of controlling states within the character
		public int labelLeng; // Length of our label
		public int nControlled; // No. of characters directly controlled

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);
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
