/*
 * MetalAlignerTreeUI.java:  a TreeUI with node alignment between
 * trees
 *
 * Copyright (c) 2003, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.plaf.metal;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.metal.MetalScrollPaneUI;

import org.nomencurator.gui.swing.plaf.ScrollPaneResizeAdaptor;

/**
 * {@code MetalResizableScrollPaneUI} provides a resizable
 * {@code MetalScrollPaneUI}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class MetalResizableScrollPaneUI
    extends MetalScrollPaneUI
{
    protected ScrollPaneResizeAdaptor resizer;

    public static ComponentUI createUI(JComponent c)
    {
	return new MetalResizableScrollPaneUI();
    }

    public void installUI(JComponent c)
    {
        super.installUI(c);
    }

    public void installListeners(JScrollPane scrollPane)
    {
        super.installListeners(scrollPane);
	resizer = new ScrollPaneResizeAdaptor(scrollPane);
	scrollPane.addMouseListener(resizer);
	scrollPane.addMouseMotionListener(resizer);
    }


    protected void uninstallListeners(JComponent scrollPane)
    {
	scrollPane.removeMouseMotionListener(resizer);
	scrollPane.removeMouseListener(resizer);
        super.uninstallListeners(scrollPane);
    }


}
