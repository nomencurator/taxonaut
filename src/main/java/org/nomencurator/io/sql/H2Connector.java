/*
 * H2Connector.java
 *
 * Copyright (c) 2007, 2015 Nozomi `James' Ytow
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

package org.nomencurator.io.sql;

import java.sql.Connection;

/**
 * <CODE>H2Connector</CODE> provides a connection to
 * the H2 database engine.
 *
 * @see <A HREF="http://www.h2database.com/">H2 database engine</A>
 * @see org.nomencurator.model.NamedObject
 * @see java.sql.Connection
 *
 * @version 	15 July 2015
 * @author 	Nozomi `James' Ytow
 */
public class H2Connector
{
    public static String DRIVER_CLASS_NAME =
	"org.h2.Driver";

    public H2Connector()
    {
    }

    /*
    public Connection connectTo(
 throws Exception {
    Class.forName("org.h2.Driver");
    Connection conn = DriverManager.
      getConnection("jdbc:h2:test", "sa", ""); 
    // add application code here
  }
    */
}
