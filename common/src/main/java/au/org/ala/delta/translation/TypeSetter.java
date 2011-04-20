package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;

/**
 * Typesets the translated output of a DELTA data set.  Handles formatting marks, paragraphs etc.
 */
public interface TypeSetter {

	public abstract void beforeFirstItem();

	public abstract void beforeItem(Item item);

	public abstract void afterItem(Item item);

	public abstract void beforeAttribute(Attribute attribute);

	public abstract void afterAttribute(Attribute attribute);

	public abstract void afterLastItem();

	public abstract void beforeItemHeading();

	public abstract void afterItemHeading();

	public abstract void beforeItemName();

	public abstract void afterItemName();

	public abstract void newParagraph();

	public String typeSetItemDescription(String description);

	public abstract void beforeNewParagraphCharacter();
	
	public String rangeSeparator();
}