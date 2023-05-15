/*
 * DualTreeSelectionModel.java:  a TreeModel representing a flip tree.
 *
 * Copyright (c) 2022 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */


package org.nomencurator.gui.swing.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Vector;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.TreeUI;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.FlippableTree;

import org.nomencurator.gui.swing.plaf.FlippableTreeUI;

/**
 * {@code DualTreeSelectionModel} is a tree selection
 * broaker between two {@code TreeSelectionModel}s 
 * behind {@code DualTree}.
 *
 * @version 	01 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class DualTreeSelectionModel
    extends DefaultTreeSelectionModel
    implements PropertyChangeListener, TreeSelectionListener
{
    protected TreeSelectionModel upperSelection;
    protected TreeSelectionModel lowerSelection;

    protected FlippableTree upperTree;
    protected FlippableTree lowerTree;    

    protected boolean dispaching = false;

    protected static final TreePath[] zeroPath = new TreePath[0];

    protected static final int[] zeroRow = new int[0];    

    public DualTreeSelectionModel()
    {
	this(null);
    };

    public DualTreeSelectionModel(RowMapper mapper)
    {
	super();
	setRowMapper(mapper);
    };

    public FlippableTree getUpperTree()
    {
	return upperTree;
    }

    public void setUpperTree(FlippableTree tree)
    {
	if (upperTree == tree) return;
	if (upperTree != null) removeListener(upperTree);
	upperTree = tree;
	if (upperTree != null) addListener(upperTree);	
    }

    public FlippableTree getLowerTree()
    {
	return lowerTree;
    }

    public void setLowerTree(FlippableTree tree)
    {
	if (lowerTree == tree) return;
	if (lowerTree != null) removeListener(lowerTree);
	lowerTree = tree;
	if (lowerTree != null) addListener(lowerTree);	
    }

    protected void addListener(FlippableTree tree)
    {
    }

    protected void removeListener(FlippableTree tree)
    {
    }

    protected TreeUI getUI(FlippableTree tree)
    {
	return (tree == null) ? null : tree.getUI();
    }

    protected TreeUI getUpperUI()
    {
	return getUI(getUpperTree());
    }
    
    protected TreeUI getLowerUI()
    {
	return getUI(getLowerTree());
    }
    
    protected FlippableTreeUI getFlippableTreeUI(FlippableTree tree)
    {
	return (tree == null) ? null : tree.getFlippableTreeUI();
    }
    
    protected TreeModel getTreeModel(FlippableTree tree)
    {
	return (tree == null) ? null : tree.getModel();
    }

    protected TreeModel getUpperTreeModel()
    {
	return getTreeModel(getUpperTree());
    }
    
    protected TreeModel getLowerTreeModel()
    {
	return getTreeModel(getLowerTree());
    }
    
    
    protected TreeSelectionModel getSelectionModel(FlippableTree tree)
    {
	return (tree == null) ? null : tree.getSelectionModel();
    }
    

    public TreeSelectionModel getUpperSelectionModel()
    {
	return getSelectionModel(getUpperTree());
    }
    
    public synchronized void setUpperSelectionModel(TreeSelectionModel model)
    {
	if (upperSelection != null) {
	    upperSelection.removeTreeSelectionListener(this);
	    upperSelection.removePropertyChangeListener(this);	    
	}

	upperSelection = model;

	if (upperSelection != null) {
	    upperSelection.addTreeSelectionListener(this);
	    upperSelection.addPropertyChangeListener(this);	    
	}

    }
    
    public TreeSelectionModel getLowerSelectionModel()
    {
	return getSelectionModel(getLowerTree());
    }
    
    public synchronized void setLowerSelectionModel(TreeSelectionModel model)
    {
	if (lowerSelection != null) {
	    lowerSelection.removeTreeSelectionListener(this);
	    lowerSelection.removePropertyChangeListener(this);	    
	}

	lowerSelection = model;

	if (lowerSelection != null) {
	    lowerSelection.addTreeSelectionListener(this);
	    lowerSelection.addPropertyChangeListener(this);	    
	}

    }


    protected String ignoreChangeEventName = null;

    public void setSelectinoMode(int mode)
    {
	super.setSelectionMode(mode);
	synchronized(this) {
	    ignoreChangeEventName = SELECTION_MODE_PROPERTY;
	}

	TreeSelectionModel model = getLowerSelectionModel();
	if (model != null)
	    model.setSelectionMode(mode);
	model = getUpperSelectionModel();
	if (model != null)
	    model.setSelectionMode(mode);

	synchronized(this) {
	    ignoreChangeEventName = null;
	}
	
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void propertyChange(PropertyChangeEvent event)
    {
	if (event.getPropertyName().equals(ignoreChangeEventName))
	    return;

	if (!event.getPropertyName().equals(SELECTION_MODE_PROPERTY))
	    return;

	setSelectionMode(((Integer)(event.getNewValue())).intValue());
    }


    protected Object eventSource = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void valueChanged(TreeSelectionEvent event)
    {
	synchronized(this) {
	    while (eventSource != null) {}
	    eventSource = event.getSource();
	}

	Vector<TreePath> toAdd = new Vector<TreePath>();
	Vector<TreePath> toRemove = new Vector<TreePath>();
	for (TreePath path : event.getPaths()) {
	    if (event.isAddedPath(path))
		toAdd.add(path);
	    else
		toRemove.add(path);
	}
	TreePath[] pathsToAdd = toAdd.toArray(new TreePath[toAdd.size()]);
	TreePath[] pathsToRemove = toRemove.toArray(new TreePath[toRemove.size()]);
	if (pathsToAdd.length > 0) addSelectionPaths(pathsToAdd);
	if (pathsToRemove.length > 0) removeSelectionPaths(pathsToAdd);
	leadPath = event.getNewLeadSelectionPath();

	synchronized(this) {
	    eventSource = null;
	}
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionPath(TreePath path)
    {
	int row = addSelectionPath(path, getLowerTree());
	if (row < 0)
	    row = addSelectionPath(path, getUpperTree());
    }

    /**
     * Add {@code path} to selections of {@code model}.
     *
     * @param path to add
     * @param model which the {@code path} add to.
     * @return row of the {@code path} if added, or -1.
     */
    protected int addSelectionPath(TreePath path, FlippableTree tree)
    {
	int row = -1;
	if (path != null&& tree != null) {
	    row = getRowForPath(path, tree);
	    if (row > -1)
		getSelectionModel(tree).addSelectionPath(path);
	}
	return row;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionPaths(TreePath[] paths)
    {
	addSelectionPaths(paths, getLowerSelectionModel());
	addSelectionPaths(paths, getUpperSelectionModel());
    }

    protected void addSelectionPaths(TreePath[] paths, TreeSelectionModel model)
    {
	if (paths != null && paths.length > 0 && model != null)
	    model.addSelectionPaths(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection()
    {
	clearSelection(getLowerSelectionModel());
	clearSelection(getUpperSelectionModel());
    }

    protected void clearSelection(TreeSelectionModel model)
    {
	if (model != null) model.clearSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLeadSelectionRow()
    {
	return getRowForPath(getLeadSelectionPath());
    }

    protected int getRowForPath(TreePath path)
    {
	int row = getRowForPath(path, getLowerTree());
	return  (row > -1) ?
	    (row + getRowCount(getUpperTree())) :
	    getRowForPath(path, getUpperTree());
    }
	
    protected int getRowForPath(TreePath path, FlippableTree tree)
    {
	int row = -1;
	if (path != null && tree != null)
	    row = tree.getUI().getRowForPath(tree, path);
	return row;
    }

    protected int getRowCount(FlippableTree tree)
    {
	return (tree == null) ? 0 : tree.getRowCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxSelectionRow()
    {
	int row = getMaxSelectionRow(getLowerSelectionModel());
	return  (row > -1) ? (row + getRowCount(getUpperTree())) :
	    getMaxSelectionRow(getUpperSelectionModel());
    }
    
    protected int getMaxSelectionRow(TreeSelectionModel model)
    {
	return (model == null) ? -1 : model.getMaxSelectionRow();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinSelectionRow()
    {
	int row = getMinSelectionRow(getUpperSelectionModel());
	if (row < 0) {
	    row = getMinSelectionRow(getLowerSelectionModel());
	    if (row > -1)
		row += getRowCount(getUpperTree());
	}
	return row;
    }

    protected int getMinSelectionRow(TreeSelectionModel model)
    {
	return (model == null) ? -1 : model.getMinSelectionRow();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectionCount()
    {
	return getSelectionCount(getLowerSelectionModel()) +
	    getSelectionCount(getUpperSelectionModel());
    }

    protected int getSelectionCount(TreeSelectionModel model)
    {
	return (model == null)? 0 : model.getSelectionCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath 	getSelectionPath()
    {
	TreePath path = getSelectionPath(getUpperSelectionModel());
	return (path != null)? path :
	    getSelectionPath(getLowerSelectionModel());
    }

    protected TreePath getSelectionPath(TreeSelectionModel model)
    {
	return (model == null) ? null : model.getSelectionPath();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath[] getSelectionPaths()
    {
	TreePath[] lowerPaths = getSelectionPaths(getLowerSelectionModel());
	TreePath[] upperPaths = getSelectionPaths(getUpperSelectionModel());
	if (lowerPaths.length == 0)
	    return upperPaths;
	else if (upperPaths.length == 0)
	    return lowerPaths;
	TreePath[] paths = new TreePath[lowerPaths.length + upperPaths.length];
	System.arraycopy(upperPaths, 0, paths, 0, upperPaths.length);
	System.arraycopy(lowerPaths, 0, paths, upperPaths.length, lowerPaths.length);
	return paths;
    }
    
    protected TreePath[] getSelectionPaths(TreeSelectionModel model)
    {
	return (model == null)? zeroPath : model.getSelectionPaths();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getSelectionRows()
    {
	int[] upperRows = getSelectionRows(getUpperSelectionModel());
	int[] lowerRows = getSelectionRows(getLowerSelectionModel());

	if (lowerRows.length == 0)
	    return upperRows;
	else if (upperRows.length == 0)
	    return lowerRows;

	for (int i = 0; i < lowerRows.length; i++)
	    lowerRows[i] += upperRows.length;

	int[] rows = new int[lowerRows.length + upperRows.length];
	System.arraycopy(upperRows, 0, rows, 0, upperRows.length);
	System.arraycopy(lowerRows, 0, rows, upperRows.length, lowerRows.length);

	return rows;
    }

    protected int[] getSelectionRows(TreeSelectionModel model)
    {
	return (model == null) ? zeroRow : model.getSelectionRows();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPathSelected(TreePath path)
    {
	return isPathSelected(path, getUpperSelectionModel()) ||
	    isPathSelected(path, getLowerSelectionModel());
    }

    protected boolean isPathSelected(TreePath path, TreeSelectionModel model)
    {
	return (model == null) ? false : model.isPathSelected(path);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRowSelected(int row)
    {
	return isRowSelected(row, getUpperSelectionModel()) ||
	    isRowSelected(row - getSelectionCount(getUpperSelectionModel()), getLowerSelectionModel());
    }

    protected boolean isRowSelected(int row, TreeSelectionModel model)
    {
	return (model == null) ? false : model.isRowSelected(row);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelectionEmpty()
    {
	return isSelectionEmpty(getUpperSelectionModel()) &&
	    isSelectionEmpty(getLowerSelectionModel());
    }

    protected boolean isSelectionEmpty(TreeSelectionModel model)
    {
	return (model == null) ? true : model.isSelectionEmpty();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionPath(TreePath path)
    {
	removeSelectionPath(path, getUpperSelectionModel());
	removeSelectionPath(path, getLowerSelectionModel());
    }

    protected void removeSelectionPath(TreePath path, TreeSelectionModel model)
    {
	if (model != null) model.removeSelectionPath(path);
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionPaths(TreePath[] paths)
    {
	removeSelectionPaths(paths, getUpperSelectionModel());
	removeSelectionPaths(paths, getLowerSelectionModel());
    }

    protected void removeSelectionPaths(TreePath[] paths, TreeSelectionModel model)
    {
	if (model != null) model.removeSelectionPaths(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void resetRowSelection()
    {
	resetRowSelection(getUpperSelectionModel());
	resetRowSelection(getLowerSelectionModel());
    }

    protected void resetRowSelection(TreeSelectionModel model)
    {
	if (model != null) model.resetRowSelection();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowMapper(RowMapper rowMapper)
    {
	super.setRowMapper(rowMapper);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionMode(int mode)
    {
	setSelectionMode(mode,getUpperSelectionModel());
	setSelectionMode(mode,getLowerSelectionModel());
	super.setSelectionMode(mode);
    }

    protected void setSelectionMode(int mode, TreeSelectionModel model)
    {
	if (model == null) return;
	switch (mode) {
	case SINGLE_TREE_SELECTION:
	case CONTIGUOUS_TREE_SELECTION:
	case DISCONTIGUOUS_TREE_SELECTION:
	    model.setSelectionMode(mode);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionPath(TreePath path)
    {
	setSelectionPath(path, getUpperSelectionModel());
	setSelectionPath(path, getLowerSelectionModel());
    }

    protected void setSelectionPath(TreePath path, TreeSelectionModel model)
    {
	if (model != null) model.setSelectionPath(path);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionPaths(TreePath[] paths)
    {
	setSelectionPaths(paths, getUpperSelectionModel());
	setSelectionPaths(paths, getLowerSelectionModel());
    }

    /**
     * {@inheritDoc}
     */
    protected void setSelectionPaths(TreePath[] paths, TreeSelectionModel model)
    {
	if (model != null) model.setSelectionPaths(paths);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean arePathsContiguous(TreePath[] paths)
    {
	return super.arePathsContiguous(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canPathsBeAdded(TreePath[] paths)
    {
	return super.canPathsBeAdded(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canPathsBeRemoved(TreePath[] paths)
    {
	return super.canPathsBeRemoved(paths);
    }

    /**
     * {@inheritDoc}
     */
    /*
    @Override
    public Object clone()
    {
	DualTreeSelectionModel clone = (DualTreeSelectionModel) super.clone();
	
	return clone;
    }
    */

    /**
     * {@inheritDoc}
     */
    @Override
    protected void insureRowContinuity()
    {
	super.insureRowContinuity();
    }
}
