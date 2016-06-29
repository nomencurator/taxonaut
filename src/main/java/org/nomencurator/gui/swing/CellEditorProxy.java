/*
 * CellEditorProxy.java:  a proxy for CellEditor
 *
 * Copyright (c) 2004, 2015, 2016 Nozomi `James' Ytow
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

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.CellEditor;

/**
 * <CODE>CellEditorProxy</CODE> provides a proxy to impelemnt
 * <CODE>CellEditor</CODE> more general than what provided by
 * <CODE>DefaultCellEditor</CODE>.
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class CellEditorProxy
    extends AbstractCellEditor
{
    private static final long serialVersionUID = -6125890335328790805L;

    protected CellEditor editor;

    public CellEditorProxy()
    {
	this(null);
    }

    public CellEditorProxy(CellEditor editor)
    {
	setCellEditor(editor);
    }

    public void setCellEditor(CellEditor editor)
    {
	this.editor = editor;
    }

    public CellEditor getCellEditor()
    {
	return editor;
    }

    public void cancelCellEditing()
    {
	if(editor != null)
	    editor.cancelCellEditing();
	else 
	    super.cancelCellEditing();
    }

    public Object getCellEditorValue()
    {
	if(editor != null)
	    return editor.getCellEditorValue();

	return null;
    }

    public boolean isCellEditable(EventObject event)
    {
	if(editor != null)
	    return editor.isCellEditable(event);

	return super.isCellEditable(event);
    }

    public boolean shouldSelectCell(EventObject event)
    {
	if(editor != null)
	    return editor.shouldSelectCell(event);

	return super.shouldSelectCell(event);
    }

    public boolean stopCellEditing()
    {
	if(editor != null)
	    return editor.stopCellEditing();

	return super.stopCellEditing();
    }
}

