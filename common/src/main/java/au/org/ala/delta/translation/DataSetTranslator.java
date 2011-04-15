package au.org.ala.delta.translation;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.ParsedAttribute.Values;

public interface DataSetTranslator {

	public abstract void beforeFirstItem();

	public abstract void beforeItem(Item item);

	public abstract void afterItem(Item item);

	public abstract void beforeAttribute(Attribute attribute);

	public abstract void afterAttribute(Attribute attribute);

	public abstract void afterLastItem();

	public abstract void attributeComment(String comment);

	public abstract void attributeValues(Values values);

}