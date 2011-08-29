package au.org.ala.delta.translation;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.ConforDirectiveFileParser;
import au.org.ala.delta.directives.ConforDirectiveParserObserver;

public abstract class TranslatorTest extends TestCase {

	protected ByteArrayOutputStream _bytes;
	protected DeltaContext _context;

	public TranslatorTest() {

	}

	public TranslatorTest(String name) {
		super(name);
	}

	/**
	 * Reads in specs/chars/items from the simple test data set but no other
	 * configuration. Test cases can manually configure the DeltaContext before
	 * doing the translation.
	 * 
	 * @throws Exception
	 *             if there was an error reading the input files.
	 */
	protected void initialiseContext(String path) throws Exception {

		File specs = classloaderPathToFile(path);

		ConforDirectiveFileParser parser = ConforDirectiveFileParser
				.createInstance();
		parser.registerObserver(new ConforDirectiveParserObserver(_context));
		parser.parse(specs, _context);
	}

	protected File classloaderPathToFile(String path) throws URISyntaxException {
		URL resource = getClass().getResource(path);
		return new File(resource.toURI());
	}

	protected String classLoaderPathToString(String path) throws Exception {
		File file = classloaderPathToFile(path);

		return FileUtils.readFileToString(file, "Cp1252");
	}

	protected String actualResults() throws IOException {
		_bytes.flush();
		return new String(_bytes.toByteArray(), Charset.forName("UTF-8"));
	}
}
