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
package au.org.ala.delta.translation.intkey;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageInfo;
import au.org.ala.delta.translation.delta.DeltaWriter;
import au.org.ala.delta.translation.delta.ImageOverlayWriter;

public class IntkeyImageWriter {

	public String imagesToString(List<Image> images, Illustratable subject) {
		String imageData = "";
		if (!images.isEmpty()) {
	
			StringBuilder buffer = new StringBuilder();
			
			for (Image image : images) {
				if (buffer.length() > 0) {
					buffer.append(" ");
				}
				buffer.append(image.getFileName());
				StringBuilder overlayTextBuilder = new StringBuilder();
				ImageOverlayWriter overlayWriter = createOverlayWriter(overlayTextBuilder);
				overlayWriter.writeOverlays(image.getOverlays(), 0, subject);
				String overlayText = overlayTextBuilder.toString().replaceAll("\\s+", " ").trim();
				if (StringUtils.isNotEmpty(overlayText)) {
					buffer.append(" ");
					buffer.append(overlayText);
				}
			}
			imageData = buffer.toString();
		}
		return imageData;
	}
	
	public String imagesToString(List<ImageInfo> images) {
		StringBuilder buffer = new StringBuilder();
		ImageOverlayWriter overlayWriter = createOverlayWriter(buffer);
		for (ImageInfo image : images) {
			if (buffer.length() > 0) {
				buffer.append(" ");
			}
			buffer.append(image.getFileName()).append(" ");
			overlayWriter.writeOverlays(image.getOverlays(), 0, null);
		}
		return buffer.toString().replace("\\s", " ");
	}
	
	private ImageOverlayWriter createOverlayWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new ImageOverlayWriter(writer);
	}
}
