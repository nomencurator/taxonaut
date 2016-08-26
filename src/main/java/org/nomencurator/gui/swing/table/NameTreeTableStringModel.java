/*
 * NameTreeTableStringModel.java:  a TableModel to list NameUsage attributes
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.table;

import org.nomencurator.model.NameUsage;

import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NameTreeTableStringModel} is a {@link TableModel} 
 * wrapping a {@link NameTreeTableModel} to provide 
 * a table of {@link NameUsage}s'  name literals with rank.
 *
 * @version 	26 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableStringModel
    extends NameTreeTableModel
{
    private static final long serialVersionUID = 621936144735796994L;

    @Getter @Setter protected NameTreeTableModel model;
    
    /**
     * Constructs an 'empty' {@link NameTreeTableModel}.
     */
    public NameTreeTableStringModel()
    {
	this(null);
    }

    /**
     * Constructs a NameTreeTableStringModel wrapping {@code model}.
     *
     * @paraman model {@link NameTreeTableModel} to be wrapped.
     */
    public NameTreeTableStringModel(NameTreeTableModel model)
    {
	setModel(model);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
	return model == null ?
	    super.getColumnClass(columnIndex) :
	    model.getColumnClass(columnIndex);
    }

    @Override
    public int getColumnCount()
    {
	return model == null ?
	    super.getColumnCount() :
	    model.getColumnCount();
    }

    @Override
    public String getColumnName(int columnIndex)
    {
	return model == null ?
	    super.getColumnName(columnIndex) :
	    model.getColumnName(columnIndex);
    }

    @Override
    public int getRowCount()
    {
	return model == null ?
	    super.getRowCount() :
	    model.getRowCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
	if (model == null)
	    return super.getValueAt(rowIndex, columnIndex);

	Object value = model.getValueAt(rowIndex, columnIndex);

	if (value instanceof NameUsage) {
	    value = ((NameUsage)value).getRankedName();
	}
	else if (value instanceof NameTreeNode) {
	    value = ((NameTreeNode)value).getRankedName();
	}

	return value;
    }

    @Override
    public boolean  isCellEditable(int rowIndex, int columnIndex)
    {
	return model == null ?
	    super.isCellEditable(rowIndex, columnIndex) :
	    model.isCellEditable(rowIndex, columnIndex);
    }
    
    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
	if (model != null)
	    model.setValueAt(aValue, rowIndex, columnIndex);
	else
	    super.setValueAt(aValue, rowIndex, columnIndex);
    }
}
