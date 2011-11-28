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
package au.org.ala.delta.model;

public class ImplicitValue {
	
	private Integer _t;
	private Integer _s;
	
	public ImplicitValue() {		
	}
	
	public void setCoded(Integer t) {
		_t = t;
	}
	
	public void setUncoded(Integer s) {
		_s = s;
	}
	
	public Integer getCoded() {
		return _t;		
	}
	
	public Integer getUncoded() {
		return _s;
	}
	
	@Override
	public String toString() {
		return String.format("s (uncoded)=%s, t (coded)=%s", _s, _t);
	}

}
