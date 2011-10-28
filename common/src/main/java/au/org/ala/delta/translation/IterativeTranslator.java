package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

/**
 * An IterativeTranslator is one that can perform a data set translation
 * one Item or Character at a time.
 * It's purpose is to allow interleaving of the output of multiple translations
 * for example TRANSLATE INTO NATURAL LANGUAGE and PRINT UNCODED CHARACTERS.
 *
 */
public interface IterativeTranslator {
	public void beforeFirstItem();

	public void beforeItem(Item item);

	public void afterItem(Item item);

	public void beforeAttribute(Attribute attribute);

	public void afterAttribute(Attribute attribute);

	public void afterLastItem();

	public void attributeComment(String comment);

	public void attributeValues(Values values);
	
	public void beforeFirstCharacter();
	
	public void beforeCharacter(Character character);
	
	public void afterCharacter(Character character);
	
	public void afterLastCharacter();
	
	public void translateOutputParameter(String parameterName);
}
