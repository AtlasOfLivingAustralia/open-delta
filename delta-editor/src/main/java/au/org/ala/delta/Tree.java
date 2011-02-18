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
package au.org.ala.delta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.org.ala.delta.directives.Directive;
import au.org.ala.delta.directives.DirectiveSearchResult;
import au.org.ala.delta.directives.DirectiveSearchResult.ResultType;

public class Tree {

	private TreeNodeList _toplevel;
	
	public Tree() {
		_toplevel = new TreeNodeList(null, "ROOT");
	}
	
	public void addDirective(Directive directive) {
		TreeNodeList p = _toplevel;
		
		String[] words = directive.getControlWords();
		String key = words[0];
		for (int i = 0; i < words.length - 1; ++i) {
			
			key = makeKey(words[i]);
			
			if (p.getChildren().containsKey(key)) {
				TreeNode n = p.getChildren().get(key);
				if (n instanceof DirectiveTreeNode) {
					throw new RuntimeException("Directive tree already contains directive: " + directive.toString());
				} else {
					p = (TreeNodeList) n;
				}				
			} else {
				TreeNodeList newChild = new TreeNodeList(p, key);
				p.addChild(newChild);
				p = newChild;
			}
		}
		p.addChild(new DirectiveTreeNode(p, makeKey(words[words.length-1]), directive));
	}
	
	public DirectiveSearchResult findDirective(List<String> controlWords) {
		TreeNodeList p = _toplevel;
		for (String cw : controlWords) {
			String key = makeKey(cw);
			if (p.getChildren().containsKey(key)) {
				TreeNode n = p.getChildren().get(key);
				if (n instanceof DirectiveTreeNode) {
					return new DirectiveSearchResult(ResultType.Found, ((DirectiveTreeNode) n).getDirective());
				} else {
					p = (TreeNodeList) n;
				}
			} else {
				return new DirectiveSearchResult(ResultType.NotFound, null);
			}
		}
		return new DirectiveSearchResult(ResultType.MoreSpecificityRequired, null);
	}
	
	private String makeKey(String str) {
		if (str == null) {
			return null;
		}
		str = str.trim().toLowerCase();
		if (str.length() > 3) {
			str = str.substring(0,3);
		}
		return str;	
	}
	
	public void dump() {
		_toplevel.dump(0);		
	}

}

class TreeNode {
	
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
		Logger.log("%s", b.toString());
	}
	
}

class DirectiveTreeNode extends TreeNode {
	private Directive _directive;
	
	public DirectiveTreeNode(TreeNode parent, String name, Directive directive) {
		super(parent, name);
		_directive = directive;
	}
	
	public Directive getDirective() {
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
		Logger.log("%s (%s)", b.toString(), _directive);
	}
}

class TreeNodeList extends TreeNode {
	
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
	
	public void dump(int indent) {
		super.dump(indent);		
		for (TreeNode n : _children.values()) {
			n.dump(indent + 1);
		}
		
	}

}
