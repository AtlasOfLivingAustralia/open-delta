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
package au.org.ala.delta.directives;

import java.util.HashMap;

import au.org.ala.delta.DeltaContext;
import au.org.ala.delta.Logger;
import au.org.ala.delta.TranslateType;
import au.org.ala.delta.directives.args.DirectiveArguments;
import au.org.ala.delta.directives.validation.DirectiveError;

public class TranslateInto extends AbstractTextDirective {
	
	private static HashMap<String, TranslateType>  TRANSLATE_TYPE_MAP = new HashMap<String, TranslateType>();
	
	static {
		TRANSLATE_TYPE_MAP.put("NATLAN", TranslateType.NaturalLanguage);
		TRANSLATE_TYPE_MAP.put("KEYFOR", TranslateType.Key);
		TRANSLATE_TYPE_MAP.put("HENFOR", TranslateType.Hennig86);
		TRANSLATE_TYPE_MAP.put("DELFOR", TranslateType.Delta);
		TRANSLATE_TYPE_MAP.put("ALIFOR", TranslateType.Alice);
		TRANSLATE_TYPE_MAP.put("NEXFOR", TranslateType.NexusFormat);
		TRANSLATE_TYPE_MAP.put("EXIFOR", TranslateType.EXIR);
		TRANSLATE_TYPE_MAP.put("PAYFOR", TranslateType.Payne);
		TRANSLATE_TYPE_MAP.put("DISFOR", TranslateType.Dist);
		TRANSLATE_TYPE_MAP.put("PAUFOR", TranslateType.PAUP);	
		TRANSLATE_TYPE_MAP.put("INTFOR", TranslateType.IntKey);	
		
	}

	public TranslateInto() {
		super("translate", "into");
	}

	@Override
	public void process(DeltaContext context, DirectiveArguments args) throws Exception {
		
		String data = args.getFirstArgumentText();
		
		String[] bits = data.split(" ");
		assert bits.length == 2;		
		String code = (bits[0].substring(0,3) + bits[1].substring(0,3)).toUpperCase();
		if (TRANSLATE_TYPE_MAP.containsKey(code)) {
			TranslateType t = TRANSLATE_TYPE_MAP.get(code);
			switch (t) {
				case NaturalLanguage:
				case NexusFormat:
				case Delta:
				case IntKey:
				case Key:
				case Dist:
				case PAUP:
				case Hennig86:
				case Payne:
					break;
				default:
					throw DirectiveError.asException(DirectiveError.Error.UNSUPPORTED_TRANSLATION, 0, data);
			}
			
			Logger.debug("Setting Translate Type to %s", t);
			
			context.setTranslateType(t);
			
		} else {
			throw DirectiveError.asException(DirectiveError.Error.UNSUPPORTED_TRANSLATION, 0, data);
		}

	}
	
	protected boolean ignoreEmptyArguments() {
		return false;
	}
	
	@Override
	public int getOrder() {
		return 4;
	}

}
