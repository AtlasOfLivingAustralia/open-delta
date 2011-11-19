package au.org.ala.delta.delfor;

import java.util.Arrays;

import au.org.ala.delta.editor.directives.DirectivesFileImporter;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * This subclass of DirectivesFileImporter behaves in the same way as it's
 * parent.  The only difference is that some of the CONFOR directives
 * that have reconfigured so that they do not execute some critical directives
 * that should have already been executed.
 */
public class DelforDirectivesFileImporter extends DirectivesFileImporter {
	
	private Directive[] _conforDirArray;
	
	public DelforDirectivesFileImporter(EditorViewModel model, ImportContext context) {
		super(model, context);
		initialiseConforDirectives();
	}

	private void initialiseConforDirectives() {
		_conforDirArray = Arrays.copyOf(ConforDirType.ConforDirArray, ConforDirType.ConforDirArray.length);
		int[] toCopy = {ConforDirType.CHARACTER_TYPES, ConforDirType.MAXIMUM_NUMBER_OF_ITEMS, 
				ConforDirType.MAXIMUM_NUMBER_OF_STATES, ConforDirType.NUMBER_OF_CHARACTERS, 
				ConforDirType.NUMBERS_OF_STATES};
		
		for (int dirId : toCopy) {
			_conforDirArray[dirId] = copyWithoutImplementationClass(_conforDirArray[dirId]);
		}
	}

	@Override
	protected Directive[] directivesOfType(DirectiveType type) {
		Directive[] directives = super.directivesOfType(type);
		if (directives == ConforDirType.ConforDirArray) {
			return _conforDirArray;
		}
		return directives;
	}

	private Directive copyWithoutImplementationClass(Directive directive) {
		return new Directive(directive.getName(), directive.getLevel(), 
				directive.getNumber(), DirectiveArgType.DIRARG_NONE, directive.getInFunc(),
				directive.getOutFunc());
	}
	
}
