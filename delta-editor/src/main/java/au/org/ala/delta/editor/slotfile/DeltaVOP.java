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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.util.Utils;

public class DeltaVOP extends VOP {

	private VODeltaMasterDesc _deltaMaster;
	private VOImageInfoDesc _imageInfo;
	private Map<String, VOItemDesc> _itemNames = new HashMap<String, VOItemDesc>();

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

	public VODeltaMasterDesc getDeltaMaster() {
		return _deltaMaster;
	}

	public VOImageInfoDesc getImageInfo() {
		return _imageInfo;
	}

	public VOItemDesc getItemFromName(String name, boolean canonical) {

		String ansiName;
		if (!canonical) {
			ansiName = Utils.stripExtraSpaces(Utils.RTFToANSI(name));
		} else {
			ansiName = name;
		}

		if (_itemNames.containsKey(ansiName)) {
			return _itemNames.get(ansiName);
		}

		return null;

	}

	public int getItemNameCount(String name) {
		return _itemNames.size();
	}

	public boolean deleteFromNameList(VOItemDesc item) {
		return (_itemNames.remove(item.getAnsiName()) != null);
	}

	public void insertInNameList(VOItemDesc item) {
		_itemNames.put(item.getAnsiName(), item);
	}

	// Wrapper around Vop::Commit to also make a .bak copy of the "original" file
	public boolean commit(SlotFile slotFile) {
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
					_itemNames = new HashMap<String, VOItemDesc>();
				}
				VOItemDesc item = (VOItemDesc) desc;
				_itemNames.put(item.getAnsiName(), item);
			}
		}
		return desc;
	}

	protected VOAnyDesc removeDesc(VOAnyDesc desc) {
		throw new NotImplementedException();
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
