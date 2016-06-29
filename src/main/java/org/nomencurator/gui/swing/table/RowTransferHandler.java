/*
 * RowTransferHandler.java:  a TransferHandler to manipulate JTable rows
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import java.awt.datatransfer.DataFlavor;

import javax.activation.ActivationDataFlavor;

import javax.swing.JTable;
import javax.swing.TransferHandler;

/**
 * {@code RowTransferHandler} provides a TransferHandler to manipulate JTable rows.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RowTransferHandler
    extends TransferHandler
{
    private static final long serialVersionUID = 1841061032113141192L;

    protected final static DataFlavor flavor =
	new ActivationDataFlavor(Object[].class, DataFlavor.javaJVMLocalObjectMimeType, "Object array");

    /**
     * Row iindices affected
     */
    protected int[] indices;

    /**
     * Row index where to insert
     */
    protected int insretedAt;

    /**
     * Number of rows to insert
     */
    protected int insrtionCount;

    public RowTransferHandler()
    {
	super();
    }

    
}
