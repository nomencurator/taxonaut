/*
 * NameTreeTableHeaderAdatpor.java:  an adaptor providing
 * core functions of NameTreeTableHeaderUI
 *
 * Copyright (c) 2005, 2014, 2015 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.gui.swing.plaf;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;

import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTableHeaderUI;

import org.nomencurator.gui.swing.HeaderEditableTable;

import org.nomencurator.gui.swing.plaf.EditableTableHeaderAdaptor;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;

import org.nomencurator.gui.swing.AlignerTree;
import org.nomencurator.gui.swing.NameTreeTable;
import org.nomencurator.gui.swing.tree.SynchronizedTreeSelectionModel;

/**
 * <code>NameTreeTableHeaderAdaptor</code> provides core functrions
 * of NameTreeTableHeaderUI
 *
 * @version 	26 Sep. 2015
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableHeaderAdaptor
    extends EditableTableHeaderAdaptor
{
    /** constant to specify no synchronized scrolling */
    public static final int NONE = -1;

    /** constant to specify left button for synchronized scrolling */
    public static final int LEFT = 1;

    /** constant to specify right button for synchronized scrolling */
    public static final int RIGHT = 2;

    /** constant to specify middle button for synchronized scrolling */
    public static final int MIDDLE = 3;
    
    /** button for synchronized scroll */
    protected int button;

    /** scroll synchronizer */
    protected SynchronizedTreeSelectionModel treeSelection;

    /** viewport to scroll */
    protected JViewport viewport;

    /**
     * Constracts a <CODE>NameTreeTableHeaderAdaptor</CODE>
     * managing <CODE>header</CODE> using left button for
     * synchronized scrolling.
     *
     * @param header <CODE>JTableHeader</CODE> to be managed
     * by this object
     */
    public NameTreeTableHeaderAdaptor(JTableHeader header)
    {
	this(header, LEFT);
    }

    /**
     * Constracts a <CODE>NameTreeTableHeaderAdaptor</CODE>
     * managing <CODE>header</CODE> using <CODE>button</CODE> for
     * synchronized scrolling.
     *
     * @param header <CODE>JTableHeader</CODE> to be managed
     * by this object
     * @param button int specifying button used for synchronized scroll
     */
    public NameTreeTableHeaderAdaptor(JTableHeader header, int button)
    {
	super(header);
	setSynchronizedScrollButton(button);
	// installSelectionModel();
    }

    /**
     * Sets <CODE>button</CODE> for synchronized scroll.
     * 
     * @param button int specifying button used for synchronized scroll,
     * either of <CODE>LEFT</CODE>, <CODE>RIGHT</CODE>, <CODE>MIDDLE</CODE>,
     * or <CODE>NONE</CODE> to disable synchronized scroll.
     */
    public void setSynchronizedScrollButton(int button)
    {
	switch(button) {
	case LEFT:
	case RIGHT:
	case MIDDLE:
	case NONE:
	    break;
	default:
	     throw new IllegalArgumentException("argument must be one of LEFT, RIGHT, MIDDLE or NONE");
	}

	this.button = button;
    }

    /**
     * Install <CODE>SynchronizedTreeSelectionModel</CODE>
     * used in a <CODE>NameTreeTable</CODE> containig the
     * <CODE>header</CODE>.
     */
    protected void installSelectionModel()
    {
	JTable table = header.getTable();

	if(table instanceof NameTreeTable) {
	    AlignerTree tree =
		((NameTreeTable)table).getAlignerTree();
	    treeSelection = 
		(SynchronizedTreeSelectionModel)tree.getSelectionModel();
	    treeSelection.getViewportSynchronizer().add(tree);
	}
    }

    /**
     * Sets <CODE>viewport</CODE> and <CODE>treeSelection</CODE>
     * if necessary.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void postMousePressed(MouseEvent event)
    {
	super.postMousePressed(event);

	// target must be set by preMousePressed of the super class
	if(target instanceof JScrollBar) {
	    boolean syncScroll = false;
	    switch(button) {
	    case LEFT:
		syncScroll = SwingUtilities.isLeftMouseButton(event);
		break;
	    case RIGHT:
		syncScroll = 
		    SwingUtilities.isRightMouseButton(event) ||
		    event.isControlDown();
		break;
	    case MIDDLE:
		syncScroll = 
		    SwingUtilities.isMiddleMouseButton(event) ||
		    event.isMetaDown();
		break;
	    }

	    if(syncScroll) {
		viewport = ((JScrollPane)target.getParent()).getViewport();
		if(treeSelection == null) {
		    installSelectionModel();
		}
		treeSelection.setScrollSynchronize(true);
		viewport.addChangeListener(treeSelection);
	    }
	}
    }
    
    /**
     * Releases the liten <CODE>viewport</CODE>
     * when mouse released
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public boolean preMouseReleased(MouseEvent event)
    {
	if(viewport != null) {
	    viewport.removeChangeListener(treeSelection);
	    viewport = null;
	}

	return super.preMouseReleased(event);
    }


    /**
     * It may be necessary to releases <CODE>treeSelection</CODE>
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseReleased(MouseEvent)
     */
    public void postMouseReleased(MouseEvent event)
    {
	// do we need?
	/*
	treeSelection == null;
	*/
    }

    /**
     * Repaints <CODE>header</CODE> when dragged
     *
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseDragged(MouseEvent)
     */
    public void postMouseDragged(MouseEvent event)
    {
	super.postMouseDragged(event);

	if((target instanceof JScrollBar) &&
	   viewport != null)
	    header.repaint();
    }
}
