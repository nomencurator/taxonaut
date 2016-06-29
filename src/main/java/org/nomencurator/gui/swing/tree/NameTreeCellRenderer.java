/*
 * NameTreeCellRenderer.java:  a TreeCellRenerer for NameUsageNode
 *
 * Copyright (c) 2002, 2003, 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.tree;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JTree;

import javax.swing.Icon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeNode;

import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;

import org.nomencurator.gui.swing.DefaultColors;

import org.nomencurator.gui.swing.tree.NameTreeNode;
import org.nomencurator.gui.swing.tree.RenderingOptions;

/**
 * <code>TreeCellREnderer</code> for a <code>NameUsageNode</code>
 *
 * @version 	29 June. 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeCellRenderer
    extends DefaultTreeCellRenderer
{
    private static final long serialVersionUID = -326702521750531727L;

    /**
     * Foreground color of disabled components.
     */
    protected Color disabledForeground;

    /**
     * Foreground color of enabled components.
     */
    protected Color enabledForeground;

    public NameTreeCellRenderer()
    {
	super();
	setLeafIcon(null);
	setClosedIcon(null);
	setOpenIcon(null);
	setDefaultTextColor();
    }

    protected void setDefaultTextColor()
    {
	disabledForeground = DefaultColors.getDisabledForeground(DefaultColors.ComponentName.LABEL);
	enabledForeground = DefaultColors.getForeground(DefaultColors.ComponentName.LABEL);
    }

    public void updateUI()
    {
	super.updateUI();
	setDefaultTextColor();
    }

    public Component getTreeCellRendererComponent(JTree tree,
						  Object value,
						  boolean selected,
						  boolean expanded,
						  boolean leaf,
						  int row,
						  boolean hasFocus)
    {
	Component component = 
	    super.getTreeCellRendererComponent(tree, value,
					       selected, expanded, leaf,
					       row, hasFocus);

	if(value instanceof RenderingOptions) {
	    RenderingOptions options = (RenderingOptions)value;
	    if(selected) {
		component.setBackground(options.getBackgroundSelectionColor());
		component.setForeground(options.getTextSelectionColor());
	    }
	    else {
		component.setBackground(options.getBackgroundNonSelectionColor());
		component.setForeground(options.getTextNonSelectionColor());
	    }
	}
	if (value instanceof DefaultMutableTreeNode) {
	    Object userObject = ((DefaultMutableTreeNode)value).getUserObject();
	    if (userObject != null
		&& userObject instanceof NameUsage
		&& ((NameUsage)userObject).isSynonym()) {
		 component.setForeground(disabledForeground);
	    }
	    else {
		component.setForeground(enabledForeground);
	    }
	}

	return component;
    }

}




