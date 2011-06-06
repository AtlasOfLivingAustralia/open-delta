package au.org.ala.delta.directives.args;

import java.util.HashMap;
import java.util.Map;

public class CharacterValueArgs<T> implements DirectiveArgs {
	private Map<Integer, T> args;
	
	public CharacterValueArgs() {
		args = new HashMap<Integer, T>();
	}
	
	public Map<Integer, T> getArgs() {
		return args;
	}
	
	public void add(Integer characterNum, T value) {
		args.put(characterNum, value);
	}
	
}
