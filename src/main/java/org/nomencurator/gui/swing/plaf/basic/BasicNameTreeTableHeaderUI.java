/*
 * BasicNameTreeTableHeaderUI.java:  a TableHeaderUI displaying
 * NameTree on the table header
 *
 * Copyright (c) 2003, 2005, 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.event.MouseEvent;

import javax.swing.JComponent;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTableHeaderUI;

import org.nomencurator.gui.swing.plaf.EditableTableHeaderAdaptor;
import org.nomencurator.gui.swing.plaf.NameTreeTableHeaderAdaptor;

/**
 * <CODE>BasicNameTreeTableHedearUI</CODE> manages <CODE>NameTreeTable</CODE>
 * on <CODE>JTableHeader</CODE>
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class BasicNameTreeTableHeaderUI
    extends BasicEditableTableHeaderUI
{
    public static ComponentUI createUI(JComponent h)
    {
        return new BasicNameTreeTableHeaderUI();
    }

    protected EditableTableHeaderAdaptor createMouseHandleAdaptor()
    {
	return new NameTreeTableHeaderAdaptor(header);
    }
}
