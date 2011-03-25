package au.org.ala.delta.model.observer;

import au.org.ala.delta.model.Attribute;

/**
 * This interface should be implemented by classes interested in being notified of changes to Attributes.
 * They should then call Attribute.addAttributeObserver(this) to register interest in changes to the attribute.
 */
public interface AttributeObserver {

	/**
	 * Invoked when the Attribute changes.
	 * @param attribute the changed Attribute.
	 */
	public void attributeChanged(Attribute attribute);
}
