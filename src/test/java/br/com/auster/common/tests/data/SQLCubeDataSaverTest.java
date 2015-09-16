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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import br.com.auster.common.data.sql.SQLCubeDataSaver;
import br.com.auster.common.io.IOUtils;
import br.com.auster.common.log.LogFactory;
import br.com.auster.common.xml.DOMUtils;

/**
 * @author framos
 * @version $Id: SQLCubeDataSaverTest.java 364 2007-01-11 20:57:37Z framos $
 */
public class SQLCubeDataSaverTest extends TestCase {

	
    public static final String INVOICE_CONFIG_FILE = "br/com/auster/common/tests/data/cube-sample-configuration.xml";
	
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        InputStream in = IOUtils.openFileForRead("/br/com/auster/common/tests/log4j.xml");
        LogFactory.configureLogSystem(DOMUtils.openDocument(in));
    }        
    
    public void testSaver() {
        SQLCubeDataSaver saver = new SQLCubeDataSaver();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rset = null;
        try {
        	conn = createConnection();
        	assertNotNull(conn);

            stmt = conn.createStatement();
            rset = stmt.executeQuery("delete from ep_cycle_test");
            rset = stmt.executeQuery("delete from ep_invoice_test");
            
        	saver.configure(DOMUtils.openDocument(IOUtils.openFileForRead(INVOICE_CONFIG_FILE)), conn);
            Map source = buildSourceMap();
            saver.save(source);
            
            rset = stmt.executeQuery("select count(*) from ep_cycle_test");
            assertTrue(rset.next());
            assertEquals(2, rset.getInt(1));
            
            rset = stmt.executeQuery("select count(*) from ep_invoice_test");
            assertTrue(rset.next());
            assertEquals(3, rset.getInt(1));

            // saving more to simulate another set of accounts
            saver = new SQLCubeDataSaver();
            saver.configure(DOMUtils.openDocument(IOUtils.openFileForRead(INVOICE_CONFIG_FILE)), conn);
            source = buildSourceMap();
            saver.save(source);

            rset = stmt.executeQuery("select count(*) from ep_cycle_test");
            assertTrue(rset.next());
            assertEquals(2, rset.getInt(1));
            
            rset = stmt.executeQuery("select count(*) from ep_invoice_test");
            assertTrue(rset.next());
            assertEquals(6, rset.getInt(1));
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        } finally {
        	try {
        		if (rset != null) { rset.close(); }
        	} catch (SQLException sqle) { 
        		sqle.printStackTrace();
        		fail();
        	}
        	try {
        		if (stmt != null) { stmt.close(); }
        	} catch (SQLException sqle) { 
        		sqle.printStackTrace();
        		fail();
        	}
        	try {
        		if (conn != null) { conn.close(); }
        	} catch (SQLException sqle) { 
        		sqle.printStackTrace();
        		fail();
        	}
        }
    }    
 
	private Map buildSourceMap() {
		Map source = new HashMap();
		DummyInvoice invoice = new DummyInvoice();
		DummyCycle cycle = new DummyCycle();
		cycle.setCycleCode("55");
		cycle.setCycleEndDate(null);

		List inv = new ArrayList();
		// inv 1
		invoice.setCycle(cycle);
		invoice.setInvoiceId("1001");
		invoice.setAdjustmentsAmount(10d);
		invoice.setDisputeAmount(13d);
		inv.add(invoice);
		// inv 2
		invoice = new DummyInvoice();
		invoice.setInvoiceId("1002");
		invoice.setCycle(cycle);
		invoice.setAdjustmentsAmount(15d);
		inv.add(invoice);
		// inv 3
		invoice = new DummyInvoice();
		invoice.setInvoiceId("1003");
		DummyCycle cycle1 = new DummyCycle();
		cycle1.setCycleCode("56");
		invoice.setCycle(cycle1);
		inv.add(invoice);
		
		source.put("invoice-list", inv);
		return source;
	}

    
    private Connection createConnection() throws Exception {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection("jdbc:oracle:thin:@tiamat:1521:TEST01", "test", "test");
//        return DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:DEVEL01", "test", "test");
    }
    
}

