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
package au.org.ala.delta.editor.slotfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.org.ala.delta.Logger;

public class DirEnumConverter {
	
	static List<LineMatcher> _lineMatchers = new ArrayList<LineMatcher>();
	
	static {
		_lineMatchers.add(new EnumStartMatcher());
		_lineMatchers.add(new DirStartMatcher());
		_lineMatchers.add(new DirLevelMatcher());
		_lineMatchers.add(new ArgTypeMatcher());
		_lineMatchers.add(new InFuncMatcher());
		_lineMatchers.add(new OutFuncMatcher());
		_lineMatchers.add(new DirNumberMatcher());		
	}

	public static void main(String[] args) throws Exception {

		BufferedReader reader = new BufferedReader(new FileReader("c:/zz/intkeydir.txt"));

		String line = reader.readLine();
		
		ConverterState state = new ConverterState();
		
		while (line != null) {			
			for (LineMatcher m : _lineMatchers) {
				m.process(line, state);
			}
			line = reader.readLine();
		}
		
		for (String enumName : state.EnumMap.keySet()) {
			List<DirModel> list = state.EnumMap.get(enumName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(String.format("c:/zz/%sType.java", enumName)));
			writer.write(String.format("public class %sType {\n", enumName));
			writer.write(String.format("    public static Directive[] %sArray = new Directive[] {\n", enumName));
			int i = 0;
			for (DirModel dir : list) {
				writer.write(String.format("        new Directive(new String[] {\"%s\", \"%s\", \"%s\", \"%s\"}, %d, %s, DirectiveArgType.%s, %s, %s)",
						dir.Names[0], dir.Names[1], dir.Names[2], dir.Names[3],
						dir.DirLevel, dir.DirNumber, dir.ArgType, dir.InDirFunc, dir.OutDirFunc));
				if (i < list.size()-1) {
					writer.write(",\n");
				} else {
					writer.write("\n");
				}	
				++i;
			}
			
			writer.write(String.format("    };\n\n"));
			writer.write(String.format("}\n", enumName));			
			writer.close();
		}
		

	}
	
	
}

class ConverterState {
	
	public DirModel CurrentDirective = null;	
	public List<DirModel> CurrentList = null;
	public Map<String, List<DirModel>> EnumMap = new HashMap<String, List<DirModel>>();	
}

class DirModel {	
	public String[] Names = new String[4];
	public int DirLevel;
	public String DirNumber;
	public String ArgType;
	public String InDirFunc;
	public String OutDirFunc;
}

abstract class LineMatcher {
	private Pattern _pattern;
	
	protected LineMatcher(String regex) {		
		_pattern = Pattern.compile(regex);
	}
	
	public void process(String line, ConverterState state) {
		Matcher m = _pattern.matcher(line);
		if (m.matches()) {
			processImpl(line, state, m);
		}
	}
	
	protected abstract void processImpl(String line, ConverterState state, Matcher m);
	
}

class DirLevelMatcher extends LineMatcher {
	public DirLevelMatcher() {
		super("^\\s*(\\d{1}),\\s*$");
	}

	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		state.CurrentDirective.DirLevel = Integer.parseInt(m.group(1));
		Logger.debug("Directive Level: %d", state.CurrentDirective.DirLevel);
	}
	
}

class DirStartMatcher extends LineMatcher {
	
	public DirStartMatcher() {
		super("^\\s*\\{\\{\\s*\\\"(\\w+)\\\"\\s*,\\s*\\\"(\\w*)\\\"\\s*,\\s*\\\"(\\w*)\\\"\\s*,\\s*\\\"(\\w*)\\\"\\s*\\},\\s*$");
	}

	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		state.CurrentDirective = new DirModel();
		state.CurrentDirective.Names = new String[] { m.group(1), m.group(2), m.group(3), m.group(4) };	
		state.CurrentList.add(state.CurrentDirective);		
		Logger.debug("encountered new directive: \"%s\", \"%s\", \"%s\", \"%s\"", state.CurrentDirective.Names);
	}
	
}

class EnumStartMatcher extends LineMatcher {

	protected EnumStartMatcher() {
		super("^TDirective TDirectivesInOut::(\\w+)[\\[][\\]]\\s+\\=\\s*$");
	}

	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		Logger.debug("Found new array: %s", m.group(1));
		List<DirModel> list = new ArrayList<DirModel>();
		state.EnumMap.put(m.group(1), list);
		state.CurrentList = list;
	}
	
}

class ArgTypeMatcher extends LineMatcher {
	public ArgTypeMatcher() {
		super("^\\s*(DIRARG_\\w*),$");
	}

	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		state.CurrentDirective.ArgType = m.group(1);
		Logger.debug("ArgType: %s", state.CurrentDirective.ArgType);
	}
	
}

class InFuncMatcher extends LineMatcher {
	
	public InFuncMatcher() {
		super("^\\s*&TDirectivesInOut::(\\w+),\\s*$");
	}
	
	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		String s = m.group(1);
		if (!s.equals("null")) {
			s = String.format("new %s()", s);
		}
		state.CurrentDirective.InDirFunc = s;
	}
}

class OutFuncMatcher extends LineMatcher {
	
	public OutFuncMatcher() {
		super("^\\s*&TDirectivesInOut::(\\w+)},*\\s*$");
	}
	
	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {
		String s = m.group(1);
		if (!s.equals("null")) {
			s = String.format("new %s()", s);
		}
		state.CurrentDirective.OutDirFunc = s;
	}
}

class DirNumberMatcher extends LineMatcher {
	
	public DirNumberMatcher() {
		super("^\\s*(\\w+)::([A-Z_0-9]+),$");
	}

	@Override
	protected void processImpl(String line, ConverterState state, Matcher m) {		
		state.CurrentDirective.DirNumber = String.format("%sDirType.%s",m.group(1), m.group(2));
	}
}



