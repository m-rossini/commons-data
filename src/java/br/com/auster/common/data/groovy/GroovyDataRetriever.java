/*
 * Copyright (c) 2004-2005 Auster Solutions do Brasil. All Rights Reserved.
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
 * Created on 02/12/2005
 */
package br.com.auster.common.data.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.data.DataRetriever;
import br.com.auster.common.data.DataRetrieverException;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * <p><b>Title:</b> GroovyDataRetriever</p>
 * <p><b>Description:</b> A data retriever that process Groovy scripts to infer values 
 * and return them.</p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: GroovyDataRetriever.java 317 2006-09-14 15:35:58Z framos $
 * 
 * Configuration sample is:
 * 
 * <groovy-data-retriever-config id="retriever1">
 *     <data name="info1">
 *         //groovy script
 *     </data>
 *     <data name="info2">
 *         // groovy script
 *     </data>
 * </groovy-data-retriever-config>
 * 
 */
public class GroovyDataRetriever implements DataRetriever {
  public static final String CONFIG_ROOT_ELEM      = "groovy-data-retriever-config";
  public static final String CONFIG_ROOT_ID_ELEM   = "id";
  public static final String CONFIG_DATA_ELEM      = "data";
  public static final String CONFIG_DATA_NAME_ATTR = "name";
  
  // to synch GroovyShell avoiding the concurrency bug 
  private static Object lock = new Object();
  
  private static Logger log = LogFactory.getLogger(GroovyDataRetriever.class);
  private String id = "";
  private List                   dataList = new ArrayList();
  private Map                    scripts  = new HashMap();

  /**
   * @inheritDoc
   * 
   * @throws CompilationFailedException 
   */
  public void configure(Element config) throws DataRetrieverException {
    log.debug("Configuring GroovyDataRetriever...");
    
    if(!CONFIG_ROOT_ELEM.equals(config.getLocalName())) {
      config = DOMUtils.getElement(config, CONFIG_ROOT_ELEM, true);
    }
    id = DOMUtils.getAttribute(config, CONFIG_ROOT_ID_ELEM, true);

    GroovyShell shell = null;
    // just making sure no concurrency bug will show up when creating the shell
    synchronized (lock) {
      shell = new GroovyShell();
    }
    
    NodeList infos = DOMUtils.getElements(config, CONFIG_DATA_ELEM);
    int length = infos.getLength();
    for(int i=0; i<length; i++) {
      Element info = (Element) infos.item(i);
      String name = DOMUtils.getAttribute(info, CONFIG_DATA_NAME_ATTR, true);
      String scriptStr = DOMUtils.getText(info).toString();
      
      log.debug("Found data name=["+name+"] script=["+scriptStr+"]");
      
      try {
        Script script = null;
        // necessary to avoid concurrency bug when compiling the script
        synchronized (lock) {
          script = shell.parse(scriptStr);
        }

        dataList.add(name);
        scripts.put(name, script);
        
      } catch (CompilationFailedException cfe) {
        throw new DataRetrieverException("Exception compiling script for info "+name, cfe);
      }
      log.debug("Info ["+name+"] successfully configured.");
    }
    
    log.debug("GroovyDataRetriever ["+id+"] successfully configured to retrieve ["+
        dataList.size()+"] data pieces.");
  }

  /**
   * @inheritDoc
   */
  public Map retrieve(Map inputData) {
    Map input   = new HashMap(inputData);
    Map results = new HashMap(); 
    for(Iterator i = dataList.iterator(); i.hasNext(); ) {
      String data   = (String) i.next();
      Script script = (Script) scripts.get(data);
      for(Iterator j = input.entrySet().iterator(); j.hasNext(); ) {
        Map.Entry entry = (Map.Entry) j.next();
        script.getBinding().setVariable(((String) entry.getKey()).replaceAll( "-", "_" ), 
        		                        entry.getValue());
      }
      results.put(data, script.run());
      input.put(data, results.get(data));
      
      script.setBinding(new Binding());
    }
    return results;
  }

}
