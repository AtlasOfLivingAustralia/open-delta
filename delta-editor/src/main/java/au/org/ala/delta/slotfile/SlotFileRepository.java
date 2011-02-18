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

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaFileReader;
import au.org.ala.delta.model.DeltaDataSetRepository;
import au.org.ala.delta.util.IProgressObserver;

/**
 * @author god08d
 *
 */
public class SlotFileRepository implements DeltaDataSetRepository {

	/** 
	 * Saves the supplied data set to permanent storage
	 * @param dataSet the DELTA data set to save.
	 * @param observer allows the progress of the save to be tracked if required.
	 * @see au.org.ala.delta.model.DeltaDataSetRepository#save(au.org.ala.delta.model.DeltaDataSet)
	 */
	@Override
	public void save(DeltaContext dataSet, IProgressObserver observer) {
		
		((DeltaContext)dataSet).VOP.commit(null);
		
	}
	
	/** 
	 * Saves the supplied data set to permanent storage
	 * @param dataSet the DELTA data set to save.
	 * @param name the new file name for the data set.
	 * @param observer allows the progress of the save to be tracked if required.
	 * @see au.org.ala.delta.model.DeltaDataSetRepository#save(au.org.ala.delta.model.DeltaDataSet)
	 */
	public void saveAsName(DeltaContext dataSet, String name, IProgressObserver observer) {
		
		SlotFile newFile = new SlotFile(name, BinFileMode.FM_NEW);
		((DeltaContext)dataSet).VOP.commit(newFile);
		
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
	public DeltaContext findByName(String name, IProgressObserver observer) {

		DeltaContext context = DeltaFileReader.readDeltaFile(name, observer);
		return context;
	}

	
	
}
