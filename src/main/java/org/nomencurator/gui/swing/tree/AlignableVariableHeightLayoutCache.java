/*
 * AlignableVariableHeightLayoutCache.java: a javax.swing.tree.VariableHeightLayoutCache
 * with node alignment
 *
 * Copyright (c) 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Enumeration;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.VariableHeightLayoutCache;

import org.nomencurator.gui.swing.tree.NameTreeNode;

import org.nomencurator.gui.swing.plaf.AlignableTreeUI;

/**
 * {@code AlignableVariableHeightLayoutCache} provides a 
 * {@code VariableHeightLayoutCache} with node alignment
 *
 * @version 	29 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class AlignableVariableHeightLayoutCache
    extends VariableHeightLayoutCache
    implements Alignable
{
    protected Aligner aligner;

    protected BasicTreeUI ui;

    protected AlignableTreeUI alignable;

    protected boolean enabled;

    public AlignableVariableHeightLayoutCache(BasicTreeUI ui)
    {
	this(ui, null);
    }

    public AlignableVariableHeightLayoutCache(BasicTreeUI ui,
					      LayoutCacheAligner aligner)
    {
	super();
	setUI(ui);
	setAligner(aligner);
	setAlignable(true);
    }

    public synchronized void setUI(BasicTreeUI ui)
    {
	this.ui = ui;
	if(ui instanceof AlignableTreeUI)
	    alignable = (AlignableTreeUI)ui;
	else
	    alignable = null;
    }

    public BasicTreeUI getUI()
    {
	return ui;
    }

    public synchronized void setAligner(Aligner aligner)
    {
	if(this.aligner == aligner)
	    return;
	this.aligner = aligner;
    }

    public Aligner getAligner()
    {
	return aligner;
    }

    public void setAlignable(boolean alignable)
    {
	enabled = alignable;
    }

    public boolean isAlignable()
    {
	return (enabled && (aligner != null));
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	if(path != null) {
	    Object leaf = path.getLastPathComponent();
	}

	Rectangle rectIn = null;
	if(placeIn != null)
	    rectIn = (Rectangle)placeIn.clone();
	Rectangle rect = super.getBounds(path, rectIn);

	/*
	if(path != null) {
	    Object o = path.getLastPathComponent();
	    if(o instanceof NameTreeNode) {
		NameTreeNode node = 
		    (NameTreeNode)((TreeNode)o).getParent();
		if(node != null) {
		    String literal = node.getLiteral();
		    if(literal == null ||
		       literal.length() == 0) {
			Rectangle rIn = null;
			if(placeIn != null)
			    rIn = (Rectangle)placeIn.clone();
			Rectangle p = 
			    super.getBounds(path.getParentPath(), rIn);
			rect.y -= p.height; 
		    }
		}
	    }
	}
	*/

	if(!isAlignable()) {
	    return rect;
	}

	Rectangle alignedIn = null;
	if(placeIn != null)
	    alignedIn = (Rectangle)placeIn.clone();
	Rectangle aligned = null;
	/*
	aligned = aligner.getBounds(path, alignedIn);

	if(aligned != null &&
	   aligned.y > rect.y)
	    rect.y = aligned.y;

	return rect;
	*/

	if(path == null) {
	    aligned = aligner.getBounds(path, alignedIn);
	    if(aligned != null &&
	       aligned.y > rect.y)
		rect.y = aligned.y;
	    //return rect;
	    if(placeIn == null)
		placeIn = new Rectangle();
	    return placeIn;
	}

	/*
	TreePath parentPath = path;
	TreePath p = path;
	while(p != null) {
	    parentPath = p;
	    p = p.getParentPath();
	}
	*/
	//TreePath parentPath = getRootPath(path);
	TreePath parentPath = getPathForRow(0);

	Enumeration<TreePath> e = getVisiblePathsFrom(parentPath);


	DefaultMutableTreeNode node =
	    (DefaultMutableTreeNode)path.getLastPathComponent();


	if(e == null) {
	    aligned = aligner.getBounds(path, alignedIn);
	    if(aligned != null && rect != null &&
	       aligned.y > rect.y)
		rect.y = aligned.y;

	    return rect;
	}

	int y = 0;
	int rectY = 0;
	int alignY = 0;
	int lastAlignY = 0;

	do {
	    parentPath = e.nextElement();
	    rect = super.getBounds(parentPath, rectIn);
	    aligned = aligner.getBounds(parentPath, alignedIn);
	    if(aligned == null) {
		if(alignY < 0) {
		}
		else {
		    lastAlignY = alignY;
		}
		y += rect.y - rectY;
		alignY = -1;
	    }
	    else {
		if(alignY < 0) {
		    y += rect.y - rectY;
		    //y += aligned.y - lastAlignY;
		}
		else {
		    y += aligned.y - alignY;
		}
		alignY = aligned.y;
	    }

	    if(y < rect.y)
		y = rect.y;

	    rectY = rect.y;

	} while(e.hasMoreElements() && 
		/*!node.equals(parentPath.getLastPathComponent())*/
		!path.equals(parentPath)
		);

	rect.y = y;

	return rect;
    }

    protected TreePath getRootPath(TreePath path)
    {
	if(path == null)
	    return null;

	TreePath parentPath = path;
	TreePath p = path.getParentPath();
	while(p != null) {
	    parentPath = p;
	    p = p.getParentPath();
	}

	return parentPath;
    }

    public TreePath getPathClosestTo(int x, int y)
    {
	if(aligner == null)
	    return super.getPathClosestTo(x, y);

	TreePath path = getPathForRow(0);

	Enumeration<TreePath> e = getVisiblePathsFrom(path);
	if(e == null)
	    return null;

	int boundsY = 0;
	int rectY = 0;
	int lastY = 0;
	int lastH = 0;
	int alignY = 0;
	int lastAlignY = 0;
	Rectangle rectIn = new Rectangle();
	Rectangle alignedIn = new Rectangle();
	Rectangle rect = null;
	Rectangle aligned = null;

	TreePath previousPath = null;
	do {
	    previousPath = path;
	    path = e.nextElement();
	    lastH = boundsY - lastY;
	    lastY = boundsY;
	    rect = super.getBounds(path, rectIn);
	    aligned = aligner.getBounds(path, alignedIn);
	    if(aligned == null) {
		if(alignY < 0) {
		}
		else {
		    lastAlignY = alignY;
		}
		boundsY += rect.y - rectY;
		alignY = -1;
	    }
	    else {
		if(alignY < 0) {
		    boundsY += rect.y - rectY;
		    //boundsY += aligned.y - lastAlignY;
		}
		else {
		    boundsY += aligned.y - alignY;
		}
		alignY = aligned.y;
	    }

	    if(boundsY < rect.y)
		boundsY = rect.y;

	    rectY = rect.y;

	    if(y < boundsY && y > lastH + lastY) {
		if((y - (lastH + lastY)) < (boundsY - y))
		    return previousPath;
		else 
		    return path;
	    }

	} while((y < lastY || y >= boundsY) &&
		e.hasMoreElements());

	if(y >= lastY && y < boundsY)
	    return previousPath;

	return path;

    }

    public int getPreferredHeight()
    {
	int height = super.getPreferredHeight();
	if(!isAlignable())
	    return height;

	int rowCount = getRowCount();

	if(rowCount > 0) {
	    Rectangle bounds = 
		getBounds(getPathForRow(rowCount - 1),
			  null);

	    if(bounds != null)
		bounds.y += bounds.height;

	    if(height < bounds.y)
		return bounds.y;

	    return height;
	}
	return 0;
    }

}

