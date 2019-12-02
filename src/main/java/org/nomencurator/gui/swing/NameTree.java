/*
 * NameTree.java: a JTree for NameUsageNode
 *
 * Copyright (c) 2002, 2003, 2004, 2005, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import com.sun.java.swing.plaf.motif.MotifTreeUI;
// import com.sun.java.swing.plaf.windows.WindowsTreeUI;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.swing.DebugGraphics;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.UIManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.TreeUI;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.plaf.metal.MetalTreeUI;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.io.Exchangable;
import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.NameUsageExchanger;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Person;
import org.nomencurator.model.Rank;

import org.nomencurator.gui.swing.plaf.AlignableTreeUI;
import org.nomencurator.gui.swing.plaf.basic.BasicAlignableTreeUI;
import org.nomencurator.gui.swing.plaf.metal.MetalAlignableTreeUI;
import org.nomencurator.gui.swing.plaf.motif.MotifAlignableTreeUI;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.NameTreeTableModel;
import org.nomencurator.gui.swing.table.NameTreeEditor;
import org.nomencurator.gui.swing.table.NameTreeRenderer;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;
import org.nomencurator.gui.swing.table.RenderingTableCellEditor;
import org.nomencurator.gui.swing.table.TableColumnSynchronizer;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.NameTreeCellEditor;
import org.nomencurator.gui.swing.tree.NameTreeCellRenderer;

/**
 * A {@code JTree} for a {@code NameUsageNode}
 *
 * @version 	03 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class NameTree
    extends GenericNameTree<NameUsage<?>, NameUsageNode<?>>
{
    private static final long serialVersionUID = 5822064655034983379L;

    /**
     * Line style properties of JTree.
     */
    public enum LineStyle {
	ANGLED("Angled"),
	HORIZONTAL("Horizontal"),
	NONE("None");

	private String value;

	private static Map<String, LineStyle> lineStyles;
	
	static {
	    LineStyle[] styles = LineStyle.values();
	    lineStyles = new HashMap<String, LineStyle>(styles.length);
	    for (LineStyle style : styles) {
		lineStyles.put(style.getValue(), style);
	    }
	}

	private LineStyle(String value)
	{
	    this.value = value;
	}

	public String getValue()
	{
	    return value;
	}

	public String toString()
	{
	    return value;
	}

	public static LineStyle getLineStyle(String value)
	{
	    return lineStyles.get(value);
	}
    }

    public NameTree()
    {
	super();
    }

    public NameTree(NameUsage<?> node)
    {
	super(node);
    }

    public NameTree(NameUsageNode<?> node)
    {
	super(node);
    }
    
    public NameTree(NameTreeNode node)
    {
	super(node);
    }

    public NameTree(NameTreeModel model)
    {
	super(model);
    }

    public NameTree(NameTreeModel model, Aligner aligner)
    {
	super(model, aligner);
    }

    public NameTree(NameTreeModel model, Aligner aligner, ResizeDirection direction)
    {
	super(model, aligner, direction);
    }

    public void setLineStyle(LineStyle style)
    {
	putClientProperty("JTree.lineStyle", style.getValue());
    }

}

/**
 * A {@code JTree} for a {@code NameUsageNode}
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
class GenericNameTree<T extends NameUsage<?>, N extends NameUsageNode<?>>
    extends JTree
    implements Alignable,
	       NamedTree,
	       //MouseListener,
	       PropertyChangeListener
{
    private static final long serialVersionUID = 7162858196935385720L;

    /* User interface class ID */
    private static final String uiClassID = "AlignableTreeUI";

    /* Set user interface class ID according to current default look and feel*/
    protected static void putUI()
    {
	if (LookAndFeelManager.isMotif())
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.motif.MotifAlignableTreeUI");
	else
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.metal.MetalAlignableTreeUI");
    }

    static {
	putUI();
    }

    public void propertyChange(PropertyChangeEvent event) 
    {
	if(LookAndFeelManager.isLookAndFeelEvent(event))
	    putUI();
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    protected GenericNameTree<?, ?> lookAndFeelListener = null;


    /**
     * Default {@code TreeCellRenderer} for
     * {@code NameTree}
     */
    public static final TreeCellRenderer renderer =
	new NameTreeCellRenderer();

    /*
    protected static TreeCellEditor editor =
	new NameTreeCellEditor();
    */

    protected RowMapper defaultMapper;

    protected AlignerTree alignerTree;

    /**
     * Enumeration of direction properties
     * to calcurate preferred size as a union
     * of the tree and aligner tree areas.
     */
    public enum ResizeDirection {
	HORIZONTAL,
	VERTICAL,
	BOTH
    }

    protected ResizeDirection resizeDirection;

    public JTree getTree()
    {
	return this;
    }

    public void setTreeName(String name)
    {
	setViewName(name);
    }
    
    public String getTreeName()
    {
	return getViewName();
    }

    protected GenericNameTree()
    {
	this(new NameTreeModel((NameTreeNode)null));
    }

    protected GenericNameTree(NameUsage<?> node)
    {
	this(new NameTreeNode(node));
    }

    protected GenericNameTree(NameUsageNode<?> node)
    {
	this((NameUsage<?>)node);
    }

    protected GenericNameTree(NameTreeNode node)
    {
	this(new NameTreeModel(node));
    }

    protected GenericNameTree(NameTreeModel model)
    {
	this(model, null);
    }

    protected GenericNameTree(NameTreeModel model, Aligner aligner)
    {
	this(model, aligner, ResizeDirection.VERTICAL);
    }

    protected GenericNameTree(NameTreeModel model, Aligner aligner, ResizeDirection direction)
    {
	super(model);
	initializeLocalVars();
	/*
	setDebugGraphicsOptions(DebugGraphics.BUFFERED_OPTION |
				DebugGraphics.FLASH_OPTION |
				DebugGraphics.LOG_OPTION );
	*/
	if(lookAndFeelListener == null) {
	    UIManager.addPropertyChangeListener(this);
	    lookAndFeelListener = this;
	}
	//addMouseListener(this);

	if(aligner instanceof AlignerTree) {
	    setAlignerTree((AlignerTree) aligner);
	    aligner = ((AlignerTree)aligner).getAligner();
	}
	setAligner(aligner);
	setResizeDirection(direction);
    }

    protected void initializeLocalVars()
    {
	setRootVisible(true);

	String osName = System.getProperty("os.name");
	if(osName == null ||
	   osName.indexOf("Mac") == -1) {
	    //setShowsRootHandles(false);
	    setShowsRootHandles(true);
	}
	
	setCellRenderer(renderer);
	//setCellEditor(editor);

	/*
	int rows = getRowCount() * 2; //reasonable deduce?
	if(rows < getVisibleRowCount())
	    setVisibleRowCount(rows);
	*/

	setToolTipText("");
	if(getModel() != null &&
	   getModel() instanceof NameTreeModel)
	    setRootVisible(((NameTreeModel)getModel()).isRootVisible());
    }

    /**
     * Returns view name of the name tree.
     * If it is set by {@code setViewName(String)},
     * the set name is used.  Instead, it returns
     * view name held by the root {@code NameUsage}
     *
     * @return String representing view name of this tree
     *
     * @see #setViewName(String)
     * @see org.nomencurator.model.NameUsage#getViewName()
     */
    public String getViewName()
    {
	return ((NameTreeModel)getModel()).getViewName();
    }

    /**
     * Sets {@code name} as view name of this tree
     *
     * @see #getViewName()
     */
    public void setViewName(String name)
    {
	((NameTreeModel)getModel()).setViewName(name);
    }

    public void setCollapsedIcon(Icon icon)
    {
	((BasicTreeUI)getUI()).setCollapsedIcon(icon);
    }

    public void setExpandedIcon(Icon icon)
    {
	((BasicTreeUI)getUI()).setExpandedIcon(icon);
    }

    public String convertValueToText(Object value, boolean selected,
                                     boolean expanded, boolean leaf, int row,
                                     boolean hasFocus)
    {
	if(value instanceof NamedNode) {
	    return ((NamedNode)value).getRankedName();
	}
	else if (value instanceof NameUsage)  {
	    return ((NameUsage)value).getRankedName();
	}
	else if (value instanceof DefaultMutableTreeNode) {
	    Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
	    if (userObject instanceof NameUsage)  {
		return ((NameUsage)userObject).getRankedName();
	    }
	}

	return super.convertValueToText(value, selected,
					expanded, leaf, row,
					hasFocus);
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
						   boolean isSelected, boolean hasFocus, 
						   int row, int column)
    {
	if(value instanceof NameTreeModel &&
	   value != getModel())
	    setModel((NameTreeModel)value);

	return this;
    }

    protected void processMouseEventGenerics(MouseEvent event)
	throws IOException
    {
	if(event.getID() == MouseEvent.MOUSE_PRESSED) {
	    TreePath path =
		getClosestPathForLocation(event.getX(), event.getY());
	    if (path != null
		&& getPathBounds(path).x >= event.getX()
	       ) {
		NameTreeNode node =
		    (NameTreeNode)path.getLastPathComponent();
		@SuppressWarnings("unchecked")
		    NameUsage<T> usage = (NameUsage<T>)node.getNameUsage();
		if(usage.getLowerNameUsages() == null) {
		    ObjectExchanger<?> source = null;
		    if (usage instanceof Exchangable)
			source = ((Exchangable<?>)usage).getExchanger();
		    if (source != null &&
			source instanceof NameUsageExchanger) {
			setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			List<NameUsage<T>> lowerNameUsages = null;
			if (source instanceof NameUsageExchanger) {
			    @SuppressWarnings("unchecked")
				NameUsageExchanger<T> exchanger =
				(NameUsageExchanger<T>)source;
			    lowerNameUsages = exchanger.getLowerNameUsages(usage, null, 1);
			}
			NameTreeNode lowerNode = null;
			if(lowerNameUsages != null) {
			    usage.setLowerNameUsages(lowerNameUsages);
			    for(NameUsage<T> lowerNameUsage: lowerNameUsages) {
				node.add(new NameTreeNode(lowerNameUsage));
			    }
			}
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			//expandPath(path);
		    }
		}
	    }
	}
	super.processMouseEvent(event);
    }

    public ResizeDirection getResizeDirection()
    {
	return resizeDirection;
    }

    public ResizeDirection setResizeDirection(ResizeDirection direction)
    {
	ResizeDirection previous = resizeDirection;
	resizeDirection = direction;
	return previous;
    }

    public void setAlignerTree(AlignerTree alignerTree)
    {
	if(this.alignerTree == alignerTree)
	    return;

	this.alignerTree = alignerTree;
	/*
	if(this.alignerTree != null)
	    setAligner(alignerTree.getAligner());
	else
	    setAligner((Aligner)null);
	*/
	setAligner(alignerTree);
    }

    public synchronized void setAligner(Aligner aligner)
    {
	TreeUI ui = getUI();
	synchronized(ui) {
	    if(ui instanceof Alignable && aligner != getAligner()) {
		((Alignable)ui).setAligner(aligner);
	    }
	}
    }

    public synchronized Aligner getAligner()
    {
	/*
	TreeUI ui = getUI();
	if(ui != null) {
	    synchronized(ui) {
		if(ui instanceof Alignable)
		    return ((Alignable)ui).getAligner();
	    }
	}
	return null;
	*/
	return alignerTree;
    }

    public void setUI(TreeUI ui) {
	if(this.ui != ui) {
	    Aligner aligner = getAligner();
	    super.setUI(ui);
	    if(ui instanceof Alignable) {
		((Alignable)ui).setAligner(aligner);
	    }
	}
    }

    public int getHeight()
    {
	int height = super.getHeight();
	if(alignerTree == null)
	    return height;

	int alignerHeight = 
	    alignerTree.getHeight();

	return (height > alignerHeight? height:alignerHeight);
    }

    protected Dimension getUnion(Dimension a, Dimension b)
    {
	return getUnion(a, b, getResizeDirection());
    }

    protected Dimension getUnion(Dimension a, Dimension b, ResizeDirection direction)
    {
	Dimension dim = (Dimension)a.clone();
	if (direction == ResizeDirection.HORIZONTAL
	    || direction == ResizeDirection.BOTH) {
	    if(b.width > dim.width)
		dim.width = b.width;
	}
	if (direction == ResizeDirection.VERTICAL
	    || direction == ResizeDirection.BOTH) {
	    if(b.height > dim.height)
		dim.height = b.height;
	}
	return dim;
    }

    public Dimension getPreferredScrollableViewportSize() 
    {
	Dimension dim = super.getPreferredScrollableViewportSize();
	if(alignerTree == null)
	    return dim;

	/*
	if(ui instanceof AlignableTreeUI) {
	    Dimension d = ((AlignableTreeUI)ui).getPreferredScrollableViewportSize();
	    if(d != null)
	    dim = getUnion(dim, d);
	}
	*/
	//return getUnion(dim, alignerTree.getPreferredScrollableViewportSize());
	dim = getUnion(dim, alignerTree.getPreferredScrollableViewportSize());
	return dim;
    }

    public Dimension getPreferredSize()
    {
	Dimension dim = super.getPreferredSize();
	if(alignerTree == null)
	    return dim;

	/*
	if(ui instanceof AlignableTreeUI) {
	    Dimension d = ((AlignableTreeUI)ui).getPreferredScrollableViewportSize();
	    if(d != null)
		dim = getUnion(dim, d);
	}
	*/

	//return dim;
	return getUnion(dim, alignerTree.getPreferredSize());
    }

    public Dimension getMaximumSize()
    {
	Dimension dim = super.getMaximumSize();
	if(alignerTree == null)
	    return dim;
	return getUnion(dim, alignerTree.getMaximumSize());
    }

    public Dimension getMinimumSize()
    {
	Dimension dim =super.getMinimumSize();
	if(alignerTree == null)
	    return dim;

	return getUnion(dim, alignerTree.getMinimumSize());
    }

    public String getToolTipText(MouseEvent event)
    {
	if (getRowForLocation(event.getX(), event.getY()) == -1) {
	    return null;    
	}

	return getToolTipText(getPathForLocation(event.getX(), event.getY()));
    }

    public String getToolTipText(TreePath path)
    {
	return getToolTipText((NamedNode<?>)path.getLastPathComponent());
    }

    public String getToolTipText(NamedNode<?> node)
    {
	return node.getToolTipText();
    }

    public JToolTip createToolTip()
    {
        return new MultiLinesToolTip(this);
    }

    public void scrollPathToVisibleCenter(TreePath path)
    {
	expandPath(path);
	setSelectionPath(path);
	scrollPathToVisible(path);
	Rectangle viewPort = getVisibleRect();
	Rectangle pathRect = getPathBounds(path);

	viewPort.y = pathRect.y + pathRect.height/2 - viewPort.height/2;

	int height = getHeight();
	if(viewPort.y + viewPort.height > height) {
	    viewPort.y = height - viewPort.height;
	}

	if(pathRect.x + pathRect.width > viewPort.width) {
	    viewPort.x = pathRect.x + pathRect.width - viewPort.width;
	    if(viewPort.x + pathRect.width > viewPort.width)
		viewPort.x = getPathBounds(path.getParentPath()).x;
	}
	scrollRectToVisible(viewPort);
    }

    protected static void setAlignerTable(NameTreeTable<?> table)
    {
	TableColumn a = table.getColumnModel().getColumn(0);

	AlignerTree atree = (AlignerTree)((TreeHeaderRenderer)a.getHeaderRenderer()).getTree();
	List<NameTree> trees = table.getAlignableTrees();
	for(NameTree tree : trees) {
	    atree.addTree(tree);
	}
	Aligner aligner = ((AlignerTree)((TreeHeaderRenderer)a.getHeaderRenderer()).getTree()).getAligner();
	table.setAligner(aligner);
    }

    @SuppressWarnings("rawtypes")
    protected static NameUsageNode createNode(String rank,
				    String name,
				    Publication publication)
    {
	return createNode(rank, name, publication, null);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected static NameUsageNode createNode(String rank, 
					      String name,
					      Publication publication,
					      NameUsageNode parent)
    {
	NameUsageNode node =
	    parent == null ? new DefaultNameUsageNode(): parent.create();
	node.setRank(Rank.get(rank));
	node.setLiteral(name);
	node.setAppearance(new Appearance(publication));
	if(parent != null)
	    parent.addLowerNameUsage(node);
	return node;
    }

    public void expandAll()
    {
	visitAllNodes(new TreePath(getModel().getRoot()), Boolean.TRUE);
    }

    public void collapseAll()
    {
	visitAllNodes(new TreePath(getModel().getRoot()), Boolean.FALSE);
    }

    protected void visitAllNodes(TreePath path, Boolean toExpand)
    {
	TreeNode node = (TreeNode)path.getLastPathComponent();
	if(!node.isLeaf() && node.getChildCount() > 0) {
	    Enumeration<?> children = node.children();
	    while(children.hasMoreElements()) {
		Object element = children.nextElement();
		if (element instanceof TreeNode) {
		    visitAllNodes(path.pathByAddingChild((TreeNode)element), toExpand);
		}
	    }
	}

	if(toExpand == null)
	    return;

	if(toExpand)
	    expandPath(path);
	else
	    collapsePath(path);
    }
}
