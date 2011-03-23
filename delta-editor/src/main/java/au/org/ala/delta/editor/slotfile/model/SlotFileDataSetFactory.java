package au.org.ala.delta.editor.slotfile.model;

import au.org.ala.delta.editor.slotfile.DeltaVOP;
import au.org.ala.delta.editor.slotfile.VOCharBaseDesc;
import au.org.ala.delta.editor.slotfile.VOCharTextDesc;
import au.org.ala.delta.editor.slotfile.VOItemDesc;
import au.org.ala.delta.model.Character;
import au.org.ala.delta.model.CharacterFactory;
import au.org.ala.delta.model.CharacterType;
import au.org.ala.delta.model.DeltaDataSet;
import au.org.ala.delta.model.DeltaDataSetFactory;
import au.org.ala.delta.model.Item;


/**
 * Creates instances of the DELTA model classes backed by slotfile virtual objects.
 */
public class SlotFileDataSetFactory implements DeltaDataSetFactory {

	/** The Virtual Object that represents the whole data set */
	private DeltaVOP _vop;
	
	
	/**
	 * Creates a new instance of the SlotFileDataSetFactory without an existing DeltaVOP.
	 * A SlotFileDataSetFactory created in this way will create and initialise a new DeltaVOP.
	 */
	public SlotFileDataSetFactory() {
		_vop =  new DeltaVOP();
		initialiseVOP();
	}
	
	/**
	 * Creates a new instance of the SlotFileDataSetFactory that can create instances of the model
	 * classes associated backed by the supplied VOP.
	 * @param vop the Virtual Object that represents the whole data set and provides access to the slot file.
	 */
	public SlotFileDataSetFactory(DeltaVOP vop) {
		_vop = vop;
	}
	
	/**
	 * Creates a new DeltaDataSet backed by our VOP.
	 * @param name ignored in this case as the VOP already has a file name associated with it.
	 */
	@Override
	public DeltaDataSet createDataSet(String name) {

		DeltaDataSet dataSet = new SlotFileDataSet(_vop, this);
		return dataSet;
	}

	/**
	 * Creates a new Item backed by a VOItemAdaptor.
	 * @param number identifies the item. Items in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Item createItem(int number) {
		int itemId = _vop.getDeltaMaster().uniIdFromItemNo(number);
		VOItemDesc itemDesc = (VOItemDesc) _vop.getDescFromId(itemId);
		VOItemAdaptor adaptor = new VOItemAdaptor(itemDesc, number);
		return new Item(adaptor, number);
	}

	/**
	 * Creates a new Character of the specified type backed by a VOCharacterAdaptor.
	 * If the supplied character number exists in the model, it will be wrapped in a 
	 * model Character object and returned, otherwise it will be created first.
	 * 
	 * @param type the type of character to create.
	 * @param number identifies the character. Characters in a DeltaDataSet must have unique numbers.
	 */
	@Override
	public Character createCharacter(CharacterType type, int number) {
		
		Character character = CharacterFactory.newCharacter(type, number);
		VOCharBaseDesc characterDesc = null;
		
		if (number > _vop.getDeltaMaster().getNChars()) {
			characterDesc = newVOCharDesc(type, number);
		}
		else {
			int charId = _vop.getDeltaMaster().uniIdFromCharNo(number);	
			characterDesc = (VOCharBaseDesc)_vop.getDescFromId(charId);
			
		}
		VOCharTextDesc textDesc = characterDesc.readCharTextInfo(0, (short) 0);
		VOCharacterAdaptor characterAdaptor = new VOCharacterAdaptor(characterDesc, textDesc);		
		character.setImpl(characterAdaptor);	
		return character;
	}
	
	private VOCharBaseDesc newVOCharDesc(CharacterType type, int characterNumber) {
		VOCharBaseDesc.CharBaseFixedData characterFixedData = new VOCharBaseDesc.CharBaseFixedData();
		VOCharBaseDesc characterBase = (VOCharBaseDesc)_vop.insertObject(characterFixedData, VOCharBaseDesc.CharBaseFixedData.SIZE, null, 0, 0);
		int charId = characterBase.getUniId();
		_vop.getDeltaMaster().insertCharacter(charId, characterNumber);
		
		characterBase.setCharType((short)CharacterTypeConverter.toCharType(type));
		
		return characterBase;
	}
	
	/**
	 * Populates the VOP with the set of template directives files that are distributed
	 * with the DELTA suite.  These templates take the form of _<type>_<filename> where type
	 * can be one of "c" (confor), "i" (intkey), "k" (key) or "d" (dist).
	 */
	private void initialiseVOP() {
		
		
		/*
	          char buffer[MAX_PATH];
	          char curDir[MAX_PATH];
	          GetCurrentDirectory(sizeof(curDir), curDir);
	          GetDocManager().GetApplication()->GetModuleFileName (buffer, _MAX_PATH);

	          char* pathEnd = strrchr(buffer, '\\');
	          if (!pathEnd)
	            return status;
	          *(pathEnd + 1) = 0;
	          strcat(buffer, "_?_*");
	          WIN32_FIND_DATA findData;
	          HANDLE finder = FindFirstFile(buffer, &findData);
	          if (pathEnd == buffer || *(pathEnd - 1) == ':')
	            ++pathEnd;
	          *pathEnd = 0;
	          SetCurrentDirectory(buffer);
	          if (finder != INVALID_HANDLE_VALUE)
	            {
	              // Do a completely silent import...
	              TImportStatusDialog* statusDialog = new TImportStatusDialog(GetDocManager().GetApplication()->GetMainWindow(), "IMPORTSTATUSDIALOG");
	              statusDialog->Create();
	              statusDialog->pauseCheck->Uncheck();
	              GetDocManager().GetApplication()->GetMainWindow()->SetActiveWindow();
	              ::SetCursor(::LoadCursor(0, IDC_WAIT));

	              TDirectivesInOut dirInOut(this, statusDialog);
	              dirInOut.EnableStatusBar(false);
	              char fileTitle[MAX_PATH];
	              DWORD noUse = FILE_ATTRIBUTE_DIRECTORY | FILE_ATTRIBUTE_HIDDEN | FILE_ATTRIBUTE_TEMPORARY;
	              do
	                {
	                  if (!(findData.dwFileAttributes & noUse) &&
	                      MyGetFileTitle(findData.cFileName, fileTitle, sizeof(fileTitle)) == 0)
	                    {
	                      char typeCh = tolower(fileTitle[1]);
	                      short progType;
	                      if (typeCh == 'c')
	                        progType = PROGTYPE_CONFOR;
	                      else if (typeCh == 'i')
	                        progType = PROGTYPE_INTKEY;
	                      else if (typeCh == 'd')
	                        progType = PROGTYPE_DIST;
	                      else if (typeCh == 'k')
	                        progType = PROGTYPE_KEY;
	                      else
	                        continue;
	                      TVODirFileDesc* newDirFile = CreateDirFile(findData.cFileName, 0, progType << 16, false);
	                      if (newDirFile == NULL)
	                        continue;
	                      std::string tempName;
	                      try
	                        {
	                          dirInOut.Init();
	                          if (dirInOut.ReadDirectivesFile(newDirFile, tempName, true))
	                            {
	                              newDirFile->SetFileName(fileTitle + 3);
	                              SYSTEMTIME sysTime;
	                              FILETIME modTime;
	                              GetSystemTime(&sysTime);
	                              if (SystemTimeToFileTime(&sysTime, &modTime))
	                                newDirFile->SetFileModifyTime(modTime);
	                              NotifyDirFileCreate(newDirFile);
	                            }
	                          else
	                            throw TDirInOutEx(ED_DELTADOC_UNOPENED);
	                        }
	                      catch (TDirInOutEx& ex)
	                        {
	                          DeleteDirFile(newDirFile->GetUniId(), false);
	                        }
	                    }
	                }
	              while (FindNextFile(finder, &findData));
	              FindClose(finder);
	              ::SetCursor(::LoadCursor(0, IDC_ARROW));
	            }
	          SetCurrentDirectory(curDir);
	          BuildSpecialDirFiles();
	          status = true;
	          Commit(); */
		
	}
}
