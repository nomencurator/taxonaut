/*
 * NameTreeNodeRenderer.java:  a TableCellRenerer for NameTreeNode
 *
 * Copyright (c) 2002, 2003, 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
import java.awt.Point;

import java.awt.event.MouseEvent;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JLabel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;

import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.gui.swing.DefaultColors;
import org.nomencurator.gui.swing.NameTree;
import org.nomencurator.gui.swing.NameTreeTable;

import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

/**
 * {@code NameTreeNodeRenderer} is a {@code TableCellRenderer} to render a {@code NameTree}
 *
 * @version 	02 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeNodeRenderer
    extends DefaultTableCellRenderer
{
    private static final long serialVersionUID = 7273064108631364109L;

    protected TableCellRenderer renderer;

    protected boolean toAbbreviate;

    /**
     * Foreground color of disabled components.
     */
    protected Color disabledForeground;

    /**
     * Foreground color of enabled components.
     */
    protected Color enabledForeground;

    public NameTreeNodeRenderer()
    {
	this(true);
    }

    public NameTreeNodeRenderer(boolean abbreviate)
    {
	super();
	setToAbbreviate(abbreviate);
	setDefaultTextColor();
    }

    protected void setDefaultTextColor()
    {
	disabledForeground = DefaultColors.getDisabledForeground(DefaultColors.ComponentName.LABEL);
	enabledForeground = DefaultColors.getForeground(DefaultColors.ComponentName.LABEL);
    }

    public void updateUI()
    {
	super.updateUI();
	setDefaultTextColor();
    }

    public void setRenderer(TableCellRenderer renderer)
    {
	this.renderer = renderer; 
    }

    public void setToAbbreviate(boolean abbreviate)
    {
	toAbbreviate = abbreviate;
    }

    public boolean isToAbbreviate()
    {
	return toAbbreviate;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, 
						   int row, int column)
    {
	NameUsage<?> n = null;
	boolean isSynonym = false;
	if(value == null) {
	    value = "";
	}
	else if(value instanceof UnitedNameTreeNode) {
	    value = ((UnitedNameTreeNode)value).getLiteral();
	}
	else if(value instanceof NameUsage) {
	    n = (NameUsage)value;
	}
	else if(value instanceof NameTreeNode) {
	    n = (NameUsage)((NameTreeNode)value).getUserObject();
	}
	else if (value instanceof Collection) {
	    Collection<?> collection = (Collection<?>)value;
	    if (collection.isEmpty())
		value = "";
	    else {
		StringBuffer buffer = new StringBuffer();
		int objects = collection.size();
		int i = 0;
		for (Object object : collection) {
		    i++;
		    if (object instanceof NameUsage) {
			isSynonym  |= renderText((NameUsage)object, buffer);
		    }
		    else if (object instanceof NameTreeNode) {
			isSynonym  |= renderText((NameUsage)((NameTreeNode)object).getUserObject(), buffer);
		    }
		    else {
			buffer.append(' ');
		    }
		    if (buffer.length() > 0 && i < objects)
			buffer.append('/');
		}
		value = buffer.toString();
	    }
	}

	if(n != null) {
	    StringBuffer buffer = new StringBuffer();
	    isSynonym |= renderText(n, buffer);
	    value = buffer.toString();
	}

	Component component =  super.getTableCellRendererComponent(table, value, isSelected,
								   hasFocus, row, column);
	if (isSynonym) {
	    component.setForeground(disabledForeground);
	}
	else {
	    component.setForeground(enabledForeground);
	}
	    
	return component;
    }

    protected boolean renderText(NameUsage<?> nameUsage, StringBuffer buffer)
    {
	if (nameUsage == null)
	    return false;
	if (buffer == null)
	    buffer = new StringBuffer();

	Rank rank = nameUsage.getRank();
	String literal = null;
	if(rank != null) {
	    if(isToAbbreviate())
		literal = Rank.getAbbreviation(rank.getName());
	    if(literal == null)
		literal = rank.getName();
	    if (literal != null && literal.length() > 0)
		buffer.append(literal).append(' ');
	}
	buffer.append(nameUsage.getLiteral());
	return nameUsage.isSynonym();
    }

    /**
     * Returns a {@code StringBuffer} representing the tool tip text on
     * direct lower {@code NameUsage}s of {@code nameUsage},
     * having the literal of {@code filter} but
     * not contained in the {@code toSkip} which can be null.
     * The {@code buffer} is used as the buffer to return
     * if it is not null, otherwise a new {@code StringBuffer} to be
     * created.  If either {@code nameUsage} or {@code filter} is null,
     * or the {@code filter} is empty, the method do nothing, except
     * creation of a new {@code StringBuffer} if not given.
     *
     * @param nameUsage of which lower usages to be processed.
     * @param filter name of lower {@code NameUsage}s to be picked up
     * @param buffer to which the result tool tip text is stored, or null if
     * the method is expected to create the buffer.
     * @param toSkip a {@code Collection} of {@code NameUsage}s
     * to ignore, or null unnecessary to skip.
     */
    @SuppressWarnings("unchecked")
    protected <T extends NameUsage<?>> StringBuffer getToolTipTextBuffer(final T nameUsage, final String filter, StringBuffer buffer, Collection<T> toSkip)
    {
	if (buffer == null)
	    buffer = new StringBuffer();
	if (nameUsage != null&& filter != null && filter.length() > 0) {
	    List<T> lowers = (List<T>)nameUsage.getLowerNameUsages();
	    if (lowers != null && lowers.size() > 0) {
		for (T lower : lowers) {
		    if (filter.equals(lower.getLiteral())
			&& (toSkip == null || !toSkip.contains(lower))) {
			toSkip.add(lower);
			if (buffer.length() > 0)
			    buffer.append("\n\n");
			buffer.append(lower.getSummary());
		    }
		}
	    }
	}
	return buffer;
    }

    /**
     * Returngs tool tip text on detail of the {@code NameUsage} at given {@code event} and {@code table}.
     *
     * @param event where the tool tip to pop up
     * @param table on which the tool tip to tell about
     * @return String of tool tip text representing detail of the target {@code NameUsage}
     */
    public String getToolTipText(MouseEvent event, NameTreeTable<?> table)
    {
	Object source = event.getSource();

	if (table == null) {
	    return super.getToolTipText(event);
	}

	Point cursor = event.getPoint();
	int row = table.rowAtPoint(cursor);
	int column = table.columnAtPoint(cursor);
	if (row == -1 || column == -1)
	    return super.getToolTipText(event);
	row = table.convertRowIndexToModel(row); 
	column = table.convertColumnIndexToModel(column);
	Object value = table.getModel().getValueAt(row, column);
	Object key = table.getModel().getValueAt(row, 0);
	StringBuffer buffer = new StringBuffer();
	if (value == null || value instanceof UnitedNameTreeNode) {
	    return null;
	}
	else if(value instanceof NamedNode) {
	    buffer.append(((NamedNode)value).getToolTipText());
	    if (!((NamedNode)value).isLeaf()) {
		if (key instanceof UnitedNameTreeNode) {
		    UnitedNameTreeModel unitedTreeModel = ((NameTreeTableModel)table.getModel()).getUnitedTree();
		    TreeModel treeModel = 
			((TreeHeaderRenderer)table.getColumnModel().getColumn(column).getHeaderRenderer()).getTree().getModel();
		    TreeNode childNode = unitedTreeModel.getNodeFor((TreeNode)key, treeModel);
		    if (childNode != null) {
			if (buffer.length() > 0)
			    buffer.append("\n\n");
			buffer.append(((NamedNode)childNode).getToolTipText());
		    }
		}
		else if (key instanceof String && value instanceof DefaultMutableTreeNode) {
		    Set<NameUsage<?>> usages = new HashSet<NameUsage<?>>();
		    Enumeration<?> children = ((NamedNode)value).children();
		    String literal = (String) key;
		    NameUsage<?> usage = null;
		    while (children.hasMoreElements()) {
			NamedNode<?> child = (NamedNode<?>)children.nextElement();
			if (literal.equals(child.getLiteral())) {
			    usage = (NameUsage<?>)((DefaultMutableTreeNode)child).getUserObject();
			    if (! usages.contains(usage)) {
				usages.add(usage);
				if (buffer.length() > 0)
				    buffer.append("\n\n");
				buffer.append(child.getToolTipText());
			    }
			}
		    }
		    usage = (NameUsage<?>)((DefaultMutableTreeNode)value).getUserObject();
		    buffer = getToolTipTextBuffer(usage, (String) key, buffer, usages);
		    usages.clear();
		}
	    }
	}
	else if (value instanceof NamedObject)  {
	    buffer.append(((NamedObject<?>)value).getSummary());
	    String literal = null;
	    if (value instanceof NameUsage) {
		if (key instanceof UnitedNameTreeNode) {
		    literal = ((UnitedNameTreeNode)key).getLiteral();
		}
		else if (key instanceof NameUsage) {
		    literal = ((NameUsage<?>)key).getLiteral();
		}
		if (literal != null) {
		    buffer = getToolTipTextBuffer((NameUsage<?>)value, literal, buffer, null);
		}
	    }
	}
	else if (value instanceof Collection) {
	    Collection<?> collection = (Collection<?>)value;
	    if (collection.isEmpty())
		//return super.getToolTipText(event);
		return null;
	    else {
		int objects = collection.size();
		int objectCount = 0;
		for (Object object : collection) {
		    objectCount++;
		    if (value == null || value instanceof UnitedNameTreeNode) continue;
		    else if (object instanceof NamedNode) {
			buffer.append(((NamedNode<?>)object).getToolTipText());
		    }
		    else if (object instanceof NamedObject) {
			buffer.append(((NamedObject<?>)object).getSummary());
		    }
		    if (buffer.length() > 0 && objectCount < objects)
			buffer.append("\n\n");
		}
	    }
	}
	else {
	    return null;
	}

	return buffer.toString();
    }
}
