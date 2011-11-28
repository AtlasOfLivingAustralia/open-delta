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
package au.org.ala.delta.editor.slotfile.directive;

import java.text.ParseException;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.TaxonImages;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.util.DataSetHelper;

public class DirInTaxonImages extends TaxonImages {

	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws ParseException {
		super.process(context, directiveArguments);
		
		DataSetHelper helper = new DataSetHelper(context.getDataSet());
		helper.addItemImages(context.getImages(ImageType.IMAGE_TAXON));
	}

}
