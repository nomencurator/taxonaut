/*
 * LocaleModel.java:  a ComboBoxModel retaining all locales
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

package org.nomencurator.gui.swing.model;

import java.util.Vector;
import java.util.List;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.nomencurator.util.NomenclaturalCode;

/**
 * {@code LocaleModel} provides a {@code ComboBoxModel}.
 * containing locale data
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class LocaleModel
    extends DefaultComboBoxModel<Locale>
    implements ListDataListener
{
    private static final long serialVersionUID = 4877651216784398121L;

    public static Locale UNSPECIFIED;
    protected static LocaleModel master;

    static {
	master = new LocaleModel(Locale.getAvailableLocales());
	UNSPECIFIED = Locale.ROOT;
	master.insertElementAt(UNSPECIFIED, 0);
	Locale[] codes = NomenclaturalCode.getCodes();
	for (Locale code : codes)
	    master.addElement(code);
	master.setSelectedItem(null);
    }

    protected static Vector<Locale> getLocales() {
	Vector<Locale> locales = null;
	synchronized(master) {
	    int size = master.getSize();
	    locales = new Vector<Locale>(size);
	    for(int i = 0; i < size; i++) {
		locales.addElement(master.getElementAt(i));
	    }
	}
	return locales;
    }

    private LocaleModel(Locale[] locales) {
	super(locales);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private LocaleModel(List<Locale> locales) {
	super(new Vector(locales));
    }

    public LocaleModel()
    {
	super(getLocales());
	master.addListDataListener(this);
    }

    public void addListDataListener(ListDataListener listener) {
	if(listener != master)
	    super.addListDataListener(listener);
    }

    protected void addElement(Locale anObject, LocaleModel caller) {
	if(caller == master)
	    super.addElement(anObject);
	else
	    master.addElement(anObject);
    }

    public void addElement(Locale anObject) {
	if(this == master) {
	    if(getIndexOf(anObject) < 0 || getIndexOf(anObject) >= getSize()) {
		super.addElement(anObject);
	    }
	} else {
	    addElement(anObject, this);
	}
    }

    protected void insertElementAt(Locale anObject, int index, LocaleModel caller) {
	if(caller == master)
	    super.insertElementAt(anObject, index);
	else
	    master.insertElementAt(anObject, index);
    }

    public void insertElementAt(Locale anObject, int index) {
	if(this == master) {
	    if(getIndexOf(anObject) < 0 || getIndexOf(anObject) >= getSize()) {
		super.insertElementAt(anObject, index);
	    }
	} else {
	    insertElementAt(anObject, index, this);
	}
    }

    public void intervalAdded(ListDataEvent e) {
	if(e.getType() != ListDataEvent.INTERVAL_ADDED)
	    return;
	if(e.getSource() != master)
	    return;

	int index0 = e.getIndex0();
	int index1 = e.getIndex1();

	for (int i = index1; i >= index0; i--) {
	    insertElementAt(master.getElementAt(i), index0, master);
	}
    }

    public void intervalRemoved(ListDataEvent e) {
	if(e.getType() != ListDataEvent.INTERVAL_REMOVED)
	    return;
	if(e.getSource() != master)
	    return;

	int index0 = e.getIndex0();
	int index1 = e.getIndex1();

	for (int i = index1; i >= index0; i--) {
	    removeElementAt(i, master);
	}
    }

    protected void removeElementAt(int index, LocaleModel caller) {
	if(caller == master)
	    super.removeElementAt(index);
	else
	    master.removeElementAt(index);
    }

    public void removeElementAt(int index) {
	if(this == master)
	    super.removeElementAt(index);
	else
	    removeElementAt(index, this);
    }

    protected void removeElement(Object anObject, LocaleModel caller) {
	if(caller == master)
	    super.removeElement(anObject);
	else
	    master.removeElement(anObject);
    }

    public void removeElement(Object anObject) {
	if(this == master)
	    super.removeElement(anObject);
	else
	    removeElement(anObject, this);
    }

    protected void removeAllElements(LocaleModel caller) {
	if(caller == master)
	    super.removeAllElements();
	else
	    master.removeAllElements();
    }

    public void removeAllElements() {
	if(this == master)
	    super.removeAllElements();
	else
	    removeAllElements(this);
    }

    public void contentsChanged(ListDataEvent e) {
    }
}
