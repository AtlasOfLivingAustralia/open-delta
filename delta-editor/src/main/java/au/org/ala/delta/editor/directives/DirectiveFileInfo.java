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
package au.org.ala.delta.editor.directives;

import au.org.ala.delta.editor.slotfile.model.DirectiveFile;
import au.org.ala.delta.editor.slotfile.model.DirectiveFile.DirectiveType;

/**
 * A DirectiveFileInfo provides information about a directive file that is
 * used by the import/export process.  It is separate from the
 * DirectiveFile model class because the import process needs this information
 * but will not necessarily have a slotfile backed DirectiveFiles available for
 * use.
 */
public class DirectiveFileInfo {

	private String _fileName;
	private DirectiveType _type;
	private DirectiveFile _directiveFile;
	private String _name;
    private boolean _itemsFile;
	private boolean _charsFile;
    private boolean _specsFile;

	public DirectiveFileInfo(String fileName) {
		this(fileName, fileName, null, null);
	}
	
	public DirectiveFileInfo(String fileName, DirectiveType type) {
		this(fileName, fileName, type, null);
	}
	
	public DirectiveFileInfo(String name, String fileName, DirectiveType type) {
		this(name, fileName, type, null);
	}
	
	public DirectiveFileInfo(DirectiveFile file) {
		_name = file.getFileName();
		_fileName = _name;
		_type = file.getType();
		_directiveFile = file;
	}
	
	public DirectiveFileInfo(String fileName, DirectiveType type, DirectiveFile file) {
		this(fileName, fileName, type, file);
	}
	
	public DirectiveFileInfo(String name, String fileName, DirectiveType type, DirectiveFile file) {
		_name = name;
		setFileName(fileName);
		setType(type);
		setDirectiveFile(file);
	}

    public boolean isItemsFile() {
        if (_directiveFile != null) {
            return _directiveFile.isItemsFile();
        }

        return _itemsFile;
    }

    public void setItemsFile(boolean itemsFile) {
        if (_directiveFile != null) {
            _directiveFile.setItemsFile(itemsFile);
        }
        _itemsFile = itemsFile;
    }

    public boolean isCharsFile() {
        if (_directiveFile != null) {
            return _directiveFile.isCharsFile();
        }

        return _charsFile;
    }

    public void setCharsFile(boolean charsFile) {
        if (_directiveFile != null) {
            _directiveFile.setCharsFile(charsFile);
        }
        _charsFile = charsFile;
    }

    public boolean isSpecsFile() {
        if (_directiveFile != null) {
            return _directiveFile.isSpecsFile();
        }

        return _specsFile;
    }

    public void setSpecsFile(boolean specsFile) {
        if (_directiveFile != null) {
            _directiveFile.setCharsFile(specsFile);
        }
        _specsFile = specsFile;
    }

	/**
	 * @return the desired name of the DirectiveFile, which may be different
	 * to the fileName (in the case of importing template directive files
	 * when creating a new dataset.
	 */
	public String getName() {
		return _name;
	}
	
	public String getFileName() {
		return _fileName;
	}
	public void setFileName(String fileName) {
		_fileName = fileName;
	}
	public DirectiveType getType() {
		return _type;
	}
	public void setType(DirectiveType type) {
		_type = type;
	}
	public DirectiveFile getDirectiveFile() {
		return _directiveFile;
	}
	public void setDirectiveFile(DirectiveFile directiveFile) {
		_directiveFile = directiveFile;
	}
	
	
	
	public String toString() {
		StringBuilder result = new StringBuilder(_fileName);
		if (_type != null) {
			result.append(" (").append(_type.getAbbreviation()).append(")");
		}
		return result.toString();
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_fileName == null) ? 0 : _fileName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DirectiveFileInfo other = (DirectiveFileInfo) obj;
		if (_fileName == null) {
			if (other._fileName != null)
				return false;
		} else if (!_fileName.equals(other._fileName))
			return false;
		return true;
	}

	
}
