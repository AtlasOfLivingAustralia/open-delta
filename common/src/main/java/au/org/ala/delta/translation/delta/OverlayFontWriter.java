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
		StringBuilder _textBuffer = new StringBuilder();
		_textBuffer.append("#").append(fontType.ordinal() + 1).append(". ");
		
		
		String comment = fontInfo.comment;
		if (StringUtils.isNotEmpty(comment)) {
			_textBuffer.append(" <");
			_textBuffer.append(comment);
			_textBuffer.append('>');
			_deltaWriter.outputTextBuffer(_textBuffer.toString(), indent, indent, true);
			_textBuffer = new StringBuilder();
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
			_deltaWriter.outputTextBuffer(_textBuffer.toString(),indent, 10, true);
			_textBuffer = new StringBuilder();
		} else
			_textBuffer = new StringBuilder();
	}
}
