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

public class CharactersFileHeader {

    private int _nc;
    private int _maxDes; //Not used
    private int rpCdes;
    private int rpStat;
    private int rpChlp;
    private int rpChlpGrp;
    private int rpChlpFmt1;
    private int rpChlpFmt2;
    private int rpCImagesC;
    private int rpStartupImages;
    private int rpCKeyImages;
    private int rpTKeyImages;
    private int rpHeading;
    private int rpRegSubHeading;
    private int rpValidationString;
    private int rpCharacterMask; //Not used
    private int rpOrWord;
    private int rpCheckForCd;
    private int rpFont;
    private int rpItemSubHead;
    
    private int _cptr;
    
    public int getNC() {
        return _nc;
    }

    public int getMaxDes() {
        return _maxDes;
    }

    public int getRpCdes() {
        return rpCdes;
    }

    public int getRpStat() {
        return rpStat;
    }

    public int getRpChlp() {
        return rpChlp;
    }

    public int getRpChlpGrp() {
        return rpChlpGrp;
    }

    public int getRpChlpFmt1() {
        return rpChlpFmt1;
    }

    public int getRpChlpFmt2() {
        return rpChlpFmt2;
    }

    public int getRpCImagesC() {
        return rpCImagesC;
    }

    public int getRpStartupImages() {
        return rpStartupImages;
    }

    public int getRpCKeyImages() {
        return rpCKeyImages;
    }

    public int getRpTKeyImages() {
        return rpTKeyImages;
    }

    public int getRpHeading() {
        return rpHeading;
    }

    public int getRpRegSubHeading() {
        return rpRegSubHeading;
    }

    public int getRpValidationString() {
        return rpValidationString;
    }

    public int getRpCharacterMask() {
        return rpCharacterMask;
    }

    public int getRpOrWord() {
        return rpOrWord;
    }

    public int getRpCheckForCd() {
        return rpCheckForCd;
    }

    public int getRpFont() {
        return rpFont;
    }

    public int getRpItemSubHead() {
        return rpItemSubHead;
    }

    public int getCptr() {
        return _cptr;
    }

    void setNC(int nc) {
        this._nc = nc;
    }

    void setRpCdes(int rpCdes) {
        this.rpCdes = rpCdes;
    }

    void setRpStat(int rpStat) {
        this.rpStat = rpStat;
    }

    void setRpChlp(int rpChlp) {
        this.rpChlp = rpChlp;
    }

    void setRpChlpGrp(int rpChlpGrp) {
        this.rpChlpGrp = rpChlpGrp;
    }

    void setRpChlpFmt1(int rpChlpFmt1) {
        this.rpChlpFmt1 = rpChlpFmt1;
    }

    void setRpChlpFmt2(int rpChlpFmt2) {
        this.rpChlpFmt2 = rpChlpFmt2;
    }

    void setRpCImagesC(int rpCImagesC) {
        this.rpCImagesC = rpCImagesC;
    }

    void setRpStartupImages(int rpStartupImages) {
        this.rpStartupImages = rpStartupImages;
    }

    void setRpCKeyImages(int rpCKeyImages) {
        this.rpCKeyImages = rpCKeyImages;
    }

    void setRpTKeyImages(int rpTKeyImages) {
        this.rpTKeyImages = rpTKeyImages;
    }

    void setRpHeading(int rpHeading) {
        this.rpHeading = rpHeading;
    }

    void setRpRegSubHeading(int rpRegSubHeading) {
        this.rpRegSubHeading = rpRegSubHeading;
    }

    void setRpValidationString(int rpValidationString) {
        this.rpValidationString = rpValidationString;
    }

    void setRpCharacterMask(int rpCharacterMask) {
        this.rpCharacterMask = rpCharacterMask;
    }

    void setRpOrWord(int rpOrWord) {
        this.rpOrWord = rpOrWord;
    }

    void setRpCheckForCd(int rpCheckForCd) {
        this.rpCheckForCd = rpCheckForCd;
    }

    void setRpFont(int rpFont) {
        this.rpFont = rpFont;
    }

    void setRpItemSubHead(int rpItemSubHead) {
        this.rpItemSubHead = rpItemSubHead;
    }

    void setCptr(int cptr) {
        this._cptr = cptr;
    }

}
