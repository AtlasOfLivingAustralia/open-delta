package au.org.ala.delta.directives;

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArguments;

public abstract class AbstractTextDirective extends AbstractDirective<DeltaContext> {

	private static Pattern VAR_PATTERN = Pattern.compile("[#]([A-Z]+)");
	
	protected String _args;
	
	protected AbstractTextDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArguments getDirectiveArgs() {
		DirectiveArguments args = new DirectiveArguments();
		args.addTextArgument(_args);
		return args;
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXT;
	}
	
	
	@Override
	public void parse(DeltaContext context, String data) throws ParseException {
		_args = data;
		
	}

	protected String replaceVariables(DeltaContext context, String str) {
		String result = str;
		Matcher m = VAR_PATTERN.matcher(str);	
		while (m.find()) {
			String varname = m.group(1);
			String value = context.getVariable(varname, "#" + varname).toString();
			result = result.replaceAll("[#]" + varname, value);			
		}
		return result;		
	}
}
