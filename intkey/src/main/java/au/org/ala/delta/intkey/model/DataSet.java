package au.org.ala.delta.intkey.model;

import org.apache.commons.lang.NotImplementedException;

import au.org.ala.delta.intkey.Params;

/**
 * 
 * @author Chris
 *
 */
public class DataSet {
    public DataSet() {
        _initDone = false;
        _noDeltaOutput = false;
    }
    
    
    //void Init(HFILE, HFILE, TDeltaReg *);

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
    long Nchar() const {return nChar;}
    bool NewParagraph(long c) const {return (cNewPara && cNewPara[c-1]) ? true : false;}
    long Nitem() const {return nItem;}
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
