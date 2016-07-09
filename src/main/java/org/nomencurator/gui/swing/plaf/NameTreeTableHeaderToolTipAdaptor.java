/*
 * MultiLinesToolTipAdaptor.java:  an adaptor supporting multiple lines
 * tooltip
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing.plaf;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

import javax.swing.plaf.metal.MetalLookAndFeel;

import org.nomencurator.gui.swing.MatrixToolTip;

import org.nomencurator.gui.swing.table.NameTreeTableHeader;

import org.nomencurator.model.NameUsage;

/**
 * {@code NameTreeTableHeaderToolTipAdaptor} provides an addaptor
 * supporting multiple lined  {@code JToolTip} for {@code NameTreeTableHeader}.
 *
 * @version 	03 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class NameTreeTableHeaderToolTipAdaptor
{
    public static final int padSpaceBetweenStrings = 12;

    public static void paint(Graphics g,
		      JComponent c)
    {
	paint(g, c, ((JToolTip)c).getTipText());
    }

    public static void paint(Graphics g,
		      JComponent c,
		      String tipText)
    {
	paint(g, c, tipText, null);
    }

    @SuppressWarnings("unchecked")
    public static void paint(Graphics g,
		      JComponent c,
		      String tipText,
		      String keyText)
    {
	String[][] lines = getLines(tipText);

	if(lines.length < 2) {
	    MultiLinesToolTipAdaptor.paint(g, c, tipText, keyText);
	    return;
	}

	Font font = c.getFont();
	FontMetrics metrics = g.getFontMetrics(font);
	Dimension size = c.getSize();
	/*
        if (c.isOpaque()) {
	    g.setColor(c.getBackground());
	    g.fillRect(0, 0, size.width, size.height);
	}
	*/
	g.setColor(c.getForeground());
	g.setFont(font);

        Insets insets = c.getInsets();
        Rectangle rectangle = new Rectangle(
            insets.left,
            insets.top,
            size.width - (insets.left + insets.right),
            size.height - (insets.top + insets.bottom));

	NameTreeTableHeader<NameUsage<?>> header = 
	    (NameTreeTableHeader<NameUsage<?>>)((JToolTip)c).getComponent();

	int columns = header.getColumnModel().getColumnCount();
	int origin = header.getHeaderRect(1).x;
	int eachHeight = metrics.getHeight();
	for(int index = 1; index < columns; index++) {
	    int x = 3 + header.getHeaderRect(index).x - origin;
	    int i = index - 1;
	    int tipLines = lines[i].length;
	    int maxWidth = 0;
	    for (int j = 0; j < tipLines; j++) {
		int width = SwingUtilities.computeStringWidth(metrics,
							      lines[i][j]);
		if(maxWidth < width)
		    maxWidth = width;
	    }
	    g.setColor(c.getBackground());
	    g.fillRect(x, 0, maxWidth + 6, size.height);
	    g.setColor(c.getForeground());

	    for (int j = 0; j < tipLines; j++) {
		g.drawString(lines[i][j], x, eachHeight * (j + 1));
	    }
	}
	/*
	if(keyText == null || keyText.length() == 0)
	    return;
	lines = getLines(keyText);
	if (lines == null)
	    return;

	g.setFont(new Font
	    (font.getName(), font.getStyle(), font.getSize() - 2 ));

	g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());

	tipLines -= lines.length;
	if(tipLines < 0)
	    tipLines = 0;

	for (int i = 0; i < lines.length; i++) {
	    g.drawString(lines[i],
			 maxWidth + padSpaceBetweenStrings, 
			 (metrics.getHeight()) * (i+1 - tipLines));
	}
	*/
	//RepaintManager.currentManager(c).setDoubleBufferingEnabled(true);
	//c.getRootPane().setDoubleBuffered(true);
	//c.getContentPane().setDoubleBuffered(true);
    }

    public static Dimension getPreferredSize(JComponent c)
    {
	return getPreferredSize(c, ((JToolTip)c).getTipText());
    }

    public static Dimension getPreferredSize(JComponent c,
				      String tipText)
    {
	return getPreferredSize(c, tipText, null);
    }

    @SuppressWarnings("unchecked")
    public static Dimension getPreferredSize(JComponent c,
				      String tipText,
				      String keyText)
    {
	String[][] lines = getLines(tipText);
	if(lines.length < 2)
	    return MatrixToolTipAdaptor.getPreferredSize(c, tipText, keyText);
	/*
	c.setOpaque(false);
	((JToolTip)c).getComponent().getRootPane().setOpaque(false);
	((JToolTip)c).getComponent().getRootPane().setDoubleBuffered(true);
	//c.getContentPane().setDoubleBuffered(false);
	RepaintManager.currentManager(c).setDoubleBufferingEnabled(false);
	RepaintManager.currentManager(((JToolTip)c).getComponent().getRootPane()).setDoubleBufferingEnabled(false);
	*/
	FontMetrics metrics = c.getGraphics().getFontMetrics(c.getFont());

	int maxWidth = 0;
	int maxLines = 0;

	if(lines != null) {
	    for(int i = 0; i < lines.length; i++) {
		int maxLineWidth = 0;
		if(maxLines < lines[i].length)
		    maxLines = lines[i].length;
		for(int j = 0; j < lines[i].length; j++) {
		    int width = SwingUtilities.computeStringWidth(metrics, lines[i][j]);
		    if(maxLineWidth < width)
			maxLineWidth = width;
		}
		maxWidth += maxLineWidth + 6;
	    }
	}

	/*
	if(keyText != null && keyText.length() > 0) {
	    int maxKey = 0;
	    if(lines != null) {
		for(int i = 0; i < lines.length; i++) {
		    int width = SwingUtilities.computeStringWidth(metrics, lines[i]);
		    if(maxKey < width)
			maxKey = width;
		}
	    }
	    maxWidth += maxKey;
	}
	*/

	int height = metrics.getHeight() * maxLines;

	NameTreeTableHeader<NameUsage<?>> header = 
	    (NameTreeTableHeader<NameUsage<?>>)((JToolTip)c).getComponent();

	int columns = header.getColumnModel().getColumnCount();
	Rectangle rect = header.getHeaderRect(columns - 1);
	maxWidth = rect.x + rect.width - header.getHeaderRect(1).x + 6 * (columns - 2);

	return new Dimension(maxWidth, height + 4);
    }

    public static String[][] getLines(String text)
    {
	return MatrixToolTipAdaptor.getLines(text);
    }
    
}
