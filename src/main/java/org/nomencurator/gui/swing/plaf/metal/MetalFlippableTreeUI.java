/*
 * MetalFlippableTreeUI.java:  a TreeUI with node alignment between
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

package org.nomencurator.gui.swing.plaf.metal;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.UIManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.metal.MetalTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.Flippable.Orientation;

import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.plaf.FlippableTreeUI;
import org.nomencurator.gui.swing.plaf.FlippableTreeUIUtilities;

import org.nomencurator.gui.swing.tree.FlippableFixedHeightLayoutCache;
//import org.nomencurator.gui.swing.tree.FlippableLayoutCache;
import org.nomencurator.gui.swing.tree.FlippableVariableHeightLayoutCache;


/**
 * <CODE>MetalFlippableTreeUI</CODE> provides a {@code Flippable} <code>TreeUI</code>
 *
 * @version 	08 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class MetalFlippableTreeUI
    extends MetalTreeUI
    implements FlippableTreeUI
{
    protected Flippable flipper = null;

    public static ComponentUI createUI(JComponent c)
    {
	return new MetalFlippableTreeUI();
    }

    protected Orientation orientation;

    /**
     * Constructor
     */
    public MetalFlippableTreeUI()
    {
	super();
	setCapability(Flippable.VERTICAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractLayoutCache createLayoutCache()
    {
	AbstractLayoutCache layoutCache;

        if(isLargeModel() && getRowHeight() > 0) {
            layoutCache = new FlippableFixedHeightLayoutCache();
        }
	else {
	    layoutCache = new FlippableVariableHeightLayoutCache();
	}
	if (layoutCache instanceof Flippable)
	    flipper = (Flippable) layoutCache;

	return layoutCache;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isShowsRootVerticalLineLeg()
    {
	return FlippableTreeUIUtilities.isShowsRootVerticalLineLeg(tree);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractLayoutCache getLayoutCache()
    {
	return treeState;
    }

    /**
     * {@inheritDoc}
     */
    public Flippable getFlipper()
    {
	return FlippableTreeUIUtilities.getFlipper(this);
    }

    /**
     * {@inheritDoc}
     */
    public Orientation getCapability()
    {
	if(flipper != null)
	    return flipper.getCapability();
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public Orientation setCapability(Orientation orientation)
    {
	if(flipper != null)
	    return flipper.setCapability(orientation);
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public Orientation getOrientation()
    {
	if(flipper != null)
	    return flipper.getOrientation();
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public Orientation setOrientation(Orientation orientation)
    {
	this.orientation = orientation;
	if(flipper != null) {
	    return flipper.setOrientation(orientation);
	}
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFlipped()
    {
	if(flipper != null)
	    return flipper.isFlipped();
	return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isFlipped(Orientation orientation)
    {
	if(flipper != null)
	    return flipper.isFlipped(orientation);
	return false;
    }

    /**
     * {@inheritDoc}
     */
    public Icon getUnmirroredCollapsedIcon()
    {
	return super.getCollapsedIcon();
    }

    /**
     * {@inheritDoc}
     */
    public Icon getCollapsedIcon()
    {
	return FlippableTreeUIUtilities.getCollapsedIcon(this);
    }

    /**
     * {@inheritDoc}
     */
    public Icon getUnmirroredExpandedIcon()
    {
	return super.getExpandedIcon();
    }

    /**
     * {@inheritDoc}
     */
    public Icon getExpandedIcon()
    {
	return FlippableTreeUIUtilities.getExpandedIcon(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path)
    {
	FlippableTreeUIUtilities.paintVerticalPartOfLeg(g, clipBounds, insets, path, tree, this);
    }

    /**
     * {@inheritDoc}
     */
    public TreePath _getLastChildPath(TreePath parent)
    {
	return getLastChildPath( parent);
    }

    /**
     * {@inheritDoc}
     */
    public Color _getHashColor()
    {
	return getHashColor();
    }

    /**
     * {@inheritDoc}
     */
    public TreeModel _getModel()
    {
	return getModel();
    }

    /**
     * {@inheritDoc}
     */
    public int _getRowX(int depth)
    {
	return getRowX(-1, depth);
    }

    /**
     * {@inheritDoc}
     */
    public boolean _getShowsRootHandles()
    {
	return getShowsRootHandles();
    }

    /**
     * {@inheritDoc}
     */
    public int _getVerticalLegBuffer()
    {
	return getVerticalLegBuffer();
    }

    /**
     * {@inheritDoc}
     */
    public int _getHorizontalLegBuffer()
    {
	return getHorizontalLegBuffer();
    }

    /**
     * {@inheritDoc}
     */
    public boolean _isRootVisible()
    {
	return isRootVisible();
    }

    
    /**
     * {@inheritDoc}
     */
    public void _paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom)
    {
	paintVerticalLine(g, c, x, top, bottom);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDrawVerticalLines()
    {
	return UIManager.getBoolean("Tree.paintLines");
    }

    /**
     * {@inheritDoc}
     */
    public void setDrawVerticalLines(boolean toDraw)
    {
    }

    /**
     * {@inheritDoc}
     */
    public JTree getTree()
    {
	return tree;
    }

    /**
     * {@inheritDoc}
     */
    public FlippableTree getFlippableTree()
    {
	return (tree instanceof FlippableTree)? (FlippableTree)getTree() : null;
    }
    
    /**
     * {@inheritDoc}
     */
    /*
    public void setOffsetX(int offset)
    {
	if (getOffsetX() == offset ||
	    treeState == null || !(treeState instanceof  FlippableLayoutCache))
	    return;

	((FlippableLayoutCache)treeState).setOffsetX(offset);
	updateLayoutCacheExpandedNodes();
    }
    */
    /**
     * {@inheritDoc}
     *
     /*
    public int getOffsetX()
    {
	return (treeState != null && treeState instanceof  FlippableLayoutCache) ?
	    ((FlippableLayoutCache)treeState).getOffsetX() : 0;
    }
    */

    public int getVerticalPartOfLegX(int depth)
    {
	return FlippableTreeUIUtilities.getVerticalPartOfArmX(depth+1, tree.getInsets(), tree, this);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void updateCachedPreferredSize()
    {
	super.updateCachedPreferredSize();
	validCachedPreferredSize = false;
	//preferredSize.width += getOffsetX();
	validCachedPreferredSize = true;	
    }

    /**
     * {@inheritDoc}
     */
/*
    protected int getRowX(int row, int depth)
    {
	return getOffsetX() + super.getRowX(row, depth);
    }
*/

    /**
     * {@inheritDoc}
     */
    /*
    public TreePath getClosestPathForLocation(JTree tree, int x, int y)
    {
	return super.getClosestPathForLocation(tree, x - getOffsetX(), y);
    }
    */

    /**
     * {@inheritDoc}
     */
    public Rectangle getPathBounds(JTree tree, TreePath path)
    {
	return FlippableTreeUIUtilities.getPathBounds(super.getPathBounds(tree, path), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
   protected TreeSelectionListener createTreeSelectionListener()
    {
	return this;
    }

    /**
     * {@inheritDoc}
     */
    public void valueChanged(TreeSelectionEvent event)
    {
	super.createTreeSelectionListener().valueChanged(event);
	if (isFlipped(VERTICAL))
	    tree.repaint();
    }

}
