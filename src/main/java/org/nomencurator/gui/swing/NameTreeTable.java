/*
 * NameTreeTable.java: a JTable to compare NameTrees
 *
 * Copyright (c) 2002, 2003, 2005, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.event.NameTreeTableModelEvent;
import org.nomencurator.gui.swing.event.NameTreeTableModelListener;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelEvent;
import org.nomencurator.gui.swing.event.UnitedNameTreeModelListener;

import org.nomencurator.gui.swing.plaf.basic.BasicNameTreeTableHeaderUI;

import org.nomencurator.gui.swing.NameTree.LineStyle;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.NameTreeTableModel;
import org.nomencurator.gui.swing.table.NameTreeTableHeader;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;
import org.nomencurator.gui.swing.table.NameTreeNodeRenderer;
import org.nomencurator.gui.swing.table.RenderingTableCellEditor;
import org.nomencurator.gui.swing.table.SynchronizedListSelectionModel;

import org.nomencurator.gui.swing.tree.Alignable;
import org.nomencurator.gui.swing.tree.Aligner;
import org.nomencurator.gui.swing.tree.AlignerTreeSynchronizer;
import org.nomencurator.gui.swing.tree.NamedNode;
import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.SynchronizedTreeSelectionModel;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.MatchingMode;
import org.nomencurator.io.NameUsageExchanger;
import org.nomencurator.io.NameUsageQueryParameter;
import org.nomencurator.io.QueryMode;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;
import org.nomencurator.io.QueryResultListenerAdaptor;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code JTable} of {@code NameUsageNode}s
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTable<T extends NameUsage<?>>
    extends HeaderEditableTable
    implements Alignable, 
	       // FIXME
	       // NameUsageExchanger<T>,
	       //ObjectExchanger<T>,
	       NameQuery,
	       ListSelectionListener,
	       TreeSelectionListener,
	       NameTreeTableModelListener
{
    private static final long serialVersionUID = 8721600536732960675L;

    protected JCheckBox drawLines;
    protected JPanel cornerPanel;
    protected int autoResizeMode;
    protected LineStyle lineStyle;
    protected Icon expandedIcon;
    protected Icon collapsedIcon;
    protected AlignerTree aligner;
    protected boolean selectAndScroll;
    protected Object eventSource;

    protected static TableCellRenderer cellRenderer = new NameTreeNodeRenderer();

    protected AlignerTreeSynchronizer synchronizer;

    protected int preferredHeight;

    public static final int ASSIGNMENT_TO = NameTreeTableModel.ASSIGNMENT_TO;

    public static final int EXCLUDE_COMMON_ASSIGNMENTS = NameTreeTableModel.EXCLUDE_COMMON_ASSIGNMENTS;

    public static final int EXCLUDE_DIFFERENT_ASSIGNMENTS = NameTreeTableModel.EXCLUDE_DIFFERENT_ASSIGNMENTS;

    public static final int EXCLUDE_MISSING_ASSIGNMENTS = NameTreeTableModel.EXCLUDE_MISSING_ASSIGNMENTS;

    public static final int MISSING_ASSIGNMENTS_ONLY = NameTreeTableModel.MISSING_ASSIGNMENTS_ONLY;

    protected QueryResultListenerAdaptor<T> adaptor;

    /*
    @Getter
    @Setter
    protected NameUsageQueryManager queryManager;
    */

    public NameTreeTable()
    {
	this(new NameTreeTableModel());
    }

    public NameTreeTable(NameTreeTableModel model)
    {
	super(model);
	setDefaultRenderer(NameTreeNode.class, cellRenderer);
	setDefaultRenderer(NameUsage.class, cellRenderer);
    }

    protected void initializeLocalVars()
    {
	super.initializeLocalVars();
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableColumn column = e.nextElement();
	    setHeaderRenderer(column);
	}
	//setHeaderRenderer(getHeaderColumn());

	/*
	AlignerTree aligner = getAlignerTree();
	if(aligner == null)
	    return;
	aligner.addTreeSelectionListener(this);
	e = getAlignableTrees();
	while(e.hasMoreElements()) {
	    aligner.addTree((NameTree)e.nextElement());
	}
	
	setAligner(aligner);
	TableModel rowMapper = getModel();
	if(rowMapper != null &&
	   rowMapper instanceof RowMapper) {
	    SynchronizedListSelectionModel selector = 
		(SynchronizedListSelectionModel)getSelectionModel();
	    selector.setRowMapper((RowMapper)rowMapper);
	    selector.setTree(aligner);
	    selector.addListSelectionListener(this);
	}
	setSelectAndScroll(true);
	*/
	
	TableColumn a = getColumnModel().getColumn(0);
	AlignerTree atree = (AlignerTree)((TreeHeaderRenderer)a.getHeaderRenderer()).getTree();
	List<NameTree> trees = getAlignableTrees();
	for(NameTree tree : trees)  {
	    atree.addTree(tree);
	    tree.setAligner(atree);
	}
	/*
	Aligner aligner = ((AlignerTree)((TreeHeaderRenderer)a.getHeaderRenderer()).getTree()).getAligner();
	setAligner(aligner);
	*/

	TableModel rowMapper = getModel();
	if(rowMapper != null &&
	   rowMapper instanceof RowMapper) {
	    SynchronizedListSelectionModel selector = 
		(SynchronizedListSelectionModel)getSelectionModel();
	    selector.setRowMapper((RowMapper)rowMapper);
	    selector.setTree(atree);
	    selector.setTable(this);
	    SynchronizedTreeSelectionModel treeSelector =
		(SynchronizedTreeSelectionModel)atree.getSelectionModel();
	    treeSelector.addTreeSelectionListener(selector);
	    treeSelector.setTable(this);
	    selector.setTreeSelectionModel(treeSelector);
	}
	setSelectAndScroll(true);

	preferredHeight = -1;

    }

    protected JTableHeader createDefaultTableHeader()
    {
	//return new NameTreeTableHeader(columnModel, this);

	NameTreeTableHeader<T> hdr = 
	    new NameTreeTableHeader<T>(columnModel, this);
	//hdr.setPreferredHeight(getPreferredScrollableViewportSize().height);
	return hdr;
    }

    public void setModel(TableModel dataModel)
    {
	if(this.dataModel != null &&
	   this.dataModel instanceof NameTreeTableModel)
	    ((NameTreeTableModel)dataModel).removeNameTreeTableModelListener(this);

	if(dataModel instanceof NameTreeTableModel) {
	    ((NameTreeTableModel)dataModel).addNameTreeTableModelListener(this);
	}
	super.setModel(dataModel);
    }


    protected ListSelectionModel createDefaultSelectionModel()
    {
        return new SynchronizedListSelectionModel();
    }


    protected JTable createRowHeaderTable()
    {
	JTable header = super.createRowHeaderTable();	

	TableColumn column = header.getColumnModel().getColumn(0);
	//setHeaderRenderer(column);
	column.getHeaderRenderer().getTableCellRendererComponent(this, column.getHeaderValue(),
								 false, false,
								 -1, 0);
	return header;
    }

    protected void setHeaderRenderer(TableColumn column)
    {
	TableCellRenderer headerRenderer = 
	    column.getHeaderRenderer();
	if(headerRenderer == null) {
	    JTableHeader header = getTableHeader();
	    if(header != null) {
		headerRenderer = header.getDefaultRenderer();
	    }
	}
	if(headerRenderer == null || 
	   !(headerRenderer instanceof TreeHeaderRenderer)) {
	    TreeHeaderRenderer treeHeaderRenderer = null;
	    Object headerValue = column.getHeaderValue();
	    if(!(headerValue instanceof TreeModel)) {
		if(headerValue != null)
		    treeHeaderRenderer = 
			new TreeHeaderRenderer(headerRenderer);
	    }
	    else {
		if(headerValue instanceof UnitedNameTreeModel) {
		    treeHeaderRenderer = 
			new TreeHeaderRenderer
			    ((UnitedNameTreeModel)headerValue,
			     headerRenderer);
		}

		else if(headerValue instanceof NameTreeModel) {
		    treeHeaderRenderer = 
			new TreeHeaderRenderer(
					       //(NameTreeModel)headerValue, getAligner(),
					       (NameTreeModel)headerValue, getAlignerTree(),
					       headerRenderer);
		    JTree tree = treeHeaderRenderer.getTree();
		    if(tree instanceof Alignable) {
			((Alignable)tree).setAligner(getAlignerTree());
		    }
		}
		else
		    treeHeaderRenderer = 
			new TreeHeaderRenderer
			    ((TreeModel)headerValue,
			     headerRenderer);
	    }

	    column.setHeaderRenderer(treeHeaderRenderer);
	    if(column instanceof HeaderEditableTableColumn)
		((HeaderEditableTableColumn)column).setHeaderEditor(new RenderingTableCellEditor(treeHeaderRenderer));
	}

    }


    protected void setDefaultHeaderRenderer()
    {
	
    }

    protected void setDefaultHeaderEditor()
    {
    }

    public JPanel getCornerPanel()
    {
	if(cornerPanel == null)
	    cornerPanel = createCornerPanel();
	return cornerPanel;
    }

    public void setAlignerTreeSynchronizer(AlignerTreeSynchronizer synchronizer)
    {
	this.synchronizer = synchronizer;
    }

    /*
    protected JPanel createCornerPanel()
    {
	drawLines = 
	    new JCheckBox("Draw lines", true);
	JPanel p = new JPanel();
	p.add(drawLines);
	drawLines.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if(drawLines.isSelected()) {
			setLineStyle(LineStyle.ANGLED);
		    }
		    else {
			setLineStyle(LineStyle.NONE);
		    }
		}
	    });
	return p;
    }

    */
    protected JPanel createCornerPanel()
    {
	TableColumn a = getHeaderColumn();

	if(a.getHeaderRenderer() == null) {
	    a.setHeaderRenderer(
				new TreeHeaderRenderer
				    ((UnitedNameTreeModel)a.getHeaderValue(),
				     null));
	}
	JPanel p = (JPanel)a.getHeaderRenderer().getTableCellRendererComponent(this, a.getHeaderValue(),
									       false, false,
									       -1, 0);
	AlignerTree atree = (AlignerTree)((TreeHeaderRenderer)a.getHeaderRenderer()).getTree();

	List<NameTree> trees = getAlignableTrees();
	if(atree != null && trees != null)
	    for(NameTree tree : trees) {
		atree.addTree(tree);
	    }

	return p;
    }


    /**
     * Returns preferred size of the viewport for this table
     *
     * @return a {@code Dimension} representing 
     * {@code prfeeredSize} of {@code JViewport}
     * of which view is this table
     *
     * @see Scrollable#getPreferredScrollableViewportSize
     */
    public Dimension getPreferredScrollableViewportSize()
    {
	return super.getPreferredScrollableViewportSize();
    }
    
    public void addColumn(TableColumn column)
    {
	setHeaderRenderer(column);
	column.setCellRenderer(cellRenderer);
        super.addColumn(column);
    }

    protected TableColumn createTableColumn(int index)
    {
	TableColumn column = 
	    super.createTableColumn(index);
        TableModel model = getModel();
	if(model instanceof NameTreeTableModel) {
	    column.setHeaderValue(((NameTreeTableModel)model).getHeaderValue(index));
	}
	return column;
    }

    public void setTableHeader(JTableHeader header)
    {
	TableCellRenderer defaultRenderer = null;
	if(header != null)
	    defaultRenderer = 
		header.getDefaultRenderer();

	TableCellRenderer currentRenderer = null;
	JTableHeader currentHeader = 
	    getTableHeader();
	if(currentHeader != null)
	    currentRenderer = 
		currentHeader.getDefaultRenderer();

	Enumeration<TableColumn> columns = getColumnModel().getColumns();
	while (columns.hasMoreElements()) {
	    TableColumn column = columns.nextElement();
	    TableCellRenderer headerRenderer = 
		column.getHeaderRenderer();
	    if(!(headerRenderer instanceof TreeHeaderRenderer))
		continue;

	    TreeHeaderRenderer renderer = 
		(TreeHeaderRenderer)headerRenderer;
	    headerRenderer = renderer.getRenderer();
	    if(headerRenderer == currentRenderer)
		renderer.setRenderer(defaultRenderer);
	}

	super.setTableHeader(header);
    }

    public void setCollapsedIcon(Icon icon)
    {
	if(collapsedIcon == icon ||
	   (collapsedIcon != null && icon != null &&
	    icon.equals(collapsedIcon)))
	    return;
	    
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableColumn column = e.nextElement();
	    TableCellRenderer headerRenderer = 
		column.getHeaderRenderer();
	    if(!(headerRenderer instanceof TreeHeaderRenderer))
		continue;
	    ((TreeHeaderRenderer)headerRenderer).setCollapsedIcon(icon);
	}

	firePropertyChange("JTable.collapsedIcon", collapsedIcon, icon);
	getTableHeader().repaint();
	collapsedIcon = icon;
    }

    public void setExpandedIcon(Icon icon)
    {
	if(expandedIcon == icon ||
	   (expandedIcon != null && icon != null &&
	    icon.equals(expandedIcon)))
	    return;
	    
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableColumn column = e.nextElement();
	    TableCellRenderer headerRenderer = 
		column.getHeaderRenderer();
	    if(!(headerRenderer instanceof TreeHeaderRenderer))
		continue;
	    ((TreeHeaderRenderer)headerRenderer).setExpandedIcon(icon);
	}

	firePropertyChange("JTable.expandedIcon", expandedIcon, icon);
	getTableHeader().repaint();
	expandedIcon = icon;
    }

    public void setLineStyle(LineStyle style)
    {
	if(lineStyle == style ||
	   (lineStyle != null && style != null &&
	    style == lineStyle))
	    return;
	    
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableColumn column = e.nextElement();
	    TableCellRenderer headerRenderer = 
		column.getHeaderRenderer();
	    if(!(headerRenderer instanceof TreeHeaderRenderer))
		continue;
	    ((TreeHeaderRenderer)headerRenderer).setLineStyle(style.getValue());
	}

	firePropertyChange("JTable.lineStyle", lineStyle, style);
	getTableHeader().repaint();
	lineStyle = style;
	if(drawLines != null) {
	    drawLines.setSelected(LineStyle.NONE != style);
	}
    }

    public Icon getCollapsedIcon()
    {
	return collapsedIcon;
    }

    public Icon getExpandedIcon()
    {
	return expandedIcon;
    }

    public LineStyle getLineStyle()
    {
	return lineStyle;
    }

    public void setAligner(AlignerTree aligner)
    {
	setAligner(aligner.getAligner());
    }

    public void setAligner(Aligner aligner)
    {
	if(this.aligner == aligner)
	    return;

	Enumeration<TableColumn> columns = getColumnModel().getColumns();
	while(columns.hasMoreElements()) {
	    TreeHeaderRenderer renderer = 
		(TreeHeaderRenderer)columns.nextElement().getHeaderRenderer();
	    if(renderer == null)
		continue;
	    JTree tree = renderer.getTree();
	    if(tree instanceof Alignable)
		((Alignable)tree).setAligner(aligner);		
	}

	if(aligner instanceof AlignerTree)
	    this.aligner = (AlignerTree)aligner;
	else
	    this.aligner = null; // new AlignerTree(); 
    }

    //public AlignerTree getAligner()
    public Aligner getAligner()
    {
	AlignerTree tree = getAlignerTree();
	if(tree != null)
	    return tree.getAligner();

	return null;
    }

    public JTree getTree(TreeModel model)
    {
	TableColumn column = 
	    getColumn(model.toString());
	if(column == null)
	    return null;
	return ((TreeHeaderRenderer)column.getHeaderRenderer()).getTree();
    }

    public List<NameTree> getAlignableTrees()
    {
	List<NameTree> trees = new ArrayList<NameTree>();
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableCellRenderer r = e.nextElement().getHeaderRenderer();

	    if(!(r instanceof TreeHeaderRenderer))
		continue;

	    JTree tree = ((TreeHeaderRenderer)r).getTree();
	    if(tree != null && (tree instanceof NameTree))
		trees.add((NameTree)tree);
	}

	return trees;
    }

    public AlignerTree getAlignerTree()
    {
	Enumeration<TableColumn> e = getColumnModel().getColumns();
	while(e.hasMoreElements()) {
	    TableCellRenderer r =	e.nextElement().getHeaderRenderer();

	    if(!(r instanceof TreeHeaderRenderer))
		continue;

	    JTree tree = ((TreeHeaderRenderer)r).getTree();
	    if(tree != null && (tree instanceof AlignerTree))
		return (AlignerTree)tree;
	}

	return null;
    }


    public void valueChanged(ListSelectionEvent event)
    {
	repaintHeader();
	super.valueChanged(event);
    }

    /*
    public void valueChanged(TreeSelectionEvent event)
    {
	repaintHeader();
    }
    */

    protected void repaintHeader()
    {
	JTableHeader header = 
	    getTableHeader();
	if(header != null)
	    header.repaint();
    }

    protected void setSelectAndScroll(boolean toScroll)
    {
	selectAndScroll = toScroll;
    }

    protected boolean isSelectAndScroll()
    {
	return selectAndScroll;
    }

    public void setRowSelectionInterval(int index0, int index1)
    {
	super.setRowSelectionInterval(index0, index1);
	scrollTo(index0, 0);
    }   

    public void addRowSelectionInterval(int index0, int index1)
    {
	super.addRowSelectionInterval(index0, index1);
	scrollTo(index0, 0);
    }   

    public void scrollTo(int row, int column)
    {
        Container pane = getParent();
        if (!(pane instanceof JViewport))
	    return;
	pane = pane.getParent();
	if (!(pane instanceof JScrollPane))
	    return;

	JViewport viewport = 
	    ((JScrollPane)pane).getViewport();

	Rectangle view = viewport.getViewRect();

	Rectangle cell = getCellRect(row, column, true);

	int y = cell.y - (view.height - cell.height)/2;
	if(y < 0)
	    y = 0;

	int x = cell.x - (view.width - cell.width)/2;
	if(x < 0)
	    x = 0;

	viewport.setViewPosition(new Point(x, y));
    }

    /**
     * Converts selection paths in one of synchronized trees
     * represented by {@code event}
     * to corresponding paths in other trees, and set 
     * them to selection.
     *
     * @param event {@code TreeSelectionEvent} in one
     * of synchronized trees
     */
    public void valueChanged(TreeSelectionEvent event)
    {
	//repaintHeader();

	if(getEventSource() != null) {
	    return;
	}

	setEventSource(event.getSource());

	TreePath path = event.getPath();
	TreePath[] paths = event.getPaths();

	if(path == null) {
	    clearSelection();
	    setEventSource(null);
	    return;
	}

	RowMapper rowMapper = (RowMapper)getModel();

	int[] rows = rowMapper.getRowsForPaths(paths);

	if(rows.length == 0) {
	    setEventSource(null);
	    return;
	}

	if(paths.length == 1) {
	    if(event.isAddedPath()) {
		setRowSelectionInterval(rows[0], rows[0]);
	    }
	    else
		removeRowSelectionInterval(rows[0], rows[0]);
	    setEventSource(null);
	    return;
	}

	List<TreePath> selected = new ArrayList<TreePath>();
	List<TreePath> unselected = new ArrayList<TreePath>();

	for(int i = 0; i < paths.length; i++) {
	    path = paths[i];
	    if(event.isAddedPath(paths[i]))
		selected.add(path);
	    else
		unselected.add(path);
	}

	paths = new TreePath[unselected.size()];
	for(int i = 0; i < paths.length; i++) {
	    paths[i] = unselected.get(i);
	}
	rows = rowMapper.getRowsForPaths(paths);
	for(int i = 0; i < rows.length; i++) {
	    int row = rows[i];
	    removeRowSelectionInterval(row, row);
	}

	paths = new TreePath[selected.size()];
	for(int i = 0; i < paths.length; i++) {
	    paths[i] = selected.get(i);
	}
	rows = rowMapper.getRowsForPaths(paths);
	for(int i = 0; i < rows.length; i++) {
	    int row = rows[i];
	    addRowSelectionInterval(row, row);
	}

	setEventSource(null);
    }

    /**
     * Sets {@code source} as the `owner' of selection
     * to control event handling
     *
     * @param source {@code Object} represents
     * the `owner' of selection
     */
    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    /**
     * Returns `owner' {@code Object}  of selection
     * to control event handling
     *
     * @return {@code Object} represents
     * the `owner' of selection
     */
    protected Object getEventSource()
    {
	return eventSource;
    }

    @Override
    public JToolTip createToolTip()
    {
        return new MultiLinesToolTip(this);
    }

    @Override
    public String getToolTipText(MouseEvent event)
    {
	int column = getColumnModel().getColumnIndexAtX(event.getX());
	if (column == -1)
	    return super.getToolTipText(event);
	column = convertColumnIndexToModel(column);
	return ((NameTreeNodeRenderer)cellRenderer).getToolTipText(event, this);
    }


    public int getPreferredHeight()
    {
	return preferredHeight;
    }

    public void setPreferredHeight(int height)
    {
	preferredHeight = height;
    }

    /*
    public Dimension getPreferredSize()
    {
        Dimension size = super.getPreferredSize();
	if(preferredHeight > 0)
	    size.height = preferredHeight;

	return size;
    }
    */

    @SuppressWarnings("unchecked")
    public int getPreferredHeaderHeight()
    {
	return ((NameTreeTableHeader<T>)getTableHeader()).getPreferredHeight();
    }

    @SuppressWarnings("unchecked")
    public void setPreferredHeaderHeight(int height)
    {
	((NameTreeTableHeader<T>)getTableHeader()).setPreferredHeight(height);
    }

    public String getNames(Collection<? extends NameUsage<?>> nameUsages, String ascribedName, Rank rank, String authors, String year, MatchingMode queryType)
    {
	return getNames(ascribedName, rank, authors, year, queryType);
    }

    public String getNames(String ascribedName, Rank rank, String authors, String year, MatchingMode queryType)
    {
	AlignerTree atree = 
	    (AlignerTree)((TreeHeaderRenderer)getColumnModel().getColumn(0).getHeaderRenderer()).getTree();
	NameTreeTableModel model = (NameTreeTableModel)getModel();

	List<NamedNode<?>> nodes = model.getNodesForLiteral(ascribedName, rank, authors, year);
	String result = 
	    atree.getNames(ascribedName, rank, authors, year, queryType, Collections.enumeration(nodes));
	nodes.clear();

	/*
	if(model.getTableMode() == NameTreeTableModel.AS_IS) {
	    doLayout();
	    revalidate();
	}
	*/
	return result;
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	super.setLocale(locale);
	if(aligner != null) {
	    aligner.setLocale(locale);
	}
    }

    public ObjectExchanger<?> getObjectExchanger()
    {
	if (aligner instanceof ObjectExchanger)
	    return (ObjectExchanger<?>)aligner;
	return null;
    }

    public T[] getObjects(String name){ return null; }
    public T[] getObjects(String[] name){ return getObjects(name[0]); }
    public T[] getObjects(String name, MatchingMode queryType){ return null; }
    public T[] getObjects(String[] name, MatchingMode queryType){ return getObjects(name[0], queryType); }

    public Thread requestObject(String id){ return null; }

    public Thread requestObjects(String name)
    {
	return requestNameUsages(name, null, null);
    }

    public Thread requestObjects(String[] names)
    {
	return requestNameUsages(names[0], Rank.get(names[1]));
    }

    public Thread requestObjects(String name, MatchingMode queryType)
    {
	return requestObjects(name);
    }

    public Thread requestObjects(String[] names, MatchingMode queryType)
    {
	return requestObjects(names);
    }

    public Thread requestNameUsages(String name, Rank rank)
    {
	return requestNameUsages(name, rank, null);
    }

    public Thread requestNameUsages(String name, Rank rank, MatchingMode queryType)
    {
	Thread t = new RequestNameUsages<T>(name, rank, this);
	t.start();
	return t;
    }

    public void addQueryResultListener(QueryResultListener<T> listener)
    {
	if(adaptor == null)
	    adaptor = new QueryResultListenerAdaptor<T>();

	adaptor.addQueryResultListener(listener);
    }

    public void removeQueryResultListener(QueryResultListener<T> listener)
    {
	if(adaptor != null)
	    adaptor.removeQueryResultListener(listener);
    }

    public void fireQueryResultEvent(QueryResultEvent<T> event)
    {
	if(adaptor != null)
	    adaptor.fireQueryResultEvent(event);
    }

    public void setDefaultQueryType(MatchingMode matchingMode) { }

    public MatchingMode getDefaultQueryType()
    {
	return MatchingMode.EXACT;
    }

    class RequestNameUsages<E extends NameUsage<?>>
	extends Thread
    {
	Rank rank;
	String name;
	NameTreeTable<? extends E> table;
	
	public RequestNameUsages(String name,
				 Rank rank,
				 NameTreeTable<? extends E> table)
	{
	    this.rank = rank;
	    if(name != null)
		this.name = name.toLowerCase();
	    this.table = table;
	}
	
	public void run()
	{
	    NameTreeTableModel model = (NameTreeTableModel)table.getModel();
	    
	    List<NamedNode<?>> nodes = model.getNodesForLiteral(name, rank, null, null);
	    if(nodes.isEmpty())
		return;
	    
	    AlignerTree atree = 
		(AlignerTree)((TreeHeaderRenderer)table.getColumnModel().getColumn(0).getHeaderRenderer()).getTree();
	    String result = 
		atree.getNames(name, rank, null, null, MatchingMode.EXACT, Collections.enumeration(nodes));
	    nodes.clear();
	    
	    /*
	    if(model.getTableMode() == NameTreeTableModel.AS_IS) {
		table.doLayout();
		table.revalidate();
	    }
	    */

	    // FIXME 20141123
	    /*
	    NameUsageQueryParameter<N, T> queryParameter = 
		new NameUsageQueryParameter<N, T>(name, rank, , MatchingMode.EXACT);
	    queryParameter.setQueryMode(QueryMode.NAMEUSAGES);
	    
	    //table.fireQueryResultEvent(new QueryResultEvent(table, nodes, this, QueryThread.NAMEUSAGES));
	    table.fireQueryResultEvent(new QueryResultEvent(table, queryParameter, nodes, this));
	    */
	}
    }

    public void treeAdded(NameTreeTableModelEvent event)
    {
	TableColumn column = 
	    new HeaderEditableTableColumn(event.getColumnIndex());
	column.setHeaderValue(event.getHeaderValue());
	addColumn(column);
    }

    public void treeRemoved(NameTreeTableModelEvent event)
    {
    }

    public void columnAdded(TableColumnModelEvent event)
    {
	super.columnAdded(event);
	TableColumn column = getColumnModel().getColumn(event.getToIndex());
	column.setCellRenderer(cellRenderer);
	AlignerTree aligner = getAlignerTree();
	if(synchronizer != null &&
	   aligner != null) {
	    synchronizer.add(aligner);
	    synchronizer = null;
	}

	TableCellRenderer r = column.getHeaderRenderer();
	if(r instanceof TreeHeaderRenderer) {
	    JTree tree = ((TreeHeaderRenderer)r).getTree();
	    if(!(tree instanceof AlignerTree) &&
	       aligner != null)
		aligner.addTree((NameTree)tree);
	    resizeAndRepaint();
	}
    }


    public void columnRemoved(TableColumnModelEvent event)
    {
	AlignerTree atree = getAlignerTree();
	if(atree != null) {
	    List<JTree> renderers = atree.getTrees();
	    
	    Enumeration<TableColumn> e =
		getColumnModel().getColumns();
	    
	    while(e.hasMoreElements()) {
		TableCellRenderer r =
		    e.nextElement().getHeaderRenderer();
		if(renderers.contains(r))
		    renderers.remove(r);
	    }
	    
	    for(JTree tree: renderers) {
		atree.removeTree((NameTree)tree);
	    }
	    
	    renderers.clear();
	}

	super.columnRemoved(event);
    }

}

