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

import au.org.ala.delta.rtf.RTFUtils;
import au.org.ala.delta.util.Utils;

public class VOCharTextDesc extends VOAnyDesc {

	private CharTextFixedData _fixedData;
	private List<Integer> _stateLengs;

	public VOCharTextDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();

		assert diskFixedSize == CharTextFixedData.SIZE;

		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;

		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);

		_fixedData = new CharTextFixedData();
		_fixedData.read(_slotFile);

		// Logger.debug("Unid: %d charUniD=%d, LangUnid=%d, Feature Len: %d NotesLen: %d nStateLengs: %d", _fixedData.UniId, _fixedData.charBaseId, _fixedData.charLangId, _fixedData.featureLeng,
		// _fixedData.notesLeng, _fixedData.nStateLengs);

		dataSeek(0);

		// SNIP >>>>>
		_stateLengs = readStateLengs();

		// Logger.debug("State lengths: %s", _stateLengs);
		//
		// Logger.debug("Feature Text: %s", readFeatureText(TextType.RTF));
		//
		// Logger.debug("State Texts: %s", readAllStates(TextType.RTF));
		//
		// Logger.debug("Note Text: %s", readNoteText(TextType.RTF));
	}

	@Override
	public int getTypeId() {
		return VODescFactory.VOCharTextDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Character Text";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void StoreQData() {
		throw new NotImplementedException();
		// write the cached data
	}

	public int getCharBaseId() {
		return _fixedData.charBaseId;
	}

	public int getCharLangId() {
		return _fixedData.charLangId;
	}

	public int getNotesLeng() {
		return _fixedData.notesLeng;
	}

	public int getFeatureLeng() {
		return _fixedData.featureLeng;
	}

	public List<Integer> readStateLengs() {
		dataSeek(0);
		return readIntArrayToList(_fixedData.nStateLengs);
	}

	public String readFeatureText(TextType textType) {

		dataSeek(_fixedData.nStateLengs * 4);

		String s = readString(_fixedData.featureLeng);

		if (textType == TextType.ANSI) {
			s = Utils.RTFToANSI(s);
		} else if (textType == TextType.UTF8) {
			s = RTFUtils.stripFormatting(s);
		}

		return s;
	}

	public String readStateText(int stateId, TextType textType) {

		int readSize = _stateLengs.get(stateId);
		int seekPos = _fixedData.nStateLengs * 4 + _fixedData.featureLeng;
		for (int i = 0; i < stateId; ++i) {
			seekPos += _stateLengs.get(i);
		}

		dataSeek(seekPos);

		String s = readString(readSize);

		if (textType == TextType.ANSI) {
			s = Utils.RTFToANSI(s);
		} else if (textType == TextType.UTF8) {
			s = RTFUtils.stripFormatting(s);
		}

		return s;
	}

	public List<String> readAllStates(TextType textType) {
		List<String> list = new ArrayList<String>();
		dataSeek(_fixedData.nStateLengs * 4 + _fixedData.featureLeng);
		for (int i = 0; i < _fixedData.nStateLengs; ++i) {
			String s = readString(_stateLengs.get(i));

			if (textType == TextType.ANSI) {
				s = Utils.RTFToANSI(s);
			} else if (textType == TextType.UTF8) {
				s = RTFUtils.stripFormatting(s);
			}
			list.add(s);
		}
		return list;

	}

	public String readNoteText(TextType textType) {
		if (_fixedData.notesLeng > 0) {

			int seekPos = _fixedData.nStateLengs * 4 + _fixedData.featureLeng;
			for (int i = 0; i < _fixedData.nStateLengs; ++i) {
				seekPos += _stateLengs.get(i);
			}

			dataSeek(seekPos);

			String dest = readString(_fixedData.notesLeng);
			if (textType == TextType.ANSI) {
				dest = Utils.RTFToANSI(dest);
			} else if (textType == TextType.UTF8) {
				dest = RTFUtils.stripFormatting(dest);
			}
			return dest;
		}

		return "";
	}

	public String[] ReadAllText(TextType textType, List<String> states) {
		String[] results = new String[2];
		results[0] = readFeatureText(textType);
		results[1] = readNoteText(textType);
		if (states != null) {
			states.clear();
			states.addAll(readAllStates(textType));
		}

		return results;
	}

	public void writeStateLengs(List<Integer> src) {

		byte[] trailerBuf = null;
		int trailerLen = 0;
		int startPos = 0;
		if (src.size() != _fixedData.nStateLengs) {
			trailerBuf = dupTrailingData(_fixedData.nStateLengs * SIZE_OF_INT_IN_BYTES);
			if (trailerBuf != null) {
				trailerLen = trailerBuf.length;
			}
		}
		dataSeek(startPos + SIZE_OF_INT_IN_BYTES * src.size() + trailerLen);
		dataSeek(startPos);

		for (int i : src) {
			dataWrite(i);
		}

		if (src.size() != _fixedData.nStateLengs) {
			_fixedData.nStateLengs = src.size();
			setDirty();
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}
		_stateLengs = src;
	}

	public void writeFeatureText(String src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int startPos = _fixedData.nStateLengs * SIZE_OF_INT_IN_BYTES;

		byte[] srcBytes = stringToBytes(src);

		if (srcBytes.length != _fixedData.featureLeng) {
			trailerBuf = dupTrailingData(startPos + _fixedData.featureLeng);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		dataSeek(startPos + srcBytes.length + trailerLeng);
		dataSeek(startPos);

		dataWrite(srcBytes);
		if (srcBytes.length != _fixedData.featureLeng) {
			_fixedData.featureLeng = srcBytes.length;
			setDirty();
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}
	}

	public void writeStateText(String src, int stateId) {

		byte[] srcBytes = stringToBytes(src);

		byte[] trailerBuf = null;
		int trailerLeng = 0;
		if (stateId >= _fixedData.nStateLengs) {
			List<Integer> newLengs = new ArrayList<Integer>(_stateLengs);
			writeStateLengs(newLengs);
		}

		int seekPos = _fixedData.nStateLengs * SIZE_OF_INT_IN_BYTES + _fixedData.featureLeng;
		for (int i = 0; i < stateId; ++i) {
			seekPos += _stateLengs.get(i);
		}
		if (srcBytes.length != _stateLengs.get(stateId)) {
			trailerBuf = dupTrailingData(seekPos + _stateLengs.get(stateId));
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		dataSeek(seekPos + srcBytes.length + trailerLeng);
		dataSeek(seekPos);

		dataWrite(srcBytes);
		if (srcBytes.length != _stateLengs.get(stateId)) {
			_stateLengs.set(stateId, srcBytes.length);
			setDirty();
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}

	}

	public void writeAllStates(List<String> src) {
		byte[] trailerBuf = null;
		int trailerLeng = 0;
		int newLen = 0;
		int oldLen = 0;
		List<Integer> newLengs = new ArrayList<Integer>(_stateLengs);
		for (int i = 0; i < _fixedData.nStateLengs; ++i) {
			oldLen += _stateLengs.get(i);
		}
		for (int i = 0; i < src.size(); i++) {
			byte[] srcBytes = stringToBytes(src.get(i));
			newLen += srcBytes.length;
		}

		if (newLen != oldLen) { // Save a copy of any following data!
			trailerBuf = dupTrailingData(_fixedData.nStateLengs * SIZE_OF_INT_IN_BYTES + _fixedData.featureLeng + oldLen);
			if (trailerBuf != null) {
				trailerLeng = trailerBuf.length;
			}
		}

		int seekPos = _fixedData.nStateLengs * SIZE_OF_INT_IN_BYTES + _fixedData.featureLeng;

		dataSeek(seekPos + newLen + trailerLeng);
		dataSeek(seekPos);
		for (int i = 0; i < src.size(); i++) {
			byte[] srcBytes = stringToBytes(src.get(i));

			dataWrite(srcBytes);
			if (newLengs.get(i) != srcBytes.length) {
				newLengs.set(i, srcBytes.length);
			}
		}
		if (newLen != oldLen) {
			if (trailerBuf != null) {
				dataWrite(trailerBuf);
				dataTruncate();
			}
		}
		writeStateLengs(newLengs);
	}

	void writeNoteText(String src) {
		throw new NotImplementedException();
	}

	public void writeAllText(String feature, List<String> states, String notes) {
		throw new NotImplementedException();
	}

	// Fixed data offsets
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 0;
	public static final int charBaseOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 2;
	public static final int charLangOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 6;
	public static final int featureLengOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 10;
	public static final int notesLengOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 14;
	public static final int nStateLengsOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE + 18;

	public class CharTextFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + 4 + 4 + 4 + 4 + 4;

		public CharTextFixedData() {
			super("Char Text");
			TypeID = VODescFactory.VOCharTextDesc_TypeId;
			fixedSize = SIZE;
		}

		public short fixedSize;
		public int charBaseId;
		public int charLangId;
		public int featureLeng;
		public int notesLeng;
		public int nStateLengs;

		@Override
		public void read(BinFile file) {
			super.read(file);
			ByteBuffer b = file.readByteBuffer(SIZE);

			fixedSize = b.getShort();
			charBaseId = b.getInt();
			charLangId = b.getInt();
			featureLeng = b.getInt();
			notesLeng = b.getInt();
			nStateLengs = b.getInt();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(charBaseId);
			file.write(charLangId);
			file.write(featureLeng);
			file.write(notesLeng);
			file.write(nStateLengs);

		}

	}

}
