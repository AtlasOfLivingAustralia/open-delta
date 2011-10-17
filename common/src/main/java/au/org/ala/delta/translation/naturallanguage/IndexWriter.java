package au.org.ala.delta.translation.naturallanguage;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.format.ItemFormatter;
import au.org.ala.delta.translation.PrintFile;

/**
 * Writes the index file during a translate into natural language with
 * output format html.
 */
public class IndexWriter {

	private PrintFile _indexFile;
	private ItemFormatter _formatter;
	private DeltaContext _context;
	
	public IndexWriter(PrintFile indexFile, ItemFormatter itemFormatter, DeltaContext context) {
		_indexFile = indexFile;
		_context = context;
		_formatter = itemFormatter;
	}
	
	public void writeIndexText() {
		
	}
	
	public void addItemToIndex(Item item) {
		writeItemIndexHeading(item);
		writeItemIndexEntry(item);
	}
	
	private void writeItemIndexHeading(Item item) {
		
		String heading = _context.getIndexHeading(item.getItemNumber());
		if (StringUtils.isNotEmpty(heading)) {
			_indexFile.outputLine(heading);
		}
	}
	
	private void writeItemIndexEntry(Item item) {
		String outputFile = _context.getOutputFileSelector().getItemOutputFile(item.getItemNumber());
		
		StringBuilder indexEntry = new StringBuilder();
		indexEntry.append("&#149;&nbsp;<a href=\"");
		indexEntry.append(outputFile);
		indexEntry.append("\">");
		indexEntry.append(_formatter.formatItemDescription(item));
		indexEntry.append("</a>");
		
		_indexFile.outputLine(indexEntry.toString());
	}
	
}
