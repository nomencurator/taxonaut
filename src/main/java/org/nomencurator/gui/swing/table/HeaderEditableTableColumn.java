/*
 * HeaderEditableTableColumn.java: a TableColumn of which header can
 * be also editable
 *
 * Copyright (c) 2003, 2015, 2016, 2019 Nozomi `James' Ytow
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

import java.beans.PropertyChangeListener;

import javax.swing.event.SwingPropertyChangeSupport;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

/**
 * A {@code TableColumn} of which header cell can
 * be editable also
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class HeaderEditableTableColumn
    extends TableColumn
{
    private static final long serialVersionUID = 8119973972273742528L;

    protected TableCellEditor headerEditor;
    
    protected boolean headerEditable;

    protected SwingPropertyChangeSupport changeListeners;

    /** 
     * Constructs a {@code HeaderEditableTableColumn} object
     * using a defaul model index, default width, default renderer
     * and editor used in the super class, and null header renderer
     * and editor.
     * This method is intended for serialization. 
     *
     *  @see TableColumn#TableColumn()
     */
    public HeaderEditableTableColumn() {
	super();
	setHeaderEditor(null);
    }

    /** 
     * Constructs a {@code HeaderEditableTableColumn} object
     * using a default width, default renderer and default editor
     * of the super class, and null header renderer and editor. 
     *
     * @see TableColumn#TableColumn(int)
     */
    public HeaderEditableTableColumn(int modelIndex)
    {
	super(modelIndex);
	setHeaderEditor(null);
    }

    /** 
     * Constructs a {@code HeaderEditableTableColumn} object
     * using defualt renderer and default editor of the super class,
     * and null header renderer and editor. 
     *
     *  @see TableColumn#TableColumn(int, int)
     */
    public HeaderEditableTableColumn(int modelIndex, int width)
    {
	super(modelIndex, width);
	setHeaderEditor(null);
    }

    /**
     * Constructs a {@code HeaderEditableTableColumn} object
     * with {@code modelIndex}, {@code width}, 
     * {@code cellRenderer}, {@code cellEditor},
     * null header renderer and null header editor.
     *
     * @see #HeaderEditableTableColumn(int, int, TableCellRenderer, TableCellEditor, TableCellRenderer, TableCellEditor)
     */
    public HeaderEditableTableColumn(int modelIndex, int width,
				     TableCellRenderer cellRenderer,
				     TableCellEditor cellEditor)
    {
	this(modelIndex, width, cellRenderer, cellEditor, null, null);
    }

    /**
     * Constructs a {@code HeaderEditableTableColumn} object
     * with {@code modelIndex}, {@code width}, 
     * {@code cellRenderer}, {@code cellEditor},
     * null header renderer and null header editor.
     *
     * @see TableColumn#TableColumn(int, int, TableCellRenderer, TableCellEditor)
     */
    public HeaderEditableTableColumn(int modelIndex, int width,
				     TableCellRenderer cellRenderer,
				     TableCellEditor cellEditor,
				     TableCellRenderer headerRenderer,
				     TableCellEditor headerEditor)
    {
	super(modelIndex, width, cellRenderer, cellEditor);
	setHeaderRenderer(headerRenderer);
	setHeaderEditor(headerEditor);
    }

    /**
     * Returns the {@code TableCellEditor}
     * of header to edit contents of the header of this column.
     *
     * @return {@code TableCellEditor} used as
     * the header editor
     *
     * @see #setHeaderEditor
     */
    public TableCellEditor getHeaderEditor()
    {
	return headerEditor;
    }

    /**
     * Sets {@code editor} as {@code TableCellEditor}
     * of header to edit contents of the header of this column.
     *
     * @param editor {@code TableCellEditor} to be set as
     * the header editor
     *
     * @see #getHeaderEditor
     */
    public void setHeaderEditor(TableCellEditor editor)
    {
	TableCellEditor previous = getHeaderEditor();
	if(editor == previous)
	    return;

	headerEditor = editor;
	if(changeListeners != null)
	    changeListeners.firePropertyChange("hederEditor", previous, headerEditor); 
    }

    /**
     * Returns true if the header value is editable
     *
     * @return true if the header value is editable
     */
    public boolean isHeaderEditable()
    {
	return headerEditable && (headerEditor != null);
    }

    /**
     * Sets the header {@code editable}
     *
     * @param editable true to make the header editable
     */
    public void setHeaderEditable(boolean editable)
    {
	boolean previous = headerEditable;
	if(editable == previous)
	    return;

	headerEditable = editable;
	if(changeListeners != null)
	    changeListeners.firePropertyChange("hederEditable", Boolean.valueOf(previous), Boolean.valueOf(headerEditable)); 
    }

    /**
     * Overrides {@code addPropertyChangeListener} of
     * {@code TableColumn} to provide access to change listeners.
     *
     * @param listener  the listener to be added
     */
    public synchronized void addPropertyChangeListener(
                                PropertyChangeListener listener)
    {
        if (changeListeners == null) {
            changeListeners = new SwingPropertyChangeSupport(this);
        }
        changeListeners.addPropertyChangeListener(listener);
	super.addPropertyChangeListener(listener);
    }

    /**
     * Overrides {@code removePropertyChangeListener} of
     * {@code TableColumn} to provide access to change listeners.
     *
     * @param listener  the listener to be removed
     *
     */
    public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
    {
        if (changeListeners != null) {
	    changeListeners.removePropertyChangeListener(listener);
	}
	super.removePropertyChangeListener(listener);
    }

}
