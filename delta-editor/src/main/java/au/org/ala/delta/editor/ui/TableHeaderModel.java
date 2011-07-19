package au.org.ala.delta.editor.ui;

import javax.swing.table.AbstractTableModel;

import au.org.ala.delta.editor.model.EditorViewModel;
import au.org.ala.delta.model.format.CharacterFormatter;

public class TableHeaderModel extends AbstractTableModel {

	private static final long serialVersionUID = 777956252484757022L;

	private EditorViewModel _model;
	private CharacterFormatter _formatter;
	
	public TableHeaderModel(EditorViewModel model) {
		_model = model;
		_formatter = new CharacterFormatter(false, false, false, false, true);
	}
	
	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public int getColumnCount() {
		return _model.getNumberOfCharacters();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		au.org.ala.delta.model.Character ch = _model.getCharacter(columnIndex + 1);
		return _formatter.formatCharacterDescription(ch);
	}

}
