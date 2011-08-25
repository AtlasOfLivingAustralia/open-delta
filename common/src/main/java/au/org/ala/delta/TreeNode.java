package au.org.ala.delta;

public class TreeNode {

    protected String _name;
    protected TreeNode _parent;

    protected TreeNode(TreeNode parent, String name) {
        _parent = parent;
        _name = name;
    }

    public TreeNode getParent() {
        return _parent;
    }

    public String getName() {
        return _name;
    }
    
    public void dump(int indent) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            b.append("  ");
        }
        b.append(_name);
        System.out.printf("%s\n", b.toString());
        // Logger.log("%s", b.toString());
    }
    
    public void visit(Tree.TreeVisitor visitor) {
    	if (visitor != null) {
    		visitor.visit(this);
    	}
    }

}
