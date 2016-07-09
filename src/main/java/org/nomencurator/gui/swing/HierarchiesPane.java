/*
 * HierarchiesPane.java:  a JTabbedPane ree for name comparison
 *
 * Copyright (c) 2002, 2003, 2014, 2015, 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.BorderLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.tree.TreeModel;

import org.nomencurator.io.ObjectExchanger;
import org.nomencurator.io.ubio.UBio;
import org.nomencurator.io.QueryResultEvent;
import org.nomencurator.io.QueryResultListener;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;

import org.nomencurator.gui.swing.NameTree.LineStyle;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.NameTreeEditor;
import org.nomencurator.gui.swing.table.NameTreeRenderer;
import org.nomencurator.gui.swing.table.NameTreeTableHeader;
import org.nomencurator.gui.swing.table.NameTreeTableMode;
import org.nomencurator.gui.swing.table.NameTreeTableModel;
import org.nomencurator.gui.swing.table.RenderingTableCellEditor;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;
import org.nomencurator.gui.swing.table.TableColumnModelSynchronizer;
import org.nomencurator.gui.swing.table.TableColumnSynchronizer;

import org.nomencurator.gui.swing.tree.AlignerTreeSynchronizer;
import org.nomencurator.gui.swing.tree.NameTreeModel;
import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.NameTreeCellEditor;
import org.nomencurator.gui.swing.tree.NameTreeCellRenderer;
import org.nomencurator.gui.swing.tree.UnitedNameTreeModel;

import org.nomencurator.util.XMLResourceBundleControl;


import lombok.Getter;
import lombok.Setter;

/**
 * A set of tabs containging tables to compare hierarchies
 *
 * @version 	02 June. 2016
 * @author 	Nozomi `James' Ytow
 */
public class HierarchiesPane<T extends NameUsage<?>>
    extends JTabbedPane
    implements ActionListener,
	       ChangeListener,
	       MouseListener, // to show a popup menu
	       PropertyChangeListener//,
	       //	       QueryResultListener
{
    private static final long serialVersionUID = 1989260220254311916L;

    @Getter
    @Setter
    /** Title of this comparator */
    protected String title;

    protected TableColumnModelSynchronizer columnModelSynchronizer;

    protected TableColumnSynchronizer headerSynchronizer;

    protected Map<JScrollPane, NameTreeTable<T>> tables;

    @Getter
    @Setter
    protected JPopupMenu popupMenu;

    protected JMenuItem openMenuItem;

    protected JMenuItem swapMenuItem;

    protected Object propertyEventSource;

    protected AlignerTreeSynchronizer treeSynchronizer;

    protected Map<TreeModel, ViewportSynchronizer> viewportSynchronizers;

    protected DefaultBoundedRangeModel splitter;

    protected static int thumbWidth = 15;

    protected Object eventSource;

    protected List<NameTreeTable<T>> nameTreeTables;

    protected static final NameTreeTableMode[] tableModes = NameTreeTableMode.values();

    protected String[] hierarchiesPaneTabTexts;

    static {
	Icon icon =
	    UIManager.getIcon( "Slider.verticalThumbIcon" );
	if(icon!= null)
	    thumbWidth = icon.getIconHeight();
    }

    public HierarchiesPane()
    {
	this(BOTTOM);
    }

    public HierarchiesPane(int tabPlacement)
    {
	this(tabPlacement, Locale.getDefault());
    }

    public HierarchiesPane(Locale locale)
    {
	this(BOTTOM, locale);
    }

    public HierarchiesPane(int tabPlacement, Locale locale)
    {
	this(tabPlacement, locale, false);
    }

    public HierarchiesPane(int tabPlacement, Locale locale, boolean child)
    {
	super(tabPlacement);
	addMouseListener(this);

	popupMenu = new JPopupMenu();
	if(child) {
	    swapMenuItem = new JMenuItem("Swap comparator", KeyEvent.VK_S);
	    addMenuItem(swapMenuItem);
	} else {
	    openMenuItem = new JMenuItem("Move to new window", KeyEvent.VK_W);
	    addMenuItem(openMenuItem);
	}

	//int h = getHeight();
	int h = getPreferredSize().height;
	if(h > 0)
	    h -= thumbWidth * 2;
	if(h < thumbWidth)
	    h = 2 * thumbWidth;
	splitter = new DefaultBoundedRangeModel((h + 1)/2, 0, 0, h);
	splitter.addChangeListener(this);

	setLocale(locale);
    }

    public HierarchiesPane(UnitedNameTreeModel unitedTreeModel)
    {
	this(unitedTreeModel, Locale.getDefault());
    }

    public HierarchiesPane(UnitedNameTreeModel unitedTreeModel, Locale locale)
    {
	this(locale);
	classify(unitedTreeModel);
    }

    protected int getPopupMenuIndex(JMenuItem item) {
	Component[] components = popupMenu.getComponents();
	int index = 0;
	for(Component component : components) {
	    if(component == item) {
		return index;
	    }
	    index++;
	}
	return -1;
    }

    public void addMenuItem(JMenuItem item) {
	int index = getPopupMenuIndex(item);
	if(index != -1) {
	    popupMenu.remove(index);
	}
	else {
	    item.addActionListener(this);
	}
	popupMenu.add(item);
    }

    public void removeMenuItem(JMenuItem item) {
	int index = getPopupMenuIndex(item);
	if(index != -1) {
	    popupMenu.remove(index);
	    item.removeActionListener(this);
	}
    }

    public void setSize(Dimension size)
    {
	super.setSize(size);
	resetSplitter(getSize());
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != splitter || 
	   tables == null || 
	   getEventSource() != null)
	    return;
	setEventSource(event.getSource());
	resizeHeader();
	setEventSource(null);
    }

    protected synchronized void setEventSource(Object source)
    {
	eventSource = source;
    }
    
    protected Object getEventSource()
    {
	return eventSource;
    }

    public void revalidate()
    {
	super.revalidate();
    }
    
    public void validate()
    {
	super.validate();
    }

    public void repaint()
    {
	super.repaint();
	//updateSlider();
    }

    public void updateSlider()
    {
	if(model == null)
	    return;
	Component c = getSelectedComponent();
	if(c == null)
	    return;

	int height = ((JComponent)c).getHeight();
	if(height == 0)
	    return;

	height -=  thumbWidth * 2;
	if(height < 0)
	    return;

	int components = getComponentCount();
	for(int i = 0; i < components; i++) {
	    c = getComponent(i);
	    if(c instanceof JScrollPane)
		break;
	}
	if(!(c instanceof JScrollPane))
	    return;

	setEventSource(this);
	int value = splitter.getValue() * height/splitter.getMaximum();
	if(splitter.getMaximum() != height)
	    splitter.setMaximum(height);
	height = ((JScrollPane)c).getColumnHeader().getHeight();
	if(height > 0)
	    splitter.setValue(((JScrollPane)c).getColumnHeader().getHeight() - thumbWidth);
	else {
	    splitter.setValue(value);
	    resizeHeader();
	}
	setEventSource(null);
    }
    
    public void resizeHeader()
    {
	if(splitter == null ||
	   tables == null) {
	    return;
	}
	if(((JComponent)getSelectedComponent()).getHeight() == 0)
	    return;

	int value = splitter.getValue();

	int headerHeight = 
	    value + thumbWidth;
	Set<JScrollPane> panes = tables.keySet();
	int height = 0;
	for (JScrollPane pane : panes) {
	    if(height < pane.getHeight())
		height = pane.getHeight();
	}
	for (JScrollPane pane : panes) {
	    int bodyHeight = 
		pane.getHeight() - value;
	    NameTreeTable<T> table =tables.get(pane);
	    table.setPreferredHeaderHeight(headerHeight);
	    Dimension size = pane.getViewport().getSize();
	    size.height = bodyHeight;
	    pane.getViewport().setSize(size);
	    Component c = pane.getColumnHeader();
	    if(c != null) {
		size = c.getSize();
		size.height = headerHeight;
		c.setSize(size);
	    }
	}

	if(height > 0) {
	    splitter.setMaximum(height - thumbWidth * 2);
	}

	((JComponent)getSelectedComponent()).revalidate();
	((JComponent)getSelectedComponent()).repaint();
    }

    protected void resetSplitter(int value)
    {
	splitter.setValue(value);
	resizeHeader();
	//revalidate();
    }

    protected void resetSplitter(Dimension size)
    {
	int max = size.height - thumbWidth * 2;
	if(max < 0)
	    return;
	int value = 
	    (max * splitter.getValue())/ splitter.getMaximum();
	splitter.setMaximum(max);
	splitter.setValue(value);
	resizeHeader();
	//revalidate();
    }

    public void insertTab(String title, Icon icon, Component component, String tip, int index)
    {
	insertTab(title, icon, component, tip, index, true);
    }

    @SuppressWarnings("unchecked")
    public void insertTab(String title, Icon icon, Component component, String tip, int index, boolean useSplitter)
    {
	Dimension size = null;
	int height = -1;
	if(component instanceof NameTreeTable) {
	    NameTreeTable<T> table = (NameTreeTable<T>)component;
	    if(nameTreeTables == null)
		nameTreeTables = new ArrayList<NameTreeTable<T>>();
	    nameTreeTables.add(table);
	    height = 
		table.getTableHeader().getPreferredSize().height;
	    component = createTablePane(table, useSplitter);
	    size = component.getPreferredSize();
	}
	super.insertTab(title, icon, component, tip, index);
	if(size != null) {
	    //resetSplitter(height);
	    //resetSplitter(size);
	}
    }

    protected void preprocessRemovalAt(int index)
    {
	Component[] components = ((Container)getComponentAt(index)).getComponents();
	NameTreeTable<T> table = null;
	JScrollPane pane = null;
	int i = 0;
	while (table == null && i < components.length) {
	    Component c = components[i++];
	    if (c instanceof JScrollPane) {
		pane = (JScrollPane)c;
		table = tables.get(c);
		if(table == null)
		    pane = null;
	    }
	}
	if(table != null) {
	    tables.remove(pane);
	    treeSynchronizer.remove(table.getAlignerTree());
	    headerSynchronizer.remove(table);
	    int cnt = columnModelSynchronizer.size();
	    columnModelSynchronizer.remove(table);
	    if(tables.isEmpty()) {
		/*
		tables = null;
		headerSynchronizer = null;
		columnModelSynchronizer = null;
		*/
	    }
	}
    }

    public void removeTabAt(int index)
    {
	preprocessRemovalAt(index);
	super.removeTabAt(index);
    }

    public AlignerTree getAlignerTree(int index)
    {
	int tabCount = getTabCount();
	if(index < 0 || index >= tabCount)
	    return null;
	Component component = getComponentAt(index);
	NameTreeTable<T> table = tables.get(component);
	if(table == null) {
	    return null;
	}
	return table.getAlignerTree();
    }

    protected JComponent createTablePane(NameTreeTable<T> table)
    {
	return createTablePane(table, true);
    }

    @SuppressWarnings("unchecked")
    protected JComponent createTablePane(NameTreeTable<T> table, boolean useSplitter)
    {
	if(columnModelSynchronizer == null) {
	    tables = Collections.synchronizedMap(new HashMap<JScrollPane, NameTreeTable<T>>());
	    headerSynchronizer = new TableColumnSynchronizer();
	    columnModelSynchronizer = new TableColumnModelSynchronizer();
	}
	
	TableColumnModel columnModel
	    = table.getColumnModel();

	columnModelSynchronizer.put(table, columnModel);
	
	JScrollPane pane = 
	    new JScrollPane(table,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	ResizableScrollPaneLayout layout = 
	    new ResizableScrollPaneLayout.UIResource(splitter, null, thumbWidth);
	pane.setLayout(layout);
	layout.syncWithScrollPane(pane);
	/*
	JScrollPane pane = 
	    new ResizableScrollPane(table,
			    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	*/
	tables.put(pane, table);
	table.addPropertyChangeListener(this);
	NameTreeTableHeader<T> header = 
	    (NameTreeTableHeader<T>)table.getTableHeader();
	//header.setPreferredHeight(header.getPreferredSize().height);
	//header.setPreferredHeight((1 + table.getSize().height) / 2);
	
	if(treeSynchronizer == null)
	    treeSynchronizer = new AlignerTreeSynchronizer();
	treeSynchronizer.add(table.getAlignerTree());

	if(useSplitter) {
	if(viewportSynchronizers == null)
	    viewportSynchronizers = new HashMap<TreeModel, ViewportSynchronizer>();
	viewPortSynchronize(table.getAlignerTree());
	    
	List<NameTree> nameTrees = table.getAlignableTrees();
	for (NameTree nameTree : nameTrees) {
	    viewPortSynchronize(nameTree);
	}
	}

	JPanel panel = new JPanel(new BorderLayout());
	panel.add(pane, BorderLayout.CENTER);
	JSlider slider = new JSlider(splitter);
	slider.setOrientation(JSlider.VERTICAL);
	slider.setInverted(true);
	JPanel sliderPanel = new JPanel();
	sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
	sliderPanel.add(Box.createVerticalStrut((thumbWidth + 1)/2));
	sliderPanel.add(slider);
	sliderPanel.add(Box.createVerticalStrut((thumbWidth + 1)/2));
	panel.add(sliderPanel, BorderLayout.WEST);
	//return pane;
	return panel;
    }

    protected void viewPortSynchronize(JTree tree)
    {
	if(tree == null)
	    return;

	ViewportSynchronizer vSync = viewportSynchronizers.get(tree.getModel());
	if(vSync == null) {
	    vSync = new ViewportSynchronizer();
	    viewportSynchronizers.put(tree.getModel(), vSync);
	}
	vSync.add(tree);
    }

    protected void putHeader(NameTreeTable<T> table)
    {
	int w = table.getHeaderWidth();
	int max = headerSynchronizer.getPreferredMaxWidth();

	TableColumn headerColumn = 
	    table.getHeaderColumn();
	if(tables.isEmpty()) {
	    headerColumn.setMinWidth(w);
	    headerColumn.setMaxWidth(w);
	    headerColumn.setPreferredWidth(w);
	    headerColumn.setWidth(w);
	}
	else if(w > max) {
	    headerSynchronizer.setMaxWidth(w);
	    headerSynchronizer.setMinWidth(w);
	    headerSynchronizer.setPreferredWidth(w);
	    headerSynchronizer.setWidth(w);
	    Set<JScrollPane> panes = tables.keySet();
	    for (JScrollPane pane : panes) {
		JTable header = tables.get(pane).getRowHeaderTable();
		header.setPreferredScrollableViewportSize(new Dimension(w +
									header.getColumnModel().getColumnMargin(), 0));
		pane.getRowHeader().setPreferredSize(header.getPreferredScrollableViewportSize());
	    }
	    max = w;
	}

	headerColumn.setMinWidth(max);
	headerColumn.setMaxWidth(max);
	headerColumn.setPreferredWidth(max);
	headerColumn.setWidth(max);
	/*
	JTable header = 
	    table.getRowHeaderTable();
	header.setPreferredScrollableViewportSize(new Dimension(max +
								header.getColumnModel().getColumnMargin(), 0));
	*/
	headerSynchronizer.put(table, headerColumn);
    }

    public void propertyChange(PropertyChangeEvent event)
    {
	String property = event.getPropertyName();
	if(property == null)
	    return;

	if(getPropertyEventSource() != null)
	    return;

	setPropertyEventSource(event.getSource());
	Collection<NameTreeTable<T>> nameTreeTables = tables.values();
	Object value = event.getNewValue();

	if(property.equals("JTable.lineStyle")) {
	    for (NameTreeTable<T> table : nameTreeTables) {
		if(table == getPropertyEventSource())
		    continue;
		table.setLineStyle(LineStyle.getLineStyle(value.toString()));
	    }
	}
	else if(property.equals("JTable.collapsedIcon")) {
	    for (NameTreeTable<T> table : nameTreeTables) {
		if(table == getPropertyEventSource())
		    continue;
		table.setCollapsedIcon((Icon)value);
	    }
	}
	else if(property.equals("JTable.expandedIcon")) {
	    for (NameTreeTable<T> table : nameTreeTables) {
		if(table == getPropertyEventSource())
		    continue;
		table.setExpandedIcon((Icon)value);
	    }
	}

	setPropertyEventSource(null);
    }

    protected Object getPropertyEventSource()
    {
	return propertyEventSource;
    }

    protected synchronized void setPropertyEventSource(Object source)
    {
	/*
	if(propertyEventSource != null)
	    return;
	*/
	propertyEventSource = source;
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	if(locale == null)
	    locale = Locale.getDefault();

	super.setLocale(locale);

	if(locale.equals(getLocale())
	   && hierarchiesPaneTabTexts != null)
	    return;

	int tabs = getTabCount();
	for(int i = 0; i < tabs; i++) {
	    Component c = getComponentAt(i);
	    if(c == null ||
	       !(c instanceof JPanel))
		continue;
	    Component[] components = ((JPanel)c).getComponents();
	    if(components == null)
		continue;
	    for(int j = 0; j < components.length; j++) {
		if(!(components[j] instanceof JScrollPane))
		    continue;
		c = ((JScrollPane)components[j]).getViewport().getView();

		if(c ==  null ||
		   !(c instanceof NameTreeTable))
		    continue;

		((NameTreeTable)c).getAlignerTree().setLocale(locale);
	    }
	}
	setTextProperties(locale);
    }

    protected void setTextProperties(Locale locale)
    {
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(NameTreeTableMode.class.getName(), locale, new XMLResourceBundleControl());
	    hierarchiesPaneTabTexts = new String[tableModes.length];
	    for (int i = 0; i < hierarchiesPaneTabTexts.length; i++) {
		hierarchiesPaneTabTexts[i] = resource.getString(tableModes[i].name());
	    }
	}
	catch(MissingResourceException e) {
	}
    }

    public HierarchiesPane<T> swapComparator(HierarchiesPanel<T> hierarchiesPanel) {
	if(hierarchiesPanel == null)
	    return null;

	HierarchiesPane<T> toSwap = null;
	Container container = getParent();
	synchronized(hierarchiesPanel.getTreeLock()) {
	    toSwap = hierarchiesPanel.removeHierarchies();
	    JPopupMenu menu = toSwap.getPopupMenu();
	    if(container != null && toSwap != null) {
		synchronized(container.getTreeLock()) {
		    Component[] components = container.getComponents();
		    int index = -1;
		    int i = 0;
		    for (Component component : components) {
			if(component == this) {
			    index = i;
			    break;
			}
			i++;
		    }
		    if(index != -1) {
			container.remove(index);
			toSwap.setPopupMenu(getPopupMenu());
			container.add(toSwap, index);
		    }
		}
		toSwap.revalidate();
	    }
	    hierarchiesPanel.setHierarchiesPane(this);
	    revalidate();
	}

	return toSwap;
    }

    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	if(source == openMenuItem) {
	    JFrame frame = createCompartorFrame();
	    frame.setSize(frame.getPreferredSize());
	    frame.pack();
	    frame.setVisible(true);
	}
    }

    protected JFrame createCompartorFrame() {
	HierarchiesPanel<T> hierarchiesPanel = new HierarchiesPanel<T>(getLocale());
	HierarchiesPane<T> toSwap = swapComparator(hierarchiesPanel);
	JFrame frame = new JFrame();
	frame.getContentPane().add(hierarchiesPanel);
	//frame.addWindowFocusListener(this);
	/*
	  if(comparators == null)
	  comparators = new ArrayList<Comparator>();
	  if(!comparators.contains(currentComparator))
	  comparators.add(currentComparator);
	*/
	return frame;
    }

    protected void showPopup(MouseEvent event) {
	if(popupMenu != null && event != null)
	    popupMenu.show(event.getComponent(), event.getX(), event.getY());
    }

    public void mouseClicked(MouseEvent event) {
	if(event ==  null)
	    return;

	if(event.isPopupTrigger())
	    showPopup(event);
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
	if(event ==  null)
	    return;

	if(event.isPopupTrigger())
	    showPopup(event);
    }

    public void mouseReleased(MouseEvent event) {
	if(event ==  null)
	    return;

	if(event.isPopupTrigger())
	    showPopup(event);
    }

    public List<NameTreeTable<T>> classify(UnitedNameTreeModel unitedTreeModel)
    {
	final List<NameTreeTableModel> tableModels = NameTreeTableModel.createTableModels(unitedTreeModel);

	if(nameTreeTables == null)
	    nameTreeTables = new ArrayList<NameTreeTable<T>>();
	else
	    nameTreeTables.clear();

	removeAll();

	nameTreeTables.add(add(tableModels, NameTreeTableMode.ASSIGNMENTS));
	nameTreeTables.add(add(tableModels, NameTreeTableMode.INCONSISTENCY));
	nameTreeTables.add(add(tableModels, NameTreeTableMode.SYNONYMS));
	nameTreeTables.add(add(tableModels, NameTreeTableMode.DIFFERENCE));
	nameTreeTables.add(add(tableModels, NameTreeTableMode.MISSINGS));
	nameTreeTables.add(add(tableModels, NameTreeTableMode.COMMONS));

	return nameTreeTables;
    }

    protected NameTreeTable<T> add(List<NameTreeTableModel> tableModels, NameTreeTableMode mode)
    {
	int index = mode.ordinal();
	NameTreeTable<T> nameTreeTable = new NameTreeTable<T>(tableModels.get(index));
	addTab(hierarchiesPaneTabTexts[index], nameTreeTable);
	return nameTreeTable;
    }

}
