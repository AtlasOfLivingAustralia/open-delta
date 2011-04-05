package au.org.ala.delta.intkey.model.ported;

import java.io.File;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.io.BinFile;
import au.org.ala.delta.io.BinFileMode;


/**
 * 
 * @author Chris
 *
 */

public class DataSet {
    
    private static final int sizeIntInBytes = Integer.SIZE / Byte.SIZE;
    
    public DataSet() {
        _initDone = false;
        _noDeltaOutput = false;
    }
    
  //-----------------------Init-------------------------------------------------//
 // reads in data set parameters
 // revised 29-jun-00.
    
private void seekToRecord(BinFile bFile, int recordNumber) {
    bFile.seek(recordNumber * Constants.LREC * sizeIntInBytes); 
}

private String readString(BinFile bFile, int length) {
    byte[] bytes = bFile.read(length);
    return new String(bytes);
}
  
 public void init(File cFile, File tFile)
 {
     int recno, rpCdat, rpCdep, rpCheckForCd, rpCimagesC, rpCimagesI, rpCsynon,
          rpFont, rpNewPara, rpNext, rpOmitOr, rpOmitPeriod,
          rpHeading, rpInvdep, rpItemSubHead, rpOrWord, rpRegSubHeading, rpSpec,
          rpStat, rpUseCc=0, rpNonAutoCc=0, rpValidationString;
     int cptr, dupItemPtr, enableDeltaOutput, i, lbtree, lkstat,
          nc, nrealc=0, ns, tmp, tptr, wrd;
     int[] fparam = new int[Constants.LREC];
     float verd, verp;
     int bit, j, last_used=26, len, maxint, ms, type,
         ver_major=5, ver_minor=2;
     String validationString;

     if (_initDone) {
       clear();
     }
     
     BinFile cBinFile = new BinFile(cFile.getAbsolutePath(), BinFileMode.FM_READONLY);
     BinFile tBinFile = new BinFile(tFile.getAbsolutePath(), BinFileMode.FM_READONLY);
  
     // read first record of characters file
     nc = cBinFile.readInt(); // 0
     
     cBinFile.readInt(); // 1
     
     _rpCdes = cBinFile.readInt(); // 2
     rpStat = cBinFile.readInt(); // 3
     _rpChlp = cBinFile.readInt(); // 4
     _rpChlpGrp = cBinFile.readInt(); // 5
     _rpChlpFmt1 = cBinFile.readInt(); // 6
     _rpChlpFmt2 = cBinFile.readInt(); // 7
     rpCimagesC = cBinFile.readInt(); // 8
     _rpStartupImages = cBinFile.readInt(); // 9
     _rpCKeyImages = cBinFile.readInt(); // 10
     _rpTKeyImages = cBinFile.readInt(); // 11
     rpHeading = cBinFile.readInt(); // 12
     rpRegSubHeading = cBinFile.readInt(); // record pointer to registration subheading (13)
     rpValidationString = cBinFile.readInt(); // record pointer to validation string for registered dataset (14)
     
     cBinFile.readInt(); // 15
     
     rpOrWord = cBinFile.readInt(); // 16
     rpCheckForCd = cBinFile.readInt(); // 17
     rpFont = cBinFile.readInt(); //18
     rpItemSubHead = cBinFile.readInt(); // 19
     
     seekToRecord(cBinFile, Constants.LREC - 2);
     
     cptr = cBinFile.readInt();
  
     /*CCCif (!cptr)
     {
       _lclose(cFile);
       if ((cFile = OpenFile((LPSTR)NULL, &Datafile[CHRDAT].Ofstr,
         OF_REOPEN | OF_READ | OF_SHARE_DENY_WRITE)) == HFILE_ERROR)
         message(5, MB_OK, 2, 1);
       Datafile[CHRDAT].hfile = cFile;
       Datafile[HLPCHR].hfile = cFile;
       // reposition to second record
       _llseek(cFile, LREC*sizeof(long), SEEK_SET);
     }ccc*/
  
     // read first record of items file
     
     _nItem = tBinFile.readInt();             // number of items (0)
     _nChar = tBinFile.readInt();             // number of characters (1) 
     ms = tBinFile.readInt();                // maximum number of states (2)
     
     tBinFile.readInt(); // 3
     int itemsFileRecordLength = tBinFile.readInt(); // 4
     
     _rpTnam = tBinFile.readInt();            // record pointer to taxon names (5) 
     rpSpec = tBinFile.readInt();            // record pointer to specifications  (6)
     _rpMini = tBinFile.readInt();            // record pointer to minima of integer characters (7)
     _lDep = tBinFile.readInt();              // length of dependency array (8)
     rpCdep = tBinFile.readInt();            // record pointer to character dependency array (9)
     _lInvdep = tBinFile.readInt();          // length of inverted dependency array (10)
     rpInvdep = tBinFile.readInt();         // record pointer to inverted dependency array (11)
     rpCdat = tBinFile.readInt();           // record pointer to data for each character (12)
     _lSbnd = tBinFile.readInt();            // length of state bounds array (13)
     lkstat = Math.max(1, tBinFile.readInt());   // length of key states array (14)
     
     int itemsFileMajorVersion = tBinFile.readInt(); // 15
     
     _rpNkbd = tBinFile.readInt();           // record pointer to key state bounds array (16)
     maxint = tBinFile.readInt();           // maximum integer value (17)
     
     tBinFile.readInt(); // 18
     tBinFile.readInt(); // 19
     int itemsFileMinorVersion = tBinFile.readInt(); // 20
     
     Params.setTaxonImageChar(tBinFile.readInt());   // character specifying taxon images (21)
     rpCimagesI = tBinFile.readInt();        // pointer to character images (22)
     _rpTimages = tBinFile.readInt();        // pointer to taxon images (23)
     enableDeltaOutput = tBinFile.readInt(); // whether to allow DELTA output via OUTPUT SUMMARY command (24)
     _chineseFmt = tBinFile.readInt() > 0;       // whether chinese character set (25)
     rpCsynon = tBinFile.readInt();         // record pointer to characters for synonomy (26)
     rpOmitOr = tBinFile.readInt();         // record pointer to "omit or" list of characters (27)
     rpNext = tBinFile.readInt();           // pointer to second parameter record (28)
     dupItemPtr = tBinFile.readInt();   // pointer to duplicated item name mask (29: Constants.LREC - 3)
     tptr = tBinFile.readInt();         // pointer to b-tree and image masks appended to items file (30: Constants.LREC - 2)
     lbtree = tBinFile.readInt();       // length of btree in bytes (31: Constants.LREC - 1)
     
     // check compatability
     if (nc != _nChar) {           // same number of characters?
       //message(10, MB_OK, 2, 1);
       throw new RuntimeException("Differing number of characters");  
     }
     if (itemsFileRecordLength != Constants.LREC)         // correct record length?
       //message(11, MB_OK, 2, 1);
       throw new RuntimeException("Incorrect record length");  
     if (itemsFileMajorVersion != ver_major) // correct major version number?
     {
       //verd = (float)fparam[15] + ((float)fparam[20])/100.;
       //verp = (float)ver_major + ((float)ver_minor)/100.;
       //message(12, MB_OK, 2, 1, verd, verp);
         throw new RuntimeException("Differing major version");
     }
     
     //Not sure why rpOmitOr is being checked here. Original syntax was "fparam[last_used+1] != 0", where
     //last_used was set to 26. fparam was the array holding all of the integers in the record. CPF 4/4/2011. 
     if (itemsFileMinorVersion != ver_minor && rpOmitOr != 0) // correct minor version number?
     {
       //verd = (float)fparam[15] + ((float)fparam[20])/100.;
       //verp = (float)ver_major + ((float)ver_minor)/100.;
       //message(107, MB_OK, 0, 1, verd, verp);
         throw new RuntimeException("Differing minor version");
     }
     
     if (rpNext > 0)
     {
       // read second parameters record of items file
       
       // SHOULD THIS BE rpNext or npNext - 1?
       seekToRecord(tBinFile, rpNext - 1);  
         
  
       rpUseCc = tBinFile.readInt(); // pointer to USE CONTROLLING CHARACTER FIRST character list
       _rpTlinks[0] = tBinFile.readInt(); // pointer to taxon links information
       rpOmitPeriod = tBinFile.readInt(); // pointer to Omit Period for Characters information
       rpNewPara = tBinFile.readInt(); // pointer to New Paragraphs at Characters information
       rpNonAutoCc = tBinFile.readInt(); // pointer to NONAUTOMATIC CONTROL CHARACTERS chartacters
       _rpTlinks[1] = tBinFile.readInt(); // pointer to automatically generated taxon links information
     }
     else
     {
       rpUseCc = 0;
       _rpTlinks[0] = 0;
       _rpTlinks[1] = 0;
       rpOmitPeriod = 0;
       rpNewPara = 0;
       rpNonAutoCc = 0;
     }
     
     /* ccc
     if (!tptr)
     {
       _lclose(tFile);
       if ((tFile = OpenFile((LPSTR)NULL, &Datafile[ITMDAT].Ofstr,
         OF_REOPEN | OF_READ | OF_SHARE_DENY_WRITE)) == HFILE_ERROR)
         message(6, MB_OK, 2, 1);
       Datafile[ITMDAT].hfile = tFile;
       Datafile[NAMDAT].hfile = tFile;
       Datafile[CIMGDAT].hfile = tFile;
       // reposition to second record
       _llseek(tFile, LREC*sizeof(long), SEEK_SET);
     }
     ccc*/

     // read and display data heading
     BinFile hFile;
     if (rpHeading > 0)  // heading is in chars file
     {
       hFile = cBinFile;
       recno = rpHeading;
     }
     else
     {
       hFile = tBinFile;
       recno = 2;
     }

     seekToRecord(hFile, recno-1);
     len = hFile.readInt();
     seekToRecord(hFile, recno);
     _heading = readString(hFile, len);
     System.out.println(_heading);
     //output to log window
     //set as heading of main window

     /* ccc
     // check that there is a loaded CD containing the specified ichars file
     // with the same data set heading as the current data set
     if (tptr && rpCheckForCd)
     {
       // extract the name of the file to find on the CD
       _llseek(cFile, (rpCheckForCd-1)*LREC*sizeof(long), SEEK_SET);
       if (_lread(cFile, (char *)fparam, sizeof(long)*LREC) < sizeof(long)*LREC)
         message(8, MB_OK, 2, 1);
       len = (int)fparam[0];
       char *temp;
       if ((temp = (char *)malloc(len+1)) == NULL)
         InsuffMem();
       else
       {
         _lread(cFile, temp, len);
         temp[len] = '\0';
       }
       bool status = CheckForCD(temp);
       free(temp);
       if (!status)
       {
         free(heading);
         _lclose(Datafile[CHRDAT].hfile);
         _lclose(Datafile[ITMDAT].hfile);
         message(341, MB_OK, 2, 1);
       }
     }*/
     
     if (rpRegSubHeading > 0)
     {
       // read and display registered dataset subheading  
       seekToRecord(hFile, rpRegSubHeading-1);
       len = hFile.readInt();
       seekToRecord(hFile, rpRegSubHeading);
       String regSubHeading = readString(hFile, len);
       System.out.println(regSubHeading);
     }

     if (rpValidationString > 0)
     {
         // read validation string
         seekToRecord(hFile, rpValidationString-1);
         len = hFile.readInt();
         seekToRecord(hFile, rpValidationString);
         validationString = readString(hFile, len);
         System.out.println(validationString);
     }

     /*
     // set image and information paths for compressed files
 #ifdef __WIN32__
     char *dataSetImagePath = CurrentNetImagePath();
     if (dataSetImagePath && lstrlen(dataSetImagePath))
       SetIpath(dataSetImagePath, false, NImageDir, ImageDir);
  
     char *dataSetInfoPath = CurrentNetInfoPath();
     if (dataSetInfoPath && lstrlen(dataSetInfoPath))
       SetIpath(dataSetInfoPath, false, NInfoDir, InfoDir);
 #endif
  
     // read value of "or" string from chars file, if present, otherwise use the
     // English word.
     // This will be used in descriptions.
     // Note: "or" should be in the same language as the character descriptions,
     // which is not necessarily that which INTKEY is using.
     if (rpOrWord)
     {
       _llseek(cFile, (rpOrWord-1)*LREC*sizeof(long), SEEK_SET);
       if (_lread(cFile, (char *)fparam, sizeof(long)*LREC) < sizeof(long)*LREC)
         message(8, MB_OK, 2, 1);
       len = (int)fparam[0];
     }
     else
       len = lstrlen(TIntkeyApp->EnglishOr());
     if ((OrWord = (char *)malloc(len+1)) == NULL)
       InsuffMem();
     else
     {
       if (rpOrWord)
         _lread(cFile, OrWord, len);
       else
         lstrcpy(OrWord, TIntkeyApp->EnglishOr());
       OrWord[len] = '\0';
     }
  
     // get overlay fonts from chars file
     GetImageFonts(rpFont, buttonFont, defaultFont, featureFont);
  
     // get record pointers for Item Subheadings
     if (rpItemSubHead)
     {
       _llseek(hFile, (rpItemSubHead-1)*LREC*sizeof(long), SEEK_SET);
  
       if ((itemSubHead = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
         ReadDa((LPSTR)itemSubHead, nChar*sizeof(long), cFile, LREC, rpItemSubHead);
     }

     if (hFile == cFile)
     {
       // restore position in chars file to record number 2
       _llseek(cFile, LREC*sizeof(long), SEEK_SET);
       // reset position in items file to step over dummy header
       _llseek(tFile, 3*LREC*sizeof(long), SEEK_SET);
     }
  
     Ntrem = nItem;
     if (Stopbest == 0)
       Stopbest = nChar;
//     if (Autobest == 1)
//       Autobest = nItem;
//     SavedAutobest = Autobest;
  
     // allocate memory for storage of basic information
     // read numbers of states
     if ((nStat = (LPLONG)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
     else
     {
      ReadDa((LPSTR)nStat, nChar*sizeof(long), cFile, LREC, rpStat);
     }
  
     // read character types
     if ((tmp = (LPLONG)malloc(nChar*sizeof(long))) == NULL ||
         (cType = (LPSTR)malloc(nChar*sizeof(char))) == NULL)
       InsuffMem();
     else
     {
       ReadDa((LPSTR)tmp, nChar*sizeof(long), tFile, LREC, rpSpec);
       for (i = 0; i < nChar; ++i)
         cType[i] = tmp[i];
     }
  
     if (enableDeltaOutput)
     {
       // check validity of checksum
       long chk = 0;
       for (i = 0; i < nChar; ++i)
         chk += cType[i];
       if (enableDeltaOutput == chk)
         noDeltaOutput = 0;
       else
         noDeltaOutput = 1;
     }
     else
       noDeltaOutput = 1;

     recno = rpSpec + (nChar+LREC-1)/LREC;
     // read numbers of states from items file and check for compatability
     // (only compare multistates because if ICHARS and IITEMS are generated
     //  separately, numerics characters with units will differ)

     ReadDa((LPSTR)tmp, nChar*sizeof(long), tFile, LREC, recno);
     for (i = 0; i < nChar; ++i)
     if (abs(cType[i]) < 3 && nStat[i] != tmp[i])
        message(57, MB_OK, 2, 1);

     // used characters
     Used = tmp;
     SET_ARRAY(Used, nChar, 0L)
     // list of characters in the order in which they were used
     if ((UsedInOrder = (long *)malloc(nChar*sizeof(long))) == NULL)
       InsuffMem();
     else
       SET_ARRAY(UsedInOrder, nChar, 0L);
  
     // reliabilities
     recno += (nChar+LREC-1)/LREC;
     if ((charRel = (LPFLOAT)calloc(nChar, sizeof(float))) == NULL ||
     (srtCharRel = (LPFLOAT)calloc(nChar, sizeof(float))) == NULL ||
     (keyCharRel = (LPLONG)calloc(nChar, sizeof(float))) == NULL)
       InsuffMem();
     else
     {
       ReadDa((LPSTR)charRel, nChar*sizeof(float), tFile, LREC, recno);
       SortRel();
     }

  
     // table of item differences
     if ((Itmdif = (long *)calloc(nItem, sizeof(long))) == NULL)
       InsuffMem();
     for (long k = 0; k < nItem; ++k)
       Itmdif[k] = 0;

     if (rpMini)
     {
       // minima of integer characters
       if ((minC = (LPLONG)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
     ReadDa((LPSTR)minC, nChar*sizeof(long), tFile, LREC, rpMini);
       }

       // maxima of integer characters
       if ((maxC = (LPLONG)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         rpMini += (nChar + LREC - 1)/LREC;
     ReadDa((LPSTR)maxC, nChar*sizeof(long), tFile, LREC, rpMini);
       }
     }

     // character dependencies.
     if (lDep > nChar)
     {
       if ((cDep = (LPLONG)calloc(lDep, sizeof(long))) == NULL)
         InsuffMem();
       else
       {
     ReadDa((LPSTR)cDep,lDep*sizeof(long), tFile, LREC, rpCdep);
       }

       if ((invdep = (LPLONG)calloc(lInvdep, sizeof(long))) == NULL)
         InsuffMem();
       else
       {
     ReadDa((LPSTR)invdep, lInvdep*sizeof(long), tFile, LREC, rpInvdep);
 //  GlobalUnlock(hgblLplongInvdep);
       }
     }
  
     // record addresses for character data
     if ((chrDat = (LPDWORD)malloc(nChar*sizeof(DWORD))) == NULL)
       InsuffMem();
     else
     {
       ReadDa((LPSTR)chrDat, nChar*sizeof(long), tFile, LREC, rpCdat);
     }

     // characters for synonomy
     if (rpCsynon)
     {
       if ((chrSynonymy = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)chrSynonymy, nChar*sizeof(long), tFile, LREC, rpCsynon);
       }
     }

     // omit "or" for characters
     if (rpOmitOr)
     {
       if ((cOmitOr = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)cOmitOr, nChar*sizeof(long), tFile, LREC, rpOmitOr);
       }
     }

     // use controlling character first
     if (rpUseCc)
     {
       if ((useCcFirst = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)useCcFirst, nChar*sizeof(long), tFile, LREC, rpUseCc);
       }
     }

     // omit period for characters
     if (rpOmitPeriod)
     {
       if ((cOmitPeriod = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)cOmitPeriod, nChar*sizeof(long), tFile, LREC, rpOmitPeriod);
       }
     }

     // new paragraphs at characters
     if (rpNewPara)
     {
       if ((cNewPara = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)cNewPara, nChar*sizeof(long), tFile, LREC, rpNewPara);
       }
     }

     if (rpNonAutoCc)
     {
       if ((nonAutoCc = (long *)malloc(nChar*sizeof(long))) == NULL)
         InsuffMem();
       else
       {
         ReadDa((LPSTR)nonAutoCc, nChar*sizeof(long), tFile, LREC, rpNonAutoCc);
       }
     }


     // specimen description
     if ((SpecWrd = (LPLONG)malloc(nChar*sizeof(long))) == NULL ||
     (SpecBit = (LPINT)malloc(nChar*sizeof(int))) == NULL)
       InsuffMem();
  
     // initialize pointers for Specimen description
     ms1 = max(ms+1, maxint+3);
     mss = max(ms1, (int)lkstat);
     for (i=0,bit=0,wrd=0; i < nChar; ++i)
     {
       type = abs((int)cType[i]);
       if (type <= 3)
       {
     ns = (type < 3) ? nStat[i] : maxC[i] - minC[i] + 3;
     SpecWrd[i]= wrd;
     SpecBit[i] = bit;
     bit += (ns + 1);
     j = bit/NBINWD;
     wrd += j;
     bit -= (j*NBINWD);
       }
       else if (type == 4)
     ++nrealc;
     }
     if (bit)
       ++wrd;
     if (nrealc)
     {
       for (i = 0; i < nChar; ++i)
       {
     if (abs((int)cType[i]) == 4)
     {
       SpecWrd[i] = wrd;
       wrd += (2 * sizeof(float));
     }
       }
     }
     if (wrd)
     {
      if ((SpecData = (LPDWORD)malloc(wrd*sizeof(long))) == NULL)
        InsuffMem();
     }
     else
       SpecData = NULL;

     // single attribute
     len = (int)max((int)(ms1+NBINWD-1)/NBINWD, (int)((MAXCMDLINE)/sizeof(long)));
     LenAttrib = max(2, len);
     if ((Attrib = (LPDWORD)malloc(LenAttrib*sizeof(long))) == NULL)
       InsuffMem();

     // allocate space for storing state values
     if ((State1 = (LPBYTE)malloc(mss*sizeof(char))) == NULL ||
     (State2 = (LPBYTE)malloc(mss*sizeof(char))) == NULL ||
     (State3 = (LPBYTE)malloc(mss*sizeof(char))) == NULL)
       InsuffMem();

//     InitFileMapping();
     InitBtree(tptr, lbtree, dupItemPtr, nItem);
     InitKeywords(TIntkeyApp->RpKeywords(), &tptr, nItem, rpTnam);
  
     CharMask = new MASK(1, nChar);
     CharMask->set_default_mask(nChar);
     TaxonMask = new MASK(1, nItem);
     TaxonMask->set_default_mask(nItem);
     SavedCharMask = new MASK(1, nChar);
     SavedCharMask->copy_mask(CharMask, nChar);

     // Character image info has been shifted from items file to characters file.
     // However, to maintain compatability with older datasets, need to determine
     // in which file the information resides
     // 23-may-94.
     if (rpCimagesC != 0)
     {
       rpCimages = rpCimagesC;
       Datafile[CIMGDAT].hfile = Datafile[CHRDAT].hfile;
       Datafile[CIMGDAT].Ofstr = Datafile[CHRDAT].Ofstr;
       TIntkeyApp->SetFileTypeMap(CIMGDAT, 0);
       j = 1;
     }
     else if (rpCimagesI != 0)
     {
       rpCimages = rpCimagesI;
       Datafile[CIMGDAT].hfile = Datafile[ITMDAT].hfile;
       Datafile[CIMGDAT].Ofstr = Datafile[ITMDAT].Ofstr;
       TIntkeyApp->SetFileTypeMap(CIMGDAT, 1);
       j = 2;
     }
     else
     {
       rpCimages = 0;
       j = 0;
     }

//     if (rpTimages != 0)
//     {
//       Datafile[TIMGDAT].hfile = Datafile[CHRDAT].hfile;
//       Datafile[TIMGDAT].Ofstr = Datafile[CHRDAT].Ofstr;
//       FileTypeMap[TIMGDAT] = 0;
//     }
//     else
//     {
       Datafile[TIMGDAT].hfile = Datafile[ITMDAT].hfile;
       Datafile[TIMGDAT].Ofstr = Datafile[ITMDAT].Ofstr;
       TIntkeyApp->SetFileTypeMap(TIMGDAT, 1);
//     }

     // other image data (Startup, Character Keyword, Taxon Keyword) is in
     // characters file
     Datafile[OTHERIMGDAT].hfile = Datafile[CHRDAT].hfile;
     Datafile[OTHERIMGDAT].Ofstr = Datafile[CHRDAT].Ofstr;
     TIntkeyApp->SetFileTypeMap(OTHERIMGDAT, 0);

     // prepare image masks and character note masks
     InitImages(tptr, nChar, nItem, rpCimages, rpTimages);
     InitNotes(cptr, nChar, rpChlp);
//     InitMenus(cptr, tptr, nItem, nChar, rpTnam, rpCdes);
  
     secure_data_files(cptr, tptr, j);
     InitFileMapping();
     SetTabInfo(TabInfo);

*/
     _initDone = true;
     return;
 }

    //int best(TDialog *, long);

    
    /*---------------------------cc_process---------------------------------------*/
 // process controlling characters of a character to be USEd.
 // revised 24-nov-99.

  /*
   *  /*          
 long c,        // receives the character number
 long *list,    // receives and returns a list of control characters used
 char *useType, // receives and returns flags indicating whether the user (=2)
                // or Intkey (=1) used the character
 long *n        // receives and returns the number of control characters used
 ) 
   */
    

    /**
     * process controlling characters of a character to be USEd.
     * 
     * @param c receives the character number
     * @param list receives and returns a list of control characters used
     * @param useType receives and returns flags indicating whether the user (=2)
     * or Intkey (=1) used the character
     * @param n receives and returns the number of control characters used
     * @return success
     */
    public boolean cc_process(int c, int[] list, String useType, int[] n) {
          // If this is set, if USEing a control character causes a taxon to be
          // SEPARATEd to be no longer in contention.
          // In this case, we want abort any further processing.
    
          _ccProcessing = true;
          boolean status = cc_process1(c, list, useType, n);
          _ccProcessing = false;
          return status;
      }
          
  /*---------------------------cc_process1--------------------------------------*/
//process controlling characters of a character to be USEd.
//revised 24-nov-99.

  /*
   * 
   * long c,        // receives the character number
long *list,    // receives and returns a list of control characters used
char *useType, // receives and returns flags indicating whether the user (=2)
              // or Intkey (=1) used the character
long *n        // receives and returns the number of control characters used
   */

/**
 * 
 */
public boolean cc_process1(int c, int[] list, String useType, int[] n) 
{

   int i;
   boolean done = false; 
   boolean error = false; 
   boolean ok = true;

   if (true)//IsCharDependant(c))
   {
     i = c;
     do
     {
       if (!(error = use_cc(i, list, useType, n)))
       {
         _invdep[i-1] = Math.abs(_invdep[i-1]); // clear flag
         for (i = 1; i <= _nChar; ++i)   // look for more controlling characters
           if (_invdep[i-1] < 0)
             break;
         if (i > _nChar)
           done = true;
       }
     } while (!done && !error && !Params.isReUseLastChar());
     
     if (error || Params.isReUseLastChar())
     {
       ok = false;
       for (i = 0; i < _nChar; ++i)  // clear all flags
         _invdep[i] = Math.abs(_invdep[i]);
     }
   }
   return(ok);
}        


    
    
    /*
    int chkapp(long, long);
    int chkinapp(long c);
    unsigned long *depcmp(long c,unsigned long *attrib);
    bool HasMultiSetCc(long c);
    bool HasNonAutoCc(long c);
    bool IsCharControlling(c) {return(lDep > nChar && cDep[c-1] > 0);}
    bool IsCharDependant(c) {return(lInvdep > nChar && invdep[c-1] != 0);}
    bool IsMultiSetCc(long c, long cc);
    bool MultiSetCcAvail(long c);
    bool NonAutoCc() const {return nonAutoCc != NULL;}
    long num_cc(long c);
    void ResetSuitability(long c);
    int set_not_available(long c, unsigned long *attrib);
    void SortRel();
    bool UseCc(c) {return(useCcFirst != NULL && useCcFirst[c-1]);}
    bool UseCcFirst() const {return useCcFirst != NULL;}
    */
    
    public boolean use_cc(int c, int[] usedList, String useType, int[] nused) {
        throw new NotImplementedException();
    }
    
    /*
    int usech(long c, unsigned long *attrib, bool ccFirst=false);

    // descriptive functions
    char *cdesc(long c, unsigned char *state, long ndesc, int no_comments,
      int indent, int indentinc, int omitNum, int doOutput, int fmtType,
      bool bold=false);
    void char_desc(MASK *m);
    float *CharRel() {return charRel;}
    char *desc_used_char(long c);
    void desc_used_chars(long *clist, char *type, unsigned long *cout, bool ifDeleting);
    char *desint(long c, long t, unsigned char *state, int omitInapp, int cmdout,
      int indent, int indentinc, int omitNum, bool &newPara, char *&heading,
      bool doCaps=true, bool doStop=true, bool doOutput=true, bool bold=false);
    char *desmul(long c, long t, unsigned char *state, int omitInapp, int cmdout,
      int indent, int indentinc, int omitNum, bool &newPara, char *&heading,
      bool doCaps=true, bool doStop=true, bool doOutput=true, bool bold=false);
    char *desrel(long c, long t, unsigned char *state, int omitInapp, int cmdout,
      int indent, int indentinc, int omitNum, bool &newPara, char *&heading,
      bool doCaps=true, bool doStop=true, bool doOutput=true, bool bold=false);
    char *destxt(long c, long t, unsigned char *state, int omitInapp, int cmdout,
      int indent, int indentinc, int omitNum, bool &newPara, char *&heading,
      bool doCaps=true, bool doStop=true, bool doOutput=true, bool bold=false);
    void output_text_char(long c, long t, int indent);
    void taxa_desc(MASK *tmask, int no_comments, bool noTypsetMarks, bool highLight=false, int lineType=0);
    void taxnam(long tnum, int no_number, int no_comments, int ifdiff, int indent,
      int indentinc, bool insertRtf, bool delta=FALSE, bool noAngleBkts=true,
      bool noTypsetMarks=true, bool highLight=false, int lineType=0);
    char *units(long c);
 
    // accessor functions
    TOverlayFont *ButtonFont() const {return buttonFont;}
    long *Cdep() const {return cDep;}
    long Cdep(long i) const {return(cDep ?  cDep[i] : 0);}
    long ChrDat(long i) const {return chrDat[i];}
    long ChrSynonymy(long i) const {return(chrSynonymy ? chrSynonymy[i] : 0);}
    long COmitOr(long i) const {return(cOmitOr ?  cOmitOr[i] : 0);}
    long COmitPeriod(long i) const {return(cOmitPeriod ?  cOmitPeriod[i] : 0);}
    char Ctype(long i) const { return cType[i]; }
    float CharRel(long i) const { return charRel[i]; }
    FLAG ChineseFmt() const {return chineseFmt;}
    TOverlayFont *DefaultFont() const {return defaultFont;}
    TOverlayFont *FeatureFont() const {return featureFont;}
    char *Heading() const {return heading;}
    long Invdep(long i) const {return(invdep ?  invdep[i] : 0);}
    char *ItemSubHeading(long c);
    bool ItemSubHeadings() const {return (itemSubHead ? true : false);}
    long KeyCharRel(long i) const { return keyCharRel[i]; }
    long Lsbnd() const {return lSbnd;}
    long MaxC(long i) const { return(cType[i] == 3 ?  maxC[i] : 0); }
    long MinC(long i) const { return(cType[i] == 3 ?  minC[i] : 0); }
    int Mss() const {return mss;}
    int Ms1() const {return ms1;}
    */
    public int Nchar() {return _nChar;}
    
    /*
    bool NewParagraph(long c) const {return (cNewPara && cNewPara[c-1]) ? true : false;}
    */
    public int Nitem(){ return _nItem;}
    /*
    FLAG NoDeltaOutput() const {return noDeltaOutput;}
    long Nstat(long i) const { return nStat[i]; }
    void SetCharRel(float *rel) { memcpy(charRel, rel, nChar*sizeof(float));}
    void SetCharRel(long i, float rel) {charRel[i] = rel;}
    float SrtCharRel(long i) const { return srtCharRel[i]; }
    bool Synonymy() const {return (chrSynonymy != 0);}

    unsigned long RpCdes() const {return rpCdes;}
    unsigned long RpChlp() const { return rpChlp; }
    unsigned long Rpchlpfmt1() const { return rpChlpFmt1; }
    unsigned long RpChlpFmt2() const { return rpChlpFmt2; }
    unsigned long RpChlpGrp() const { return rpChlpGrp; }
    unsigned long RpCimages() const { return rpCimages; }
    unsigned long RpCKeyImages() const { return rpCKeyImages; }
    unsigned long RpMini() const { return rpMini; }
    unsigned long RpNkbd() const { return rpNkbd; }
    unsigned long RpStartupImages() const { return rpStartupImages; }
    unsigned long RpTimages() const { return rpTimages; }
    unsigned long RpTlinks(int index) const { return rpTlinks[index]; }
    unsigned long RpTKeyImages() const { return rpTKeyImages; }
    unsigned long RpTnam() const { return rpTnam; }
*/

    public int getRpCdes() {
        return _rpCdes;
    }

    public int getRpChlp() {
        return _rpChlp;
    }

    public int getRpChlpFmt1() {
        return _rpChlpFmt1;
    }

    public int getRpChlpFmt2() {
        return _rpChlpFmt2;
    }

    public int getRpChlpGrp() {
        return _rpChlpGrp;
    }

    public int getRpCimages() {
        return _rpCimages;
    }

    public int getRpCKeyImages() {
        return _rpCKeyImages;
    }

    public int getRpMini() {
        return _rpMini;
    }

    public int getRpNkbd() {
        return _rpNkbd;
    }

    public int getRpStartupImages() {
        return _rpStartupImages;
    }

    public int getRpTimages() {
        return _rpTimages;
    }

    public int getRpTKeyImages() {
        return _rpTKeyImages;
    }

    public int[] getRpTlinks() {
        return _rpTlinks;
    }

    public int getRpTnam() {
        return _rpTnam;
    }
    
    protected void clear() {
        
    }
    
    /*
 
 
  protected:
    void OutputCharDesc(long c, char *text, int indent, int indentinc,
         bool &newPara, char *&heading, bool doCaps, bool doStop, bool blankPrefix);*/
 
    //this doesn't seem to be used anywhere
    //enum {REDISPLAY=1, BEST, SEPARATE, DIAGNOSE};

    
    //TOverlayFont *buttonFont, *defaultFont, *featureFont;
 
    protected int _rpCdes;                // record containing record pointers
                                          // to char. descriptions 
    protected int _rpChlp;                // record pointer to char. help notes
    protected int _rpChlpFmt1;            // record pointer to char. notes format 1
    protected int _rpChlpFmt2;            // record pointer to char. notes format 2
    protected int _rpChlpGrp;             // record pointer to char. help notes groups
    protected int _rpCimages;             // record pointer to character images
    protected int _rpCKeyImages;          // record pointer to character keyword images
    protected int _rpMini;                // record pointer to integer char. min and max
    protected int _rpNkbd;                // record pointer to key state bounds
    protected int _rpStartupImages;       // record pointer to startup images
    protected int _rpTimages;             // record pointer to taxon images
    protected int _rpTKeyImages;          // record pointer to taxon keyowrd images
    protected int[] _rpTlinks = new int[2];           // record pointers to taxon links
    protected int _rpTnam;                // record pointer to taxon names

    protected int[] _chrDat;
    
    protected int[] _cDep;
    protected int[] _chrSynonymy;
    protected int[] _cNewPara;
    protected int[] _cOmitOr;
    protected int[] _cOmitPeriod;
    protected int[] _invdep;
    protected int[] _itemSubHead;
    protected int[] _keyCharRel;
    protected int[] _minC;
    protected int[] _maxC;
    protected int[] _nStat;
    protected int[] _useCcFirst;
    protected int[] _nonAutoCc;
    
    
    protected int _lDep, _lInvdep, _lSbnd, _nChar, _nItem;
    protected int _ms1, _mss;
    protected float[] _charRel, _srtCharRel;
    protected String _cType, _heading;
    protected boolean _chineseFmt, _noDeltaOutput;
    protected boolean _ccProcessing, _initDone;
}
