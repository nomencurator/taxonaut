/*
 * NameUsageNode.java:  a Java implementation of NameUsageNode class
 * for the Nomencurator, a Nomenclature Heuristic Model.
 *
 * Copyright (c) 2003, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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
import java.util.Set;
import java.util.Vector;

import org.w3c.dom.Element;

/**
 * An implementation of {@code NameUsageNode} in Nomencurator
 * data model.  It was referred to as NRnode in the original publication.
 * It wraps a {@code NameUsage} for efficient navigation over
 * annotated {@code NameUsage}s.
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org</A>
 *
 * @version 	05 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public interface NameUsageNode <T extends NameUsageNode<?>>
    extends NameUsage <T>
{
    ////protected void initializeReferants();

    /**
     * Returns {@code Collection} of
     * relevant {@code Annotations}, or null
     * if there is no {@code Annotations}
     * relevant to this {@code NameUsageNode}
     *
     * @return Collection of relevant {@code Annotation}s
     */
    public Collection<Annotation> getRelevantAnnotations();

    /**
     * Returns {@code Collection} of
     * relevant {@code Annotations} with specified
     * {@code linkType}, or null
     * if there is no {@code Annotations} having
     * {@code linkType}.
     *
     * @param linkType link type of {@code Annotation} to be
     * returned
     *
     * @return Collection of {@code Annotation}s having
     * {@code linkType}
     */
    public Collection<Annotation> getRelevantAnnotations(String linkType);

    /**
     * Adds {@code annotation} to the list of
     * {@code Annotation}s relevant to the
     * {@code NameUsage} and provide cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotation} to be added to
     * the list of relevant {@code Annotation}s
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantAnnotation(Annotation annotation);

    /**
     * Removes {@code annotation} from the list of
     * {@code Annotation}s relevant to the
     * {@code NameUsage} and cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotatation} to be remvoed
     */
    public boolean removeRelevantAnnotation(Annotation annotation);

    /**
     * Returns {@code Collection} of
     * relevant {@code NameUsageNode}s, or null
     * if there is no {@code NameUsageNode}
     * relevant to this {@code NameUsageNode}
     *
     * @return Collection of relevant {@code NameUsageNode}
     * or null if none
     */
    public Collection<NameUsageNode<?/* extends T*/>> getRelevantNodes();

    /**
     * Returns {@code Collection} of
     * relevant {@code NameUsageNode}s with
     * {@code name}, or null
     * if there is no {@code NameUsageNode}
     * ascribed as {@code name}
     *
     * @param name ascribed name of {@code NameUsageNode}s
     *
     * @return Collection of relevant {@code NameUsageNode}
     * with {@code name} or null if none
     */
    public Collection<NameUsageNode<?/* extends T*/>> getRelevantNodesOfName(String name);

    /**
     * Returns {@code Collection} of
     * relevant {@code NameUsageNode}s with
     * persisntet ID {@code id}, or null
     * if there is no {@code NameUsageNode}
     * ascribed as {@code name}.
     * Class name part of {@code id} will be dropped
     * when looking for {@code NameUsageNode}s so
     * persistent ID of either {@code NameUsage}
     * {@code NameUsageNode} will accepted.
     *
     * @param id persisntet ID of {@code NameUsageNode}s
     *
     * @return Collection of relevant {@code NameUsageNode}
     * with {@code id} or null if none
     */
    public Collection<NameUsageNode<?/* extends T*/>> getRelevantNodesOfID(String id);

    /**
     * Adds {@code NameUsageNode}s relevant to
     * {@code NameUsage} referred to by {@code annotation}
     *
     * @param annotation {@code Annotation} where relevant
     * {@code NameUsage} should be retrieved
     *
     * @param annotation {@code Annotation} to be added
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation);
   
    /**
     * Adds {@code NameUsageNode}s representing
     * {@code NameUsage}s enumerated in {@code nodes}
     * to the list of {@code NameUsageNode} relevant to this,
     * with pointer to {@code annotation}.
     *
     * @param annotation {@code Annotation} to be added
     * @param nodes {@code Collection} of {@code NameUsage}
     * to be added
     * @param nodeSet {@code Set}  to hold {@code NameUsageNode}s
     * relevant to the {@code annotation}
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation,
				Collection<? extends NameUsage<?>> nodes,
				Set<NameUsageNode<?>> nodeSet);

    /**
     * Adds {@code NameUsageNode}s representing
     * {@code NameUsage}s enumerated in {@code nodes}
     * to the list of {@code NameUsageNode} relevant to this,
     * with pointer to {@code annotation}.
     *
     * @param annotation {@code Annotation} to be added
     * @param nodes {@code Iterator} for {@code NameUsage}s
     * to be added
     * @param nodeSet {@code Set}  to hold {@code NameUsageNode}s
     * relevant to the {@code annotation}
     *
     * @return number of {@code NameUsageNode} added
     */
    public int addRelevantNodes(Annotation annotation,
				Iterator<? extends NameUsage<?>> nodes,
				Set<NameUsageNode<?>> nodeSet);

    /**
     * Removes {@code NameUsageNode}s relevant to
     * {@code annotation} from cross
     * references between them.
     *
     * @param annotation to which relevant {@code NameUsageNode}s
     * to be removed
     *
     * @return number of removed {@code NameUsageNode}s
     */
    public int removeRelevantNodes(Annotation annotation);

    /**
     * Removes {@code annotation} from the list of
     * {@code Annotation}s relevant to then
     * {@code NameUsage} and cross
     * references between relevant {@code NameUsage}s
     *
     * @param annotation {@code Annotation} of which
     * relevant {@code NameUsageNode} to be remvoed
     * @param nodes {@code Map} from which
     * {@code NameUsageNode} relevant to
     * {@code annotation} to be remvoed
     *
     */
    public int removeRelevantNodes(Annotation annotation, Map<? extends NameUsageNode<?>, Annotation> nodes);

    ////protected String convertToNameUsageNodePID(String nameUsagePID)

    /**
     * Returns persistent ID as a {@code NameUsage},
     * instead of as {@code NameUsageNode}
     *
     * @return String persisten ID of this as a {@code NameUsage}
     */
    public String getNameUsageID();

    /**
     * Converts {@code usage} to {@code NameUsageNode}.
     * If {@code usage} is an instance of {@code NameUsageNode},
     * it does nothing.
     *
     * @param usage {@code NameUsage} to be converted.
     *
     * @return NameUsageNode representing {@code uasge}
     */
    //    public NameUsageNode getNameUsageNode(NameUsage usage);
    public NameUsageNode<T> getNameUsageNode(Object object);

    /**
     * Returns {@code NameUsageNode} proxied by this {@code NameUsagNode}
     *
     * @return NameUsageNode proxied by this {@code NameUsagNode}
     */
    public NameUsageNode<T> getNameUsageNode();

    /**
     * Returns {@code NameUsageNode} representing higher name usage
     *
     * @return {@code NameUsageNode} representing higher name usage
     */
    public NameUsageNode<T> getHigherNameUsageNode();

    public NameUsageNode<T> clone();

    public NameUsageNode<T> create();

}
