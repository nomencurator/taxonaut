/*
 * RankModel.java:  a ComboBoxModel retaining all ranks
 *
 * Copyright (c) 2006, 2014, 2015, 2016 Nozomi `James' Ytow
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

import javax.swing.DefaultComboBoxModel;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.nomencurator.model.Rank;

/**
 * {@code RankModel} provides a {@code ComboBoxModel}.
 * containing rank data
 *
 * @version 	29 June 2016
 * @author 	Nozomi `James' Ytow
 */
public class RankModel
    extends DefaultComboBoxModel<Rank>
    implements ListDataListener
{
    private static final long serialVersionUID = 1135895132182918840L;

    public static Rank UNSPECIFIED;
    protected static RankModel master;

    static {
	master = new RankModel(Rank.getSortedRanks());
	//UNSPECIFIED = new Rank();
	UNSPECIFIED = Rank.UNRANKED;
	master.insertElementAt(UNSPECIFIED, 0);
	master.setSelectedItem(null);
    }

    protected static Vector<Rank> getRanks() {
	Vector<Rank> ranks = null;
	synchronized(master) {
	    int size = master.getSize();
	    ranks = new Vector<Rank>(size);
	    for(int i = 0; i < size; i++) {
		ranks.addElement(master.getElementAt(i));
	    }
	}
	return ranks;
    }

    private RankModel(List<Rank> sortedRanks) {
	super(new Vector<Rank>(sortedRanks));
    }

    public RankModel()
    {
	super(getRanks());
	master.addListDataListener(this);
    }

    public void addListDataListener(ListDataListener listener) {
	if(listener != master)
	    super.addListDataListener(listener);
    }

    protected void addElement(Rank anObject, RankModel caller) {
	if(caller == master)
	    super.addElement(anObject);
	else
	    master.addElement(anObject);
    }

    public void addElement(Rank anObject) {
	if(this == master) {
	    if(getIndexOf(anObject) < 0 || getIndexOf(anObject) >= getSize()) {
		super.addElement(anObject);
	    }
	} else {
	    addElement(anObject, this);
	}
    }

    protected void insertElementAt(Rank anObject, int index, RankModel caller) {
	if(caller == master)
	    super.insertElementAt(anObject, index);
	else
	    master.insertElementAt(anObject, index);
    }

    public void insertElementAt(Rank anObject, int index) {
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

    protected void removeElementAt(int index, RankModel caller) {
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

    protected void removeElement(Object anObject, RankModel caller) {
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

    protected void removeAllElements(RankModel caller) {
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
