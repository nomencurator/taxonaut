/*
 * UnitedNameTreeModelEvent.java:  an EventObject
 * telling addition or removal of a TreeModelto an 
 * UnitedNameTreeModel.
 *
 * Copyright (c) 2006, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.gui.swing.event;

import java.util.EventObject;

import javax.swing.tree.TreeModel;

import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

/**
 * <CODE>UnitedNameTreeModelEvent</code> is an <CODE>EventObject</CODE>
 * telling addition or removal of a <CODE>TreeModel</CODE> to an 
 * <CODE>UnitedNameTreeModel</CODE>.
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UnitedNameTreeModelEvent
    extends EventObject
{
    private static final long serialVersionUID = -486444621895978586L;

    protected TreeModel treeModel;
    protected int fromIndex;
    protected int toIndex;

    /**
     * Constructs an <CODE>UnitedNameTreeModelEvent</CODE>
     * representing a modification of the <CODE>model</CODE>.
     *
     */
    public UnitedNameTreeModelEvent(UnitedNameTreeModel model,
				     TreeModel treeModel,
				     int from,
				     int to)
    {
	super(model);
	this.treeModel = treeModel;
	fromIndex = from;
	toIndex = to;
    }

    public TreeModel getTreeModel()
    {
	return treeModel;
    }
    
    public int getFromIndex()
    {
	return fromIndex;
    }
          
    public int getToIndex()
    {
	return toIndex;
    }
}
