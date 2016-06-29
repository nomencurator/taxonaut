/*
 * LookAndFeelManager.java:  a class manage LookAndFeel relevant constants
 *
 * Copyright (c) 2005, 2015, 2016 Nozomi `James' Ytow
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.lang.reflect.Method;

import javax.swing.UIManager;

/**
 * <CODE>LookAndFeelManager</CODE> provides 
 * a utility method to manage look and feel of
 * <CODE>JComponent</CODE>s
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LookAndFeelManager
{
    public static final String PROPERTY_NAME = "lookAndFeel";

    public static final String MOTIF = "Motif";

    public static final String WINDOWS = "Windows";

    public static final String MAC = "Mac";

    public static final String METAL = "Metal";

    public static final String GTK = "GTK";

    public static boolean isLookAndFeelEvent(PropertyChangeEvent event)
    {
	return !PROPERTY_NAME.equals(event.getPropertyName());
    }

    protected static boolean isA(String id)
    {
	return UIManager.getLookAndFeel().getID().equals(id);
    }

    public static boolean isMotif()
    {
	return isA(MOTIF);
    }

    public static boolean isWindows()
    {
	return isA(WINDOWS);
    }

    public static boolean isMac()
    {
	return isA(MAC);
    }

    public static boolean isMetal()
    {
	return isA(METAL);
    }

    public static boolean isGTK()
    {
	return isA(GTK);
    }

}
    

