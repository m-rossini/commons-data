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
 * Created on 20/03/2006
 */
package br.com.auster.common.tests.data;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import br.com.auster.common.data.sql.SQLDataSaver;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author framos
 * @version $Id: SQLDataSaverTest.java 364 2007-01-11 20:57:37Z framos $
 */
public class SQLDataSaverTest extends TestCase {

	
	public static final String SAMPLE_CONFIG_FILE = "br/com/auster/common/tests/data/sample-configuration.xml";
	
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        InputStream in = IOUtils.openFileForRead("/br/com/auster/common/tests/log4j.xml");
        LogFactory.configureLogSystem(DOMUtils.openDocument(in));
    }
    
	public void testSampleConfiguration() {
		SQLDataSaver saver = new SQLDataSaver();
		try {
			saver.configure(DOMUtils.openDocument(IOUtils.openFileForRead(SAMPLE_CONFIG_FILE)), createConnection());
			saver.save(buildSourceMap());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
   	
	private Map buildSourceMap() {
		Map source = new HashMap();
		DummyInvoice invoice = new DummyInvoice();
		DummyCycle cycle = new DummyCycle();
		cycle.setCycleCode("55");

		List inv = new ArrayList();
		// inv 1
		invoice.setInvoiceId("1001");
		invoice.setCycle(cycle);
		invoice.setAdjustmentsAmount(10d);
		inv.add(invoice);
		// inv 2
		invoice = new DummyInvoice();
		invoice.setInvoiceId("1003");
		invoice.setCycle(cycle);
		invoice.setAdjustmentsAmount(15d);
		inv.add(invoice);
		// inv 3
		invoice = new DummyInvoice();
		invoice.setInvoiceId("1002");
		cycle = new DummyCycle();
		cycle.setCycleCode("56");
		invoice.setCycle(cycle);
		inv.add(invoice);
		
		source.put("invoice-list", inv);
		return source;
	}

	
    private Connection createConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@tiamat:1521:TEST01", "test", "test");
    }    
 }

