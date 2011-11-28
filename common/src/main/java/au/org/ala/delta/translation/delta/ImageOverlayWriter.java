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
package au.org.ala.delta.translation.delta;

import java.util.Formatter;
import java.util.List;

import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Illustratable;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.OverlayLocation;
import au.org.ala.delta.model.image.OverlayLocation.OLDrawType;
import au.org.ala.delta.model.image.OverlayType;
import au.org.ala.delta.util.Utils;

/**
 * Base class for exporters of directives that work with images and image
 * overlays.
 */
public class ImageOverlayWriter {

	private DeltaWriter _deltaWriter;
	public ImageOverlayWriter(DeltaWriter writer) {
		_deltaWriter = writer;
	}
	
	
	/**
	 * Outputs the supplied List of ImageOverlays to the Printer provided by the
	 * state.
	 * 
	 * @param overlayList
	 *            the overlays to output.
	 * @param startIndent
	 *            the indent to use when outputting the first overlay.
	 * @param subject
	 *            the subject of the image overlays. May be null if the list
	 *            does not contain value or state overlays.
	 */
	public void writeOverlays(List<ImageOverlay> overlayList,
			int startIndent, Illustratable subject) {
		
		OverlayLocation olLoc;
		StringBuilder textBuffer = new StringBuilder();
		for (ImageOverlay overlay : overlayList) {
			int indent = startIndent;
			int curType = overlay.type;

			textBuffer.append('<');
			if (curType != OverlayType.OLCOMMENT) {
				textBuffer.append('@');
				textBuffer.append(OverlayType.keywordFromType(curType));
				textBuffer.append(' ');
			}

			// These types have only simple text, with no location information
			if (curType == OverlayType.OLSUBJECT
					|| curType == OverlayType.OLSOUND
					|| curType == OverlayType.OLCOMMENT) {

				String text = overlay.overlayText;
				if (curType == OverlayType.OLCOMMENT || curType == OverlayType.OLSUBJECT) {
					text = Utils.despaceRtf(text, true);
				}
				textBuffer.append(text);
				textBuffer.append('>');
				_deltaWriter.outputTextBuffer(textBuffer.toString(), indent, indent + 5, true);
				textBuffer = new StringBuilder();
				continue;
			}

			// All remaining types MUST have a location
			if (overlay.location.size() == 0)
				throw new RuntimeException("TDirInOutEx(ED_INTERNAL_ERROR)");
			olLoc = overlay.location.get(0);

			// Keyword, value, and state have arguments before the positioning
			// information
			if (curType == OverlayType.OLKEYWORD) {
				textBuffer.append('\"');
				textBuffer.append(overlay.keywords);
				textBuffer.append('\"');
				textBuffer.append(' ');
			} else if (curType == OverlayType.OLVALUE) {
				Character character = (Character) subject;
				if (!character.getCharacterType().isNumeric())
					throw new RuntimeException("TDirInOutEx(ED_INTERNAL_ERROR)");

				textBuffer.append(overlay.getValueString());
				textBuffer.append(' ');
			} else if (curType == OverlayType.OLSTATE) {
				Character character = (Character) subject;
				if (!character.getCharacterType().isMultistate())
					throw new RuntimeException("ED_INTERNAL_ERROR)");

				if (overlay.stateId <= 0)
					throw new RuntimeException("ED_INTERNAL_ERROR)");
				textBuffer.append(overlay.stateId);
				textBuffer.append(' ');
			}

			int xIndent = startIndent + textBuffer.length();

			if (overlay.comment.length() > 0) {
				textBuffer.append('<');

				textBuffer.append(Utils.despaceRtf(overlay.comment, true));
				textBuffer.append("> ");
			}

			textBuffer.append("x=");
			if (curType == OverlayType.OLUNITS && olLoc.X == Short.MIN_VALUE)
				textBuffer.append('~');
			else
				textBuffer.append(olLoc.X);
			textBuffer.append(" y=");
			if (curType == OverlayType.OLUNITS && olLoc.Y == Short.MIN_VALUE)
				textBuffer.append('~');
			else
				textBuffer.append(olLoc.Y);
			// These button types have only x and y co-ordinates.
			if (curType == OverlayType.OLOK || curType == OverlayType.OLCANCEL
					|| curType == OverlayType.OLNOTES) {
				textBuffer.append('>');
				_deltaWriter.outputTextBuffer(textBuffer.toString(), indent, indent, true);
				textBuffer = new StringBuilder();
				continue;
			}

			if (curType != OverlayType.OLIMAGENOTES) {
				textBuffer.append(" w=");
				textBuffer.append(olLoc.W);
				textBuffer.append(" h=");
				textBuffer.append(olLoc.H);
			}

			// Output hotspot information
			int tmpIndent = indent;
			for (int loc = 1; loc<overlay.location.size(); loc++) {
				OverlayLocation hsLoc = overlay.location.get(loc);
				_deltaWriter.outputTextBuffer(textBuffer.toString(), tmpIndent, tmpIndent, true);
				textBuffer = new StringBuilder();
				tmpIndent = xIndent;

				textBuffer.append(" x=").append(hsLoc.X);
				textBuffer.append(" y=").append(hsLoc.Y);
				textBuffer.append(" w=").append(hsLoc.W);
				textBuffer.append(" h=").append(hsLoc.H);
				if (hsLoc.drawType == OLDrawType.ellipse)
					textBuffer.append(" e");
				if (hsLoc.isPopup())
					textBuffer.append(" p");
				if (hsLoc.isColorSet()) {
					textBuffer.append(" f=");
					Formatter formatter = new Formatter(textBuffer);
					formatter.format("%06X", hsLoc.getColorAsBGR());
				}
			}

			// Output other flags
			if (overlay.omitDescription())
				textBuffer.append(" n");
			if (overlay.includeComments())
				textBuffer.append(" c");
			if (overlay.centreText())
				textBuffer.append(" m");

			boolean hasText = false;
			if (overlay.overlayText.length() > 0) {
				_deltaWriter.outputTextBuffer(textBuffer.toString(), tmpIndent, indent, true);
				textBuffer = new StringBuilder();
				textBuffer.append(" t=");
				textBuffer.append(Utils.despaceRtf(overlay.overlayText, true));
				hasText = true;
			}
			if (hasText) {
				tmpIndent = indent +1; 
			}
			textBuffer.append('>');
			_deltaWriter.outputTextBuffer(textBuffer.toString(), tmpIndent, indent, true);
			textBuffer = new StringBuilder();
		}

	}
	
}
