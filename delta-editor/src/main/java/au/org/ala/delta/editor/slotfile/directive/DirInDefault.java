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
package au.org.ala.delta.editor.slotfile.directive;



public class DirInDefault implements DirectiveFunctor {

	@Override
	public void process(DirectiveInOutState state) {}
//		
//		DeltaDataSet dataSet = state.getDataSet();
//		Directive aDirective = state.getCurrentDirective().getDirective();
//		
//		int argType = aDirective.getArgType();
//		String temp = "";
//		DirectiveType directiveType = state.getCurrentDirective().getType();
//		boolean atLineStart;
//		  boolean atLineEnd;
//		int dirType = aDirective.getNumber();
//		DirectiveArguments args = new DirectiveArguments();
//		  if (asComment)
//		    dirType |= DIRARG_COMMENT_FLAG;
//		  
//		  String nextWord;
//		  StringBuilder buffer;
//		  int lower, upper;
//		  int GNWflags = GNW_INCLUDENEWLINES | GNW_INCLUDESPACE;
//
//		 
//
//		  switch (argType)
//		    {
//		      case DirectiveArgType.DIRARG_NONE:
//		      case DirectiveArgType.DIRARG_TRANSLATION:
//		      case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
//		        break;
//
//		      case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirInComment
//		      case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be preserved?
//		      case DirectiveArgType.DIRARG_FILE:
//		      case DirectiveArgType.DIRARG_OTHER:
//		      case DirectiveArgType.DIRARG_INTERNAL:
//		        args.resize(1);
//		        buffer.setLength(0);
//		        if (argType == DirectiveArgType.DIRARG_FILE)
//		          GNWflags |= GNW_HANDLEQUOTES | GNW_IGNORERTF;
//		        while (GetNextWord(nextWord, GNWflags) > 0)
//		        //while (GetNextWord(nextWord, true, false, true, false, argType != DIRARG_FILE) > 0)
//		          {
//		            if (buffer.length() == 0 && atLineStart)
//		              buffer.append('\n');
//		            //if (buffer.length() > 0 && buffer.charAt(buffer.length() -1] != '\n')
//		            //  buffer.append(" ";
//		            buffer.append(nextWord);
//		          }
//		        if (buffer.length() > 0 && buffer.charAt(0) == ' ')
//		          buffer.delete(0, 1);
//		        while (buffer.length() > 0 &&
//		                 (buffer.charAt(buffer.length() - 1) == '\n' ||
//		                  buffer.charAt(buffer.length() - 1) == ' '))
//		          buffer.setLength(buffer.length() - 1);
//		        args.addTextArgument(buffer.toString());
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTEGER:
//		      case DirectiveArgType.DIRARG_REAL:
//		        args.resize(1);
//		        ReadSingleNumber(args[0].value, argType == DirectiveArgType.DIRARG_INTEGER);
//		        break;
//
//		      case DirectiveArgType.DIRARG_CHAR:
//		      case DirectiveArgType.DIRARG_ITEM:
//		        args.resize(1);
//		          {   // Braces to allow declaration of local variables
//		            int aVal;
//		            ReadSingleUint(aVal);
//		            TVOUniId anId = IdFromNumber(aVal, argType == DirectiveArgType.DIRARG_CHAR ? CHARACTER : ITEM);
//		            args[0].id = anId;
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_CHARLIST:
//		      case DirectiveArgType.DIRARG_ITEMLIST:
//		        // Should probably be enhanced to make sure that no character (or item)
//		        // appears more than once on the list....
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            TDirArgs anArg;
//		            if (!ExtractUintRange(nextWord, lower, upper) ||
//		                upper < lower || upper > (argType == DIRARG_CHARLIST ? deltaDoc->GetNChars() : deltaDoc->GetNItems()))
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		            for (int i = lower; i <= upper; ++i)
//		              {
//		                TVOUniId anId = IdFromNumber(i, argType == DIRARG_CHARLIST ? CHARACTER : ITEM);
//		                args.add(TDirArgs(anId));
//		              }
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_TEXTLIST:
//		      case DirectiveArgType.DIRARG_CHARTEXTLIST:
//		      case DirectiveArgType.DIRARG_ITEMTEXTLIST:
//		      case DirectiveArgType.DIRARG_ITEMFILELIST:
//		        {
//		        // Should probably be enhanced to make sure that no character (or item)
//		        // appears more than once on the list....
//		        buffer.reserve(1024);
//		        char delim = 0;
//		        TUintVect charSet;
//		        TDirArgs firstArg (VOUID_NAME); // Flags possible presence of comments,
//		                                        // added August 2000
//		        while (GetNextWord(nextWord, 0) > 0)
//		          {
//		            if (nextWord.charAt(0) != ELEMSTART)
//		              {
//		                if (nextWord.length() == 1 &&
//		                    args.size() == 0 &&
//		                    /* (argType == DIRARG_ITEMTEXTLIST ||
//		                     argType == DIRARG_ITEMFILELIST) && */
//		                    nextWord.charAt(0) != OPENBRACK &&
//		                    nextWord.charAt(0) != CLOSEBRACK)
//		                  {
//		                    // An optional delimiter is present ...
//		                    firstArg.text = delim = nextWord.charAt(0);
//		                  }
//		                else
//		                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                continue;
//		              }
//		            if (args.empty())
//		              args.add(firstArg);
//		            nextWord.erase(0, 1);
//		            charSet.resize(0);
//		            //if (nextWord.length() == 0 && GetNextWord(nextWord) <= 0)
//		            //  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE);
//
//		            if (argType == DIRARG_CHARTEXTLIST ||
//		                argType == DIRARG_TEXTLIST ||
//		                (nextWord.length() > 0 &&
//		                 isdigit(nextWord.charAt(0]) &&
//		                 nextWord.charAt(nextWord.length() - 1) == '.'))
//		              {
//		                if (nextWord.length() == 0 && GetNextWord(nextWord) <= 0)
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                if (argType == DIRARG_TEXTLIST)
//		                  {
//		                    int aVal;
//		                    if (!ExtractSingleUint(nextWord, aVal))
//		                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                    charSet.add(aVal);
//		                  }
//		                else
//		                  {
//		                    if (!ExtractCharSet(nextWord, charSet, '.'))
//		                      throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                  }
//		                if (nextWord.length() != 1 || nextWord.charAt(0] != '.')
//		                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		              }
//		            else
//		              {
//		                 // First pull together all text up to the '/'.
//		                if (nextWord.length() == 0 && GetNextWord(nextWord) <= 0)
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                buffer.setLength(0);
//		                bool atEnd = false;
//		                do
//		                  {
//		                    if (nextWord.charAt(nextWord.length() - 1) == ELEMEND)
//		                      {
//		                        atEnd = true;
//		                        nextWord.resize(nextWord.length() - 1);
//		                      }
//		                    if (buffer.length() > 0)
//		                      buffer.append(' ');
//		                    buffer.append(nextWord);
//		                  }
//		                while (!atEnd && GetNextWord(nextWord) > 0);
//
//		                TVOItemDesc* item = deltaDoc->FindItemByName(buffer);
//		                if (item == NULL)
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_ITEM_NAME)");
//		                long itemNo = deltaDoc->GetDeltaMaster()->ItemNoFromUniId(item->GetUniId());
//		                charSet.add(itemNo);
//		              }
//
//		            // We have the object (set) parsed - now get its text
//		            buffer.setLength(0);
//		            std::string comment;
//		            bool wasDelimited = false;
//		            while (GetNextWord(nextWord, GNW_INCLUDENEWLINES | GNW_IGNORERTF, buffer.empty() ? delim : 0) > 0)
//		              {
//		                if (buffer.empty() && nextWord.charAt(0) == delim && nextWord.charAt(nextWord.length() - 1] == delim)
//		                  {
//		                    wasDelimited = true;
//		                    buffer = nextWord.substr(1, nextWord.length() - 2);
//		                    break;
//		                  }
//		                if (nextWord.charAt(0) == ELEMSTART)
//		                  {
//		                    usePrevWord = true;
//		                    break;
//		                  }
//		                else if (nextWord.charAt(0) == OPENBRACK && argType != DIRARG_ITEMFILELIST /* && argType == DIRARG_TEXTLIST */)
//		                  {
//		                    if (!(comment.empty() && buffer.empty()))
//		                      throw new RuntimeException("TDirInOutEx(ED_BAD_COMMENT_POSITION)");
//		                    usePrevWord = true;
//		                    ReadComment(comment);
//		                    continue;
//		                  }
//		                if (buffer.length() > 0 && buffer.charAt(buffer.length() -1] != '\n' )
//		                  buffer.append(' ');
//		                buffer.append(nextWord);
//		              }
//		            while (!wasDelimited && buffer.length() > 0 &&
//		                     (buffer.charAt(buffer.length() - 1) == '\n' ||
//		                      buffer.charAt(buffer.length() - 1) == ' '))
//		              buffer.setLength(buffer.length() - 1);
//		            for (TUintVect::iterator i = charSet.begin(); i != charSet.end(); ++i)
//		              {
//		                TVOUniId anId;
//		                if (argType == DIRARG_TEXTLIST)
//		                  anId = *i;
//		                else
//		                  anId = IdFromNumber(*i, argType == DIRARG_CHARTEXTLIST ? CHARACTER : ITEM);
//		                TDirArgs anArg(anId);
//		                anArg.text = buffer;
//		                anArg.comment = comment;
//		                args.add(anArg);
//		              }
//		          }
//		        break;
//		        }
//		      case DirectiveArgType.DIRARG_CHARINTEGERLIST:
//		      case DirectiveArgType.DIRARG_CHARREALLIST:
//		      case DirectiveArgType.DIRARG_ITEMREALLIST:
//		        // Should probably be enhanced to make sure that no character (or item)
//		        // appears more than once on the list....
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            TDirArgs anArg;
//		            if (!ExtractUintRange(nextWord, lower, upper) ||
//		                upper < lower || upper > (argType == DIRARG_ITEMREALLIST ? deltaDoc->GetNItems() : deltaDoc->GetNChars()))
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		            if (nextWord.length() == 0 || nextWord.charAt(0) != ',')
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		            nextWord.erase(0, 1);
//
//		            TDeltaNumber number;
//		            if (!ExtractSingleNumber(nextWord, number, argType == DIRARG_CHARINTEGERLIST) || nextWord.length() != 0)
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//
//		            for (int i = lower; i <= upper; ++i)
//		              {
//		                TVOUniId anId = IdFromNumber(i, argType == DIRARG_ITEMREALLIST ? ITEM : CHARACTER);
//		                TDirArgs anArg(anId);
//		                anArg.value = number;
//		                args.add(anArg);
//		              }
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_CHARGROUPS:
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            TUintVect charSet;
//		            if (!ExtractCharSet(nextWord, charSet, 0) || nextWord.length() != 0)
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		            TDirArgs anArg;
//		            anArg.dataVect.resize(charSet.size());
//		            for (int j = 0; j < charSet.size(); ++j)
//		              {
//		                anArg.dataVect[j].uniId = IdFromNumber(charSet[j], CHARACTER);
//		              }
//		            args.add(anArg);
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_ITEMCHARLIST:
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            TUintVect itemSet;
//		            if (nextWord.charAt(0) != ELEMSTART)
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		            nextWord.erase(0, 1);
//
//		            if (nextWord.length() > 0 &&
//		                isdigit(nextWord.charAt(0)) &&
//		                nextWord.charAt(nextWord.length() - 1) == '.')
//		              {
//		                if (!ExtractCharSet(nextWord, itemSet, '.'))
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                if (nextWord.length() != 1 || nextWord.charAt(0) != '.')
//		                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		              }
//		            else if (directiveArray == ConforDirArray) /// Use names for Confor, but not for Key
//		              {
//		                 // First pull together all text up to the '/'.
//		                if (nextWord.length() == 0 && GetNextWord(nextWord) <= 0)
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                buffer.setLength(0);
//		                bool atEnd = false;
//		                do
//		                  {
//		                    if (nextWord.charAt(nextWord.length() - 1] == ELEMEND)
//		                      {
//		                        atEnd = true;
//		                        nextWord.resize(nextWord.length() - 1);
//		                      }
//		                    if (buffer.length() > 0)
//		                      buffer.append(' ');
//		                    buffer.append(nextWord);
//		                  }
//		                while (!atEnd && GetNextWord(nextWord) > 0);
//
//		                TVOItemDesc* item = deltaDoc->FindItemByName(buffer);
//		                if (item == NULL)
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_ITEM_NAME)");
//		                long itemNo = deltaDoc->GetDeltaMaster()->ItemNoFromUniId(item->GetUniId());
//		                itemSet.add(itemNo);
//		              }
//		            else
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//
//		            TDirArgsVector items;
//		            items.reserve(itemSet.size());
//		            for (TUintVect::iterator iter = itemSet.begin(); iter != itemSet.end(); ++iter)
//		              {
//		                TVOUniId anId = IdFromNumber(*iter, ITEM);
//		                items.add(TDirArgs(anId));
//		              }
//		            while (GetNextWord(nextWord) > 0)
//		              {
//		                if (nextWord.charAt(0] == ELEMSTART)
//		                  {
//		                    usePrevWord = true;
//		                    break;
//		                  }
//		                if (!ExtractUintRange(nextWord, lower, upper) ||
//		                    upper < lower || upper > deltaDoc->GetNChars())
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//
//		                for (TDirArgsVector::iterator iIter = items.begin(); iIter != items.end(); ++iIter)
//		                  {
//		                    for (int i = lower; i <= upper; ++i)
//		                      {
//		                        TDirListData aChar;
//		                        aChar.uniId = IdFromNumber(i, CHARACTER);
//		                        (*iIter).dataVect.add(aChar);
//		                      }
//		                  }
//		              }
//		            for (TDirArgsVector::iterator iIter = items.begin(); iIter != items.end(); ++iIter)
//		              args.add(*iIter);
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_ALLOWED:
//		        // Should probably be enhanced to make sure that no character
//		        // appears more than once on the list....
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            TDirArgs anArg;
//		            if (!ExtractUintRange(nextWord, lower, upper) || // Is the first part a single character number or a range???
//		                upper < lower || upper > deltaDoc->GetNChars())
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		            if (nextWord.length() == 0 || nextWord.charAt(0] != ',')
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		            nextWord.erase(0, 1);
//
//		            TDeltaNumber number[3];
//		            for (int i = 0; i < 3; ++i)
//		              {
//		                if (!ExtractSingleNumber(nextWord, number[i]))
//		                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                if (i < 2)
//		                  {
//		                    if (nextWord.length() == 0)
//		                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                    if (nextWord.charAt(0] != ':')
//		                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                  }
//		                else
//		                  {
//		                    if (nextWord.length() != 0)
//		                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                  }
//		              }
//		            if (number[1] < number[0])
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_ALLOWED_VALUES)");
//
//		            for (int i = lower; i <= upper; ++i)
//		              {
//		                TVOUniId anId = IdFromNumber(i, CHARACTER);
//		                TDirArgs anArg(anId);
//		                anArg.dataVect.resize(3);
//		                anArg.dataVect[0] = number[0];
//		                anArg.dataVect[1] = number[1];
//		                anArg.dataVect[2] = number[2];
//		                args.add(anArg);
//		              }
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_KEYSTATE:
//		        while (GetNextWord(nextWord) > 0)
//		          {
//		            if (!ExtractUintRange(nextWord, lower, upper) ||
//		                upper < lower || upper > deltaDoc->GetNChars())
//		              throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		            if (nextWord.length() == 0 || nextWord.charAt(0] != ',')
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		            nextWord.erase(0, 1);
//		            if (nextWord.length() == 0)
//		              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//
//		            TCharType charType = CHARTYPE_LISTEND;
//		            for (int i = lower; i <= upper; ++i)
//		              {
//		                TVOCharBaseDesc* charBase = GetCharBase(i);
//		                TCharType curCharType = charBase->GetCharType();
//
//		                if (curCharType != charType)
//		                  {
//		                    if (charType == CHARTYPE_LISTEND)
//		                      charType = curCharType;
//		                    else
//		                      throw new RuntimeException("TDirInOutEx(ED_MIXED_CHAR_TYPES, charBase->GetUniId())");
//		                  }
//		              }
//		            int keyState = 0;
//		            while (nextWord.length() > 0)
//		              {
//		                ++keyState;
//		                switch (charType)
//		                  {
//		                    case DirectiveArgType.CHARTYPE_UNORDERED:
//		                      {
//		                        TStateIdVector states;
//		                        int stateNo = 0;
//		                        if (!ExtractSingleUint(nextWord, stateNo))
//		                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                        states.add(stateNo);
//		                        while (nextWord.length() > 0 && nextWord.charAt(0) == '&')
//		                          {
//		                            nextWord.erase(0, 1);
//		                            if (!ExtractSingleUint(nextWord, stateNo))
//		                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                            states.add(stateNo);
//		                          }
//		                        for (int i = lower; i <= upper; ++i)
//		                          {
//		                            TVOCharBaseDesc* charBase = GetCharBase(i);
//		                            TDirArgs anArg(charBase->GetUniId());
//		                            anArg.value.SetFromValue(keyState, 0);
//		                            anArg.dataVect.resize(states.size());
//		                            for (int j = 0; j < states.size(); ++j)
//		                              {
//		                                anArg.dataVect[j].stateId = charBase->UniIdFromStateNo(states[j]);
//		                                if (anArg.dataVect[j].stateId == STATEID_NULL)
//		                                  throw new RuntimeException("TDirInOutEx(ED_BAD_STATE_NUMBER, charBase->GetUniId())");
//		                              }
//		                            args.add(anArg);
//		                          }
//		                        break;
//		                      }
//
//		                    // case DirectiveArgType.CHARTYPE_CYCLIC: This will require special handling, since a
//		                    // range of Nov-Mar is quite different from a range of Mar-Nov....
//
//		                    case DirectiveArgType.CHARTYPE_ORDERED:
//		                    case DirectiveArgType.CHARTYPE_LIST:
//		                      {
//		                        int minState, maxState;
//		                        if (!ExtractSingleUint(nextWord, minState))
//		                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                        if (nextWord.length() == 0 || nextWord.charAt(0) == '/')
//		                          maxState = minState;
//		                        else if (nextWord.charAt(0) == '-')
//		                          {
//		                            nextWord.erase(0, 1);
//		                            if (!ExtractSingleUint(nextWord, maxState))
//		                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                          }
//		                        else
//		                          throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                        for (int i = lower; i <= upper; ++i)
//		                          {
//		                            TVOCharBaseDesc* charBase = GetCharBase(i);
//		                            TDirArgs anArg(charBase->GetUniId());
//		                            anArg.value.SetFromValue(keyState, 0);
//		                            anArg.dataVect.resize(2);
//		                            anArg.dataVect[0].stateId = charBase->UniIdFromStateNo(minState);
//		                            anArg.dataVect[1].stateId = charBase->UniIdFromStateNo(maxState);
//		                            if (anArg.dataVect[0].stateId == STATEID_NULL ||
//		                                anArg.dataVect[0].stateId == STATEID_NULL)
//		                              throw new RuntimeException("TDirInOutEx(ED_BAD_STATE_NUMBER, charBase->GetUniId())");
//		                            args.add(anArg);
//		                          }
//		                        break;
//		                      }
//
//		                    case DirectiveArgType.CHARTYPE_INTEGER:
//		                    case DirectiveArgType.CHARTYPE_REAL:
//		                      {
//		                        TDirListDataVector dataVect;
//		                        TDeltaNumber aNumber;
//		                        dataVect.resize(2);
//		                        if (nextWord.charAt(0) == '~')
//		                          {
//		                            nextWord.erase(0, 1);
//		                            dataVect[0] = -MAXFLOAT;
//		                            if (!ExtractSingleNumber(nextWord, aNumber))
//		                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                            dataVect[1] = aNumber;
//		                          }
//		                        else
//		                          {
//		                            if (!ExtractSingleNumber(nextWord, aNumber))
//		                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                            dataVect[0] = aNumber;
//		                            if (nextWord.length() == 0 || nextWord.charAt(0) == '/')
//		                              dataVect[1] = dataVect[0];
//		                            else if (nextWord.charAt(0) == '~')
//		                              {
//		                                nextWord.erase(0, 1);
//		                                dataVect[1] = MAXFLOAT;
//		                              }
//		                            else if (nextWord.charAt(0) == '-')
//		                              {
//		                                nextWord.erase(0, 1);
//		                                if (!ExtractSingleNumber(nextWord, aNumber))
//		                                  throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		                                dataVect[1] = aNumber;
//		                              }
//		                            else
//		                              throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                          }
//		                        if (dataVect[0].realNumb > dataVect[1].realNumb)
//		                          throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                        for (int i = lower; i <= upper; ++i)
//		                          {
//		                            TDirArgs anArg(GetCharBase(i)->GetUniId());
//		                            anArg.value.SetFromValue(keyState, 0);
//		                            anArg.dataVect = dataVect;
//		                            args.add(anArg);
//		                          }
//		                        break;
//		                      }
//
//		                    default:
//		                      throw new RuntimeException("TDirInOutEx(ED_INAPPROPRIATE_TYPE)");
//		                      //break;
//		                  }
//
//		                if (nextWord.length() > 0)
//		                  {
//		                    if (nextWord.charAt(0) == '/')
//		                      nextWord.erase(0, 1);
//		                    else
//		                      throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		                  }
//		              }
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_PRESET:
//		        {
//		          int lastCol=0, lastGroup=0;
//		          // Should probably be enhanced to make sure that no character
//		          // appears more than once on the list....
//		          while (GetNextWord(nextWord) > 0)
//		            {
//		              int aVal;
//		              ExtractSingleUint(nextWord, aVal); // Always a single character value, not a range
//		              TVOUniId anId = IdFromNumber(aVal, CHARACTER);
//		              TDirArgs anArg(anId);
//		              if (nextWord.length() == 0 || nextWord.charAt(0] != ',')
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//
//		              nextWord.erase(0, 1);
//
//		              int col, group;
//		              if (!ExtractSingleUint(nextWord, col))
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		              if (nextWord.length() == 0)
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		              if (nextWord.charAt(0] != ':')
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//		              nextWord.erase(0, 1);
//		              if (!ExtractSingleUint(nextWord, group))
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DATA)");
//		              if (nextWord.length() != 0)
//		                throw new RuntimeException("TDirInOutEx(ED_MISSING_DELIMITER)");
//
//		              if (col < lastCol || group <= lastGroup)
//		                throw new RuntimeException("TDirInOutEx(ED_BAD_VALUE_ORDER, anId)");
//		              lastGroup = group;
//		              if (col > lastCol)
//		                lastGroup = 0;
//		              lastCol = col;
//
//		              anArg.dataVect.resize(2);
//		              anArg.dataVect[0].intNumb = col;
//		              anArg.dataVect[1].intNumb = group;
//		              args.add(anArg);
//		            }
//		        }
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTKEY_ONOFF:
//		        args.resize(1);
//		        if (GetNextWord(nextWord) > 0)
//		          {
//		            if (stricmp(nextWord.c_str(), "On") == 0)
//		              args[0].value.SetFromValue(1.0);
//		            else if (stricmp(nextWord.c_str(), "Of") == 0 ||
//		                     stricmp(nextWord.c_str(), "Off") == 0)
//		              args[0].value.SetFromValue(-1.0);
//		            else
//		              throw new RuntimeException("TDirInOutEx(ED_DIRECTIVE_INVALID)");
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTKEY_ITEM:
//		        {
//		          args.resize(1);
//		          if (ReadIntkeyRange(nextWord, lower, upper))
//		            {
//		              if (nextWord.empty())
//		                {
//		                  if (upper != lower || upper > deltaDoc->GetNItems())
//		                    throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                  args[0].id = IdFromNumber(lower, ITEM);
//		                }
//		              else
//		                args[0].text = nextWord;
//		            }
//		        }
//		        break;
//
//		      case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
//		      case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
//		        // Get keyword, then drop through
//		        if (!ReadIntkeyRange(nextWord, lower, upper, true))
//		          break;
//		        if (nextWord.empty())
//		          throw new RuntimeException("TDirInOutEx(ED_BAD_KEYWORD)");
//		        args.add(TDirArgs(nextWord));
//		      case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
//		      case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
//		        {
//		          bool isChar = (argType == DIRARG_KEYWORD_CHARLIST ||
//		                         argType == DIRARG_INTKEY_CHARLIST);
//
//		          while (ReadIntkeyRange(nextWord, lower, upper))
//		            {
//		              if (nextWord.empty())
//		                {
//		                  if (upper > (isChar ? deltaDoc->GetNChars() : deltaDoc->GetNItems()))
//		                    throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                  for (int i = lower; i <= upper; ++i)
//		                    {
//		                      TVOUniId anId = IdFromNumber(i, isChar ? CHARACTER : ITEM);
//		                      args.add(TDirArgs(anId));
//		                    }
//		                }
//		              else
//		                args.add(TDirArgs(nextWord));
//		            }
//		        }
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
//		        while (ReadIntkeyValuePair(nextWord, buffer, lower, upper))
//		          {
//		            TDeltaNumber number(-1.0);
//		            if (buffer.length() > 0)
//		              {
//		                ExtractSingleNumber(buffer, number);
//		                if (buffer.length() > 0)
//		                  throw new RuntimeException("TDirInOutEx(ED_INVALID_NUMERIC)");
//		              }
//		            TDirArgs anArg(number);
//		            if (nextWord.empty())
//		              {
//		                if (upper > deltaDoc->GetNChars())
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                for (int i = lower; i <= upper; ++i)
//		                  {
//		                     anArg.id = IdFromNumber(i, CHARACTER);
//		                     args.add(anArg);
//		                  }
//		              }
//		            else
//		              {
//		                anArg.text = nextWord;
//		                args.add(anArg);
//		              }
//		          }
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
//		        {
//		          TStdStringArray strings;
//		          bool grouped;
//		          enum state
//		            { IN_MODIFIERS,
//		              IN_TAXA,
//		              IN_CHARS
//		            } curState = IN_MODIFIERS;
//		          while (ReadIntkeyGroup(strings, grouped))
//		            {
//		              if (curState == IN_MODIFIERS && (grouped || strings[0][0] != '/'))
//		                curState = IN_TAXA;
//		              for (int k = 0; k < strings.size(); ++k)
//		                {
//		                  TDirArgs anArg(TDeltaNumber(curState == IN_MODIFIERS ? 0.0 : (curState == IN_TAXA ? -1.0 : 1.0)));
//		                  if (isdigit(strings[k][0]))
//		                    {
//		                      if (!ExtractUintRange(strings[k], lower, upper) || upper < lower)
//		                        throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                      if ((curState == IN_TAXA && upper > deltaDoc->GetNItems()) ||
//		                          (curState == IN_CHARS && upper > deltaDoc->GetNChars()))
//		                        throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                      for (int i = lower; i <= upper; ++i)
//		                        {
//		                           anArg.id = IdFromNumber(i, curState == IN_TAXA ? ITEM : CHARACTER);
//		                           args.add(anArg);
//		                        }
//		                    }
//		                  else
//		                    {
//		                      anArg.text = strings[k];
//		                      args.add(anArg);
//		                    }
//		                }
//		              if (curState == IN_TAXA)  // Can only have a single taxon or group of taxa
//		                curState = IN_CHARS;
//		            }
//		        }
//		        break;
//
//		      case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
//		        while (ReadIntkeyValuePair(nextWord, buffer, lower, upper))
//		          {
//		            TDirArgs anArg;
//		            if (nextWord.empty())
//		              {
//		                if (upper > deltaDoc->GetNChars())
//		                  throw new RuntimeException("TDirInOutEx(ED_BAD_RANGE)");
//		                for (int i = lower; i <= upper; ++i)
//		                  {
//		                    anArg.id = IdFromNumber(i, CHARACTER);
//		                    if (buffer.length() > 0)
//		                      {
//		                        TVOCharBaseDesc* charBase = GetCharBase(i);
//		                        anArg.attrib.Init(anArg.id);
//		                        //if (IsText(charBase->GetCharType()))
//		                        //  buffer = std::string("<") + buffer + ">";
//		                        //anArg.attrib.Parse(buffer, charBase, true);
//		                        try
//		                          {
//		                            anArg.attrib.Parse(buffer, IsText(charBase->GetCharType()) ? NULL : charBase, true);
//		                          }
//		                        catch (TAttributeParseEx & ex)
//		                          {
//		                            itemCharNo = i;
//		                            attribBuf = buffer;
//		                            taxonName.resize(0);
//		                            throw;
//		                          }
//		                      }
//		                    args.add(anArg);
//		                  }
//		              }
//		            else // The "character" was a keyword
//		              {
//		                anArg.text = nextWord;
//		                if (buffer.length() > 0)
//		                  {
//		                    anArg.attrib.SetCharId(VOUID_NULL);
//		                    anArg.attrib.Parse(buffer, NULL);
//		                  }
//		                args.add(anArg);
//		              }
//		          }
//		        break;
//
//		      default:
//		        break;
//		    }
//		  dirArray.add(dir);
//	}


}
