/*
 * ArrayUtility.java:  an utilty to operate an Java array
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

/*
 * The work is partially supproted by JSPS KAKENHI Grant Number JP17300071
 */

package org.nomencurator.util;

import java.util.ArrayList;
import java.util.List;


/**
 * {@code ArrayUtility} provides operations manipurating an Java array
 *
 * @version 	09 July 2016
 * @author 	Nozomi `James' Ytow
 */
public class ArrayUtility
{

    ArrayUtility() {}

    /**
     * Examine whether {@code array} contains {@code object}
     *
     * @param object to be looked for
     * @param array to be examined
     *
     * @return true if {@code array} contains {@code object}
     * or false if {@code object} is not found in {@code array}, or inapplicable,
     * i.e. if either {@code object} or {@code array} is null.
     */
    public static <T> boolean contains(T object, T[] array) 
    {
	return !(indexOf(object, array) == -1);
    }

    /**
     * Returns index of {@code object} in {@code array}
     *
     * @param object to be looked for
     * @param array to be searched for the {@code object}
     *
     * @return index of the {@code object} in {@code array},
     * or -1 if {@code object} is not found in {@code array}, or inapplicable,
     * i.e. if either {@code object} or {@code array} is null.
     */
    public static <T> int indexOf(T object, T[] array) 
    {
	if(object == null || 
	   array == null ||
	   array.length == 0)
	    return -1;
	
	for(int i = 0; i < array.length; i++) {
	    if(array[i] == object)
		return i;
	}

	return -1;
    }

    /**
     * Create and return an array containing {@code object} after elements
     * of {@code array} if {@code array} does not contain {@code object}.
     * If returned array is the {@code array}, {@code object} was not added.
     * If the {@code array} is null or empty and {@code object} is not null,
     * it crates a new array contains only the {@code object}.
     *
     * @param object to add
     * @param array to accept {@code object}
     *
     * @return a new array containing {@code object} after elements of{@code array},
     * or null if both are null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] add(T object, T[] array)
    {
	if(object == null)
	    return array;

	if(array == null ||
	   array.length == 0) {
	    array = (T[])create(object.getClass(), 1);
	    array[0] = object;
	    return array;
	}

	   
	T[] toReturn = (T[])java.lang.reflect.Array.newInstance(object.getClass(), array.length + 1);
	System.arraycopy(array, 0, toReturn, 0, array.length);

	toReturn[array.length] = object;

	return toReturn;
    }


    /**
     * Create and return an array containing {@code object} after elements
     * of {@code array} if {@code array} does not contain {@code object}.
     * If returned array is the {@code array}, {@code object} was not added.
     * If the {@code array} is null or empty and {@code object} is not null,
     * it crates a new array contains only the {@code object}.
     *
     * @param object to add
     * @param array to accept {@code object}
     *
     * @return a new array containing {@code object} after elements of{@code array},
     * or null if both are null.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] add(T object, int index, T[] array)
    {

	if(object == null ||
	   index < 0)
	    return array;

	if(array == null ||
	   array.length == 0) {
	    array = (T[])java.lang.reflect.Array.newInstance(object.getClass(), index);
	    array[index - 1] = object;
	    return array;
	}

	T[] toReturn = null;
	if(index <= array.length) {
	    toReturn = 
		(T[])java.lang.reflect.Array.newInstance(object.getClass(), array.length + 1);
	    System.arraycopy(array, 0, toReturn, 0, index - 1);
	    System.arraycopy(array, index, toReturn, index + 1, array.length - index);
	}
	else {
	    toReturn = 
		(T[])java.lang.reflect.Array.newInstance(object.getClass(), index);
	    System.arraycopy(array, 0, toReturn, 0, array.length);
	}
	toReturn[index] = object;
	return toReturn;
    }

    /**
     * Return an array of elements in {@code array}
     * but {@code object}.
     * If {@code array} does not contain {@code object},
     * it returns {@code array} itslef.
     *
     * @param object to remove
     * @param array to be removed
     *
     * @return a new array containing elements of {@code array}
     * but {@code object} or {@code array} if it does
     * not contain the {@code object}.
     *
     * @see removeElement(T, T[])
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] remove(T object, T[] array)
    {
	int index = indexOf(object, array);
	if(index == -1)
	    return array;

	if(array.length == 1) {
	    array[0] = null;
	    return null;
	}
	
	T[] toReturn = 
	    (T[])java.lang.reflect.Array.newInstance(object.getClass(), array.length - 1);
	System.arraycopy(array, 0, toReturn, 0, index);
	if(index < array.length - 1) {
	    System.arraycopy(array, index+1, toReturn, index, array.length - index - 1);
	}

	return toReturn;
    }

    /**
     * Remove all elements from {@code array}
     *
     * @param array to clear
     */
    public static <T> void clear(T[] array)
    {
	if(array == null ||
	   array.length == 0)
	    return;

	synchronized (array) {
	    for (int i = 0; i < array.length; i++) {
		array[i] = null;
	    }
	}
    }

    /**
     * Returns a copy of {@code array}
     *
     * @param array to copy
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] copy(T[] array)
    {
	T[] toReturn = null;
	if(array != null) {
	    synchronized (array) {
		toReturn = (T[])java.lang.reflect.Array.newInstance(array[0].getClass(), array.length);
		System.arraycopy(array, 0, toReturn, 0, array.length);
	    }
	}

	return toReturn;
    }

    /**
     * Returns a copy of {@code list}
     *
     * @param array to copy
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] toArray(List<? extends T> list)
    {
	T[] toReturn = null;
	if(list != null) {
	    synchronized (list) {
		toReturn =  (T[])java.lang.reflect.Array.newInstance(list.toArray()[0].getClass(), list.size());
		toReturn = list.toArray(toReturn);
	    }
	}

	return toReturn;
    }

    /**
     * Creates an array of speicified capacity.
     *
     * @param size of array to create
     * @return created array
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] create(Class<T> clazz, int size)
    {
	return (T[])java.lang.reflect.Array.newInstance(clazz, size);
    }

    /**
     * Creates an array of speicified capacity.
     *
     * @param size of array to create
     * @return created array
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] create(int size)
    {
	List<T> list = new ArrayList<T>(1);
	return (T[])java.lang.reflect.Array.newInstance(list.toArray()[0].getClass(), size);
    }

    /**
     * Create and return an array containing elements of {@code source1} and 
     * {@code source2}, or null if both sources are null.
     *
     * @param source1 array to merge
     * @param source2 array to merge
     *
     * @return a new array containing all elements of source arrays, or null if both are null.
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T[] merge(T[] source1, T[] source2)
    {
	if(source1 == null || source1.length == 0)
	    return source2;

	if(source2 == null || source2.length == 0)
	    return source1;

	T[] toReturn =
	    (T[])java.lang.reflect.Array.newInstance(source1[0].getClass(), source1.length + source2.length);
	System.arraycopy(source1, 0, toReturn, 0, source1.length);
	System.arraycopy(source2, 0, toReturn, source1.length + 1, source2.length);

	return toReturn;
    }

    /**
     * Create and return an array containing elements of {@code sources},
     * or null if all source arrays are null.
     *
     * @param sources arrays to merge
     *
     * @return a new array containing all elements of source arrays, or null if all source arrays are null.
     */
    @SafeVarargs
    @SuppressWarnings({"unchecked", "varargs"})
    public static <T> T[] merge(T[]... sources)
    {
	if(sources == null)
	    return null;

	switch(sources.length) {
	case 0:
	    return null;
	case 1:
	    return sources[0];
	case 2:
	    return merge(sources[0], sources[1]);
	default:
	    int length1 = sources.length / 2;
	    int length2 = sources.length - length1;
	    
	    T[][] source1 = (T[][])java.lang.reflect.Array.newInstance(sources[0][0].getClass(), length1);
	    System.arraycopy(sources, 0, source1, 0, length1);
	    T[][]source2 = (T[][])java.lang.reflect.Array.newInstance(sources[0][0].getClass(), length2);
	    System.arraycopy(sources, length1+1, source2, 0, length2);
	    
	    return merge(merge(source1), merge(source2));
	}
    }
}
