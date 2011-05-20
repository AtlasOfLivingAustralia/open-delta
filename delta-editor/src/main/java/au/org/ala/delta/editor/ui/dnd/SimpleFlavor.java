package au.org.ala.delta.editor.ui.dnd;

import java.awt.datatransfer.DataFlavor;

/**
 * Overrides DataFlavor to allow comparisons to succeed if one flavor is a parent class of
 * the other.
 * (This is necessary because our TransferHandler deals with the Character class but specific
 * dragged Characters are subclasses of Character).
 */
public class SimpleFlavor extends DataFlavor {

	public SimpleFlavor(Class<?> flavourClass, String className) {
		super(flavourClass, className);
	}
	
	public boolean equals(DataFlavor flavour) {
		return getRepresentationClass().isAssignableFrom(flavour.getRepresentationClass());
	}
}
