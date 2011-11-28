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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.util.Pair;

/**
 * Implements ItemData and stores the data in memory.
 */
public class DefaultItemData implements ItemData {

    private String _description;
    private boolean _variant;
    
    private List<Pair<String, String>> _linkFiles = new ArrayList<Pair<String, String>>();
    private Map<Character, Attribute> _attributes = new HashMap<Character, Attribute>();
    private List<Image> _images = new ArrayList<Image>();
    
    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public void setDescription(String description) {
        _description = description;
    }

    @Override
    public List<Attribute> getAttributes() {

        return null;
    }

    @Override
    public Attribute getAttribute(Character character) {

        Attribute attribute = _attributes.get(character);
        if (attribute == null) {
            attribute = AttributeFactory.newAttribute(character, new DefaultAttributeData(character));
        }

        return attribute;
    }

    @Override
    public void addAttribute(Character character, Attribute attribute) {
        _attributes.put(character, attribute);
    }

    @Override
    public boolean isVariant() {
        return _variant;
    }

    @Override
    public void setVariant(boolean variant) {
        _variant = variant;
    }
    
    @Override 
    public List<Pair<String, String>> getLinkFiles() {
        return new ArrayList<Pair<String, String>>(_linkFiles);
    }
    
    @Override
    public void setLinkFiles(List<Pair<String, String>> linkFiles) {
        _linkFiles = new ArrayList<Pair<String, String>>(linkFiles);
    }

    @Override
    public Image addImage(String fileName, String comments) {
        DefaultImageData imageData = new DefaultImageData(fileName);
        Image image = new Image(imageData);
        try {
            if (comments != null) {
                List<ImageOverlay> overlayList = new ImageOverlayParser().parseOverlays(comments, ImageType.IMAGE_TAXON);
                imageData.setOverlays(overlayList);
            }
            _images.add(image);
            return image;
        } catch (ParseException ex) {
            throw new RuntimeException("Error parsing taxon image overlay data");
        }
    }
    
    @Override
    public void addImage(Image image) {
        _images.add(image);        
    }

	@Override
	public List<Image> getImages() {
		return _images;
	}

	@Override
	public int getImageCount() {
		return _images.size();
	}

	@Override
	public void deleteImage(Image image) {
		_images.remove(image);
	}

	@Override
	public void moveImage(Image image, int position) {
		int imageIndex = _images.indexOf(image);
		_images.remove(imageIndex);
		_images.add(position, image);
	}
}
