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
package au.org.ala.delta.model.observer;

/**
 * Provides empty implementations of the DeltaDataSetObserver interface.
 * A convenience class for classes interested in only a subset of the DeltaDataSetObserver interface.
 */
public abstract class AbstractDataSetObserver implements DeltaDataSetObserver {

	@Override
	public void itemAdded(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemDeleted(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemMoved(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemEdited(DeltaDataSetChangeEvent event) {}

	@Override
	public void itemSelected(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterAdded(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterDeleted(DeltaDataSetChangeEvent event) {}
	
	@Override
	public void characterTypeChanged(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterMoved(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterEdited(DeltaDataSetChangeEvent event) {}

	@Override
	public void characterSelected(DeltaDataSetChangeEvent event) {}

	@Override
	public void imageEdited(DeltaDataSetChangeEvent event) {}
}
