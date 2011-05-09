package au.org.ala.delta.editor;

/**
 * Provides information about changes to the state of a DeltaView.
 */
public interface DeltaViewStatusObserver {
	/**
	 * Called when a view is closed.
	 * @param controller the controller managing the view.
	 * @param view the view that has closed.
	 */
	public void viewClosed(DeltaViewController controller, DeltaView view);
	
	/**
	 * Called when a view is selected/focused.
	 * @param controller the controller managing the view.
	 * @param view the view that has been selected.
	 */
	public void viewSelected(DeltaViewController controller, DeltaView view);
}
