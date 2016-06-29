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

package org.nomencurator.gui.swing.plaf.basic;

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

import org.nomencurator.gui.swing.HeaderEditableTable;

import org.nomencurator.gui.swing.plaf.EditableTableHeaderAdaptor;
import org.nomencurator.gui.swing.plaf.EditableTableHeaderUI;

import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.TreeHeaderRenderer;

/**
 * <code>BasicEditableTableHedearUI</code> provides editable header
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class BasicEditableTableHeaderUI
    extends BasicTableHeaderUI
    implements EditableTableHeaderUI
{
    /**
     * <CODE>EditableTableHeaderAdaptor</CODE> implements
     * core function of EditableTableHeaderUI
     */
    protected EditableTableHeaderAdaptor adaptor;
    
    public static ComponentUI createUI(JComponent h)
    {
        return new BasicEditableTableHeaderUI();
    }

    /**
     * Returns true if the heder is under edition
     *
     * @return true if the heder is under edition
     */
    public boolean isEditing()
    {
	return adaptor.isEditing();
    }

    /**
     * Returns <CODE>Component</CODE> editing the header
     * or null if not under edition.
     *
     * @return <CODE>Component</CODE> editing the header
     * or null if not under edition.
     */
    public Component getEditorComponent()
    {
	return adaptor.getEditorComponent();
    }

    /**
     * Creates the mouse listener for <CODE>HeaderEditableTable</CODE>.
     *
     * @return MouseInputListener to be used by this object
     */
    protected MouseInputListener createMouseInputListener()
    {
	adaptor = createMouseHandleAdaptor();
	return new MouseInputHandler();
    }

    /**
     * Creates and returns <CODE>EditableTableHeaderAdaptor</CODE>
     * implementing necessary functions
     *
     * @return EditableTableHeaderAdaptor to be used by this object
     */
    protected EditableTableHeaderAdaptor createMouseHandleAdaptor()
    {
	return new EditableTableHeaderAdaptor(header);
    }

    /**
     * Converts <CODE>event</CODE> to a <CODE>MouseEvent</CODE>
     * in the <CODE>destination</CODE>
     *
     * @param event source <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(MouseEvent event,
					Component destination)
    {
	return adaptor.convertMouseEvent(event, destination);
    }

    /**
     * Converts <CODE>event</CODE> on <CODE>source</CODE> to a
     * <CODE>MouseEvent</CODE> in the <CODE>destination</CODE>
     *
     * @param source where the <CODE>event</CODE> happened
     * @param event <CODE>source</CODE> <CODE>MouseEvent</CODE>
     * @param destination <CODE>Componenet</CODE> to which the
     * <CODE>event</CODE> to be converted
     *
     * @return converted <CODE>MouseEvent</CODE>
     */
    public MouseEvent convertMouseEvent(Component source,
					MouseEvent event,
					Component destination)
    {
	return adaptor.convertMouseEvent(source, event, destination);
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
	/** Constructor */
	public MouseInputHandler()
	{
	    super();
	}

        public void mouseClicked(MouseEvent event)
	{
	    if(adaptor.preMouseClicked(event))
		super.mouseEntered(event);
	    adaptor.postMouseClicked(event);

	}

	public void mousePressed(MouseEvent event)
	{
	    if(adaptor.preMousePressed(event))
		super.mousePressed(event);
	    adaptor.postMousePressed(event);
	}

	public void mouseReleased(MouseEvent event)
	{
	    if(adaptor.preMouseReleased(event))
		super.mouseReleased(event);
	    adaptor.postMouseReleased(event);
	}

        public void mouseEntered(MouseEvent event)
	{
	    if(adaptor.preMouseEntered(event))
		super.mouseEntered(event);
	    adaptor.postMouseEntered(event);
	}

        public void mouseExited(MouseEvent event)
	{
	    if(adaptor.preMouseExited(event))
		super.mouseExited(event);
	    adaptor.postMouseExited(event);
	}

        public void mouseMoved(MouseEvent event)
	{
	    if(adaptor.preMouseMoved(event))
		super.mouseMoved(event);
	    adaptor.postMouseMoved(event);
        }

        public void mouseDragged(MouseEvent event)
	{
	    if(adaptor.preMouseDragged(event))
		super.mouseDragged(event);
	    adaptor.postMouseDragged(event);
        }

	public void stateChanged(ChangeEvent event)
	{
	    adaptor.stateChanged(event);
	}

    }

}
