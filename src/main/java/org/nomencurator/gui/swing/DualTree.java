/*
 * DualTree.java:  a TreeUI with node alignment between
 * trees
 *
 * Copyright (c) 2022 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.gui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.KeyEventPostProcessor;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.Method;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Hashtable;
import java.util.Vector;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DropMode;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.JTree.DynamicUtilTreeNode;
import javax.swing.LookAndFeel;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;

import javax.swing.plaf.basic.BasicTreeUI.TreeIncrementAction;

import javax.swing.plaf.synth.SynthStyleFactory;

import javax.swing.text.Position;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.RowMapper;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.Flippable.Orientation;
import org.nomencurator.gui.swing.FlippableTree;
import org.nomencurator.gui.swing.LookAndFeelUtilities;

import org.nomencurator.gui.swing.plaf.DualTreeUI;
import org.nomencurator.gui.swing.plaf.FlippableTreeUI;
import org.nomencurator.gui.swing.plaf.FlippableTreeUIUtilities;

import org.nomencurator.gui.swing.plaf.basic.BasicDualTreeUI;

import org.nomencurator.gui.swing.plaf.synth.SynthFlippableTreeUI;
import org.nomencurator.gui.swing.plaf.synth.SynthLookAndFeel;

import org.nomencurator.gui.swing.plaf.nimbus.NimbusLookAndFeel;

import org.nomencurator.gui.swing.tree.DualTreeModel;
import org.nomencurator.gui.swing.tree.DualTreeSelectionModel;
import org.nomencurator.gui.swing.tree.FlippableFixedHeightLayoutCache;
import org.nomencurator.gui.swing.tree.FlippableVariableHeightLayoutCache;

/**
 * <CODE>DualTree</CODE> provides a {@code Flippable} <code>JTree</code>
 *
 * @version 	01 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class DualTree
    extends JTree
    implements TreeExpansionListener, PropertyChangeListener, RowMapper, TreeSelectionListener, KeyEventDispatcher, KeyEventPostProcessor
{
    public void updateUI()
    {
	System.out.println("updateUI() on " + UIManager.getUI(this));
	super.updateUI();
    }

    private static final String uiClassID = "DualTreeUI";

    // public String getUIClassID() { return uiClassID; }


    static {
	// UIManager.put(uiClassID, BasicDualTreeUI.class.getCanonicalName());	    	
    }
    
    protected FlippableTree upperTree;

    protected TreeSelectionModel upperSelectionModel;

    protected FlippableTree lowerTree;

    protected TreeSelectionModel lowerSelectionModel;

    protected DualTreeSelectionModel dualSelectionModel;    

    protected FlippableTree configureUpperTreeInProgress;

    protected FlippableTree configureLowerTreeInProgress;

    protected  SpringLayout layout;

    protected boolean alignRootVerticalLeg = false;

    protected boolean doTreeTranslation = false;

    protected static Border zeroBorder = BorderFactory.createEmptyBorder();

    protected static Dimension zeroDimension = new Dimension();

    protected Dimension upperPreferredSize;

    protected Dimension lowerPreferredSize;
    
    /**
     * Determines of which root node is visible, upper or lower tree.
     * The root node of the lower tree is visible if {@true}, otherwise upper tree.
     * A {@code DualTree} becomes harder to recognise withot root node.
     */
    protected boolean lowerRootVisible = lowerRootVisibleDefault;

    public static final int MODIFIER_DOWN_MASK = InputEvent.ALT_DOWN_MASK
	| InputEvent.ALT_GRAPH_DOWN_MASK
	| InputEvent.CTRL_DOWN_MASK
	| InputEvent.META_DOWN_MASK
	| InputEvent.SHIFT_DOWN_MASK
	;
    
    public static final int MOUSE_DOWN_MASK = InputEvent.BUTTON1_DOWN_MASK
	| InputEvent.BUTTON2_DOWN_MASK
	| InputEvent.BUTTON3_DOWN_MASK
	;

    public boolean dispatchKeyEvent​(KeyEvent keyEvent)
    {
	if (KeyEvent.KEY_PRESSED != keyEvent.getID()) return false;
	
	Object source = keyEvent.getSource();
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();
	int keyCode = keyEvent.getKeyCode();
	boolean modified = (0 != (MODIFIER_DOWN_MASK & keyEvent.getModifiersEx()));
	if (source == lower
	    && (KeyEvent.VK_UP == keyCode || KeyEvent.VK_KP_UP == keyCode)
	    && null != lower.getSelectionRows()
	    && 0 == lower.getSelectionRows()[0]
	    && (modified || (1 == lower.getSelectionCount()))
	    ){
	    synchronized(upper) {
		    if(!modified)
			upper.clearSelection();
		    KeyboardFocusManager.getCurrentKeyboardFocusManager​().clearGlobalFocusOwner();
		    upper.requestFocusInWindow();
		    KeyboardFocusManager.getCurrentKeyboardFocusManager​().redispatchEvent​(upper, keyEvent);
	    }
	    if(!modified)		
		lower.clearSelection();
	    lower.setLeadSelectionPath(null);			
	    lower.setAnchorSelectionPath(null);
	    return true;
	}
	else if (source == upper
		 && (KeyEvent.VK_DOWN == keyCode || KeyEvent.VK_KP_DOWN == keyCode)
		 && null != upper.getSelectionRows()
		 && upper.getRowCount() - 1 == upper.getSelectionRows()[0]
		 && (modified ||(1 == upper.getSelectionCount()))
		 ) {
	    synchronized(lower) {
		if (!modified)
		    lower.clearSelection();
		KeyboardFocusManager.getCurrentKeyboardFocusManager​().clearGlobalFocusOwner();
		lower.requestFocusInWindow();
		KeyboardFocusManager.getCurrentKeyboardFocusManager​().redispatchEvent​(lower, keyEvent);
	    }
	    if (!modified)
		upper.clearSelection();
	    upper.setLeadSelectionPath(null);			
	    upper.setAnchorSelectionPath(null);
	    return true;
	}
	return false;
    }

    public boolean postProcessKeyEvent​(KeyEvent keyEvent)
    {
	return true;
    }

    /**
     * Defalut value of {@code lowerRootVisible}
     */
    protected static boolean lowerRootVisibleDefault = true;


    /**
     * Sets the status of which root node is visib, upper or lower tree.
     *
     * @param lower {@code true} if the root of the lwoer tree is visible
     */
    public void setLowerRootVisible(boolean lower)
    {
	this.lowerRootVisible = lower;
    }

    /**
     * Returns {@code true} if the root node of lower tree is visible,
     * othewise the root of upper tree is visible.
     *
     * @return true if the root of the lower tree is visible.
     */
    public boolean isLowerRootVisible()
    {
	return lowerRootVisible;
    }

    /**
     * Sets default value of lowerRootVisible determinining which of the node is visible, upper or lower tree.
     *
     * @param lower  {@code true} if the root of the lwoer tree to be visible by default.
     */
    public static void setLowerRootVisibleDefalut(boolean lower)
    {
	lowerRootVisibleDefault = lower;
    }

    /**
     * Returns the default value of lowerRootVisible determinining which of the node is visible, upper or lower tree.
     *
     * @return  {@code true} if the root of the lwoer tree to be visible by default.
     */
    public static boolean isLowerRootVisibleDefault()
    {
	return lowerRootVisibleDefault;
    }

    /**
     * {@code true} to show root handles.
     */
    protected boolean showsRootHandles = showsRootHandlesDefault;

    /**
     * Default valie of {@code setShowsRootHandles}
     */
    protected static boolean showsRootHandlesDefault = true;

    /**
     * Sets the status to show the part of vertial line leg attached to the root node.
     *
     * @param toShow true to show the part of the line
     */
    public void setShowsRootHandles(boolean toShow)
    {
	this.showsRootHandles = toShow;
    }

    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @return true if it shows the part of the line
     */
    public boolean isShowsRootHandles()
    {
	return showsRootHandles;
    }

    /**
     * Sets the status to show the part of vertial line leg attached to the root node.
     *
     * @param toShow true to show the part of the line
     */
    public static void setShowsRootHandlesDefalut(boolean toShow)
    {
	showsRootHandlesDefault = toShow;
    }

    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @return true if it shows the part of the line
     */
    public static boolean isShowsRootHandlesDefault()
    {
	return showsRootHandlesDefault;
    }

    /**
     * {@code true} if the part of vertical line attached to the root node
     * even if the root is invisible.
     */
    protected boolean showsRootVerticalLineLeg = showsRootVerticalLineLegDefault;

    /**
     * Defalut value of {@code showsRootVerticalLineLeg}
     */
    protected static boolean showsRootVerticalLineLegDefault = true;

    /**
     * Sets the status to show the part of vertial line leg attached to the root node.
     *
     * @param toShow true to show the part of the line
     */
    public void setShowsRootVerticalLineLeg(boolean toShow)
    {
	this.showsRootVerticalLineLeg = toShow;
    }

    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @return true if it shows the part of the line
     */
    public boolean isShowsRootVerticalLineLeg()
    {
	return showsRootVerticalLineLeg;
    }

    /**
     * Sets the status to show the part of vertial line leg attached to the root node.
     *
     * @param toShow true to show the part of the line
     */
    public static void setShowsRootVerticalLineLegDefalut(boolean toShow)
    {
	showsRootVerticalLineLegDefault = toShow;
    }

    /**
     * Returns the status to show the part of vertial line leg attached to the root node.
     *
     * @return true if it shows the part of the line
     */
    public static boolean isShowsRootVerticalLineLegDefault()
    {
	return showsRootVerticalLineLegDefault;
    }

    public static FlippableTree createFlippableTree()
    {
	FlippableTree.defaultLineProperties.setLineProperties();
	return new FlippableTree();
    }

    public static FlippableTree createFlippableTree(Object source)
    {
	FlippableTree.defaultLineProperties.setLineProperties();
	FlippableTree tree = null;
	if (source instanceof FlippableTree)
	    tree = (FlippableTree)source;	    		
	else if (source instanceof Hashtable<?,?>)
	    tree = new FlippableTree((Hashtable<?,?>) source);
	else if (source instanceof Object[])
	    tree = new FlippableTree((Object[])source);
	else if (source instanceof TreeModel)
	    tree = new FlippableTree((TreeModel)source);
	else if (source instanceof TreeNode)
	    tree = new FlippableTree((TreeNode)source);
	else if (source instanceof Vector<?>)
	    tree = new FlippableTree((Vector<?>)source);
	else
	    tree = new FlippableTree();
	//tree.setBorder(zeroBorder);
	return tree;
    }

    public static FlippableTree createFlippableTree(TreeNode root, boolean asksAllowsChildren)
    {
	FlippableTree.defaultLineProperties.setLineProperties();
	return new FlippableTree(root, asksAllowsChildren);
    }
						
    /**
     * Constructs {@code DualTree} using default {@code FlippableTree}s.
     */
    public DualTree()
    {
	this(createFlippableTree(), createFlippableTree());	
    }

    public DualTree(Hashtable<?,?> upper, Hashtable<?,?> lower)
    {
	this(createFlippableTree(upper), createFlippableTree(lower));		
    }

    public DualTree(Object[] upper, Object[] lower)
    {
	this(createFlippableTree(upper), createFlippableTree(lower));
    }

    public DualTree(TreeModel upper, TreeModel lower)
    {
	this(createFlippableTree(upper), createFlippableTree(lower));
    }

    public DualTree(TreeNode upper, TreeNode lower)
    {
	this(createFlippableTree(upper), createFlippableTree(lower));
    }

    public DualTree(TreeNode upper, boolean upperAsksAllowsChildren, TreeNode lower, boolean lowerAsksAllowsChildren)
    {
	this(createFlippableTree(upper, upperAsksAllowsChildren), createFlippableTree(lower, lowerAsksAllowsChildren));
    }

    public DualTree(Vector<?> upper, Vector<?> lower)
    {
	this(createFlippableTree(upper), createFlippableTree(lower));
    }

    /**
     * Constructs {@code DualTree} using given {@code upperTree} and {@code lowerTree}.
     *
     * @param upperTree {@code FlippableTree} to be displayed as upper part of the {@code DualTree}
     * @param lowerTree {@code FlippableTree} to be displayed as lower part of the {@code DualTree}
     */
    public DualTree(FlippableTree upperTree, FlippableTree lowerTree)
    {
	//super(getNullTreeModel());
	//setSelectionModel(createTreeSelectionModel());
	super(createTreeModel(lowerTree.getModel(), upperTree.getModel()));
	setSelectionModel(createTreeSelectionModel());
	setTrees(upperTree, lowerTree);
	
	KeyboardFocusManager.getCurrentKeyboardFocusManager​().addKeyEventDispatcher(this);
	KeyboardFocusManager.getCurrentKeyboardFocusManager​().addKeyEventPostProcessor(this);	
    }

    public  static  TreeNode createRootNode(Object value)
    {
	return createRootNode(value, value.toString());
    }

    public  static  TreeNode createRootNode(Object value, Object rootNodeKey)    
    {
	if ((value instanceof Object[]) || (value instanceof Hashtable) || (value instanceof Vector)) {
	    DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootNodeKey);
	    DynamicUtilTreeNode.createChildren(root, value);
	    return root;
	}
	else
	    return new DynamicUtilTreeNode(rootNodeKey, value);
    }

    public static TreeModel createTreeModel(Object value)
    {
	return createTreeModel(value, value.toString());
    }

    public static TreeModel createTreeModel(Object value, Object rootNodeKey)
    {
	return new DefaultTreeModel(createRootNode(value, rootNodeKey));
    }
    /*
    public static DualTreeModel createDualTreeModel()
    {
    }
    */

    protected void cretateComponents()
    {
    }

    /*
    protected TreeModel createTreeModel()
    {
	return new DualTreeModel();
    }
    */

    protected DualTreeSelectionModel createTreeSelectionModel()
    {
	return new DualTreeSelectionModel(this);
    }

    public DualTreeSelectionModel getDualTreeSelectionModel()
    {
	return (selectionModel instanceof DualTreeSelectionModel) ?
	    (DualTreeSelectionModel) selectionModel : null;
    }
    

    protected void listen(TreeSelectionModel listener)
    {
	if (listener == null ||
	    !(listener instanceof DualTreeSelectionModel)) return;

	DualTreeSelectionModel dualModel =
	    (DualTreeSelectionModel)listener;
	    
	TreeSelectionModel model = null;
	FlippableTree tree = getUpperTree();
	if (tree != null) {
	    model = tree.getSelectionModel();
	    if (model != null) {
		//model.addTreeSelectionListener(dualModel);
		//model.addPropertyChangeListener(dualModel);
	    }	    
	}
	tree = getLowerTree();
	if (tree != null) {
	    model = tree.getSelectionModel();
	    if (model != null) {
		//model.addTreeSelectionListener(dualModel);
		//model.addPropertyChangeListener(dualModel);
	    }	    
	}
    }

    protected void unlisten(TreeSelectionModel listener)
    {
	if (listener == null ||
	    !(listener instanceof DualTreeSelectionModel)) return;

	DualTreeSelectionModel dualModel =
	    (DualTreeSelectionModel)listener;
	    
	TreeSelectionModel model = null;
	FlippableTree tree = getUpperTree();
	if (tree != null) {
	    model = tree.getSelectionModel();
	    if (model != null) {
		model.removeTreeSelectionListener(dualModel);
		model.removePropertyChangeListener(dualModel);
	    }	    
	}
	tree = getLowerTree();
	if (tree != null) {
	    model = tree.getSelectionModel();
	    if (model != null) {
		model.removeTreeSelectionListener(dualModel);
		model.removePropertyChangeListener(dualModel);
	    }	    
	}
    }

    protected SpringLayout createLayout()
    {
	return new SpringLayout();
    }
    
    protected void setupConstraints()
    {
	if (layout == null) {
	    layout = createLayout();
	    setLayout(layout);
	}
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();
	layout.putConstraint( SpringLayout.NORTH, upper,  0, SpringLayout.NORTH, this);
	layout.putConstraint(SpringLayout.NORTH, lower, 0, SpringLayout.SOUTH, upper);
	layout.putConstraint(SpringLayout.WEST, upper, 0, SpringLayout.WEST, this);
	layout.putConstraint(SpringLayout.WEST, lower, 0, SpringLayout.WEST, this);		
    }

    protected void addComponents()
    {
    }

    protected void configureTree(FlippableTree tree)
    {
	tree.setShowsRootVerticalLineLeg(isShowsRootVerticalLineLeg());
    }

    protected void configureUpperTree(FlippableTree tree)
    {
	configureTree(tree);
	tree.setOrientation(Orientation.VERTICAL);
	tree.setRootVisible(!isLowerRootVisible());	
	tree.setShowsRootHandles(isLowerRootVisible());
    }

    protected void configureLowerTree(FlippableTree tree)
    {
	configureTree(tree);
	tree.setOrientation(Orientation.NONE);
	tree.setRootVisible(isLowerRootVisible());
	tree.setShowsRootHandles(!isLowerRootVisible());
    }

    protected void setTrees(FlippableTree upper, FlippableTree lower)
    {
	//unlisten(getSelectionModel());
	if (upperTree != null) {
	    upperTree.removeTreeExpansionListener(this);
	    upperTree.removePropertyChangeListener(this);
	    upperTree.setSelectionModel(upperSelectionModel);
	}
	if (lowerTree != null) {
	    lowerTree.removeTreeExpansionListener(this);
	    lowerTree.removePropertyChangeListener(this);
	    upperTree.setSelectionModel(lowerSelectionModel);	    
	}

	synchronized (getTreeLock()) {

	    if (layout != null)	 {   
		if (upperTree != null)
		    layout.removeLayoutComponent(upperTree);
		if (lowerTree != null)
		    layout.removeLayoutComponent(lowerTree);		
	    }

	    super.removeAll();

	    if (upper != null && lower != null && upper != lower) {
		configureUpperTree(upper);
		configureLowerTree(lower);

		upper.addTreeExpansionListener(this);
		upper.addPropertyChangeListener(this);
		upper.setFocusTraversalPolicy(null);
		/*
		ActionMap upperActions = upper.getActionMap();
		Action upperAction = upperActions.get(SELECT_NEXT);
		ActionMap lowerActions = lower.getActionMap();
		Action lowerAction = lowerActions.get(SELECT_PREVIOUS);		
		upperActions.put(SELECT_NEXT,
				 new InterTreeIncrementAction(1, SELECT_NEXT,upperAction, lowerAction, this));
		*/
		upper.setVerifyInputWhenFocusTarget(false);

		lower.addTreeExpansionListener(this);
		lower.addPropertyChangeListener(this);
		lower.setFocusTraversalPolicy(null);
		//lower.addTreeSelectionListener(this);
		/*
		lowerActions.put(SELECT_PREVIOUS,
				 new InterTreeIncrementAction(-1, SELECT_PREVIOUS, lowerAction, upperAction, this));
		*/
		upperTree = upper;
		lowerTree = lower;

		/*
		SwingUtilities.replaceUIActionMap(upperTree,null);
		SwingUtilities.replaceUIInputMap(upperTree,
						  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						  null);
		SwingUtilities.replaceUIInputMap(upperTree,
						  JComponent.WHEN_FOCUSED,
						  null);

		SwingUtilities.replaceUIActionMap(lowerTree,null);
		SwingUtilities.replaceUIInputMap(lowerTree,
						  JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT,
						  null);
		SwingUtilities.replaceUIInputMap(lowerTree,
						  JComponent.WHEN_FOCUSED,
						  null);
		*/

		/*
		upperSelectionModel = upperTree.getSelectionModel();
		lowerSelectionModel = lowerTree.getSelectionModel();		
		TreeSelectionModel selectionModel = getSelectionModel();		
		upperTree.setSelectionModel(selectionModel);
		lowerTree.setSelectionModel(selectionModel);		
		*/

		DualTreeSelectionModel model = getDualTreeSelectionModel();
		if (null != model) {
		    model.setUpperTree(upperTree);
		    model.setLowerTree(lowerTree);
		}
		/*
		((DualTreeSelectionModel)getSelectionModel()).setUpperTree(upperTree);
		((DualTreeSelectionModel)getSelectionModel()).setLowerTree(lowerTree);
		*/

		//listen(getSelectionModel());		

		setupConstraints();
		add(upper);
		add(lower);
		adjustSize();
	    }
	}
	//getUI().installUI(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredSize()
    {
	Dimension d = super.getPreferredSize();
	if(isPreferredSizeSet())
	    return d;

	d.width = adjustSize();
	Dimension d2 = getPreferredSize(getUpperTree());
	int height = d2.height;
	d2 = getPreferredSize(getLowerTree());
	d.height = height + d2.height;
	return d;
    }

    /*
    public void setPreferredSize()
    {
	Dimension d = getPreferredSize();
	setTreeSize(getUpperTree(), d.width);
	setTreeSize(getLowerTree(), d.width);
	invalidate();
	validate();			
    }

    public void setTreeSize(FlippableTree tree, int width)
    {
	tree.setSize(new Dimension(width, tree.getHeight()));
	tree.invalidate();
	tree.validate();	
    }
    */	    


    /**
     * Returns uppter {@code FlippableTree}, or nul if unspecified.
     *
     * @return upper {@code FlippableTree}
     */
    public FlippableTree getUpperTree()
    {
	return upperTree;
    }

    /**
     * Returns uppter {@code FlippableTree}, or nul if unspecified.
     *
     * @return lower {@code FlippableTree}
     */
    public FlippableTree getLowerTree()
    {
	return lowerTree;
    }


    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent event)
    {
	String eventType = event.getPropertyName();
	if (!eventType.equals("Orientation") && !eventType.equals("componentOrientation"))
	    return;

	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();

	if (upper == null && lower == null) return;
	if (!toTranslateTrees()) {
	    upper.setBorder(zeroBorder);
	    lower.setBorder(zeroBorder);	    
	}
	upper.setBorder(zeroBorder);
	lower.setBorder(zeroBorder);	
	invalidate();
	validate();
	invalidate();
	validate();	
    }

    public boolean isAlignRootVerticalLeg()
    {
	return alignRootVerticalLeg;
    }
    
    public void setAlignRootVerticalLeg(boolean toAlign)
    {
	if (alignRootVerticalLeg == toAlign)
	    return;
	
	alignRootVerticalLeg = toAlign;
	invalidate();
	if (!toAlign) {
	    getUpperTree().setBorder(zeroBorder);
	    getLowerTree().setBorder(zeroBorder);
	}
	adjustSize();
    }

    public boolean isAntiparallel()
    {
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();
	return (null == upper || null == lower) ? false :
	    upper.getComponentOrientation().isLeftToRight()
	    != lower.getComponentOrientation().isLeftToRight() &&
	    upper.isFlipped(Orientation.VERTICAL);
    }
    

    protected boolean toTranslateTrees()
    {
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();

	if (upper == null || lower == null)
	    return false;

	return isAlignRootVerticalLeg() && isAntiparallel();
	/*
	    getUpperTree().getComponentOrientation().isLeftToRight()
	    != getLowerTree().getComponentOrientation().isLeftToRight() &&
	    getUpperTree().isFlipped(Orientation.VERTICAL);
	*/
    }

    /**
     * {@inheritDoc}
     */
    public void treeCollapsed(TreeExpansionEvent event)
    {
	adjustSize();
    }

    /**
     * {@inheritDoc}
     */
    public void treeExpanded(TreeExpansionEvent event)
    {
	adjustSize();
    }

    public int getAntiparallelOffset()
    {
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();
	return (null == upper || null == lower)? 0 :
	    (upper.getFlippableTreeUI().getLeftChildIndent() +
	     lower.getFlippableTreeUI().getLeftChildIndent());
    }

    public int adjustSize()
    {
	FlippableTree upper = getUpperTree();
	FlippableTree lower = getLowerTree();
	int width = 0;	

	if (upper == null && lower == null) return width;

	upper.setBorder(zeroBorder);
	lower.setBorder(zeroBorder);	
	Dimension upperSize = (upper == null) ? zeroDimension : upper.getUI().getPreferredSize(upper);
	Dimension lowerSize = (upper == null) ? zeroDimension : lower.getUI().getPreferredSize(upper);

	if (!toTranslateTrees()) {
	    width = upperSize.width > lowerSize.width ? upperSize.width : lowerSize.width;
	} else {
	    Insets upperInsets = upper.getInsets();
	    Insets lowerInsets = lower.getInsets();	
	    boolean upperLeftToRight = upper.getComponentOrientation().isLeftToRight();
	    boolean lowerLeftToRight = lower.getComponentOrientation().isLeftToRight();
	    int upperInset = upperLeftToRight ? upperInsets.left :  upperInsets.right;
	    int lowerInset = lowerLeftToRight ? lowerInsets.left :  lowerInsets.right;
	    width = upperSize.width - upperInset + lowerSize.width - lowerInset - getAntiparallelOffset();
	    int upperLeading = width - (upperSize.width - upperInset);
	    int lowerLeading = width - (lowerSize.width - lowerInset);
	    if (upperLeftToRight) {
		upper.setBorder(BorderFactory.createEmptyBorder(0, upperLeading, 0, 0));
		lower.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, lowerLeading));
	    } else {
		upper.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, upperLeading));
		lower.setBorder(BorderFactory.createEmptyBorder(0, lowerLeading, 0, 0));
	    }
	}
	upperSize.width = width;
	lowerSize.width = width;
	/*
	upper.setPreferredWidth(width);
	lower.setPreferredWidth(width);
	*/
	upper.setPreferredSize(upperSize);
	lower.setPreferredSize(lowerSize);	
 	//setPreferredSize(new Dimension(width, upperSize.height + lowerSize.height));
	//setPreferredSize(new Dimension(width, upper.getHeight() + lower.getHeight()));
	setSize(new Dimension(width, upper.getHeight() + lower.getHeight()));		
	return width;
    }

    protected Dimension getPreferredSize(JComponent c)
    {
	return (c == null) ? zeroDimension :
	    c.getUI().getPreferredSize(c);

    }

    protected Dimension getTreeSize()
    {
	Dimension d = null;
	FlippableTree tree = getUpperTree();
	if (tree != null)
	    d = tree.getPreferredSize();	    
	else
	    d = new Dimension(0, 0);
	tree = getLowerTree();
	if (tree != null) {
	    Dimension d2 = tree.getPreferredSize();	    
	    d.height += d2.height;
	    if (d2.width > d.width)
		d.width = d2.width;
	}
	return d;
    }

   protected int getPreferredWidth(FlippableTree tree)
    {
	int width = tree.getPreferredSize().width;
	TreeUI ui = tree.getUI();
	if (ui != null) {
	    Dimension d = ui.getPreferredSize(tree);
	    if (d != null) width = d.width;
	}
	return width;
    }

    protected int getPreferredWidth()
    {
	int width = 0;
	FlippableTree tree = getUpperTree();
	if (tree != null) {
	    width = getPreferredWidth(tree);
	}
	tree= getLowerTree();
	if (tree != null) {
	    width = Math.max(width, getPreferredWidth(tree));
	}
		
	return width;
    }

    protected void setPreferredWidth()
    {
	/*
	int width = getPreferredWidth();
	FlippableTree tree = getUpperTree();	
	if (tree != null) {	
	    tree.setPreferredWidth(width);
	}
	tree= getLowerTree();	
	if (tree != null) {	
	    tree.setPreferredWidth(width);
	}
	*/
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setBackground(Color color)
    {
	FlippableTree tree= getUpperTree();
	if (tree != null) tree.setBackground(color);
	tree = getLowerTree();
	if (tree != null) tree.setBackground(color);
	super.setBackground(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setForeground(Color color)
    {
	FlippableTree tree= getUpperTree();
	if (tree != null) tree.setForeground(color);
	tree = getLowerTree();
	if (tree != null) tree.setForeground(color);
	super.setForeground(color);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFont(Font font)
    {
	FlippableTree tree= getUpperTree();
	if (tree != null) tree.setFont(font);
	tree = getLowerTree();
	if (tree != null) tree.setFont(font);
	super.setFont(font);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLocale(Locale locale)
    {
	FlippableTree tree= getUpperTree();
	if (tree != null) tree.setLocale(locale);
	tree = getLowerTree();
	if (tree != null) tree.setLocale(locale);
	super.setLocale(locale);
    }

    protected int getRowCount(FlippableTree tree)
    {
	return (tree == null)? 0: tree.getRowCount();
    }

    protected int getUpperRowCount()
    {
	return getRowCount(getUpperTree());
    }

    protected int getLowerRowCount()
    {
	return getRowCount(getLowerTree());
    }

    protected int getRow(int row, FlippableTree tree)
    {
	if (tree == null || row == -1)
	    return -1;
	if (tree == getLowerTree())
	    return getLowerRow(row);
	return row;
    }

    protected int getLowerRow(int row)
    {
	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0)
	    return -1;

	return  row - upperCount;
    }

    protected int getRow(int row)
    {
	int upperCount = getUpperRowCount();

	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0)
	    return -1;

	if (row < upperCount)
	    return row;
	
	return  row - upperCount;
    }

    protected FlippableTree getContainerTree(TreePath path)
    {
	if (path == null) return null;
	FlippableTree tree = getLowerTree();
	if (getRowForPath(path, tree) >= 0)
	    return tree;
	tree = getUpperTree();
	if (getRowForPath(path, tree) >= 0)
	    return tree;
	return null;
    }

    protected FlippableTree getContainerTree(int row)
    {
	return getContainerTree(getPathForRow(row));
    }
    
    protected int gettRowForPath(TreePath path, FlippableTree tree)
    {
	return (tree == null) ? -1 : tree.getRowForPath(path);
    }

    protected static TreeModel getNullTreeModel()
    {
	return null;
    }

    // conviinience methods
   protected int getWidth(FlippableTree tree)
    {
	return (tree == null)? 0 : tree.getWidth();
    }

    protected int getHeight(FlippableTree tree)
    {
	return (tree == null)? 0 : tree.getHeight();
    }

    protected Dimension getSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getSize();
    }

    protected Dimension getMinimumSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getMinimumSize();
    }

    protected Dimension getMaximumSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getMaximumSize();
    }

    protected Dimension getPreferredSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getPreferredSize();
    }

    protected Dimension getUIPreferredSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getUI().getPreferredSize(tree);
    }

    protected int[][] getRows(int[] rows)
    {
	Vector<Integer> upper = new Vector<Integer>(rows.length);
	Vector<Integer> lower = new Vector<Integer>(rows.length);	
	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();
	for (int row : rows) {
	    if (row < 0) continue;
	    else if (row < upperCount) upper.add(Integer.valueOf(row));
	    else if (row < lowerCount) lower.add(Integer.valueOf(row - upperCount));
	}

	int[][] rowsArray = new int[2][];
	rowsArray[0] = new int[upper.size()];
	rowsArray[1] = new int[lower.size()];
	int i = 0;
	if (upper.size() > 0) {
	    for (Integer row : upper)
		rowsArray[0][i++] = row.intValue();
	}
	if (lower.size() > 0) {
	    i = 0;
	    for (Integer row : lower)
		rowsArray[1][i++] = row.intValue();
	}
	upper.clear();
	lower.clear();
	return rowsArray;
    }

    protected TreePath[][] getPaths(TreePath[] paths)
    {
	Vector<TreePath> upper = new Vector<TreePath>(paths.length);
	Vector<TreePath> lower = new Vector<TreePath>(paths.length);
	FlippableTree upperTree = getUpperTree();
	FlippableTree lowerTree = getLowerTree();	
	for (TreePath path : paths) {
	    FlippableTree tree = getContainerTree(path);
	    if (tree == null) continue;
	    else if (tree == upperTree) upper.add(path);
	    else if (tree == upperTree) lower.add(path);
	}

	TreePath[][] pathsArray = new TreePath[2][];
	pathsArray[0] = upper.toArray(new TreePath[upper.size()]);
	pathsArray[1] = lower.toArray(new TreePath[lower.size()]);
	upper.clear();
	lower.clear();
	return pathsArray;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionInterval(int index0, int index1)
    {
	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0) return;

	if (index0 > upperCount) {
	    if (lowerCount > 0)
		getLowerTree().addSelectionInterval(getLowerRow(index0), getLowerRow(index1));
	}
	else if (index1 < upperCount) {
	    if (upperCount >0)
		getUpperTree().addSelectionInterval(index0, index1);
	}
	else {
	    if (upperCount >0)
		getUpperTree().addSelectionInterval(index0, upperCount - 1);
	    if (lowerCount > 0)
		getLowerTree().addSelectionInterval(0, getLowerRow(index1));		
	}
	super.addSelectionInterval(index0, index1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public  void addSelectionPath(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.addSelectionPath(path);
	super.addSelectionPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionPaths(TreePath[] paths)
    {
	TreePath[][] pathsArray = getPaths(paths);
	FlippableTree tree = getUpperTree();
	if (pathsArray[0].length > 0 && tree != null)
	    tree.addSelectionPaths(pathsArray[0]);
	tree = getLowerTree();	
	if (pathsArray[1].length > 0 && tree != null)
	    tree.addSelectionPaths(pathsArray[1]);
	super.addSelectionPaths(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionRow(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.addSelectionRow(getRow(row));
	super.addSelectionRow(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addSelectionRows(int[] rows)
    {
	int[][] rowsArray = getRows(rows);
	FlippableTree tree = getUpperTree();
	if (rowsArray[0].length > 0 && tree != null)
	    tree.addSelectionRows(rowsArray[0]);
	tree = getLowerTree();	
	if (rowsArray[1].length > 0 && tree != null)
	    tree.addSelectionRows(rowsArray[1]);
	super.addSelectionRows(rows);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelEditing()
    {
	cancelEditing(getUpperTree());
	cancelEditing(getLowerTree());	
    }

    protected void cancelEditing(FlippableTree tree)
    {
	if (tree != null)
	    tree.cancelEditing();
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    public void clearSelection()
    {
	clearSelection(getUpperTree());
	clearSelection(getLowerTree());
	super.clearSelection();
    }

    protected void clearSelection(FlippableTree tree)    
    {
	if (tree != null)
	    tree.clearSelection();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapsePath(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.collapsePath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void collapseRow(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.collapseRow(getRow(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandPath(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.expandPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandRow(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.expandRow(getRow(row));
    }

    protected FlippableTree getClosestTreeForLocation(int x, int y)
    {
	FlippableTree tree = getUpperTree();
	int upperHeight = getHeight(tree);
	return (y < upperHeight)? tree : getLowerTree();
   }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath getClosestPathForLocation(int x, int y)
    {
	FlippableTree tree = getUpperTree();
	int upperHeight = getHeight(tree);
	if (y < upperHeight)
	    return tree.getClosestPathForLocation(x,  y);
	tree = getLowerTree();
	return (tree == null) ? null :
	    tree.getClosestPathForLocation(x,  y - upperHeight);	    
   }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDragEnabled()
    {
	return getDragEnabled(getUpperTree()) || getDragEnabled(getLowerTree());
    }

    public boolean getDragEnabled(FlippableTree tree)
    {
	return (tree == null)? false : tree.getDragEnabled();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Enumeration<TreePath> getExpandedDescendants(TreePath parent)
    {
	FlippableTree tree = getContainerTree(parent);
	return (tree == null) ? null : tree.getExpandedDescendants(parent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxSelectionRow()
    {
	FlippableTree tree = getLowerTree();
	int row = (tree == null) ? -1 : tree.getMaxSelectionRow();
	if (row < 0) {
	    tree = getUpperTree();
	    row = (tree == null) ? -1 : tree.getMaxSelectionRow();
	}
	return row;
    }
	    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getMinSelectionRow()
    {
	FlippableTree tree = getUpperTree();
	int row = (tree == null) ? -1 : tree.getMinSelectionRow();
	if (row < 0) {
	    tree = getLowerTree();
	    row = (tree == null) ? -1 : tree.getMinSelectionRow();
	}
	return row;
    }
	    
    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath getNextMatch(String prefix, int startingRow, Position.Bias bias)
    {
	int upperCount = getUpperRowCount();

	if (upperCount + getLowerRowCount() == 0) return null;

	FlippableTree tree = null;
	
	if (startingRow >= upperCount) {
	    tree = getLowerTree();
	    return (tree == null)? null :
		tree.getNextMatch(prefix, startingRow - upperCount, bias);
	}

	tree = getUpperTree();
	TreePath path = null;
	
	if (tree != null)
	    path = tree.getNextMatch(prefix, startingRow, bias);
	if (path == null) {
	    tree = getLowerTree();
	    path = (tree == null)? null :
		tree.getNextMatch(prefix, 0, bias);
	}
	return path;
    }

    protected static final TreePath[] zeroTreePath = new TreePath[0];
    /**
     * {@inheritDoc}
     */
    @Override
    protected TreePath[] getPathBetweenRows(int index0, int index1)
    {
	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0) return zeroTreePath;
	TreePath[] upper = zeroTreePath, lower = zeroTreePath;

	if (index0 > upperCount) {
	    if (lowerCount > 0)
		lower = getLowerTree()._getPathBetweenRows(getLowerRow(index0), getLowerRow(index1));
	}
	else if (index1 < upperCount) {
	    if (upperCount >0)
		upper = getUpperTree()._getPathBetweenRows(index0, index1);
	}
	else {
	    if (upperCount >0)
		upper = getUpperTree()._getPathBetweenRows(index0, upperCount - 1);
	    if (lowerCount > 0)
		lower = getLowerTree()._getPathBetweenRows(0, getLowerRow(index1));		
	}
	Stream<TreePath> upperStream =Arrays.stream(upper);
	Stream<TreePath> lowerStream = Arrays.stream(lower);
	Stream<TreePath> paths = Stream.concat(upperStream, lowerStream);
	return paths.toArray(TreePath[]::new);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Rectangle getPathBounds(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree == null) return null;

	Rectangle bounds = tree.getPathBounds(path);	
	if (tree == getLowerTree())
	    bounds.y += getHeight(getUpperTree());
	return bounds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath getPathForRow(int row)
    {
	int upperRowCount = getUpperRowCount();
	if(row < upperRowCount)
	    return getUpperTree().getPathForRow(row);
	if (row < upperRowCount +getLowerRowCount())
	    return getLowerTree().getPathForRow(row - upperRowCount);
	return null;
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    public Dimension getPreferredScrollableViewportSize()
    {
	Dimension upper = getPreferredScrollableViewportSize(getUpperTree());
	Dimension lower = getPreferredScrollableViewportSize(getLowerTree());
	return new Dimension(((upper.width > lower.width) ? upper.width : lower.width),
			     upper.height + lower.height);
    }

    protected Dimension getPreferredScrollableViewportSize(FlippableTree tree)
    {
	return (tree == null)? zeroDimension : tree.getPreferredScrollableViewportSize();
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowCount()
    {
	return getRowCount(getUpperTree()) + getRowCount(getLowerTree());
    }
	
    /**
     * {@inheritDoc}
     */
    @Override
    public int getRowForPath(TreePath path)
    {
	int row = getRowForPath(path, getLowerTree());
	if (row < 0)
	    row = getRowForPath(path, getUpperTree());
	return row;
    }

    protected int getRowForPath(TreePath path, FlippableTree tree)
    {
	return (tree == null)? -1 : tree.getRowForPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
    {
	return Math.max(getScrollableUnitIncrement(visibleRect, orientation, direction, getUpperTree()),
			getScrollableUnitIncrement(visibleRect, orientation, direction, getLowerTree()));	
    }

    protected int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction, FlippableTree tree)
    {
	return (tree == null)? -1 : tree.getScrollableUnitIncrement(visibleRect, orientation, direction);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSelectionCount()
    {
	return getSelectionCount(getUpperTree()) + getSelectionCount(getLowerTree());
    }

    protected int getSelectionCount(FlippableTree tree)
    {
	return (tree == null)? 0 : tree.getSelectionCount();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath getSelectionPath()
    {
	TreePath path = getSelectionPath(getUpperTree());
	return (path != null)? path : getSelectionPath(getLowerTree());
    }

    protected TreePath getSelectionPath(FlippableTree tree)
    {
	return (tree == null)? null : tree.getSelectionPath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreePath[] getSelectionPaths()
    {
	Stream<TreePath> upper =Arrays.stream(getSelectionPaths(getUpperTree()));
	Stream<TreePath> lower = Arrays.stream(getSelectionPaths(getLowerTree()));
	Stream<TreePath> paths = Stream.concat(upper, lower);
	return paths.toArray(TreePath[]::new);
			     
    }

    protected TreePath[] getSelectionPaths(FlippableTree tree)
    {
	TreePath[] path = (tree == null)? null : tree.getSelectionPaths();
	return  (path == null)? new TreePath[0] : path;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getSelectionRows()
    {
	return IntStream.concat(Arrays.stream(getSelectionRows(getUpperTree())),
				Arrays.stream(getSelectionRows(getLowerTree()))).toArray();
    }

    protected int[] getSelectionRows(FlippableTree tree)
    {
	int[] rows = (tree == null)? null : tree.getSelectionRows();
	return (rows == null)? new int[0] : rows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getVisibleRowCount()
    {
	return getVisibleRowCount(getUpperTree()) + getVisibleRowCount(getLowerTree());
    }

    protected int getVisibleRowCount(FlippableTree tree)
    {
	return (tree == null)? 0 : tree.getVisibleRowCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBeenExpanded(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	return (tree == null)? false : tree.hasBeenExpanded(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollapsed(int row)
    {
	return isCollapsed(getPathForRow(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCollapsed(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	return (tree == null)? false : tree.isCollapsed(path);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable()
    {
	return isEditable(getUpperTree()) || isEditable(getLowerTree());
    }

    protected boolean isEditable(FlippableTree tree)
    {
	return (tree == null)? false : tree.isEditable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditing()
    {
	return isEditing(getUpperTree()) || isEditing(getLowerTree());
    }

    protected boolean isEditing(FlippableTree tree)
    {
	return (tree == null)? false : tree.isEditing();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isExpanded(int row)
    {
	return isExpanded(getPathForRow(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExpanded(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	return (tree == null)? false : tree.isExpanded(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFixedRowHeight()
    {
	return isFixedRowHeight(getUpperTree()) && isFixedRowHeight(getLowerTree());
    }

    protected boolean isFixedRowHeight(FlippableTree tree)
    {
	return (tree == null)? true : tree.isFixedRowHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isLargeModel()
    {
	return isLargeModel(getUpperTree()) || isLargeModel(getLowerTree());
    }

    protected boolean isLargeModel(FlippableTree tree)
    {
	return (tree == null)? false : tree.isLargeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPathSelected(TreePath path)
    {
	return isPathSelected(path, getUpperTree()) || isPathSelected(path, getLowerTree());
    }

    protected boolean isPathSelected(TreePath path, FlippableTree tree)
    {
	return (tree == null)? false : tree.isPathSelected(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRootVisible()
    {
	return isRootVisible(getUpperTree()) || isRootVisible(getLowerTree());
    }

    protected boolean isRootVisible(FlippableTree tree)
    {
	return (tree == null) ? false : tree.isRootVisible();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRowSelected(int row)
    {
	FlippableTree tree = getContainerTree(row);
	return (tree == null)? false : tree.isRowSelected(getRow(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSelectionEmpty()
    {
	return isSelectionEmpty(getUpperTree()) &&  isSelectionEmpty(getLowerTree());
    }
    
    protected boolean isSelectionEmpty(FlippableTree tree)
    {
	return (tree == null)? true : tree.isSelectionEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isVisible(TreePath path)
    {
	return isVisible(path, getUpperTree())  ||  isVisible(path, getLowerTree());
    }
    protected boolean isVisible(TreePath path, FlippableTree tree)
    {
	return (tree == null) ? false : tree.isVisible(path);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void makeVisible(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.makeVisible(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionInterval(int index0, int index1)
    {
	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0) return;

	if (index0 > upperCount) {
	    if (lowerCount > 0)
		getLowerTree().removeSelectionInterval(getLowerRow(index0), getLowerRow(index1));
	}
	else if (index1 < upperCount) {
	    if (upperCount >0)
		getUpperTree().removeSelectionInterval(index0, index1);
	}
	else {
	    if (upperCount >0)
		getUpperTree().removeSelectionInterval(index0, upperCount - 1);
	    if (lowerCount > 0)
		getLowerTree().removeSelectionInterval(0, getLowerRow(index1));		
	}
    }

    protected void removeSelectionInterval(int index0, int index1, FlippableTree tree)
    {
	if (tree != null)
	    tree.removeSelectionInterval(index0, index1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionPath(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.removeSelectionPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionPaths(TreePath[] paths)
    {
	removeSelectionPaths(paths, getUpperTree());
	removeSelectionPaths(paths, getLowerTree());
    }

    protected void removeSelectionPaths(TreePath[] paths, FlippableTree tree)
    {
	if (tree != null)
	    tree.removeSelectionPaths(paths);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionRow(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.removeSelectionRow(getRow(row));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeSelectionRows(int[] rows)
    {
	removeSelectionRows(rows, getUpperTree());
	removeSelectionRows(rows, getLowerTree());
    }

    protected void removeSelectionRows(int[] rows, FlippableTree tree)
    {
	if (tree != null)
	    tree.removeSelectionRows(rows);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollPathToVisible(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.scrollPathToVisible(path);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void scrollRowToVisible(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.scrollRowToVisible(getRow(row));
    }

    protected boolean anchoring = false;
    /**
     * {@inheritDoc}
     */
    @Override
    public void setAnchorSelectionPath(TreePath newPath)
    {
	synchronized(this) {
	    anchoring = true;
	    FlippableTree tree = null;
	    TreePath oldPath = getAnchorSelectionPath();
	    if (oldPath != null) {
		tree = getContainerTree(oldPath);
		if (tree != null) {
		    tree.setAnchorSelectionPath(null);
		}
	    }
	    tree = getContainerTree(newPath);
	    if (tree != null) {
		tree.setAnchorSelectionPath(newPath);
	    }
	    anchoring = false;
	}
	super.setAnchorSelectionPath(newPath);
    }

    protected boolean cellEditorSetting = false;
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellEditor(TreeCellEditor cellEditor)
    {
	cellEditorSetting = true;
	setCellEditor(cellEditor, getUpperTree());
	setCellEditor(cellEditor, getLowerTree());
	cellEditorSetting = false;
	super.setCellEditor(cellEditor);
    }

    protected void setCellEditor(TreeCellEditor cellEditor, FlippableTree tree)
    {
	if (tree != null)
	    tree.setCellEditor(cellEditor);
    }

    protected boolean cellRendererSetting = false;
    /**
     * {@inheritDoc}
     */
    @Override
    public void setCellRenderer(TreeCellRenderer cellRenderer)
    {
	cellRendererSetting = true;
	setCellRenderer(cellRenderer, getUpperTree());
	setCellRenderer(cellRenderer, getLowerTree());
	cellRendererSetting = false;
	super.setCellRenderer(cellRenderer);
    }

    protected void setCellRenderer(TreeCellRenderer cellRenderer, FlippableTree tree)
    {
	if (tree != null)
	    tree.setCellRenderer(cellRenderer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDragEnabled(boolean enableDrag)
    {
	setDragEnabled(enableDrag, getUpperTree());
	setDragEnabled(enableDrag, getLowerTree());
	super.setDragEnabled(enableDrag);
    }

    protected void setDragEnabled(boolean enableDrag, FlippableTree tree)
    {
	if(tree != null)
	    tree.setDragEnabled(enableDrag);
    }

    public  void setDropModes(DropMode dropMode)
    {
	setDropMode(dropMode, getUpperTree());
	setDropMode(dropMode, getLowerTree());
	setDropMode( dropMode);
    }

    public  void setDropMode(DropMode dropMode, FlippableTree tree)
    {
	if(tree != null)
	    tree.setDropMode(dropMode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEditable(boolean flag)
    {
	setEditable(flag, getUpperTree());
	setEditable(flag, getLowerTree());
	super.setEditable(flag);
    }

    protected void setEditable(boolean flag, FlippableTree tree)
    {
	if (tree != null)
	    tree.setEditable(flag);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setExpandsSelectedPaths(boolean newValue)
    {
	setExpandsSelectedPaths(newValue, getUpperTree());
	setExpandsSelectedPaths(newValue, getLowerTree());
	super.setExpandsSelectedPaths(newValue);
    }
    
    protected void setExpandsSelectedPaths(boolean newValue, FlippableTree tree)
    {
	if (tree != null)
	    tree.setExpandsSelectedPaths(newValue);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setInvokesStopCellEditing(boolean newValue)
    {
	setInvokesStopCellEditing(newValue, getUpperTree());
	setInvokesStopCellEditing(newValue, getLowerTree());
	super.setInvokesStopCellEditing(newValue);
    }
    
    protected void setInvokesStopCellEditing(boolean newValue, FlippableTree tree)
    {
	if (tree != null)
	    tree.setInvokesStopCellEditing(newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLargeModel(boolean newValue)
    {
	setLargeModel(newValue, getUpperTree());
	setLargeModel(newValue, getLowerTree());
	super.setLargeModel(newValue);
    }
    
    protected void setLargeModel(boolean newValue, FlippableTree tree)
    {
	if (tree != null)
	    tree.setLargeModel(newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLeadSelectionPath(TreePath newPath)
    {
	FlippableTree tree = getContainerTree(newPath);
	if (tree != null)
	    tree.setLeadSelectionPath(newPath);
	super.setLeadSelectionPath(newPath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRootVisible(boolean rootVisible)
    {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRowHeight(int rowHeight)
    {
	setRowHeight(rowHeight, getUpperTree());
	setRowHeight(rowHeight, getLowerTree());
	super.setRowHeight(rowHeight);
    }

    protected void setRowHeight(int rowHeight, FlippableTree tree)
    {
	if (tree != null)
	    tree.setRowHeight(rowHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setScrollsOnExpand(boolean newValue)
    {
	setScrollsOnExpand(newValue, getUpperTree());
	setScrollsOnExpand(newValue, getLowerTree());
	super.setScrollsOnExpand(newValue);
    }
    
    protected void setScrollsOnExpand(boolean newValue, FlippableTree tree)
    {
	if (tree != null)
	    tree.setScrollsOnExpand(newValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionInterval(int index0, int index1)
    {
	new Error().printStackTrace();
	super.setSelectionInterval(index0, index1);

	int upperCount = getUpperRowCount();
	int lowerCount = getLowerRowCount();

	if (upperCount + lowerCount == 0) return;

	if (index0 > upperCount) {
	    if (lowerCount > 0)
		getLowerTree().setSelectionInterval(getLowerRow(index0), getLowerRow(index1));
	}
	else if (index1 < upperCount) {
	    if (upperCount >0)
		getUpperTree().setSelectionInterval(index0, index1);
	}
	else {
	    if (upperCount >0)
		getUpperTree().setSelectionInterval(index0, upperCount - 1);
	    if (lowerCount > 0)
		getLowerTree().setSelectionInterval(0, getLowerRow(index1));		
	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionModel(TreeSelectionModel selectionModel)
    {
	//unlisten(getSelectionModel());
	super.setSelectionModel(selectionModel);
	//listen(getSelectionModel());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionPath(TreePath path)
    {
	FlippableTree tree = getContainerTree(path);
	if (tree != null)
	    tree.setSelectionPath(path);
	super.setSelectionPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionPaths(TreePath[] paths)
    {
	setSelectionPaths(paths, getUpperTree());
	setSelectionPaths(paths, getLowerTree());
	super.setSelectionPaths(paths);
    }
    
    protected void setSelectionPaths(TreePath[] paths, FlippableTree tree)
    {
	if (tree != null)
	    tree.setSelectionPaths(paths);
    }	

    /**
     * {@inheritDoc}
     */
    public void setSelectionRow(int row)
    {
	FlippableTree tree = getContainerTree(row);
	if (tree != null)
	    tree.setSelectionRow(getRow(row));
	super.setSelectionRow(row);
    }

    /**
     * {@inheritDoc}
     */
    public void setSelectionRows(int[] rows)
    {
	if (rows == null) return;
	int[][] rowsArray = getRows(rows);
	FlippableTree tree = getUpperTree();
	if (rowsArray[0].length > 0 && tree != null)
	    tree.setSelectionRows(rowsArray[0]);
	tree = getLowerTree();
	if (rowsArray[1].length > 0 && tree != null)
	    tree.setSelectionRows(rowsArray[1]);
    }

    /**
     * {@inheritDoc}
     */
    /*
    @Override
    public void setShowsRootHandles(boolean newValue)
    {
	setShowsRootHandles(newValue, getUpperTree());
	setShowsRootHandles(newValue, getLowerTree());
	super.setShowsRootHandles(newValue);
    }	
    protected void setShowsRootHandles(boolean newValue, FlippableTree tree)
    {
	if (tree != null)
	    tree.setShowsRootHandles(newValue);
    }
    */
    /**
     * {@inheritDoc}
     */
    @Override
    public void setToggleClickCount(int clickCount)
    {
	setToggleClickCount(clickCount, getUpperTree());
	setToggleClickCount(clickCount, getLowerTree());
	super.setToggleClickCount(clickCount);
    }
    
    public void setToggleClickCount(int clickCount, FlippableTree tree)
    {
	if (tree != null)
	    tree.setToggleClickCount(clickCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisibleRowCount(int newCount)
    {
	setVisibleRowCount(newCount, getUpperTree());
	setVisibleRowCount(newCount, getLowerTree());
	super.setVisibleRowCount(newCount);
    }
    protected void setVisibleRowCount(int newCount, FlippableTree tree )
    {
	if (tree != null)
	    tree.setVisibleRowCount(newCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void startEditingAtPath(TreePath path)
    {
	startEditingAtPath(path, getUpperTree());
	startEditingAtPath(path, getLowerTree());
	super.startEditingAtPath(path);
    }
    
    protected void startEditingAtPath(TreePath path, FlippableTree tree)
    {
	if (tree != null)
	    tree.startEditingAtPath(path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean stopEditing()
    {
	super.stopEditing();
	return stopEditing(getUpperTree()) || stopEditing(getLowerTree());
    }		
    protected boolean stopEditing(FlippableTree tree)
    {
	return (tree == null) ? false: tree.stopEditing();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void treeDidChange()
    {
	treeDidChange(getUpperTree());
	treeDidChange(getLowerTree());
	super.treeDidChange();
    }
    protected void treeDidChange(FlippableTree tree)
    {
	if(tree != null)
	    tree.treeDidChange();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int[] getRowsForPaths(TreePath[] paths)
    {
	if (paths == null) return null;
	int[] rows = new int[paths.length];
	int i = 0;
	for (TreePath path : paths) {
	    rows[i++] = getRowForPath(path);
	}
	return rows;
    }

    public void valueChanged(TreeSelectionEvent event)
    {
	if( null != event) {
	    TreePath newLead = event.getNewLeadSelectionPath();
	    if (newLead != getLeadSelectionPath())
		setLeadSelectionPath(newLead);
	}
    }
}
