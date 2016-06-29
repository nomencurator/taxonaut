/*
 * MultiLinesToolTip.java:  a JToolTip supporting multiple lines
 *
 * Copyright (c) 2003, 2005, 2015, 2016 Nozomi `James' Ytow
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

import javax.swing.JToolTip;
import javax.swing.UIManager;

import javax.swing.plaf.ComponentUI;

import org.nomencurator.gui.swing.plaf.metal.MetalMatrixToolTipUI;

/**
 * {@code MultiLinesToolTip} provides 
 * multiple lined  {@code JToolTip}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class MatrixToolTip
    extends JToolTip
{
    private static final long serialVersionUID = 360465930810225234L;

    private static final String uiClassID = "MatrixToolTipUI";

    public String getUIClassID()
    {
        return uiClassID;
    }

    static {
	UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.metal.MetalMatrixToolTipUI");
    }

    public MatrixToolTip()
    {  
	super();
    }

}

