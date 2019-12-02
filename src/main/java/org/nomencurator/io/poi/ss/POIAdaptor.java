/*
 * POIAdaptor.java: an adaptor to handle TableModel contents with POI
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

package org.nomencurator.io.poi.ss;

import javax.swing.ListModel;
import javax.swing.table.TableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * {@code POIAdaptor} provides methods to export data models in
 * Excel format
 *
 * @version 	26 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class POIAdaptor
{
    /**
     * Creates a {@link Sheet} of {@code workBook} having {@code title} from given {@code model}.
     * The sheet contains the header depending on {@code includeHeader}.
     *
     * @param model {@link TableModel} to export to the sheet
     * @param workBook {@link Workbook} to add a new {@link Sheet} containing data
     * @param title {@link String} representing the name of the sheet.
     * @param  includeHeader true to inlude the header of the {@code model} in the sheet
     * @return {@link Sheet} created.
     */
    public static Sheet createSheet(TableModel model, Workbook workBook, String title, boolean includeHeader)
    {
	return createSheet(model, workBook, title, includeHeader, 0, 0);
    }

    /**
     * Creates a {@link Sheet} of {@code workBook} having {@code title} from a regiion of
     * given {@code model} of whch top left corner is specified by {@code startRow} and
     * {@code startColumn}.
     * The sheet contains the header depending on {@code includeHeader}.
     *
     * @param model {@link TableModel} to export to the sheet
     * @param workBook {@link Workbook} to add a new {@link Sheet} containing data
     * @param title {@link String} representing the name of the sheet.
     * @param  includeHeader true to inlude the header of the {@code model} in the sheet
     * @param startRow top row of {@link TableModel} region to write out
     * @param startColumn left odst column of {@link TableModel} region to write out
     * @return {@link Sheet} created.
     */
    public static Sheet createSheet(TableModel model, Workbook workBook, String title, boolean includeHeader, int startRow, int startColumn)
    {
	int rowCount = 0;
	Sheet sheet = workBook.createSheet(title);
	if (includeHeader) {
	    Row headerRow = sheet.createRow(rowCount++);
	    for (int j = startColumn; j < model.getColumnCount(); j++) {
		headerRow.createCell(j).setCellValue(model.getColumnName(j));
	    }
	}
	for (int i = startRow; i < model.getRowCount(); i++, rowCount++) {
	    Row row = sheet.createRow(rowCount);
	    for (int j = startColumn; j < model.getColumnCount(); j++) {
		Object value = model.getValueAt(i, j);
		if (value != null) {
		    String expression = value.toString();
		    row.createCell(j).setCellValue(expression);
		}
	    }
	}
	if (includeHeader) {
	    sheet.createFreezePane(0, 1);
	}
	return sheet;
    }

    /**
     * Creates a {@link Sheet} of {@code workBook} having {@code title} from given {@code model}.
     *
     * @param model {@link ListModel} to export to the sheet
     * @param workBook {@link Workbook} to add a new {@link Sheet} containing data
     * @param title {@link String} representing the name of the sheet.
     * @return {@link Sheet} created.
     */
    public static <E> Sheet createSheet(ListModel<E> model, Workbook workBook, String title)
    {
	return createSheet(model, workBook, title, 0);
    }

    /**
     * Creates a {@link Sheet} of {@code workBook} having {@code title} from given {@code model}
     * from {@code start}th item.
     *
     * @param model {@link ListModel} to export to the sheet
     * @param workBook {@link Workbook} to add a new {@link Sheet} containing data
     * @param title {@link String} representing the name of the sheet
     * @param start the first item to export
     * @return {@link Sheet} created.
     */
    public static <E> Sheet createSheet(ListModel<E> model, Workbook workBook, String title, int start)
    {
	Sheet sheet = workBook.createSheet(title);
	for (int i = start; i < model.getSize(); i++) {
	    sheet.createRow(i).createCell(0).setCellValue(model.getElementAt(i).toString());
	}
	return sheet;
    }
}
