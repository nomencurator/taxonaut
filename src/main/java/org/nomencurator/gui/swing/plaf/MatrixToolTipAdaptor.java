/*
 * MatrixToolTipAdaptor.java:  an adaptor supporting multiple lines
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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;

import javax.swing.plaf.metal.MetalLookAndFeel;

import org.nomencurator.gui.swing.MatrixToolTip;
/**
 * {@code MultiLinesToolTipAdaptor} provides an addaptor
 * supporting multiple lined  {@code JToolTip}.
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class MatrixToolTipAdaptor
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
	String[][] lines = getLines(tipText);

	if(lines == null || lines.length < 2) {
	    MultiLinesToolTipAdaptor.paint(g, c, tipText, keyText);
	    return;
	}

	Font font = c.getFont();
	FontMetrics metrics = c.getFontMetrics(font);
	Dimension size = c.getSize();

        if (c.isOpaque()) {
	    g.setColor(c.getBackground());
	    g.fillRect(0, 0, size.width, size.height/2);
	}

	g.setColor(c.getForeground());
	g.setFont(font);

        Insets insets = c.getInsets();
        Rectangle rectangle = new Rectangle(
            insets.left,
            insets.top,
            size.width - (insets.left + insets.right),
            size.height - (insets.top + insets.bottom));

	int tipParagraphs = lines.length;
	int paragraphAt = 3 + rectangle.x;

	for (int i = 0; i < tipParagraphs; i++) {
	    int maxWidth = 0;
	    int tipLines = lines[i].length;
	    for (int j = 0; j < tipLines; j++) {
		int width = SwingUtilities.computeStringWidth(metrics, lines[i][j]);
		if(maxWidth < width)
		    maxWidth = width;
	    }
	    if (c.isOpaque() && i > 0) {
		g.setColor(c.getBackground().darker());
		g.drawLine(paragraphAt - 3, 0, paragraphAt - 3 , size.height);
		g.setColor(c.getForeground());
	    }

	    int height = metrics.getHeight();
	    int y = rectangle.y + height;
	    for (int j = 0; j < tipLines; j++) {
		g.drawString(lines[i][j], paragraphAt, y);
		y += height;
	    }
	    paragraphAt += maxWidth + 6;
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
	String[][] lines = getLines(tipText);

	if(lines.length < 2)
	    return MultiLinesToolTipAdaptor.getPreferredSize(c, tipText, keyText);

	FontMetrics metrics = c.getFontMetrics(c.getFont());

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

	return new Dimension(maxWidth, height + 4);
    }

    public static String[][] getLines(String text)
    {
	if(text == null)
	    return new String[][]{{""}};
	BufferedReader reader = 
	    new BufferedReader(new StringReader(text));

	List<List<String>> paragraphs = new ArrayList<>();
	List<String> line = new ArrayList<>();
	paragraphs.add(line);
	int paragraphCount = 1;
	try {
	    while ((text = reader.readLine()) != null) {
		if(text.length() != 0) {
		    line.add(text);
		}
		else {
		    line = new ArrayList<String>();
		    paragraphs.add(line);
		    paragraphCount++;
		}
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}

	if (paragraphCount < 1)
	    return new String[][]{{""}};

	String[][] lines = new String[paragraphCount][];
	for(int i = 0; i < paragraphCount; i++) {
	    line = paragraphs.get(i);
	    int lineCount = line.size();
	    lines[i] = new String[lineCount];
	    for(int j = 0; j < lineCount; j++) {
		lines[i][j] = line.get(j);
	    }
	}

	return lines;
    }
    
}





