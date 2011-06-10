package au.org.ala.delta.editor.directives;

import java.util.Arrays;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.directive.ConforDirType;

public class FindoutMissingDirectives {
	public static void main(String[] args) {
		Directive[] directives = ConforDirType.ConforDirArray; 
		
		for (int i=0; i<directives.length; i++) {
			System.out.print(directives[i].getNumber()+", "+Arrays.asList(directives[i].getName()));
			System.out.println(", "+directives[i].getImplementationClass());
			
		}
	}
}
