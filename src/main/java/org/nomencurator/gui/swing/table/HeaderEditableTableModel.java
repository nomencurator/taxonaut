/*
 * HeaderEditableTableModel.java:  a DefaultTableModel capable to
 * manage non-String header value
 *
 * Copyright (c) 2003, 2015 Nozomi `James' Ytow
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

import javax.swing.table.TableModel;

/**
 * <code>TreeModel</code> to manage <code>NameTreeNode</code>
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface HeaderEditableTableModel
    extends TableModel
{
    /**
     * Returns header value at the <CODE>column</CODE>
     *
     * @return an <CODE>Object</CODE> for header at
     * <CODE>column</CODE>.
     */
    public Object getHeaderValue(int column);
}
