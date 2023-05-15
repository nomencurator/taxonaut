/*
 * EditableTableHeaderUI.java:  a TableHeaderUI providing
 * eidtable headers
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.Component;
import java.awt.Point;

import java.awt.event.MouseEvent;

import javax.swing.CellEditor;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import javax.swing.event.MouseInputListener;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicTableUI;
/**
 * A <code>TableHedearUI</code> provides editable header
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class BasicHeaderEditableTableUI
    extends BasicTableUI
{
    public static ComponentUI createUI(JComponent c)
    {
        return new BasicHeaderEditableTableUI();
    }

    /**
     * Creates the mouse listener for the JTable.
     */
    protected MouseInputListener createMouseInputListener()
    {
        return new MouseInputHandler();
    }

    /**
     * This inner class is marked &quot;public&quot; due to a compiler bug.
     * This class should be treated as a &quot;protected&quot; inner class.
     * Instantiate it only within subclasses of BasicTableUI.
     */
    //    public class MouseInputHandler
    protected class MouseInputHandler
	extends BasicTableUI.MouseInputHandler
    {
        // Component recieving mouse events during editing. May not be editorComponent.
        private Component dispatchComponent;

	/**
	 * Target <CODE>Component</CODE> to which event
	 * to be dsipached
	 */
	protected Component target;

        private void setDispatchComponent(MouseEvent e) { 
            Component editorComponent = table.getEditorComponent();
            Point p = e.getPoint();
            Point p2 = SwingUtilities.convertPoint(table, p, editorComponent);
            dispatchComponent = SwingUtilities.getDeepestComponentAt(editorComponent, 
                                                                 p2.x, p2.y);
        }

        private boolean repostEvent(MouseEvent e) { 
            if (dispatchComponent == null) {
                return false; 
            }
            MouseEvent e2 = SwingUtilities.convertMouseEvent(table, e, dispatchComponent);
            dispatchComponent.dispatchEvent(e2); 
            return true; 
        }

        private void setValueIsAdjusting(boolean flag) {
            table.getSelectionModel().setValueIsAdjusting(flag); 
            table.getColumnModel().getSelectionModel().setValueIsAdjusting(flag); 
        }

	private boolean shouldIgnore(MouseEvent e) { 
	    return !(SwingUtilities.isLeftMouseButton(e) && table.isEnabled()); 
	}

	public void mousePressed(MouseEvent e)
	{
	    if (shouldIgnore(e)) {
	        return;
	    }

            Point p = e.getPoint();
            int row = table.rowAtPoint(p);
            int column = table.columnAtPoint(p);
            if (column >  -1) {
		super.mousePressed(e);
                return;
            }

            if (row < 0) 
		return;

            if (table.editCellAt(row, column, e)) {
                setDispatchComponent(e); 
                repostEvent(e); 
            } 
	    else { 
		table.requestFocus();
	    }
        	
            CellEditor editor = table.getCellEditor(); 
            if (editor == null || editor.shouldSelectCell(e)) { 
                setValueIsAdjusting(true);
                table.changeSelection(row, column, e.isControlDown(), e.isShiftDown());  
	    }
        }

        public void mouseDragged(MouseEvent e) {
	    if (shouldIgnore(e)) {
	        return;
	    }

            repostEvent(e); 
        	
            CellEditor editor = table.getCellEditor();         
            if (editor == null || editor.shouldSelectCell(e)) {
                Point p = e.getPoint();
                int row = table.rowAtPoint(p);
                int column = table.columnAtPoint(p);

                if (row < 0)
                    return;
	        table.changeSelection(row, column, false, true); 
            }
        }
	/*
	protected void dispatchEvent(MouseEvent e)
	{
	    dispatchEvent(target, e);
	}

	protected void dispatchEvent(Component target, MouseEvent e)
	{
	    if(target == null)
		return;
	    target.dispatchEvent(SwingUtilities.convertMouseEvent(header, e, target));
	}

	protected Component getTarget(MouseEvent e)
	{
	    TableColumnModel model = header.getColumnModel();

	    int index =
		model.getColumnIndexAtX(e.getX());

	    if(index == -1 || 
	       !((HeaderEditableTable)header.getTable()).editCellAt(-1, index, e)) {
		return null;
	    }

	    HeaderEditableTableColumn c = 
		(HeaderEditableTableColumn)model.getColumn(index);

	    Component editor = 
		c.getHeaderEditor().getTableCellEditorComponent(header.getTable(),
								c.getHeaderValue(),
								true, -1, index);
	    Point p = 
		SwingUtilities.convertPoint(header, e.getPoint(), editor);

	    return SwingUtilities.getDeepestComponentAt(editor, p.x, p.y);
	}
	*/
    }
}
