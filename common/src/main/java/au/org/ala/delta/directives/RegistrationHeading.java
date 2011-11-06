package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class RegistrationHeading extends AbstractTextDirective {


	public RegistrationHeading() {
		super("registration", "heading");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String data = directiveArguments.getFirstArgumentText();
		
		String heading = replaceVariables(context, data.trim());
		context.setHeading(HeadingType.REGISTRATION_HEADING, heading);
		Logger.log("REGISTRATION HEADING: %s", heading);

	}

}
