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
 * Created on 20/03/2006
 */
package br.com.auster.common.data.runtime;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.WeakHashMap;

import br.com.auster.common.asm.ClassBuilder;
import br.com.auster.common.asm.ClassDefinition;
import br.com.auster.common.asm.FieldAccessor;
import br.com.auster.common.data.DataSaverException;
import br.com.auster.common.data.definition.Column;
import br.com.auster.common.data.definition.ForeignKeyColumn;
import br.com.auster.common.data.definition.Table;


/**
 * @author framos
 * @version $Id: RecordInstanceFactory.java 218 2006-03-28 03:33:43Z framos $
 */
public final class RecordInstanceFactory {


	private ClassBuilder classBuilder;
	private WeakHashMap instanceCache;

    
	public RecordInstanceFactory() {
		classBuilder = new ClassBuilder();
		instanceCache = new WeakHashMap();
	}
	
	
	public final RecordInstance createInstance(Table _definition) 
		throws DataSaverException {

		try {
			Class instanceClass = buildTableDefinitionInstanceClass(_definition);
			RecordInstance instance = (RecordInstance) instanceClass.newInstance();
			return instance;
		} catch (Exception e) {
			throw new DataSaverException("Could not instantiate table definition object", e);
		}
	}
	
	
	public final void setFields(Table _definition, RecordInstance _instance, Object _values, 
                                MultiTypeCache _tdiCache, MultiTypeCache _faCache)  
		throws DataSaverException {

		try {
			for (Iterator it=_definition.getFields().iterator(); it.hasNext();) {
				Column fieldDefinition = (Column) it.next();
				FieldAccessor fieldAccessor = (FieldAccessor) _faCache.get(_values.getClass(), fieldDefinition.getValueExpression());
				Object currentValue = null;
				if (fieldAccessor != null) {
					currentValue = fieldAccessor.getValue(_values);
				}
				if (fieldDefinition instanceof ForeignKeyColumn) {
					currentValue = _tdiCache.get(((ForeignKeyColumn)fieldDefinition).getForeignKeyTable(), currentValue);
                    if (currentValue == null) {
                        throw new IllegalStateException("Foreign key for " + fieldDefinition.getFieldName() + " must be in cache.");
                    }
					fieldAccessor = (FieldAccessor) _faCache.get(currentValue.getClass(), ((ForeignKeyColumn)fieldDefinition).getForeignKeyName());
					currentValue = fieldAccessor.getValue(currentValue);
					if (currentValue == null) {
						throw new IllegalStateException("Foreign key for " + fieldDefinition.getFieldName() + " must be in cache.");
					}
				}
                if (currentValue != null) {
                    fieldDefinition.getFieldDefinition().setValue(_instance, currentValue);
                }
			}
		} catch (Exception e) {
			throw new DataSaverException("Could not set all values to table instance", e);
		}
	}
	
	
	private final Class buildTableDefinitionInstanceClass(Table _definition) throws SecurityException, IllegalArgumentException, IOException, IntrospectionException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
		Class klass = (Class) instanceCache.get(_definition);
		if (klass == null) {
			ClassDefinition def = _definition.getClassDefinition(null, new String[] {"br.com.auster.common.data.runtime.RecordInstance"} );
			def.addField(RecordInstance.NEWINSTANCE_FIELDDEFINITION);
			klass = classBuilder.buildAndLoadClass(def);
			instanceCache.put(_definition, klass);
		}
		return klass;
	}
}
