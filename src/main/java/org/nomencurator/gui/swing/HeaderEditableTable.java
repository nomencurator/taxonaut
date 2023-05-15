/*
 * HeaderEditableTable.java:  a JTable with eidtable headers
 *
 * Copyright (c) 2003, 2004, 2005, 2014, 2015, 2016 Nozomi `James' Ytow
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
import java.awt.Dimension;
import java.awt.Rectangle;

import java.util.EventObject;
import java.util.List;

import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.nomencurator.gui.swing.plaf.basic.BasicEditableTableHeaderUI;
import org.nomencurator.gui.swing.plaf.basic.BasicHeaderEditableTableUI;

import org.nomencurator.gui.swing.table.DefaultHeaderEditableTableModel;
import org.nomencurator.gui.swing.table.EditableTableHeader;
import org.nomencurator.gui.swing.table.HeaderEditableTableColumn;
import org.nomencurator.gui.swing.table.HeaderEditableTableModel;

/**
 * A <code>JTable</code> with editable header
 *
 * @version 	24 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class HeaderEditableTable
    extends ViewportSizedTable
{
    private static final long serialVersionUID = 5386554098883560972L;

    private static final String uiClassID = "HeaderEditableTableUI";

    static {
	UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.basic.BasicHeaderEditableTableUI");
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    protected int uneditableRow;
    protected int uneditableColumn;

    protected JTable rowHeaderTable;

    public HeaderEditableTable()
    {
        super(null, null, null);
    }

    public HeaderEditableTable(int rows)
    {
        super(null, null, null, rows);
    }

    public HeaderEditableTable(TableModel dm)
    {
        super(dm, null, null);
    }

    public HeaderEditableTable(TableModel dm, int rows)
    {
        super(dm, null, null, rows);
    }

    public HeaderEditableTable(TableModel dm, TableColumnModel cm)
    {
        super(dm, cm, null);
    }

    public HeaderEditableTable(TableModel dm, TableColumnModel cm, int rows)
    {
        super(dm, cm, null, rows);
    }

    public HeaderEditableTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
    {
        super(dm, cm, sm);
    }

    public HeaderEditableTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm, int rows)
    {
        super(dm, cm, sm, rows);
    }

    public HeaderEditableTable(int numRows, int numColumns)
    {
	this(new DefaultHeaderEditableTableModel(numRows, numColumns), numRows);
    }

    public HeaderEditableTable(final List<List<Object>> rowData, final List<Object> columnNames)
    {
	this(rowData, columnNames, DEFAULT_ROWS);
    }

    public HeaderEditableTable(final List<List<Object>> rowData, final List<Object> columnNames, int rows)
    {
        this(new AbstractTableModel() {
		private static final long serialVersionUID = 5386554098883560972L;
		public String getColumnName(int column) { return columnNames.get(column).toString(); }
		public int getRowCount() { return rowData.size(); }
		public int getColumnCount() { return columnNames.size(); }
		public Object getValueAt(int row, int column) {
		    if(row < 0)
			return columnNames.get(column);
		    else
			return rowData.get(row).get(column);
		}
		public boolean isCellEditable(int row, int column) { return true; }
		public void setValueAt(Object value, int row, int column) {
		    if(row < 0)
			columnNames.set(column, value);
		    else
			rowData.get(row).set(column, value);
		    fireTableCellUpdated(row, column);
		}
	    }, rows);
    }

    public HeaderEditableTable(final Object[][] rowData, final Object[] columnValues)
    {
	this(rowData, columnValues, DEFAULT_ROWS);
    }

    public HeaderEditableTable(final Object[][] rowData, final Object[] columnValues, int rows)
    {
	this(new DefaultHeaderEditableTableModel(rowData, columnValues), rows);
    }

    public TableColumn getHeaderColumn()
    {
	return getRowHeaderTable().getColumnModel().getColumn(0);
    }

    public JTable getRowHeaderTable()
    {
	if (rowHeaderTable == null)
	    rowHeaderTable = createRowHeaderTable();
	return rowHeaderTable;
    }

    protected JTable createRowHeaderTable()
    {
	TableColumnModel columnModel = getColumnModel();
	TableColumn column = columnModel.getColumn(0);
	columnModel.removeColumn(column);

	columnModel = new DefaultTableColumnModel();
	columnModel.addColumn(column);

	JTable headerTable = new JTable(getModel(), columnModel);
	headerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
	headerTable.getTableHeader().setReorderingAllowed(false);
	headerTable.setPreferredScrollableViewportSize(new Dimension(column.getPreferredWidth() + 
								     columnModel.getColumnMargin(), 0));
	return headerTable;
    }

    protected void initializeLocalVars()
    {
	super.initializeLocalVars();
	setDefaultHeaderRenderer();
	setDefaultHeaderEditor();
	uneditableRow = -2;
	setEditingRow(uneditableRow);
	uneditableColumn = -1;
	/*
	if (rowHeaderTable == null)
	    rowHeaderTable = createRowHeaderTable();
	*/
    }

    protected JTableHeader createDefaultTableHeader()
    {
        return new EditableTableHeader(columnModel);
    }

    protected void setDefaultHeaderRenderer()
    {
    }

    protected void setDefaultHeaderEditor()
    {
    }

    public void setTableHeader(JTableHeader tableHeader)
    {
	if(tableHeader != null &&
	   !(tableHeader.getUI() instanceof BasicEditableTableHeaderUI))
	    tableHeader.setUI(new BasicEditableTableHeaderUI());
	super.setTableHeader(tableHeader);
    }

    /**
     * Returns <CODE>TableCellEditor</CODE> at <CODE>row</CODE>
     * and <CODE>column</CODE>.  If <CODE>row</CODE> is less
     * than zero, it return <CODE>TableCellEditor</CODE> assigned
     * to the header cell.
     *
     * @param row the row of the cell to edit, where 0 is the first row 
     * and negative values specifies header
     * @param column the column of the cell to edit, 
     * where 0 is the first column
     *
     * @return the editor for the cell
     *
     * @see JTable#getCellEditor(int, int)
     */
    public TableCellEditor getCellEditor(int row, int column)
    {
	if(row > -1)
	    return super.getCellEditor(row, column);

        HeaderEditableTableColumn tableColumn =
	    (HeaderEditableTableColumn)getColumnModel().getColumn(column);
        TableCellEditor editor = null;

	editor = tableColumn.getHeaderEditor();
        if (editor == null) {
            editor = getDefaultEditor(getColumnClass(column));
        }
        return editor;
    }

    public boolean editCellAt(int row, int column, EventObject e)
    {
        if (cellEditor != null && !cellEditor.stopCellEditing())
            return false;

	if (row > -1 && row < getRowCount())
	    return super.editCellAt(row, column, e);

	if (row < -1 || row >= getRowCount() || 
	    column < 0 || column >= getColumnCount())
	    return false;

        if (!isCellEditable(row, column))
            return false;

        TableCellEditor editor = getCellEditor(row, column);
        if (editor == null || !editor.isCellEditable(e))
	    return false;

	editorComp = prepareEditor(editor, row, column);
	if (editorComp == null) { 
	    removeEditor(); 
	    return false; 
	}
	editorComp.setBounds(getCellRect(row, column, false));
	add(editorComp);
	editorComp.validate();
	
	setCellEditor(editor);
	setEditingRow(row);
	setEditingColumn(column);
	editor.addCellEditorListener(this);
	
	return true;
    }

    public Component prepareEditor(TableCellEditor editor, int row, int column)
    {
	Component c = super.prepareEditor(editor, row, column);
	return c;
    }

    public Rectangle getCellRect(int row, int column, boolean includeSpacing)
    {
	if(row > -1)
	    return super.getCellRect(row, column, includeSpacing);

	return SwingUtilities.convertRectangle(getTableHeader(), getTableHeader().getHeaderRect(column), this);
    }

    protected TableColumn createTableColumn(int index)
    {
	return new HeaderEditableTableColumn(index);
    }

    public void createDefaultColumnsFromModel()
    {
        TableModel model = getModel();
        if (model == null)
	    return;

	TableColumnModel columnModel = 
	    getColumnModel();
	while (columnModel.getColumnCount() > 0) {
	    columnModel.removeColumn(columnModel.getColumn(0));
	}
	    
	for (int i = 0; i < model.getColumnCount(); i++) {
	    addColumn(createTableColumn(i));
	}
    }

    /**
     * Returns preferred size of the viewport for this table
     *
     * @return a <CODE>Dimension</CODE> representing 
     * <CODE>prfeeredSize</CODE> of <CODE>JViewport</CODE>
     * of which view is this table
     *
     * @see Scrollable#getPreferredScrollableViewportSize
     */
    public Dimension getPreferredScrollableViewportSize()
    {
	Dimension fixed = super.getPreferredScrollableViewportSize();
	int d = getRowCount() * getRowHeight();
	if(d < fixed.height)
	    fixed.height = d;
	return fixed;
    }

    public void addColumn(TableColumn column)
    {
	TableModel model = getModel();
	if(!(model instanceof HeaderEditableTableModel)) {
	    super.addColumn(column);
	    return;
	}
	
        if (column.getHeaderValue() == null)
            column.setHeaderValue(((HeaderEditableTableModel)model).getHeaderValue(column.getModelIndex()));

        getColumnModel().addColumn(column);
    }

    public int getHeaderWidth()
    {
	JTable header = getRowHeaderTable();

	int col = getHeaderColumn().getModelIndex();
	int w = 0;
	int length = 0;

	for(int i = 0; i < header.getRowCount(); i++) {
	    Object value = header.getValueAt(i, col);
	    int l = value.toString().length();
	    if(l > length)
		length = l;

	    int p = 
		header.getCellRenderer(i, col).getTableCellRendererComponent(header,
									     value,
									     false, 
									     false, 
									     i, col).getMaximumSize().width;
	    if(p > w) {
		w = p;
	    }
	}
	
	if(length != 0)
	    w += w/length/4; //magic....
	
	return w;
    }
}
