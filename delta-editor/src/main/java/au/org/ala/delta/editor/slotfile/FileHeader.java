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
package au.org.ala.delta.editor.slotfile;

import au.org.ala.delta.io.BinFile;

public class FileHeader implements IOObject {

	public static final int SIZE = 4; // 4 bytes to store a 32 bit number

	public int SysDataPtr;

	public FileHeader() {
		SysDataPtr = FileSignature.SIZE + FileHeader.SIZE;
	}

	@Override
	public void read(BinFile file) {
		SysDataPtr = file.readInt();
	}

	@Override
	public void write(BinFile file) {
		file.writeInt(SysDataPtr);		
	}

	@Override
	public int size() {
		return SIZE;
	}
	
	
	
}
