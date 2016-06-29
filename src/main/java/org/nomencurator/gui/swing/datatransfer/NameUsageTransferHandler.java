/*
 * NameUsageTransferHander.java:  a TransferHander for NameUsage
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

package org.nomencurator.gui.swing.datatransfer;

import java.awt.datatransfer.Transferable;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.TransferHandler;

import org.nomencurator.gui.swing.NameTable;
import org.nomencurator.gui.swing.NameTree;
import org.nomencurator.gui.swing.NameTreeTable;

import org.nomencurator.gui.swing.table.NameTableModel;
import org.nomencurator.gui.swing.table.NameTreeTableModel;

import org.nomencurator.gui.swing.tree.NameTreeModel;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.Rank;

import lombok.Getter;
import lombok.Setter;

/**
 * {@code NameUsageTransferHander} provides {@code NameUsage} data transfer between {@coce JCompoent}s
 * by drag and drop.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameUsageTransferHandler
    extends TransferHandler
{
    private static final long serialVersionUID = 2790498813182848512L;

    public NameUsageTransferHandler()
    {
    }

    public int getSourceActions(JComponent c) {
	if (c instanceof JTable
	    && ((JTable)c).getModel() instanceof NameTableModel) {
	    return COPY_OR_MOVE;
	}
	else if (c instanceof JTree
		 && ((JTree)c).getModel() instanceof NameTreeModel) {
	    return COPY;
	}
	else {
	    return NONE;
	}
    }

    public Transferable createTransferable(JComponent c) {
	return null;
    }

    public void exportDone(JComponent c, Transferable t, int action) {
    }
}

