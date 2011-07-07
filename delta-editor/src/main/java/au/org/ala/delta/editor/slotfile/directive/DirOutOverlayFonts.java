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

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.editor.slotfile.VOImageInfoDesc.OverlayFontType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.image.ImageSettings;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;

public class DirOutOverlayFonts extends AbstractDirOutFunctor {

	@Override
	public void writeDirectiveArguments(DirectiveInOutState state) {
		outputTextBuffer(0, 0, true);
		
		DeltaDataSet dataSet = state.getDataSet();

		ImageSettings settings = dataSet.getImageSettings();
		if (settings != null) {

			for (OverlayFontType fontType : OverlayFontType.values()) {
				int indent = 2;
				_textBuffer.append("#").append(fontType.ordinal() + 1).append(". ");
				FontInfo fontInfo;
				if (fontType == OverlayFontType.OF_DEFAULT) {
					fontInfo = settings.getDefaultFontInfo();
				}
				else if (fontType == OverlayFontType.OF_FEATURE) {
					fontInfo = settings.getDefaultFeatureFontInfo();
				}
				else {
					fontInfo = settings.getDefaultButtonFontInfo();
				}
				
				String comment = fontInfo.comment;
				if (StringUtils.isNotEmpty(comment)) {
					_textBuffer.append(" <");
					_textBuffer.append(comment);
					_textBuffer.append('>');
					outputTextBuffer(indent, indent, true);
					indent = 10;
				}
				if (StringUtils.isNotEmpty(fontInfo.name)) {

					String buffer = String.format("%d %d %d %d %d %d %s",
							fontInfo.size,
							fontInfo.weight, 
							fontInfo.italic ? 1 : 0, 
							fontInfo.pitch,
							fontInfo.family,
							fontInfo.charSet, fontInfo.name);
					_textBuffer.append(buffer);
					outputTextBuffer(indent, 10, true);
				} else
					_textBuffer = new StringBuilder();
			}
		}
	}

}
