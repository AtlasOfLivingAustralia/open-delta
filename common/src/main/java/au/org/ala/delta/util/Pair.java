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
package au.org.ala.delta.util;

public class Pair<T, K> {

    private T _first;
    private K _second;

    public Pair(T first, K second) {
        _first = first;
        _second = second;
    }

    public T getFirst() {
        return _first;
    }

    public K getSecond() {
        return _second;
    }

    @Override
    public String toString() {
        return "Pair [_first=" + _first + ", _second=" + _second + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_first == null) ? 0 : _first.hashCode());
        result = prime * result + ((_second == null) ? 0 : _second.hashCode());
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
        Pair<?,?> other = (Pair<?,?>) obj;
        if (_first == null) {
            if (other._first != null)
                return false;
        } else if (!_first.equals(other._first))
            return false;
        if (_second == null) {
            if (other._second != null)
                return false;
        } else if (!_second.equals(other._second))
            return false;
        return true;
    }

}
