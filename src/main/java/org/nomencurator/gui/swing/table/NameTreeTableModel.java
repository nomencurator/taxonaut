/*
 * NameTreeTableModel.java: a TableModel to compare NameTrees
 *
 * Copyright (c) 2003, 2004, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.table;

import java.text.Collator;
import java.text.CollationKey;
import java.text.RuleBasedCollator;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JTable;

import javax.swing.event.EventListenerList;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Rank;

import org.nomencurator.gui.swing.event.NameTreeTableModelEvent;
import org.nomencurator.gui.swing.event.NameTreeTableModelListener;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelEvent;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelListener;
import org.nomencurator.gui.swing.event.UniqueEventListenerList;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;

import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;

import org.nomencurator.io.ProgressEvent;
import org.nomencurator.io.ProgressListener;

/**
 * {@code NameTreeTableMode} provieds a {@code TableModel} to compare {@code NameTree}s.
 *
 * @version 	23 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableModel
    extends AbstractTableModel
    implements RowMapper,
	       UnitedNameTreeModelListener
{
    private static final long serialVersionUID = 928495398386167837L;

    protected List<Map<String, List<Integer>>> rowMapList;

    protected Map<String, Collection<Map<TreeModel, NameTreeNode>>> synonyms;

    protected List<String> unifiedNodeLiterals;

    protected List<List<String>> unifiedNodeLiteralList;

    protected List<TreeModel> columnIdentifiers;

    protected static RuleBasedCollator collator = 
	(RuleBasedCollator)Collator.getInstance(Locale.US);

    protected NameTreeTableMode tableMode;

    public static final int AS_IS = 0;

    public static final int ASSIGNMENT_TO = 0x1;

    public static final int EXCLUDE_COMMON_ASSIGNMENTS = 0x2;

    public static final int EXCLUDE_DIFFERENT_ASSIGNMENTS = 0x4;

    public static final int EXCLUDE_MISSING_ASSIGNMENTS = 0x8;

    public static final int MISSING_ASSIGNMENTS_ONLY = 0x10;

    public static final int COMMON_TAXA = 0x20;

    public static final int MISSING_TAXA = 0x40;

    public static final int DIFFERENT_TAXA = 0x80;

    public static final int INCONSISTENT_TAXA = 0x100;

    public static final int SYNONYMOUS_TAXA = 0x200;

    public static final int COMMON_TAXA_INDEX = 1;

    public static final int MISSING_TAXA_INDEX = COMMON_TAXA_INDEX + 1;

    public static final int DIFFERENT_TAXA_INDEX = MISSING_TAXA_INDEX + 1;

    public static final int INCONSISTENT_TAXA_INDEX = DIFFERENT_TAXA_INDEX  + 1;

    public static final int SYNONYMOUS_TAXA_INDEX = INCONSISTENT_TAXA_INDEX + 1;

    protected static final int TAXA_TYPE_BASE_SHIFT = 5;

    public static final NameTreeTableMode[] tableModes = NameTreeTableMode.values();

    protected UnitedNameTreeModel unitedTree;

    protected TableColumnModel columnModel;

    protected JTable table;

    protected UniqueEventListenerList listeners;

    protected NameTreeTableModel masterTable;

    public static List<NameTreeTableModel> createTableModels(UnitedNameTreeModel unitedTree)
    {
	final List<NameTreeTableModel> models = new ArrayList<NameTreeTableModel>(tableModes.length);
	NameTreeTableModel masterTable = new NameTreeTableModel(unitedTree, NameTreeTableMode.ASSIGNMENTS);
	models.add(masterTable);
	for (int i = 1; i < tableModes.length; i++)
	    models.add(masterTable.getTableModel(tableModes[i]));
	return models;
    }

    public NameTreeTableModel getTableModel(NameTreeTableMode tableMode)
    {
	return new NameTreeTableModel(this, tableMode);
    }

    protected NameTreeTableModel(NameTreeTableModel masterTable, NameTreeTableMode tableMode)
    {
	super();
	this.masterTable = masterTable;
	this.tableMode = tableMode;
    }

    public NameTreeTableModel()
    {
	this(new UnitedNameTreeModel());
    }

    public NameTreeTableModel(UnitedNameTreeModel tree)
    {
	this(tree, NameTreeTableMode.ASSIGNMENTS);
    }

    public NameTreeTableModel(UnitedNameTreeModel tree, NameTreeTableMode extractionMode)
    {
	this(tree, null, extractionMode);
    }

    public NameTreeTableModel(UnitedNameTreeModel tree, String rank)
    {
	this(tree, rank, NameTreeTableMode.ASSIGNMENTS);
    }

    public NameTreeTableModel(UnitedNameTreeModel tree, String rank, NameTreeTableMode extractionMode)
    {
	this(tree, null, rank, extractionMode);
    }

    public NameTreeTableModel(UnitedNameTreeModel unitedTree,
			      Collection<TreeModel> trees, 
			      String rank, NameTreeTableMode tableMode)
    {
	super();
	columnIdentifiers = new ArrayList<TreeModel>();
	if(unitedTree == null)
	    unitedTree = new UnitedNameTreeModel();
	this.unitedTree = unitedTree;
	columnIdentifiers.add(unitedTree);
	this.tableMode = tableMode;
	Iterator<TreeModel> models = null;
	if (trees != null && !trees.isEmpty()) {
	    models = trees.iterator();
	    while(models.hasNext()) {
		TreeModel model =models.next();
		//header.addElement(model);
		unitedTree.add(model);
	    }
	}

	models = unitedTree.getTrees();
	while(models.hasNext()){
	    columnIdentifiers.add(models.next());
	}

	unitedTree.addUnitedNameTreeModelListener(this);

	//if(NameTreeTableMode.SYNONYMS.equals(tableMode))
	    synonyms = new HashMap<String, Collection<Map<TreeModel, NameTreeNode>>>();

	unifiedNodeLiteralList =  updateUnifiedNodeLiteralList();
	rowMapList = updateRowMapList();
    }

    public void setColumnModel(TableColumnModel columnModel)
    {
	this.columnModel = columnModel;
    }

    public TableColumnModel getColumnModel()
    {
	return columnModel;
    }

    public void setTable(JTable table)
    {
	this.table = table;
    }

    public JTable getTable()
    {
	return table;
    }

    protected Map<String, List<Integer>> getRowMap()
    {
	return getRowMapList().get(tableMode.ordinal());
    }

    protected List<Map<String, List<Integer>>> getRowMapList()
    {
	if (rowMapList == null)
	    rowMapList = updateRowMapList();
	return rowMapList;
    }

    protected List<Map<String, List<Integer>>> updateRowMapList()
    {
	final List<Map<String, List<Integer>>> rowMapList = new ArrayList<Map<String, List<Integer>>>(tableModes.length);
	final List<List<String>> unifiedNodeLiteralList = getUnifiedNodeLiteralList();

	for (NameTreeTableMode tableMode : tableModes) {
	    final Map<String, List<Integer>> rowMap = new HashMap<String, List<Integer>>();
	    rowMapList.add(rowMap);
	    final List<String>unifiedNodeLiterals = unifiedNodeLiteralList.get(tableMode.ordinal());
	    for(int row = 0; row < unifiedNodeLiterals.size(); row++) {
		String key = unifiedNodeLiterals.get(row);
		if(key == null)
		    continue;
		List<Integer> rows = rowMap.get(key);
		if(rows == null) {
		    rows = new ArrayList<Integer>();
		    rowMap.put(key, rows);
		}
		rows.add(row);
	    }
	}

	return rowMapList;
    }

    protected UnitedNameTreeModel getUnitedTree()
    {
	if (masterTable != null)
	    return masterTable.getUnitedTree();

	return unitedTree;
    }

    protected List<List<String>> getUnifiedNodeLiteralList()
    {
	if (masterTable != null)
	    return masterTable.getUnifiedNodeLiteralList();

	if (unifiedNodeLiteralList == null) {
	    unifiedNodeLiteralList = updateUnifiedNodeLiteralList();
	}
	return unifiedNodeLiteralList;
    }

    protected List<String> getUnifiedNodeLiterals()
    {
	return getUnifiedNodeLiteralList().get(tableMode.ordinal());
    }

    public int getColumnCount()
    {
	return getUnitedTree().getTreeCount() + 1;
    }

    public int getRowCount()
    {
	return getUnifiedNodeLiterals().size();
    }

    protected Map<String, Collection<Map<TreeModel, NameTreeNode>>> getSynonyms()
    {
	return (masterTable == null) ? synonyms : masterTable.synonyms;
    }


    public Object getValueAt(int row, int column)
    {
	if(row < 0) {
	    if (masterTable != null)
		return masterTable.getValueAt(row, column);
	    return columnIdentifiers.get(column);
	}

	String literal = getUnifiedNodeLiterals().get(row);

	// expecting that covnersion of column number from view to model is already done before calling this method
	if(column == 0)
	    return literal;

	UnitedNameTreeModel unitedTree = getUnitedTree();
	NameTreeModel tree = 
	    (NameTreeModel)unitedTree.getTreeList().get(--column);

	if (!NameTreeTableMode.SYNONYMS.equals(tableMode)) {
	    Collection<UnitedNameTreeNode> unifieds = unitedTree.getNodesFor(literal);
	    if (unifieds == null || unifieds.size() == 0)
		return null;
	    Set<NameTreeNode> nodes = new HashSet<NameTreeNode>();
	    for (UnitedNameTreeNode unified : unifieds) {
		TreeNode node = unitedTree.getNodeFor(unified, tree);
		if(node != null && node instanceof NameTreeNode) {
		    node = (NameTreeNode)node.getParent();
		    if (node != null && node instanceof NameTreeNode)
			nodes.add((NameTreeNode)node);
		}
	    }
	    switch (nodes.size()) {
	    case 0:
		return null;
	    case 1:
		return nodes.iterator().next();
	    default:
		return nodes;
	    }
	}

	Iterator<Map<TreeModel, NameTreeNode>> synonymMaps =
	    getSynonyms().get(literal).iterator();
	while (synonymMaps.hasNext()) {
	    NameTreeNode synonym = synonymMaps.next().get(tree);
	    if(synonym != null)
		return synonym;
	}

	return null;
    }


    public boolean isCellEditable(int row, int column)
    {
	if(row < 0)
	    return true;

	return false;
    }

    public Collection<Integer> getRows(String rankedName)
    {
	List<Integer> rows = getRowMap().get(rankedName);
	if(rows == null) {
	    return null;
	}
	return new ArrayList<Integer>(rows);
    }

    public String getColumnName(int column)
    {
	if (masterTable != null)
	    return masterTable.getColumnName(column);

        if (columnIdentifiers == null
	    || columnIdentifiers.size() <= column) {
            return super.getColumnName(column);
        }
	
        Object id = columnIdentifiers.get(column);
        if (id == null)
            return super.getColumnName(column);

	if(id instanceof NameTreeModel)
	    return ((NameTreeModel)id).getViewName();
	else if (id instanceof UnitedNameTreeModel)
	    return "Integration";
	
	return id.toString();
    }

    public void setValueAt(Object value, int row, int column)
    {
	if(row < 0)
	    return;

	//super.setValueAt(value, row, column);
    }

    /*
    public Object getValueAt(int row, int column)
    {
	if(row < 0)
	    return columnIdentifiers.get(column);
	else
	    return super.getValueAt(row, column);
    }
    */

    public List<TreeModel> getCoulumnIdentifiers()
    {
	return columnIdentifiers;
    }

    public Object getHeaderValue(int index)
    {
	if (masterTable != null)
	    return masterTable.getHeaderValue(index);
	return columnIdentifiers.get(index);
    }

    public NameTreeModel getTreeModel(int index)
    {
	if (masterTable != null)
	    return masterTable.getTreeModel(index);
	return (NameTreeModel)columnIdentifiers.get(index + 1);
    }

    protected List<NamedTreeNodeList<TreeNode>> getAssignments(List<TreeModel> trees, String rank, NameTreeTableMode extractionMode)
    {
	if(rank == null)
	    return getParentAssignments(trees, extractionMode);

	List<NamedTreeNodeList<TreeNode>> v = null;
	List<NamedTreeNodeList<TreeNode>> s = null;

	if((extractionMode.mask() & ASSIGNMENT_TO) != 0) {
	    v = getAssignmentsTo(trees, "super" + rank, extractionMode);
	    v.addAll(getAssignmentsTo(trees, rank, extractionMode));
	    v.addAll(getAssignmentsTo(trees, "infra" + rank, extractionMode));
	    return v;
	}
	
	v = getAssignmentsOf(trees, "super" + rank, extractionMode);
	v.addAll(getAssignmentsOf(trees, rank, extractionMode));
	v.addAll(getAssignmentsOf(trees, "sub" + rank, extractionMode));
	v.addAll(getAssignmentsOf(trees, "infra" + rank, extractionMode));
	return v;
    }

    public UnitedNameTreeModel getUnitedNameTreeModel()
    {
	return unitedTree;
    }

    protected boolean isSelfConsistent(int exclusionMode)
    {
	return (((exclusionMode & EXCLUDE_COMMON_ASSIGNMENTS) != 0) &&
	       (((exclusionMode & EXCLUDE_DIFFERENT_ASSIGNMENTS) != 0) ||
		((exclusionMode & EXCLUDE_MISSING_ASSIGNMENTS) != 0)));
    }


    protected int remove(List<TreeModel> trees,
			 Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable,
			 EnumSet<NameTreeTableMode> exclusionMode)
    {
	/*
	if(!isSelfConsistent(exclusionMode))
	    return -1;
	*/

	boolean excludeCommon = exclusionMode.contains(NameTreeTableMode.COMMONS);

	boolean excludeDifference =exclusionMode.contains(NameTreeTableMode.DIFFERENCE);

	boolean excludeMissings =exclusionMode.contains(NameTreeTableMode.MISSINGS);

	boolean missingsOnly = excludeMissings && !excludeCommon && !excludeDifference;

	boolean missingOrDifferent = excludeDifference | excludeMissings;

	if(!excludeCommon && !excludeDifference && 
	   !excludeMissings && !missingsOnly)
	    return 0;

	List<String> toBeRemoved = new ArrayList<String>();

	int removed = 0;

	Set<String> ranks = rankedNameTable.keySet();
	for(String rank : ranks) {
	    Map<String, Map<TreeModel, TreeNode>> namesAtRank = rankedNameTable.get(rank);
	    
	    Set<String> names = namesAtRank.keySet();		    
	    for (String name : names) {
		Map<TreeModel, TreeNode> nodesOfTheName = namesAtRank.get(name);
		Iterator<TreeModel> treeIterator = trees.iterator();
		if(!treeIterator.hasNext())
		    continue;
		boolean viewDepend = false;
		boolean hasEmpty   = false;
		NameTreeNode node  = (NameTreeNode)nodesOfTheName.get(treeIterator.next());
		if(node == null) {
		    if(excludeMissings || excludeDifference) {
			toBeRemoved.add(name);
			nodesOfTheName.clear();
			continue;
		    }
		    hasEmpty = true;
		    viewDepend = true;
		    while(node == null && treeIterator.hasNext())
			node  = (NameTreeNode)nodesOfTheName.get(treeIterator.next());
		}
		String parentName = node.getLiteral(/*getRankedName(*/);
		while(treeIterator.hasNext()) {
		    node = (NameTreeNode)nodesOfTheName.get(treeIterator.next());
		    if(node == null) {
			hasEmpty = true;
			viewDepend = true;
			if(excludeMissings || excludeDifference)
			    break;
		    }
		    else if (!parentName.equals(node.getLiteral(/*getRankedName(*/))) {
			viewDepend = true;
			if(excludeDifference)
			    break;
		    }
		}

		if((viewDepend && excludeDifference) ||
		   (hasEmpty && excludeMissings) ||
		   (!hasEmpty && missingsOnly) ||
		   (!viewDepend && excludeCommon)) {
		    toBeRemoved.add(name);
		    nodesOfTheName.clear();
		}
		
	    }
	    
	    removed += toBeRemoved.size();
	    for (String nameToRemove : toBeRemoved) {
		namesAtRank.remove(nameToRemove);
	    }
	    toBeRemoved.clear();
	}

	removeEmptyEntries(rankedNameTable);

	return removed;
    }

    protected void removeEmptyEntries(Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable)
    {
	List<String> toBeRemoved = new ArrayList<String>();

	//find empty entries
	Set<String> ranks = rankedNameTable.keySet();
	for (String rank : ranks) {
	    if(rankedNameTable.get(rank).isEmpty())
		toBeRemoved.add(rank);
	}
	
	//remove empty entries
	for (String rank : ranks) {
	    rankedNameTable.remove(rank);
	}

	toBeRemoved.clear();
    }
    
    protected List<NamedTreeNodeList<TreeNode>> getParentAssignments(List<TreeModel> trees,
					  NameTreeTableMode exclusionMode)
    {
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable = getDirectAssignments(trees);

	if(NameTreeTableMode.ASSIGNMENTS != exclusionMode)
	    remove(trees, rankedNameTable, EnumSet.of(exclusionMode));

	List<NamedTreeNodeList<TreeNode>> v = hashToList(trees, rankedNameTable);

	clearMaps(rankedNameTable);

	return v;
    }

    protected List<NamedTreeNodeList<TreeNode>> createUnifiedNodeList(UnitedNameTreeModel tree,
								      NameTreeTableMode tableMode)
    {
	List<TreeModel> trees = tree.getTreeList();
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable = getDirectAssignments(trees);

	EnumSet<NameTreeTableMode> exclusionMode = EnumSet.of(tableMode);
	if(!exclusionMode.contains(NameTreeTableMode.ASSIGNMENTS))
	    remove(trees, rankedNameTable, exclusionMode);

	List<NamedTreeNodeList<TreeNode>> v = hashToList(trees, rankedNameTable);

	clearMaps(rankedNameTable);

	return v;
    }

    //protected void clearMaps(Map<String, Map<String, Map<NameTreeModel, TreeNode>>> rankedNameTable)
    protected void clearMaps(Map<?, ?> map)
    {
	Collection<?> values = map.values();
	for(Object value : values) {
	    if(value instanceof Map)
		clearMaps((Map<?, ?>)value);
	}

	map.clear();

	System.gc();
    }

    protected List<NamedTreeNodeList<TreeNode>> getAssignments(List<TreeModel> trees, String rank, boolean isTarget)
    {
	if(rank == null)
	    return getParentAssignments(trees, isTarget);

	List<NamedTreeNodeList<TreeNode>> v = null;
	List<NamedTreeNodeList<TreeNode>> s = null;
	Iterator<NameTreeNode> e = null;

	if(isTarget) {
	    v = getAssignmentsTo(trees, "super" + rank);
	    v.addAll(getAssignmentsTo(trees, rank));
	    v.addAll(getAssignmentsTo(trees, "sub" + rank));
	    v.addAll(getAssignmentsTo(trees, "infra" + rank));
	    return v;
	}
	
	v = getAssignmentsOf(trees, "super" + rank);
	v.addAll(getAssignmentsOf(trees, rank));
	v.addAll(getAssignmentsOf(trees, "sub" + rank));
	v.addAll(getAssignmentsOf(trees, "infra" + rank));
	return v;
    }

    protected List<NamedTreeNodeList<TreeNode>> getParentAssignments(List<TreeModel> trees, 
					  boolean incompatible)
    {
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable = getDirectAssignments(trees);

	if(incompatible) {
	    List<String> toBeRemoved = new ArrayList<String>();
	    Set<String> ranks = rankedNameTable.keySet();
	    for(String rank : ranks) {
		Map<String, Map<TreeModel, TreeNode>> namesAtRank =
		    rankedNameTable.get(rank);

		Set<String> names = namesAtRank.keySet();

		if(!trees.isEmpty()) {
		    for(String name : names) {
			Map<TreeModel, TreeNode> nodesOfTheName =
			    namesAtRank.get(name);
			Iterator<TreeModel> treeIterator = trees.iterator();
			NameTreeNode node = null;
			TreeNode treeNode = nodesOfTheName.get(treeIterator.next());
			if(treeNode instanceof NameTreeNode)
			    node = (NameTreeNode)treeNode;
			if(node == null) {
			    continue;
			}
			String parentName = node.getLiteral(/*getRankedName(*/);
			boolean viewDepend = false;
			while(treeIterator.hasNext()) {
			    treeNode = nodesOfTheName.get(treeIterator.next());
			    if(treeNode instanceof NameTreeNode)
				node = (NameTreeNode)treeNode;
			    if(node == null ||
			       !parentName.equals(node.getLiteral(/*getRankedName(*/))) {
				viewDepend = true;
				break;
			    }
			}
			if(!viewDepend) {
			    toBeRemoved.add(name);
			    nodesOfTheName.clear();
			}
			    
		    }
		}

		for(String removeIt : toBeRemoved) {
		    namesAtRank.remove(removeIt);
		}
		toBeRemoved.clear();
	    }

	    toBeRemoved.clear();
	    for(String rank : ranks) {
 		if(rankedNameTable.get(rank).isEmpty())
		    toBeRemoved.add(rank);
	    }

	    for(String removeIt : toBeRemoved) {
		rankedNameTable.remove(removeIt);
	    }
	}

	List<NamedTreeNodeList<TreeNode>> list = hashToList(trees, rankedNameTable);

	clearMaps(rankedNameTable);

	return list;
    }

    protected Map<String, Map<String, Map<TreeModel, TreeNode>>> getDirectAssignments(List<TreeModel> trees)
    {
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable = 
	    new HashMap<String, Map<String, Map<TreeModel, TreeNode>>>();

	for(TreeModel model : trees) {
	    if(!(model instanceof NameTreeModel))
		continue;
	    NameTreeModel tree = (NameTreeModel)model;

	    Map<String, Map<String, NameTreeNode>> namesAtRank =
		tree.getNamesAtRankSet();

	    if(namesAtRank == null)
		continue;

	    Set<String> ranks = namesAtRank.keySet();
	    for(String rank : ranks) {
	
		Map<String, Map<TreeModel, TreeNode>> names = rankedNameTable.get(rank);
		if(names == null) {
		    names = new HashMap<String, Map<TreeModel, TreeNode>>();
		    rankedNameTable.put(rank, names);
		}
		    
		Map<String, NameTreeNode> nodes = namesAtRank.get(rank);
		Set<String> nameKeys = nodes.keySet();
		for (String name : nameKeys) {
		    TreeNode node = nodes.get(name).getParent();
		    if(node != null) {
			Map<TreeModel, TreeNode> nodesOfTheName = names.get(name);
			if(nodesOfTheName == null) {
			    nodesOfTheName = new HashMap<TreeModel, TreeNode>();
			    names.put(name, nodesOfTheName);
			}
			nodesOfTheName.put(tree, node);
		    }
		}
	    }

	}

	return rankedNameTable;
    }

    /**
     * Returns list of list of name literals in specified <tt>UnitedNameTreeModel</tt>,
     * indexed by type of extraction.
     *
     * @param tree <tt>UnitedNameTreeModel</tt> to retrieve
     * @param exclusionMode parameter to specify taxa to be excluded from the name list
     *
     * @return List of <tt>String</tt>s representing nams with their rank.
     */
    protected List<String> getRankedNameList(UnitedNameTreeModel tree,
				       NameTreeTableMode tableMode)
    {
	if (unifiedNodeLiteralList == null)
	    return null;
	return unifiedNodeLiteralList.get(tableMode.ordinal());
    }

    /**
     * Returns list of list of name literals in specified <tt>UnitedNameTreeModel</tt>,
     * indexed by type of extraction.
     *
     * @param tree <tt>UnitedNameTreeModel</tt> to retrieve
     * @param exclusionMode parameter to specify taxa to be excluded from the name list
     *
     * @return List of <tt>String</tt>s representing nams with their rank.
     */
    //protected List<String> getRankedNameList(UnitedNameTreeModel tree,
    //protected List<List<String>> getRankedNameList(int exclusionMode)
    protected List<List<String>> updateUnifiedNodeLiteralList()
    {
	UnitedNameTreeModel unitedTree = getUnitedNameTreeModel();
	List<Set<String>> literalSets = new ArrayList<Set<String>>();
	for (int i = 0; i < tableModes.length; i++) {
	    literalSets.add(new HashSet<String>());
	}

	// UnitedNameTreeNode set indexed by name literal
	Map<String, Set<UnitedNameTreeNode>> rankedNameTable = getUnitedNameTreeModel().getRankedUnitedNodes();

	// List of trees retained by the UnitedNameTreeModel
	List<TreeModel> trees = unitedTree.getTreeList();
	int treeCount = trees.size();

	// List of root nodes  of each tree retained by the UnitedNameTreeModel
	List<Object> roots = new ArrayList<Object>(treeCount);
	for (TreeModel treeModel : trees) {
	    roots.add(treeModel.getRoot());
	}

	Collection<String> literals = unitedTree.getLiterals();

	for (String literal : literals) {
	    NameTreeTableMode taxaType = NameTreeTableMode.ASSIGNMENTS;
	    final Set<UnitedNameTreeNode> allParents = new HashSet<UnitedNameTreeNode>();
	    final Set<String> unifiedParentNames = new HashSet<String>();
	    final Map<String, Set<UnitedNameTreeNode>> parentSets = new HashMap<String, Set<UnitedNameTreeNode>>();
	    final Map<TreeModel, NameTreeNode> parents = new HashMap<TreeModel, NameTreeNode>();
	    final Map<String, NameTreeNode> parentNames = new HashMap<String, NameTreeNode>();

	    final Collection<UnitedNameTreeNode> nodes = unitedTree.getNodesFor(literal);
	    if (nodes == null)
		continue;
	    if (nodes.size() > 1)
		taxaType = NameTreeTableMode.INCONSISTENCY;
	    for (UnitedNameTreeNode uNode : nodes) {
		UnitedNameTreeNode parent = (UnitedNameTreeNode)uNode.getParent();
		if (parent != null) {
		    allParents.add(parent);
		    String unifiedParentLiteral = parent.getLiteral();
		    Set<UnitedNameTreeNode> parentSet = parentSets.get(unifiedParentLiteral);
		    if(parentSet == null) {
			parentSet = new HashSet<UnitedNameTreeNode>();
			parentSets.put(unifiedParentLiteral, parentSet);
		    }
		    parentSet.add(parent);
	    
		    unifiedParentNames.add(unifiedParentLiteral);
		}
	    }
    
	    // if there are two or more higher literals, reduce the name list
	    // by removing names on higher node path
	    if (unifiedParentNames.size() > 1) {
		final Collection<String> keys = parentSets.keySet();
		for (String key : keys) {
		    if(!unifiedParentNames.contains(key))
			continue;
		    final Set<UnitedNameTreeNode> parentSet = parentSets.get(key);
		    final Iterator<String> keysToTest = parentSets.keySet().iterator();
		    while (keysToTest.hasNext() && unifiedParentNames.contains(key)) {
			String keyToTest = keysToTest.next();
			if(key.equals(keyToTest) || !unifiedParentNames.contains(keyToTest))
			    continue;
		
			String nameToRemove = null;
		
			final Set<UnitedNameTreeNode> toTest = parentSets.get(keyToTest);
			final Iterator<UnitedNameTreeNode> targets = toTest.iterator();
			while (targets.hasNext() && nameToRemove == null) {
			    if (targets.next().getAncestor(key) != null)
				nameToRemove = keyToTest;
			    if (nameToRemove == null) {
				final Iterator<UnitedNameTreeNode> parentIterator = parentSet.iterator();
				while (parentIterator.hasNext() && nameToRemove == null) {
				    if (parentIterator.next().getAncestor(keyToTest) != null)
					nameToRemove = key;
				}
			    }
			}
			if (nameToRemove != null)
			    unifiedParentNames.remove(nameToRemove);
		    }
		}
	    }

	    int status = 0;
    
	    // type of taxa of the name
	    if (unifiedParentNames.size() > 1) {
		taxaType = NameTreeTableMode.INCONSISTENCY;
	    }
	    else {
		final Set<NameTreeNode> treeIteratorSource = new HashSet<NameTreeNode>();
		for (UnitedNameTreeNode unifiedNode : nodes) {
		    final Iterator<? extends TreeNode> treeSubIterator = unitedTree.getNodesFor(unifiedNode);
		    if(treeSubIterator != null) {
			while (treeSubIterator.hasNext()) {
			    final TreeNode treeNode = treeSubIterator.next();
			    if(treeNode instanceof NameTreeNode)
				treeIteratorSource.add((NameTreeNode)treeNode);
			}
		    }
		}
		final Iterator<NameTreeNode> treeIterator = treeIteratorSource.iterator();
	
		if(treeIterator == null) {
		}
		else {
		    int nodeCount = 0;
		    boolean noneHasParent = true;
		    String parentName = null;
		    while(treeIterator.hasNext()) {
			NameTreeNode node = treeIterator.next();
			nodeCount++;
			Object parentNode = node.getParent();
			node = (parentNode == null || parentNode instanceof NameTreeNode) ?
			    (NameTreeNode)parentNode : null;
			// if the node has a parental node...
			if(node != null) {
			    noneHasParent &= false;
			    parentNames.put(node.getLiteral(/*getRankedName(*/), node);
			    Object root = node.getRoot();
			    for(int k = 0; k < treeCount; k++) {
				if(root == roots.get(k)) {
				    parents.put(trees.get(k), node);
				    break;
				}
			    }
			}
		    }
	    
		    // check taxa category
		    if(nodeCount < treeCount)
			taxaType = NameTreeTableMode.MISSINGS;
		    else if (parentNames.size() == 1)
			taxaType = NameTreeTableMode.COMMONS;
		    else if (parentNames.size() == 0 && noneHasParent)
			taxaType = NameTreeTableMode.COMMONS;
		    else {
			//these parents may be synonyms...
			taxaType = NameTreeTableMode.DIFFERENCE;
		    }
		}
	    }
    
	    literalSets.get(NameTreeTableMode.ASSIGNMENTS.ordinal()).add(literal);
	    literalSets.get(taxaType.ordinal()).add(literal);
	    if(taxaType == NameTreeTableMode.DIFFERENCE &&
	       //exclusionMode == SYNONYMOUS_TAXA &&
	       parents.size() > 1) {
		final Set<String> synonymLiterals = literalSets.get(NameTreeTableMode.SYNONYMS.ordinal());
		int previousSize = parents.size();
		List<String> parentalRank = null;
		List<Map<TreeModel, NameTreeNode>> synonymSets = new ArrayList<Map<TreeModel, NameTreeNode>>();
		Set<TreeNode> unifiedNodeSet = new HashSet<TreeNode>();
		Map<TreeModel, NameTreeNode> synonymSet = null;
		Map<String, List<NameTreeNode>> rankTest = new HashMap<String, List<NameTreeNode>>();
		do {
		    previousSize = parents.size();
		    parentalRank = new ArrayList<String>(previousSize);
		    synonymSet = new HashMap<TreeModel, NameTreeNode>();
		    Set<String> rankSet =  rankTest.keySet();
		    for (String rank : rankSet) {
			List<NameTreeNode> nodesAtTheRank = rankTest.get(rank);
			if(nodesAtTheRank != null)
			    nodesAtTheRank.clear();
		    }
		    rankTest.clear();
	    
		    //we need rank check code here
		    Collection<NameTreeNode> parentNodes = parents.values();
		    for (NameTreeNode synonym : parentNodes) {
			List<NameTreeNode> nodesAtTheRank = 
			    rankTest.get(Rank.getRank(synonym.getNameUsage()));
			if(nodesAtTheRank == null) {
			    nodesAtTheRank = new ArrayList<NameTreeNode>();
			    rankTest.put(Rank.getRank(synonym.getNameUsage()), nodesAtTheRank);
			}
			nodesAtTheRank.add(synonym);
		    }
		    rankSet = rankTest.keySet();
		    for (String rank : rankSet) {
			List<NameTreeNode> nodesAtTheRank = rankTest.get(rank);
			if(nodesAtTheRank.size() < 1) {
			    nodesAtTheRank.clear();
			    parentalRank.add(rank);
			}
		    }
		    for(String rank : parentalRank) {
			rankTest.remove(rank);
		    }
		    parentalRank.clear();
	    
		    List<TreeModel> parentTreeModels = new ArrayList<TreeModel>(parents.keySet());
		    NameTreeNode synonym = parents.get(parentTreeModels.get(0));
		    for(int k = 1; k < parentTreeModels.size(); k++) {
			TreeModel view = parentTreeModels.get(k);
			NameTreeNode node = parents.get(view);
			if(rankTest.get(Rank.getRank(node.getNameUsage())) == null)
			    continue;
			if(node.isCompatible(synonym)) {
			    unifiedNodeSet.add(getUnitedTree().getNodeFor(node));
			    synonymSet.put(view, node);
			    parents.remove(view);
			}
		
		    }
		    if(!synonymSet.isEmpty()) {
			TreeModel view = parentTreeModels.get(0);
			synonymSet.put(view, synonym);
			parents.remove(view);
			synonymSets.add(synonymSet);
		    }
		} while(parents.size() > 1
			&& parents.size() != previousSize);
		if(!synonymSets.isEmpty()) {
		    boolean embeddable = true;
		    List<TreeNode> unifiedNodeList = new ArrayList<TreeNode>(unifiedNodeSet);
		    for (int i = 0; i < unifiedNodeList.size() && embeddable; i++) {
			for (int j = 0; j < unifiedNodeList.size() && embeddable; j++) {
			    if (j == i) continue;
			    embeddable =
				((DefaultMutableTreeNode)unifiedNodeList.get(i)).isNodeAncestor(unifiedNodeList.get(j))
				|| ((DefaultMutableTreeNode)unifiedNodeList.get(j)).isNodeAncestor(unifiedNodeList.get(i));
			    if (!embeddable)
				break;
			}
		    }
		    if (!embeddable) {
			synonymLiterals.add(literal);
			getSynonyms().put(literal, synonymSets);
		    }
		}
	    }
	}

	final List<List<String>> unifiedNodeLiteralList = new ArrayList<List<String>>(literalSets.size());
	for (Collection<String> literalSet : literalSets) {
	    CollationKey[] sortedLiteralKeys = new CollationKey[literalSet.size()];
	    final Iterator<String> literalIterator = literalSet.iterator();
	    for(int j = 0; j < sortedLiteralKeys.length; j++) {
		sortedLiteralKeys[j] =
		    collator.getCollationKey(literalIterator.next());
	    }
	    Arrays.sort(sortedLiteralKeys);
	    final List<String> literalList = new ArrayList<String>(sortedLiteralKeys.length);
	    unifiedNodeLiteralList.add(literalList);
	    for(int j = 0; j < sortedLiteralKeys.length; j++) {
		final String literal = sortedLiteralKeys[j].getSourceString();
		literalList.add(literal);
	    }
	}

	return unifiedNodeLiteralList;
    }

    protected Map<String, Set<UnitedNameTreeNode>> getRankedUnitedNodes(UnitedNameTreeModel tree)
    {
	return getRankedUnitedNodes(tree, -1);
    }


    protected Map<String, Set<UnitedNameTreeNode>> getRankedUnitedNodes(UnitedNameTreeModel tree,
					     int exclusionMode)
    {
	Map<String, Set<UnitedNameTreeNode>> rankedNameTable = 
	    new HashMap<String, Set<UnitedNameTreeNode>>();

	UnitedNameTreeNode root = 
	    (UnitedNameTreeNode)tree.getRoot();

	Enumeration<?> e = root.depthFirstEnumeration();

	while(e.hasMoreElements()) {
	    UnitedNameTreeNode node = 
		(UnitedNameTreeNode)e.nextElement();
	    if(node == root)
		continue;

	    String name = node.getLiteral();
	    String rank = name;
	    int rankIndex = name.indexOf(' ');
	    if(rankIndex != -1)
		rank = name.substring(0, rankIndex);

	    Set<UnitedNameTreeNode> rankedName =
		rankedNameTable.get(rank);
	    if(rankedName == null) {
		rankedName = new HashSet<UnitedNameTreeNode>();
		rankedNameTable.put(rank, rankedName);
	    }
	    rankedName.add(node);
	}

	return rankedNameTable;
    }


    protected List<NamedTreeNodeList<TreeNode>> getAssignmentsOf(List<TreeModel> trees, String rank)
    {
	return getAssignmentsOf(trees, rank, NameTreeTableMode.ASSIGNMENTS);
    }

    protected List<NamedTreeNodeList<TreeNode>> getAssignmentsOf(List<TreeModel> trees,
				      String rank,
				      NameTreeTableMode exclusionMode)
    {
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable = 
	    new HashMap<String, Map<String, Map<TreeModel, TreeNode>>>();

	for(TreeModel treeModel : trees) {
	    NameTreeModel tree  = null;
	    if(treeModel instanceof NameTreeModel)
		tree = (NameTreeModel)treeModel;

	    Map<String, NameTreeNode> namesAtRank = tree.getNameSetAtRank(rank);

	    if(namesAtRank == null)
		continue;

	    Set<String> keys = namesAtRank.keySet();
	    for (String key :  keys) { 
		Map<String, Map<TreeModel, TreeNode>> ranks = rankedNameTable.get(key);
		if(ranks == null) {
		    ranks = new HashMap<String, Map<TreeModel, TreeNode>>();
		    rankedNameTable.put(key, ranks);
		}
		TreeNode node =
		    (TreeNode)namesAtRank.get(key);
		TreeNode[] path =
		    tree.getPathToRoot(node);
		for(int i = 0; i < path.length - 1; i++) {
		    String rankName =
			Rank.getRank((NameUsage)((NameTreeNode)path[i]).getUserObject());
		    Map<TreeModel, TreeNode> nodes = ranks.get(rankName);
		    if(nodes == null) {
			nodes = new HashMap<TreeModel, TreeNode>();
			ranks.put(rankName, nodes);
		    }
		    nodes.put(tree, path[i]);
		}
	    }
	} 

	remove(trees, rankedNameTable, EnumSet.of(exclusionMode));

	List<NamedTreeNodeList<TreeNode>> rankedNames =
	    new ArrayList<NamedTreeNodeList<TreeNode>>(rankedNameTable.size());
	String[] names = new String[rankedNameTable.size()];
	CollationKey[] nameKeys = new CollationKey[rankedNameTable.size()];
	Set<String> keys = rankedNameTable.keySet();
	int columns = trees.size() + 1;
	int i = 0;
	for(String key : keys) {
	    nameKeys[i++] = collator.getCollationKey(key);
	}
	Arrays.sort(nameKeys);
	for(i = 0; i < nameKeys.length; i++) {
	    StringBuffer buffer = new StringBuffer();
	    if(rank != null) {
		String r = Rank.getAbbreviation(rank);
		if(r == null)
		    r = rank;
		buffer.append(r).append(' ');
	    }
	    buffer.append(nameKeys[i].getSourceString());
	    String rowName = buffer.toString();
	    
	    Map<String, Map<TreeModel, TreeNode>> ranked = 
		rankedNameTable.get(nameKeys[i].getSourceString());
	    ListIterator<Rank> ranks = Rank.getSortedRanks().listIterator();
	    while(ranks.hasPrevious()) {
		String rankName = ranks.previous().getName();
		Map<TreeModel, TreeNode> nodes = ranked.get(rankName);

		if(nodes == null)
		    continue;

		NamedTreeNodeList<TreeNode> row = new NamedTreeNodeList<TreeNode>(rowName, trees.size());
		rankedNames.add(row);
		Iterator<TreeModel> iterator = trees.iterator();
		while(iterator.hasNext()) {
		    row.add(nodes.get(iterator.next()));
		}
	    }
	}

	clearMaps(rankedNameTable);

	return rankedNames;
    }

    protected List<NamedTreeNodeList<TreeNode>> getAssignmentsTo(List<TreeModel> trees, String rank)
    {
	return getAssignmentsTo(trees, rank, NameTreeTableMode.ASSIGNMENTS);
    }

    protected List<NamedTreeNodeList<TreeNode>> getAssignmentsTo(List<TreeModel> trees,
				      String rank,
				      NameTreeTableMode exclusionMode)
    {
	Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable =
	    new HashMap<String, Map<String, Map<TreeModel, TreeNode>>>();

	for (TreeModel treeModel : trees) {
	    Map<String, NameTreeNode> namesAtRank = null;
	    NameTreeModel tree = null;
	    if(treeModel instanceof NameTreeModel) {
		tree = (NameTreeModel)treeModel;
		namesAtRank = tree.getNameSetAtRank(rank);
	    }
	    if(namesAtRank == null)
		continue;
	    
	    Collection<NameTreeNode> assignedNodes = namesAtRank.values();
	    
	    for (NameTreeNode node : assignedNodes) { 
		Enumeration<?> subtree = node.breadthFirstEnumeration();
		subtree.nextElement(); //discurd the root of the subtree
		while(subtree.hasMoreElements()) {
		    NameUsage<?> n =(NameUsage<?>)((NameTreeNode)subtree.nextElement()).getUserObject();
		    String key = Rank.getRank(n);
		    Map<String, Map<TreeModel, TreeNode>> ranks = rankedNameTable.get(key);
		    if(ranks == null) {
			ranks = new HashMap<String, Map<TreeModel, TreeNode>>();
			rankedNameTable.put(key, ranks);
		    }
		    key = n.getLiteral();
		    Map<TreeModel, TreeNode> rankedNames = ranks.get(key);
		    if(rankedNames == null) {
			rankedNames = new HashMap<TreeModel, TreeNode>();
			ranks.put(key, rankedNames);
		    }
		    rankedNames.put(tree, node);
		}
	    }
	}

	remove(trees, rankedNameTable, EnumSet.of(exclusionMode));

	List<NamedTreeNodeList<TreeNode>> rankedNames =
	    new ArrayList<NamedTreeNodeList<TreeNode>>();
	int columns = trees.size() + 1;
	ListIterator<Rank> rankIterator = Rank.getSortedRanks().listIterator();
	while(rankIterator.hasPrevious()) {
	    rank = rankIterator.previous().getName();
	    Map<String, Map<TreeModel, TreeNode>> namesAtTheRank =
		rankedNameTable.get(rank);

	    if(namesAtTheRank == null)
		continue;

	    CollationKey[] nameKeys = new CollationKey[namesAtTheRank.size()];
	    Set<String> keys = namesAtTheRank.keySet();
	    int i = 0;
	    for (String key : keys) {
		nameKeys[i++] = collator.getCollationKey(key);
	    }
	    Arrays.sort(nameKeys);
	    for(i = 0; i < nameKeys.length; i++) {
		Map<TreeModel, TreeNode> nodes = namesAtTheRank.get(nameKeys[i].getSourceString());
		if(nodes == null)
		    continue;

		StringBuffer buffer = new StringBuffer();
		if(rank != null) {
		    String r = Rank.getAbbreviation(rank);
		    if(r == null)
			r = rank;
		    buffer.append(r).append(' ');
		}
		buffer.append(nameKeys[i].getSourceString());

		NamedTreeNodeList<TreeNode> row = new NamedTreeNodeList<TreeNode>(buffer.toString(), trees.size());
		rankedNames.add(row);
		Iterator<TreeModel> iterator = trees.iterator();
		while(iterator.hasNext()) {
		    row.add(nodes.get(iterator.next()));
		}
	    }
	}

	clearMaps(rankedNameTable);

	return rankedNames;
    }

    protected String getRankedName(NameTreeNode node)
    {
	StringBuffer buffer = new StringBuffer();
	NameUsage<?> n = (NameUsage<?>)node.getUserObject();
	String rank = Rank.getRank(n);
	if(rank != null) {
	    rank = Rank.getAbbreviation(rank);
	    if(rank == null)
		rank = Rank.getRank(n);
	    buffer.append(rank).append(' ');
	}
	buffer.append(n.getLiteral());
	return buffer.toString();
    }

    protected List<NamedTreeNodeList<TreeNode>> hashToList(List<TreeModel> trees, 
				  Map<String, Map<String, Map<TreeModel, TreeNode>>> rankedNameTable)
    {
	List<NamedTreeNodeList<TreeNode>> rankedNames =
	    new ArrayList<NamedTreeNodeList<TreeNode>>(rankedNameTable.size());
	ListIterator<Rank> ranks = Rank.getSortedRanks().listIterator();

	while(ranks.hasPrevious()){
	    String rank = ranks.previous().getName();
	    Map<String, Map<TreeModel, TreeNode>> namesAtRank = rankedNameTable.get(rank);
	    if(namesAtRank == null)
		continue;
	    CollationKey[] nameKeys = new CollationKey[namesAtRank.size()];
	    int i = 0;
	    Set<String> names = namesAtRank.keySet();
	    for(String name : names) {
		nameKeys[i++] = collator.getCollationKey(name);
	    }
	    Arrays.sort(nameKeys);

	    String r = Rank.getAbbreviation(rank);
	    if(r == null)
		r = rank;
	    for(i = 0; i < nameKeys.length; i++) {

		StringBuffer buffer = new StringBuffer();
		if(r != null)
		    buffer.append(r).append(' ');
		buffer.append(nameKeys[i].getSourceString());
		NamedTreeNodeList<TreeNode> assignedTo =
		    new NamedTreeNodeList<TreeNode>(buffer.toString(), trees.size());
		rankedNames.add(assignedTo);
		Map<TreeModel, TreeNode> nodes = namesAtRank.get(nameKeys[i].getSourceString());
		for(TreeModel tree : trees) {
		    assignedTo.add(nodes.get(tree));
		}
	    }
	}

	return rankedNames;
    }
    
    public void treeNodesChanged(TreeModelEvent e) {;}
    public void treeNodesInserted(TreeModelEvent e) {;}
    public void treeNodesRemoved(TreeModelEvent e) {;}
    public void treeStructureChanged(TreeModelEvent e) {;}

    public int[] getRowsForPaths(TreePath[] path)
    {
	if(path == null)
	    return new int[0];

	List<Integer> rows = new ArrayList<Integer>();
	final Map<String, List<Integer>> rowMap = getRowMap();
	for(int i = 0; i < path.length; i++) {
	    List<Integer> v =
		rowMap.get(((NamedNode<?>)path[i].getLastPathComponent()).getLiteral());

	    if(v == null) {
		continue;
	    }

	    rows.addAll(v);
	}
	if(rows.isEmpty()) {
	    return new int[0];
	}

	int[] rowValues = new int[rows.size()];
	for(int i = 0; i < rowValues.length; i++)
	    rowValues[i] = rows.get(i);
	rows.clear();

	return rowValues;
    }

    public List<NamedNode<?>> getNodesForLiteral(String ascribedName, Rank rank, String authors, String year)
    {
	if(unitedTree == null)
	    return null;
	
	List<NamedNode<?>> namedNodes = 
	    unitedTree.getNodesForLiteral(ascribedName.toLowerCase(), rank);
	List<NamedNode<?>> nodes = new ArrayList<NamedNode<?>>(namedNodes);

	return nodes;
    }


    public NameTreeTableMode getTableMode()
    {
	return tableMode;
    }

    public void treeAdded(UnitedNameTreeModelEvent event)
    {
	int index = -1;
	synchronized (columnIdentifiers) {
	    TreeModel tree = event.getTreeModel();
	    if(columnIdentifiers.contains(tree))
		return;

	    columnIdentifiers.add(event.getTreeModel());
	    index = columnIdentifiers.size() - 1;

	    unifiedNodeLiteralList = updateUnifiedNodeLiteralList();
	    rowMapList = updateRowMapList();
	}

	fireTreeAdded(new NameTreeTableModelEvent(this,
						  index, 
						  getHeaderValue(index)));
    }

    public void treeRemoved(UnitedNameTreeModelEvent event)
    {
    }

    public void treeMoved(UnitedNameTreeModelEvent event)
    {
    }

    protected UniqueEventListenerList getListeners()
    {
	if(listeners == null)
	    listeners = new UniqueEventListenerList();

	return listeners;
    }

    public void addNameTreeTableModelListener(NameTreeTableModelListener listener)
    {
	UniqueEventListenerList listenrs = getListeners();

	synchronized(listeners) {
	    listeners.add(NameTreeTableModelListener.class, listener);
	}
    }

    public void removeNameTreeTableModelListener(NameTreeTableModelListener listener)
    {
	if(listeners == null)
	    return;
	synchronized(listeners) {
	    listeners.remove(NameTreeTableModelListener.class, listener);
	}
    }

    protected void fireTreeAdded(NameTreeTableModelEvent event)
    {
	if(listeners == null)
	    return;

	synchronized(listeners) {
	    NameTreeTableModelListener[] targets = 
		listeners.getListeners(NameTreeTableModelListener.class);
	    for(NameTreeTableModelListener target : targets) {
                target.treeAdded(event);
	    }
	}
    }

    protected void fireTreeRemoved(NameTreeTableModelEvent event)

    {
	if(listeners == null)
	    return;

	synchronized(listeners) {
	    NameTreeTableModelListener[] targets = 
		listeners.getListeners(NameTreeTableModelListener.class);
	    for(NameTreeTableModelListener target : targets) {
                target.treeRemoved(event);
	    }
	}
    }

    public void addProgressListener(ProgressListener listener)
    {
	UniqueEventListenerList listenrs = getListeners();

	listeners.add(ProgressListener.class, listener);
    }

    public void removeProgressListener(ProgressListener listener)
    {
	if (listeners != null)
	    listeners.remove(ProgressListener.class, listener);
    }

    public void fireProgressMade(ProgressEvent event)
    {
	ProgressListener[] progressListeners = listeners.getListeners(ProgressListener.class);
	for (ProgressListener listener : progressListeners) {
            listener.progressMade(event);
        }
    }
}
