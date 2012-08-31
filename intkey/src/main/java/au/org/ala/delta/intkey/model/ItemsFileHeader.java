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


public class ItemsFileHeader {
    
    private int _nItem;
    private int _nChar;
    private int _ms;
    private int _lRec;
    private int _rpTnam;
    private int _rpSpec;
    private int _rpMini;
    private int _lDep;
    private int _rpCdep;
    private int _lInvdep;
    private int _rpInvdep;
    private int _rpCdat;
    private int _lSbnd;
    private int _lkstat;
    private int _majorVer;
    private int _rpNkbd;
    private int _maxInt;
    private int _minorVer;
    private int _taxonImageChar;
    private int _rpCimagesI;
    private int _rpTimages;
    private int _enableDeltaOutput;
    private int _chineseFmt;
    private int _rpCsynon;
    private int _rpOmitOr;
    private int _rpNext;
    
    private int _dupItemPtr;
    private int _tptr;
    private int _lbtree;
    
    private int _rpUseCc;
    private int[] _rpTlinks = new int[2];
    private int _rpOmitPeriod;
    private int _rpNewPara;
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
