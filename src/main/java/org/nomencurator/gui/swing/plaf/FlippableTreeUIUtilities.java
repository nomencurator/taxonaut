/*
 * FlippableTreeUIUtilities.java:  a TreeUI with node alignment between
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

import java.awt.ComponentOrientation;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import java.util.Hashtable;
import java.util.HashMap;

import javax.swing.Icon;
import javax.swing.JTree;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.Flippable.Orientation;
import org.nomencurator.gui.swing.FlippableTree;
import org.nomencurator.gui.swing.FlippedIcon;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.plaf.FlippableTreeUI;

/**
 * <CODE>FlippableTreeUIUtilities</CODE> provides utility methods to implement {@code FlippableTreeUI} 
 *
 * @version 	25 May. 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippableTreeUIUtilities
{
    /**
     * Mapping from original {@code Icon} to its flipped version
     */
    protected static HashMap<Orientation, HashMap<Icon, Icon>> iconMap;

    /**
     * Mapping from original {@code Icon} to its flipped version
     */
    protected static Hashtable<Icon, Icon> vFlippedIcons;

    /**
     * Mapping from original {@code Icon} to its flipped version
     */
    protected static Hashtable<Icon, Icon> hFlippedIcons;

    /**
`>     * Mapping from original {@code Icon} to its flipped version
     */
    protected static Hashtable<Icon, Icon> hvFlippedIcons;
    

    /**
     * The constructor.
     */
    protected FlippableTreeUIUtilities()
    {
    }

    /**
     * Returns the {@code Flipper} of given {@code ui}
     *
     * @param ui {@code FlippableTreeUI} to handle
     * @return Flipper of the instance, of null if unavailable
     */
    public static Flippable getFlipper(FlippableTreeUI ui)
    {
	AbstractLayoutCache layoutCache = ui.getLayoutCache();
	if(layoutCache instanceof Flippable)
	    return (Flippable)layoutCache;
	return null;
    }

    /**
     * Returns {@code Icon} used to inidicate expanded state handle of given {@code ui}.
     * It is flipped if the {@code ui}. is fipped.
     *
     * @param ui where returned {@code Icon} to use.
     *.@
     */
    public static Icon getExpandedIcon(FlippableTreeUI ui)
    {
	Icon icon = ui.getUnmirroredExpandedIcon();
	if (ui.isFlipped())
	    icon = getFlippedIcon(icon, ui.getOrientation());
	return icon;
    }

    /**
     * Returns {@code Icon} used to inidicate expanded state handle of given {@code ui}.
     * It is flipped if the {@code ui}. is fipped.
     *
     * @param ui where returned {@code Icon} to use.
     *.@
     */
    public static Icon getCollapsedIcon(FlippableTreeUI ui)
    {
	Icon icon = ui.getUnmirroredCollapsedIcon();
	if (ui.isFlipped(Orientation.HORIZONTAL))
	    icon = getFlippedIcon(icon, ui.getOrientation());
	else if (!(ui.getTree().getComponentOrientation().isLeftToRight()))
	    icon = getFlippedIcon(icon, Orientation.HORIZONTAL);
	return icon;
    }

    /**
     * Returns an flipped {@code Icon} according to given {@code orientation}.
     * If {@cdoe orientation} is {@code NONE}, returns the original {@code Icon}.
     *
     * @param icon to flip
     * @param orientation to flip
     * @return flipped {@code Icon}
     */
    public static Icon getFlippedIcon(Icon icon, Orientation orientation)
    {
	if (iconMap == null)
	    iconMap = new HashMap<Orientation, HashMap<Icon, Icon>>();
	HashMap<Icon, Icon> icons = iconMap.get(orientation);
	if (icons == null) {
	    icons =new HashMap<Icon, Icon>();
	    iconMap.put(orientation, icons);
	}

	Icon flipped = icon;
	if(icons != null) {
	    flipped = icons.get(icon);
	    if (flipped == null) {
		flipped = new FlippedIcon(icon, orientation);
		icons.put(icon, flipped);
	    }
	}
	return flipped;
    }

    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @param tree to test
     * @return true if it shows the part of the line
     */
    public static boolean isShowsRootVerticalLineLeg(JTree tree)
    {
	return (tree != null && tree instanceof FlippableTree)? ((FlippableTree)tree).getShowsRootVerticalLineLeg() : false;
    }

    /**
     * Returns x positoin of the "arm" connecting nodes having {@code depth} to its parent node, 
     * includeing {@code insets}.   Note that it is alos x position of "leg" from nodes to
     * its child nodes  having  {@code depth}.
     *
     * @param depth of nodes return x location for
     * @param instes of compoent to draw the arm
     * @param tree to draw the "arm" 
     * @param ui UI proxy of the {@code tree}
     * @return amount to indent nodes of the given depth
     */
    public static int getVerticalPartOfArmX(int depth, FlippableTree tree)
    {
	return getVerticalPartOfArmX(depth, tree.getInsets(), tree, tree.getFlippableTreeUI());
    }
    
    /**
     * Returns x positoin of the "arm" connecting nodes having {@code depth} to its parent node, 
     * includeing {@code insets}.   Note that it is alos x position of "leg" from nodes to
     * its child nodes  having  {@code depth}.
     *
     * @param depth of nodes return x location for
     * @param instes of compoent to draw the arm
     * @param tree to draw the "arm" 
     * @param ui UI proxy of the {@code tree}
     * @return amount to indent nodes of the given depth
     */
    public static int  getVerticalPartOfArmX(int depth, Insets insets, JTree tree, FlippableTreeUI ui)
    {
	int x = ui._getRowX(depth);
	if (tree.getComponentOrientation().isLeftToRight()) {
	    x = insets.left + x - ui.getRightChildIndent();
	}
	else {
	    x = tree.getWidth() - (insets.right + x) + ui.getRightChildIndent();
	}
	return x;
    }

        /**
     * Vertical line drawing helper supportig flippapable {@code ui}.
     *
     * @param g a graphics context
     * @param clipBounds a clipped rectangle
     * @param insets insets
     * @param path a tree path
     * @param tree the JTree to draw the line
     * @param ui to paint
     */
    public static void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds,
					      Insets insets, TreePath path, JTree tree, FlippableTreeUI ui)
    {
	if (g == null || clipBounds == null || insets == null || path == null || tree == null || ui == null)
	    return;

	Object o = path.getLastPathComponent();
	if (o instanceof TreeNode && ((TreeNode)o).isLeaf())
	    return;

	if (!tree.isVisible(path) || !tree.isExpanded(path))
	    return;
	
	int childDepth = path.getPathCount();

	if (childDepth == 1 && !tree.isRootVisible() && !tree.getShowsRootHandles())
	    return;

	int x = getVerticalPartOfArmX(childDepth, insets, tree, ui);

	if (x < clipBounds.x || x > clipBounds.x + clipBounds.width) return;

	int northEnd = clipBounds.y;
	int southEnd = northEnd + clipBounds.height;

	Rectangle higherBounds = ui.getPathBounds(tree, path);
	Rectangle lowerBounds = ui.getPathBounds(tree, ui._getLastChildPath(path));

	int north = northEnd;;
	int south = southEnd;
	
	int verticalGap = ui._getVerticalLegBuffer();

	boolean flipped = ui.isFlipped(Flippable.VERTICAL);

	if (flipped)
	    north = Math.max(northEnd, lowerBounds.y + lowerBounds.height /2);	    
	else
	    south = Math.min(southEnd, lowerBounds.y + lowerBounds.height /2);

	if (flipped) {
	    if (higherBounds == null)
		south = Math.min(southEnd, southEnd - (insets.bottom +verticalGap));		
	    else
		south = Math.min(southEnd, higherBounds.y - verticalGap);		
	} else {
	    if (higherBounds == null)
		north = Math.max(northEnd, insets.top + verticalGap);
	    else
		north = Math.max(northEnd, higherBounds.y + higherBounds.height+ verticalGap);
	}

	if (childDepth ==1 && !tree.isRootVisible() && tree.getModel().getChildCount(o)> 0) {
	    higherBounds = ui.getPathBounds(tree, path.pathByAddingChild(tree.getModel().getChild(o,0)));
	    if (higherBounds != null) {
		if (flipped)
		    south = Math.min(southEnd - (insets.bottom - verticalGap),
				     higherBounds.y + (ui.isShowsRootVerticalLineLeg() ? higherBounds.height : higherBounds.height/2));
		else
		    north = Math.max(insets.top + verticalGap,
				     higherBounds.y + (ui.isShowsRootVerticalLineLeg() ? 0 : higherBounds.height/2));
	    }
	}
	
	if (north <= south || flipped) {
	    g.setColor(ui._getHashColor());
	    if (north <= south)
		ui._paintVerticalLine(g, tree, x, north, south);
	    else
		ui._paintVerticalLine(g, tree, x, south, north);		
	}
	
    }

    public static Rectangle getPathBounds(Rectangle bounds, FlippableTreeUI ui)
    {
	/*
	if (bounds != null)
	    bounds.x += ui.getRightChildIndent();
	*/
	return bounds;
    }
}

