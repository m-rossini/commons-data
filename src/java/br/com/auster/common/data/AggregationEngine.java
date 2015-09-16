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

package br.com.auster.common.data;

import java.util.Map;

import org.w3c.dom.Element;

/**
 * <p><b>Title:</b> AggregationEngine</p>
 * <p><b>Description:</b> An interface to define a generic Engine to calculate Aggregation</p>
 * <p><b>Copyright:</b> Copyright (c) 2005</p>
 * <p><b>Company:</b> Auster Solutions</p>
 *
 * @author etirelli
 * @version $Id: AggregationEngine.java 116 2006-02-16 17:48:49Z etirelli $
 */
public interface AggregationEngine {
  
  /**
   * A general configuration method. This method is implementation dependent.
   *  
   * @param config
   * @throws AggregationException
   */
  public void configure(Element config) throws AggregationException;

  /**
   * The method that actually triggers the aggregations on the inputData map
   * returning a map instance with the aggregated facts.
   * This method is implementation dependent.
   * 
   * @param inputData
   * @return Map<String, Object> of aggregated data
   * 
   * @throws AggregationException in case something goes wrong
   */
  public Map aggregate(Map inputData) throws AggregationException;

}
