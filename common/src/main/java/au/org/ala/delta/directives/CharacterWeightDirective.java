package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;


/**
 * Parent class for the CharacterReliabilities and CharacterWeight directives as these directives
 * perform very similar functions.
 */
public abstract class CharacterWeightDirective extends AbstractCharacterListDirective<Double>{

	/** The minimum value any character weight is allowed to have */
	private double _minimumWeight;
	
	/** The maximum value any character weight is allowed to have */
	private double _maximumWeight;
	
	/** The default value for unspecified weights */
	private double _defaultWeight;
	
	
	public CharacterWeightDirective(double minimumWeight, double maximumWeight, double defaultWeight, String... controlWords) {
		super(controlWords);
		_minimumWeight = minimumWeight;
		_maximumWeight = maximumWeight;
		_defaultWeight = defaultWeight;
	}
	
	/**
	 * Initialises character weights to 5.0 before invoking the super class processing.
	 */
	@Override
	public void process(DeltaContext context, String data) throws Exception {
		
		for (int i=1; i<=context.getNumberOfCharacters(); i++) {
			context.setCharacterWeight(i, _defaultWeight);
		}
		
		super.process(context, data);
	}

	@Override
	protected Double interpretRHS(DeltaContext context, String weight) {
		return Double.parseDouble(weight);
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, Double weight) {
		
		if (weight < _minimumWeight) {
			throw new IllegalArgumentException("The weight must be greater than "+ _minimumWeight);
		}
		if (weight > _maximumWeight) {
			throw new IllegalArgumentException("The weight must be less than "+ _maximumWeight);
		}
		context.setCharacterWeight(charIndex, weight);
	}
}
