/*
 * Copyright (c) 2004-2006 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 13/03/2006
 */

package br.com.auster.common.data.definition;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.auster.common.asm.ClassDefinition;

public class Table {
	
	
    public static final String MODE_INSERT = "add";
    public static final String MODE_UPDATE = "update";
    public static final String MODE_IGNORE = "ignore";
    

    private String  name;
    private String dataList;
    private String mode;
    private int cache;
    private String itemExpr;
    private String itemExprUid;
    
    private ClassDefinition classDef;
    
    private PreparedStatement insertStatement;
    private PreparedStatement selectStatement;
    private PreparedStatement updateStatement;
    private Statement idStatement;
    
    private List fields;
    private List keyFields;
    private List nonKeyFields;
    private IdColumn idField;
    
    
    public Table(String name, String dataList) {
        this.name = name;
        this.dataList = ((dataList == null) || (dataList.equals( "" ))) ? null : dataList;

        this.fields = new ArrayList();
        this.keyFields = new ArrayList();
        this.nonKeyFields = new ArrayList();
        // TODO is this really necessary????
       	classDef = new ClassDefinition(getInstanceClassname());
    }

    public final String getInstanceClassname() {
    	
    	String instanceName = this.getClass().getPackage().getName() + "." +
    						  this.name.substring(0, 1).toUpperCase() + 
    	                      this.name.substring(1) + 
    	                      "Instance";
    	return instanceName;
    }

    public final ClassDefinition getClassDefinition() {
    	return getClassDefinition(null, null);
    }
    
    public final ClassDefinition getClassDefinition(String _superClass, String[] _interfaces) {
       	classDef = new ClassDefinition(getInstanceClassname(), _superClass, _interfaces);
       	for (Iterator it=this.getFields().iterator(); it.hasNext();) {
       		Column fd = (Column) it.next();
       		classDef.addField(fd.getFieldDefinition());
       	}
        return classDef;
    }
    
    public final String getDataList() {
        return dataList;
    }

    public final void setDataList(String dataList) {
        this.dataList = dataList;
    }

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final PreparedStatement getInsertStatement() {
        return insertStatement;
    }

    public final void setInsertStatement(PreparedStatement statement) {
        this.insertStatement = statement;
    }

    public final PreparedStatement getUpdateStatement() {
        return updateStatement;
    }

    public final void setUpdateStatement(PreparedStatement statement) {
        this.updateStatement = statement;
    }

    public final PreparedStatement getSelectStatement() {
        return selectStatement;
    }

    public final void setSelectStatement(PreparedStatement statement) {
        this.selectStatement = statement;
    }
    
    public final void setIdStatement(Statement _stmt) {
        this.idStatement = _stmt;
    }
    
    public final Statement getIdStatement() { 
        return this.idStatement;
    }

    public final void addField(Column field) {
        this.fields.add(field);
        if (field.isKey()) {
            this.keyFields.add(field);
        } else if (field instanceof IdColumn) {
        	this.idField = (IdColumn)field;
        } else { 
            this.nonKeyFields.add(field);
        }
    }

    public List getFields() {
        return fields;
    }

    public List getKeyFields() {
        return this.keyFields;
    }

    public IdColumn getIdField() {
    	return this.idField;
    }
    
    public int getCache() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public final List getNonKeyFields() {
        return nonKeyFields;
    }

    public String getItemExpression() {
    	return this.itemExpr;
    }
    
    public void setItemExpression(String _expr) {
    	if ((_expr != null) && (_expr.trim().length() <= 0)) {
    		this.itemExpr = null;
    	} else {
    		this.itemExpr = _expr;
    	}
    }
    
    public String getItemExpressionUid() {
    	return this.itemExprUid;
    }
    
    public void setItemExpressionUid(String _expr) {
    	if ((_expr != null) && (_expr.trim().length() <= 0)) {
    		this.itemExprUid = null;
    	} else {
    		this.itemExprUid = _expr;
    	}
    }
    
    public String toString() {
        return "TableDef("+this.name+": "+this.getKeyFields()+")";
    }

}