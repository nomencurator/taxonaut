/*
 * ViewportSynchronizer.java:  synchronize JViewports
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Point;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;

/**
 * <CODE>ViewportSynchronizer</CODE> synchronizes
 * <CODE>JViewport</CODE>s
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class ViewportSynchronizer
    implements ChangeListener
{
    public static final int SYNCHRONIZE_X = 1;
    public static final int SYNCHRONIZE_Y = 2;
    public static final int SYNCHRONIZE_BOTH =
	SYNCHRONIZE_X | SYNCHRONIZE_Y;
    
    /**
     * <CODE>Object</CODE> invoked a
     * <CODE>ChangeEvent</CODE> under process
     */
    protected Object eventSource;

    protected boolean scrollSynchronize;

    protected int synchronizeDirection;

    protected List<JViewport> viewports;

    protected Set<JViewport> viewPositions;

    public ViewportSynchronizer()
    {
	this(SYNCHRONIZE_BOTH);
    }

    public ViewportSynchronizer(int direction)
    {
	this(direction, false);
    }

    public ViewportSynchronizer(boolean toSynchronize)
    {
	this(SYNCHRONIZE_BOTH, toSynchronize);
    }

    public ViewportSynchronizer(int direction, boolean toSynchronize)
    {
	setSynchronizeDirection(direction);
	setScrollSynchronize(toSynchronize);
    }

    public int getSynchronizeDirection()
    {
	return synchronizeDirection;
    }

    public void setSynchronizeDirection(int direction)
    {
	synchronizeDirection = direction;
    }

    public boolean isScrollSynchronized()
    {
	return scrollSynchronize;
    }

    public synchronized void setScrollSynchronize(boolean toSynchronize)
    {
	updateViewPositions();

	if(scrollSynchronize == toSynchronize)
	    return;

	scrollSynchronize = toSynchronize;
    }

    protected synchronized  void setEventSource(Object source)
    {
	eventSource = source;
    }

    protected Object getEventSource()
    {
	return eventSource;
    }

    public void updateViewPositions()
    {
	updateViewPositions(null);
    }

    public void updateViewPositions(Point point)
    {
	if(viewPositions == null)
	    return;

	for (JViewport vp : viewports) {
	    if(point != null &&
	       vp != getEventSource()) {
		Point p = vp.getViewPosition();
		p.x += point.x;
		p.y += point.y;
		vp.setViewPosition(p);
	    }
	    viewPositions.add(vp);
	}
    }

    public void add(Component c)
    {
	JViewport vp = getViewport(c);
	if(vp == null)
	    return;

	if(viewPositions == null) {
	    viewPositions = new HashSet<JViewport>();
	    viewports = new ArrayList<JViewport>();
	}

	if(!viewPositions.contains(vp))
	    viewports.add(vp);

	viewPositions.add(vp);
    }

    public void remove(Component c)
    {
	JViewport vp = getViewport(c);
	if(vp == null)
	    return;

	viewports.remove(vp);
	viewPositions.remove(vp);

	if(viewPositions.isEmpty()) {
	    viewPositions = null;
	    viewports = null;
	}

    }

    public static JViewport getViewport(Component c)
    {
	while(c != null &&
	      !(c instanceof JViewport)) {
	    c = c.getParent();
	}

	if (c == null)
	    return null;
	return (JViewport)c;
	/*
	c = c.getParent();
	if(!(c instanceof JScrollPane))
	    return null;
	return ((JScrollPane)c).getViewport();
	*/
    }



    public void stateChanged(ChangeEvent event)
    {
	if(getEventSource() != null ||
	   viewPositions == null ||
	   viewPositions.isEmpty())
	    return;

	setEventSource(event.getSource());

	JViewport viewport = 
	    (JViewport)event.getSource();

	Point current = viewport.getViewPosition();

	if(!isScrollSynchronized()) {
	    setViewPositions(current);
	}
	else {
	    Point previous = viewPositions.contains(viewport)? viewport.getViewPosition() : null;
	    if(previous != null &&
	       current != null) {
	    
	    Point delta = null;
	    switch(getSynchronizeDirection()) {
	    case 1:
		if(current.x != previous.x)
		    delta = new Point(current.x - previous.x, 0);
		break;
	    case 2:
		if(current.y != previous.y)
		    delta = new Point(0, current.y - previous.y);
		break;
	    default:
		if(current.x != previous.x || 
		   current.y != previous.y) {
		    delta = new Point();
		    if(current.x != previous.x)
			delta.x = current.x - previous.x;
		    if(current.y != previous.y)
			delta.y = current.y - previous.y;
		}
	    }
	    updateViewPositions(delta);
	    }
	}

	setEventSource(null);
    }

    public void setViewPositions(Point point)
    {
	if(viewPositions == null)
	    return;

	Iterator<JViewport> v = viewports.iterator();
	while(v.hasNext()) {
	    JViewport vp = v.next();
	    if(vp == getEventSource())
		continue;
	    if(point != null) {
		vp.setViewPosition(point);
	    }
	    viewPositions.add(vp);
	}
    }

}
