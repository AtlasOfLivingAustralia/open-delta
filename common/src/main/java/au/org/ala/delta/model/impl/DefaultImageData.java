package au.org.ala.delta.model.impl;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.image.ImageOverlay;

public class DefaultImageData implements ImageData {

	private ArrayList<ImageOverlay> _overlays;
	private String _fileName;
	
	public DefaultImageData(String fileName) {
		_fileName = fileName;
		_overlays = new ArrayList<ImageOverlay>();
	}
	
	@Override
	public List<ImageOverlay> getOverlays() {
		return _overlays;
	}
	
	@Override
	public void setOverlays(List<ImageOverlay> overlays) {
		_overlays = new ArrayList<ImageOverlay>(overlays);
	}


	@Override
	public String getFileName() {
		return _fileName;
	}

	@Override
	public void addOverlay(ImageOverlay overlay) {
		_overlays.add(overlay);
	}

	@Override
	public void updateOverlay(ImageOverlay overlay) {
		
		int id = overlay.getId();
		ImageOverlay toReplace = null;
		for (ImageOverlay tmp : _overlays) {
			if (tmp.getId() == id) {
				toReplace = tmp;
				break;
			}
		}
		if (toReplace == null) {
			throw new RuntimeException("Cannot replace overlay.  No overlay with id "+id+ "exists");
		}
		int index = _overlays.indexOf(toReplace);
		_overlays.set(index, overlay);
	}

	@Override
	public void deleteOverlay(ImageOverlay overlay) {
		_overlays.remove(overlay);
	}

}
