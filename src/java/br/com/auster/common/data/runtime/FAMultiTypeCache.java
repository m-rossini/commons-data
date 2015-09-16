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
package br.com.auster.common.data.runtime;

import org.apache.log4j.Logger;

import br.com.auster.common.asm.FieldAccessor;
import br.com.auster.common.asm.FieldAccessorBuilder;
import br.com.auster.common.log.LogFactory;


/**
 * @author framos
 * @version $Id: FAMultiTypeCache.java 218 2006-03-28 03:33:43Z framos $
 */
public class FAMultiTypeCache extends MultiTypeCache {

    
    private static Logger log = LogFactory.getLogger(FAMultiTypeCache.class);
    
    private static final int INTERNAL_CACHE = 20;
    
    FieldAccessorBuilder builder = new FieldAccessorBuilder();
    
    
    /**
     * @see br.com.auster.common.data.runtime.MultiTypeCache#get(java.lang.Object, java.lang.Object)
     */
    public Object get(Object _klass, Object _expression) {
        
        if ((_klass == null) || (!(_klass instanceof Class))) {
            throw new IllegalArgumentException("cannot cache field accessors without the correct Class reference: " + _klass);
        }
        if ((_expression != null) && (!(_expression instanceof String))) {
            throw new IllegalArgumentException("cannot cache field accessors without the correct String expression: " + _expression);
        }
        // initializing field-accessor cache on the fly
        if (!this.cache.containsKey(_klass)) { this.initCache(_klass, INTERNAL_CACHE); }
        
        FieldAccessor acc = (FieldAccessor) super.get(_klass, _expression);
        if (acc == null) {
            log.debug("field accessor for class " + _klass + " and expression " + _expression + " not found... building it now.");
            try {
                Class accClass = builder.buildAndLoadFieldAccessor((Class)_klass, (String)_expression);
                acc = (FieldAccessor) accClass.newInstance();
                this.put(_klass, _expression, acc);
            } catch (Exception e) {
                log.debug("could not build field accessor for (" + _klass + "/" + _expression + ") : " + e.getMessage());
            }
        } else {
            log.debug("found field accessor in cache");
        }
        return acc;
    }
}
