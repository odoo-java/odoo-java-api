/*
 *   Copyright 2011, 2013-2014 De Bortoli Wines Pty Limited (Australia)
 *
 *   This file is part of OdooJavaAPI.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License. 
 *
 */

package com.debortoliwines.odoo.api;

import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;

/**
 * Wrapper class for Odoo commands. It uses the session object to make the call,
 * but builds the parameters in this class
 * 
 * @author Pieter van der Merwe
 *
 */
public class OdooCommand {
	
	private final Session session;
	
	/**
	 * Main constructor
	 * 
	 * @param session
	 *            Session object that will be used to make the calls to Odoo.
	 */
	public OdooCommand(Session session){
		this.session = session;
	}
	
	/**
	 * Searches for objects that satisfies the filter.  These IDs are typically used in a following readObject call to the server to get the data
	 * @param objectName The object name to do a search for
	 * @param filter A filter array that contains a list of filters to be applied.
	 * @return Array of ID's for the objects found
	 * @throws XmlRpcException
	 */
	public Object[] searchObject(String objectName, Object [] filter) throws XmlRpcException {
		return (Object[]) searchObject (objectName,filter,-1,-1,null,false);
	}
	
	/**
	 * Searches for objects that satisfies the filter.  These IDs are typically used in a following readObject call to the server to get the data 
	 * @param objectName The object name to do a search for
	 * @param filter A filter array that contains a list of filters to be applied.
	 * @param offset Number of records to skip. -1 for no offset.
	 * @param limit Maximum number of rows to return. -1 for no limit.
	 * @param order Field name to order on
	 * @param count If the count should be returned, in stead of the IDs
	 * @return If count = true, a integer is returned, otherwise a Object[] of IDs are returned 
	 * @throws XmlRpcException
	 */
	public Object searchObject(String objectName, Object [] filter, int offset, int limit, String order, boolean count) throws XmlRpcException {
		Object offsetParam = offset < 0 ? false : offset;
		Object limitParam = limit < 0 ? false : limit;
		Object orderParam = order == null || order.length() == 0 ? false : order;
		Object[] params = new Object[] {filter, offsetParam, limitParam, orderParam, session.getContext(), count};
		return session.executeCommand(objectName, "search", params);
	}

	/**
	 * Fetches field information for an object n Odoo
	 * 
	 * @param objectName
	 *            Object or model name to fetch field information for
	 * @param filterFields
	 *            Only return data for files in the filter list
	 * @return A HashMap of field-value pairs.
	 * @throws XmlRpcException
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getFields(String objectName, String[] filterFields) throws XmlRpcException {
		return (Map<String, Object>) session.executeCommand(objectName, "fields_get",
				new Object[] { filterFields, session.getContext() });
	}

	/**
	 * Reads object data from the Odoo server
	 * 
	 * @param objectName
	 *            Name of the object to return data for
	 * @param ids
	 *            List of id to fetch data for. Call searchObject to get a
	 *            potential list
	 * @param fields
	 *            List of fields to return data for
	 * @returnA collection of rows for an Odoo object
	 * @throws XmlRpcException
	 */
	public Object[] readObject(String objectName, Object [] ids, String [] fields) throws XmlRpcException {
		return (Object[]) session.executeCommand(objectName, "read", new Object[] {ids, fields, session.getContext()});
	}
	
	/**
	 * Updates object values
	 * @param objectName Name of the object to update
	 * @param id Database ID number of the object to update
	 * @param valueList Field/Value pairs to update on the object
	 * @return True if the update was successful
	 * @throws XmlRpcException
	 */
	public boolean writeObject(String objectName, int id, Map<String, Object> valueList) throws XmlRpcException {
		return (Boolean) session.executeCommand(objectName, "write", new Object[] { id, valueList });
	}

	/**
	 * Calls the import function on the server to bulk create/update records
	 * @param objectName Name of the object to update
	 * @param fieldList List of fields to update.  The import function has some specific naming conventions.  Consider using the ObjectAdapter
	 * @param rows Rows to import.  Fields must be in the same order as the 'fieldList' parameter
	 * @return The result returned from the server.  Either returns the number of successfully imported rows or returns the error.
	 * @throws XmlRpcException
	 */
	
	public Object [] importData(String objectName, String[] fieldList, Object [][] rows) throws XmlRpcException {
		return (Object []) session.executeCommand(objectName, "import_data", new Object[] {fieldList, rows, "init", "", false, session.getContext()});
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> load(String objectName, String[] fieldList, Object[][] rows) throws XmlRpcException {
	  Object o = session.executeCommand(objectName, "load", new Object[] {fieldList, rows});
		return (Map<String, Object>) o;
  }
  
	/**
	 * Returns the name_get result of an object in the Odoo server.
	 * 
	 * @param objectName
	 *            Object name to invoke the name_get on
	 * @param ids
	 *            Database IDs to invoke the name_get for
	 * @return An Object[] with an entry for each ID. Each entry is another
	 *         Object [] with index 0 being the ID and index 1 being the Name
	 * @throws XmlRpcException
	 */
	public Object[] nameGet(String objectName, Object[] ids) throws XmlRpcException{
		return (Object[]) session.executeCommand(objectName, "name_get", new Object[] {ids});
	}
	
	/**
	 * Deletes objects from the Odoo Server
	 * 
	 * @param objectName
	 *            Object name to delete rows from
	 * @param ids
	 *            List of ids to delete data from
	 * @return If the command was successful
	 * @throws XmlRpcException
	 */
	public boolean unlinkObject(String objectName, Object [] ids) throws XmlRpcException {
		return (Boolean) session.executeCommand(objectName, "unlink", new Object[] {ids});
	}
	
	/**
	 * Creates a single object
	 * 
	 * @param objectName
	 *            Name of the object to create
	 * @param values
	 *            Map of values to assign to the new object
	 * @return The database ID of the new object
	 * @throws XmlRpcException
	 */
	public Object createObject(String objectName, Map<String, Object> values) throws XmlRpcException {
		return session.executeCommand(objectName, "create", new Object[] {values, session.getContext()});
	}
	
	/**
	 * Calls any function on an object. The function Odoo must have the
	 * signature like (self, cr, uid, *param) and return a dictionary or object.
	 * *param can be replaced by separate parameters if you are sure of the
	 * number of parameters expected
	 * 
	 * @param objectName
	 *            Object name where the function exists
	 * @param functionName
	 *            function to call
	 * @param parameters
	 *            Additional parameters that will be passed to the object
	 * @return An Object array of values
	 */
	public Response callObjectFunction(String objectName, String functionName, Object[] parameters) {
		try {
			return new Response( session.executeCommand(objectName, functionName, parameters) );
		} catch (XmlRpcException e) {
			return new Response(e);
		}
	}
	
	/**
   * Executes a workflow by sending a signal to the workflow engine for a specific object.
   * All parameters are prepended by: "databaseName,userID,password"
   * @param objectName Object or model name to send the signal for
   * @param signal Signal name to send, for example order_confirm
   * @param objectID Specific object ID to send the signal for
   * @return 
   * @throws XmlRpcException
   */
  public void executeWorkflow(final String objectName, final String signal, final int objectID) throws XmlRpcException {
    session.executeWorkflow(objectName, signal, objectID);   
  }

}