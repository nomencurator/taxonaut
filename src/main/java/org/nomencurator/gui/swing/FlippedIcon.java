/*
 * FlippedIcon.java:  a flipped Icon
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

package org.nomencurator.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import java.awt.geom.AffineTransform;

import java.awt.image.BufferedImage;
import java.awt.image.AffineTransformOp;

import java.util.Hashtable;

import javax.swing.Icon;

import org.nomencurator.gui.swing.Flippable.Orientation;

/**
 * <CODE>FlippedIcon</CODE> provides {@code Icon} flipped in {@code Orientation}.
 *
 * @version 	04 June 2022
 * @author 	Nozomi `James' Ytow
 */
public class FlippedIcon
    implements Icon
{
    /**
     * Affin transformer for vertical flipping.
     */
    protected static AffineTransform vIconFlipper;

    /**
     * Affin transformer for horizontal flipping.
     */
    protected static AffineTransform hIconFlipper;

    /**
     * Affin transformer for flipping.in both directino
     */
    protected static AffineTransform hvIconFlipper;

    /**
     * Returns {@code AffineTransform} to flip in given {@code orientation}
     *
     * @param orientatin to flip the icon
     * @return Affine transform 
     */
    protected static AffineTransform getIconFlipper (Orientation orientation)
    {
	AffineTransform transform = null;
	switch(orientation) {
	case VERTICAL:
	    if(vIconFlipper == null)
		vIconFlipper = AffineTransform.getScaleInstance(1.0d, -1.0d);
	    transform =vIconFlipper;
	    break;
	case HORIZONTAL:
	    if(hIconFlipper == null)
		hIconFlipper = AffineTransform.getScaleInstance(-1.0d, 1.0d);
	    transform =  hIconFlipper;
	    break;
	case BOTH:
	    if(hvIconFlipper == null)
		hvIconFlipper = AffineTransform.getScaleInstance(-1.0d, -1.0d);
	    transform = hvIconFlipper;
	    break;
	}
	return transform;
	// Java 14 or later
	/*
	return switch(orientation) {
	case VERTICAL -> {
	if(vIconFlipper == null)
	    vIconFlipper = AffineTransform.getScaleInstance(1.0d, -1.0d);
	yield vIconFlipper;
	}
	case HORIZONTAL -> {
	if(hIconFlipper == null)
	    hIconFlipper = AffineTransform.getScaleInstance(-1.0d, 1.0d);
	yield hIconFlipper;
	}
	case BOTH -> {
	if(hvIconFlipper == null)
	    hvIconFlipper = AffineTransform.getScaleInstance(-1.0d, -1.0d);
	yield hvIconFlipper;
	}
	default -> { yield null; }
	};
	*/
    }

    /**
     * Size of icon
     */
    protected final Dimension d;

    /**
     * icon image
     */
    protected final BufferedImage image;

    /**
     * icon image
     */
    protected BufferedImage flippedImage;

    /**
     * Flipping {@code Orientation}
     */
    protected final Orientation orientation;

    /**
     * {@code AffineTransform} of the orientation;
     */
    protected AffineTransform flipper;

    /**
     * The constructor.
     */
    public FlippedIcon(Icon icon, Orientation orientation)
    {
	if (icon == null) {
	    d = null;
	    image = null;
	    this.orientation = null;
	    return;
	}

	this.orientation = orientation;

	flipper = getIconFlipper(orientation);
	if (orientation.isVertical())
	    flipper.translate(0, -icon.getIconHeight());
	if (orientation.isHorizontal())
	    flipper.translate(-icon.getIconWidth(), 0);
	AffineTransformOp filter = new AffineTransformOp(flipper, null);

	d =  new Dimension(icon.getIconWidth(), icon.getIconHeight());
	image = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
	Graphics g = image.getGraphics();
	flippedImage = filter.filter(image, null);
	icon.paintIcon(new Container(), g, 0, 0);
	g.dispose();
    }

    /**
     * {@inderitDoc}
    */
    @Override
    public int getIconWidth()
    {
	return d.width;
    }

    /**
     * {@inderitDoc}
    */
    @Override
    public int getIconHeight()
    {
	return d.height;
    }

    /**
     * {@inderitDoc}
    */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y)
    {
	if (!(g instanceof Graphics2D))
	    return;

	Graphics2D g2 = (Graphics2D)g;
	g2.translate(x, y);
	g2.drawImage(image, flipper, c);
	g2.translate(-x,-y);
    }
}

