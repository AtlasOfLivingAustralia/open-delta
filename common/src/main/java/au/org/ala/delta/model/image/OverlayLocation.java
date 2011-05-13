package au.org.ala.delta.model.image;


public class OverlayLocation {
	public OLDrawType drawType = OLDrawType.Unknown;
	public int flags;
	public int ID;
	public short X;
	public short Y;
	public short W;
	public short H;

	
	public void clearAll() {
		drawType = OLDrawType.Unknown;
		ID = flags = 0;
		X = Y = W = H = 0;
	}

	@Override
	public String toString() {
		return String.format("OverlayLoc: drawType=%s, flags=%d, ID=%d, X=%d, Y=%d, W=%d, H=%d", drawType, flags, ID, X, Y, W, H);
	}


	public static enum OLDrawType {
		Unknown, frame, rectangle, line, arrow, ellipse;
	
		public static OLDrawType fromOrdinal(int ord) {
			return values()[ord];
		}
	}
}