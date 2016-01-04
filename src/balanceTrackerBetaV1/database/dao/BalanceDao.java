/**
 *Project: balanceTrackerBetaV1
 *File: BalanceDao.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.database.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.data.Balance;
import balanceTrackerBetaV1.database.Database;
import balanceTrackerBetaV1.data.Bank;

/**
 * This class creates the balanceDao object.
 * Note: It contains the total methods. If another account type is created like 
 * non RRSP investment etc. another total method needs to be created for that type.
 * There's two total methods that needs to be updated one with with no params 
 * and the other with LocalDate as a param.
 * @author Dewi Tjin
 *
 */
public class BalanceDao extends Dao{
	
	private static Logger LOG = LogManager.getLogger(BalanceDao.class.getName()); //is this supposed to be getName()??
	//singleton pattern
	private static final BalanceDao theInstance = new BalanceDao();
	public static final String TABLE_NAME_BALANCES = "Balances";
	private static int referenceKey;
	
	private BalanceDao() {
		super(TABLE_NAME_BALANCES);
	}
	
	/**
	 * @return the the Instance
	 */
	public static BalanceDao getTheInstance() {
		return theInstance;
	}

	/**
	 * Sets the reference key static variable to the max primary key in balance table
	 * @throws SQLException 
	 */
	public static void init() throws SQLException {		
		try {
			String sqlString = "SELECT MAX (DISTINCT " + Fields.REFERENCE_KEY
					+ ") FROM " + TABLE_NAME_BALANCES;
			connectCreateStatementAndExecute(sqlString);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}
	
	/**
	 * Helper method for init method to connect to database,
	 * execute the query and debug the the sqlString
	 * @param sqlString
	 */
	private static void connectCreateStatementAndExecute(String sqlString){
		Statement statement = null;
		Connection connection;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			//this creates a result set with the largest PK key in table then retrieves that value	
			referenceKey = (resultSet.next()) ? resultSet.getInt(1) : 0; 	
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}finally {
			close(statement);
		}
		
	}

	@Override
	public void create() throws SQLException {
		String createStatement = String.format("create table %s ("
				+ "%s INTEGER,"
				+ "%s VARCHAR(80), "   // bank name
				+ "%s VARCHAR(10), "   //prefix
				+ "%s VARCHAR(80), "   // account name
				+ "%s VARCHAR(80), "   //type
				+ "%s INTEGER, " 
				+ "%s INTEGER, "
				+ "%s INTEGER, "
				+ "%s FLOAT, "         //amount
				+ "%s VARCHAR(250), "  //notes
				+ "primary key (%s)"   // remember no comma after last one
				+ ")", 
				TABLE_NAME_BALANCES, 	
				Fields.REFERENCE_KEY, //primary key	
				Fields.BANK_NAME,
				Fields.PREFIX,
				Fields.ACCOUNT_NAME, 
				Fields.ACCOUNT_TYPE,
				Fields.YEAR_UPDATED,
				Fields.MONTH,
				Fields.DAY_OF_MONTH,
				Fields.AMOUNT,
				Fields.EXTRA_NOTES,
				Fields.REFERENCE_KEY //primary key				
				);
		super.create(createStatement);
		LOG.info("Create statement: " + createStatement);
	}
	
	/**
	 * Updates a balance row if the balance exists in the table already (PK key
	 * will be updated automatically). If the balance does not exist then we
	 * insert a new balance and assign it a new incremented PK (this insert part
	 * is basically the old and deleted add method from assignment 2 project)
	 * NOTE: this update method does not take a param of reference key, so it
	 * doesn't update a specific line - TODO create a method that does??
	 * 
	 * @param balance
	 * @throws SQLException
	 */
	public int update(Balance balance) throws SQLException {
		Connection connection;
		Statement statement = null;
		int rowcount;
		
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString;	
			if (balance.existsInDatabase()) {
				sqlString = String
				        .format("UPDATE %s SET "
				        		+ "%s='%s', %s='%s', %s='%s', %s='%s', "
				        		+ "%s=%d, %s=%d, %s=%d, %s=%d, %s='%s' "
				        		+ "WHERE %s=%d",
				        		TABLE_NAME_BALANCES, 
				                //REFERENCE_KEY(1), 
								Fields.BANK_NAME    , balance.getBank().getName(),
								Fields.PREFIX       , balance.getBank().getPrefix(),
								Fields.ACCOUNT_NAME , balance.getAccount().getName(),
								Fields.ACCOUNT_TYPE , balance.getAccount().getType(),
								Fields.YEAR_UPDATED , balance.getYear(),
								Fields.MONTH        , balance.getMonth(),
								Fields.DAY_OF_MONTH , balance.getDayOfMonth(),
								Fields.AMOUNT       , balance.getAmount(),
								Fields.EXTRA_NOTES  , balance.getExtraNotes(),
								Fields.REFERENCE_KEY, balance.getKey()
								);
				LOG.debug("update statment: " + sqlString);
			} else {
				balance.setKey(++referenceKey); //this is what increments the PK properly!
				sqlString = String.format(
						//%.2F means needs two number after decimal
				        "insert into %s values( %d ,'%s','%s', '%s', '%s', %d, %d, %d, %.2f, '%s')",              
				        TABLE_NAME_BALANCES, 
				        balance.getKey(), 
				        balance.getBank().getName(),
				        balance.getBank().getPrefix(),
				        balance.getAccount().getName(),
				        balance.getAccount().getType(),
				        balance.getYear(),
				        balance.getMonth(),
				        balance.getDayOfMonth(),
				        balance.getAmount(),
				        balance.getExtraNotes()
						);
				LOG.debug("add statment: " + sqlString);		
			}
			
			rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Updated %d rows", rowcount));
		} finally {
			close(statement);
		}
		return rowcount;
	}
	
	/**
	 * Retrieves a Balance 
	 * @param referenceKey
	 * @return Balance
	 * @throws SQLException
	 * @throws Exception
	 */
	public Balance readByBalanceReferenceKey(int referenceKey) throws SQLException,
			Exception {
		Connection connection;
		Statement statement = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;			
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s WHERE %s = %d",
					TABLE_NAME_BALANCES, Fields.REFERENCE_KEY, referenceKey);
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
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setKey(referenceKey); 
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));		
				LOG.debug(count + " " + balance);
			}
		} finally {
			close(statement);
		}
		return balance;
	}
	
	/**
	 * This method is used to place a CLOSED status for the UpdateAccountJList class
	 * First, it will find all the balances with the account being passed and update the account name
	 * to PREPEND the words CLOSED dash "the day it was closed" and the account name
	 * NOTE: this method updates future balance inputs with this account name to the new account name,
	 * but I don't think it updates all the old balances to the new name
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
			        		TABLE_NAME_BALANCES, 		
							Fields.ACCOUNT_NAME , "ClosedOn-" + date.getYear() + "/" + date.getMonthValue() 
							+ "/" + date.getDayOfMonth() + " " + account.getName(),							
							Fields.ACCOUNT_NAME , account.getName()							
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
	 * Updates a balance row - this is the old update method, probably can delete now
	 * @param bank
	 * @throws SQLException
	 */
	public void updateOld(Balance balance) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String
					//not sure how this will update with auto primary key
			        .format("UPDATE %s set %s='%s', %s='%s' "
			        		+ "%s='%s', %s='%s'"
			        		+ "%s=%d, %s=%d"
			        		+ "%s=%d, %s=%d"
			        		+ "%s='%s'"
			        		+ "WHERE %s=%d",//this where doesn't do anything I think..
			        		TABLE_NAME_BALANCES, 
			                //REFERENCE_KEY(1), 
							Fields.BANK_NAME    , balance.getBank().getName(),
							Fields.PREFIX       , balance.getBank().getPrefix(),
							Fields.ACCOUNT_NAME , balance.getAccount().getName(),
							Fields.ACCOUNT_TYPE , balance.getAccount().getType(),
							Fields.YEAR_UPDATED , balance.getYear(),
							Fields.MONTH        , balance.getMonth(),
							Fields.DAY_OF_MONTH , balance.getDayOfMonth(),
							Fields.AMOUNT       , balance.getAmount(),
							Fields.EXTRA_NOTES  , balance.getExtraNotes()
							);
			LOG.debug("update statment: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Updated %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Delete row from balance table by reference key
	 * TODO: need to test
	 * @param referenceKey
	 * @throws SQLException
	 */
	public void delete(int referenceKey) throws SQLException {
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			// Execute a statement
			String sqlString = String.format("DELETE from %s WHERE %s=%d",
					TABLE_NAME_BALANCES, Fields.REFERENCE_KEY, referenceKey);
			LOG.debug("drop statement: " + sqlString);
			int rowcount = statement.executeUpdate(sqlString);
			LOG.debug(String.format("Deleted %d rows", rowcount));
		} finally {
			close(statement);
		}
	}
	
	/**
	 * Method to retrieve ALL balance data in balance table
	 * In order to create a balance object, bank and account object needs to be set too
	 * @param 
	 * @return
	 * @throws Exception 
	 */
	public List<Balance> getAllBalance() throws Exception {
		Connection connection;
		Statement statement = null;
		List<Balance> balances = new ArrayList<Balance>();
		Balance balance = null;
		Bank bank = null; //may need to set this to null even though we aren't doing anything with it
		Account account = null;
		int count = 0;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s", TABLE_NAME_BALANCES);
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			while (resultSet.next()) {
				count++;				
			    balance = balance(resultSet); 
			    balances.add(balance);
				LOG.debug("value of count not primary key: " + count + " " + balance);
			}				
		} finally {
			close(statement);
		}
		return balances;
	}
		
	/**
	 * Method to retrieve all balance data with one date
	 * In order to create a balance object, bank and account object needs to be set too
	 * @param date
	 * @return
	 * @throws Exception 
	 */
	public List<Balance> getAllBalance(LocalDate date) throws Exception {
		Connection connection;
		Statement statement = null;
		List<Balance> balances = new ArrayList<Balance>();
		Balance balance = null;
		Bank bank = null; //may need to set this to null even though we aren't doing anything with it
		Account account = null;
		int count = 0;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			String sqlString = String.format("SELECT * FROM %s WHERE %s = %d AND %s = %d AND %s = %d", 
					TABLE_NAME_BALANCES, Fields.YEAR_UPDATED, date.getYear(), Fields.MONTH, date.getMonthValue(),
					Fields.DAY_OF_MONTH,date.getDayOfMonth());
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			while (resultSet.next()) {
				count++;		    
				balance = balance(resultSet); 
				balances.add(balance);
				//LOG.debug("value of count not primary key: " + count + " " + balance);
			}				
		} finally {
			close(statement);
		}
		return balances;	
	}
	
	/**
	 * Helper method to getAllBalances() to set columns up with the right data
	 * TODO: use this for other methods too
	 * @param referenceKey
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private Balance balance(ResultSet resultSet) throws SQLException {
		Balance balance = new Balance();
		Bank bank = new Bank();
	    Account account = new Account();
		
		bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
	    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
	    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
	    account.setBank(bank);
	    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type	    
		balance.setKey(resultSet.getInt(Fields.REFERENCE_KEY.name())); //the method before the changed passed referenceKey as a param, but we don't need to because if we pass it here, we will only get the last referenceKey Value, here we ant the actually values of all reference keys
	    balance.setBank(bank);
	    balance.setAccount(account);
	    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
	    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
	    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
	    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
	    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
		return balance;
	}
		
	
	/**
	 * Method retrieves all the balances within a specific date range; using in Date Range Dialog
	 * @param startDate
	 * @param endDate
	 * @return balances
	 * @throws SQLException
	 */
	public List <Balance> getAllBalances(LocalDate startDate, LocalDate endDate) throws SQLException{
		LOG.debug(startDate.getYear() + "  " +  endDate.getYear());
		LOG.debug(startDate.getMonthValue() + " " +  endDate.getMonthValue());
		LOG.debug(startDate.getDayOfMonth() + " " +  endDate.getDayOfMonth());
		String selectString = String.format("SELECT * FROM %s WHERE %s BETWEEN %d AND %d" 
				+ " AND %s BETWEEN %d AND %d"
				+ " AND %s BETWEEN %d AND %d",
				TABLE_NAME_BALANCES, 
				Fields.YEAR_UPDATED, startDate.getYear(), endDate.getYear(),
				Fields.MONTH, startDate.getMonthValue(), endDate.getMonthValue(),
				Fields.DAY_OF_MONTH, startDate.getDayOfMonth(), endDate.getDayOfMonth()				
				);
		LOG.debug(selectString);
		List<Balance> balances = new ArrayList<Balance>();
		Statement statement = null;
		ResultSet resultSet = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;	
		int count = 0;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);			
			while (resultSet.next()) {
				count++;
				bank = new Bank();
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setKey(resultSet.getInt(Fields.REFERENCE_KEY.name())); //this needs to be set or else report keys are all zero, not sure why it's between account and balance though: check later
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
				balances.add(balance);
//				LOG.debug(balance.toString());		
//				LOG.debug(balances.size());	
			}
			
		} finally {
			close(statement);
		}
		return balances;	
	};
	
	/**
	 * This method is a helper to sum all balances in different ways
	 * @param sqlString
	 */
	public float getTotalOfBalances(String sqlString){
		float totalBalances = 0;
		Connection connection;
		Statement statement = null;
		try {
			connection = Database.getConnection();
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlString);
			LOG.debug(sqlString);
			int index = 1;
			while (resultSet.next()) {
				totalBalances = resultSet.getFloat(index);
			}
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}finally {
			close(statement);
		}		
		return totalBalances;
	}

	/**
	 * Method to get the total balance for accounts with
	 * credit/debit type
	 * 
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalDebitAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.DEBITS.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.DEBITS.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	
	/**
	 * Method to get the total balance for accounts with specify date
	 * credit/debit type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalDebitAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.DEBITS.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.DEBITS.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	/**
	 * Method to get the total balance for accounts with
	 * chequing type
	 * 
	 * @return
	 * @throws SQLException
	 */

	public float getTotalChequingAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.CHEQUING.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);
		LOG.debug(Account.Types.CHEQUING.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	
	/**
	 * Method to get the total balance for accounts with specify date
	 * chequing type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalChequingAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.CHEQUING.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.CHEQUING.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
		
	/**
	 * Method to get the total balance for accounts with savings type
	 * 
	 * @return
	 * @throws SQLException
	 */
	public float getTotalSavingAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.SAVING.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);
		LOG.debug(Account.Types.SAVING.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	
	/**
	 * Method to get the total balance for accounts with specify date
	 * saving type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalSavingAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.SAVING.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.SAVING.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	/**
	 * Method to get the total balance for accounts with
	 * RRSP Investment type
	 * 
	 * @return totalBalances
	 * @throws SQLException
	 */

	public float getTotalRRSP_InvestmentAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.RRSP_INVESTMENTS.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);
		LOG.debug(Account.Types.RRSP_INVESTMENTS.getAccountType() + " "
				+ totalBalances);
		return totalBalances;
	}
	/**
	 * Method to get the total balance for accounts with specify date
	 * RRSP Investment type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalRRSP_InvestmentAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.RRSP_INVESTMENTS.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.RRSP_INVESTMENTS.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	
	/**
	 * Method to get the total balance for accounts with 
	 * Others_Investments type
	 * 
	 * @return totalBalances
	 * @throws SQLException
	 */

	public float getTotalOthersAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.OTHER_INVESTMENTS.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);
		LOG.debug(Account.Types.OTHER_INVESTMENTS.getAccountType() + " "
				+ totalBalances);
		return totalBalances;
	}
	/**
	 * Method to get the total balance for accounts with specify date
	 * RRSP Investment type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getTotalOthersAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.OTHER_INVESTMENTS.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.OTHER_INVESTMENTS.getAccountType() + " " + totalBalances);
		return totalBalances;
	}
	
	/**
	 * Method to get the total balance for accounts with 
	 * Cash type
	 * 
	 * @return totalBalances
	 * @throws SQLException
	 */

	public float getCashTotalAmount() throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s'", Fields.AMOUNT,
				TABLE_NAME_BALANCES, Fields.ACCOUNT_TYPE,
				Account.Types.CASH.getAccountType());
		totalBalances = getTotalOfBalances(sqlString);
		LOG.debug(Account.Types.CASH.getAccountType() + " "
				+ totalBalances);
		return totalBalances;
	}
	/**
	 * Method to get the total balance for accounts with specify date
	 * Cash type
	 * @date
	 * @return totalBalances
	 * @throws SQLException
	 */
	public float getCashTotalAmount(LocalDate date) throws SQLException {
		float totalBalances = 0;
		String sqlString = String.format(
				"SELECT SUM(%s) FROM %s WHERE %s='%s' AND %s = %d AND %s = %d AND %s = %d", 
				Fields.AMOUNT,
				TABLE_NAME_BALANCES, 
				Fields.ACCOUNT_TYPE,  Account.Types.CASH.getAccountType(),
				Fields.YEAR_UPDATED, date.getYear(), 
				Fields.MONTH, date.getMonthValue(),
				Fields.DAY_OF_MONTH,date.getDayOfMonth()			
				);
		totalBalances = getTotalOfBalances(sqlString);	
		LOG.debug(Account.Types.CASH.getAccountType() + " " + totalBalances);
		return totalBalances;
	}

	//start of methods not fully tested yet
	//TODO: work on these methods in the future
	
	/**
	 * DIDN'T USE YET
	 * Get all balances by bank prefix
	 * Note: will return all balances with the specify prefix
	 * @param prefix
	 * @return a list of balances
	 * @throws SQLException 
	 */
	public List<Balance> getAllBalances_ByBankPrefix(String prefix) throws SQLException{
		String selectString = String.format("SELECT * FROM %s WHERE %s = '%s'", 
				TABLE_NAME_BALANCES, Fields.PREFIX, prefix);
		LOG.debug(selectString);
		List<Balance> balances = new ArrayList<Balance>();
		Statement statement = null;
		ResultSet resultSet = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;	
		
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);	
			int count = 0;
			while (resultSet.next()) {
				count++;
				bank = new Bank();
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
				balances.add(balance);
				LOG.debug(balance.toString());		
			}
			LOG.debug("total balances retrieved by prefix" + prefix + " is: " + balances.size());
		} finally {
			close(statement);
		}
		return null;
	};
	
	/**
	 *  * DIDN'T USE YET
	 * Retrieves a list of balances using bank prefixes and account names
	 * @param prefix
	 * @param name
	 * @return
	 * @throws SQLException 
	 */
	public List <Balance> getAllBalances_ByBankPrefixAndAccountName(String prefix, String account_name) throws SQLException{
		String selectString = String.format("SELECT * FROM %s "
				+ "WHERE %s = '%s' AND %s = '%s'", 
				TABLE_NAME_BALANCES, 
				Fields.PREFIX, prefix,
				Fields.ACCOUNT_NAME, account_name
				);
		LOG.debug(selectString);
		List<Balance> balances = new ArrayList<Balance>();
		Statement statement = null;
		ResultSet resultSet = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;	
		int count = 0;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);			
			while (resultSet.next()) {
				count++;
				bank = new Bank();
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
				balances.add(balance);
				LOG.debug(balance.toString());		
			}
			LOG.debug("total balances retrieved by " + prefix + " " + account_name + " is: " + balances.size());
		} finally {
			close(statement);
		}
		return balances;
	};
	
	/**
	 *  * DIDN'T USE YET
	 * Retrieves a list of balances from specified year and month
	 * @param year
	 * @param month
	 * @return a list of balances
	 * @throws SQLException 
	 */
	public List <Balance> getAllBalances_ByYEARandMONTH(int year, int month) throws SQLException{
		String selectString = String.format("SELECT * FROM %s "
				+ "WHERE %s = %d AND %s = %d", 
				TABLE_NAME_BALANCES, 
				Fields.YEAR_UPDATED, year,
				Fields.MONTH, month
				);
		LOG.debug(selectString);
		List<Balance> balances = new ArrayList<Balance>();
		Statement statement = null;
		ResultSet resultSet = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;	
		int count = 0;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);			
			while (resultSet.next()) {
				count++;
				bank = new Bank();
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
				balances.add(balance);
				//LOG.debug(balance.toString());		
			}
			LOG.debug("total balances retrieved by " + year + " and " + month + " is: " + balances.size());
		} finally {
			close(statement);
		}
		return balances;
	};
	
	/**
	 *  * DIDN'T USE YET
	 * Retrieves a list of balances to do by date - year, month, day of month integers passes in
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @return a list
	 * @throws SQLException 
	 */
	public List <Balance> getAllBalances_BySpecificDATE(int year, int month, int dayOfMonth) throws SQLException{
		String selectString = String.format("SELECT * FROM %s "
				+ "WHERE %s = %d AND %s = %d AND %s = %d", 
				TABLE_NAME_BALANCES, 
				Fields.YEAR_UPDATED, year,
				Fields.MONTH, month,
				Fields.DAY_OF_MONTH, dayOfMonth
				);
		LOG.debug(selectString);
		List<Balance> balances = new ArrayList<Balance>();
		Statement statement = null;
		ResultSet resultSet = null;
		Balance balance = null;
		Bank bank = null;
		Account account = null;	
		int count = 0;
		try {
			Connection connection = Database.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery(selectString);			
			while (resultSet.next()) {
				count++;
				bank = new Bank();
			    account = new Account();
				balance = new Balance();		    
			    bank.setName(resultSet.getString(Fields.BANK_NAME.name()));  //has to be bank name!
			    bank.setPrefix(resultSet.getString(Fields.PREFIX.name()));
			    account.setName(resultSet.getString(Fields.ACCOUNT_NAME.name()));
			    account.setBank(bank);
			    account.setType(resultSet.getString(Fields.ACCOUNT_TYPE.name())); //changed this to String type
			    balance.setBank(bank);
			    balance.setAccount(account);
			    balance.setYear(resultSet.getInt(Fields.YEAR_UPDATED.name()));
			    balance.setMonth(resultSet.getInt(Fields.MONTH.name()));
			    balance.setDayOfMonth(resultSet.getInt(Fields.DAY_OF_MONTH.name()));
			    balance.setAmount(resultSet.getFloat(Fields.AMOUNT.name()));
			    balance.setExtraNotes(resultSet.getString(Fields.EXTRA_NOTES.name()));
				balances.add(balance);
				//LOG.debug(balance.toString());				
			}
			LOG.debug("total balances retrieved by " + year + "/" + month + "/" + dayOfMonth + " is " + balances.size());
		} finally {
			close(statement);
		}
		return balances;		
	};
	
	/**
	 * This is an enum for the properties in the Balance class.
	 * 
	 * @author dewitjin, A00735080
	 *
	 */
	public enum Fields {
 		
		REFERENCE_KEY(1), 
		BANK_NAME(2),
		PREFIX(3),
		ACCOUNT_NAME(4), 
		ACCOUNT_TYPE(5),
		YEAR_UPDATED(6), //year throws an error for some reason
		MONTH(7),
		DAY_OF_MONTH(8),
		AMOUNT(9),
		EXTRA_NOTES(10);

		private final int column;

		Fields(int column) {
			this.column = column;
		}

		public int getColumn() {
			return column;
		}
	}
}
