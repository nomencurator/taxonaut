/*
 * NameTreeTableModelEvent.java:  an EventObject
 * telling addition or removal of a TreeModel to an 
 * NameTreeTableModel.
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

package org.nomencurator.gui.swing.event;

import java.util.EventObject;

import org.nomencurator.gui.swing.table.NameTreeTableModel;

/**
 * <CODE>NameTreeTableModelEvent</code> is an <CODE>EventObject</CODE>
 * telling addition or removal of a <CODE>TreeModel</CODE> to a 
 * <CODE>NameTreeTableModel</CODE>.
 *
 * @version 	25 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableModelEvent
    extends EventObject
{
    private static final long serialVersionUID = 928495398386167837L;

    protected int columnIndex;

    protected Object headerValue;

    /**
     * Constructs an <CODE>NameTreeTableModelEvent</CODE>
     * representing a modification of the <CODE>model</CODE>.
     *
     */
    public NameTreeTableModelEvent(NameTreeTableModel model,
				   int columnIndex,
				   Object headerValue)
    {
	super(model);
	this.columnIndex = columnIndex;
	this.headerValue = headerValue;
    }

    public NameTreeTableModel getTableModel()
    {
	return (NameTreeTableModel)getSource();
    }
    
    public int getColumnIndex()
    {
	return columnIndex;
    }

    public Object getHeaderValue()
    {
	return headerValue;
    }
}
