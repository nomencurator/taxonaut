/*
 * NameTableCellRenderer.java:  a TableCellRenerer for NameTreeNode
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;

import javax.swing.table.DefaultTableCellRenderer;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.model.gbif.NubNameUsage;

import org.nomencurator.model.util.NameUsageAttribute;

import org.nomencurator.gui.swing.DefaultColors;

import org.nomencurator.gui.swing.tree.NameTreeNode;

/**
 * {@code NameTableCellRenderer} is a {@code TableCellRenderer} to render a {@code NameTree}
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTableCellRenderer
    extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = -7370663090747254139L; 	

    /**
     * Attribute of {@code NameUsage} to be rendered.
     */
    protected NameUsageAttribute attribute;

    /**
     * Foreground color of disabled components.
     */
    protected Color disabledForeground;

    /**
     * Foreground color of enabled components.
     */
    protected Color enabledForeground;

    /**
     * Constructs a cell renderer for given {@code attribute} of {@code NameUsage}.
     *
     * @param attribute to be rendered.
     */
    public NameTableCellRenderer(NameUsageAttribute attribute)
    {
	super();
	this.attribute = attribute;
	setDefaultTextColor();
    }

    /**
     * Sets default foreground color of enabled and disabled elements.
     */
    protected void setDefaultTextColor()
    {
	disabledForeground = DefaultColors.getDisabledForeground(DefaultColors.ComponentName.LABEL);
	enabledForeground = DefaultColors.getForeground(DefaultColors.ComponentName.LABEL);
    }

    @Override
    public void updateUI()
    {
	super.updateUI();
	setDefaultTextColor();
    }

    /**
     * Returns the cell rendering {@code Component} with foreground color depeding on
     * synonym status of the {@code NameUsage} to be rendered.
     *
     * @param table the {@code JTable}
     * @param value the value to assign to the cell at {@code [row, column]}, {@code NameUsage} expected
     * @param isSelected true if the cell is selected
     * @param hasFocus true if the chell has focus
     * @param row the row of the cell to render
     * @param column the column of the cell to render
     * @return the table cell renderer
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, 
						   int row, int column)
    {
	NameUsage<?> nameUsage = null;
	boolean isSynonym = false;
	if(value != null) {
	    if(value instanceof NameUsage) {
		nameUsage = (NameUsage)value;
	    }
	    else if(value instanceof NameTreeNode) {
		nameUsage = (NameUsage<?>)((NameTreeNode)value).getUserObject();
	    }
	}

	if(nameUsage != null) {
	    isSynonym = nameUsage.isSynonym();
	    switch (attribute) {
	    case RANK:
		value = nameUsage.getRank();
		break;
	    case NAME:
		value = nameUsage.getLiteral();
		break;
	    case  AUTHORITY:
		value = nameUsage.getAuthority();
		if(value == null) {
		    nameUsage = nameUsage.getSensu();
		    if(nameUsage != null)
			value = nameUsage.getViewName();
		}
		break;
	    case YEAR:
		value = nameUsage.getAuthorityYear();
		break;
	    case SENSU:
		value = nameUsage.getViewName();
		break;
	    case DATASET:
		if (nameUsage instanceof NubNameUsage) {
		    value = ((NubNameUsage)nameUsage).getDatasetTitle();
		}
		break;
	    case DESCENDANTS_COUNT:
		value = nameUsage.getDescendantCount();
		break;
	    default:
		// nothing to do
		break;
	    }
	    if (value != null)
		value = value.toString();
	}

	Component component =  super.getTableCellRendererComponent(table, value, isSelected,
								   hasFocus, row, column);
	if (nameUsage != null) {
	    if (isSynonym) {
		 component.setForeground(disabledForeground);
	    }
	    else {
		component.setForeground(enabledForeground);
	    }
	}
	    
	return component;
    }

}
