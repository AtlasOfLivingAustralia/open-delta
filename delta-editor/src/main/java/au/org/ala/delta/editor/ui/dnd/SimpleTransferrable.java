package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class SimpleTransferrable<T> implements Transferable {
	
	private T _toTransfer;
	
	public SimpleTransferrable(T toTransfer) {
		_toTransfer = toTransfer;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {new DataFlavor(_toTransfer.getClass(), _toTransfer.getClass().getName())};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		
		return flavor.getRepresentationClass().equals(_toTransfer.getClass());
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		if (!isDataFlavorSupported(flavor)) {
			throw new UnsupportedFlavorException(flavor);
		}
		return _toTransfer;
	}
	

}
