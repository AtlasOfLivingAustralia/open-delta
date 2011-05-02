package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import au.org.ala.delta.model.Item;

public class ItemTransferrable implements Transferable {
	
	public static DataFlavor ITEM_DATA_FLAVOUR = new DataFlavor(Item.class, "Item");
	private Item _item;
	
	public ItemTransferrable(Item item) {
		_item = item;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {ITEM_DATA_FLAVOUR};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		
		return ITEM_DATA_FLAVOUR.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return _item;
	}
}
