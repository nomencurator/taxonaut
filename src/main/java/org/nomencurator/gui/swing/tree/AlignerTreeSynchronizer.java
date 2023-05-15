/*
 * AlignerTreeSynchronizer.java: synchronize AlignerTrees
 *
 * Copyright (c) 2003, 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.AlignerTree;
import org.nomencurator.gui.swing.ViewportSynchronizer;

/**
 * <tt>AlignerTreeSynchronizer</tt> synchronizes AlignerTrees  to align nodes of  trees
 *
 * @version 	23 Oct. 2015
 * @author 	Nozomi `James' Ytow
 */
public class AlignerTreeSynchronizer
    implements TreeExpansionListener,
	       TreeSelectionListener

{
    protected TreeModel treeModel;

    protected Object eventSource;

    protected Set<AlignerTree> trees;

    protected boolean scrollSynchronize;

    protected ViewportSynchronizer viewports;

    protected static int count = 0;

    public AlignerTreeSynchronizer()
    {
	this(null);
    }

    public AlignerTreeSynchronizer(TreeModel model)
    {
	setModel(model);
	scrollSynchronize = true; //REVISIT
    }

    public synchronized void setModel(TreeModel model)
    {
	if(treeModel == model)
	    return;

	if(treeModel != null &&
	   trees != null) {
	    Iterator<AlignerTree> t = trees.iterator();
	    while(t.hasNext()) {
		t.next().removeTreeExpansionListener(this);
	    }
	    trees.clear();
	    trees = null;
	}

	treeModel = model;
    }

    public void add(AlignerTree tree)
    {
	if(tree == null)
	    return;

	if(trees == null) {
	    trees = new HashSet<AlignerTree>();
	    viewports = 
		new ViewportSynchronizer
		    (ViewportSynchronizer.SYNCHRONIZE_Y, isScrollSynchronized());
	}

	if(trees.contains(tree))
	    return;

	if(treeModel == null)
	    setModel(tree.getModel());
	else if(treeModel != tree.getModel()) {
	    return;
	}

	trees.add(tree);
	/*
	viewports.add(tree);
	ViewportSynchronizer.getViewport(tree).addChangeListener(viewports);
	*/

	tree.addTreeExpansionListener(this);
	tree.setSynchronize(true);
	tree.addTreeSelectionListener(this);
	tree.setSelectionSynchronize(true);
    }

    public void remove(AlignerTree tree)
    {
	if(tree == null ||
	   trees == null||
	   !trees.contains(tree))
	    return;
	/*
	ViewportSynchronizer.getViewport(tree).removeChangeListener(viewports);
	viewports.remove(tree);
	*/
	tree.removeTreeSelectionListener(this);
	tree.removeTreeExpansionListener(this);
	trees.remove(tree);
	if(trees.isEmpty()) {
	    viewports = null;
	    trees = null;
	    setModel(null);
	}
    }

    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    protected Object getEventSource()
    {
	return eventSource;
    }


    public void treeCollapsed(TreeExpansionEvent event)
    {
	Object source = event.getSource();
	if(getEventSource() != null ||
	   trees == null ||
	   trees.isEmpty())
	    return;

	setEventSource(event.getSource());

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();

	    if(getEventSource() == tree)
		continue;

	    if(tree.isSynchronized()) {
		tree.collapsePath(event.getPath());
	    }
	}

	setEventSource(null);
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
	Object source = event.getSource();
	synchronized (this) {
	    if(getEventSource() != null ||
	       trees == null ||
	       trees.isEmpty())
		return;

	    setEventSource(event.getSource());
	}

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    /*
	    if(getEventSource() == tree)
		continue;
	    */
	    if(tree.isSynchronized()) {
		tree.expandPath(event.getPath());
	    }
	}

	synchronized (this) {
	    setEventSource(null);
	}
    }


    public void valueChanged(TreeSelectionEvent event)
    {
	Object source = event.getSource();
	if(getEventSource() != null ||
	   trees == null ||
	   trees.isEmpty()) {
	    return;
	}
	setEventSource(event.getSource());

	TreePath path = event.getPath();
	TreePath[] paths = event.getPaths();

	if(path == null) {
	    clearSelection();
	}
	else if(paths.length == 1) {
	    if(event.isAddedPath()) {
		clearSelection();
		setSelectionPath(path);
	    }
	    else {
		removeSelectionPath(path);
	    }
	}
	else {
	    final List<TreePath> selected = new ArrayList<TreePath>();
	    final List<TreePath> unselected = new ArrayList<TreePath>();
	    
	    for(TreePath treePath : paths) {
		if(event.isAddedPath(treePath))
		    selected.add(treePath);
		else
		    unselected.add(treePath);
	    }
	    
	    removeSelectionPaths(unselected.toArray(new TreePath[unselected.size()]));
	    addSelectionPaths(selected.toArray(new TreePath[selected.size()]));
	}
	setEventSource(null);
    }

    public void clearSelection()
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.clearSelection();
	}
    }

    /**
     * Sets selection to <CODE>path</CODE>.
     *
     * @param path <CODE>TreePath</CODE> to be set
     * as new selection
     */
    public void setSelectionPath(TreePath path)
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.setSelectionPath(path);
	}
    }


    /**
     * Sets selection to <CODE>paths</CODE>.
     *
     * @param paths an array of <CODE>TreePath</CODE> to be set
     * as new selection
     */
    public void setSelectionPaths(TreePath[] paths)
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.setSelectionPaths(paths);
	}
    }

    /**
     * Adds <CODE>paths</CODE> to selection.
     *
     * @param paths an array of <CODE>TreePath</CODE> to be add
     * to selection
     */
    public void addSelectionPaths(TreePath[] paths)
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.addSelectionPaths(paths);
	}
    }

    /**
     * Removes <CODE>paths</CODE> from selection.
     *
     * @param paths an array of <CODE>TreePath</CODE> to be removed
     * from selection
     */
    public void removeSelectionPath(TreePath path)
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.removeSelectionPath(path);
	}
    }
    /**
     * Removes <CODE>paths</CODE> from selection.
     *
     * @param paths an array of <CODE>TreePath</CODE> to be removed
     * from selection
     */
    public void removeSelectionPaths(TreePath[] paths)
    {
	if(trees == null)
	    return;

	Iterator<AlignerTree> t = trees.iterator();
	while(t.hasNext()) {
	    AlignerTree tree = t.next();
	    if(getEventSource() == tree)
		continue;
	    if(tree.isSelectionSynchronized())
		tree.removeSelectionPaths(paths);
	}
    }

    public synchronized void setScrollSynchronize(boolean toSynchronize )
    {
	if(scrollSynchronize == toSynchronize)
	    return;

	scrollSynchronize = toSynchronize;
	/*
	if(viewports != null)
	    viewports.setScrollSynchronize(scrollSynchronize);
	*/
    }

    public boolean isScrollSynchronized()
    {
	return scrollSynchronize;
    }

}
