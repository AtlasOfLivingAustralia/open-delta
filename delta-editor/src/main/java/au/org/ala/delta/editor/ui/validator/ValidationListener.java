package au.org.ala.delta.editor.ui.validator;

public interface ValidationListener {

	public void validationSuceeded(ValidationResult results);
	public void validationFailed(ValidationResult results);
	
}
