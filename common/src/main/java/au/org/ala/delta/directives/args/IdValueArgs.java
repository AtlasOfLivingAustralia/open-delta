package au.org.ala.delta.directives.args;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.util.Pair;

public class IdValueArgs<T> implements DirectiveArgs {
	private List<Pair<Integer, T>> args;
	
	public IdValueArgs() {
		args = new ArrayList<Pair<Integer, T>>();
	}
	
	public List<Pair<Integer, T>> getArgs() {
		return args;
	}
	
	public void add(Integer characterNum, T value) {
		args.add(new Pair<Integer, T>(characterNum, value));
	}
	
}
