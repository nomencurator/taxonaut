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

package org.nomencurator.gui.swing.table;

import javax.swing.JToolTip;
import javax.swing.UIManager;

import org.nomencurator.gui.swing.plaf.metal.MetalNameTreeTableHeaderToolTipUI;

/**
 * <CODE>NameTreeTableHeaderToolTipUI</CODE> provides 
 * multiple lined  <code>JToolTip</code>
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableHeaderToolTip
    extends JToolTip
{
    private static final long serialVersionUID = 6101335664732865805L;

    private static final String uiClassID = "NameTreeTableHeaderToolTipUI";

    static {
	UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.metal.MetalNameTreeTableHeaderToolTipUI");
    }

    public NameTreeTableHeaderToolTip()
    {  
	super();
	setOpaque(false);
    }

    public String getUIClassID()
    {
        return uiClassID;
    }
}

