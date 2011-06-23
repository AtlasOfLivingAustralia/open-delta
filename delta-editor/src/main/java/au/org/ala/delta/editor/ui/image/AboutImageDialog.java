package au.org.ala.delta.editor.ui.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.JDialog;

public class AboutImageDialog extends JDialog {

	private static final long serialVersionUID = 3136873729289853125L;
	private int _width;
	private int _height;
	private String _type;
	private int _numColours;
	private String _fileName;
	private String _caption;
	
	
	public AboutImageDialog(JComponent parent, String caption, URL imagePath, BufferedImage image, String imageType) {
		_caption = caption;
		_fileName = imagePath.getFile();
		_width = image.getWidth();
		_height = image.getHeight();
		_type = imageType;
		countColours(image);
		
		dump();
	}
	
	private void dump() {
		System.out.print("Width: "+_width+", Height: "+_height+", type: "+_type);
		System.out.print(", colours: "+_numColours+", Filename: "+_fileName+", caption: "+_caption);
	}
	
	/** There is probably a better way to do this */
	private void countColours(BufferedImage image) {
		Set<Integer> colours = new HashSet<Integer>();
		
		for (int x=0; x<_width; x++) {
			for (int y=0; y<_height; y++) {
				colours.add(image.getRGB(x, y));
			}
		}
		
		_numColours = colours.size();
		
	}
	
	/** Determine type */
	private void determineType(URL fileName) {
		try {
			ImageInputStream iis = ImageIO.createImageInputStream(fileName);
			// Find all image readers that recognize the image format
	        Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
	        if (!iter.hasNext()) {
	            // No readers found
	            return;
	        }

	        // Use the first reader
	        ImageReader reader = (ImageReader)iter.next();

	        // Close stream
	        iis.close();

	        // Return the format name
	        _type = reader.getFormatName();
		}
		catch (Exception e) {e.printStackTrace();}
	}
}
