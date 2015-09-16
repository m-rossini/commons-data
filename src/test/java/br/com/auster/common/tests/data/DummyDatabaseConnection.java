package br.com.auster.common.tests.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

public class DummyDatabaseConnection implements Connection {

	public Statement createStatement() throws SQLException {
		return prepareStatement(null);
	}

	public PreparedStatement prepareStatement(String arg0) throws SQLException {
		return new DummyStatement();
	}

	public CallableStatement prepareCall(String arg0) throws SQLException {
		return null;
	}

	public String nativeSQL(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAutoCommit(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean getAutoCommit() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void commit() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void rollback() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void close() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return new DummyDatabaseMetaData();
	}

	public void setReadOnly(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isReadOnly() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setCatalog(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getCatalog() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTransactionIsolation(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getTransactionIsolation() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public SQLWarning getWarnings() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void clearWarnings() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public Statement createStatement(int arg0, int arg1) throws SQLException {
		return prepareStatement(null);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2) throws SQLException {
		return prepareStatement(null);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setHoldability(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}

	public Savepoint setSavepoint() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Savepoint setSavepoint(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rollback(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void releaseSavepoint(Savepoint arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public Statement createStatement(int arg0, int arg1, int arg2) throws SQLException {
		return prepareStatement(null);
	}

	public PreparedStatement prepareStatement(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		return prepareStatement(null);
	}

	public CallableStatement prepareCall(String arg0, int arg1, int arg2, int arg3) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public PreparedStatement prepareStatement(String arg0, int arg1) throws SQLException {
		return prepareStatement(null);
	}

	public PreparedStatement prepareStatement(String arg0, int[] arg1) throws SQLException {
		return prepareStatement(null);
	}

	public PreparedStatement prepareStatement(String arg0, String[] arg1) throws SQLException {
		return prepareStatement(null);
	}

	public Map getTypeMap() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTypeMap(Map arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}	
}
