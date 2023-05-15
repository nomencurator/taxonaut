/*
 * NamedNode.java: an interface for rough set operation
 *
 * Copyright (c) 2002, 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.gui.swing.tree;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

/**
 * {@code NamedNode} provides an interface for rough set operation
 *
 * @version 	08 July 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NamedNode<T extends NamedNode<?>>
    extends TreeNode
{
    public static String SEPARATOR = "_";

    /**
     * Returns name of the {@code TreeNode} with rank prior to the name literal separeted by a {@code SEPARATOR}.
     *
     * @return concatinated rank and name literal separeted by a {@code SEPARATOR}
     */ 
    public String getRankedName();

    /**
     * Retruns name literal of the {@code TreeNode}
     *
     * @return name literal of the {@code TreeNode}
     */
    public String getLiteral();
    
    /**
     * Returnes number of child nodes matched with those of {@code node}
     * within given {@code depth} recursively, or less if depth of under either the
     * {@code NamedNode} or the {@code node} is smaller than {@code depth}.
     *
     * @param node {@code NamedNode} to be matched with.
     * @param depth to count number of child nodes recursively, or -1 to count
     * until leaf nodes.
     */
    public int getMatchedChildrenCount(NamedNode<?> node, int depth);

    /**
     * Adds given {@code NamedNode} to the positive Rough set of the {@code NemdNode}
     *
     * @param node {@code NamedNode} to be add to the positive Rough set of the {@code NemdNode}.
     */
    public void addIncludant(NamedNode<?> node);

    /**
     * Removes given {@code NamedNode} from the positive Rough set of the {@code NemdNode}
     *
     * @param node {@code NamedNode} to be removed from the positive Rough set of the {@code NemdNode}.
     */
    public void removeIncludant(NamedNode<?> node);

    /**
     * Returns the positive Rough set of the {@code NemdNode} as mapping from names to {@code NemdNode}.
     *
     * @return Map<String, NamedNode> representing the positive Rough set of the {@code NemdNode}
     */
    public Map<String, NamedNode<?>> getIncludants();

    /**
     * Removes all {@code NamedNode{@code s from the  positive Rough set of the {@code NemdNode}.
     */
    public void clearIncludants();

    /**
     * Adds given {@code NamedNode} to the negative Rough set of the {@code NemdNode}
     *
     * @param node {@code NamedNode} to be add to the negative Rough set of the {@code NemdNode}.
     */
    public void addExcludant(NamedNode<?> node);

    /**
     * Removes given {@code NamedNode} from the negative Rough set of the {@code NemdNode}
     *
     * @param node {@code NamedNode} to be removed from the negative Rough set of the {@code NemdNode}.
     */
    public void removeExcludant(NamedNode<?> node);

    /**
     * Returns the negative Rough set of the {@code NemdNode} as mapping from names to {@code NemdNode}.
     *
     * @return Map<String, NamedNode<?>> representing the negative Rough set of the {@code NemdNode}
     */
    public Map<String, NamedNode<?>> getExcludants();

    /**
     * Removes all {@code NamedNode{@code s from the  negative Rough set of the {@code NemdNode}.
     */
    public void clearExcludants();
    
    /**
     * Returns true if {@code node} is compatible with this {@code NamedNode}, i.e. there is no {@code NamedNode} common
     * in positive set of one {@code NamedNode}  and negative set of the other.
     *
     * @param node {@code NamedNode} of interest
     * @return true if {@code node} is compatible with this {@code NamedNode}
     */
    public boolean isCompatible(NamedNode<?> node);

    /**
     * Returns mapping containing {@code NamedNode}s which fall in both the positive set of one
     * {@code NamedNode} and the negativet set of the other, indexed by their names.
     *
     * @param node another {@code NamedNode} of interest
     * @return Map<String, NamedNode<?>> containing {@code NemdNode}s which fall in both the positive set of one {@code NamedNode} and the negativet set of the other, indexed by their names
     */
    public Map<String, NamedNode<?>> getCrossSection(NamedNode<?> node);

    /**
     * Returns a list of mappings containing {@code NamedNode}s which fall in intersections of
     * either positives set pair or negative sets pair of  {@code NamedNode} object having
     * this method and {@code node}.
     * Returend positive and negative pair represents a relaxed concept comparing with concpets
     * evaluated.
     *
     * @param node one of {@code NamedNode}s to be evaluated.
     * @return List of Map<String, NamedNode> reprsenting shared subest of postive and negative sents of {@code NemdNode}s to be examined.
     */
    public List<Map<String, NamedNode<?>>> getIntersection(NamedNode<?> node);

    /**
     * Returns a list of mappings containing {@code NamedNode}s which fall in unions of
     * either positives set pair or negative sets pair of  {@code NamedNode} object having
     * this method and {@code node}.
     * Returend positive and negative pair represents a narrower concept comparing with concpets
     * evaluated.
     *
     * @param node one of {@code NamedNode}s to be evaluated.
     * @return List of Map<String, NamedNode> reprsenting superset of postive and negative sents of {@code NemdNode}s to be examined.
     */
    public List<Map<String, NamedNode<?>>> getUnion(NamedNode<?> node);

    public double getCoverage(NamedNode<?> node);

    public String getToolTipText();

    public String getChildParentLiteral();

    public String getChildParentLiteral(boolean withRank);

    public String getPseudonymForUnnamedNode();

    public void setPseudonymForUnnamedNode(String pseudonym);

    public int getHeight();

    /**
     * Returnes parent node of the {@code NamedNode},
     * some node having a name on the parental chain if the parent node is unnamed,
     * or null if none node on the prental chain has name or if the 
     * {@code NamedNode} is the root node.
     */
    public NamedNode<?> getNamedParent();

    /**
     * Returns a child {@code NamedNode} having {@code name}
     * or null if this does not have such child node.
     *
     * @param name of child {@code NamedNode} to look for
     *
     * @return NamedNode having {@code name} or null if not found.
     */
    public T getChild(String name);

    /**
     * Returns an ancestoral {@code NamedNode} having {@code name}
     * or null if this does not have such node on its path.  It tries 
     * {@code getRankedName} method at fisrt, then {@code getLiteral}.
     *
     * @param name of ancestoral {@code NamedNode} to look for
     *
     * @return NamedNode having {@code name} or null if not found.
     */
    public T getAncestor(String name);

    /**
     * Returns a collection of child {@code NamedNode}s having {@code name}
     * or null if this node is a leaf node.  If the node does not have such
     * a name but not a leaf, returns a collection of zero size.
     *
     * @param name of child {@code NamedNode} to look for
     *
     * @return array of NamedNode having {@code name},
     * or null if this is a leaf node.
     */
    public Collection<T> getChildren(String name);
}
