package au.org.ala.delta.directives;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgs;
import au.org.ala.delta.directives.args.TextArg;

public abstract class AbstractTextDirective extends AbstractDirective<DeltaContext> {

	private static Pattern VAR_PATTERN = Pattern.compile("[#]([A-Z]+)");
	
	protected String _args;
	
	protected AbstractTextDirective(String... controlWords) {
		super(controlWords);
	}

	@Override
	public DirectiveArgs getDirectiveArgs() {

		return new TextArg(_args);
	}

	@Override
	public int getArgType() {
		return DirectiveArgType.DIRARG_TEXT;
	}
	
	@Override
	public void process(DeltaContext context, String data) throws Exception {
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
