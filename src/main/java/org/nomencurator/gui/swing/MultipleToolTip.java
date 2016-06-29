/*
 * MultiLinesToolTip.java:  a JToolTip supporting multiple lines
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

import java.awt.Graphics;

import java.util.Iterator;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JToolTip;

import org.nomencurator.gui.swing.plaf.metal.MetalMultiLinesToolTipUI;

/**
 * {@code MultiLinesToolTip} provides multiple lined {@code JToolTip}
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class MultipleToolTip
    extends JToolTip
{
    private static final long serialVersionUID = 2912583985509214383L;

    private static final String uiClassID = "MultipleToolTipUI";

    protected Set<JToolTip> toolTips;

    public MultipleToolTip()
    {  
	super();
    }

    public void paintComponent(Graphics g)
    {
	if(toolTips == null || ui == null)
	    return;
	    
	Iterator<JToolTip> e = toolTips.iterator();
	/*
	while(e.hasNext()) {
	    (JToolTip)e.next().paintComponent(g);
	}
	*/
    }

    public void add(JToolTip toolTip)
    {
	if(toolTips == null)
	    toolTips = new HashSet<JToolTip>();
	if(!toolTips.contains(toolTip))
	    toolTips.add(toolTip);
    }

    public void remove(JToolTip toolTip)
    {
	if(toolTips == null)
	    return;
	
	toolTips.remove(toolTip);
	if(toolTips.isEmpty())
	    toolTips = null;
    }

    /*
    public String getUIClassID()
    {
        return uiClassID;
    }
    */

}

