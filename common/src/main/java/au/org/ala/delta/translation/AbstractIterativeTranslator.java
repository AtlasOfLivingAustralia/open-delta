package au.org.ala.delta.translation;

import au.org.ala.delta.directives.OutputParameters.OutputParameter;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.attribute.CommentedValueList.Values;

public abstract class AbstractIterativeTranslator implements IterativeTranslator {
	
	@Override
	public void beforeFirstItem() {};

	@Override
	public void beforeItem(Item item) {};

	@Override
	public void afterItem(Item item) {};

	@Override
	public void beforeAttribute(Attribute attribute) {};

	@Override
	public void afterAttribute(Attribute attribute) {};

	@Override
	public void afterLastItem() {};

	@Override
	public void attributeComment(String comment) {};

	@Override
	public void attributeValues(Values values) {};
	
	@Override
	public void beforeFirstCharacter() {};
	
	@Override
	public void beforeCharacter(Character character) {};
	
	@Override
	public void afterCharacter(Character character) {};
	
	@Override
	public void afterLastCharacter() {};
	
	@Override
	public void translateOutputParameter(OutputParameter parameterName) {};
}
