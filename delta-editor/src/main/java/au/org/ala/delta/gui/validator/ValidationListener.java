package au.org.ala.delta.gui.validator;

public interface ValidationListener {

	public void validationSuceeded(ValidationResult results);
	public void validationFailed(ValidationResult results);
	
}
