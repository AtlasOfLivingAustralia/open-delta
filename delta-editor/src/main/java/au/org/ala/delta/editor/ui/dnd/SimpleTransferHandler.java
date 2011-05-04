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
	
	private DataFlavor _flavour;
	
	public SimpleTransferHandler(Class<T> clazz) {
		_flavour = new DataFlavor(clazz, clazz.getName());
	}
	
	public boolean canImport(TransferHandler.TransferSupport info) {
		
		return info.isDataFlavorSupported(_flavour);
	}
	
	protected Transferable createTransferable(JComponent c) {
		T transferObject = getTransferObject();
		sourceIndex = getStartIndex();
		
		return new SimpleTransferrable<T>(transferObject);
		
	}
	protected abstract int getStartIndex();
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
		
		int targetIndex = getDropLocationIndex();
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
	
	protected abstract int getDropLocationIndex();
	
	protected abstract void move(T transferObject, int targetIndex);
	
	protected abstract void copy(T transferObject, int targetIndex);
	

}
