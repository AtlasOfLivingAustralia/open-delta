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

public class FileSignature implements IOObject {

	public static final int SIZE = 68;

	public static final short MAGIC_NUMBER = 0x000c;
	public static final byte MAJOR_VERSION = 0;
	public static final byte MINOR_VERSION = 2;
	public static final String COPYRIGHT = "DELTA-Datafile (c) G.F. Weiller 1996; CSIRO Entomology 1997";

	public String Copyright;
	public byte MajorVersion;
	public byte MinorVersion;
	public short Magic;

	@Override
	public void read(BinFile file) {

		Copyright = SlotFileEncoding.decode(file.readBytes(64));
		MajorVersion = file.readByte();
		MinorVersion = file.readByte();
		Magic = file.readShort();

		if (Magic != MAGIC_NUMBER) {
			throw new RuntimeException("bad magic number in file header. Are you sure this is a data file?");
		}
		if (MajorVersion != MAJOR_VERSION) {
			throw new RuntimeException("Unsupported major version number: " + MinorVersion + " expected " + MAJOR_VERSION);
		}

		if (MinorVersion != MINOR_VERSION) {
			throw new RuntimeException("Unsupported minor version number: " + MinorVersion + " expected " + MINOR_VERSION);
		}

	}

	@Override
	public void write(BinFile file) {
		file.writeByte((byte) 0x6);
		file.writeByte((byte) 0x22);
		file.swrite(COPYRIGHT, 62);
		file.writeByte(MAJOR_VERSION);
		file.writeByte(MINOR_VERSION);
		file.writeShort(MAGIC_NUMBER);
	}

}
