package au.org.ala.delta.model.attribute;

import au.org.ala.delta.model.Attribute;

public class DefaultAttributeChunkFormatter implements AttributeChunkFormatter {
	
	boolean _encloseInCommentBrackets = false;
	private String _rangeSeparator;
	
	public DefaultAttributeChunkFormatter() {
		this(false);
	}
	
	public DefaultAttributeChunkFormatter(boolean encloseInCommentBrackets) {
		this(encloseInCommentBrackets, Character.toString(Attribute.Delimiters.STATERANGE));
	}
	
	public DefaultAttributeChunkFormatter(boolean encloseInCommentBrackets, String rangeSeparator) {
		_rangeSeparator = rangeSeparator;
		_encloseInCommentBrackets = encloseInCommentBrackets;
	}
	
	public String formatChunk(AttrChunk chunk) {
		String dest = null;
		switch (chunk.getType()) {
			case ChunkType.CHUNK_STOP:
				dest = "";
				break;

			case ChunkType.CHUNK_TEXT:
			case ChunkType.CHUNK_LONGTEXT:
				if (_encloseInCommentBrackets)
					dest = Attribute.Delimiters.OPENBRACK + chunk.getString() + Attribute.Delimiters.CLOSEBRACK;
				else
					dest = chunk.getString();
				break;
			case ChunkType.CHUNK_STATE:
				dest = chunk.getStateId() + "";			
				break;
			case ChunkType.CHUNK_NUMBER:
				dest = chunk.getNumber().toPlainString();
				break;
			case ChunkType.CHUNK_EXLO_NUMBER:
				dest = "(" + chunk.getNumber().toPlainString() + getRangeSeparator() + ")";
				break;
			case ChunkType.CHUNK_EXHI_NUMBER:
				dest = "("+getRangeSeparator() + chunk.getNumber().toPlainString() + ")";
				break;
			case ChunkType.CHUNK_VARIABLE:
				dest = Attribute.VARIABLE;
				break;
			case ChunkType.CHUNK_UNKNOWN:
				dest = Attribute.UNKNOWN;
				break;
			case ChunkType.CHUNK_INAPPLICABLE:
				dest = getRangeSeparator();
				break;

			case ChunkType.CHUNK_OR:
				dest = Character.toString(Attribute.Delimiters.ORSTATE);
				break;

			case ChunkType.CHUNK_AND:
				dest = Character.toString(Attribute.Delimiters.ANDSTATE);
				break;

			case ChunkType.CHUNK_TO:
				dest = getRangeSeparator();
				break;

			default:
				dest = "";
				break;
		}

		return dest;
	}
	
	protected String getRangeSeparator() {
		return _rangeSeparator;
	}
}
