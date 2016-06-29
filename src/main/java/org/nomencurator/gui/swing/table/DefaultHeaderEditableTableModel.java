/*
 * HeaderEditableTableModel.java:  a DefaultTableModel capable to
 * manage non-String header value
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

package org.nomencurator.gui.swing.table;

import java.util.Vector;

//import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Rank;

/**
 * <code>TreeModel</code> to manage <code>NameTreeNode</code>
 *
 * @version 	28 June 2016
 * @author 	Nozomi `James' Ytow
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class DefaultHeaderEditableTableModel
    extends DefaultTableModel
    implements HeaderEditableTableModel
{
    private static final long serialVersionUID = -2484757998080458762L;

    public DefaultHeaderEditableTableModel()
    {
        super();
    }

    public DefaultHeaderEditableTableModel(int numRows, int numColumns)
    {
        super(numRows, numColumns);
    }

    public DefaultHeaderEditableTableModel(Vector columnNames, int numRows)
    {
        super(columnNames, numRows);
    }

    public DefaultHeaderEditableTableModel(Object[] columnNames, int numRows)
    {
        super(columnNames, numRows);
    }

    public DefaultHeaderEditableTableModel(Vector data, Vector columnNames)
    {
        super(data, columnNames);
    }

    public DefaultHeaderEditableTableModel(Object[][] data, Object[] columnNames)
    {
        super(data, columnNames);
    }

    /**
     * Returns header value at the <CODE>column</CODE>
     *
     * @return an <CODE>Object</CODE> for the header value
     * at <CODE>column</CODE> using the string value of the
     * appropriate member in <code>columnIdentifiers</code>.
     * If <code>columnIdentifiers</code> is <code>null</code>
     * or does not have an entry for this index, returns the default
     * name provided by <CODE>getName</CODE>
     *
     * @see #getName(int)
     */
    public Object getHeaderValue(int column)
    {
        if (columnIdentifiers == null
	    || columnIdentifiers.size() <= column)
            return getColumnName(column);

        Object value = columnIdentifiers.elementAt(column);
        if (value == null)
            return getColumnName(column);

	return value;
    }

    public Object getValueAt(int row, int column)
    {
	if(row < 0)
	    //return columnIdentifiers.elementAt(column);
	    return getHeaderValue(column);
	else
	    return ((Vector)dataVector.elementAt(row)).elementAt(column);
    }

    public void setValueAt(Object value, int row, int column)
    {
	if(row < 0)
	    columnIdentifiers.setElementAt(value, column);
	else
	    ((Vector)dataVector.elementAt(row)).setElementAt(value, column);
	fireTableCellUpdated(row, column);
    }

}
