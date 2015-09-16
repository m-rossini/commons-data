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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


/**
 * @author framos
 * @version $Id: CubeTable.java 293 2006-08-28 14:51:18Z framos $
 */
public class CubeTable extends Table {

    
    
    private Map<String, Table> dimensions;
    
    
    public CubeTable(String _name, String _dataList) {
        super(_name, _dataList);
        this.setCache(0);
        dimensions = new HashMap<String, Table>();
    }
    
    /**
     * @see br.com.auster.common.data.definition.Table#getCache()
     */
    public int getCache() { return 0; }
    
    public void addDimension(Table _dimension) {
        dimensions.put(_dimension.getName(), _dimension);
    }
    
    public Collection getDimensions() {
        return dimensions.values();
    }
}
