/*
 * LayoutCacheFlipper.java: flipps a LayoutCache
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.Flippable;



/**
 * <CODE>LayoutCacheFlipper</CODE> helps to flip <CODE>AbstractLayoutCache</CODE> upside down.
 *
 * @version 	02 May 2022
 * @author 	Nozomi `James' Ytow
 */
public class LayoutCacheFlipper
{
    public static int flipY(int y, FlippableLayoutCache layout)
    {
	return (layout == null || !layout.isFlipped(Flippable.VERTICAL)) ?	
	    y : (layout.getPreferredHeight() - y);
    }

    public static int flipRow(int row, FlippableLayoutCache layout)
    {
	return (layout == null || !layout.isFlipped(Flippable.VERTICAL)) ?
	    row :  ((layout.getRowCount() - 1) - row);
    }

    /*
    public static int unflipRow(int row, FlippableLayoutCache layout)
    {
	return (layout == null || !layout.isFlipped(Flippable.VERTICAL)) ?
	    row :  ((layout.getRowCount() - 1) - row);
    }
    */

    public static Rectangle flipRectangle(Rectangle rect, FlippableLayoutCache layout)
    {
	if(rect != null && layout != null && layout.isFlipped(Flippable.VERTICAL))
	    rect.y = flipY(rect.y, layout) - rect.height;
	return rect;
    }

    public static TreePath getVisibleTopPathFor(TreePath path, FlippableLayoutCache layout)
    {
	return (layout == null  || !layout.isFlipped(Flippable.VERTICAL))?
	    path : (layout.getPathForRow((layout.getRowCount() - 1) - layout.getRowForPath(path)));
    }

    public static <P extends TreePath> Enumeration<P> 	flipEnumeration(Enumeration<P> path, FlippableLayoutCache layout)
    {
	if  (path == null || layout == null  || !layout.isFlipped(Flippable.VERTICAL))
	    return path;
	ArrayList<P> list = Collections.list(path);
	Collections.reverse(list);
	return Collections.enumeration(list);
    }
								

}
