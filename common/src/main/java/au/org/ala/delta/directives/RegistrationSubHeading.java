package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.DeltaContext.HeadingType;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class RegistrationSubHeading extends AbstractTextDirective {


	public RegistrationSubHeading() {
		super("registration", "subheading");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		String data = directiveArguments.getFirstArgumentText();
		
		String heading = replaceVariables(context, data.trim());
		context.setHeading(HeadingType.REGISTRATION_SUBHEADING, heading);
		Logger.log("REGISTRATION SUBHEADING: %s", heading);

	}

}
