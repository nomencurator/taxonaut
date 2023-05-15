/*
 * NameTreeCellRenderer.java:  a TableCellRenerer for NameTree
 *
 * Copyright (c) 2002, 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.JTable;

import javax.swing.table.TableCellRenderer;

import org.nomencurator.gui.swing.NameTree;

import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.NameTreeModel;

/**
 * {@code TableCellRenderer} to render a {@code NameTree}.
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeRenderer
    extends JScrollPane
    implements TableCellRenderer
{
    private static final long serialVersionUID = 5589775479531609802L;

    protected NameTree tree;

    public NameTreeRenderer()
    {
	super();
    }

    public NameTreeRenderer(NameTreeModel model)
    {
	this(model, null);
    }

    public NameTreeRenderer(NameTreeModel model, Aligner aligner)
    {
	this(new NameTree(model, aligner));
    }

    public NameTreeRenderer(NameTree tree)
    {
	super(tree);
	this.tree = tree;
    }

    public NameTree getNameTree()
    {
	return tree;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, 
						   int row, int column)
    {
	if(value instanceof NameTreeModel) {
	    if(tree == null) {
		tree = new NameTree((NameTreeModel)value);
		setViewportView(tree);
	    }
	    else if(tree.getModel() != value)
		tree.setModel((NameTreeModel)value);
	}

	return this;
    }
}

