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
package au.org.ala.delta.intkey.model;

/**
 * In memory representation of Intkey items (taxa) file header
 * 
 * @author ChrisF
 * 
 */
public class ItemsFileHeader {

    /**
     * the number of items.
     */
    private int _nItem;

    /**
     * the number of characters
     */
    private int _nChar;

    /**
     * the maximum number of character states occuring in the data.
     */
    private int _ms;

    /**
     * the record length used in creating the file. Should be 32.
     */
    private int _lRec;

    /**
     * the record number for the taxon names.
     */
    private int _rpTnam;

    /**
     * the record number for the data specifications.
     */
    private int _rpSpec;

    /**
     * the record number for the minima and maxima of integer characters.
     */
    private int _rpMini;

    /**
     * the length of the dependency array.
     */
    private int _lDep;

    /**
     * the record number for the character dependency information.
     */
    private int _rpCdep;

    /**
     * length of the inverted dependency data.
     */
    private int _lInvdep;

    /**
     * the record number for the inverted dependency data.
     */
    private int _rpInvdep;

    /**
     * the record number for the taxon data for each character
     */
    private int _rpCdat;

    /**
     * total number of real numeric class boundary points.
     */
    private int _lSbnd;

    /**
     * maximum number of real numeric class boundary points for any one
     * character.
     */
    private int _lkstat;

    /**
     * major version number.
     */
    private int _majorVer;

    /**
     * record number for key state bounds for real numeric characters.
     */
    private int _rpNkbd;

    /**
     * maximum number of bits to store an integer value.
     */
    private int _maxInt;

    /**
     * minor version number.
     */
    private int _minorVer;

    /**
     * ??? Not used
     */
    private int _taxonImageChar;

    /**
     * the record number for the character images information.
     */
    private int _rpCimagesI;

    /**
     * the record number for the taxon images information.
     */
    private int _rpTimages;

    /**
     * a flag, which if non-zero, enables output of DELTA format data. (Not
     * used)
     */
    private int _enableDeltaOutput;

    /**
     * whether the text should be treated as Chinese. (Not used)
     */
    private int _chineseFmt;

    /**
     * the record number for synonymy information.
     */
    private int _rpCsynon;

    /**
     * the record number for characters where the word "or" should be omitted
     * between states.
     */
    private int _rpOmitOr;

    /**
     * the record number for the second parameter record.
     */
    private int _rpNext;

    private int _dupItemPtr;

    private int _tptr;

    private int _lbtree;

    /**
     * the record number for information about use of controlling characters.
     */
    private int _rpUseCc;

    /**
     * the taxon links information.
     */
    private int[] _rpTlinks = new int[2];

    /**
     * the record number for information about the omission of terminating
     * periods in descriptions.
     */
    private int _rpOmitPeriod;

    /**
     * the record number for information about the start positions of new
     * paragraphs in descriptions.
     */
    private int _rpNewPara;

    /**
     * the record number for information about ‘non-automatic’ controlling
     * characters
     */
    private int _rpNonAutoCc;

    public int getNItem() {
        return _nItem;
    }

    public int getNChar() {
        return _nChar;
    }

    public int getMs() {
        return _ms;
    }

    public int getLRec() {
        return _lRec;
    }

    public int getRpTnam() {
        return _rpTnam;
    }

    public int getRpSpec() {
        return _rpSpec;
    }

    public int getRpMini() {
        return _rpMini;
    }

    public int getLDep() {
        return _lDep;
    }

    public int getRpCdep() {
        return _rpCdep;
    }

    public int getRpInvdep() {
        return _rpInvdep;
    }

    public int getRpCdat() {
        return _rpCdat;
    }

    public int getLSbnd() {
        return _lSbnd;
    }

    public int getLkstat() {
        return _lkstat;
    }

    public int getMajorVer() {
        return _majorVer;
    }

    public int getRpNkbd() {
        return _rpNkbd;
    }

    public int getMaxInt() {
        return _maxInt;
    }

    public int getMinorVer() {
        return _minorVer;
    }

    public int getTaxonImageChar() {
        return _taxonImageChar;
    }

    public int getRpCimagesI() {
        return _rpCimagesI;
    }

    public int getRpTimages() {
        return _rpTimages;
    }

    public int getEnableDeltaOutput() {
        return _enableDeltaOutput;
    }

    public int getChineseFmt() {
        return _chineseFmt;
    }

    public int getRpCsynon() {
        return _rpCsynon;
    }

    public int getRpOmitOr() {
        return _rpOmitOr;
    }

    public int getRpNext() {
        return _rpNext;
    }

    public int getDupItemPtr() {
        return _dupItemPtr;
    }

    public int getTptr() {
        return _tptr;
    }

    public int getLbtree() {
        return _lbtree;
    }

    public int getRpUseCc() {
        return _rpUseCc;
    }

    public int[] getRpTlinks() {
        return _rpTlinks;
    }

    public int getRpOmitPeriod() {
        return _rpOmitPeriod;
    }

    public int getRpNewPara() {
        return _rpNewPara;
    }

    public int getRpNonAutoCc() {
        return _rpNonAutoCc;
    }

    void setNItem(int nItem) {
        this._nItem = nItem;
    }

    void setNChar(int nChar) {
        this._nChar = nChar;
    }

    void setMs(int ms) {
        this._ms = ms;
    }

    void setLRec(int lRec) {
        this._lRec = lRec;
    }

    void setRpTnam(int rpTnam) {
        this._rpTnam = rpTnam;
    }

    void setRpSpec(int rpSpec) {
        this._rpSpec = rpSpec;
    }

    void setRpMini(int rpMini) {
        this._rpMini = rpMini;
    }

    void setLDep(int lDep) {
        this._lDep = lDep;
    }

    void setRpCdep(int rpCdep) {
        this._rpCdep = rpCdep;
    }

    void setRpInvdep(int rpInvdep) {
        this._rpInvdep = rpInvdep;
    }

    void setRpCdat(int rpCdat) {
        this._rpCdat = rpCdat;
    }

    void setLSbnd(int lSbnd) {
        this._lSbnd = lSbnd;
    }

    void setLkstat(int lkstat) {
        this._lkstat = lkstat;
    }

    void setMajorVer(int majorVer) {
        this._majorVer = majorVer;
    }

    void setRpNkbd(int rpNkbd) {
        this._rpNkbd = rpNkbd;
    }

    void setMaxInt(int maxInt) {
        this._maxInt = maxInt;
    }

    void setMinorVer(int minorVer) {
        this._minorVer = minorVer;
    }

    void setTaxonImageChar(int taxonImageChar) {
        this._taxonImageChar = taxonImageChar;
    }

    void setRpCimagesI(int rpCimagesI) {
        this._rpCimagesI = rpCimagesI;
    }

    void setRpTimages(int rpTimages) {
        this._rpTimages = rpTimages;
    }

    void setEnableDeltaOutput(int enableDeltaOutput) {
        this._enableDeltaOutput = enableDeltaOutput;
    }

    void setChineseFmt(int chineseFmt) {
        this._chineseFmt = chineseFmt;
    }

    void setRpCsynon(int rpCsynon) {
        this._rpCsynon = rpCsynon;
    }

    void setRpOmitOr(int rpOmitOr) {
        this._rpOmitOr = rpOmitOr;
    }

    void setRpNext(int rpNext) {
        this._rpNext = rpNext;
    }

    void setDupItemPtr(int dupItemPtr) {
        this._dupItemPtr = dupItemPtr;
    }

    void setTptr(int Ttptr) {
        this._tptr = Ttptr;
    }

    void setLbtree(int lbtree) {
        this._lbtree = lbtree;
    }

    void setRpUseCc(int rpUseCc) {
        this._rpUseCc = rpUseCc;
    }

    void setRpTlinks(int[] rpTlinks) {
        this._rpTlinks = rpTlinks;
    }

    void setRpOmitPeriod(int rpOmitPeriod) {
        this._rpOmitPeriod = rpOmitPeriod;
    }

    void setRpNewPara(int rpNewPara) {
        this._rpNewPara = rpNewPara;
    }

    void setRpNonAutoCc(int rpNonAutoCc) {
        this._rpNonAutoCc = rpNonAutoCc;
    }

    public int getLinvdep() {
        return _lInvdep;
    }

    void setLinvdep(int lInvdep) {
        this._lInvdep = lInvdep;
    }

}
