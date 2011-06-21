package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.image.Image;

public interface ItemData {
	
	String getDescription();
	
	void setDescription(String description);
	
	List<Attribute> getAttributes();
	
	Attribute getAttribute(Character character);

	void addAttribute(Character character, Attribute attribute);

	boolean isVariant();
	
	void setVariant(boolean variant);
	
    String getImageData();
    
    void setImageData(String imageData);
    
    String getLinkFileDataWithSubjects();
    
    void setLinkFileDataWithSubjects(String linkFileData);
    
    String getLinkFileDataNoSubjects();
    
    void setLinkFileDataNoSubjects(String linkFileData);

	Image addImage(String fileName, String comments);
	
	void deleteImage(Image image);
	
	void moveImage(Image image, int position);

	List<Image> getImages(); 
}
