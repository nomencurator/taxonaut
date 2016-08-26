/*
 * MenuBar.java:  a JMenuBar for TaxoNote
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
 * All rights reserved.
 */

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.nomencurator.gui.swing;

import java.awt.Component;
import java.awt.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

import org.nomencurator.resources.ResourceKey;

/**
 * {@coee MenuBar} provides a menu bar to control the aplication software.
 *
 * @version 	21 Aug. 2016
 * @author 	Nozomi `James' Ytow
 */
public class MenuBar
    extends JMenuBar
    implements ChangeListener
{
    private static final long serialVersionUID = -7166036140160098620L;

    @Getter
    protected JMenu fileMenu;
    @Getter
    protected JMenuItem openItem;
    @Getter
    protected JMenuItem closeItem;
    @Getter
    protected JMenuItem exportItem;
    @Getter
    protected JMenuItem exitItem;

    @Getter
    protected SuffixedFileChooser fileChooser;

    @Getter
    protected JMenu helpMenu;
    @Getter
    protected JMenuItem versionMenu;

    @Getter
    protected LanguageMenu languageMenu;

    public MenuBar()
    {
	this(Locale.getDefault());
    }

    public MenuBar(Locale locale)
    {
	super();

	createFileMenu();
	configureFileMenu();
	add(fileMenu);

	createLanguageMenu();
	configureLanguageMenu();
	add(languageMenu);

	createHelpMenu();
	configureHelpMenu();
	add(helpMenu);

	setLocale(locale);
    }

    protected void createFileMenu()
    {
	fileMenu = new JMenu();
	openItem = new JMenuItem();
	closeItem = new JMenuItem();
	exportItem = new JMenuItem();
	exitItem = new JMenuItem();

	fileChooser = new SuffixedFileChooser();
    }

    protected void configureFileMenu()
    {
	fileMenu.add(openItem);
	fileMenu.add(closeItem);
	fileMenu.add(exportItem);
	fileMenu.add(exitItem);
	exitItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    System.exit(0);
		}
	    });

	closeItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    Component container = fileMenu;
		    while(container != null) {
			container = container.getParent();
			if(container instanceof Window)
			    break;
		    }
		    if(container != null)
			((Window)container).dispose();
		}
	    });


	openItem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    fileChooser.showOpenDialog(null);
		}
	    });
    }

    protected void createLanguageMenu()
    {
	languageMenu = new LanguageMenu();
    }

    protected void configureLanguageMenu()
    {
	languageMenu.addChangeListener(this);
    }

    protected void createHelpMenu()
    {
	helpMenu = new JMenu();
	versionMenu = new JMenuItem();
    }

    protected void configureHelpMenu()
    {
	helpMenu.add(versionMenu);
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	super.setLocale(locale);

	String fileText = ResourceKey.FILE;
	String openText = ResourceKey.OPEN;
	String closeText = ResourceKey.CLOSE;
	String exportText = ResourceKey.EXPORT;
	String exitText = ResourceKey.EXIT;
	String helpText = ResourceKey.HELP;
	String versionText = ResourceKey.VERSION;

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(ResourceKey.TAXONAUT, locale);
	    fileText = resource.getString(fileText);
	    openText = resource.getString(openText);
	    closeText = resource.getString(closeText);
	    exportText = resource.getString(exportText);
	    exitText = resource.getString(exitText);
	    helpText = resource.getString(helpText);
	    versionText = resource.getString(versionText);

	}
	catch(MissingResourceException e) {

	}

	fileMenu.setText(fileText);
	fileMenu.setMnemonic(KeyEvent.VK_F);
	openItem.setText(openText);
	closeItem.setText(closeText);
	exportItem.setText(exportText);
	exitItem.setText(exitText);
	helpMenu.setText(helpText);
	versionMenu.setText(versionText);

	languageMenu.setLocale(locale);
	fileChooser.setLocale(locale);
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;
	setLocale(languageMenu.getLocale());
    }
}
