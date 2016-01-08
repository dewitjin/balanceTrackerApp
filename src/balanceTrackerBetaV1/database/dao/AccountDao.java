/**
 *Project: balanceTrackerBetaV1
 *File: AccountDao.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */

package balanceTrackerBetaV1.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.data.Bank;
import balanceTrackerBetaV1.database.Database;
import balanceTrackerBetaV1.utilities.ApplicationException;


/**
 * 
 * This class contains CRUD operations for a BANK access object
 * (create, read, update, delete) 
 * TODO: DID NOT TEST ALL METHODS IN THIS CLASS YET: update, delete etc.
 * @author Dewi Tjin
 *
 */
public class AccountDao extends Dao {
	
	private static Logger LOG = LogManager.getLogger(AccountDao.class.getName()); //is this supposed to be getName()??
	//singleton pattern
	private static final AccountDao theInstance = new AccountDao();
	public static final String TABLE_NAME_ACCOUNTS = "Accounts";
	
	protected AccountDao() {
		super(TABLE_NAME_ACCOUNTS);
	}
	
	/**
	 * @return the theInstance
	 */
	public static AccountDao getTheInstance() {
		return theInstance;
	}

	/**
	 * Check if the table exist or not
	 * @throws ApplicationException
	 * @throws SQLException 
	 */
	public static void init() throws ApplicationException, SQLException {
		// check if database has data loaded already in this table
		if (Database.tableExists(TABLE_NAME_ACCOUNTS)) {
			LOG.error(String.format(
					"This database already as a table with the name of %s",
					TABLE_NAME_ACCOUNTS));
			System.exit(-1);
		}
	}
	@Override
	public void create() throws SQLException {	
		String createStatement = String.format("create table %s ("
				+ "%s VARCHAR(100), %s VARCHAR(100), "
				+ "%s VARCHAR(100), %s VARCHAR(100), "
				+ "primary key (%s))", 
				TABLE_NAME_ACCOUNTS, 
				Fields.NAME_ACCOUNT,
				Fields.BANK_PREFIX,
				Fields.BANK_NAME,
				Fields.TYPE,
				Fields.NAME_ACCOUNT// pk key in this table, to prevent duplicates
				);
		super.create(createStatement);
		LOG.info("Create statement: " + createStatement);

	}
	
	/**
	 * Add a account to the row
	 * @param account
	 * @throws SQLException
	 */
	public void add(Account account) throws SQLException {
		Statement statement = null;
		Account accountCheck;
		try {
			//accountCheck = readByBankPrefix(account.getName());
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			String insertString = String.format(
			        "insert into %s (%s, %s, %s, %s) values (?, ?, ?, ?)", 
			        TABLE_NAME_ACCOUNTS, Fields.NAME_ACCOUNT,
			        Fields.BANK_PREFIX,  Fields.BANK_NAME,
			        Fields.TYPE);
			        
			PreparedStatement preparedStatement = connection.prepareStatement(insertString);
			preparedStatement.setString(1,account.getName());
			preparedStatement.setString(2,account.getBank().getPrefix());
			preparedStatement.setString(3, account.getBank().getName());
			preparedStatement.setString(4, account.getType());
			preparedStatement.executeUpdate();
			LOG.debug(insertString);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	/**
	 * This method should not be used; used add()
	 * Add a account to the row
	 * Note: keeping it here just for other people to study
	 * @param account
	 * @throws SQLException
	 */
	public void addQueryWithoutPreparedStatement(Account account) throws SQLException {
		Statement statement = null;
		Account accountCheck;
		try {
			//accountCheck = readByBankPrefix(account.getName());
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			String insertString = String.format(
			        "insert into %s values('%s', '%s', '%s', '%s')", TABLE_NAME_ACCOUNTS, 
			        account.getName(),
			        account.getBank().getPrefix(),  // not sure if this will work because account just sends in Bank object ...
			        account.getBank().getName(),
			        account.getType()			        
					);
			statement.executeUpdate(insertString);
			LOG.debug(insertString);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Retrieves a account 
	 * Note: use to create a UpdateAccount Arraylist object
	 * @param name
	 * @return Account
	 * @throws SQLException
	 * @throws Exception
	 */
	public Account readByAccountName(String name) throws SQLException,
			Exception {
		Connection connection;
		Statement statement = null;
		Account account = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s WHERE %s = '%s'",
					TABLE_NAME_ACCOUNTS, Fields.NAME_ACCOUNT, name);
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug("read by identifier statement: " + sqlString);
			int count = 0;
			while (resultSet.next()) {
				count++;
				if (count > 1) {
					throw new Exception(String.format(
							"Expected one result, got %d", count));
				}
				account = new Account();
				account.setName(resultSet.getString(Fields.NAME_ACCOUNT.name()));
				
				Bank bank = new Bank();
				bank.setPrefix(resultSet.getString(Fields.BANK_PREFIX.name()));
				bank.setName(resultSet.getString(Fields.BANK_NAME.name()));
				account.setBank(bank); //THIS MAY BE A PROBLEM ?? NEED TO TEST
				
				account.setType(resultSet.getString(Fields.TYPE.name()));
				//LOG.debug(bank);
			}
		} finally {
			close(statement);
		}
		return account;
	}
	
	/**
	 * Updates an account row
	 * Note: since 2016Jan07 haven't needed to use this update();
	 * but it's here if I need it - tested already
	 * @param bank
	 * @throws SQLException
	 */
	public void update(Account account) throws SQLException {
		Connection connection;
		Statement statement = null;
		int rowcount = 0;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String
			        .format("UPDATE %s set %s=?, %s=? , %s=?, %s=?" +
			        		"WHERE %s=?", //note: wasn't working because I left out question mark here
			        		TABLE_NAME_ACCOUNTS , Fields.NAME_ACCOUNT ,
			        		Fields.BANK_PREFIX  , Fields.BANK_NAME    , 
			        		Fields.TYPE         , Fields.NAME_ACCOUNT  
			        		);	
			
			PreparedStatement preparedStatement = connection.prepareStatement(sqlString);
			preparedStatement.setString(1,account.getName());
			preparedStatement.setString(2,account.getBank().getPrefix());
			preparedStatement.setString(3, account.getBank().getName());
			preparedStatement.setString(4, account.getType());
			preparedStatement.setString(5, account.getName());
			rowcount = preparedStatement.executeUpdate();
			
			LOG.debug("update statment: " + sqlString);
			LOG.debug(String.format("Updated %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Updates an account row - general update statement
	 * @param bank
	 * @throws SQLException
	 */
	public void updateWithoutPreparedStatementQueries(Account account) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String
			        .format("UPDATE %s set %s='%s', %s='%s' " +
			        		" %s='%s', %s='%s'" +
			        		"WHERE %s='%s'", // not sure what happens when you don't have pk keys??
			        		TABLE_NAME_ACCOUNTS, 
			        		Fields.NAME_ACCOUNT   , account.getName(),
			        		Fields.BANK_PREFIX    , account.getBank().getPrefix(),
			        		Fields.BANK_NAME      , account.getBank().getName(),
			        		Fields.TYPE           , account.getType(),
			        		Fields.NAME_ACCOUNT   , account.getName() // add this in for clause after where?
			        		);	
			LOG.debug("update statment: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Updated %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * This method is used to place a CLOSED status for the UpdateAccountJList class
	 * First, it will find all the accounts with the account being passed and update the account name
	 * to PREPEND the words CLOSED dash "the day it was closed" and the account name
	 * @param account
	 * @param date
	 */
	public void readByAccountAndUpdate(Account account, LocalDate date){
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String
					//reference key kept the same
			        .format("UPDATE %s set "
			        		+ "%s='%s'"
			        		+ "WHERE %s='%s'",
			        		TABLE_NAME_ACCOUNTS, 		
							Fields.NAME_ACCOUNT , "ClosedOn-" + date.getYear() + "/" + date.getMonthValue() 
							+ "/" + date.getDayOfMonth() + " " + account.getName(),							
							Fields.NAME_ACCOUNT , account.getName()							
							);
			LOG.debug("update statment: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Updated to CLOSED status %d rows", rowcount));
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}finally {
			close(statement);
		}
	}
	
	/**
	 * Delete row from account table by account name
	 * @param account
	 * @throws SQLException
	 */
	public void delete(Account account) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String.format("DELETE from %s WHERE %s='%s'",
					TABLE_NAME_ACCOUNTS, Fields.NAME_ACCOUNT, account.getName());
			LOG.debug("drop statement: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Method to retrieve all accounts 
	 * @param 
	 * @return list of accounts
	 * @throws Exception 
	 */
	public List<Account> getAllAccounts() throws Exception {
		Connection connection;
		Statement statement = null;
		List<Account> accounts = new ArrayList<Account>();
		Account account = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s", 
					TABLE_NAME_ACCOUNTS);
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			while (resultSet.next()) {
				account = new Account();
				account.setName(resultSet.getString(Fields.NAME_ACCOUNT.name()));
				
				Bank bank = new Bank();
				bank.setPrefix(resultSet.getString(Fields.BANK_PREFIX.name()));
				bank.setName(resultSet.getString(Fields.BANK_NAME.name()));
				account.setBank(bank); //THIS MAY BE A PROBLEM ?? NEED TO TEST	
				account.setType(resultSet.getString(Fields.TYPE.name()));			
				accounts.add(account);
				LOG.debug(account.toString());
				System.out.println(account);
			}				
		} finally {
			close(statement);
		}
		return accounts;
	}

	/**
	 * This is an enum for the properties in the Bank class.
	 * 
	 * @author Dewi Tjin
	 *
	 */
	public enum Fields {

		NAME_ACCOUNT(1), BANK_PREFIX(2), BANK_NAME(3), TYPE(4);
		
		private final int column;

		Fields(int column) {
			this.column = column;
		}

		public int getColumn() {
			return column;
		}
	}
}
	

