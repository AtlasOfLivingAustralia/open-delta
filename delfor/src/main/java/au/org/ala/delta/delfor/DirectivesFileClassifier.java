package au.org.ala.delta.delfor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.editor.directives.ImportContext;
import au.org.ala.delta.editor.directives.ImportDirective;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.directive.KeyDirType;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * The DirectivesFileClassifier attempts to determine the target program for a
 * directives file. It does this by counting the number of directives in the
 * file that match valid directives for each of CONFOR, INTKEY, DIST and KEY. If
 * the result is ambiguous the type is determined by the above order.
 */
public class DirectivesFileClassifier extends DirectiveParser<ImportContext> {

	private int[] _counts;
	private Directive[][] _allDirectives;
	
	private ImportContext _context;

	public DirectivesFileClassifier(ImportContext context) {
		_context = context;
		_allDirectives = new Directive[][]{ ConforDirType.ConforDirArray, IntkeyDirType.IntkeyDirArray,
				DistDirType.DistDirArray, KeyDirType.KeyDirArray };
		_counts = new int[DirectiveType.values().length];
		_registry.setNumberOfSignificantCharacters(-1);
		List<Directive> allDirectives = DirectivesUtils.mergeAllDirectives();
		registerDirectives(allDirectives);
		
	}

	public DirectiveType classify(File directivesFile) throws IOException, DirectiveException {
		_counts = new int[DirectiveType.values().length];
		parse(directivesFile, _context);
		return getClassification();
	}

	@Override
	protected void handleUnrecognizedDirective(ImportContext context, List<String> controlWords) {
		System.out.println("Unrecognised: " + controlWords);
	}

	protected void executeDirective(AbstractDirective<ImportContext> directive, String data, ImportContext context)
			throws DirectiveException {
		
		for (DirectiveType type : DirectiveType.values()) {
			if (DirectivesUtils.containsByName(directive, Arrays.asList(_allDirectives[type.ordinal()]))) {
				_counts[type.ordinal()]++;
			}
		}

	}

	public DirectiveType getClassification() {
		int max = -1;
		DirectiveType maxType = null;
		for (DirectiveType type : DirectiveType.values()) {
			if (_counts[type.ordinal()] > max) {
				max = _counts[type.ordinal()];
				maxType = type;
			}
		}
		return maxType;
	}

	private void registerDirectives(List<Directive> directives) {

		for (Directive directive : directives) {
			try {
				registerDirective(new ImportDirective(directive));
			} catch (Exception e) {
				throw new RuntimeException("Failed to find directive for: " + directive.joinNameComponents(), e);
			}

		}

	}

}
