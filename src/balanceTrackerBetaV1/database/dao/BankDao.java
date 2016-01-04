/**
 *Project: balanceTrackerBetaV1
 *File: BankDao.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.database.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Bank;
import balanceTrackerBetaV1.database.Database;
import balanceTrackerBetaV1.utilities.ApplicationException;


/**
 * 
 * This class contains CRUD operations for a BANK access object
 * (create, read, update, delete) 
 * TODO: not all method have been testes like update and delete
 * @author Dewi Tjin
 *
 */
public class BankDao extends Dao {
	
	private static Logger LOG = LogManager.getLogger(BankDao.class.getName()); //is this supposed to be getName()??
	//singleton pattern
	private static final BankDao theInstance = new BankDao();
	public static final String TABLE_NAME_BANKS = "Banks";
	
	protected BankDao() {
		super(TABLE_NAME_BANKS);
	}
	
	/**
	 * @return the theInstance
	 */
	public static BankDao getTheInstance() {
		return theInstance;
	}

	/**
	 * Check if the table exist or not
	 * @throws ApplicationException
	 * @throws SQLException 
	 */
	public static void init() throws SQLException {
		if (Database.tableExists(TABLE_NAME_BANKS)) {
			LOG.error(String.format(
					"This database already as a table with the name of %s",
					TABLE_NAME_BANKS));
			System.exit(-1);
		}
	}
	@Override
	public void create() throws SQLException {	
		String createStatement = String.format("create table %s ("
				+ "%s VARCHAR(10), %s VARCHAR(80), primary key (%s))", 
				TABLE_NAME_BANKS, 
				Fields.PREFIX,
				Fields.NAME,
				Fields.PREFIX // need this to be pk so we don't have duplicates
				);
		super.create(createStatement);
		LOG.info("Create statement: " + createStatement);
	}
	/**
	 * Add a bank to the row
	 * @param bank
	 * @throws SQLException
	 */
	public void add(Bank bank) throws SQLException {
		Statement statement = null;
		
		try {
			Connection connection = Database.getConnection(); 
			statement = connection.createStatement();
			String insertString = String.format(
			        "insert into %s values('%s', '%s')", TABLE_NAME_BANKS, 
			        bank.getPrefix(),
			        bank.getName());
			statement.executeUpdate(insertString);
			LOG.debug(insertString);		
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}finally {
			close(statement);
		}
	}
	
	/**
	 * Retrieves a Bank 
	 * Note: use to create a UpdateBalance Arraylist object
	 * @param prefix
	 * @return Bank
	 * @throws SQLException
	 * @throws Exception
	 */
	public Bank readByBankPrefix(String prefix) throws SQLException,
			Exception {
		Connection connection;
		Statement statement = null;
		Bank bank = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s WHERE %s = '%s'",
					TABLE_NAME_BANKS, Fields.PREFIX, prefix);
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug("read by identifier statement: " + sqlString);
			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new Exception(String.format(
							"Expected one result, got %d", count));
				}
				bank = new Bank();
				bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
				bank.setName(resultSet.getString(Fields.NAME.name()));
				LOG.debug(bank);
			}
		} finally {
			close(statement);
		}
		return bank;
	}
	
	/**
	 * Updates a bank row
	 * @param bank
	 * @throws SQLException
	 */
	public void update(Bank bank) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String
			        .format("UPDATE %s set %s='%s', %s='%s' " +
			        		"WHERE %s='%s'",
			        		TABLE_NAME_BANKS, 
			                Fields.PREFIX , bank.getPrefix(),
							Fields.NAME   , bank.getName(),
							Fields.PREFIX , bank.getPrefix());	// add this in for clause after where
			LOG.debug("update statment: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Updated %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Delete row from bank table by bank name
	 * @param bank
	 * @throws SQLException
	 */
	public void delete(Bank bank) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String.format("DELETE from %s WHERE %s='%s'",
					TABLE_NAME_BANKS, Fields.NAME, bank.getName());
			LOG.debug("drop statement: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Method to retrieve all banks 
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public List<Bank> getAllBanks() throws Exception {
		Connection connection;
		Statement statement = null;
		List<Bank> banks = new ArrayList<Bank>();
		Bank bank = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s", 
					TABLE_NAME_BANKS);
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			while (resultSet.next()) {
				bank = new Bank();
				bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
				bank.setName(resultSet.getString(Fields.NAME.name()));
				banks.add(bank);
				LOG.debug(bank);
			}				
		} finally {
			close(statement);
		}
		return banks;
	}

	/**
	 * This is an enum for the properties in the Bank class.
	 * 
	 * @author Dewi Tjin
	 *
	 */
	public enum Fields {

		NAME(1), PREFIX(2);

		private final int column;

		Fields(int column) {
			this.column = column;
		}

		public int getColumn() {
			return column;
		}
	}
}
	

