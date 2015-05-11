package au.org.ala.delta.example;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.directives.*;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.model.*;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.translation.IterativeTranslator;
import au.org.ala.delta.translation.attribute.CommentedValueList;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Example class that gets callbacks during the Natural Language Translation process.
 */
public class TranslationObserver extends OutputStream implements IterativeTranslator {

    private StringBuilder buffer = new StringBuilder();
    DeltaContext context;
    private File input;

    public static void main(String[] args) throws Exception {
        // Expecting to be run from project root.
        String tonat = "confor/src/test/resources/dataset/sample/tonat";
        File tonatFile = new File(tonat);
        TranslationObserver observer = new TranslationObserver(tonatFile);
        observer.process();

    }

    public TranslationObserver(File tonatFile) {
        PrintStream out = new PrintStream(this);
        input = tonatFile;
        context = new DeltaContext(out, out);
        context.setTranslationObserver(this);
    }

    public void process() throws Exception {
        ConforDirectiveFileParser p = ConforDirectiveFileParser.createInstance();
        ConforDirectiveParserObserver observer = new ConforDirectiveParserObserver(context);
        p.registerObserver(observer);
        try {
            p.parse(input, context);
        } catch (DirectiveException e) {
            // Ignore, this just allows us to terminate parsing early.
        }
        observer.finishedProcessing();
    }

    @Override
    public void write(final int b) throws IOException {
        char ch = (char) b;
        buffer.append((char) b);
    }

    @Override
    public void beforeFirstItem() {

    }

    @Override
    public void beforeItem(Item item) {
        buffer.append("Before Item :"+item.getDescription()+'\n');
    }

    @Override
    public void afterItem(Item item) {
        buffer.append("After Item :"+item.getDescription()+'\n');
    }

    @Override
    public void beforeAttribute(Attribute attribute) {
        buffer.append("Before character: "+attribute.getCharacter().getDescription()+'\n');
    }

    @Override
    public void afterAttribute(Attribute attribute) {
        buffer.append("After character: "+attribute.getCharacter().getDescription()+'\n');
    }

    @Override
    public void afterLastItem() {
        System.out.println(buffer.toString());
    }

    @Override
    public void attributeComment(String comment) {

    }

    @Override
    public void attributeValues(CommentedValueList.Values values) {

    }

    @Override
    public void beforeFirstCharacter() {

    }

    @Override
    public void beforeCharacter(au.org.ala.delta.model.Character character) {

    }

    @Override
    public void afterCharacter(Character character) {

    }

    @Override
    public void afterLastCharacter() {

    }

    @Override
    public void translateOutputParameter(OutputParameters.OutputParameter parameterName) {

    }
}
