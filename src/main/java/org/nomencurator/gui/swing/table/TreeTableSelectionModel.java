/*
 * TreeTableSelectionModel.java:  a ListSelectionModel for table
 * synchronized with ListSelectionModels of trees
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

package org.nomencurator.gui.swing.table;

import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListSelectionModel;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.NameTree;


/**
 * {@code TreeModel} to manage {@code NameTreeNode}.
 *
 * @version 	29 June 201
 * @author 	Nozomi `James' Ytow
 */
public class TreeTableSelectionModel
    extends DefaultListSelectionModel
	    //    implements ListSelectionListener 
    implements TreeSelectionListener 
{
private static final long serialVersionUID = -6183844874442642278L;

    protected Map<NameTree, TreeSelectionModel> treeSelectionModels;

    public TreeTableSelectionModel()
    {
	super();
    }

    public Object put(NameTree tree)
    {
	if(treeSelectionModels == null)
	    treeSelectionModels = new HashMap<NameTree, TreeSelectionModel>();

	TreeSelectionModel previousModel = treeSelectionModels.get(tree);

	if(previousModel != null)
	    previousModel.removeTreeSelectionListener(this);

	TreeSelectionModel model = tree.getSelectionModel();
	treeSelectionModels.put(tree, model);
	model.addTreeSelectionListener(this);

	return previousModel;
    }

    public Object remove(NameTree tree)
    {
	if(treeSelectionModels == null ||
	   treeSelectionModels.isEmpty())
	    return null;

	TreeSelectionModel model =treeSelectionModels.get(tree);

	if(model != null)
	    model.removeTreeSelectionListener(this);

	return treeSelectionModels.remove(tree);
    }

    public void valueChanged(TreeSelectionEvent e)
    {
    }
}
