package au.org.ala.delta.model.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.AttributeFactory;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;

/**
 * Implements ItemData and stores the data in memory.
 */
public class DefaultItemData implements ItemData {

    private String _description;
    private boolean _variant;
    private String _imageData;
    private String _linkFileDataWithSubjects;
    private String _linkFileDataNoSubjects;

    private Map<Character, Attribute> _attributes = new HashMap<Character, Attribute>();

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
            attribute = AttributeFactory.newAttribute(character, new DefaultAttributeData());
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
    public String getImageData() {
        return _imageData;
    }

    @Override
    public void setImageData(String imageData) {
        _imageData = imageData;
    }

    @Override
    public String getLinkFileDataWithSubjects() {
        return _linkFileDataWithSubjects;
    }

    @Override
    public void setLinkFileDataWithSubjects(String linkFileData) {
        _linkFileDataWithSubjects = linkFileData;
    }

    @Override
    public String getLinkFileDataNoSubjects() {
        return _linkFileDataNoSubjects;
    }

    @Override
    public void setLinkFileDataNoSubjects(String linkFileData) {
        _linkFileDataNoSubjects = linkFileData;
    }
    
    @Override
    public Image addImage(String fileName, String comments) {
    	throw new NotImplementedException();
    }

	@Override
	public List<Image> getImages() {
		throw new NotImplementedException();
	}

	@Override
	public int getImageCount() {
		throw new NotImplementedException();
	}

	
	@Override
	public void deleteImage(Image image) {
		throw new NotImplementedException();
	}

	@Override
	public void moveImage(Image image, int position) {
		throw new NotImplementedException();
	}
}
