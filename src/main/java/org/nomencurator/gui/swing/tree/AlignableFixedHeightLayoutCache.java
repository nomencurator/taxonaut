/*
 * AlignableFixedHeightLayoutCache.java: a javax.swing.tree.FixedHeightLayoutCache
 * with node alignment
 *
 * Copyright (c) 2003, 2005, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.awt.Rectangle;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.FixedHeightLayoutCache;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.tree.NameTreeNode;

/**
 * <CODE>AlignableFixedHeightLayoutCache</CODE> provides a 
 * <CODE>FixedHeightLayoutCache</CODE> with node alignment
 *
 * @version 	18 Sep. 2016
 * @author 	Nozomi `James' Ytow
 */
public class AlignableFixedHeightLayoutCache
    extends FixedHeightLayoutCache
    implements Alignable
{
    protected Aligner aligner;

    protected BasicTreeUI ui;

    public AlignableFixedHeightLayoutCache(BasicTreeUI ui)
    {
	this(ui, null);
    }

    public AlignableFixedHeightLayoutCache(BasicTreeUI ui,
					   LayoutCacheAligner aligner)
    {
	super();
	setUI(ui);
	setAligner(aligner);
    }

    public synchronized void setUI(BasicTreeUI ui)
    {
	this.ui = ui;
    }

    public BasicTreeUI getUI()
    {
	return ui;
    }

    public synchronized void setAligner(Aligner aligner)
    {
	this.aligner = aligner;
    }

    public Aligner getAligner()
    {
	return aligner;
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	Rectangle r = 
	    super.getBounds(path, placeIn);

	Rectangle aligned = null;

	if(aligner != null)  {
	    aligned = aligner.getBounds(path, placeIn);
	}
	    
	if(aligned == null) {
	    return r;
	}

	return aligned;
    }
}
