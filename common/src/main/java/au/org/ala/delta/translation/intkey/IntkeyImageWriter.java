package au.org.ala.delta.translation.intkey;

import java.util.List;

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
			ImageOverlayWriter overlayWriter = createOverlayWriter(buffer);
			for (Image image : images) {
				buffer.append(" "+image.getFileName()).append(" ");
				overlayWriter.writeOverlays(image.getOverlays(), 0, subject);
			}
			imageData = buffer.toString();
			imageData = imageData.replaceAll("\\s", " ");
			
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
			buffer.append(" "+image.getFileName()).append(" ");
			overlayWriter.writeOverlays(image.getOverlays(), 0, null);
		}
		return buffer.toString().replace("\\s", " ");
	}
	
	private ImageOverlayWriter createOverlayWriter(StringBuilder buffer) {
		DeltaWriter writer = new DeltaWriter(buffer);
		return new ImageOverlayWriter(writer);
	}
}
