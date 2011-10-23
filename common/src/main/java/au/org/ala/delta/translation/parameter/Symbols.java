package au.org.ala.delta.translation.parameter;

import java.util.Iterator;

import au.org.ala.delta.model.IdentificationKeyCharacter;
import au.org.ala.delta.translation.FilteredDataSet;
import au.org.ala.delta.translation.PrintFile;

public class Symbols extends ParameterTranslator {

	private FilteredDataSet _dataSet;
	private boolean _quoteSymbols;
	private boolean _numberFromZero;
	
	public Symbols(PrintFile outputFile, FilteredDataSet dataSet, boolean quoteSymbols, boolean numberFromZero) {
		super(outputFile);
		_dataSet = dataSet;
		_quoteSymbols = quoteSymbols;
		_numberFromZero = numberFromZero;
	}

	@Override
	public void translateParameter(String parameter) {
		_outputFile.outputLine(symbols());
	}
	
	protected String symbols() {
		StringBuilder symbols = new StringBuilder();
		symbols.append("SYMBOLS ");
		Iterator<IdentificationKeyCharacter> characters = _dataSet.identificationKeyCharacterIterator();
		int max = 0;
		while (characters.hasNext()) {
			max = Math.max(max, characters.next().getNumberOfStates());
		}
		int first = _numberFromZero ? 0 : 1;
		
		if (_quoteSymbols) {
			symbols.append("\"");
		}
		writeSymbols(symbols, first, max+first);
		
		if (_quoteSymbols) {
			symbols.append("\"");
		}
		symbols.append(";");
		
		return symbols.toString();
	}
	
	protected void writeSymbols(StringBuilder symbols, int first, int last) {
		
		for (int i=first; i<last+first; i++) {
			symbols.append(ParameterBasedTranslator.STATE_CODES[i]);
		}
		
	}

}
