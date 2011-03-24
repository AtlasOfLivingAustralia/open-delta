package au.org.ala.delta.editor.ui;

public class MatrixCellViewModel {
	
	private String _text;
	private boolean _implicit;
	
	public MatrixCellViewModel() {		
	}
	
	public MatrixCellViewModel(String text) {
		_text = text;
	}
	
	public MatrixCellViewModel(String text, boolean implicit) {
		_text = text;
		_implicit = implicit;
	}
	
	public void setImplicit(boolean implicit) {
		_implicit = implicit;
	}
	
	public boolean isImplicit() {
		return _implicit;
	}
	
	public String getText() {
		return _text;
	}
	
	public void setText(String text) {
		_text = text;
	}
	
	@Override
	public String toString() {
		return String.format("%s [Implicit: %b]", _text, _implicit);
	}

}
