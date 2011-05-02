package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import au.org.ala.delta.model.Item;


/**
 * Transfer handler for dragging and dropping items.
 */
public abstract class ItemTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = 889705892088002277L;
	private int sourceIndex;
	
	
	public ItemTransferHandler() {
		
	}
	
	public boolean canImport(TransferHandler.TransferSupport info) {
		
		return info.isDataFlavorSupported(ItemTransferrable.ITEM_DATA_FLAVOUR);
	}
	
	protected Transferable createTransferable(JComponent c) {
		Item item = getItem();
		sourceIndex = getStartIndex();
		
		return new ItemTransferrable(item);
		
	}
	protected abstract int getStartIndex();
	protected abstract Item getItem();
	
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}
	
	public boolean importData(TransferHandler.TransferSupport info) {
		
		
		Transferable transferrable = info.getTransferable();
		
		Item item = null;
		try {
			item = (Item)transferrable.getTransferData(ItemTransferrable.ITEM_DATA_FLAVOUR);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		int targetIndex = getDropLocationIndex();
		if (targetIndex > sourceIndex) {
			targetIndex--;
		}
		
		if (info.getUserDropAction() == DnDConstants.ACTION_MOVE) {
			moveItem(item, targetIndex);
		}
		else if (info.getUserDropAction() == DnDConstants.ACTION_COPY) {
			copyItem(item, targetIndex);
		}
		return true;
	}
	
	protected abstract int getDropLocationIndex();
	
	protected abstract void moveItem(Item item, int targetIndex);
	
	protected abstract void copyItem(Item item, int targetIndex);
	

}
