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

import java.util.List;

import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.util.Utils;

/**
 * Exports the TAXON IMAGES directive.
 */
public class DirOutTaxonImages extends DirOutImageOverlay {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {

		DeltaDataSet dataSet = state.getDataSet();

		for (int i = 1; i <= dataSet.getMaximumNumberOfItems(); ++i) {
			Item item = dataSet.getItem(i);

			if (item.getImageCount() == 0)
				continue;
			String description = item.getDescription();

			_textBuffer.append("\n");
			outputTextBuffer(0, 0, true);
			_textBuffer.append("# ");
			_textBuffer.append(Utils.despaceRtf(description, true));
			_textBuffer.append('/');
			outputTextBuffer(0, 0, true);

			List<Image> imageList = item.getImages();
			for (int j = 0; j < imageList.size(); j++) {
				Image image = imageList.get(j);
				if (!image.getSubject().equals(item)) {
					throw new RuntimeException("TDirInOutEx(ED_INTERNAL_ERROR");
				}
				_textBuffer.append(image.getFileName());
				outputTextBuffer(5, 2, true);

				writeOverlays(image.getOverlays(), 10, image.getSubject());
			}
		}
	}

}
