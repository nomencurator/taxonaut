/*
 * RankComparator.java:  a Comparator  for Rank
 *
 * Copyright (c) 2015 Nozomi `James' Ytow
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

package org.nomencurator.model.util;

import java.util.Comparator;

import org.nomencurator.model.Rank;

import lombok.Getter;
import lombok.Setter;

/**
 * <tt>RankComparator</tt> is a Comparator for <tt>Rank</tt>.
 *
 * @version 	04 Sep. 2015
 * @author 	Nozomi `James' Ytow
 */
public class RankComparator
    implements Comparator<Rank>
{
    @Getter
    @Setter
    protected boolean nullFirst;

    public RankComparator()
    {
	this(true);
    }

    public RankComparator(boolean nullsFist)
    {
	setNullFirst(nullFirst);
    }

    public int compare(Rank r1, Rank r2)
    {
	if (r1 == null) {
	    return (r2 == null) ? 0 : (nullFirst ? -1 : 1);
	}
	else if (r2 == null) {
		return nullFirst ? 1 : -1;
	}

	if (r1.equals(r2))
	    return 0;

	return r1.isHigher(r2) ? 1 : -1;
    }
}

