package au.org.ala.delta.model.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    	DefaultImageData imageData = new DefaultImageData(fileName);
		Image image = new Image(imageData);
    	_images.add(image);
    	return image;
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
