/*
 * FlippableTree.java:  a updown flippable tree GUI component
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

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.util.Hashtable;
import java.util.Vector;

import javax.accessibility.AccessibleContext;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TreeUI;

import javax.swing.plaf.basic.BasicTreeUI;

import javax.swing.plaf.metal.MetalLookAndFeel;

import javax.swing.plaf.synth.SynthLookAndFeel;

import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.LookAndFeelUtilities;

import org.nomencurator.gui.swing.plaf.UIBroaker;
import org.nomencurator.gui.swing.plaf.FlippableTreeUI;
import org.nomencurator.gui.swing.plaf.FlippableTreeUIUtilities;

import org.nomencurator.gui.swing.plaf.metal.MetalFlippableTreeUI;

import org.nomencurator.gui.swing.plaf.synth.SynthFlippableTreeUI;

import org.nomencurator.gui.swing.tree.FlippableFixedHeightLayoutCache;
import org.nomencurator.gui.swing.tree.FlippableVariableHeightLayoutCache;

/**
 * <CODE>FlippableTree</CODE> provides a {@code Flippable} <code>JTree</code>
 *
 * @version 	30 Apr. 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippableTree
    extends JTree
    implements Flippable //, UIBroaker
	       //, TreeSelectionListener
{
    /**
     * UI Class ID
     */
    private static final String uiClassID = "FlippableTreeUI";

    private static final String uiSuperClassID = "TreeUI";

    protected boolean showsSuperClassID = false;

    /**
     * Enumeratoin of line style
     */
    public enum LinesStyle {
	SOLID("solid"),
	DASHED("dashed");

	private String style;

	private LinesStyle(String style)
	{
	    this.style = style;
	}

	public String getStyle()
	{
	    return style;
	}
    }

    public static final LinesStyle SOLID = LinesStyle.SOLID;
    public static final LinesStyle DASHED = LinesStyle.DASHED;

    /**
     * {@code LineProperties} represents properies to draw lines joining nods of {@code FlippableTree}
     * used with Synth UI through {@code UIManager}.
     */
    public static class LineProperties {

	/**
	 * {@code true} to draw lines between nodes.
	 */
	protected boolean drawLines;

	/**
	 * {@code true} to draw vertical lines.
	 */
	protected boolean drawVerticalLines;

	/**
	 * {@code true} to draw vertical lines.
	 */
	protected boolean drawHorizontalLines;

	protected LinesStyle linesStyle;

	public LineProperties(){}

	public  boolean isDrawLines()
	{
	    return drawLines;
	}

	public void setDrawLines(boolean toDraw)
	{
	    drawLines = toDraw;;
	}

	public boolean isDrawVerticalLines()
	{
	    return drawVerticalLines;
	}

	public void setDrawVerticalLines(boolean toDraw)
	{
	    drawVerticalLines = toDraw;
	}

	public boolean isDrawHorizontalLines()
	{
	    return drawHorizontalLines;
	}

	public void setDrawHorizontalLines(boolean toDraw)
	{
	    drawHorizontalLines = toDraw;
	}

	public LinesStyle getLinesStyle()
	{
	    return linesStyle;
	}

	public void setLinesStyle(LinesStyle style)
	{
	    linesStyle = style;
	}

	public static final String DRAW_LINES_PROPERTY = "Tree.paintLines";
	
	public static final String DRAW_VERTICAL_LINES_PROPERTY = "Tree.drawVerticalLines";
	
	public static final String DRAW_HORIZONTAL_LINES_PROPERTY = "Tree.drawHorizontalLines";

	public static final String LINES_STYLE_PROPERTY = "Tree.linesStyle";        

	public void setLineProperties(UIDefaults defaults)
	{
	    if (defaults == null || !(UIManager.getLookAndFeel() instanceof SynthLookAndFeel))
		return;

	    defaults.put(DRAW_LINES_PROPERTY, isDrawLines());
	    defaults.put(DRAW_VERTICAL_LINES_PROPERTY, isDrawVerticalLines());
	    defaults.put(DRAW_HORIZONTAL_LINES_PROPERTY, isDrawHorizontalLines());	
	    defaults.put(LINES_STYLE_PROPERTY, getLinesStyle().getStyle());
	}

	public void setLineProperties()
	{
	    setLineProperties(UIManager.getLookAndFeelDefaults());
	}

    }

    public static LineProperties defaultLineProperties = new LineProperties();

    static {
	defaultLineProperties.setDrawLines(true);
	defaultLineProperties.setDrawVerticalLines(true);
	defaultLineProperties.setDrawHorizontalLines(true);
	defaultLineProperties.setLinesStyle(DASHED);
	defaultLineProperties.setLineProperties();
    }

    static {
	UIManager.put("Tree.repaintWholeRow", Boolean.TRUE);
    }

    /**
     * True if the part of vertical line attached to the root node
     * even if the root is invisible.
     */
    protected boolean showsRootVerticalLineLeg = false;

    protected Integer preferredWidth;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUIClassID()
    {
	return isShowsSuperClassID()? uiSuperClassID : uiClassID;
    }

    public static void setUIBroaker(LookAndFeel lookAndFeel)
    {
	if (lookAndFeel instanceof SynthLookAndFeel) {
	    SynthFlippableTreeUI.setLineProperties();
	    UIManager.getLookAndFeelDefaults().put(uiClassID, SynthFlippableTreeUI.class.getCanonicalName());	    
	    //UIManager.getDefaults().put(uiClassID, SynthFlippableTreeUI.class.getCanonicalName());
	    //SynthFlippableTreeUI.setLineProperties();
	}
	else {
	    //UIManager.getDefaults().put(uiClassID, MetalFlippableTreeUI.class.getCanonicalName());
	    UIManager.getLookAndFeelDefaults().put(uiClassID, MetalFlippableTreeUI.class.getCanonicalName());	    
	}

    }

    static {
	LookAndFeelUtilities.put(FlippableTree.class, "setUIBroaker");
	setUIBroaker(UIManager.getLookAndFeel());
    }

    /**
     * Constructor
     */
    public FlippableTree()
    {
	super();
    }

    public FlippableTree(Hashtable<?,?> value)
    {
	super(value);
    }

    public FlippableTree(Object[] value)
    {
	super(value);
    }

    public FlippableTree(TreeModel newModel)
    {
	super(newModel);
    }

    public FlippableTree(TreeNode root)
    {
	super(root);
    }

    public FlippableTree(TreeNode root, boolean asksAllowsChildren)
    {
	super(root, asksAllowsChildren);
    }

    public FlippableTree(Vector<?> value)
    {
	super(value);
    }

    public void setShowsSuperClassID(boolean toShow)
    {
	this.showsSuperClassID = toShow;
    }

    public boolean isShowsSuperClassID()
    {
	return this.showsSuperClassID;
    }

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
    public boolean getShowsRootVerticalLineLeg()
    {
	return showsRootVerticalLineLeg;
    }

    public BasicTreeUI getBasicTreeUI()
    {
	TreeUI ui = getUI();
	return (ui instanceof BasicTreeUI)? (BasicTreeUI)ui : null;
    }

    public FlippableTreeUI getFlippableTreeUI()
    {
	TreeUI ui = getUI();
	return (ui instanceof FlippableTreeUI) ? (FlippableTreeUI) ui : null;
    }

    /**
     * Returns {@code Flippable} of this instance or NULL if unavilable.
     *
     * @return {@code Flippable} of this instance,
     */
    protected Flippable getFlipper()
    {
	TreeUI ui = getUI();
	return (ui instanceof Flippable) ? (Flippable) ui : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orientation getCapability()
    {
	Flippable flipper = getFlipper();
	return (flipper != null) ? flipper.getCapability() : Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orientation setCapability(Orientation orientation)
    {
	Flippable flipper = getFlipper();
	if(flipper != null)
	    return flipper.setCapability(orientation);
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orientation getOrientation()
    {
	Flippable flipper = getFlipper();
	if(flipper != null)
	    return flipper.getOrientation();
	return Flippable.NONE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Orientation setOrientation(Orientation orientation)
    {
	Flippable flipper = getFlipper();
	Orientation oldOrientation = getOrientation();
	if(oldOrientation == orientation)
	    return orientation; 

	Orientation newOrientation =  Flippable.NONE;
	if(flipper != null)
	    newOrientation = flipper.setOrientation(orientation);
	firePropertyChange("Orientation", oldOrientation, newOrientation);
	return newOrientation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped()
    {
	Flippable flipper = getFlipper();
	if(flipper != null)
	    return flipper.isFlipped();
	return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped(Orientation orientation)
    {
	Flippable flipper = getFlipper();
	if(flipper != null)
	    return flipper.isFlipped(orientation);
	return false;
    }

    public void setPreferredWidth(int width)
    {
	setPreferredWidth(Integer.valueOf(width));
    }

    public void setPreferredWidth(Integer width)
    {
	preferredWidth = width;
    }

    public Integer getPreferredWidth()
    {
	return preferredWidth;
    }

    public Dimension getPreferredSize()
    {
	Dimension d = getPreferredTreeSize();
        if (!isPreferredSizeSet() && preferredWidth != null) {
	    TreeUI ui = getUI();
	    if  (ui != null) {
		Dimension uDim = getUI().getPreferredSize(this);
		if (uDim != null) {
		    d = uDim;
		}
	    }
	    d.width = getPreferredWidth().intValue();
	}
	return d;
    }

    public Dimension getPreferredTreeSize()
    {
	return super.getPreferredSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponentOrientation(ComponentOrientation orientation)
    {
	ComponentOrientation current = getComponentOrientation();

	if(current == orientation)
	    return;

	super.setComponentOrientation(orientation);

	firePropertyChange("ComponentOrientation", current, orientation);
    }

    /*
    public int getRootLegOffsetX()
    {
	return getLegOffsetX(0);
    }

    public int getLegOffsetX(int depth)
    {
	return FlippableTreeUIUtilities.getVerticalPartOfArmX(depth + 1, this);
    }
    */

    /*
    public void setOffsetX(int offset)
    {
	getFlippableTreeUI().setOffsetX(offset);
    }
    */
    /*
    public int getOffsetX()
    {
	return getFlippableTreeUI().getOffsetX();
    }
    */
    
    public static void main(String[] args)
    {
	FlippableTree upperTree = new FlippableTree();
	upperTree.setRootVisible(false);
	upperTree.setShowsRootHandles(true);
	upperTree.setShowsRootVerticalLineLeg(true);
	upperTree.setOrientation(Flippable.VERTICAL);


	FlippableTree lowerTree = new FlippableTree();
	lowerTree.setOrientation(Flippable.NONE);
	lowerTree.setRootVisible(true);
	lowerTree.setShowsRootHandles(false);
	lowerTree.setShowsRootVerticalLineLeg(false);	

	JPanel panel = new JPanel(new GridLayout(2, 1));
	//JPanel panel = new JPanel();
	//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
	panel.add(upperTree);
	//upperTree.setAlignmentX(Component.RIGHT_ALIGNMENT);
	panel.add(lowerTree);
	//lowerTree.setAlignmentX(Component.RIGHT_ALIGNMENT);

	JFrame frame = new JFrame();
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.getContentPane().add(panel);
	frame.setSize(frame.getPreferredSize());
	frame.pack();
	UIDefaults defaults = LookAndFeelUtilities.getDefaults("Nimbus");
	if (defaults != null)
	    defaults.put(uiClassID, "org.nomencurator.gui.swing.plaf.synth.SynthFlippableTreeUI");
	try{
	UIManager.setLookAndFeel("Nimbus");
	}
	catch(Exception ex){
	}
	frame.setVisible(true);
    }

    public void setSize(Dimension d)
    {
	Dimension old = getSize();
	super.setSize(d);
	Dimension newDim = getSize();
	invalidate();
	validate();	
    }

    TreePath[] _getPathBetweenRows(int index0, int index1)
    {
	return getPathBetweenRows(index0, index1);
    }

    protected TreePath[] getPathBetweenRows(int index0, int index1)
    {
	return super.getPathBetweenRows(index0, index1);
    }
}
