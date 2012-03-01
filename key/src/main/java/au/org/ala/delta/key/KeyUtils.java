package au.org.ala.delta.key;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.io.BinaryKeyFile;
import au.org.ala.delta.key.directives.io.KeyCharactersFileReader;
import au.org.ala.delta.key.directives.io.KeyItemsFileReader;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.util.Pair;

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
    
    public static BracketedKey convertTabularKeyToBracketedKey(TabularKey tabularKey) {

        // Store data in maps while processing. This will be converted into
        // BracketedKey/BracketedKeyNodes afterwards
        List<Map<List<MultiStateAttribute>, Object>> nodeInfoMaps = new ArrayList<Map<List<MultiStateAttribute>, Object>>();
        Map<Integer, Integer> nodeBackReferences = new HashMap<Integer, Integer>();
        Map<List<MultiStateCharacter>, Integer> latestNodeForCharacterGroupMap = new HashMap<List<MultiStateCharacter>, Integer>();

        for (int i = 0; i < tabularKey.getNumberOfRows(); i++) {
            TabularKeyRow row = tabularKey.getRowAt(i);
            for (int j = 0; j < row.getNumberOfColumns(); j++) {
                int columnNumber = j + 1;
                List<MultiStateAttribute> columnAttrs = row.getAllAttributesForColumn(columnNumber);
                List<MultiStateCharacter> columnChars = getCharactersFromAttributes(columnAttrs);

                TabularKeyRow previousRow = null;

                // If the corresponding column in the previous row has the same
                // characters, use the latest existing index for that set of
                // characters.
                // Otherwise we need to create a new index
                boolean newIndex = false;
                if (i > 0) {
                    previousRow = tabularKey.getRowAt(i - 1);
                    if (previousRow.getNumberOfColumns() >= j + 1) {
                        if (!rowsMatchCharactersAtColumn(row, previousRow, columnNumber)) {
                            newIndex = true;
                        }
                    } else {
                        newIndex = true;
                    }
                }

                int indexForColumn;
                Map<List<MultiStateAttribute>, Object> indexInfo;

                if (newIndex || !latestNodeForCharacterGroupMap.containsKey(columnChars)) {
                    indexForColumn = nodeInfoMaps.size();
                    indexInfo = new HashMap<List<MultiStateAttribute>, Object>();
                    nodeInfoMaps.add(indexInfo);
                    latestNodeForCharacterGroupMap.put(columnChars, indexForColumn);
                } else {
                    indexForColumn = latestNodeForCharacterGroupMap.get(columnChars);
                    indexInfo = nodeInfoMaps.get(indexForColumn);
                }

                if (j == row.getNumberOfColumns() - 1) {
                    if (indexInfo.containsKey(columnAttrs)) {
                        List<Item> taxaList = (List<Item>) indexInfo.get(columnAttrs);
                        taxaList.add(row.getItem());
                    } else {
                        List<Item> taxaList = new ArrayList<Item>();
                        taxaList.add(row.getItem());
                        indexInfo.put(columnAttrs, taxaList);
                    }
                } else {
                    List<MultiStateAttribute> nextColumnAttrs = row.getAllAttributesForColumn(columnNumber + 1);
                    List<MultiStateCharacter> nextColumnChars = getCharactersFromAttributes(nextColumnAttrs);

                    // Get the index for the next column. If no index has been
                    // recorded for the group of characters, or
                    // the group of characters is different to the corresponding
                    // column in the previous row, then we
                    // need to create a new
                    int indexForNextColumn;
                    if (latestNodeForCharacterGroupMap.containsKey(nextColumnChars)) {
                        indexForNextColumn = latestNodeForCharacterGroupMap.get(nextColumnChars);

                        if (i > 0) {
                            if (previousRow.getNumberOfColumns() >= j + 2) {
                                if (!rowsMatchCharactersAtColumn(row, previousRow, columnNumber + 1)) {
                                    indexForNextColumn = nodeInfoMaps.size();
                                }
                            } else {
                                indexForNextColumn = nodeInfoMaps.size();
                            }
                        }
                    } else {
                        indexForNextColumn = nodeInfoMaps.size();
                    }

                    indexInfo.put(columnAttrs, indexForNextColumn);
                    nodeBackReferences.put(indexForNextColumn, indexForColumn);
                }
            }
        }

        // Build a BracketedKey/BracketedKeyNodes from the data in the maps.
        BracketedKey bracketedKey = new BracketedKey();

        for (int i = 0; i < nodeInfoMaps.size(); i++) {
            Map<List<MultiStateAttribute>, Object> nodeInfo = nodeInfoMaps.get(i);

            int backReference = 0;
            if (i > 0) {
                // Node references are 1 indexed in the output.
                backReference = nodeBackReferences.get(i) + 1;
            }

            // Sort attribute lists by the first state values of the first
            // attribute
            // - any other attributes in the list are for
            // confirmatory characters, these will not effect the sort order.
            List<List<MultiStateAttribute>> sortedAttributeLists = new ArrayList<List<MultiStateAttribute>>(nodeInfo.keySet());
            Collections.sort(sortedAttributeLists, new Comparator<List<MultiStateAttribute>>() {

                @Override
                public int compare(List<MultiStateAttribute> l1, List<MultiStateAttribute> l2) {
                    MultiStateAttribute l1FirstAttr = l1.get(0);
                    MultiStateAttribute l2FirstAttr = l2.get(0);

                    int l1FirstAttrStateNumber = l1FirstAttr.getPresentStates().iterator().next();
                    int l2FirstAttrStateNumber = l2FirstAttr.getPresentStates().iterator().next();

                    return Integer.valueOf(l1FirstAttrStateNumber).compareTo(l2FirstAttrStateNumber);
                }
            });

            BracketedKeyNode node = new BracketedKeyNode(i + 1); // Node
                                                                 // references
                                                                 // are 1
                                                                 // indexed in
                                                                 // the output.
            node.setBackReference(backReference);
            for (int j = 0; j < sortedAttributeLists.size(); j++) {
                List<MultiStateAttribute> nodeLineAttributes = sortedAttributeLists.get(j);
                Object forwardReferenceOrTaxaList = nodeInfo.get(nodeLineAttributes);

                if (forwardReferenceOrTaxaList instanceof Integer) {
                    int forwardReferenceNodeNumber = ((Integer) forwardReferenceOrTaxaList) + 1;
                    node.addLine(nodeLineAttributes, forwardReferenceNodeNumber);
                } else {
                    node.addLine(nodeLineAttributes, (List<Item>) forwardReferenceOrTaxaList);
                }
            }
            bracketedKey.addNode(node);
        }

        return bracketedKey;
    }
    
    private static List<MultiStateCharacter> getCharactersFromAttributes(List<MultiStateAttribute> attrs) {
        List<MultiStateCharacter> chars = new ArrayList<MultiStateCharacter>();
        for (MultiStateAttribute attr : attrs) {
            chars.add(attr.getCharacter());
        }

        return chars;
    }

    private static boolean rowsMatchCharactersAtColumn(TabularKeyRow row1, TabularKeyRow row2, int columnIndex) {
        List<MultiStateAttribute> row1ColumnAttrs = row1.getAllAttributesForColumn(columnIndex);
        List<MultiStateCharacter> row1ColumnChars = getCharactersFromAttributes(row1ColumnAttrs);

        List<MultiStateAttribute> row2ColumnAttrs = row2.getAllAttributesForColumn(columnIndex);
        List<MultiStateCharacter> row2ColumnChars = getCharactersFromAttributes(row2ColumnAttrs);

        return row1ColumnChars.equals(row2ColumnChars);
    }
    
    public static String formatPresetCharacters(KeyContext context) {
        StringBuilder builder = new StringBuilder();
        
        LinkedHashMap<Pair<Integer, Integer>, Integer> presetCharactersMap = context.getPresetCharacters();
        
        for (Pair<Integer, Integer> columnGroupPair: presetCharactersMap.keySet()) {
            int columnNumber = columnGroupPair.getFirst();
            int groupNumber = columnGroupPair.getSecond();
            int characterNumber = presetCharactersMap.get(columnGroupPair);
            
            builder.append(String.format("%s,%s:%s", characterNumber, columnNumber, groupNumber));
            builder.append(" ");
        }
        
        return builder.toString().trim();
    }
    
    public static String formatCharacterReliabilities(KeyContext context, String valueSeparator, String rangeSeparator) {
        List<Character> charsList = context.getDataSet().getCharactersAsList(); 
        List<Integer> charNumbersList = new ArrayList<Integer>();
        List<Double> charReliabiltiesList = new ArrayList<Double>();
        
        for (Character ch: charsList) {
            charNumbersList.add(ch.getCharacterId());
            charReliabiltiesList.add((double)ch.getReliability());
        }

        return formatIndexValuePairs(charNumbersList, charReliabiltiesList, valueSeparator, rangeSeparator);
    }
    
    public static String formatTaxonAbunances(KeyContext context, String valueSeparator, String rangeSeparator) {
        List<Item> taxaList = context.getDataSet().getItemsAsList();
        List<Integer> taxaNumbersList = new ArrayList<Integer>();
        List<Double> taxaAbundancesList = new ArrayList<Double>();
        
        for (Item taxon: taxaList) {
            taxaNumbersList.add(taxon.getItemNumber());
            taxaAbundancesList.add(context.getItemAbundancy(taxon.getItemNumber()));
        }

        return formatIndexValuePairs(taxaNumbersList, taxaAbundancesList, valueSeparator, rangeSeparator);
    }
    
    private static String formatIndexValuePairs(List<Integer> indicies, List<Double> values, String valueSeparator, String rangeSeparator) {
        DecimalFormat formatter = new DecimalFormat("#,##0.#");
        
        StringBuilder builder = new StringBuilder();
        
        int startRange = 0;
        int previousIndex = 0;
        double rangeValue = 0;
        
        for (int i=0; i < indicies.size(); i++) {
            int index = indicies.get(i);
            double value = values.get(i);
            
            if (i == 0) {
                startRange = index;
                rangeValue = value;
            } else {
                if (previousIndex < index - 1 || value != rangeValue) {
                    builder.append(" ");
                    builder.append(startRange);
                    
                    if (previousIndex != startRange) {
                        builder.append(rangeSeparator);
                        builder.append(previousIndex);
                    }
                    
                    builder.append(valueSeparator);
                    builder.append(formatter.format(rangeValue));
                    
                    startRange = index;
                    rangeValue = value;
                }
                
                if (i == indicies.size() - 1) {
                    builder.append(" ");
                    builder.append(startRange);
                    
                    if (index != startRange) {
                        builder.append(rangeSeparator);
                        builder.append(index);
                    }
                    
                    builder.append(valueSeparator);
                    builder.append(formatter.format(rangeValue));
                    
                    startRange = index;
                    rangeValue = value;
                }
            }
            
            previousIndex = index;
        }
        
        return builder.toString().trim();
    }

}
