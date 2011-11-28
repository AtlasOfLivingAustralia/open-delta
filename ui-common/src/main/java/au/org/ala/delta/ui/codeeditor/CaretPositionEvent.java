/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
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
