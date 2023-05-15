/*
 * AlignableTreeUI.java:  a TreeUI with node alignment between
 * trees
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
 */

package org.nomencurator.gui.swing.tree;

/**
 * <CODE>Alignable</CODE> defines an interface
 * providing a node alignable tree
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public interface Alignable
{
    /**
     * Returns <CODE>Aligner</CODE> used
     * to align nodes, or null if node alignment is
     * disabled
     *
     * @return <CODE>Aligner</CODE> used
     * to align nodes, or null if node alignment is
     * disabled
     */
    public Aligner getAligner();


    /**
     * Sets <CODE>aligner</CODE> as the
     * <CODE>Aligner</CODE> to align nodes, 
     * or null to disable node alignment
     *
     * @param aligner <CODE>Aligner</CODE> to align nodes,
     * or null to disable node alignment
     */
    public void setAligner(Aligner aligner);

    /**
     * Enable or disble alignment of an <CODE>Alignable</CODE>
     * object according to balue of <CODE>alignable</CODE>.
     *
     * @param alignable true to enable alignment, or false
     * to disable. 
     */
    //public void setAlignable(boolean alignable);

    /**
     * Retuens true if alignment of the <CODE>Alignable</CODE> object
     * is enabled
     * 
     * @retuen true if alignment of the <CODE>Alignable</CODE> object
     * is enabled
     */
    //public boolean isAlignable();
}
