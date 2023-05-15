/*
 * DualTreeLayoutCache.java: a LayoutCache to mange DualTree
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

package org.nomencurator.gui.swing.tree;

import java.awt.Rectangle;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.plaf.TreeUI;

import javax.swing.event.TreeModelEvent;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.DualTree;
import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.plaf.FlippableTreeUI;


/**
 * {@code DualTreeLayoutCache} provieds a proxy to two {@code FlippableTree}s
 * via {@code DualTree} instance.
 *
 * @version 	17 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class DualTreeLayoutCache
    extends AbstractLayoutCache
{
    /**
     * {@code AbstractLayoutCache} for upper tree of a {@code DualTree} 
     */
    protected AbstractLayoutCache upperLayoutCache;

    /**
     * {@code AbstractLayoutCache} for lower tree of a {@code DualTree} 
     */
    protected AbstractLayoutCache lowerLayoutCache;

    protected int childIndent;

    protected DualTree dualTree;

    public void setChildIndent(int indent)
    {
	childIndent = indent;
    }
    
    public int getChildIndent()
    {
	return childIndent;
    }

    protected boolean antiparallel =false;

    public void setAntiparallel(boolean antiparallel)
    {
	this.antiparallel = antiparallel;
    }

    public boolean isAntiparallel()
    {
	return antiparallel;
    }

    protected boolean alignRootVerticalLeg =false;

    public void setAlignRootVerticalLeg(boolean align)
    {
	alignRootVerticalLeg =align;
    }

    public boolean isAlignRootVerticalLeg()
    {
	return alignRootVerticalLeg;
    }

    public DualTreeLayoutCache(DualTree dualTree)
    {
	super();
	this.dualTree = dualTree;
    }

    protected DualTree getDualTree()
    {
	return dualTree;
    }

    protected FlippableTree getUpperTree()
    {
	DualTree dTree = getDualTree();
	return (null == dTree)? null : dTree.getUpperTree();
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
    
    protected FlippableTreeUI getFlippableTreeUI(FlippableTree fTree)
    {
	return (null == fTree)? null : fTree.getFlippableTreeUI();
    }
    
    protected AbstractLayoutCache getLayoutCache(FlippableTreeUI ui)
    {
	return (null == ui)? null : ui.getLayoutCache();
    }


    protected AbstractLayoutCache getUpperLayoutCache()
    {
	return getLayoutCache(getFlippableTreeUI(getUpperTree()));
    }

    protected AbstractLayoutCache getLowerLayoutCache()
    {
	return getLayoutCache(getFlippableTreeUI(getLowerTree()));	
    }

    public boolean isExpanded(TreePath path)
    {
	return isExpanded(path, getLowerLayoutCache()) || isExpanded(path, getUpperLayoutCache());
    }

    protected boolean isExpanded(TreePath path, AbstractLayoutCache cache)
    {
	return (null == path || null == cache) ? false : cache.isExpanded(path);
    }
    
    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	Rectangle rect = getBounds(path, placeIn, getLowerLayoutCache());
	return (null != rect) ? rect : getBounds(path, placeIn, getUpperLayoutCache());
	    
    }

    protected Rectangle getBounds(TreePath path, Rectangle placeIn, AbstractLayoutCache cache)
    {
	return (null == path || null == cache) ? null :
	    cache.getBounds(path,  placeIn);
    }
    
    public TreePath getPathForRow(int row)
    {
	TreePath path = getPathForRow(row, getUpperLayoutCache());
	return (null != path) ? path :
	    getPathForRow(row - getRowCount(getUpperLayoutCache()), getLowerLayoutCache());
    }

    protected TreePath getPathForRow(int row, AbstractLayoutCache cache)
    {
	return (null == cache) ? null : cache.getPathForRow(row);
    }

    protected int getRowCount(AbstractLayoutCache cache)
    {
	return (null == cache) ? 0 : cache.getRowCount();
    }
    
    public int getRowForPath(TreePath path)
    {
	int row = getRowForPath(path, getUpperLayoutCache());
	return (-1 < row) ? row : getRowCount(getUpperLayoutCache()) + getRowForPath(path, getLowerLayoutCache());
    }

    protected int getRowForPath(TreePath path, AbstractLayoutCache cache)
    {
	return (null == path || null == cache) ? -1 : cache.getRowForPath(path);
    }

    protected int getBorderY()
    {
	AbstractLayoutCache cache = getUpperLayoutCache();
	int upperRows = getRowCount(cache);
	int y = 0;
	if (0 < upperRows) {
	    Rectangle rect = cache.getBounds(getPathForRow(upperRows - 1, cache), null);
	    y = (null == rect)? 0 : rect.y;
	}
	return y;
    }
    
    public TreePath getPathClosestTo(int x, int y)
    {
	int borderY = getBorderY();
	return (borderY < y)?
	    getPathClosestTo(x, y - borderY, getLowerLayoutCache()) :
	    getPathClosestTo(x, y, getUpperLayoutCache());
    }

    protected TreePath getPathClosestTo(int x, int y, AbstractLayoutCache cache)
    {
	return (null == cache) ? null : cache.getPathClosestTo(x, y);
    }

    public AbstractLayoutCache getCacheForPath(TreePath path)
    {
	if (path == null) return null;
	AbstractLayoutCache cache = getUpperLayoutCache();
	if (-1 < getRowForPath(path, cache)) return cache;
	cache = getLowerLayoutCache();
	return (0 > getRowForPath(path, cache)) ? null: cache;
    }

    public AbstractLayoutCache getCacheForRow(int row)
    {
	if (row < 0) return null;
	
	AbstractLayoutCache cache = getUpperLayoutCache();
	int upperRow = getRowCount(cache);
	if (row >= upperRow) cache = getLowerLayoutCache();
	return cache;
    }
    
    public Enumeration<TreePath> getVisiblePathsFrom(TreePath path)
    {
	if (null == path) return null;
	AbstractLayoutCache cache = getCacheForPath(path);
	if (null == cache) return null;
	AbstractLayoutCache upper = getUpperLayoutCache();
	AbstractLayoutCache lower = getLowerLayoutCache();
	Enumeration<TreePath> pathEnum = cache.getVisiblePathsFrom(path);
	if (cache == lower)
	    return pathEnum;
	Vector<TreePath> paths = new Vector<TreePath>();
	while(pathEnum.hasMoreElements())
	    paths.add(pathEnum.nextElement());
	pathEnum = lower.getVisiblePathsFrom(path);		      
	while(pathEnum.hasMoreElements())
	    paths.add(pathEnum.nextElement());
	return paths.elements();
    }
    
    public int getVisibleChildCount(TreePath path)
    {
	return getVisibleChildCount(path, getCacheForPath(path));
    }

    protected int getVisibleChildCount(TreePath path, AbstractLayoutCache cache)
    {
	return (null == cache) ? -1 : cache.getVisibleChildCount(path);
    }
    
    public void setExpandedState(TreePath path, boolean isExpanded)
    {
	AbstractLayoutCache cache = getCacheForPath(path);
	if (cache != null) cache.setExpandedState(path, isExpanded);
    }
    public boolean getExpandedState(TreePath path)
    {
	AbstractLayoutCache cache = getCacheForPath(path);
	return (null == cache)? false: cache.getExpandedState(path);
    }
    
    public int getRowCount()
    {
	return getRowCount(getUpperLayoutCache()) + getRowCount(getLowerLayoutCache());
    }
    
    public void invalidateSizes()
    {
	invalidateSizes(getUpperLayoutCache());
	invalidateSizes(getLowerLayoutCache());	
    }

    protected void invalidateSizes(AbstractLayoutCache cache)
    {
	if (null != cache) cache.invalidateSizes();
    }

    public void invalidatePathBounds(TreePath path)
    {
	if (null != path) {
	    AbstractLayoutCache cache = getCacheForPath(path);
	    if (null != cache)
		cache.invalidatePathBounds(path);
	}
    }

    public void treeNodesChanged(TreeModelEvent event)
    {
	treeNodesChanged(event, getUpperLayoutCache());
	treeNodesChanged(event, getLowerLayoutCache());
    }
    
    protected void treeNodesChanged(TreeModelEvent event, AbstractLayoutCache cache)
    {
	if (null != cache) cache.treeNodesChanged(event);
    }
    
    public void treeNodesInserted(TreeModelEvent event)
    {
	treeNodesInserted(event, getUpperLayoutCache());
	treeNodesInserted(event, getLowerLayoutCache());
    }
    
    protected void treeNodesInserted(TreeModelEvent event, AbstractLayoutCache cache)
    {
	if (null != cache) cache.treeNodesInserted(event);
    }
    
    public void treeNodesRemoved(TreeModelEvent event)
    {
	treeNodesRemoved(event, getUpperLayoutCache());
	treeNodesRemoved(event, getLowerLayoutCache());	
    }
    
    protected void treeNodesRemoved(TreeModelEvent event, AbstractLayoutCache cache)
    {
	if (null != cache) cache.treeNodesRemoved(event);
    }
    
    public void treeStructureChanged(TreeModelEvent event)
    {
	treeStructureChanged(event, getUpperLayoutCache());
	treeStructureChanged(event, getLowerLayoutCache());
    }
	
    protected void treeStructureChanged(TreeModelEvent event, AbstractLayoutCache cache)
    {
	if (null != cache) cache.treeStructureChanged(event);
    }
    
    public void setNodeDimensions(NodeDimensions nd)
    {
	super.setNodeDimensions(nd);
	setNodeDimensions(nd, getUpperLayoutCache());	
	setNodeDimensions(nd, getLowerLayoutCache());	
    }
    public void setNodeDimensions(NodeDimensions nd, AbstractLayoutCache cache)
    {
	if (null != cache) cache.setNodeDimensions(nd);	
    }

    public void setRootVisible(boolean rootVisible)
    {
	super.setRootVisible(rootVisible);
	if (null != getLowerLayoutCache()) getLowerLayoutCache().setRootVisible(rootVisible);
    }

    public void setRowHeight(int rowHeight)
    {
	super.setRowHeight(rowHeight);
	setRowHeight(rowHeight, getUpperLayoutCache());
	setRowHeight(rowHeight, getLowerLayoutCache());
    }

    protected void setRowHeight(int rowHeight, AbstractLayoutCache cache)
    {
	if (null != cache) cache.setRowHeight(rowHeight);
    }

    public int getPreferredHeight()
    {
	return getPreferredHeight(getUpperLayoutCache()) + getPreferredHeight(getLowerLayoutCache());
    }

    protected int getPreferredHeight(AbstractLayoutCache cache)
    {
	return (null == cache) ? 0 : cache.getPreferredHeight();
    }

    public int getPreferredWidth(Rectangle bounds)
    {
	if (0 == getRowCount(getLowerLayoutCache()))
	    return getPreferredWidth(bounds, getUpperLayoutCache());
	else if (0 == getRowCount(getUpperLayoutCache()))
	    return getPreferredWidth(bounds, getLowerLayoutCache());

	Rectangle lowerBounds = (bounds == null)? new Rectangle() : new Rectangle(bounds);
	lowerBounds.y += getUpperLayoutCache().getPreferredHeight();
	int upperWidth = getUpperLayoutCache().getPreferredWidth(bounds);
	int lowerWidth = getLowerLayoutCache().getPreferredWidth(lowerBounds);
	if (isAntiparallel() && isAlignRootVerticalLeg())
	    return upperWidth + lowerWidth - getChildIndent();
	else
	    return (upperWidth > lowerWidth) ? upperWidth : lowerWidth;
    }

    protected int getPreferredWidth(Rectangle bounds, AbstractLayoutCache cache)
    {
	return (null == cache) ? 0: cache.getPreferredWidth(bounds);
    }    

    protected Rectangle getNodeDimensions(Object value, int row, int depth,
                                          boolean expanded,
                                          Rectangle placeIn)
    {
	AbstractLayoutCache cache = getUpperLayoutCache();
	if (row >= cache.getRowCount()) cache = getLowerLayoutCache();
	return (null == cache || !(cache instanceof NodeDimensionsAccessor))? null :
	    ((NodeDimensionsAccessor)cache)._getNodeDimensions(value, row, depth, expanded,  placeIn);
    }
}
