/*******************************************************************************
 * Copyright (C) 2011 Atlas of Living Australia
 * All Rights Reserved.
 *   
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *   
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ******************************************************************************/
package au.org.ala.delta.intkey.model;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.FloatRange;
import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.directives.validation.DirectiveException;
import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileEncoding;
import au.org.ala.delta.io.BinFileMode;
import au.org.ala.delta.model.Attribute;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterDependency;
import au.org.ala.delta.model.CharacterFactory;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DefaultDataSetFactory;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.IntegerAttribute;
import au.org.ala.delta.model.IntegerCharacter;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;
import au.org.ala.delta.model.MultiStateCharacter;
import au.org.ala.delta.model.RealAttribute;
import au.org.ala.delta.model.RealCharacter;
import au.org.ala.delta.model.TextAttribute;
import au.org.ala.delta.model.TextCharacter;
import au.org.ala.delta.model.image.Image;
import au.org.ala.delta.model.image.ImageOverlay;
import au.org.ala.delta.model.image.ImageOverlayParser;
import au.org.ala.delta.model.image.ImageSettings.FontInfo;
import au.org.ala.delta.model.image.ImageType;
import au.org.ala.delta.model.impl.CharacterData;
import au.org.ala.delta.model.impl.DefaultCharacterData;
import au.org.ala.delta.model.impl.DefaultImageData;
import au.org.ala.delta.model.impl.ItemData;
import au.org.ala.delta.model.impl.SimpleAttributeData;
import au.org.ala.delta.util.Pair;
import au.org.ala.delta.util.Utils;

public final class IntkeyDatasetFileReader {

    private static String DEFAULT_OR_WORD = "or";

    public static IntkeyDataset readDataSet(File charactersFile, File itemsFile) {

        // TODO should modify BinFile so that you can pass in a File.
        BinFile charBinFile = new BinFile(charactersFile.getAbsolutePath(), BinFileMode.FM_READONLY);
        BinFile itemBinFile = new BinFile(itemsFile.getAbsolutePath(), BinFileMode.FM_READONLY);

        IntkeyDataset ds = new IntkeyDataset();
        CharactersFileHeader charFileHeader = new CharactersFileHeader();
        ItemsFileHeader itemFileHeader = new ItemsFileHeader();
        List<au.org.ala.delta.model.Character> characters = new ArrayList<au.org.ala.delta.model.Character>();
        List<Item> taxa = new ArrayList<Item>();

        readCharactersFileHeader(charBinFile, charFileHeader);
        readItemsFileHeader(itemBinFile, itemFileHeader);

        // Check number of characters is same in two files
        if (charFileHeader.getNC() != itemFileHeader.getNChar()) {
            throw new RuntimeException("Characters and taxa files do not match");
        }

        // Check stated record length in items file is correct
        if (itemFileHeader.getLRec() != Constants.RECORD_LENGTH_INTEGERS) {
            throw new RuntimeException("Record length incorrect");
        }

        // Check file is correct version

        // Not sure why rpOmitOr is being checked here. Original syntax was
        // "fparam[last_used+1] != 0", where
        // last_used was set to 26. fparam was the array holding all of the
        // integers in the record. CPF 4/4/2011.
        if (itemFileHeader.getMajorVer() != Constants.DATASET_MAJOR_VERSION || (itemFileHeader.getMinorVer() != Constants.DATASET_MINOR_VERSION && itemFileHeader.getRpOmitOr() != 0)) {
            throw new RuntimeException("Incorrect file version");
        }

        ds.setChineseFormat(itemFileHeader.getChineseFmt() != 0);

        readHeadingsAndValidationString(charFileHeader, charBinFile, itemBinFile, ds);

        readTaxonData(itemFileHeader, itemBinFile, taxa);
        readCharacters(charFileHeader, itemFileHeader, charBinFile, itemBinFile, characters, ds);

        readCharacterImages(charFileHeader, charBinFile, itemFileHeader, itemBinFile, characters);
        readStartupImages(charFileHeader, charBinFile, ds);
        readCharacterKeywordImages(charFileHeader, charBinFile, ds);
        readTaxonKeywordImages(charFileHeader, charBinFile, ds);
        readOrWord(charFileHeader, charBinFile, ds);
        readOverlayFonts(charFileHeader, charBinFile, ds);
        readCharacterItemSubheadings(charFileHeader, charBinFile, characters, ds);
        readRealCharacterKeyStateBoundaries(itemFileHeader, itemBinFile, characters);
        readTaxonImages(itemFileHeader, itemBinFile, taxa);

        ds.setCharactersFile(charactersFile);
        ds.setItemsFile(itemsFile);
        ds.setCharactersFileHeader(charFileHeader);
        ds.setItemsFileHeader(itemFileHeader);
        ds.setCharacters(characters);
        ds.setTaxa(taxa);

        // Dataset needs a reference to the open items file so that
        // attribute data can be read on demand later.
        ds.setItemsBinFile(itemBinFile);

        // Close the open characters file as it is no longer needed
        charBinFile.close();

        return ds;
    }

    private static void readCharactersFileHeader(BinFile charBinFile, CharactersFileHeader charFileHeader) {
        // read first record which contains header file information;

        ByteBuffer headerBytes = readRecord(charBinFile, 1);

        // read first record of characters file
        charFileHeader.setNC(headerBytes.getInt()); // 0

        headerBytes.getInt(); // 1 - maxDes - not used.

        charFileHeader.setRpCdes(headerBytes.getInt()); // 2
        charFileHeader.setRpStat(headerBytes.getInt()); // 3
        charFileHeader.setRpChlp(headerBytes.getInt()); // 4
        charFileHeader.setRpChlpGrp(headerBytes.getInt()); // 5
        charFileHeader.setRpChlpFmt1(headerBytes.getInt()); // 6
        charFileHeader.setRpChlpFmt2(headerBytes.getInt()); // 7
        charFileHeader.setRpCImagesC(headerBytes.getInt()); // 8
        charFileHeader.setRpStartupImages(headerBytes.getInt()); // 9
        charFileHeader.setRpCKeyImages(headerBytes.getInt()); // 10
        charFileHeader.setRpTKeyImages(headerBytes.getInt()); // 11
        charFileHeader.setRpHeading(headerBytes.getInt()); // 12
        charFileHeader.setRpRegSubHeading(headerBytes.getInt()); // record
                                                                 // pointer to
                                                                 // registration
                                                                 // subheading
                                                                 // (13)
        charFileHeader.setRpValidationString(headerBytes.getInt()); // record
                                                                    // pointer
                                                                    // to
                                                                    // validation
                                                                    // string
                                                                    // for
                                                                    // registered
                                                                    // dataset
                                                                    // (14)

        headerBytes.getInt(); // 15 - record number for character mask - not
                              // used.

        charFileHeader.setRpOrWord(headerBytes.getInt()); // 16
        charFileHeader.setRpCheckForCd(headerBytes.getInt()); // 17
        charFileHeader.setRpFont(headerBytes.getInt()); // 18
        charFileHeader.setRpItemSubHead(headerBytes.getInt()); // 19

        headerBytes.position(Constants.RECORD_LENGTH_INTEGERS - 1);

        charFileHeader.setCptr(headerBytes.getInt());
    }

    private static void readItemsFileHeader(BinFile itemBinFile, ItemsFileHeader itemFileHeader) {

        ByteBuffer headerBytes = readRecord(itemBinFile, 1);

        itemFileHeader.setNItem(headerBytes.getInt()); // number of items (0)
        itemFileHeader.setNChar(headerBytes.getInt()); // number of characters
                                                       // (1)
        itemFileHeader.setMs(headerBytes.getInt()); // maximum number of states
                                                    // (2)

        headerBytes.getInt(); // 3 - MaxDat - not used
        itemFileHeader.setLRec(headerBytes.getInt()); // 4 - record length used
                                                      // in items file

        itemFileHeader.setRpTnam(headerBytes.getInt()); // record pointer to
                                                        // taxon names (5)
        itemFileHeader.setRpSpec(headerBytes.getInt()); // record pointer to
                                                        // specifications (6)
        itemFileHeader.setRpMini(headerBytes.getInt()); // record pointer to
                                                        // minima of integer
                                                        // characters (7)
        itemFileHeader.setLDep(headerBytes.getInt()); // length of dependency
                                                      // array (8)
        itemFileHeader.setRpCdep(headerBytes.getInt()); // record pointer to
                                                        // character dependency
                                                        // array (9)
        itemFileHeader.setLinvdep(headerBytes.getInt()); // length of inverted
                                                         // dependency array
                                                         // (10)
        itemFileHeader.setRpInvdep(headerBytes.getInt()); // record pointer to
                                                          // inverted
                                                          // dependency array
                                                          // (11)
        itemFileHeader.setRpCdat(headerBytes.getInt()); // record pointer to
                                                        // data for each
                                                        // character (12)
        itemFileHeader.setLSbnd(headerBytes.getInt()); // length of state
                                                       // bounds array (13)
        itemFileHeader.setLkstat(Math.max(1, headerBytes.getInt())); // length
                                                                     // of key
                                                                     // states
                                                                     // array
                                                                     // (14)

        itemFileHeader.setMajorVer(headerBytes.getInt()); // 15

        itemFileHeader.setRpNkbd(headerBytes.getInt()); // record pointer to
                                                        // key state bounds
                                                        // array (16)
        itemFileHeader.setMaxInt(headerBytes.getInt()); // maximum integer
                                                        // value (17)

        headerBytes.getInt(); // 18 - Maxtxt1 - not used
        headerBytes.getInt(); // 19 - Maxtxt2 - not used
        itemFileHeader.setMinorVer(headerBytes.getInt()); // 20

        itemFileHeader.setTaxonImageChar(headerBytes.getInt()); // character
                                                                // specifying
                                                                // taxon images
                                                                // (21)
        itemFileHeader.setRpCimagesI(headerBytes.getInt()); // pointer to
                                                            // character images
                                                            // (22)
        itemFileHeader.setRpTimages(headerBytes.getInt()); // pointer to taxon
                                                           // images (23)
        itemFileHeader.setEnableDeltaOutput(headerBytes.getInt()); // whether
                                                                   // to allow
                                                                   // DELTA
                                                                   // output
                                                                   // via
                                                                   // OUTPUT
                                                                   // SUMMARY
                                                                   // command
                                                                   // (24)
        itemFileHeader.setChineseFmt(headerBytes.getInt()); // whether chinese
                                                            // character set
                                                            // (25)
        itemFileHeader.setRpCsynon(headerBytes.getInt()); // record pointer to
                                                          // characters for
                                                          // synonomy (26)
        itemFileHeader.setRpOmitOr(headerBytes.getInt()); // record pointer to
                                                          // "omit or" list of
                                                          // characters (27)
        itemFileHeader.setRpNext(headerBytes.getInt()); // pointer to second
                                                        // parameter record
                                                        // (28)

        itemFileHeader.setDupItemPtr(headerBytes.getInt()); // pointer to
                                                            // duplicated item
                                                            // name mask (29:
                                                            // Constants.LREC -
                                                            // 3)
        itemFileHeader.setTptr(headerBytes.getInt()); // pointer to b-tree and
                                                      // image masks appended
                                                      // to items file (30:
                                                      // Constants.LREC - 2)
        itemFileHeader.setLbtree(headerBytes.getInt()); // length of btree in
                                                        // bytes (31:
                                                        // Constants.LREC - 1)

        if (itemFileHeader.getRpNext() > 0) {
            ByteBuffer secondHeaderBytes = readRecord(itemBinFile, itemFileHeader.getRpNext());

            itemFileHeader.setRpUseCc(secondHeaderBytes.getInt());
            int rpTlinks1 = secondHeaderBytes.getInt();
            itemFileHeader.setRpOmitPeriod(secondHeaderBytes.getInt());
            itemFileHeader.setRpNewPara(secondHeaderBytes.getInt());
            itemFileHeader.setRpNonAutoCc(secondHeaderBytes.getInt());
            int rpTlinks2 = secondHeaderBytes.getInt();

            itemFileHeader.setRpTlinks(new int[] { rpTlinks1, rpTlinks2 });

        } else {
            itemFileHeader.setRpUseCc(0);
            itemFileHeader.setRpTlinks(new int[] { 0, 0 });
            itemFileHeader.setRpOmitPeriod(0);
            itemFileHeader.setRpNewPara(0);
            itemFileHeader.setRpNonAutoCc(0);
        }
    }

    private static void readHeadingsAndValidationString(CharactersFileHeader charFileHeader, BinFile charBinFile, BinFile itemBinFile, IntkeyDataset ds) {
        // read and display data heading
        BinFile hFile;
        int recno;
        if (charFileHeader.getRpHeading() > 0) // heading is in chars file
        {
            hFile = charBinFile;
            recno = charFileHeader.getRpHeading();
        } else // heading is in items file
        {
            hFile = itemBinFile;
            recno = 2;
        }

        String heading = readReferencedString(hFile, recno);
        ds.setHeading(heading);
        // output to log window
        // set as heading of main window

        if (charFileHeader.getRpRegSubHeading() > 0) {
            // read and display registered dataset subheading
            ds.setSubHeading(readReferencedString(hFile, charFileHeader.getRpRegSubHeading()));
        }

        if (charFileHeader.getRpValidationString() > 0) {
            // read validation string
            ds.setValidationString(readReferencedString(hFile, charFileHeader.getRpValidationString()));
        }
    }

    private static void readCharacters(CharactersFileHeader charFileHeader, ItemsFileHeader itemFileHeader, BinFile charBinFile, BinFile itemBinFile, List<Character> characters, IntkeyDataset ds) {

        int numChars = charFileHeader.getNC();

        // READ NUMBER OF CHARACTER STATES
        seekToRecord(charBinFile, charFileHeader.getRpStat());
        List<Integer> numCharacterStates = readIntegerList(charBinFile, numChars);

        // READ CHARACTER TYPES
        seekToRecord(itemBinFile, itemFileHeader.getRpSpec());
        List<Integer> charTypesList = readIntegerList(itemBinFile, numChars);

        // Used to determine whether or not output to delta format is permitted
        // - see below.
        int charTypeSum = 0;

        for (int i = 0; i < numChars; i++) {
            charTypeSum += charTypesList.get(i);

            int charType = Math.abs(charTypesList.get(i));

            au.org.ala.delta.model.Character newChar = null;

            switch (charType) {
            case 1:
                newChar = CharacterFactory.newCharacter(CharacterType.UnorderedMultiState, i + 1);
                break;
            case 2:
                newChar = CharacterFactory.newCharacter(CharacterType.OrderedMultiState, i + 1);
                break;
            case 3:
                newChar = CharacterFactory.newCharacter(CharacterType.IntegerNumeric, i + 1);
                break;
            case 4:
                newChar = CharacterFactory.newCharacter(CharacterType.RealNumeric, i + 1);
                break;
            case 5:
                newChar = CharacterFactory.newCharacter(CharacterType.Text, i + 1);
                break;
            default:
                throw new RuntimeException("Unrecognized character type");
            }

            CharacterData impl = new DefaultCharacterData();
            newChar.setImpl(impl);

            // A char type of -4 indicates that the character is an integer
            // represented as a real.
            if (charTypesList.get(i) == -4) {
                ((RealCharacter) newChar).setIntegerRepresentedAsReal(true);
            }

            characters.add(newChar);
        }

        // A checksum is supplied in the items file. If this checksum matches
        // the sum of the
        // integers used to specify the character types, delta output is
        // enabled. Otherwise
        // delta output is disabled.
        readEnableDeltaOutput(charTypeSum, itemFileHeader, ds);

        int recordsSpannedByCharTypes = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

        // read numbers of states from items file and check for compatability
        // (only compare multistates because if ICHARS and IITEMS are generated
        // separately, numerics characters with units will differ)
        seekToRecord(itemBinFile, itemFileHeader.getRpSpec() + recordsSpannedByCharTypes);
        List<Integer> itemsFileNumCharacterStates = readIntegerList(itemBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            Character ch = characters.get(i);
            if (ch instanceof MultiStateCharacter) {
                int numStatesFromCharsFile = numCharacterStates.get(i);
                int numStatesFromItemsFile = itemsFileNumCharacterStates.get(i);
                if (numStatesFromItemsFile != numStatesFromCharsFile) {
                    throw new RuntimeException("Numbers of states for characters differ between characters file and items file");
                }
            }
        }

        int recordsSpannedByNumCharStates = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

        // READ CHARACTER RELIABILITIES
        seekToRecord(itemBinFile, itemFileHeader.getRpSpec() + (recordsSpannedByCharTypes + recordsSpannedByNumCharStates));

        List<Float> reliabilityList = readFloatList(itemBinFile, numChars);
        for (int i = 0; i < numChars; i++) {
            Character ch = characters.get(i);
            float reliability = reliabilityList.get(i);
            ch.setReliability(reliability);
        }

        readCharacterDescriptionsAndStates(charFileHeader, charBinFile, characters, numCharacterStates);
        readCharacterNotes(charFileHeader, charBinFile, characters, ds);
        readCharacterMinimumsAndMaximums(itemFileHeader, itemBinFile, characters);
        readCharacterDependencies(itemFileHeader, itemBinFile, characters);
        // readCharacterTaxonData();

        // READ CONTAINS SYNONMY INFORMATION
        List<Integer> synonmyInfoList = null;
        if (itemFileHeader.getRpCsynon() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpCsynon());
            synonmyInfoList = readIntegerList(itemBinFile, numChars);
        }

        // READ OMIT OR
        List<Integer> omitOrList = null;
        if (itemFileHeader.getRpOmitOr() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpOmitOr());
            omitOrList = readIntegerList(itemBinFile, numChars);
        }

        // READ USE CONTROLLING CHARACTERS FIRST
        List<Integer> useCcList = null;
        if (itemFileHeader.getRpUseCc() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpUseCc());
            useCcList = readIntegerList(itemBinFile, numChars);
        }

        // READ OMIT PERIOD
        List<Integer> omitPeriodList = null;
        if (itemFileHeader.getRpOmitPeriod() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpOmitPeriod());
            omitPeriodList = readIntegerList(itemBinFile, numChars);
        }

        // READ NEW PARAGRAPH
        List<Integer> newParagraphList = null;
        if (itemFileHeader.getRpNewPara() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpNewPara());
            newParagraphList = readIntegerList(itemBinFile, numChars);
        }

        // READ NON AUTOMATIC CONTROLLING CHARACTERS
        List<Integer> nonAutoCcList = null;
        if (itemFileHeader.getRpNonAutoCc() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpNonAutoCc());
            nonAutoCcList = readIntegerList(itemBinFile, numChars);
        }

        List<TextCharacter> synonymyCharacters = new ArrayList<TextCharacter>();

        for (int i = 0; i < numChars; i++) {
            Character ch = characters.get(i);

            if (synonmyInfoList != null) {
                ch.setContainsSynonmyInformation(synonmyInfoList.get(i) != 0);
                if (ch.getContainsSynonmyInformation()) {
                    if (ch instanceof TextCharacter) {
                        synonymyCharacters.add((TextCharacter) ch);
                    } else {
                        throw new RuntimeException("Only text characters can contains synonymy information");
                    }
                }
            }

            if (omitOrList != null) {
                ch.setOmitOr(omitOrList.get(i) != 0);
            }

            if (useCcList != null) {
                ch.setUseCc(useCcList.get(i) != 0);
            }

            if (omitPeriodList != null) {
                ch.setOmitPeriod(omitPeriodList.get(i) != 0);
            }

            if (newParagraphList != null) {
                ch.setNewParagraph(newParagraphList.get(i) != 0);
            }

            if (nonAutoCcList != null) {
                ch.setNonAutoCc(nonAutoCcList.get(i) != 0);
            }
        }

        ds.setSynonymyCharacters(synonymyCharacters);

    }

    private static void readCharacterDescriptionsAndStates(CharactersFileHeader charFileHeader, BinFile charBinFile, List<Character> characters, List<Integer> numCharacterStates) {
        int numChars = charFileHeader.getNC();

        // READ CHARACTER DESCRIPTIONS
        seekToRecord(charBinFile, charFileHeader.getRpCdes());

        List<Integer> charDescriptionRecordIndicies = readIntegerList(charBinFile, numChars);

        for (int i = 0; i < numChars; i++) {
            au.org.ala.delta.model.Character ch = characters.get(i);

            int descRecordIndex = charDescriptionRecordIndicies.get(i);
            seekToRecord(charBinFile, descRecordIndex);

            int numStatesForChar = numCharacterStates.get(i);
            List<Integer> charDescriptionsLengths = readIntegerList(charBinFile, numStatesForChar + 1);
            int lengthTotal = 0;

            for (int charDescriptionLength : charDescriptionsLengths) {
                lengthTotal += charDescriptionLength;
            }

            int recordsSpannedByDescLengths = recordsSpannedByBytes((numStatesForChar + 1) * Constants.SIZE_INT_IN_BYTES);

            List<String> charStateDescriptions = new ArrayList<String>();

            seekToRecord(charBinFile, descRecordIndex + recordsSpannedByDescLengths);
            ByteBuffer descBuffer = charBinFile.readByteBuffer(lengthTotal);

            for (int k = 0; k < charDescriptionsLengths.size(); k++) {
                int len = charDescriptionsLengths.get(k);
                byte[] descArray = new byte[len];
                descBuffer.get(descArray);

                String descriptionText = BinFileEncoding.decode(descArray);

                if (k == 0) {
                    // First description listed is the character description
                    ch.setDescription(descriptionText);
                } else {
                    charStateDescriptions.add(descriptionText);
                }
            }

            if (ch instanceof IntegerCharacter) {
                if (charStateDescriptions.size() == 1) {
                    ((IntegerCharacter) ch).setUnits(charStateDescriptions.get(0));
                } else if (charStateDescriptions.size() > 1) {
                    throw new RuntimeException("Integer characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof RealCharacter) {
                if (charStateDescriptions.size() == 1) {
                    ((RealCharacter) ch).setUnits(charStateDescriptions.get(0));
                } else if (charStateDescriptions.size() > 1) {
                    throw new RuntimeException("Real numeric characters should only have one state listed which represents the units description.");
                }
            } else if (ch instanceof MultiStateCharacter) {
                MultiStateCharacter multiStateChar = (MultiStateCharacter) ch;

                multiStateChar.setNumberOfStates(charStateDescriptions.size());

                for (int l = 0; l < charStateDescriptions.size(); l++) {
                    multiStateChar.setState(l + 1, charStateDescriptions.get(l));
                }
            } else {
                if (charStateDescriptions.size() > 0) {
                    throw new RuntimeException("Text characters should not have a state specified");
                }
            }
        }
    }

    private static void readCharacterNotes(CharactersFileHeader charFileHeader, BinFile charBinFile, List<Character> characters, IntkeyDataset ds) {
        int numChars = charFileHeader.getNC();

        // READ TEXT OF CHARACTER NOTES
        if (charFileHeader.getRpChlp() > 0) {
            List<String> characterNotes = readStringList(charBinFile, charFileHeader.getRpChlp(), numChars);

            for (int i = 0; i < numChars; i++) {
                characters.get(i).setNotes(characterNotes.get(i));
            }

        }

        // READ CHARACTER NOTES FORMATTING INFORMATION

        // Formatting information for when character notes are output to main
        // intkey window
        if (charFileHeader.getRpChlpFmt1() > 0) {
            ds.setMainCharNotesFormattingInfo(readReferencedString(charBinFile, charFileHeader.getRpChlpFmt1()));
        }

        // Formatting information for when character notes are output to help
        // window
        if (charFileHeader.getRpChlpFmt2() > 0) {
            ds.setHelpCharNotesFormattingInfo(readReferencedString(charBinFile, charFileHeader.getRpChlpFmt2()));
        }
    }

    private static void readCharacterMinimumsAndMaximums(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Character> characters) {
        int numChars = itemFileHeader.getNChar();

        if (itemFileHeader.getRpMini() != 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpMini());

            List<Integer> minimumValues = readIntegerList(itemBinFile, numChars);

            int recordsSpannedByMinimumValues = recordsSpannedByBytes(numChars * Constants.SIZE_INT_IN_BYTES);

            seekToRecord(itemBinFile, itemFileHeader.getRpMini() + recordsSpannedByMinimumValues);

            List<Integer> maximumValues = readIntegerList(itemBinFile, numChars);

            for (int i = 0; i < numChars; i++) {
                Character c = characters.get(i);

                if (c instanceof IntegerCharacter) {
                    IntegerCharacter intChar = (IntegerCharacter) c;

                    int minValue = minimumValues.get(i);
                    int maxValue = maximumValues.get(i);

                    intChar.setMinimumValue(minValue);
                    intChar.setMaximumValue(maxValue);
                }
            }
        }
    }

    private static void readCharacterDependencies(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Character> characters) {
        DeltaDataSetFactory factory = new DefaultDataSetFactory();
        int numChars = itemFileHeader.getNChar();

        // If LDep is 0, there are no dependencies. Otherwise dependency data
        // consists of LDep integers, starting at record
        // rpCdep.
        if (itemFileHeader.getLDep() >= numChars && itemFileHeader.getRpCdep() > 0) {
            seekToRecord(itemBinFile, itemFileHeader.getRpCdep());
            List<Integer> dependencyData = readIntegerList(itemBinFile, itemFileHeader.getLDep());
            // At the start of the dependency data there is an integer value for
            // each character.
            // If non zero, the value is an offset further down the list where
            // its dependency data is.
            // Otherwise the character does not have any dependent characters.
            for (int i = 0; i < numChars; i++) {
                int charDepIndex = dependencyData.get(i);
                if (charDepIndex > 0) {
                    au.org.ala.delta.model.Character c = characters.get(i);
                    if (!(c instanceof MultiStateCharacter)) {
                        throw new RuntimeException("Only multistate characters can be controlling characters");
                    }

                    MultiStateCharacter controllingChar = (MultiStateCharacter) c;

                    int numStates = controllingChar.getStates().length;

                    // The dependency data for each character consists of one
                    // integer for each of the character's states. If the
                    // integer
                    // value listed for a state is non-zero, the value is an
                    // offset pointing to further down the list where
                    // the state's dependency data is.
                    int stateDepIndiciesStart = charDepIndex - 1;
                    int stateDepIndiciesEnd = charDepIndex - 1 + numStates;
                    List<Integer> stateDepRecordIndicies = dependencyData.subList(stateDepIndiciesStart, stateDepIndiciesEnd);

                    // We need to coalesce the dependency data so that we have
                    // one CharacterDependency object per
                    // controlling character and set of states that make a set
                    // of dependent characters inapplicable.
                    // Use this map to keep track of the state ids that make the
                    // same set of dependent characters
                    // inapplicable.
                    Map<Set<Integer>, Set<Integer>> depCharsToStateIds = new HashMap<Set<Integer>, Set<Integer>>();

                    for (int j = 0; j < numStates; j++) {
                        Integer stateId = j + 1;

                        int stateDepRecordIndex = stateDepRecordIndicies.get(j);

                        if (stateDepRecordIndex > 0) {
                            // First value listed in the state's dependency data
                            // is the number of character ranges dependent on
                            // that state.
                            int numDependentCharRanges = dependencyData.get(stateDepRecordIndex - 1);

                            // Immediately after the range information is listed
                            // - the upper and lower bound is listed for each
                            // range.
                            List<Integer> rangeNumbers = dependencyData.subList(stateDepRecordIndex, stateDepRecordIndex + (numDependentCharRanges * 2));

                            Set<Integer> dependentChars = new HashSet<Integer>();

                            for (int k = 0; k < numDependentCharRanges * 2; k = k + 2) {
                                int lowerBound = rangeNumbers.get(k);
                                int upperBound = rangeNumbers.get(k + 1);

                                IntRange r = new IntRange(lowerBound, upperBound);

                                for (int dependentChar : r.toArray()) {
                                    dependentChars.add(dependentChar);
                                }
                            }

                            if (depCharsToStateIds.containsKey(dependentChars)) {
                                Set<Integer> stateSet = depCharsToStateIds.get(dependentChars);
                                stateSet.add(stateId);
                            } else {
                                Set<Integer> stateSet = new HashSet<Integer>();
                                stateSet.add(stateId);
                                depCharsToStateIds.put(dependentChars, stateSet);
                            }
                        }
                    }

                    // Now that we have coalesced the dependency data into the
                    // form we need, we can
                    // create the CharacterDependency objects.
                    for (Set<Integer> depCharsSet : depCharsToStateIds.keySet()) {
                        Set<Integer> stateSet = depCharsToStateIds.get(depCharsSet);
                        CharacterDependency charDep = factory.createCharacterDependency(controllingChar, stateSet, depCharsSet);
                        c.addDependentCharacters(charDep);
                        for (int idxDependentChar : depCharsSet) {
                            // need to subtract one from the index because
                            // the data file uses 1 based indexes while
                            // java uses zero based indexes.
                            Character dependentCharacter = characters.get(idxDependentChar - 1);

                            dependentCharacter.addControllingCharacter(charDep);
                        }
                    }
                }
            }
        }
    }

    private static void readTaxonData(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Item> taxa) {

        int numItems = itemFileHeader.getNItem();

        for (int i = 0; i < numItems; i++) {
            ItemData itemData = new IntkeyItemData();
            Item item = new Item(itemData, i + 1);
            taxa.add(item);
        }

        // READ TAXON NAMES - rpTnam
        seekToRecord(itemBinFile, itemFileHeader.getRpTnam());

        List<Integer> taxonNameOffsets = readIntegerList(itemBinFile, numItems + 1);

        int recordsSpannedByOffsets = recordsSpannedByBytes(numItems * Constants.SIZE_INT_IN_BYTES);

        seekToRecord(itemBinFile, itemFileHeader.getRpTnam() + recordsSpannedByOffsets);

        ByteBuffer nameBuffer = itemBinFile.readByteBuffer(taxonNameOffsets.get(taxonNameOffsets.size() - 1));
        nameBuffer.position(0);

        for (int i = 0; i < numItems; i++) {
            int start = taxonNameOffsets.get(i);
            int end = taxonNameOffsets.get(i + 1);
            int nameLength = end - start;
            byte[] nameArray = new byte[nameLength];
            nameBuffer.get(nameArray);
            taxa.get(i).setDescription(BinFileEncoding.decode(nameArray));
        }

        readTaxonLinksFiles(itemFileHeader, itemBinFile, taxa);
    }

    private static void readCharacterImages(CharactersFileHeader charFileHeader, BinFile charBinFile, ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Character> characters) {
        int numChars = charFileHeader.getNC();

        // Character image info has been shifted from items file to characters
        // file.
        // However, to maintain compatability with older datasets, need to
        // determine
        // in which file the information resides
        int rpCImages = 0;
        BinFile imagesFile = null;
        if (charFileHeader.getRpCImagesC() != 0) {
            rpCImages = charFileHeader.getRpCImagesC();
            imagesFile = charBinFile;
        } else if (itemFileHeader.getRpCimagesI() != 0) {
            rpCImages = itemFileHeader.getRpCimagesI();
            imagesFile = itemBinFile;
        }

        if (rpCImages != 0) {
            List<String> charactersImageData = readStringList(imagesFile, rpCImages, numChars);

            for (int i = 0; i < numChars; i++) {
                Character ch = characters.get(i);

                String imagesData = charactersImageData.get(i);

                if (imagesData != null) {
                    List<Pair<String, String>> imageData = parseFileData(imagesData);
                    for (Pair<String, String> pair : imageData) {
                        Image image = createImage(pair.getFirst(), pair.getSecond(), ImageType.IMAGE_CHARACTER);
                        ch.addImage(image);
                    }
                }
            }
        }
    }

    private static void readTaxonImages(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Item> taxa) {
        int numItems = itemFileHeader.getNItem();
        int recNo = itemFileHeader.getRpTimages();

        if (recNo != 0) {
            List<String> taxaImageData = readStringList(itemBinFile, recNo, numItems);
            for (int i = 0; i < numItems; i++) {
                Item taxon = taxa.get(i);

                String imagesData = taxaImageData.get(i);

                if (imagesData != null) {
                    List<Pair<String, String>> imageData = parseFileData(imagesData);
                    for (Pair<String, String> pair : imageData) {
                        Image image = createImage(pair.getFirst(), pair.getSecond(), ImageType.IMAGE_TAXON);
                        taxon.addImage(image);
                    }
                }
            }
        }
    }

    private static void readStartupImages(CharactersFileHeader charFileHeader, BinFile charBinFile, IntkeyDataset ds) {
        if (charFileHeader.getRpStartupImages() > 0) {
            seekToRecord(charBinFile, charFileHeader.getRpStartupImages());

            int imageDataRecord = charBinFile.readInt();
            String startupImagesData = readReferencedString(charBinFile, imageDataRecord);
            if (!StringUtils.isEmpty(startupImagesData)) {
                List<Image> startupImages = new ArrayList<Image>();
                List<Pair<String, String>> imageData = parseFileData(startupImagesData);
                for (Pair<String, String> pair : imageData) {
                    Image image = createImage(pair.getFirst(), pair.getSecond(), ImageType.IMAGE_STARTUP);
                    startupImages.add(image);
                }
                ds.setStartupImages(startupImages);
            }
        }
    }

    private static void readCharacterKeywordImages(CharactersFileHeader charFileHeader, BinFile charBinFile, IntkeyDataset ds) {
        if (charFileHeader.getRpCKeyImages() > 0) {
            seekToRecord(charBinFile, charFileHeader.getRpCKeyImages());

            int imageDataRecord = charBinFile.readInt();
            String characterKeywordImagesData = readReferencedString(charBinFile, imageDataRecord);
            if (!StringUtils.isEmpty(characterKeywordImagesData)) {
                List<Image> characterKeywordImages = new ArrayList<Image>();
                List<Pair<String, String>> imageData = parseFileData(characterKeywordImagesData);
                for (Pair<String, String> pair : imageData) {
                    Image image = createImage(pair.getFirst(), pair.getSecond(), ImageType.IMAGE_CHARACTER_KEYWORD);
                    characterKeywordImages.add(image);
                }
                ds.setCharacterKeywordImages(characterKeywordImages);
            }
        }
    }

    private static void readTaxonKeywordImages(CharactersFileHeader charFileHeader, BinFile charBinFile, IntkeyDataset ds) {
        if (charFileHeader.getRpTKeyImages() > 0) {
            seekToRecord(charBinFile, charFileHeader.getRpTKeyImages());

            int imageDataRecord = charBinFile.readInt();
            String taxonKeywordImagesData = readReferencedString(charBinFile, imageDataRecord);
            if (!StringUtils.isEmpty(taxonKeywordImagesData)) {
                List<Image> taxonKeywordImages = new ArrayList<Image>();
                List<Pair<String, String>> imageData = parseFileData(taxonKeywordImagesData);
                for (Pair<String, String> pair : imageData) {
                    Image image = createImage(pair.getFirst(), pair.getSecond(), ImageType.IMAGE_TAXON_KEYWORD);
                    taxonKeywordImages.add(image);
                }
                ds.setTaxonKeywordImages(taxonKeywordImages);
            }
        }
    }

    private static void readOrWord(CharactersFileHeader charFileHeader, BinFile charBinFile, IntkeyDataset ds) {
        int recordNo = charFileHeader.getRpOrWord();
        String orWord = null;
        if (recordNo != 0) {
            seekToRecord(charBinFile, recordNo);
            int orWordLength = charBinFile.readInt();
            seekToRecord(charBinFile, recordNo + 1);
            orWord = readString(charBinFile, orWordLength);
        } else {
            orWord = DEFAULT_OR_WORD;
        }

        ds.setOrWord(orWord);
    }

    private static void readOverlayFonts(CharactersFileHeader charFileHeader, BinFile charBinFile, IntkeyDataset ds) {
        int recordNo = charFileHeader.getRpFont();
        if (recordNo != 0) {
            seekToRecord(charBinFile, recordNo);

            // single integer showing the number of fonts
            int numFonts = charBinFile.readInt();

            seekToRecord(charBinFile, recordNo + 1);
            List<Integer> fontTextLengths = readIntegerList(charBinFile, numFonts);

            int totalFontsLength = 0;
            for (int fontLength : fontTextLengths) {
                totalFontsLength += fontLength;
            }

            int recordsSpannedByFontTextLengths = recordsSpannedByBytes(numFonts * Constants.SIZE_INT_IN_BYTES);
            seekToRecord(charBinFile, recordNo + 1 + recordsSpannedByFontTextLengths);

            List<FontInfo> fonts = new ArrayList<FontInfo>();
            ByteBuffer fontTextData = charBinFile.readByteBuffer(totalFontsLength);
            for (int fontLength : fontTextLengths) {
                byte[] fontTextBytes = new byte[fontLength];
                fontTextData.get(fontTextBytes);
                String fontText = BinFileEncoding.decode(fontTextBytes);
                FontInfo fontInfo = parseOverlayFontString(fontText);
                fonts.add(fontInfo);
            }

            ds.setOverlayFonts(fonts);
        }
    }

    private static FontInfo parseOverlayFontString(String fontInfoStr) {

        String[] tokens = fontInfoStr.split(" ");
        int size = Integer.parseInt(tokens[0]);
        int weight = Integer.parseInt(tokens[1]);
        boolean italic = Integer.parseInt(tokens[2]) > 0;
        int pitch = Integer.parseInt(tokens[3]);
        int family = Integer.parseInt(tokens[4]);
        int charSet = Integer.parseInt(tokens[5]);
        String name = StringUtils.join(Arrays.copyOfRange(tokens, 6, tokens.length), ' ');

        return new FontInfo(size, weight, italic, pitch, family, charSet, name);
    }

    private static void readCharacterItemSubheadings(CharactersFileHeader charFileHeader, BinFile charBinFile, List<Character> characters, IntkeyDataset ds) {
        int numChars = charFileHeader.getNC();
        int recordNo = charFileHeader.getRpItemSubHead();
        if (recordNo != 0) {
            ds.setItemSubheadingsPresent(true);
            List<String> itemSubheadings = readStringList(charBinFile, recordNo, numChars);

            for (int i = 0; i < numChars; i++) {
                characters.get(i).setItemSubheading(itemSubheadings.get(i));
            }
        }
    }

    private static void readRealCharacterKeyStateBoundaries(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Character> characters) {
        int numChars = itemFileHeader.getNChar();
        int recNo = itemFileHeader.getRpNkbd();

        if (recNo != 0) {
            seekToRecord(itemBinFile, recNo);
            List<Integer> keyStateBoundariesRecordIndicies = readIntegerList(itemBinFile, numChars);

            for (int i = 0; i < numChars; i++) {
                Character ch = characters.get(i);
                if (ch instanceof RealCharacter) {
                    RealCharacter realChar = (RealCharacter) ch;

                    int keyStateBoundariesRecord = keyStateBoundariesRecordIndicies.get(i);
                    seekToRecord(itemBinFile, keyStateBoundariesRecord);
                    int numKeyStateBoundaries = itemBinFile.readInt();
                    seekToRecord(itemBinFile, keyStateBoundariesRecord + 1);

                    List<Float> keyStateBoundaries = readFloatList(itemBinFile, numKeyStateBoundaries);
                    realChar.setKeyStateBoundaries(keyStateBoundaries);
                }
            }
        }
    }

    // A checksum is supplied in the items file. If this checksum matches the
    // sum of the
    // integers used to specify the character types, delta output is enabled.
    // Otherwise
    // delta output is disabled.
    private static void readEnableDeltaOutput(int calculatedChecksum, ItemsFileHeader itemFileHeader, IntkeyDataset ds) {
        boolean deltaOutputEnabled = false;

        int fileChecksum = itemFileHeader.getEnableDeltaOutput();

        if (fileChecksum != 0) {
            if (fileChecksum == calculatedChecksum) {
                deltaOutputEnabled = true;
            }
        }

        ds.setDeltaOutputPermitted(deltaOutputEnabled);
    }

    private static void readTaxonLinksFiles(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Item> taxa) {
        int numItems = itemFileHeader.getNItem();

        // from TAXON LINKS Confor directive
        List<String> firstLinksFileData = null;

        // from SUBJECT FOR OUTPUT FILES Confor directive
        List<String> secondLinksFileData = null;

        if (itemFileHeader.getRpTlinks()[0] != 0) {
            firstLinksFileData = readStringList(itemBinFile, itemFileHeader.getRpTlinks()[0], numItems);
        }

        if (itemFileHeader.getRpTlinks()[1] != 0) {
            secondLinksFileData = readStringList(itemBinFile, itemFileHeader.getRpTlinks()[1], numItems);
        }

        for (int i = 0; i < numItems; i++) {
            Item it = taxa.get(i);

            List<Pair<String, String>> taxonLinks = new ArrayList<Pair<String, String>>();

            // links from SUBJECT FOR OUTPUT FILES Confor directive (there
            // should only
            // be one) go in the list first.
            if (secondLinksFileData != null) {
                if (secondLinksFileData.get(i) != null) {
                    List<Pair<String, String>> parsedLinks = parseFileData(secondLinksFileData.get(i));
                    for (Pair<String, String> pair : parsedLinks) {
                        String fileName = pair.getFirst();
                        String subject = pair.getSecond();
                        subject = subject.replace("<", "");
                        subject = subject.replace(">", "");
                        subject = subject.replace("@subject", "");
                        subject = subject.trim();
                        taxonLinks.add(new Pair<String, String>(fileName, subject));
                    }
                }
            }

            if (firstLinksFileData != null) {
                if (firstLinksFileData.get(i) != null) {
                    List<Pair<String, String>> parsedLinks = parseFileData(firstLinksFileData.get(i));
                    for (Pair<String, String> pair : parsedLinks) {
                        String fileName = pair.getFirst();
                        String subject = pair.getSecond();
                        subject = subject.replace("<", "");
                        subject = subject.replace(">", "");
                        subject = subject.replace("@subject", "");
                        subject = subject.trim();
                        taxonLinks.add(new Pair<String, String>(fileName, subject));
                    }
                }
            }

            it.setLinkFiles(taxonLinks);
        }
    }

    public static List<Attribute> readAttributesForCharacter(ItemsFileHeader itemFileHeader, BinFile itemBinFile, List<Character> characters, List<Item> taxa, int charNo) {
        List<Attribute> retList = new ArrayList<Attribute>();

        int numChars = itemFileHeader.getNChar();
        int numTaxa = itemFileHeader.getNItem();

        seekToRecord(itemBinFile, itemFileHeader.getRpCdat());
        List<Integer> charAttributeDataRecordIndicies = readIntegerList(itemBinFile, numChars);

        // Subtract 1 from the charNo because characters are zero indexed in
        // intkey API
        int charTaxonDataRecordIndex = charAttributeDataRecordIndicies.get(charNo - 1);
        au.org.ala.delta.model.Character c = characters.get(charNo - 1);

        seekToRecord(itemBinFile, charTaxonDataRecordIndex);

        if (c instanceof MultiStateCharacter) {

            MultiStateCharacter multiStateChar = (MultiStateCharacter) c;

            int bitsPerTaxon = multiStateChar.getStates().length + 1;
            int totalBitsNeeded = bitsPerTaxon * taxa.size();

            int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(totalBitsNeeded) / Double.valueOf(Byte.SIZE))).intValue();

            byte[] bytes = new byte[bytesToRead];
            itemBinFile.readBytes(bytes);
            boolean[] taxaData = Utils.byteArrayToBooleanArray(bytes);

            for (int j = 0; j < numTaxa; j++) {
                Item t = taxa.get(j);

                int startIndex = j * bitsPerTaxon;
                int endIndex = startIndex + bitsPerTaxon;

                boolean[] taxonData = Arrays.copyOfRange(taxaData, startIndex, endIndex);

                // Taxon data consists of a bit for each state, indicating
                // the states presence, followed by
                // a final bit signifying whether or not the character is
                // inapplicable for the taxon.
                boolean inapplicable = taxonData[taxonData.length - 1];

                HashSet<Integer> presentStates = new HashSet<Integer>();
                for (int k = 0; k < taxonData.length - 1; k++) {
                    boolean statePresent = taxonData[k];
                    if (statePresent) {
                        presentStates.add(k + 1);
                    }
                }

                SimpleAttributeData attrData = new SimpleAttributeData(presentStates.isEmpty(), inapplicable);
                MultiStateAttribute msAttr = new MultiStateAttribute(multiStateChar, attrData);
                msAttr.setItem(t);

                msAttr.setPresentStates(presentStates);

                retList.add(msAttr);
            }

        } else if (c instanceof IntegerCharacter) {
            IntegerCharacter intChar = (IntegerCharacter) c;
            int charMinValue = intChar.getMinimumValue();
            int charMaxValue = intChar.getMaximumValue();

            // 1 bit for all values below minimum, 1 bit for each value between
            // minimum and maximum (inclusive),
            // 1 bit for all values above maximum, 1 inapplicability bit.
            int bitsPerTaxon = charMaxValue - charMinValue + 4;
            int totalBitsNeeded = bitsPerTaxon * numTaxa;

            int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(totalBitsNeeded) / Double.valueOf(Byte.SIZE))).intValue();

            byte[] bytes = new byte[bytesToRead];
            itemBinFile.readBytes(bytes);
            boolean[] taxaData = Utils.byteArrayToBooleanArray(bytes);

            for (int j = 0; j < numTaxa; j++) {
                Item t = taxa.get(j);

                int startIndex = j * bitsPerTaxon;
                int endIndex = startIndex + bitsPerTaxon;

                boolean[] taxonData = Arrays.copyOfRange(taxaData, startIndex, endIndex);

                boolean inapplicable = taxonData[taxonData.length - 1];

                Set<Integer> presentValues = new HashSet<Integer>();
                for (int k = 0; k < taxonData.length - 1; k++) {
                    boolean present = taxonData[k];
                    if (present) {
                        presentValues.add(k + charMinValue - 1);
                    }
                }

                IntegerAttribute intAttr = new IntegerAttribute(intChar, new SimpleAttributeData(presentValues.isEmpty(), inapplicable));
                intAttr.setItem(t);
                intAttr.setPresentValues(presentValues);

                retList.add(intAttr);
            }

        } else if (c instanceof RealCharacter) {
            // Read NI inapplicability bits
            int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(numTaxa) / Double.valueOf(Byte.SIZE))).intValue();
            byte[] bytes = new byte[bytesToRead];
            itemBinFile.readBytes(bytes);
            boolean[] taxaInapplicabilityData = Utils.byteArrayToBooleanArray(bytes);

            int recordsSpannedByInapplicabilityData = recordsSpannedByBytes(bytesToRead);

            seekToRecord(itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData);

            // Read two float values per taxon
            List<Float> taxonData = readFloatList(itemBinFile, numTaxa * 2);

            for (int j = 0; j < numTaxa; j++) {
                Item t = taxa.get(j);

                float lowerFloat = taxonData.get(j * 2);
                float upperFloat = taxonData.get((j * 2) + 1);

                boolean inapplicable = taxaInapplicabilityData[j];

                // Character is unknown for the corresponding taxon if
                // lowerfloat > upperfloat
                boolean unknown = lowerFloat > upperFloat;

                RealAttribute realAttr = new RealAttribute((RealCharacter) c, new SimpleAttributeData(unknown, inapplicable));

                if (!unknown) {
                    FloatRange range = new FloatRange(lowerFloat, upperFloat);
                    realAttr.setPresentRange(range);
                }
                realAttr.setItem(t);

                retList.add(realAttr);
            }

        } else if (c instanceof TextCharacter) {
            TextCharacter textChar = (TextCharacter) c;

            // Read NI inapplicability bits
            int bytesToRead = Double.valueOf(Math.ceil(Double.valueOf(numTaxa) / Double.valueOf(Byte.SIZE))).intValue();
            byte[] bytes = new byte[bytesToRead];
            itemBinFile.readBytes(bytes);
            boolean[] taxaInapplicabilityData = Utils.byteArrayToBooleanArray(bytes);

            int recordsSpannedByInapplicabilityData = recordsSpannedByBytes(bytesToRead);

            seekToRecord(itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData);

            List<Integer> taxonTextDataOffsets = readIntegerList(itemBinFile, numTaxa + 1);

            int recordsSpannedByOffsets = recordsSpannedByBytes((numTaxa + 1) * Constants.SIZE_INT_IN_BYTES);

            seekToRecord(itemBinFile, charTaxonDataRecordIndex + recordsSpannedByInapplicabilityData + recordsSpannedByOffsets);

            ByteBuffer taxonTextData = itemBinFile.readByteBuffer(taxonTextDataOffsets.get(taxonTextDataOffsets.size() - taxonTextDataOffsets.get(0)));

            for (int j = 0; j < numTaxa; j++) {
                Item t = taxa.get(j);

                int lowerOffset = taxonTextDataOffsets.get(j);
                int upperOffset = taxonTextDataOffsets.get(j + 1);
                int textLength = upperOffset - lowerOffset;

                String txt = "";
                if (textLength > 0) {
                    byte[] textBytes = new byte[textLength];
                    taxonTextData.get(textBytes);

                    txt = BinFileEncoding.decode(textBytes);
                }

                boolean inapplicable = taxaInapplicabilityData[j];
                boolean unknown = StringUtils.isEmpty(txt);

                TextAttribute txtAttr = new TextAttribute(textChar, new SimpleAttributeData(unknown, inapplicable));
                try {
                	txtAttr.setText(txt);
                }
                catch (DirectiveException e) {
                	// The SimpleAttributeData implementation won't throw this Exception.
                }
                txtAttr.setItem(t);

                retList.add(txtAttr);

            }
        }

        return retList;
    }

    // --------------- UTILITY METHODS
    // --------------------------------------------------------

    // Note that records are 1 indexed.
    private static void seekToRecord(BinFile bFile, int recordNumber) {
        bFile.seek((recordNumber - 1) * Constants.RECORD_LENGTH_INTEGERS * Constants.SIZE_INT_IN_BYTES);
    }

    // Read the designed record from the file. Note that records are 1 indexed.
    private static ByteBuffer readRecord(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        return bFile.readByteBuffer(Constants.RECORD_LENGTH_INTEGERS * Constants.SIZE_INT_IN_BYTES);
    }

    private static String readString(BinFile bFile, int numBytes) {
        byte[] bytes = bFile.read(numBytes);
        return BinFileEncoding.decode(bytes);
    }

    // Helper method to deal with a common pattern in intkey data files - one
    // record
    // contains a single integer which is the length of the string in bytes, the
    // following
    // record contains the text of the string
    private static String readReferencedString(BinFile bFile, int recordNumber) {
        seekToRecord(bFile, recordNumber);
        int stringLength = bFile.readInt();
        seekToRecord(bFile, recordNumber + 1);
        return readString(bFile, stringLength);
    }

    // Helper method to deak with a common pattern in intkey data files - a
    // record contains
    // N integer values, each of which, if non-zero point to records from which
    // a string can be
    // read using readReferencedString (see above)
    private static List<String> readStringList(BinFile bFile, int recordNumber, int listSize) {
        List<String> returnList = new ArrayList<String>();

        seekToRecord(bFile, recordNumber);
        List<Integer> stringReferences = readIntegerList(bFile, listSize);

        for (int stringReference : stringReferences) {
            if (stringReference != 0) {
                returnList.add(readReferencedString(bFile, stringReference));
            } else {
                returnList.add(null);
            }
        }

        return returnList;
    }

    private static List<Integer> readIntegerList(BinFile bFile, int numInts) {
        ByteBuffer bb = bFile.readByteBuffer(numInts * Constants.SIZE_INT_IN_BYTES);

        List<Integer> retList = new ArrayList<Integer>();
        for (int i = 0; i < numInts; i++) {
            retList.add(bb.getInt());
        }
        return retList;
    }

    private static List<Float> readFloatList(BinFile bFile, int numFloats) {
        ByteBuffer bb = bFile.readByteBuffer(numFloats * Constants.SIZE_INT_IN_BYTES);

        List<Float> retList = new ArrayList<Float>();
        for (int i = 0; i < numFloats; i++) {
            retList.add(bb.getFloat());
        }
        return retList;
    }

    private static int recordsSpannedByBytes(int numBytes) {
        return (int) (Math.ceil((double) numBytes / (double) Constants.RECORD_LENGTH_BYTES));
    }

    // parse a string containing filenames and metadata data in the format
    // filename {<file information>} filename {<file information>} ...
    // where <file information> is optional
    // and return a list of filename and file information pairs.
    public static List<Pair<String, String>> parseFileData(String fileData) {
        List<Pair<String, String>> retList = new ArrayList<Pair<String, String>>();

        List<String> separateFileDataList = separateFileDataStrings(fileData);

        for (String sepFileData : separateFileDataList) {
            String fileName = null;
            String fileInfo = null;

            if (sepFileData.contains("<")) {
                int firstOpenBracketIndex = sepFileData.indexOf('<');
                fileName = sepFileData.substring(0, firstOpenBracketIndex).trim();
                fileInfo = sepFileData.substring(firstOpenBracketIndex).trim();
            } else {
                fileName = sepFileData;
            }

            retList.add(new Pair<String, String>(fileName, fileInfo));
        }

        return retList;
    }

    // take a string with format:
    // "filename <file information> <more file information> ..."
    // and return a list with two items, the first being the filename, and the
    // second being all
    // of the file information.
    public static List<String> separateFileDataStrings(String filesData) {
        List<String> filesDataList = new ArrayList<String>();

        int endLastSubstring = -1;
        boolean inBracket = false;

        String[] tokens = filesData.split(" ");

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.startsWith("<") && !token.endsWith(">")) {
                inBracket = true;
            } else if (token.endsWith(">")) {
                inBracket = false;
            } else if (i > 0 && !inBracket) {
                String[] subList = Arrays.copyOfRange(tokens, endLastSubstring + 1, i);
                filesDataList.add(StringUtils.join(subList, " "));
                endLastSubstring = i - 1;
            }

            if (i == tokens.length - 1) {
                String[] subList = Arrays.copyOfRange(tokens, endLastSubstring + 1, i + 1);
                filesDataList.add(StringUtils.join(subList, " "));
                endLastSubstring = i;
                continue;
            }

        }

        return filesDataList;
    }

    private static Image createImage(String fileName, String comments, int imageType) {
        DefaultImageData imageData = new DefaultImageData(fileName);
        Image image = new Image(imageData);
        try {
            if (comments != null) {
                ImageOverlayParser parser = new ImageOverlayParser();
                parser.setColorsBGR(true);
                List<ImageOverlay> overlayList = parser.parseOverlays(comments, imageType);
                imageData.setOverlays(overlayList);
            }
            return image;
        } catch (Exception ex) {// (ParseException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Error parsing image overlay data");
        }
    }
}
