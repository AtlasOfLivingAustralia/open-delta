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
package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.util.Pair;

public interface ItemData {
	
	String getDescription();
	
	void setDescription(String description);
	
	List<Attribute> getAttributes();
	
	Attribute getAttribute(Character character);

	void addAttribute(Character character, Attribute attribute);

	boolean isVariant();
	
	void setVariant(boolean variant);
	
	List<Pair<String, String>> getLinkFiles();
	
	void setLinkFiles(List<Pair<String, String>> linkFiles);

	Image addImage(String fileName, String comments);
	
	void addImage(Image image);
	
	void deleteImage(Image image);
	
	void moveImage(Image image, int position);

	List<Image> getImages();

	int getImageCount(); 
}
