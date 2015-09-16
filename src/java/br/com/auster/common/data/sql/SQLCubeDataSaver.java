/*
 * Copyright (c) 2004 Auster Solutions. All Rights Reserved.
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
 * Created on 24/03/2006
 */
package br.com.auster.common.data.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.asm.FieldAccessor;
import br.com.auster.common.data.DataSaverException;
import br.com.auster.common.data.definition.Column;
import br.com.auster.common.data.definition.CubeTable;
import br.com.auster.common.data.definition.CubeTableBuilder;
import br.com.auster.common.data.definition.DimensionTable;
import br.com.auster.common.data.definition.DimensionTableBuilder;
import br.com.auster.common.data.definition.IdColumn;
import br.com.auster.common.data.definition.Table;
import br.com.auster.common.data.definition.TableBuilder;
import br.com.auster.common.data.definition.TableBuilderFactory;
import br.com.auster.common.data.runtime.RecordInstance;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;


/**
 * @author framos
 * @version $Id: SQLCubeDataSaver.java 371 2007-02-24 01:05:09Z mtengelm $
 */
public class SQLCubeDataSaver extends SQLDataSaver {

    
    private static Logger log = LogFactory.getLogger(SQLCubeDataSaver.class);
    

    /**
     * @see br.com.auster.common.data.sql.SQLDataSaver#configureTable(Connection, DatabaseMetaData, Element, TableBuilder)
     */
    protected Table configureTable(DatabaseMetaData _meta, Element _tableConfiguration, 
                                   TableBuilder _tableBuilder) throws DataSaverException {
        log.debug("Starting configure table.");
        TableBuilder tableBuilder = TableBuilderFactory.getTableBuilder(CubeTable.class);
        TableBuilder dimTableBuilder = TableBuilderFactory.getTableBuilder(DimensionTable.class);
        CubeTable tableDef = (CubeTable) tableBuilder.readTableDefinition(_tableConfiguration); 
        try {
            NodeList dimNodeList = DOMUtils.getElements(_tableConfiguration, CubeTableBuilder.CONFIG_DIMENSION_ELEM);
            for ( int j = 0; j < dimNodeList.getLength(); j++ ) {
                Element dimConfiguration = (Element) dimNodeList.item(j);
                DimensionTable dimensionDef = (DimensionTable) super.configureTable(_meta, dimConfiguration, dimTableBuilder);
                dimensionDef.setExportField(DOMUtils.getAttribute(dimConfiguration, DimensionTableBuilder.CONFIG_TABLE_EXPORTTO_ATTR, true));
                tableDef.addDimension(dimensionDef);
                this.tdiCache.initCache(dimensionDef.getName(), dimensionDef.getCache());
            }            
            // Iterating through fields list
            NodeList fieldNodeList = DOMUtils.getElements(_tableConfiguration, TableBuilder.CONFIG_FIELD_ELEM);
            for ( int j = 0; j < fieldNodeList.getLength(); j++ ) {
                tableDef.addField(tableBuilder.readFieldDefinition(tableDef, _meta, (Element) fieldNodeList.item(j)));
            }
            // Iterating through ID fields list
            Element idField = DOMUtils.getElement(_tableConfiguration, TableBuilder.CONFIG_ID_FIELD_ELEM, false);
            if (idField != null) {
            	tableDef.addField(_tableBuilder.readFieldDefinition(tableDef, _meta, idField));
            }
            tableBuilder.createStatements(this.conn, tableDef);
        } catch (SQLException sqle) {
            throw new DataSaverException("Error preparing statements", sqle);
        }
        log.debug("Table [" + tableDef.getName() + "] successfully configured.Def:" + tableDef);
        return tableDef;
    }

    
    protected RecordInstance processRecord(Table _table, Object _values) throws DataSaverException {
        CubeTable cube = (CubeTable) _table;
        RecordInstance instance = this.tdiFactory.createInstance(_table);
        // setting fields
        this.tdiFactory.setFields(_table, instance, _values, tdiCache, faCache);
        // handling dimension definitions
        for (Iterator it=cube.getDimensions().iterator(); it.hasNext();) {
            DimensionTable dimension = (DimensionTable) it.next();
            FieldAccessor fa = (FieldAccessor) this.faCache.get(_values.getClass(), dimension.getItemExpression());
            RecordInstance dimensionInstance = super.processRecord(dimension, (fa == null? _values : fa.getValue(_values)));
            IdColumn idField = dimension.getIdField();
            if (idField != null) {                
                fa = (FieldAccessor) this.faCache.get(dimensionInstance.getClass(), idField.getFieldName());
                FieldAccessor cubeFa = (FieldAccessor) this.faCache.get(instance.getClass(), dimension.getExportField());
                cubeFa.setValue(instance, fa.getValue(dimensionInstance));
            }
            try {
                dimension.getInsertStatement().executeBatch();
                dimension.getUpdateStatement().executeBatch();
            } catch (SQLException sqle) {
                throw new DataSaverException("could not insert records for table " + dimension.getName(), sqle);
            }
        }
        // saving cube record
	if (cubeRecordExists(_table, instance)) { 
            updateRecord(_table, instance); 
        } else { 
            insertRecord(_table, instance); 

    	//if(instance.isNewInstance() || _table.getMode().equals(Table.MODE_INSERT)) {
    	//	this.insertRecord(_table, instance);
        //} else if (_table.getMode().equals(Table.MODE_UPDATE)) {
        //    this.updateRecord(_table, instance);
        }
        return instance;
    }    
    
    
    private boolean cubeRecordExists(Table _table, RecordInstance _instance) throws DataSaverException {
        int index=0;
        if(_table.getKeyFields().isEmpty()) {
           return false; 
        }
        PreparedStatement select = _table.getSelectStatement();
        for (Iterator i = _table.getKeyFields().iterator(); i.hasNext();) {
            Column fieldDef = (Column) i.next();
            try {
                log.debug("current value is " + _instance);
                FieldAccessor accessor = (FieldAccessor)faCache.get(_instance.getClass(), fieldDef.getFieldName());
                Object value = accessor.getValue(_instance);
                if (value != null) {
                    select.setObject(++index, value);
                } else {
                    select.setNull(++index, fieldDef.getSQLType());
                }
            } catch (Exception e) {
                throw new DataSaverException("Error setting parameter [" + fieldDef.getFieldName() + "] to select data from table [" + _table.getName() + "]", e);
            }
        }
        // executing the select
        ResultSet rs = null;
        RecordInstance instance = null;
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
                if (isSameInstance(_table, _instance, instance)) {
                	break;
                }
                instance = null;
            }
            return (instance != null);
        } catch (Exception e) {
            throw new DataSaverException("Error querying data from table [" + _table.getName() + "]", e);
        } finally {
            try {
                if (rs != null) { rs.close(); }
            } catch (SQLException e) {}
        }
    }

}
