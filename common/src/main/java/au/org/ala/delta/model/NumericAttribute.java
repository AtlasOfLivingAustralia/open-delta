package au.org.ala.delta.model;

import java.util.List;

import au.org.ala.delta.model.impl.AttributeData;

public abstract class NumericAttribute extends Attribute {

	public NumericAttribute(Character character, AttributeData impl) {
		super(character, impl);
	}

	public List<NumericRange> getNumericValue() {
		return _impl.getNumericValue();
	}

}