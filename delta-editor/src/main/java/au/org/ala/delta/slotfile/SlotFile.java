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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Encapsulates the 
 */
public class SlotFile extends BinFile {

	public static final long SID_NULL = 0x00000000L;
	public static final long SID_USER_BASE = 0x00000001L;
	public static final long SID_SYS_BASE = 0xF0000000L;
	public static final long SID_USER_MAX = SID_SYS_BASE - 1;
	// public static final long SID_AT_END = SID_SYS_BASE + 1;
	public static final long SID_DELETED = 0xFFFFFFFFL;

	private FileSignature _fileSignature;
	private FileHeader _fileHeader;
	
	/** 
	 * Stores the file location and size of free slots in the slot file.
	 * Key:   the offset (in bytes) into the slot file that marks the beginning of the free slot.
	 * Value: size of slot (in bytes)
	 */
	private HashMap<Integer, Integer> _freeSlotMap = new HashMap<Integer, Integer>();
	
	private Entry<Integer, Integer> findFreeSlot(int size) {
		Entry<Integer, Integer> bestSlot = null;
		for (Entry<Integer, Integer> slot : _freeSlotMap.entrySet()) {
			int slotSize = slot.getValue();
			if (slotSize >= size) {
				if ((bestSlot == null) || (slotSize < bestSlot.getValue())) {
					bestSlot = slot;
					break;
				}
			}
		}
		return bestSlot;
	}
	
	/**
	 * Removes an entry from the _freeSlotMap identified by the supplied key.
	 * @param key the offset into the slot file that identifies the free slot.
	 */
	private void removeFreeSlot(int key) {
		_freeSlotMap.remove(key);
	}
	
		
	public SlotFile() {
		this(null, BinFileMode.FM_TEMPORARY);
	}
	
	public SlotFile(String filename) {
		this(filename, BinFileMode.FM_TEMPORARY);
	}

	public SlotFile(String filename, BinFileMode mode) {
		super(filename, mode);
		_fileSignature = new FileSignature();
		if (mode == BinFileMode.FM_NEW || mode == BinFileMode.FM_TEMPORARY) {			
			_fileSignature.write(this);
			_fileHeader = new FileHeader();
			_fileHeader.SysDataPtr = FileSignature.SIZE + FileHeader.SIZE;
			_fileHeader.write(this);
		} else {			
			_fileSignature.read(this);
			_fileHeader = new FileHeader();
			_fileHeader.read(this);
		}
	}
	
	public Map<Integer, Integer> getFreeSlotMap() {
		return _freeSlotMap;
	}
	
	/**
	 * Marks a slot identified by slotPosition and size as being free.
	 * @param slotPosition the position (in bytes) into the file that identifies the start of the slot.
	 * @param size the size of the slot.
	 */
	public void freeSlot(int slotPosition, int size) {
		_freeSlotMap.put(slotPosition, size);
	}

	public String getVersion() {
		if (_fileSignature != null) {
			return String.format("%s %d.%d [%d]", _fileSignature.Copyright, _fileSignature.MajorVersion, _fileSignature.MinorVersion, _fileSignature.Magic);
		}
		return "File not created/read!";
	}

	public FileHeader getFileHeader() {
		return _fileHeader;
	}

	public int getUserDataPtr() {
		return (FileSignature.SIZE + FileHeader.SIZE);
	}

	public int getSysDataPtr() {
		return _fileHeader.SysDataPtr;
	}
	
	/**
	 * GrowSlotData  - Adjust the size of the data area. (similar to realloc())
	 *
	 * Update the DataSize field of the slot header.
	 * If absolute is true, the new dataSize will be set to size
	 * otherwise to  current size + size (in this case size can be negative
	 * Moves the slot and data if necessary to accommodate the new size.
	 * @param slotHdrPtr location of the slot header in the file.
	 * @param size specifies the new size (in bytes) for the slot or the amount to increase the
	 * slot by, depending on the value supplied for absolute.
	 * @param absolute true if the supplied size should be the new absolute size of the slot,
	 * otherwise the slot is increased by size bytes.
	 * @return new slotHdrPtr
	*/
	public int growSlotData(int slotHdrPtr, int size, boolean absolute) {
		seek(slotHdrPtr);
		SlotHeader slotHeader = readSlotHeader();
		int newSlotHdrPtr = slotHdrPtr;
		
		if (!absolute) {
			size += slotHeader.DataSize;
		}
		
		if (size < 0) {
			throw new RuntimeException("Negative data size");
		}
		
		if (slotHeader.SlotSize >= size) {
			// There is still space in the slot.
			slotHeader.DataSize = size;
			seek(slotHdrPtr);
			writeSlotHeader(slotHeader);		
		}
		else {
			// Find a new slot and copy the data.
			newSlotHdrPtr = allocSlot(size + slotHeader.GrowSize, slotHeader.SlotId, size, slotHeader.GrowSize);
			copySlotData(slotHdrPtr, this);
			
			freeSlot(slotHdrPtr, slotHeader.SlotSize);
		}
		
		return newSlotHdrPtr;
	}
	
	/**
	 * General method to copy the data from one slot to another.
	 * The source file can be the same (this) or a different one.
	 * The SlotHeader must already be in place and is not further examined.
	 * No check is done for overwriting
	 * On Entry:
	 *			The current filePtr must point to beginning of destination slot DATA
	 *			scrPos points to beginning of source slot HEADER
	 *	    scrFile is the file to copy from
     */
    boolean copySlotData(int srcPos, SlotFile srcFile) {
    	int destinationPos = tell();
    	if (srcFile == null) {
    		srcFile = this;
    	}
    	
    	if ((srcFile == this) && (destinationPos == srcPos)) {
    		return false;
    	}
    	
    	srcFile.seek(srcPos);
    	SlotHeader srcSlotHeader = srcFile.readSlotHeader();
    	srcPos += SlotHeader.SIZE;
    	
    	int dataSize = srcSlotHeader.DataSize;
    	final int BLOCK_SIZE = 1024 * 8;
    	int numBlocks = dataSize / BLOCK_SIZE;
    	int rest = dataSize % BLOCK_SIZE;
    	
    	byte[] buffer = new byte[BLOCK_SIZE];
    	for (int i=0; i<numBlocks; i++) {
    		srcFile.readBytes(buffer);
    		srcPos += BLOCK_SIZE;
    		seek(destinationPos);
    		swrite(buffer);
    		destinationPos += BLOCK_SIZE;
    		srcFile.seek(srcPos);
    	}
    	// TODO we haven't implemented a read(byte[], length) so we'll just
    	// create a new buffer for now.
    	byte[] restOfData = srcFile.read(rest);
    	seek(destinationPos);
    	swrite(restOfData);
    	
    	return true;
	}

	

	/**
	 * Finds or creates an empty Slot and writes the slot header to it.
	 *
	 * On Exit:
     *	      new Slot Header is inserted to file
     *	      current file ptr is at beginning of slot DATA
	 * 
	 * @param minSize the minimum required size for the slot.
	 * @param slotTypeId specifies the type of the new slot.
	 * @param dataSize the size to allocate for the variable length data for this slot
	 * @param growSize the size to grow the slot by
	 * @return the file position of the new slot.
	 */
	public int allocSlot(int minSize, int slotTypeId, int dataSize, int growSize) {
		
	   SlotHeader slotHeader = new SlotHeader();
	   slotHeader.SlotId = slotTypeId;
	   slotHeader.DataSize = dataSize;
	   slotHeader.GrowSize = growSize;
	   
	   int headerPointer;
	   
	   // Search through deleted_slot container to find a slot large enough to hold the requested size.
	   Entry<Integer,Integer> freeSlot = findFreeSlot(minSize);
	   
	   if (freeSlot != null) {
	      slotHeader.SlotSize = freeSlot.getValue();
	      headerPointer = freeSlot.getKey();
	      seek(headerPointer);	      
	      removeFreeSlot(headerPointer);
	   }
	   else { // If no deleted slot fits, create a new one
	   
	     // Create a new slot at the end of the file 
		 headerPointer = seekToEnd();
	     slotHeader.SlotSize = minSize;
	     setLength(getLength() + minSize + SlotHeader.SIZE);
	     headerPointer = tell();
	     _fileHeader.SysDataPtr = headerPointer + slotHeader.SlotSize;  //Store the end of the last slot.
	   }

	   writeSlotHeader(slotHeader);

	   return headerPointer;
	}

	/**
	 * @return a new instance of SlotHeader initialised from the data at the current file position.  
	 * (It is therefore important to seek to the correct location in the file before calling this method).
	 */
	public SlotHeader readSlotHeader() {
		SlotHeader slotHeader = new SlotHeader();
		slotHeader.read(this);
		
		return slotHeader;
	}
	
	/**
	 * Writes the supplied slot header to the current position in the file.
	 * @param header the slot header to write.
	 */
	public void writeSlotHeader(SlotHeader header) {
		header.write(this);
	}
	
	
	// Mark the SlotId of all deleted entries found in the Free slot container
	// As there is currently no field in the info for newly deleted files,
	// the long-time deleted files get marked over and over again, but that does not realy matter !!!
	//
	public void brandDeleted() {
	   // TODO have to be careful here about longs & ints... I think we've been using ints to this point but this
		// constant is a long.
	   int delId = (int)SID_DELETED;
	   for (int slotPosition : _freeSlotMap.keySet()) {
	      seek(slotPosition);
	      writeInt(delId);
	   }
	}
	
	public void writeFileHeader() {
		seek(FileSignature.SIZE);
		_fileHeader.write(this);
	}
	
	// Offsets...
	public static final int SlotIdOffs = 0;
	public static final int SlotSizeOffs = SlotIdOffs + 4; // 4 bytes to a int
	public static final int SlotDataSizeOffs = SlotSizeOffs + 4;
	public static final int SlotGrowSizeOffs = SlotDataSizeOffs + 4;

	public static class SlotHeader implements IOObject {

		public static final int SIZE = 4 + 4 + 4 + 4;

		public SlotHeader() {
			SlotId = 0;
			SlotSize = 0;
			DataSize = 0;
			GrowSize = 0;
		}

		int SlotId;
		int SlotSize;
		int DataSize;
		int GrowSize;

		@Override
		public void read(BinFile file) {
			ByteBuffer bb = file.readByteBuffer(SIZE);
			
			SlotId = bb.getInt();
			SlotSize = bb.getInt();
			DataSize = bb.getInt();
			GrowSize = bb.getInt();
			
//			SlotId = file.readInt();
//			SlotSize = file.readInt();
//			DataSize = file.readInt();
//			GrowSize = file.readInt();
		}
		
		@Override
		public void write(BinFile file) {
			file.writeInt(SlotId);
			file.writeInt(SlotSize);
			file.writeInt(DataSize);
			file.writeInt(GrowSize);
		}
		
		@Override
		public String toString() {
			return String.format("Slot ID=%d, Size=%d, DataSize=%d, GrowSize=%d", SlotId, SlotSize, DataSize, GrowSize);
		}

	}


}
