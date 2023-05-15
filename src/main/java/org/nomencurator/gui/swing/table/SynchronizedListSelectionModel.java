/*
 * SynchronizedListSelectionModel.java:  a synchronized ListSelectionModel
 *
 * Copyright (c) 2003, 2005, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071, JP19K12711
 */

package org.nomencurator.gui.swing.table;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

/**
 * <CODE>SynchronizedListSelectionModel</code> provides a synchronized ListSelectionModel
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class SynchronizedListSelectionModel
    extends DefaultListSelectionModel
    implements ListSelectionListener,
	       TreeSelectionListener
{
    private static final long serialVersionUID = 3954105655194822583L;

    protected Object eventSource;

    protected RowMapper rowMapper;

    protected TreeSelectionModel treeSelectionModel;

    protected JTree tree;

    protected JTable table;

    public SynchronizedListSelectionModel()
    {
	this(null);
    }

    public SynchronizedListSelectionModel(RowMapper mapper)
    {
	this(mapper, null);
    }

    public SynchronizedListSelectionModel(RowMapper mapper,
					  TreeSelectionModel treeSelectionModel)
    {
	super();
	setRowMapper(mapper);
	setTreeSelectionModel(treeSelectionModel);
    }

    public RowMapper getRowMapper()
    {
	return rowMapper;
    }

    public synchronized void setRowMapper(RowMapper mapper)
    {
	rowMapper = mapper;
    }

    public TreeSelectionModel getTreeSelectionModel()
    {
	return treeSelectionModel;
    }

    public synchronized void setTreeSelectionModel(TreeSelectionModel treeSelectionModel)
    {
	if(this.treeSelectionModel != null)
	    this.treeSelectionModel.removeTreeSelectionListener(this);
	this.treeSelectionModel = treeSelectionModel;
	if(this.treeSelectionModel != null)
	    this.treeSelectionModel.addTreeSelectionListener(this);
    }


    public JTree getTree()
    {
	return tree;
    }

    public synchronized void setTree(JTree tree)
    {
	this.tree = tree;
    }


    public JTable getTable()
    {
	return table;
    }

    public synchronized void setTable(JTable table)
    {
	this.table = table;
    }


    /**
     * Sets <CODE>source</CODE> as the `owner' of selection
     * to control event handling
     *
     * @param source <CODE>Object</CODE> represents
     * the `owner' of selection
     */
    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    /**
     * Returns `owner' <CODE>Object</CODE>  of selection
     * to control event handling
     *
     * @return <CODE>Object</CODE> represents
     * the `owner' of selection
     */
    protected Object getEventSource()
    {
	return eventSource;
    }


    public void valueChanged(ListSelectionEvent event)
    {
	/*
	if(event.getValueIsAdjusting() ||
	   rowMapper == null)
	    return;

	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());
	int first = getFirstIndex();
	int last = getLastIndex();
	NameTreeTableModel model =
	    (NameTreeTableModel)rowMapper;
	TreePath[] p = new TreePath[last - first];
	last++;
	int n = 0;
	for(int i = first; i < last; i++) {
	    if
	}
	setEventSource(null);
	*/
    }

    public void clearSelection()
    {
	if(getEventSource() != null)
	    return;

	if(getEventSource() == null)
	    setEventSource(this);

	super.clearSelection();

	if(treeSelectionModel != null && 
	   getEventSource() == this)
	    treeSelectionModel.clearSelection();

	if(getEventSource() == this)
	    setEventSource(null);
    }

    protected enum SelectionMode {
	REMOVE, SET, ADD
    }

    protected void handleSelectionInterval(int index0, int index1, SelectionMode selectionMode)
    {
	synchronized (this) {
	    if(getEventSource() != null)
		return;

	    if(getEventSource() == null)
		setEventSource(this);
	}

	switch (selectionMode) {
	case SET:
	    super.setSelectionInterval(index0, index1);
	    break;
	case ADD:
	    super.addSelectionInterval(index0, index1);
	    break;
	case REMOVE:
	    super.removeSelectionInterval(index0, index1);
	    break;
	default:
	    break;
	}

	if(getEventSource() == this) {
	    if(!getValueIsAdjusting())
		treeSelectionModel.clearSelection();
	    if(treeSelectionModel != null) {
		switch (selectionMode) {
		case SET:
		    treeSelectionModel.setSelectionPaths(getPathsFor(index0, index1));
		    break;
		case ADD:
		    treeSelectionModel.addSelectionPaths(getPathsFor(index0, index1));
		    break;
		case REMOVE:
		    treeSelectionModel.removeSelectionPaths(getPathsFor(index0, index1));
		    break;
		default:
		    break;
		}
	    }
	}

	switch (selectionMode) {
	case SET:
	case ADD:
	    scrollTo(index0);
	    break;
	}

	if(table != null)
	    table.repaint();

	synchronized (this) {
	    if(getEventSource() == this)
		setEventSource(null);
	}
    }


    public void addSelectionInterval(int index0, int index1)
    {
	handleSelectionInterval(index0, index1, SelectionMode.ADD);
    }

    public void setSelectionInterval(int index0, int index1)
    {
	handleSelectionInterval(index0, index1, SelectionMode.SET);
    }

    public void removeSelectionInterval(int index0, int index1)
    {
	handleSelectionInterval(index0, index1, SelectionMode.REMOVE);
    }

    protected TreePath[] getPathsFor(int index0, int index1)
    {
	index1++;
	LinkedHashSet<DefaultMutableTreeNode> nodes = new LinkedHashSet<DefaultMutableTreeNode>();
	NameTreeTableModel model = (NameTreeTableModel)rowMapper;
	if(model == null)
	    return null;  //REVISIT

	for(int i = index0; i < index1; i++) {
	    /*
	    if(isSelectedIndex(i))
		continue;
	    */
	    String name = (String)model.getValueAt(i, 0);
	    Collection<UnitedNameTreeNode> unitedNodes =
		((UnitedNameTreeModel)tree.getModel()).getNodesFor(name);
	    if(unitedNodes != null) {
		for (UnitedNameTreeNode node : unitedNodes) {
		    if(nodes.contains(node))
			continue;
		    nodes.add(node);
		}
	    }
	}

	if (nodes.size() == 0) {
	    return null;
	}

	TreePath[] paths = new TreePath[nodes.size()];
	Iterator<DefaultMutableTreeNode> iterator = nodes.iterator();
	int i = 0;
	while(iterator.hasNext()) {
	    paths[i++] = 
		new TreePath((iterator.next()).getPath());
	}
	return paths;
    }

    /**
     * Converts selection paths in one of synchronized trees
     * represented by <CODE>event</CODE>
     * to corresponding paths in other trees, and set 
     * them to selection.
     *
     * @param event <CODE>TreeSelectionEvent</CODE> in one
     * of synchronized trees
     */
    public void valueChanged(TreeSelectionEvent event)
    {
	if(rowMapper == null ||
	   getEventSource() != null) {
	    return;
	}

	setEventSource(event.getSource());

	TreePath path = event.getPath();
	TreePath[] paths = event.getPaths();

	if(path == null) {
	    clearSelection();
	    setEventSource(null);
	    return;
	}

	int[] rows = rowMapper.getRowsForPaths(paths);


	if(rows.length == 0) {
	    setEventSource(null);
	    return;
	}

	if(paths.length == 1) {
	    if(event.isAddedPath()) {
		setSelectionInterval(rows[0], rows[0]);
	    }
	    else
		removeSelectionInterval(rows[0], rows[0]);
	    setEventSource(null);
	    return;
	}

	List<TreePath> selected = new ArrayList<TreePath>();
	List<TreePath> unselected = new ArrayList<TreePath>();

	for(int i = 0; i < paths.length; i++) {
	    path = paths[i];
	    if(event.isAddedPath(paths[i]))
		selected.add(path);
	    else
		unselected.add(path);
	}

	paths = new TreePath[unselected.size()];
	for(int i = 0; i < paths.length; i++) {
	    paths[i] = unselected.get(i);
	}

	rows = getRowRanges(rowMapper.getRowsForPaths(paths));
	if(rows != null) {
	    for(int i = 0; i < rows.length; i += 2 ) {
		removeSelectionInterval(rows[i],
					rows[i + 1]);
	    }
	}
	    
	paths = new TreePath[selected.size()];
	for(int i = 0; i < paths.length; i++) {
	    paths[i] = selected.get(i);
	}

	rows = getRowRanges(rowMapper.getRowsForPaths(paths));
	if(rows != null) {
	    for(int i = 0; i < rows.length; i += 2 ) {
		addSelectionInterval(rows[i],
					   rows[i + 1]);
	    }
	}
	
	setEventSource(null);
    }

    protected int[] getRowRanges(int[] rows)
    {
	if(rows == null || rows.length == 0)
	    return null;

	if(rows.length == 1)
	    return new int[]{rows[0],  rows[0]};

	Arrays.sort(rows);
	List<Integer> selections = new ArrayList<Integer>();
	int begin = rows[0];
	int previous = begin;
	for(int i = 1; i < rows.length; i++) {
	    if(rows[i] != previous + 1) {
		selections.add(Integer.valueOf(begin));
		selections.add(Integer.valueOf(previous));
		begin = rows[i];
	    }
	    previous = rows[i];
	}

	if(selections.isEmpty())
	    return null;
	
	rows = new int[selections.size()];
	for(int i = 0; i < selections.size(); i++) {
	    rows[i] = selections.get(i).intValue();
	    i++;

	    rows[i] =selections.get(i).intValue();
	}

	selections.clear();
	return rows;
    }

    protected void scrollTo(int index)
    {
	if(table == null ||
	   index < 0 || index > table.getRowCount())
	    return;

	Container pane = table.getParent();
        if (!(pane instanceof JViewport))
	    return;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return;

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	Rectangle view = viewport.getViewRect();

	int h = table.getHeight();
	if(h < view.height)
	    return;

	int y = 0;
	for(int i = 0; i < index; i++)
	    y += table.getRowHeight(i);

	if(y < view.height/2)
	    return;

	y -= view.height/2;

	Point p = viewport.getViewPosition(); 
	p.y = y;
	viewport.setViewPosition(p);

    }

    /*
    protected void fireValueChanged(int firstIndex, int lastIndex, boolean isAdjusting)
    {
	//if(getEventSource() == null)
	    super.fireValueChanged(firstIndex, lastIndex, isAdjusting);
    }

    public void addListSelectionListener(ListSelectionListener listener)
    {
	super.addListSelectionListener(listener);
    }
    */
}
