package au.org.ala.delta.gui.validator;

import javax.swing.JComponent;

import au.org.ala.delta.ui.rtf.RtfEditor;

/**
 * Invokes the correct method on the RtfEditor to return the RTF formatted text instead of plain text.
 */
public class RtfEditorValidator extends TextComponentValidator {

	public RtfEditorValidator(Validator validator, ValidationListener listener) {
		super(validator, listener);
	}
	
	public Object getValueToValidate(JComponent component) {
		return ((RtfEditor)component).getRtfTextBody();
	}
}
