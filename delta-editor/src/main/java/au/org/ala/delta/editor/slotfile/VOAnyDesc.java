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
import java.util.Date;
import java.util.List;

import au.org.ala.delta.editor.slotfile.SlotFile.SlotHeader;
import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;

public abstract class VOAnyDesc {

	public static int VOTID_NULL = 0;
	public static int VOTID_USER_BASE = 1;
	public static int VOTID_DELTA_BASE = 100; // Just to avoid an conflict with
												// Georg's stuff
	public static int VOTID_SYS_BASE = 10000; // Stay below signed int16
	public static int VOTID_USER_MAX = VOTID_SYS_BASE - 1;
	
	public static final int LONG_MIN = Integer.MIN_VALUE;
	public static final int VOUID_NULL = 0;
	public static final int VOUID_NAME = LONG_MIN;
	public static final int VOUID_DELETED = LONG_MIN+1;


	public static final int SIZE_ACRONYM = 13;
	public static final int SIZE_NOTE = 256;

	public static final byte VOF_DIRTY = 0x1;  // Quick access (acronym) data are dirty.
	public static final byte VOF_REFCHANGED = 0x2; // Referenc count has changed.
	public static final byte VOF_LOCKEDRD = 0x4; // Desc is locked for reading.
	public static final byte VOF_LOCKEDWR = 0x8; // Desc is locked for writing.
	public static final byte VOF_LOCKEDRDWR = VOF_LOCKEDWR|VOF_LOCKEDWR; // Desc is lokecked for reading and writing.
	public static final byte VOF_EMBEDDED = (byte) 0xf0;
	
	public static final int SID_DESC = 100; //or anything
	public static final int SID_UIDS = 101;

	private byte _flags;
	private List<Integer> _dependents = new ArrayList<Integer>();
	private List<Integer> _embedded = new ArrayList<Integer>();
	private short _refCount;
	private short _tempRefCount;
	protected int _uniId;
	
	private String _acronym;
	protected SlotFile _slotFile;
	protected int _slotHdrPtr;
	protected int _dataPtr;
	protected int _dataOffs;
	private VOP _vop;
	
	enum VOErrorType
	{
	  VOE_RD_NO_STORE,
	  VOE_WR_NO_STORE,
	  VOE_UNKNOWN_OBJECT,
	  VOE_RD_PAST_DATA,
	  VOE_WR_PAST_DATA,
	} ;
	
	public static class VOException extends RuntimeException  {
		
		private static final long serialVersionUID = 1L;
		
		private VOErrorType _error;
		
		public VOException(VOErrorType error) {
			super(error.name());
			_error = error;
		}
		
		public VOErrorType getError() {
			return _error;
		}
	}
	
	/** Size of int in bytes */
	public static final int SIZE_OF_INT_IN_BYTES = 4;
	
	public static VOAnyDesc CreateAnyDesc(VOP vop, boolean useTemp) {
		SlotFile slotFile = useTemp ? vop.getTempSlotFile() : vop.getPermSlotFile();
		int tid = slotFile.readInt();
		if (tid < VOTID_USER_BASE || tid > VOTID_USER_MAX) {
			throw new RuntimeException("TypeID is unrecognized (out of bounds)");
		}
		
		VOAnyDesc desc = VODescFactory.getDescFromTypeId(tid, slotFile, vop);
		if (desc.getDataOffset() == 0) {
			desc.setDataOffset( SlotFile.SlotHeader.SIZE + desc.getFixedDataSize());
		}
		return desc;
	}

	public VOAnyDesc(SlotFile slotFile, VOP vop) {
		_slotFile = slotFile;
		_slotHdrPtr = slotFile.tell() - TypeIdOffs - 4;
		_acronym = readAcronym();
		_uniId = readUniId();
		_refCount = readRefCount();
		_flags |= readStoreFlags();
		_vop = vop;
	}
		
	public int getDataOffset() {
		return _dataOffs;
	}
	
	public void setDataOffset(int offset) {
		_dataOffs = offset;
	}

	public abstract int getTypeId();

	public abstract String getStringId();

	public boolean isA(int typeid) {
		return getTypeId() == typeid;
	}

	public boolean dependOn(int uid) {
		return _dependents.contains(uid);
	}

	public int getNumDependent() {
		return _dependents.size();
	}

	public List<Integer> getDependents() {
		return _dependents;
	}

	public boolean isEmbedded() {
		return (_flags & VOF_EMBEDDED) > 0;
	}

	public int getNumEmbedded() {
		return _embedded.size();
	}

	public List<Integer> getEmbeddeds() {
		return _embedded;
	}

	protected void writeChar(int offset, byte d) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		_slotFile.writeByte(d);
	}

	protected void writeShort(int offset, short s) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		_slotFile.writeShort(s);		
	}

	protected void writeLong(int offset, int i) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		_slotFile.writeInt(i);		

	}

	protected byte readChar(int offset) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		return _slotFile.readByte();
	}

	protected short readShort(int offset) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		return _slotFile.readShort();
	}
	
	protected short[] readShortArray(int count) {
		short[] dest = new short[count];
		ByteBuffer b = readBuffer(count * 2);		
		for (int i =0; i < count; ++i) {
			dest[i] = b.getShort();
		}
		return dest;
	}

	protected int readLong(int offset) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + offset);
		return _slotFile.readInt();
	}
	
	protected int readInt() {
		assert _slotFile != null;
		return _slotFile.readInt();
	}
	
	protected short readShort() {
		assert _slotFile != null;
		return _slotFile.readShort();
	}
	
	protected ByteBuffer readBuffer(int size) {
		assert _slotFile != null;
		return _slotFile.readByteBuffer(size);
	}
	
	protected byte[] readBytes(int size) {
		return _slotFile.read(size);
	}
	
	protected DeltaNumber readNumber() {
		byte[] data = _slotFile.read(DeltaNumber.size());
		DeltaNumber n = new DeltaNumber();
		n.fromBinary(data);
		return n;
	}
	
	protected DeltaNumber dataReadNumber() {
		ByteBuffer data = dataReadBuffer(DeltaNumber.size());
		DeltaNumber n = new DeltaNumber();
		n.fromBinary(data.array());
		return n;
	}

	protected void writePersData(byte[] data) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + TypeIdOffs);
		_slotFile.writeBytes(data);		
	}

	protected byte[] readPersData(int size) {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + TypeIdOffs);
		return _slotFile.read(size);
	}
	
	protected String readString(int size) {
		return _slotFile.sread(size);
	}
	
	protected String dataReadString(int size) {
		ByteBuffer bb = dataReadBuffer(size);
		return BinFileEncoding.decode(bb.array());
	}
	
	protected ByteBuffer dataReadBuffer(int len) {
		if (len > 0) {
			int endPos = getDataSize();
			if (_dataPtr + len > endPos) {
				len = endPos - _dataPtr;
			}
			dataSeek(_dataPtr);
			ByteBuffer b = _slotFile.readByteBuffer(len);
			// Weird, this comes straight from the C++, but anyhoo...
			// _dataPtr += len;
			_dataPtr = _slotFile.tell() - (_slotHdrPtr + _dataOffs);
			return b;
		}
		return ByteBuffer.allocate(0);		
	}

	protected short[] dataReadShorts(int numShorts) {
		ByteBuffer b = dataReadBuffer(numShorts*2);
		short[] shorts = new short[numShorts];
		for (int i=0; i<shorts.length; i++) {
			shorts[i] = b.getShort();
		}
		return shorts;
	}
	protected int dataRead(byte[] dest, int len) {
		if (len > 0) {
			int endPos = getDataSize();
			if (_dataPtr + len > endPos) {
				len = endPos - _dataPtr;
			}
			dataSeek(_dataPtr);
			if (_slotFile.readBytes(dest) != len) {
				throw new RuntimeException("Bad read!");
			} else {
				// Weird, this comes straight from the C++, but anyhoo...
				_dataPtr += len;
			}
			_dataPtr = _slotFile.tell() - (_slotHdrPtr + _dataOffs);			
		}
		return len;
	}
	
	protected int dataReadInt() {
		final int SIZE_OF_INT = 4;
		ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_INT);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int numBytes = dataRead(buffer.array(), SIZE_OF_INT);
		if (numBytes != SIZE_OF_INT) {
			throw new RuntimeException("Failed to read an int!");
		}
		return buffer.getInt();
	}
	
	protected short dataReadShort() {
		final int SIZE_OF_SHORT = 2;
		ByteBuffer buffer = ByteBuffer.allocate(SIZE_OF_SHORT);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int numBytes = dataRead(buffer.array(), SIZE_OF_SHORT);
		if (numBytes != SIZE_OF_SHORT) {
			throw new RuntimeException("Failed to read a short!");
		}
		return buffer.getShort();
	}
	
	protected List<Integer> readIntArrayToList(int count) {
		int size = count * 4;
		
		ByteBuffer b = dataReadBuffer(size);
		
		List<Integer> dest = new ArrayList<Integer>();
		
		for (int i = 0; i < count; i++) {
			dest.add(b.getInt());
		}
		
		return dest;
	}
	
	protected int dataSeek(int pos) {
		return dataSeek(pos, SeekDirection.FROM_BEG);
	}
	
	
	/** Allows the automatic makeTemp() operation to be disabled during saves */
	private boolean _tempDisabled = false;
	public void disableTemp() {
		_tempDisabled = true;
	}
	public void enableTemp() {
		_tempDisabled = false;
	}
		
	public boolean makeTemp() {
		if (_tempDisabled) {
			return false;
		}
		
		if (_slotFile != _vop.getTempSlotFile()) {
			if (_dataOffs != SlotHeader.SIZE + getFixedDataSize()) {
				if (_vop instanceof DeltaVOP) {
					((DeltaVOP) _vop).getDeltaMaster().setDirty();
				}
				return _vop.move2Temp(this);
			}
		}
		return false;
	}
	
	public int getFixedDataSize() {
		return FixedData.SIZE;
	}

	protected byte[] stringToBytes(String string) {
		
		return BinFileEncoding.encode(string);
	}
	
	protected void dataWrite(int i) {
		// TODO should really reuse those utilities in binfile to take care of byte ordering.
		ByteBuffer buff = ByteBuffer.allocate(SIZE_OF_INT_IN_BYTES);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putInt(i);
		dataWrite(buff.array());
	}
	
	protected void dataWrite(short i) {
		ByteBuffer buff = ByteBuffer.allocate(2);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putShort(i);
		dataWrite(buff.array());
	}
	
	protected void dataWrite(short[] shorts) {
		ByteBuffer buff = ByteBuffer.allocate(2*shorts.length);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		for (int i=0; i<shorts.length; i++) {
			buff.putShort(shorts[i]);
		}
		dataWrite(buff.array());
	}
	
	protected void dataWrite(byte[] buf) {
		makeTemp();
		int endPos = getDataSize();
		if (buf.length + _dataPtr > endPos) {
			_slotHdrPtr = _slotFile.growSlotData(_slotHdrPtr, buf.length + _dataPtr - endPos, false);
		}
		
		dataSeek(_dataPtr);
		_slotFile.swrite(buf);
		_dataPtr += buf.length;
		_dataPtr = _slotFile.tell() - (_slotHdrPtr + _dataOffs);
	}
	
	protected void dataWrite(IOObject obj) {
		makeTemp();
		int endPos = getDataSize();
		if (obj.size() + _dataPtr > endPos) {
			_slotHdrPtr = _slotFile.growSlotData(_slotHdrPtr, obj.size() + _dataPtr - endPos, false);
		}
		dataSeek(_dataPtr);
		obj.write(_slotFile);
		_dataPtr += obj.size();
		_dataPtr = _slotFile.tell() - (_slotHdrPtr + _dataOffs);
	}

	protected void dataTruncate() {
		_slotHdrPtr = _slotFile.growSlotData(_slotHdrPtr, _dataPtr - getDataSize(), false);
		dataSeek(0, SeekDirection.FROM_END);
	}

	protected int dataTell() {
		return _dataPtr;
	}
	
	protected int dataSeek(int pos, SeekDirection fromWhere) {
		assert _slotFile != null;
		int aPos = 0;
		int endPos = getDataSize();
		switch (fromWhere) {
			case FROM_END:
				aPos = endPos + pos;
				break;
			case FROM_CUR:
				aPos = _dataPtr + pos;
				break;
			case FROM_BEG:
			default:
				aPos = pos;
				break;
		}
		
		if (aPos < 0) {
			aPos = 0;
		}
		
		if (aPos > endPos) {
			makeTemp();
			_slotHdrPtr = _slotFile.growSlotData(_slotHdrPtr, aPos - endPos, false);
		}
		_dataPtr = _slotFile.seek(_slotHdrPtr + _dataOffs + aPos) - (_slotHdrPtr + _dataOffs);
		return _dataPtr;		
	}

	protected byte[] dupTrailingData(int pos) {
		return dupTrailingData(pos, SeekDirection.FROM_BEG);
	}
	
	protected byte[] dupTrailingData(int pos, SeekDirection fromWhere) {
		dataSeek(pos, fromWhere);
		int buflen = getDataSize() - dataTell();
		if (buflen > 0) {
			byte[] buffer = new byte[buflen];
			dataRead(buffer, buflen);
			return buffer;
		}
		
		return null;
	}

	protected int getDataSize() {
		return readSlotDataSize() + SlotHeader.SIZE - _dataOffs;
	}
	
	protected String readAcronym() {
		assert _slotFile != null;
		_slotFile.seek(_slotHdrPtr + AcronymOffs);
		return _slotFile.sread(SIZE_ACRONYM);
	}
	
	protected int readSlotId() {
		return readLong(SlotFile.SlotIdOffs);
	}

	protected int readSlotSize() {
		return readLong(SlotFile.SlotSizeOffs);
	}

	protected int readSlotDataSize() {
		return readLong(SlotFile.SlotDataSizeOffs);
	}
	
	protected int readSlotGrowSize() {
		return readLong(SlotFile.SlotGrowSizeOffs);
	}

	protected int readTypeId() {
		return readLong(TypeIdOffs);
	}

	protected int readCreateTime() {
		return readLong(CreateTimeOffs);
	}

	protected int readModifyTime() {
		return readLong(ModifyTimeOffs);
	}

	protected byte readStoreFlags() {
		return (byte) readShort(StoreFlagsOffs);
	}

	protected short readRefCount() {
		return readShort(RefCountOffs);
	}

	protected int readOwnerId() {
		return readLong(OwnerIdOffs);
	}
	
	protected int readUniId() {
		return readLong(UniIdOffs);
	}
		
	public short getRefCount() {
		return _refCount;
	}
	
	public int getTempRefCount() {
		return _tempRefCount;
	}
	
	public int getActiveRefCount() {
		return _refCount + _tempRefCount;
	}
	
	public int incRefCount() {
		_flags |= VOF_REFCHANGED;
		return _refCount ++;	
	}
	
	public int decRefCount() {
		_flags |= VOF_REFCHANGED;
		return _refCount--;
	}
	
	public int incTempRefCount() {
		return _tempRefCount++;
	}
	
	public int decTempRefCount() {
		return _tempRefCount--;
	}
	
	public boolean hasRefChanged() {
		return (_flags & VOF_REFCHANGED) != 0;
	}
	
	// Flags...
	public void setDirty() {
		setDirty(true);
	}
	
	public void setDirty(boolean dirty) {
		if (dirty) {
			_flags |= VOF_DIRTY;
		} else {
			_flags &= ~VOF_DIRTY;
		}
	}
	
	public boolean isDirty() {
		return (_flags & VOF_DIRTY) != 0;
	}
	
	public void lockWr(boolean lock) {
		if (lock) {
			_flags |= VOF_LOCKEDWR;
		} else {
			_flags &= ~VOF_LOCKEDWR;
		}		
	}
	
	public boolean canWrite() {
		return (_flags & VOF_LOCKEDWR) == 0;
	}
	
	public void lockRd(boolean lock) {
		if (lock) {
			_flags |= VOF_LOCKEDRD;
		} else {
			_flags &= ~VOF_LOCKEDRD;
		}		
	}
	
	public boolean canRead() {
		return (_flags & VOF_LOCKEDRD) == 0;
	}


	public void lockRdWr(boolean lock) {
		lockRd(lock);
		lockWr(lock);	
	}	
	
	public boolean canReadWrite() {
		return canRead() && canWrite();
	}
	
	public byte getFlags() {
		return _flags;
	}
	
	// buffered data...
	
	public String getAcronym() {
		return _acronym;
	}
	
	public void setAcronym(String acronym) {
		setDirty();
		if (acronym.length() > SIZE_ACRONYM) {
			acronym = acronym.substring(0,SIZE_ACRONYM);
		}
		_acronym = acronym;
	}
	
	public int getUniId() {
		return _uniId;
	}
	
	/**
	 * Commits buffered data.  Normally invoked by VOP.commit();
	 */
	public void storeQData() {
		writeAcronyn();                // provided the REFCHANGED flag is set
		writeRefCount();               // RefCount is written anyway if DeltaRefCount !=0
	}
	
	public void writeAcronyn() {
		if (_slotFile == null) {
			throw new VOException(VOErrorType.VOE_WR_NO_STORE);
		}
	   _slotFile.seek(_slotHdrPtr + AcronymOffs);
	   _slotFile.swrite(_acronym, SIZE_ACRONYM);
	}
	
	public void touch() {
		Date d = new Date();
		writeModifyTime((int) d.getTime() / 1000);
	}
	
	public abstract int getNumberOfItems();
	
	public SlotFile getSlotFile() {
		return _slotFile;
	}
	
	public int getSlotHdrPtr() {
		return _slotHdrPtr;
	}
	
	public int getDataPtr() {
		return _dataPtr;
	}
	
	public void writeModifyTime(int time) {
		writeLong(ModifyTimeOffs, time);
	}
	
	public void writeRefCount(short v) {
		writeShort(RefCountOffs, v);
	}
	
	public void writeRefCount() {
		writeShort(RefCountOffs, _refCount);
	}
	
	public void writeStoreFlags(byte flags) {
		writeChar(StoreFlagsOffs, flags);
	}
	
	public void writeOwnerId(int id) {
		writeLong(OwnerIdOffs, id);
	}
	
	public void writeAcronym(String acronym) {
		if (_slotFile == null) {
			throw new IllegalStateException("Slot File is null!");
		}
		
		_slotFile.seek(_slotHdrPtr + AcronymOffs);
		_slotFile.swrite(acronym, 13);		
	}
	
	public void writeAcronym() {
		writeAcronym(_acronym);
	}
	
	public void setVOP(VOP vop) {
		_vop = vop;
	}
	
	public VOP getVOP() {
		return _vop;
	}
	

	// Offsets...
	public static final int TypeIdOffs = SlotFile.SlotHeader.SIZE + 0;
	public static final int CreateTimeOffs = SlotFile.SlotHeader.SIZE + 4;
	public static final int ModifyTimeOffs = SlotFile.SlotHeader.SIZE + 8;
	public static final int RefCountOffs = SlotFile.SlotHeader.SIZE + 12;
	public static final int StoreFlagsOffs = SlotFile.SlotHeader.SIZE + 14;
	public static final int UniIdOffs = SlotFile.SlotHeader.SIZE + 15;
	public static final int OwnerIdOffs = SlotFile.SlotHeader.SIZE + 19;
	public static final int AcronymOffs = SlotFile.SlotHeader.SIZE + 23;

	public static class FixedData implements IOObject {
				
		public static final int SIZE = 4 + 4 + 4 + 2 + 1 + 4 + 4 + SIZE_ACRONYM + 4 + 4;
		
		public FixedData(String acronym) {
			assert acronym.length() <= SIZE_ACRONYM;
			byte[] src = BinFileEncoding.encode(acronym);
			for (int i = 0; i < SIZE_ACRONYM; ++i) {
				if (i < src.length) {
					Acronym[i] = src[i];
				} else {
					Acronym[i] = 0;
				}
			}

		}

		public int TypeID;
		public int CreateTime;
		public int ModifyTime;
		public short RefCount;
		public byte StoreFlags;
		public int UniId;
		public int OwnerId;
		byte[] Acronym = new byte[VOAnyDesc.SIZE_ACRONYM];
		int unused1;
		int unused2;
		
		
		@Override		
		public void read(BinFile file) {
			ByteBuffer b = file.readByteBuffer(SIZE);
			
			TypeID = b.getInt();
			CreateTime = b.getInt();
			ModifyTime = b.getInt();
			RefCount = b.getShort();
			StoreFlags = b.get();
			UniId = b.getInt();
			OwnerId = b.getInt();			
			b.get(Acronym);
			unused1 = b.getInt();
			unused2 = b.getInt();			
			
//			TypeID = file.readInt();
//			CreateTime = file.readInt();
//			ModifyTime = file.readInt();
//			RefCount = file.readShort();
//			StoreFlags = file.readByte();
//			UniId = file.readInt();
//			OwnerId = file.readInt();
//			Acronym = file.readBytes(SIZE_ACRONYM);
//			unused1 = file.readInt();
//			unused2 = file.readInt();			
		}


		@Override
		public void write(BinFile file) {
			file.writeInt(TypeID);
			file.writeInt(CreateTime);
			file.writeInt(ModifyTime);
			file.writeShort(RefCount);
			file.write(StoreFlags);
			file.writeInt(UniId);
			file.writeInt(OwnerId);
			file.writeBytes(Acronym);
			file.writeInt(unused1);
			file.writeInt(unused2);
			
		}
		
		@Override
		public int size() {
			return SIZE;
		}
	}

	/**
	 * @param allocSlot
	 */
	public void setSlotHdrPtr(int slotHeaderPointer) {
		_slotHdrPtr = slotHeaderPointer;
	}

	/**
	 * @param dstFile
	 */
	public void setSlotFile(SlotFile dstFile) {
		_slotFile = dstFile;
	}
}
