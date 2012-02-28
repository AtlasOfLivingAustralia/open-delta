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
