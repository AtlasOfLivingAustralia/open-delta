package au.org.ala.delta.directives.args;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DirectiveArguments {

	private List<DirectiveArgument<?>> _args;
	
	public DirectiveArguments() {
		_args = new ArrayList<DirectiveArgument<?>>();
	}
	
	public void add(DirectiveArgument<?> arg) {
		_args.add(arg);
	}
	
	public List<DirectiveArgument<?>> getDirectiveArguments() {
		return _args;
	}
	
	public DirectiveArgument<?> get(int index) {
		return _args.get(index);
	}
	
	public <T> void addDirectiveArgument(T id, String comment, String text) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setComment(comment);
		arg.setText(text);
		
		_args.add(arg);
	}
	
	public <T> void addTextArgument(T id, String text) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setText(text);
		
		_args.add(arg);
	}
	
	public void addTextArgument(String text) {
		DirectiveArgument<Integer> arg = new DirectiveArgument<Integer>();
		arg.setText(text);
		
		_args.add(arg);
	}
	
	public <T,V> void addNumericArgument(T id, String value) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		arg.setValue(new BigDecimal(value));
	}
	
	public <T> void addDirectiveArgument(List<Integer> ids) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>();
		
		for (int id : ids) {
			arg.add(id);
		}
	}

	public <T> void addDirectiveArgument(T id) {
		DirectiveArgument<T> arg = new DirectiveArgument<T>(id);
		_args.add(arg);
	}
	
	public static DirectiveArguments textArgument(String text) {
		DirectiveArguments args = new DirectiveArguments();
		args.addTextArgument(text);
		return args;
	}
}
