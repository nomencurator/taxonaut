/*
 * NamedNode.java: an interface for rough set operation
 *
 * Copyright (c) 2002, 2003, 2005, 2014, 2015 Nozomi `James' Ytow
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
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeNode;

import org.nomencurator.model.NameUsage;

//import org.nomencurator.util.Map;

/**
 * {@code NamedNode} provides an interface for rough set operation
 *
 * @version 	21 Nov. 2015
 * @author 	Nozomi `James' Ytow
 */
public interface NamedNode<T extends NamedNode<?>>
    extends TreeNode
{
    public static String SEPARATOR = "_";

    /**
     * Returns name of the <tt>TreeNode</tt> with rank prior to the name literal separeted by a <tt>SEPARATOR</tt>.
     *
     * @return concatinated rank and name literal separeted by a <tt>SEPARATOR</tt>
     */ 
    public String getRankedName();

    /**
     * Retruns name literal of the <tt>TreeNode</tt>
     *
     * @return name literal of the <tt>TreeNode</tt>
     */
    public String getLiteral();
    
    /**
     * Returnes number of child nodes matched with those of <tt>node</tt>
     * within given <tt>depth</tt> recursively, or less if depth of under either the
     * <tt>NamedNode</tt> or the <tt>node</tt> is smaller than <tt>depth</tt>.
     *
     * @param node <tt>NamedNode</tt> to be matched with.
     * @param depth to count number of child nodes recursively, or -1 to count
     * until leaf nodes.
     */
    public int getMatchedChildrenCount(NamedNode<?> node, int depth);

    /**
     * Adds given <tt>NamedNode</tt> to the positive Rough set of the <tt>NemdNode</tt>
     *
     * @param node <tt>NamedNode</tt> to be add to the positive Rough set of the <tt>NemdNode</tt>.
     */
    public void addIncludant(NamedNode<?> node);

    /**
     * Removes given <tt>NamedNode</tt> from the positive Rough set of the <tt>NemdNode</tt>
     *
     * @param node <tt>NamedNode</tt> to be removed from the positive Rough set of the <tt>NemdNode</tt>.
     */
    public void removeIncludant(NamedNode<?> node);

    /**
     * Returns the positive Rough set of the <tt>NemdNode</tt> as mapping from names to <tt>NemdNode</tt>.
     *
     * @return Map<String, NamedNode> representing the positive Rough set of the <tt>NemdNode</tt>
     */
    public Map<String, NamedNode<?>> getIncludants();

    /**
     * Removes all <tt>NamedNode<tt>s from the  positive Rough set of the <tt>NemdNode</tt>.
     */
    public void clearIncludants();

    /**
     * Adds given <tt>NamedNode</tt> to the negative Rough set of the <tt>NemdNode</tt>
     *
     * @param node <tt>NamedNode</tt> to be add to the negative Rough set of the <tt>NemdNode</tt>.
     */
    public void addExcludant(NamedNode<?> node);

    /**
     * Removes given <tt>NamedNode</tt> from the negative Rough set of the <tt>NemdNode</tt>
     *
     * @param node <tt>NamedNode</tt> to be removed from the negative Rough set of the <tt>NemdNode</tt>.
     */
    public void removeExcludant(NamedNode<?> node);

    /**
     * Returns the negative Rough set of the <tt>NemdNode</tt> as mapping from names to <tt>NemdNode</tt>.
     *
     * @return Map<String, NamedNode<?>> representing the negative Rough set of the <tt>NemdNode</tt>
     */
    public Map<String, NamedNode<?>> getExcludants();

    /**
     * Removes all <tt>NamedNode<tt>s from the  negative Rough set of the <tt>NemdNode</tt>.
     */
    public void clearExcludants();
    
    /**
     * Returns true if <tt>node</tt> is compatible with this <tt>NamedNode</tt>, i.e. there is no <tt>NamedNode</tt> common
     * in positive set of one <tt>NamedNode</tt>  and negative set of the other.
     *
     * @param node <tt>NamedNode</tt> of interest
     * @return true if <tt>node</tt> is compatible with this <tt>NamedNode</tt>
     */
    public boolean isCompatible(NamedNode<?> node);

    /**
     * Returns mapping containing <tt>NamedNode</tt>s which fall in both the positive set of one
     * <tt>NamedNode</tt> and the negativet set of the other, indexed by their names.
     *
     * @param node another <tt>NamedNode</tt> of interest
     * @return Map<String, NamedNode<?>> containing <tt>NemdNode</tt>s which fall in both the positive set of one <tt>NamedNode</tt> and the negativet set of the other, indexed by their names
     */
    public Map<String, NamedNode<?>> getCrossSection(NamedNode<?> node);

    /**
     * Returns a list of mappings containing <tt>NamedNode</tt>s which fall in intersections of
     * either positives set pair or negative sets pair of  <tt>NamedNode</tt> object having
     * this method and <tt>node</tt>.
     * Returend positive and negative pair represents a relaxed concept comparing with concpets
     * evaluated.
     *
     * @param node one of <tt>NamedNode</tt>s to be evaluated.
     * @return List of Map<String, NamedNode> reprsenting shared subest of postive and negative sents of <tt>NemdNode</tt>s to be examined.
     */
    public List<Map<String, NamedNode<?>>> getIntersection(NamedNode<?> node);

    /**
     * Returns a list of mappings containing <tt>NamedNode</tt>s which fall in unions of
     * either positives set pair or negative sets pair of  <tt>NamedNode</tt> object having
     * this method and <tt>node</tt>.
     * Returend positive and negative pair represents a narrower concept comparing with concpets
     * evaluated.
     *
     * @param node one of <tt>NamedNode</tt>s to be evaluated.
     * @return List of Map<String, NamedNode> reprsenting superset of postive and negative sents of <tt>NemdNode</tt>s to be examined.
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
     * Returnes parent node of the <tt>NamedNode</tt>,
     * some node having a name on the parental chain if the parent node is unnamed,
     * or null if none node on the prental chain has name or if the 
     * <tt>NamedNode</tt> is the root node.
     */
    public NamedNode<?> getNamedParent();

    /**
     * Returns a child <tt>NamedNode</tt> having <tt>name</tt>
     * or null if this does not have such child node.
     *
     * @param name of child <tt>NamedNode</tt> to look for
     *
     * @return NamedNode having <tt>name</tt> or null if not found.
     */
    public T getChild(String name);

    /**
     * Returns an ancestoral <tt>NamedNode</tt> having <tt>name</tt>
     * or null if this does not have such node on its path.  It tries 
     * <tt>getRankedName</tt> method at fisrt, then <tt>getLiteral</tt>.
     *
     * @param name of ancestoral <tt>NamedNode</tt> to look for
     *
     * @return NamedNode having <tt>name</tt> or null if not found.
     */
    public T getAncestor(String name);

    /**
     * Returns a collection of child <tt>NamedNode</tt>s having <tt>name</tt>
     * or null if this node is a leaf node.  If the node does not have such
     * a name but not a leaf, returns a collection of zero size.
     *
     * @param name of child <tt>NamedNode</tt> to look for
     *
     * @return array of NamedNode having <tt>name</tt>,
     * or null if this is a leaf node.
     */
    public Collection<T> getChildren(String name);
}
