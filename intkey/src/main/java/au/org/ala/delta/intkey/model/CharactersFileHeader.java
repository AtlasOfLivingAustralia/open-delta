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
 * In memory representation of Intkey characters file header.
 * 
 * @author ChrisF
 * 
 */
public class CharactersFileHeader {

    /**
     * the number of characters
     */
    private int _nc;

    /**
     * the maximum length of any character description. (not used)
     */
    private int _maxDes;

    /**
     * the record number for the character description information.
     */
    private int rpCdes;

    /**
     * the record number for the numbers of character states.
     */
    private int rpStat;

    /**
     * the record number for the character notes.
     */
    private int rpChlp;

    /**
     * the record number for the character notes groups.
     */
    private int rpChlpGrp;

    /**
     * the record number for formatting information (TYPSET) for character notes
     * output in the main INTKEY window.
     */
    private int rpChlpFmt1;

    /**
     * the record number for formatting information (TYPSET) for character notes
     * output in a help window.
     */
    private int rpChlpFmt2;

    /**
     * the record number for character image data.
     */
    private int rpCImagesC;

    /**
     * the record number for startup image data.
     */
    private int rpStartupImages;

    /**
     * the record number for character keyword image data.
     */
    private int rpCKeyImages;

    /**
     * the record number for taxon keyword image data.
     */
    private int rpTKeyImages;

    /**
     * the record number for the heading (or dataset registration heading) text.
     */
    private int rpHeading;

    /**
     * the record number for the dataset registration subheading text.
     */
    private int rpRegSubHeading;

    /**
     * the record number for the dataset validation string.
     */
    private int rpValidationString;

    /**
     * the record number for the character mask used when creating the
     * characters file. (Not used)
     */
    private int rpCharacterMask;

    /**
     * the record number for the string to be used for "or".
     */
    private int rpOrWord;

    /**
     * the record number for the name of the characters file to be located on a
     * CD. (Not used)
     */
    private int rpCheckForCd;

    /**
     * the record number for font information for image overlays.
     */
    private int rpFont;

    /**
     * the record number for the item subheading information.
     */
    private int rpItemSubHead;

    /**
     * ???? Not used
     */
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
