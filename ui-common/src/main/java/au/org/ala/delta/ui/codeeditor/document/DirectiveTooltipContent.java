package au.org.ala.delta.ui.codeeditor.document;


public class DirectiveTooltipContent {
	
	private String _name;
	private String _description;
	private int _id;
	
	public DirectiveTooltipContent(String name, String description, int id) {
		_name = name;
		_description = description;
		_id = id;
	}
	
	@Override
	public String toString() {
		return String.format("<html><h1>%s</h1><p>%s</p></html>", _name, _description ); 
	}
	
	public String getName() {
		return _name;
	}
	
	public String getDescription() {		
		return _description;
	}
	
	public int getId() {
		return _id;
	}
	
}
