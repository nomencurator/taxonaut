/*
 * NameTreeNode.java:  a TreeNode wrapping NameUsageNode
 *
 * Copyright (c) 2002, 2003, 2004, 2005, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JTree;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.nomencurator.beans.PropertyChangeable;

import org.nomencurator.io.Exchangable;
import org.nomencurator.io.ObjectExchanger;

import org.nomencurator.model.Annotation;
import org.nomencurator.model.Name;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

/**
 * {@code NameTreeNode} wrapping a {@code NameUsage}
 *
 * @version 	06 Dec 2019
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeNode
    extends GenericNameTreeNode <NameUsage<?>, NameUsageNode<?>>
{
    private static final long serialVersionUID = 5788535401126987413L;

    /**
     * Create a tree node without parent nor child
     * but initialise it with the given object
     */
    // Safe wrapping by an interface
    @SuppressWarnings("unchecked")
    public NameTreeNode(NameUsage<?> nameUsage)
    {
	super((NameUsage<NameUsage<?>>)nameUsage);
    }

    /**
     * Create a tree node without parent nor child
     * but initialise it with the given object and
     * given allowance
     */
    // Safe wrapping by an interface
    @SuppressWarnings("unchecked")
    public NameTreeNode(NameUsage<?> nameUsage, boolean allowsChildren)
    {
	super((NameUsage<NameUsage<?>>)nameUsage, allowsChildren);
    }
}

/**
 * {@code GenericNameTreeNode} wrapping a {@code NameUsage} as {@code N}.
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
class GenericNameTreeNode <T extends NameUsage<?>, N extends NameUsageNode<?>>
    extends AbstractNameTreeNode<GenericNameTreeNode<?, ?>>
    implements PropertyChangeListener
	       // Exchangable<NameTreeNode>
{
    private static final long serialVersionUID = 7118376193252108532L;

    /** NameUsageNode wrapped by this */
    protected NameUsage<T> node;

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
    protected GenericNameTreeNode(NameUsage<T> nameUsageNode)
    {
	this(nameUsageNode, true);
    }

    /**
     * Create a tree node without parent nor child
     * but initialise it with the given object and
     * given allowance
     */
    protected GenericNameTreeNode(NameUsage<T> nameUsageNode, boolean allowsChildren)
    {
	super(nameUsageNode, allowsChildren);

	node = nameUsageNode;
	if(node instanceof PropertyChangeable)
	    ((PropertyChangeable)node).addPropertyChangeListener(this);

	if(!allowsChildren || !node.hasLowerNameUsages())
	    return;

	List<NameUsage<T>> taxa = nameUsageNode.getLowerNameUsages();
	if(taxa != null) {
	    lowerNameUsageUnchecked = false;
	    for(NameUsage<T> taxon : taxa) {
		if (taxon instanceof NameUsage) {
		    add(new NameTreeNode(taxon));
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


    public synchronized void propertyChange(PropertyChangeEvent event)
    {
    }

    /*
    protected void finalize()
	throws Throwable
    {
	if(node instanceof PropertyChangeable)
	    ((PropertyChangeable)node).removePropertyChangeListener(this);
	super.finalize();
    }
    */

    public NameUsage<T> getNameUsage()
    {
	NameUsage<T> theUsage = null;
	Object object = getUserObject();
	if (object instanceof NameUsage) {
	    @SuppressWarnings("unchecked")
		NameUsage<T> tmp = (NameUsage<T>)object;
	    theUsage = tmp;
	}
	return theUsage;
    }

    public NameUsageNode<N> getNameUsageNode()
    {
	NameUsageNode<N> theNode = null;
	Object object = getUserObject();
	if (object instanceof NameUsageNode) {
	    @SuppressWarnings("unchecked")
		NameUsageNode<N> tmp = (NameUsageNode<N>)object;
	    theNode = tmp;
	}
	return theNode;
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
	NameUsage<T> usage = getNameUsage();
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
	NameUsage<T> usage = getNameUsage();
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
		o = ((NameUsage<?>)o).getRankedName(true);
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
