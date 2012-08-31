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
package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BracketedKey implements Iterable<BracketedKeyNode> {

    private List<BracketedKeyNode> _nodes;

    public BracketedKey() {
        _nodes = new ArrayList<BracketedKeyNode>();
    }

    public int getNumberOfNodes() {
        return _nodes.size();
    }

    public void addNode(BracketedKeyNode node) {
        _nodes.add(node);
    }

    public BracketedKeyNode getNodeAt(int nodeNumber) {
        return _nodes.get(nodeNumber - 1);
    }

    @Override
    public Iterator<BracketedKeyNode> iterator() {
        return _nodes.iterator();
    }
}
