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
package br.com.auster.common.data.definition;


/**
 * @author framos
 * @version $Id: ForeignKeyColumn.java 218 2006-03-28 03:33:43Z framos $
 */
public class ForeignKeyColumn extends Column {

    
    private String fkName;
    private String fkTable;

    
    
    public ForeignKeyColumn(String _fieldName, String _type, String _valueExpr, boolean _key) {
        super(_fieldName, _type, _valueExpr, _key);
    }
    
    
    public final void setForeignKeyName(String _fkName) {
        this.fkName = _fkName;
    }
    
    public final String getForeignKeyName() {
        return this.fkName;
    }
    
    public final void setForeignKeyTable(String _fkTable) {
        this.fkTable = _fkTable;
    }
    
    public final String getForeignKeyTable() {
        return this.fkTable;
    }    
}
