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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgument;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.editor.slotfile.DeltaNumber;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.Item;
import au.org.ala.delta.translation.Printer;

/**
 * Writes the directive to the output.
 */
public class DirOutDefault implements DirectiveFunctor {

	private Printer _printer;
	private StringBuilder _textBuffer;
	
	@Override
	public void process(DirectiveInOutState state) {
		
		_printer = state.getPrinter();
		_textBuffer = new StringBuilder();
		DeltaDataSet dataSet = state.getDataSet();
		int lineIndent = 2;
		Directive curDirective = state.getCurrentDirective().getDirective();
		//Dir directive;
		DirectiveArguments directiveArgs = state.getCurrentDirective().getDirectiveArguments();
		
		int argType = curDirective.getArgType();
		String temp = "";
		DirectiveType directiveType = DirectiveType.CONFOR;
		List<Integer> dataList = null;;
		int prevNo, curNo;
		
		List<DirectiveArgument<?>> args = null;;
		List<Integer> data = null;

		switch (argType) {
		      case DirectiveArgType.DIRARG_NONE:
		      case DirectiveArgType.DIRARG_TRANSLATION:
		      case DirectiveArgType.DIRARG_INTKEY_INCOMPLETE:
		        break;

		//TODO handle this label WriteAsText:
		      case DirectiveArgType.DIRARG_TEXT: // What about multiple lines of text? Should line breaks ALWAYS be preserved?
		      case DirectiveArgType.DIRARG_COMMENT: // Will actually be handled within DirComment
		        lineIndent = 0;
		      case DirectiveArgType.DIRARG_FILE:
		      case DirectiveArgType.DIRARG_OTHER:
		      case DirectiveArgType.DIRARG_INTERNAL:
		        if (directiveArgs.size() > 0)
		          {
		            _textBuffer.append(' ');
		            temp = directiveArgs.getFirstArgumentText();
		            if (argType != DirectiveArgType.DIRARG_FILE)
		              despaceRTF(temp);  /// SHOULD THIS BE DONE???
		            _textBuffer.append(temp);
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTEGER:
		      case DirectiveArgType.DIRARG_REAL:
		        if (directiveArgs.size() > 0)
		          {
		            _textBuffer.append(' ');
		            _textBuffer.append(directiveArgs.getFirstArgumentValueAsString());
		          }
		        break;

		      case DirectiveArgType.DIRARG_CHAR:
		      case DirectiveArgType.DIRARG_ITEM:
		        if (directiveArgs.size() > 0)
		          {
		            _textBuffer.append(' ');
		            curNo = directiveArgs.getFirstArgumentIdAsInt();
		            _textBuffer.append(curNo);
		          }
		        break;

		      case DirectiveArgType.DIRARG_CHARLIST:
		      case DirectiveArgType.DIRARG_ITEMLIST:
		        if (directiveArgs.size() > 0)
		          {
		            dataList = new ArrayList<Integer>();
		            for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		              dataList.add((Integer)vectIter.getId());
		            _textBuffer.append(' ');
		            AppendRange(dataList, ' ', true, _textBuffer);
		          }
		        break;

		      case DirectiveArgType.DIRARG_TEXTLIST:
		       // Special handling for "legacy" data, when this was stored as a simple, large
		       // text block...
		        if (directiveArgs.size() == 1 &&
		            directiveArgs.getFirstArgumentIdAsInt() <= 0 &&
		            directiveArgs.getFirstArgumentText().length() > 1)
		          {
		            // argType == DirectiveArgType.DIRARG_TEXT;
		            // TODO goto WriteAsText;
		          }
		        // Otherwise drop through...
		      case DirectiveArgType.DIRARG_CHARTEXTLIST:
		      case DirectiveArgType.DIRARG_ITEMTEXTLIST:
		      case DirectiveArgType.DIRARG_ITEMFILELIST:
		        {
		        	Collections.sort(directiveArgs.getDirectiveArguments());
		      char delim = 0;
		        
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            boolean commentsSupported = false; // Flags changes in format, August 2000
		            if (vectIter == directiveArgs.get(0))
		              {
		                commentsSupported = (Integer)vectIter.getId() == Integer.MIN_VALUE;
		                // Check for optional delimiter...
		                boolean delimSupported = (/*(argType == DIRARG_ITEMTEXTLIST ||
		                                       argType == DIRARG_ITEMFILELIST) &&
		                                       */
		                                       (Integer)vectIter.getId() <= 0 ||
		                                        commentsSupported);
		                if (delimSupported && vectIter.getText().length() > 0)
		                  {
		                    delim = vectIter.getText().charAt(0);
		                    if (delim != 0)
		                      {
		                        _textBuffer.append(' ');
		                        _textBuffer.append(delim);
		                      }
		                  }
		                outputTextBuffer(0, 0, true);
		                if (delimSupported)
		                  continue;
		              }
		            _textBuffer.append("#");
		            if ((argType == DirectiveArgType.DIRARG_TEXTLIST) ||
		                (argType == DirectiveArgType.DIRARG_CHARTEXTLIST))
		              {
		                _textBuffer.append(vectIter.getId());
		                _textBuffer.append(".");
		              }
		            else
		              {
		                Item item = dataSet.getItem((Integer)vectIter.getId());
		                temp = item.getDescription();
		                despaceRTF(temp, true); ////
		                _textBuffer.append(' ');
		                _textBuffer.append(temp);
		                _textBuffer.append("/");
		              }
		            boolean hasComment = vectIter.getComment().length() > 0;
		            if (hasComment)
		              {
		                _textBuffer.append(" <");
		                _textBuffer.append(vectIter.getComment());
		                _textBuffer.append('>');
		                outputTextBuffer (0, 0, true);
		              }
		            temp = vectIter.getText();
		            if (!temp.isEmpty())
		              {
		                _textBuffer.append(' ');
		                /// SHOULD RTF be "handled" here or not???
		                ////if (argType != DIRARG_ITEMFILELIST)
		                ////  DespaceRTF(temp);
		                // If an optional delimitor is defined, and doesn't appear within
		                // the string, quote the string with the delimitor
		                boolean useDelim = (delim != 0 &&
		                                 temp.indexOf(delim) == -1 &&
		                                 commentsSupported
		                                 );
		                if (useDelim)
		                  _textBuffer.append(delim);
		                _textBuffer.append(temp);
		                if (useDelim)
		                  _textBuffer.append(delim);
		              }
		            outputTextBuffer(/* hasComment ? 7 : */ 0, 0, true);
		          }
		        break;
		        }
		      case DirectiveArgType.DIRARG_CHARINTEGERLIST:
		      case DirectiveArgType.DIRARG_CHARREALLIST:
		      case DirectiveArgType.DIRARG_ITEMREALLIST:
		        if (directiveArgs.size() > 0)
		          {	      
		        	args = directiveArgs.getDirectiveArguments();
		            Collections.sort(args);
		            curNo = prevNo = Integer.MAX_VALUE;
		            dataList = new ArrayList<Integer>();
		            int curVal = -1;
		            int prevVal = -1;
		            for (int i=0; i <= args.size(); i++)
		            // Note that i is allowed to go up to directiveArgs.size()
		            // This allows the last value to be handled correctly within the loop.
		            // Be careful not to de-reference vectIter when this happens.
		              {
		            	
		                if (i != args.size())
		                  {
		                	DirectiveArgument<Integer> vectIter = (DirectiveArgument<Integer>)args.get(i);
		                    curNo = vectIter.getId();
		                    curVal = vectIter.getValueAsInt();
		                  }
		                if (i == 0 || (prevNo == curNo - 1 && prevVal == curVal))
		                  dataList.add(curNo);
		                else
		                  {
		                    _textBuffer.append(' ');
		                    AppendRange(dataList, ' ', false, _textBuffer);
		                    temp = Integer.toString(prevVal);
		                    _textBuffer.append(',');
		                    _textBuffer.append(temp);
		                    dataList = new ArrayList<Integer>();
		                    dataList.add(curNo);
		                  }
		                prevNo = curNo;
		                prevVal = curVal;
		              }
		          }
		        break;

		      case DirectiveArgType.DIRARG_CHARGROUPS:
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            dataList = new ArrayList<Integer>(vectIter.getDataList());
		            _textBuffer.append(' ');
		            AppendRange(dataList, ':', true, _textBuffer);
		          }
		        break;

		      case DirectiveArgType.DIRARG_ITEMCHARLIST:
		        outputTextBuffer(0, 2, true);
		        args = directiveArgs.getDirectiveArguments(); 
		        Collections.sort(args);
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            _textBuffer.append("#");
		            if (directiveType == DirectiveType.CONFOR) /// Use names for Confor, but not for Key
		              {
		                Item item = dataSet.getItem((Integer)vectIter.getId());
		                temp = item.getDescription();
		                despaceRTF(temp, true); ////
		                _textBuffer.append(' ');
		                _textBuffer.append(temp);
		                _textBuffer.append("/ ");
		              }
		            else
		              {
		                curNo = (Integer)vectIter.getId();
		                _textBuffer.append(curNo);
		                _textBuffer.append(". ");
		              }
		            dataList = new ArrayList<Integer>(vectIter.getDataList());
		           
		            AppendRange(dataList,' ', true, _textBuffer);
		            outputTextBuffer(0, 2, true);
		          }
		        break;

		      case DirectiveArgType.DIRARG_ALLOWED:
		    	  args = directiveArgs.getDirectiveArguments(); 
			        Collections.sort(args);
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            curNo = (Integer)vectIter.getId();
		            _textBuffer.append(' ');
		            _textBuffer.append(curNo);
		            _textBuffer.append(',');
		            DeltaNumber aNumber;
		            List<Integer> tmpData = vectIter.getDataList();
		            if (tmpData.size() < 3)
		              throw new RuntimeException("ED_INTERNAL_ERROR");
		            temp = Integer.toString(tmpData.get(0));
		            _textBuffer.append(temp + ':');
		            temp = Integer.toString(tmpData.get(1));
		            _textBuffer.append(temp + ':');
		            temp = Integer.toString(tmpData.get(2));
		            _textBuffer.append(temp);
		          }
		        break;

		      case DirectiveArgType.DIRARG_KEYSTATE:
		        // The comparison function will sort all the key states, grouping
		        // all those belonging to a single character, sorted in order by
		        // there pseudo-value.
		    	  args = directiveArgs.getDirectiveArguments(); 
			        Collections.sort(args);
		        prevNo = 0;
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            au.org.ala.delta.model.Character charBase = dataSet.getCharacter((Integer)vectIter.getId());
		            CharacterType charType = charBase.getCharacterType();
		            curNo = charBase.getCharacterId();
		            if (curNo != prevNo)
		              {
		                _textBuffer.append(" ").append(curNo);
		                prevNo = curNo;
		              }
		            else
		              _textBuffer.append('/');
		           
		            switch (charType)
		              {
		                case UnorderedMultiState:
		                  data = vectIter.getDataList();
		                  Collections.sort(data);
		                  for (int j=0; j<data.size(); j++)
		                    {
		                      if (j != 0)
		                        _textBuffer.append('&');
		                      _textBuffer.append(data.get(j));
		                    }
		                  break;

		                case OrderedMultiState:
		             
		                  {
		                	  data = vectIter.getDataList();
		                    int loState, hiState, aState;
		                   
		                    if (data.size() < 2)
		                      throw new RuntimeException("ED_INTERNAL_ERROR");
		                    aState = data.get(0);
		                    hiState =  data.get(1);
		                    loState = Math.min(aState, hiState);
		                    hiState = Math.max(aState, hiState);
		                    _textBuffer.append(loState);
		                    if (hiState > loState)
		                      {
		                        _textBuffer.append('-');
		                        _textBuffer.append(hiState);
		                      }
		                  }
		                  break;

		                case IntegerNumeric:
		                case RealNumeric:
		                  {
		                	List<BigDecimal> bigDecimals = vectIter.getData();
		                    BigDecimal loNumb, hiNumb;
		                    if (bigDecimals.size() < 2)
		                      throw new RuntimeException("ED_INTERNAL_ERROR");
		                    loNumb = bigDecimals.get(0);
		                    hiNumb = bigDecimals.get(1);
		                    if (loNumb.floatValue() == Float.MIN_VALUE)
		                      _textBuffer.append('~');
		                    else
		                      {
		                        _textBuffer.append(loNumb.toPlainString());
		                      }
		                    if (hiNumb.floatValue() == Float.MAX_VALUE)
		                      _textBuffer.append('~');
		                    else if (loNumb.compareTo(hiNumb) < 0)
		                      {
		                        if (!(loNumb.floatValue() == Float.MIN_VALUE))
		                          _textBuffer.append('-');
		                       
		                        _textBuffer.append(hiNumb.toPlainString());
		                      }
		                  }
		                  break;

		                default:
		                  throw new RuntimeException("ED_INAPPROPRIATE_TYPE");
		                  //break;
		              }
		          }
		        break;

		      case DirectiveArgType.DIRARG_PRESET:
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            curNo = (Integer)vectIter.getId();
		            _textBuffer.append(' ');
		            _textBuffer.append(curNo);
		            _textBuffer.append(',');
		            if (vectIter.getData().size() < 2)
		              throw new RuntimeException("ED_INTERNAL_ERROR");
		            _textBuffer.append(vectIter.getDataList().get(0));
		            _textBuffer.append(':');
		            _textBuffer.append(vectIter.getDataList().get(1));
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ONOFF:
		        if (directiveArgs.size() > 0 && directiveArgs.getFirstArgumentValue() != 0.0)
		          {
		            _textBuffer.append(' ');
		            if (directiveArgs.getFirstArgumentValue() < 0.0)
		              _textBuffer.append("Off");
		            else if (directiveArgs.getFirstArgumentValue() > 0.0)
		              _textBuffer.append("On");
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ITEM:
		        if (directiveArgs.size() > 0)
		          {
		            if (directiveArgs.getFirstArgumentIdAsInt() > 0)
		              {
		                _textBuffer.append(' ');
		                curNo = directiveArgs.getFirstArgumentIdAsInt();
		                _textBuffer.append(curNo);
		              }
		            else
		              appendKeyword(directiveArgs.getFirstArgumentText());
		          }
		        break;

		      case DirectiveArgType.DIRARG_KEYWORD_CHARLIST:
		      case DirectiveArgType.DIRARG_KEYWORD_ITEMLIST:
		      case DirectiveArgType.DIRARG_INTKEY_CHARLIST:
		      case DirectiveArgType.DIRARG_INTKEY_ITEMLIST:
		        dataList = new ArrayList<Integer>();
		        for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
		          {
		            if ((Integer)vectIter.getId() > 0)
		              dataList.add((Integer)vectIter.getId());
		            else
		              appendKeyword(vectIter.getText(),
		                 (argType == DirectiveArgType.DIRARG_KEYWORD_CHARLIST ||
		                  argType == DirectiveArgType.DIRARG_KEYWORD_ITEMLIST) &&
		                  vectIter == directiveArgs.get(0));
		          }
		        if (dataList.size() > 0)
		          {
		            _textBuffer.append(' ');
		            AppendRange(dataList, ' ', true, _textBuffer);
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_CHARREALLIST:
		        if (directiveArgs.size() > 0)
		          {
		            // Will sort all keywords to appear before actual character numbers, and group characters appropriately
		        	args = directiveArgs.getDirectiveArguments(); 
			        Collections.sort(args);
		            curNo = Integer.MAX_VALUE;
		            prevNo = Integer.MAX_VALUE;
		            dataList = new ArrayList<Integer>();
		            BigDecimal curVal = BigDecimal.ZERO;
		            BigDecimal prevVal = BigDecimal.ZERO;
		            boolean firstChar = true;
		            for (int i=0; i <= args.size(); i++)
		            // Note that vectIter is allowed to equal .end()
		            // This allows the last value to be handled correctly within the loop.
		            // Be careful not to de-reference vectIter when this happens.
		              {
		                if (i != args.size())
		                  {
		                	DirectiveArgument<?> vectIter = args.get(i);
		                    curVal = vectIter.getValue();
		                    if ((Integer)vectIter.getId() <= 0)
		                      {
		                        appendKeyword(vectIter.getText());
		                        if (!(curVal.compareTo(BigDecimal.ZERO) < 0.0))
		                          {
		                            _textBuffer.append(',');
		                            _textBuffer.append(curVal.toPlainString());
		                          }
		                        continue;
		                      }
		                    curNo = (Integer)vectIter.getId();
		                  }
		                if (firstChar || (prevNo == curNo - 1 && prevVal.equals(curVal)))
		                  {
		                    dataList.add(curNo);
		                    firstChar = false;
		                  }
		                else if (dataList.size() > 0)
		                  {
		                    _textBuffer.append(' ');
		                    AppendRange(dataList, ' ', false, _textBuffer);
		                    if (!(prevVal.compareTo(BigDecimal.ZERO) < 0.0))
		                      {
		                       
		                        _textBuffer.append(',');
		                        _textBuffer.append(prevVal.toPlainString());
		                      }
		                    dataList = new ArrayList<Integer>();
		                    dataList.add(curNo);
		                  }
		                prevNo = curNo;
		                prevVal = curVal;
		              }
		          }
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ITEMCHARSET:
		        writeIntItemCharSetArgs(directiveArgs, _textBuffer);
		        break;

		      case DirectiveArgType.DIRARG_INTKEY_ATTRIBUTES:
		        writeIntkeyAttributesArgs(directiveArgs, _textBuffer);
		        break;

		      default:
		        break;
		    }
		}

	private void appendKeyword(String text, boolean b) {
		// TODO Auto-generated method stub

	}

	private void appendKeyword(String firstArgumentText) {
		// TODO Auto-generated method stub

	}

	private void despaceRTF(String temp, boolean b) {
		// TODO Auto-generated method stub

	}

	private void outputTextBuffer(int i, int j, boolean b) {
		_printer.writeJustifiedText(_textBuffer.toString(), -1);
		_textBuffer = new StringBuilder();

	}

	private void despaceRTF(String temp) {
		despaceRTF(temp, false);

	}

	void AppendRange(List<Integer> data, char separator, boolean doSort,
			StringBuilder textBuffer) {
		if (doSort) {
			Collections.sort(data);

		}
		int curNo = 0;
		int prevNo = 0;
		boolean inRange = false;
		for (int id : data) {
			curNo = id;
			if (prevNo > 0 && prevNo == curNo - 1) {
				if (!inRange) {
					textBuffer.append('-');
					inRange = true;
				}
			} else {
				if (inRange) {
					textBuffer.append(prevNo);
					inRange = false;
				}
				if (prevNo > 0)
					textBuffer.append(separator);
				textBuffer.append(curNo);
			}
			prevNo = curNo;
		}
		if (inRange)
			textBuffer.append(curNo);
	}
	
	private void writeIntItemCharSetArgs(DirectiveArguments directiveArgs, StringBuilder textBuffer) {
//		if (directiveArgs.size() > 0)
//        {
//          enum state
//            { IN_MODIFIERS,
//              IN_TAXA,
//              IN_CHARS
//            } curState = IN_MODIFIERS;
//          dataList.resize(0);
//          for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
//            {
//              TDeltaNumber curVal = vectIter.value;
//              if (curState == IN_MODIFIERS && curVal != 0.0)
//                {
//                  if (!(curVal < 0.0))
//                    break;  // No taxa present, so nothing else matters
//                  curState = IN_TAXA;
//                  dataList.resize(0);
//                  textBuffer.append(" (");
//                }
//              if (curState == IN_TAXA && curVal > 0.0)
//                {
//                  if (dataList.size() > 0)
//                    AppendRange(dataList, ' ', true, textBuffer);
//                  textBuffer.append(')');
//                  curState = IN_CHARS;
//                  dataList.resize(0);
//                }
//              if (curState == IN_CHARS && !(curVal > 0.0))
//                break;  // Should never happen, unless arguments were somehow reordered
//              if (vectIter.getId() != VOUID_NULL)
//                dataList.add(vectIter.getId());
//              else
//                AppendKeyword(vectIter.text,
//                   (argType == DIRARG_KEYWORD_CHARLIST ||
//                    argType == DIRARG_KEYWORD_ITEMLIST) &&
//                    vectIter == directiveArgs.begin(),
//                    !(curState == IN_TAXA && textBuffer[textBuffer.size() - 1] == '('));
//            }
//          if (dataList.size() > 0)
//            {
//              textBuffer.append(' ');
//              AppendRange(dataList, ' ', true, textBuffer);
//            }
//        }
	}
	
	private void writeIntkeyAttributesArgs(DirectiveArguments directiveArgs, StringBuilder textBuffer) {
//		for (DirectiveArgument<?> vectIter : directiveArgs.getDirectiveArguments())
//        {
//          Character charBase = null;
//          if (vectIter.getId() == VOUID_NULL)
//            AppendKeyword(vectIter.text);
//          else
//            {
//              curNo = vectIter.getId();
//              textBuffer.append(' ');
//              textBuffer.append(curNo);
//              charBase = dataSet.getCharacter(curNo);
//
//              temp = "";
//              String text;
//              for (TAttribute::iterator iter = vectIter.attrib.begin(); iter != vectIter.attrib.end(); ++iter)
//                {
//                  (*iter).GetAsText(text, charBase.getCharacterType().isText() ? null : charBase);
//                  temp += text;
//                }
//              if (temp.size() > 0)
//                {
//                  if (temp.find(' ') != temp.npos)
//                    temp = "\"" + temp + "\"";
//                  textBuffer.append(',');
//                  textBuffer.append(temp);
//                }
//            }
//        }
	}
}
