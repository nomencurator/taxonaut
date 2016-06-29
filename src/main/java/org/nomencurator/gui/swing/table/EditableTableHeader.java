/*
 * EditableTableHeader.java:  a JTableHeader providing
 * eidtable headers
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

//import com.sun.java.swing.plaf.windows.WindowsTableHeaderUI;

import java.awt.Component;

import java.awt.event.MouseEvent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import java.util.EventObject;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.TableHeaderUI;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

import org.nomencurator.gui.swing.HeaderEditableTable;
import org.nomencurator.gui.swing.LookAndFeelManager;

import org.nomencurator.gui.swing.plaf.basic.BasicEditableTableHeaderUI;



/**
 * A {@code JTableHedear} provides editable header
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class EditableTableHeader
    extends JTableHeader
    implements PropertyChangeListener
{
    private static final long serialVersionUID = -2336172811660604580L;

    private static final String uiClassID = "EditableTableHeaderUI";

    /* Set user interface class ID according to current default look and feel*/
    protected static void putUI()
    {
	if(LookAndFeelManager.isWindows())
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.windows.WindowsEditableTableHeaderUI");
	else
	    UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.basic.BasicEditableTableHeaderUI");
    }

    static {
	putUI();
    }

    public void propertyChange(PropertyChangeEvent event) 
    {
	if(LookAndFeelManager.isLookAndFeelEvent(event))
	    putUI();
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    protected static EditableTableHeader lookAndFeelListener = null;

    /**
     * Constructs a {@code EditableTableHeader}
     * with a default {@code TableColumnModel}
     */
    public EditableTableHeader()
    {
	this(null);
    }

    /**
     * Constructs a {@code EditableTableHeader}
     * with a default {@code TableColumnModel}
     */
    public EditableTableHeader(TableColumnModel model)
    {
	super(model);

	if(lookAndFeelListener == null) {
	    UIManager.addPropertyChangeListener(this);
	    lookAndFeelListener = this;
	}

	setToolTipText("default");
    }

    public void updateUI()
    {
	super.updateUI();
	validate();
    }

    public String getToolTipText(MouseEvent e)
    {
	Component c = this;
	ComponentUI cui = getUI();
	if(!(cui instanceof BasicEditableTableHeaderUI)) {
	    return null;
	}

	BasicEditableTableHeaderUI ui = 
	    (BasicEditableTableHeaderUI)cui;

	if(ui.isEditing())
	    c = ui.getEditorComponent();
	else
	    c =
		SwingUtilities.getDeepestComponentAt(this, e.getX(), e.getY());
	//(JComponent)getComponentAt(e.getX(), e.getY());
	if(c == null || 
	   c == this ||
	   !(c instanceof JComponent)) {
	    //return null;
	    return super.getToolTipText(e);
	}
	/*
	if(c instanceof TreeHeaderRenderer)
	    c = ((TreeHeaderRenderer)c).getTree();
	*/
	return ((JComponent)c).getToolTipText
	    (((BasicEditableTableHeaderUI)getUI()).convertMouseEvent(e, c));
    }

}
