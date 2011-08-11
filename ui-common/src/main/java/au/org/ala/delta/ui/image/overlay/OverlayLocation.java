package au.org.ala.delta.ui.image.overlay;

import java.awt.Rectangle;

public interface OverlayLocation {

	public int getX();

	public int getY();

	public int getHeight();

	public int getWidth();
	
	public void updateLocationFromBounds(Rectangle bounds);

}