package au.org.ala.delta.model.observer;

import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Item;

/**
 * This interface should be implemented by classes interested in being notified of changes to Items.
 * They should then call Item.addItemObserver(this) to register interest in changes to the item.
 */
public interface ItemObserver extends ImageObserver {

	/**
	 * Invoked when an item changes.
	 * @param item the item that has changed.
	 */
	public void itemChanged(Item item, Attribute attribute);
}
