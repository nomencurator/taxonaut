/*
 * FlippableVariableHeightLayoutCache.java: a javax.swing.tree.VariableHeightLayoutCache
 * with flipping capability
 *
 * Copyright (c) 2021, 2022 Nozomi `James' Ytow
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

import javax.swing.event.TreeModelEvent;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.VariableHeightLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.Flippable.Orientation;
import org.nomencurator.gui.swing.FlippableAdaptor;

/**
 * <CODE>FlippableVariableHeightLayoutCache</CODE> provides an
 * {@code Flippable} {@code VariableHeightLayoutCache}
 *
 * @version 	02 May 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippableVariableHeightLayoutCache
    extends VariableHeightLayoutCache
    implements FlippableLayoutCache, NodeDimensionsAccessor
{
    protected FlippableAdaptor flippable;

    protected VariableHeightLayoutCache layoutCache;

    public FlippableVariableHeightLayoutCache()
    {
	super();
	flippable = createFlippableAdaptor();
	layoutCache = createCache();
	
    }

    protected VariableHeightLayoutCache createCache()
    {
	return new VariableHeightLayoutCache();
    }

    protected FlippableAdaptor createFlippableAdaptor()
    {
	return new FlippableAdaptor(Flippable.VERTICAL);
    }

    /**
     * Returns flipping capablity of the instance.
     *
     * @return capable orientation
     */
    public Orientation getCapability()
    {
	if(flippable == null)
	    return null;
	return flippable.getCapability();
    }

    /**
     * Sets flipping capability of the instance to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from
     * the given parameter if the instance is not capable to cope with.
     *
     * @param orientation to flip
     * @return capable orientation of the instance
     */
    public Orientation setCapability(Orientation orientation)
    {
	return getCapability();
    }

    /**
     * Returns orientation of the instance.
     *
     * @return drawing orientation
     */
    public Orientation getOrientation()
    {
	if(flippable == null)
	    return null;
	return flippable.getOrientation();
    }

    /**
     * Sets orientation of the instance  to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from
     * given parameter if the instance doesnt accept it.
     *
     * @param orientation of drawing to set
     * @return orientation of the instance
     */
    public Orientation setOrientation(Orientation orientation)
    {
	if(flippable != null)
	    return flippable.setOrientation(orientation);
	else
	    return getOrientation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped()
    {
	if(flippable != null)
	    return flippable.isFlipped();
	return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped(Orientation orientation)
    {
	if(flippable != null)
	    return flippable.isFlipped(orientation);
	return false;
    }

    public TreePath getPathForRow(int row)
    {
	return super.getPathForRow(flipRow(row));
    }

    protected int flipRow(int row)
    {
	return LayoutCacheFlipper.flipRow(row, this);
    }

    public int getRowForPath(TreePath path)
    {
	return flipRow(super.getRowForPath(path));
    }

    protected int flipY(int y)
    {
	return LayoutCacheFlipper.flipY(y, this);
    }

    public TreePath getPathClosestTo(int x, int y)
    {
	return super.getPathClosestTo(x, flipY(y));
    }

    public Enumeration<TreePath> getVisiblePathsFrom(TreePath path)
    {
	if(!isFlipped(Flippable.VERTICAL))
	    return super.getVisiblePathsFrom(path);

	Vector<TreePath> paths = new Vector<TreePath>();
	int rowCount = getRowCount();
	int row = getRowForPath(path);
	for (int i = rowCount -1; i > row; i--){
	    paths.add(getPathForRow(i));
	}
	Enumeration<TreePath> pathEnum = super.getVisiblePathsFrom(path);
	while(pathEnum.hasMoreElements()) {
			paths.add(pathEnum.nextElement());
	}
	return LayoutCacheFlipper.flipEnumeration(paths.elements(), this);
    }

    protected Rectangle flipRectangle(Rectangle rect)
    {
	return LayoutCacheFlipper.flipRectangle(rect, this);
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	return flipRectangle(super.getBounds(path, placeIn));
    }

    public Rectangle _getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle placeIn)
    {
	return getNodeDimensions(value, row, depth, expanded, placeIn);
    }

}		


