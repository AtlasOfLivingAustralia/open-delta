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
package au.org.ala.delta.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.impl.ItemData;
import au.org.ala.delta.model.observer.AttributeObserver;
import au.org.ala.delta.model.observer.ImageObserver;
import au.org.ala.delta.model.observer.ItemObserver;
import au.org.ala.delta.util.Pair;

/**
 * Represents an Item in the DELTA system.
 * An item usually corresponds to a Taxon, but a 1-1 relationship is not required.
 */
public class Item implements AttributeObserver, ImageObserver, Illustratable, Comparable<Item> {

    private ItemData _impl;

	private int _itemNumber;
	
	private List<ItemObserver> _observers;
	
	public Item(ItemData impl, int itemNum) {
		setItemData(impl);
		_itemNumber = itemNum;
	}
	
	public Item(int itemId) {
		_itemNumber = itemId;
	}
	
	public int getItemNumber() {
		return _itemNumber;
	}
	
	/**
	 * This needs to be done when items are inserted or deleted - the remaining items
	 * may need to be renumbered to account for the change.
	 * @param number the new number for this item.
	 */
	public void setItemNumber(int number) {
		_itemNumber = number;
	}

	public void setDescription(String description) {
		_impl.setDescription(description);
		notifyObservers();
	}
	
	public String getDescription() {
		return _impl.getDescription();
	}
	
	public List<Attribute> getAttributes() {
		return _impl.getAttributes();
	}
	
	public Attribute getAttribute(Character character) {
		Attribute attribute = doGetAttribute(character);

		if (attribute != null) {
			attribute.setItem(this);
			attribute.addAttributeObserver(this);
		}
		return attribute;
	}
	
	protected Attribute doGetAttribute(Character character) {
		return _impl.getAttribute(character);
	}
	
	public void addAttribute(Character character, Attribute attribute) {
	    if (!attribute.getItem().equals(this)) {
	        throw new IllegalArgumentException("Supplied attribute does not reference this item");
	    }
	    
	    if (!attribute.getCharacter().equals(character)) {
	        throw new IllegalArgumentException("Supplied attribute does not reference the supplied character");
	    }
	    
		_impl.addAttribute(character, attribute);
		notifyObservers();
	}
	
	public boolean hasAttribute(Character character) {
		Attribute attribute = getAttribute(character);
		return ((attribute != null) && StringUtils.isNotEmpty(attribute.getValueAsString()));
	}
	
	public boolean isVariant() {
		return _impl.isVariant();
	}
	
	public void setVariant(boolean variant) {
		_impl.setVariant(variant);
		notifyObservers();
	}
	
	public List<Pair<String, String>> getLinkFiles() {
	    return _impl.getLinkFiles();
	}
	
	public void setLinkFiles(List<Pair<String, String>> linkFiles) {
	    _impl.setLinkFiles(linkFiles);
	}
    
    @Override
    public Image addImage(String fileName, String comments) {
    	Image image = _impl.addImage(fileName, comments);
    	image.setSubject(this);
    	image.addImageObserver(this);
    	notifyObservers();
    	
    	return image;
    }
    
    @Override
	public void deleteImage(Image image) {
    	_impl.deleteImage(image);
    	notifyObservers();
	}

	@Override
	public void moveImage(Image image, int position) {
		_impl.moveImage(image, position);
    	notifyObservers();
	}

	@Override
    public List<Image> getImages() {
    	List<Image> images = _impl.getImages();
    	
    	for (Image image : images) {
    		image.setSubject(this);
    		image.addImageObserver(this);
    	}
    	
    	return images;
    }
	
	@Override
	public int getImageCount() {
		return _impl.getImageCount();
	}
    
	
	@Override
	public void attributeChanged(Attribute attribute) {
		notifyObservers(attribute);
		
	}
	
	/**
	 * Registers interest in being notified of changes to this Item.
	 * @param observer the object interested in receiving notification of changes.
	 */
	public void addItemObserver(ItemObserver observer) {
		if (_observers == null) {
			_observers = new ArrayList<ItemObserver>(1);
		}
		if (!_observers.contains(observer)) {
			_observers.add(observer);
		}
	}
	
	/**
	 * De-registers interest in changes to this Item.
	 * @param observer the object no longer interested in receiving notification of changes.
	 */
	public void removeItemObserver(ItemObserver observer) {
		if (_observers == null) {
			return;
		}
		_observers.remove(observer);
	}
	
	/**
	 * Notifies all registered ItemObservers that this Item has changed.
	 */
	protected void notifyObservers() {
		notifyObservers(null);
	}	
	
	 @Override
    public void imageChanged(Image image) {
    	if (_observers == null) {
            return;
        }
        // Notify observers in reverse order to support observer removal during
        // event handling.
        for (int i = _observers.size() - 1; i >= 0; i--) {
            _observers.get(i).imageChanged(image);
        }
    }
	
	/**
	 * Notifies all registered ItemObservers that this Item has changed.
	 */
	protected void notifyObservers(Attribute attribute) {
		if (_observers == null) {
			return;
		}
		// Notify observers in reverse order to support observer removal during event handling.
		for (int i=_observers.size()-1; i>=0; i--) {
			_observers.get(i).itemChanged(this, attribute);
		}
	}
	
	public ItemData getItemData() {
		return _impl;
	}
	
	protected void setItemData(ItemData impl) {
		_impl = impl;
	}
	
	/**
	 * Items are equal if they have the same item number.
	 */
	public boolean equals(Object item) {
		if ((item == null) || !(item instanceof Item)) {
			return false;
		}
		
		return _itemNumber == ((Item)item).getItemNumber();
	}
	
	/**
	 * The item number is unique so makes a decent hashcode.
	 */
	public int hashCode() {
		return _itemNumber;
	}

    @Override
    public int compareTo(Item o) {
        return Integer.valueOf(this.getItemNumber()).compareTo(Integer.valueOf(o.getItemNumber()));
    }
    
    @Override
    public String toString() {
        return _itemNumber + ". " + getDescription();
    }
}
