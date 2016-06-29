/*
 * SynchronizedTreeSelectionModel.java:  a synchronized TreeSelectionModel
 *
 * Copyright (c) 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;

import java.io.PrintStream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JViewport;

import javax.swing.table.TableModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.AlignerTree;
import org.nomencurator.gui.swing.NameTree;
import org.nomencurator.gui.swing.ViewportSynchronizer;

import org.nomencurator.gui.swing.tree.NameTreeModel;

import org.nomencurator.util.StackTracer;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code SynchronizedTreeSelectionModel} synchronizes
 * selections of tree nodes in trees
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class SynchronizedTreeSelectionModel
    extends DefaultTreeSelectionModel
    implements Alignable,
	       ChangeListener,
	       TreeSelectionListener
{
    private static final long serialVersionUID = 3954105655194822583L;

    /**
     * {@code NodeMapper} to map each node in a 
     * united tree with nodes in trees.
     */
    protected NodeMapper nodeMapper;

    protected TableModel tableModel;

    protected AlignerTree aligner;
    /**
     * {@code Set} of {@code JTree}s
     * to be synchronized
     */
    //protected Hashtable trees;
    protected Set<JTree> trees;

    /**
     * {@code Object} made the selection.  It is
     * used to control {@code TreeSelectionEvent}
     * handling.
     */
    protected Object eventSource;

    protected JTable table;

    protected ViewportSynchronizer viewports;

    protected boolean selectAndScroll;

    /** true to synchronize scrolling of viewports */
    protected boolean scrollSynchronize;

    /** true to select the second best if strict selection is unavailable */
    @Getter
    @Setter
    protected boolean relaxedSelection;



    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object with a mapping specified by {@code mapper}.
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper)
    {
	this(mapper, null);
    }

    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object using <tt>mapper<tt> and <tt>model</tt>.
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     * @param mode <tt>TableModel</tt> representing a table to select
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper,
					  TableModel model)
    {
	this(mapper, model, null);
    }

    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object using <tt>mapper<tt>, <tt>model</tt> and <tt>aligner</tt>.
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     * @param mode <tt>TableModel</tt> representing a table to select
     * @param aligner <tt>AlignerTree</tt> to synchronize
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper,
					  TableModel model,
					  AlignerTree aligner)
    {
	this(mapper, model, aligner, true);
    }

    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object using <tt>mapper<tt>, <tt>model</tt>, <tt>aligner</tt> and
     * <tt>scrollOnSelection</tt>
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     * @param mode <tt>TableModel</tt> representing a table to select
     * @param aligner <tt>AlignerTree</tt> to synchronize
     * @param scrollOnSelection true to scroll showing the node when selected
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper,
					  TableModel model,
					  AlignerTree aligner,
					  boolean scrollOnSelection)
    {
	this(mapper, model, aligner, scrollOnSelection, false);
    }

    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object using <tt>mapper<tt>, <tt>model</tt>, <tt>aligner</tt>,
     * <tt>scrollOnSelection</tt> and <tt>scrollSynchronize</tt>.
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     * @param mode <tt>TableModel</tt> representing a table to select
     * @param aligner <tt>AlignerTree</tt> to synchronize
     * @param scrollOnSelection true to scroll showing the node when selected
     * @param synchronizedScroll true to synchronize scroll between views.
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper,
					  TableModel model,
					  AlignerTree aligner,
					  boolean scrollOnSelection,
					  boolean synchronizedScroll)
    {
	this(mapper, model, aligner, scrollOnSelection, synchronizedScroll, false);
    }

    /**
     * Constracts {@code SynchronizedTreeSelectionModel}
     * object using <tt>mapper<tt>, <tt>model</tt>, <tt>aligner</tt>,
     * <tt>scrollOnSelection</tt>, <tt>scrollSynchronize</tt> and
     * <tt>relaxedSelection</tt>.
     *
     * @param mapper {@code NodeMapper} provides
     * node mapping between trees, or null to disable
     * mapping
     * @param mode <tt>TableModel</tt> representing a table to select
     * @param aligner <tt>AlignerTree</tt> to synchronize
     * @param scrollOnSelection true to scroll showing the node when selected
     * @param synchronizedScroll true to synchronize scroll between views.
     * @param relaxedSelection true to allow relaxed seleciton, i.e. select second best
     * item when strictly matched item is unavalbale.
     */
    public SynchronizedTreeSelectionModel(NodeMapper mapper,
					  TableModel model,
					  AlignerTree aligner,
					  boolean scrollOnSelection,
					  boolean synchronizedScroll,
					  boolean relaxedSelection)
    {
	super();
	setSelectionMode(DISCONTIGUOUS_TREE_SELECTION);
	setNodeMapper(mapper);
	setTableModel(model);
	setAligner(aligner);
	setSelectAndScroll(scrollOnSelection);
	setScrollSynchronize(synchronizedScroll);
	setRelaxedSelection(relaxedSelection);
	// addTreeSelectionListener(this);
    }

    /**
     * Sets {@code mapper} as mapping between nodes
     *
     * @param mapper {@code NodeMapper} to be used 
     * as mapping between nodes, or null ot disable mapping
     */
    public synchronized void setNodeMapper(NodeMapper mapper)
    {
	nodeMapper = mapper;
    }

    /**
     * Returns mapping between nodes
     *
     * @return {@code NodeMapper} representing
     * the mapping between nodes, or null if mapping
     * is disabled
     */
    public NodeMapper getNodeMapper()
    {
	return nodeMapper;
    }

    /**
     * Sets {@code mapper} as mapping between nodes
     *
     * @param mapper {@code NodeMapper} to be used 
     * as mapping between nodes, or null ot disable mapping
     */
    public synchronized void setTableModel(TableModel model)
    {
	tableModel = model;
    }

    /**
     * Returns mapping between nodes
     *
     * @return {@code NodeMapper} representing
     * the mapping between nodes, or null if mapping
     * is disabled
     */
    public TableModel getTableModel()
    {
	return tableModel;
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
     * Sets {@code source} as the `owner' of selection
     * to control event handling
     *
     * @param source {@code Object} represents
     * the `owner' of selection
     */
    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    /**
     * Returns `owner' {@code Object}  of selection
     * to control event handling
     *
     * @return {@code Object} represents
     * the `owner' of selection
     */
    protected Object getEventSource()
    {
	return eventSource;
    }

    /**
     * Synchronize selections in {@code tree} with
     * this selection.  If {@code tree} is already
     * synchronized with this, the method do nothing.
     *
     * @param tree {@code JTree} of which selection
     * to be synchronized.
     */
    public void addTree(JTree tree)
    {
	synchronized(this) {
	    if(trees == null) {
		//trees = new Hashtable();
		trees = new HashSet<JTree>();
		if(viewports == null)
		    viewports = new ViewportSynchronizer(ViewportSynchronizer.SYNCHRONIZE_Y);
	    }

	    if(trees.contains(tree))
		return;

	    trees.add(tree);
	    viewports.add(tree);
	}

	tree.addTreeSelectionListener(this);
    }

    /**
     * Unsynchronize selections in {@code tree} with
     * this selection.  If {@code tree} is not
     * synchronized with this, the method do nothing.
     *
     * @param tree {@code JTree} of which selection
     * to be unsynchronized.
     */
    public void removeTree(JTree tree)
    {
	synchronized(this) {
	    if(trees == null || !trees.contains(tree))
		return;
	    
	    viewports.remove(tree);

	    trees.remove(tree);
	}

	// if(trees.isEmpty()) trees = null;

	tree.removeTreeSelectionListener(this);
    }

    protected Collection<UnitedNameTreeNode> getUnfiedNodesForPaths(TreePath ... paths)
    {
	if (paths == null || paths.length == 0)
	    return null;
	final Set<UnitedNameTreeNode> unifiedNodes = new  HashSet<UnitedNameTreeNode>();

	for(int i = 0; i < paths.length; i++) {
	    if(paths[i] == null) {
		continue;
	    }
	    unifiedNodes.addAll(nodeMapper.getNodesFor
				   (((NamedNode)paths[i].getLastPathComponent()).getLiteral()));
	}

	return unifiedNodes;
    }

    /**
     * Returns {@code Hashtable} retaining
     * {@code TreePaths} in {@code JTree}
     * corresponding to {@code paths}.
     * Both {@code JTree} synchronized
     * and this object are used as keys.
     *
     * @param paths an array of {@code TreePath}
    * to be mapped
     */
    protected Map<Object, TreePath[]> getPathMap(TreePath ... paths)
    {
	final Map<Object, TreePath[]> pathMap = new HashMap<Object, TreePath[]>(trees.size() + 1);
	if(paths == null) {
	    return pathMap;
	}

	synchronized(this) {
	    if(nodeMapper == null) {
		pathMap.put(this, paths);
		return pathMap;
	    }
	}
	
	return getPathMap(getUnfiedNodesForPaths(paths), paths);
    }


    /**
     * Returns {@code Hashtable} retaining
     * {@code TreePaths} in {@code JTree}
     * corresponding to {@code paths}.
     * Both {@code JTree} synchronized
     * and this object are used as keys.
     *
     * @param paths an array of {@code TreePath}
    * to be mapped
     */
    protected Map<Object, TreePath[]> getPathMap(Collection<UnitedNameTreeNode> unifiedNodes, TreePath ... paths)
    {
	final Map<Object, TreePath[]> pathMap = new HashMap<Object, TreePath[]>(trees.size() + 1);

	if(unifiedNodes == null) {
	    return pathMap;
	}

	synchronized(this) {
	    if(nodeMapper == null) {
		pathMap.put(this, paths);
		return pathMap;
	    }
	}

	TreeNode[] nodes = new TreeNode[unifiedNodes.size()];
	paths = new TreePath[unifiedNodes.size()];
	int i = 0;
	for (UnitedNameTreeNode node : unifiedNodes) {
	    nodes[i] = node;
	    paths[i] = new TreePath(node.getPath());
	    i++;
	}
	pathMap.put(this, paths);
	unifiedNodes.clear();

	for(JTree tree : trees) {

	    /*
	    if(tree == getEventSource())
		continue;
	    */

	    TreeModel model = tree.getModel();
	    i = 0;
	    List<TreePath> buffer = new ArrayList<TreePath>();

	    for(i = 0; i < nodes.length; i++) {
		TreePath path = nodeMapper.getPathFor(nodes[i], model);
		if(path != null) {
		    buffer.add(path);
		}
	    }

	    if(buffer.isEmpty()) {
		Map<TreeNode, TreePath> bestNodes = new HashMap<TreeNode, TreePath>();
		int nearest = Integer.MAX_VALUE;
		for(i = 0; i < nodes.length; i++) {
		    TreeNode node = nodes[i];

		    TreePath path = nodeMapper.getPathFor(node, model);
		    int distance = 0;

		    while(path == null) {
			node = node.getParent();
			if(node == null)
			    break;
			distance++;
			path = nodeMapper.getPathFor(node, model);
		    }
		    if(path == null && node == null) {
			path = new TreePath
			    (((DefaultMutableTreeNode)model.getRoot()).getPath());
		    }
		    if(distance <= nearest) {
			if(distance < nearest) {
			    nearest = distance;
			    bestNodes.clear();
			}
			if(node != null)
			bestNodes.put(node, path); //original
			//bestNodes.put(nodes[i], path); //right? FIXME
		    }
		}

		if(isRelaxedSelection())
		    buffer.addAll(bestNodes.values());
		bestNodes.clear();
	    }
	    if(buffer.isEmpty()) {
		continue;
	    }

	    paths = new TreePath[buffer.size()];
	    paths = buffer.toArray(paths);
	    pathMap.put(tree, paths);
	}
	return pathMap;
    }

    /**
     * Clears current selections in trees.
     */
    public void clearSelection()
    {
	if(trees == null || getEventSource() != null)
	    return;

	if(getEventSource() == null)
	    setEventSource(this);

	for (JTree tree : trees) {
	    if(tree == getEventSource())
		continue;
	    tree.clearSelection();
	}

	super.clearSelection();

	if(getEventSource() == this)
	    setEventSource(null);
    }

    /**
     * Sets selection to {@code path}.
     *
     * @param path {@code TreePath} to be set
     * as new selection
     */
    public void setSelectionPaths(TreePath path)
    {
	if(path != null) {
	    setSelectionPaths(new TreePath[]{path});
	    return;
	}

	if(getEventSource() == null)
	    setEventSource(this);

	for (JTree tree : trees) {
	    if(tree == getEventSource()) {
		scrollTo(tree, path);//?
		continue;
	    }
	    tree.clearSelection();
	    tree.setSelectionPath(path);
	    scrollTo(tree, path);//?
	}
	if(getEventSource() == this)
	    setEventSource(null);
	super.setSelectionPath(path);
    }

    protected String formatPath(TreePath path) {
	StringBuffer buffer = new StringBuffer();
	if(path != null) {
	    for(Object node : path.getPath()) {
		if(buffer.length() > 0)
		    buffer.append(" -> ");
		if(node instanceof NamedNode)
		buffer.append(((NamedNode)node).getRankedName());
	    }
	}
	return buffer.toString();
    }

    protected TreePath[] unifiedPaths(final TreePath[] paths)
    {
	if (paths == null)
	    return null;
	return unifiedPaths(getUnfiedNodesForPaths(paths));
    }

    protected TreePath[] unifiedPaths(final Collection<UnitedNameTreeNode> unifiedNodes)
    {
	if (unifiedNodes == null || unifiedNodes.isEmpty())
	    return null;

	final TreePath[] unifiedPaths = new TreePath[unifiedNodes.size()];
	int j = 0;
	for (UnitedNameTreeNode unifiedNode : unifiedNodes) {
	    unifiedPaths[j++] = new TreePath(unifiedNode.getPath());
	}

	return unifiedPaths;
    }

    /**
     * Sets selection to {@code paths}.
     *
     * @param paths an array of {@code TreePath} to be set
     * as new selection
     */
    public void setSelectionPaths(TreePath[] paths)
    {
	synchronized (this) {
	    if(getEventSource() != null) {
		return ;
	    }
	    setEventSource(this);
	}

	clearSelection();

	if(paths == null) {
	    if(getEventSource() == this)
		setEventSource(null);
	    return;
	}

	final Collection<UnitedNameTreeNode> unifiedNodes = getUnfiedNodesForPaths(paths);
	super.setSelectionPaths(unifiedPaths(unifiedNodes));

	Map<Object, TreePath[]> pathMap = getPathMap(unifiedNodes, paths);

	paths = pathMap.get(this);
	Point p = null;
	if(aligner != null && paths != null) {
	    Object src = getEventSource();
	    setEventSource(this);
	    for(int i = 0; i < paths.length; i++) {
		aligner.makeVisible(paths[i]);
	    }
	    setEventSource(src);
	    p = scrollTo(aligner, paths);
	}
	else
	    p = new Point(0, 0);

	for(JTree tree : trees) {
	    if(tree == aligner)
		continue;

	    paths = pathMap.get(tree);
	    if(paths != null) {
		if(tree == getEventSource()) {
		    scrollTo(tree, paths, p);
		    continue;
		}
		if (paths != null) {
		    for(TreePath treePath : paths) {
			tree.makeVisible(treePath);
		    }
		}
		scrollTo(tree, paths, p);
	    }
	    tree.clearSelection();
	    tree.getSelectionModel().setSelectionPaths(paths);
	}

	if(getEventSource() == this)
	    setEventSource(null);
    }

    /**
     * Adds {@code paths} to selection.
     *
     * @param paths an array of {@code TreePath} to be add
     * to selection
     */
    public void addSelectionPaths(TreePath[] paths)
    {
	final Collection<UnitedNameTreeNode> unifiedNodes = getUnfiedNodesForPaths(paths);
	super.addSelectionPaths(unifiedPaths(unifiedNodes));

	Map<Object, TreePath[]> pathMap = getPathMap(unifiedNodes, paths);

	paths = pathMap.get(this);
	Point p = null;
	if(aligner != null && paths != null) {
	    Object src = getEventSource();
	    setEventSource(this);
	    for(int i = 0; i < paths.length; i++) {
		aligner.makeVisible(paths[i]);
	    }
	    setEventSource(src);
	    p = scrollTo(aligner, paths);
	}
	else 
	    p = new Point(0, 0);

	for(JTree tree : trees) {
	    paths = pathMap.get(tree);
	    if(tree == getEventSource()) {
		if(paths != null)
		    scrollTo(tree, paths, p);
		continue;
	    }
	    if (paths != null) {
		for(TreePath treePath : paths) {
		    tree.makeVisible(treePath);
		}
	    }
	    tree.getSelectionModel().addSelectionPaths(paths);
	    scrollTo(tree, paths, p);
	}
    }

    /**
     * Removes {@code paths} from selection.
     *
     * @param paths an array of {@code TreePath} to be removed
     * from selection
     */
    public void removeSelectionPaths(TreePath[] paths)
    {
	if(getEventSource() == this)
	    setEventSource(null);
	Map<Object, TreePath[]> pathMap = getPathMap(paths);

	for(JTree tree : trees) {
	    if(tree == getEventSource()) {
		scrollTo(tree, tree.getSelectionPath());
		continue;
	    }
	    tree.getSelectionModel().removeSelectionPaths(pathMap.get(tree));
	    scrollTo(tree, tree.getSelectionPath());
	}
	if(getEventSource() == this)
	    setEventSource(null);
	super.removeSelectionPaths(pathMap.get(this));
	if(aligner != null)
	    scrollTo(aligner, aligner.getSelectionPath());
    }

    /**
     * Notifies {@code event} to listeners
     *
     * @param event {@code TreeSelectionEvent} to be notified
     */
    /*
    protected void fireValueChanged(TreeSelectionEvent event)
    {
	synchronized(this) {
	    if(getEventSource() == null)
		super.fireValueChanged(event);
	}
    }
    */

    /**
     * Converts selection paths in one of synchronized trees
     * represented by {@code event}
     * to corresponding paths in other trees, and set 
     * them to selection.
     *
     * @param event {@code TreeSelectionEvent} in one
     * of synchronized trees
     */
    public void valueChanged(TreeSelectionEvent event)
    {
	synchronized(this) {
	    if(getEventSource() != null)
		return;
	    setEventSource(event.getSource());
	}

	TreePath path = event.getPath();
	TreePath[] paths = event.getPaths();

	if(path == null) {
	    clearSelection();
	    setEventSource(null);
	    return;
	}

	if (event.getSource() == this) {
	}

	if(paths.length == 1) {
	    if(nodeMapper != null) {
		//path = nodeMapper.getPathFor(path);
		paths = nodeMapper.getPathsFor(path);
	    }
	    if(event.isAddedPath()) {
		removeSelectionPaths(getSelectionPaths());
		addSelectionPaths(paths);
	    }
	    else
		removeSelectionPath(path);
	    setEventSource(null);
	    return;
	}

	List<TreePath> selected = new ArrayList<TreePath>();
	List<TreePath> unselected = new ArrayList<TreePath>();

	for(int i = 0; i < paths.length; i++) {
	    path = paths[i];
	    TreePath[] treePaths = null;
	    if(nodeMapper != null)
		treePaths = nodeMapper.getPathsFor(path);
	    if(treePaths != null) {
		List<TreePath> toAdd = (event.isAddedPath(paths[i]))? selected : unselected;
		for (TreePath treePath : treePaths) {
			toAdd.add(treePath);
		}
	    }
	}

	if (!unselected.isEmpty()) {
	    removeSelectionPaths(unselected.toArray(new TreePath[unselected.size()]));
	}

	if (!selected.isEmpty()) {
	    addSelectionPaths(selected.toArray(new TreePath[selected.size()]));
	}

	setEventSource(null);
    }

    public void valueChanged(ListSelectionEvent event)
    {
	if(getEventSource() != null ||
	   event.getValueIsAdjusting() ||
	   tableModel == null)
	    return;
	setEventSource(this);

	setEventSource(event.getSource());
	setSelectionPaths(getPathsFor(event.getFirstIndex(), event.getLastIndex()));
	setEventSource(null);
    }

    protected TreePath[] getPathsFor(int index0, int index1)
    {
	index1++;
	List<TreePath> paths = new ArrayList<TreePath>();
	for(int i = index0; i < index1; i++) {
	    String name =
		(String)tableModel.getValueAt(i, 0);
	    Collection<UnitedNameTreeNode> nodes = nodeMapper.getNodesFor(name);
	    if(nodes == null)
		continue;

	    for(UnitedNameTreeNode node : nodes) {
		paths.add(new TreePath(node.getPath()));
	    }
	}

	return paths.toArray(new TreePath[paths.size()]);
    }

    public Aligner getAligner()
    {
	return aligner;
    }


    /**
     * Sets {@code aligner} as the
     * {@code Aligner} to align nodes, 
     * or null to disable node alignment
     *
     * @param aligner {@code Aligner} to align nodes,
     * or null to disable node alignment
     */
    public synchronized void setAligner(Aligner aligner)
    {
	if(this.aligner == aligner)
	    return;

	if(aligner != null && 
	   !(aligner instanceof AlignerTree))
	    return;

	if(this.aligner != null &&
	   viewports != null)
	    viewports.remove(this.aligner);

	this.aligner = (AlignerTree)aligner;
	if(this.aligner == null)
	    return;

	if(viewports == null)
	    viewports = new ViewportSynchronizer(ViewportSynchronizer.SYNCHRONIZE_Y);

	viewports.add(this.aligner);
    }

    public synchronized void setSelectAndScroll(boolean toScroll)
    {
	selectAndScroll = toScroll;
    }

    public boolean isSelectAndScroll()
    {
	return selectAndScroll;
    }

    public Point scrollTo(JTree tree, Point p)
    {
        Container pane = tree.getParent();
        if (!(pane instanceof JViewport))
	    return p;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return p; 

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	viewport.setViewPosition(p);
	/*
	if(table != null)
	    table.repaint();
	*/

	return p;
    }

    public Point scrollTo(JTree tree, TreePath path, Point p)
    {
        Container pane = tree.getParent();
        if (!(pane instanceof JViewport))
	    return p;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return p; 

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	Rectangle view = viewport.getViewRect();

	Rectangle cell = tree.getPathBounds(path);

	//p = new Point(cell.x - (tree.getWidth() - cell.width)/2, p.y);
	p = new Point(tree.getWidth() - cell.width, p.y);

	viewport.setViewPosition(p);
	/*
	if(table != null)
	    table.repaint();
	*/

	return p;
    }

    public Point scrollTo(JTree tree, TreePath path)
    {
	if(path == null)
	    return null;

	if(!isSelectAndScroll()) {
	    tree.scrollPathToVisible(path);
	    return null;
	}

	tree.makeVisible(path);

        Container pane = tree.getParent();
        if (!(pane instanceof JViewport))
	    return null;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return null;

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	Rectangle view = viewport.getViewRect();

	Rectangle cell = tree.getPathBounds(path);
	/*
	if(cell == null)
	    return; 
	*/

	int y = cell.y - (view.height - cell.height)/2;
	/*
	if(y < 0)
	    y = 0;
	*/
	int x = cell.x - (view.width - cell.width)/2;
	/*
	if(x < 0)
	    x = 0;
	*/

	Point p = new Point(cell.x, y);
	//Point p = new Point(-x, -y);
	//Point p = new Point(x, y);
	/*
	Point p = viewport.getViewPosition();
	p.x = x;
	viewport.setViewPosition(p);
	p = viewport.getViewPosition();
	p.y = y;
	*/

	//Dimension d = 
	viewport.setViewPosition(p);
	/*
	if(table != null)
	    table.repaint();
	*/
	//tree.setLocation(p);
	((JScrollPane)pane).getHorizontalScrollBar().setValue(x);
	pane.getParent().repaint();
	return viewport.getViewPosition(); 
	//return p;
    }

    public Point scrollTo(JTree tree, TreePath[] paths)
    {
	return scrollTo(tree, paths, null);
    }

    public Point scrollTo(JTree tree, TreePath[] paths, Point p)
    {
	if(paths == null)
	    return scrollTo(tree, p);

	if(paths == null || paths.length < 1)
	    return null;

	if(paths.length == 1) {
	    return scrollTo(tree, paths[0]);
	}

        Container pane = tree.getParent();
        if (!(pane instanceof JViewport))
	    return null;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return null;

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	Rectangle view = viewport.getViewRect();

	Rectangle[] cells = new Rectangle[paths.length];
	int minX = Integer.MAX_VALUE;
	int maxX = Integer.MIN_VALUE;
	int maxXEnd = Integer.MIN_VALUE;
	int minXIndex = -1;
	int maxXIndex = -1;
	int minY = Integer.MAX_VALUE;
	int maxY = Integer.MIN_VALUE;
	int minYIndex = -1;
	int maxYIndex = -1;

	for(int i = 0; i < paths.length; i++) {
	    if(paths[i] == null)
		continue;
	    tree.makeVisible(paths[i]);
	}

	int nonNullCells = 0;
	for(int i = 0; i < paths.length; i++) {
	    if(paths[i] == null)
		continue;

	    //tree.makeVisible(paths[i]);
	    cells[i] = tree.getPathBounds(paths[i]);
	    if(cells[i] == null)
		continue;

	    nonNullCells++;

	    //cells[i].y -= (view.height - cells[i].height)/2;
	    if(minY > cells[i].y) {
		minY = cells[i].y;
		minYIndex = i;
	    }

	    if(maxY < cells[i].y) {
		maxY = cells[i].y;
		maxYIndex = i;
	    }

	    //cells[i].x -= (view.width - cells[i].width)/2;
	    if(minX > cells[i].x) {
		minX = cells[i].x;
		minXIndex = i;
	    }

	    if(maxX < cells[i].x) {
		maxX = cells[i].x;
		maxXIndex = i;
	    }

	    int endX = cells[i].x + cells[i].width;
	    if(maxXEnd < endX) {
		maxXEnd = endX;
	    }
	}

	if(nonNullCells == 0) {
	    return null;
	}

	int treeWidth = tree.getWidth();

	if(p != null) {
	    if(nonNullCells != 0) {
		/*
		if(treeWidth > 0 && minX + view.width > treeWidth)
		    minX = treeWidth - view.width;
		*/

		if(minY >= p.y && minY < p.y + view.height)
		    minY = p.y;
		else
		    minY -= (view.height - cells[minYIndex].height)/2;

		p = new Point(minX, minY);

		if(maxXEnd != minX) {
		    p.x = (maxXEnd + minX - view.width)/2;
		}
		else
		  p.x = minX;
	    }
	}
	else {
	    p = new Point();

	    //maxY += cells[maxYIndex].height;
	    
	    if(nonNullCells == 1) {
		p.x = minX - (view.width - cells[minXIndex].width)/2;
		p.y = minY - (view.height - cells[minYIndex].height)/2;
	    }
	    else if((maxY + cells[maxYIndex].height - minY) < 
		    view.height && maxY != minY) {
		p.y = (maxY + minY + cells[minYIndex].height - view.height)/2;

		if(maxXEnd != minX) {
		    p.x = (maxXEnd + minX - view.width)/2;
		}
		else
		  p.x = minX;
	    }
	    else {
		p.y = minY - (view.height - cells[minYIndex].height)/2;
		if(maxXEnd != minX)
		    p.x = (maxXEnd + minX - view.width)/2;
		else
		    p.x = minX - (view.width - cells[minXIndex].width)/2;
	    }
	    /*
	    if(treeWidth > 0 && p.x + view.width > treeWidth)
		p.x = treeWidth - view.width;
	    */
	}

	viewport.setViewPosition(p);
	/*
	if(table != null)
	    table.repaint();
	*/
	return viewport.getViewPosition();
    }

    public synchronized void setScrollSynchronize(boolean toSynchronize )
    {
	if(scrollSynchronize == toSynchronize)
	    return;

	scrollSynchronize = toSynchronize;
	if(viewports != null) {
	    viewports.setScrollSynchronize(scrollSynchronize);
	    //viewports.add(aligner);
	}
    }

    public boolean isScrollSynchronized()
    {
	return scrollSynchronize;
    }

    public void stateChanged(ChangeEvent event)
    {
	if(viewports != null)
	    viewports.stateChanged(event);
    }

    public ViewportSynchronizer getViewportSynchronizer()
    {
	return viewports;
    }

}
