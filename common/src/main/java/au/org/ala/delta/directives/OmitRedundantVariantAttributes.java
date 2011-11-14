package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the OMIT REDUNDANT VARIANT ATTRIBUTES directive.
 */
public class OmitRedundantVariantAttributes extends AbstractNoArgDirective {

	public OmitRedundantVariantAttributes() {
		super("omit", "redundant", "variant", "attributes");
	}
	
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.setOmitRedundantVariantAttributes(false);
	}

	@Override
	public int getOrder() {
		return 4;
	}
}
