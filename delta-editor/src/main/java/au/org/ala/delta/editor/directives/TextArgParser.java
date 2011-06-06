package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;

public class TextArgParser implements DirectiveArgParser {

	@Override
	public Dir parse(String directiveArgs) {
		Dir directive = new Dir();
		directive.resizeArgs(1);
		directive.args.get(0).text = directiveArgs;
		
		return directive;
	}
}
