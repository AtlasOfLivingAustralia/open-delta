package au.org.ala.delta;

import java.util.HashMap;
import java.util.Map;

public class TreeNodeList extends TreeNode {

    private Map<String, TreeNode> _children;

    public TreeNodeList(TreeNode parent, String name) {
        super(parent, name);
        _children = new HashMap<String, TreeNode>();
    }

    public Map<String, TreeNode> getChildren() {
        return _children;
    }

    public void addChild(TreeNode child) {
        _children.put(child.getName(), child);
    }

    public String toString() {
        return String.format("TreeNodeList: %s", _name);
    }
    
    @Override
    public void visit(Tree.TreeVisitor visitor) {
    	visitor.visit(this);
    	for (TreeNode n : _children.values()) {
    		n.visit(visitor);
    	}
    }

    public void dump(int indent) {
        super.dump(indent);
        for (TreeNode n : _children.values()) {
            n.dump(indent + 1);
        }
    }

}
