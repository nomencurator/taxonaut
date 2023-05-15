/*
 * FlippableLayoutCache.java: an API defintion of  FlippableLayoutCache
 *
 * Copyright (c) 2022 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP19K12711
 */

package org.nomencurator.gui.swing.tree;

import java.awt.Rectangle;

import javax.swing.tree.TreePath;

import org.nomencurator.gui.swing.Flippable;

/**
 * {@code FlippableLayoutCache} defines API to cope with {@code DualTree} via
 * {@code FlippableTree} and its UIs.
 *
 * @version 	15 June 2022
 * @author 	Nozomi `James' Ytow
 */
public interface FlippableLayoutCache
    extends Flippable
{
    /**
     * Returns number of rows displayed.
     *
     * @see {@code AbsractLayoutCache#getRowCount()}
     * @return nunber of rows displayed.
     */
    public int getRowCount();

    public int getRowForPath(TreePath path);

    public TreePath getPathForRow(int row);

    public int getPreferredHeight();
}

interface NodeDimensionsAccessor
{
    public Rectangle _getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle placeIn);
}
