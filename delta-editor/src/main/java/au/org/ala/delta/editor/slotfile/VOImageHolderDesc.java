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

import java.util.List;

public abstract class VOImageHolderDesc extends VOAnyDesc {

	public VOImageHolderDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
	}

	@Override
	public int getTypeId() {
		return 0;
	}

	@Override
	public String getStringId() {
		return null;
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}
	
	public abstract List<Integer> readImageList();
	
	public abstract boolean writeImageList(List<Integer> imagelist);
	
	public abstract int getImageType();

	public abstract void deleteImage(int imageId);
	
	public abstract int getNImages();
}
