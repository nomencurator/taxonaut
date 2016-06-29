/*
 * RenderingOptions.java: an interface to manage options in  JTree node rendering
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
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

import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;

/**
 * <tt>RenderingOptions</code> provides an interface to manage node rendering options of <tt>JTree</tt>.
 *
 * @version 	27 Aug. 2015
 * @author 	Nozomi `James' Ytow
 */
public interface RenderingOptions
{
    /**
      * Sets <tt>icon</tt> to represent an expanded non-leaf node.
      *
      * @param icon to represent expanded status.
      */
    public void setOpenIcon(Icon icon);

    /**
      * Returns the icon representing an expanded non-leaf node. 
      *
      * @retrun the icon to represent expanded status, or null if unspecified.
      */
    public Icon getOpenIcon();

    /**
      * Sets <tt>icon</tt> to represent a collapsed non-leaf node.
      *
      * @param icon to represent collapsed status.
      */
    public void setClosedIcon(Icon icon);

    /**
      * Returns the icon representing a collapsed non-leaf node. 
      *
      * @retrun the icon to represent collapsed status, or null if unspecified.
      */
    public Icon getClosedIcon();

    /**
      * Sets <tt>icon</tt> to represent a leaf node.
      *
      * @param icon to represent a leaf node.
      */
    public void setLeafIcon(Icon icon);

    /**
      * Returns the icon representing a leaf node. 
      *
      * @retrun the icon to represent a leaf node.
      */
    public Icon getLeafIcon();

    /**
      * Sets <tt>color</tt> as the text color  displayed when the node is selected.
      * 
      * @param color to set.
      */
    public void setTextSelectionColor(Color color);

    /**
      * Returns the text color displayed when the node is selected.
      *
      * @return color of the text displayed on selection.
      */
    public Color getTextSelectionColor();

    /**
      * Sets <tt>color</tt> as the text color of  the node unselected.
      * 
      * @param color to set.
      */
    public void setTextNonSelectionColor(Color color);

    /**
      * Returns the text color of the node unselected.
      *
      * @return color of the text displayed when the node is not selected.
      */
    public Color getTextNonSelectionColor();

    /**
      * Sets <tt>color</tt> as background color to be used if the node is selected.
      * 
      * @param color to set.
      */
    public void setBackgroundSelectionColor(Color color);

    /**
      * Returns the backgournd color to use  if the node is selected.
      *
      * @return background color of the node on selection.
      */
    public Color getBackgroundSelectionColor();

    /**
      * Sets <tt>color</tt> as the background color to be painted when the node is not selected.
      * 
      * @param color to set.
      */
    public void setBackgroundNonSelectionColor(Color color);

    /**
      * Returns the background color to be painted when the node is not selected.
      *
      * @return background color displayed  when the node is not selected.
      */
    public Color getBackgroundNonSelectionColor();

    /**
      * Sets <tt>color</tt> as the border color.
      * 
      * @param color to set.
      */
    public void setBorderSelectionColor(Color color);

    /**
      * Returns the border color.
      *
      * @return color of the border.
      */
    public Color getBorderSelectionColor();

    /**
     * Set <tt>font</tt> as the text font of the node component.
     *
     * @param font of the node text.
     */
    public void setFont(Font font);

    /**
     * Returns the font of the node component.
     *
     * @return font of the component.
     */
    public Font getFont();
}
