/*
 * FlippableAdaptor: an utillity class implmenetions methods of Flippable.
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

import org.nomencurator.gui.swing.Flippable;
import org.nomencurator.gui.swing.Flippable.Orientation;

/**
 * {@code FlippableAdaptor} is an utility class implementing methods to specify rendering orientation.
 *
 * @version 	01 May 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippableAdaptor implements Flippable
{
    protected Orientation capability = NONE;
    protected Orientation orientation = NONE;

    /**
     * Default constructor.
     */
    public FlippableAdaptor()
    {
	this(NONE, NONE);
    }

    /**
     * Constructor
     */
    public FlippableAdaptor(Orientation capability)
    {
	this(capability, NONE);
    }

    /**
     * Constructor
     */
    public FlippableAdaptor(Orientation capability, Orientation orientation)
    {
	setCapability(capability);
	setOrientation(orientation);	
    }

    /**
     * Returns flipping capability of the instance.
     *
     * @return capable orientation
     */
    public Orientation getCapability()
    {
	return capability;
    }
    

    /**
     * Sets flipping capability of the instance to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from 
     * the given parameter if the instance is not capable to cope with.
     *
     * @param orientation to flip
     * @return capable orientation of the instance
     */
    public Orientation setCapability(Orientation orientation)
    {
	if (orientation == null)
	    capability = NONE;
	else
	    capability = orientation;

	return getCapability();
    }


    
    /**
     * Returns rendering orientation.
     *
     * @return orientation to render, or {@code NONE}.
     */
    public Orientation getOrientation()
    {
	return orientation;
    }

    /**
     * Sets orientation of the instance  to geiven {@code orientation}.
     * Returns {@code Orientation} of the instance which may different from 
     * given parameter if the instance doesnt accept it.
     *
     * @param orientation of drawing to set
     * @return orientation of the instance
     */
    public Orientation setOrientation(Orientation orientation)
    {
	if (orientation == null) {
	    this.orientation = NONE;
	} else if (orientation != null && getOrientation() != null &&
		   ((orientation.getOrientation() & getCapability().getOrientation()) != 0)) {
	    this.orientation = orientation;
	}
        return getOrientation();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped()
    {
	return NONE != getOrientation();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFlipped(Orientation orientation)
    {
	if (orientation == NONE || orientation == BOTH)
	    return (orientation == getOrientation());
	else
	    return ((orientation.getOrientation() & getOrientation().getOrientation()) != 0);
    }

}
