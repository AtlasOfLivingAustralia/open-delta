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
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.directive.KeyDirType;
import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
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
		synchronized (getVOP()) {
			_slotFile.seek(_slotHdrPtr + fixedSizeOffs);
			short diskFixedSize = _slotFile.readShort();
			_dataOffs = SlotFile.SlotHeader.SIZE + diskFixedSize;
			_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
			assert diskFixedSize == DirFileFixedData.SIZE;

			_fixedData = new DirFileFixedData();
			_fixedData.read(_slotFile);

			// Logger.debug("DirectiveFile: %s (Flags %d, nDirs=%d, filetime=%s)", new String(_fixedData.fileName).trim(), _fixedData.fileFlags, _fixedData.nDirs,
			// Utils.FILETIMEToDate(_fixedData.fileModifyTime));

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

	public void storeQData() {
	    
		List<Dir> dirList = readAllDirectives();
		if (_fixedData.fixedSize <  DirFileFixedData.SIZE) {
		    _dataOffs = SlotFile.SlotHeader.SIZE + DirFileFixedData.SIZE; ///// Adjust DataOffs accordingly
		    _fixedData.fixedSize = DirFileFixedData.SIZE;
		}
	    writeAllDirectives(dirList);
	}

	public String getFileName() {
		return new String(_fixedData.fileName);
	}

	public void setFileName(String newFileName) {
		int leng = Math.min(MAX_PATH - 1, newFileName.length());
		_fixedData.fileName = newFileName.substring(0, leng);
		// Because the length isn't stored anywhere we need to null terminate 
		// the string.
		_fixedData.fileName = _fixedData.fileName + Character.toString((char)0);
		setDirty();
	}

	private long windowsToJavaTimeOffset() {
		Calendar cal = Calendar.getInstance();
		cal.clear();
		cal.set(1601, 01, 01);
		
		Calendar cal2 = Calendar.getInstance();
		cal2.clear();
		cal2.set(1970, 01, 01);
		
		return cal.getTimeInMillis() - cal2.getTimeInMillis();
	}
	
	public long getFileModifyTime() {
		// Convert from the windows FILETIME format (the number of 100
		// nanosecond intervals since Jan 1, 1601) to the Java representation
		// (number of milliseconds since Jan 1, 1970).
		
		long windowsFileTime = _fixedData.fileModifyTime;
		long javaFileTime = windowsFileTime/10000 + windowsToJavaTimeOffset();
		return javaFileTime;
	}

	public void setFileModifyTime(long newTime) {
		long windowsFileTime = (newTime - windowsToJavaTimeOffset()) * 10000L;
		_fixedData.fileModifyTime = windowsFileTime;
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
		directive.args.get(0).text = readString(nObjs);
	}

	public Dir readDirective(int dirNo, List<Integer> dirIncludeFilter) {
		synchronized (getVOP()) {
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

			if (dirIncludeFilter != null && !dirIncludeFilter.contains(dirType)) {
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
				directive.args.get(0).value = readNumber();
				break;

			case DirectiveArgType.DIRARG_CHAR:
			case DirectiveArgType.DIRARG_ITEM:
				directive.resizeArgs(1);
				directive.args.get(0).id = readInt();
				break;

			case DirectiveArgType.DIRARG_CHARLIST:
			case DirectiveArgType.DIRARG_ITEMLIST:
				nObjs = readInt();
				directive.resizeArgs(nObjs);

				for (i = 0; i < nObjs; ++i) {
					directive.args.get(i).id = readInt();
				}
				break;

			case DirectiveArgType.DIRARG_TEXTLIST:
			case DirectiveArgType.DIRARG_CHARTEXTLIST:
			case DirectiveArgType.DIRARG_ITEMTEXTLIST:
			case DirectiveArgType.DIRARG_ITEMFILELIST: {
				boolean commentsSupported = false; // Flag for presence of comment fields, added August 2000
				nObjs = readInt();

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) {
					// For each ID
					directive.args.get(i).id = readInt();
					if (i == 0) // Do checking to allow for changes in file structure
					{
						if (directive.args.get(i).id == VOUID_NAME) {
							commentsSupported = true;
						} else if (argType == DirectiveArgType.DIRARG_TEXTLIST) { // These directives used to be treated as DIRARG_TEXT prior to 30 August 2000
																					// If this is the case, back up and read it as if it where DIRARG_TEXT.
							directive.args.get(i).id = VOUID_NULL;
							dataSeek(_dirOffset + _dirLocVector.get(dirNo).getLoc() + 4 + 4);
							readAsText(directive);
							continue;
						}
					}
					nObjs = readInt();
					directive.args.get(i).text = readString(nObjs);

					if (commentsSupported) {
						nObjs = readInt();
						directive.args.get(i).comment = readString(nObjs);
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
					directive.args.get(i).id = readInt();
					directive.args.get(i).value = readNumber();
				}
				break;

			case DirectiveArgType.DIRARG_CHARGROUPS:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) { // For each char group
					nObjs = readInt();
					directive.args.get(i).resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) { // Read the char ids.
						directive.args.get(i).dataVect.get(j).setUniId(readInt());
					}
				}
				break;

			case DirectiveArgType.DIRARG_ITEMCHARLIST:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) { // For each item
					directive.args.get(i).id = readInt();
					nObjs = readInt();
					directive.args.get(i).resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) {
						// Read the char ids.
						directive.args.get(i).dataVect.get(j).setUniId(readInt());
					}
				}
				break;

			case DirectiveArgType.DIRARG_ALLOWED:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) {
					// For each character
					directive.args.get(i).id = readInt();

					directive.args.get(i).resizeDataVect(3);
					for (j = 0; j < 3; ++j) {
						// Read the numbers.
						directive.args.get(i).dataVect.get(j).read(_slotFile);
					}
				}
				break;

			case DirectiveArgType.DIRARG_KEYSTATE:
				nObjs = readInt(); // Read number of key states

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) { // For each key state
					directive.args.get(i).id = readInt();
					directive.args.get(i).value = readNumber();
					nObjs = readInt();
					directive.args.get(i).resizeDataVect(nObjs);
					for (j = 0; j < nObjs; ++j) {
						// Read associated values.
						directive.args.get(i).dataVect.get(j).read(_slotFile);
					}
				}
				break;

			case DirectiveArgType.DIRARG_PRESET:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) { // For each character
					directive.args.get(i).id = readInt();
					directive.args.get(i).resizeDataVect(2);
					for (j = 0; j < 2; ++j)
						// Read the numbers.
						directive.args.get(i).dataVect.get(j).setIntNumb(readInt());
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ITEM:
				directive.resizeArgs(1);
				nObjs = readInt();
				if (nObjs < 0) // Is an ID
					directive.args.get(0).id = readInt();
				else // Is a keyword string (which OUGHT to refer to a single taxon)
				{
					directive.args.get(0).text = readString(nObjs);
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
			case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
			case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
			case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST: // Almost like the others, but not quite....
				nObjs = readInt();

				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) {
					nObjs = readInt();
					if (nObjs < 0) // Is an ID
						directive.args.get(i).id = readInt();
					else // Is a keyword string (or possibly command modifier
					{
						nObjs &= INTKEY_TEXT_MASK;

						directive.args.get(i).text = readString(nObjs);
					}
					if (argType == DirectiveArgType.DIRARG_INTKEY_CHARREALLIST)
						directive.args.get(i).value = readNumber();
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) {
					nObjs = readInt();
					if (nObjs == IS_ITEM_ID) // Is an item ID
					{
						directive.args.get(i).id = readInt();
						directive.args.get(i).value.setFromValue((float) -1.0);
					} else if (nObjs == IS_CHAR_ID) // Is an character ID
					{
						directive.args.get(i).id = readInt();
						directive.args.get(i).value.setFromValue((float) 1.0);
					} else // Is a string
					{
						int type = nObjs & INTKEY_TYPE_MASK;
						if (type != 0) {
							if (type == ITEM_KEYWORD)
								directive.args.get(i).value.setFromValue((float) -1.0);
							else if (type == CHAR_KEYWORD)
								directive.args.get(i).value.setFromValue((float) 1.0);
							nObjs &= INTKEY_TEXT_MASK;
						} else
							directive.args.get(i).value.setFromValue((float) 0.0);

						directive.args.get(i).text = readString(nObjs);
					}
				}
				break;

			case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
				nObjs = readInt();
				directive.resizeArgs(nObjs);
				for (i = 0; i < directive.args.size(); ++i) {
					nObjs = readInt();
					if (nObjs == IS_CHAR_ID) // Is an ID
					{
						directive.args.get(i).id = readInt();
					} else // Is a keyword string (or possibly command modifier
					{
						nObjs &= INTKEY_TEXT_MASK;
						directive.args.get(i).text = readString(nObjs);
					}
					nObjs = readInt();
					if (nObjs > 0) {
						Attribute ourAttrib = directive.args.get(i).attrib; // Just to simplify the following statements, like a Pascal "with"

						ourAttrib.setCharId(directive.args.get(i).id);
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

	public int getWriteLength(Dir directive) {
		int dirType = directive.getDirType() & DIRARG_DIRTYPE_MASK;

		if (dirType >= _nDirectives) {
		    return 0;
	    }

		// check that DirectiveArray has been sorted, so that the element at
		// index i has directiveNumber == i
		if (_directiveArray.get(dirType).getNumber() != dirType) {
		 
		    throw new IllegalStateException("Array of directives not sorted!");
		}

		int argType = _directiveArray.get(dirType).getArgType();
		
		int size = SIZE_OF_INT_IN_BYTES;  // for the directive type
		int i;
		switch (argType) {
		      case DirectiveArgType.DIRARG_NONE:
		      case DirectiveArgType.DIRARG_TRANSLATION:
		      case DirectiveArgType.DIRARG_INTERNAL:
		      case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		        break;

		      case DirectiveArgType.DIRARG_OTHER:
		      case DirectiveArgType.DIRARG_TEXT:
		      case DirectiveArgType.DIRARG_FILE:
		      case DirectiveArgType.DIRARG_COMMENT:
		        size += SIZE_OF_INT_IN_BYTES;
		        if (directive.args.size() > 0) {
		        	size += directive.args.get(0).text.length();
		        }
		        break;

		      case DirectiveArgType.DIRARG_INTEGER:
		      case DirectiveArgType.DIRARG_REAL:
		      case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		        size += DeltaNumber.size();
		        break;

		      case DirectiveArgType.DIRARG_CHAR:
		      case DirectiveArgType.DIRARG_ITEM:
		        size += SIZE_OF_INT_IN_BYTES;
		        break;

		      case DirectiveArgType.DIRARG_CHARLIST:
		      case DirectiveArgType.DIRARG_ITEMLIST:
		        size += SIZE_OF_INT_IN_BYTES + directive.args.size() * SIZE_OF_INT_IN_BYTES;
		        break;

		      case DirectiveArgType.DIRARG_TEXTLIST:
		      case DirectiveArgType.DIRARG_CHARTEXTLIST:
		      case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		      case DirectiveArgType.DIRARG_ITEMFILELIST:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          {
		            size += SIZE_OF_INT_IN_BYTES + SIZE_OF_INT_IN_BYTES + directive.args.get(i).text.length();
		            if (directive.args.get(0).id == VOUID_NAME)
		              size += SIZE_OF_INT_IN_BYTES + directive.args.get(i).comment.length();
		          }
		        break;

		      case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		      case DirectiveArgType.DIRARG_CHARREALLIST:
		      case DirectiveArgType.DIRARG_ITEMREALLIST:
		        size += SIZE_OF_INT_IN_BYTES +
		               directive.args.size() * (SIZE_OF_INT_IN_BYTES + DeltaNumber.size());
		        break;

		      case DirectiveArgType.DIRARG_CHARGROUPS:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          size += SIZE_OF_INT_IN_BYTES +
		                  directive.args.get(i).dataVect.size() * SIZE_OF_INT_IN_BYTES;
		        break;

		      case DirectiveArgType.DIRARG_ITEMCHARLIST:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          size += SIZE_OF_INT_IN_BYTES + SIZE_OF_INT_IN_BYTES +
		                  directive.args.get(i).dataVect.size() * SIZE_OF_INT_IN_BYTES;
		        break;

		      case DirectiveArgType.DIRARG_ALLOWED:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          size += SIZE_OF_INT_IN_BYTES + 3 * DirListData.SIZE;
		        break;

		      case DirectiveArgType.DIRARG_KEYSTATE:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          size += SIZE_OF_INT_IN_BYTES +
		                  DeltaNumber.size() +
		                  SIZE_OF_INT_IN_BYTES +
		                  directive.args.get(i).dataVect.size() * DirListData.SIZE;
		        break;

		      case DirectiveArgType.DIRARG_PRESET:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          size += SIZE_OF_INT_IN_BYTES + 2 * SIZE_OF_INT_IN_BYTES;
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ITEM:
		        size += SIZE_OF_INT_IN_BYTES;
		        if (directive.args.size() == 0) // Having 1 "empty" argument should be equivalent
		          directive.resizeArgs(1); // to having no arguments, and allows use of index [0]
		        if (directive.args.get(0).id != VOUID_NULL)
		          size += SIZE_OF_INT_IN_BYTES;
		        else
		          size += directive.args.get(0).text.length();
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		      case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		      case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		      case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
		      case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		      case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          {
		            size += SIZE_OF_INT_IN_BYTES;
		            if (directive.args.get(i).id != VOUID_NULL)
		              size += SIZE_OF_INT_IN_BYTES;
		            else
		              size += directive.args.get(i).text.length();
		            if (argType == DirectiveArgType.DIRARG_INTKEY_CHARREALLIST)
		              size += DeltaNumber.size();
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		        size += SIZE_OF_INT_IN_BYTES;
		        for (i = 0; i < directive.args.size(); ++i)
		          {
		            size += SIZE_OF_INT_IN_BYTES;
		            if (directive.args.get(i).id != VOUID_NULL)
		              size += SIZE_OF_INT_IN_BYTES;
		            else
		              size += directive.args.get(i).text.length();
		            size += SIZE_OF_INT_IN_BYTES + directive.args.get(i).attrib.getDataLength();
		          }
		        break;

		      default:
		        break;
		    }
		  return size;
	}

	public void writeDirective(Dir directive, int dirNo) {
		if (directive.dirType == 0) { // This is a special case. Delete if already present.
	        deleteDirective(dirNo);
	        return;
	    }

	    int needSize = getWriteLength(directive);

	    if (dirNo < _dirLocVector.size())  {
	      
	      dataSeek(_dirOffset + _dirLocVector.get(dirNo).getLoc());
	      int oldBlockLeng = dataReadInt();
	      if (needSize <= oldBlockLeng) { // Just use the old block, if data will fit
	          write(directive);
	          setDirty();
	          return;
	      }
	      else { // Free up the currently used block
	          _freeBlockMap.put(_dirLocVector.get(dirNo).getLoc(), oldBlockLeng);
	          _dirLocVector.get(dirNo).setType(0);  // Flag as unused
	        }
	    }

	    int pos = bestFitFreeSlot(needSize);
	    int size = _freeBlockMap.get(pos);
	    if (pos >= 0) { // There is an "old" block large enough to hold this
	        dataSeek(_dirOffset + size + SIZE_OF_INT_IN_BYTES);
	        _freeBlockMap.remove(pos);
	    }
	    else {
	        int endPos = getDataSize();
	        dataSeek(endPos + SIZE_OF_INT_IN_BYTES + needSize); // DataSeek to grow slot
	        dataSeek(endPos); // Then seek to position for writing.
	        dataWrite(needSize);
	    }
	    int startPos = dataTell() - _dirOffset - SIZE_OF_INT_IN_BYTES;
	    write(directive);
	    if (dirNo >= _dirLocVector.size()) {
	        dirNo = _dirLocVector.size(); // If out of current range, then append.
	        _dirLocVector.add(null);
	    }
	    _dirLocVector.set(dirNo,  new DirSummary(directive.dirType, startPos));
	    setDirty();
	}
	
	private int bestFitFreeSlot(int neededSize) {
		int bestPos = -1;
		int closestSize = Integer.MAX_VALUE;
		for (int pos : _freeBlockMap.keySet()) {
			int size = _freeBlockMap.get(pos);
			if (size >= neededSize && size < closestSize) {
				closestSize = size;
				bestPos = pos;
			}
		}
		
		return bestPos;
	}

	public void deleteDirective(int dirNo) {
		if (dirNo < _dirLocVector.size()) {
	        dataSeek(_dirOffset + _dirLocVector.get(dirNo).getLoc());
	        int oldBlockLeng = dataReadInt();
	        _freeBlockMap.put(_dirLocVector.get(dirNo).getLoc(), oldBlockLeng);
	        _dirLocVector.remove(dirNo);
	        setDirty();
	    }
	}

	public void deleteItem(VOP vop, int itemId) {
		
		boolean changed = false;
		if (itemId == VOUID_NULL) {  // Just in case....
		    return;
		}
		List<Dir> directiveList = readAllDirectives();
		// Read in all directives, and look for those requiring modification.
		// If the entire directive is to be deleted, just set it's "type" to 0.
		// WriteAllDirectives should then omit it. This circumvents potential
		// problems in erasing vector elements while still trying to iterate over
		// them.
		for (Dir dir : directiveList) {
		    int j;
		    int dirType = dir.getDirType() & DIRARG_DIRTYPE_MASK;
		    int argType = _directiveArray.get(dirType).getArgType();
		    switch (argType) {
		        case DirectiveArgType.DIRARG_NONE:
		        case DirectiveArgType.DIRARG_TRANSLATION:
		        case DirectiveArgType.DIRARG_OTHER:
		        case DirectiveArgType.DIRARG_INTERNAL:
		        case DirectiveArgType.DIRARG_TEXT:
		        case DirectiveArgType.DIRARG_FILE:
		        case DirectiveArgType.DIRARG_COMMENT:
		        case DirectiveArgType.DIRARG_INTEGER:
		        case DirectiveArgType.DIRARG_REAL:
		        case DirectiveArgType.DIRARG_CHAR:
		        case DirectiveArgType.DIRARG_CHARLIST:
		        case DirectiveArgType.DIRARG_TEXTLIST:
		        case DirectiveArgType.DIRARG_CHARTEXTLIST:
		        case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		        case DirectiveArgType.DIRARG_CHARREALLIST:
		        case DirectiveArgType.DIRARG_CHARGROUPS:
		        case DirectiveArgType.DIRARG_ALLOWED:
		        case DirectiveArgType.DIRARG_KEYSTATE:
		        case DirectiveArgType.DIRARG_PRESET:
		        case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		        case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		        case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		        case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		        case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		        case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		        default:
		            break; // The above don't store info. about items...

		        case DirectiveArgType.DIRARG_ITEM:
		        case DirectiveArgType.DIRARG_ITEMLIST:
		        case DirectiveArgType.DIRARG_ITEMCHARLIST:
		        case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		        case DirectiveArgType.DIRARG_ITEMFILELIST:
		        case DirectiveArgType.DIRARG_ITEMREALLIST:
		        case DirectiveArgType.DIRARG_INTKEY_ITEM:
		        case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		        case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
		        case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		            // Loop through all arguments of the directive.
		            // If the id == the target id, erase the argument, but be careful
		            // to decrement the "iterator" after doing so.
		        	for (j = 0; j < dir.args.size(); ++j) {
		                if (dir.args.get(j).id == itemId)  {
		                    dir.args.remove(j--);
		                    changed = true;
		                }
		            }
		            // If there are no items left, delete the entire directive
		            if (dir.args.size() == 0 && !_directiveArray.equals(Arrays.asList(IntkeyDirType.IntkeyDirArray))) {
		                dir.setDirType(0);
		            }
		            break;
		        }
		    }
		  if (changed) {
		      writeAllDirectives(directiveList);
		  }
	}

	public void deleteChar(VOP vop, int charId) {
		boolean changed = false;
		List<Dir> directiveList = readAllDirectives();
		if (charId == VOUID_NULL) {
		    return;
		}
		// Read in all directives, and look for those requiring modification.
		// If the entire directive is to be deleted, just set it's "type" to 0.
		// WriteAllDirectives should then omit it. This circumvents potential
		// problems in erasing vector elements while still trying to iterate over
		// them.
		for (Dir dir : directiveList) {
		    int j;
		    int dirType = dir.getDirType() & DIRARG_DIRTYPE_MASK;
		    int argType = _directiveArray.get(dirType).getArgType();
		    switch (argType) {
		        case DirectiveArgType.DIRARG_NONE:
		        case DirectiveArgType.DIRARG_TRANSLATION:
		        case DirectiveArgType.DIRARG_OTHER:
		        case DirectiveArgType.DIRARG_INTERNAL:
		        case DirectiveArgType.DIRARG_TEXT:
		        case DirectiveArgType.DIRARG_FILE:
		        case DirectiveArgType.DIRARG_COMMENT:
		        case DirectiveArgType.DIRARG_INTEGER:
		        case DirectiveArgType.DIRARG_REAL:
		        case DirectiveArgType.DIRARG_ITEM:
		        case DirectiveArgType.DIRARG_ITEMLIST:
		        case DirectiveArgType.DIRARG_TEXTLIST:
		        case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		        case DirectiveArgType.DIRARG_ITEMFILELIST:
		        case DirectiveArgType.DIRARG_ITEMREALLIST:
		        case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		        case DirectiveArgType.DIRARG_INTKEY_ITEM:
		        case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		        case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
		        case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		        default:
		            break; // The above don't store info. about characters...

		        case DirectiveArgType.DIRARG_CHAR:
		        case DirectiveArgType.DIRARG_CHARLIST:
		        case DirectiveArgType.DIRARG_CHARTEXTLIST:
		        case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		        case DirectiveArgType.DIRARG_CHARREALLIST:
		        case DirectiveArgType.DIRARG_ALLOWED:
		        case DirectiveArgType.DIRARG_KEYSTATE:
		        case DirectiveArgType.DIRARG_PRESET:
		        case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		        case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		        case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		        case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		        case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		            // Loop through all arguments of the directive.
		            // If the id == the target id, erase the argument, but be careful
		            // to decrement the "iterator" after doing so.
		        	for (j = 0; j < dir.args.size(); ++j) {
		                if (dir.args.get(j).id == charId)  {
		                    dir.args.remove(j--);
		                    changed = true;
		                }
		            }
		            // If there are no items left, delete the entire directive
		            if (dir.args.size() == 0) {
		                dir.setDirType(0);
		            }
		            
		         case DirectiveArgType.DIRARG_CHARGROUPS:
		         case DirectiveArgType.DIRARG_ITEMCHARLIST:
		            // Loop through all arguments of the directive.
		            for (j = 0; j < dir.args.size(); ++j) {
		                for (int k = 0; k < dir.args.get(j).dataVect.size(); ++k)  {
		                    if (dir.args.get(j).dataVect.get(k).getUniId() == charId) {
		                        dir.args.get(j).dataVect.remove(k--);
		                        changed = true;
		                    }
		                }
		                if (dir.args.get(j).dataVect.isEmpty()) {
		                    dir.args.remove(j--);
		                }
		            }
		            if (dir.args.size() == 0) {
		                dir.setDirType(0);
		            }
		            break;
		        }
		    }
		if (changed) {   
		    writeAllDirectives(directiveList);
		}
	}

	public void deleteState(VOP vop, VOCharBaseDesc charBase, int stateId) {
		int charType = charBase.getCharType();
		if (!CharType.isMultistate(charType) || stateId == VOCharBaseDesc.STATEID_NULL) {
		    return;
		}
		boolean changed = false;
		int charId = charBase.getUniId();
		List<Dir> directiveList = readAllDirectives();
		// Read in all directives, and look for those requiring modification.
		// For CONFOR, only the *KEY STATES directive is involved
		for (Dir dir : directiveList) {
		    int j;
		    int dirType = dir.getDirType() & DIRARG_DIRTYPE_MASK;
		    int argType = _directiveArray.get(dirType).getArgType();
		    if (argType == DirectiveArgType.DIRARG_KEYSTATE) {
		        for (j = 0; j < dir.args.size(); ++j) {
		            if (dir.args.get(j).id == charId) {
		                if (charType == CharType.UNORDERED) {
		                    for (int k = 0; k < dir.args.get(j).dataVect.size(); ++k) {
		                        if (dir.args.get(j).dataVect.get(k).getStateId() == stateId) {
		                              dir.args.get(j).dataVect.remove(k--);
		                              changed = true;
		                        }
		                    }
		                    if (dir.args.get(j).dataVect.isEmpty()) {
		                        dir.args.remove(j--);
		                    }
		                }
		                else if (charType == CharType.ORDERED || charType == CharType.LIST) {
		                    if (dir.args.get(j).dataVect.size() < 2) {
		                        throw new RuntimeException("ED_INTERNAL_ERROR");
		                }
		                int stateNo = charBase.stateNoFromUniId(stateId);
		                int aStateNo = charBase.stateNoFromUniId(dir.args.get(j).dataVect.get(0).getStateId());
		                int bStateNo = charBase.stateNoFromUniId(dir.args.get(j).dataVect.get(1).getStateId());
		                if (stateNo == aStateNo) {
		                    if (aStateNo == bStateNo) {
		                        dir.args.remove(j--);
		                    }
		                    else {
		                        if (aStateNo < bStateNo)
		                            ++aStateNo;
		                        else
		                            --aStateNo;
		                        dir.args.get(j).dataVect.get(0).setStateId(charBase.uniIdFromStateNo(aStateNo));
		                    }
		                    changed = true;
		                }
		                else if (stateNo == bStateNo) {
		                    if (bStateNo < aStateNo)
		                        ++bStateNo;
		                    else
		                        --bStateNo;
		                    dir.args.get(j).dataVect.get(1).setStateId(charBase.uniIdFromStateNo(bStateNo));
		                    changed = true;
		                }
		            }
		        }
		    }
		    // If there are no key states left, delete the entire directive (highly unlikely!)
		    if (dir.args.isEmpty())
		        dir.setDirType(0);
		    }
		    else if (argType == DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES) {
		        for (j = 0; j < dir.args.size(); ++j) {
		            if (dir.args.get(j).id == charId) {
		                if (dir.args.get(j).attrib.deleteState(charBase, stateId))
		                    changed = true;
		            }
		        }
		    }
		    else
		        continue;
		    }
		if (changed) {		      
		    writeAllDirectives(directiveList);
		}
	}

	public void writeAllDirectives(List<Dir> directiveList) {
		makeTemp();
		List<DirSummary> newLocVector = new ArrayList<DirSummary>(directiveList.size());

		int startPos = 0;
		int i;

		setDirArray();
		for (i = 0; i < directiveList.size(); ++i) {
		    int dirType = directiveList.get(i).dirType & DIRARG_DIRTYPE_MASK;
		    if (dirType > 0 && dirType < _nDirectives) { // Add only if valid		       
		        newLocVector.add(new DirSummary(directiveList.get(i).dirType, startPos));
		        startPos += SIZE_OF_INT_IN_BYTES + getWriteLength(directiveList.get(i));
		    }
		}
		_dirOffset = newLocVector.size() * DirSummary.SIZE;

		dataSeek(_dirOffset + startPos);  // Get a big enough slot for everything

		//// These steps ensure that "fixedData" is written as well,
		//// allowing us to clear the 'dirty' flag.
		_fixedData.nDirs = newLocVector.size();
		_slotFile.seek(_slotHdrPtr + SlotFile.SlotHeader.SIZE);
		_fixedData.write(_slotFile);

        dataSeek(0);
		for (i = 0; i < newLocVector.size(); ++i) {
		    dataWrite(newLocVector.get(i));
		}
		for (i = 0; i < directiveList.size(); ++i) {
		    int dirType = directiveList.get(i).dirType & DIRARG_DIRTYPE_MASK;
		    if (dirType > 0 && dirType < _nDirectives) { // Add only if valid
		        int blockLeng = getWriteLength(directiveList.get(i));
		        dataWrite(blockLeng);
		        write(directiveList.get(i));
		    }
		}
		_dirLocVector = newLocVector;
		_freeBlockMap.clear();

		setDirty(false);
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
		int dirType = directive.dirType & DIRARG_DIRTYPE_MASK;

		if (dirType >= _nDirectives) {
		    return;
		}
		
		// check that DirectiveArray has been sorted, so that the element at
		// index i has directiveNumber == i
		if (_directiveArray.get(dirType).getNumber() != dirType) {
		      throw new IllegalStateException("Array of directives not sorted!");
		}

		int argType = _directiveArray.get(dirType).getArgType();

		dataWrite(directive.dirType);

		int nObjs;
		int i, j;

		switch (argType) {
		    case DirectiveArgType.DIRARG_NONE:
		    case DirectiveArgType.DIRARG_TRANSLATION:
		    case DirectiveArgType.DIRARG_INTERNAL:
		    case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		        break;  // Nothing more to write

		    case DirectiveArgType.DIRARG_OTHER:
		    case DirectiveArgType.DIRARG_TEXT:
		    case DirectiveArgType.DIRARG_FILE:
		    case DirectiveArgType.DIRARG_COMMENT:
		        if (directive.args.size() > 0) {
		            nObjs = directive.args.get(0).text.length();
		            dataWrite(nObjs);
		            dataWrite(BinFileEncoding.encode(directive.args.get(0).text));
		        }
		        else {
		            nObjs = 0;
		            dataWrite(nObjs);
		        }
		        break;

		    case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		    case DirectiveArgType.DIRARG_INTEGER:
		    case DirectiveArgType.DIRARG_REAL:
		        if (directive.args.size() > 0) {
		            dataWrite(directive.args.get(0).value.toBinary());
		        }
		        else {
		            DeltaNumber zero = new DeltaNumber();
		            dataWrite(zero.toBinary());
		        }
		        break;

		    case DirectiveArgType.DIRARG_CHAR:
		    case DirectiveArgType.DIRARG_ITEM:
		        if (directive.args.size() > 0) {
		            dataWrite(directive.args.get(0).id);
		        }
		        else {
		            int nullId = VOUID_NULL;
		            dataWrite(nullId);
		        }
		        break;

		    case DirectiveArgType.DIRARG_CHARLIST:
		    case DirectiveArgType.DIRARG_ITEMLIST:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < nObjs; ++i) {
		            dataWrite(directive.args.get(i).id);
		        }
		        break;

		    case DirectiveArgType.DIRARG_TEXTLIST:
		    case DirectiveArgType.DIRARG_CHARTEXTLIST:
		    case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		    case DirectiveArgType.DIRARG_ITEMFILELIST:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            dataWrite(directive.args.get(i).id);
		            nObjs = directive.args.get(i).text.length();
		            dataWrite(nObjs);
		            dataWrite(BinFileEncoding.encode(directive.args.get(i).text));
		            if (directive.args.get(0).id == VOUID_NAME) {
		                nObjs = directive.args.get(i).comment.length();
		                dataWrite(nObjs);
		                dataWrite(BinFileEncoding.encode(directive.args.get(i).comment));
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		    case DirectiveArgType.DIRARG_CHARREALLIST:
		    case DirectiveArgType.DIRARG_ITEMREALLIST:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < nObjs; ++i) {
		            dataWrite(directive.args.get(i).id);
		            dataWrite(directive.args.get(i).value.toBinary());
		        }
		        break;

		    case DirectiveArgType.DIRARG_CHARGROUPS:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            nObjs = directive.args.get(i).dataVect.size();
		            dataWrite(nObjs);
		            for (j = 0; j < nObjs; ++j) {
		                dataWrite(directive.args.get(i).dataVect.get(j).getUniId());
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_ITEMCHARLIST:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            dataWrite(directive.args.get(i).id);
		            nObjs = directive.args.get(i).dataVect.size();
		            dataWrite(nObjs);
		            for (j = 0; j < nObjs; ++j) {
		                dataWrite(directive.args.get(i).dataVect.get(j).getUniId());
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_ALLOWED:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i)  {
		            dataWrite(directive.args.get(i).id);
		            for (j = 0; j < 3; ++j) {
		                dataWrite(directive.args.get(i).dataVect.get(j));
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_KEYSTATE:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		    
		        for (i = 0; i < directive.args.size(); ++i) {
		            dataWrite(directive.args.get(i).id);
		            dataWrite(directive.args.get(i).value.toBinary());
		            nObjs = directive.args.get(i).dataVect.size();
		            dataWrite(nObjs);
		            for (j = 0; j < nObjs; ++j)
		                dataWrite(directive.args.get(i).dataVect.get(j));
		        }
		        break;

		    case DirectiveArgType.DIRARG_PRESET:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            dataWrite(directive.args.get(i).id);
		            for (j = 0; j < 2; ++j) {
		                dataWrite(directive.args.get(i).dataVect.get(j).getIntNumb());
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_INTKEY_ITEM:
		        if (directive.args.isEmpty()) // Having 1 "empty" argument should be equivalent
		            directive.resizeArgs(1); // to having no arguments, and allows use of index [0]
		        if (directive.args.get(0).id != VOUID_NULL) {
		            nObjs = IS_ITEM_ID;
		            dataWrite(nObjs);
		            dataWrite(directive.args.get(0).id);
		        }
		        else {
		            nObjs = directive.args.get(0).text.length();
		            dataWrite(nObjs);
		            dataWrite(BinFileEncoding.encode(directive.args.get(0).text));
		        }
		        break;

		    case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		    case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		    case DirectiveArgType.DIRARG_KEYWORD_CHARLIST: // Almost the same as DIRARG_INTKEY_CHARLIST, but the first
		    case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST: // argument, if present, should always be a text string. This is NOT enforced here...
		    case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            if (directive.args.get(i).id != VOUID_NULL) {
		                nObjs = (argType == DirectiveArgType.DIRARG_INTKEY_CHARLIST ||
		                         argType == DirectiveArgType.DIRARG_KEYWORD_CHARLIST ||
		                         argType == DirectiveArgType.DIRARG_INTKEY_CHARREALLIST) ?
		                         IS_CHAR_ID : IS_ITEM_ID;
		                dataWrite(nObjs);
		                dataWrite(directive.args.get(i).id);
		            }
		            else {
		                nObjs = directive.args.get(i).text.length();
		                dataWrite(nObjs);
		                dataWrite(BinFileEncoding.encode(directive.args.get(i).text));
		              }
		            if (argType == DirectiveArgType.DIRARG_INTKEY_CHARREALLIST)
		                dataWrite(directive.args.get(i).value.toBinary());
		        }
		        break;

		    case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            if (directive.args.get(i).id != VOUID_NULL) {
		                nObjs = directive.args.get(i).value.lessThan(0.0f) ? IS_ITEM_ID : IS_CHAR_ID;
		                dataWrite(nObjs);
		                dataWrite(directive.args.get(i).id);
		            }
		            else {
		                int temp;
		                nObjs = directive.args.get(i).text.length();
		                if (directive.args.get(i).value.lessThan(0.0f))
		                    temp = nObjs | ITEM_KEYWORD;
		                else
		                    temp = nObjs | CHAR_KEYWORD;
		                dataWrite(temp);
		                dataWrite(BinFileEncoding.encode(directive.args.get(i).text));
		            }
		        }
		        break;

		    case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		        nObjs = directive.args.size();
		        dataWrite(nObjs);
		        for (i = 0; i < directive.args.size(); ++i) {
		            if (directive.args.get(i).id != VOUID_NULL) {
		                nObjs = IS_CHAR_ID;
		                dataWrite(nObjs);
		                dataWrite(directive.args.get(i).id);
		            }
		            else {
		                nObjs = directive.args.get(i).text.length();
		                dataWrite(nObjs);
		                dataWrite(BinFileEncoding.encode(directive.args.get(i).text));
		            }
		            nObjs = directive.args.get(i).attrib.getDataLength();
		            dataWrite(nObjs);
		            if (nObjs > 0)
		                dataWrite(directive.args.get(i).attrib.getData());
		        }
		        break;

		    default:
		        break;
		}
	}

	// Fixed data and offsets...
	public static final int fixedSizeOffs = SlotFile.SlotHeader.SIZE + FixedData.SIZE;
	public static final int fileNameOffs = fixedSizeOffs + 2;
	public static final int fileModifyTimeOffs = fileNameOffs + MAX_PATH;
	public static final int nDirsOffs = fileModifyTimeOffs + 8;
	public static final int nFileFlagsOffs = nDirsOffs + 4;

	public static class DirFileFixedData extends FixedData {

		private static final int DIR_FILE_DATA_SIZE = 2 + MAX_PATH + 8 + 4 + 4;
		public static final int SIZE = FixedData.SIZE + DIR_FILE_DATA_SIZE;
			
		public DirFileFixedData() {
			super("DirFile Desc");
			this.TypeID = VODescFactory.VODirFileDesc_TypeId;
			this.fixedSize = SIZE;
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
			ByteBuffer b = file.readByteBuffer(DIR_FILE_DATA_SIZE);

			fixedSize = b.getShort();
			byte[] sbytes = new byte[MAX_PATH];
			b.get(sbytes);
			// The c++ system relied on null termination in the file name...
			int i = 0;
			while (i<MAX_PATH && sbytes[i] != 0) {
				i++;
			}
			fileName = BinFileEncoding.decode(Arrays.copyOfRange(sbytes, 0, i));
			fileModifyTime = b.getLong();
			nDirs = b.getInt();
			fileFlags = b.getInt();
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

	public static class Dir {
		public int dirType;
		public List<DirArgs> args = new ArrayList<DirArgs>();

		public void resizeArgs(int size) {
			args = new ArrayList<DirArgs>(size);
			for (int i = 0; i < size; ++i) {
				args.add(new DirArgs());
			}
		}
		
		public int getDirType() {
			return dirType;
		}
		
		public void setDirType(int aDirType) {
			dirType = aDirType;
		}

		@Override
		public String toString() {
			return String.format("dirType=%d, args=%s", dirType, args);
		}
	}

	public static class DirArgs implements Comparable<DirArgs>{

		public DirArgs() {
			this(0);
		}

		public DirArgs(int id) {
			this.id = id;
			dataVect = new ArrayList<DirListData>();
		}

		
		@Override
		public int compareTo(DirArgs o) {
			return new Integer(id).compareTo(o.getId());
		}


		public String text;
		public String comment;
		DeltaNumber value = new DeltaNumber();
		int id;
		List<DirListData> dataVect;
		public Attribute attrib = new Attribute();

		public void resizeDataVect(int size) {
			dataVect = new ArrayList<DirListData>(size);
			for (int i = 0; i < size; ++i) {
				dataVect.add(new DirListData());
			}
		}
		
		public void setText(String aText) {
			text = aText;
		}
		
		public void setValue(int aValue) {
			value = new DeltaNumber((float)aValue);
		}
		
		public void setValue(String aValue) {
			value = new DeltaNumber(aValue);
		}
		
		public void setId(int anId) {
			id = anId;
		}
		
		public int getId() {
			return id;
		}
		
		public List<DirListData> getData() {
			return dataVect;
		}
		
		public List<Integer> getDataAsInts() {
			List<Integer> data = new ArrayList<Integer>();
			for (DirListData item : dataVect) {
				data.add(item.getAsInt());
			}
			
			return data;
		}

		public DeltaNumber getValue() {
			return value;
		}
		@Override
		public String toString() {
			return String.format("ArgId=%d, text=%s comment=%s value=%s, dataVect=%s", id, text, comment, value, dataVect == null ? "null" : dataVect);
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
	public static class DirListData implements IOObject {

		private byte[] _bytes = new byte[4];
		private byte _decimal;

		public static final int SIZE = 5;
		
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
		
		public void setAsDeltaNumber(DeltaNumber number) {
			_decimal = number.getDecimal();
			byte[] value = number.toBinary();
			System.arraycopy(value, 0, _bytes, 0, _bytes.length);
		}

		@Override
		public int size() {
			return SIZE;
		}
		
		public String asString() {
			return new DeltaNumber(getAsFloat(), _decimal).asString();
		}
	}

	public class DirSummary implements IOObject {

		public static final int SIZE = 4 + 4;

		private int _loc;
		private int _type;

		public DirSummary() {
			this(0, 0);
		}

		public DirSummary(int type, int loc) {
			_loc = loc;
			_type = type;
		}

		public int getLoc() {
			return _loc;
		}

		public int getType() {
			return _type;
		}
		
		public void setType(int type) {
			_type = type;
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
		public int size() {
			return SIZE;
		}

		@Override
		public String toString() {
			return String.format("DirSummary: Loc=%d, Type=%d", _loc, _type);
		}

	}

}
