/**
 *Project: BankingApp
 *File: Dao.java
 *Date: Dec 5, 2015
 *Time: 8:47:26 PM
 */
package balanceTrackerBetaV1.database.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.database.Database;



/**
 * This is the abstract class for Dao, extend it to create other dao classes.
 * @author Dewi Tjin
 *
 */
public abstract class Dao {
	private static final Logger LOG = LogManager.getLogger(Dao.class);
	protected final String tableName;

	protected Dao(String tableName) {
		this.tableName = tableName;
	}

	public abstract void create() throws SQLException;

	/**
	 * Creates new statement by 
	 * getting a connection, creating a statement and executing
	 * @param createStatement
	 * @throws SQLException
	 */
	protected void create(String createStatement) throws SQLException {
		Statement statement = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			statement.executeUpdate(createStatement);
		} finally {
			close(statement);
		}
	}
	/**
	 * Gets a connection, creates a statement and executes
	 * Lab 10: I used void add; this method however returns
	 * the number of row ?? Note need to test
	 * @param updateStatement
	 * @throws SQLException
	 */
	protected int add(String updateStatement) throws SQLException {
		int row = -1;
		Statement statement = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			row = statement.executeUpdate(updateStatement);
		} finally {
			close(statement);
		}
		return row;
	}
	
	/**
	 * Delete the database table
	 * @throws SQLException
	 */
	public void drop() throws SQLException {
		Statement statement = null;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			if (Database.tableExists(tableName)) {
				statement.executeUpdate("drop table " + tableName);
			}
		} finally {
			close(statement);
		}
	}

	/**
	 * Tell the database we're shutting down.
	 */
	public void shutdown() {
		Database.getTheinstance().shutdown();
		LOG.debug("database shutdown");
	}
	/**
	 * Releases this Statement object's database and JDBC resources immediately 
	 * @param statement
	 */
	protected static void close(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
			}
		} catch (SQLException e) {
			LOG.error("Failed to close statment" + e);
		}
	}
}
	

