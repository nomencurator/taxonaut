/*
 * BasicAlignableTreeUI.java:  a TreeUI with node alignment between
 * trees
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.plaf.AlignableTreeUI;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.AlignableFixedHeightLayoutCache;
import org.nomencurator.gui.swing.tree.AlignableVariableHeightLayoutCache;


/**
 * <CODE>BasicAlignableTreeUI</CODE> provides a <code>TreeUI</code> capable node
 * alignemt between trees using <CODE>Alinger</CODE>
 *
 * @version 	4 Nov. 2014
 * @author 	Nozomi `James' Ytow
 */
public class BasicAlignableTreeUI
    extends BasicTreeUI
    implements Alignable, AlignableTreeUI
{
    public BasicAlignableTreeUI()
    {
	super();
    }

    protected AbstractLayoutCache createLayoutCache()
    {
	if(isLargeModel() && getRowHeight() > 0) {
	    return new AlignableFixedHeightLayoutCache(this);
	}

	return new AlignableVariableHeightLayoutCache(this);
    }

    public synchronized void setAligner(Aligner aligner)
    {
	((Alignable)treeState).setAligner(aligner);
    }

    public Aligner getAligner()
    {
	return ((Alignable)treeState).getAligner();
    }

    public static ComponentUI createUI(JComponent x)
    {
	return new BasicAlignableTreeUI();
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	return getAligner().getBounds(path, placeIn);
    }

    protected void installDefaults()
    {
	super.installDefaults();
	/*
	setExpandedIcon(null);
	setCollapsedIcon(null);
	*/
    }

    public boolean getShowsRootHandles()
    {
	return super.getShowsRootHandles();
    }

    public int getTotalChildIndent()
    {
	 return totalChildIndent;
    }

    public int getDepthOffset()
    {
	return depthOffset;
    }

    /*
    public Dimension getPreferredScrollableViewportSize()
    {
	if(treeState instanceof AlignableVariableHeightLayoutCache)
	    return ((AlignableVariableHeightLayoutCache)treeState).getPreferredScrollableViewportSize();

	return null;
    }
    */
}
