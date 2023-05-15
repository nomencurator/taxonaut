/*
 * NameTreeCellEditor.java:  a TreeCellEditor for NameUsageNode
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

import javax.swing.JTree;
import javax.swing.JTable;

import javax.swing.AbstractCellEditor;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.nomencurator.gui.swing.tree.NameTreeModel;

/**
 * <code>RenderingTableCellEnderer</code> for a <code>NameUsageNode</code>
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RenderingTableCellEditor
    extends AbstractCellEditor
    implements TableCellEditor
{
    private static final long serialVersionUID = -732280702798019989L;
    
    TableCellRenderer renderer;

    Object value;

    public RenderingTableCellEditor(TableCellRenderer renderer)
    {
	super();
	this.renderer = renderer;
    }


    public Component getTableCellEditorComponent(JTable table,
						 Object value,
						 boolean isSelected,
						 int row,
						 int column)
    {
	if(renderer == null)
	    return null;

	this.value = value;

	return renderer.getTableCellRendererComponent(table, value, isSelected, true,
						      row, column);

    }

    public Object getCellEditorValue()
    {
	return value;
    }
}

