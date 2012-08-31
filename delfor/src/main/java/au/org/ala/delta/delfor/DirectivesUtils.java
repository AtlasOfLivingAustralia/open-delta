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
package au.org.ala.delta.delfor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;
import au.org.ala.delta.editor.slotfile.directive.DistDirType;
import au.org.ala.delta.editor.slotfile.directive.IntkeyDirType;
import au.org.ala.delta.editor.slotfile.directive.KeyDirType;

public class DirectivesUtils {

	public static List<Directive> mergeAllDirectives() {
		
		List<Directive> allDirectives = new ArrayList<Directive>();
		allDirectives.addAll(Arrays.asList(ConforDirType.ConforDirArray));
		addUniqueDirectives(KeyDirType.KeyDirArray, allDirectives);
		addUniqueDirectives(DistDirType.DistDirArray, allDirectives);
		addUniqueDirectives(IntkeyDirType.IntkeyDirArray, allDirectives);
	
		return allDirectives;
	}
	
	/**
	 * We can get away with this because when directive share names they
	 * also share argument types.
	 * @param directives the directives to add.
	 * @param allDirectives the list to add the directives to.
	 */
	private static void addUniqueDirectives(Directive[] directives, List<Directive> allDirectives) {
		for (Directive d: directives) {
			if (!containsByName(d, allDirectives)) {
				allDirectives.add(d);
			}
		}
	}
	
	public static boolean containsByName(Directive d, List<Directive> allDirectives) {
		String name = d.joinNameComponents().toUpperCase();
		return containsName(name, allDirectives);
	}
	
	public static boolean containsByName(AbstractDirective<?> directive, List<Directive> allDirectives) {
		String name = StringUtils.join(directive.getControlWords(), " ").toUpperCase();
		return containsName(name, allDirectives);	
	}
	
	private static boolean containsName(String name, List<Directive> allDirectives) {
		for (Directive tmp : allDirectives) {
			if (name.equals(tmp.joinNameComponents().toUpperCase())) {
				return true;
			}
		}
		return false;
	}

}
