package JDBC;

import java.io.File;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class JDriver implements Driver  {

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		url = url.replaceAll("\\s+","");
		String[] parts = url.split(":");
		String jdbcStr = parts[0];//i:e "jdbc"
		String protocolStr = parts[1];//i:e "xmldb" or "jsondb"
		String localhostStr = parts[2];//i:e "//localhost"
		
		if(!Objects.equals(jdbcStr,"jdbc")){
			return false; 
		}//if
		if(!(Objects.equals(protocolStr,"xmldb")||Objects.equals(protocolStr,"jsondb"))){
			return false; 
		}//if
		if(!Objects.equals(localhostStr,"//localhost")){
			return false; 
		}//if		
		
		return true;
	}

	@Override
	public Connection connect(String url, Properties path) throws SQLException {
		
		if(!acceptsURL(url)){
			return null;
		}//if
		
		String strPath = path.getProperty("path");
		File dir = new File(strPath);
		if(!dir.exists()){
			dir.mkdir();
		}//if		
		return new JConnection();
	}//method

	@Override
	public int getMajorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMinorVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String arg0, Properties arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		// TODO Auto-generated method stub
		return false;
	}

}/*Class*/
