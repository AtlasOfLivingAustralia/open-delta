/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 * 
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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
		return new SimpleFlavor[] {new SimpleFlavor(_toTransfer.getClass(), _toTransfer.getClass().getName())};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		
		return flavor.getRepresentationClass().isAssignableFrom(_toTransfer.getClass());
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
