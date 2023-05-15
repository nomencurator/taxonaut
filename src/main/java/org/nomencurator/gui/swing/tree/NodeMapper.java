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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
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

import org.nomencurator.model.NameUsage;

/**
 * {@code NodeMapper} defines an interface
 * to map nodes in trees into nodes in a united tree
 * synthesized from those trees
 *
 * @version 	08 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NodeMapper
{
    /**
     * Adds {@code tree} to the {@code NodeMapper}.
     * If {@code tree} already in the {@code NodeMapper},
     * this method does nothing.
     *
     * @param tree {@code TreeModel} containing nodes
     * to be mapped to other nodes
     */
    public void add(TreeModel tree);

    /**
     * Removes {@code tree} from the {@code NodeMapper}.
     *
     * @param tree {@code TreeModel} containing nodes
     * to be mapped to other nodes
     */
    public void remove(TreeModel tree);

    /**
     * Returns {@code TreeModel} integrated all
     * trees added to the {@code NodeMapper},
     * or null if the {@code NodeMapper} does not
     * use tree integration method to provide node mapping.
     *
     * @return {@code TreeModel} integrated all trees,
     * or null if tree integration method is not used.
     */
    //public TreeModel getTree();

    /**
     * Returns a {@code TreeNode} in a united tree
     * mapped to {@code node},
     * or null if there is no node in the united tree
     * is mapped to {@code node}
     *
     * @param node {@code TreeNode} in a tree mapped
     * to the united tree
     *
     * @return {@code TreeNode} representing 
     * a node in united tree, corresponding to 
     * {@code node}, or null if the united tree
     * does not contain such node
     */
    public TreeNode getNodeFor(TreeNode node);

    /**
     * Returns a {@code TreeNode} in {@code tree}
     * mapped to {@code node} in a united tree,
     * or null if {@code tree} does not contain such node.
     * It is inverse mapping of {@code #getNodeFor(TreeNode)}.
     *
     * @param node {@code TreeNode} in a unified tree
     * @param tree {@code TreeModel} to contain the returend
     * {@code TreeNode}
     *
     * @return {@code TreeNode} in {@code tree} mapped
     * to {@code node} in united tree, or null if no node in
     * {@code tree} is mapped to {@code node}
     *
     * @see {@code #getNodeFor(TreeNode)}
     */
    public TreeNode getNodeFor(TreeNode node, TreeModel tree);

    /**
     * Returns {@code Set}s of {@code UnitedNameTreeNode}
     * having the {@code name} as a {@code Map} indexed by the name of the higher
     * {@code UnitedNameTreeNode}.
     *
     * @param name to look for
     * @return {@code Set}s of {@code UnitedNameTreeNode}
     * having the {@code name} as a {@code Map} indexed by the name of the higher
     * {@code UnitedNameTreeNode}
     */
    public Map<String, Set<UnitedNameTreeNode>> getNodeParentalMapFor(String name);

    /**
     * Returns {@code Set}s of {@code UnitedNameTreeNode} having the {@code name}.
     *
     * @param name to look for
     * @return {@code Set}s of {@code UnitedNameTreeNode} having the {@code name}.
     */
    public Collection<UnitedNameTreeNode> getNodesFor(String name);

    /**
     * Returns {@code Enumeration} of {@code TreeNode}
     * in trees mapped to {@code node} in the united tree,
     * or null if no node is mapped to {@code node}
     *
     * @param node {@code TreeNode} in the united tree
     *
     * @return {@code Enumeration} of {@code TreeNode}
     * mapped to {@code node}, or null if no node is
     * mapped to {@code node}.
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
     * Retruns {@code TreePath} of the node in the
     * unified tree to which {@code node} in a tree
     * mapped, or null if ....
     */
    public TreePath getPathFor(TreeNode node);

    public TreePath getPathFor(TreePath path);

    public TreePath getPathFor(TreeNode node, TreeModel tree);

    public TreePath getPathFor(TreePath path, TreeModel tree);

    public Iterator<TreeModel> getTreesFor(TreeNode node);

    public Map<TreeModel, NameTreeNode> getTreesAndNodesFor(TreeNode node);

    /**
     * Maps {@code node} to {@code key}
     */
    public TreeNode mapNode(TreeNode key, TreeNode node);

}
