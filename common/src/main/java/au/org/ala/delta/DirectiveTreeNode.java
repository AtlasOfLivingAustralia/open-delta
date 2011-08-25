package au.org.ala.delta;

import au.org.ala.delta.directives.AbstractDirective;

public @SuppressWarnings("rawtypes")
class DirectiveTreeNode extends TreeNodeList {
    private AbstractDirective _directive;

    public DirectiveTreeNode(TreeNode parent, String name, AbstractDirective directive) {
        super(parent, name);
        _directive = directive;
    }

    public AbstractDirective getDirective() {
        return _directive;
    }

    @Override
    public String toString() {
        return String.format("DirectiveNode: %s", _directive);
    }

    @Override
    public void dump(int indent) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < indent; ++i) {
            b.append("  ");
        }
        b.append(_name);
        System.out.printf("%s (%s)\n", b.toString(), _directive);
        // Logger.log("%s (%s)", b.toString(), _directive);
    }
}
