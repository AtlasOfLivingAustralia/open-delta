package au.org.ala.delta.editor.slotfile;

import au.org.ala.delta.directives.args.DirectiveArguments;

/** 
 * Represents a specific instance of a stored directive, complete with
 * arguments.
 */
public class DirectiveInstance {

	private Directive _directive;
	private DirectiveArguments _args;
	private boolean _commented;
	
	public DirectiveInstance(Directive directive, DirectiveArguments args) {
		_directive = directive;
		_args = args;
	}
	
	public Directive getDirective() {
		return _directive;
	}
	
	public DirectiveArguments getDirectiveArguments() {
		return _args;
	}
	
	public void setCommented(boolean commented) {
		_commented = commented;
	}
	
	public boolean isCommented() {
		return _commented;
	}
}
