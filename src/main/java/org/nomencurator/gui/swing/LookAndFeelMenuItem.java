/*
 * LookAndFeelMenuItem.java:  a JMenuItem to select LookAndFeel
 *
 * Copyright (c) 2016 Nozomi `James' Ytow
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import javax.swing.event.EventListenerList;

import org.nomencurator.util.XMLResourceBundleControl;

/**
 * {@code LookAndFeelMenuItem} is a {@link JMenuItem}
 * to select a look and feel.
 *
 * @version 	23 Sep. 2016
 * @author 	Nozomi `James' Ytow
 */
public class LookAndFeelMenuItem
    extends JMenu
    implements ActionListener
{
    private static final long serialVersionUID = -7994533581428189502L;

    public enum TextPropertyKey {
	LOOK_AND_FEEL("Appearance")
	;
	
	private String value;

	private TextPropertyKey(String value) {
	    this.value = value;
	}

	public String toString() {
	    return value;
	}
    }

    /** list of {@code EventListener}s listening this {@code LanguageMenu} */
    protected EventListenerList listeners;

    protected ButtonGroup lookAndFeelGroup;

    protected ButtonGroup getLookAndFeelGroup()
    {
	if (lookAndFeelGroup == null)
	    lookAndFeelGroup = new ButtonGroup();
	return lookAndFeelGroup;
    }

    public LookAndFeelMenuItem()
    {
	updateLookAndFeels();
    }

    /**
     * Localizes components according to specified locale.
     *
     * @param locale {@code Locale} to be used for localization
     */
    public void setLocale(Locale locale)
    {
	ResourceBundle resource =
	    ResourceBundle.getBundle(getClass().getName(), locale, new XMLResourceBundleControl());
	String text = resource.getString(TextPropertyKey.LOOK_AND_FEEL.name());
	if (text == null)
	    text = TextPropertyKey.LOOK_AND_FEEL.toString();
	setText(text);
    }

    public void updateUI()
    {
	super.updateUI();
	LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
	if (currentLookAndFeel != null) {
	    String current = currentLookAndFeel.getClass().getName();
	    for (Enumeration<AbstractButton> buttons = getLookAndFeelGroup().getElements();
		 buttons.hasMoreElements();) {
		AbstractButton button = buttons.nextElement();
		if (button.getActionCommand().equals(current)) {
		    button.setSelected(true);
		    break;
		}
	    }
	}
    }

    protected void updateLookAndFeels()
    {
	clearButtons();
	LookAndFeel currentLookAndFeel = UIManager.getLookAndFeel();
	String current = currentLookAndFeel == null ? "" : currentLookAndFeel.getName();
	LookAndFeelInfo[] infoArray = UIManager.getInstalledLookAndFeels();
	if (infoArray == null || infoArray.length == 0) {
	    return;
	}

	for (LookAndFeelInfo info : infoArray) {
	    JRadioButtonMenuItem button = new JRadioButtonMenuItem(info.getName());
	    button.setActionCommand(info.getClassName());
	    if (info.getName().equals(current))
		button.setSelected(true);
	    addButton(button);
	}
    }

    protected void addButton(AbstractButton button)
    {
	if (button == null)
	    return;
	add(button);
	getLookAndFeelGroup().add(button);
	button.addActionListener(this);
    }

    protected void removeButton(AbstractButton button)
    {
	if (button == null)
	    return;
	button.removeActionListener(this);
	getLookAndFeelGroup().remove(button);
	remove(button);
    }

    protected void clearButtons()
    {
	List<AbstractButton> toRemove = new ArrayList<>();
	for (Enumeration<AbstractButton> buttons = lookAndFeelGroup.getElements();
	     buttons.hasMoreElements();) {
	    AbstractButton button = buttons.nextElement();
	    button.removeActionListener(this);
	    toRemove.add(button);
	}
	for (AbstractButton button : toRemove) {
	    lookAndFeelGroup.remove(button);
	}
    }

    public void addActionListener(ActionListener listener)
    {
	if(listeners == null)
	    listeners = new EventListenerList();
	listeners.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener)
    {
	listeners.remove(ActionListener.class, listener);
    }

    public void actionPerformed(ActionEvent event)
    {
	if(listeners == null)
	    return;

	ActionEvent e = new ActionEvent(this,
					event.getID(), event.getActionCommand(),
					event.getWhen(), event.getModifiers());
	ActionListener[] actionListeners = listeners.getListeners(ActionListener.class);
	for (ActionListener listener : actionListeners) {
	    listener.actionPerformed(e);
	}
    }
}
    

