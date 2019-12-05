/*
 * NamedObjectConnection.java
 *
 * Copyright (c) 2003, 2004, 2005, 2007, 2014, 2015, 2016, 2019 Nozomi `James' Ytow
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

package org.nomencurator.io.sql;

import java.lang.reflect.InvocationTargetException;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Savepoint;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Types;

import java.text.MessageFormat;

import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import java.util.concurrent.Executor;

import org.nomencurator.model.Agent;
import org.nomencurator.model.Annotation;
import org.nomencurator.model.Appearance;
import org.nomencurator.model.DefaultNameUsage;
import org.nomencurator.model.DefaultNameUsageNode;
import org.nomencurator.model.NamedObject;
import org.nomencurator.model.NameUsage;
import org.nomencurator.model.NameUsageNode;
import org.nomencurator.model.Person;
import org.nomencurator.model.Publication;
import org.nomencurator.model.Rank;

import org.nomencurator.resources.ResourceKey;

/**
 * {@code NamedObjectConnection} provides pooled {@code Connection}
 * with support of SQL interfaces for {@code NamedObject}
 *
 * @see <A HREF="http://www.nomencurator.org/">http://www.nomencurator.org/</A>
 * @see org.nomencurator.model.NamedObject
 * @see java.sql.Connection
 *
 * @version 	06 Dec. 2019
 * @author 	Nozomi `James' Ytow
 */
public class NamedObjectConnection<T extends NamedObject<?>>
    implements Connection
{
    /** Constant to specify nominal mode in access to the database */
    public static final int NOMINAL = 1;

    /** Constant to specify to resolve references in access to the database */
    public static final int RESOLVE = 2;

    /** Constant to specify recurseve access to the database */
    public static final int RECURSIVE = 4;

    /** Pool of {@code Connections} */
    protected static Map<String, Set<Connection>> connections;

    /** Mappings from object ID to {@code NamedObject} */
    protected static Map<String, Map<String, NamedObject<?>>> oidToObjectMaps;

    /** Mappings from persistent ID to {@code NamedObject} */
    protected static Map<String, Map<String, NamedObject<?>>> pidToObjectMaps;

    /** Mappings from {@code NamedObject} to object ID */
    protected static Map<String, Map<NamedObject<?>, Integer>> objectToOIDMaps;

    /** Mapping from locale name to {@code Locale} for each {@code dataSource} */
    protected static Map<String, Map<String, Locale>> localeMaps;

    /** Mappings between {@code Rank}s and rank ID in each {@code dataSource} */
    protected static Map<String, Map<String, Rank>> rankMaps;
    
    protected Map<String, Rank> ranks;

    /**
     * {@code Connection} wrapped by this {@code Object}
     */
    protected Connection connection;

    /**
     * true if the {@code connection} is in use
     */
    protected boolean isOpen;

    /** {@code String} representing the database */
    protected String dataSource;

    /**
     * {@code String} representing base name of property file
     * storing SQL statements
     */
    protected String resourceBaseName;

    /**
     * Subtype of SQL such as PostgreSQL used to choose appropriate
     * property resource
     */
    protected Locale queryType;

    /**
     * {@code PreparedStatement}s to select a {@code NamedObject}
     * of specified type and object ID
     */
    protected Map<String, PreparedStatement> selectByOID;

    /**
     * {@code PreparedStatement}s to select a {@code NamedObject}
     * of specified type and persistent ID
     */
    protected Map<String, PreparedStatement> selectByPID;

    /**
     * {@code PreparedStatement}s to select {@code NamedObject}s
     * of specified type having persistent ID like given one
     */
    protected Map<String, PreparedStatement> selectLikePID;

    /**
     * {@code PreparedStatement} to select {@code NamedObject}'s
     * object ID of specified persistent ID
     */
    protected PreparedStatement getOIDbyPIDStatement;

    /**
     * {@code PreparedStatement} to select {@code NamedObject}'s
     * persistent ID of specified object ID
     */
    protected PreparedStatement getPIDbyOIDStatement;

    /**
     * {@code PreparedStatement} to select {@code NamedObject}'s
     * object type of specified object ID
     */
    protected PreparedStatement getTypeByOIDStatement;

    /**
     * {@code PreparedStatement} to select {@code NamedObject}'s
     * object type of specified persistent ID
     */
    protected PreparedStatement getTypeByPIDStatement;

    /**
     * {@code PreparedStatement} to select a {@code Locale}
     * of specified locale name
     */
    protected PreparedStatement getLocaleStatement;

    /**
     * {@code CallableStatement} to call stored procedure
     * getting the next unused object ID, or null if the database
     * does not support stored procedure
     */
    protected CallableStatement getNextObjectIDProc;

    /**
     * Sequence of {@code PreparedStatement} to get
     * the next unused object ID, or null if the database
     * supports stored procedure
     */
    protected PreparedStatement[] getNextObjectIDPrep;

    /** {@code PreparedStatement} to start a transaction */
    protected PreparedStatement startTransaction;

    /** {@code PreparedStatement} to commit a transaction */
    protected PreparedStatement commitTransaction;

    /**
     * {@code CallableStatement} to call a stored procedure
     * to insert a NamedObject
     */
    protected CallableStatement insertNamedObjectProcedure;

    /** {@code PreparedStatement} to insert a NamedObject */
    protected PreparedStatement insertNamedObjectStatement;

    /** {@code PreparedStatement}s to set values of a NamedObject */
    protected Map<String, PreparedStatement> insertStatements;

    /** {@code PreparedStatement}s to set values of a NamedObject */
    protected Map<String, PreparedStatement> setStatements;

    /** {@code PreparedStatement} to delete a pooled OID */
    protected PreparedStatement deleteOIDinPool;

    protected void setConnection(Connection connection)
    {
	/*
	if(this.connection != null)
	    this.connection.close();
	*/

	this.connection = connection;
    }

    protected void prepareDriver(String url,
			    Locale queryType,
			    String driverName)
	throws ClassNotFoundException
    {
	if(queryType == null)
	    queryType = ResourceKey.SQL;
	setQueryType(queryType);

	if(driverName == null) {
	    driverName = 
		ResourceKey.getResourceString(ResourceKey.QUERY,
					      queryType,
					      ResourceKey.JDBC_DRIVER);
	}

	prepareDriver(url, driverName);
    }

    protected void prepareDriver(String url,
				 String driverName)
	throws ClassNotFoundException
    {
	try {
	    DriverManager.getDriver(url);
	}
	catch (SQLException e) {
	    //Does cheking SQLState here help?
	    try {
		Class.forName(driverName).getDeclaredConstructor().newInstance();
	    }
	    catch (NoSuchMethodException nse) {
	    }
	    catch (InvocationTargetException ite) {
	    }
	    catch (InstantiationException ie) {
	    }
	    catch (IllegalAccessException iae) {
	    }

	}
    }

    public void setQueryType(Locale queryType)
    {
	this.queryType = queryType;
    }

    public Locale getQueryType()
    {
	return queryType;
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     */
    public NamedObjectConnection(String url)
	throws ClassNotFoundException,
	       SQLException
    {
	this(url, ResourceKey.SQL);
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param queryType {@code Locale} specifying SQL subtype
     */
    public NamedObjectConnection(String url,
				 Locale queryType)
	throws ClassNotFoundException,
	       SQLException
    {
	this(url, queryType, null);
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param queryType {@code Locale} specifying SQL subtype
     * @param driverName {@code Stirng} representing JDBC driver's name,
     * or null to determine it using property value
     */
    public NamedObjectConnection(String url,
				 Locale queryType,
				 String driverName)
	throws ClassNotFoundException,
	       SQLException
    {
	prepareDriver(url, queryType, driverName);
	setConnection(DriverManager.getConnection(url));
    }
				 
    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     * based on default property file and JDBC driver specified in the file
     *
     * @param url {@code String} representing data source in URL
     * @param user {@code String} representing user name to connect to the
     * database.  It may be zero-length {@code String} but never null
     * @param password {@code String} representing password to connect to the
     * database as {@code user}  It may be zero-length {@code String}
     * but never null
     * @param queryType {@code Locale} specifying SQL subtype
     */
    public NamedObjectConnection(String url,
				 String user,
				 String password,
				 Locale queryType)
	throws ClassNotFoundException,
	       IllegalAccessException,
	       InstantiationException,
	       SQLException
    {
	this(url, user, password, queryType, null);
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param user {@code String} representing user name to connect to the
     * database.  It may be zero-length {@code String} but never null
     * @param password {@code String} representing password to connect to the
     * database as {@code user}  It may be zero-length {@code String}
     * but never null
     * @param queryType {@code Locale} specifying SQL subtype
     * @param driverName {@code Stirng} representing JDBC driver's name,
     * or null to determine it using property value
     */
    public NamedObjectConnection(String url,
				 String user,
				 String password,
				 Locale queryType,
				 String driverName)
	throws ClassNotFoundException,
	       IllegalAccessException,
	       InstantiationException,
	       SQLException
    {
	prepareDriver(url, queryType, driverName);
	setConnection(DriverManager.getConnection(url, user, password));
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param info a list of pairs of any {@code String} tag and value 
     * as connection parameter.  Propaties "user" and "password" are expected
     * at least ordinarily.
     */
    public NamedObjectConnection(String url,
				 Properties info)
	throws ClassNotFoundException,
	       IllegalAccessException,
	       InstantiationException,
	       SQLException
    {
	this(url, info, null, null);
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param info a list of pairs of any {@code String} tag and value 
     * as connection parameter.  Propaties "user" and "password" are expected
     * at least ordinarily.
     * @param queryType {@code Locale} specifying SQL subtype
     */
    public NamedObjectConnection(String url,
				 Properties info,
				 Locale queryType)
	throws ClassNotFoundException,
	       IllegalAccessException,
	       InstantiationException,
	       SQLException
    {
	this(url, info, queryType, null);
    }

    /**
     * Creates a {@code NamedObjectConnection} with specified parameters
     *
     * @param url {@code String} representing data source in URL
     * @param info a list of pairs of any {@code String} tag and value 
     * as connection parameter.  Propaties "user" and "password" are expected
     * at least ordinarily.
     * @param queryType {@code Locale} specifying SQL subtype
     * @param driverName {@code Stirng} representing JDBC driver's name,
     * or null to determine it using property value
     */
    public NamedObjectConnection(String url,
				 Properties info,
				 Locale queryType,
				 String driverName)
	throws ClassNotFoundException,
	       IllegalAccessException,
	       InstantiationException,
	       SQLException
    {
	prepareDriver(url, queryType, driverName);
	setConnection(DriverManager.getConnection(url, info));
    }

   /**
     * Returns {@code NamedObject} of {@code objectType}
     * in the database having {@code objectID},
     * or null if there is no data
     * having the {@code objectID} in the database
     *
     * @param objectID object ID of the target {@code NamedObject}
     * @param objectType {@code String} representing subtype of
     * the target {@code NamedObject} 
     *
     * @return NamedObjct in the database having given {@code objectID},
     * or null if no data in the databae has the {@code objectID}
     *
     * @exception SQLException
     */
    public NamedObject<?> getNamedObject(int objectID,
				      String objectType,
				      int mode)
	throws SQLException
    {
	if(oidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);
	    if(map != null) {
		NamedObject<?> o = map.get(Integer.toString(objectID));
		if(o != null)
		    return o;
	    }
	}

	if(selectByOID != null)
	    selectByOID = Collections.synchronizedMap(new HashMap<String, PreparedStatement>());
	PreparedStatement query = 
	    getPreparedStatementForNamedObject(selectByOID,
					       ResourceKey.SELECT_NAMED_OBJECT_BY_OID,
					       objectType);
	query.setInt(1, objectID);

	NamedObject<?> object = null;
	ResultSet result = query.executeQuery();
	if(result.next()) {
	    object = createNamedObject(result, mode);
	}

	result.close();

	return object;
    }

    /**
     * Returns next object ID
     *
     * @return object ID not yet used
     */
    public int getNextObjectID()
	throws SQLException
    {
	if(getNextObjectIDProc != null) {
	    getNextObjectIDProc.execute();
	    return getNextObjectIDProc.getInt(1);
	}

	return getNextObjectID(ResourceKey.getResourceString(resourceBaseName,
							     queryType, 
							     ResourceKey.GET_NEXT_OBJECT_ID_PROCEDURE));
    }

    /**
     * Returns {@code CallableStatemet} named {@code procedure}
     * if it is supported by the database, or null unspoorted.
     * The {@code CallableStatemet} returns a value if
     * {@code withReturnValue} is true.
     *
     * @param procedure {@code String} retaining name of the stored procedure
     * @param withReturnValue true if the {@code procedure} returns a value
     *
     * @exception SQLException
     */
    protected CallableStatement getCallableStatement(String procedure, 
						     boolean withReturnValue)
	throws SQLException
    {
	DatabaseMetaData metaData = connection.getMetaData();
	ResultSet r = metaData.getProcedures(null, null, procedure);
	r.next();
	if(r == null || r.wasNull()) {
	    r.close();
	    return null;
	}
	
	r.close();
	StringBuffer statement = new StringBuffer("{ ");
	if(withReturnValue)
	    statement.append("? = ");
	return connection.prepareCall(statement.append("call ").append(procedure).append(" }").toString());
    }


    /**
     * Returns next object ID using specified <CDOE>procedure}
     * if available
     *
     * @param procedure Stored procedure call statement in {@code String}
     *
     * @return object ID not yet used
     */
    public int getNextObjectID(String procedure)
	throws SQLException
    {
	ResultSet r = null;

	if(getNextObjectIDProc == null &&
	   getNextObjectIDPrep == null) {
	    if(procedure != null &&
	       procedure.length() > 0) {
		getNextObjectIDProc =
		    getCallableStatement(procedure, true);
	    }
	    if(getNextObjectIDProc != null) {
		getNextObjectIDProc.registerOutParameter(1, Types.INTEGER);
	    }
	    else {
		ResourceBundle resource =
		    ResourceBundle.getBundle(resourceBaseName, queryType);
		String oidPoolTable = resource.getString(ResourceKey.TABLE_OID_POOL);
		String namedObjectTable = resource.getString(ResourceKey.TABLE_NAMED_OBJECT);

		getNextObjectIDPrep = new PreparedStatement[8];
		for(int i = 0; i < 4; i++) {
		    getNextObjectIDPrep[i] = 
			connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.GET_NEXT_OBJECT_ID_STATEMENT_KEYS[i]),
									 new Object[]{oidPoolTable}));
		}
		for(int i = 4; i < 8; i++) {
		    getNextObjectIDPrep[i] = 
			connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.GET_NEXT_OBJECT_ID_STATEMENT_KEYS[i]),
									 new Object[]{namedObjectTable}));
		}
	    }
	}

	if(getNextObjectIDProc == null &&
	   getNextObjectIDPrep == null) 
	    return 0;

	if(getNextObjectIDProc != null) {
	    /*
	    r = getNextObjectIDProc.executeQuery();
	    //r.next();
	    return r.getInt(1);
	    */
	    getNextObjectIDProc.execute();
	    return getNextObjectIDProc.getInt(1);
	}

	int id = 0;
	int count = 0;

	// SELECT INTO result COUNT(object_id) FROM object_id_pool;
	r = getNextObjectIDPrep[0].executeQuery();
	r.next();

	count = r.getInt(1);
	r.close();

	if(count > 0) {	// IF result.count > 0 THEN
	    // SELECT INTO result MIN(object_id) FROM object_id_pool;
	    // i := result.min;
	    r = getNextObjectIDPrep[1].executeQuery();
	    r.next();
	    id = r.getInt(1);
	    // SELECT INTO result COUNT(object_id) FROM object_id_pool WHERE object_id > 0;
	    r.close();
	    r = getNextObjectIDPrep[2].executeQuery();
	    r.next();
	    count = r.getInt(1);
	    r.close();
	    
	    if(count > 0) { // IF result.count > 0 THEN
		// SELECT INTO result MIN(object_id) FROM object_id_pool WHERE object_id > 0;
		// i := result.min;
		r = getNextObjectIDPrep[3].executeQuery();
		r.next();
		id = r.getInt(1);

		
	    } // END IF;
	}
	else { // ELSE
	    // SELECT INTO result COUNT(object_id) FROM named_object;
	    r = getNextObjectIDPrep[4].executeQuery();
	    r.next();
	    count = r.getInt(1);
	    r.close();

	    if(count > 0) { // IF result.count > 0 THEN
		// SELECT INTO result MAX(object_id) FROM named_object;
		// i := result.max;
		r = getNextObjectIDPrep[5].executeQuery();
		r.next();
		id = r.getInt(1);
		r.close();

		if(id == Integer.MAX_VALUE) { // IF i = max_int THEN
		    // SELECT INTO result COUNT(object_id) FROM named_object WHERE object_id < 0;
		    r = getNextObjectIDPrep[6].executeQuery();
		    r.next();
		    count = r.getInt(1);
		    r.close();

		    if(count > 0) { // IF result.count > 0 THEN
			// SELECT INTO result MAX(object_id) FROM named_object WHERE object_id < 0;
			// i := result.max;
			r = getNextObjectIDPrep[7].executeQuery();
			r.next();
			id = r.getInt(1);
			r.close();
		    } // END IF;
		} // END IF;
	    } // END IF;				
	    
	    if(++id == 0) { // i := i + 1; IF i = 0 THEN
		id = 1; // i := 1;
	    } //END IF;
	} // END IF;	

	return id;
    }

    /**
     * Returns object ID of {@code object} in the 
     * detabase through the {@code connection},
     * or zero if the {@code object} does not stored
     * in the database.
     *
     * @param objectID object ID of target {@code NamedObject}
     *
     * @return int representing the object ID of the {@code object}
     * in the database behind this {@code Connection}, or zero
     * if the {@code object} is not in the database
     */
    public String getPersistentID(int objectID)
	throws SQLException
    {
	if(getPIDbyOIDStatement == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    getPIDbyOIDStatement =
		connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.SELECT_PID_BY_OID),
								 new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT)}));
	}

	getPIDbyOIDStatement.setInt(1, objectID);
	ResultSet results = 
	    getPIDbyOIDStatement.executeQuery();
	results.next();

	String str = results.getString(1);
	results.close();

	return str;
    }

    /**
     * Returns object ID of {@code object} in the 
     * detabase through the {@code connection},
     * or zero if the {@code object} does not stored
     * in the database.
     *
     * @param object {@code NamedObject} to know of which
     * object ID
     *
     * @return int representing the object ID of the {@code object}
     * in the database behind this {@code Connection}, or zero
     * if the {@code object} is not in the database
     */
    public int getObjectID(NamedObject<?> object)
	throws SQLException
    {
	return getObjectID(object, false);
    }

    /**
     * Returns object ID of {@code object} in the 
     * detabase through the {@code connection},
     * or zero if the {@code object} does not stored
     * in the database.
     *
     * @param object {@code NamedObject} to know of which
     * object ID
     *
     * @return int representing the object ID of the {@code object}
     * in the database behind this {@code Connection}, or zero
     * if the {@code object} is not in the database
     */
    public int getObjectID(NamedObject<?> object, boolean withInsertion)
	throws SQLException
    {
	
	if(objectToOIDMaps != null) {
	    Map<NamedObject<?>, Integer> h = objectToOIDMaps.get(dataSource);
	    
	    if(h != null) {
		Integer objectID = h.get(object);

		if(objectID != null)
		    return objectID.intValue();
	    }
	}

	if(getOIDbyPIDStatement == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    getOIDbyPIDStatement =
		connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.SELECT_OID_BY_PID),
								 new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT)}));
	}

	getOIDbyPIDStatement.setString(1, object.getLiteral());
	ResultSet results = 
	    getOIDbyPIDStatement.executeQuery();
	results.next();

	int i = 0;
	if(!results.wasNull()) {
	    i = results.getInt(1);
	    results.close();
	    return i;
	}

	results.close();

	if(withInsertion)
	    return insert(object);

	return 0;
    }

    /**
     * Sets {@code objectID} to {@code object}
     *
     * @param objectID {@code int} representing object_id of
     * the {@code object} in the database behind this
     * {@code Connection}
     * @param object {@code NamedObject} to which the
     * {@code objectID} to be assigned
     */
    public void setObjectID(int objectID, NamedObject<?> object)
    {
	if(objectToOIDMaps == null) {
	    objectToOIDMaps = Collections.synchronizedMap(new HashMap<String, Map<NamedObject<?>, Integer>>());
	    oidToObjectMaps =  Collections.synchronizedMap(new HashMap<String, Map<String, NamedObject<?>>>());
	}

	Map<NamedObject<?>, Integer> o2oidMap = objectToOIDMaps.get(dataSource);

	if(o2oidMap == null) {
	    o2oidMap = Collections.synchronizedMap(new HashMap<NamedObject<?>, Integer>());
	    objectToOIDMaps.put(dataSource, o2oidMap);
	}

	Integer id = Integer.valueOf(objectID);

	o2oidMap.put(object, id);

	Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);

	if(map == null) {
	    map = Collections.synchronizedMap(new HashMap<String, NamedObject<?>>());
	    oidToObjectMaps.put(dataSource, map);
	}

	map.put(id.toString(), object);
    }

    /**
     * Returns {@code String} representing type of
     * a {@code NamedObject} in the database having
     * {@code objectID}, or null if it does not 
     * exist in the database
     *
     * @param objectID object ID of the target {@code NamedObject}
     *
     * @return String representing the type of the 
     * target {@code NamedObject}, or null if
     * there is no data having the {@code objectID}
     * in the database
     *
     * @exception SQLException
     */
    public String getObjectType(int objectID)
	throws SQLException
    {
	if(getTypeByOIDStatement == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    getTypeByOIDStatement =
		connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.SELECT_TYPE_BY_OID),
								 new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT)}));
	}
	
	getTypeByOIDStatement.setInt(1, objectID);
	ResultSet results = 
	    getTypeByOIDStatement.executeQuery();
	results.next();

	if(results.wasNull()) {
	    results.close();
	    return null;
	}

	String str = results.getString(1);
	results.close();
	return str;
    }

    /**
     * Returns {@code String} representing type of
     * a {@code NamedObject} in the database having
     * {@code persistentID}, or null if it does not 
     * exist in the database
     *
     * @param persistentID persistent ID of the target {@code NamedObject}
     *
     * @return String representing the type of the 
     * target {@code NamedObject}, or null if
     * there is no data having the {@code persistentID}
     * in the database
     *
     * @exception SQLException
     */
    public String getObjectType(String persistentID)
	throws SQLException
    {
	if(getTypeByPIDStatement == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    getTypeByPIDStatement =
		connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.SELECT_TYPE_BY_PID),
								 new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT)}));
	}
	
	getTypeByPIDStatement.setString(1, persistentID);
	ResultSet results = 
	    getTypeByPIDStatement.executeQuery();
	results.next();

	if(results.wasNull()) {
	    results.close();
	    return null;
	}

	String str = results.getString(1);
	results.close();
	return str;
    }

    /**
     * Returns {@code NamedObject} in the database having
     * {@code objectID}, or null if there is no data
     * having the {@code objectID} in the database
     *
     * @param objectID object ID of the target {@code NamedObject}
     *
     * @return NamedObjct in the database having given {@code objectID},
     * or null if no data in the databae has the {@code objectID}
     *
     * @exception SQLException
     */
    public NamedObject<?> getNamedObject(int objectID,
				      int mode)
	throws SQLException
    {
	String objectType =
	    getObjectType(objectID);
	if(objectType == null)
	    return null;
	return getNamedObject(objectID, objectType, mode);
    }



    /**
     * Returns {@code NamedObject} in the database having
     * {@code persistnetID}, or null if there is no data
     * having the {@code persistentID} in the database
     *
     * @param persistentID persistent ID of the target {@code NamedObject}
     *
     * @return NamedObjct in the database having given {@code persistentID},
     * or null if no data in the databae has the {@code persistentID}
     *
     * @exception SQLException
     */
    public NamedObject<?> getNamedObject(String persistentID,
				      int mode)
	throws SQLException
    {
	String objectType =
	    getObjectType(persistentID);
	if(objectType == null)
	    return null;
	return getNamedObject(persistentID, objectType, mode);
    }

    /**
     * Returns {@code NamedObject} in the database having
     * {@code persistnetID}, or null if there is no data
     * having the {@code persistentID} in the database
     *
     * @param persistentID persistent ID of the target {@code NamedObject}
     * @param objectType {@code String} representing subtype of
     * the target {@code NamedObject} 
     *
     * @return NamedObjct in the database having given {@code persistentID},
     * or null if no data in the databae has the {@code persistentID}
     *
     * @exception SQLException
     */
    public NamedObject<?> getNamedObject(String persistentID,
				      String objectType,
				      int mode)
	throws SQLException
    {
	if(pidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = pidToObjectMaps.get(dataSource);
	    if(map != null) {
		NamedObject<?> o = map.get(persistentID);
		if(o != null)
		    return o;
	    }
	}

	if(selectByPID != null)
	    selectByPID = new HashMap<String, PreparedStatement>();
	PreparedStatement query = 
	    getPreparedStatementForNamedObject(selectByPID,
					       ResourceKey.SELECT_NAMED_OBJECT_BY_PID,
					       objectType);
	query.setString(1, persistentID);

	ResultSet result = query.executeQuery();
	NamedObject<?> object = null;
	if(result.next()) {
	    object = createNamedObject(result, mode);
	}
	result.close();
	return null;
    }

    /**
     * Returns {@code NamedObject} in the database having persistent ID
     * like {@code pattern}
     *
     * @param pattern like persistent ID of the target {@code NamedObject}
     * @param objectType {@code String} representing subtype of
     * the target {@code NamedObject} 
     *
     * @return ResultSet containing NamedObjcts in the database
     * having persistent ID similer to {@code pattern}
     *
     * @exception SQLException
     */
    public ResultSet getNamedObjectLike(String pattern, String objectType)
	throws SQLException
    {
	if(selectLikePID != null)
	    selectLikePID = new HashMap<String, PreparedStatement>();
	PreparedStatement query = 
	    getPreparedStatementForNamedObject(selectLikePID,
					       ResourceKey.SELECT_NAMED_OBJECT_LIKE_PID,
					       objectType);
	query.setString(1, pattern);

	return query.executeQuery();
    }

    /**
     * Creates a {@code NamedObject} based on {@code result},
     * or null if {@code result} is NULL.
     * It uses only the cursor currently pointed.
     *
     * @param result {@code ResultSet} containing data to crate
     * a {@code NamedObject}
     *
     * @return NamedObject based on {@code result},
     * or null if {@code result} is NULL.
     *
     * @exception SQLException
     */
    protected NamedObject<?> createNamedObject(ResultSet result,
					    int mode)
	throws SQLException
    {
	if(result.wasNull())
	    return null;

	String objectType =
	    result.getString("object_type");
	if(objectType == null ||
	   objectType.length() == 0)
	    return null;

	if(objectType.equals(ResourceKey.TABLE_AUTHOR)) {
	    return createAgent(result, mode);
	}
	else if(objectType.equals(ResourceKey.TABLE_PUBLICATION)) {
	    return createPublication(result, mode);
	}
	else if(objectType.equals(ResourceKey.TABLE_APPEARANCE)) {
	    return createAppearance(result, mode);
	}
	else if(objectType.equals(ResourceKey.TABLE_NAMEUSAGE)) {
	    return createNameUsage(result, mode);
	}
	else if(objectType.equals(ResourceKey.TABLE_ANNOTATION)) {
	    return createAnnotation(result, mode);
	}
	else if(objectType.equals(ResourceKey.TABLE_NAMEUSAGENODE)) {
	    return createDefaultNameUsageNode(result, mode);
	}

	return null;
    }

    /**
     * Sets values in {@code result} to {@code object}
     *
     * @param object {@code NamedObject} to which values
     * to be set
     * @param result {@code ResultSet} containing values to be
     * set to {@code object}
     *
     * @exception SQLException
     */
    protected void setValues(NamedObject<?> object, ResultSet result)
	throws SQLException
    {
	setObjectID(result.getInt("object_id"), object);

	//String str = null;
	//str = result.getString("persistent_id");
	//str = result.getString("object_type");
	object.setContributor(result.getString("contirubor"));
	object.setEditable(1 == result.getInt("editable"));
	object.setCopyright(result.getString("copyright"));
	object.setNotes(("notes"));
	object.setLastUpdated(result.getTimestamp("lastUpdated"));
    }

    /**
     * Creates an {@code Agent} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code Agent}
     *
     * @return Agent based on {@code result}
     *
     * @exception SQLException
     */
    protected Agent createAgent(ResultSet result,
				  int mode)
	throws SQLException
    {
	Agent agent = new Agent();
	setValues(agent, result);
	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(result.getDate("birth"));
	agent.setBeginningDate(cal);
	cal = new GregorianCalendar();
	cal.setTime(result.getDate("death"));
	agent.setEndingDate(cal);
	if(agent instanceof Person) {
	    Person author = (Person)agent;
	    author.setSurnamePrefix(result.getString("surname_prefix"));
	    author.setSurname(result.getString("surname"));
	    author.setEpithet(result.getString("surname_epithet"));
	    author.setFirstName(result.getString("first_name"));
	    author.setMiddleNames(result.getString("middle_names"));
	    author.setTitle(result.getString("title"));
	}
	return agent;
    }

    /**
     * Creates an {@code Publication} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code Publication}
     *
     * @return Publication based on {@code result}
     *
     * @exception SQLException
     */
    protected Publication createPublication(ResultSet result,
					    int mode)
	throws SQLException
    {
	Publication publication = new Publication();
	setValues(publication, result);
	publication.setPublicationType(result.getInt("publication_type"));
	publication.setAuthorNames(result.getString("authors"));
	publication.setAffiliations(result.getString("affiliations"));
	publication.setDOI(result.getString("doi"));
	publication.setISXN(result.getString("isxn"));
	publication.setCitationTitle(result.getString("citationTitle"));
	publication.setContentsTitle(result.getString("contentsTitle"));
	publication.setYear(result.getString("year"));
	publication.setVolume(result.getString("volume"));
	publication.setIssue(result.getString("issue"));
	publication.setFirstPage(result.getString("first_page"));
	publication.setLastPage(result.getString("last_page"));
	publication.setPublisher(result.getString("publisher"));
	publication.setPlace(result.getString("place"));
	publication.setReceived(result.getString("received"));
	publication.setRevised(result.getString("revised"));
	publication.setAccepted(result.getString("accepted"));
	publication.setPublished(result.getString("published"));

	int containerID = result.getInt("part_of");
	if(result.wasNull() || containerID == 0)
	    return publication;

	Publication container = null;

	if(oidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);
	    if(map != null) {
		container = 
		    (Publication)map.get(Integer.toString(containerID));
		if(container != null) {
		    publication.setContainer(container);
		    return publication;
		}
	    }
	}

	if((mode & NOMINAL) != 0) {
	    String pid = getPersistentID(containerID);
	    if(pid != null && pid.length() > 0) {
		container = new Publication();
		container.setLiteral(pid);
	    }
	}
	else {
	    container = 
		(Publication)getNamedObject(containerID, publication.getClassName(), mode);
	}
	publication.setContainer(container);

	return publication;
    }

    /**
     * Creates an {@code Appearance} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code Appearance}
     *
     * @return Appearance based on {@code result}
     *
     * @exception SQLException
     */
    protected Appearance createAppearance(ResultSet result,
					  int mode)
	throws SQLException
    {
	Appearance appearance = new Appearance();
	setValues(appearance, result);
	appearance.setPages(result.getString("page"));
	appearance.setLines(result.getString("lines"));
	appearance.setAppearance(result.getString("appearance"));
	int publicationID = result.getInt("publication");

	Publication publication = null;

	if(oidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);
	    if(map != null) {
		publication = (Publication)map.get(Integer.toString(publicationID));
	    }
	}

	if (publication == null) {
	    if((mode & NOMINAL) != 0) {
		String pid = getPersistentID(publicationID);
		if(pid != null && pid.length() > 0) {
		    publication = new Publication();
		    publication.setLiteral(pid);
		}
	    }
	    else {
		publication = 
		    (Publication)getNamedObject(publicationID, mode);
	    }
	}

	appearance.setPublication(publication);
	return appearance;
    }

    protected Appearance getAppearance(int objectID, int mode)
	throws SQLException
    {
	Appearance appearance = null;

	if(oidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);
	    if(map != null) {
		appearance = 
		    (Appearance)map.get(Integer.toString(objectID));
	    }
	}

	if (appearance == null) {
	    if((mode & NOMINAL) != 0) {
		String pid = getPersistentID(objectID);
		if(pid != null && pid.length() > 0) {
		    appearance = new Appearance();
		    appearance.setLiteral(pid);
		}
	    }
	    else {
		appearance = 
		    (Appearance)getNamedObject(objectID, mode);
	    }
	}
	return appearance;
    }



    /**
     * Creates an {@code Annotation} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code Annotation}
     *
     * @return Annotation based on {@code result}
     *
     * @exception SQLException
     */
    protected Annotation createAnnotation(ResultSet result,
					  int mode)
	throws SQLException
    {
	Annotation annotation = new Annotation();
	setValues(annotation, result);
	annotation.setLinkType(result.getString("link_type"));
	int appearanceID = result.getInt("appearance");
	if(appearanceID != 0) {
	    annotation.setAppearance(getAppearance(appearanceID, mode));
	}
	/*
	explicit INTEGER, -- true if this annotation was stated in a publication explicitly
	CHECK (explicit IN (0, 1)),
	path INTEGER -- ID of the synthetic path, i.e. path not retained by Annotation between referer and referant NameUsages
	*/
	return annotation;
    }

    /**
     * Returns {@code Objec} of {@code objectID}
     * in cache, or null if it is not found in the cache. 
     */
    protected Object getCachedObject(int objectID)
	throws SQLException
    {
	Object object = null;

	if(oidToObjectMaps != null) {
	    Map<String, NamedObject<?>> map = oidToObjectMaps.get(dataSource);
	    if(map != null) {
		object = map.get(Integer.toString(objectID));
	    }
	}

	return object;
    }

    /**
     * Returns {@code NameUsage} of {@code objectID},
     * or null it is not found. 
     */
    protected NameUsage<?> getNameUsage(int objectID, int mode)
	throws SQLException
    {
	NameUsage<?> nameUsage = null;

	Object object = getCachedObject(objectID);
	if(object instanceof NameUsage)
	    nameUsage = (NameUsage<?>)object;

	if (nameUsage == null) {
	    if((mode & NOMINAL) != 0) {
		String pid = getPersistentID(objectID);
		if(pid != null && pid.length() > 0) {
		    nameUsage = (NameUsage<?>)new DefaultNameUsage();
		    nameUsage.setLiteral(pid);
		}
	    }
	    else {
		object = getNamedObject(objectID, mode);
		if(object instanceof NameUsage<?>)
		    nameUsage = (NameUsage<?>)object;
	    }
	}

	return nameUsage;
    }

    /**
     * Returns {@code Rank} in {@code ranks}
     * indexed by {@code rankID}
     */
    protected Rank getRank(Map<String, Rank> rankMap, String rankID)
    {
	Rank rank = rankMap.get(rankID);
	if(rank == null) {
	    rank = new Rank();
	    rankMap.put(rankID, rank);
	    //	    rankMap.put(rank.getName(), rankID);
	    rankMap.put(rank.getName(), rank);
	}
	return rank;
    }

    /**
     * Returns {@code Rank} of {@code rankID},
     * or null it is not found. 
     */
    protected Rank getRank(int rankID)
	throws SQLException
    {
	return (Rank)getRank(Integer.toString(rankID));
    }

    /**
     * Returns {@code Rank} of {@code rankID},
     * or null it is not found. 
     */
    protected Object getRank(String key)
	throws SQLException
    {
	if(ranks == null) {
	    if(rankMaps == null)
		rankMaps = Collections.synchronizedMap(new HashMap<String, Map<String, Rank>>());

	    ranks = rankMaps.get(dataSource);
	    if(ranks == null) {
		ranks = prepareRankMap();
		rankMaps.put(dataSource, ranks);
	    }
	}

	return ranks.get(key);
    }
	 
    protected Map<String, Rank> prepareRankMap()
	throws SQLException
    {
	ResourceBundle resource =
	    ResourceBundle.getBundle(resourceBaseName, queryType);
	Statement query = connection.createStatement();
	ResultSet result = 
	    query.executeQuery(MessageFormat.format(resource.getString(ResourceKey.SELECT_TABLE),
						    new Object[]{resource.getString(ResourceKey.TABLE_RANK)}));
	Map<String, Rank> rankMap = Collections.synchronizedMap(new HashMap<String, Rank>());
	while(result.next()) {
	    String id = Integer.toString(result.getInt("ID")); 
	    Rank rank = getRank(rankMap, id);
	    rank.setName(result.getString("name"));
	    id = Integer.toString(result.getInt("higher"));
	    if(!result.wasNull()) {
		rank.setHigher(getRank(rankMap, id));
	    }
	    id = Integer.toString(result.getInt("lower"));
	    if(!result.wasNull()) {
		rank.setLower(getRank(rankMap, id));
	    }
	    id = Integer.toString(result.getInt("equivalent"));
	    if(!result.wasNull()) {
		rank.setEquivalent(getRank(rankMap, id));
	    }
	    int i = result.getInt("optional");
	    if(!result.wasNull())
		rank.setOptional(i == 1);
	    i = result.getInt("code_compliance");
	    if(!result.wasNull()) {
		rank.setCodeCompliance(i);
	    }
	    i = result.getInt("naming_convention");
	    if(!result.wasNull()) {
		rank.setConvention(i);
	    }
	}
	result.close();

	return rankMap;
    }

    protected Locale getLocale(String localeName)
    {
	if(localeMaps == null)
	    localeMaps = Collections.synchronizedMap(new HashMap<String, Map<String, Locale>>());
	Map<String, Locale> locales = localeMaps.get(dataSource);
	if(locales == null) {
	    locales = Collections.synchronizedMap(new HashMap<String, Locale>());
	    localeMaps.put(dataSource, locales);
	    Locale[] availableLocales = Locale.getAvailableLocales();
	    for(int i = 0; i < availableLocales.length; i++) {
		locales.put(availableLocales[i].toString(), availableLocales[i]);
	    }
	}
	Locale locale = locales.get(localeName);
	if(locale ==  null) {
	    int langEnd = localeName.indexOf("_");
	    int regionEnd = localeName.indexOf(".");
	    String lang = localeName.substring(0, langEnd);
	    String region = localeName.substring(1 + langEnd, regionEnd);
	    String variant = localeName.substring(1 + regionEnd);
	    if(lang == null)
		lang = "";
	    if(region == null)
		region = "";
	    if(variant == null)
		variant = "";
	    locale = new Locale(lang, region, variant);
	}

	return locale;
    }

    /**
     * Creates an {@code DefaultNameUsage} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code DefaultNameUsage}
     *
     * @return DefaultNameUsage based on {@code result}
     *
     * @exception SQLException
     */
    protected NameUsage<?> createNameUsage(ResultSet result, int mode)
	throws SQLException
    {
	int i = result.getInt("object_id");
	NameUsage<?> nameUsage = null;
	Object object = getCachedObject(i);
	if(object instanceof NameUsage)
	    nameUsage = (NameUsage<?>)object;
	if(nameUsage == null) {
	    nameUsage = new DefaultNameUsage();
	}

	setValues(nameUsage, result);
	String localeName = result.getString("locale");
	if(localeName != null && localeName.length() > 0)
	    nameUsage.setLocale(getLocale(localeName));
	//nameUsage.setLocale(localeName);

	i = result.getInt("rank");
	if(!result.wasNull())
	    nameUsage.setRankLiteral(getRank(i).getName());
	//nameUsage.setRank(getRank(i));

	nameUsage.setLiteral(result.getString("literal"));

	i = result.getInt("appearance");
	if(!result.wasNull() && i != 0) {
	    nameUsage.setAppearance(getAppearance(i, mode));
	}
	
	i = result.getInt("sensu");
	if(!result.wasNull() && i != 0) {
	    nameUsage.setSensu(getNameUsage(i, mode));
	}

	i = result.getInt("higher_taxon");
	if(!result.wasNull() && i != 0) {
	    nameUsage.setSensu(getNameUsage(i, mode));
	    i = result.getInt("sequence");
	    if(!result.wasNull())
		nameUsage.setIndex(i);
	}

	i = result.getInt("type");
	if(i != 0) {
	    nameUsage.setType(true);
	    nameUsage.setTypeStatus(result.getString("type_of_type"));
	}
	nameUsage.setIncertaeSedis(1 == result.getInt("insertae_sedis"));

	return nameUsage;
    }

    /**
     * Creates an {@code DefaultNameUsageNode} object using contens of
     * {@code result}
     *
     * @param result {@code ResultSet} containing data to crate
     * an {@code DefaultNameUsageNode}
     *
     * @return DefaultNameUsageNode based on {@code result}
     *
     * @exception SQLException
     */
    protected DefaultNameUsageNode createDefaultNameUsageNode(ResultSet result,
							      int mode)
	throws SQLException
    {
	DefaultNameUsageNode nameUsageNode = new DefaultNameUsageNode();
	setValues(nameUsageNode, result);
	/*
  	nameusage INTEGER -- reference to the NameUsage relevent to the NameUsage_node as an object ID
	CONSTRAINT nameusage_node_nameusage REFERENCES nameusage(object_id),
	relevant INTEGER -- reference to the NameUsage referring to, referred to by the NameUsage as an object ID
	CONSTRAINT nameusage_node_referer REFERENCES nameusage(object_id),
	is_referred INTEGER, -- true if the NameUsage is referred to by the relevant
	CHECK (is_referred IN (0, 1)),
	PRIMARY KEY (nameusage, relevant, is_referred)

	*/
	return nameUsageNode;
    }

    /**
     * Returns a {@code PreparedStatement} to select a 
     * {@code NamedObject} having object type specified
     * property of {@code objectTypeKey}, using SQL satatement
     * specified by {@code statementKey}.  If there is
     * a {@code PreparedStatement} having there parameters
     * in {@code statements} it will be returend.
     *
     * @param statements {@code Hashtable} storing
     * {@code PreparedStatement} already provided
     * @param statementKey property key to a SQL statement
     * @param objctTypeKey property key to objct type
     * 
     * @exception SQLException
     */
    protected PreparedStatement 
	getPreparedStatementForNamedObject(Map<String, PreparedStatement> statements,
					   String statementKey,
					   String objectTypeKey)
	throws SQLException
    {
	PreparedStatement query =statements.get(objectTypeKey);
	if(query == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    query = 
		connection.prepareStatement(MessageFormat.format(resource.getString(statementKey),
								 new Object[]{resource.getString(objectTypeKey)}));
	    statements.put(objectTypeKey, query);
	}

	return query;
    }

    /**
     * Returns a {@code PreparedStatement} of
     * SQL specified by properties of 
     * {@code statementKey} and {@code paramterKey}
     * specified by {@code statementKey}.  If there is
     * a {@code PreparedStatement} having there parameters
     * in {@code statements} it will be returend.
     *
     * @param statements {@code Hashtable} storing {@code PreparedStatement} already provided
     * @param statementKey property key to a SQL statement
     * @param objctTypeKey property key to objct type
     * 
     * @exception SQLException
     */
    protected PreparedStatement 
	getPreparedStatement(String statementKey,
			     String parameterKey)
	throws SQLException
    {
	try {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    String sql = resource.getString(statementKey);
	    if(parameterKey == null || 
	       parameterKey.length() == 0)
		return connection.prepareStatement(sql);
	    
	    StringTokenizer tokens = 
		new StringTokenizer(resource.getString(parameterKey));
	    Object[] parameters = new Object[tokens.countTokens()];
	    int i = 0;
	    while(tokens.hasMoreTokens())
		parameters[i++] = tokens.nextElement();
	    
	    return connection.prepareStatement(MessageFormat.format(sql, parameters));
	}
	catch(MissingResourceException e) {
	    return null;
	}
    }


    public int insert(NamedObject<?> object)
	throws SQLException
    {
	if(startTransaction == null) {
	    ResourceBundle resource =
		ResourceBundle.getBundle(resourceBaseName, queryType);
	    startTransaction = 
		connection.prepareStatement(resource.getString(ResourceKey.START_TRANSACTION));
	    commitTransaction = 
		connection.prepareStatement(resource.getString(ResourceKey.COMMIT_TRANSACTION));

	    insertNamedObjectProcedure =
		getCallableStatement(resource.getString(ResourceKey.INSERT_NAMED_OBJECT_PROCEDURE), true);

	    if(insertNamedObjectProcedure != null) {
		insertNamedObjectProcedure.registerOutParameter(1, Types.INTEGER);
	    }
	    else {
		/*
		insertNamedObjectStatement = 
		    connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.INSERT_NAMED_OBJECT),
								     new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT)}));
		*/
		insertStatements = new HashMap<String, PreparedStatement>();

		deleteOIDinPool = 
		    connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.DELETE_OID_IN_POOL),
								     new Object[]{resource.getString(ResourceKey.TABLE_OID_POOL)}));
	    }

	    setStatements = new HashMap<String, PreparedStatement>();
	}

	String subtype = object.getClassName();
	PreparedStatement setNamedObject = setStatements.get(subtype);
	if(setNamedObject == null) {
	    setNamedObject =
		getPreparedStatement("SET_"+subtype, subtype);
	    setStatements.put(subtype, setNamedObject);
	}

	if(insertStatements != null) {
	    insertNamedObjectStatement = insertStatements.get(subtype);
	    if(insertNamedObjectStatement == null) {
		ResourceBundle resource = ResourceBundle.getBundle(resourceBaseName, queryType);
		insertNamedObjectStatement = 
		    connection.prepareStatement(MessageFormat.format(resource.getString(ResourceKey.INSERT_NAMED_OBJECT),
								     new Object[]{resource.getString(ResourceKey.TABLE_NAMED_OBJECT),
										  resource.getString(subtype)}));
		insertStatements.put(subtype, insertNamedObjectStatement);
	    } 
	}


	int oid = 0;
	int index = -1;
	String tableName = 
	    ResourceKey.getResourceString(resourceBaseName, 
					  queryType,
					  subtype);

	if(insertNamedObjectProcedure != null) {
	    setValues(insertNamedObjectProcedure, object, 2);
	}
	else {
	    oid = getNextObjectID();
	    deleteOIDinPool.setInt(1, oid);
	    insertNamedObjectStatement.setInt(1, oid);
	    index = setValues(insertNamedObjectStatement, object, 2);
	    insertNamedObjectStatement.setInt(index++, oid);
	}

	startTransaction.execute();

	if(insertNamedObjectProcedure != null) {
	    insertNamedObjectProcedure.execute();
	    oid = insertNamedObjectProcedure.getInt(1);
	}
	else {
	    insertNamedObjectStatement.execute();
	    deleteOIDinPool.execute();
	}
	try {
	    commitTransaction.execute();

	    setObjectID(oid, object);
	    index = object.setValues(setNamedObject, this, 1);
	    setNamedObject.setInt(index, oid);
	    setNamedObject.execute();
	    return oid;
	}
	catch (SQLException e) {
	    return 0;
	}
    }

    protected void setString(PreparedStatement statement,
			    int index, String value)
	throws SQLException
    {
	if(value != null)
	    statement.setString(index, value);
	else
	    statement.setNull(index, java.sql.Types.NULL);
    }

    protected void setDate(PreparedStatement statement,
			    int index, Date value)
	throws SQLException
    {
	if(value != null)
	    statement.setDate(index, value);
	else
	    statement.setNull(index, java.sql.Types.NULL);
    }

    public int setValues(PreparedStatement statement,
			 NamedObject<?> namedObject,
			 int index)
	throws SQLException
    {
	statement.setString(index++, namedObject.getClassName());
	statement.setString(index++, namedObject.getLiteral());
	setString(statement, index++, namedObject.getContributor());
	if(namedObject.isEditable())
	    statement.setInt(index++, 1);
	else
	    statement.setInt(index++, 0);
	setString(statement, index++, namedObject.getCopyright());
	setString(statement, index++, namedObject.getNotes());

	return index;
    }

    public int setValues(PreparedStatement statement,
			 Agent agent,
			 int index)
	throws SQLException
    {
	if(agent instanceof Person) {
	    Person author = (Person) agent;
	setString(statement, index++, author.getSurnamePrefix());
	setString(statement, index++, author.getSurname());
	setString(statement, index++, author.getEpithet());
	setString(statement, index++, author.getFirstName());
	setString(statement, index++, author.getMiddleNames());
	setString(statement, index++, author.getTitle());
	}
	setDate(statement, index++, new Date(agent.getBeginningDate().getTime().getTime()));
	setDate(statement, index++, new Date(agent.getEndingDate().getTime().getTime()));
	return index;
    }

    public int setValues(PreparedStatement statement,
			 Publication publication,
			 int index)
	throws SQLException
    {
	statement.setInt(index++, publication.getPublicationType());
	setString(statement, index++, publication.getAuthorNames());
	setString(statement, index++, publication.getAffiliations());
	setString(statement, index++, publication.getDOI());
	setString(statement, index++, publication.getISXN());
	setString(statement, index++, publication.getCitationTitle());
	setString(statement, index++, publication.getContentsTitle());
	setString(statement, index++, publication.getYear());
	setString(statement, index++, publication.getVolume());
	setString(statement, index++, publication.getIssue());
	setString(statement, index++, publication.getFirstPage());
	setString(statement, index++, publication.getLastPage());
	setString(statement, index++, publication.getPublisher());
	setString(statement, index++, publication.getPlace());
	setString(statement, index++, publication.getReceived());
	setString(statement, index++, publication.getRevised());
	setString(statement, index++, publication.getAccepted());
	setString(statement, index++, publication.getPublished());
	Publication container = publication.getContainer();
	if(container == null)
	    statement.setInt(index++, 0);
	else
	    statement.setInt(index++, getObjectID(container, true));
	statement.setNull(index++, java.sql.Types.NULL);

	return index;
    }

    public int setValues(PreparedStatement statement,
			 Appearance appearance,
			 int index)
	throws SQLException
    {
	Publication publication = appearance.getPublication();
	if(publication == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setInt(index++, getObjectID(publication, true));
	setString(statement, index++, appearance.getPages());
	setString(statement, index++, appearance.getLines());
	setString(statement, index++, appearance.getAppearance());
	return index;
    }

    public int setValues(PreparedStatement statement,
			 NameUsage<?> nameUsage,
			 int index)
	throws SQLException
    {
	Locale locale = nameUsage.getLocale();
	if(locale == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setString(index++, locale.toLanguageTag());
	    //statement.setString(index++, locale.toString());
	    //statement.setString(index++, locale);
	String rank = nameUsage.getRank().toString(); //FIXME
	if(rank == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setInt(index++, Integer.getInteger((String)getRank(rank)).intValue());

	setString(statement, index++, nameUsage.getLiteral());

	NamedObject<?> object = nameUsage.getAppearance();
	if(object == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setInt(index++, getObjectID(object, true));

	object = nameUsage.getSensu();
	if(object == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setInt(index++, getObjectID(object, true));

	object = nameUsage.getHigherNameUsage();
	if(object == null) {
	    statement.setNull(index++, java.sql.Types.NULL);
	    statement.setNull(index++, java.sql.Types.NULL);
	}
	else {
	    statement.setInt(index++, getObjectID(object, true));
	    int i = nameUsage.getIndex();
	    if(i < 0)
		statement.setNull(index++, java.sql.Types.NULL);
	    else
		statement.setInt(index++, i);
	}
	if(nameUsage.isType()) {
		statement.setInt(index++, 1);
		setString(statement, index++, nameUsage.getTypeStatus().getTypeStatus());
	}
	else{
		statement.setNull(index++, java.sql.Types.NULL);
		statement.setNull(index++, java.sql.Types.NULL);
	}            
	if(nameUsage.isIncertaeSedis())
		statement.setInt(index++, 1);
	else
		statement.setInt(index++, 0);

	return index;
    }

    public int setValues(PreparedStatement statement,
			 Annotation annotation,
			 int index)
	throws SQLException
    {
	setString(statement, index++, annotation.getLinkType());
	NamedObject<?> object = annotation.getAppearance();
	if(object == null)
	    statement.setNull(index++, java.sql.Types.NULL);
	else
	    statement.setInt(index++, getObjectID(object, true));
	//explicit is yet spported
	statement.setNull(index++, java.sql.Types.NULL);
	//path  is yet suported
	statement.setNull(index++, java.sql.Types.NULL);
	return index;
    }

    public int setValues(PreparedStatement statement,
			 NameUsageNode<?> nameUsageNode,
			 int index)
	throws SQLException
    {
	return index;
    }
    /*
    protected int setValues(CallableStatement statement,
			    NamedObject<?> object,
			    int index)
    {
	return index;
    }

    protected int setValues(PreparedStatement statement, 
			    NamedObject<?> object,
			    int index)
    {
	return -1;
    }
    */

    public void clearWarnings()
	throws SQLException
    {
	connection.clearWarnings();
    }

    public void close()
	throws SQLException
    {
	close(true);
    }

    public void close(boolean pool)
	throws SQLException
    {
	if(pool) {
	    isOpen = false;
	    Set<Connection> connectionSet = connections.get(dataSource);
	    if(connectionSet == null) {
		connectionSet = Collections.synchronizedSet(new HashSet<Connection>());
		connections.put(dataSource, connectionSet);
	    }
	    connectionSet.add(connection);
	}
	else {
	    connection.close();
	    connection = null;
	}
    }

    public void commit()
	throws SQLException
    {
	connection.commit();
    }

    public Statement createStatement()
	throws SQLException
    {
	return connection.createStatement();
    }


    public Statement createStatement(int resultSetType,
				     int resultSetConcurrency)
	throws SQLException
    {
	return connection.createStatement(resultSetType, resultSetConcurrency);
    }

    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
	throws SQLException
    {
	return createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    public boolean getAutoCommit()
	throws SQLException
    {
	return  connection.getAutoCommit();
    }

    public String getCatalog()
	throws SQLException
    {
	if(connection != null)
	    return connection.getCatalog();

	return null;
    }

    public int getHoldability()
	throws SQLException
    {
	return connection.getHoldability();
    }

    public DatabaseMetaData getMetaData()
	throws SQLException
    {
	if(connection != null)
	    return connection.getMetaData();

	return null;
    }

    public int getTransactionIsolation()
	throws SQLException
    {
	return connection.getTransactionIsolation();
    }

    public Map<String, Class<?>> getTypeMap()
	throws SQLException
    {
	if(connection != null)
	    return connection.getTypeMap();

	return null;
    }

    public SQLWarning getWarnings()
	throws SQLException
    {
	if(connection != null)
	    return connection.getWarnings();

	return null;
    }

    public boolean isClosed()
	throws SQLException
    {
	return isClosed(false);
    }

    public boolean isClosed(boolean pool)
	throws SQLException
    {
	if(pool)
	    return !isOpen;

	if(connection != null)
	    return connection.isClosed();

	return false;
    }

    public boolean isReadOnly()
	throws SQLException
    {
	return connection.isReadOnly();
    }

    public String nativeSQL(String sql)
	throws SQLException
    {
	if(connection != null)
	    return connection.nativeSQL(sql);

	return null;
    }

    public CallableStatement prepareCall(String sql)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareCall(sql);

	return null;
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareCall(sql, resultSetType, resultSetConcurrency);

	return null;
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

	return null;
    }

    public PreparedStatement prepareStatement(String sql)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql);

	return null;
    }

    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql, autoGeneratedKeys);

	return null;
    }

    public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql, columnIndexes);

	return null;
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql, resultSetType, resultSetConcurrency);

	return null;
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

	return null;
    }

    public PreparedStatement prepareStatement(String sql, String[] columnNames)
	throws SQLException
    {
	if(connection != null)
	    return connection.prepareStatement(sql, columnNames);

	return null;
    }

    public void releaseSavepoint(Savepoint savepoint)
	throws SQLException
    {
	if(connection != null)
	    connection.releaseSavepoint(savepoint);
    }

    public void rollback()
	throws SQLException
    {
	if(connection != null)
	    connection.rollback();
    }

    public void rollback(Savepoint savepoint)
	throws SQLException
    {
	if(connection != null)
	    connection.rollback(savepoint);
    }

    public void setAutoCommit(boolean autoCommit)
	throws SQLException
    {
	if(connection != null)
	    connection.setAutoCommit(autoCommit);
    }
    
    public void setCatalog(String catalog)
	throws SQLException
    {
	connection.setCatalog(catalog);
    }

    public void setHoldability(int holdability)
	throws SQLException
    {
	if(connection != null)
	    connection.setHoldability(holdability);
    }

    public void setReadOnly(boolean readOnly)
	throws SQLException
    {
	if(connection != null)
	    connection. setReadOnly(readOnly);
    }

    public Savepoint setSavepoint()
	throws SQLException
    {
	if(connection != null)
	    return connection.setSavepoint();

	return null;
    }

    public Savepoint setSavepoint(String name)
	throws SQLException
    {
	if(connection != null)
	    return connection.setSavepoint(name);
	return null;
    }

    public void setTransactionIsolation(int level)
	throws SQLException
    {
	connection.setTransactionIsolation(level);
    }

    public void setTypeMap(Map<String, Class<?>> map)
	throws SQLException
    {
	if(connection != null)
	    connection.setTypeMap(map);
    }

    //interfaces introduced JDK 1.6

    public Clob createClob()
	throws SQLException
    {
	if(connection != null)
	    return connection.createClob();

	return null;
    }

    public Blob createBlob()
                throws SQLException
    {
	if(connection != null)
	    return connection.createBlob();

	return null;
    }

    public NClob createNClob()
                  throws SQLException
    {
	if(connection != null)
	    return connection.createNClob();

	return null;
    }

    public SQLXML createSQLXML()
	throws SQLException
    {
	if(connection != null)
	    return connection.createSQLXML();

	return null;
    }

    public boolean isValid(int timeout)
	throws SQLException
    {
	if(connection != null)
	    return connection.isValid(timeout);

	return false;
    }

    public void setClientInfo(String name,
			      String value)
	throws SQLClientInfoException
    {
	if(connection != null)
	    connection.setClientInfo(name, value);
    }

    public void setClientInfo(Properties properties)
                   throws SQLClientInfoException
    {
	if(connection != null)
	    connection.setClientInfo(properties);
    }

    public String getClientInfo(String name)
	throws SQLException
    {
	if(connection != null)
	    return connection.getClientInfo(name);
	return null;
    }

    public Properties getClientInfo()
	throws SQLException
    {
	if(connection != null)
	    return connection.getClientInfo();

	return null;
    }

    public Array createArrayOf(String typeName,
			       Object[] elements)
	throws SQLException
    {
	if(connection != null)
	    return connection.createArrayOf(typeName, elements);

	return null;
    }

    public Struct createStruct(String typeName,
			       Object[] attributes)
	throws SQLException
    {
	if(connection != null)
	    return connection.createStruct(typeName, attributes);
	return null;
    }


    //methods to implement java.sql.Wrapper

    public <T> T unwrap(Class<T> iface)
	throws SQLException
    {
	return connection.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface)
	throws SQLException
    {
	return connection.isWrapperFor(iface);
    }

    public void setSchema(String schema)
               throws SQLException
    {
	if(connection != null)
	    connection.setSchema(schema);
    }

    public String getSchema()
                 throws SQLException
    {
	if(connection != null)
	    return connection.getSchema();
	return null;
    }

    public void abort(Executor executor)
           throws SQLException
    {
	if(connection != null)
	    connection.abort(executor);
    }	

    public void setNetworkTimeout(Executor executor,
                     int milliseconds)
                       throws SQLException
    {
	// or, set to all connections if pooled?
	if(connection != null)
	    connection.setNetworkTimeout(executor, milliseconds);
    }

    public int getNetworkTimeout()
	throws SQLException
    {
	if(connection != null)
	    return connection.getNetworkTimeout();
	return 0;
    }

    static public void main (String[] args)
    {
	NamedObjectConnection<DefaultNameUsage> c = null;

	try {
	    String passwd = "";
	    if(args.length > 2)
		passwd = args[2];

	    c = new NamedObjectConnection<DefaultNameUsage>(args[0], 
					  args[1], 
					  passwd,
					  ResourceKey.POSTGRESQL);
	}
	catch(Exception e) {
	    e.printStackTrace();
	    return;
	}
	if(c == null) {
	    return;
	}

	try {
	    Agent a = new Agent();
	    if(a instanceof Person)
		((Person)a).setSurname("Linnaeus");
	    int oid = c.insert(a);
	    Agent b = (Agent)c.getNamedObject(oid, NOMINAL);
	    c.close();
	}
	catch(Exception e) {
	    e.printStackTrace();
	}
    }

}
