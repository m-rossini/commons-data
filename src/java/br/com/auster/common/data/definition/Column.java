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

import java.sql.Types;

import br.com.auster.common.asm.FieldDefinition;


public class Column {
	
	
    protected String  fieldName;
    protected String  valueExpression;
    protected String  type;
    protected int sqltype;
    protected boolean key;
    
    protected FieldDefinition fieldDef;
    
    

    public Column(String _fieldName, String _type, String _valueExpr, boolean _key) {
        this.fieldName = _fieldName;
        this.valueExpression = _valueExpr;
        this.type = _type;
        this.key = _key;
        fieldDef = new br.com.auster.common.asm.FieldDefinition(_fieldName, _type, _key);
    }

 
    public final String getValueExpression() {
        return valueExpression;
    }

//    public final void setValueExpression(String dataName) {
//        this.valueExpression = dataName;
//    }
//
    public final String getFieldName() {
        return fieldName;
    }
//
//    public final void setFieldName(String fieldName) {
//        this.fieldName = fieldName;
//    }

    public final String getType() {
    	return this.type;
    }
//    
//    public final void setType(String _type) {
//    	this.type = _type;
//    }
    
    public final FieldDefinition getFieldDefinition() {
    	return this.fieldDef;
    }
 
    public final int getSQLType() {
        if (this.type.equals("int") || this.type.equals("long")) {
            return Types.INTEGER;
        } else if (this.type.equals("double")) {
            return Types.DOUBLE;
        } else {
            return this.sqltype;
        }
    }
    
    public final void setSQLType(int _sqltype) {
        this.sqltype = _sqltype;
    }
    
    public final boolean isKey() {
        return this.key;
    }
}