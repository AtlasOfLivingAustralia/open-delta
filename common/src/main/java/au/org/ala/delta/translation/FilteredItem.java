package au.org.ala.delta.translation;

import au.org.ala.delta.model.Item;

public class FilteredItem {

	private int _filteredNumber;
	private Item _item;

	public FilteredItem(int filteredNumber, Item item) {
		_filteredNumber = filteredNumber;
		_item = item;
	}

	public int getItemNumber() {
		return _filteredNumber;
	}

	public Item getItem() {
		return _item;
	}
}
