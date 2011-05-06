package au.org.ala.delta.editor;

public interface DeltaView {

	public String getViewTitle();
	public void open();
	public void close();
	public boolean isValid();
}
