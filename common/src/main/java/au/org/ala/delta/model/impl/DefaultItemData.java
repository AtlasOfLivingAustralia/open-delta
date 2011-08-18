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

/**
 * Implements ItemData and stores the data in memory.
 */
public class DefaultItemData implements ItemData {

    private String _description;
    private boolean _variant;
    
    private Map<String, String> _linkFiles = new HashMap<String, String>();
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
    public Map<String, String> getLinkFiles() {
        return new HashMap<String, String>(_linkFiles);
    }
    
    @Override
    public void setLinkFiles(Map<String, String> linkFiles) {
        _linkFiles = new HashMap<String, String>(linkFiles);
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
