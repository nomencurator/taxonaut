/*
 * RowHeaderLabelRenderer.java:  a CellRenerer providing 
 * row header of a JTable
 *
 * Copyright (c) 2003, 2015, 2016 Nozomi `James' Ytow
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

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import javax.swing.table.JTableHeader;

/**
 * {@code CellRenderer} to render row header of {@code JTable}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RowHeaderLabelRenderer
    extends JLabel
    implements ListCellRenderer<Object>
{
    private static final long serialVersionUID = -5124038088939755239L;

    /**
     * Constructs {@code RowHeaderRenderer} for
     * {@code table}
     *
     * @param table {@code JTable} to which this renderer
     * provides row header
     */
    public RowHeaderLabelRenderer(JTable table)
    {
	this(table.getTableHeader());
    }

    /**
     * Constructs {@code RowHeaderRenderer} using
     * font, foreground and background colors of
     * {@code header}
     *
     * @param header {@code JTableHeder} of which font
     * and colors to be set to the renderer
     */
    public RowHeaderLabelRenderer(JTableHeader header)
    {
	setOpaque(true);
	setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	setHorizontalAlignment(CENTER);

	setForeground(header.getForeground());
	setBackground(header.getBackground());
	setFont(header.getFont());
    }

    public Component getListCellRendererComponent(JList<?> list,
						  Object value,
						  int index,
						  boolean isSelected,
						  boolean cellHasFocus)
    {
	if(value == null)
	    value ="";

	setText(value.toString());
	return this;
    }

}
