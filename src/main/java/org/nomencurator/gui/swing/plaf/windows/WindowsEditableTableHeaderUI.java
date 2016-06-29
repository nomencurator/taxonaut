/*
 * BasicEditableTableHeaderUI.java:  a TableHeaderUI providing
 * eidtable headers
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

package org.nomencurator.gui.swing.plaf.windows;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import java.awt.event.MouseEvent;

import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputListener;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTableHeaderUI;

import com.sun.java.swing.plaf.windows.WindowsTableHeaderUI;

import org.nomencurator.gui.swing.HeaderEditableTable;

import org.nomencurator.gui.swing.plaf.EditableTableHeaderAdaptor;
import org.nomencurator.gui.swing.plaf.EditableTableHeaderUI;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;

/**
 * <code>WindowsEditableTableHedearUI</code> provides editable header
 *
 * @version 	14 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class WindowsEditableTableHeaderUI
    extends WindowsTableHeaderUI
    implements EditableTableHeaderUI
{
    protected EditableTableHeaderAdaptor adaptor;
    
    public static ComponentUI createUI(JComponent h)
    {
        return new WindowsEditableTableHeaderUI();
    }

    public boolean isEditing()
    {
	return adaptor.isEditing();
    }

    public Component getEditorComponent()
    {
	return adaptor.getEditorComponent();
    }

    /**
     * Creates the mouse listener for <CODE>HeaderEditableTable</CODE>.
     */
    protected MouseInputListener createMouseInputListener()
    {
	adaptor = createMouseHandleAdaptor();
	return new MouseInputHandler(super.createMouseInputListener());
    }

    protected EditableTableHeaderAdaptor createMouseHandleAdaptor()
    {
	return new EditableTableHeaderAdaptor(header);
    }

    public MouseEvent convertMouseEvent(MouseEvent e,
					Component destination)
    {
	return adaptor.convertMouseEvent(e, destination);
    }

    public MouseEvent convertMouseEvent(Component source,
					MouseEvent e,
					Component destination)
    {
	return adaptor.convertMouseEvent(source, e, destination);
    }


    /**
     * <CODE>MouseInputListener</CODE> customized for
     * <CODE>HeaderEditableTable</CODE>
     */
    //public class MouseInputHandler 
    protected class MouseInputHandler 
	extends BasicTableHeaderUI.MouseInputHandler
	implements ChangeListener
    {
	MouseInputListener windowsListener;

	/** Constructor */
	public MouseInputHandler(MouseInputListener listener)
	{
	    super();
	    windowsListener = listener;
	}

        public void mouseClicked(MouseEvent e)
	{
	    if(adaptor.preMouseClicked(e))
		windowsListener.mouseEntered(e);
	    adaptor.postMouseClicked(e);

	}

	public void mousePressed(MouseEvent e)
	{
	    if(adaptor.preMousePressed(e))
		windowsListener.mousePressed(e);
	    adaptor.postMousePressed(e);
	}

	public void mouseReleased(MouseEvent e)
	{
	    if(adaptor.preMouseReleased(e))
		windowsListener.mouseReleased(e);
	    adaptor.postMouseReleased(e);
	}

        public void mouseEntered(MouseEvent e)
	{
	    if(adaptor.preMouseEntered(e))
		windowsListener.mouseEntered(e);
	    adaptor.postMouseEntered(e);
	}

        public void mouseExited(MouseEvent e)
	{
	    if(adaptor.preMouseExited(e))
		windowsListener.mouseExited(e);
	    adaptor.postMouseExited(e);
	}

        public void mouseMoved(MouseEvent e)
	{
	    if(adaptor.preMouseMoved(e))
		windowsListener.mouseMoved(e);
	    adaptor.postMouseMoved(e);
        }

        public void mouseDragged(MouseEvent e)
	{
	    if(adaptor.preMouseDragged(e))
		windowsListener.mouseDragged(e);
	    adaptor.postMouseDragged(e);
        }

	public void stateChanged(ChangeEvent e)
	{
	    adaptor.stateChanged(e);
	}

    }

}
