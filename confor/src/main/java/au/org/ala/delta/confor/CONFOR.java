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
package au.org.ala.delta.confor;

import java.io.File;
import java.io.IOException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.ConforDirectiveFileParser;

public class CONFOR {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

	    ConforDirectiveFileParser p = ConforDirectiveFileParser.createInstance();
		p.getDirectiveTree().dump();

		String filename = "c:\\delta\\test\\tokey";
		File f = new File(filename);
		if (!f.exists()) {
			Logger.log("File %s does not exist!", filename);
			return;
		}

		DeltaContext context = new DeltaContext();
		
		StringBuilder credits = new StringBuilder("CONFOR version 3.00 (Java)");
		credits.append("\n\nM. J. Dallwitz, T.A. Paine and E.J. Zurcher");
		credits.append("\n\nCSIRO Division of Entomology, GPO Box 1700, Canberra, ACT 2601, Australia\nPhone +61 2 6246 4075. Fax +61 2 6246 4000. Email delta@ento.csiro.au");
		credits.append("\n\nJava edition ported by the Atlas of Living Australia, 2010.\n");
		
		context.setCredits(credits.toString());
		
		Logger.log("%s", context.getCredits());

		p.parse(f, context);
		
	}

}
