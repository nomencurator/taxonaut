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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP13878069
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

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import lombok.Getter;

// import org.nomencurator.resources.ResourceKey;
import org.nomencurator.util.XMLResourceBundleControl;

/**
 * {@coee MenuBar} provides a menu bar to control the aplication software.
 *
 * @version 	23 Sep. 2016
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

    @Getter
    protected JMenu viewMenu;

    @Getter
    protected LookAndFeelMenuItem lookAndFeelItem;

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

	createViewMenu();
	configureViewMenu();
	add(viewMenu);

	/*
	createLanguageMenu();
	configureLanguageMenu();
	add(languageMenu);
	*/

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
	fileMenu.setMnemonic(KeyEvent.VK_F);

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


    protected void createViewMenu()
    {
	viewMenu = new JMenu();
	createLanguageMenu();
	lookAndFeelItem = new LookAndFeelMenuItem();
    }

    protected void configureViewMenu()
    {
	configureLanguageMenu();
	viewMenu.add(languageMenu);
	viewMenu.add(lookAndFeelItem);
    }

    public enum TextPropertyKey {
	FILE("File")
	,OPEN("Open")
	,CLOSE("Close")
	// ,IMPORT("Import")
	,EXPORT("Export")
	// ,SAVE("Save")
	// ,SAVE_AS("Save as ...")
	// ,PRINT("Print")
	,EXIT("Exit")
	,HELP("Help")
	,VERSION("Version")
	,VIEW("View")
	;

	private String value;

	private TextPropertyKey(String value) {
	    this.value = value;
	}

	public String toString() {
	    return value;
	}
    }

    protected static TextPropertyKey[] textPropertyKeys = TextPropertyKey.values();


    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	super.setLocale(locale);

	String[] textProperties = new String[textPropertyKeys.length];
	for (int i = 0; i < textProperties.length; i++) {
	    textProperties[i] = textPropertyKeys[i].toString();
	}

	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(getClass().getName(), locale, new XMLResourceBundleControl());
	    for (int i = 0; i < textProperties.length; i++) {
		textProperties[i] = resource.getString(textPropertyKeys[i].name());
	    }
	}
	catch(MissingResourceException e) {
	}

	fileMenu.setText(textProperties[TextPropertyKey.FILE.ordinal()]);
	openItem.setText(textProperties[TextPropertyKey.OPEN.ordinal()]);
	closeItem.setText(textProperties[TextPropertyKey.CLOSE.ordinal()]);
	exportItem.setText(textProperties[TextPropertyKey.EXPORT.ordinal()]);
	exitItem.setText(textProperties[TextPropertyKey.EXIT.ordinal()]);
	helpMenu.setText(textProperties[TextPropertyKey.HELP.ordinal()]);
	versionMenu.setText(textProperties[TextPropertyKey.VERSION.ordinal()]);
	viewMenu.setText(textProperties[TextPropertyKey.VIEW.ordinal()]);

	fileChooser.setLocale(locale);
	languageMenu.setLocale(locale);
	lookAndFeelItem.setLocale(locale);
    }

    public void stateChanged(ChangeEvent event)
    {
	if(event.getSource() != languageMenu)
	    return;
	setLocale(languageMenu.getLocale());
    }
}
