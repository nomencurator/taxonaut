/*
 * ScrollPaneResizeAdaptor.java:  an adaptor to resize a ScrollPane
 * tooltip
 *
 * Copyright (c) 2003, 2014, 2015 Nozomi `James' Ytow
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

import java.awt.Cursor;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

import javax.swing.event.MouseInputAdapter;

/**
 * <tt>ScrollPaneResizeAdaptor</tt> provides an addaptor
 * to resize a <tt>ScrollPane</tt>
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class ScrollPaneResizeAdaptor
    extends MouseInputAdapter
{
    public static Cursor eastCursor = 
	Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR); 

    public static Cursor westCursor = 
	Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR); 

    public static Cursor northCursor = 
	Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR); 

    public static Cursor northEastCursor = 
	Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR); 

    public static Cursor northWestCursor = 
	Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR); 

    public static Cursor southCursor = 
	Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR); 

    public static Cursor southEastCursor = 
	Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR); 

    public static Cursor southWestCursor = 
	Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR); 

    protected Point point;

    protected JScrollPane scrollPane;

    protected Cursor cursor;

    protected Cursor systemCursor;

    protected boolean repaintWhileDrag = true;

    public ScrollPaneResizeAdaptor()
    {
	this(null);
    }

    public ScrollPaneResizeAdaptor(JScrollPane scrollPane)
    {
	setScrollPane(scrollPane);
    }

    public void setScrollPane(JScrollPane scrollPane)
    {
	if(this.scrollPane == scrollPane)
	    return;

	this.scrollPane = scrollPane;
    }

    protected Cursor getResizingCursor(MouseEvent event)
    {
	Point point = event.getPoint();

	Dimension viewDim = scrollPane.getSize();

	//Rectangle viewRect = scrollPane.getViewportBorderBounds();
	Rectangle viewRect = new Rectangle(0, 0, viewDim.width, viewDim.height);

	JViewport columnHeader = scrollPane.getColumnHeader();
	JViewport rowHeader = scrollPane.getRowHeader();

	if(columnHeader == null &&
	   rowHeader == null)
	    return null;

	Point viewCorner = new Point();

	Rectangle columnRect = null;
	if(columnHeader != null &&
	   columnHeader.isVisible()) {
	    Dimension dim = columnHeader.getSize();
	    columnRect = new Rectangle(0, 0, dim.width, dim.height);
	    viewCorner.y = columnRect.height;
	    columnRect.height -= 3;
	    viewRect.y += columnRect.height;
	    viewRect.height -= columnRect.height;
	}

	Rectangle rowRect = null;
	if(rowHeader != null &&
	   rowHeader.isVisible()) {
	    Dimension dim = rowHeader.getSize();
	    rowRect = new Rectangle(0, 0, dim.width, dim.height);
	    viewCorner.x = rowRect.width;
	    rowRect.width -= 3;
	    viewRect.x += rowRect.width;
	    viewRect.width -= rowRect.width;
	    if(columnRect != null) {
		rowRect.y = columnRect.height + 3;
		columnRect.y = dim.height;
	    }
	}


	Rectangle cornerRect = null;
	Component corner = scrollPane.getCorner(JScrollPane.UPPER_LEADING_CORNER);
	if(corner != null &&
	   corner.isVisible()) {
	    Dimension dim = corner.getSize();
	    cornerRect = 
		new Rectangle(dim.width - 3, 
			      dim.height - 3);
	}
	else if (viewCorner.x != 0 && viewCorner.y != 0) {
	    cornerRect = 
		new Rectangle(0, 0, viewCorner.x -3, viewCorner.y -3);
	}

	if( viewRect.contains(point) ||
	    (columnRect != null && columnRect.contains(point)) ||
	    (rowRect != null && rowRect.contains(point)) ||
	   (cornerRect != null && cornerRect.contains(point)) ) {
	    return null;
	}
	
	point  = new Point(viewCorner.x -point.x,
			   viewCorner.y - point.y);
	if(viewCorner.x == 0) {
	    //columnHeader only
	    point.x = 0;
	}
	else if(viewCorner.y == 0) {
	    //rowHeader only
	    point.y = 0;
	}
	else {
	    if (point.y <= columnRect.height ||
		point.y >= viewRect.y) {
		point.y = 0;
	    }
	    else if (point.x <= rowRect.width ||
		     point.x >= viewRect.x) {
		point.x = 0;
	    }
	}

	return getResizingCursor(point);
    }

    protected Cursor getResizingCursor(Point delta)
    {
	if (delta.x == 0 && delta.y == 0)
	    return null;

	else if (delta.x == 0) {
	    if(delta.y < 0)
		return northCursor;
	    else
		return southCursor;
	}
	else if (delta.y == 0) {
	    if(delta.x < 0)
		return westCursor;
	    else
		return eastCursor;
	}
	else {
	    if(delta.x > 0) {
		if(delta.y > 0)
		    return southEastCursor;
		else
		    return southWestCursor;
	    }
	    else {
		if(delta.y > 0)
		    return northEastCursor;
		else
		    return northWestCursor;
	    }
	}
    }

    public void mouseEntered(MouseEvent event)
    {
    }

    public void mousePressed(MouseEvent event)
    {
	if(scrollPane == null)
	    return;

	cursor = getResizingCursor(event);
	if(cursor == null)
	    return;
	point = event.getPoint();
    }

    public void mouseReleased(MouseEvent event)
    {
	if(scrollPane == null)
	    return;
	setCursor(null);
	point = event.getPoint();
	if(repaintWhileDrag)
	    resizeComponents(scrollPane, getMovement(point, event.getPoint()));
	else
	point = null;
    }

    public void mouseMoved(MouseEvent event)
    { 

    }

    public void mouseDragged(MouseEvent event)
    {
	if(scrollPane == null)
	    return;
	Point delta = 
	    getMovement(point, event.getPoint());

	point = event.getPoint();

	if(delta == null)
	    return;

	Cursor c = getResizingCursor(delta);
	if(c != null)
	    setCursor(c);

	if(!repaintWhileDrag)
	    return;

	resizeComponents(scrollPane, delta);
    }

    protected Point getMovement(Point previous, Point current)
    {
	if(previous == null ||
	   current == null)
	    return null;

	Point delta = previous;

	delta.x = current.x - previous.x;
	delta.y = current.y - previous.y;

	return delta;
    }

    protected void setCursor(Cursor cursor)
    {
	if(this.cursor == cursor ||
	   scrollPane == null)
	    return;
	if(this.cursor == null)
	    systemCursor = scrollPane.getCursor();
	this.cursor = cursor;
	if(cursor == null)
	    scrollPane.setCursor(systemCursor);
	else
	    scrollPane.setCursor(cursor);
	

    }

    protected void resizeComponents(JScrollPane scrollPane, Point delta)
    {
	if(delta == null)
	    return;
	resizeComponent(scrollPane.getViewport(), delta, -1, -1);
	resizeComponent(scrollPane.getColumnHeader(), delta, -1, 1);
	resizeComponent(scrollPane.getRowHeader(), delta, 1, -1);
	resizeComponent(scrollPane.getCorner(JScrollPane.UPPER_LEADING_CORNER), delta, 1, 1);
	scrollPane.repaint();
    }

    protected void resizeComponent(Component c, Point delta, int signX, int signY)
    {
	if(c == null || delta == null ||
	   (delta.x == 0 && delta.y == 0))
	    return;

	Dimension size = c.getSize();
	size.width += delta.x * signX;
	size.height += delta.y * signY;

	Dimension limit = c.getMaximumSize();
	if(size.width > limit.width)
	    size.width = limit.width;
	if(size.height > limit.height)
	    size.height = limit.height;

	limit = c.getMinimumSize();
	if(size.width < limit.width)
	    size.width = limit.width;
	if(size.height < limit.height)
	    size.height = limit.height;

	c.setSize(size);
    }

}

