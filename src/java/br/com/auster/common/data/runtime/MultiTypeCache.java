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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.map.LRUMap;
import org.apache.log4j.Logger;

import br.com.auster.common.log.LogFactory;

/**
 * @author framos
 * @version $Id: MultiTypeCache.java 218 2006-03-28 03:33:43Z framos $
 */
public class MultiTypeCache {

    private static Logger log = LogFactory.getLogger(MultiTypeCache.class);

    protected Map           cache;
    protected Map           cacheCounter;

    public MultiTypeCache() {
        this.cache = new HashMap();
        this.cacheCounter = new HashMap();
    }

    public void initCache(Object _outterKey, int _size) {
        if (_size <= 0) {
            log.debug("cache for outter key " + _outterKey + " not initialized. Size must be bigger than zero");
            return;
        }
        if (!cache.containsKey(_outterKey)) {
            log.debug("initializing cache for outter key " + _outterKey);
            cache.put(_outterKey, new LRUMap(_size));
            cacheCounter.put(_outterKey, new CacheHitCounter());
        }
    }

    public Object get(Object _outterKey, Object _innerkey) {
        LRUMap map = (LRUMap)cache.get(_outterKey);        
        if (map == null) {
            log.debug("no cache defined for " + _outterKey);
            return null;
        }
        CacheHitCounter c = (CacheHitCounter)cacheCounter.get(_outterKey);
        c.times++;
        if (map.containsKey(_innerkey)) { c.hits++; } 
        log.debug("cache for " + _outterKey + " found. Returning cached value for key " + _innerkey);
        return map.get(_innerkey);        
    }

    public void put(Object _outterKey, Object _innerkey, Object _value) {
        LRUMap map = (LRUMap)cache.get(_outterKey);
        if (map != null) {
            map.put(_innerkey, _value);
            log.debug("Key " + _innerkey + " added to " + _outterKey + " cache");
        } else {
            log.debug("cannot put " + _innerkey + " into cache, since there is none defined for " + _outterKey);
        }
    }

    public final String dump() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator it=cache.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            buffer.append("MASTER_KEY [" + entry.getKey() + "]\nCACHED_VALUES [\n");
            LRUMap cacheMap = (LRUMap)entry.getValue();
            if (cacheMap != null) {
                for (Iterator it2=cacheMap.entrySet().iterator(); it2.hasNext();) {
                    Map.Entry localEntry = (Map.Entry) it2.next();
                    buffer.append(" key{" + localEntry.getKey() + "} ==> value {" + localEntry.getValue() + "},\n");
                }
            } else {
                buffer.append(" NULL ");
            }
            buffer.append("]\n");
        }
        return buffer.toString();
    }

    
    public final String dumpHits() {
        StringBuffer buffer = new StringBuffer();
        DecimalFormat formatter = new DecimalFormat("0.####");
        for (Iterator it=cacheCounter.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            CacheHitCounter c = (CacheHitCounter) entry.getValue();
            buffer.append("[");
            buffer.append(entry.getKey());
            buffer.append("=");
            if (c.times > 0) {
                buffer.append(formatter.format(c.hits/c.times));
                buffer.append(" of ");
                buffer.append((int)c.times);
            } else {
                buffer.append("not-used");
            }
            buffer.append("], ");
        }
        return buffer.toString();
    }    
    
    protected static class CacheHitCounter {
        public double times;
        public double hits;
    }    
}
