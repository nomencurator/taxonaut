/*
 * UniqueEventListener.java:  an EventListener guarantees
 * uniqueness of listener for each <CODE>EventListener</CODE> sub-type
 *
 * Copyright (c) 2003, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069, JP17300071
 */

package org.nomencurator.gui.swing.event;

//import java.util.Enumeration;
import java.util.EventListener;

import javax.swing.event.EventListenerList;


/**
 * <CODE>UniqueEventListenerList</code> is an <CODE>EventList</CODE>
 * with guarantee of non-redundant listener list of the same event listener
 * sub-type
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class UniqueEventListenerList
    extends EventListenerList
{
    private static final long serialVersionUID = 5669904401375401348L;

    public UniqueEventListenerList()
    {
	super();
    }

    public <T extends EventListener> void add(Class<T> listenerType, T listener)
    {
	synchronized(listenerList) {
	    T[] currentListeners =
		getListeners(listenerType);

	    for(T currentListener : currentListeners) {
		if(listener == currentListener)
		    return;
	    }
	    
	    super.add(listenerType, listener);
	}
    }

    /*
    public Enumeration getListenersOf(Class eventType)
    {
	if(listeners == null)
	    return null;
	Hashtable typeListeners =
	    (Hashtable)listeners.get(eventType);
	if(typeListeners == null)
	    return null;

	return typeListeners.elements();
    }

    public synchronized void removeListenersOf(Class eventType)
    {
	Enumeration e = getListenersOf(eventType);
	if(e == null)
	    return;

	while(e.hasMoreElements()) {
	    super.remove(eventType, 
			 (EventListener)e.nextElement());
	}

	((Hashtable)listeners.get(eventType)).clear();
	listeners.remove(eventType);
	if(listeners.isEmpty())
	    listeners = null;
    }
    */
}
