/*
 * EditableTableHeaderAdaptor.java:  an class implementing funcitions
 * of EditableTableHeaderUI
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

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;

/**
 * <code>EditableTableHedearAdaptor</code> provides core functions
 * of EditableTableHedearUI
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class EditableTableHeaderAdaptor
    implements ChangeListener
{
    /** cell editor */
    protected Component cellEditor;

    /** table header to be edited */
    protected JTableHeader header;

    /**
     * Target <CODE>Component</CODE> to which event
     * to be dsipached
     */
    protected Component target;

    /** true if mouse dragging */
    protected transient boolean dragging;
    

    protected int rendererX;
    protected int rendererY;

    /**
     * Constructs <CODE>EditableTableHeaderAdaptor</CODE>
     * managing <CODE>header</CODE>
     *
     * @param header <CODE>JTableHeader</CODE> to be managed
     * by this object.
     */
    public EditableTableHeaderAdaptor(JTableHeader header)
    {
	this.header = header;
	cellEditor = null;
	target = null;
	dragging = false;
    }

    /**
     * Returns true if the <CODE>header</CODE> is under edition
     *
     * @return true if the <CODE>header</CODE> is under edition
     */
    public boolean isEditing()
    {
	return (cellEditor != null);
    }

    /**
     * Returns <CODE>Component</CODE> editing the <CODE>header</CODE>,
     * or null if not under edition.
     *
     * @return <CODE>Component</CODE> editing the <CODE>header</CODE>,
     * or null if not under edition.
     */
    public Component getEditorComponent()
    {
	return cellEditor;
    }

    /**
     * Sets <CODE>editor</CODE> as the cell editor, or
     * null to disable cell edition
     *
     * @param editor <CODE>Component</CODE> to edit the cell
     */
    protected synchronized void setEditor(Component editor)
    {
	cellEditor = editor;
    }


    /**
     * Sets dragging status to be <CODE>state</CODE>
     *
     * @param state <CODE>boolean</CODE> indicating dragging
     * status, true if under dragging
     */
    protected synchronized void setDragging(boolean state)
    {
	dragging = state;
    }
    
    /**
     * Returns true if under dragging
     *
     * @return true if under dragging
     */
    public synchronized boolean isDragging()
    {
	return dragging;
    }

    /**
     * Returns target <CODE>Component</CODE> to dispatch to
     * <CODE>MouseEvent</CODE>s
     *
     * @return <CODE>Component</CODE> to dispatch to
     * <CODE>MouseEvent</CODE>s, or null if no <CODE>Component</CODE>
     * to dispatch events.
     */
    public Component getTarget()
    {
	return target;
    }

    /**
     * Sets <CODE>target</CODE> as target <CODE>Component</CODE>
     * to dispatch mouse events.
     *
     * @param target <CODE>Component</CODE> to dispatch mouse events,
     * or null to unlink the current target.
     */
    protected synchronized void setTarget(Component target)
    {
	if(this.target != null && 
	   this.target instanceof JScrollBar)
	    ((JScrollBar)this.target).getModel().removeChangeListener(this);

	this.target = target;

	if(target != null &&
	   target instanceof JScrollBar)
	    ((JScrollBar)target).getModel().addChangeListener(this);
    }

    /**
     * Preprocesses <CODE>event</CODE> prior to process
     * it to UI's mouseClicked(MouseEvent), especially
     * identify <CODE>target</CODE> component to dispatch
     * <CODE>MouseEvent</CODE>s.  The UI's
     * mouseClicked(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public boolean preMouseClicked(MouseEvent event)
    {
	setTarget(getTarget(event));
	setDragging(false);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after 
     * UI's mouseClicked(MouseEvent) by dispatching
     * the <CODE>event</CODE> to <CODE>target</CODE>
     * component.  The UI's
     * mouseClicked(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be dispatched
     *
     * @see java.awt.event.MouseListener#mouseClicked(MouseEvent)
     */
    public void postMouseClicked(MouseEvent event)
    {
	dispatchEvent(event);
    }

    /**
     * Preprocesses <CODE>event</CODE> prior to process
     * it to UI's mousePressed(MouseEvent), especially
     * identify <CODE>target</CODE> component to dispatch
     * <CODE>MouseEvent</CODE>s.  The UI's
     * mousePressed(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public boolean preMousePressed(MouseEvent event)
    {
	setTarget(getTarget(event));
	setDragging(false);
	dispatchEvent(event);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after 
     * UI's mousePressed(MouseEvent).  The UI's
     * mouseClicked(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mousePressed(MouseEvent)
     */
    public void postMousePressed(MouseEvent event) {}

    public boolean preMouseReleased(MouseEvent event)
    {
	dispatchEvent(event);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after 
     * UI's mouseRelased(MouseEvent) by removing
     * references to the <CODE>target</CODE>.  The UI's
     * mouseRelased(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseRelased(MouseEvent)
     */
    public void postMouseReleased(MouseEvent event)
    {
	setTarget(null);
    }

    /**
     * Preprocesses <CODE>event</CODE> prior to 
     * UI's mouseEntered(MouseEvent), especially
     * identify <CODE>target</CODE> component to dispatch
     * <CODE>MouseEvent</CODE>s.  The UI's
     * mouseEntered(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public boolean preMouseEntered(MouseEvent event)
    {
	setTarget(getTarget(event));
	dispatchEvent(event);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after calling
     * UI's mouseEntered(MouseEvent).  The UI's
     * mouseEntered(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseEntered(MouseEvent)
     */
    public void postMouseEntered(MouseEvent event) {}

    /**
     * Preprocesses <CODE>event</CODE> prior to 
     * UI's mouseExited(MouseEvent).  The UI's
     * mouseExited(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public boolean preMouseExited(MouseEvent event)
    {
	dispatchEvent(event);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after calling
     * UI's mouseExited(MouseEvent).  It unlinks from
     * the dispatching target <CODE>Component</CODE>.  
     * The UI's mouseExited(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseExited(MouseEvent)
     */
    public void postMouseExited(MouseEvent event)
    {
	setTarget(null);
	setEditor(null);
    }

    /**
     * Preprocesses <CODE>event</CODE> prior to 
     * UI's mouseMoved(MouseEvent), especially
     * identify <CODE>target</CODE> component to dispatch
     * <CODE>MouseEvent</CODE>s.  The UI's
     * mouseMoved(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mouseMoved(MouseEvent)
     */
    public boolean preMouseMoved(MouseEvent event)
    {
	setTarget(getTarget(event));
	dispatchEvent(event);
	return true;
    }

    /**
     * Postprocesses <CODE>event</CODE> after calling
     * UI's mouseMoved(MouseEvent).  The UI's
     * mouseMoved(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseMoved(MouseEvent)
     */
    public void postMouseMoved(MouseEvent event) {}

    /**
     * Preprocesses <CODE>event</CODE> prior to 
     * UI's mouseDragged(MouseEvent).  The UI's
     * mouseDragged(MouseEvent) should call this method
     * prior to calling its original method or method of
     * its super class.  Returns false to tell the caller
     * to skip its original or super class' method.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @return false to skip caller's original or super class' method.
     *
     * @see java.awt.event.MouseListener#mouseDragged(MouseEvent)
     */
    public boolean preMouseDragged(MouseEvent event)
    {
	setDragging(true);
	dispatchEvent(event);

	if(target == null ||
	   (!(target instanceof JSlider) &&
	    !(target instanceof Adjustable)
	    )
	   ) {
	    return true;
	}

	int v;
	if(!(target instanceof JScrollBar)) {
	    if(target instanceof JSlider) {
		v = ((JSlider)target).getValue();
		Rectangle r = getHeaderRect(event);
		if(r != null) {
		    header.repaint(r);
		}
	    }
	    return false;
	}
	
	v = ((JScrollBar)target).getValue();
	
	Rectangle r = getHeaderRect(event);
	if(r != null) {
	    header.repaint(r);
	}
	return false;
    }

    /**
     * Postprocesses <CODE>event</CODE> after calling
     * UI's mouseDragged(MouseEvent).  The UI's
     * mouseDragged(MouseEvent) should call this method
     * after calling its original method or method of
     * its super class.
     *
     * @param event <CODE>MouseEvent</CODE> to be processed
     *
     * @see java.awt.event.MouseListener#mouseDragged(MouseEvent)
     */
    public void postMouseDragged(MouseEvent event)
    {
	setDragging(false);
    }

    /**
     * Converts <CODE>p</CODE> from screen coordinate to
     * <CODE>Component</CODE> <CODE>c</CODE> coordiante.
     *
     * @param p <CODE>Point</CODE> to covnert
     * @param c target <CODE>Componenet</CODE>
     *
     * @see javax.swing.SwingUtilities#convertPointFromScreen(Point, Component)
     */
    public void convertPointFromScreen(Point p, Component c)
    {
	Rectangle b;
	int x,y;
	
	do {
	    if(c instanceof JComponent) {
		x = ((JComponent)c).getX();
		y = ((JComponent)c).getY();
	    } else if(c instanceof java.applet.Applet) {
		Point pp = c.getLocationOnScreen();
		x = pp.x;
		y = pp.y;
	    } else {
		b = c.getBounds();
		x = b.x;
		y = b.y;
	    }
	    
	    if(c instanceof TreeHeaderRenderer) {
		//c = header;
		if(!dragging) {
		    //record position of renderer
		    rendererX = x;
		    rendererY = y;
		}
		
		else {
		    p.x -= rendererX;
		    p.y -= rendererY;;
		    c = header.getTable();
		    continue;
		}
	    }
	    
	    p.x -= x;
	    p.y -= y;
	    
	    if(c instanceof java.awt.Window || c instanceof java.applet.Applet)
		break;
	    c = c.getParent();
	} while(c != null);
    }
    
    /**
     * It will be called when listener's status changed.
     *
     * @param event <CODE>ChangeEvent</CODE> informing the change
     *
     * @see javax.swing.event.ChangeListener#stateChanged(ChangeEvent)
     */
    public void stateChanged(ChangeEvent event)
    {
	if(target != null) {
	    //header.repaint();
	}
    }

    /**
     * Dispatchs <CODE>event</CODE> to the <CODE>target</CODE>
     *
     * @param event <CODE>MouseEvent</CODE> to be dispateched
     */
    protected void dispatchEvent(MouseEvent event)
    {
	dispatchEvent(target, event);
    }
    
    /**
     * Dispatchs <CODE>event</CODE> to the <CODE>target</CODE>
     *
     * @param target <CODE>Component</CODE> to which the
     * <CODE>event</CODE> to be dispateched
     * @param event <CODE>MouseEvent</CODE> to be dispateched
     */
    protected void dispatchEvent(Component target, MouseEvent event)
    {
	if(target == null)
	    return;
	/*
	  int x = e.getX();
	  int y = e.getY();
	  
	  Point p = new Point(e.getX(), e.getY());
	  SwingUtilities.convertPointToScreen(p, header);
	  
	  //workaround for editor handling
	  convertPointFromScreen(p, target);
	  target.dispatchEvent(new MouseEvent(target,
	  e.getID(),
	  e.getWhen(),
	  e.getModifiers(),
	  p.x,p.y,
	  e.getClickCount(),
	  e.isPopupTrigger()));
	*/
	target.dispatchEvent(convertMouseEvent(event, target));
    }
    
    /**
     * Converts <CODE>event</CODE> to a <CODE>MouseEvent</CODE>
     * in the <CODE>destination</CODE>
     *
     * @param event source <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(MouseEvent event,
					Component destination)
    {
	return convertMouseEvent(header, event, destination);
    }
    
    /**
     * Converts <CODE>event</CODE> on <CODE>source</CODE> to a
     * <CODE>MouseEvent</CODE> in the <CODE>destination</CODE>
     *
     * @param source where the <CODE>event</CODE> happened
     * @param event <CODE>source</CODE> <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(Component source, 
					MouseEvent event,
					Component destination)
    {
	int x = event.getX();
	int y = event.getY();
	
	Point p = new Point(event.getX(), event.getY());
	SwingUtilities.convertPointToScreen(p, source);
	
	//workaround for editor handling
	convertPointFromScreen(p, destination);
	return new MouseEvent(destination,
			      event.getID(),
			      event.getWhen(),
			      event.getModifiers(),
			      p.x, p.y,
			      event.getClickCount(),
			      event.isPopupTrigger());
    }
    
    /**
     * Returns column index where the <CODE>event</CODE>
     * happened
     *
     * @param event to identify its column index
     *
     * @return int representing a column index where <CODE>event</CODE>
     * happened 
     */
    protected int getColumnIndex(MouseEvent event)
    {
	return header.getColumnModel().getColumnIndexAtX(event.getX());
    }
    
    /**
     * Returns <CODE>Rectangle</CODE> around the header cell
     * where the <CODE>event</CODE> happened, or null if it
     * is not on the header
     *
     * @param event to identify its header <CODE>Rectangle</CODE>
     *
     * @return Rectangle surrounding the header cell where
     * <CODE>event</CODE> happened, or null if it
     * is not on the header
     *
     */
    protected Rectangle getHeaderRect(MouseEvent event)
    {
	int index = getColumnIndex(event);
	
	if(index > -1)
	    return header.getHeaderRect(index);
	return null;
    }
    
    /**
     * Returns deepest <CODE>Component</CODE> in the header
     * to which the <CODE>event</CODE> to be dispatched
     *
     * @param event to dispatched to the target
     *
     * @return Component to which the <CODE>event</CODE> to be
     * dispatched, or null if the <CODE>event</CODE> happened
     * outside of the header
     */
    protected Component getTarget(MouseEvent event)
    {
	TableColumnModel model = header.getColumnModel();
	
	int index =
	    model.getColumnIndexAtX(event.getX());
	
	if(index == -1 || 
	   !((HeaderEditableTable)header.getTable()).editCellAt(-1, index, event)) {
	    return null;
	}
	
	HeaderEditableTableColumn c = 
	    (HeaderEditableTableColumn)model.getColumn(index);
	
	//Component cellEditor = 
	cellEditor =
	    c.getHeaderEditor().getTableCellEditorComponent(header.getTable(),
							    c.getHeaderValue(),
							    true, -1, index);
	Point p = 
	    SwingUtilities.convertPoint(header, event.getPoint(), cellEditor);
	
	return SwingUtilities.getDeepestComponentAt(cellEditor, p.x, p.y);
    }
    
}

