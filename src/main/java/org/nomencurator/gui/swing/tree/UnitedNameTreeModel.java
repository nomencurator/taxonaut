/*
 * UnitedNameTreeModel.java: a TreeModel as union of TreeModels
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

/*
  synopsys:
  the node to add could be either new name or existing name,
  either root node or other.
  Assuming top-down approach and preparation of new united nodes at the end of
  each node processing, it can't happen to add a new node
  with new higher node because it is already existed.
  It can happen only on the root node but the root node do not have higher node so
  any united node can match to that.
 */
package org.nomencurator.gui.swing.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.event.EventListenerList;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.QueryResultListenerAdaptor;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.Rank;

import org.nomencurator.gui.swing.event.UnitedNameTreeModelEvent;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelListener;

import org.nomencurator.util.CollectionUtility;
import org.nomencurator.util.StackTracer;

/**
 * <CODE>UnitedTreeModel</CODE> provides an integrated tree 
 * to navigate <code>NameTreeModel</code>s.
 * <P>
 * <strong><a name="integeration">Implementation Note:</a></strong>
 * This class provides a method to integrate named concepts captured as
 * nodes in  <CODE>NameTreeModel</CODE>s into a signle tree structure of
 * these names based on Rough set theory, aiming compative display
 * of name trees of which names are operable in a consistent way.
 * Biological txonomy is the main target of the model, although it can be
 * applicable in more generalised names and trees.
 *
 * A concept accompanies a pair of an extent and an intent.
 * Formal concpet analysis, a theoretical framework to manage 
 * concepts, modelled the pair as a pair of sets, a set of objects
 * falling in the conceptand and a set of attrributes which are shared
 * by all objects of the concept, respectively.  
 * Formal concepts are embeded into
 * a formal context where these concepts compose a concept lattice.
 * Formal concepts can be used to represent taxon concepts where
 * objects are individual organisams of the taxon and attributes are
 * character states shared by individuals of the taxon.
 * Each taxon concept has its own set of individuals which is a subset
 * of the set of all individuals under consideration.
 * <P>
 * Sets of objects and sets of are dual each other.  Inclusion series
 * of these sets makes sets of objects excluded from each concept
 * to be order isomorphic to the sets of attributes.
 * It enables to rpresend a formal concept also as pair objecst sets
 * incluced in and excluded from the concept, the latter is a proxy
 * of intent..
 * <P>
 * Fromal concepts are equal when  set paris, of extension and of
 * either intension or exclusion, are eual respectively.
 * Two sets are equal if each member of sets match.
 * A taxon concept is, however, given ostensively.
 * It is unlikey that we have complete list of individuals included
 * in a taxon.
 * Caracter status of a taxon can be refined by development of
 * understanding of the nature, or by using new technique.
 * Set equivalence is too strict to compare taxon concepts.
 * <P>
 * Routh set theory, a method to manage concepts with
 * incomplete information,
 * approximates concepts using equavalent classes,
 * instead of using set members directly.
 * It renormalises set members into equvalent classes, and sort
 * these classes into three categories, classes included in the concept,
 * classes excluded from the concpet, and classes falls in boundary,
 * i.e. partially included in and partially excluded from the concept.
 * These catecorie are called as positive sets, negative sets or
 * boundary sets respectively.  Positive sets is also known as
 * lower approximation of the concept, while union of positive and
 * boundary sets is known as upper aproximation of the concept.
 * <P>
 * A pair of positive and negative sets in Rough set theory enables relaxed
 * comparison of taxon concepts because the pare approximates extension
 * and exclusion, and hence intension of the concept.  Strict matching
 * of set members in Fromal concept analysis is replaced with
 * comparison of  a representative members of each euivalence classes.
 * Weaker comparison is available by examination of common member
 * between postive sets of a concept and negative sets of another concept,
 * and vise versa.
 * If those positive-negative sets share their members, concepts under
 * evaluation are incompatible becase sets excluded from a concept
 * are included in another concept.
 * Use of equivalent classes and postive-negative cross matching between
 * concepts enables ostensive comparison.
 * <P>
 * Taxon cocnepts are organised in a taxonomic hierarchy, or hierarchies.
 * More generally, Formal concepts are embedded into a formal context
 * where these concepts compose a concept lattice.
 * A concept hierarchy is a subset of concept lattice where choice of
 * concepts and edges shuld be specified separetely.
 * It implies that a concept and hierarchies under the concept should be
 * treated separetely, although a concept might have an implication of specific
 * hiearchy.
 * A concept hierarchy under a concept is organisation of members of lower concepts,
 * both extension of the highset concept and intension of (or exclusion from)  lower concepts.
 * A concept hiearchy above a concept, i.e. higher concept path, reflects organisation of
 * intesion of (or exclusion from) the concept.
 * The organisagtion of the hierarchy is independent from membership of the concept itself.
 * It implies that integaration of <code>NameTreeModel</code> into a 
 * <CODE>UnitedTreeModel</CODE> has two phases, detection of concpet equivalence
 * and integration of trees to navigate hierarchies.
 * <P>
 * <CODE>UnitedTreeModel</CODE> represents hierarchical organisation of
 * concepts by a tree.
 * Concepts equvalent, i.e. identical or compatible, of the same name may have different higher concepts
 * depending on organisation of higher hierarchy, i.e. organisation of intension or exclusion,
 * which is beyond control of the equivalent concepts.
 * Same name, equivalent concepts with different higher concept path should be under their
 * own higher concept path, hence <CODE>UnitedTreeModel</CODE> should have
 * different nodes for the concept depending on its higher concept path.
 * <P>
 * Concepts compatible but not identical, having the same name and having
 * equivalent path of higher concepts, would be treated as a single node in
 * <CODE>UnitedTreeModel</CODE> with union
 * of lower concepts.
 * <P>
 * Concepts of the same name with the same higher concept path but incompatible each other
 * retsults in duplicated nodes of <CODE>UnitedTreeModel</CODE> sharing the path of higher node.
 * There are two possible duplication, duplication of incompatible concpets themselves, or, 
 * lower concepts causing the incompatibility of the higher concept.
 * The latter is simpler becase duplicated nodes can be distinguished
 * by their higher concept path.  Concepts under duplicated node also give duplicate nodes in
 * the <CODE>UnitedTreeModel</CODE>, hence having different higher path itself does not indicate
 * the incomatibility directly.
 * <P>
 * Synonymy, i.e. compatible concepts having different names results in duplicated or multiplicand
 * nodes having equvalent lower names.  Their position in the tree depends on higher concpet
 * path of each synonym.
 * <P>
 * The class <CODE>UnitedTreeModel</CODE> uses following algorithm to integrate
 * name trees in top-down way based on background given above.  
 * The alogrithm can be divided into three phases: incorporation of the first
 * tree into the model, placing root node of the second or later tree to be integrated,
 * and recursive integration of remaining node in the tree.
 *
 * <DL COMPACT>
 * <DT>STEP 0
 * <DD>Preparation of a TreeModel in construction.<P>
 * Provide a <CODE>TreeModel</CODE> having a dummy root node with
 * name of zero length litreal.
 * Prepare three maps, 
 * <OL>
 * <LI>from name literals to maps from a higher name literals to
 * a set of unified nodes,
 * <LI>from unified nodes to sets of nods in inegrated trees, and 
 * <LI>from nodes in integrated trees to unified nodes.
 * </OL>
 * <DT>STEP 0.5
 * If there is only root node in the tree model, i.e. integrating the fist treee,
 * create a Unified node for each node of the tree node and put it
 * into the United tree with populating the three maps.
 * It is unnecessary to check against existing tree for the first one,
 * assuming that the source tree does not have inconsistency.
 * Integration is over for the fist tree.  Wait for the next integration.
 * <DT>STEP 1
 * <DD>Take the root node of the tree of names to be integreted.
 * <DT>STEP 2
 * <DD>Loop entry point<P>
 * Look for a node or nodes in the united tree having the same literal value of the
 * node under process.
 * Factors affects on process of nodes are:<P>
 * <OL>
 * <LI> Presence of the name in united tree.
 * <LI> Uniqueness of the united node of the name.
 * <LI> Higher name matching between the node to integrate and united nodes sharing the name.
 *</OL>
 * If there is no node of the name literal in the united tree, goto STEP 3.
 * Else if the node to integrate is the root node of the tree,  and 
 * there is only a single united node of the name litereal, then goto STEP 4.1.
 * Else if the node to integrate is the root node of the tree then goto STEP 3.
 * Else if there is only one united node having parental literal same to that of the node, goto STEP 4.1.
 * Else if there are two or more united nodes having parental literal same to the node, goto STEP 5.
 * Else if  there is only one united node of the name, and it is a direct child node of the root node, goto STEP 4.2.
 * Else if  there is only one united node of the name, goto STEP 4.3.
 * Else if there is no united node of the name shareing its parental literal with the node, goto STEP 5.
 * Note that case STEP 3 is expected to happen only on the root node of tree to integrate
 * because child new name is already added to the united tree in the SETP 8 of
 * the previous recursion.
 * <DT>STEP 3
 * <DD>Processing a new name.<P>
 * Add a new unified node havind the literal value same to the node as a
 * child node of the unified node having the literal value same to the higner node
 * of the node to be inegrated, or child of the root node of the united tree if the node
 *  to be integrated is the root node.
 * If the node to be integrated is the root node,
 * add the new unified node as a child node  of the unnamed root node of the
 * united tree.
 * Add the new unified node to the three maps.
 * Goto STEP 8.
 * <DT>STEP 4
 * <DD>Unique path to the name.<P>
 * <DT>STEP 4.1
 * Compare literal value of parents of the node and the unified node having the same name.
 * If parents' literal values match, or, if the node under process is the root node , 
 * then corresponding united node is already in the united tree.  Goto STEP 7
 * with the unified node found.
 * <DT>STEP 4.2
 * Else if the node is not the root and the parent of the united node is root,
 * move the united node to be a child node of the higher node of the node
 * which must be already in united tree by previsous recursion.
 * Add mappings releveant to the node, then Goto STEP 8.
 * <DT>STEP 4.3
 * Else, i.e. both parents have non-empty literal different each other, then
 * try path matchng.  If one of paret names on parental path of the other parent,
 * the partial path between the parent name on the other's parental path should be
 * treated as an intermidiate path of the name.  If the partial path is a part of the
 * united tree, it is already integrated.  If the partial path is a part of the tree to be
 * integrated, the partial path must be inserted between the shared parent name
 * and the name.  It requires to move the united node at the end of path of which
 * terminal is lower than the other.  Note that the path itself is already integrated
 * into the united tree because the path is already visited.  Add mappings between
 * the unified node and the node in the tree under process.
 * Goto STEP 8.
 * <DT>STEP 4.4
 * If path matching is unsuccessful, then
 * create new unified node with the name literal of the node and insert as a child node
 * of the united node representing the parent node of the node, and add the created
 * unified node to the three maps.
 * Goto STEP 8.
 * <DT>STEP 5
 * <DD>Multiple path to the name, or multiple united nodes of the same higher literal.<P>
 * There are two or more unified nodes of the name having different parents in the unified tree.
 * The parents may have the same name or different names.
 * Compare name literal of parents of the node and unified nodes with the literal
 * of the node.
 * <DT>STEP 5.1
 * If there are unified nodes of the literal having the parent literal matches to that of the node,
 * try parental path match to find the best one.  If found, goto STEP 4 with the united node.
 * Else, goto STEP 3.
 * <DT>STEP 5.2
 * Else if there is a unified node of the literal as a child node of root, goto STEP 6.
 * <DT>STEP 5.3
 * Else, i.e. there is no unified node with the literal rooted or having the same parent,
 * try path matching.  If unified node having matched path found, take lowermost
 * matching one.  If the lowermost is unique, then move the unified node to inegrate
 * the path and add mappings between.  
 * If path matching is unsuccessful, or lowermost matchied is not unique, then 
 * create new united node with the literal of the node and insert as a child node
 * of the united node representing the parent node of the node, and add the crated
 * unified node to the three maps.
 * Goto STEP 8.
 * <DT>STEP 6
 * <DD>Compatibility test.<P>
 * Note: as a zero-th order approximation, we assume that taxon concepts have the same parent
 * can be integrated.  However, we need to check compatibility using includants and excludants
 * of taxon concepts.   This step is a place holder for such method so simply goto STEP 8.
 * When implemented the method, then compare compatibility.  If incompatible, create a
 * new unified node and insert it as a child appropriate parent unified node.
 * Goto STEP 8.
 * <DT>STEP 7
 * <DD>Correspoinding node is already in the <tt>UnitedTree</tt><P>
 * Add mappings between the <tt>UnifiedNode</tt> and the source node.
 * Then GOTO STEP 8. 
 * <DT>STEP 8
 * <DD>For each child taxon, create a unifide node representing the child taxon and put as child of
 * the unified taxonis if it is a new name to the united tree. 
 * If there is no child taxa anymore, then take the next node from the tree to be united in breadth first
 * and then goto STEP 2 with the node.
 * Note that new names added at this step will be examined again throgh SETP 2 and then.
 * If no node remains in the tree, tree integeration has done.
 * </DL>       
 *
 * Note: Here uniquenss of ranked name literal is assumed.  It is inappripriate assumptions
 * for phylogenetic tree containing unnamed nodes, parehaps not intended to be a taxon.
 * There are two ways to manage this:
 * <OL>
 * <LI> Manage unnamed node as special <CODE>NameUsage</CODE>s</LI>
 * <LI> Extract named nodes only.  Phylogenetic sequence can be captured by sequence of
 * lower taxa</LI>
 * </OL>
 * <P>
 *
 * @version 	25 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UnitedNameTreeModel
    extends DefaultTreeModel
    implements TreeModelListener,
	       NodeMapper //,
	       //FIXME!
	       //	       ObjectExchanger<NamedNode, NamedNode>
	       // NameUsageExchanger<NameUsage, NameUsage>
{
    private static final long serialVersionUID = -5470307104391635186L;

    protected static int NEW_LOWERS = 0;
    protected static int INDIRECT_LOWERS = NEW_LOWERS + 1;
    protected static int DIRECT_LOWERS = INDIRECT_LOWERS + 1;
    protected static int DESCENDANTS = DIRECT_LOWERS + 1;
    protected static int ASCENDANTS = DESCENDANTS + 1;
    protected static int UNMAPPED = ASCENDANTS + 1;
    protected static int ALL_NEW = INDIRECT_LOWERS + 1;
    protected static int SOME_MAPPED = UNMAPPED + 1;

    protected static int NEW_LOWERS_UNRANKED = NEW_LOWERS + 3;
    protected static int INDIRECT_LOWERS_UNRANKED = INDIRECT_LOWERS + 3;
    protected static int DIRECT_LOWERS_UNRANKED = DIRECT_LOWERS + 3;

    public static int STRICT = 0;
    public static int IGNORE_INTERMEDIATE_RANK = 1;
    public static int IGNORE_RANK = 2;
    public static int STEMMING = 4;

    public static String ROOT_NAME = "";


    /**
     * {@code Map} of {@code Map}s of {@code Set} of unified {@code TreeNode}s,
     * indexed by parental name literal without rank,
     * indexed by unranked names appeard in {@code NameTreeModel}s
     * added to this {@code TreeModel}.
     * It provides a mapping from each name to its usages
     * distinguished by their parental name.
     */
    protected Map<String, Map<String, Set<UnitedNameTreeNode>>> names;

    /**
     * Mapping from fragments of unranked name to sets of <tt>NamedNode</tt>s of which name starts with the fragment
     */
    protected Map<String, Set<NamedNode<?>>> forwardMatches;

    /**
     * Mapping from fragments of unranked name to sets of <tt>NamedNode</tt>s of which name ends with the fragment
     */
    protected Map<String, Set<NamedNode<?>>> backwardMatches;

    /**
     * <CODE>Map</CODE> from
     * each <CODE>NameTreeNode</CODE> in <CODE>NameTree</CODE>
     * to a <CODE>UnitedNameTreeNode</CODE> in this
     * <CODE>UnitedNameTreeModel</CODE>.
     */
    protected Map<TreeNode, TreeNode> rankedNodes;

    protected Map<UnitedNameTreeNode, Set<NameTreeNode>> unitedNodeToNodeSet;
    protected Map<UnitedNameTreeNode, Map<TreeModel, NameTreeNode>> unifiedByTree;


    protected List<TreeModel> trees;

    protected Map<TreeModel, NameTreeNode> focalNodes;

    protected Map<UnitedNameTreeNode, Set<NameTreeNode>> unitedChildrens;

    protected Map<String, Map<String, Set<UnitedNameTreeNode>>> multipleChildrens;

    protected TreeModel eventSource;

    protected EventListenerList listeners;

    protected int unificationMode;


    public UnitedNameTreeModel()
    {
	this(0);
    }

    public UnitedNameTreeModel(Collection<TreeModel> treeModels)
    {
	this(treeModels.size());
	for(TreeModel model: treeModels) {
	    add(model);
	}
    }

    public UnitedNameTreeModel(TreeModel[] treeModels)
    {
	this(treeModels.length);
	for(TreeModel model:treeModels) {
	    add(model);
	}
    }

    public UnitedNameTreeModel(int size)
    {
	super(new UnitedNameTreeNode(ROOT_NAME));
	((UnitedNameTreeNode)getRoot()).setLiteral(ROOT_NAME);
	setUnificationMode(STRICT);
	createHashtables();
	trees = Collections.synchronizedList(new ArrayList<TreeModel>(size));
    }

    public int getUnificationMode()
    {
	return unificationMode;
    }

    public void setUnificationMode(int mode)
    {
	if(mode == getUnificationMode())
	    return;

	unificationMode = mode;

	/*
	if(trees.isEmpty())
	    return;

	synchronized(trees) {
	    TreeModel[] models = 
		trees.toArray(new TreeModel[trees.size()]);
	    
	    trees.clear();
	    clearHashtables();
	    for (TreeModel model: models) {
		add(model);
	    }
	    models = null;
	}
	*/
    }

    protected void createHashtables()
    {
	names = new HashMap<String, Map<String, Set<UnitedNameTreeNode>>>();
	forwardMatches = new HashMap<String, Set<NamedNode<?>>>();
	backwardMatches = new HashMap<String, Set<NamedNode<?>>>();
	rankedNodes = new HashMap<TreeNode, TreeNode>();
	unitedNodeToNodeSet = new HashMap<UnitedNameTreeNode, Set<NameTreeNode>>();
	unifiedByTree = 
	    new HashMap<UnitedNameTreeNode, Map<TreeModel, NameTreeNode>>();
	unitedChildrens = new HashMap<UnitedNameTreeNode, Set<NameTreeNode>>();
	multipleChildrens = new HashMap<String, Map<String, Set<UnitedNameTreeNode>>>();
	focalNodes = new HashMap<TreeModel, NameTreeNode>();
    }

    protected void clearHashtables()
    {
	clearCollectionMetaMap(names);

	clearCollectionMap(forwardMatches);
	clearCollectionMap(backwardMatches);
	rankedNodes.clear();
	clearCollectionMap(unitedNodeToNodeSet);
	clearMetaMap(unifiedByTree);
	clearCollectionMap(unitedChildrens);
	clearCollectionMetaMap(multipleChildrens);
	focalNodes.clear();
    }

    protected <K, L, V> void clearMetaMap(Map<K, Map<L, V>> metamap)
    {
	metamap.values().parallelStream().forEach(map -> map.clear());
	metamap.clear();
    }

    protected <K, V extends Collection<?>> void clearCollectionMap(Map<K, V> metamap)
    {
	metamap.values().parallelStream().forEach(map -> map.clear());
	metamap.clear();
    }

    protected <K, L, V extends Collection<?>> void clearCollectionMetaMap(Map<K, Map<L, V>> metamap)
    {
	metamap.values().parallelStream().forEach(map -> clearCollectionMap(map));
	metamap.clear();
    }


    public int getTreeCount()
    {
	return trees.size();
    }

    public Collection<TreeModel> getTreeCollection()
    {
	return trees;
    }

    public List<TreeModel> getTreeList()
    {
	return trees;
    }

    public Iterator<TreeModel> getTrees()
    {
	return trees.iterator();
    }

    public ListIterator<TreeModel> getTreeListIterator()
    {
	return trees.listIterator();
    }

    /**
     * Sets a mapping entry from {@code key} to {@code node}.
     * It returns a {@code TreeNode} assigned to the {@code key} previously,
     * or null if nothing is assigned.
     *
     * @param key {@code TreeNode} to be mapped to{@code node}.
     * @param node {@code TreeNode} to be assigned to the {@code key}.
     *
     * @return {@code TreeNode} previously assigned to the {@code key}, or null
     * if the is no mapping for the {@code key}.
     */
    protected TreeNode setNodeFor(TreeNode key, TreeNode node)
    {
	return rankedNodes.put(key, node);
    }


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
    public TreeNode getNodeFor(TreeNode node)
    {
	return rankedNodes.get(node);
    }

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
    public TreeNode getNodeFor(TreeNode node, TreeModel tree)
    {
	if(node == null)
	    return null;
	Map<TreeModel, NameTreeNode> map = unifiedByTree.get(node);
	if(map == null)
	    return null;

	return (TreeNode)map.get(tree);
    }

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
    public Iterator<? extends TreeNode> getNodesFor(TreeNode node)
    {
	Set<NameTreeNode> nodeSet = unitedNodeToNodeSet.get(node);
	return (nodeSet == null)? null:nodeSet.iterator();
    }

    /**
     * Maps <CODE>node</CODE> to <CODE>key</CODE>
     */
    public TreeNode mapNode(TreeNode key, TreeNode node)
    {
	TreeNode previous = null;
	if(node != null && node instanceof UnitedNameTreeNode) {
	    previous = rankedNodes.put(key, node);
	    UnitedNameTreeNode unified = (UnitedNameTreeNode)node;
	    TreeNode parent = key.getParent();
	    if  (parent != null &&  parent instanceof NamedNode)
		indexByNames(unified, (NamedNode<?>)parent);
	    else
		indexByNames(unified);
	}
	return previous;
    }

    public TreePath[] getPathsFor(TreePath path)
    {
	if(path == null)
	    return null;
	return getPathsFor((TreeNode)path.getLastPathComponent());
    }

    public TreePath[] getPathsFor(TreeNode node)
    {
	if(node == null)
	    return null;
	node = getNodeFor(node);
	if(node == null || !(node instanceof NamedNode))
	    return null;

	return getPathsFor(((NamedNode<?>)node).getLiteral());
    }

    public TreePath[] getPathsFor(String literal)
    {
	if(literal == null)
	    return null;

	final	Collection<UnitedNameTreeNode> unifiedNodes = getNodesFor(literal);
	if(unifiedNodes == null || unifiedNodes.isEmpty())
	    return null;
	final List<TreePath> pathList = new ArrayList<TreePath>(unifiedNodes.size());
	unifiedNodes.forEach(node -> pathList.add(new TreePath(node.getPath())));
	return pathList.toArray(new TreePath[unifiedNodes.size()]);
    }

    public TreePath getPathFor(TreePath path)
    {
	if(path == null)
	    return null;
	return getPathFor((TreeNode)path.getLastPathComponent());
    }

    /**
     * Retruns <CODE>TreePath</CODE> of the node in the
     * unified tree to which <CODE>node</CODE> in a tree
     * mapped, or null if 
     */
    public TreePath getPathFor(TreeNode node)
    {
	return getPath(getNodeFor(node));
    }

    protected TreePath getPath(TreeNode node)
    {
	if(node == null)
	    return null;

	if(node instanceof DefaultMutableTreeNode)
	    return new TreePath(((DefaultMutableTreeNode)node).getPath());

	List<TreeNode> nodePath = new ArrayList<TreeNode>();
	while(node != null) {
	    nodePath.add(node);
	    node = node.getParent();
	}

	TreePath path = new TreePath(nodePath.toArray());
	nodePath.clear();
	return path;
    }

    public TreePath getPathFor(TreeNode node, TreeModel tree)
    {
	node = getNodeFor(node, tree);
	if(node == null)
	    return null;
	if(node instanceof DefaultMutableTreeNode)
	    return new TreePath(((DefaultMutableTreeNode)node).getPath());
	return null; //!
    }

    public TreePath getPathFor(TreePath path, TreeModel tree)
    {
	if(path == null)
	    return null;
	return getPathFor((TreeNode)path.getLastPathComponent(), tree);
    }

    public Iterator<TreeModel> getTreesFor(TreeNode node)
    {
	Map<TreeModel, NameTreeNode> map = unifiedByTree.get(node);
	if(map == null)
	    return null;

	return map.keySet().iterator();
    }

    public Map<TreeModel, NameTreeNode> getTreesAndNodesFor(TreeNode node)
    {
	return unifiedByTree.get(node);
    }

    public Map<String, Set<UnitedNameTreeNode>> getNodeParentalMapFor(String name)
    {
	return (name == null)? null : names.get(name);
    }

    public final Collection<UnitedNameTreeNode> getNodesFor(String literal)
    {
	final Map<String, Set<UnitedNameTreeNode>> map = getNodeParentalMapFor(literal);
	if (map == null || map.size() == 0) {
	    return Collections.emptySet();
	}
	final Collection<UnitedNameTreeNode> nodes = new HashSet<UnitedNameTreeNode>();
	map.values().forEach(set -> nodes.addAll(set));
	return nodes;
    }

    public final Set<UnitedNameTreeNode> getNodeFor(String name, String parentName)
    {
	Map<String, Set<UnitedNameTreeNode>> parents = getNodeParentalMapFor(name);
	return (parents == null)? null:parents.get(parentName);
    }

    public final UnitedNameTreeNode getNodeFor(String name, UnitedNameTreeNode parentNode)
    {
	if(name == null || parentNode == null)
	    return null;
	Map<String, Set<UnitedNameTreeNode>> nodeSets = getNodeParentalMapFor(name);
	if(nodeSets == null)
	    return null;
	Set<UnitedNameTreeNode> nodes = nodeSets.get(parentNode.getLiteral());
	if(nodes == null)
	    nodes = nodeSets.get(parentNode.getUnrankedName());
	if(nodes == null)
	    return null;
	UnitedNameTreeNode node = null;
	Iterator<UnitedNameTreeNode> children = nodes.iterator();
	while(node == null && children.hasNext()){
	    node = children.next();
	    if(node.getParent() != parentNode)
		node = null;
	}
	return node;
    }

    /**
     *
     */
    protected void indexByNames(UnitedNameTreeNode node)
    {
	if(node == null)
	    return;
	TreeNode parent = node.getParent();
	if (parent instanceof NamedNode)
	    indexByNames(node, (NamedNode<?>)parent);
    }

    /**
     *
     */
    protected void indexByNames(UnitedNameTreeNode node, NamedNode<?> parent)
    {
	if(node == null || parent == null)
	    return;

	String name = node.getLiteral();
	String parentName = parent.getLiteral();

	Map<String, Set<UnitedNameTreeNode>> parentIndex = names.get(name);
	if(parentIndex == null) {
	    parentIndex = new HashMap<String, Set<UnitedNameTreeNode>>();
	    names.put(name, parentIndex);
	}

	Set<UnitedNameTreeNode> parentSet = parentIndex.get(parentName);
	if(parentSet == null) {
	    parentSet = new HashSet<UnitedNameTreeNode>();
	    parentIndex.put(parentName, parentSet);
	}
	if(!parentSet.contains(node))
	    parentSet.add(node);
    }

    /**
     *
     */
    protected void unindexByNames(NamedNode<?> parent)
    {
    }

    protected void unindexByNames(UnitedNameTreeNode node)
    {
    }

    /**
     * Integrates <CODE>tree</CODE> to this unified tree using the
     * algorithm described in the <a href="#integration">Implementation Note</a>.
     *
     * param tree <CODE>TreeModel</CODE> to be integrated
     */
    public void add(TreeModel tree) {
	add(tree, true, null);
    }

    /**
     * Copy given {@code tree} structure to this {@coce UnitedNameTreeModel}.
     * It is expected to be called for the first {@code TreeModel}.
     *
     * @param tree the first {@code TreeModel} to be integrated
     */
    protected synchronized void copy(TreeModel tree, NameTreeNode focus)
    {
	Map<NameTreeNode, Map<NameTreeNode, List<NameTreeNode>>> reductions = null;
	if(trees.isEmpty()) {
	    //simple mapping for the first tree
	    synchronized(tree) {
		List<NameTreeNode> traverser = new ArrayList<NameTreeNode>();
		Set<NameTreeNode> toSkip = new HashSet<NameTreeNode>();
		Enumeration<?> e = ((NameTreeNode)tree.getRoot()).breadthFirstEnumeration();
		while (e.hasMoreElements()) {
		    traverser.add((NameTreeNode)e.nextElement());
		}
		for (int i = 0; i < traverser.size(); i++) {
		    NameTreeNode node = traverser.get(i);

		    if (toSkip.contains(node)) continue;

		    UnitedNameTreeNode unitedNode = (UnitedNameTreeNode)getNodeFor(node);
		    if (unitedNode == null) {
			unitedNode = new UnitedNameTreeNode(node);
			NameTreeNode parentNode = (NameTreeNode)node.getParent();
			UnitedNameTreeNode unitedParent = (parentNode == null)?
			    (UnitedNameTreeNode)getRoot() : (UnitedNameTreeNode)getNodeFor(node);
			if(unitedParent !=  null) {
			    add(unitedParent, unitedNode);
			}
			mapNode(tree, node, unitedNode);
		    }

		    if (node.isLeaf()) continue;

		    Map<NameTreeNode, List<NameTreeNode>> reduction = reduce(node, toSkip);
		    if (reduction != null) {
			if (reductions == null) {
			    reductions  = new HashMap<NameTreeNode, Map<NameTreeNode, List<NameTreeNode>>>();
			}
			reductions.put(node, reduction);
		    }

		    if (reduction != null) {
			Set<NameTreeNode> keys = reduction.keySet();
			for (NameTreeNode reduced : keys) {
			    UnitedNameTreeNode primary = (UnitedNameTreeNode)getNodeFor(reduced);
			    if (primary == null) {
				primary = new UnitedNameTreeNode(reduced);
				add(unitedNode, primary);
				mapNode(tree, reduced, primary);
			    }
			    List<NameTreeNode> subsidiaries = reduction.get(reduced);
			    int toInsert = Integer.MAX_VALUE;
			    for (NameTreeNode subsidiary : subsidiaries) {
				setNodeFor(subsidiary, primary);
				toSkip.add(subsidiary);
				int index = traverser.indexOf(subsidiary);
				if (index < toInsert)
				    toInsert = index;
			    }
			    if (!traverser.contains(reduced))
				traverser.add(toInsert, reduced);
			}
		    }

		    Enumeration<?> childEnum = node.children();
		    List<NameTreeNode> children = new ArrayList<NameTreeNode>(node.getChildCount());
		    while (childEnum.hasMoreElements()) {
			children.add((NameTreeNode)childEnum.nextElement());
		    }
		    for (NameTreeNode child : children) {
			if (toSkip.contains(child)) continue;

			UnitedNameTreeNode childNode = (UnitedNameTreeNode)getNodeFor(child);
			if (childNode == null) {
			    childNode = new UnitedNameTreeNode(child);
			    add(unitedNode, childNode);
			    mapNode(tree, child, childNode);
			}
		    }
		    children.clear();
		}
		toSkip.clear();
		traverser.clear();
	    }

	    trees.add(tree);

	    fireTreeAdded(tree);
	}
    }

    protected Map<NameTreeNode, List<NameTreeNode>> reduce(NameTreeNode parentNode, 
							   NameTreeNode primary,
							   List<NameTreeNode> subtrees,
							   Map<NameTreeNode, List<NameTreeNode>> reduction,
							   Set<NameTreeNode> toSkip)
    {
	if (parentNode == null || primary == null ||
	    subtrees == null || subtrees.size() == 0)
	    return null;

	if (reduction == null)
	    reduction = new HashMap<NameTreeNode, List<NameTreeNode>>();

	reduction.put(primary, new ArrayList<NameTreeNode>(subtrees));
	if (toSkip != null)
	    toSkip.addAll(subtrees);
	for (NameTreeNode subtree : subtrees) {
	    parentNode.remove(subtree);
	    if (subtree.isLeaf()) continue;

	    Enumeration<?> grandchildEnum = subtree.children();
	    List<MutableTreeNode> grandchildren = new ArrayList<MutableTreeNode>();
	    while (grandchildEnum.hasMoreElements()) {
		Object object = grandchildEnum.nextElement();
		if (object instanceof MutableTreeNode) {
		    grandchildren.add((MutableTreeNode)object);
		}
	    }
	    for (MutableTreeNode grandchild : grandchildren) {
		    primary.add(grandchild);
	    }
	    grandchildren.clear();
	}
	return reduction;
    }

    /**
     * Reduces duplication of names just under the {@code parentNode},
     * i.e. multiple child {@code NameTreeNode}s of the {@code parentNode}
     * sharing the same name literal if exist.  Redundant child nodes unchosen
     * are removed from child node list of the {@code parentNode} and stored
     * in {@code skippers} if given.  It returns mapping from each primary
     * child node to a list of subsidiary nodes of the same name literal.
     *
     * @param parentNode of which child nodes' redundancey to be reduced.
     * @param skippers to which removed redundant nodes to be stored, or
     * null if unnecessary to store.
     * @return
     */
    protected Map<NameTreeNode, List<NameTreeNode>> reduce(NameTreeNode parentNode,
							   Set<NameTreeNode> skippers)
    {
	if (parentNode == null || parentNode.isLeaf())
	    return null;

	Map<NameTreeNode, List<NameTreeNode>> reduction = null;

	int multiplicity = 0;
	Map<String, Set<NameTreeNode>> names = new HashMap<String, Set<NameTreeNode>>();
	Enumeration<?> children = parentNode.children();
	while (children.hasMoreElements()) {
	    Object childObject = children.nextElement();
	    if (childObject instanceof NameTreeNode) {
		NameTreeNode childNode = (NameTreeNode)childObject;
		String literal = childNode.getLiteral();
		Set<NameTreeNode> namedChildren = names.get(literal);
		if (namedChildren == null) {
		    namedChildren = new HashSet<NameTreeNode>();
		    names.put(literal, namedChildren);
		}
		namedChildren.add(childNode);
		int size = namedChildren.size();
		multiplicity = (multiplicity > size) ? multiplicity : size;
	    }
	}
	if (multiplicity > 1) {
	    reduction = new HashMap<NameTreeNode, List<NameTreeNode>>();
	    Set<String> literals = names.keySet();
	    for (String literal : literals) {
		NameTreeNode primary = null;
		Set<NameTreeNode> namedChildren = names.get(literal);
		List<NameTreeNode> leaves = new ArrayList<NameTreeNode>();
		List<NameTreeNode> subtrees = new ArrayList<NameTreeNode>();
		List<NameTreeNode> implications = new ArrayList<NameTreeNode>();
		List<NameTreeNode> explications = new ArrayList<NameTreeNode>();
		for (NameTreeNode childNode : namedChildren) {
		    if (childNode.isLeaf()) {
			leaves.add(childNode);
			}
		    else {
			subtrees.add(childNode);
		    }

		    if (childNode.getNameUsage().isImplicit()) {
			implications.add(childNode);
		    }
		    else {
			explications.add(childNode);
		    }
		}

		if (implications.size() != 0 && explications.size() == 1) {
		    reduction = reduce(parentNode, explications.get(0), implications, reduction, skippers);
		}
		else if (subtrees.size() == 0) {
		    if (leaves.size() > 1) {
			primary = leaves.get(0);
			leaves.remove(0);
			reduction.put(primary, leaves);
			if (skippers != null)
			    skippers.addAll(leaves);
			leaves.stream().forEach(leaf -> parentNode.remove(leaf));

		    }
		}
		else {
		    switch (leaves.size()) {
		    case 0:
			primary = new NameTreeNode(new DefaultNameUsageNode());
			primary.getNameUsage().setLiteral(literal);
			primary.getNameUsage().setRank(subtrees.get(0).getNameUsage().getRank());
			((DefaultNameUsageNode)primary.getNameUsage()).setImplicit(true);
			parentNode.insert(primary, parentNode.getIndex(subtrees.get(0)));
			reduction = reduce(parentNode, primary, subtrees, reduction, skippers);
			break;
		    case 1:
			if (subtrees.size() == 1) {
			    reduction.put(subtrees.get(0), new ArrayList<NameTreeNode>(leaves));
			    parentNode.remove(leaves.get(0));
			    if (skippers != null)
				skippers.add(leaves.get(0));
			}
			else {
			    reduction = reduce(parentNode, leaves.get(0), subtrees, reduction, skippers);
			}
			break;
		    default:
			primary = leaves.get(0); 
			reduction = reduce(parentNode, primary, subtrees, reduction, skippers);
			leaves.remove(primary);
			if (skippers != null)
			    skippers.addAll(leaves);
			leaves.stream().forEach(leaf ->parentNode.remove(leaf));
		    }
		}
		leaves.clear();
		subtrees.clear();
		implications.clear();
		explications.clear();
	    }
	}
	clearCollectionMap(names);

	return reduction;
    }

    /**
     * Integrates <CODE>tree</CODE> to this unified tree using the
     * rithm described in the <a href="#integration">Implementation Note</a>.
     *
     * param tree <CODE>TreeModel</CODE> to be integrated
     */
    public synchronized void add(TreeModel tree, boolean useRankedName, NameTreeNode focus)
    {
	if(tree == null)
	    return;

	// synchronized(this) {
	    if(trees == null) {
		trees = new ArrayList<TreeModel>();
	    }
	    else if(trees.contains(tree)) {
		return;
	    }
	// }

	if(!(tree instanceof NameTreeModel))
	    return;

	// TBD pivotting implementation
	// first tree
	// STEP 0.5
	if(trees.isEmpty()) {
	    copy(tree, focus);
	    return;
	} //end of simple mapping for the first tree


	NameTreeNode node = null;
	Enumeration<?> e = null; 
	List<NameTreeNode> traverser = null;

	// Capture the root of shared trees
	// in the case that the united tree is
	// pro parte of the tree
	NameTreeNode mappedRoot = null;

	// Capture the last node before
	// mappedRoot in the enumeration
	NameTreeNode previousNode = null;

	Map<NameTreeNode, Map<NameTreeNode, List<NameTreeNode>>> reductions = null;


	//copy nodes of the tree
	synchronized(tree) {
	    node = (NameTreeNode)tree.getRoot();

	    // if trees is empty, then there is
	    // no unitied tree.  it can't happen....
	    boolean isProParte = !trees.isEmpty();
	    if(!isProParte)
		mappedRoot = node;

	    e = node.breadthFirstEnumeration();
	    traverser = new ArrayList<NameTreeNode>();
	    while(e.hasMoreElements()) {
		//copy to traverser
		node = (NameTreeNode)e.nextElement();
		traverser.add(node);
		
		// pro parte check
		if(isProParte){
		    String rankedName = 
			node.getLiteral();
		    String literal = node.getLiteral();
		    if(mappedRoot == null) {
			previousNode = node;
			if(getNodeParentalMapFor(rankedName) != null) {
			    mappedRoot = node;
			}
		    }
		}
	    }
	}

	UnitedNameTreeNode unitedParent = null;
	String viewName = ((NameTreeModel)tree).getViewName();
	Set<NameTreeNode> skippers = new HashSet<NameTreeNode>();

	// if there are nodes in the tree higher than nodes in 
	// the unified tree....
	/*
	Vector<NameTreeNode> superAncestor = null;
	if(previousNode != null &&
	   previousNode != (NameTreeNode)tree.getRoot()) {
	    node = null;
	    superAncestor = new Vector<NameTreeNode>();
	    while(e.hasMoreElements() && node != previousNode) {
		superAncestor.add((NameTreeNode)e.nextElement());

	}
	*/

	//STEP 2 ? or 1?

	for (int traverse = 0; traverse < traverser.size(); traverse++) {
	    node = traverser.get(traverse);

	    if (skippers.contains(node))
		continue;

	    // Since it is breadth-first traverse,  the node udner process
	    // may be mapped to an UnifedNameTreeNode as
	    // a child node in one of previous traversal steps.  Check it at first.
	    UnitedNameTreeNode unitedNode =	(UnitedNameTreeNode)rankedNodes.get(node);

	    // It is not yet mapped to UnitedNameTreeNode.
	    // It may be a new name, or existing name but different higher name.
	    if(unitedNode == null) {
		unitedNode = mapToUnifiedNode(node);
		mapNode(tree, node, unitedNode);
	    }

	    if(node.isLeaf()) continue;

	    // prepare to process child nodes of the node
	    NameTreeNode parentNode = node;

	    // reduce duplication of names
	    Map<NameTreeNode, List<NameTreeNode>> reduction = reduce(parentNode, skippers);
	    if (reduction != null) {
		if (reductions == null) {
		    reductions  = new HashMap<NameTreeNode, Map<NameTreeNode, List<NameTreeNode>>>();
		}
		reductions.put(parentNode, reduction);
		Set<NameTreeNode> primaries = reduction.keySet();
		for (NameTreeNode primary : primaries) {
		    List<NameTreeNode> subsidiaries = reduction.get(primary);
		    int toInsert = Integer.MAX_VALUE;
		    for (NameTreeNode subsidiary : subsidiaries) {
			skippers.add(subsidiary);
			int index = traverser.indexOf(subsidiary);
			if (index < toInsert)
			    toInsert = index;
		    }
		    if (!traverser.contains(primary)) {
			traverser.add(toInsert, primary);
		    }
		}
	    }

	    // list up reduced children
	    List<NameTreeNode> children = new ArrayList<NameTreeNode>(parentNode.getChildCount());
	    Enumeration<?> enm = parentNode.children();
	    while(enm.hasMoreElements()) {
		children.add((NameTreeNode)enm.nextElement());
	    }
		    
	    // classify child nodes into categories, 
	    // NEW_LOWERS, DIRECT_LOWERS or INDIRECT_LOWERS,
	    // i.e new names, existing names with or without sharing the parent unified node.
	    List<Map<String, AbstractNameTreeNode<?>>> category = classifyChildren(parentNode, tree);

	    // step down to the nexe level
	    unitedParent = unitedNode;

	    List<NameTreeNode> insertions = new ArrayList<NameTreeNode>();

	    // simple addition
	    if(!category.get(NEW_LOWERS).isEmpty()){
		Map<String, AbstractNameTreeNode<?>> newLowers = category.get(NEW_LOWERS);
		Collection<AbstractNameTreeNode<?>> newChildren = newLowers.values();
		Map<String, AbstractNameTreeNode<?>> childLiterals = new HashMap<String, AbstractNameTreeNode<?>>();
		for(AbstractNameTreeNode<?> nc : newChildren) {
		    if(!nc.isLeaf()) {
			Enumeration<?> lowers = nc.children();
			while(lowers.hasMoreElements()) {
			    NameTreeNode lower = (NameTreeNode)lowers.nextElement();
			    childLiterals.put(lower.getLiteral(), nc);
			}
		    }
		}

		enm = unitedParent.children();
		while(enm.hasMoreElements()) {
		    unitedNode = (UnitedNameTreeNode)enm.nextElement();
		    AbstractNameTreeNode<?> insert = childLiterals.get(unitedNode.getLiteral());
		    if(insert != null && insert instanceof NameTreeNode && !insertions.contains(insert)) {
			insertions.add((NameTreeNode)insert);
		    }
		}
		childLiterals.clear();
		// restore to higher
		unitedNode = unitedParent;

	    }

	    // parentNode.removeAllChildren();
	    // removeAllChildren(parentNode, (DefaultTreeModel)tree);
	    List<DefaultMutableTreeNode>reAssign = new ArrayList<DefaultMutableTreeNode>();

	    if(!category.get(INDIRECT_LOWERS).isEmpty()) {
		//need to classify child nodes to determine
		//order to re-add to the parent node, although
		//consistency of each child node is determined
		//by getConsistentNode method
		category = classifyChildren(unitedParent, category, tree);

		if(!category.get(ASCENDANTS).isEmpty()) {
		    Collection<AbstractNameTreeNode<?>> ascendantCategory = category.get(ASCENDANTS).values();
		    for(AbstractNameTreeNode<?> ascendant : ascendantCategory) {
			if(!(ascendant instanceof NameTreeNode)) {
			    continue;
			}

			node = (NameTreeNode)ascendant;

			unitedNode = null;
			String rankedName = node.getLiteral();
		    }
		}
		if(!category.get(DESCENDANTS).isEmpty()){
		    Collection<AbstractNameTreeNode<?>> descendantCategory = category.get(DESCENDANTS).values();
		    for(AbstractNameTreeNode<?> descendant : descendantCategory) {
			if(!(descendant instanceof NameTreeNode)) {
			    continue;
			}

			node = (NameTreeNode)descendant;

			unitedNode = null;
			String rankedName = node.getLiteral();
			Enumeration<?> descendants = unitedParent.breadthFirstEnumeration();
			while(unitedNode == null &&
			      descendants.hasMoreElements()) {
			    unitedNode =
				(UnitedNameTreeNode)descendants.nextElement();
			    if(!rankedName.equals(unitedNode.getLiteral())) {
				unitedNode = null;
			    }
			}
			if(unitedNode == null) {
			    category.get(NEW_LOWERS).put(rankedName, node);
			}
			else if (rankedName != null) {
			    if(!reAssign.contains(node)) {
				reAssign.add(node);
			    }
			    mapNode(tree, node, unitedNode);
			}
		    }
		}
	    }

	    if(!category.get(INDIRECT_LOWERS).isEmpty()) {
		int depth = parentNode.getHeight();
		UnitedNameTreeNode highest = null;
		Set<String> keys = category.get(INDIRECT_LOWERS).keySet();
		for(String key : keys)  {
		    Map<String, Set<UnitedNameTreeNode>> parents = getNodeParentalMapFor(key);
		    if(parents != null) {
			Collection<Set<UnitedNameTreeNode>> pSets = parents.values();
			for(Set<UnitedNameTreeNode> ps: pSets) {
			    for(UnitedNameTreeNode p : ps)   {
				int pd = p.getHeight();
				if(pd < depth) {
				    depth = pd;
				    highest = p;
				}
			    }
			}
		    }
		}
		unitedNode = unitedParent;
		unitedParent = (highest != null)?
		    highest:(unitedNode != null)? unitedNode : (UnitedNameTreeNode)getRoot();
		if(unitedNode != unitedParent) {
		    if(unitedNode.getParent() != unitedParent) {
			add(unitedParent, unitedNode);
		    }
		}
		unitedParent = unitedNode;

		for(NameTreeNode child: children) {
		    if(!reAssign.contains(child))
			reAssign.add(child);

		    unitedNode = getMappedChildOrLeafNode(child, /* (NameTreeModel)tree, */ unitedParent);
		    if(unitedNode == null) {
			unitedNode = new UnitedNameTreeNode(child);
			add(unitedParent, unitedNode);
		    }
		    mapNode(tree, child, unitedNode);
		}
		for(int i = 0; i < SOME_MAPPED; i++)
		    category.get(i).clear();
		continue; //?
	    }
	    //else {
	    // }
	    if(!category.get(DIRECT_LOWERS).isEmpty()){
		enm = unitedParent.children();
		while(enm.hasMoreElements()) {
		    node = 
			(NameTreeNode)category.get(DIRECT_LOWERS).get(((NamedNode<?>)enm.nextElement()).getLiteral());
		    if(node != null) {
			unitedNode = getMappedChildOrLeafNode(node, /* (NameTreeModel)tree,*/ unitedParent);
			if(unitedNode == null ||
			   (unitedNode.getParent() != unitedParent)) {
			    category.get(NEW_LOWERS).put(node.getLiteral(), node);
			}
			else {
			    if(!reAssign.contains(node))
				reAssign.add(node);
			    mapNode(tree, node, unitedNode);
			}
		    }
		}
	    }

	    if(!category.get(NEW_LOWERS).isEmpty()){
		List<AbstractNameTreeNode<?>> newNodes =
		    new ArrayList<AbstractNameTreeNode<?>>(category.get(NEW_LOWERS).size());
		Collection<AbstractNameTreeNode<?>> newChildrenNodes = category.get(NEW_LOWERS).values();
		for(AbstractNameTreeNode<?> newChildrenNode : newChildrenNodes) {
		    if(!newChildrenNode.isLeaf()) {
			newNodes.add(newChildrenNode);
		    }
		}

		enm = unitedParent.children();
		while(enm.hasMoreElements()) {
		    unitedNode = (UnitedNameTreeNode)enm.nextElement();
		    String name = unitedNode.getLiteral();
		    for(AbstractNameTreeNode<?> abstractNode: newNodes) {
			if(abstractNode.isLeaf() || !(abstractNode instanceof NameTreeNode))
			    continue;
			NameTreeNode newNode = (NameTreeNode)abstractNode;
			Enumeration<?> chldn = newNode.children();
			NameTreeNode insert = null;
			while(insert == null && chldn.hasMoreElements()) {
			    NameTreeNode child =
				(NameTreeNode)chldn.nextElement();
			    if(name.equals(child.getLiteral())) {
				insert = newNode;
			    }
			}
			if(insert != null && !insertions.contains(insert))
			    insertions.add(insert);
			}
		}
		
		if(!insertions.isEmpty()) {
		    int insCount = insertions.size();
		    String[] insNames = new String[insCount];
		    for(int i = 0; i < insCount; i++) {
			insNames[i] = insertions.get(i).getLiteral();
		    }
		    insCount = 0;
		    for(NameTreeNode insert : insertions) {
			if(insert == null)
			    continue;
			List<UnitedNameTreeNode> uChildren
			    = new ArrayList<UnitedNameTreeNode>(unitedParent.getChildCount());
			enm = unitedParent.children();
			while(enm.hasMoreElements()) {
			    uChildren.add((UnitedNameTreeNode)enm.nextElement());
			}
			insCount++;

			if(!reAssign.contains(insert))
			    reAssign.add(insert);

		    }
		}

		// processing children
		for(NameTreeNode child : children) {
		    String rankedName = child.getLiteral();
		    child = (NameTreeNode)category.get(NEW_LOWERS).get(rankedName);
		    if(child != null) {
			if(!reAssign.contains(child))
			    reAssign.add(child);
			unitedNode = mapNode(tree, child);

			if(!child.isLeaf() && !unitedParent.isLeaf()) {
			    List<Map<String, AbstractNameTreeNode<?>>> grandCategory = classifyChildren(child, tree);
			    int realocateIndex = unitedParent.getChildCount();
			    if(grandCategory != null &&
			       !grandCategory.get(INDIRECT_LOWERS).isEmpty()) {
				grandCategory = classifyChildren(unitedParent, grandCategory, tree);
				if(grandCategory != null &&
				   grandCategory.size() > ALL_NEW &&
				   !grandCategory.get(DIRECT_LOWERS).isEmpty()){
				    List<UnitedNameTreeNode> reallocate
					= new ArrayList<UnitedNameTreeNode>();
				    Enumeration<?> grandChildren = unitedParent.children();
				    while(grandChildren.hasMoreElements()) {
					UnitedNameTreeNode grandChild = 
					    (UnitedNameTreeNode)grandChildren.nextElement();
					if(grandCategory.get(DIRECT_LOWERS).get(grandChild.getLiteral()) != null) {
					    reallocate.add(grandChild);
					    int index = unitedParent.getIndex(grandChild);
					    if(index < realocateIndex)
						realocateIndex = index;
					}
				    }
				}
			    }
			    if(realocateIndex == unitedParent.getChildCount()) {
				add(unitedParent, unitedNode);
			    }
			    else {
				insertNodeInto(unitedNode, unitedParent, realocateIndex);
			    }
			}
			else {
			    add(unitedParent, unitedNode);
			}
		    }
		}
		children.clear();
	    }

	    if(!reAssign.isEmpty()) {
		removeAllChildren(parentNode, (DefaultTreeModel)tree);

		enm = unitedParent.children();
		while(enm.hasMoreElements()) {
		    unitedNode = 
			(UnitedNameTreeNode)enm.nextElement();
		    node = (NameTreeNode)getNodeFor(unitedNode, tree);
		    if(reAssign.contains(node)) {
			add((NameTreeModel)tree, parentNode, node);
			reAssign.remove(node);
		    }
		}

		for(DefaultMutableTreeNode toAdd : reAssign) {
		    add((NameTreeModel)tree, parentNode, toAdd);
		}
	    }
	    else {
	    }
	} // end of tree traverse
	skippers.clear();

	if (reductions != null && !reductions.isEmpty()) {
	    for (NameTreeNode parentNode : traverser) {
		Map<NameTreeNode, List<NameTreeNode>> reduction = reductions.get(parentNode);
		if (reduction == null) continue;

		Set<NameTreeNode> keys = reduction.keySet();
		for (NameTreeNode reduced : keys) {
		    UnitedNameTreeNode primary = (UnitedNameTreeNode)getNodeFor(reduced);
		    if (primary != null) {
			List<NameTreeNode> subsidiaries = reduction.get(reduced);
			for (NameTreeNode subsidiary : subsidiaries) {
				setNodeFor(subsidiary, primary);
			}
		    }
		}
	    }
	}

	trees.add(tree);
	tree.addTreeModelListener(this);
	
	node = 
	    (NameTreeNode)tree.getRoot();
	e = node.breadthFirstEnumeration();
	while(e.hasMoreElements()) {
	    node = (NameTreeNode)e.nextElement();
	    node.clearExcludants();
	    node.clearIncludants();
	}
	System.gc();

	fireTreeAdded(tree);

    }

    /**
     * Returns <tt>List</tt> of <tt>anchesters</tt> sharing child name with <tt>ancestor</tt>
     *
     * @param ancestor
     * @param ancestors
     * @return list of <tt>anchesters</tt> sharing child name with <tt>ancestor</tt>, or null if
     * either of parameters is null
     */
    protected List<AbstractNameTreeNode<?>> getSharingAncestors(AbstractNameTreeNode<?> ancestor, 
								   Collection<? extends AbstractNameTreeNode<?>> ancestors)
    {
	// This method is not used at all.  Is it necessary?  Check method itself to use; it is untested at all.

	// null parameters are invalid
	if(ancestor == null || ancestors == null)
	    return null;

	// List to store ancestors sharing children name with the ancestor
	List<AbstractNameTreeNode<?>> sharer = new ArrayList<AbstractNameTreeNode<?>>();

	// Map of ancestors indexed by ranked name of children.  There may be multiple ancestors shareing a
	// child name hence it is a map from String to a list
	Map<String, List<AbstractNameTreeNode<?>>> rankedNames = 
	    new HashMap<String, List<AbstractNameTreeNode<?>>>();
	// Map of ancestors indexed by unranked name of children.  There may be multiple ancestors shareing a
	// child name hence it is a map from String to a list
	
	// Create maps
	for(AbstractNameTreeNode<?> candidate : ancestors) {
	    if(!candidate.isLeaf()) {
		Enumeration<?> lowers = candidate.children();
		while(lowers.hasMoreElements()) {
		    NameTreeNode lower = (NameTreeNode)lowers.nextElement();
		    String name = lower.getLiteral();
		    List<AbstractNameTreeNode<?>> list = rankedNames.get(name);
		    if(list == null) {
			list = new ArrayList<AbstractNameTreeNode<?>>();
			rankedNames.put(name, list);
		    }
		    list.add(candidate);
		    name = lower.getLiteral();
		}
	    }
	}
	
	Enumeration<?> toExamine = ancestor.children();
	while(toExamine.hasMoreElements()) {
	    AbstractNameTreeNode<?> child = (AbstractNameTreeNode<?>)toExamine.nextElement();
	    Collection<AbstractNameTreeNode<?>> toInsert = rankedNames.get(child.getLiteral());
	    if(toInsert != null) {
		for(AbstractNameTreeNode<?> insert : toInsert) {
		    if(insert != null &&  !sharer.contains(insert)) {
			sharer.add(insert);
		    }
		}
	    }
	    Map<String, Set<UnitedNameTreeNode>> literals = names.get(child.getLiteral());
	    if(literals != null) {
		Collection<Set<UnitedNameTreeNode>> list = literals.values();
		for(Set<UnitedNameTreeNode> nodes : list) {
		    for(UnitedNameTreeNode insert : nodes) {
			if(insert != null &&  !sharer.contains(insert)) {
			    sharer.add(insert);
			}
		    }
		}
	    }
	}

	return sharer;
    }

    protected void add(MutableTreeNode parent,
		       MutableTreeNode child)
    {
	if(child.getParent() == parent)
	    return;
	add(this, parent, child);
    }

    protected void add(DefaultTreeModel tree,
		       MutableTreeNode parent,
		       MutableTreeNode child)
    {
	tree.insertNodeInto(child, parent, parent.getChildCount());
    }


    protected void removeAllChildren(MutableTreeNode parentNode,
				     DefaultTreeModel tree)
    {
	Enumeration<?> enm = parentNode.children();
	MutableTreeNode[] toRemove =
	    new MutableTreeNode[parentNode.getChildCount()];

	int i = 0;
	while(enm.hasMoreElements()) {
	    toRemove[i++] =
		(MutableTreeNode)enm.nextElement();
	}
	for(i = 0; i < toRemove.length; i++) {
		tree.removeNodeFromParent(toRemove[i]);
		toRemove[i] = null;
	}
	toRemove = null;
    }

    /**
     * Returns a <tt>UnitedNameTreeNode</tt> to be mapped to <tt>node</tt>,
     * chosen from given <tt>nodes</tt>.
     * Mapping is excamined by <tt>node</tt>'s name and parental name match
     * against these of <tt>UnitedNameTreeNode</tt>.  If there is no
     * <tt>UnitedNameTreeNode</tt> of the name of the <tt>node</tt> in the
     * given <tt>nodest</tt>,
     * a new <tt>UnitedNameTreeNode</tt> will be created to reaturn.
     *
     * @param node to map.
     * @param nodes a <tt>Collection</tt> of <tt>NameTreeNodes</tt>s to be mapped to.
     * @return UnitedNameTreeNode to be mapped to.
     */
    protected UnitedNameTreeNode mapToUnifiedNode(NameTreeNode node, Collection<UnitedNameTreeNode> nodes)
    {
	if(node == null || nodes == null || nodes.isEmpty())
	    return null;

	UnitedNameTreeNode unitedNode = null;
	UnitedNameTreeNode[] unitedNodes =
	    nodes.toArray(new UnitedNameTreeNode[nodes.size()]);
	if(nodes.size() == 1) {
	    unitedNode = unitedNodes[0];
	}
	else {
	    // how can one choose a single entry from two or more candidates...
	    // try max match of direct children
	    int maxMatchCount = 0;
	    Map<Integer, Set<UnitedNameTreeNode>> childMap
		= new HashMap<Integer, Set<UnitedNameTreeNode>>();
	    
	    for(UnitedNameTreeNode uNode : unitedNodes) {
		int count = node.getMatchedChildrenCount(uNode, -1);
		if(count > 0) {
		    if(count >= maxMatchCount) {
			maxMatchCount = count;
			Set<UnitedNameTreeNode>nodeSet = childMap.get(count);
			if(nodeSet == null) {
			    nodeSet = new HashSet<UnitedNameTreeNode>();
			    childMap.put(count, nodeSet);
			}
			nodeSet.add(uNode);
		    }
		}
	    }
	    
	    unitedNode = null;
	    
	    if(maxMatchCount > 0) {
		Set<UnitedNameTreeNode>nodeSet = childMap.get(maxMatchCount);
		if(nodeSet != null && !nodeSet.isEmpty()) {
		    unitedNodes = nodeSet.toArray(new UnitedNameTreeNode[nodeSet.size()]);
		    if(unitedNodes.length == 1) {
			unitedNode = unitedNodes[0];
		    }
		}
	    }
	    
	    // clean up the map
	    Collection<Set<UnitedNameTreeNode>> values
		= childMap.values();
	    if(values != null) {
		for(Set<UnitedNameTreeNode> value : values) {
		    value.clear();
		}
	    }
	    childMap.clear();
	}
	return unitedNode;
    }

    /**
     * Returns a <tt>UnitedNameTreeNode</tt> to be mapped to given <tt>node</tt>.
     * Mapping is excamined by <tt>node</tt>'s name and parental name match
     * against these of <tt>UnitedNameTreeNode</tt>.  If there is no
     * <tt>UnitedNameTreeNode</tt> of the name of the <tt>node</tt>,
     * a new <tt>UnitedNameTreeNode</tt> will be created to reaturn.
     *
     * @param node to map
     * @return UnitedNameTreeNode to be mapped to.
     */
    protected UnitedNameTreeNode mapToUnifiedNode(NameTreeNode node)
    {
	// pre STEP2
	String rankedName = node.getLiteral();
	String literal = node.getLiteral();
	boolean unrankedNode = rankedName.equals(literal);

	UnitedNameTreeNode unitedNode = null;
	UnitedNameTreeNode unitedParent = null;
	//The node has not yet mapped to any node in the unified tree.
	//Check a UnifiedNameTree node of the node's name.
	//If there is a UnifiedNameTree node of the name,
	//then there must be the parental UnifiedNameTree node
	//if the node is not the root node of the tree.
	
	Map<String, Set<UnitedNameTreeNode>> parents = names.get(rankedName);

	Set<UnitedNameTreeNode> parentSet = null;
	
	if(parents == null) { // STEP 3: name does not yet exist in unified tree, i.e. a new name
	    parents = new HashMap<String, Set<UnitedNameTreeNode>>();
	    names.put(rankedName, parents);
	    
	    if(trees.isEmpty() && node == node.getRoot()){
		unitedNode = (UnitedNameTreeNode)getRoot();
		unitedNode.setRankedName(rankedName);
	    }
	    else{
		unitedNode = 
		    new UnitedNameTreeNode(rankedName);
	    }
	    unitedNode.setLiteral(literal);
	}
	
	// it will branch to STEP 4 or 5 later
	
	NameTreeNode parentNode = (NameTreeNode)node.getParent();

	// if the parent node is root, use zero-length literal as parental name
	String parentName = ROOT_NAME;
	String parentLiteral = ROOT_NAME;
	
	if(parentNode != null) { // the node is not the root
	    parentName = parentNode.getLiteral();
	    parentLiteral = parentNode.getLiteral();
	    TreeNode rankedNode = rankedNodes.get(parentNode);
	    if(rankedNode instanceof UnitedNameTreeNode) {
		unitedParent= (UnitedNameTreeNode)rankedNode;
	    }
	}
	boolean unrankedParent = parentName.equals(parentLiteral);

	if(unitedNode == null) {
	    // It would be non-null via STEP 3 if it were a new name,
	    // Hence the name is not new to the UnitedNameTreeModel
	    if(unitedParent != null) {
		// the unitedParent may have a child UnitedNameTreeNode of the name...
		// it is assumed that direct children of a UnitedNameTreeNode do not share a name.
		Enumeration<?> children = unitedParent.children();
		while(unitedNode == null &&
		      children.hasMoreElements()) {
		    Object child = children.nextElement();
		    if(child instanceof UnitedNameTreeNode) {
			unitedNode = (UnitedNameTreeNode)child;
			if(!rankedName.equals(unitedNode.getLiteral()) &&
			   !literal.equals(unitedNode.getLiteral())) {
			    // diffarent name value
			    unitedNode = null;
			}
		    }
		}
	    }
	    if(unitedNode == null) {
		if(parents != null) {
		    unitedNode = mapToUnifiedNode(node, parents.get(parentName));
		}
	    }
	    else {
		    
	    }
	}

	if(unitedNode == null) {
	    if(parentNode != null) { // STEP 4
		//we may have multiple views... 
		unitedNode = new UnitedNameTreeNode(rankedName);
		unitedNode.setLiteral(literal);
		unitedParent = (UnitedNameTreeNode)rankedNodes.get(parentNode);
		if(unitedParent != null) //is it right?
		    insertNodeInto(unitedNode, unitedParent, unitedParent.getChildCount());
	    }  // end of STEP 4
	    else { // STEP 5 ... is it?
		Collection<Set<UnitedNameTreeNode>> parentSetCollection = parents.values();
		Iterator<Set<UnitedNameTreeNode>> parentSetIter = parentSetCollection.iterator();
		parentSet = parentSetIter.next();

		// The null parentNode implies that the node in process is the root node of the tree in process.
		//there is a same named, same ranked node with differnt parents...
		if(parents.size() == 1 && parentSet.size() == 1) { // only one parental name chain
		    Iterator<UnitedNameTreeNode> parentIter = parentSet.iterator();
		    unitedNode = parentIter.next();
		}
		else if(node.isLeaf()) { // if the node is a leaf node, it can be consistent without minding its composition
		    List<UnitedNameTreeNode> candidates = new ArrayList<UnitedNameTreeNode>();
		    for(Set<UnitedNameTreeNode> candidateSet : parentSetCollection) {
			for(UnitedNameTreeNode candidate : candidateSet) {
			    if(candidate.isLeaf())
				candidates.add(candidate);
			}
		    }
		    if(candidates.size() == 1) {
			unitedNode = candidates.get(0);
		    }
		    else {
			unitedNode = 
			    new UnitedNameTreeNode(rankedName);
			unitedNode.setLiteral(literal);
		    }
		} // end of else if(node.isLeaf())
		else { //not a leaf node, with multiple parents
		    List<UnitedNameTreeNode> candidates = new ArrayList<UnitedNameTreeNode>();
		    Map<String,  NameTreeNode> childNodes = new HashMap<String,  NameTreeNode>(node.getChildCount());
		    CollectionUtility<Collection<String>> collectionUtility = new CollectionUtility<Collection<String>>();
		    Enumeration<?> childEnum = node.children();
		    int equals = 0;
		    int greaterThan = 0;
		    int lessThan = 0;
		    while(childEnum.hasMoreElements()) {
			NameTreeNode childNode =
			    (NameTreeNode)childEnum.nextElement();
			childNodes.put(childNode.getLiteral(), childNode);
		    }
		    //REVISIT
		    //choose appropriate one from candidates...how?
		    Set<UnitedNameTreeNode> parentsCollection = new HashSet<UnitedNameTreeNode>();
		    for(Set<UnitedNameTreeNode> parentsSet : parentSetCollection) {
			parentsCollection.addAll(parentsSet);
		    }
		    for(UnitedNameTreeNode candidate : parentsCollection) {
			if(candidate.isLeaf()) {
			    candidates.add(candidate);
			}
			else {
			    Set<NameTreeNode> candidatesChildren = unitedChildrens.get(candidate);
			    Collection<NameTreeNode> childNodeValues = childNodes.values();
			    if(!CollectionUtility.hasIntersection(childNodeValues, candidatesChildren)) {
				continue;
			    }
			    
			    if(childNodeValues.containsAll(candidatesChildren) && candidatesChildren.containsAll(childNodeValues)) {
				candidates.add(0, candidate);
				equals++;
			    }
			    else if(childNodeValues.containsAll(candidatesChildren)) {
				candidates.add(equals, candidate);
				greaterThan++;
			    }
			    else if(candidatesChildren.containsAll(childNodeValues)) {
				candidates.add(greaterThan, candidate);
				lessThan++;
			    }
			}
		    }
		    
		    if(candidates.isEmpty()) {
			unitedNode = 
			    new UnitedNameTreeNode(rankedName);
			unitedNode.setLiteral(literal);
		    }
		    else {
			int index = 0;
			if(equals == 1)
			    index = 0;
			else if (equals > 1) {
			    //how can we find the best match?
			    index = 0;
			}
			else if(greaterThan == 1) {
			    index = equals;
			}
			else if(greaterThan > 0) {
			    index = equals;
			}
			else if(lessThan == 1) {
			    index = equals + greaterThan;
			}
			else if(lessThan > 1) {
			    index = equals + greaterThan;
			}
			else
			    index = equals + greaterThan + lessThan;
			
			unitedNode = candidates.get(index);
		    }
		    childNodes.clear();
		    candidates.clear();
		} //parentSize
	    } //end of STEP 5
	} //end of new name
	else {

	} 

	unitedParent = (UnitedNameTreeNode)unitedNode.getParent();
	if(unitedParent == null) {
	    unitedParent = (UnitedNameTreeNode)getRoot();
	    if(unitedParent != unitedNode) {
		add(unitedParent, unitedNode); //FIXME
	    }
	}
	if(parentNode == null && unitedParent != null) {
	    parentName = unitedParent.getLiteral();
	    parentLiteral = unitedParent.getLiteral();
	}
	else if(parentNode != null) {
	}
	indexByNames(unitedNode);
	return unitedNode;
    }

    /**
     * Returns a <CODE>UnitedNameTreeNode</CODE> consitent to <CODE>node</CODE>
     * and <CODE>parentNode</CODE>.  The returned node may or may not be a child node of
     * <CODE>parentNode</CODE>.
     * If the rturned node is a consistent node, retruned node is already a
     * child node of <CODE>parentNode</CODE>.
     */
    protected UnitedNameTreeNode getMappedChildOrLeafNode(NameTreeNode node,
							   // NameTreeModel tree,
							   UnitedNameTreeNode parentNode)
    {
	String literal = node.getLiteral();

	UnitedNameTreeNode unitedNode = null;
	TreeNode mappedNode = rankedNodes.get(node);
	if (mappedNode != null && mappedNode instanceof UnitedNameTreeNode) {
	    unitedNode = (UnitedNameTreeNode)mappedNode;
	}
	if (unitedNode == null) {
	    unitedNode = parentNode.getChild(literal);
	}
	// FIXME cheat
	if (unitedNode != null) {
	    return unitedNode;
	}
	
	if(node.isLeaf()) {
	    unitedNode = new UnitedNameTreeNode(node);
	    add(parentNode, unitedNode);
	}

	return unitedNode;
    }

    /**
     * Returns a <CODE>UnitedNameTreeNode</CODE> consitent to <CODE>node</CODE>
     * and <CODE>parentNode</CODE>.  The returned node may or may not be a child node of
     * <CODE>parentNode</CODE>.
     * If the rturned node is a consistent node, retruned node is already a
     * child node of <CODE>parentNode</CODE>.
     */
    protected UnitedNameTreeNode getConsistentNode(NameTreeNode node,
						    NameTreeModel tree,
						    UnitedNameTreeNode parentNode)
    {
	return getConsistentNode(node, tree, parentNode, getMappedChildOrLeafNode(node, /*tree, */ parentNode));
    }

    /**
     * Returns a <CODE>UnitedNameTreeNode</CODE> consitent to <CODE>node</CODE>
     * and <CODE>parentNode</CODE>.  The returned node may or may not be a child node of
     * <CODE>parentNode</CODE>.
     * If the rturned node is a consistent node, retruned node is already a
     * child node of <CODE>parentNode</CODE>.
     */
    protected UnitedNameTreeNode getConsistentNode(NameTreeNode node,
						    NameTreeModel tree,
						    UnitedNameTreeNode parentNode, UnitedNameTreeNode unitedNode)
    {
	if(node.isLeaf())
	    return unitedNode;

	String literal = node.getLiteral();

	UnitedNameTreeNode unitedGrandParent = parentNode;
	List<Map<String, AbstractNameTreeNode<?>>> grandChildCategory = classifyChildren(node, tree);
	if(!grandChildCategory.get(INDIRECT_LOWERS).isEmpty()) {
	    grandChildCategory = classifyChildren(unitedGrandParent, grandChildCategory, tree);
	    if(grandChildCategory.size() > ALL_NEW) {
		if(!grandChildCategory.get(ASCENDANTS).isEmpty()) {
		    unitedNode = new UnitedNameTreeNode(node);
		    node = (NameTreeNode)getHighest(grandChildCategory.get(ASCENDANTS), node);
		    parentNode = (UnitedNameTreeNode)getNodeFor(node);
		    if(parentNode != null)
			add(tree, parentNode, unitedNode); 
		    else {
			Collection<AbstractNameTreeNode<?>> grandChildrenNodes = grandChildCategory.get(ASCENDANTS).values();
			for(AbstractNameTreeNode<?> grandChildrenNode : grandChildrenNodes) {
			    if(!(grandChildrenNode instanceof NameTreeNode))
				continue;
			    node = (NameTreeNode)grandChildrenNode;
			}
		    }
		    return unitedNode;
		}
		else if(!grandChildCategory.get(INDIRECT_LOWERS).isEmpty()) {
		    unitedNode = new UnitedNameTreeNode(node);
		    //or find the best code...?
		    return unitedNode;
		}
	    }
	}
	
	Collection<UnitedNameTreeNode> unitedNodes = null;
	if(unitedNode == null) {
	    unitedNode = new UnitedNameTreeNode(node);
	}
	else {
	    unitedNodes = parentNode.getChildren(literal);
	    if(unitedNodes.size() == 1) {
		unitedNodes.clear();
		unitedNodes = null;
	    }
	    else if (unitedNodes.size() > 0) {
		unitedNode = unitedNodes.iterator().next();
	    }
	}
	
	List<Map<String, AbstractNameTreeNode<?>>> childCategory = grandChildCategory;
	if(!childCategory.get(INDIRECT_LOWERS).isEmpty()) {
	    childCategory = classifyChildren(unitedNode, childCategory, tree);
	    if(childCategory.size() > ALL_NEW) {
		if(!childCategory.get(ASCENDANTS).isEmpty()) {
		    parentNode = unitedNode;
		    unitedNode = new UnitedNameTreeNode(node);
		    node = (NameTreeNode)getHighest(childCategory.get(ASCENDANTS), node);
		    ((UnitedNameTreeNode)getNodeFor(node)).add(unitedNode); 
		    return unitedNode;
		}
		else if(!childCategory.get(INDIRECT_LOWERS).isEmpty()) {
		    unitedNode = new UnitedNameTreeNode(node);
		    return unitedNode;
		}
	    }
	}
	
	if(unitedNode != null && parentNode != null &&
	   unitedNode.getParent() != parentNode)
	    add(tree, parentNode, unitedNode); 
	
	if(grandChildCategory.size() > ALL_NEW && 
	    !grandChildCategory.get(DIRECT_LOWERS).isEmpty()) {
	    
	    int size = node.getChildCount();
	    Map<String, NameTreeNode> children =
		new HashMap<String, NameTreeNode>(size);
	    Vector<NameTreeNode> v = 
		new Vector<NameTreeNode>(size);
	    Enumeration<?> e = node.children();
	    int i = 0;
	    while(e.hasMoreElements()) {
		NameTreeNode n = 
		    (NameTreeNode)e.nextElement();
		v.addElement(n);
		children.put(n.getLiteral()/*getRankedName(true)*/, n);
	    }
	    
	    removeAllChildren(node, (DefaultTreeModel)tree);

	    Map<String, UnitedNameTreeNode> grandChildren =
		new HashMap<String, UnitedNameTreeNode>(unitedGrandParent.getChildCount());
	    e = unitedGrandParent.children();
	    while(e.hasMoreElements()) {
		UnitedNameTreeNode u = 
		    (UnitedNameTreeNode)e.nextElement();
		grandChildren.put(u.getLiteral()/*getRankedName()*/, u);
	    }
	    
	    for(String key : grandChildCategory.get(DIRECT_LOWERS).keySet()) {
		NameTreeNode child = children.get(key);
		UnitedNameTreeNode u = grandChildren.get(key);
		if(child != null && u != null) {
		    add(tree, node, child);
		    add(unitedNode, u);
		}
		else {
		}
	    }
	    
	    if(!childCategory.get(DIRECT_LOWERS).isEmpty()) {
		for(String key : childCategory.get(DIRECT_LOWERS).keySet())  {
		    NameTreeNode child = children.get(key);
		    if(child != null)
			add(tree, node, child);
		}
	    }
	    
	    
	    if(!childCategory.get(NEW_LOWERS).isEmpty()) {
		for(NameTreeNode child : v) {
		    child = 
			(NameTreeNode)childCategory.get(NEW_LOWERS).get(child.getLiteral());
		    if(child != null) {
			add(tree, node, child);
		    }
		}
	    }
	}
	
	int categorySize = childCategory.size();
	if(childCategory != null) {
	    for(int i = 0; i < categorySize; i++) {
		childCategory.get(i).clear();
	    }
	    childCategory.clear();
	    childCategory = null;
	}
	
	categorySize = grandChildCategory.size();
	if(grandChildCategory != null) {
	    for(int i = 0; i < categorySize; i++) {
		grandChildCategory.get(i).clear();
	    }
	    grandChildCategory.clear();
	    grandChildCategory = null;
	}
	
	return unitedNode;
    }
    
    protected NamedNode<?> getHighest(Map<String, AbstractNameTreeNode<?>> ascendants,
				   NamedNode<?> node)
    {
	int depth = node.getHeight();
	for(AbstractNameTreeNode<?> n : ascendants.values()) {
	    int d = n.getHeight();
	    if(d < depth) {
		depth = d;
		node = n;
	    }
	}
	return node;
    }

    protected UnitedNameTreeNode[] getBest(UnitedNameTreeNode[] unitedNodes,
					    NameTreeNode node, TreeModel tree)
    {
	List<UnitedNameTreeNode> candidates = new ArrayList<UnitedNameTreeNode>();
	List<Map<String, AbstractNameTreeNode<?>>> category = classifyChildren(node, tree);

	int size = 0;

	if(category.get(INDIRECT_LOWERS).isEmpty()) {
	    //all child nodes of the node is new to this tree

	    //find nodes with maximum coverage
	    for(UnitedNameTreeNode unitedNode: unitedNodes) {
		int s = unitedNode.getChildCount();
		if(s > size)
		    size = s;
	    }
	    if(size == 0)
		return null;
	}
	else {
	    for(UnitedNameTreeNode unitedNode : unitedNodes) {
		category = classifyChildren(unitedNode, node, tree);
		
		//ignore united nodes to inconsitent to the node
		if(!category.get(ASCENDANTS).isEmpty() ||
		   !category.get(INDIRECT_LOWERS).isEmpty())
		    continue;
		
		candidates.add(unitedNode);
		int s = unitedNode.getChildCount();
		if(s > size)
		    size = s;
	    }
	    unitedNodes = new UnitedNameTreeNode[candidates.size()];
	    unitedNodes = candidates.toArray(unitedNodes);
	    candidates.clear();
	}

	for(UnitedNameTreeNode unitedNode: unitedNodes) {
	    if(size == unitedNode.getChildCount())
		candidates.add(unitedNode);
	}
	
	unitedNodes = new UnitedNameTreeNode[candidates.size()];
	unitedNodes = candidates.toArray(unitedNodes);
	candidates.clear();
	return unitedNodes;
    }
    

    protected List<Map<String, AbstractNameTreeNode<?>>> classifyChildren(UnitedNameTreeNode unitedParent,
								       NameTreeNode node, TreeModel tree)
    {
	return classifyChildren(unitedParent, classifyChildren(node, tree), tree);
    }

    /**
     * Classifies child <tt>AbstractNameTreeNode<?></tt>s of given <tt>unitedParent</tt>
     * into a <tt>List</tt> of <tt>Map</tt>s from names to <tt>AbstractNameTreeNode<?></tt>s
     * given as <tt>classified</tt>.
     * 
     *
     */
    protected List<Map<String, AbstractNameTreeNode<?>>> classifyChildren(UnitedNameTreeNode unitedParent,
    					   List<Map<String, AbstractNameTreeNode<?>>> classified, TreeModel tree)
    {
	if (classified == null) {
	    return null;
	}

	if (unitedParent == null) {
	    return classified;
	}

	Map<String, AbstractNameTreeNode<?>> newChildren = classified.get(NEW_LOWERS);
	Map<String, AbstractNameTreeNode<?>> indirectChildren = classified.get(INDIRECT_LOWERS);
	Map<String, AbstractNameTreeNode<?>> directChildren = classified.get(DIRECT_LOWERS);
	Map<String, AbstractNameTreeNode<?>> descendants = new HashMap<String, AbstractNameTreeNode<?>>();
	Map<String, AbstractNameTreeNode<?>> ascendants = new HashMap<String, AbstractNameTreeNode<?>>();

	//list of children nodes already in unified tree
	Map<String, AbstractNameTreeNode<?>> unitedChildren = new HashMap<String, AbstractNameTreeNode<?>>();

	Enumeration<?> children = unitedParent.children();
	while(children.hasMoreElements()) {
	    UnitedNameTreeNode unitedNode = 
		(UnitedNameTreeNode)children.nextElement();
	    unitedChildren.put(unitedNode.getLiteral(), unitedNode);
	}
	
	// copy of indices
	List<String> rankedNames = new ArrayList<String>();
	for(String rankedName : indirectChildren.keySet()) {
	    rankedNames.add(rankedName);
	}
	
	for(String rankedName: rankedNames) {
	    UnitedNameTreeNode unitedNode = (UnitedNameTreeNode)unitedChildren.get(rankedName);
	    
	    Map<String, AbstractNameTreeNode<?>> toPut = null;
	    if(unitedNode != null) {
		toPut = directChildren;
	    }
	    else {
		unitedNode = getNodeFor(rankedName, unitedParent);
		if(unitedNode == null) {
		    Map<String, Set<UnitedNameTreeNode>> parentSets = getNodeParentalMapFor(rankedName);
		    if(parentSets != null) {
			Collection<Set<UnitedNameTreeNode>> parentSetCollection = parentSets.values();
			Iterator<Set<UnitedNameTreeNode>> parentSetIter = parentSetCollection.iterator();
			Set<UnitedNameTreeNode> parentSet = parentSetIter.next();
			if(parentSets.size() == 1 && parentSet.size() == 1) {
			    // Iterator<UnitedNameTreeNode> e = parentSet.values().iterator();
			    unitedNode =parentSet.iterator().next();
			}
			else {
			    String parentName = unitedParent.getLiteral();
			    parentSet = parentSets.get(parentName);
			    if (parentSet != null && parentSet.size() == 1) {
				Iterator<UnitedNameTreeNode> unitedNodes = parentSet.iterator();
				unitedNode = (UnitedNameTreeNode)unitedNodes.next().getParent();
			    }
			    else {
				parentSet = new HashSet<UnitedNameTreeNode>();
				for(Set<UnitedNameTreeNode> s : parentSetCollection) {
				    parentSet.addAll(s);
				    Iterator<UnitedNameTreeNode> e = parentSet.iterator();
				    while(unitedNode == null && e.hasNext()) {
					UnitedNameTreeNode u = e.next();
					children = u.children();
					while(unitedNode == null &&
					      children.hasMoreElements()) {
					    UnitedNameTreeNode child =
						(UnitedNameTreeNode)children.nextElement();
					    if(rankedName.equals(child.getLiteral())) {
						unitedNode = child;
					    }
					}
				    }
				}
			    }
			}
		    }
		    else {
		    }
		}
		if(unitedNode != null) {
		    if(unitedParent == unitedNode || 
		       rankedName.equals(unitedParent.getLiteral())) {
			indirectChildren.remove(rankedName);
		    }
		    else {
			if(unitedParent.isNodeDescendant(unitedNode)) {
			    toPut = descendants;
			}
			else if(unitedParent.isNodeAncestor(unitedNode)) {
			    toPut = ascendants;
			}
			else {
			    UnitedNameTreeNode toTest = (UnitedNameTreeNode)unitedNode.getParent();
			    if (toTest != null) {
				if(unitedParent.isNodeAncestor(toTest)) {
				    unitedParent.add(unitedNode);
				    unitedChildren.remove(rankedName);
				    NameTreeNode node = 
					(NameTreeNode)indirectChildren.get(rankedName);
				    mapNode(tree, node, unitedNode);
				    indirectChildren.remove(rankedName);
				}
				else {
				    if (toPut == null) {
					toPut = directChildren;
				    }
				}
			    }
			    else {
			    }
			}
		    }
		}
		else {
		    Collection<UnitedNameTreeNode> unifiedNodes = getNodesFor(rankedName);
		    Set<UnitedNameTreeNode> candidates = new HashSet<UnitedNameTreeNode>();
		    for (UnitedNameTreeNode unifiedNode : unifiedNodes) {
			UnitedNameTreeNode toTest = (UnitedNameTreeNode)unifiedNode.getParent();
			if (toTest != null) {
			    if(unitedParent.isNodeDescendant(toTest)) {
				candidates.add(unifiedNode);
			    }
			}
		    }
		    if (candidates.size() == 1) {
			unitedNode = candidates.iterator().next();
			if (unitedParent == unitedNode
			    || unitedParent.isNodeAncestor(unitedNode)) {
			    toPut = directChildren;
			}
			else {
			    toPut = descendants;
			}
		    }
		    else {
			toPut = directChildren;
		    }
		}
	    }

	    if(toPut != null) {
		unitedChildren.remove(rankedName);
		NameTreeNode node = 
		    (NameTreeNode)indirectChildren.get(rankedName);
		toPut.put(rankedName, node);
		indirectChildren.remove(rankedName);
	    }
	}

	classified = new ArrayList<Map<String, AbstractNameTreeNode<?>>>(6);
	classified.add(newChildren);
	classified.add(indirectChildren);
	classified.add(directChildren);
	classified.add(descendants);
	classified.add(ascendants);
	classified.add(unitedChildren);

	return classified;
    }

    /**
    * Returns a List of mappings from names to <tt>AbstractNameTreeNode<?></tt>s
     * where maps are classified into categories, new lower names,
     * direct or indirect child of given <tt>node</tt>
     *
     * @param node of which child node
     */
    protected List<Map<String, AbstractNameTreeNode<?>>> classifyChildren(NameTreeNode node, TreeModel tree)
    {
	if(node == null || node.isLeaf()) {
	    return null;
	}

	// sort children nodes into three categoeis, i.e. 
	// names new to the united tree
	Map<String, AbstractNameTreeNode<?>> newChildren = new HashMap<String, AbstractNameTreeNode<?>>();
	Map<String, AbstractNameTreeNode<?>> newChildrenUnranked = new HashMap<String, AbstractNameTreeNode<?>>();

	// or names in the united tree but not children of the parent unified node
	Map<String, AbstractNameTreeNode<?>> indirectChildren = new HashMap<String, AbstractNameTreeNode<?>>();
	Map<String, AbstractNameTreeNode<?>> indirectChildrenUnranked = new HashMap<String, AbstractNameTreeNode<?>>();

	// or names in the united tree as children of the prent unified node
	Map<String, AbstractNameTreeNode<?>> directChildren = new HashMap<String, AbstractNameTreeNode<?>>();
	Map<String, AbstractNameTreeNode<?>> directChildrenUnranked = new HashMap<String, AbstractNameTreeNode<?>>();

	String parentalName = node.getLiteral();
	String parentalLiteral = node.getLiteral();

	Enumeration<?> children = null;

	UnitedNameTreeNode unifiedParent = (UnitedNameTreeNode)rankedNodes.get(node);
	if(unifiedParent == null) {
	    // the node is unmapped (yet)
	    NameTreeNode parentNode = (NameTreeNode)node.getParent();
	    if(parentNode != null) {
		unifiedParent = (UnitedNameTreeNode)rankedNodes.get(parentNode);
	    }
	    else {
		unifiedParent = (UnitedNameTreeNode)getRoot();
	    }
	    children = unifiedParent.children();

	    UnitedNameTreeNode unifiedParentCandidate = null;
	    while (unifiedParentCandidate == null && children.hasMoreElements()) {
		unifiedParentCandidate = (UnitedNameTreeNode)children.nextElement();
	    }
	    children = null;
	    unifiedParent = unifiedParentCandidate;

	}

	children = node.children();
	while(children.hasMoreElements()) {
	    Object childObject = children.nextElement();
	    // just in case...
	    if(!(childObject instanceof NameTreeNode)) {
		continue;
	    }

	    NameTreeNode child = (NameTreeNode)childObject;
	    String rankedName = child.getLiteral();
	    String unrankedName = child.getLiteral();

	    // Holds the mapping to put the node depending on the classification
	    Map<String, AbstractNameTreeNode<?>> toPut = null;
	    Map<String, AbstractNameTreeNode<?>> toPutUnranked = null;

	    Map<String, Set<UnitedNameTreeNode>> rankedNodeSets = getNodeParentalMapFor(rankedName);
	    Map<String, Set<UnitedNameTreeNode>> unrankedNodeSets = getNodeParentalMapFor(unrankedName);

	    Set<UnitedNameTreeNode> unifiedNodes = null;

	    int parentCount = -1;
	    
	    if(rankedNodeSets == null && unrankedNodeSets == null) { // new name
		// the name of the child node is new to this UnitedNameTreeModel instance
		toPut = newChildren;
		toPutUnranked = newChildrenUnranked;
	    }
	    else { // the name already extists
		if ((rankedNodeSets != null && rankedNodeSets.get(parentalName) != null)
		   || (unrankedNodeSets != null && unrankedNodeSets.get(parentalLiteral) != null)) { // matched parental pair
		    if (rankedNodeSets != null) {
			unifiedNodes = rankedNodeSets.get(parentalName);
		    }
		    if (unifiedNodes == null && unrankedNodeSets != null) {
			unifiedNodes = unrankedNodeSets.get(parentalName);
		    }
		    if (unifiedNodes != null) {
			parentCount = unifiedNodes.size();
			UnitedNameTreeNode found = null;
			Iterator<UnitedNameTreeNode> nodes = unifiedNodes.iterator();
			int iteration = 0;
			while(found == null && nodes.hasNext()) {
			    found = nodes.next();
			    if(found.getParent() != unifiedParent) {
				found = null;
			    }
			}
			
			if (found != null) {
			    // child-parent name pair already exsits
			    toPut = directChildren;
			    toPutUnranked = directChildrenUnranked;
			}
		    }
		}
		else  {
		    Set<UnitedNameTreeNode> roots = null;
		    if (rankedNodeSets != null
			&& rankedNodeSets.size() == 1) {
			roots = rankedNodeSets.get(ROOT_NAME);
		    }
		    if (roots == null &&
			unrankedNodeSets != null
			&& unrankedNodeSets.size() == 1) {
			roots = unrankedNodeSets.get(ROOT_NAME);
		    }
		    if (roots != null) {
			UnitedNameTreeNode root = roots.iterator().next();
			if(root.getParent() == getRoot() &&
			   unifiedParent != getRoot()) {
			    unifiedParent.add(root);
			    // fixme....
			    mapNode(tree, child, root);
			    toPut = directChildren;
			    toPutUnranked = directChildrenUnranked;
			}
		    }
		}

		if(toPut == null) { // existing name with new parental combination
		    UnitedNameTreeNode ancestor = null;
		    Collection<UnitedNameTreeNode> ancestors = new HashSet<UnitedNameTreeNode>();

		    if(unifiedNodes == null) {

			unifiedNodes = new HashSet<UnitedNameTreeNode>();
			Collection<Set<UnitedNameTreeNode>> unifiedNodesSet = 
			    (rankedNodeSets != null)? rankedNodeSets.values() : unrankedNodeSets.values();
			if (unifiedNodesSet != null) {
			    for (Set<UnitedNameTreeNode> set :unifiedNodesSet) {
				unifiedNodes.addAll(set);
			    }
			}
			else {
			    unifiedNodesSet = unrankedNodeSets.values();
			    if (unifiedNodesSet != null) {
				for (Set<UnitedNameTreeNode> set :unifiedNodesSet) {
				    unifiedNodes.addAll(set);
				}
			    }
			}
		    }

		    // unifiedNodes retains UnitedNameTreeNodes shareing its name with the node parameter
		    if(unifiedNodes != null) {
			Iterator<UnitedNameTreeNode> iterator = unifiedNodes.iterator();
			while(iterator.hasNext()) {
			    ancestor = iterator.next().getAncestor(parentalName);
			    if(ancestor != null) {
				ancestors.add(ancestor);
				ancestor = null;
			    }
			}
			if(ancestors.size() == 0) {
			    iterator = unifiedNodes.iterator();
			    while(iterator.hasNext()) {
				ancestor = iterator.next().getAncestor(parentalLiteral);
				if(ancestor != null) {
				    ancestors.add(ancestor);
				    ancestor = null;
				}
			    }
			}
			if(ancestors.size() == 0) {
			    iterator = unifiedNodes.iterator();
			    while(iterator.hasNext()) {
				ancestor = iterator.next();
				ancestor = (UnitedNameTreeNode)ancestor.getParent();
				if(unifiedParent != null) {
				    ancestor = unifiedParent.getAncestor(ancestor.getLiteral());
				}
				if(ancestor != null) {
				    ancestors.add(ancestor);
				    ancestor = null;
				}
			    }
			}
			if(ancestors.size() == 0) {
			    iterator = unifiedNodes.iterator();
			    while(iterator.hasNext()) {
				ancestor = unifiedParent.getAncestor(((UnitedNameTreeNode)iterator.next().getParent()).getLiteral());
				if(ancestor != null) {
				    ancestors.add(ancestor);
				    ancestor = null;
				}
			    }
			}
		    } //end if(unifiedNodes != null)
		    
		    //FIXME: does any anchestor result in indirect?
		    if(ancestors.size() == 1) {
			ancestor = ancestors.iterator().next();
			UnitedNameTreeNode mappedNode = 
			    (UnitedNameTreeNode)rankedNodes.get(node);
			if(mappedNode != null && ancestor != mappedNode) {
			    if(mappedNode.getLiteral().equals(ancestor.getLiteral()) ||
			       mappedNode.getLiteral().equals(ancestor.getLiteral())) {
				toPut = newChildren;
				toPutUnranked = newChildrenUnranked;
			    }
			}
			if(toPut == null) {
			    toPut = indirectChildren;
			    toPutUnranked = indirectChildrenUnranked;
			}
		    }
		    else {
			if(ancestors.size() == 0 && 
			   unifiedNodes != null && unifiedNodes.size() == 1 &&
			   unifiedParent == unifiedNodes.iterator().next().getParent()) {
				toPut = directChildren;
				toPutUnranked = directChildrenUnranked;
			}
			else {
			    toPut = newChildren;
			    toPutUnranked = newChildrenUnranked;
			}
		    }
		    ancestors.clear();
		}
	    }

	    toPut.put(rankedName, child);
	    toPutUnranked.put(unrankedName, child);
	}
	
	List<Map<String, AbstractNameTreeNode<?>>> classifiedChildren = new ArrayList<Map<String, AbstractNameTreeNode<?>>>(3);
	classifiedChildren.add(newChildren);
	classifiedChildren.add(indirectChildren);
	classifiedChildren.add(directChildren);

	return classifiedChildren;
    }


    protected UnitedNameTreeNode getMaxCoverageNode(UnitedNameTreeNode unitedNode,
						     Collection<UnitedNameTreeNode> candidates)
    {
	if(candidates == null ||
	   candidates.isEmpty())
	    return null;

	UnitedNameTreeNode maxCoverer = null;
	if(candidates.size() > 1) {
	    double coverage = 0.0d;
	    for(UnitedNameTreeNode u : candidates) {
		double c = unitedNode.getCoverage(u);
		if(c > coverage) {
		    coverage = c;
		    maxCoverer = u;
		}
	    }
	}

	if(maxCoverer == null) {
	    maxCoverer = candidates.iterator().next();
	}

	return maxCoverer;
    }

    /**
     * Creates a {@code UnitedNameTreeNode} for given {@code node}
     * and map the {@code node} in the {@code tree} to it.
     * It is a convinience method.
     *
     * @param tree covering given {@code node}
     * @param node NameTreeNode to be mapped
     * @return UnitedNameTreeNode to whihc the {@code node} is mapped
     */
    protected UnitedNameTreeNode mapNode(TreeModel tree,
					  NameTreeNode node)
    {
	UnitedNameTreeNode unitedNode = new UnitedNameTreeNode(node);
	mapNode(tree, node, unitedNode);
	return unitedNode;
    }

    /**
     * Maps the {@code node} in the {@code tree} to {@code UnitedNameTreeNode},
     *
     * @param tree covering given {@code node}
     * @param node NameTreeNode to be mapped
     * @param unitedNode to whihc the {@code node} is mapped
     */
    protected void mapNode(TreeModel tree,
			   NameTreeNode node,
			   UnitedNameTreeNode unitedNode)
    {
	mapNode(node, unitedNode);
	Set<NameTreeNode> nodeSet = unitedNodeToNodeSet.get(unitedNode);
	if(nodeSet == null) {
	    nodeSet = new HashSet<NameTreeNode>();
	    unitedNodeToNodeSet.put(unitedNode, nodeSet);
	}
	nodeSet.add(node);

	Map<TreeModel, NameTreeNode> inverse = unifiedByTree.get(unitedNode);
	if(inverse == null) {
	    inverse = new HashMap<TreeModel, NameTreeNode>();
	    unifiedByTree.put(unitedNode, inverse);
	}
	inverse.put(tree, node);
    }

    protected void unmapNode(TreeModel tree,
			     NameTreeNode node,
			     UnitedNameTreeNode unitedNode)
    {
	Map<TreeModel, NameTreeNode> treeMap =
	    unifiedByTree.get(unitedNode);
	if(treeMap != null) {
	    if(treeMap.size() == 1) {
		unifiedByTree.remove(unitedNode);
	    }
	    treeMap.remove(tree);
	}

	Set<NameTreeNode> nodeSet = unitedNodeToNodeSet.get(unitedNode);
	if(nodeSet != null) {
	    if(nodeSet.size() == 1) {
		unitedNodeToNodeSet.remove(unitedNode);
	    }
	    nodeSet.remove(node);
	}
	rankedNodes.remove(node);
    }


    public void remove(TreeModel tree)
    {
	if(trees == null)
	    return;

	int removed = trees.indexOf(tree);
	if(removed == -1)
	    return;

	trees.remove(tree);

	fireTreeRemoved(tree, removed);
    }

    public void treeNodesChanged(TreeModelEvent event) {;}
    public void treeNodesInserted(TreeModelEvent event) {;}

    public void treeNodesRemoved(TreeModelEvent event) {
	Object[] children = event.getChildren();
	for(Object child : children) {
	    NameTreeNode parent = 
		(NameTreeNode)event.getTreePath().getLastPathComponent();
	    NameTreeNode node = (NameTreeNode)parent.getRoot();
	    TreeModel tree = null;
	    Iterator<TreeModel> iterator = trees.iterator();
	    while(iterator.hasNext() && tree == null) {
		tree = iterator.next();
		if(tree.getRoot() != node)
		    tree = null;
	    }
	    node = (NameTreeNode)child;
	    UnitedNameTreeNode unitedNode = 
		(UnitedNameTreeNode)rankedNodes.get(node);

	    unmapNode(tree, node, unitedNode);
	}
    }
    public void treeStructureChanged(TreeModelEvent event) {;}

    public static boolean isCompatible(UnitedNameTreeNode unified,
				       NameTreeNode node)
    {
	Set<String> unifiedIncludedNames = unified.getIncludants().keySet();
	Set<String> unifiedExcludedNames = unified.getExcludants().keySet();
	NameUsage<?, ?> u = node.getNameUsage();
	Set<String> nodeIncludedNames = u.getIncludants().keySet();
	Set<String> nodeExcludedNames = u.getExcludants().keySet();

	return (!CollectionUtility.hasIntersection(unifiedIncludedNames, nodeExcludedNames) &&
		!CollectionUtility.hasIntersection(unifiedExcludedNames, nodeIncludedNames));
    }

    protected UnitedNameTreeNode getUnifiedNodeFor(NameTreeNode node,
						    TreeModel tree)
    {
	UnitedNameTreeNode unifiedNode =
	    (UnitedNameTreeNode)getNodeFor(node);

	if(unifiedNode != null)
	    return unifiedNode;

	String rankedName = node.getLiteral();

	Map<String, Set<UnitedNameTreeNode>> parentSetMap = names.get(rankedName);
	Collection<Set<UnitedNameTreeNode>> parentSetCollection = parentSetMap.values();

	// if  there is no node of the rankedName
	if(parentSetMap == null)
	    return createUnifiedNodeFor(node, tree);

	Iterator<String> parentNames = parentSetMap.keySet().iterator();

	if(parentSetMap.size() == 1 && parentSetCollection.size() == 1) {
	    Iterator<Set<UnitedNameTreeNode>> parentSetIter = parentSetCollection.iterator();
	    Set<UnitedNameTreeNode> parentSet = parentSetIter.next();
	    return getUnifiedNode(parentSet.iterator().next(), node, tree);
	}

	NameTreeNode parentNode = (NameTreeNode)node.getParent();
	String parentName = parentNode.getLiteral();
	List<String> compatibleKeys = new ArrayList<String>();
	while(parentNames.hasNext()) {
	    String key = parentNames.next();
	    boolean addKey = false;
	    Set<UnitedNameTreeNode>unifiedNodes = parentSetMap.get(key);
	    if(unifiedNodes != null) {
		for(UnitedNameTreeNode uNode : unifiedNodes) {
		    if(uNode.isCompatible(node)) {

		    }
		}
	    }
	    if(addKey)
		compatibleKeys.add(key);
	}

	unifiedNode = getUnifiedNode(compatibleKeys, parentSetMap, node, tree);
	if(unifiedNode != null) {
	    compatibleKeys.clear();
	    return unifiedNode;
	}

	parentNames = compatibleKeys.iterator();
	List<String> sameNames = new ArrayList<String>();
	for(String key : compatibleKeys) {
	    if(key.equals(parentName))
		sameNames.add(key);
	}

	unifiedNode = null;
	if(!sameNames.isEmpty())
	    unifiedNode = getUnifiedNode(sameNames, parentSetMap, node, tree);
	sameNames.clear();

	if(unifiedNode != null) {
	    compatibleKeys.clear();
	    return unifiedNode;
	}

	for (String key : compatibleKeys) {
	    Set<UnitedNameTreeNode> parentSet = parentSetMap.get(key);
	    for(UnitedNameTreeNode uNode : parentSet) {
		UnitedNameTreeNode commonNode =
		    getSharedAncestor(uNode, node);
		if(commonNode != null &&
		   commonNode != getRoot())
		    //FIXME
		    sameNames.add(commonNode.getLiteral());
	    }
	}
	
	unifiedNode = getUnifiedNode(sameNames, parentSetMap, node, tree);
	if(unifiedNode != null)
	    return unifiedNode;

	return createUnifiedNodeFor(node, tree);
    }

    protected UnitedNameTreeNode getUnifiedNode(List<String> compatibleNames,
						 Map<String, Set<UnitedNameTreeNode>> parentMap,
						 NameTreeNode node,
						 TreeModel tree)
    {
	if(compatibleNames.isEmpty())
	    return createUnifiedNodeFor(node, tree);
	if(compatibleNames.size() == 1) {
	    Set<UnitedNameTreeNode> parentSet = parentMap.get(compatibleNames.get(0));
	    if(parentSet != null && parentSet.size() == 1) {
		return getUnifiedNode(parentSet.iterator().next(), node, tree);
	    }
	}
	return null;
    }
    
    protected UnitedNameTreeNode getUnifiedNode(UnitedNameTreeNode unifiedNode,
						 NameTreeNode node,
						 TreeModel tree)
    {
	if(!unifiedNode.isCompatible(node))
	    return createUnifiedNodeFor(node, tree);

	NameTreeNode parentNode = 
	    (NameTreeNode)node.getParent();
	String parentName = parentNode.getLiteral();

	if(unifiedNode.getLiteral().equals(parentName)) {
	    mapNode(tree, node, unifiedNode);
	    return unifiedNode;
	}

	UnitedNameTreeNode commonNode =
	    getSharedAncestor(unifiedNode, node);

	if(commonNode == null ||
	   commonNode == getRoot())
	    return createUnifiedNodeFor(node, tree);
	
	UnitedNameTreeNode unifiedParent = 
	    (UnitedNameTreeNode)getNodeFor(parentNode);
	if(commonNode != unifiedParent) {
	    removeNodeFromParent(unifiedNode);
	    insertNodeInto(unifiedNode, unifiedParent, unifiedParent.getChildCount());
	}
	
	mapNode(tree, node, unifiedNode);
	
	return unifiedNode;
    }

    protected UnitedNameTreeNode getSharedAncestor(UnitedNameTreeNode unifiedNode,
						    NameTreeNode node)
    {
	TreeNode[] path = unifiedNode.getPath();
	String parentName =
	    ((NamedNode<?>)node.getParent()).getLiteral();
	boolean found = false;

	int i =  path.length;
	while(! found && i > 0) {
	    if(parentName.equals(((NamedNode<?>)path[--i]).getLiteral())) {
		found = true;
	    }
	}

	if(found)
	    return (UnitedNameTreeNode)path[i];
	
	path = node.getPath();
	parentName = 
	    ((NamedNode<?>)unifiedNode.getParent()).getLiteral();

	i =  path.length;
	while(!found && i > 0) {
	    if(parentName.equals(((NamedNode<?>)path[--i]).getLiteral())) {
		found = true;
	    }
	}

	if(found)
	    return (UnitedNameTreeNode)getNodeFor(path[i]);

	return null;
    }

    protected UnitedNameTreeNode createUnifiedNodeFor(NameTreeNode node,
						       TreeModel tree)
    {
	String rankedName = node.getLiteral();
	String literal = node.getLiteral();

	UnitedNameTreeNode unitedParent = (UnitedNameTreeNode)getRoot();
	TreeNode parent =  node.getParent();
	if(parent != null) {
	    unitedParent = (UnitedNameTreeNode)getNodeFor(parent);
	}
	String parentalName = unitedParent.getLiteral();

	UnitedNameTreeNode unifiedNode = new UnitedNameTreeNode(rankedName);
	unifiedNode.setLiteral(literal);
	
	insertNodeInto(unifiedNode, 
		       unitedParent, 
		       unitedParent.getChildCount());
	mapNode(tree, node, unifiedNode);

	return unifiedNode;
    }

    protected List<List<NamedNode<?>>> subDivide(NamedNode<?> node,
			       NamedNode<?> divider)
    {
	return subDivide(node.children(), divider);

    }


    protected List<List<NamedNode<?>>> subDivide(Enumeration<?> childrenEnum,
			       NamedNode<?> divider)
    {
	Map<String, NamedNode<?>> children = new HashMap<String, NamedNode<?>>();
	while(childrenEnum.hasMoreElements()) {
	    NamedNode<?> child = (NamedNode<?>)childrenEnum.nextElement();
	    children.put(child.getLiteral(), child);
	}

	List<List<NamedNode<?>>> subDivided = new ArrayList<List<NamedNode<?>>>();
	childrenEnum = divider.children();
	while(childrenEnum.hasMoreElements()) {
	    NamedNode<?> child = (NamedNode<?>)childrenEnum.nextElement();
	    List<NamedNode<?>> v = new ArrayList<NamedNode<?>>();
	    subDivided.add(v);
	    NamedNode<?> n = child;
	    String name = n.getLiteral();
	    n = children.get(name);
	    if(n == null)
		continue;
	    v.add(n);
	    children.remove(n);
	}

	if(!children.isEmpty()) {
	    List<NamedNode<?>> v = new ArrayList<NamedNode<?>>();
	    subDivided.add(v);
	    Collection<NamedNode<?>> namedNodes = children.values();
	    for(NamedNode<?> namedNode : namedNodes) {
		v.add(namedNode);
	    }
	}
	children.clear();
	
	return subDivided;
    }

    protected List<List<NamedNode<?>>> subDivide(Map<String, NamedNode<?>> h,
					      Map<String, NamedNode<?>> d)
    {
	List<NamedNode<?>> dividers = new ArrayList<NamedNode<?>>();
	Enumeration<?> e = 
	    ((UnitedNameTreeNode)getRoot()).depthFirstEnumeration();;

	while(e.hasMoreElements()) {
	    String name =
		((UnitedNameTreeNode)e.nextElement()).getLiteral();
	    NamedNode<?> node = d.get(name);
	    if(node != null)
		dividers.add(node);
	}

	return subDivide(h, dividers);
    }

    protected List<List<NamedNode<?>>> subDivide(Map<String, NamedNode<?>> h,
			       List<NamedNode<?>> dividers)
    {
	Map<String, NamedNode<?>> children = new HashMap<String, NamedNode<?>>(h.size());
	Collection<NamedNode<?>> namedNodes  = h.values();
	for(NamedNode<?> child : namedNodes) {
	    children.put(child.getLiteral(), child);
	}

	List<List<NamedNode<?>>> subDivided = new ArrayList<List<NamedNode<?>>>(dividers.size() + 1);
	for (TreeNode treeNode : dividers) {
	    Enumeration<?> c = treeNode.children();
	    List<NamedNode<?>> v = new ArrayList<NamedNode<?>>();
	    subDivided.add(v);

	    while(c.hasMoreElements()) {
		NamedNode<?> child = (NamedNode<?>)c.nextElement();
		String name = child.getLiteral();
		if(children.get(name) == null)
		    continue;
		v.add(child);
		children.remove(name);
	    }
	}

	if(!children.isEmpty()) {
	    List<NamedNode<?>> v = new ArrayList<NamedNode<?>>(children.size());
	    subDivided.add(v);
	    for(NamedNode<?> node : children.values()) {
		v.add(node);
	    }
	}

	children.clear();
	
	return subDivided;
    }

    protected List<List<NamedNode<?>>> subDivide(Map<String, NamedNode<?>> h,
			       NamedNode<?> divider)
    {
	List<NamedNode<?>> dividers = new ArrayList<NamedNode<?>>(divider.getChildCount());
	Enumeration<?> e = divider.children();
	while(e.hasMoreElements()) {
	    dividers.add((NamedNode<?>)e.nextElement());
	}

	return subDivide(h, dividers);
    }

    protected void processSubDivision(Enumeration<?> divisions,
				      NameTreeNode node,
				      Map<String, ? extends NamedNode<?>> children,
				      TreeModel tree)
    {
	UnitedNameTreeNode unitedParent = 
	    (UnitedNameTreeNode)getNodeFor(node);
	while(divisions.hasMoreElements()) {
	    Enumeration<?> subDivisions = 
		((Vector)divisions.nextElement()).elements();
	    while(subDivisions.hasMoreElements()) {
		NamedNode<?> n = 
		    (NamedNode<?>)subDivisions.nextElement();
		String rankedName = n.getLiteral();
		UnitedNameTreeNode unitedNode = null;
		//?
		if(rankedName == null || rankedName.length() == 0)
		    continue;
		if(n instanceof UnitedNameTreeNode) {
		    unitedNode = (UnitedNameTreeNode)n;
		    n = (NamedNode<?>)children.get(rankedName);
		}
		else {
		    unitedNode = (UnitedNameTreeNode)getNodeFor((TreeNode)n);
		}

		NameTreeNode child = (NameTreeNode)n;
		add((NameTreeModel)tree, node, child);
		if(unitedNode != null) {
		    mapNode(tree, child, unitedNode);
		}
		else {
		    unitedNode = getUnifiedNodeFor(child, tree);
		}
		
		UnitedNameTreeNode p = 
		    (UnitedNameTreeNode)unitedNode.getParent();
		if(p != unitedParent &&
		   unitedParent.isNodeAncestor(p) &&
		   p != getRoot()) {
		    p.remove(unitedNode);
		    add(unitedParent, unitedNode);
		}
		children.remove(rankedName);
	    }
	}
    }

    public Map<String, NamedNode<?>> getChildParentRelationships()
    {
	Map<String, NamedNode<?>> h = new HashMap<String, NamedNode<?>>();
	Enumeration<?> e = 
	    ((DefaultMutableTreeNode)getRoot()).breadthFirstEnumeration();
	while(e.hasMoreElements()) {
	    NamedNode<?> n = 
		(NamedNode<?>)e.nextElement();
	    h.put(n.getChildParentLiteral(), n);
	}

	return h;
    }

    public Collection<String> getLiterals()
    {
	return names.keySet();
    }

    public Map<String, Set<UnitedNameTreeNode>> getRankedUnitedNodes()
    {
	return null;
    }

    public List<NamedNode<?>> getNodesForLiteral(String literal)
    {
       return getNodesForLiteral(literal, null);
    }

    public List<NamedNode<?>> getNodesForLiteral(String literal, Rank rank)
    {
       Set<NamedNode<?>> h = null;
       if(literal == null) {
           return null;
       }
       literal = literal.toLowerCase();
       if(!literal.startsWith("*") && !literal.startsWith("%") &&
          !literal.endsWith("*") && !literal.endsWith("%")) {
	   Map<String, Set<UnitedNameTreeNode>> map = names.get(literal);
	   if (map != null) {
	       Collection<Set<UnitedNameTreeNode>>values =  map.values();
	       h =new HashSet<NamedNode<?>>();
	       for (Set<UnitedNameTreeNode> set :values) {
		   for (UnitedNameTreeNode node: set) {
		       h.add(node);
		   }
	       }
	   }
       }
       else if((literal.startsWith("*")  || literal.startsWith("%")) &&
          !literal.endsWith("*") && !literal.endsWith("%")) {
           int index = literal.indexOf("*");
           if(index == -1)
               index = literal.indexOf("%");
           if(index == -1)
               index = 0;
           else
               index++;
           h = backwardMatches.get(literal.substring(index));
       }
       else if(!literal.startsWith("*")  && !literal.startsWith("%") &&
          (literal.endsWith("*") || !literal.endsWith("%"))) {
           int index = literal.indexOf("*");
           if(index == -1)
               index = literal.indexOf("%");
           if(index == -1)
               index = literal.length();
           else
               index--;
           h = forwardMatches.get(literal.substring(0, index));
       }
       if(h == null) {
           Set<NamedNode<?>> f = forwardMatches.get(literal);
           Set<NamedNode<?>> b = backwardMatches.get(literal);
           if(f == null && b == null) {
               h = new HashSet<NamedNode<?>>();
           }
           else if(f == null) {
               h = new HashSet<NamedNode<?>>(b);
           }
           else if(b == null) {
               h = new HashSet<NamedNode<?>>(f);
           }
           else {
               h = new HashSet<NamedNode<?>>(f.size() > b.size() ? f : b);
               h.addAll(f.size() < b.size() ? f : b);
           }
       }

       if(h == null) {
           return null;
       }

       String rankName = (rank == null) ? null : rank.getName();
       if(rank == null || rankName.length() == 0)
           return new ArrayList<NamedNode<?>>(h);

       String r = Rank.getAbbreviation(rankName);
       if(r == null)
           r = rankName;

       List<NamedNode<?>> v = new ArrayList<NamedNode<?>>();
       for(NamedNode<?> n : h) {
           if(n.getRankedName().startsWith(r))
               v.add(n);
       }
       h.clear();

       return v;
    }

    public void addUnitedNameTreeModelListener(UnitedNameTreeModelListener listener)
    {
	if(listeners == null)
	    listeners = new EventListenerList();
	synchronized(listeners) {
	    listeners.add(UnitedNameTreeModelListener.class, listener);
	}
    }

    public void removeUnitedNameTreeModelListener(UnitedNameTreeModelListener listener)
    {
	if(listeners == null)
	    return;
	synchronized(listeners) {
	    listeners.remove(UnitedNameTreeModelListener.class, listener);
	}
    }

    protected void fireTreeAdded(TreeModel tree)
    {
	if(listeners == null)
	    return;

	UnitedNameTreeModelEvent event
	    = new UnitedNameTreeModelEvent(this, tree, -1, trees.size());

	synchronized(listeners) {
	    UnitedNameTreeModelListener[] targets = 
		listeners.getListeners(UnitedNameTreeModelListener.class);
	    for(UnitedNameTreeModelListener target : targets) {
                target.treeAdded(event);
	    }
	}
    }

    protected void fireTreeRemoved(TreeModel tree,
				     int removed)
    {
	if(listeners == null)
	    return;

	UnitedNameTreeModelEvent event
	    = new UnitedNameTreeModelEvent(this, tree, removed, -1);

	synchronized(listeners) {
	    UnitedNameTreeModelListener[] targets = 
		listeners.getListeners(UnitedNameTreeModelListener.class);
	    for(UnitedNameTreeModelListener target : targets) {
                target.treeRemoved(event);
	    }
	}
    }

    protected NamedNode<?>[] toArray(Enumeration<NamedNode<?>> e)
    {
	if(e == null)
	    return new NamedNode<?>[0];

	Vector<NamedNode<?>> v = new Vector<NamedNode<?>>();
	while(e.hasMoreElements()) {
	    v.addElement(e.nextElement());
	}
	NamedNode<?>[] nodes = new NamedNode<?>[v.size()];
	nodes = v.toArray(nodes);
	v.clear();
	return nodes;
    }
}
