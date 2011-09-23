package au.org.ala.delta.translation.delta;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageSettings.OverlayFontType;

public class OverlayFontWriter {
	private DeltaWriter _deltaWriter;
	public OverlayFontWriter(DeltaWriter writer) {
		_deltaWriter = writer;
	}
	
	public void writeFont(FontInfo fontInfo, OverlayFontType fontType) {
		int indent = 2;
		
		writeId(fontType);
		
		String comment = fontInfo.comment;
		if (StringUtils.isNotEmpty(comment)) {
			writeComment(indent, comment);
			indent = 10;
		}
		if (StringUtils.isNotEmpty(fontInfo.name)) {

			writeFontInfo(fontInfo, indent);
		}
		
	}

	public void writeFontInfo(FontInfo fontInfo, int indent) {
		StringBuilder textBuffer = new StringBuilder();
		String buffer = String.format("%d %d %d %d %d %d %s",
				fontInfo.size,
				fontInfo.weight, 
				fontInfo.italic ? 1 : 0, 
				fontInfo.pitch,
				fontInfo.family,
				fontInfo.charSet, fontInfo.name);
		textBuffer.append(buffer);
		_deltaWriter.outputTextBuffer(textBuffer.toString(), indent, 10, true);
	}

	private void writeComment(int indent, String comment) {
		StringBuilder textBuffer = new StringBuilder(); 
		textBuffer.append(" <");
		textBuffer.append(comment);
		textBuffer.append('>');
		_deltaWriter.outputTextBuffer(textBuffer.toString(), indent, indent, true);
	}

	private void writeId(OverlayFontType fontType) {
		StringBuilder textBuffer = new StringBuilder(); 
		textBuffer.append("#").append(fontType.ordinal() + 1).append(". ");
		_deltaWriter.outputTextBuffer(textBuffer.toString(), 0, 0, false);
	}
}
