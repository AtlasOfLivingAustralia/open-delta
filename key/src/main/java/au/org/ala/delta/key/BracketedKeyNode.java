package au.org.ala.delta.key;

import java.util.ArrayList;
import java.util.List;

import au.org.ala.delta.model.Item;
import au.org.ala.delta.model.MultiStateAttribute;

public class BracketedKeyNode {
    int _nodeNumber;
    int _backReference;

    private List<List<MultiStateAttribute>> _lineAttributes;
    private List<Object> _lineDestinations;

    public BracketedKeyNode(int nodeNumber) {
        _backReference = 0;
        _nodeNumber = nodeNumber;
        _lineAttributes = new ArrayList<List<MultiStateAttribute>>();
        _lineDestinations = new ArrayList<Object>();
    }

    public int getNodeNumber() {
        return _nodeNumber;
    }

    public int getNumberOfLines() {
        return _lineAttributes.size();
    }
    
    public int getBackReference() {
        return _backReference;
    }
    
    public void setBackReference(int backReference) {
        _backReference = backReference;
    }

    public List<MultiStateAttribute> getAttributesForLine(int lineNumber) {
        return _lineAttributes.get(lineNumber);
    }

    /**
     * Return the "destination" for a line in the node. If the line identifies
     * one or more taxa, this will be the list of taxa (List<Item>). Otherwise
     * an integer reference to another node is returned.
     * 
     * @param lineNumber
     *            The line number
     * @return A List<Item> if the line identifies one or more taxa. Otherwise
     *         an integer reference to another node.
     */
    public Object getDestinationForLine(int lineNumber) {
        return _lineDestinations.get(lineNumber);
    }

    public void addLine(List<MultiStateAttribute> attributes, int destinationNodeReference) {
        _lineAttributes.add(attributes);
        _lineDestinations.add(destinationNodeReference);
    }

    public void addLine(List<MultiStateAttribute> attributes, List<Item> identifiedTaxa) {
        _lineAttributes.add(attributes);
        _lineDestinations.add(identifiedTaxa);
    }

}
