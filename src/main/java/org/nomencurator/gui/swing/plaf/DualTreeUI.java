/*
 * DualTreeUI.java:  a TreeUI with node alignment between
 * trees
 *
 * Copyright (c) 2022 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.gui.swing.plaf;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.TreeUI;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.DualTree;
import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.tree.DualTreeLayoutCache;
/**
 * <CODE>DualTreeUI</CODE> provides an inteface definition of  {@code Flippable} <code>TreeUI</code>
 *
 * @version 	03 May. 2022
 * @author 	Nozomi `James' Ytow
 */
public class DualTreeUI
    extends BasicTreeUI
{
    /**
     * {@inheritDoc}
     */
    public static DualTreeUI createUI(JComponent c)
    {
        return new DualTreeUI();
    }
    protected DualTree dualTree;

    protected DualTreeUI()
    {
	super();
	System.out.println("DualTreeUI()");
    }

    public void installUI(JComponent c)
    {
	System.out.println("installUI(" + c + ")");

	if (!(c instanceof DualTree)) {
	    dualTree = null;
	    return;
	}
	dualTree = (DualTree)c;
	super.installUI(c);
    }

    protected void completeUIInstall()
    {
	System.out.println("copmleteUIinstall(): " + tree.getModel());
	super.completeUIInstall();
    }
    

    protected DualTree getDualTree()
    {
	return dualTree;
    }

    /*
    public void paint(Graphics g, JComponent c)
    {
	DualTree target = getDualTree();
	if (target == null)
	    return;
	FlippableTree tree = target.getUpperTree();
	TreeUI treeUI = getTreeUI(tree);
	Dimension d = null;
	if (treeUI != null) {
	    d = treeUI.getPreferredSize(tree);
	    treeUI.paint(g, c);
	    g.translate(0, d.height);
	}
	treeUI = getTreeUI(target.getLowerTree());
	if (treeUI != null) {
	    treeUI.paint(g, c);
	    if (d != null)
		g.translate(0, -d.height);		
	}
    }
    */

    protected AbstractLayoutCache createLayoutCache()
    {
	return new DualTreeLayoutCache(getDualTree());
    }

    protected DualTreeLayoutCache getDualTreeLayoutCache()
    {
	return (null == treeState || !(treeState instanceof DualTreeLayoutCache)) ? null :
	    (DualTreeLayoutCache)treeState;
    }

    protected FlippableTree getUpperTree()
    {
	DualTree dTree = getDualTree();
	return (null == dTree) ? null : dTree.getUpperTree();
    }
    
    protected FlippableTree getLowerTree()
    {
	DualTree dTree = getDualTree();
	return (null == dTree)? null : dTree.getLowerTree();
    }
    
    protected TreeUI getTreeUI(FlippableTree fTree)
    {
	return (null == fTree)? null : fTree.getUI();
    }
    
    protected TreeUI getUpperTreeUI()
    {
	return getTreeUI(getUpperTree());
    }
    
    protected TreeUI getLowerTreeUI()
    {
	return getTreeUI(getLowerTree());
    }
    
    protected BasicTreeUI getBasicTreeUI(FlippableTree fTree)
    {
	return (null == fTree)? null : fTree.getBasicTreeUI();
    }
    
    protected BasicTreeUI getUpperBasicTreeUI()
    {
	return getBasicTreeUI(getUpperTree());
    }
    
    protected BasicTreeUI getLowerBasicTreeUI()
    {
	return getBasicTreeUI(getLowerTree());
    }
    
    protected FlippableTreeUI getFlippableTreeUI(FlippableTree fTree)
    {
	return (null == fTree)? null : fTree.getFlippableTreeUI();
    }

    protected FlippableTreeUI getUpperFlippableTreeUI()
    {
	return getFlippableTreeUI(getUpperTree());
    }

    protected FlippableTreeUI getLowerFlippableTreeUI()
    {
	return getFlippableTreeUI(getLowerTree());
    }

    protected AbstractLayoutCache getLayoutCache(FlippableTreeUI ui)
    {
	return (null == ui)? null : ui.getLayoutCache();
    }

    protected AbstractLayoutCache getUpperLayoutCache()
    {
	return getLayoutCache(getUpperFlippableTreeUI());
    }

    protected AbstractLayoutCache getLowerLayoutCache()
    {
	return getLayoutCache(getLowerFlippableTreeUI());
    }

    public int getAntiparallelOffset()
    {
	FlippableTreeUI upper = getUpperFlippableTreeUI();
	FlippableTreeUI lower = getLowerFlippableTreeUI();
	return (null == upper || null == lower)? 0 :
	    (upper.getLeftChildIndent() + lower.getLeftChildIndent());
    }

    public Rectangle getPathBounds(JTree tree, TreePath path)
    {
	Rectangle bounds = null;
	FlippableTreeUI ui = getLowerFlippableTreeUI();
	if (null != ui) bounds = ui.getPathBounds(getLowerTree(), path);
	if (null == bounds) {
	    ui = getUpperFlippableTreeUI();
	    if (null != ui)  bounds = ui.getPathBounds(getUpperTree(), path);
	}
	return bounds;
    }

    public boolean isEditing(JTree tree)
    {
	TreeUI upper = getUpperTreeUI();
	TreeUI lower = getLowerTreeUI();
	return ((null == upper) ? false : upper.isEditing(getUpperTree())) ||
	    ((null == upper) ? false : lower.isEditing(getLowerTree()));
	
    }

    public  boolean stopEditing(JTree tree)
    {
	boolean result = true;
	TreeUI ui = getUpperTreeUI();
	if (null != ui)
	    result &= ui.stopEditing(getUpperTree());
	ui = getLowerTreeUI();
	if (null != ui)
	    result &= ui.stopEditing(getLowerTree());
	return result;
    }
    
    public  void cancelEditing(JTree tree)
    {
	TreeUI ui = getUpperTreeUI();
	if (null != ui) ui.cancelEditing(getUpperTree());
	ui = getLowerTreeUI();
	if (null != ui) ui.cancelEditing(getLowerTree());
    }
    
    public  void startEditingAtPath(JTree tree, TreePath path)
    {
	DualTreeLayoutCache dCache = getDualTreeLayoutCache();
	if (null == dCache) return;
	AbstractLayoutCache cache = dCache.getCacheForPath(path);
	if (cache == null) return;
	if (cache == getLowerLayoutCache())
	    getLowerTreeUI().startEditingAtPath(getLowerTree(), path);
	else if (cache == getUpperLayoutCache())
	    getUpperTreeUI().startEditingAtPath(getUpperTree(), path);
    }

    public  TreePath getEditingPath(JTree tree)
    {
	TreeUI ui = getLowerTreeUI();
	TreePath path = (null == ui)? null : ui.getEditingPath(getLowerTree());
	if (null == path) {
	    ui = getUpperTreeUI();
	    path = (null == ui)? null : ui.getEditingPath(getUpperTree());
	}
	return path;
    }

    public void paint(Graphics g, JComponent c) {}
}
