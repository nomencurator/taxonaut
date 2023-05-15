/*
 * ResizableScrollPane.java:  a resizable ScrollPane
 *
 * Copyright (c) 2002, 2003, 2005, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.awt.Component;

import javax.swing.JScrollPane;
import javax.swing.UIManager;

import javax.swing.plaf.ScrollPaneUI;

import org.nomencurator.gui.swing.plaf.metal.MetalResizableScrollPaneUI;

/**
 * {@code ResizableScrollPane} provides a resizable {@code JScrollPane}
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ResizableScrollPane
    extends JScrollPane
{
    private static final long serialVersionUID = 3566395369735542410L;

    /* User interface class ID */
    private static final String uiClassID = "ResizableScrollPaneUI";

    static {
	UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.metal.MetalResizableScrollPaneUI");
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    public ResizableScrollPane()
    {
	super();
    }

    public ResizableScrollPane(Component view)
    {
	super(view);
    }

    public ResizableScrollPane(Component view, int vsbPolicy, int hsbPolicy)
    {
	super(view, vsbPolicy, hsbPolicy);
    }

    public ResizableScrollPane(int vsbPolicy, int hsbPolicy)
    {
	super(vsbPolicy, hsbPolicy);
    }

}
