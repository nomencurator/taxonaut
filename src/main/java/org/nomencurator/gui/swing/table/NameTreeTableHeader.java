/*
 * EditableTableHeader.java:  a JTableHeader providing
 * eidtable headers
 *
 * Copyright (c) 2003, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.beans.PropertyChangeEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import javax.swing.plaf.ComponentUI;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultMutableTreeNode;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import org.nomencurator.gui.swing.HeaderEditableTable;
import org.nomencurator.gui.swing.LookAndFeelManager;
import org.nomencurator.gui.swing.NameTree;
import org.nomencurator.gui.swing.NamedTree;
import org.nomencurator.gui.swing.NameTreeTable;
import org.nomencurator.gui.swing.MultiLinesToolTip;
import org.nomencurator.gui.swing.MultipleToolTip;
import org.nomencurator.gui.swing.MatrixToolTip;
import org.nomencurator.gui.swing.MultipleToolTipManager;

import org.nomencurator.gui.swing.tree.NameTreeModel;

import org.nomencurator.gui.swing.plaf.basic.BasicEditableTableHeaderUI;
import org.nomencurator.gui.swing.plaf.basic.BasicNameTreeTableHeaderUI;

import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeNode;


/**
 * A {@code JTableHedear} provides an editable header
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableHeader<T extends NameUsage<?>>
    extends EditableTableHeader
    implements MouseListener,
	       MouseMotionListener
{
    private static final long serialVersionUID = 7603295128370032378L;

     /* User interface class ID */
    private static final String uiClassID = "NameTreeTableHeaderUI";

    protected static void putUI() {
	if(LookAndFeelManager.isWindows())
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.windows.WindowsNameTreeTableHeaderUI");
	else
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.basic.BasicNameTreeTableHeaderUI");
    }

    static {
	putUI();
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    public void propertyChange(PropertyChangeEvent event) 
    {
	if(LookAndFeelManager.isLookAndFeelEvent(event))
	    putUI();
    }

    /** List of <tt>JTree</tt>s relevant to the table header */
    protected List<JTree> trees;

    /** parameter to determin alignment in the ToolTip */
    protected boolean alignToolTipByRank;

    /** preferred heigh of the table header */
    protected int preferredHeight;

    /**
     * Constructs a {@code EditableTableHeader}
     * with a default {@code TableColumnModel}
     */
    public NameTreeTableHeader()
    {
	this(null, null);
    }

    /**
     * Constructs a {@code EditableTableHeader}
     * with a default {@code TableColumnModel}
     */
    public NameTreeTableHeader(TableColumnModel model, 
			       NameTreeTable<T> tree)
    {
	this(model, tree, true);
    }

    /**
     * Constructs a {@code EditableTableHeader}
     * with a default {@code TableColumnModel}
     */
    //@SuppressWarnings("unchecked")
    public NameTreeTableHeader(TableColumnModel model, 
			       NameTreeTable<T> treeTable,
			       boolean alignToolTipByRank)
    {
	super(model);
	setTable(treeTable);
	trees = new ArrayList<JTree>();
	trees.add(treeTable.getAlignerTree());
	trees.addAll(treeTable.getAlignableTrees());
	setToolTipAlignByRank(alignToolTipByRank);
	/*
	addMouseListener(this);
	addMouseMotionListener(this);
	*/
	preferredHeight = -1;
    }

    /**
     * Sets alighment mode in the <tt>ToolTip</tt> to <tt>alignToolTipByRank</tt>,
     * true to align by rank.
     * 
     * @param alignToolTipByRank <tt>true</tt> to align by rank.
     */
    public void setToolTipAlignByRank(boolean alignToolTipByRank)
    {
	this.alignToolTipByRank = alignToolTipByRank;
    }

    /**
     * Returns true if contens of <tt>ToolTip</tt> is aligned by rank.
     *
     * @return true if contens of <tt>ToolTip</tt> is aligned by rank
     */
    public boolean isToolTipAlignByRank()
    {
	return alignToolTipByRank;
    }

    /**
     * Creates a <tt>JToolTip</tt> to dispay.
     *
     * @return JToolTip created
     */
    public JToolTip createToolTip()
    {
	ToolTipManager.sharedInstance().setDismissDelay(600000);
	ToolTipManager.sharedInstance().setReshowDelay(30);
	JToolTip tip = new MatrixToolTip();
	//tip.setOpaque(false);
	return tip;
    }

    protected int getAlignerIndex()
    {
	TableColumnModel model = getColumnModel();
	int columnCount = model.getColumnCount();
	int i = 0;
	for (; i < columnCount; i++) {
	    if (((TreeHeaderRenderer)model.getColumn(i).getHeaderRenderer()).getTree() instanceof Aligner)
		break;
	}

	return i < columnCount ? i : -1;
    }


    /**
     * Returns a <tt>String</tt> to display in the tool tip.
     *
     * @param event <tt>MouseEvent</tt> to evoke the tool tip.
     * @return String to display in the tool tip.
     */
    public String getToolTipText(MouseEvent event)
    {
	TableColumnModel model = getColumnModel();

	int index = model.getColumnIndexAtX(event.getX());

	if(index == -1) {
	    return super.getToolTipText(event);
	}

	JTree c = ((TreeHeaderRenderer)model.getColumn(index).getHeaderRenderer()).getTree();

	if(c == null) {
	    return super.getToolTipText(event);
	}

	ComponentUI ui = getUI();
	if(!(ui instanceof BasicEditableTableHeaderUI))
	    return super.getToolTipText(event);

	MouseEvent reference = 
	    ((BasicEditableTableHeaderUI)ui).convertMouseEvent((Component)event.getSource(), event, c);

	if(reference.getY() <= 0) { //it is the header
	    if(c instanceof NamedTree) {
		return ((NamedTree)c).getTreeName();
	    }
	    else {
		return null;
	    }
	}

	int alignerIndex = getAlignerIndex();

	if (index == alignerIndex) {
	    // if the event happend on the AlignerTree
	    return c.getToolTipText(reference);
	}

	StringBuffer buff = new StringBuffer();

	// get TreeModel of the AlignerTree
	UnitedNameTreeModel unitedTreeModel =
	    (UnitedNameTreeModel)
	    ((TreeHeaderRenderer)model.getColumn(alignerIndex).getHeaderRenderer()).getTree().getModel();

	TreePath path = c.getClosestPathForLocation(reference.getX(), reference.getY()); 
	if(path == null) {
	    return null;
	}

	NamedNode<?> node = (NamedNode<?>)path.getLastPathComponent();
	String literal = node.getLiteral();
	Collection<UnitedNameTreeNode> unifiedNodeSet = unitedTreeModel.getNodesFor(literal);

	if(unifiedNodeSet == null) {
	    return null;
	}

	List<UnitedNameTreeNode> unifiedNodeList = new ArrayList<UnitedNameTreeNode>(unifiedNodeSet);

	UnitedNameTreeNode[] unifiedNodes = 
	    unifiedNodeList.toArray(new UnitedNameTreeNode[unifiedNodeList.size()]);

	int columns = model.getColumnCount();

	if(!alignToolTipByRank) {
	    for(int i = 0; i < columns; i++) {
		if (i == alignerIndex) continue;
		c = ((TreeHeaderRenderer)model.getColumn(i).getHeaderRenderer()).getTree();
		@SuppressWarnings("unchecked")
		    NameTree nameTree = (c instanceof NameTree)? null: (NameTree)c;
		TreeModel treeModel = c.getModel();
		List<NamedNode<?>> nodes = new ArrayList<>();
		node = null;
		for(int j = 0; node == null && j < unifiedNodes.length; j++) {
		    nodes.add((NamedNode<?>)unitedTreeModel.getNodeFor(unifiedNodes[j], treeModel));
		}
		switch(nodes.size()) {
		case 0:
		    buff.append("(no match)");
		    break;
		case 1:
		    if (nameTree != null) {
			buff.append(nameTree.getToolTipText(nodes.get(0)));
		    }
		    break;
		default:
		    int nodeSize = nodes.size();
		    String[][] toolTipTexts = new String[nodeSize][];
		    int maxLength[] = new int[nodeSize];
		    int maxLines = 0;
		    for (int j = 0; j < nodeSize; j++) {
			toolTipTexts[j] = nameTree == null ? null :
			    nameTree.getToolTipText(nodes.get(j)).split("\\n");
			maxLength[j] = 0;
			int l = toolTipTexts[j].length;
			if (l > maxLines)
			    maxLines = l;
			for (int k = 0; k < l; k++) {
			    int length = toolTipTexts[j][k].length();
			    if (length > maxLength[j]) {
				maxLength[j] = length;
			    }
			}
		    }
		    String ws[] = new String[nodeSize];
		    for (int j = 0; j < nodeSize; j++) {
			StringBuffer buf = new StringBuffer();
			for (int k = 0; k < maxLength[j]; k++) {
			    buf.append(' ');
			}
			ws[j] = buf.toString();
		    }
		    for (int k = 0; k < maxLines; k++) {
			StringBuffer buf = new StringBuffer();
			for (int j = 0; j < nodeSize; j++) {
			    if (k < toolTipTexts[j].length) {
				buf.append(toolTipTexts[j][k]);
				buf.append(ws[j].substring(toolTipTexts[j][k].length()));
			    }
			    else {
				buf.append(ws[j]);
			    }
			}
			buff.append(buf.toString());
		    }
		}
		if(i < columns - 1) {
		    buff.append("\n\n");
		}
	    }
	    return buff.toString();
	}

	Set<String> rankSet = new HashSet<String>();
	Map<TableColumn, Map<String, Object[]>> rankedNodes =
	    new HashMap<TableColumn, Map<String, Object[]>>(columns);
	Map<TableColumn, Integer> nodeCounts = null;
	Map<TableColumn, String[]> whiteSpaces = null;
	Map<String, Object[]> emptyRankedNode = Collections.emptyMap();
	Map<String, Object[]> rankedNode = null;
	for(int i = 0; i < columns; i++) {
	    if (i == alignerIndex) continue;
	    TableColumn column = model.getColumn(i);
	    c = ((TreeHeaderRenderer)column.getHeaderRenderer()).getTree();
	    TreeModel treeModel = c.getModel();
	    List<NamedNode<?>> namedNodes = new ArrayList<NamedNode<?>>();
	    for(int j = 0; j < unifiedNodes.length; j++) {
		node = (NamedNode<?>)unitedTreeModel.getNodeFor(unifiedNodes[j], treeModel);
		if (node != null) {
		    namedNodes.add(node);
		}
	    }
	    int mappedNodeCount = namedNodes.size();
	    if(mappedNodeCount == 0) {
		rankedNode = emptyRankedNode;
	    }
	    else {
		if (mappedNodeCount > 1) {
		    if (nodeCounts == null)
			nodeCounts = new HashMap<TableColumn, Integer>();
		    nodeCounts.put(column, mappedNodeCount);
		}
		rankedNode = new HashMap<String, Object[]>();
		int j = 0;
		for (NamedNode<?> namedNode : namedNodes) {
		    Object[] nodes = ((DefaultMutableTreeNode)namedNode).getPath();
		    for(int k = 0; k < nodes.length; k++) {
			String name = ((NamedNode<?>)nodes[k]).getRankedName();
			int rankIndex = name.indexOf(' ');
			Object[] array = null;
			if (rankIndex == -1) {
			    array = rankedNode.get(name);
			    if (array == null) {
				array  = new Object[mappedNodeCount];
				rankedNode.put(name, array);
			    }
			}
			else {
			    String rankName = name.substring(0, rankIndex);
			    array = rankedNode.get(rankName);
			    if (array == null) {
				array  = new Object[mappedNodeCount];
				rankedNode.put(rankName, array);
			    }
			    rankSet.add(rankName);
			}
			array[j] = nodes[k];
		    }
		    j++;
		}
	    }
	    rankedNodes.put(column, rankedNode);
	}

	if (nodeCounts != null) {
	    whiteSpaces = new HashMap<TableColumn, String[]>(nodeCounts.size());
	    Set<TableColumn> columnSet = nodeCounts.keySet();
	    for (TableColumn column : columnSet) {
		int nodeCount = nodeCounts.get(column) - 1;
		int[] maxLength = new int[nodeCount];
		rankedNode = rankedNodes.get(column);
		if (rankedNode == null) continue;
		Collection<Object[]> nodeArrays = rankedNode.values();
		for (Object[] nodes : nodeArrays) {
		    for (int j = 0; j < nodeCount; j++) {
			Object nodeObject = nodes[j];
			if (nodeObject == null || !(nodeObject instanceof NamedNode))
			    continue;
			node = (NamedNode<?>)nodeObject;
			int length = ((DefaultMutableTreeNode)node).getLevel() + node.getRankedName().length();
			if (length > maxLength[j])
			    maxLength[j] = length;
		    }
		}
		String[] wss = new String[nodeCount];
		whiteSpaces.put(column, wss);
		for (int j = 0; j < nodeCount; j++) {
		    StringBuffer wsBuffer = new StringBuffer();
		    for (int k = 0; k < maxLength[j]; k++) {
			wsBuffer.append(' ');
		    }
		    wss[j] = wsBuffer.toString();
		}
	    }
	}

	List<String> ranks = new ArrayList<String>(rankSet.size());
	String[] sortedRanks = Rank.getRankNames();

	int rankCount = sortedRanks.length;
	for(int i = 0; !rankSet.isEmpty() && i < rankCount; i++) {
	    String r = 
		Rank.getAbbreviation(sortedRanks[i]);
	    if(r != null && rankSet.contains(r)) {
		ranks.add(r);
		rankSet.remove(r);
	    }
	}

	rankCount = ranks.size();
	for(int i = 0; i < columns; i++) {
	    if (i == alignerIndex && i == 0) continue;
	    TableColumn column = model.getColumn(i);
	    String[] ws = null;
	    if (whiteSpaces != null)
		ws = whiteSpaces.get(column);
	    if (i == alignerIndex) {
		buff.append("[Aligner]");
	    }
	    else if(rankedNodes.get(column) == emptyRankedNode) {
		buff.append("(no match)");
	    }
	    else {
		for(int j = 0; j < rankCount; j++) {
		    Object[] objects = rankedNodes.get(column).get(ranks.get(j));
		    if(objects == null || objects.length == 0) {
			buff.append(" ");
		    }
		    else {
			if (ws == null) {
			    node = (NamedNode<?>)objects[0];
			    if (node == null)
				buff.append(" ");
			    else {
				int level = ((DefaultMutableTreeNode)node).getLevel();
				for(int l = 0; l < level; l++) {
				    buff.append(' ');
				}
				buff.append(node.getRankedName());
			    }
			}
			else {
			    for (int k = 0; k < ws.length; k++) {
				node = (NamedNode<?>)objects[k];
				if (node == null) {
				    buff.append(ws[k]);
				}
				else {
				    StringBuffer lineBuffer = new StringBuffer();
				    int level = ((DefaultMutableTreeNode)node).getLevel();
				    for(int l = 0; l < level; l++) {
					lineBuffer.append(' ');
				    }
				    lineBuffer.append(node.getRankedName());
				    lineBuffer.append(ws[k].substring(lineBuffer.length()));
				    buff.append(lineBuffer.toString());
				}
			    }
			    node = (NamedNode<?>)objects[ws.length];
			    if (node == null) {
				buff.append(" ");
			    }
			    else {
				int level = ((DefaultMutableTreeNode)node).getLevel();
				for(int l = 0; l < level; l++) {
				    buff.append(' ');
				}
				buff.append(node.getRankedName());
			    }
			}
		    }
		    if(j < rankCount - 1) {
			buff.append('\n');
		    }
		}
	    }
	    if(i < columns - 1) {
		buff.append("\n\n");
	    }
	}

	return buff.toString();
    }

    @Override
    public void setToolTipText(String text)
    {
	super.setToolTipText(text);
    }

    public void mouseEntered(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseExited(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mousePressed(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseDragged(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseMoved(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseClicked(MouseEvent event)
    {
    }

    public void mouseReleased(MouseEvent event)
    {
    }

    public void dispachMouseEvent(MouseEvent event)
    {
	BasicEditableTableHeaderUI converter = 
	    (BasicEditableTableHeaderUI)getUI();
	Component source = (Component)event.getSource();

	for (JTree jtree : trees) {
	    jtree.dispatchEvent
		(converter.convertMouseEvent(source, event, jtree));
	}
    }

    public int getPreferredHeight()
    {
	return preferredHeight;
    }

    public void setPreferredHeight(int height)
    {
	preferredHeight = height;
    }

    public Dimension getPreferredSize()
    {
        Dimension size = super.getPreferredSize();
	if(preferredHeight > 0)
	    size.height = preferredHeight;

	return size;
    }
}
