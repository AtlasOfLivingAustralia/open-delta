package au.org.ala.delta.directives;

public interface DirectiveVisitor<T extends AbstractDeltaContext> {
	
	void visit(AbstractDirective<T> directive);

}
