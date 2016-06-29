/*
 * TreeHeaderRenderer.java:  a TableCellRenerer for JTree
 *
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

package org.nomencurator.gui.swing.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.table.TableCellRenderer;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.AlignerTree;
import org.nomencurator.gui.swing.NameTree;
import org.nomencurator.gui.swing.NameTree.LineStyle;
import org.nomencurator.gui.swing.NamedTree;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NodeMapper;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

/**
 * {@code TableCellRenderer} to render a {@code NameTree}
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class TreeHeaderRenderer
    extends JPanel
    implements TableCellRenderer, Alignable, Aligner
{
    private static final long serialVersionUID = -5975697623154868510L;

    protected JTree tree;

    protected JScrollPane pane;

    protected TableCellRenderer renderer;

    protected JTable table;

    protected int column;

    protected Component title;

    public TreeHeaderRenderer(TableCellRenderer renderer)
    {
	this.renderer = renderer;
    }

    public TreeHeaderRenderer(NameTreeModel model, TableCellRenderer renderer)
    {
	this(model, (Aligner)null, renderer);
    }

    public TreeHeaderRenderer(NameTreeModel model, Aligner aligner, TableCellRenderer renderer)
    {
	this(new NameTree(model, aligner), renderer);
    }

    public TreeHeaderRenderer(UnitedNameTreeModel model, TableCellRenderer renderer)
    {
	this(new AlignerTree(model), renderer);
    }

    public TreeHeaderRenderer(TreeModel model, TableCellRenderer renderer)
    {
	this(new JTree(model), renderer);
    }

    public TreeHeaderRenderer(JTree tree, TableCellRenderer renderer)
    {
	super();
	this.tree = tree;
	this.renderer = renderer;
	pane = new JScrollPane(tree,
			       JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			       JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	setLayout(new BorderLayout());
	add(pane, BorderLayout.CENTER);
    }

    public JTree getTree()
    {
	return tree;
    }

    public JScrollPane getPane()
    {
	return pane;
    }

    public NameTree getNameTree()
    {
	return (NameTree)tree;
    }

    public void setRenderer(TableCellRenderer renderer)
    {
	this.renderer = renderer;
    }

    public TableCellRenderer getRenderer()
    {
	return renderer;
    }

    /**
     *  Returns the component used for drawing the cell.  This method is
     *  used to configure the renderer appropriately before drawing.
     *
     * @param	table		the {@code JTable} that is asking the 
     *				renderer to draw; can be {@code null}
     * @param	value		the value of the cell to be rendered.  It is
     *				up to the specific renderer to interpret
     *				and draw the value.  For example, if
     *				{@code value}
     *				is the string "true", it could be rendered as a
     *				string or it could be rendered as a check
     *				box that is checked.  {@code null} is a
     *				valid value
     * @param	isSelected	true if the cell is to be rendered with the
     *				selection highlighted; otherwise false
     * @param	hasFocus	if true, render cell appropriately.  For
     *				example, put a special border on the cell, if
     *				the cell can be edited, render in the color used
     *				to indicate editing
     * @param	row	        the row index of the cell being drawn.  When
     *				drawing the header, the value of
     *				{@code row} is -1
     * @param	column	        the column index of the cell being drawn
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, 
						   int row, int column)
    {
	this.table = table;
	this.column = column;

	if(row != -1)
	    return renderer.getTableCellRendererComponent(table, value, isSelected,
							  hasFocus, row, column);
	if(value instanceof TreeModel) {
	    TreeModel model = (TreeModel)value;
	    if(tree == null) {
		if(value instanceof NameTreeModel)
		    tree = new NameTree((NameTreeModel)model);
		else if (value instanceof UnitedNameTreeModel) {
		    tree = new AlignerTree((UnitedNameTreeModel)model);
		}
		else {
		    tree = new JTree(model);
		    setCollapsedIcon(null);
		    setExpandedIcon(null);
		    tree.setRootVisible(false);
		    DefaultTreeCellRenderer renderer =
			(DefaultTreeCellRenderer)tree.getCellRenderer();
		    renderer.setLeafIcon(null);
		    renderer.setClosedIcon(null);
		    renderer.setOpenIcon(null);

		}
		pane = new JScrollPane(tree);
		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);
	    }
	    else if(value != tree.getModel()) {
		tree.setModel(model);
	    }
	    String s = model.toString();
	    if(value instanceof NameTreeModel) {
		s = ((NameTreeModel)model).getViewName(); 
	    }
	    else if(value instanceof UnitedNameTreeModel &&
		    tree instanceof AlignerTree)
		s = ((AlignerTree)tree).getTreeName();
	    else
		s = "";

	    title = 
		renderer.getTableCellRendererComponent(table, 
						       s,
						       isSelected, hasFocus,
						       row, column);
	    ((JComponent)title).setToolTipText(s);
	    /*
	    if(title == null)
		title = new javax.swing.JSlider();
	    */
	    add(title, BorderLayout.NORTH);

	}
	else {
	    title = 
		renderer.getTableCellRendererComponent(table, 
						       value,
						       isSelected, hasFocus,
						       row, column);
	    ((JComponent)title).setToolTipText(value.toString());
	    add(title, BorderLayout.NORTH);
	}

	return this;
    }

    public void setCollapsedIcon(Icon icon)
    {
	if(tree instanceof NameTree)
	    ((NameTree)tree).setCollapsedIcon(icon);
	else
	    ((BasicTreeUI)tree.getUI()).setCollapsedIcon(icon);
    }

    public void setExpandedIcon(Icon icon)
    {
	if(tree instanceof NameTree)
	    ((NameTree)tree).setExpandedIcon(icon);
	else
	    ((BasicTreeUI)tree.getUI()).setExpandedIcon(icon);
    }

    public void setLineStyle(String style)
    {
	if(tree instanceof NameTree)
	    ((NameTree)tree).setLineStyle(LineStyle.getLineStyle(style));
	else
	    tree.putClientProperty("JTree.lineStyle", style);
    }

    public void setAligner(Aligner aligner)
    {
	if((tree instanceof Alignable) &&
	   !(tree instanceof Aligner))
	    ((Alignable)tree).setAligner(aligner);
    }

    public Aligner getAligner()
    {
	if(tree instanceof Alignable)
	    return ((Alignable)tree).getAligner();

	return null;
    }

    public void setMapper(NodeMapper mapper)
    {
	if(tree instanceof Aligner)
	    ((Aligner)tree).setMapper(mapper);
    }

    public NodeMapper getMapper()
    {
	if(tree instanceof Aligner)
	    return ((Aligner)tree).getMapper();

	return null;
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	if(tree instanceof Aligner)
	    return ((Aligner)tree).getBounds(path, placeIn);

	return placeIn;
    }

}

