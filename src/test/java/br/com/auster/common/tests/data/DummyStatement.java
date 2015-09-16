package br.com.auster.common.tests.data;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

public class DummyStatement implements PreparedStatement {

	public ResultSet executeQuery() throws SQLException {
		return new DummyResultSet(false);
	}

	public int executeUpdate() throws SQLException {
		return 1;
	}

	public void setNull(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBoolean(int arg0, boolean arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setByte(int arg0, byte arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setShort(int arg0, short arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setInt(int arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setLong(int arg0, long arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setFloat(int arg0, float arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setDouble(int arg0, double arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBigDecimal(int arg0, BigDecimal arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBytes(int arg0, byte[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setDate(int arg0, Date arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setTime(int arg0, Time arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setTimestamp(int arg0, Timestamp arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setUnicodeStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void clearParameters() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setObject(int arg0, Object arg1, int arg2, int arg3) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setObject(int arg0, Object arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setObject(int arg0, Object arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean execute() throws SQLException {
		return true;
	}

	public void addBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setRef(int arg0, Ref arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setArray(int arg0, Array arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setDate(int arg0, Date arg1, Calendar arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setTime(int arg0, Time arg1, Calendar arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setTimestamp(int arg0, Timestamp arg1, Calendar arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNull(int arg0, int arg1, String arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setURL(int arg0, URL arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet executeQuery(String arg0) throws SQLException {
		return new DummyResultSet(true);
	}

	public int executeUpdate(String arg0) throws SQLException {
		return 1;
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getMaxFieldSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMaxFieldSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getMaxRows() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setMaxRows(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setEscapeProcessing(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getQueryTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setQueryTimeout(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void cancel() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCursorName(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean execute(String arg0) throws SQLException {
		return true;
	}

	public ResultSet getResultSet() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getUpdateCount() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean getMoreResults() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setFetchDirection(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getFetchDirection() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setFetchSize(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getFetchSize() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getResultSetConcurrency() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getResultSetType() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void addBatch(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void clearBatch() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int[] executeBatch() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Connection getConnection() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getMoreResults(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int executeUpdate(String arg0, int arg1) throws SQLException {
		return 1;
	}

	public int executeUpdate(String arg0, int[] arg1) throws SQLException {
		return 1;
	}

	public int executeUpdate(String arg0, String[] arg1) throws SQLException {
		return 1;
	}

	public boolean execute(String arg0, int arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean execute(String arg0, int[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean execute(String arg0, String[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public int getResultSetHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
		
}
