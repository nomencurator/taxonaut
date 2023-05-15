/*
 * DualTreeModel.java:  a TreeModel representing a flip tree.
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

import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JTree.DynamicUtilTreeNode;

import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;

/**
 *
 * @version 	16 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class DualTreeModel
    extends DefaultTreeModel
    implements TreeModelListener
{
    public static final int UPPER = 0;
    public static final int LOWER = 1;    
    
    public static DefaultMutableTreeNode createRootNode()
    {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();
	root.add(new DefaultMutableTreeNode());
	root.add(new DefaultMutableTreeNode());
	return root;
    }
    
    protected DefaultTreeModel upperTreeModel;

    protected DefaultTreeModel lowerTreeModel;

    protected Hashtable<TreeNode, DefaultTreeModel> nodeModelCache;

    public void setUpperModel(DefaultTreeModel upperTreeModel)
    {
	if (this.upperTreeModel == upperTreeModel) return;

	if (null != this.upperTreeModel)
	    this.upperTreeModel.removeTreeModelListener(this);

	((DefaultMutableTreeNode)((TreeNode)getRoot()).getChildAt(UPPER)).setUserObject(upperTreeModel);
	this.upperTreeModel = upperTreeModel;

	if (null != this.upperTreeModel) {
	    this.upperTreeModel.addTreeModelListener(this);
	}
    }

    public DefaultTreeModel getUpperModel()
    {
	return upperTreeModel;
    }

    public DefaultMutableTreeNode getUpperRoot()
    {
	DefaultTreeModel model = getUpperModel();

	return (null == model) ? null : (DefaultMutableTreeNode)model.getRoot();
    }
    
    public void setUpperRoot(TreeNode root)
    {
	DefaultTreeModel model = getUpperModel();
	if (null != model)
	    model.setRoot(root);
    }

    public void setLowerModel(DefaultTreeModel lowerTreeModel)
    {
	if (this.lowerTreeModel == lowerTreeModel) return;

	if (null != this.lowerTreeModel)
	    this.lowerTreeModel.removeTreeModelListener(this);

	((DefaultMutableTreeNode)((TreeNode)getRoot()).getChildAt(UPPER)).setUserObject(lowerTreeModel);
	this.lowerTreeModel = lowerTreeModel;

	if (null != this.lowerTreeModel) {
	    this.lowerTreeModel.addTreeModelListener(this);
	}
    }

    public DefaultTreeModel getLowerModel()
    {
	return lowerTreeModel;
    }

    public TreeNode getLowerRoot()
    {
	DefaultTreeModel model = getLowerModel();

	return (null == model) ? null : (TreeNode)model.getRoot();
    }
    
    public void setLowerRoot(TreeNode root)
    {
	DefaultTreeModel model = getLowerModel();
	if (null != model)
	    model.setRoot(root);
    }

    protected Hashtable<TreeNode, DefaultTreeModel> getNodeModelCache()
    {
	if (null == nodeModelCache)
	    nodeModelCache = new Hashtable<TreeNode, DefaultTreeModel>();
	return nodeModelCache;
    }

    protected void setNodeModelCache(Hashtable<TreeNode, DefaultTreeModel> cache)
    {
	if (null != nodeModelCache) nodeModelCache.clear();
	nodeModelCache = cache;
    }

    public DualTreeModel()
    {
	super(createRootNode());
    }

    public DualTreeModel(TreeNode lowerRoot, TreeNode upperRoot)
    {
	this(createDefaultTreeModel(lowerRoot), createDefaultTreeModel(upperRoot));
    }

    public DualTreeModel(DefaultTreeModel lowerModel, DefaultTreeModel upperModel)
    {
	this();
	setLowerModel(upperModel);
	setUpperModel(upperModel);
    }

    public static TreeNode createEmptyTreeNode()
    {
	return new DefaultMutableTreeNode("");
    }

    public static DefaultTreeModel createDefaultTreeModel(TreeNode root)
    {
	if (null == root)
	    root = createEmptyTreeNode();
	return new DefaultTreeModel(root);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsksAllowsChildren(boolean allows)
    {
	//super.setAsksAllowsChildren(allows);
	setAsksAllowsChildren(allows, getUpperModel());
	setAsksAllowsChildren(allows, getLowerModel());
    }

    protected void setAsksAllowsChildren(boolean allows, DefaultTreeModel model)
    {
	if (model != null)
	    model.setAsksAllowsChildren(allows);
    }
    

    protected DefaultTreeModel getTreeModelFor(TreeNode node)
    {
	if(null == node) return null;

	DefaultTreeModel model = null;
	Hashtable<TreeNode, DefaultTreeModel> cache = getNodeModelCache();
	if (null == cache) {
	    setNodeModelCache(new Hashtable<TreeNode, DefaultTreeModel>());
	    cache = getNodeModelCache();
	}
	else
	    model = cache.get(node);
	if (null == model) {
	    model = getTreeModelFor(node, getLowerModel());
	    if (null == model)
		model = getTreeModelFor(node, getUpperModel());
	    if (null != model)
		cache.put(node, model);
	}
	return model;
    }

    protected DefaultTreeModel getTreeModelFor(TreeNode node, DefaultTreeModel model)
    {
	if (null == node || null == model) return null;
	TreeNode[] path = model.getPathToRoot(node);
	if (null == path || null == path[0]) return null;
	return (path[0] == model.getRoot())? model : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload()
    {
	reload(getUpperModel());
	reload(getLowerModel());	
	super.reload();
    }

    protected void reload(DefaultTreeModel model)
    {
	if (null != model)
	    model.reload();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insertNodeInto(MutableTreeNode child, MutableTreeNode parent, int index)
    {
	if (null == child || null == parent || child == parent) return;
	DefaultTreeModel model = getTreeModelFor(child);
	if (null != model)
	    nodeModelCache.remove(child);
	model = getTreeModelFor(parent);
	if (null != model)
	    model.insertNodeInto(child, parent, index);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNodeFromParent(MutableTreeNode node)
    {
	if (node == null) return;
	DefaultTreeModel model = getTreeModelFor(node);
	if (null != model)
	    model.removeNodeFromParent(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeChanged(TreeNode node)
    {
	DefaultTreeModel model = getTreeModelFor(node);	
	if (null != model)
	    model.nodeChanged(node);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reload(TreeNode node)
    {
	if(null != node) {
	    DefaultTreeModel model = getTreeModelFor(node);
	    if (null != model)
		model.reload(node);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodesWereInserted(TreeNode node, int[] childIndices)
    {
	if (null == node) return;
	DefaultTreeModel model = getTreeModelFor(node);
	if (this == model)
	    super.nodesWereInserted(node, childIndices);
	else if (null != model)
	    model.nodesWereInserted(node, childIndices);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodesWereRemoved(TreeNode node, int[] childIndices, Object[] removedChildren)
    {
	if (null == node
	    || null == childIndices || 0 == childIndices.length
	    || null == removedChildren || 0 == removedChildren.length) return;
	DefaultTreeModel model = getTreeModelFor(node);
	if (null != model) {
	    for (Object child : removedChildren)
		getNodeModelCache().remove((TreeNode)node);
	    model.nodesWereRemoved(node, childIndices, removedChildren);
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodesChanged(TreeNode node, int[] childIndices)
    {
	if (null == node) return;
	DefaultTreeModel model = getTreeModelFor(node);
	if (this == model)
	    super.nodesChanged(node, childIndices);
	else if (null != model)
	    model.nodesChanged(node, childIndices);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nodeStructureChanged(TreeNode node)
    {
	if (null == node) return;
	DefaultTreeModel model = getTreeModelFor(node);
	if (null != model)
	    model.nodeStructureChanged(node);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addTreeModelListener(TreeModelListener listener)
    {
	addTreeModelListener(listener, getUpperModel());
	addTreeModelListener(listener, getLowerModel());	
	super.addTreeModelListener(listener);
    }

    protected void addTreeModelListener(TreeModelListener listener, DefaultTreeModel model)
    {
	if (null != listener && null != model)
	    model.addTreeModelListener(listener);
    }
      
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void removeTreeModelListener(TreeModelListener listener)
    {
	removeTreeModelListener(listener, getUpperModel());
	removeTreeModelListener(listener, getLowerModel());
	super.removeTreeModelListener(listener);
    }
    
    protected void removeTreeModelListener(TreeModelListener listener, DefaultTreeModel model)
    {
	if (null != listener && null != model)
	    model.removeTreeModelListener(listener);
    
}
    protected boolean isSubtree(Object object)
    {
	return (null != object
		&& (object == getUpperModel() || object == getLowerModel()));
    }

    public void treeNodesChanged(TreeModelEvent event)
    {
	if (isSubtree(event.getSource()))
	    fireTreeNodesChanged(
				this, event.getPath(),
				event.getChildIndices(), event.getChildren());
    }

    public void treeNodesInserted(TreeModelEvent event)
    {
	if (isSubtree(event.getSource()))
	    fireTreeNodesInserted(
				  this, event.getPath(),
				  event.getChildIndices(), event.getChildren());

    }

    public void treeNodesRemoved(TreeModelEvent event)
    {
	if (isSubtree(event.getSource()))
	    fireTreeNodesRemoved(
				this, event.getPath(),
				event.getChildIndices(), event.getChildren());

    }

    public void treeStructureChanged(TreeModelEvent event)
    {	
	if (isSubtree(event.getSource()))
	    fireTreeStructureChanged(
				     this, event.getPath(),
				     event.getChildIndices(), event.getChildren());

    }
}
