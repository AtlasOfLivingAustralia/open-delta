package au.org.ala.delta.model;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.*;
import au.org.ala.delta.directives.validation.DirectiveError;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.util.IProgressObserver;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.List;

/**
 * An implementation of the DataSetRepository interface that deals with data sets consisting
 * of the chars, items and specs files.
 */
public class TextFileDataSetRepository implements DeltaDataSetRepository {

    private DefaultDataSetFactory _factory = new DefaultDataSetFactory();

    @Override
    public void save(MutableDeltaDataSet dataSet, IProgressObserver observer) {
        throw new UnsupportedOperationException("Save is not supported by this implementation");
    }
    @Override
    public void saveAsName(MutableDeltaDataSet dataSet, String name, IProgressObserver observer) {
        throw new UnsupportedOperationException("Save is not supported by this implementation");
    }

    /**
     * Loads the DELTA data set identified by the supplied name.
     * The name parameter may specify either:
     * <ol>
     * <li>The name of a directory which contains files named specs,  chars and items</li>
     * <li>The name of a directives file, in which case this file should include or contain the directives
     * normally found in the specs, chars and items files.</li>
     * </ol>
     * @param name the name of a directory or directives file containing the data set specification.
     * @param observer not used, may be null.
     * @return a newly created MutableDeltaDataSet
     */
    @Override
    public MutableDeltaDataSet findByName(String name, IProgressObserver observer) {

        File file = new File(name);

        if (!file.exists()) {
            throw new IllegalArgumentException("Data set location: "+name+" does not exist.");
        }

        DeltaContext context = new DeltaContext(_factory.createDataSet(file.getName()));

        DefaultDirectiveFileParser parser = new DefaultDirectiveFileParser();
        if (file.isDirectory()) {
            String[] specsFiles = new String[] {"specs", "chars", "items"};
            for (String specsFileName : specsFiles) {
                File specsFile = new File(file, specsFileName);
                if (!specsFile.exists()) {
                    throw new IllegalArgumentException("Unable to locate file: "+specsFile.getAbsolutePath());
                }
                parse(specsFile, parser, context);
            }

        }
        else {
            parse(file, parser, context);
        }
        return context.getDataSet();
    }

    /**
     * Just wraps checked exceptions in a RuntimeException.
     * @param file the file to parse.
     * @param parser the Parser that is doing the work.
     * @param context the parsing context to be used.
     */
    private void parse(File file, DefaultDirectiveFileParser parser, DeltaContext context) {
        try {
            parser.parse(file, context);
        }
        catch (Exception e) {
            throw new RuntimeException("Error loading data set: "+file.getName(), e);
        }
    }


    @Override
    public MutableDeltaDataSet newDataSet() {
        return _factory.createDataSet("unnamed");
    }


    /**
     * The DefaultDirectiveFileParser knows how to parse the directives that build up the DELTA model.
     * Directives which specify a transformation or processing action are ignored.
     */
    public class DefaultDirectiveFileParser extends DirectiveParser<DeltaContext> {

        DefaultDirectiveFileParser() {
            registerDirectives();
        }

        public void registerDirectives() {
            registerDirective(new CharacterImages());
            registerDirective(new CharacterList());
            registerDirective(new CharacterNotes());
            registerDirective(new CharacterTypes());
            registerDirective(new CharacterWeights());
            registerDirective(new DataBufferSize());
            registerDirective(new DependentCharacters());
            registerDirective(new ImplicitValues());
            registerDirective(new InapplicableCharacters());
            registerDirective(new InputDeltaFile());
            registerDirective(new InputFile());
            registerDirective(new ItemDescriptions());
            registerDirective(new MandatoryCharacters());
            registerDirective(new MaximumNumberOfItems());
            registerDirective(new MaximumNumberOfStates());
            registerDirective(new NumberOfCharacters());
            registerDirective(new NumbersOfStates());
            registerDirective(new OverlayFonts());

        }

        /** Tracks the order of the most recently processed directive. */
        private int _currentOrder;

        @Override
        protected void handleUnrecognizedDirective(DeltaContext context, List<String> controlWords) {
            ParsingContext pc = context.getCurrentParsingContext();
            if (pc.getFile() != null) {
                Logger.log("Unrecognized Directive: %s at offset %s %d:%d", StringUtils.join(controlWords, " "), pc.getFile().getName(), pc.getCurrentDirectiveStartLine(),
                        pc.getCurrentDirectiveStartOffset());
            } else {
                Logger.log("Unrecognized Directive: %s at offset %d:%d", StringUtils.join(controlWords, " "), pc.getCurrentDirectiveStartLine(), pc.getCurrentDirectiveStartOffset());
            }
        }

        @Override
        protected void executeDirective(AbstractDirective<DeltaContext> directive, String data, DeltaContext context)
                throws DirectiveException {

            int order = directive.getOrder();
            // Directives with order 0 can appear anywhere
            if (order > 0 && order < _currentOrder) {
                throw DirectiveError.asException(DirectiveError.Error.DIRECTIVE_OUT_OF_ORDER, 0);
            }
            _currentOrder = order;
            super.executeDirective(directive, data, context);
        }



    }

}
