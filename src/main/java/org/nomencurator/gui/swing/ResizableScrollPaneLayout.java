/*
 * ResizableScrollPaneLayout.java:  a resizable ScrollPaneLayout
 *
 * Copyright (c) 2002, 2003, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javax.swing.BoundedRangeModel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.ScrollPaneLayout;

import org.nomencurator.gui.swing.plaf.metal.MetalResizableScrollPaneUI;

/**
 * {@code ResizableScrollPaneLayout} provides a resizable {@code ScrollPaneLayout}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class ResizableScrollPaneLayout
    extends ScrollPaneLayout
    implements ChangeListener
{
    private static final long serialVersionUID = -9200502459878929407L;

    protected BoundedRangeModel vSlider;

    protected BoundedRangeModel hSlider;

    protected int thumbWidth;

    protected Object eventSource;

    protected boolean resynchronized;

   public ResizableScrollPaneLayout()
    {
	this(null, null);
    }

    public ResizableScrollPaneLayout(BoundedRangeModel virticalModel,
			       BoundedRangeModel horizontalModel)
    {
	this(null, null, 0);
    }

    public ResizableScrollPaneLayout(BoundedRangeModel virticalModel,
			       BoundedRangeModel horizontalModel,
			       int thumbWidth)
    {
	super();
	setVerticalBoundRangeModel(virticalModel);
	setHorizontalBoundRangeModel(horizontalModel);
	setThumbWidth(thumbWidth);
	resynchronized = true;
    }

    public BoundedRangeModel getVerticalBoundRangModel()
    {
	return vSlider;
    }

    public void setVerticalBoundRangeModel(BoundedRangeModel model)
    {
	if(vSlider != null)
	    vSlider.removeChangeListener(this);
	vSlider = model;
	if(vSlider != null)
	    vSlider.addChangeListener(this);
    }

    public BoundedRangeModel getHorizontalBoundRangeModel()
    {
	return hSlider;
    }

    public void setHorizontalBoundRangeModel(BoundedRangeModel model)
    {
	if(hSlider != null)
	    hSlider.removeChangeListener(this);
	hSlider = model;
	if(hSlider != null)
	    hSlider.addChangeListener(this);
    }

    public int getThumbWidth()
    {
	return thumbWidth;
    }

    public void setThumbWidth(int width)
    {
	thumbWidth = width;
    }

    protected synchronized void setEventSource(Object source)
    {
	eventSource = source;
    }
    
    protected Object getEventSource()
    {
	return eventSource;
    }


    public void stateChanged(ChangeEvent event)
    {
	/*
	if(getEventSource() != null)
	    return;
	setEventSource(event.getSource());
	synchronizeSlider();
	setEventSource(null);
	*/
    }

    public void layoutContainer(Container parent) 
    {
	super.layoutContainer(parent);
	synchronizeSlider();
    }

    public void synchronizeSlider()
    {
	if((vSlider == null && hSlider == null) ||
	   viewport == null)
	    return;

	Rectangle view = viewport.getBounds();
	Rectangle available = viewport.getBounds();

	Rectangle colHeadRect = null;

	if(vSlider != null && colHead != null) {
	    colHeadRect = colHead.getBounds();
	    available.height += colHeadRect.height;
	    available.y = colHeadRect.y;
	    if(available.height > 0) {
		int headerHeight = vSlider.getValue() + thumbWidth;
		int totalHeight = vSlider.getMaximum() + thumbWidth * 2;
		if(resynchronized) {
		    headerHeight *= available.height;
		    headerHeight /= totalHeight;
		    vSlider.setMaximum(available.height - thumbWidth * 2);
		    vSlider.setValue(headerHeight - thumbWidth);
		}
		if(totalHeight != available.height ||
		   headerHeight != colHeadRect.height) {
		    if(totalHeight != available.height) {
			vSlider.setMaximum(available.height - thumbWidth * 2);
		    }
		    if(headerHeight != colHeadRect.height) {
			headerHeight *= totalHeight;
			headerHeight /= available.height;
			colHeadRect.height = headerHeight;
			available.height -= headerHeight;
			view.height = available.height;
			view.y = headerHeight;
			colHead.setBounds(colHeadRect);
			viewport.setBounds(view);
			if(upperLeft != null) {
			    Rectangle r = upperLeft.getBounds();
			    r.height = headerHeight;
			    upperLeft.setBounds(r);
			}
			if(upperRight != null) {
			    Rectangle r = upperRight.getBounds();
			    r.height = headerHeight;
			    upperRight.setBounds(r);
			}
		    }
		}
	    }
	}

	if(hSlider == null || rowHead == null) {
	    if(available.height >0)
		resynchronized = false;
	    return;
	}

	Rectangle rowHeadRect = rowHead.getBounds();
	int headerWidth = hSlider.getValue() + thumbWidth;
	int totalWidth  = hSlider.getMaximum() + thumbWidth * 2;
	available.width += rowHeadRect.height;

	if(available.width <= 0)
	    return;

	if(resynchronized) {
	    headerWidth *= available.width;
	    headerWidth /= totalWidth;
	    hSlider.setMaximum(available.width - thumbWidth * 2);
	    hSlider.setValue(headerWidth - thumbWidth);
	}

	available.x = rowHeadRect.x;

	if(totalWidth == available.width && headerWidth == rowHeadRect.width)
	    return;

	if(totalWidth != available.width) {
	    hSlider.setMaximum(available.width - thumbWidth * 2);
	}
	if(headerWidth != rowHeadRect.width) {
	    headerWidth *= totalWidth;
	    headerWidth /= available.width;
	    rowHeadRect.width = headerWidth;
	    available.width -= headerWidth;
	    view.width = available.width;
	    view.x = headerWidth;
	    rowHead.setBounds(rowHeadRect);
	    viewport.setBounds(view);
	    if(upperLeft != null) {
		Rectangle r = upperLeft.getBounds();
		r.width = headerWidth;
		upperLeft.setBounds(r);
	    }
	    if(lowerLeft != null) {
		Rectangle r = lowerLeft.getBounds();
		r.width = headerWidth;
		lowerLeft.setBounds(r);
	    }
	}

	if(available.width >0)
	    resynchronized = false;
    }

    public void syncWithScrollPane(JScrollPane sp)
    {
	super.syncWithScrollPane(sp);
	resynchronized = true;
    }

    public static class UIResource 
	extends ResizableScrollPaneLayout
	implements javax.swing.plaf.UIResource
    {
	private static final long serialVersionUID = -7134555604670142223L;
	
	public UIResource(BoundedRangeModel virticalModel,
			  BoundedRangeModel horizontalModel)
	{
	    super(virticalModel, horizontalModel);
	}

	public UIResource(BoundedRangeModel virticalModel,
			  BoundedRangeModel horizontalModel,
			  int thumbWidth)
	{
	    super(virticalModel, horizontalModel, thumbWidth);
	}

    }
}
