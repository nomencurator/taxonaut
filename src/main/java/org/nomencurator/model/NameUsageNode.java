/*
 * NameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2003, 2014, 2015, 2016 Nozomi `James' Ytow
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

package org.nomencurator.model;

import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * An implementation of <code>NameUsageNode</code> in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a <code>NameUsage</code> for efficient navigation over 
 * annotated <code>NameUsage</code>s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	23 June 2016
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsageNode <N extends NameUsageNode<?, ?>, T extends N>
					  //public interface NameUsageNode <T>
    extends NameUsage <N, T>
{
    ////protected void initializeReferants();

    /**
     * Returns <CODE>Collection</CODE> of 
     * relevant <CODE>Annotations</CODE>, or null
     * if there is no <CODE>Annotations</CODE>
     * relevant to this <CODE>NameUsageNode</CODE>
     *
     * @return Collection of relevant <CODE>Annotation</CODE>s
     */
    public Collection<Annotation> getRelevantAnnotations();

    /**
     * Returns <CODE>Collection</CODE> of 
     * relevant <CODE>Annotations</CODE> with specified
     * <CODE>linkType</CODE>, or null
     * if there is no <CODE>Annotations</CODE> having
     * <CODE>linkType</CODE>.
     *
     * @param linkType link type of <CODE>Annotation</CODE> to be
     * returned
     *
     * @return Collection of <CODE>Annotation</CODE>s having
     * <CODE>linkType</CODE>
     */
    public Collection<Annotation> getRelevantAnnotations(String linkType);

    /**
     * Adds <CODE>annotation</CODE> to the list of
     * <CODE>Annotation</CODE>s relevant to the
     * <CODE>NameUsage</CODE> and provide cross
     * references between relevant <CODE>NameUsage</CODE>s
     *
     * @param annotation <CODE>Annotation</CODE> to be added to
     * the list of relevant <CODE>Annotation</CODE>s
     *
     * @return number of <CODE>NameUsageNode</CODE> added
     */
    public int addRelevantAnnotation(Annotation annotation);

    /**
     * Removes <CODE>annotation</CODE> from the list of
     * <CODE>Annotation</CODE>s relevant to the
     * <CODE>NameUsage</CODE> and cross
     * references between relevant <CODE>NameUsage</CODE>s
     *
     * @param annotation <CODE>Annotatation</CODE> to be remvoed
     */
    public boolean removeRelevantAnnotation(Annotation annotation);

    /**
     * Returns <CODE>Collection</CODE> of 
     * relevant <CODE>NameUsageNode</CODE>s, or null
     * if there is no <CODE>NameUsageNode</CODE>
     * relevant to this <CODE>NameUsageNode</CODE>
     *
     * @return Collection of relevant <CODE>NameUsageNode</CODE>
     * or null if none
     */
    //public Collection<NameUsageNode<? extends N, ? extends T>> getRelevantNodes();
    public Collection<NameUsageNode<N, ? extends T>> getRelevantNodes();

    /**
     * Returns <CODE>Collection</CODE> of 
     * relevant <CODE>NameUsageNode</CODE>s with
     * <CODE>name</CODE>, or null
     * if there is no <CODE>NameUsageNode</CODE>
     * ascribed as <CODE>name</CODE>
     *
     * @param name ascribed name of <CODE>NameUsageNode</CODE>s
     *
     * @return Collection of relevant <CODE>NameUsageNode</CODE>
     * with <CODE>name</CODE> or null if none
     */
    //public Collection<NameUsageNode<? extends N, ? extends T>> getRelevantNodesOfName(String name);
    public Collection<NameUsageNode<N, ? extends T>> getRelevantNodesOfName(String name);

    /**
     * Returns <CODE>Collection</CODE> of 
     * relevant <CODE>NameUsageNode</CODE>s with
     * persisntet ID <CODE>id</CODE>, or null
     * if there is no <CODE>NameUsageNode</CODE>
     * ascribed as <CODE>name</CODE>.
     * Class name part of <CODE>id</CODE> will be dropped
     * when looking for <CODE>NameUsageNode</CODE>s so
     * persistent ID of either <CODE>NameUsage</CODE>
     * <CODE>NameUsageNode</CODE> will accepted.
     *
     * @param id persisntet ID of <CODE>NameUsageNode</CODE>s
     *
     * @return Collection of relevant <CODE>NameUsageNode</CODE>
     * with <CODE>id</CODE> or null if none
     */
    //public Collection<NameUsageNode<? extends N, ? extends T>> getRelevantNodesOfID(String id);
    public Collection<NameUsageNode<N, ? extends T>> getRelevantNodesOfID(String id);

    /**
     * Adds <CODE>NameUsageNode</CODE>s relevant to 
     * <CODE>NameUsage</CODE> referred to by <CODE>annotation</CODE>
     *
     * @param annotation <CODE>Annotation</CODE> where relevant
     * <CODE>NameUsage</CODE> should be retrieved
     *
     * @param annotation <CODE>Annotation</CODE> to be added
     *
     * @return number of <CODE>NameUsageNode</CODE> added
     */
    public int addRelevantNodes(Annotation annotation);
    
    /**
     * Adds <CODE>NameUsageNode</CODE>s representing
     * <CODE>NameUsage</CODE>s enumerated in <CODE>nodes</CODE>
     * to the list of <CODE>NameUsageNode</CODE> relevant to this,
     * with pointer to <CODE>annotation</CODE>.
     *
     * @param annotation <CODE>Annotation</CODE> to be added
     * @param nodes <CODE>Collection</CODE> of <CODE>NameUsage</CODE>
     * to be added
     * @param nodeSet <CODE>Set</CODE>  to hold <CODE>NameUsageNode</CODE>s 
     * relevant to the <CODE>annotation</CODE>
     *
     * @return number of <CODE>NameUsageNode</CODE> added
     */

    public int addRelevantNodes(Annotation annotation,
				Collection<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nodes,
				Set<NameUsageNode<N, ? extends T>> nodeSet);
				//Set<NameUsageNode<? extends N, ? extends T>> nodeSet);

    public int addRelevantNodes(Annotation annotation,
				Iterator<NameUsage<? extends NameUsage<?, ?>, ? extends NameUsage<?, ?>>> nodes,
				Set<NameUsageNode<N, ? extends T>> nodeSet);
				//				Set<NameUsageNode<? extends N, ? extends T>> nodeSet);

    /**
     * Removes <CODE>NameUsageNode</CODE>s relevant to
     * <CODE>annotation</CODE> from cross
     * references between them.
     *
     * @param annotation to which relevant <CODE>NameUsageNode</CODE>s 
     * to be removed
     *
     * @return number of removed <CODE>NameUsageNode</CODE>s 
     */
    public int removeRelevantNodes(Annotation annotation);

    /**
     * Removes <CODE>annotation</CODE> from the list of
     * <CODE>Annotation</CODE>s relevant to then
     * <CODE>NameUsage</CODE> and cross
     * references between relevant <CODE>NameUsage</CODE>s
     *
     * @param annotation <CODE>Annotation</CODE> of which 
     * relevant <CODE>NameUsageNode</CODE> to be remvoed
     * @param nodes <CODE>Map</CODE> from which 
     * <CODE>NameUsageNode</CODE> relevant to 
     * <CODE>annotation</CODE> to be remvoed
     *
     */
    //public int removeRelevantNodes(Annotation annotation, Map<NameUsageNode<? extends N, ? extends T>, Annotation> nodes);
    public int removeRelevantNodes(Annotation annotation, Map<NameUsageNode<N, ? extends T>, Annotation> nodes);

    ////protected String convertToNameUsageNodePID(String nameUsagePID)

    /**
     * Returns persistent ID as a <CODE>NameUsage</CODE>,
     * instead of as <CODE>NameUsageNode</CODE>
     *
     * @return String persisten ID of this as a <CODE>NameUsage</CODE>
     */
    public String getNameUsageID();

    /**
     * Converts <CODE>usage</CODE> to <CODE>NameUsageNode</CODE>.
     * If <CODE>usage</CODE> is an instance of <CODE>NameUsageNode</CODE>,
     * it does nothing.
     *
     * @param usage <CODE>NameUsage</CODE> to be converted.
     *
     * @return NameUsageNode representing <CODE>uasge</CODE>
     */
    //    public NameUsageNode getNameUsageNode(NameUsage usage);
    public NameUsageNode<N, T> getNameUsageNode(Object object);

    /**
     * Returns <tt>NameUsageNode</tt> proxied by this <tt>NameUsagNode</tt>
     *
     * @return NameUsageNode proxied by this <tt>NameUsagNode</tt>
     */
    public NameUsageNode<N, T> getNameUsageNode();

    /**
     * Returns <code>NameUsageNode</code> representing higher name usage
     *
     * @return <code>NameUsageNode</code> representing higher name usage
     */
    public NameUsageNode<N, T> getHigherNameUsageNode();

    public NameUsageNode<N, T> clone();

    public NameUsageNode<N, T> create();

}

