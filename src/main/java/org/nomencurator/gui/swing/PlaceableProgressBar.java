/*
 * PlaceableProgerssBar.java:  a JProgressBar allowing specification
 * of text plamenct
 *
 * Copyright (c) 2006, 2015, 2016 Nozomi `James' Ytow
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
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.gui.swing;

import java.awt.Color;
import java.awt.Point;

import javax.swing.BoundedRangeModel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import javax.swing.plaf.ProgressBarUI;

import org.nomencurator.gui.swing.plaf.basic.BasicPlaceableProgressBarUI;

/**
 * <code>PlaceableProgressBar</code> is a <CODE>JProgressBar</CODE>
 * supporting specification of text plamenct by users
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class PlaceableProgressBar
    extends JProgressBar
{
    private static final long serialVersionUID = 1255468097768448898L;

    private static final String uiClassID = "PlaceableProgressBarUI";

    static {
	UIManager.put(uiClassID, "org.nomencurator.gui.swing.plaf.basic.BasicPlaceableProgressBarUI");
    }

    public String getUIClassID()
    {
	return uiClassID;
    }

    public void setTextPlacement(int placement)
    {
	/*
	ProgressBarUI ui = getUI();
	if(!(ui instanceof BasicPlaceableProgressBarUI))
	    setUI(new BasicPlaceableProgressBarUI());
	*/
	((BasicPlaceableProgressBarUI)getUI()).setPlacement(placement);
    }

    public int getTextPlacement()
    {
	return ((BasicPlaceableProgressBarUI)getUI()).getPlacement();
    }

    public void setTextColor(Color color)
    {
	((BasicPlaceableProgressBarUI)getUI()).setForeground(color);
    }

    public PlaceableProgressBar()
    {
	super();
	setTextPlacement(SwingConstants.CENTER);
    }

    public PlaceableProgressBar(BoundedRangeModel newModel)
    {
	this(newModel, SwingConstants.CENTER);
    }

    public PlaceableProgressBar(BoundedRangeModel newModel, int placement)
    {
	super(newModel);
	setTextPlacement(placement);
    }

    public PlaceableProgressBar(int orient)
    {
	super(orient);
	setTextPlacement(SwingConstants.CENTER);
    }

    public PlaceableProgressBar(int min, int max)
    {
	super(min, max);
	setTextPlacement(SwingConstants.CENTER);
    }

    public PlaceableProgressBar(int orient, int min, int max)
    {
	this(orient, min, max, SwingConstants.CENTER);
    }

    public PlaceableProgressBar(int orient, int min, int max, int placement)
    {
	super(orient, min, max);
	setTextPlacement(placement);
    }

} 

