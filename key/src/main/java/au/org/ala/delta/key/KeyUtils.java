package au.org.ala.delta.key;

import java.io.File;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.directives.io.KeyCharactersFileReader;
import au.org.ala.delta.key.directives.io.KeyItemsFileReader;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;

public class KeyUtils {

    public static void loadDataset(KeyContext context) {
        File charactersFile = context.getCharactersFile();
        File itemsFile = context.getItemsFile();

        BinaryKeyFile keyCharactersFile = new BinaryKeyFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        BinaryKeyFile keyItemsFile = new BinaryKeyFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

        KeyCharactersFileReader keyCharactersFileReader = new KeyCharactersFileReader(context, context.getDataSet(), keyCharactersFile);
        keyCharactersFileReader.createCharacters();

        KeyItemsFileReader keyItemsFileReader = new KeyItemsFileReader(context, context.getDataSet(), keyItemsFile);
        keyItemsFileReader.readAll();

        // Calculate character costs and item abundance values

        DeltaDataSet dataset = context.getDataSet();

        for (int i = 0; i < dataset.getNumberOfCharacters(); i++) {
            Character ch = dataset.getCharacter(i + 1);
            double charCost = Math.pow(context.getRBase(), 5.0 - Math.min(10.0, ch.getReliability()));
            context.setCharacterCost(ch.getCharacterId(), charCost);
        }

        for (int i = 0; i < dataset.getMaximumNumberOfItems(); i++) {
            Item taxon = dataset.getItem(i + 1);
            double itemAbundanceValue = Math.pow(context.getABase(), context.getItemAbundancy(i + 1) - 5.0);
            context.setCalculatedItemAbundanceValue(taxon.getItemNumber(), itemAbundanceValue);
        }
    }

}
