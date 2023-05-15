/*
 * LanuguageMenu.java:  a JMenu for language selection
 *
 * Copyright (c) 2003, 2014, 2016 Nozomi `James' Ytow
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

package org.nomencurator.gui.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code LangageMenu} provieds a menu to choose a language
 *
 * @version 	27 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LanguageMenu
    extends JMenu
    implements ActionListener
{
    private static final long serialVersionUID = 1795988815270720388L;

    /**
       Array of {@code JMenuItem}s
       with supported {@code Locale} names
       as their labels
    */
    protected JMenuItem[] locales;

    /** Array of {@code Locale}s supported by resources */
    protected Locale[] supported;

    /** list of {@code EventListener}s listening this {@code LanguageMenu} */
    protected EventListenerList listeners;

    /** index of current locale */
    protected int currentLocaleIndex;

    public LanguageMenu()
    {
	this(Locale.getDefault());
    }

    public LanguageMenu(Locale locale)
    {
	supported = 
	    ResourceKey.getSupportedLocale();
	locales = new JMenuItem[supported.length];
	for(int i = 0; i < locales.length; i++) {
	    locales[i] = new JMenuItem();
	    locales[i].addActionListener(this);
	    add(locales[i]);
	}
	setLocale(locale);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	Locale l = getLocale();

	super.setLocale(locale);

	String languageText = ResourceKey.LANGUAGES;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    languageText = resource.getString(languageText);
	}
	catch(MissingResourceException e) {

	}

	setText(languageText);

	int lastLocale = currentLocaleIndex;
	currentLocaleIndex = -1;
	for(int i = 0, j = 1;
	    i < locales.length && j < locales.length;
	    i++) {
	    if(locale.equals(supported[i]))
		currentLocaleIndex = i;
	}

	if(currentLocaleIndex == -1) {
	    for(int i = 0; i < locales.length; i++) {
		locales[i].setText(supported[i].getDisplayName(locale));
	    }
	}
	else {
	    locales[0].setText(locale.getDisplayName(locale));
	    for(int i = 0, j = 1;
		i < locales.length && j < locales.length;
		i++) {
		if(!locale.equals(supported[i]))
		    locales[j++].setText(supported[i].getDisplayName(locale));
	    }
	}
	
	if(currentLocaleIndex == -1)
	    currentLocaleIndex = lastLocale;

	if(!locale.equals(l))
	    fireChangeEvent();
    }

    public void addChangeListener(ChangeListener listener)
    {
	if(listeners == null)
	    listeners = new EventListenerList();
	listeners.add(ChangeListener.class, listener);
    }

    public void removeChangeListener(ChangeListener listener)
    {
	listeners.remove(ChangeListener.class, listener);
    }

    protected void fireChangeEvent()
    {
	if(listeners == null)
	    return;

	Object[] targets = listeners.getListenerList();
	ChangeEvent changeEvent = null;
	for (int i = targets.length-2; i>=0; i-=2) {
	    if (targets[i]==ChangeListener.class) {
		if (changeEvent == null)
		    changeEvent = 
			new ChangeEvent(this);
		((ChangeListener)targets[i+1]).stateChanged(changeEvent);
	    }
	}	
    }

    public void actionPerformed(ActionEvent event)
    {
	Object source = event.getSource();
	for(int i = 0; i < locales.length; i++) {
	    if(source == locales[i]) {
		if(i == 0 /* && currentLocaleIndex > 0*/)
		    setLocale(supported[currentLocaleIndex]);
		else if(i > currentLocaleIndex)
		    setLocale(supported[i]);
		else
		    setLocale(supported[i - 1]);
		return;
	    }
	}
    }
}


