/*
 * Flippable.java:  an interface to flip a Swing component
 *
 * Copyright (c) 2021, 2022 Nozomi `James' Ytow
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

/**
 * {@code Flippable} defines an interface to flip a Swing component on screen.
 *
 * @version 	28 Apr 2022
 * @author 	Nozomi `James' Ytow
 */
public interface Flippable
{
    /**
     * Enumeration of orientational constants.
     */
    public enum Orientation {
	NONE(0)
	,HORIZONTAL(1)
	,VERTICAL(2)
	,BOTH(3)
	;

	private int orientation;

	private Orientation(int orientation) {
	    this.orientation = orientation;
	}

	/**
	 * Returns the orientation in an {@code int} value,
	 * as defined in {@link java.awt.Adjustable}.
	 *
	 * @return int vlue of the orientation.
	 */
	public int getOrientation ()
	{
	    return orientation;
	}

	/**
	 * Returns {@code true} if the object has horizontal factor
	 * either {@code HORIZONTAL} or {@code BOTH}.
	 *
	 * @return {@code true} if the object has horizontal factor
	 */
	public boolean isHorizontal()
	{
	    return (this == HORIZONTAL || this == BOTH);
	}

	/**
	 * Returns {@code true} if the object has vertical factor
	 * either {@code VERTICAL} or {@code BOTH}.
	 *
	 * @return {@code true} if the object has vertical factor
	 */
	public boolean isVertical()
	{
	    return (this == VERTICAL || this == BOTH);
	}
    }

    static public Orientation NONE = Orientation.NONE;
    static public Orientation HORIZONTAL = Orientation.HORIZONTAL;
    static public Orientation VERTICAL = Orientation.VERTICAL;
    static public Orientation BOTH = Orientation.BOTH;

    /**
     * Returns flipping capablity of the instance.
     *
     * @return capable orientation
     */
    public Orientation getCapability();

    /**
     * Sets flipping capability of the instance to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from 
     * the given parameter if the instance is not capable to cope with.
     *
     * @param orientation to flip
     * @return capable orientation of the instance
     */
    public Orientation setCapability(Orientation orientation);

    /**
     * Returns orientation of the instance.
     *
     * @return drawing orientation
     */
    public Orientation getOrientation();

    /**
     * Sets orientation of the instance  to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from 
     * given parameter if the instance doesnt accept it.
     *
     * @param orientation of drawing to set
     * @return orientation of the instance
     */
    public Orientation setOrientation(Orientation orientation);

    /**
     * Returns true if the isntace is flipped
     *
     * @return true if the instance is flipped.
     */
    public boolean isFlipped();

    /**
     * Returns true if the isntace is flipped in given {@code orientation}
     *
     * @param orientation to test
     * @return true if the instance is flipped.
     */
    public boolean isFlipped(Orientation orientation);
}
