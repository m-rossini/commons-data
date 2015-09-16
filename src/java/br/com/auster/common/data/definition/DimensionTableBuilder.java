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

import org.w3c.dom.Element;

import br.com.auster.common.xml.DOMUtils;


/**
 * @author framos
 * @version $Id: DimensionTableBuilder.java 218 2006-03-28 03:33:43Z framos $
 */
public class DimensionTableBuilder extends TableBuilder {

    
    
    public static final String CONFIG_TABLE_EXPORTTO_ATTR = "export-to";
    
    private static TableBuilder singleton;
    
    
    public static synchronized TableBuilder getInstance() {
        if (singleton == null) {
            singleton = new DimensionTableBuilder();
        }
        return singleton;
    }    
    
    
    /**
     * @see br.com.auster.common.data.definition.TableBuilder#buildTableInstance(java.lang.String, org.w3c.dom.Element)
     */
    protected Table buildTableInstance(String _name, Element _configuration) {
        return new DimensionTable(_name, null, DOMUtils.getAttribute(_configuration, CONFIG_TABLE_EXPORTTO_ATTR, true));
    }
    
}
