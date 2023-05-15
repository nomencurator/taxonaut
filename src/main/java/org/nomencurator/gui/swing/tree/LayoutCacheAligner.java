/*
 * LayoutCacheAligner.java: an alignment adaptor to align nodes
 * in trees
 *
 * Copyright (c) 2003, 2014 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing.tree;

import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * <CODE>LayoutCacheAlinger</CODE> provides an alignment
 * adaptor for <CODE>AbstractLayoutCache</CODE>
 *
 * @version 	13 Sep. 2015
 * @author 	Nozomi `James' Ytow
 */
public class LayoutCacheAligner
    implements Aligner
{
    /**
     * <CODE>AbstractLayoutCache</CODE> to be used
     * geometory calcuration of an alinged node
     */
    protected AbstractLayoutCache aligner;

    /**
     * <CODE>NodeMapper</CODE> to map a node
     * to be alined to the node of aligner tree
     */
    protected NodeMapper mapper;

    /**
     * An instance of <CODE>BasicTreeUI</CODE> to be used 
     * to calcurate node position.  It may be unnecessary.
     */
    protected BasicTreeUI ui;

    /**
     * Constrcuts <CODE>LayoutCacheAligner</CODE> with
     * <CODE>ui</CODE> as aligning <CODE>TreeUI</CODE>
     *
     * @param ui <CODE>BasicTreeUI</CODE> to be used
     * to calcurate node position
     */
    public LayoutCacheAligner(BasicTreeUI ui)
    {
	this(ui, null);
    }

    /**
     * Constrcuts <CODE>LayoutCacheAligner</CODE> with
     * <CODE>ui</CODE> and <CODE>aligner</CODE> 
     *
     * @param ui <CODE>BasicTreeUI</CODE> to be used
     * to calcurate node position
     * @param aligner <CODE>AbstractLayoutChache</CODE> to be used
     * to calcurate node position
     */
    public LayoutCacheAligner(BasicTreeUI ui,
			      AbstractLayoutCache aligner)
    {
	this(ui, aligner, null);
    }

    /**
     * Constrcuts <CODE>LayoutCacheAligner</CODE> with
     * <CODE>ui</CODE>, <CODE>aligner</CODE> and
     * <CODE>mapper</CODE>
     *
     * @param ui <CODE>BasicTreeUI</CODE> to be used
     * to calcurate node position
     * @param aligner <CODE>AbstractLayoutChache</CODE> to be used
     * to calcurate node position
     * @param mapper <CODE>NodeMapper</CODE> to map nodes and paths
     */
    public LayoutCacheAligner(BasicTreeUI ui,
			      AbstractLayoutCache aligner,
			      NodeMapper mapper)
    {
	super();
	setUI(ui);
	setAligner(aligner);
	setMapper(mapper);
    }

    /**
     * Returns an instance of <CODE>BasicTreeUI</CODE>
     * used to align nodes, or null if unspecified
     *
     * @return <CODE>BasicTreeUI</CODE> used
     * to calcurate node position, or null if unspecified
     */
    public BasicTreeUI getUI()
    {
	return ui;
    }

    /**
     * Sets <CODE>ui</CODE> as aligner <CODE>TreeUI</CODE>.
     *
     * @param ui <CODE>BasicTreeUI</CODE> to be used
     * to calcurate node position
     */
    public synchronized void setUI(BasicTreeUI ui)
    {
	this.ui = ui;
    }

    /**
     * Returns <CODE>AbstractLayoutCache</CODE> used
     * to align nodes, or null if node alignment is
     * disabled
     *
     * @return <CODE>AbstractLayoutCache</CODE> used
     * to align nodes, or null if node alignment is
     * disabled
     */
    public AbstractLayoutCache getAligner()
    {
	return aligner;
    }

    /**
     * Sets <CODE>aligner</CODE> as the
     * <CODE>AbstractLayoutCache</CODE> to be used
     * to align nodes, or null to disable
     * node alignment
     *
     * @param aligner <CODE>AbstractLayoutCache</CODE> to be
     * used to align nodes, or null to disable node alignment
     */
    public synchronized void setAligner(AbstractLayoutCache aligner)
    {
	if(this.aligner == aligner)
	    return;

	this.aligner = aligner;
	if(aligner != null && mapper == null) {
	    TreeModel model = aligner.getModel();
	    if(model instanceof NodeMapper)
		setMapper((NodeMapper)model);
	}
    }

    /*
    public AbstractLayoutCache getLayoutCache()
    {
	return aligner;
    }
    */

    /**
     * Returns <CODE>NodeMapper</CODE> used to map
     * nodes between trees, or null if node mapping
     * is disabled
     *
     * @return <CODE>NodeMapper</CODE> used to map
     * nodes between trees, or null if node mapping
     * is disabled
     */
    public NodeMapper getMapper()
    {
	return mapper;
    }

    /**
     * Sets <CODE>mapper</CODE> as the <CODE>NodeMapper</CODE>
     * to be used to map nodes between trees, or null to disable
     * node mapping
     *
     * @param mapper <CODE>NodeMapper</CODE> to be used to map
     * nodes between trees, or null to disable node mapping
     */
    public synchronized void setMapper(NodeMapper mapper)
    {
	this.mapper = mapper;
    }

    /**
     * Returns a <CODE>Rectangle</CODE> representing the
     * bounds to draw a path in aligner tree corresponding
     * to <CODE>path</CODE> to be aligned in original tree.
     *
     * @param path    a <CODE>TreePath</CODE> to be aligned
     * @param placeIn a <CODE>Rectangle</CODE> in where to be drawn
     *
     * @return <CODE>Rectangle</CODE> replesenting space to be drawn
     *
     * @see AbstractLayoutCache#getBounds(TreePath, Rectangle)
     */
    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	if(path == null ||
	   aligner == null ||
	   mapper == null) {
	    return null;
	}

	DefaultMutableTreeNode node = 
	    (DefaultMutableTreeNode)mapper.getNodeFor
	    ((TreeNode)path.getLastPathComponent());
	if(node != null)
	    return aligner.getBounds
		(new TreePath(node.getPath()), 
		 placeIn);

	return aligner.getBounds(path, placeIn);
	//return null;
    }

    /**
     * Returns <CODE>TreePath</CODD> in <CODE>model</CODE>
     * closest to position given by <CODE>x</CODE> and </CODE>y
     *
     * @param x horizontal position
     * @param y vertical position
     * @param model to where the returned path belongs
     *
     * @return <CODE>TreePath</CODD> in <CODE>model</CODE>
     * closest to position given by <CODE>x</CODE> and </CODE>y.
     */
    public TreePath getPathClosestTo(int x, int y, TreeModel model)
    {
	if(aligner == null || mapper == null)
	    return null;

	TreePath path = 
	    aligner.getPathClosestTo(x, y);
	
	DefaultMutableTreeNode node = 
	    (DefaultMutableTreeNode)mapper.getNodeFor
	    ((TreeNode)path.getLastPathComponent(), model);
	
	if(node != null) {
	    return new TreePath(node.getPath());
	}

	return null;
    }

    public int getPreferredHeight()
    {
	if(aligner == null)
	    return 0;
	return aligner.getPreferredHeight();
    }

}
