/*
 * NameTableStringModel.java:  a TableModel mime cell renderer
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

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NameTableStringModel} is a {@link TableModel} 
 * wrapping a {@link NameTableModel} to provide 
 * a table of {@link NameUsage}s  attribets.
 * It converts values using {@link NameTableCellRenderer} internally.
 *
 * @version 	26 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTableStringModel
    extends NameTableModel
{
    private static final long serialVersionUID = 9030526307854670862L;

    @Getter @Setter protected NameTableModel model;

    /**
     * Constructs an 'empty' {@link NameTableModel}.
     */
    public NameTableStringModel()
    {
	this(null);
    }

    /**
     * Constructs a NameTableStringModel wrapping {@code model}.
     *
     * @paraman model {@link NameTableModel} to be wrapped.
     */
    public NameTableStringModel(NameTableModel model)
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
	    value =  NameTableCellRenderer.getValueAt((NameUsage<?>) value, columnIndex);
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
	if (model != null) {
	    model.setValueAt(aValue, rowIndex, columnIndex);
	}
	else {
	    super.setValueAt(aValue, rowIndex, columnIndex);
	}
    }
}
