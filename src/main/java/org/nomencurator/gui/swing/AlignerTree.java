/*
 * AlignerTree.java:  a JTree to align multiple JTrees' nodes
 *
 * Copyright (c) 2003, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import com.sun.java.swing.plaf.motif.MotifTreeUI;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.UIManager;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;

import javax.swing.plaf.TreeUI;

import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.metal.MetalTreeUI;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryManager;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.QueryResultListenerAdaptor;
import org.nomencurator.io.QueryMode;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.event.UnitedNameTreeModelEvent;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelListener;

import org.nomencurator.gui.swing.plaf.basic.BasicAlignerTreeUI;
import org.nomencurator.gui.swing.plaf.metal.MetalAlignerTreeUI;
import org.nomencurator.gui.swing.plaf.motif.MotifAlignerTreeUI;
import org.nomencurator.gui.swing.plaf.windows.WindowsAlignerTreeUI;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.NameTreeCellEditor;
import org.nomencurator.gui.swing.tree.NameTreeCellRenderer;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NodeMapper;
import org.nomencurator.gui.swing.tree.SynchronizedTreeSelectionModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;


import lombok.Getter;
import lombok.Setter;

/**
 * {@code AlingerTree} provides node alignment
 * of nodes
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class AlignerTree
    extends GenericAlignerTree<NameUsage<?,?>>
{
    private static final long serialVersionUID = 5069891953117193095L;

    public AlignerTree() 
    {
	super();
    }
    
    public AlignerTree(Locale locale) 
    {
	super(locale);
    }
    
    public AlignerTree(UnitedNameTreeModel model)
    {
	super(model);
    }
    
    public AlignerTree(UnitedNameTreeModel model, Locale locale)
    {
	super(model, locale);
    }

    public void addTree(NameTree tree)
    {
	synchronized(this) {
	    if(trees == null)
		trees = new HashSet<NameTree>();
	}
	
	synchronized(trees) {
	    if(trees.contains(tree))
		return;
	}

	TreeModel m = tree.getModel();

	add(m);

	//tree.setAlignerTree(this);
	tree.setAligner(this);
	for(int i = tree.getRowCount(); i > 0; ) {
	    expandPath(((NodeMapper)getModel()).getPathFor(tree.getPathForRow(--i)));
	}

	tree.addTreeWillExpandListener(this);
	//tree.addTreeExpansionListener(this);

	for(int i = getRowCount() - 1; i > 0; ) {
	    tree.expandPath(((NodeMapper)getModel()).getPathFor(getPathForRow(--i), m));
	}

	((SynchronizedTreeSelectionModel)getSelectionModel()).addTree(tree);

	trees.add(tree);
    }
}

abstract class GenericAlignerTree<N extends NameUsage<?,?>>
    extends JTree
    implements Aligner, 
	       //FIXME
	       // NameUsageExchanger<N, N>,
	       NameQuery,
	       NamedTree,
	       NodeMapper,
	       TreeExpansionListener,
	       TreeWillExpandListener,
	       PropertyChangeListener,
	       QueryResultListener<N, N>,
	       UnitedNameTreeModelListener
{
    private static final long serialVersionUID = -7876406468599943379L;

    private static final String uiClassID = "AligherTreeUI";

    /* Set user interface class ID according to current default look and feel*/
    protected static void putUI()
    {
	if(LookAndFeelManager.isWindows())
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.windows.WindowsAlignerTreeUI");
	else if (LookAndFeelManager.isMotif())
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.motif.MotifAlignerTreeUI");
	else
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.metal.MetalAlignerTreeUI");
    }

    static {
	putUI();
    }

    public void propertyChange(PropertyChangeEvent event) 
    {
	if(LookAndFeelManager.isLookAndFeelEvent(event))
	    putUI();
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    protected static GenericAlignerTree<?> lookAndFeelListener = null;

    protected Set<NameTree> trees;

    protected Object eventSource;

    protected boolean toSynchronize;

    protected boolean selectionSynchronize;

    protected static int count = 0;
    /*
    protected String recordMessage;
    protected String recordsMessage;
    protected String foundForMessage;
    protected String notFoundForMessage;
    */
    protected QueryMessages messages;

    protected QueryResultListenerAdaptor<N, N> adaptor;

    protected NamedTreeAdaptor nameAdaptor;

    @Getter
    @Setter
    protected NameUsageQueryManager<N> queryManager;

    public JTree getTree()
    {
	return this;
    }

    public void setTreeName(String name)
    {
	if(nameAdaptor == null)
	    nameAdaptor = new NamedTreeAdaptor(this);
	nameAdaptor.setTreeName(name);
    }
    
    public String getTreeName()
    {
	if(nameAdaptor == null)
	    nameAdaptor = new NamedTreeAdaptor(this);
	return nameAdaptor.getTreeName();
    }

    public GenericAlignerTree() 
    {
	this(new UnitedNameTreeModel());
    }
    
    public GenericAlignerTree(Locale locale) 
    {
	this(new UnitedNameTreeModel(), locale);
    }
    
    public GenericAlignerTree(UnitedNameTreeModel model)
    {
	this(model, Locale.getDefault());
    }
    
    public GenericAlignerTree(UnitedNameTreeModel model, Locale locale)
    {
	super(model);

	//new Error().printStackTrace();

	initializeLocalVars();

	if(lookAndFeelListener == null) {
	    UIManager.addPropertyChangeListener(this);
	    lookAndFeelListener = this;
	}

	setLocale(locale);
	model.addUnitedNameTreeModelListener(this);
	// FIXME 20141121
	// model.addQueryResultListener(this);
    }

    protected void initializeLocalVars()
    {
	setRootVisible(true);
	setShowsRootHandles(true);

	((Aligner)getUI()).setMapper((NodeMapper)getModel());

	setCellRenderer(NameTree.renderer);
	setSelectionModel(createSelectionModel());
	setSynchronize(false);
	setToolTipText("");

	adaptor = new QueryResultListenerAdaptor<N, N>();

	setLocale(getLocale());
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();

	super.setLocale(locale);

	String alignerLabel = ResourceKey.ALIGNER_LABEL;
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    alignerLabel = resource.getString(alignerLabel);
	    if (alignerLabel == null)
		alignerLabel = ResourceKey.ALIGNER_LABEL;
	}
	catch (Throwable e) {
	}

	setTreeName(alignerLabel);

	if(messages == null)
	    messages = new QueryMessages(locale);
	else
	    messages.setLocale(locale);
    }

    protected TreeSelectionModel createSelectionModel()
    {
	SynchronizedTreeSelectionModel selection = 
	    new SynchronizedTreeSelectionModel((UnitedNameTreeModel)getModel());
	selection.setAligner(this);
	return selection;
    }

    public void setModel(TreeModel model)
    {
	if(!(model instanceof UnitedNameTreeModel))
	    return;

	TreeModel currentModel = getModel();
	if(currentModel != null &&
	   currentModel instanceof UnitedNameTreeModel)
	    ((UnitedNameTreeModel)currentModel).removeUnitedNameTreeModelListener(this);

	super.setModel(model);
	TreeUI ui = getUI();
	if((model instanceof NodeMapper) && 
	   (ui instanceof Aligner))
	    ((Aligner)ui).setMapper((NodeMapper)model);
	

	if(model != null &&
	   model instanceof UnitedNameTreeModel)
	    ((UnitedNameTreeModel)model).addUnitedNameTreeModelListener(this);
    }

    public void add(TreeModel tree)
    {
	((UnitedNameTreeModel)getModel()).add(tree);
    }

    public void remove(TreeModel tree)
    {
    }

    public synchronized void setSynchronize(boolean toSynchronize)
    {
	if(this.toSynchronize == toSynchronize)
	    return;

	this.toSynchronize = toSynchronize;
    }

    public boolean isSynchronized()
    {
	return toSynchronize;
    }

    public synchronized void setSelectionSynchronize(boolean toSynchronize)
    {
	if(selectionSynchronize == toSynchronize)
	    return;

	selectionSynchronize = toSynchronize;
    }

    public boolean isSelectionSynchronized()
    {
	//return selectionSynchronize;
	return true;
    }

    public List<JTree> getTrees()
    {
	List<JTree> treeList = null;

	if(trees != null) {
	    synchronized (trees) {
		treeList = new ArrayList<JTree>(trees);
	    }
	}

	return treeList;
    }

    public abstract void addTree(NameTree tree);

    public void removeTree(NameTree tree)
    {
	if(trees == null ||
	   !trees.contains(tree))
	    return;

	remove(tree.getModel());

	trees.remove(tree);
	if(trees.isEmpty())
	    trees = null;

	//tree.setAlignerTree(null);
	tree.setAligner(null);
	((SynchronizedTreeSelectionModel)getSelectionModel()).removeTree(tree);
	//tree.removeTreeExpansionListener(this);
	tree.removeTreeWillExpandListener(this);
    }

    public void setMapper(NodeMapper mapper)
    {
	if(getMapper() == mapper)
	    return;

	if(ui instanceof Aligner)
	    ((Aligner)ui).setMapper(mapper);
	TreeSelectionModel treeSelectionModel = getSelectionModel();
	if(treeSelectionModel instanceof SynchronizedTreeSelectionModel)
	    ((SynchronizedTreeSelectionModel)treeSelectionModel).setNodeMapper(mapper);
    }

    public NodeMapper getMapper()
    {
	TreeModel model = getModel();
	if(model instanceof NodeMapper)
	    return (NodeMapper)model;

	return null;
    }

    public Rectangle getBounds(TreePath path, Rectangle placeIn)
    {
	Aligner aligner = getAligner();
	if(aligner != null && aligner != this)
	    return aligner.getBounds(path, placeIn);
	return placeIn;
    }

    public Aligner getAligner()
    {
	//return ((Alignable)getUI()).getAligner();
	Object aligner = getUI();
	if(aligner instanceof Aligner)
	    return (Aligner)aligner;
	//return this;
	return null;
    }

    protected void setExpandedState(TreePath path, boolean state)
    {
	if(trees == null) {
	    super.setExpandedState(path, state);
	    return;
	}

	synchronized (this) {
	if(getEventSource() == null)
	    setEventSource(this);
	}

	UnitedNameTreeNode unifiedNode = 
	    (UnitedNameTreeNode)path.getLastPathComponent();
	Map<UnitedNameTreeNode, UnitedNameTreeNode> unitedChildren = null;
	if(!state && !unifiedNode.isLeaf()) {
	    unitedChildren = new HashMap<UnitedNameTreeNode, UnitedNameTreeNode>(unifiedNode.getChildCount());
	    Enumeration<?> children = unifiedNode.children();
	    while(children.hasMoreElements()) {
		Object element = children.nextElement();
		if (element instanceof UnitedNameTreeNode) {
		    UnitedNameTreeNode child = (UnitedNameTreeNode)element;
		    unitedChildren.put(child, child);
		}
	    }
	}

	boolean collapsable = true;

	for (JTree tree : trees) {
	    if(getEventSource() == tree)
		continue;
	    
	    TreeModel m = tree.getModel();
	    TreePath p = getPathFor(path, m);
	    boolean toCollapse = true;

	    if(p == null) {
		Map<String, Set<UnitedNameTreeNode>> names = getNodeParentalMapFor(unifiedNode.getLiteral());
		if(names != null) {
		    Iterator<Set<UnitedNameTreeNode>> nodes = names.values().iterator();

		    /*
		      while(p == null && nodes.hasNext()) {
		      }
		    */
		}
	    }

	    //if(!state) {
	    {
		if(p == null) 
		    p = getUpperBoundPathFor(path, m);
		toCollapse = isCollapsable(p, unitedChildren);
		collapsable &= toCollapse;
	    }
	    
	    if(p == null ||
	       ((TreeNode)p.getLastPathComponent()).isLeaf())
		continue;

	    if(state)
		tree.expandPath(p);
	    else if(toCollapse)
		tree.collapsePath(p);
	}

	/*if(state || collapsable)*/
	super.setExpandedState(path, state);

	synchronized (this) {
	    if(getEventSource() == this)
		setEventSource(null);
	}
    }

    protected TreePath getUpperBoundPathFor(TreePath path,
					    TreeModel model)
    {
	TreePath p = getPathFor(path, model);
	
	while(p == null && path.getPathCount() > 1) {
	    path = path.getParentPath();
	    p = getPathFor(path, model);
	}

	return p;
    }

    protected boolean isCollapsable(TreePath path,
				    Map<UnitedNameTreeNode, UnitedNameTreeNode> unitedChildren)
    {
	boolean collapsable = true;

	if(path != null && 
	   unitedChildren != null && 
	   !unitedChildren.isEmpty()) {
	    NameTreeNode node = (NameTreeNode)path.getLastPathComponent();
	    if(!node.isLeaf()) {
		Enumeration<?> children = node.children();
		while(children.hasMoreElements() /*&& collapsable*/) {
		    Object child = children.nextElement();
		    if (child instanceof NameTreeNode) {
			node = (NameTreeNode)child;
			TreeNode mappedNode = getNodeFor(node);
			if(mappedNode == null || //FIXME
			   null == unitedChildren.get(mappedNode)) {
			    collapsable = false;
			}
		    }
		}
	    }
	}

	return collapsable;
    }

    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    protected Object getEventSource()
    {
	return eventSource;
    }


    public synchronized void treeWillCollapse(TreeExpansionEvent event)
    {
	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());
	TreeNode node = (TreeNode)event.getPath().getLastPathComponent();
	node = getNodeFor(node);
	if(node != null)
	    setExpandedState(new TreePath(((DefaultMutableTreeNode)node).getPath()), false);
	setEventSource(null);
    }
     
    public synchronized void treeWillExpand(TreeExpansionEvent event)
    {
	//while(getEventSource() != null) {;}

	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());
	TreeNode node = (TreeNode)event.getPath().getLastPathComponent();
	node = getNodeFor(node);
	if(node != null)
	    setExpandedState(new TreePath(((DefaultMutableTreeNode)node).getPath()), true);
	setEventSource(null);
    }

    public void treeCollapsed(TreeExpansionEvent event)
    {
	Object source = event.getSource();
	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());

	setEventSource(null);
    }

    public void treeExpanded(TreeExpansionEvent event)
    {
	Object source = event.getSource();
	if(getEventSource() != null)
	    return;

	setEventSource(event.getSource());

	setEventSource(null);
    }

    public TreeNode getNodeFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getNodeFor(node);
    }

    public TreeNode getNodeFor(TreeNode node, TreeModel tree)
    {
	return ((NodeMapper)getModel()).getNodeFor(node, tree);
    }

    public final Collection<UnitedNameTreeNode> getNodesFor(String literal)
    {
	return ((NodeMapper)getModel()).getNodesFor(literal);
    }

    public Iterator<? extends TreeNode> getNodesFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getNodesFor(node);
    }

    public Map<String, Set<UnitedNameTreeNode>> getNodeParentalMapFor(String name)
    {
	return ((NodeMapper)getModel()).getNodeParentalMapFor(name);
    }

    public TreeNode mapNode(TreeNode key, TreeNode node)
    {
	return ((NodeMapper)getModel()).mapNode(key, node);
    }

    public TreePath[] getPathsFor(String literal)
    {
	return ((NodeMapper)getModel()).getPathsFor(literal);
    }

    public TreePath[] getPathsFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getPathsFor(node);
    }

    public TreePath[] getPathsFor(TreePath path)
    {
	return ((NodeMapper)getModel()).getPathsFor(path);
    }

    public TreePath getPathFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getPathFor(node);
    }

    public TreePath getPathFor(TreePath path)
    {
	return ((NodeMapper)getModel()).getPathFor(path);
    }

    public TreePath getPathFor(TreeNode node, TreeModel tree)
    {
	return ((NodeMapper)getModel()).getPathFor(node, tree);
    }

    public TreePath getPathFor(TreePath path, TreeModel tree)
    {
	return ((NodeMapper)getModel()).getPathFor(path, tree);
    }

    public Iterator<TreeModel> getTreesFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getTreesFor(node);
    }

    public Map<TreeModel, NameTreeNode> getTreesAndNodesFor(TreeNode node)
    {
	return ((NodeMapper)getModel()).getTreesAndNodesFor(node);
    }

    public String getToolTipText(MouseEvent event)
    {
	if (getRowForLocation(event.getX(), event.getY()) == -1) {
	    return null;    
	}

	TreePath path = getPathForLocation(event.getX(), event.getY());

	return ((NamedNode)path.getLastPathComponent()).getToolTipText();
    }

    public JToolTip createToolTip()
    {
        MultiLinesToolTip tip = new MultiLinesToolTip();
        tip.setComponent(this);
        return tip;
    }

    public String getNames(Collection<? extends NameUsage<?, ?>> nameUsages, String ascribedName, Rank rank, String authors, String year, MatchingMode queryType)
    {
	return getNames(ascribedName, rank, authors, year, queryType);
    }

    public String getNames(String ascribedName, Rank rank, String authors, String year, MatchingMode queryType)
    {
	// String rankName = (rank == null)? null : rank.getName();
	// FIXME 20141121 to remove Collections.enumeration() call
	return getNames(ascribedName, rank, authors, year, queryType,
			Collections.enumeration(((UnitedNameTreeModel)getModel()).getNodesForLiteral(ascribedName.toLowerCase(), rank)));
    }

    public String getNames(String ascribedName, Rank rank, String authors, String year, MatchingMode queryType,
			   Enumeration<NamedNode<?>> e)
    {
	Object[] arguments = new Object[] {rank, ascribedName};

	if(e == null)
	    return messages.getMessage(0, arguments);

	List<NamedNode<?>> nodes = new ArrayList<NamedNode<?>>();
	while(e.hasMoreElements()) {
	    nodes.add(e.nextElement());
	}

	if(nodes.isEmpty())
	    return messages.getMessage(0, arguments);

	TreePath[] paths = new TreePath[nodes.size()];
	for(int i = 0; i < paths.length; i++) {
	    paths[i] = new TreePath
		(((DefaultMutableTreeNode)nodes.get(i)).getPath());
	}
	getSelectionModel().setSelectionPaths(paths);

	return messages.getMessage(nodes.size(), arguments);
    }

    public void queryReturned(QueryResultEvent<N, N> event)
    {
    }

    /*
    public NameUsage getObject(String name){ return null; }
    public NameUsage[] getObjects(String name){ return null; }
    public NameUsage[] getObjects(String[] names){ return null; }
    public NameUsage[] getObjects(String name, MatchingMode queryType){ return null; }
    public NameUsage[] getObjects(String[] names, MatchingMode queryType){ return null; }

    public Thread requestObject(String id){ return null; }
    public Thread requestObjects(String name){ return requestObjects(name, null, null); }
    public Thread requestObjects(String[] names){ return requestObjects(names[1], Rank.get(names[0])); }
    public Thread requestObjects(String name, MatchingMode queryType){ return requestObjects(name); }
    public Thread requestObjects(String[] names, MatchingMode queryType){ return requestObjects(names[1], Rank.get(names[0])); }
    public Thread requestObjects(String name, Rank rank)
    {
	Thread t = new RequestNameUsages<N, N>(name, rank, this);
	t.start();
	return t;
    }

    public Thread requestObjects(String name, Rank rank, MatchingMode queryType){ return requestObjects(name, rank); }

    public void addQueryResultListener(QueryResultListener listener)
    {
	adaptor.addQueryResultListener(listener);
    }

    public void removeQueryResultListener(QueryResultListener listener)
    {
	adaptor.removeQueryResultListener(listener);
    }

    //protected void fireQueryResultEvent(QueryResultEvent event)
    public void fireQueryResultEvent(QueryResultEvent event)
    {
	if(adaptor != null)
	    adaptor.fireQueryResultEvent(event);
    }

    public void setDefaultMatchingMode(MatchingMode matchingMode) { }

    public MatchingMode getDefaultMatchingMode()
    {
	return MatchingMode.EXACT;
    }
    */

    protected void appendString(StringBuffer buffer, String text)
    {
	if(text != null && text.length() > 0)
	    buffer.append(' ').append(text);
    }

    public void treeAdded(UnitedNameTreeModelEvent event)
    {
	TreeModel tree = event.getTreeModel();

	if(tree instanceof NameTree)
	    addTree((NameTree) tree);
    }

    public void treeRemoved(UnitedNameTreeModelEvent event)
    {
	TreeModel tree = event.getTreeModel();

	if(tree instanceof NameTree)
	    removeTree((NameTree) tree);
    }

    public void treeMoved(UnitedNameTreeModelEvent event)
    {
	//do nothing
    }

    /*
    class RequestNameUsages<N extends NameUsage<?, ?>, T extends N>
	extends Thread
    {
	Rank rank;
	String name;
	GenericAlignerTree<T> tree;
	NameUsageQueryParameter<N, T> parameter;
	
	public RequestNameUsages(String name, Rank rank,
				 GenericAlignerTree<T> tree)
	{
	    this.rank = rank;
	    if(name != null)
		this.name = name.toLowerCase();
	    this.tree = tree;
	    parameter = new NameUsageQueryParameter(name, rank);
	    parameter.setQueryMode(QueryMode.NAMEUSAGES);
	}
	
	public void run()
	{
	    List<NamedNode> nodesForLiteral =
		((UnitedNameTreeModel)tree.getModel()).getNodesForLiteral(name, rank);

	    if(nodesForLiteral == null)
		return;
	    
	    TreePath[] paths = new TreePath[nodesForLiteral.size()];
	    for(int i = 0; i < paths.length; i++) {
		paths[i] = new TreePath
		    (((DefaultMutableTreeNode)nodesForLiteral.get(i)).getPath());
	    }
	    
	    tree.getSelectionModel().setSelectionPaths(paths);
	    
	    //Array.clear(paths);
	    
	    tree.fireQueryResultEvent(new QueryResultEvent(tree.getQueryManager(), parameter, nodesForLiteral, this));
	}
    }
    */

    public void fireTreeExpanded(TreePath path)
    {
	super.fireTreeExpanded(path);
    }

    public void addTreeExpansionListener(TreeExpansionListener listener)
    {
	super.addTreeExpansionListener(listener);
    }

}
