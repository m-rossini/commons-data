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

import br.com.auster.common.asm.ClassDefinition;
import br.com.auster.common.asm.FieldDefinition;
import br.com.auster.common.data.runtime.dm.Dimension;

public class DimensionDefinition extends ClassDefinition {
    private String                           name         = null;

    public DimensionDefinition(String name,
                               String className) throws IntrospectionException {
        super(className, null, new String[] { Dimension.class.getName() } );
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

    public void addAttribute(AttributeDefinition attrDef) {
        this.addField(attrDef);
    }
    
    public Collection<AttributeDefinition> getAttributes() {
        Collection<AttributeDefinition> attrs = new ArrayList<AttributeDefinition>();
        for(FieldDefinition fieldDef : this.getFields()) {
            attrs.add((AttributeDefinition)fieldDef);
        }
        return attrs;
    }

}