package au.org.ala.delta.editor.directives;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.directives.AbstractDeltaContext;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveArgType;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.Dir;
import au.org.ala.delta.editor.slotfile.VODirFileDesc.DirArgs;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;

/**
 * Creates and populates a Dir object from the arguments supplied to a AbstractDirective.
 */
public class DirectiveArgConverter {

	
	private ItemDescriptionConverter _itemDescriptionConverter = new ItemDescriptionConverter();
	private CharacterNumberConverter _characterNumberConverter = new CharacterNumberConverter();
	private ItemNumberConverter _itemNumberConverter = new ItemNumberConverter();
	
	private DeltaVOP _vop;
	public DirectiveArgConverter(DeltaVOP vop) {
		_vop = vop;
	}
	
	public Dir fromDirective(AbstractDirective<? extends AbstractDeltaContext> directive) {

		Dir dir = new Dir();
		Directive directiveDescription = ConforDirType.typeOf(directive);
		dir.setDirType(directiveDescription.getNumber());

		populateArgs(dir, directive.getDirectiveArgs(), directive.getArgType());

		return dir;
	}
	
	private void populateArgs(Dir dir, DirectiveArguments args, int directiveType) {
		if (args == null || directiveType == DirectiveArgType.DIRARG_INTERNAL) {
			dir.resizeArgs(0);
		}
		else {
			for (DirectiveArgument<?> arg : args.getDirectiveArguments()) {
				IdConverter converter = idConverterFor(directiveType);
				DirArgs dirArg = new DirArgs(converter.convertId(arg.getId()));
				dirArg.setText(arg.getText());
				dirArg.comment = arg.getComment();
				
				dir.args.add(dirArg);
			}
		}
		
	}

 
	interface IdConverter {
		public int convertId(Object id);
	}
	
	class CharacterNumberConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return _vop.getDeltaMaster().uniIdFromCharNo((Integer)id);
		}
	}
	
	class ItemNumberConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return _vop.getDeltaMaster().uniIdFromItemNo((Integer)id);
		}
	}
	
	class ItemDescriptionConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			throw new NotImplementedException();
		}
	}
	
	class DirectConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return (Integer)id;
		}
	}
	
	class NullConverter implements IdConverter {
		@Override
		public int convertId(Object id) {
			return 0;
		}
	}
 
	
	private IdConverter idConverterFor(int argType) {
		
		switch (argType) {
		case DirectiveArgType.DIRARG_NONE:
		case DirectiveArgType.DIRARG_TRANSLATION:
		case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		case DirectiveArgType.DIRARG_INTERNAL: // Not sure about this - existing code treats it like text.
			return new NullConverter();
		case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
		case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be
											// preserved?
		case DirectiveArgType.DIRARG_FILE:
		case DirectiveArgType.DIRARG_OTHER:
			return new NullConverter();
		
		case DirectiveArgType.DIRARG_INTEGER:
		case DirectiveArgType.DIRARG_REAL:
			return new DirectConverter();
			
		case DirectiveArgType.DIRARG_CHAR:
			return _characterNumberConverter;
		case DirectiveArgType.DIRARG_ITEM:
			return _itemNumberConverter;
		case DirectiveArgType.DIRARG_CHARLIST:
			return _characterNumberConverter;
		case DirectiveArgType.DIRARG_ITEMLIST:
			return _itemNumberConverter;
		case DirectiveArgType.DIRARG_TEXTLIST:
			return new DirectConverter();
		case DirectiveArgType.DIRARG_CHARTEXTLIST:
			return _characterNumberConverter;
		case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		case DirectiveArgType.DIRARG_ITEMFILELIST:
			return _itemDescriptionConverter;
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
		return new NullConverter();
	}
}
