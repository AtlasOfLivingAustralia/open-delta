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
package au.org.ala.delta.delfor.directives;

import java.io.Reader;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.math.IntRange;

import au.org.ala.delta.delfor.DelforContext;
import au.org.ala.delta.delfor.format.StateReorderer;
import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.directives.args.DirectiveArgType;
import au.org.ala.delta.directives.args.DirectiveArgsParser;
import au.org.ala.delta.directives.args.DirectiveArguments;

/**
 * Processes the NEW STATE ORDER directive.
 */
public class NewStateOrders extends AbstractDirective<DelforContext> {

	private DirectiveArguments _args;
	
	
	public NewStateOrders() {
		super("new", "state", "orders");
	}
	
	@Override
	public DirectiveArguments getDirectiveArgs() {
		return _args;
	}

	@Override
	public void parse(DelforContext context, String data) throws ParseException {
		_args = new DirectiveArguments();
		_args.addTextArgument(data.trim());
		
	}

	@Override
	public void process(DelforContext context, DirectiveArguments directiveArguments) throws Exception {
		String data = directiveArguments.getFirstArgumentText();
		
		NewStateOrdersParser parser = new NewStateOrdersParser(context, new StringReader(data));
		parser.parse();
	}

	@Override
	public int getArgType() {
	     return DirectiveArgType.DIRARG_OTHER;
	}

	@Override
	public int getOrder() {
		return 4;
	}
	
	class NewStateOrdersParser extends DirectiveArgsParser {

		public NewStateOrdersParser(DelforContext context, Reader reader) {
			super(context, reader);
		}

		@Override
		public void parse() throws ParseException {
			
			readNext();
			skipWhitespace();
			while (_currentInt >=0) {
				IntRange charNumbers = readIds();
				expect(',');
				readNext();
				
				List<Integer> newOrder = readStateOrder();
				
				for (int charNum : charNumbers.toArray()) {
					StateReorderer stateReorderer = new StateReorderer(charNum, newOrder);
					((DelforContext)_context).addFormattingAction(stateReorderer);
				}
				skipWhitespace();
			}
			
		}
		
		private List<Integer> readStateOrder() throws ParseException {
			List<Integer> newOrder = new ArrayList<Integer>();
			
			addStates(newOrder);
			while (_currentChar == ':') {
				readNext();
				addStates(newOrder);
			}
			
			return newOrder;
		}
		
		private void addStates(List<Integer> newOrder) throws ParseException {
			IntRange states = readIds();
			for (int state : states.toArray()) {
				newOrder.add(state);
			}
		}
		
	}
	
}
