package au.org.ala.delta.editor.directives;

import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.IntegerArg;
import au.org.ala.delta.directives.args.IntegerList;
import au.org.ala.delta.directives.args.TextArg;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;

public class DirectiveArgConverter {

	public Dir fromDirective(AbstractDirective<DeltaContext> directive) {

		Dir dir = new Dir();
		int type = ConforDirType.typeOf(directive);
		dir.setDirType(type);

		PopulateArgs populateArgs = argsPopulatorFor(directive);
		populateArgs.populateArgs(dir, directive);

		return dir;
	}

	interface PopulateArgs {
		public void populateArgs(Dir dir,
				AbstractDirective<DeltaContext> directive);
	}

	class GetTextArgs implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, AbstractDirective<DeltaContext> directive) {

			DirectiveArgs args = directive.getDirectiveArgs();
			String text = ((TextArg) args).getText();
			
			dir.resizeArgs(1);
			dir.args.get(0).text = text;

		}
	}
	
	class GetNumberArg implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, AbstractDirective<DeltaContext> directive) {

			DirectiveArgs args = directive.getDirectiveArgs();
			int value = ((IntegerArg) args).getValue();

			dir.resizeArgs(1);
			dir.args.get(0).setValue(value);

		}
	}
	
	class GetNumberList implements PopulateArgs {
		@Override
		public void populateArgs(Dir dir, AbstractDirective<DeltaContext> directive) {
			DirectiveArgs args = directive.getDirectiveArgs();
			List<Integer> values = ((IntegerList) args).getArgList();
			for (int value : values) {
				dir.args.add(new DirArgs(value));
			}
		}
	}

	class NoArgs implements PopulateArgs {
		public void populateArgs(Dir dir,
				AbstractDirective<DeltaContext> directive) {
		}
	}
 
	
	private PopulateArgs argsPopulatorFor(AbstractDirective<DeltaContext> directive) {
		int argType = directive.getArgType();
		switch (argType) {
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_INTERNAL: // Not sure about this - existing code treats it like text.
			return new NoArgs();
		case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
		case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be
											// preserved?
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_OTHER:
		
			return new GetTextArgs();
		
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_REAL:
			return new GetNumberArg();
			
		case DirectiveArgType.DIRARG_CHAR:
		case DirectiveArgType.DIRARG_ITEM:
			break;
		case DirectiveArgType.DIRARG_CHARLIST:
		case DirectiveArgType.DIRARG_ITEMLIST:

			break;

		case DirectiveArgType.DIRARG_TEXTLIST:
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
			break;
		case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		case DirectiveArgType.DIRARG_CHARREALLIST:
		case DirectiveArgType.DIRARG_ITEMREALLIST:

			break;

		case DirectiveArgType.DIRARG_CHARGROUPS:

			break;

		case DirectiveArgType.DIRARG_ITEMCHARLIST:

			break;

		case DirectiveArgType.DIRARG_ALLOWED:

			break;

		case DirectiveArgType.DIRARG_KEYSTATE:

			break;

		case DirectiveArgType.DIRARG_PRESET:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ONOFF:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ITEM:

			break;

		case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
			break;
		case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:

			break;

		case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:

			break;

		case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
			break;
		
			
		}
		return new NoArgs();
	}
}
