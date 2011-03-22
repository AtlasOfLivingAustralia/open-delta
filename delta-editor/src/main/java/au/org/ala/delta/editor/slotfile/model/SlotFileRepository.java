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
package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.DeltaFileReader;
import au.org.ala.delta.editor.slotfile.BinFileMode;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.SlotFile;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.util.IProgressObserver;

/**
 * Provides access to DELTA Data sets via a slot file implementation.
 */
public class SlotFileRepository implements DeltaDataSetRepository {

	

	/** 
	 * Saves the supplied data set to permanent storage
	 * @param dataSet the DELTA data set to save.
	 * @param observer allows the progress of the save to be tracked if required.
	 * @see au.org.ala.delta.model.DeltaDataSetRepository#save(au.org.ala.delta.model.DeltaDataSet)
	 */
	@Override
	public void save(DeltaDataSet dataSet, IProgressObserver observer) {
		getVOP(dataSet).commit(null);
	}
	
	/** 
	 * Saves the supplied data set to permanent storage
	 * @param dataSet the DELTA data set to save.
	 * @param name the new file name for the data set.
	 * @param observer allows the progress of the save to be tracked if required.
	 * @see au.org.ala.delta.model.DeltaDataSetRepository#save(au.org.ala.delta.model.DeltaDataSet)
	 */
	@Override
	public void saveAsName(DeltaDataSet dataSet, String name, IProgressObserver observer) {
		
		SlotFile newFile = new SlotFile(name, BinFileMode.FM_NEW);
		getVOP(dataSet).commit(newFile);
		
	}

	/**
	 * This implementation expects the supplied name to be a filename.
	 * 
	 * @param name the absolute path of the DELTA file.
	 * @param observer allows the progress of the file load to be tracked if required.
	 *  
	 * @see au.org.ala.delta.model.DeltaDataSetRepository#findByName(java.lang.String)
	 */
	@Override
	public DeltaDataSet findByName(String name, IProgressObserver observer) {

		DeltaDataSet dataSet = DeltaFileReader.readDeltaFile(name, observer);
		return dataSet;
	}
	
	/**
	 * Creates a new DeltaDataSet backed by a new DeltaVOP.
	 */
	@Override
	public DeltaDataSet newDataSet() {
		DeltaVOP vop = new DeltaVOP();
		SlotFileDataSetFactory _factory = new SlotFileDataSetFactory(vop);
		return _factory.createDataSet("");
	}

	private DeltaVOP getVOP(DeltaDataSet dataSet) {
		VOPAdaptor vop = (VOPAdaptor)dataSet;
		return vop.getVOP();
	}
	
}
