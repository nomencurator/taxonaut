/*
 * NamedObjectPanel.java:  an abstract class providing a GUI to
 * NamedObject of Nomencurator
 *
 * Copyright (c) 2004, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Enumeration;
import java.util.EventObject;
import java.util.Hashtable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.CellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolTip;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;

import org.nomencurator.model.NamedObject;

import org.nomencurator.resources.ResourceKey;

import org.nomencurator.gui.swing.table.NamedObjectTableModel;

/**
 * {@code NamedObjectPanel} is an abstract class
 * providing common facility to access to a
 * {@code NamedObject}
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NamedObject
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public abstract class NamedObjectPanel<T extends NamedObject<?, ?>>
    extends JPanel
    implements ActionListener,
	       ChangeListener,
	       MouseListener,
	       TableCellEditor
{
    private static final long serialVersionUID = 399519073295154189L;

    public static int proceedingPaddingWidth = 12;

    public static int succeedingPaddingWidth = 6;

    /**
     * An instance of {@code NamedObject} to be accessed
     * via this {@code NamedObjectPanel}
     */
    protected T namedObject;
    
    /** {@code JPanel} to contain main components */
    protected Container contents;
    
    /**
     * {@code JPanel} to contain buttons and checkbox
     * used to manage the {@code NamedObject}
     */
    protected JPanel buttonsPanel;

    /**
     * {@code JCheckBox} to enable and disable 
     * modification of the {@code NamedObject}
     */
    protected JCheckBox lock;

    /**
     * {@code JButton} to create a
     * {@code NamedObject}
     */
    protected JButton newButton;

    /**
     * {@code JButton} to duplicate the
     * {@code NamedObject}
     */
    protected JButton duplicateButton;

    /**
     * {@code JButton} to delete the
     * {@code NamedObject}
     */
    protected JButton deleteButton;

    /**
     * {@code JButton} to search
     * {@code NamedObject}s
     */
    protected JButton searchButton;

    /** boolean true if the {@code NamedObject} is modified */
    protected boolean modified;

    /** boolean true if the {@code NamedObject} is editable */
    protected boolean editable;

    /** {@code Hashtable} to retain modified {@code NamedObject}s */
    protected static Set<NamedObject<?, ?>> modifiedObjects;

    /** {@code EmptyBorder} used as border of labels */
    protected static EmptyBorder labelBorder = new EmptyBorder(12, 6, 6, 12);

    protected boolean isDialog;

    /**
     * {@code Component} referred to as position origine when this
     * {@code NamedObjectPanek} used as a dialog
     */
    protected Component dialogSource;

    /** {@code String} used as title of the dialog */
    protected String dialogTitle;

    /** {@code CellEditor} handling events */
    protected CellEditorProxy cellEditor;

    protected JTabbedPane tabbedPane;

    protected JTabbedPane relevantTabs;
    protected String relevantTabsTitle;
    protected Box relevantTabsPane;

    protected EventListenerList listeners;

    protected boolean multiLinesToolTip;

    protected JPopupMenu popupMenu;

    protected JDialog dialog;

    /** 
     * Returns {@code EmptyBorder} used as border of labels
     */
    public EmptyBorder getLabelBorder()
    {
	return labelBorder;
    }

    /**
     * Constructs an instance of {@code NamedObjectPanel}
     * for {@code object} using {@code locale}.
     * It is called only from subclasses.
     *
     * @para object {@code NamedObject} to be accessed via
     * this {@code NamedObjectPanel}
     * @para locale {@code Locale} to be used to localize
     * the GUI
     */
    protected NamedObjectPanel(T object,
			       Locale locale)
    {
	this(object, locale, false);
    }

    /**
     * Constructs an instance of {@code NamedObjectPanel}
     * for {@code object} using {@code locale}.
     * It is called only from subclasses.
     *
     * @para object {@code NamedObject} to be accessed via
     * this {@code NamedObjectPanel}
     * @para locale {@code Locale} to be used to localize
     * the GUI
     * @para dialogSource {@code Component} to be used to
     * place a {@code JOptionDialog} containig this
     * {@code NamedObjectPanel} or null if it not used
     * as a dialog
     */
    protected NamedObjectPanel(T object,
			       Locale locale, boolean isDialog)
    {
	super(new BorderLayout());
	multiLinesToolTip = false;
	createComponents();
	setLocale(locale);
	layoutComponents();
	setNamedObject(object);
	modified = false;
	//setDialogSource(dialogSource);
	setDialog(isDialog);
	setToolTipText("");
    }

    /**
     * Creates components of this {@code NamedObjectPanel}
     */
    protected void createComponents()
    {
	createContents();
	buttonsPanel = new JPanel(new FlowLayout());
	lock = new JCheckBox();
	lock.addChangeListener(this);
	newButton = new JButton();
	duplicateButton = new JButton();
	deleteButton = new JButton();
	addMouseListener(this);
    };

    /**
     * Creates main components of this {@code NamedObjectPanel}
     */
    protected void createContents()
    {
	contents = new Box(BoxLayout.Y_AXIS);
    }

    /**
     * Creates JTabbedPane for relevant information
     */
    protected void createRelevantTabbedPane()
    {
	relevantTabs = new JTabbedPane(JTabbedPane.BOTTOM);
	relevantTabsPane = new Box(BoxLayout.Y_AXIS);
	relevantTabsPane.add(relevantTabs);
	relevantTabsPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()));
    }

    /**
     * Layouts components of this {@code NamedObjectPanel}
     */
    protected void layoutComponents()
    {
	if(contents != null)
	    add(contents, BorderLayout.CENTER);
	if(buttonsPanel != null) {
	    buttonsPanel.add(lock);
	    buttonsPanel.add(newButton);
	    buttonsPanel.add(duplicateButton);
	    buttonsPanel.add(deleteButton);
	    add(buttonsPanel, BorderLayout.SOUTH);
	}
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
	/*
	if(locale.equals(getLocale()))
	    return;
	*/
	super.setLocale(locale);

	String newText = ResourceKey.NEW;
	String duplicateText = ResourceKey.DUPLICATE;
	String deleteText = ResourceKey.DELETE;
	String lockText = ResourceKey.LOCK;
	relevantTabsTitle = ResourceKey.RELEVANT_TABS;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    
	    newText =
		resource.getString(newText);

	    duplicateText = 
		resource.getString(duplicateText);

	    lockText =
		resource.getString(lockText);
	    deleteText = 
		resource.getString(deleteText);
	    
	    relevantTabsTitle = 
		resource.getString(relevantTabsTitle);
	    
	}
	catch(MissingResourceException e) {

	}

	if(newButton != null) {
	    if(newText == null)
		newText = ResourceKey.NEW;
	    newButton.setText(newText);
	}

	if(duplicateButton != null) {
	    if(duplicateText == null)
		duplicateText = ResourceKey.DUPLICATE;
	    duplicateButton.setText(duplicateText);
	}

	if(deleteButton != null) {
	    if(deleteText == null)
		deleteText = ResourceKey.DELETE;
	    deleteButton.setText(deleteText);
	}

	if(lock != null) {
	    if(lockText == null)
		lockText = ResourceKey.LOCK;
	    lock.setText(lockText);
	}

	if(buttonsPanel != null) {
	    ComponentOrientation orientation = 
		getComponentOrientation();
	    FlowLayout f = 
		(FlowLayout)buttonsPanel.getLayout();
	    if(orientation.isHorizontal() &&
	       !orientation.isLeftToRight())
		f.setAlignment(FlowLayout.LEFT);
	    else
		f.setAlignment(FlowLayout.RIGHT);
	}

	if(relevantTabsPane != null) {
	    TitledBorder border = 
		(TitledBorder)relevantTabsPane.getBorder();
	    if(border != null) {
		border.setTitle(relevantTabsTitle);
	    }

	    int tabCount = relevantTabs.getTabCount();
	    for(int i = 0; i < tabCount; i++) {
		relevantTabs.getComponentAt(i).setLocale(locale);
	}


	}
    }

    /**
     * Returns true if the {@code NamedObject}
     * is modified.
     */
    public boolean isModified()
    {
	return modified;
    }

    /**
     * Returns true if the {@code NamedObject}
     * is modified.
     */
    protected void setModified(boolean state)
    {
	modified = state;
    }

    /**
     * Returns true if the {@code NamedObject}
     * is editable.
     */
    public boolean isEditable()
    {
	return editable;
    }

    /**
     * Sets editable state to {@code editable}
     * and takes care of components
     *
     * @param editable true to be editable
     */
    protected void setEditable(boolean editable)
    {
	/*
	if(this.editable == editable ||
	   !namedObject.isEditable())
	    return;
	*/
	if(contents != null) {
	}
	lock.setSelected(!editable);
	deleteButton.setEnabled(editable);
    }

    /**
     * Sets specified {@code object} to display
     *
     * @param object {@code NamedObject} to be displayed
     */
    public void setNamedObject(T object)
    {
	if(namedObject == object) {
	    loadValues();
	    return;
	}

	if(namedObject != null && modified) {
	    if(modifiedObjects == null)
		modifiedObjects = new HashSet<NamedObject<?, ?>>();
	    setValues();
	    modifiedObjects.add(namedObject);
	}

	modified = false;
	namedObject = object;
	if(object != null) {
	    editable = object.isEditable();
	    //lock.setSelected(!editable);
	    setEditable(editable);
	    loadValues();
	    setToolTipText(object.getSummary());
	}
	else
	    setToolTipText(null);

	fireStateChanged();
    }

    /**
     * Returns {@code NamedObject} under display
     *
     * @return NamedObject under display
     */
    public T getNamedObject()
    {
	return namedObject;
    }

    /**
     * Creates and sets a new {@code NamedObject}
     */
    public void setNewNamedObject()
    {
	setNamedObject(createNamedObject());
    }

    /**
     * Creates a new {@code NamedObject}
     *
     * @return NamedObject created
     */
    protected abstract T createNamedObject();


    /**
     * Sets values in GUI components to the 
     * {@code NamedObject} if the {@code namedObject} is
     * not null and it is modified.
     *
     * @return true if values were set, otherwise false in 
     */
    protected boolean setValues()
    {
	if(namedObject == null || !isModified())
	    return false;

	namedObject.setEditable(!lock.isSelected());
	return true;
    }

    /**
     * Load values of the {@code NamedObject} to
     * GUI components if the {@code namedObject} is
     * not null.
     *
     * @return true if values were loaded, otherwise false 
     */
    protected boolean loadValues()
    {
	if(namedObject == null)
	    return false;

	lock.setSelected(!namedObject.isEditable());
	return true;
    }

    /**
     * Returns {@code Iterator} of modified
     * {@code NamedObject}s, or null if
     * no {@code NamedObject} was modified
     *
     * @return Iterator of modified {@code NamedObject}s,
     * or null if no {@code NamedObject} was modified
     */
    public Iterator<NamedObject<?, ?>> getModifiedObjects()
    {
	if(modifiedObjects != null)
	    return modifiedObjects.iterator();

	return null;
    }

    /**
     * Clears list of modified
     * {@code NamedObject}s
     */
    public void clearModifiedObjects()
    {
	if(modifiedObjects != null)
	    modifiedObjects.clear();
    }

    protected abstract T duplicate();

    public void stateChanged(ChangeEvent e)
    {
	Object source = e.getSource();
	if(source == lock) {
	    setEditable(!lock.isSelected());
	}
    }

    /**
     * Layouts {@code component} to {@code container}
     * using {@code layout} under {@code constaraints}
     */
    static void layoutComponent(Container container,
				Component component,
				GridBagLayout layout,
				GridBagConstraints constraints)
    {
	layout.setConstraints(component, constraints);
	container.add(component);
    }


    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    static boolean layoutComponent(Container container,
				   JLabel label,
				   Component component,
				   GridBagLayout layout,
				   GridBagConstraints constraints)
    {
	return layoutComponent(container, label, component,
			       layout, constraints,
			       1, constraints.fill,
			       0, succeedingPaddingWidth);
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    static boolean layoutComponent(Container container,
				   JLabel label,
				   Component component,
				   GridBagLayout layout,
				   GridBagConstraints constraints,
				   int width)
    {
	return layoutComponent(container, label, component,
			       layout, constraints,
			       width,
			       GridBagConstraints.HORIZONTAL,
			       0, succeedingPaddingWidth);
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    static boolean layoutComponent(Container container,
				   JLabel label,
				   Component component,
				   GridBagLayout layout,
				   GridBagConstraints constraints,
				   int width,
				   int filling,
				   int proceedingPadding,
				   int succeedingPadding)
    {
	if(label == null &&
	   component == null)
	    return false;
	int gridwidth = constraints.gridwidth;
	int fill = constraints.fill;
	int anchor = constraints.anchor;

	boolean layouted = false;
	
	if(label != null &&
	   component != null) {
	    if(proceedingPadding > 0)
		layoutComponent(container, Box.createHorizontalStrut(proceedingPadding), layout, constraints);
	}

	if(label != null) {
	    constraints.anchor = GridBagConstraints.LINE_END;
	    layoutComponent(container, label, layout, constraints);
	    layouted |= true;
	}
	if(label != null &&
	   component != null) {
	    if(succeedingPadding > 0)
		layoutComponent(container, Box.createHorizontalStrut(succeedingPadding), layout, constraints);
	}

	if(component != null) {
	    constraints.anchor = GridBagConstraints.LINE_START;
	    constraints.gridwidth = width;
	    constraints.fill = filling;
	    layoutComponent(container, component, layout, constraints);
	    layouted |= true;
	}

	constraints.anchor = anchor;
	constraints.fill = fill;
	constraints.gridwidth = gridwidth;
	
	return layouted;
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    static boolean layoutComponent(Container container,
				   JLabel label,
				   JCheckBox checkbox,
				   GridBagLayout layout,
				   GridBagConstraints constraints)
    {
	return layoutComponent(container, label, checkbox,
			       layout, constraints,
			       0, 0);
    }

    /**
     * Layouts {@code component} to {@code container}
     * with {@code label} using {@code layout}
     * under {@code constaraints}
     */
    static boolean layoutComponent(Container container,
				   JLabel label,
				   JCheckBox checkbox,
				   GridBagLayout layout,
				   GridBagConstraints constraints,
				   int proceedingPadding,
				   int succeedingPadding)
    {
	if(label == null &&
	   checkbox == null)
	    return false;

	int anchor = constraints.anchor;

	boolean layouted = false;

	
	if(checkbox != null) {
	    constraints.anchor = GridBagConstraints.LINE_END;
	    layoutComponent(container, checkbox, layout, constraints);
	    layouted |= true;
	}

	if(label != null) {
	    if(checkbox != null &&
	       proceedingPadding > 0)
		layoutComponent(container, Box.createHorizontalStrut(proceedingPadding), layout, constraints);

	    constraints.anchor = GridBagConstraints.LINE_START;
	    layoutComponent(container, label, layout, constraints);
	    layouted |= true;

	    if(checkbox != null && succeedingPadding > 0)
		layoutComponent(container, Box.createHorizontalStrut(succeedingPadding), layout, constraints);
	}

	constraints.anchor = anchor;
	
	return layouted;
    }

    public void setCellEditor(CellEditorProxy editor)
    {
	if(cellEditor != null)
	    cellEditor.setCellEditor(null);
	cellEditor = editor;
	if(cellEditor != null)
	    cellEditor.setCellEditor(this);
    }

    public CellEditor getCellEditor()
    {
	return cellEditor;
    }

    public boolean isDialog()
    {
	return isDialog;
    }

    public void setDialog(boolean dialog)
    {
	isDialog = dialog;
    }

    public void setDialogSource(Component component)
    {
	if(cellEditor != null &&
	   component == null) {
	    setCellEditor(null);
	}
	else if (cellEditor == null &&
		 component != null)
	    setCellEditor(new CellEditorProxy());

	dialogSource = component;
    }

    public Component getDialogSource()
    {
	return dialogSource;
    }

    public String getDialogTitle()
    {
	return dialogTitle;
    }

    public void addCellEditorListener(CellEditorListener listener)
    {
	if(cellEditor == null)
	    cellEditor = new CellEditorProxy(this);
	cellEditor.addCellEditorListener(listener);
    }

    public void cancelCellEditing()
    {
	if(cellEditor == null)
	    cellEditor = new CellEditorProxy(this);
	cellEditor.cancelCellEditing();
    }

    public Object getCellEditorValue()
    {
	return getNamedObject();
    }

    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
    {
	return null;
    }

    protected boolean showOptionDialog(Component source, EventObject event)
    {
	return showOptionDialog(source, this, dialogTitle, event);
    }

    @SuppressWarnings("unchecked")
    protected <E extends NamedObject<?, ?>>boolean showOptionDialog(Component source,
								    NamedObjectPanel<E> dialog,
								    String title,
								    EventObject event)
    {
	if(source instanceof JTable) {
	    JTable table = (JTable)source;
	    if(event instanceof MouseEvent) {
		TableModel model = ((JTable)source).getModel();
		if(model instanceof NamedObjectTableModel) {
		    dialog.setNamedObject((E)((NamedObjectTableModel)model).getValueAt(table.rowAtPoint(((MouseEvent)event).getPoint()),
												 -1));
		}
	    }
	}

	if(JOptionPane.OK_OPTION == 
	     JOptionPane.showOptionDialog(source,
					  dialog,
					  title,
					  JOptionPane.OK_CANCEL_OPTION,
					  JOptionPane.PLAIN_MESSAGE,
					  null,
					  null,
					  null)) {
	    dialog.setValues();
	    return true;
	}
	return false;
    }



    public boolean isCellEditable(EventObject event)
    {
	if(cellEditor == null)
	    cellEditor = new CellEditorProxy(this);
	//Component source = dialogSource;
	Component source = null;
	Object eventSource = event.getSource();
	if(eventSource instanceof Component)
	    source = (Component)eventSource;

	return showOptionDialog(source, event);

    }

    public void removeCellEditorListener(CellEditorListener listener)
    {
	if(cellEditor == null)
	    cellEditor = new CellEditorProxy(this);
	cellEditor.removeCellEditorListener(listener);
    }

    public boolean shouldSelectCell(EventObject event)
    {
	return false;
    }

    public boolean stopCellEditing()
    {
	return true;
    }

    protected AnnotationPanel createAnnotationPanel()
    {
	AnnotationPanel annotationPanel =
	    new AnnotationPanel();
	annotationPanel.setModified(true);
	return annotationPanel;
    }

    /*
    protected NameUsagePanel createNameUsagePanel()
    {
	NameUsagePanel nameUsagePanel = new NameUsagePanel();
	nameUsagePanel.setModified(true);
	return nameUsagePanel;
    }
    */

    public boolean isMultiLinesToolTip()
    {
	return multiLinesToolTip;
    }

    public void setMultiLinesToolTip(boolean multiLines)
    {
	multiLinesToolTip = multiLines;
    }

    public JToolTip createToolTip()
    {
	if(multiLinesToolTip)
	    return new MultiLinesToolTip(/*this*/);
	return super.createToolTip();
    }

    public String getToolTipText()
    {
	T namedObject = getNamedObject();
	if(namedObject != null)
	    return namedObject.getSummary();
	return null;
    }

    public abstract void mouseClicked(MouseEvent event);

    public void mouseEntered(MouseEvent event)
    {
    }

    public void mouseExited(MouseEvent event)
    {
    }
     
    public void mousePressed(MouseEvent event)
    {
	if(event.isPopupTrigger()) {
	    if(popupMenu == null)
		popupMenu = createPopupMenu();
	    if(popupMenu != null)
		popupMenu.show(this, event.getX(), event.getY());
	}
    }
     
    public void mouseReleased(MouseEvent event) 
    {
	if(event.isPopupTrigger()) {
	    if(popupMenu != null &&
	       popupMenu.isVisible())
		popupMenu.setVisible(false);
	}
    }

    protected void setTabbedPane(JTabbedPane tabbedPane)
    {
	if(this.tabbedPane == tabbedPane)
	    return;
	
	this.tabbedPane = tabbedPane;
    }

    public JTabbedPane getTabbedPane()
    {
	return tabbedPane;
    }

    public void addChangeListener(ChangeListener listener)
    {
	if(listeners == null)
	    listeners = new EventListenerList();
	synchronized(listeners) {
	    listeners.add(ChangeListener.class, listener);
	}
    }

    public void removeChangeListener(ChangeListener listener)
    {
	if(listeners == null)
	    return;
	synchronized(listeners) {
	    listeners.remove(ChangeListener.class, listener);
	}
    }

    protected ChangeEvent changeEvent;
    protected void fireStateChanged()
    {
	if(listeners == null)
	    return;

	synchronized(listeners) {
	    ChangeListener[] targets = 
		listeners.getListeners(ChangeListener.class);
	    for(ChangeListener target : targets) {
		if(changeEvent == null)
		    changeEvent = new ChangeEvent(this);
                target.stateChanged(changeEvent);
	    }
	}
    }

    protected JMenuItem showDetail;

    protected JPopupMenu createPopupMenu()
    {
	JPopupMenu popup = new JPopupMenu();
	showDetail = new JMenuItem("Show detail...");
	showDetail.addActionListener(this);
	popup.add(showDetail);
	return popup;
    }

    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	if(source == showDetail) {
	    Component p = this;
	    while(p != null &&
		  (!(p instanceof Frame)) &&
		  (!(p instanceof Dialog))) {
		p = p.getParent();
	    }
	    if(p == null)
		return;
	    JDialog dialog = null;
	    if (p instanceof Frame)
		dialog = new JDialog((Frame)p, getDetailDialogTitle());
	    else if (p instanceof Dialog) {
		dialog = new JDialog((Dialog)p, getDetailDialogTitle());
	    }
	    else {
	    }
	    dialog.add(new JScrollPane(new JTextArea(getToolTipText(), 25, 40))); //FIXME
	    dialog.setSize(dialog.getPreferredSize());
	    dialog.pack();
	    dialog.setVisible(true);
	}
    }

    protected String getDetailDialogTitle()
    {
	T object = getNamedObject();
	if(object != null)
	    return object.getLiteral();
	return "";
    }

    protected JDialog createDailog()
    {
	JDialog dialog = new JDialog();
	return dialog;
    }

}
