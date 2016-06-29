/*
 * NodeMapper.java:  an interface to map a TreeNode to another
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 * <CODE>NodeMapper</CODE> defines an interface
 * to map nodes in trees into nodes in a united tree
 * synthesized from those trees
 *
 * @version 	09 Apr. 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NodeMapper
{
    /**
     * Adds <CODE>tree</CODE> to the <CODE>NodeMapper</CODE>.
     * If <CODE>tree</CODE> already in the <CODE>NodeMapper</CODE>,
     * this method does nothing.
     *
     * @param tree <CODE>TreeModel</CODE> containing nodes
     * to be mapped to other nodes
     */
    public void add(TreeModel tree);

    /**
     * Removes <CODE>tree</CODE> from the <CODE>NodeMapper</CODE>.
     *
     * @param tree <CODE>TreeModel</CODE> containing nodes
     * to be mapped to other nodes
     */
    public void remove(TreeModel tree);

    /**
     * Returns <CODE>TreeModel</CODE> integrated all
     * trees added to the <CODE>NodeMapper</CODE>,
     * or null if the <CODE>NodeMapper</CODE> does not
     * use tree integration method to provide node mapping.
     *
     * @return <CODE>TreeModel</CODE> integrated all trees,
     * or null if tree integration method is not used.
     */
    //public TreeModel getTree();

    /**
     * Returns a <CODE>TreeNode</CODE> in a united tree
     * mapped to <CODE>node</CODE>,
     * or null if there is no node in the united tree
     * is mapped to <CODE>node</CODE>
     *
     * @param node <CODE>TreeNode</CODE> in a tree mapped
     * to the united tree
     *
     * @return <CODE>TreeNode</CODE> representing 
     * a node in united tree, corresponding to 
     * <CODE>node</CODE>, or null if the united tree
     * does not contain such node
     */
    public TreeNode getNodeFor(TreeNode node);

    /**
     * Returns a <CODE>TreeNode</CODE> in <CODE>tree</CODE>
     * mapped to <CODE>node</CODE> in a united tree,
     * or null if <CODE>tree</CODE> does not contain such node.
     * It is inverse mapping of <CODE>#getNodeFor(TreeNode)</CODE>.
     *
     * @param node <CODE>TreeNode</CODE> in a unified tree
     * @param tree <CODE>TreeModel</CODE> to contain the returend
     * <CODE>TreeNode</CODE>
     *
     * @return <CODE>TreeNode</CODE> in <CODE>tree</CODE> mapped
     * to <CODE>node</CODE> in united tree, or null if no node in
     * <CODE>tree</CODE> is mapped to <CODE>node</CODE>
     *
     * @see <CODE>#getNodeFor(TreeNode)</CODE>
     */
    public TreeNode getNodeFor(TreeNode node, TreeModel tree);

    /**
     * Returns <tt>Set</tt>s of <tt>UnitedNameTreeNode</tt>
     * having the <tt>name</tt> as a <tt>Map</tt> indexed by the name of the higher
     * <tt>UnitedNameTreeNode</tt>.
     *
     * @param name to look for
     * @return <tt>Set</tt>s of <tt>UnitedNameTreeNode</tt>
     * having the <tt>name</tt> as a <tt>Map</tt> indexed by the name of the higher
     * <tt>UnitedNameTreeNode</tt>
     */
    public Map<String, Set<UnitedNameTreeNode>> getNodeParentalMapFor(String name);

    /**
     * Returns <tt>Set</tt>s of <tt>UnitedNameTreeNode</tt> having the <tt>name</tt>.
     *
     * @param name to look for
     * @return <tt>Set</tt>s of <tt>UnitedNameTreeNode</tt> having the <tt>name</tt>.
     */
    public Collection<UnitedNameTreeNode> getNodesFor(String name);

    /**
     * Returns <CODE>Enumeration</CODE> of <CODE>TreeNode</CODE>
     * in trees mapped to <CODE>node</CODE> in the united tree,
     * or null if no node is mapped to <CODE>node</CODE>
     *
     * @param node <CODE>TreeNode</CODE> in the united tree
     *
     * @return <CODE>Enumeration</CODE> of <CODE>TreeNode</CODE>
     * mapped to <CODE>node</CODE>, or null if no node is
     * mapped to <CODE>node</CODE>.
     */
    //public Enumeration getNodesFor(TreeNode node);
    public Iterator<? extends TreeNode> getNodesFor(TreeNode node);

    /**
     * Retruns an array of {@code TreePath} of nodes in the
     * united tree having the {@code literal} as its name.
     * or null if no node of the united tree has the name.
     *
     * @param literal name of last elements of paths returned.
     * @return an array of {@code TreePath} of which last elements have the name.
     */
    public TreePath[] getPathsFor(String literal);

    /**
     * Retruns an array of {@code TreePath} of nodes in the
     * united tree shareing its literal with the {@code node}
     * wich is mapped to a node in the united tree,
     * assuming that the {@code node} is a {@code NamedNode}.
     *
     * @param node of which name literal is shared with last elements of paths returned.
     * @return an array of {@code TreePath} of which last elements share their literal with the {@code node}.
     */
    public TreePath[] getPathsFor(TreeNode node);

    /**
     * Retruns an array of {@code TreePath} of nodes in the
     * united tree shareing its literal with the last element of
     * the {@code path}
     * wich is mapped to a node in the united tree,
     * assuming that the node is a {@code NamedNode}.
     *
     * @param path of which last elements share the name literal with last elements of paths to returned.
     * @return an array of {@code TreePath} of which last elements share their literal with the last element of the {@code path}.
     */
    public TreePath[] getPathsFor(TreePath path);

    /**
     * Retruns <CODE>TreePath</CODE> of the node in the
     * unified tree to which <CODE>node</CODE> in a tree
     * mapped, or null if ....
     */
    public TreePath getPathFor(TreeNode node);

    public TreePath getPathFor(TreePath path);

    public TreePath getPathFor(TreeNode node, TreeModel tree);

    public TreePath getPathFor(TreePath path, TreeModel tree);

    public Iterator<TreeModel> getTreesFor(TreeNode node);

    public Map<TreeModel, NameTreeNode> getTreesAndNodesFor(TreeNode node);

    /**
     * Maps <CODE>node</CODE> to <CODE>key</CODE>
     */
    public TreeNode mapNode(TreeNode key, TreeNode node);

}
