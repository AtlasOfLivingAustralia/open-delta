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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import au.org.ala.delta.editor.slotfile.SlotFile.SlotHeader;
import au.org.ala.delta.io.BinFileMode;

public class VOP {

	private SlotFile _permSlotFile;
	private SlotFile _tempSlotFile;
	protected int _lastUniId;
	private int[] _useIds;
	private Deque<Integer> _uniIdCont = new ArrayDeque<Integer>();

	private Map<Integer, VOAnyDesc> _finderMap = new TreeMap<Integer, VOAnyDesc>();

	public VOP(boolean notemp) {
		if (!notemp) {
			_tempSlotFile = new SlotFile();
		}
	}

	public VOP(String filename, boolean readonly) {
		open(filename, readonly, null);
	}

	public VOP(String filename, boolean readonly, int[] useIds) {
		open(filename, readonly, useIds);
	}
	
	public void open(String filename, boolean readonly, int[] useIds) {
		_useIds = useIds;
		BinFileMode mode = readonly ? BinFileMode.FM_READONLY
				: BinFileMode.FM_EXISTING;
		_tempSlotFile = new SlotFile();
		_permSlotFile = new SlotFile(filename, mode);
		initVOP(useIds);
	}

	private void initVOP(int[] useIds) {
		if (useIds != _useIds) {
			_useIds = useIds;
		}

		killDescList();
		_uniIdCont.clear();

		if (_permSlotFile == null) {
			return;
		}

		_permSlotFile.getFreeSlotMap().clear();

		int hdrPtr = _permSlotFile.getUserDataPtr();
		int endPtr = _permSlotFile.getSysDataPtr();

		while (hdrPtr < endPtr) {
			_permSlotFile.seek(hdrPtr);
			SlotFile.SlotHeader header = new SlotFile.SlotHeader();
			header.read(_permSlotFile);

			if (header.SlotId == SlotFile.SID_DELETED) {
				_permSlotFile.freeSlot(hdrPtr, header.SlotSize);
			}

			if (header.SlotId == VOAnyDesc.SID_UIDS) {
				// UID info...
				int n = header.DataSize / 4; // Size in bytes of a TVOUniID, or				
				_lastUniId = _permSlotFile.readInt();
				n--;				
				for (int i = 0; i < n; i++) {
					int id = _permSlotFile.readInt();
					_uniIdCont.push(id);
				}
				_permSlotFile.freeSlot(hdrPtr, header.SlotSize);
			} else if (header.SlotId == VOAnyDesc.SID_DESC) {
				VOAnyDesc desc = VOAnyDesc.CreateAnyDesc(this, false);
				
				if (useIds != null) {
					for (int typeId : useIds) {
						if (desc.isA(typeId)) {
							insertDesc(desc);
							break;
						}
					}
				} else {
					insertDesc(desc);
				}
			}

			hdrPtr += header.SlotSize + SlotFile.SlotHeader.SIZE;
		}

	}

	protected VOAnyDesc insertDesc(VOAnyDesc desc) {
		if (_finderMap.containsKey(desc.getUniId())) {
			throw new RuntimeException(
					"Duplicate UniID detected in VOP! Offending ID is "
							+ desc.getUniId());
		}

		_finderMap.put(desc.getUniId(), desc);
		return desc;
	}
	
	// Remove desc from list
	// return the removed desc or NULL if not found
	//
	protected VOAnyDesc removeDesc(VOAnyDesc desc) {

		// TODO the semantics of the existing code is a little different - 
		// because the _finderMap is a multimap it allows duplicate keys, in which case this
		// method will only remove the matching VOAnyDesc.  In our case we only allow one per unique id
		// (which meant the free list is a separate structure).
	   if (desc != null) {
	      if (_finderMap.containsValue(desc)) {
	    	  _finderMap.remove(desc.getUniId());
	    	  return desc;
	      }
	   }
	   return null;
	}

	private void killDescList() {
		_finderMap.clear();

	}
	

	public void close() {
		if (_tempSlotFile != null) {
			_tempSlotFile.close();
			_tempSlotFile = null;
		}

		if (_permSlotFile != null) {
			_permSlotFile.close();
			_permSlotFile = null;
			
		}
	}

	//
	//  Commit()
	//
	// Write everything to permanent storage
	// after here there is no undo
	// if data can be committed to a new file the new file will replace the PermSlotFile.
	//
	boolean commit(SlotFile file)
	{
		if(file == null) {
			file = _permSlotFile;
		}
		if(file == null) {
			return false;
		}

		for (VOAnyDesc desc : _finderMap.values()) {
			
			copyObject(desc, file); // do not free the original slot so it can be used for save-as
			if(desc.isDirty())  { // if quick access descriptor data need storing
			
				// At this stage, DataSeek and DataWrite must NOT automatically move the object
				// to the temporary file. So we set the descriptor's VOP pointer to NULL, so that
				// MakeTemp() will fail.
		         desc.disableTemp();

		         desc.storeQData();
		         desc.setDirty(false); ////

		        desc.enableTemp();
			}
			if(desc.hasRefChanged()) {
				desc.writeRefCount(desc.getRefCount());  // sum stored RefCount.
			}

		}

	   // brand all deleted objects in the file
	   file.brandDeleted();

	   // save all recovered uids in a system slot
	   ////long n = UniIdCont.GetItemsInContainer();
	   int n = _uniIdCont.size();
  
       int dataSize = (n + 1) * 4 /*size of int*/;
       int hdrPtr = file.allocSlot (dataSize+SlotHeader.SIZE, VOAnyDesc.SID_UIDS, dataSize, 0 );
       
       SlotHeader header = new SlotHeader();
       file.seek(hdrPtr);
       header.read(file);
       
       file.writeInt(_lastUniId);
       
       for ( int u : _uniIdCont ) {
         file.writeInt(u);
       }
       
       file.freeSlot(hdrPtr, header.SlotSize);    // FreeSlot
   
	   // there are currently no important data in the FileHeader (might come later)
	   file.writeFileHeader();
	   file.commit();
	   // if data were committed to a new file, use the new file from now.
	   if(file != _permSlotFile)
	   {
		   // If this is a new VOP _permSlotFile can be null at this point.
		   if (_permSlotFile != null) {
			   _permSlotFile.close();
		   }
	      
	      _permSlotFile = file;
	   }
	
	   // Tempfile has no data anymore.
	   // get a new one to avoid all the empty slots.
	   _tempSlotFile.close();
	   _tempSlotFile = new SlotFile();
	
	   return true;
	
	}

	
	//
	//  Revert()
	//
	// Discard all changes
	// after here there is no undo
	//
	public boolean revert() {
	   // Init all descriptors from the PermSlotFile.
		 //// It is faster when we're just closing to call Close
	   //// But don't do this if we REALLY want to revert (that is, go back to
	   //// our earlier state)
	   close();  //// DON'T DO THIS IF REVERT IS REALLY REVERT AND NOT PART OF CLOSE... TODO this looks very strange, we will end up with a closed perm file and a new temp file???
	   ////
	   initVOP(_useIds);

	   // Tempfile has no data anymore.
	   // get a new one to avoid all the empty slots.
	   if (_tempSlotFile != null) {
		   _tempSlotFile.close();
		   _tempSlotFile = new SlotFile();
	   }
	   return true;
	}

	public boolean isDirty() {
		for (VOAnyDesc desc : _finderMap.values()) {
			if( desc.getSlotFile() != _permSlotFile  ||  desc.isDirty() ) {
				return true;
			}
		}
		return false;
	}

	
	/**
	 * Inserts the header data described by the supplied FixedData, and if not
	 * null the variable length data supplied in the variData byte array.
	 * 
	 * @param fixed
	 *            the header data for the object to insert.
	 * @param size
	 *            (i thought this was in the fixed data struct.... check)
	 * @param variData
	 *            variable sized data - may be null if we are just creating a
	 *            header.
	 * @param initSlotSize
	 * @param growSize
	 * @return
	 */
	 public VOAnyDesc insertObject(VOAnyDesc.FixedData fixed, int size,
	     byte[] variData, int initSlotSize, int growSize) { 
		 //TVOAnyDesc *TVop::InsertObject( TVOAnyDesc::TFixedData *fixedData, size_t fixedDataSize, 
		 // void huge *variData, long variDataSize, 
		 // long initSlotSize, long growsSize )
	  
	     // Use the temp file by default. 
		 SlotFile file = _tempSlotFile;
	  
	  // Create the Slot. 
		 int slotDataSize = size; 
		 if (variData != null) {
			 slotDataSize += variData.length; 
	  }
	  
	  // what should be keep growing by grow size till we have enough space? 
		 if (initSlotSize < slotDataSize) { 
			 initSlotSize = slotDataSize + growSize; 
		 }
	  
	  file.allocSlot( initSlotSize, VOAnyDesc.SID_DESC, slotDataSize , growSize );
	  
	  // Supply a unique id and put in presData struct (overwrite provided value) 
	  fixed.UniId = getUniId();
	  
	  // Save SlotHdrPtr to construct the Descriptor. 
	  int slotPtr = file.tell(); // - sizeof(TSlotFile::TSlotHeader);
	 
	  // Write Fixed descriptor data. 
	  fixed.write(file);
	  
	  // Write variable data if supplied, otherwise variable data region is not initialized and 
	  // the caller must then use the descriptor to write the data. 
	  if (variData != null) { 
		  file.swrite(variData); 
	  }
	  
	  // Construct the descriptor 
	  file.seek(slotPtr);
	 
	  VOAnyDesc desc = VOAnyDesc.CreateAnyDesc(this,true); 
	  
	  // Insert desc in desclist 
	  insertDesc(desc);
	  
	  return desc;
	  
	 }
	 
    /*
	 * --- Delete Object from the Vop
	 *
	 * Objects that are referenced or embedded are not deleted.
	 * If the object references other objects, than these will be unreferenced.
	 * If the object embedds other objecte, than these are deleted too.
	 * The actual work is done by a recursive delete function.
	 */
	public void deleteObject(VOAnyDesc desc) {
	  if( desc.getActiveRefCount()<= 0 && !desc.isEmbedded() )  // can't delete embedded or linked ones
	      deleteObjectR(desc);
	}

	/**
	 *  Recursive delete.
	 */
	void deleteObjectR(VOAnyDesc desc) {
	    // Undo all references in the other objects  ( UN-SYMMETRY references are set at a higher level !!!!)
	    // should make an extra function UnReference(desc)
	
	    // Get a sorted array of all dependent uids
		List<Integer> uids = desc.getDependents();
	    for (int i : uids) {
	        if (_finderMap.containsKey(i)) {
	    		  
	            VOAnyDesc d = _finderMap.get(i);
	            d.decRefCount();
	        }
	    }
	 
	    // Recursivly delete all embedded objects
	    // Get an array of all embedded uids
		uids = desc.getEmbeddeds();
	    // check every descriptor and if embedded in current one delete
		for (int i : uids) {
	      
	        if (_finderMap.containsKey(i)) {
	       
	            VOAnyDesc d = _finderMap.get(i);
	            deleteObjectR(d);
	        }
	    }
	  
	    // Recover uniId;
	    _uniIdCont.add(desc.getUniId());

	    // Recover slot;
	    desc.getSlotFile().freeSlot(desc.getSlotHdrPtr(), desc.readSlotSize());

	    // Remove from List.
	    removeDesc(desc);
	}

	
	 
	 public boolean move2Temp(VOAnyDesc desc) {
		 return moveObject(desc, _tempSlotFile);
	 }
	 
	 public boolean copy2Temp(VOAnyDesc desc) {
		 return copyObject(desc, _tempSlotFile);
	 }
	 
	 //
	// Copy an object (represented by a descriptor) to another file.
	// The minimum SlotSize at the new location = DataSize + GrowSize.
	// Adjust SlotFile and SlotHdrPtr members of descriptor,  everything else is unchanged.
	// Return:
	 //			true 	if successfully moved
	 //			false if not moved ( either a file = NULL or object resides already on dest)
	//
	boolean copyObject(VOAnyDesc desc, SlotFile dstFile) {
	   SlotFile srcFile = desc.getSlotFile();
	   int srcHdrPtr = desc.getSlotHdrPtr();

	   if( (srcFile == null) || (dstFile == null) || (srcFile == dstFile)) {
			return false;
		}
	   
	   // Get slot header data.
	   srcFile.seek(desc.getSlotHdrPtr());
	   SlotHeader sHdr = srcFile.readSlotHeader();

	   // Create new Slot in dest file
	   int minSlotSize = sHdr.DataSize + sHdr.GrowSize;
	   //dstFile->AllocSlot(minSlotSize, sHdr.SlotId, sHdr.DataSize, sHdr.GrowSize);
	   desc.setSlotHdrPtr(dstFile.allocSlot(minSlotSize, sHdr.SlotId, sHdr.DataSize, sHdr.GrowSize));

	   // Adjust the new File and position
	   //desc->SlotHdrPtr = dstFile->Tell() - sizeof(TSlotFile::TSlotHeader);
	   desc.setSlotFile(dstFile);

	   // Copy the data
	   return dstFile.copySlotData(srcHdrPtr, srcFile);
	}

	 
	// Move object to another file
	// same as CopyObject but also frees the original slot
	//
	public boolean moveObject(VOAnyDesc desc, SlotFile dstFile) {
	    SlotFile srcFile = desc.getSlotFile();
		if( (srcFile == null) || (dstFile == null) || (srcFile == dstFile)) {
			return false;
		}

	  desc.getSlotFile().freeSlot(desc.getSlotHdrPtr(), desc.readSlotSize());
	  return copyObject(desc, dstFile);
	}
	
	
	 

	// Objects that are referenced or embedded are not deleted.
	// If the object references other objects, than these will be unreferenced.
	// If the object embedds other objecte, than these are deleted too.
	// The actual work is done by a recursive delete function.
	/*public void deleteObject(TVOAnyDesc desc) {
	  if( desc->GetActiveRefCount()<= 0 && !desc->IsEmbedded() )  // can't delete embedded or linked ones
	      DeleteObjectR(desc);
	}*/

	
	
	public boolean containsDesc(VOAnyDesc desc) {
		return false;
	}

	public SlotFile getPermSlotFile() {
		return _permSlotFile;
	}

	public SlotFile getTempSlotFile() {
		return _tempSlotFile;
	}

	public String getFilename() {
		if (_permSlotFile == null) {
			return null;
		}
		return _permSlotFile.getFileName();
	}

	public VOAnyDesc getDescFromId(int id) {
		if (_finderMap.containsKey(id)) {
			return _finderMap.get(id);
		}
		return null;
	}

	// ------------ UniId management --------------------------------
	//
	/**
	 * TODO check this explanation - i think that's what _uniIdCont is - if so we'll rename it.
	 * @return the next available unique id - reusing one from a deleted slot if available.
	 */
	int getUniId() {
		int uid;

		if (_uniIdCont.isEmpty()) {
			uid = ++_lastUniId;
		} else {
			uid = _uniIdCont.pop();
		}

		return uid;
	}

}
