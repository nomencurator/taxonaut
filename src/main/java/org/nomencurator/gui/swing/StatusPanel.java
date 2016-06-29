/*
 * StatusPanel.java:  a JPanel to show status of a software
 *
 * Copyright (c) 2015, 2016 Nozomi `James' Ytow
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

import java.awt.CardLayout;

import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import lombok.Getter;

/**
 * <tt>StatusPanel</tt> provieds a JPanel to show status of a software.
 *
 * @version 	26 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class StatusPanel
    extends JPanel
{
    private static final long serialVersionUID = 3875242235368888252L;

    /** <tt>CardLaytout</tt> to switch <tt>Component</tt>s showing status */
    protected CardLayout statusLayout;

    /** A progress bar to show status */
    @Getter
    protected PlaceableProgressBar progressBar;

    /** Designater of the <tt>progress</tt> to switch to */
    public static final String PROGRESS_BAR = "progressBar";

    /** A <tt>JLabel</tt> to show status */
    @Getter
    protected JLabel statusLabel;

    /** Designater of the <tt>statusLabel</tt> to switch to */
    public static final String STATUS_LABEL = "statusLabel";

    /**
     * Constructs a <tt>StatusPanel</tt> using default <tt>Locale</tt>.
     */
    public StatusPanel() {
	this(Locale.getDefault());
    }

    /**
     * Constructs a <tt>StatusPanel</tt> using <tt>locale</tt>.
     *
     * @param locale to be used.
     */
    public StatusPanel(Locale locale) {
	super(new CardLayout());
	statusLayout = (CardLayout)getLayout();
	createComponents();
	setLocale(locale);
	layoutComponents();
	setComponentsSize();
    }

    /**
     * Creates components to display.
     */
    protected void createComponents() {
	statusLabel = new JLabel();
	statusLabel.setText("");
	add(statusLabel, STATUS_LABEL);
	progressBar = new PlaceableProgressBar();
	progressBar.setTextPlacement(SwingConstants.LEFT);
	add(progressBar, PROGRESS_BAR);
	show(STATUS_LABEL);
    }

    @Override
    public void setLocale(Locale locale) {
    }

    /**
     * Layouts components
     */
    protected void layoutComponents() {
    }

    /**
     * Sets size of components
     */
    protected void setComponentsSize() {
    }

    /**
     * Show the component specified by <tt>name</tt>
     *
     * @param name of the component to show
     */
    public void show(String name) {
	statusLayout.show(this, name);
    } 

    /**
     * Sets <tt>text</tt> to the label
     *
     * @param text to set
     */
    public void setLabelText(String text) {
	statusLabel.setText(text);
    }

    /**
     * Show the label afeter setting <tt>text</tt>.
     *
     * @param text to set
     */
    public void showLabel(String text) {
	setLabelText(text);
	show(STATUS_LABEL);
    }

    /**
     * Show the label for <tt>milliSeconds</tt> afeter setting <tt>text</tt>,
     * then clear the contents.
     *
     * @param text to set
     * @param milliSeconds duration to show the text, in milliseconds
     */
    public void showLabel(String text, final long milliseconds) {
	showLabel(text);
	Thread thread = new Thread() {
		public void run() {
		    try{
			sleep(milliseconds);
		    } catch (Throwable e) {
		    }
		    finally {
			showLabel("");
		    }
		}
	    };
	thread.start();
    }

    /**
     * Sets <tt>text</tt> to the progeress bar
     *
     * @param text to set
     */
    public void setProgressBarlText(String text) {
	progressBar.setString(text);
    }
}
