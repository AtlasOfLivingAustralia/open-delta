package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

public interface DataSetTranslator {

	public void beforeFirstItem();

	public void beforeItem(Item item);

	public void afterItem(Item item);

	public void beforeAttribute(Attribute attribute);

	public void afterAttribute(Attribute attribute);

	public void afterLastItem();

	public void attributeComment(String comment);

	public void attributeValues(Values values);
	
}