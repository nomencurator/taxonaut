/*
 * Aligner.java:  an interface to align nodes in trees
 *
 * Copyright (c) 2003, 2014, 2015 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing.tree;

import java.awt.Rectangle;

import javax.swing.tree.TreePath;

/**
 * <CODE>Aligner</CODE> defines an interface to align nodes of  trees
 *
 * @version 	06 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface Aligner
{
    /**
     * Returns <CODE>NodeMapper</CODE> used to map
     * nodes between trees, or null if node mapping
     * is disabled
     *
     * @return <CODE>NodeMapper</CODE> used to map
     * nodes between trees, or null if node mapping
     * is disabled
     */
    public NodeMapper getMapper();

    /**
     * Sets <CODE>mapper</CODE> as the <CODE>NodeMapper</CODE>
     * to be used to map nodes between trees, or null to disable
     * node mapping
     *
     * @param mapper <CODE>NodeMapper</CODE> to be used to map
     * nodes between trees, or null to disable node mapping
     */
    public void setMapper(NodeMapper mapper);


    /**
     * Returns a <CODE>Rectangle</CODE> representing the
     * bounds to draw a path in aligner tree corresponding
     * to <CODE>path</CODE> to be aligned in original tree.
     *
     * @param path    a <CODE>TreePath</CODE> to be aligned
     * @param placeIn a <CODE>Rectangle</CODE> in where to be drawn
     *
     * @return <CODE>Rectangle</CODE> replesenting space to be drawn
     *
     * @see AbstractLayoutCache#getBounds(TreePath, Rectangle)
     */
    public Rectangle getBounds(TreePath path, Rectangle placeIn);

}
