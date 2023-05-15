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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * {@code MultiLinesToolTipAdaptor} provides an addaptor
 * supporting multiple lined  {@code JToolTip}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class MultiLinesToolTipAdaptor
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

    public static void paint(Graphics g,
		      JComponent c,
		      String tipText,
		      String keyText)
    {
	Font font = c.getFont();
	FontMetrics metrics = c.getFontMetrics(font);
	Dimension size = c.getSize();
        if (c.isOpaque()) {
	    g.setColor(c.getBackground());
	    g.fillRect(0, 0, size.width, size.height);
	}
	g.setColor(c.getForeground());
	g.setFont(font);

        Insets insets = c.getInsets();
        Rectangle rectangle = new Rectangle(
            insets.left,
            insets.top,
            size.width - (insets.left + insets.right),
            size.height - (insets.top + insets.bottom));

	String[] lines = getLines(tipText);
	int tipLines = lines.length;
	int maxWidth = 0;
	if (lines != null) {
	    for (int i = 0; i < tipLines; i++) {
		g.drawString(lines[i], 3, (metrics.getHeight()) * (i+1));
		int width = SwingUtilities.computeStringWidth(metrics, lines[i]);
		if(maxWidth < width)
		    maxWidth = width;
	    }
	}

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

    public static Dimension getPreferredSize(JComponent c,
				      String tipText,
				      String keyText)
    {
	FontMetrics metrics = c.getFontMetrics(c.getFont());
	String[] lines = getLines(tipText);

	int maxWidth = 0;
	
	if(lines != null) {
	    for(int i = 0; i < lines.length; i++) {
		int width = SwingUtilities.computeStringWidth(metrics, lines[i]);
		if(maxWidth < width)
		    maxWidth = width;
	    }
	}
	    
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

	int height = metrics.getHeight() * lines.length;

	return new Dimension(maxWidth + 6, height + 4);
    }

    public static String[] getLines(String text)
    {
	if(text == null)
	    return new String[]{""};

	BufferedReader reader = 
	    new BufferedReader(new StringReader(text));

	List<String>line = new ArrayList<>();
	int lineCount = 0;
	try {
	    while ((text = reader.readLine()) != null) {
		line.add(text);
		lineCount++;
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}

	if (lineCount < 1)
	    return new String[]{""};

	String[] lines = new String[lineCount];
	for(int i = 0; i < lineCount; i++) {
	    lines[i] = line.get(i);
	}

	return lines;
    }
    
}





