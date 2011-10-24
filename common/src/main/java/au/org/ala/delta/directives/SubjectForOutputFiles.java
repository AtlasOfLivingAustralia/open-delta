package au.org.ala.delta.directives;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArguments;

public class SubjectForOutputFiles extends AbstractTextDirective {

	public SubjectForOutputFiles() {
		super("subject", "for", "output", "files");
	}
	@Override
	public void process(DeltaContext context, DirectiveArguments directiveArguments) throws Exception {
		context.getOutputFileSelector().setSubjectForOutputFiles(directiveArguments.getFirstArgumentText().trim());
	}

}
