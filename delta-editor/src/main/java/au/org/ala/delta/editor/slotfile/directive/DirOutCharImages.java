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

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.image.Image;

/**
 * Exports the CHARACTER IMAGES directive.
 */
public class DirOutCharImages extends DirOutImageOverlay {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {

		int fileIndent;
		MutableDeltaDataSet dataSet = state.getDataSet();

		for (int i = 1; i <= dataSet.getNumberOfCharacters(); ++i) {
			Character character = dataSet.getCharacter(i);

			if (character.getImageCount() == 0)
				continue;

			outputTextBuffer(0, 10, true);

			_textBuffer.append("#").append(i).append(". ");
			fileIndent = _textBuffer.length();

			List<Image> imageList = character.getImages();

			for (int j = 0; j < imageList.size(); j++) {

				Image image = imageList.get(j);
				if (!image.getSubject().equals(character)) {
					throw new RuntimeException("TDirInOutEx(ED_INTERNAL_ERROR");
				}
				_textBuffer.append(image.getFileName());

				outputTextBuffer(j == 0 ? 0 : fileIndent, 2, true);

				writeOverlays(image.getOverlays(), fileIndent + 5, image.getSubject());
			}
		}
	}

}
