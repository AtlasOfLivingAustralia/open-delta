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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.Logger;
import au.org.ala.delta.slotfile.directive.ConforDirType;
import au.org.ala.delta.slotfile.directive.DistDirType;
import au.org.ala.delta.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.slotfile.directive.KeyDirType;
import au.org.ala.delta.util.Utils;

public class VODirFileDesc extends VOAnyDesc implements WindowsConstants {

	public static final int FILEFLAG_SPECS = 0x0001;
	public static final int FILEFLAG_CHARS = 0x0002;
	public static final int FILEFLAG_ITEMS = 0x0004;
	public static final int FILEFLAG_CONFOR_SPECIAL = (FILEFLAG_SPECS | FILEFLAG_CHARS | FILEFLAG_ITEMS);
	public static final int FILEFLAG_DETAILS_MASK = 0x0000FFFF;

	public static final int DIRARG_COMMENT_FLAG = 0x80000000;
	public static final int DIRARG_FOREIGN_FLAG = 0x40000000;
	public static final int DIRARG_DIRTYPE_MASK = (~(DIRARG_COMMENT_FLAG | DIRARG_FOREIGN_FLAG));

	public static final int PROGTYPE_CONFOR = 0;
	public static final int PROGTYPE_INTKEY = 1;
	public static final int PROGTYPE_DIST = 2;
	public static final int PROGTYPE_KEY = 3;
	public static final int PROGTYPE_ALL = -1;

	public static final int IS_ITEM_ID = -1;
	public static final int IS_CHAR_ID = -2;
	public static final int ITEM_KEYWORD = 0x81000000;
	public static final int CHAR_KEYWORD = 0x82000000;
	public static final int INTKEY_TEXT_MASK = 0x00ffffff;
	public static final int INTKEY_TYPE_MASK = 0xff000000;

	private DirFileFixedData _fixedData;
	private List<DirSummary> _dirLocVector;
	protected Map<Integer, Integer> _freeBlockMap = new HashMap<Integer, Integer>();
	private int _dirOffset;

	protected int _nDirectives;
	protected List<Directive> _directiveArray;

	public VODirFileDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);

		_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
		short diskFixedSize = _slotFile.readShort();
		_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		assert diskFixedSize == DirFileFixedData.SIZE;

		_fixedData = new DirFileFixedData();
		_fixedData.read(_slotFile);

//		Logger.debug("DirectiveFile: %s (Flags %d, nDirs=%d, filetime=%s)", new String(_fixedData.fileName).trim(), _fixedData.fileFlags, _fixedData.nDirs,
				//Utils.FILETIMEToDate(_fixedData.fileModifyTime));

		dataSeek(0);

		setDirArray();

		_dirOffset = _fixedData.nDirs * DirSummary.SIZE;
		_dirLocVector = new ArrayList<VODirFileDesc.DirSummary>(_fixedData.nDirs);
		for (int i = 0; i < _fixedData.nDirs; ++i) {
			DirSummary dirLoc = new DirSummary();
			dirLoc.read(_slotFile);
			_dirLocVector.add(dirLoc);
		}

	}

	@Override
	public int getTypeId() {
		return VODescFactory.VODirFileDesc_TypeId;
	}

	@Override
	public String getStringId() {
		return "Directives file descriptor";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	public void StoreQData() {
		// Write the cached data
		throw new NotImplementedException();
	}

	public String getFileName() {
		return new String(_fixedData.fileName);
	}

	public void setFileName(String newFileName) {
		int leng = Math.min(MAX_PATH - 1, newFileName.length());
		_fixedData.fileName = newFileName.substring(0, leng);
		setDirty();
	}

	public long getFileModifyTime() {
		return _fixedData.fileModifyTime;
	}

	public void setFileModifyTime(long newTime) {
		_fixedData.fileModifyTime = newTime;
		setDirty();
	}

	public int getFileFlags() {
		return _fixedData.fileFlags;
	}

	public short getFileType() {
		return Utils.LOWORD(_fixedData.fileFlags);
	}

	public short getProgType() {
		return Utils.HIWORD(_fixedData.fileFlags);
	}

	public void setFileFlags(int flagVal) {
		_fixedData.fileFlags = flagVal;
		setDirty();
	}

	public void setFileType(short typeVal) {
		_fixedData.fileFlags |= (typeVal & 0xffff);
		setDirty();
	}

	public void clearFileType(short typeVal) {
		_fixedData.fileFlags &= (~typeVal | 0xffff0000);
		setDirty();
	}

	public void setProgType(short progType) {
		_fixedData.fileFlags |= (progType << 16);
		setDirArray();
		setDirty();
	}

	public void setDirArray() {
		switch (getProgType()) {
			case PROGTYPE_CONFOR:
				_directiveArray = Arrays.asList(ConforDirType.ConforDirArray);
				_nDirectives = _directiveArray.size();
				break;
			case PROGTYPE_KEY:
				_directiveArray = Arrays.asList(KeyDirType.KeyDirArray);
				_nDirectives = _directiveArray.size();
				break;
			case PROGTYPE_DIST:
				_directiveArray = Arrays.asList(DistDirType.DistDirArray);
				_nDirectives = _directiveArray.size();
				break;
			case PROGTYPE_INTKEY:
				_directiveArray = Arrays.asList(IntkeyDirType.IntkeyDirArray);
				_nDirectives = _directiveArray.size();
				break;
		}
	}
	
	public List<Directive> getDirArray() {
		return _directiveArray;
	}

	public int getNDirectives() {
		return _dirLocVector.size();
	}

	public Dir readDirective(int dirNo) {
		return readDirective(dirNo, null);

	}

	private void readAsText(Dir directive) {
		int nObjs = readInt();
		directive.resizeArgs(1);
		directive.args[0].text = readString(nObjs);
	}

	public Dir readDirective(int dirNo, List<Integer> dirIncludeFilter) {

		Dir directive = new Dir();

		if (dirNo >= _dirLocVector.size()) {
			directive.dirType = 0;
			return directive;
			// / SHOULD POSSIBLY THROW SOMETHING INSTEAD OF JUST RETURNING???
		}

		dataSeek(_dirOffset + _dirLocVector.get(dirNo).getLoc() + 4); // Skip over blockLeng (that's the "long" part)

		int dirType = readInt();

		if (dirType != _dirLocVector.get(dirNo).getType()) {
			// should throw something....
			throw new RuntimeException("Inconsistent directive types");
		}

		directive.dirType = dirType;

		// dirType &= ~DIRARG_COMMENT_FLAG;
		dirType &= DIRARG_DIRTYPE_MASK;

		if (dirType >= _nDirectives) {
			throw new RuntimeException("Internal error. nDirectives mismatch");
		}

		if (dirIncludeFilter != null && dirIncludeFilter.contains(dirType)) {
			return null;
		}

		// check that DirectiveArray has been sorted, so that the element at
		// index i has directiveNumber == i
		if (_directiveArray.get(dirType).getNumber() != dirType) {
			// throw...
			throw new RuntimeException("Array of directives not sorted!");
		}

		int argType = _directiveArray.get(dirType).getArgType();

		int nObjs = 0;
		// char* buffer;
		int i, j;

		switch (argType) {
			case DirectiveArgType.DIRARG_NONE:
			case DirectiveArgType.DIRARG_TRANSLATION:
			case DirectiveArgType.DIRARG_INTERNAL:
			case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
				break;

			// ReadAsText:
			case DirectiveArgType.DIRARG_OTHER:
			case DirectiveArgType.DIRARG_TEXT:
			case DirectiveArgType.DIRARG_FILE:
			case DirectiveArgType.DIRARG_COMMENT: {
				readAsText(directive);
				break;
			}
			case DirectiveArgType.DIRARG_INTEGER:
			case DirectiveArgType.DIRARG_REAL:
			case DirectiveArgType.DIRARG_INTKEY_ONOFF:

				directive.resizeArgs(1);
				directive.args[0].value = readNumber();
				break;

			case DirectiveArgType.DIRARG_CHAR:
			case DirectiveArgType.DIRARG_ITEM:
				directive.resizeArgs(1);
				directive.args[0].id = readInt();
				break;

			case DirectiveArgType.DIRARG_CHARLIST:
			case DirectiveArgType.DIRARG_ITEMLIST:
				nObjs = readInt();
				directive.resizeArgs(nObjs);

				for (i = 0; i < nObjs; ++i) {
					directive.args[i].id = readInt();
				}
				break;

			case DirectiveArgType.DIRARG_TEXTLIST:
			case DirectiveArgType.DIRARG_CHARTEXTLIST:
			case DirectiveArgType.DIRARG_ITEMTEXTLIST:
			case DirectiveArgType.DIRARG_ITEMFILELIST: {
				boolean commentsSupported = false; // Flag for presence of comment fields, added August 2000
				nObjs = readInt();

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) {
					// For each ID
					directive.args[i].id = readInt();
					if (i == 0) // Do checking to allow for changes in file structure
					{
						if (directive.args[i].id == VOUID_NAME) {
							commentsSupported = true;
						} else if (argType == DirectiveArgType.DIRARG_TEXTLIST) { // These directives used to be treated as DIRARG_TEXT prior to 30 August 2000
																					// If this is the case, back up and read it as if it where DIRARG_TEXT.
							directive.args[i].id = VOUID_NULL;
							dataSeek(_dirOffset + _dirLocVector.get(dirNo).getLoc() + 4 + 4);
							readAsText(directive);
							continue;
						}
					}
					nObjs = readInt();
					directive.args[i].text = readString(nObjs);

					if (commentsSupported) {
						nObjs = readInt();
						directive.args[i].comment = readString(nObjs);
					}
				}
				break;
			}

			case DirectiveArgType.DIRARG_CHARINTEGERLIST:
			case DirectiveArgType.DIRARG_CHARREALLIST:
			case DirectiveArgType.DIRARG_ITEMREALLIST:
				nObjs = readInt();

				directive.resizeArgs(nObjs);
				for (i = 0; i < nObjs; ++i) {
					// For each ID
					directive.args[i].id = readInt();
					directive.args[i].value = readNumber();
				}
				break;

			case DirectiveArgType.DIRARG_CHARGROUPS:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) { // For each char group
					nObjs = readInt();
					directive.args[i].resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) { // Read the char ids.
						directive.args[i].dataVect[j].setUniId(readInt());
					}
				}
				break;

			case DirectiveArgType.DIRARG_ITEMCHARLIST:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) { // For each item
					directive.args[i].id = readInt();
					nObjs = readInt();
					directive.args[i].resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) {
						// Read the char ids.
						directive.args[i].dataVect[j].setUniId(readInt());
					}
				}
				break;

			case DirectiveArgType.DIRARG_ALLOWED:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) {
					// For each character
					directive.args[i].id = readInt();

					directive.args[i].resizeDataVect(3);
					for (j = 0; j < 3; ++j) {
						// Read the numbers.
						directive.args[i].dataVect[j].read(_slotFile);
					}
				}
				break;

			case DirectiveArgType.DIRARG_KEYSTATE:
				nObjs = readInt(); // Read number of key states

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) { // For each key state
					directive.args[i].id = readInt();
					directive.args[i].value = readNumber();
					nObjs = readInt();

					directive.args[i].resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) {
						// Read associated values.
						directive.args[i].dataVect[j].read(_slotFile);
					}
				}
				break;

			case DirectiveArgType.DIRARG_PRESET:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) { // For each character
					directive.args[i].id = readInt();
					directive.args[i].resizeDataVect(2);
					for (j = 0; j < 2; ++j)
						// Read the numbers.
						directive.args[i].dataVect[j].setIntNumb(readInt());
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ITEM:
				directive.resizeArgs(1);
				nObjs = readInt();
				if (nObjs < 0) // Is an ID
					directive.args[0].id = readInt();
				else // Is a keyword string (which OUGHT to refer to a single taxon)
				{
					directive.args[0].text = readString(nObjs);
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
			case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
			case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
			case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST: // Almost like the others, but not quite....
				nObjs = readInt();

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) {
					nObjs = readInt();
					if (nObjs < 0) // Is an ID
						directive.args[i].id = readInt();
					else // Is a keyword string (or possibly command modifier
					{
						nObjs &= INTKEY_TEXT_MASK;

						directive.args[i].text = readString(nObjs);
					}
					if (argType == DirectiveArgType.DIRARG_INTKEY_CHARREALLIST)
						directive.args[i].value = readNumber();
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) {
					nObjs = readInt();
					if (nObjs == IS_ITEM_ID) // Is an item ID
					{
						directive.args[i].id = readInt();
						directive.args[i].value.setFromValue((float) -1.0);
					} else if (nObjs == IS_CHAR_ID) // Is an character ID
					{
						directive.args[i].id = readInt();
						directive.args[i].value.setFromValue((float) 1.0);
					} else // Is a string
					{
						int type = nObjs & INTKEY_TYPE_MASK;
						if (type != 0) {
							if (type == ITEM_KEYWORD)
								directive.args[i].value.setFromValue((float) -1.0);
							else if (type == CHAR_KEYWORD)
								directive.args[i].value.setFromValue((float) 1.0);
							nObjs &= INTKEY_TEXT_MASK;
						} else
							directive.args[i].value.setFromValue((float) 0.0);

						directive.args[i].text = readString(nObjs);
					}
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.length; ++i) {
					nObjs = readInt();
					if (nObjs == IS_CHAR_ID) // Is an ID
					{
						directive.args[i].id = readInt();
					} else // Is a keyword string (or possibly command modifier
					{
						nObjs &= INTKEY_TEXT_MASK;
						directive.args[i].text = readString(nObjs);
					}
					nObjs = readInt();
					if (nObjs > 0) {
						Attribute ourAttrib = directive.args[i].attrib; // Just to simplify the following statements, like a Pascal "with"

						ourAttrib.setCharId(directive.args[i].id);
						byte[] data = readBytes(nObjs);
						ourAttrib.setData(data);
						ourAttrib.initReadData();
					}
				}
				break;

			default:
				throw new RuntimeException("Bad arg type");
		}

		return directive;

	}

	public List<Dir> readAllDirectives() {
		return readAllDirectives(null);
	}

	public List<Dir> readAllDirectives(List<Integer> dirIncludeFilter) {
		setDirArray();
		List<Dir> directiveList = new ArrayList<VODirFileDesc.Dir>(_dirLocVector.size());
		for (int i = 0; i < _dirLocVector.size(); ++i) {
			Dir aDir = readDirective(i, dirIncludeFilter);
			if (aDir != null) {
				directiveList.add(aDir);
			}
		}
		return directiveList;
	}

	public long getWriteLength(Dir directive) {
		throw new NotImplementedException();
	}

	public void writeDirective(Dir directive, int dirNo) {
		throw new NotImplementedException();
	}

	public void deleteDirective(int dirNo) {
		throw new NotImplementedException();
	}

	public void deleteItem(VOP vop, int itemId) {
		throw new NotImplementedException();
	}

	public void deleteChar(VOP vop, int charId) {
		throw new NotImplementedException();
	}

	public void deleteState(VOP vop, VOCharBaseDesc charBase, int stateId) {
		throw new NotImplementedException();
	}

	public void writeAllDirectives(List<Dir> directiveList) {
		throw new NotImplementedException();
	}

	public int getPrincipleConforAction() {

		if (getProgType() != PROGTYPE_CONFOR) {
			return 0;
		}

		int bestType = 0;
		for (VODirFileDesc.DirSummary dirLocSummary : _dirLocVector) {
			int dirType = dirLocSummary.getType();
			switch (dirType) {
				case ConforDirType.NUMBER_OF_CHARACTERS: // used to "flags" specifications generally
				case ConforDirType.ITEM_DESCRIPTIONS:
				case ConforDirType.CHARACTER_LIST:
				case ConforDirType.TRANSLATE_INTO_ALICE_FORMAT:
				case ConforDirType.TRANSLATE_INTO_DELTA_FORMAT:
				case ConforDirType.TRANSLATE_INTO_DCR_FORMAT:
				case ConforDirType.TRANSLATE_INTO_DIST_FORMAT:
				case ConforDirType.TRANSLATE_INTO_HENNIG86_FORMAT:
				case ConforDirType.TRANSLATE_INTO_INTKEY_FORMAT:
				case ConforDirType.TRANSLATE_INTO_KEY_FORMAT:
				case ConforDirType.TRANSLATE_INTO_NATURAL_LANGUAGE:
				case ConforDirType.TRANSLATE_INTO_NEXUS_FORMAT:
				case ConforDirType.TRANSLATE_INTO_PAUP_FORMAT:
				case ConforDirType.TRANSLATE_INTO_PAYNE_FORMAT:
					return dirType;
				case ConforDirType.PRINT_ITEM_NAMES:
				case ConforDirType.PRINT_ITEM_DESCRIPTIONS:
				case ConforDirType.PRINT_CHARACTER_LIST:
				case ConforDirType.PRINT_SUMMARY:
				case ConforDirType.PRINT_UNCODED_CHARACTERS:
					bestType = dirType;
					break;
				case ConforDirType.CHARACTER_NOTES:
				case ConforDirType.CHARACTER_IMAGES:
				case ConforDirType.TAXON_IMAGES:
				case ConforDirType.STARTUP_IMAGES:
				case ConforDirType.CHARACTER_KEYWORD_IMAGES:
				case ConforDirType.TAXON_KEYWORD_IMAGES:
					if (bestType == 0)
						bestType = dirType;
					break;
				default:
					break;
			}
		}
		return bestType;
	}

	protected void write(Dir directive) {
		throw new NotImplementedException();
	}

	// Fixed data and offsets...
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int fileNameOffs = fixedSizeOffs + 2;
	public static final int fileModifyTimeOffs = fileNameOffs + MAX_PATH;
	public static final int nDirsOffs = fileModifyTimeOffs + 8;
	public static final int nFileFlagsOffs = nDirsOffs + 4;

	public class DirFileFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + 2 + MAX_PATH + 8 + 4 + 4;

		public DirFileFixedData() {
			super("DirFile Desc");
			this.TypeID = VODescFactory.VODirFileDesc_TypeId;
		}

		public short fixedSize;
		public String fileName; // [MAX_PATH];
		public long fileModifyTime;
		public int nDirs; // Number of directives
		public int fileFlags; // Flags whether specs, chars, items, etc. See the
								// FILEFLAG_* definitions

		@Override
		public void read(BinFile file) {
			super.read(file);
			fixedSize = file.readShort();
			fileName = file.sread(MAX_PATH).trim();
			fileModifyTime = file.readLong();
			nDirs = file.readInt();
			fileFlags = file.readInt();
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.write(fixedSize);
			file.write(fileName, MAX_PATH);
			file.write(fileModifyTime);
			file.write(nDirs);
			file.write(fileFlags);
		}

	}

	public class Dir {
		public int dirType;
		public DirArgs[] args = new DirArgs[] {};

		public void resizeArgs(int size) {
			args = new DirArgs[size];
			for (int i = 0; i < size; ++i) {
				args[i] = new DirArgs();
			}
		}

		@Override
		public String toString() {
			return String.format("dirType=%d, args=%s", dirType, Arrays.asList(args));
		}
	}

	public class DirArgs {

		public DirArgs() {
			this(0);
		}

		public DirArgs(int id) {
			this.id = id;
			dataVect = new DirListData[] {};
		}

		public String text;
		public String comment;
		DeltaNumber value = new DeltaNumber();
		int id;
		DirListData[] dataVect;
		Attribute attrib = new Attribute();

		public void resizeDataVect(int size) {
			dataVect = new DirListData[size];
			for (int i = 0; i < size; ++i) {
				dataVect[i] = new DirListData();
			}
		}

		@Override
		public String toString() {
			return String.format("ArgId=%d, text=%s comment=%s value=%s, dataVect=%s", id, text, comment, value, dataVect == null ? "null" : Arrays.asList(dataVect));
		}
	}

	/**
	 * Simulates a union over 4 bytes + an additional byte for the potential number of decimal places
	 * 
	 * ....yecchhh!
	 * 
	 * @author baird
	 * 
	 */
	public class DirListData implements IOObject {

		private byte[] _bytes = new byte[4];
		private byte _decimal;

		private void setAsInt(int val) {
			ByteBuffer b = ByteBuffer.wrap(_bytes);
			b.order(ByteOrder.LITTLE_ENDIAN);
			b.putInt(val);
		}

		private int getAsInt() {
			ByteBuffer b = ByteBuffer.wrap(_bytes);
			b.order(ByteOrder.LITTLE_ENDIAN);
			return b.getInt();
		}

		private void setAsFloat(float val) {
			ByteBuffer b = ByteBuffer.wrap(_bytes);
			b.order(ByteOrder.LITTLE_ENDIAN);
			b.putFloat(val);
		}

		private float getAsFloat() {
			ByteBuffer b = ByteBuffer.wrap(_bytes);
			b.order(ByteOrder.LITTLE_ENDIAN);
			return b.getFloat();
		}

		public void setUniId(int uniId) {
			setAsInt(uniId);
		}

		public int getUniId() {
			return getAsInt();
		}

		public void setStateId(int stateId) {
			setAsInt(stateId);
		}

		public int getStateId() {
			return getAsInt();
		}

		public void setRealNumb(float realNum) {
			setAsFloat(realNum);
		}

		public float getRealNumb() {
			return getAsFloat();
		}

		public void setIntNumb(int numb) {
			setAsInt(numb);
		}

		public int getIntNumb() {
			return getAsInt();
		}

		@Override
		public void read(BinFile file) {
			file.readBytes(_bytes);
			_decimal = file.readByte();
		}

		@Override
		public void write(BinFile file) {
			file.writeBytes(_bytes);
			file.writeByte(_decimal);
		}

	}

	public class DirSummary implements IOObject {

		public static final int SIZE = 4 + 4;

		private int _loc;
		private int _type;

		public DirSummary() {
			this(0, 0);
		}

		public DirSummary(int loc, int type) {
			_loc = loc;
			_type = type;
		}

		public int getLoc() {
			return _loc;
		}

		public int getType() {
			return _type;
		}

		@Override
		public void read(BinFile file) {
			_loc = file.readInt();
			_type = file.readInt();
		}

		@Override
		public void write(BinFile file) {
			file.write(_loc);
			file.write(_type);
		}

		@Override
		public String toString() {
			return String.format("DirSummary: Loc=%d, Type=%d", _loc, _type);
		}

	}

}
