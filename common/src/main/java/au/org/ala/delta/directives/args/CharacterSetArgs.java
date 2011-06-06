package au.org.ala.delta.directives.args;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CharacterSetArgs implements DirectiveArgs {
	private List<Set<Integer>> args;
	
	public CharacterSetArgs() {
		args = new ArrayList<Set<Integer>>();
	}
	
	public List<Set<Integer>> getArgs() {
		return args;
	}
	
	public void add(Set<Integer> characterNums) {
		args.add(characterNums);
	}
	
}
