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
import au.org.ala.delta.io.BinFileEncoding;

public class VONoteDesc extends VOAnyDesc {

	private static int _noteOffs = FixedData.SIZE + SlotFile.SlotHeader.SIZE;

	public static final int NOTE_SIZE = 256;

	public VONoteDesc(SlotFile slotFile, VOP vop) {
		super(slotFile, vop);
	}

	@Override
	public int getTypeId() {
		return VOAnyDesc.VOTID_USER_BASE + 1;
	}

	@Override
	public String getStringId() {
		return "Note";
	}

	@Override
	public int getNumberOfItems() {
		return 0;
	}

	@Override
	public int getFixedDataSize() {
		return NoteFixedData.SIZE;
	}

	public String readNote() {
		synchronized (getVOP()) {
			assert _slotFile != null;
			_slotFile.seek(_slotHdrPtr + _noteOffs);
			byte[] bytes = _slotFile.read(NOTE_SIZE);
			return BinFileEncoding.decode(bytes);
		}
	}

	public void writeNote(String note) {
		synchronized (getVOP()) {
			assert _slotFile != null;
			_slotFile.seek(_slotHdrPtr + _noteOffs);
			_slotFile.swrite(note, NOTE_SIZE);
		}
	}

	public static class NoteFixedData extends FixedData {

		public static final int SIZE = FixedData.SIZE + NOTE_SIZE;

		byte[] Note = new byte[NOTE_SIZE];

		public NoteFixedData(String acronym) {
			super(acronym);
		}

		@Override
		public void read(BinFile file) {
			super.read(file);
			Note = file.read(NOTE_SIZE);
		}

		@Override
		public void write(BinFile file) {
			super.write(file);
			file.swrite(Note);
		}

	}

}
