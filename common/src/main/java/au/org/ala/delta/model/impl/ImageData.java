package au.org.ala.delta.model.impl;

import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;

public interface ImageData {

	public List<ImageOverlay> getOverlays();

	public String getFileName();
}
