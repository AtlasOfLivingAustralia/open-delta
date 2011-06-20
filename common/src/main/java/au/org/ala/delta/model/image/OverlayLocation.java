package au.org.ala.delta.model.image;


public class OverlayLocation {
	public OLDrawType drawType = OLDrawType.Unknown;
	public int flags;
	public int ID;
	public short X;
	public short Y;
	public short W;
	public short H;

	

	private static final int OLOC_FLAG_COLOUR_MASK = 0x00ffffff;
	private static final int OLOC_FLAG_POPUP    =   0x02000000;
	private static final int OLOC_FLAG_COLOUR   =   0x04000000;
	
	public void clearAll() {
		drawType = OLDrawType.Unknown;
		ID = flags = 0;
		X = Y = W = H = 0;
	}

	@Override
	public String toString() {
		return String.format("OverlayLoc: drawType=%s, flags=%d, ID=%d, X=%d, Y=%d, W=%d, H=%d", drawType, flags, ID, X, Y, W, H);
	}
	
	public boolean colorSet() {
		return (flags & OLOC_FLAG_COLOUR) > 0;
	}
	
	public boolean isPopup() {
		return (flags & OLOC_FLAG_POPUP) > 0;
	}
	
	public int getColor() {
		// not sure why it's not rgb
		int bgr = (flags & OLOC_FLAG_COLOUR_MASK);
		int b = (bgr & 0xff0000) >> 16;
		int g = bgr & 0x00ff00;
		int r = (bgr & 0x0000ff) << 16;
		
		return r | g | b;
	}


	public static enum OLDrawType {
		Unknown, frame, rectangle, line, arrow, ellipse;
	
		public static OLDrawType fromOrdinal(int ord) {
			return values()[ord];
		}
	}
}