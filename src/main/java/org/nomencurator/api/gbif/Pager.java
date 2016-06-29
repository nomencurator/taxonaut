/*
 * Pager..java:  utility to manage paging of GBIF API
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

package org.nomencurator.api.gbif;

import org.gbif.api.model.common.paging.Pageable;
import org.gbif.api.model.common.paging.PagingResponse;

/**
 * <CODE>Pager</CODE> provieds utility methods to convert org.gbif.api.model.common.paging.Pageable into REST parameter String.
 * It also provides a method to calcurate the next page parameter.
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class Pager {

    private Pager() {}
    
    /*
     * Returns REST-ish presentation of given Pager
     * 
     * @param pager to convers
     * @return REST representaion of the paging
     */
    protected static String get(Pageable pager, int increment) {
	StringBuffer pageParameter = new StringBuffer();
	if(pager != null) {
	    pageParameter.append("limit=").append(pager.getLimit()).append("&offset=").append(pager.getOffset() + increment);
	}
	return pageParameter.toString();
    }

    /*
     * Returns REST-ish presentation of given Pager
     * 
     * @param pager to convers
     * @return REST representaion of the paging
     */
    public static String get(Pageable pager) {
	return get(pager, 0);
    }

    /*
     * Returns REST-ish presentation of given Pager
     * 
     * @param pager to convers
     * @return REST representaion of the paging
     */
    public static String getNextPage(Pageable pager) {
	return get(pager, 1);
    }

    /*
     * Returns true if <tt>response</tt> is the end of records.
     * It is workaround of GBIF API bug in isEndOfRecords calculation.
     *
     * @param response to examine
     * @return true if the response is end of records.
     *
     */
    public static boolean isEndOfRecords(PagingResponse<?> response) {
	Boolean isEndOfRecords = response.isEndOfRecords();
	int limit = response.getLimit();
	long offset = response.getOffset();
	Long count = response.getCount();

	return (isEndOfRecords != null && isEndOfRecords.booleanValue())
	    || limit <= 0 
	    || (count != null && (offset +  limit >= count));
    }

    /*
     * Returns maximum limit for next query calculated using <tt>response</tt>,
     * as next limit size = count - (offset + limit) if count of totla records is given,
     * or simply given limit  where the count is not given or the limit larger than
     * count.
     *
     * @param response to examine
     * @return maimum limit to specify
     *
     */
    public static int getMaxLimit(PagingResponse<?> response) {
	Long countObject = response.getCount();
	int limit = response.getLimit();
	long offset = response.getOffset();

	if(countObject == null)
	    return limit;

	long count = countObject.longValue();

	if(count < limit)
	    count = limit;
	else
	    count -= limit + offset;

	if(count > Integer.MAX_VALUE)
	    count = Integer.MAX_VALUE;

	return (int)count;
    }
}
