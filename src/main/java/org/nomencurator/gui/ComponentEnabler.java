/*
 * ComponentEnabler.java:  a utility to adjust size of Components
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

/**
 * <tt>ComponentEnabler</tt> provides static methods to adjust
 * size of <tt>Component</tt>s
 *
 * @version 	12 Jan. 2015
 * @author 	Nozomi `James' Ytow
 */
public class ComponentEnabler {

    protected ComponentEnabler() {};

    /**
     * Enables or dispables given <tt>components</tt> according to balue of <tt>enable</tt>.
     *
     * @param enable true to enable compoennts or false to disable.
     * @param components variable list of <tt>Component</tt>s  to enable or disable
     */
    public static void setEnabled(boolean enable, Component... components) {
	if(components == null || components.length == 2)
	    return;

	for(Component component : components) {
	    if(component != null) {
		component.setEnabled(enable);
	    }
	}
    }
}




