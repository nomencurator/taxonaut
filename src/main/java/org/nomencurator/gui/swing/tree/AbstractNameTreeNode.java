/*
 * AbstractNameTreeNode.java:  an MutableTreeNode implementing NamedNode
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.awt.Color;
import java.awt.Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.Icon;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

import org.nomencurator.model.NameUsage;

/**
 * {@code AbstractNameTreeNode} wrapping a {@code NameUsage} as generic {@code N}.
 * Concrete subclasses are expeced to be implemented like
 * {@code public class Foo extends AbstractNameTreeNode<Foo, Bar>} where {@code Bar}
 * is an insance of {@code NameUsage<?>}
 *
 * @version 	03 Dec 2019
 * @author 	Nozomi `James' Ytow
 */
public abstract class AbstractNameTreeNode<T extends AbstractNameTreeNode<?>>
    extends TrimmableTreeNode
    implements NamedNode<T>, RenderingOptions
{
    private static final long serialVersionUID = -8535391312644725675L;

    protected String literal;

    protected String rankedName;

    protected RoughSetAdaptor rough;

    protected String toolTipText;

    protected static String pseudonym = "(unnamed)";

    protected RenderingOptionsAdapter renderingOptions = new RenderingOptionsAdapter();

    protected Map<String, Set<T>> childNames;

    protected AbstractNameTreeNode(NameUsage<?>  nameUsage)
    {
	this(nameUsage, true);
    }

    protected AbstractNameTreeNode(NameUsage<?> nameUsage, boolean  allowsChildren)
    {
	super(nameUsage, allowsChildren);
    }

    protected AbstractNameTreeNode()
    {
	this(true);
    }

    protected AbstractNameTreeNode(boolean  allowsChildren)
    {
	super(null, allowsChildren);
    }


    public AbstractNameTreeNode<T> clone() {
	
	@SuppressWarnings("unchecked")
	AbstractNameTreeNode<T> newNode =
	    (AbstractNameTreeNode)super.clone();
	newNode.literal = literal;
	newNode.rankedName = rankedName;
	if(rough != null) {
	    newNode.rough = new RoughSetAdaptor(newNode);
	}
	newNode.toolTipText = toolTipText;

	return newNode;
    }



    protected abstract String getRankedName(boolean abbreviate);

    protected String getUnrankedName()
    {
	return getLiteral();
    }

    protected String getUnrankedName(boolean abbreviate)
    {
	return getUnrankedName();
    }

    public int getMatchedChildrenCount(NamedNode<?> node, int depth) {
	int childCount = 0;

	if(node != null) {
	    String rankedName = getRankedName();
	    String unrankedName = getLiteral();
	    String rankedNodeName = node.getRankedName();
	    String unrankedNodeName = node.getLiteral();

	    if((rankedName != null && rankedNodeName != null && rankedName.equals(rankedNodeName))
	       || unrankedName != null && unrankedNodeName != null && unrankedName.equals(unrankedNodeName)) {
		if(depth != 0 && !isLeaf() && !node.isLeaf()) {
		    NamedNode<?> reference = getChildCount() < node.getChildCount() ? this : node;
		    NamedNode<?> target = (reference == this ? node : this);
		    Map<String, NamedNode<?>> ranked = new HashMap<String, NamedNode<?>>(target.getChildCount());
		    Map<String, NamedNode<?>> unranked = new HashMap<String, NamedNode<?>>(target.getChildCount());
		    Enumeration<?> e = target.children();
		    while (e.hasMoreElements()) {
			Object element = e.nextElement();
			if (element instanceof NamedNode) {
			    target = (NamedNode<?>)element;
			    ranked.put(target.getRankedName(), target);
			    unranked.put(target.getLiteral(), target);
			}
		    }
		    if(depth > 0)
			depth--;
		    e = reference.children();
		    while (e.hasMoreElements()) {
			target = null;
			Object element = e.nextElement();
			if (element instanceof NamedNode) {
			    node = (NamedNode<?>)element;
			    target = ranked.get(node.getRankedName());
			    if(target == null)
				target = unranked.get(node.getLiteral());
			    if(target != null)
				childCount  += node.getMatchedChildrenCount(target, depth);
			}
		    }
		}
		childCount++; // count up for the this/node pair
	    }
	}
	return childCount;
    }

    public void addIncludant(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	rough.addIncludant(node);
    }

    public void removeIncludant(NamedNode<?> node)
    {
	if(rough == null)
	    return;

	rough.removeIncludant(node);
    }

    public Map<String, NamedNode<?>> getIncludants()
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getIncludants();
    }

    public void clearIncludants()
    {
	if(rough != null)
	    rough.clearIncludants();
    }

    public void addExcludant(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	rough.addExcludant(node);
    }

    public void removeExcludant(NamedNode<?> node)
    {
	if(rough == null)
	    return;

	rough.removeExcludant(node);
    }

    public Map<String, NamedNode<?>> getExcludants()
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getExcludants();
    }

    public void clearExcludants()
    {
	if(rough != null)
	    rough.clearExcludants();
    }


    public boolean isCompatible(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.isCompatible(node);
    }

    public Map<String, NamedNode<?>> getCrossSection(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getCrossSection(node);
    }

    public List<Map<String, NamedNode<?>>> getIntersection(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getIntersection(node);
    }

    public List<Map<String, NamedNode<?>>> getUnion(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getUnion(node);
    }

    public double getCoverage(NamedNode<?> node)
    {
	if(rough == null)
	    rough = new RoughSetAdaptor(this);

	return rough.getCoverage(node);
    }

    protected String getToolTipText(NameUsage<?> nameUsage)
    {
	if(nameUsage != null)
	    return nameUsage.getSummary();
	return "";
    }

    protected String getToolTipText(int start)
    {
	if(toolTipText == null) {
	    TreeNode[] path = getPath();
	    StringBuffer buffer = new StringBuffer();
	    int indent = 0;
	    int nextLine =  path.length - 1;
	    for(int i = start; i < path.length; i++) {
		for(int j = 0; j < indent; j++)
		    buffer.append(" ");
		indent++;
		buffer.append(((NamedNode)path[i]).getRankedName());
		if(i < nextLine)
		    buffer.append("\n");
	    }
	    path = null;
	    
	    setToolTipText(buffer.toString());
	}
	return toolTipText;
    }

    protected void setToolTipText(String text) 
    {
	toolTipText = text;
    }

    public void setParent(MutableTreeNode newParent)
    {
	super.setParent(newParent);
	setToolTipText(null);
	if(isLeaf())
	    return;

	Enumeration<?> descendants = breadthFirstEnumeration();
	while(descendants.hasMoreElements()) {
	    MutableTreeNode descendant = 
		(MutableTreeNode)descendants.nextElement();
	    if(descendant instanceof AbstractNameTreeNode) {
		((AbstractNameTreeNode<?>)descendant).setToolTipText(null);
	    }
	}
    }

    public String getChildParentLiteral()
    {
	return getChildParentLiteral(true);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public String getChildParentLiteral(boolean withRank)
    {
	StringBuffer buffer = new StringBuffer();

	if(withRank)
	    buffer.append(getRankedName());
	else
	    buffer.append(getLiteral());

	buffer.append(SEPARATOR);

	T parent =(T)getParent();

	if(parent != null) {
	    if(withRank)
		buffer.append(parent.getRankedName());
	    else
		buffer.append(parent.getLiteral());
	}

	return buffer.toString();
    }

    public String getPseudonymForUnnamedNode()
    {
	return pseudonym;
    }

    public void setPseudonymForUnnamedNode(String pseudonym)
    {
	org.nomencurator.gui.swing.tree.AbstractNameTreeNode.pseudonym = pseudonym;
    }

    public int getHeight()
    {
	return getPath().length;
    }

    /**
     * Returnes parent node of the {@code NamedNode},
     * some node having a name on the prental chain if the parent node is unnamed,
     * or null if none node on the prental chain has name or if the 
     * {@code NamedNode} is the root node.
     */
    public NamedNode<?> getNamedParent()
    {
	return NamedNodeUtility.getNamedParent(this);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public T getChild(String name)
    {
	if(isLeaf() || name == null)
	    return null;
	Enumeration<?> children = children();
	T node = null;
	while(children.hasMoreElements() &&
	      node == null) {
	    T child = (T)children.nextElement();
	    if(name.equals(child.getRankedName()))
		node = child;
	    else if(name.equals(child.getRankedName(true)))
		node = child;
	    else if(name.equals(child.getLiteral()))
		node = child;
	}
	return node;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Collection<T> getChildren(String name)
    {
	if(isLeaf() || name == null)
	    return null;

	Set<T> nodes;
	if (childNames != null) {
	    nodes = childNames.get(name);
	    if (nodes != null)
		return nodes;
	}

	Enumeration<?> children = children();
	nodes = new HashSet<T>();
	while(children.hasMoreElements()) {
	    T child = (T)children.nextElement();
	    if(name.equals(child.getRankedName()))
		nodes.add(child);
	    else if(name.equals(child.getRankedName(true)))
		nodes.add(child);
	    else if(name.equals(child.getLiteral()))
		nodes.add(child);
	}

	if (!nodes.isEmpty()) {
	    if (childNames == null)
		childNames = new HashMap<String, Set<T>>();
	    childNames.put(name, nodes);
	}

	return nodes;
    }

    protected void addChildrenNameCashe(String key, T child)
    {
	if (childNames != null && child != null && key != null) {
	    Collection<T> names = childNames.get(key);
	    if (names != null && !names.contains(child)) {
		names.add(child);
	    }
	}
    }

    protected void addChildrenNameCashe(T child)
    {
	if (childNames != null && child != null) {
	    addChildrenNameCashe(child.getRankedName(), child);
	    addChildrenNameCashe(child.getRankedName(true), child);
	    addChildrenNameCashe(child.getLiteral(), child);
	}
    }

    @SuppressWarnings("unchecked")
    public void insert(MutableTreeNode newChild, int childIndex)
    {
	super.insert(newChild, childIndex);
	if (childNames != null && newChild != null && getClass().isInstance(newChild)) {
	    // assuming
	    addChildrenNameCashe((T)newChild);
	}
    }

    protected void removeChildrenNameCashe(String key, T child)
    {
	if (key != null && child != null && childNames != null) {
	    Collection<T> names = childNames.get(key);
	    if (names != null) {
		names.remove(child);
		if (names.size() == 0) {
		    childNames.remove(key);
		    if (childNames.size() == 0)
			childNames = null;
		}
	    }
       }
    }

    protected void removeChildrenNameCashe(T child)
    {
	if (child != null && childNames != null) {
	    removeChildrenNameCashe(child.getLiteral(), child);
	    removeChildrenNameCashe(child.getRankedName(true), child);
	    removeChildrenNameCashe(child.getRankedName(), child);
	}
    }
    
    @SuppressWarnings("unchecked")
    public void remove(int childIndex)
    {
	TreeNode child = getChildAt(childIndex);
	if (getClass().isInstance(child))
	    removeChildrenNameCashe((T)child);
	super.remove(childIndex);
    }
    
    protected abstract void checkLowerNameUsage();
    
    public TreeNode getChildAt(int index)
    {
	checkLowerNameUsage();
	return super.getChildAt(index);
    }
    
    public int getChildCount()
    {
	checkLowerNameUsage();
	return super.getChildCount();
    }
    
    public int getIndex(TreeNode aChild)
    {
	checkLowerNameUsage();
	return super.getIndex(aChild);
    }
    
    public Enumeration<TreeNode> children()
    {
	checkLowerNameUsage();
	return super.children();
    }
    
    @SuppressWarnings("unchecked")
    public T getAncestor(String name)
    {
	if(name == null)
	    return null;
	
	TreeNode[] path = getPath();
	T found = null;
	int i =  path.length;
	while(found == null && i > 0) {
	    TreeNode node = path[--i];
	    if(node instanceof NamedNode &&
	       name.equals(((NamedNode<?>)node).getRankedName())) {
		found = (T)node;
	    }
	}
	
	i =  path.length;
	while(found == null && i > 0) {
	    TreeNode node = path[--i];
	    if(node instanceof NamedNode &&
	       name.equals(((NamedNode<?>)node).getLiteral())) {
		found = (T)node;
	    }
	}
	
	return found;
    }
    
    public void setOpenIcon(Icon icon)
    {
	renderingOptions.setOpenIcon(icon);
    }
    
    public Icon getOpenIcon()
    {
	return renderingOptions.getOpenIcon();
    }
    
    public void setClosedIcon(Icon icon)
    {
	renderingOptions.setClosedIcon(icon);
    }
    
    public Icon getClosedIcon()
    {
	return renderingOptions.getClosedIcon();
    }
    
    public void setLeafIcon(Icon icon)
    {
	renderingOptions.setLeafIcon(icon);
    }
    
    public Icon getLeafIcon()
    {
	return renderingOptions.getLeafIcon();
    }
    
    public void setTextSelectionColor(Color color)
    {
	renderingOptions.setTextSelectionColor(color);
    }
    
    public Color getTextSelectionColor()
    {
	return renderingOptions.getTextSelectionColor();
    }
    
    public void setTextNonSelectionColor(Color color)
    {
	renderingOptions.setTextNonSelectionColor(color);
    }

    public Color getTextNonSelectionColor()
    {
	return renderingOptions.getTextNonSelectionColor();
    }
    
    public void setBackgroundSelectionColor(Color color)
    {
	renderingOptions.setBackgroundSelectionColor(color);
    }

    public Color getBackgroundSelectionColor()
    {
	return renderingOptions.getBackgroundSelectionColor();
    }

    public void setBackgroundNonSelectionColor(Color color)
    {
	renderingOptions.setBackgroundNonSelectionColor(color);
    }

    public Color getBackgroundNonSelectionColor()
    {
	return renderingOptions.getBackgroundNonSelectionColor();
    }

    public void setBorderSelectionColor(Color color)
    {
	renderingOptions.setBorderSelectionColor(color);
    }

    public Color getBorderSelectionColor()
    {
	return renderingOptions.getBorderSelectionColor();
    }

    public void setFont(Font font)
    {
	renderingOptions.setFont(font);
    }

    public Font getFont()
    {
	return renderingOptions.getFont();
    }

}
