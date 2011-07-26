package au.org.ala.delta.directives;

import java.io.StringReader;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.args.IntegerTextListParser;
import au.org.ala.delta.directives.args.TextListParser;
import au.org.ala.delta.model.TypeSettingMark;
import au.org.ala.delta.model.TypeSettingMark.MarkPosition;

/**
 * Processes the TYPESETTING MARKS directive.
 */
public class TypeSettingMarks extends AbstractFormattingDirective {

	public TypeSettingMarks() {
		super("typesetting", "marks");
	}
	
	@Override
	public void processMark(DeltaContext context, TypeSettingMark mark) {
		context.addTypeSettingMark(mark);
	}
	

}
