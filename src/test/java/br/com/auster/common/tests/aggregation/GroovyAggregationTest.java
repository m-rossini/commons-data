/*
 * Copyright (c) 2004-2007 Auster Solutions. All Rights Reserved.
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
 * Created on 11/01/2007
 */
package br.com.auster.common.tests.aggregation;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import br.com.auster.common.data.groovy.DeclarativeAggregationEngine;
import br.com.auster.common.tests.aggregation.model.DataAccount;
import br.com.auster.common.tests.aggregation.model.DataInvoice;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author framos
 * @version $Id$
 *
 */
public class GroovyAggregationTest extends TestCase {

	
	protected static final String TEST_CONF = "br/com/auster/common/tests/aggregation/aggregation-test.xml";
	
	
	public void testGroovyScriptsWithoutNull() {
		DeclarativeAggregationEngine engine = new DeclarativeAggregationEngine();
		try {
			engine.configure(DOMUtils.openDocument(TEST_CONF, false));
			Map data = buildDataMap(false);
			Map result = engine.aggregate(data);
			assertNotNull(result.get("invoiceFact-list"));
			assertEquals(1, ((List)result.get("invoiceFact-list")).size());
			
			Object inv = ((List)result.get("invoiceFact-list")).get(0);
			assertNotNull(inv);
			
			Method m = inv.getClass().getMethod("getCycleDimension", null);
			assertNotNull(m);
			Object cycleDimension = m.invoke(inv, null); 
			assertNotNull(cycleDimension);
			// checking cycle code
			m = cycleDimension.getClass().getMethod("getCycleCode", null);
			assertNotNull(m);
			assertEquals("10", m.invoke(cycleDimension, null));
			// checking cycle end date
			m = cycleDimension.getClass().getMethod("getCutDate", null);
			assertNotNull(m);
			assertNotNull(m.invoke(cycleDimension, null));
		} catch (Exception e) {
			// should not raise since exceptions
			e.printStackTrace();
			fail();
		}
	}	

	public void testGroovyScriptsWithNull() {
		DeclarativeAggregationEngine engine = new DeclarativeAggregationEngine();
		try {
			engine.configure(DOMUtils.openDocument(TEST_CONF, false));
			Map data = buildDataMap(true);
			Map result = engine.aggregate(data);
			assertNotNull(result.get("invoiceFact-list"));
			assertEquals(1, ((List)result.get("invoiceFact-list")).size());

			Object inv = ((List)result.get("invoiceFact-list")).get(0);
			assertNotNull(inv);
			
			Method m = inv.getClass().getMethod("getCycleDimension", null);
			assertNotNull(m);
			Object cycleDimension = m.invoke(inv, null); 
			assertNotNull(cycleDimension);
			// checking cycle code
			m = cycleDimension.getClass().getMethod("getCycleCode", null);
			assertNotNull(m);
			assertEquals("10", m.invoke(cycleDimension, null));
			// checking cycle end date
			m = cycleDimension.getClass().getMethod("getCutDate", null);
			assertNotNull(m);
			assertNull(m.invoke(cycleDimension, null));
			// checking issue date
			m = cycleDimension.getClass().getMethod("getIssueDate", null);
			assertNotNull(m);
			assertNull(m.invoke(cycleDimension, null));
		} catch (Exception e) {
			// should not raise since exceptions
			e.printStackTrace();
			fail();
		}
	}	
	
	
	protected Map buildDataMap(boolean _withNulls) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		
		DataInvoice inv = new DataInvoice();
		inv.setCycleCode("10");
		try {
			inv.setCycleStartDate(sdf.parse("10-02-2006"));
			if (!_withNulls) {
				inv.setCycleEndDate(sdf.parse("09-03-2006"));
				inv.setIssueDate(sdf.parse("10-03-2006"));
			}
			inv.setDueDate(sdf.parse("15-03-2006"));
			inv.setTotalAmount(102.1d);
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		DataAccount acc = new DataAccount();
		acc.setAccountNumber("10.10101");
		acc.setAccountType("C");
		inv.setAccount(acc);
		
		Map<String, List> source = new HashMap<String, List>();
		List<DataInvoice> arr = new ArrayList<DataInvoice>();
		arr.add(inv);
		source.put("invoice-list", arr);
		return source;
	}
}
