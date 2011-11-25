package au.org.ala.delta.model.attribute;


public interface ParsedAttribute extends Iterable<AttrChunk> {

	public String getAsText();

	public String getAsText(AttributeChunkFormatter formatter);
}
