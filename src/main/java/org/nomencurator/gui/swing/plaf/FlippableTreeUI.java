/*
 * FlippableTreeUI.java:  a TreeUI with node alignment between
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
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.plaf.FlippableTreeUI;

/**
 * {@code FlippableTreeUI} provides an inteface definition of  {@code Flippable} <code>TreeUI</code>
 *
 * @version 	12 May. 2022
 * @author 	Nozomi `James' Ytow
 */
public interface FlippableTreeUI
    extends Flippable, TreeSelectionListener
{
    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @return true if it shows the part of the line
     */
    public boolean isShowsRootVerticalLineLeg();

    /**
     * Returns the LayoutCache of the instance.
     *
     * @return AbstractLayoutCach in use
     */
    public AbstractLayoutCache getLayoutCache();

    /**
     * Returns the {@code Flippable} of the instance or null if the is none. 
     *
     * @return Flippable of the instance
     */
    public Flippable getFlipper();

    /**
     * Returns {@code Icon} used to indicate collapsed stete handle dpending on 
     * either flipped state or {@code ComponentOrientation}.
     *
     * @return {@code Icon}  used to indicate collapsed stete handle dpending on flipped state
     */
    public Icon getCollapsedIcon();

    /**
     * Returns {@code Icon} used to indicate collapsed stete handle
     * in either upflipped state or {@code ComponentOrientation.LEFT_TO_RIGHT}.
     *
     * @return {@code Icon}  used to indicate collapsed stete handle unflipped stete.
     */
    public Icon getUnmirroredCollapsedIcon();

    /**
     * Returns {@code Icon} used to indicate expanded stete handle dpending on flipped state.
     *
     * @return {@code Icon}  used to indicate expanded stete handle dpending on flipped state
     */
    public Icon getExpandedIcon();

    /**
     * Returns {@code Icon} used to indicate expanded stete handle in unflipped stete.
     *
     * @return {@code Icon}  used to indicate expanded stete handle unflipped stete.
     */
    public Icon getUnmirroredExpandedIcon();


    /**
     * Proxy of {@code BasicTreeUI#getPathBounds}.
     *
     * @return the right child indent
     */
    public Rectangle getPathBounds(JTree tree, TreePath path);

    /**
     * Proxy of {@code BasicTreeUI#getRightChildIndent}.
     *
     * @return the right child indent
     */
    public int getRightChildIndent();

    /**
     * Proxy of {@code BasicTreeUI#getLeftChildIndent}.
     *
     * @return the left child indent
     */
    public int getLeftChildIndent();

    /**
     * "Back door" to protected method {@code BasicTreeUI#getHashColor}.
     *
     * @return the hash color
     */
    public Color _getHashColor();

    /**
     * "Back door" to protected method {@code BasicTreeUI#getLastChildPath(TreePath)}
     *
     * @param parent a tree path
     * @return a path to the last child of {@code parent}
     */
    public TreePath _getLastChildPath(TreePath parent);

    /**
     * "Back door" to protected method {@code BasicTreeUI#getModel()}
     *
     * @return the tree model
     */
    public TreeModel _getModel();

    /**
     * "Back door" to protected method {@code BasicTreeUI#getRowX(int, int)}.
     *
     * @param depth of nodes return x location for
     * @return amount to indent nodes of the given depth
     */
    public int _getRowX(int depth);

    /**
     * Returns x positoin of the "arm" connecting nodes having {@code depth} to its parent node, 
     * includeing {@code insets}.   Note that it is alos x position of "leg" from nodes to
     * its child nodes  having  {@code depth}.
     *
     * @param depth of nodes return x location for
     * @param instes of compoent to draw the arm
     * @return amount to indent nodes of the given depth
     */
    //public int getVerticalPatOfArmX(int depth, Insets insets);

    /**
     * "Back door" to protected method {@code BasicTreeUI#getShowsRootHandles}.
     *
     * @return {@code true} if the root handles are to be displayed
     */
    public boolean _getShowsRootHandles();

    /**
     * "Back door" to protected method {@code BasicTreeUI#getVerticalLegBuffer()}
     *
     * @return the vertical leg buffer
     */
    public int _getVerticalLegBuffer();

    /**
     * "Back door" to protected method {@code BasicTreeUI#getHorizontalLegBuffer()}
     *
     * @return the horizontal leg buffer
     */
    //    public int _getHorizontalLegBuffer();

    /**
     * "Back door" to protected method {@code BasicTreeUI#isRootVisible}
     *
     * @return {@code true} if the root node of the tree is displayed
     */
    public boolean _isRootVisible();

    /**
     * "Back door" to protected method {@code BasicTreeUI#paintVerticalLine}
     *
     * @param g a graphics context
     * @param c a component
     * @param x an X coordinate
     * @param top an Y1 coordinate
     * @param bottom an Y2 coordinate
     */
    public void _paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom);

    /**
     * Retuns true to draw vertical lines
     *
     * @return {@code true} to draw vertical lines
     */
    public boolean isDrawVerticalLines();

    /**
     * Sets to draw vertical lines
     *
     * @param toDraw {@code true} to draw vertical lines
     */
    public void setDrawVerticalLines(boolean toDraw);

    public int getVerticalPartOfLegX(int depth);

    public JTree getTree();

    public FlippableTree getFlippableTree();    
}

