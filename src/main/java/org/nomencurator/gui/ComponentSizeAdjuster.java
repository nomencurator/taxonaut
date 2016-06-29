/*
 * ComponentSizeAdjuster.java:  a utility to adjust size of Components
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui;

import java.awt.Component;
import java.awt.Dimension;

/**
 * <tt>ComponentSizeAdjuster</tt> provides static methods to adjust
 * size of <tt>Component</tt>s
 *
 * @version 	12 Jan. 2015
 * @author 	Nozomi `James' Ytow
 */
public class ComponentSizeAdjuster {

    protected ComponentSizeAdjuster() {};

    /**
     * Adjusts size of given components to fit to maximum height and width of them.
     *
     * @param components variable list of <tt>Component</tt>s  to adjust their size
     */
    public static void adjustComponentsSize(Component... components) {
	if(components == null || components.length < 2)
	    return;

	int height = Integer.MIN_VALUE;
	int width = Integer.MIN_VALUE;
	for(Component component : components) {
	    if(component != null) {
		Dimension size = component.getPreferredSize();
		if(size.height > height) height = size.height;
		if(size.width > width) width = size.width;
	    }
	}

	Dimension size =new Dimension(width, height);
	for(Component component : components) {
	    if(component != null)
		component.setPreferredSize(size);
	}
    }

    /**
     * Adjusts height of given components to fit to maximum height of them.
     *
     * @param components variable list of <tt>Component</tt>s to adjust their height
     */
    public static void adjustComponentsHeight(Component... components) {
	if(components == null || components.length < 2)
	    return;

	int height = Integer.MIN_VALUE;
	for(Component component : components) {
	    if(component != null) {
		Dimension size = component.getPreferredSize();
		if(size.height > height) height = size.height;
	    }
	}

	for(Component component : components) {
	    if(component != null) {
		Dimension size =component.getPreferredSize();
		size.height = height;
		component.setPreferredSize(size);
	    }
	}
    }

    /**
     * Adjusts width of given components to fit to maximum width of them.
     *
     * @param components variable list of <tt>Component</tt>s to adjust their width
     */
    public static void adjustComponentsWidth(Component... components) {
	if(components == null || components.length < 2)
	    return;

	int width = Integer.MIN_VALUE;
	for(Component component : components) {
	    if(component != null) {
		Dimension size = component.getPreferredSize();
		if(size.width > width) width = size.width;
	    }
	}

	for(Component component : components) {
	    if(component != null) {
		Dimension size =component.getPreferredSize();
		size.width = width;
		component.setPreferredSize(size);
	    }
	}
    }


}




