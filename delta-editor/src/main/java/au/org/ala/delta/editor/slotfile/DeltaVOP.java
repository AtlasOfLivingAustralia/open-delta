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

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.util.Utils;
import org.apache.commons.lang.NotImplementedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class DeltaVOP extends VOP {

	private VODeltaMasterDesc _deltaMaster;
	private VOImageInfoDesc _imageInfo;
	private Map<VOItemDesc, String> _itemNames;

	public DeltaVOP() {
		this(false);
	}
	
	public DeltaVOP(boolean noTemp) {
		super(noTemp);
		
		// Initialise the master descriptor
		initialiseDELTAMaster();
	}

	public DeltaVOP(String filename, boolean readonly) {
		super(filename, readonly, null);
	}
	
	public void consistencyCheck() {
		// Go through the finder map, and check that everything item and character exists in the delta master...
		for (Integer uid : getFinderMap().keySet()) {
			VOAnyDesc obj = getFinderMap().get(uid);
			if (obj instanceof VOCharBaseDesc) {
				int charNo = _deltaMaster.charNoFromUniId(uid);
				
				if (charNo <= 0) {
					VOCharBaseDesc ch = (VOCharBaseDesc) obj;
					int charDescId = ch.readCharTextInfo().get(0).charDesc;
					VOCharTextDesc t = (VOCharTextDesc) this.getDescFromId(charDescId);
					if (t != null) {
						System.err.println(t.readFeatureText(TextType.ANSI));
					}
					
					throw new RuntimeException("Inconsistent SlotFile - Character UID " + uid + " exists in finder map, but not in master character list");
				}				
			} else if (obj instanceof VOItemDesc) {
				int itemNo = _deltaMaster.itemNoFromUniId(uid);
				if (itemNo <= 0) {
					throw new RuntimeException("Inconsistent SlotFile - Item UID " + uid + " exists in finder map, but not in master item list");
				}								
			}
		}
		
	}

	public VODeltaMasterDesc getDeltaMaster() {
		return _deltaMaster;
	}

	public VOImageInfoDesc getImageInfo() {
		if (_imageInfo == null) {
			VOImageInfoDesc.ImageInfoFixedData imageInfoData = new VOImageInfoDesc.ImageInfoFixedData();

		    _imageInfo = (VOImageInfoDesc)insertObject(
		           imageInfoData,
		    	   VOImageInfoDesc.ImageInfoFixedData.SIZE,
		           null,
		           0,
		           0);
		}
		return _imageInfo;
	}

	public VOItemDesc getItemFromName(String name, boolean canonical) {

		String ansiName;
		if (!canonical) {
			ansiName = Utils.stripExtraSpaces(Utils.RTFToANSI(name));
		} else {
			ansiName = name;
		}

		for (Entry<VOItemDesc, String> entry : _itemNames.entrySet()) {
			if (ansiName.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
	
		return null;

	}

	public int getItemNameCount(String name) {
		return _itemNames.size();
	}

	public boolean deleteFromNameList(VOItemDesc item) {
		return (_itemNames.remove(item) != null);
	}

	public void insertInNameList(VOItemDesc item) {
		_itemNames.put(item, item.getAnsiName());
	}

	// Wrapper around Vop::Commit to also make a .bak copy of the "original" file
	public boolean commit(SlotFile slotFile) {
		
		synchronized(this) {
			if (slotFile == null) {
				slotFile = getPermSlotFile();
			}
			if (slotFile == null) {
				return false;
			}
			if (slotFile.getFileMode() == BinFileMode.FM_EXISTING) {
				String bakName = slotFile.getFileName();
				if (bakName.charAt(bakName.length() -1) != '.') {
			        bakName += '.';
				}
			    bakName += "bak";
			    try {
			          // Copy the entire contents of the original file,
			          // and its timestamp
			          BinFile bakFile = new BinFile(bakName, BinFileMode.FM_NEW);
			          slotFile.seekToEnd();
			          int size = slotFile.tell();
			          slotFile.seekToBegin();
			          bakFile.copyFile(slotFile, size);
			          
			          bakFile.setFileTime(slotFile.getFileTime());
			          bakFile.close();
			     }
			     catch (Exception e) {
			          //::MessageBox(NULL, "Error creating backup of data file", "File Error", MB_OK);
			    	 throw new RuntimeException(e);
			     }
			 }
			  boolean result = super.commit(slotFile);
			  // "Touch" the time stamp; otherwise the time stamp isn't updated until we
			  // finally close the file, which could lead to mis-leading times on the backup
			  // version.
			  slotFile.setFileTime(System.currentTimeMillis());
			  return result;
		}
	}

	@Override
	protected VOAnyDesc insertDesc(VOAnyDesc aDesc) {

		VOAnyDesc desc = super.insertDesc(aDesc);
		if (desc != null) {
			if (desc.isA(VODescFactory.VODeltaMasterDesc_TypeId)) {
				if (_deltaMaster != null)
					throw new RuntimeException("Internal error! Two DELTA Master records found!");

				_deltaMaster = (VODeltaMasterDesc) desc;
			}
			if (desc.isA(VODescFactory.VOImageInfoDesc_TypeId)) {
				if (_imageInfo != null) {
					throw new RuntimeException("Internal error! Two \"Image Info\" records found!");
				}
				_imageInfo = (VOImageInfoDesc) desc;
			}
			if (desc.isA(VODescFactory.VOItemDesc_TypeId)) {
				if (_itemNames == null) {
					_itemNames = new HashMap<VOItemDesc, String>();
				}
				VOItemDesc item = (VOItemDesc) desc;
				_itemNames.put(item, item.getAnsiName());
			}
		}
		return desc;
	}

	protected VOAnyDesc removeDesc(VOAnyDesc desc) {
		if (desc != null) {	
		    if (desc.isA(VOAnyDesc.VOTID_DELTA_BASE)) {
		        _deltaMaster = null;
		    }
		    if (desc.isA(VODescFactory.VOImageInfoDesc_TypeId)) {
		        _imageInfo = null;
		    }
		    if (desc.isA(VODescFactory.VOItemDesc_TypeId)) {
			  
		        VOItemDesc item = (VOItemDesc)desc;
		        // Remove the item's name from our list of names
		 
		        if (!deleteFromNameList(item)) {
		            throw new RuntimeException("Error encountered deleting item from name list");
		        }
		     }
	    }
		return super.removeDesc(desc);
	}

	protected void killDescList() {
		throw new NotImplementedException();
	}
	
	/**
	 * Creates and inserts new DELTAMaster for this DeltaVOP.
	 */
	private void initialiseDELTAMaster() {
		VODeltaMasterDesc.MasterFixedData deltaMaster = new VODeltaMasterDesc.MasterFixedData("(Unlabelled)");
		insertObject(deltaMaster, VODeltaMasterDesc.MasterFixedData.SIZE, null, 0, 128);
	}

}
