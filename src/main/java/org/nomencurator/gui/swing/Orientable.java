/*
 * Orientable is an interfece defining methods to specify rendering orientation.
 *
 * Copyright (c) 2021 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import javax.swing.SwingConstants;

/**
 * {@code Orientable} defines methods to specify rendering orientation.
 *
 * @version 	26 Oct. 2021
 * @author 	Nozomi `James' Ytow
 */
public interface Orientable
{
    /**
     *  Constant used to represent that the orientation is unspecified.
     *  Renderers are expected to use their own default orientaion.
     */
    public static final int UNSPECIFIED = -1;

    /**
     *  Constant used to specify vertical orientation for top to bottom.
     */
    public static final int TOP_TO_BOTTOM = SwingConstants.TOP;

    /**
     *  Constant used to specify vertical orientation for bottom to top.
     */
    public static final int BOTTOM_TO_TOP = SwingConstants.BOTTOM;

    /**
     *  Constant used to specify horizontal orientation for left to right.
     */
    public static final int LEFT_TO_RIGHT = SwingConstants.LEFT;

    /**
     *  Constant used to specify horizontal orientation for right to left.
     */
    public static final int RIGHT_TO_LEFT_ = SwingConstants.RIGHT;


    /**
     * Returns rendering orientation.
     *
     * @return orientation to render, or {@code UNSPECIFIED}.
     */
    public int getOrientation();

    /**
     * Sets rendering orientation to {@code orientation}.
     * Varid parameters are {@code SwingConstants.VERTICAL}, {@code SwingConstants.HORIZONTAL}
     * or {@code UNSPECIFIED}.
     * If given parameter is invalid, returned value does not match to the pareameter given.
     *
     * @param orientation to set
     * @return the orientation set by the method
     */
    public int setOrientation(int orientation);
    
    /**
     * Returns vertical orientation
     *
     * @return vertical orientation, or {@code UNSPECIFIED}.
     */
    public int getVerticalOrientation();

    /**
     * Sets vertical orientation to {@code orientation}.
     * Varid parameters are {@code TOP_TO_BOTTOM} or {@code BOTTOM_TO_TOP}.
     * If given parameter is invalid, returned value does not match to the pareameter given.
     *
     * @param orientation to set
     * @return the orientation set by the method
     */
    public int setVerticalOrientation(int orientation);
    
    /**
     * Returns horizontal orientation by starting point, i.e. 
     *
     * @return horizontal orientation, or {@code UNSPECIFIED}.
     */
    public int getHorizontalOrientation();

    /**
     * Sets horizontal orientation to {@code orientation}.
     * Varid parameters are {@code LEFT_TO_RIGHT} or {@code RIGHT_TO_LEFT}.
     * If given parameter is invalid, returned value does not match to the pareameter given.
     *
     * @param orientation to set
     * @return the orientation set by the method
     */
    public int setHorizontalOrientation(int orientation);
    
    /**
     * Returns the corner to start rendering.
     *
     * @return corner to start rendering, or {@code UNSPECIFIED}.
     */
    public int getCorner();

    /**
     * Sets the corner to {@code corner} where rendering start..
     * Valid parameters are compass-directional paremetrs of {@code SwingConstants}
     * or {@code UNSPECIFIED}.
     * If given parameter is invalid, returned value does not match to the pareameter given.
     *
     * @param corner to set
     * @return the corner set by the method.
     */
    public int setCorner(int corner);

    /**
     * Returns direction of a sequence to render.
     *
     * @return direction of a sequence to render, or {@code UNSPECIFIED}.
     */
    public int getSequenceDirection();

    /**
     * Sets the sequence to {@code sequence} where rendering start..
     * Valid parameters are {@code SwingConstants.PREVIOUS}, {@code SwingConstants.NEXT}
     * or {@code UNSPECIFIED}.
     * If given parameter is invalid, returned value does not match to the pareameter given.
     *
     * @param sequence to set as direction
     * @return the direction of sequence set by the method.
     */
    public int setSequenceDirection(int sequence);
    
    
}
