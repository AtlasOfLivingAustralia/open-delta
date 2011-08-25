package au.org.ala.delta.ui.codeeditor;

import java.util.EventObject;

public class CaretPositionEvent extends EventObject {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    /** The vertical position. */
    private int row;
    /** The horizontal position. */
    private int column;

    /**
     * Constructs a CaretPositionEvent instance with specific attributes.
     *
     * @param source
     *            The source of the event.
     * @param row
     *            The new caret row.
     * @param column
     *            The new caret column.
     */
    public CaretPositionEvent(Object source, int row, int column) {
        super(source);
        this.row = row;
        this.column = column;
    }

    /**
     * Gets the vertical position.
     *
     * @return The vertical position.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the horizontal position.
     *
     * @return The horizontal position.
     */
    public int getColumn() {
        return column;
    }
}
