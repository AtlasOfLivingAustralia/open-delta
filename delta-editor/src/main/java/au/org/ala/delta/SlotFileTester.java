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
package au.org.ala.delta;

import javax.swing.UIManager;

import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;

public class SlotFileTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// VOP v = new VOP("c:/zz/bigtest.dlt", true);
		// DeltaVOP v = new DeltaVOP("c:/zz/sample.dlt", true);
		// v.close();
		
		try {
			UIManager.setLookAndFeel(new WindowsClassicLookAndFeel());
			//UIManager.setLookAndFeel(new WindowsLookAndFeel());
// 			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception ex) {
			System.err.println(ex);
		}

		DeltaContext context = DeltaFileReader.readDeltaFileFully("c:/zz/grasses.dlt");

//		MatrixViewer grid = new MatrixViewer(context);
//		grid.setVisible(true);
//		
//		TreeViewer tree = new TreeViewer(context);
//		tree.setVisible(true);

	}

}
