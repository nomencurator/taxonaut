/*
 * BasicAlignerTreeUI.java:  a TreeUI with node alignment between
 * trees
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.LayoutCacheAligner;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.NodeMapper;

/**
 * <CODE>BasicAlignerTreeUI</CODE> provides a <code>TreeUI</code> capable to align
 * nodes between trees
 *
 * @version 	09 Apr. 2016
 * @author 	Nozomi `James' Ytow
 */
public class BasicAlignerTreeUI
    extends BasicTreeUI
    implements Alignable, Aligner
{
    protected Aligner aligner;
    
    public BasicAlignerTreeUI()
    {
	super();
	aligner = 
	    createAligner();
    }

    /*
    public AbstractLayoutCache getLayoutCache()
    {
	return treeState;
    }
    */

    public void setMapper(NodeMapper mapper)
    {
	/*
	if(aligner == null)
	    aligner = createAligner();
	*/
	aligner.setMapper(mapper);
    }

    public NodeMapper getMapper()
    {
	if(aligner == null)
	    return null;

	return aligner.getMapper();
    }

    protected LayoutCacheAligner createAligner()
    {
	return new LayoutCacheAligner(this, treeState);
    }

    public void setAligner(Aligner aligner) { ; }

    public Aligner getAligner()
    {
	return aligner;
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	return getAligner().getBounds(path, placeIn);
    }

    public static ComponentUI createUI(JComponent x)
    {
	return new BasicAlignerTreeUI();
    }

    protected void installDefaults()
    {
	super.installDefaults();
	/*
	setExpandedIcon(null);
	setCollapsedIcon(null);
	*/
    }
}
