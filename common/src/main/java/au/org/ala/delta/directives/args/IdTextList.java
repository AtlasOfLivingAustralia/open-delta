package au.org.ala.delta.directives.args;

import java.util.ArrayList;
import java.util.List;

public class IdTextList<T> implements DirectiveArgs {

	private String _delimeter;
	private List<IdTextArg<T>> _characterList;
	
	
	public IdTextList(String delimiter) {
		_delimeter = delimiter;
		_characterList = new ArrayList<IdTextArg<T>>();
	}
	
	public String getDelimiter() {
		return _delimeter;
	}
	
	public List<IdTextArg<T>> getIdTextList() {
		return _characterList;
	}
	
	public void add(IdTextArg<T> arg) {
		_characterList.add(arg);
	}
	
	public static class IdTextArg<T> {

		private String _comment;
		private String _text;
		private T _id;
		
		public IdTextArg(String comment, String text, T id) {
			_comment = comment;
			_text = text;
			_id = id;
		}
		
		public String getComment() {
			return _comment;
		}
		
		public String getText() {
			return _text;
		}
		
		public T getId() {
			return _id;
		}
	}

}
