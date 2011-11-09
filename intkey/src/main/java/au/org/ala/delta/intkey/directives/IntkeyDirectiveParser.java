package au.org.ala.delta.intkey.directives;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.Logger;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.DirectiveParser;
import au.org.ala.delta.directives.DirectiveSearchResult;
import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class IntkeyDirectiveParser extends DirectiveParser<IntkeyContext> {

    // private constructor - use factory method to get an instance.
    private IntkeyDirectiveParser() {
        // Intkey needs to be able to match supplied control words of less
        // than the standard 3 characters, e.g. "INCLUDE T" should match the
        // "INCLUDE TAXA" directive.
        getDirectiveRegistry().setNumberOfSignificantCharacters(-1);
    }

    public static IntkeyDirectiveParser createInstance() {
        IntkeyDirectiveParser instance = new IntkeyDirectiveParser();

        instance.registerDirective(new FileCharactersDirective());
        instance.registerDirective(new FileTaxaDirective());
        instance.registerDirective(new FileInputDirective());
        instance.registerDirective(new NewDatasetDirective());
        instance.registerDirective(new UseDirective());
        instance.registerDirective(new DefineCharactersDirective());
        instance.registerDirective(new DefineTaxaDirective());
        instance.registerDirective(new DefineNamesDirective());
        instance.registerDirective(new DefineButtonDirective());
        instance.registerDirective(new DefineInformationDirective());
        instance.registerDirective(new RestartDirective());
        instance.registerDirective(new ChangeDirective());
        instance.registerDirective(new SetRBaseDirective());
        instance.registerDirective(new SetReliabilitiesDirective());
        instance.registerDirective(new SetVaryWtDirective());
        instance.registerDirective(new DisplayCharacterOrderBestDirective());
        instance.registerDirective(new BestDirective());
        instance.registerDirective(new DisplayCharacterOrderNaturalDirective());
        instance.registerDirective(new DisplayCharacterOrderSeparateDirective());
        instance.registerDirective(new DifferencesDirective());
        instance.registerDirective(new SetToleranceDirective());
        instance.registerDirective(new IncludeCharactersDirective());
        instance.registerDirective(new IncludeTaxaDirective());
        instance.registerDirective(new ExcludeCharactersDirective());
        instance.registerDirective(new ExcludeTaxaDirective());
        instance.registerDirective(new SetImagePathDirective());
        instance.registerDirective(new SetInfoPathDirective());
        instance.registerDirective(new SetMatchDirective());
        instance.registerDirective(new SetDiagLevelDirective());
        instance.registerDirective(new CharactersDirective());
        instance.registerDirective(new FileOutputDirective());
        instance.registerDirective(new SetFixDirective());
        instance.registerDirective(new DeleteDirective());
        instance.registerDirective(new IllustrateCharactersDirective());
        instance.registerDirective(new IllustrateTaxaDirective());
        instance.registerDirective(new DescribeDirective());
        instance.registerDirective(new ContentsDirective());
        instance.registerDirective(new FileDisplayDirective());
        instance.registerDirective(new SetAutoToleranceDirective());
        instance.registerDirective(new SetDiagTypeSpecimensDirective());
        instance.registerDirective(new SetDiagTypeTaxaDirective());
        instance.registerDirective(new SetExactDirective());
        instance.registerDirective(new SetStopBestDirective());
        instance.registerDirective(new TaxaDirective());
        instance.registerDirective(new FindCharactersDirective());
        instance.registerDirective(new FindTaxaDirective());
        instance.registerDirective(new DisplayNumberingDirective());
        instance.registerDirective(new DisplayCommentsDirective());
        instance.registerDirective(new DisplayUnknownsDirective());
        instance.registerDirective(new DisplayInapplicablesDirective());
        instance.registerDirective(new DisplayLogDirective());
        instance.registerDirective(new FileJournalDirective());
        instance.registerDirective(new FileLogDirective());
        instance.registerDirective(new FileCloseDirective());
        instance.registerDirective(new CommentDirective());
        instance.registerDirective(new QuitDirective());
        instance.registerDirective(new ShowDirective());
        instance.registerDirective(new SummaryDirective());
        instance.registerDirective(new SimilaritiesDirective());
        instance.registerDirective(new DiagnoseDirective());
        instance.registerDirective(new InformationDirective());
        instance.registerDirective(new OutputCharactersDirective());
        instance.registerDirective(new OutputTaxaDirective());
        instance.registerDirective(new OutputDifferencesDirective());
        instance.registerDirective(new OutputSimilaritiesDirective());
        instance.registerDirective(new OutputSummaryDirective());
        instance.registerDirective(new OutputCommentDirective());

        instance.registerDirective(new StatusIncludeCharactersDirective());
        instance.registerDirective(new StatusIncludeTaxaDirective());
        instance.registerDirective(new StatusExcludeCharactersDirective());
        instance.registerDirective(new StatusExcludeTaxaDirective());
        instance.registerDirective(new StatusFilesDirective());
        instance.registerDirective(new StatusSetDirective());
        instance.registerDirective(new StatusDisplayDirective());
        instance.registerDirective(new StatusAllDirective());

        instance.registerDirective(new DisplayContinuousDirective());
        instance.registerDirective(new DisplayImagesDirective());
        instance.registerDirective(new DisplayKeywordsDirective());
        instance.registerDirective(new DisplayScaledDirective());
        instance.registerDirective(new DisplayEndIdentifyDirective());
        instance.registerDirective(new DisplayInputDirective());

        instance.registerDirective(new DefineEndIdentifyDirective());
        instance.registerDirective(new DefineSubjectsDirective());

        instance.registerDirective(new PreferencesDirective());

        instance.registerDirective(new SetDemonstrationDirective());

        return instance;
    }

    @Override
    protected void processTrailing(StringBuilder data, IntkeyContext context) throws DirectiveException {
        if (data.length() > 0) {
            char ch = data.charAt(0);
            if (Character.isDigit(ch)) {
                DirectiveSearchResult r = getDirectiveRegistry().findDirective(new ArrayList<String>(Arrays.asList("use")));
                IntkeyDirective useDirective = (IntkeyDirective) r.getMatches().get(0);
                try {
                    useDirective.parseAndProcess(context, data.toString());
                } catch (Exception ex) {
                    handleDirectiveProcessingException(context, useDirective, ex);
                }
            } else {
                super.processTrailing(data, context);
            }
        }
    }

    @Override
    protected void handleUnrecognizedDirective(IntkeyContext context, List<String> controlWords) {
        // TODO eventually all unrecognized directives need to be properly
        // handled. This is here so that
        // intkey dataset can be used with milestone release without implemented
        // directives causing
        // errors
        Logger.log(String.format("Ignoring unrecognized directive: %s ", StringUtils.join(controlWords, " ")));
    }

    @Override
    protected void handleDirectiveProcessingException(IntkeyContext context, AbstractDirective<IntkeyContext> d, Exception ex) {
        ex.printStackTrace();
        String msg;
        if (ex instanceof IntkeyDirectiveParseException) {
            msg = ex.getMessage();
        } else {
            msg = String.format("Error occurred while processing '%s' command: %s", StringUtils.join(d.getControlWords(), " ").toUpperCase(), ex.getMessage());
            Logger.error(ex);
        }

        Logger.log(msg);

        if (!context.isProcessingDirectivesFile()) {
            context.getUI().displayErrorMessage(msg);
        }
    }

}
