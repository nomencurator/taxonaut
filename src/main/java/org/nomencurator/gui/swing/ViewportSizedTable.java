/*
 * ViewportSizedTable.java:  a JTable having specified viewport size
 *
 * Copyright (c) 2004, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import javax.swing.table.TableModel;
import javax.swing.table.TableColumnModel;

/**
 * {@code ViewportSizedTable} is a {@code JTable}.
 * having specified viewport size
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see javax.swing.JTable
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ViewportSizedTable
    extends JTable
{
    private static final long serialVersionUID = -6859738296881435093L;

    public static final int DEFAULT_ROWS = 4;

    protected int viewportRows;
    
    public ViewportSizedTable()
    {
	this(null);
    }

    public ViewportSizedTable(TableModel dataModel)
    {
	this(dataModel, null);
    }

    public ViewportSizedTable(TableModel dataModel, int rows)
    {
	this(dataModel, null, rows);
    }

    public ViewportSizedTable(TableModel dataModel, TableColumnModel columnModel)
    {
        this(dataModel, columnModel, null);
    }

    public ViewportSizedTable(TableModel dataModel, TableColumnModel columnModel, int rows)
    {
        this(dataModel, columnModel, null, rows);
    }

    public ViewportSizedTable(TableModel dataModel,
			      TableColumnModel columnModel,
			      ListSelectionModel selectionModel)
    {
        this(dataModel, columnModel, selectionModel, DEFAULT_ROWS);
    }

    public ViewportSizedTable(TableModel dataModel,
			      TableColumnModel columnModel,
			      ListSelectionModel selectionModel,
			      int rows)
    {
	super(dataModel, columnModel, selectionModel);
	setViewportRows(rows);
    }

    public int getViewportRows()
    {
	return viewportRows;
    }

    public void setViewportRows(int rows)
    {
	if(viewportRows == rows)
	    return;
	if(rows > 0) {
	    Dimension d = getPreferredScrollableViewportSize();
	    d.height = rows * getRowHeight();
	    if(getShowHorizontalLines())
		d.height += (rows - 1) * getRowMargin();
	    setPreferredScrollableViewportSize(d);
	}
    }
}
