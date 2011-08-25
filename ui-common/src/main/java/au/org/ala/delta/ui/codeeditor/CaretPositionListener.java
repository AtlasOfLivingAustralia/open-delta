package au.org.ala.delta.ui.codeeditor;

public interface CaretPositionListener {

    /**
     * Invoked when the caret position needs to get reported.
     *
     * @param evt
     *            a CaretPositionEvent object
     */
    void positionUpdate(CaretPositionEvent evt);
}
