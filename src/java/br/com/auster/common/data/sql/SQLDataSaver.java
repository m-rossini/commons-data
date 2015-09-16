/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Created on 06/12/2005
 */

package br.com.auster.common.data.sql;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.asm.FieldAccessor;
import br.com.auster.common.data.DataSaver;
import br.com.auster.common.data.DataSaverException;
import br.com.auster.common.data.definition.Column;
import br.com.auster.common.data.definition.ForeignKeyColumn;
import br.com.auster.common.data.definition.IdColumn;
import br.com.auster.common.data.definition.Table;
import br.com.auster.common.data.definition.TableBuilder;
import br.com.auster.common.data.definition.TableBuilderFactory;
import br.com.auster.common.data.runtime.FAMultiTypeCache;
import br.com.auster.common.data.runtime.MultiTypeCache;
import br.com.auster.common.data.runtime.RecordInstance;
import br.com.auster.common.data.runtime.RecordInstanceFactory;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * <p><b>Title:</b> SQLDataSaver</p>
 * <p><b>Description:</b> A SQLDataSaver</p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: SQLDataSaver.java 371 2007-02-24 01:05:09Z mtengelm $
 * 
 * <sql-data-saver-config id="saver1">
 *     <table name="table1">
 *         <field name="field1" value="data1"/>
 *         <field name="field2" value="data2.property"/>
 *         <field name="field3" value="dataX.method()"/>
 *         ...
 *     </data>
 *     <table name="table2" data-list="recordList" item-name="record">
 *         <field name="field1" value="data3"/>
 *         <field name="field2" value="record.property"/>
 *         <field name="field3" value="record.method()"/>
 *         ...
 *     </data>
 * </sql-data-saver-config>
 * 
 */
public class SQLDataSaver implements DataSaver {
	
	
	
    public static final String CONFIG_ROOT_ELEM = "sql-data-saver-config";
    public static final String CONFIG_ROOT_ID_ELEM = "id";
    
    private static Logger log = LogFactory.getLogger( SQLDataSaver.class );
    
    protected String id;
    protected Map<String, Table> tables;

    protected RecordInstanceFactory tdiFactory;
    protected MultiTypeCache tdiCache;
    protected MultiTypeCache faCache;

    protected Connection conn;
    
    
    
    
    public SQLDataSaver() {
    	super();
    	this.id  = "";
    	this.tables = new LinkedHashMap<String, Table>();
    }
    
    
    public void configure(Element config, Connection _conn) throws DataSaverException {
        
        if (_conn == null) {
            throw new DataSaverException("Cannot work with NULL database connection");
        }
        this.conn = _conn;
//        try {
//            this.conn.setAutoCommit(false);
//        } catch (SQLException sqle) {
//            log.warn("could not disable auto-commit");
//        }
        
        log.debug("Configuring SQLDataSaver...");
        if (!CONFIG_ROOT_ELEM.equals(config.getLocalName())) {
            config = DOMUtils.getElement(config, CONFIG_ROOT_ELEM, true);
        }
        id = DOMUtils.getAttribute(config, CONFIG_ROOT_ID_ELEM, true);

        DatabaseMetaData meta;
        try {
            meta = this.conn.getMetaData();
        } catch (SQLException e1) {
            throw new DataSaverException("Error retrieving database metadata", e1);
        }

        tdiFactory = new RecordInstanceFactory();
        tdiCache = new MultiTypeCache();
        faCache = new FAMultiTypeCache();

        TableBuilder tableBuilder = TableBuilderFactory.getTableBuilder(Table.class);

        // Iterating through tables list
        NodeList tableNodeList = DOMUtils.getElements(config, TableBuilder.CONFIG_TABLE_ELEM);
        int tnlLength = tableNodeList.getLength();
        for (int i = 0; i < tnlLength; i++) {
            Element table = (Element) tableNodeList.item(i);
            Table t = configureTable(meta, table, tableBuilder);
            this.tables.put(t.getName(), t);
            this.tdiCache.initCache(t.getName(), t.getCache());
        }

        log.debug("SQLDataSaver [" + id + "] successfully configured with [" + tables.size() + "] tables.");
    }


    protected Table configureTable(DatabaseMetaData _meta, Element _tableConfiguration, 
                                   TableBuilder _tableBuilder) throws DataSaverException {
            
        Table tableDef = _tableBuilder.readTableDefinition(_tableConfiguration); 
        try {
            // Iterating through fields list
            NodeList fieldNodeList = DOMUtils.getElements(_tableConfiguration, TableBuilder.CONFIG_FIELD_ELEM);
            for ( int j = 0; j < fieldNodeList.getLength(); j++ ) {
            	tableDef.addField(_tableBuilder.readFieldDefinition(tableDef, _meta, (Element) fieldNodeList.item(j)));
            }
            // Iterating through FK fields list
            fieldNodeList = DOMUtils.getElements(_tableConfiguration, TableBuilder.CONFIG_FK_FIELD_ELEM);
            for ( int j = 0; j < fieldNodeList.getLength(); j++ ) {
            	tableDef.addField(_tableBuilder.readFieldDefinition(tableDef, _meta, (Element) fieldNodeList.item(j)));
            }
            // Iterating through ID fields list
            Element idField = DOMUtils.getElement(_tableConfiguration, TableBuilder.CONFIG_ID_FIELD_ELEM, false);
            if (idField != null) {
            	tableDef.addField(_tableBuilder.readFieldDefinition(tableDef, _meta, idField));
            }
            _tableBuilder.createStatements(this.conn, tableDef);
        } catch (SQLException sqle) {
            throw new DataSaverException("Error preparing statements", sqle);
        }
        log.debug("Table [" + tableDef.getName() + "] successfully configured.");
        return tableDef;
    }

    /**
     * @inheritDoc
     */
    public void save(Map inputData) throws DataSaverException {
        // iterating over all tables
        for ( Iterator i = tables.values().iterator(); i.hasNext(); ) {
            Table table = (Table) i.next();
            // iterate over the list to insert multiple records in the table
            if ( table.getDataList() != null ) {
                List list = (List) inputData.get( table.getDataList() );
                if (list == null) {
                    log.warn("no data found in " + table.getDataList() + " to handle table " + table.getName());
                    continue;
                }
                for ( Iterator j = list.iterator(); j.hasNext(); ) {
                	try {
	                	Object currentValue = j.next();
	                	FieldAccessor fa = (FieldAccessor) faCache.get(currentValue.getClass(), table.getItemExpression());
	                	Object instance = processRecord(table, (fa == null? currentValue : fa.getValue(currentValue)));
	                	this.propagateGeneratedKeys(table, instance, currentValue);
                	} catch (Exception e) {
                		log.error("could not validate record for table " + table.getName(), e);
                	}
                }
            } else {
                // populate only one record
                this.processRecord(table, inputData);
            }
            try {
                table.getInsertStatement().executeBatch();
                table.getUpdateStatement().executeBatch();
        	log.debug("executed updates/inserts for table " + table.getName());
            } catch (SQLException sqle) {
                log.error("could not insert records for table " + table.getName(), sqle);
                throw new DataSaverException(sqle);
            }
        }
        try{
            if (!this.conn.getAutoCommit()) {
                this.conn.commit();
        	log.debug("executed manual commit");
            } else {
        	log.debug("executed automatic commit");
            }
        } catch (SQLException sqle) {
            throw new DataSaverException("could not commit transaction", sqle);
        }
        log.debug("cache hits for '" + this.id + "' =" + this.tdiCache.dumpHits());
        log.debug( "Data correctly saved for SQLDataSaver [" + id + "]" );
    }

    protected RecordInstance processRecord(Table _table, Object _values) throws DataSaverException {
    	RecordInstance instance = null;
    	RecordInstance inCache = (RecordInstance) tdiCache.get(_table.getName(), _values);
    	if (inCache != null) {
    		instance = inCache;
    		log.debug("current instance found in cache");
    	} else {
    		inCache = loadRecord(_table, _values);
    		if (inCache != null) {
    			instance = inCache;
    			log.debug("current instance loaded from database");
    		} else {
    			// creating new instance
        		instance = this.tdiFactory.createInstance(_table);
                instance.setNewInstance(true);
        		// setting fields
                this.tdiFactory.setFields(_table, instance, _values, tdiCache, faCache);
                log.debug("current instance is a new record");
    		}
            tdiCache.put(_table.getName(), _values, instance);
			log.debug("instance added to cache");
    	}
        // needed since this method is called recursively within loadRecord()
        saveRecord(_table, instance);
        instance.setNewInstance(false);
        // will propagate any generated keys to the input source object.
        // This is specially important when the object was inserted into the database.
    	propagateGeneratedKeys(_table, instance, _values);
        return instance;
    }
    
    protected void saveRecord(Table _table, RecordInstance _instance) throws DataSaverException {
    	
    	if(_instance.isNewInstance() || _table.getMode().equals(Table.MODE_INSERT)) {
    		this.insertRecord(_table, _instance);
        } else if (_table.getMode().equals(Table.MODE_UPDATE)) {
            this.updateRecord(_table, _instance);
        }
    }

    protected void insertRecord(Table _table, RecordInstance _instance) throws DataSaverException {
        int index = 0;
        
        PreparedStatement insert = _table.getInsertStatement();
        for ( Iterator i = _table.getFields().iterator(); i.hasNext(); ) {
            Column fieldDef = (Column) i.next();
            if (fieldDef instanceof IdColumn) {
                try {
                    Statement stmt = _table.getIdStatement();
                    ResultSet rset = stmt.executeQuery(((IdColumn)fieldDef).getGeneratorSQL());
                    if (rset.next()) {
                        long value = rset.getLong(1);
                        log.debug("id " + value + " generated for field " + fieldDef.getFieldName());
                        insert.setLong(++index, value);
                        fieldDef.getFieldDefinition().setValue(_instance, new Long(value));
                    } else {
                        log.warn("no id generated for field " + fieldDef.getFieldName() + ". NULL will be set.");
                        insert.setNull(++index, fieldDef.getSQLType());
                    }
                } catch (Exception e) {
                    throw new DataSaverException("Could not generate ids for table [" + _table.getName() + "]", e);
                }
            } else {
                try {
                	// if type is one of TIME/DATE/TIMESTAMP should convert objects
                	Object toSave = convertJava2Database(fieldDef.getFieldDefinition().getValue(_instance), fieldDef);
                	if (toSave != null) {
                        log.debug("field [" + index + "] for table " + _table.getName() + " inserted with value " + toSave);
                		insert.setObject(++index, toSave, fieldDef.getSQLType());
                    } else {
                        log.debug("field [" + index + "] for table " + _table.getName() + " inserted with NULL");
                    	insert.setNull(++index, fieldDef.getSQLType());
                    }
                } catch (Exception e) {
                    throw new DataSaverException("Error setting value into field ["+fieldDef.getFieldName() + "] to save data into table [" + _table.getName() + "]", e);
                }
            }
        }
        // executing the insert
        try {
            log.debug("added batch insert for table " + _table.getName());
            insert.addBatch();
        } catch (SQLException e) {
            throw new DataSaverException("Error inserting data into table [" + _table.getName() + "]", e );
        }
    }

    protected void updateRecord(Table table, RecordInstance _instance) throws DataSaverException {
        // iterating over all fields to set its value into insertStatement
        int index = 0;
        if(table.getKeyFields().isEmpty()) {
            return;
        }
        PreparedStatement update = table.getUpdateStatement();
        for (Iterator i = table.getNonKeyFields().iterator(); i.hasNext();) {
            try {
                Column fieldDef = (Column) i.next();
                if(!fieldDef.isKey()) {
                    Object value = convertJava2Database(fieldDef.getFieldDefinition().getValue(_instance), fieldDef);
                    if (value != null) { 
                    	update.setObject(++index, value, fieldDef.getSQLType());
                    } else {
                    	update.setNull(++index, fieldDef.getSQLType());
                    }
                }
            } catch (Exception e) {
            	throw new DataSaverException("Error setting parameters to update data into table [" + table.getName() + "]", e);
			}
        }
        for ( Iterator i = table.getKeyFields().iterator(); i.hasNext(); ) {
            try {
                Column fieldDef = (Column) i.next();
                Object value = convertJava2Database(fieldDef.getFieldDefinition().getValue(_instance), fieldDef);
                if (value != null) { 
                    log.debug("field [" + index + "] for table "+ table.getName() + " updated with value " + value);
                	update.setObject(++index, value, fieldDef.getSQLType());
                } else {
                    log.debug("field [" + index + "] for table " + table.getName() + " updated with NULL");
                	update.setNull(++index, fieldDef.getSQLType());
                }
            } catch (Exception e) {
                throw new DataSaverException("Error setting parameters to update data into table [" + table.getName() + "]", e);
			}
        }
        // executing the update
        try {
            log.debug("added batch update for table " + table.getName());
            update.addBatch();
        } catch (SQLException e) {
            throw new DataSaverException("Error updating data into table [" + table.getName() + "]", e);
        }
    }

    protected RecordInstance loadRecord(Table _table, Object _values) throws DataSaverException {
        // iterating over all keyFields fields to set its value into insertStatement
    	RecordInstance instance = null;
        int index = 0;
        if(_table.getKeyFields().isEmpty()) {
           return null; 
        }
        PreparedStatement select = _table.getSelectStatement();
        for (Iterator i = _table.getKeyFields().iterator(); i.hasNext();) {
            Column fieldDef = (Column) i.next();
            Object value = null;
            try {
                log.debug("current value is " + _values);
            	FieldAccessor accessor = (FieldAccessor)faCache.get(_values.getClass(), fieldDef.getValueExpression());
            	if (accessor != null) {
                    value = accessor.getValue(_values);
                    log.debug("got value '" + value + "' for " + _values.getClass() + "." + fieldDef.getValueExpression());
                    if (fieldDef instanceof ForeignKeyColumn) {
                        Table fkTable = (Table) this.tables.get(((ForeignKeyColumn)fieldDef).getForeignKeyTable());
                        value = processRecord(fkTable, value);
                        if (value == null) {
                            log.warn("value should be in cache/database for table " + ((ForeignKeyColumn)fieldDef).getForeignKeyTable());
                            return null; 
                        }
                        accessor = (FieldAccessor) this.faCache.get(value.getClass(), ((ForeignKeyColumn)fieldDef).getForeignKeyName());
                        value = accessor.getValue(value);
                        if (value == null) { 
                            log.warn("value in cache should have non-null value for attribute: " + ((ForeignKeyColumn)fieldDef).getForeignKeyName());
                            return null; 
                        }
                    }
                   	select.setObject(++index, convertJava2Database(value, fieldDef), fieldDef.getSQLType());
            	} else {
            		select.setNull(++index, fieldDef.getSQLType());
            	}
            } catch (Exception e) {
                throw new DataSaverException("Error setting parameter [" + fieldDef.getFieldName() + "/" + index + "] of type " + fieldDef.getSQLType() + 
                		                     " with value [" + value + "] to select data from table [" + _table.getName() + "]", e);
            }
        }
        // executing the select
        ResultSet rs = null;
        try {
            rs = select.executeQuery();
          while (rs.next()) {
            	instance = this.tdiFactory.createInstance(_table);
                for(Iterator i = _table.getFields().iterator(); i.hasNext(); ) {
                    Column fieldDef = (Column) i.next();
                    Object value = rs.getObject(fieldDef.getFieldName());
                    if (value != null) {
                        if (fieldDef.getType().equals("long")) {
                            value = new Long(value.toString());
                        } else if (fieldDef.getType().equals("double")) {
                            value = new Double(value.toString());
                        } else if (fieldDef.getType().equals("int")) {
                            value = new Integer(value.toString());
                        }
                    }
                    fieldDef.getFieldDefinition().setValue(instance, value);
                }
                // instance created! Now validating against keys
                if (isSameInstance(_table, _values, instance)) {
                	break;
                }
                instance = null;
            }
            return instance;
        } catch (Exception e) {
            throw new DataSaverException("Error querying data from table [" + _table.getName() + "]", e);
        } finally {
            try {
                if (rs != null) { rs.close(); }
            } catch (SQLException e) {}
        }
    }
    
    protected boolean isSameInstance(Table _table, Object _values, Object _instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // will find if the _value key == _instance key
    	boolean isSame = true; 
    	for (Iterator i = _table.getKeyFields().iterator(); i.hasNext();) {
            Column fieldDef = (Column) i.next();
            Object value = null;
            log.debug("current value is " + _values);
        	FieldAccessor accessor = (FieldAccessor)faCache.get(_values.getClass(), fieldDef.getValueExpression());
        	FieldAccessor accessorInst = (FieldAccessor)faCache.get(_instance.getClass(), fieldDef.getFieldName());
        	if (accessor != null) {
        		if (accessorInst == null) {
        			throw new IllegalStateException("Should have table instance accessor, if datasource accessor exists.");
        		}
                value = accessor.getValue(_values);
                log.debug("got value '" + value + "' for " + _values.getClass() + "." + fieldDef.getValueExpression());

                if (value != null) {
                	isSame &= (value.equals(accessorInst.getValue(_instance)));
                } else {
                	isSame &= (accessorInst.getValue(_instance) == null);
                }
        	} else {
        		isSame &= (accessorInst.getValue(_instance) == null);
        	}
    	}
    	return isSame;
    }
    
    protected Object convertJava2Database(Object _value, Column _fieldDef) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
    	Object toSave = _value;
    	if (toSave != null) {
    		if (_fieldDef.getSQLType() == Types.TIMESTAMP) {
                toSave = new Timestamp(((Date) toSave).getTime());
            } else if (_fieldDef.getSQLType() == Types.TIME) {
            	toSave = new Time(((Date) toSave).getTime());
            } else if (_fieldDef.getSQLType() == Types.DATE) {
            	toSave = new java.sql.Date(((Date) toSave).getTime());
            }
    	}
    	return toSave;
    }
    
    protected void propagateGeneratedKeys(Table _table, Object _instance, Object _source) {
    	
    	if (_table.getItemExpressionUid() == null) {
    		log.debug("Will not propagate since there is no uid specified for source object");
    		return;
    	}
    	if ((_instance == null) || (_source == null)) {
    		log.debug("Will not propagate since either generated object or source object is null");
    		return;
    	}
    	if (_table.getIdField() == null) {
    		log.warn("Cannot propagate generated keys since table has no ID column defined.");
    		return;
    	}
    	// TODO maybe this can be another attribute in the table definition XML
    	IdColumn idField = (IdColumn)_table.getIdField();
    	FieldAccessor accessor = (FieldAccessor) this.faCache.get(_instance.getClass(), idField.getFieldName());
    	Object keyValue = accessor.getValue(_instance);
    	log.debug("Got key [" + keyValue + "] from current instance of " + _instance.getClass());
    	accessor = (FieldAccessor) this.faCache.get(_source.getClass(), _table.getItemExpressionUid());
    	accessor.setValue(_source, keyValue);
    	log.debug("Set key [" + keyValue + "] to current source of " + _source.getClass());
    }
}
