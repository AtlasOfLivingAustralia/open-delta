package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * Transfer handler for dragging and dropping items.
 */
public abstract class SimpleTransferHandler<T> extends TransferHandler {
	
	private static final long serialVersionUID = 889705892088002277L;
	private int sourceIndex;
	private Class<T> _transferClass;
	private DataFlavor _flavour;
	
	public SimpleTransferHandler(Class<T> clazz) {
		_flavour = new SimpleFlavor(clazz, clazz.getName());
		_transferClass = clazz;
	}
	
	public Class<T> getTransferClass() {
		return _transferClass;
	}
	
	/**
	 * Returns true if the transferable flavour is supported and the transferable has been 
	 * dragged away from it's initial position.
	 * @param info provides information about the drag.
	 * @return true if the current transferable can be imported into the current drop target.
	 */
	public boolean canImport(TransferHandler.TransferSupport info) {
		if (!info.isDataFlavorSupported(_flavour)) {
			return false;
		}
		
		int dropIndex = getDropLocationIndex(info.getDropLocation());
		
		return ((dropIndex >= 0) && (dropIndex != sourceIndex) && (dropIndex != sourceIndex+1));
	}
	
	protected Transferable createTransferable(JComponent c) {
		T transferObject = getTransferObject();
		sourceIndex = getStartIndex();
		
		return new SimpleTransferrable<T>(transferObject);
		
	}
	
	/**
	 * Should be overridden to provide the index at which the transferable was located when
	 * the drag operation began.
	 * @return the starting point of the drag.
	 */
	protected abstract int getStartIndex();
	
	/**
	 * Should be overriden to provide the actual object being dragged.
	 * @return the object being dragged.
	 */
	protected abstract T getTransferObject();
	
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY_OR_MOVE;
	}
	
	@SuppressWarnings("unchecked")
	public boolean importData(TransferHandler.TransferSupport info) {
		
		
		Transferable transferrable = info.getTransferable();
		
		T transferObject = null;
		try {
			transferObject = (T)transferrable.getTransferData(_flavour);
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		int targetIndex = getDropLocationIndex(info.getDropLocation());
		if (targetIndex > sourceIndex) {
			targetIndex--;
		}
		
		if (info.getUserDropAction() == DnDConstants.ACTION_MOVE) {
			move(transferObject, targetIndex);
		}
		else if (info.getUserDropAction() == DnDConstants.ACTION_COPY) {
			copy(transferObject, targetIndex);
		}
		return true;
	}
	
	protected abstract int getDropLocationIndex(DropLocation dropLocation);
	
	protected abstract void move(T transferObject, int targetIndex);
	
	protected abstract void copy(T transferObject, int targetIndex);
	

}
