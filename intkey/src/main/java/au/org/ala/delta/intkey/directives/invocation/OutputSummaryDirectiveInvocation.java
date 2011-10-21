package au.org.ala.delta.intkey.directives.invocation;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.intkey.model.IntkeyContext;
import au.org.ala.delta.intkey.model.ReportUtils;
import au.org.ala.delta.intkey.ui.UIUtils;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public class OutputSummaryDirectiveInvocation extends IntkeyDirectiveInvocation {

    private List<Item> _taxa;

    private List<Character> _characters;

    public void setSelectedTaxaSpecimen(Pair<List<Item>, Boolean> pair) {
        this._taxa = pair.getFirst();
        // The specimen has no relevance here. Simply ignore it if it is
        // specified.
    }

    public void setCharacters(List<Character> characters) {
        this._characters = characters;
    }

    @Override
    public boolean execute(IntkeyContext context) throws IntkeyDirectiveInvocationException {

        StringBuilder builder = new StringBuilder();
        builder.append("Output Summary\n");

        int columnNumber = 0;

        for (Character ch : _characters) {
            int characterNumber = ch.getCharacterId();
            List<Attribute> attrs = context.getDataset().getAttributesForCharacter(ch.getCharacterId());

            builder.append(characterNumber);
            builder.append(",");

            if (ch instanceof MultiStateCharacter) {

            } else if (ch instanceof IntegerCharacter) {

            } else if (ch instanceof RealCharacter) {
                List<Object> realSummaryInformation = ReportUtils.generateRealSummaryInformation((RealCharacter) ch, attrs, _taxa);
                double minValue = (Double) realSummaryInformation.get(3);
                double maxValue = (Double) realSummaryInformation.get(4);
                double mean = (Double) realSummaryInformation.get(7);
                builder.append(String.format("%.2f", minValue));
                builder.append("-");
                builder.append(String.format("%.2f", mean));
                builder.append("-");
                builder.append(String.format("%.2f", maxValue));
            } else if (ch instanceof TextCharacter) {

            }

            if (columnNumber == 4) {
                builder.append("\n");
                columnNumber = 0;
            } else {
                builder.append(" ");
                columnNumber++;
            }
        }

        try {
            context.appendToOutputFile(builder.toString());
        } catch (IllegalStateException ex) {
            throw new IntkeyDirectiveInvocationException("NoOutputFileOpen.error");
        }

        return true;
    }

}
