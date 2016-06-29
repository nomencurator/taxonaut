/*
 * ParsedName.java:  a set of setter methods to extend org.gbif.api.model.checklistbank.ParsedName
 *
 * Copyright (c) 2014, 2015 Nozomi `James' Ytow
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

package org.nomencurator.api.gbif.model.checklistbank.search;

import static com.google.common.collect.Lists.newArrayList;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

/**
 * <tt>ProxiedList</tt> provides a list of values.
 *
 * @version 	16 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class ProxiedList<E> /* implements List<E> */ {

    @Getter
    @Setter
    private List<String> values = newArrayList();

    /*
    @Getter
    private List<E> entities = null;
    */
    
    /*
    public void setEntities(List<E> entities) {
	this.entities = entities;
	values.clear();
	if(entities != null) {
	    for (E e: entities) {
		values.add(e.toString());
	    }
	}
    }

    public ProxiedList () {
	this(null);
    }

    public ProxiedList (List<E> entities) {
	setEntities(entities);
    }

    @Override
    public boolean add(E e) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.add(e);
	}
	result |= values.add(e.toString());
	return result;
    }

    @Override
    public void add(int index, E element) {
	if(entities != null) {
	    entities.add(index, element);
	}
	values.add(index, element.toString());
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.addAll(c);
	}
	for (E e: c) {
	    result |= values.add(e.toString());
	}
	return result;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.addAll(index, c);
	}
	for (E e: c) {
	    values.add(index++, e.toString());
	}
	return result;
    }

    @Override
    public void clear() {
	if(entities != null) {
	    entities.clear();
	}
	values.clear();
    }

    @Override
    public boolean contains(Object o) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.contains(o);
	}
	result |= values.contains(o.toString());
	return result;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
	boolean result = true;
	if(entities != null) {
	    result &= entities.containsAll(c);
	}
	for (Object e: c) {
	    result &= values.contains(e.toString());
	}
	return result;
    }

    @Override
    public boolean equals(Object o) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.equals(o);
	}
	result |= values.equals(o);
	return result;
    }

    public E get(int index) {
	if(entities != null) {
	    return entities.get(index);
	}
	return null;
    }

    public String getValues(int index) {
	return values.get(index);
    }

    public int hashCode() {
	if(entities != null) {
	    return entities.hashCode();
	}
	return values.hashCode();
    }

    public int indexOf(Object o) {
	int index = -1;
	if(entities != null) {
	    index = entities.indexOf(o);
	}
	if (index == -1) {
	    index = values.indexOf(o.toString());
	}
	return index;
    }

    public boolean  isEmpty() {
	boolean result = true;
	if(entities != null) {
	    result &= entities.isEmpty();
	}
	result &= values.isEmpty();
	return result;
    }

    public Iterator<E> iterator() {
	if(entities != null) {
	    return entities.iterator();
	}
	return null;
    }

    public Iterator<String> valuesIterator() {
	return values.iterator();
    }

    public int lastIndexOf(Object o) {
	int index = -1;
	if(entities != null) {
	    index = entities.lastIndexOf(o);
	}
	if(index == -1) {
	    index = values.lastIndexOf(o.toString());
	}
	return index;
    }

    public ListIterator<E> listIterator() {
	if(entities != null) {
	    return entities.listIterator();
	}
	return null;
    }

    public ListIterator<String> listValuesIterator() {
	    return values.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
	if(entities != null) {
	    return entities.listIterator(index);
	}
	return null;
    }

    public ListIterator<String> listValuesIterator(int index) {
	    return values.listIterator(index);
    }

    public E remove(int index) {
	E result  = null;
	if(entities != null) {
	    result = entities.remove(index);
	}
	values.remove(index);
	
	return result;
    }

    public String removeValues(int index) {
	return values.remove(index);
    }

    public boolean remove(Object o) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.remove(o);
	}
	result |= values.remove(o.toString());
	return result;
    }

    public boolean removeAll(Collection<?> c) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.removeAll(c);
	}
	for(Object e: c) {
	    String string = e.toString();
	    while (values.contains(string)) {
		result |= values.remove(string);
	    }
	}
	return result;
    }

    public boolean retainAll(Collection<?> c) {
	boolean result = false;
	if(entities != null) {
	    result |= entities.retainAll(c);
	}
	List<String> toRetain = newArrayList();
	for(Object e:c) {
	    toRetain.add(e.toString());
	}
	result |= values.retainAll(toRetain);
	return result;
    }

    public E set(int index, E element) {
	E result = null;
	if(entities != null) {
	    result = entities.set(index, element);
	}
	values.set(index, element.toString());

	return result;
    }

    public int size() {
	if(entities != null) {
	    return entities.size();
	}
	return values.size();
    }

    public List<E> subList(int fromIndex, int toIndex) {
	if(entities != null) {
	    return entities.subList(fromIndex, toIndex);
	}
	return null;
    }

    public List<String> valuesSubList(int fromIndex, int toIndex) {
	return values.subList(fromIndex, toIndex);
    }

    public Object[] toArray() {
	if(entities != null) {
	    return entities.toArray();
	}
	return values.toArray();
    }

    public <T> T[] toArray(T[] a) {
	if(entities != null) {
	    return entities.toArray(a);
	}
	return null;
    }

    public String[] valuesToArray(String[] a) {
	return values.toArray(a);
    }
    */
}
