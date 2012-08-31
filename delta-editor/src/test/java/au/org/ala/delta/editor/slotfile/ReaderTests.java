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

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaTestCase;
import au.org.ala.delta.editor.DeltaFileReader;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.util.CodeTimer;

public class ReaderTests extends DeltaTestCase {

	private String[] _files = new String[] { "Ponerini.dlt", "vide.dlt", "cflora.dlt", "sample.dlt", "newsample.dlt", "Grevillea.dlt" };

	public void testBasicRead() throws IOException {

		for (String filename : _files) {
			File f = copyURLToFile(String.format("/%s", filename));
			CodeTimer t = new CodeTimer("Reading " + filename);
			DeltaFileReader.readDeltaFile(f.getAbsolutePath(), null);
			t.stop(true);
		}
	}

	public void testDeepRead() throws IOException {

		for (String filename : _files) {
			File f = copyURLToFile(String.format("/%s", filename));

			CodeTimer t = new CodeTimer("Deep Reading " + filename);
			MutableDeltaDataSet ds = DeltaFileReader.readDeltaFile(f.getAbsolutePath(), null);
			deepRead(ds);
			t.stop(true);
		}
	}

//	public void testBasicRead2() throws IOException {
//		CodeTimer t = new CodeTimer("readDeltaFile");
//		DeltaDataSet ds = DeltaFileReader.readDeltaFile("c:/zz/grasses_big.dlt", null);
//		deepRead(ds);
//		t.stop(true);
//	}

}
