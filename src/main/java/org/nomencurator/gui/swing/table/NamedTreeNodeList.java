/*
 * NamedTreeNodeList.java:  a List of NameTreeNode labeled with their name
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

package org.nomencurator.gui.swing.table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import javax.swing.tree.TreeNode;

import lombok.Getter;
import lombok.Setter;

/**
 * <code>NamedTreeNodeList</code> provides a <tt>List</tt> of <code>NameTreeNode</code>s labelled with their name.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class NamedTreeNodeList<E extends TreeNode>
    implements List<E>
{
    @Getter
    @Setter
    /** name of <tt>NameTreeNode</tt>s */
    protected String name;
    
    @Getter
    /** <tt>List</tt> of <tt>NameTreeNode</tt>s */
    protected List<E> treeNodes;
    
    /**
     * Constructs a unnamed, an empty list of <tt>TreeNode</tt>s.
     */
    public NamedTreeNodeList()
    {
	this.treeNodes = new ArrayList<E>();
    }

    /**
     * Constructs a list of given <tt>size</tt> and  the name.
     *
     * @param name <tt>String</tt> representing the name of <tt>TreeNode</tt>s.
     * @param size initial size of the list.
     *
     */
    public NamedTreeNodeList(String name, int size)
    {
	this.name = name;
	this.treeNodes = new ArrayList<E>(size);
    }

    /**
     * Constructs a list containing specified <tt>NameTreeNode</tt>s having the name.
     *
     * @param name <tt>String</tt> representing the name of <tt>NameTreeNode</tt>s.
     * @param nameTreeNodes a <tt>Collection</tt> of <tt>NameTreeNode</tt>s.
     *
     */
    public NamedTreeNodeList(String name, Collection<? extends E> treeNodes)
    {
	this.name = name;
	if(treeNodes != null)
	    this.treeNodes = new ArrayList<E>(treeNodes);
	else
	    this.treeNodes = new ArrayList<E>();
    }

    public boolean add(E e) { return treeNodes.add(e); }
    public void add(int index, E element) { treeNodes.add(index, element); }
    public boolean addAll(Collection<? extends E> c) { return treeNodes.addAll(c); }
    public boolean  addAll(int index, Collection<? extends E> c) { return treeNodes.addAll(index, c); }
    public void  clear() { treeNodes.clear(); }
    public boolean  contains(Object o) { return treeNodes.contains(o); }
    public boolean  containsAll(Collection<?> c) { return treeNodes.containsAll(c); }
    public E  get(int index) { return treeNodes.get(index); }
    public int  indexOf(Object o) { return treeNodes.indexOf(o); }
    public boolean  isEmpty() { return treeNodes.isEmpty(); }
    public Iterator<E>  iterator() { return treeNodes.iterator(); }
    public int  lastIndexOf(Object o) { return treeNodes.lastIndexOf(o); }
    public ListIterator<E>  listIterator() { return treeNodes.listIterator(); }
    public ListIterator<E>  listIterator(int index) { return treeNodes.listIterator(index); }
    public E  remove(int index) { return treeNodes.remove(index); }
    public boolean  remove(Object o) { return treeNodes.remove(o); }
    public boolean  removeAll(Collection<?> c) { return treeNodes.removeAll(c); }
    public boolean  retainAll(Collection<?> c) { return treeNodes.retainAll(c); }
    public E  set(int index, E element) { return treeNodes.set(index, element); }
    public int  size() { return treeNodes.size(); }
    public List<E>  subList(int fromIndex, int toIndex) { return treeNodes.subList(fromIndex, toIndex); }
    public Object[]  toArray() { return treeNodes.toArray(); }
    public <T> T[]  toArray(T[] a) { return treeNodes.toArray(a); }
}
