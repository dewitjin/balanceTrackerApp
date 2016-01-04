/**
 *Project: balanceTrackerBetaV1
 *File: Database.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */

package balanceTrackerBetaV1.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * This Database class uses a singleton design pattern.
 * @author Dewi Tjin
 *
 */
public class Database {

	public static final String DB_DRIVER_KEY = "db.driver";
	public static final String DB_URL_KEY = "db.url";
	public static final String DB_USER_KEY = "db.user";
	public static final String DB_PASSWORD_KEY = "db.password";

	private static final Logger LOG = LogManager.getLogger(Database.class);

	private static final Database theInstance = new Database();
	private static Connection connection;
	private static Properties properties;

	private Database() {
		// can't access from outside
	}

	/**
	 * Method to assign properties to database
	 * 
	 * @param properties
	 */
	public static void init(Properties properties) {
		if (Database.properties == null) {
			LOG.debug("Loading database properties from db.properties");
			Database.properties = properties;
		}
	}

	/**
	 * Method to get the instance of the Database (singleton pattern)
	 * 
	 * @return the theInstance
	 */
	public static Database getTheinstance() {
		return theInstance;
	}

	/**
	 * Method to get a connection, if there is already a connection return the
	 * connection, if there isn't then run the connect()
	 * 
	 * @return connection
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		if (connection != null) {
			return connection;
		}
		try {
			connect();
		} catch (ClassNotFoundException e) {
			throw new SQLException(e);
		}
		return connection;
	}

	/**
	 * Method to connect to db driver and debug to see if Driver is loaded or
	 * not
	 * 
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	private static void connect() throws ClassNotFoundException, SQLException {
		String dbDriver = properties.getProperty(DB_DRIVER_KEY);
		LOG.debug(dbDriver);
		Class.forName(dbDriver);
		System.out.println("Driver loaded");
		connection = DriverManager.getConnection(
				properties.getProperty(DB_URL_KEY),
				properties.getProperty(DB_USER_KEY),
				properties.getProperty(DB_PASSWORD_KEY));
		LOG.debug("Database connected");
	}

	/**
	 * Close the connections to the database
	 */
	public void shutdown() {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	/**
	 * Determine if the database table exists.
	 * 
	 * @param tableName
	 * @return true is the table exists, false otherwise
	 * @throws SQLException
	 */
	public static boolean tableExists(String tableName) throws SQLException {
		DatabaseMetaData databaseMetaData = getConnection().getMetaData();
		ResultSet resultSet = null;
		String rsTableName = null;

		try {
			resultSet = databaseMetaData.getTables(connection.getCatalog(),
					"%", "%", null);
			while (resultSet.next()) {
				rsTableName = resultSet.getString("TABLE_NAME");
				if (rsTableName.equalsIgnoreCase(tableName)) {
					return true;
				}
			}
		} finally {
			resultSet.close();
		}
		return false;
	}
}
