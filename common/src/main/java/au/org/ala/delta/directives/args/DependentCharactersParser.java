package au.org.ala.delta.directives.args;

import java.io.Reader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.MutableDeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.MultiStateCharacter;

/**
 * Handles the DEPENDENT CHARACTERS directive.
 */
public class DependentCharactersParser extends DirectiveArgsParser {
	
	private MutableDeltaDataSet _dataSet;
	private List<CharacterDependency> _dependencies;
	private DeltaDataSetFactory _factory;
	
	public DependentCharactersParser(DeltaContext context, Reader reader) {
		super(context, reader);
		_dataSet = context.getDataSet();
		
		// The use of the DefaultDataSetFactory is deliberate, it allows us
		// to create a CharacterDependency that is independent of the dataset.
		_factory = new DefaultDataSetFactory();
	}
	
	@Override
	public void parse() throws ParseException {
		_args = new DirectiveArguments();
		_dependencies = new ArrayList<CharacterDependency>();
		
		readNext();
		skipWhitespace();
		
		while (_currentInt >= 0 && Character.isDigit(_currentChar)) {	
			readDependency();
			skipWhitespace();
		}
	}

	public List<CharacterDependency> getCharacterDependencies() {
		return _dependencies;
	}
	
	private void readDependency() throws ParseException {
		skipWhitespace();
		int charNum = readCharNum();
		
		expect(',');
		readNext();
		
		List<Integer> states = readStates();
		
		expect(':');
		
		List<Integer> controlled = readSet();

		addDependency(charNum, states, controlled);
	}
	
	private void addDependency(int charNum, List<Integer> states, List<Integer> controlled) {
		MultiStateCharacter character = (MultiStateCharacter)_dataSet.getCharacter(charNum);
		_dependencies.add(_factory.createCharacterDependency(character, new HashSet<Integer>(states), new HashSet<Integer>(controlled)));	
		
	}
	
	private int readCharNum() throws ParseException {
		int charNum = readInteger();
		CharacterType type = getType(charNum);
		if (!type.isMultistate()) {
			throw new ParseException("Invalid character type: "+type+" Must be multistate", _position);
		}
		return charNum;
	}
	
	private List<Integer> readStates() throws ParseException {
		List<Integer> states = new ArrayList<Integer>();
		states.add(readInteger());
		while (_currentInt >= 0 && _currentChar == '/') {
			readNext();
			states.add(readInteger());
		}
		return states;
	}
	
	private CharacterType getType(int charNum) throws ParseException {
		return _dataSet.getCharacter(charNum).getCharacterType();
	}
}
