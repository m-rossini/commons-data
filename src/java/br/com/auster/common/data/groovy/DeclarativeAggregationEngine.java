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
 * Created on 06/02/2006
 */

package br.com.auster.common.data.groovy;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import br.com.auster.common.asm.ClassBuilder;
import br.com.auster.common.asm.FieldDefinition;
import br.com.auster.common.data.AggregationEngine;
import br.com.auster.common.data.AggregationException;
import br.com.auster.common.data.definition.dm.AttributeDefinition;
import br.com.auster.common.data.definition.dm.DimensionDefinition;
import br.com.auster.common.data.definition.dm.FactDefinition;
import br.com.auster.common.data.definition.dm.SourceDefinition;
import br.com.auster.common.data.runtime.dm.Dimension;
import br.com.auster.common.data.runtime.dm.Fact;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;


/**
 * <p><b>Title:</b> DeclarativeAggregationEngine</p>
 * <p><b>Description:</b> An implementation for AggregationEngine interface that
 * uses declarative scripts to create facts and their related dimensions</p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: DeclarativeAggregationEngine.java 126 2006-03-23 21:12:21Z framos $
 */
public class DeclarativeAggregationEngine
    implements
    AggregationEngine {
    public static final String   CONFIG_ROOT_ELEM        = "aggregation-config";
    public static final String   CONFIG_ROOT_ID_ELEM     = "id";
    public static final String   CONFIG_FACT_ELEM        = "fact";
    public static final String   CONFIG_FACT_ATTR_NAME   = "name";
    public static final String   CONFIG_FACT_ATTR_CLASS  = "class-name";
    public static final String   CONFIG_SOURCE_ELEM      = "source";
    public static final String   CONFIG_SOURCE_ATTR_NAME = "name";
    public static final String   CONFIG_ATTR_ELEM        = "attribute";
    public static final String   CONFIG_ATTR_ATTR_NAME   = "name";
    public static final String   CONFIG_ATTR_ATTR_TYPE   = "type";
    public static final String   CONFIG_ATTR_KEY_TYPE    = "key";
    public static final String   CONFIG_DIMS_ELEM        = "dimension";
    public static final String   CONFIG_DIMS_ATTR_NAME   = "name";
    public static final String   CONFIG_DIMS_ATTR_CLASS  = "class-name";

    // to avoid concurrent script parsing
    private static Lock          lock                    = new ReentrantLock();

    private static Logger        log                     = LogFactory.getLogger( DeclarativeAggregationEngine.class );
    private String               id                      = "";

    private List<FactDefinition> factList                = new ArrayList<FactDefinition>();

    /**
     * Configures this DeclarativeAggregationEngine
     * 
     * The configuration is a DOM Element representation of the following template:
     * 
     * <fact name="invoiceFact" class-name="br.com.auster.om.invoice.dm.model.InvoiceFact">
     *   <source name="invoice">
     *      account.invoices
     *   </source>
     *   <attribute name="dueDate">
     *      invoice.dueDate
     *   </attribute>
     *   ...
     *   <dimension name="timeDimension" class-name="br.com.auster.om.invoice.dm.model.InvoiceFact">
     *     <attribute name="year">
     *       invoice.cycleEndDate.year
     *     </attribute>
     *     ...
     *   </dimension>
     *   ...
     * </fact>
     * 
     */
    public void configure(Element config) throws AggregationException {
        log.debug( "Configuring DeclarativeAggregationEngine..." );

        if ( !CONFIG_ROOT_ELEM.equals( config.getLocalName() ) ) {
            config = DOMUtils.getElement( config,
                                          CONFIG_ROOT_ELEM,
                                          true );
        }
        id = DOMUtils.getAttribute( config,
                                    CONFIG_ROOT_ID_ELEM,
                                    true );

        GroovyShell shell = new GroovyShell();
        ClassBuilder builder = new ClassBuilder();

        NodeList facts = DOMUtils.getElements( config,
                                               CONFIG_FACT_ELEM );
        int factCount = facts.getLength();
        for ( int i = 0; i < factCount; i++ ) {
            try {
                Element fact = (Element) facts.item( i );
                String name = DOMUtils.getAttribute( fact,
                                                     CONFIG_FACT_ATTR_NAME,
                                                     true );
                String className = DOMUtils.getAttribute( fact,
                                                          CONFIG_FACT_ATTR_CLASS,
                                                          true );

                FactDefinition factDef = new FactDefinition( name,
                                                             className );
                log.debug( "Found fact name=[" + name + "]" );

                Element source = DOMUtils.getElement( fact,
                                                      CONFIG_SOURCE_ELEM,
                                                      true );
                String sourceName = DOMUtils.getAttribute( source,
                                                           CONFIG_SOURCE_ATTR_NAME,
                                                           true );
                String sourceText = DOMUtils.getText( source ).toString();
                Script sourceScript = null;
                lock.lock();
                try {
                    sourceScript = shell.parse( sourceText );
                } catch (CompilationFailedException ex) {
                    log.error("Error compiling source script: "+sourceName);
                    throw ex;
                } finally {
                    lock.unlock();
                }
                SourceDefinition sourceDef = new SourceDefinition( sourceName,
                                                                   sourceText,
                                                                   sourceScript );
                factDef.setSource( sourceDef );

                // criar attributos
                NodeList attrs = DOMUtils.getElements( fact,
                                                       CONFIG_ATTR_ELEM );
                int attrsCount = attrs.getLength();
                for ( int j = 0; j < attrsCount; j++ ) {
                    Element attr = (Element) attrs.item( j );
                    AttributeDefinition attrDef = parseAttributeDef( shell,
                                                                     attr );
                    factDef.addAttribute( attrDef );
                }

                // criar dimensões
                NodeList dims = DOMUtils.getElements( fact,
                                                      CONFIG_DIMS_ELEM );
                int dimsCount = dims.getLength();
                for ( int j = 0; j < dimsCount; j++ ) {
                    Element dim = (Element) dims.item( j );

                    String dimName = DOMUtils.getAttribute( dim,
                                                            CONFIG_DIMS_ATTR_NAME,
                                                            true );
                    String dimClassName = DOMUtils.getAttribute( dim,
                                                                 CONFIG_DIMS_ATTR_CLASS,
                                                                 true );

                    DimensionDefinition dimDef = new DimensionDefinition( dimName,
                                                                          dimClassName );

                    // criar attributos
                    attrs = DOMUtils.getElements( dim,
                                                  CONFIG_ATTR_ELEM );
                    attrsCount = attrs.getLength();
                    for ( int k = 0; k < attrsCount; k++ ) {
                        Element attr = (Element) attrs.item( k );
                        AttributeDefinition attrDef = parseAttributeDef( shell,
                                                                         attr );
                        dimDef.addAttribute( attrDef );
                    }

                    builder.buildAndLoadClass( dimDef );
                    factDef.addDimension( dimDef );
                }

                builder.buildAndLoadClass( factDef );
                factList.add( factDef );
            } catch ( CompilationFailedException e ) {
                log.error( "Error compiling Groovy script",
                           e );
                throw new AggregationException( "Error compiling Groovy script",
                                                e );
            } catch ( IntrospectionException e ) {
                log.error( "Error instantiating configured class definitions",
                           e );
                throw new AggregationException( "Error instantiating configured classes definitions",
                                                e );
            } catch ( IOException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( SecurityException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( IllegalArgumentException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( ClassNotFoundException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( NoSuchMethodException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( IllegalAccessException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( InvocationTargetException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( InstantiationException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } catch ( NoSuchFieldException e ) {
                log.error( "Error defining and loading configured class definitions",
                           e );
                throw new AggregationException( "Error defining and loading configured class definitions",
                                                e );
            } finally {
            }
        }
        log.debug( "DeclarativeAggregationEngine [" + id + "] successfully configured to aggregate [" + factList.size() + "] distinct facts." );
    }

    /**
     * Parses the attribute element and creates an AttributeDefinition
     * 
     * @param shell
     * @param attr
     * @return
     * @throws CompilationFailedException
     */
    private AttributeDefinition parseAttributeDef(GroovyShell shell,
                                                  Element attr) throws CompilationFailedException {
        String attrName = DOMUtils.getAttribute( attr,
                                                 CONFIG_ATTR_ATTR_NAME,
                                                 true );
        String attrType = DOMUtils.getAttribute( attr,
                                                 CONFIG_ATTR_ATTR_TYPE,
                                                 true );
        boolean isKey = DOMUtils.getBooleanAttribute( attr, CONFIG_ATTR_KEY_TYPE, false );

        String scriptText = DOMUtils.getText( attr ).toString();

        Script script = null;
        AttributeDefinition attrDef = null;

        lock.lock();
        try {
            script = shell.parse( scriptText );
            attrDef = new AttributeDefinition( attrName,
                                               scriptText,
                                               script,
                                               attrType );
            attrDef.setKey(isKey);
        } finally {
            lock.unlock();
        }
        return attrDef;
    }

    /**
     * @inheritDoc
     */
    public Map aggregate(Map inputData) throws AggregationException {
        Map results = new HashMap();
        try {
            // for each configured fact, create a list of instances
            for ( FactDefinition factDef : factList ) {

                // get the source anchor for facts, running a script that shall return 
                // a single object or a list of them
                SourceDefinition source = factDef.getSource();
                Script srcScript = source.getScript();
                for ( Object objEntry : inputData.entrySet() ) {
                    Map.Entry entry = (Map.Entry) objEntry;
                    srcScript.getBinding().setVariable( ((String) entry.getKey()).replaceAll( "-",
                                                                                              "_" ),
                                                        entry.getValue() );
                }
                
                Object targetSource = null;
                try {
                    targetSource = srcScript.run();
                } catch (Exception ex) {
                    log.error("Error running script: "+source.getName());
                    throw ex;
                }
                srcScript.setBinding( new Binding() );

                // map result to a list
                Collection targetList = null;
                if ( targetSource instanceof Collection ) {
                    targetList = (Collection) targetSource;
                } else {
                    targetList = Collections.singletonList( targetSource );
                }

                // creates the list to store aggregated facts and iterate 
                // over targets, creating the fact instances
                List factList = new ArrayList();
                for ( Object target : targetList ) {
                    Fact fact = (Fact) factDef.getDefinedClass().newInstance();

                    // for each defined attribute, calculate and populate it
                    for ( AttributeDefinition attrDef : factDef.getAttributes() ) {
                        Script attrScript = attrDef.getScript();
                        for ( Object objEntry : inputData.entrySet() ) {
                            Map.Entry entry = (Map.Entry) objEntry;
                            attrScript.getBinding().setVariable( (String) entry.getKey(),
                                                                 entry.getValue() );
                        }
                        attrScript.getBinding().setVariable( source.getName(),
                                                             target );
                        Object result = attrScript.run();
                        attrDef.setValue( fact,
                                          result );
                        attrScript.setBinding( new Binding() );
                    }

                    // for each defined dimension, instantiate, calculate and populate it
                    for ( DimensionDefinition dimDef : factDef.getDimensions() ) {
                        Dimension dim = (Dimension) dimDef.getDefinedClass().newInstance();
                        for ( AttributeDefinition dimAttrDef : dimDef.getAttributes() ) {
                            Script dimAttrScript = dimAttrDef.getScript();
                            for ( Object objEntry : inputData.entrySet() ) {
                                Map.Entry entry = (Map.Entry) objEntry;
                                dimAttrScript.getBinding().setVariable( (String) entry.getKey(),
                                                                        entry.getValue() );
                            }
                            dimAttrScript.getBinding().setVariable( source.getName(),
                                                                    target );
                            dimAttrScript.getBinding().setVariable( factDef.getName(),
                                                                    fact );

                            Object result = dimAttrScript.run();
                            dimAttrDef.setValue( dim,
                                                 result );
                            dimAttrScript.setBinding( new Binding() );
                        }
                        FieldDefinition dimField = factDef.getDimensionField( dimDef.getName() );
                        dimField.setValue( fact,
                                           dim );
                    }

                    // add aggregated fact to the fact list
                    factList.add( fact );
                }
                results.put( factDef.getName() + "-list",
                             factList );
            }
        } catch ( InstantiationException e ) {
            log.error( "Error instantiating aggregation beans",
                       e );
            throw new AggregationException( "Error instantiating aggregation beans",
                                            e );
        } catch ( IllegalAccessException e ) {
            log.error( "Error instantiating aggregation beans",
                       e );
            throw new AggregationException( "Error instantiating aggregation beans",
                                            e );
        } catch ( IllegalArgumentException e ) {
            log.error( "Error instantiating aggregation beans",
                       e );
            throw new AggregationException( "Error instantiating aggregation beans",
                                            e );
        } catch ( InvocationTargetException e ) {
            log.error( "Error instantiating aggregation beans",
                       e );
            throw new AggregationException( "Error instantiating aggregation beans",
                                            e );
        } catch ( Exception e) {
            log.error( "Error during aggregation",
                       e );
            throw new AggregationException( "Error during aggregation",
                                            e );
            
        }
        return results;
    }
}
