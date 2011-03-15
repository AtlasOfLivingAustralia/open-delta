package au.org.ala.delta.ui.validator;

public interface ValidationListener {

	public void validationSuceeded(ValidationResult results);
	public void validationFailed(ValidationResult results);
	
}
