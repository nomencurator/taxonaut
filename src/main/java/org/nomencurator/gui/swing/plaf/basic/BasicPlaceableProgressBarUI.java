/*
 * BasicAlignableTreeUI.java:  a TreeUI with node alignment between
 * trees
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

package org.nomencurator.gui.swing.plaf.basic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import javax.swing.plaf.ComponentUI;

import javax.swing.plaf.basic.BasicProgressBarUI;

/**
 * <CODE>BasicPlaceableProgressBarUI</CODE> enables to specify
 * placement of text in the progress bar.
 *
 * @version 	10 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class BasicPlaceableProgressBarUI
    extends BasicProgressBarUI
{
    protected int placement;

    protected Color foreground;

    protected Color background;

    public BasicPlaceableProgressBarUI()
    {
	this(SwingConstants.CENTER);
    }

    public BasicPlaceableProgressBarUI(int placement)
    {
	super();
	setPlacement(placement);
    }

    public static ComponentUI createUI(JComponent x) {
	return new BasicPlaceableProgressBarUI();
    }

    public void setPlacement(int placement)
    {
	this.placement = placement;
    }

    public int getPlacement()
    {
	return placement;
    }

    protected Point getStringPlacement(Graphics g, String progressString,
				       int x,int y,int width,int height)
    {
	Point p = 
	    super.getStringPlacement(g, progressString, x, y,
				     width, height);
	
	if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
	    switch(placement) {
	    case SwingConstants.CENTER:
		break;
	    case SwingConstants.LEFT:
		p.x = x;
		break;
	    case SwingConstants.RIGHT:
		p.x = x + width;
		break;
	    }
	}
	else { // VERTICAL
	    switch(placement) {
	    case SwingConstants.CENTER:
		break;
	    case SwingConstants.TOP:
		p.y = y;
		break;
	    case SwingConstants.BOTTOM:
		p.y = y + height;
		break;
	    }
	}

	return p;
    }

    public void setForeground(Color foreground)
    {
	this.foreground = foreground;
    }

    public void setBackground(Color background)
    {
	this.background = background;
    }

    protected Color getSelectionForeground()
    {
	if(foreground != null)
	    return foreground;

	return super.getSelectionForeground();
    }
    
    protected Color getSelectionBackground()
    {
	if(background != null)
	    return background;

	return super.getSelectionBackground();
    }

}
