/*
 * LookAndFeelUtilities.java:  Utility methods to manage Look and Feel
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

import java.lang.reflect.InvocationTargetException;
		  
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.HashMap;
import java.util.Map;

import java.lang.reflect.Method;

import javax.swing.JComponent;
import javax.swing.LookAndFeel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import org.nomencurator.gui.swing.plaf.UIBroaker;

//import sun.reflect.misc.MethodUtil;

/**
 * {@code LookAndFeelUtilities} provides utility methods to manage {@code LookAndFeel}.
 *
 * @version 	09 May 2022
 * @author 	Nozomi `James' Ytow
 */
public class LookAndFeelUtilities
    implements PropertyChangeListener
{
    private LookAndFeelUtilities(){}

    protected static LookAndFeelUtilities broker;

    protected static Map<Class<? extends JComponent>, Method> methods;

    static {
	broker = new LookAndFeelUtilities();
	UIManager.addPropertyChangeListener(broker);
    }

    /**
     * {@inheritDoc}
     */
    public void propertyChange(PropertyChangeEvent event)
    {
	Object newValue = event.getNewValue();
	if (!(newValue instanceof LookAndFeel)) return;
	    
	LookAndFeel newLaF = (LookAndFeel)newValue;
	//LookAndFeel oldLaF = (LookAndFeel)oldValue;	

	if (methods == null || methods.isEmpty())
	    return;

	for (Method m : methods.values()) {
	    try {
		m.invoke(null, newLaF);
	    }
	    catch (IllegalAccessException
                     | IllegalArgumentException
		   | InvocationTargetException ex) {
		ex.printStackTrace();
	    }
	}
    }

    public static void put(Class<? extends JComponent> component, String methodName)
    {
	if (component == null) return;
	
	if (methods == null)
	    methods = new HashMap<Class<? extends JComponent>, Method>();
	try {
	    Method m = component.getMethod(methodName, LookAndFeel.class);
	    methods.put(component, m);
	} catch (NoSuchMethodException ex) {
	    ex.printStackTrace();
	}
    }

    public static void remove (Class<? extends JComponent> component)
    {
	if (component == null || methods == null) return;

	methods.remove(component);
    }

    /**
     * Returns {@code LookAndFeel} instance class name of given {@code shortName}, or null if it si not installed in the VM.
     *
     * @param shorName of which {@code LookAndFeel} instence to look up
     * @return class name of {@code LookAndFeel} instence class name having {@code shortNam}
     */
    public static String getLookAndFeelClassName(String shortName)
    {
	String className = null;

	LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();

	if (shortName != null && infoArray != null && infoArray.length > 0) {
	    for (LookAndFeelInfo info : infoArray) {
		if (className == null && shortName.equals(info.getName())) {		
		    className = info.getClassName();
		    break;
		}
	    }
	}
	return className;
    }

    public static LookAndFeel getLookAndFeel(String shortName)
    {
	LookAndFeel lookAndFeel = null;	
	if (shortName != null) {
	    String className = getLookAndFeelClassName(shortName);
	    if (className != null) {
		LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
		if (className.equals(currentLookAndFeel.getClass().getName()))
		    lookAndFeel = currentLookAndFeel;
		else {
		    try {	    
			UIManager.setLookAndFeel(className);
			lookAndFeel = UIManager.getLookAndFeel();
			if (lookAndFeel != currentLookAndFeel)
			    UIManager.setLookAndFeel(currentLookAndFeel);
		    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
			
		    }
		}
	    }
	}
	return lookAndFeel;
    }

    public static UIDefaults getDefaults(String shortName)
    {
	UIDefaults defaults = null;
	LookAndFeel lookAndFeel = getLookAndFeel(shortName);
	if (lookAndFeel != null)
	    defaults = lookAndFeel.getDefaults();
	return defaults;
    }

}
