/*
 * RenderingOptionsAdapter.java: an implematation of RenderingOptions to manage options in  JTree node rendering
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

import lombok.Getter;
import lombok.Setter;

/**
 * <tt>RenderingOptionsAdapter</code> provides an adapter to node rendering options of <tt>JTree</tt>.
 *
 * @version 	27 Aug. 2015
 * @author 	Nozomi `James' Ytow
 */
public class RenderingOptionsAdapter
    implements RenderingOptions
{
    @Getter @Setter protected  Icon openIcon;
    @Getter @Setter protected  Icon closedIcon;
    @Getter @Setter protected  Icon leafIcon;
    @Getter @Setter protected  Color textSelectionColor;
    @Getter @Setter protected  Color textNonSelectionColor;
    @Getter @Setter protected  Color backgroundSelectionColor;
    @Getter @Setter protected  Color backgroundNonSelectionColor;
    @Getter @Setter protected  Color borderSelectionColor;
    @Getter @Setter protected  Font font;

    public RenderingOptionsAdapter() {
    }
}
