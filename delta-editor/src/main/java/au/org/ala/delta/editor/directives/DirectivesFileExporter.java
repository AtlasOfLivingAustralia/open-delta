package au.org.ala.delta.editor.directives;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import au.org.ala.delta.editor.slotfile.Directive;
import au.org.ala.delta.editor.slotfile.DirectiveInstance;
import au.org.ala.delta.editor.slotfile.directive.DirectiveInOutState;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.util.FileUtils;

public class DirectivesFileExporter {

	public void writeDirectivesFile(DirectiveFile file, DirectiveInOutState state) {
		try {
			List<DirectiveInstance> directives = file.getDirectives();

			for (int i = 0; i < directives.size(); i++) {
				writeDirective(directives.get(i), state);
				if (i != directives.size() - 1) {
					state.getPrinter().writeBlankLines(1, 0);
				}
			}
			state.getPrinter().printBufferLine();
			file.setLastModifiedTime(System.currentTimeMillis());

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (state.getPrinter() != null) {
				state.getPrinter().close();
			}
		}
	}
	
	public File createExportFile(DirectiveFile file, String directoryPath) {
		String fileName = file.getShortFileName();
		FileUtils.backupAndDelete(fileName, directoryPath);
		
		FilenameUtils.concat(directoryPath, fileName);
		File directivesFile = new File(directoryPath + fileName);
		
		return directivesFile;
	}

	protected void writeDirective(DirectiveInstance directive,
			DirectiveInOutState state) {
		
		state.setCurrentDirective(directive);
		Directive directiveInfo = directive.getDirective();

		directiveInfo.getOutFunc().process(state);
	}
}
