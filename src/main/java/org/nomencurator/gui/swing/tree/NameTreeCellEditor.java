/*
 * NameTreeCellEditor.java:  a TreeCellEditor for NameUsageNode
 *
 * Copyright (c) 2002, 2003, 2015 Nozomi `James' Ytow
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

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.JTable;

import javax.swing.table.TableCellEditor;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * <code>TreeCellREnderer</code> for a <code>NameUsageNode</code>
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeCellEditor
    extends DefaultTreeCellEditor
{
    public NameTreeCellEditor(JTree tree)
    {
	super(tree, (DefaultTreeCellRenderer)tree.getCellRenderer());
    }


    public NameTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer)
    {
	super(tree, renderer);
    }

    public Component getTableCellEditorComponent(JTable table,
						 Object value,
						 boolean isSelected,
						 int row,
						 int column)
    {
	return table.getCellRenderer(row, column).getTableCellRendererComponent(table, value,
										isSelected, true, row, column);
    }
}

