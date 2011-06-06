package au.org.ala.delta.directives.args;

import java.util.ArrayList;
import java.util.List;

public class IntegerList implements DirectiveArgs {

	private List<Integer> _argList;
	
	public IntegerList() {
		_argList = new ArrayList<Integer>();
	}
	
	public void add(int value) {
		_argList.add(value);
	}
	
	public List<Integer> getArgList() {
		return _argList;
	}
}
