/*
 * NameTreeNode.java:  a TreeNode wrapping NameUsageNode
 *
 * Copyright (c) 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.nomencurator.io.Exchangable;
import org.nomencurator.io.ObjectExchanger;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Name;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

/**
 * <code>NameTreeNode</code> wrapping a <code>NameUsage</code>
 *
 * @version 	21 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeNode
    extends AbstractNameTreeNode<NameTreeNode>
    implements Observer //,
	       // Exchangable<NameTreeNode, NameTreeNode>
{
    private static final long serialVersionUID = -2874077019466493952L;

    /** NameUsageNode wrapped by this */
    protected NameUsage<?, ?> node;

    protected boolean lowerNameUsageUnchecked;

    protected static boolean useAbbreviation = true;

    public static void setUseAbbreviation(boolean abbreviation)
    {
	useAbbreviation = abbreviation;
    }

    public static boolean isUseAbbreviation()
    {
	return useAbbreviation;
    }

    /**
     * Create a tree node without parent nor child
     * but initialise it with the given object
     */
    public NameTreeNode(NameUsage<?, ?> nameUsageNode)
    {
	this(nameUsageNode, true);
    }

    /**
     * Create a tree node without parent nor child
     * but initialise it with the given object and
     * given allowance
     */
    public NameTreeNode(NameUsage<?, ?> nameUsageNode, boolean allowsChildren)
    {
	/*
	super(nameUsageNode, allowsChildren?nameUsageNode.getLowerTaxa():null);
	if(nameUsageNode instanceof Observable)
	    ((Observable)nameUsageNode).addObserver(this);
	*/
	super(nameUsageNode, allowsChildren);

	node = nameUsageNode;
	if(node instanceof Observable)
	    ((Observable)node).addObserver(this);

	if(!allowsChildren || !node.hasLowerNameUsages())
	    return;

	List<?> taxa = node.getLowerNameUsages();
	if(taxa != null) {
	    lowerNameUsageUnchecked = false;
	    for(Object taxon : taxa) {
		if (taxon instanceof NameUsage) {
		    add(new NameTreeNode((NameUsage<?, ?>)taxon));
		}
	    }
	}
	else {
	    lowerNameUsageUnchecked = true;
	}
    }

    /**
     * Returns true if this node is allowed to have children.
     *
     * @return	true if this node allows children, else false
     */
    public boolean getAllowsChildren()
    {
	return (super.getAllowsChildren() &&
		(node.isEditable() || node.hasLowerNameUsages()));
    }


    public synchronized void update(Observable obs, Object arg)
    {
    }

    protected void finalize()
    {
	if(node != null &&
	   (node instanceof Observable))
	    ((Observable)node).deleteObserver(this);
    }

    public NameUsage<?, ?> getNameUsage()
    {
	return (NameUsage<?, ?>)getUserObject();
    }

    public NameUsageNode<?, ?> getNameUsageNode()
    {
	return (NameUsageNode<?, ?>)getUserObject();
    }

    public String getRank()
    {
	return Rank.getRank(getNameUsage());
    }

    public String getName()
    {
	return getRankedName(true);
    }

    public String getLiteral()
    {
	NameUsage<?, ?> usage = getNameUsage();
	if(usage != null)
	    return getNameUsage().getLiteral();
	    
	return "";
    }
    
    public String getRankedName()
    {
	return getRankedName(useAbbreviation);
    }

    public String getRankedName(boolean abbreviate)
    {
	NameUsage<?, ?> usage = getNameUsage();
	if(usage != null)
	    return usage.getRankedName(abbreviate);
	    
	return "";
    }

    public Map<String, NameTreeNode> getChildrenSet()
    {
	Map<String, NameTreeNode> childrenSet = 
	    new HashMap<String, NameTreeNode>();
	Enumeration<?> e = children();
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NameTreeNode) {
		NameTreeNode child = (NameTreeNode)element;
		childrenSet.put(child.getLiteral(), child);
	    }
	}

	return childrenSet;
    }

    public Map<String, NameTreeNode> getSiblings()
    {
	NameTreeNode parent =
	    (NameTreeNode)getParent();
	if(parent == null)
	    return new HashMap<String, NameTreeNode>();
	Map<String, NameTreeNode> siblings = parent.getChildrenSet();
	siblings.remove(getLiteral());
	return siblings;
    }

    public void sortChildrenAs(Enumeration<?> order)
    {
	if(order == null ||
	   isLeaf())
	    return;

	// need to reconsider to allow multiple
	// children having the same name literal
	Map<String, NameTreeNode> children = new HashMap<String, NameTreeNode>();
	Map<NameTreeNode, Integer> indices  = new HashMap<NameTreeNode, Integer>();
	Map<Integer, NameTreeNode> indexedChildren  = new HashMap<Integer, NameTreeNode>();
	Enumeration<?> e = children();
	int index = 0;
	while(e.hasMoreElements()) {
	    Object element = e.nextElement();
	    if (element instanceof NameTreeNode) {
		NameTreeNode child = (NameTreeNode)element;
		children.put(child.getRankedName(), child);
		Integer indexValue = Integer.valueOf(index++);
		indices.put(child, indexValue);
		indexedChildren.put(indexValue, child);
	    }
	}

	removeAllChildren();

	while(order.hasMoreElements()) {
	    Object o = order.nextElement();
	    if(o instanceof NamedNode) {
		o = ((NamedNode<?>)o).getLiteral();
	    }
	    else if(o instanceof NameUsage) {
		o = ((NameUsage<?, ?>)o).getRankedName(true);
	    }
	    
	    NameTreeNode child = children.get(o);
	    if(child != null) {
		add(child);
		children.remove(o);
		Integer i = indices.get(child);
		indices.remove(child);
		indexedChildren.remove(i);
	    }

	}

	if(children.isEmpty())
	    return;

	for(int i = 0; i < index; i++) {
	    NameTreeNode child = indexedChildren.get(Integer.valueOf(i));
	    if(child != null)
		add(child);
	}

	children.clear();
	indices.clear();
	indexedChildren.clear();
    }

    public String getToolTipText()
    {
	//return getToolTipText(0);
	return getToolTipText(getNameUsage());
    }

    protected void checkLowerNameUsage()
    {
    }

    /*
    protected ObjectExchanger<NamedNode, NamedNode> exchanger;

    public ObjectExchanger<NamedNode, NamedNode> getObjectExchanger()
    {
	NameUsage usage = getNameUsage();
	return (exchanger != null)? exchanger:
	    ((usage == null && (usage instanceof Exchangable))? null:
	     ((Exchangable)usage).getObjectExchanger());
    }

    public void setObjectExchanger(ObjectExchanger<NamedNode, NamedNode> exchanger)
    {
	this.exchanger = exchanger;
    }
    */
}
