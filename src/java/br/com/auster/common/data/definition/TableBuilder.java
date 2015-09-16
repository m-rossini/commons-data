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
 * Created on 23/03/2006
 */
package br.com.auster.common.data.definition;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;


/**
 * @author framos
 * @version $Id: TableBuilder.java 363 2007-01-11 20:41:39Z framos $
 */
public class TableBuilder {

    
    
    public static final String CONFIG_TABLE_ELEM            = "table";
    public static final String CONFIG_TABLE_NAME_ATTR       = "name";
    public static final String CONFIG_TABLE_DATA_ATTR       = "data-list";
    public static final String CONFIG_TABLE_ITEM_EXPR_ATTR  = "item-expression";
    public static final String CONFIG_TABLE_ITEM_EXPR_UID_ATTR  = "item-expression-uid";
    public static final String CONFIG_TABLE_ITEM_MODE       = "mode";
    public static final String CONFIG_TABLE_ITEM_CACHE      = "cache";
    public static final String CONFIG_FIELD_ELEM            = "field";
    public static final String CONFIG_FK_FIELD_ELEM         = "foreign-key";
    public static final String CONFIG_ID_FIELD_ELEM         = "id";
    public static final String CONFIG_FIELD_NAME_ATTR       = "name";
    public static final String CONFIG_FIELD_VALUE_ATTR      = "value";
    public static final String CONFIG_FIELD_TYPE_ATTR       = "type";
    public static final String CONFIG_FIELD_KEY_ATTR        = "key";
    public static final String CONFIG_FK_FIELD_TABLE_ATTR   = "table";
    public static final String CONFIG_FK_FIELD_NAME_ATTR    = "foreign-name";
    public static final String CONFIG_ID_GENERATOR_ATTR     = "generator-sql";
    
    private static Logger log = LogFactory.getLogger( TableBuilder.class );
    private static TableBuilder singleton;
    

    
    
    protected TableBuilder() {
    }
    
    public static synchronized TableBuilder getInstance() {
        if (singleton == null) {
            singleton = new TableBuilder();
        }
        return singleton;
    }

    
    public Table readTableDefinition(Element _xmlConfiguration) {

        String tableName = DOMUtils.getAttribute(_xmlConfiguration, CONFIG_TABLE_NAME_ATTR, true);
        Table tableDef = buildTableInstance(tableName, _xmlConfiguration);
        tableDef.setMode(DOMUtils.getAttribute(_xmlConfiguration, CONFIG_TABLE_ITEM_MODE, true));
        tableDef.setCache(DOMUtils.getIntAttribute(_xmlConfiguration, CONFIG_TABLE_ITEM_CACHE, false));
        tableDef.setItemExpression(DOMUtils.getAttribute(_xmlConfiguration, CONFIG_TABLE_ITEM_EXPR_ATTR, false));
        tableDef.setItemExpressionUid(DOMUtils.getAttribute(_xmlConfiguration, CONFIG_TABLE_ITEM_EXPR_UID_ATTR, false));
        log.debug("Table '" + tableName + "' successfully configured.");
        return tableDef;
    }

    public Column readFieldDefinition(Table _table, DatabaseMetaData _meta, Element _fieldConfiguration) throws SQLException {
        Column definition = null;
        String fieldName = DOMUtils.getAttribute(_fieldConfiguration, CONFIG_FIELD_NAME_ATTR, true);
        String type = DOMUtils.getAttribute(_fieldConfiguration, CONFIG_FIELD_TYPE_ATTR, true);
        String valueExpr = DOMUtils.getAttribute(_fieldConfiguration, CONFIG_FIELD_VALUE_ATTR, false);        
        valueExpr = ("".equals( valueExpr )) ? null : valueExpr;
        int sqltype = 0;
        ResultSet rs = null;
        try {
            if (_meta.getDatabaseProductName().equalsIgnoreCase("ORACLE")) {
                rs = _meta.getColumns(null, "%", _table.getName().toUpperCase(), fieldName.toUpperCase());
            } else {
                rs = _meta.getColumns(null, "%", _table.getName().toLowerCase(), fieldName.toLowerCase());
            }
            if (rs.next()) {
                sqltype = rs.getInt("DATA_TYPE");
                log.debug("SQL datatype for field " + fieldName + " is " + sqltype);
            }
        } finally {
            if (rs != null) { 
                try { rs.close(); } catch (SQLException sqle) {}
            }
        }
        
        if (CONFIG_FK_FIELD_ELEM.equals(_fieldConfiguration.getLocalName())) {
            definition = new ForeignKeyColumn(fieldName, type, valueExpr, 
                                              DOMUtils.getBooleanAttribute(_fieldConfiguration, CONFIG_FIELD_KEY_ATTR, false));
            ((ForeignKeyColumn)definition).setForeignKeyName(DOMUtils.getAttribute(_fieldConfiguration, CONFIG_FK_FIELD_NAME_ATTR, true));
            ((ForeignKeyColumn)definition).setForeignKeyTable(DOMUtils.getAttribute(_fieldConfiguration, CONFIG_FK_FIELD_TABLE_ATTR, true));
            log.debug("configuring foreign key field '" + fieldName + "' of type " + type + ". This field is key?=" + definition.isKey());
            
        } else if (CONFIG_ID_FIELD_ELEM.equals(_fieldConfiguration.getLocalName())) {
            definition = new IdColumn(fieldName, type, valueExpr, 
                                      DOMUtils.getBooleanAttribute(_fieldConfiguration, CONFIG_FIELD_KEY_ATTR, false));
            ((IdColumn)definition).setGeneratorSQL(DOMUtils.getAttribute(_fieldConfiguration, CONFIG_ID_GENERATOR_ATTR, true));
            log.debug("configuring id field '" + fieldName + "' of type " + type + ". This field is key?=" + definition.isKey());
            
        } else {
            definition = new Column(fieldName, type, valueExpr, 
                                    DOMUtils.getBooleanAttribute(_fieldConfiguration, CONFIG_FIELD_KEY_ATTR, false));
            log.debug("configuring simple field '" + fieldName + "' of type " + type + ". This field is key?=" + definition.isKey());
        }
        definition.setSQLType(sqltype);
        return definition;
    }    
    
    public void createStatements(Connection _conn, Table _table) throws SQLException {
        String sql = this.createInsertStatement(_table);
        _table.setInsertStatement(_conn.prepareStatement(sql));
        sql = this.createUpdateStatement(_table);
        _table.setUpdateStatement(_conn.prepareStatement(sql));
        sql = this.createSelectStatement(_table);
        _table.setSelectStatement(_conn.prepareStatement(sql));
        _table.setIdStatement(_conn.createStatement());
    }
    
    protected Table buildTableInstance(String _name, Element _configuration) {
        return new Table(_name, DOMUtils.getAttribute(_configuration, CONFIG_TABLE_DATA_ATTR, false));    
    }
    
    protected String createInsertStatement(Table _table) {
        StringBuilder buf = new StringBuilder();
        buf.append( "INSERT INTO " );
        buf.append( _table.getName() );
        buf.append( "(" );

        int counter = 0;
        int size = _table.getFields().size();
        for ( Iterator i = _table.getFields().iterator(); i.hasNext(); ) {
            Column field = (Column) i.next();
            buf.append( field.getFieldName() );
            if ( (++counter) < size ) {
                buf.append( "," );
            }
        }
        buf.append( ") VALUES(" );
        for ( int i = 0; i < (size -1); i++ ) {
            buf.append( "?, " );
        }
        buf.append( "?)" );
        log.debug("created insert statement for table " + _table.getName() + ": " + buf.toString());
        return buf.toString();
    }

    protected String createSelectStatement(Table _table) {
        StringBuilder buf = new StringBuilder();
        buf.append( "SELECT * FROM " );
        buf.append( _table.getName() );
        buf.append( " WHERE " );
        int counter = 0;
        int size = _table.getKeyFields().size();
        for ( Iterator i = _table.getKeyFields().iterator(); i.hasNext(); ) {
            Column field = (Column) i.next();
            buf.append( "( " );
            buf.append( field.getFieldName() );
            buf.append( " = ? OR " );
            buf.append( field.getFieldName() );
            buf.append( " IS NULL ) " );
            if ( (++counter) < size ) {
                buf.append( " AND " );
            }
        }
        buf.append( " ORDER BY " );
        counter = 0;
        for ( Iterator i = _table.getKeyFields().iterator(); i.hasNext(); ) {
        	Column field = (Column) i.next();
        	buf.append( field.getFieldName() );
            if ( (++counter) < size ) {
                buf.append( ", " );
            }
        }
        log.debug("created select statement for table " + _table.getName() + ": " + buf.toString());
        return buf.toString();
    }

    protected String createUpdateStatement(Table _table) {
        StringBuilder buf = new StringBuilder();
        buf.append( "UPDATE " );
        buf.append( _table.getName() );
        buf.append( " SET " );

        int counter = 0;
        int size = _table.getNonKeyFields().size();
        for ( Iterator i = _table.getNonKeyFields().iterator(); i.hasNext(); ) {
            Column field = (Column) i.next();
            counter++;
            buf.append( field.getFieldName() );
            buf.append( " = ?" );
            if ( counter < size ) {
                buf.append( ", " );
            }
        }

        buf.append( " WHERE " );
        counter = 0;
        size = _table.getKeyFields().size();
        for ( Iterator i = _table.getKeyFields().iterator(); i.hasNext(); ) {
            Column field = (Column) i.next();
            buf.append( field.getFieldName() );
            buf.append( " = ? " );
            if ( (++counter) < size ) {
                buf.append( " AND " );
            }
        }
        log.debug("created update statement for table " + _table.getName() + ": " + buf.toString());
        return buf.toString();
    }
    
}
