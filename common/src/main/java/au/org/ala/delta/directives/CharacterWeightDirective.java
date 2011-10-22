package au.org.ala.delta.directives;

import java.math.BigDecimal;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;


/**
 * Parent class for the CharacterReliabilities and CharacterWeight directives as these directives
 * perform very similar functions.
 */
public abstract class CharacterWeightDirective extends AbstractCharacterListDirective<DeltaContext, String>{

	/** The minimum value any character weight is allowed to have */
	private double _minimumWeight;
	
	/** The maximum value any character weight is allowed to have */
	private double _maximumWeight;
	
	/** The default value for unspecified weights */
	private BigDecimal _defaultWeight;
	
	
	public CharacterWeightDirective(double minimumWeight, double maximumWeight, BigDecimal defaultWeight, String... controlWords) {
		super(controlWords);
		_minimumWeight = minimumWeight;
		_maximumWeight = maximumWeight;
		_defaultWeight = defaultWeight;
	}
	
	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_CHARREALLIST;
	}
	
	/**
	 * Initialises character weights to 5.0 before invoking the super class processing.
	 */
	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		setDefaults(context);
		
		super.process(context, args);
	}
	
	protected void setDefaults(DeltaContext context) {
		for (int i=1; i<=context.getNumberOfCharacters(); i++) {
			context.setCharacterWeight(i, _defaultWeight);
		}
	}

	@Override
	protected String interpretRHS(DeltaContext context, String weight) {
		return weight.trim();
	}

	@Override
	protected void processCharacter(DeltaContext context, int charIndex, String weightStr) {
		
		BigDecimal weight = new BigDecimal(weightStr);
		if (weight.doubleValue() < _minimumWeight) {
			throw new IllegalArgumentException("The weight must be greater than "+ _minimumWeight);
		}
		if (weight.doubleValue() > _maximumWeight) {
			throw new IllegalArgumentException("The weight must be less than "+ _maximumWeight);
		}
		context.setCharacterWeight(charIndex, weight);
	}
	
	@Override
	protected void addArgument(DirectiveArguments args, int charIndex, String value) {
		
		args.addNumericArgument(charIndex, value);	
	}
}
