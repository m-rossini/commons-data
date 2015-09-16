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
 * Created on 01/03/2006
 */

package br.com.auster.common.data.definition.dm;

import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.auster.common.asm.ClassDefinition;
import br.com.auster.common.asm.FieldDefinition;
import br.com.auster.common.data.AggregationException;
import br.com.auster.common.data.runtime.dm.Fact;

public class FactDefinition extends ClassDefinition {
    String                           name       = null;

    SourceDefinition                 source     = null;

    Map<String, DimensionDefinition> dimensions = new LinkedHashMap<String, DimensionDefinition>();
    Map<String, FieldDefinition>     dimFields  = new LinkedHashMap<String, FieldDefinition>();

    public FactDefinition(String name,
                          String className) throws IntrospectionException {
        super( className,
               null,
               new String[]{Fact.class.getName()} );
        this.name = name;
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Returns the dimensions as an Unmodifiable collection
     */
    public Collection<DimensionDefinition> getDimensions() {
        return Collections.unmodifiableCollection( dimensions.values() );
    }

    /**
     * @param dimensions The dimension to add to dimensions list
     * @throws AggregationException 
     */
    public void addDimension(DimensionDefinition dimension) throws AggregationException {
        FieldDefinition fieldDef = new FieldDefinition( dimension.getName(),
                                                       dimension.getClassName() );
        this.dimensions.put( dimension.getName(),
                             dimension );
        this.dimFields.put( dimension.getName(), fieldDef);
        this.addField( fieldDef );
    }
    
    public FieldDefinition getDimensionField(String name) {
        return this.dimFields.get(name);
    }

    public void addAttribute(AttributeDefinition attrDef) {
        this.addField( attrDef );
    }

    public Collection<AttributeDefinition> getAttributes() {
        Collection<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>();
        for ( FieldDefinition fieldDef : this.getFields() ) {
            if(fieldDef instanceof AttributeDefinition) {
                attrs.add( (AttributeDefinition) fieldDef );
            }
        }
        return attrs;
    }

    public void setSource(SourceDefinition source) {
        this.source = source;
    }

    public SourceDefinition getSource() {
        return this.source;
    }

}