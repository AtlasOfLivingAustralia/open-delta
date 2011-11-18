package au.org.ala.delta.delfor.directives;

import au.org.ala.delta.directives.NoopDirective;

/**
 * The CONTROL PHRASES directive is not required, directive types
 * are defined in ConforDirType and IntkeyDirType. 
 */
public class ControlPhrases extends NoopDirective {

	public ControlPhrases() {
		super("control", "phrases");
	}
	
	@Override
	public int getOrder() {
		return 5;
	}
}
