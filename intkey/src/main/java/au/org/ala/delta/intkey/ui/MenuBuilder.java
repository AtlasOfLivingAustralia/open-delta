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
package au.org.ala.delta.intkey.ui;

import java.util.Stack;

import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import au.org.ala.delta.directives.AbstractDirective;
import au.org.ala.delta.intkey.directives.OnOffArgument;
import au.org.ala.delta.intkey.directives.OnOffDirective;
import au.org.ala.delta.intkey.directives.invocation.OnOffDirectiveInvocation;
import au.org.ala.delta.intkey.model.IntkeyContext;

public class MenuBuilder {
    private JMenu _menu;
    private IntkeyContext _context;
    private Stack<JMenu> _menuStack;

    public MenuBuilder(String menuName, IntkeyContext context) {
        _menu = new JMenu();
        _menu.setName(menuName);
        _context = context;
        _menuStack = new Stack<JMenu>();
        _menuStack.push(_menu);
    }

    public void startSubMenu(String subMenuName, boolean enabled) {
        JMenu subMenu = new JMenu();
        subMenu.setName(subMenuName);
        subMenu.setEnabled(enabled);
        getCurrentMenu().add(subMenu);
        _menuStack.push(subMenu);
    }

    public void endSubMenu() {
        _menuStack.pop();
    }

    public void addPreconfiguredJMenu(JMenu mnu) {
        getCurrentMenu().add(mnu);
    }

    // TODO this is just to make disabled items - remove once all directives are
    // implemented.
    public void addMenuItem(String mnuItemName, boolean enabled) {
        JMenuItem mnuItem = new JMenuItem();
        mnuItem.setName(mnuItemName);
        mnuItem.setEnabled(enabled);
        getCurrentMenu().add(mnuItem);
    }

    public void addSeparator() {
        getCurrentMenu().addSeparator();
    }

    public void addActionMenuItem(Action action, boolean enabled) {
        JMenuItem mnuItem = new JMenuItem();
        mnuItem.setAction(action);
        mnuItem.setEnabled(enabled);
        getCurrentMenu().add(mnuItem);
    }

    public void addDirectiveMenuItem(String mnuItemName, AbstractDirective<IntkeyContext> directive, boolean enabled) {
        JMenuItem directiveMenuItem = new JMenuItem();
        directiveMenuItem.setName(mnuItemName);
        directiveMenuItem.setAction(new DirectiveAction(directive, _context));
        directiveMenuItem.setEnabled(enabled);
        getCurrentMenu().add(directiveMenuItem);
    }

    public void addOnOffDirectiveMenuItem(String mnuItemName, OnOffDirective directive, boolean value) {
        JMenuItem invocMenuItem = new JMenuItem();
        invocMenuItem.setName(mnuItemName);
        OnOffDirectiveInvocation invoc = directive.buildCommandObject();
        invoc.setValue(value);
        invoc.setStringRepresentation(directive.getControlWordsAsString() + " " + (value ? OnOffArgument.ON_VALUE : OnOffArgument.OFF_VALUE));
        invocMenuItem.setAction(new DirectiveInvocationAction(invoc, _context));
        getCurrentMenu().add(invocMenuItem);
    }

    public JMenu getMenu() {
        return _menu;
    }

    private JMenu getCurrentMenu() {
        return _menuStack.peek();
    }

}
