package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;

public interface DirectiveArgParser {

	public Dir parse(String directiveArgs);
}
