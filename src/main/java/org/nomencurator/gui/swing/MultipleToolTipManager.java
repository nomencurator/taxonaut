/*
 * MultipleToolTipManager.java: multicasts a MouseEvent to registerd JComponents
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

package org.nomencurator.gui.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.ToolTipManager;

/**
 * <tt>MultipleToolTipManager</tt> multicasts a <tt>MouseEvent</tt> to registerd <tt>JComponent</tt>s.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class MultipleToolTipManager
    extends MouseAdapter
    implements MouseMotionListener
{
    /** <tt>JComponents</tt> to which multicasts to */
    Set<JComponent> managers;

    /**
     * Constructs a <tt>MultipleToolTipManager</tt> without specifying event souces.
     */
    public MultipleToolTipManager()
    {
	this(null);
    }

    /**
     * Constructs a <tt>MultipleToolTipManager</tt> with specified <tt>JComponent</tt> as the event souce.
     *
     * @param c event soruce <tt>JComponent</tt> 
     */
    public MultipleToolTipManager(JComponent c)
    {
	addEventSource(c);
    }

    /**
     * Adds specified <tt>JComponent</tt> as an event souce.
     *
     * @param c event soruce to be added.
     */
    public void addEventSource(JComponent c)
    {
	c.addMouseListener(this);
	c.addMouseMotionListener(this);
    }

    /**
     * Removes specified <tt>JComponent</tt> from event souces.
     *
     * @param c event soruce to be removed
     */
    public void removeEventSource(JComponent c)
    {
	c.removeMouseListener(this);
	c.removeMouseMotionListener(this);
    }

    /**
     * Adds specified <tt>JComponent</tt> as a target of event multicast.
     *
     * @param c multicast target to be added.
     * @return true if the method modified the set of targts
     */
    public void add(JComponent c)
    {
	if(managers == null)
	    managers = new HashSet<JComponent>();
	managers.add(c);
    }

    /**
     * Removes specified <tt>JComponent</tt> from targets of event multicast.
     *
     * @param c multicast target to be removed
     * @return true if the method modified the set of targts
     */
    public boolean remove(JComponent c)
    {
	if(managers == null)
	    return false;
	return managers.remove(c);
    }

    public void mousePressed(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseDragged(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    public void mouseMoved(MouseEvent event)
    {
	dispachMouseEvent(event);
    }

    /**
     * Multicast <tt>event</tt> to registerd targets.
     *
     * @param event <tt>MouseEvent</tt> to multicast
     */
    protected void dispachMouseEvent(MouseEvent event)
    {
	if(managers == null)
	    return;

	for (JComponent c : managers)  {
	    c.dispatchEvent(event);
        }
    }

}
