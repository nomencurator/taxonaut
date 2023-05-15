/*
 * MetalMultiLinesToolTipUI.java:  a Metal UI supporting multiple lines
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing.plaf.metal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JToolTip;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.metal.MetalToolTipUI;

import org.nomencurator.gui.swing.plaf.MultiLinesToolTipAdaptor;

/**
 * <CODE>MetalMultiLinesToolTipAdaptor</CODE> provides a UI for
 * multiple lined  <code>JToolTip</code>
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class MetalMultiLinesToolTipUI
    extends MetalToolTipUI
{
    static MetalMultiLinesToolTipUI sharedInstance = 
	new MetalMultiLinesToolTipUI();

    public static ComponentUI createUI(JComponent c)
    {
        return sharedInstance;
    }

    public void paint(Graphics g, JComponent c)
    {
	MultiLinesToolTipAdaptor.paint(g, c,
				       ((JToolTip)c).getTipText(),
				       getAcceleratorString());
    }
    
    public Dimension getPreferredSize(JComponent c)
    {
	return MultiLinesToolTipAdaptor.getPreferredSize(c,
							  ((JToolTip)c).getTipText(),
							  getAcceleratorString());
    }
}

