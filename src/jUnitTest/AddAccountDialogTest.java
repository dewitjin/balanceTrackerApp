package jUnitTest;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.data.Balance;
import balanceTrackerBetaV1.data.Bank;
import balanceTrackerBetaV1.ui.AddAccountDialog;

/**
 * Test for the AddAccountDialog class Type this in terminal 
 * TODO: last method to test for this class createBalance(String balanceInput)
 * TODO: command line instructions don't work yet to compile javac -cp .:"/lib/*"
 * AddAccountDialogTest.java Then this to run the test java -cp
 * .:junit-4.12.jar:hamcrest-core-1.3.jar org.junit.runner.JUnitCore
 * AddAccountDialogTest.java 
 * TODO: need to figure out how to run JUnit in
 * terminal - In order to test from terminal, the above jars needed to be in the
 * test folder too, then use cmd line 
 * to compile: javac -cp .:junit-4.12.jar AddAccountDialogTest.java
 * to run test: java -cp .:junit-4.12.jar:hamcrest-core-1.3.jar
 * org.junit.runner.JUnitCore AddAccountDialogTest.java
 * 
 * @author Dewi Tjin
 *
 */
public class AddAccountDialogTest {

	private static final Logger LOG = LogManager
			.getLogger(AddAccountDialogTest.class);

	private static JTextField textFieldDate;
	private static JTextField textFieldPrefix;
	private static JTextField textFieldBankName;
	private static JTextField textFieldBalance;
	private static JTextField textFieldAccountName;
	private static JTextArea txtrExtraNotes;
	private static JRadioButton rdbtnChequing;
	private static JRadioButton rdbtnSaving;
	private static JRadioButton rdbtnRrspInvestments;
	private static JRadioButton rdbtnOtherInvestments;
	private static JRadioButton rdbtnDebt;
	private static JRadioButton rdbtnCash;
	private static final String TEST_DATE = "2016/01/24";
	private static final String TEST_PREFIX = "testPrefix";
	private static final String TEST_BANKNAME = "testBankName";
	private static final String TEST_BALANCE = "10.00";
	private static final String TEST_ACCOUNT_NAME = "testAccountName";
	private static final String TEST_TXTR_EXTRA_NOTES = "this is content for testing";
	private static AddAccountDialog data;
	private static Bank bankTest;
	private int year;
	private int month;
	private int dayOfMonth;
	private Account accountTest;

	/**
	 * Sets the textfield with default test inputs and selects the chequing
	 * radio button as default selected, also starts a AddAccountDialog object
	 * 
	 * 
	 * @throws Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		textFieldDate = new JTextField();
		textFieldDate.setText(TEST_DATE);
		textFieldPrefix = new JTextField();
		textFieldPrefix.setText(TEST_PREFIX);
		textFieldBankName = new JTextField();
		textFieldBankName.setText(TEST_BANKNAME);
		textFieldBalance = new JTextField();
		textFieldBalance.setText(TEST_BALANCE);
		textFieldAccountName = new JTextField();
		textFieldAccountName.setText(TEST_ACCOUNT_NAME);
		txtrExtraNotes = new JTextArea();
		txtrExtraNotes.setText(TEST_TXTR_EXTRA_NOTES);

		rdbtnChequing = new JRadioButton();
		rdbtnSaving = new JRadioButton();
		rdbtnRrspInvestments = new JRadioButton();
		rdbtnOtherInvestments = new JRadioButton();
		rdbtnDebt = new JRadioButton();
		rdbtnCash = new JRadioButton();

		data = new AddAccountDialog("extra notes", false);
		data.setTextFieldDate(textFieldDate);
		data.setTextFieldPrefix(textFieldPrefix);
		data.setTextFieldBankName(textFieldBankName);
		data.setTextFieldBalance(textFieldBalance);
		data.setTextFieldAccountName(textFieldAccountName);
		data.setTxtrExtraNotes(txtrExtraNotes);

		bankTest = new Bank(TEST_PREFIX, TEST_BANKNAME);

	}

	/**
	 * Creates test to see if bank object is correctly created Note:
	 * assertEquals simplies to equals while debugging was returning false; need
	 * to override an equals method in the bank class to make this work
	 */
	@Test
	public void createNewBankTest() {
		Bank bank = data.createNewBank();
		assertEquals(bankTest, bank);
	}

	/**
	 * Creates test to see if chequing account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountChequingTest() {
		rdbtnChequing.setSelected(true); // by default AddAccountDialog will
											// select this to be true, for
											// testing reset to fault
		data.setRdbtnChequing(rdbtnChequing);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.CHEQUING.getAccountType());
		assertEquals(accountTest, account);
	}

	/**
	 * This is a helper method for all radio button testing This unselects all
	 * radio button for a clean start
	 */
	public void setChequingRadioDefaultOff() {
		rdbtnChequing.setSelected(false); // by default AddAccountDialog will
											// select this to be true, for
											// testing reset to fault
		data.setRdbtnChequing(rdbtnChequing);
		rdbtnSaving.setSelected(false);
		data.setRdbtnSaving(rdbtnSaving);
		rdbtnRrspInvestments.setSelected(false);
		data.setRdbtnRrspInvestments(rdbtnRrspInvestments);
		rdbtnOtherInvestments.setSelected(false);
		data.setRdbtnOtherInvestments(rdbtnOtherInvestments);
		rdbtnDebt.setSelected(false);
		data.setRdbtnDebt(rdbtnDebt);
		rdbtnCash.setSelected(false);
		data.setRdbtnCash(rdbtnCash);
	}

	/**
	 * Creates test to see if saving account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountSavingTest() {
		setChequingRadioDefaultOff();
		rdbtnSaving.setSelected(true);
		data.setRdbtnSaving(rdbtnSaving);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.SAVING.getAccountType());
		assertEquals(accountTest, account);
	}

	/**
	 * Creates test to see if RRSP investment account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountRRSPInvestmentTest() {
		setChequingRadioDefaultOff();
		rdbtnRrspInvestments.setSelected(true);
		data.setRdbtnRrspInvestments(rdbtnRrspInvestments);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.RRSP_INVESTMENTS.getAccountType());
		assertEquals(accountTest, account);
	}

	/**
	 * Creates test to see if other investment account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountOtherInvestmentTest() {
		setChequingRadioDefaultOff();
		rdbtnOtherInvestments.setSelected(true);
		data.setRdbtnOtherInvestments(rdbtnOtherInvestments);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.OTHER_INVESTMENTS.getAccountType());
		assertEquals(accountTest, account);
	}

	/**
	 * Creates test to see if debt account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountDebtsTest() {
		setChequingRadioDefaultOff();
		rdbtnDebt.setSelected(true);
		data.setRdbtnDebt(rdbtnDebt);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.DEBITS.getAccountType());
		assertEquals(accountTest, account);
	}

	/**
	 * Creates test to see if cash account is correctly made
	 * Note: this test createNewAccount() but since the method is big 
	 * I have broken it down to the account types
	 */
	@Test
	public void createAccountCashTest() {
		setChequingRadioDefaultOff();
		rdbtnCash.setSelected(true);
		data.setRdbtnCash(rdbtnCash);
		Account account = data.createNewAccount();
		accountTest = new Account(TEST_ACCOUNT_NAME, bankTest,
				Account.Types.CASH.getAccountType());
		assertEquals(accountTest, account);

	}

	/**
	 * There is no parseDateTextFieldYear() in AddAccountDialog, but this is
	 * just to test that the string parsing is correct
	 */
	@Test
	public void parseDateTextFieldYearTest() {
		assertEquals(2016,
				Integer.parseInt(textFieldDate.getText().substring(0, 4)));
	}

	/**
	 * There is no parseDateTextFieldMonth() in AddAccountDialog, but this is
	 * just to test that the string parsing is correct
	 */
	@Test
	public void parseDateTextFieldMonthTest() {
		assertEquals(01,
				Integer.parseInt(textFieldDate.getText().substring(5, 7)));
	}

	/**
	 * There is no parseDateTextFieldDayInMontTest() in AddAccountDialog, but
	 * this is just to test that the string parsing is correct
	 */
	@Test
	public void parseDateTextFieldDayInMonthTest() {
		assertEquals(24,
				Integer.parseInt(textFieldDate.getText().substring(8, 10)));
	}

	/**
	 * This date parsing helper is used in
	 * createNewAccountWithOpeningBalanceTest(); Sets the date variables
	 */
	public void parseDateTextFieldHelper() {
		year = Integer.parseInt(textFieldDate.getText().substring(0, 4));
		month = Integer.parseInt(textFieldDate.getText().substring(5, 7));
		dayOfMonth = Integer.parseInt(textFieldDate.getText().substring(8, 10));
	}
	
	/**
	 * This method test the createNewAccountWithOpeningBalance() but account object is not
	 * made yet here. Use this method to help test specific account types to make
	 * a balance object.
	 * Note: In order to test the createNewAccountWithOpeningBalance(), we need to
	 * specify the accountTest object first because there are many versions,
	 * then parse out the date textfield to set the date, then get the amount
	 * and change the string value to a float type correctly.
	 */
	public void createNewAccountWithOpeningBalanceHelper() {
		parseDateTextFieldHelper();
		Balance balance = data.createNewAccountWithOpeningBalance();
		LocalDate date = LocalDate.of(year, month, dayOfMonth);
		balance.setDate(date);
		JTextField amount = data.getTextFieldBalance();
		// this before was not being set, i thought i set it in expectedResult but
		// that's a different object!
		balance.setAmount(Float.parseFloat(amount.getText()));
		// I think you can actually just insert the values here instead if using get()
		Balance resultExpected = new Balance(bankTest, accountTest,
				date.getYear(), date.getMonthValue(), date.getDayOfMonth(),
				Float.parseFloat(amount.getText()), TEST_TXTR_EXTRA_NOTES);
		assertEquals(resultExpected, balance);
	}
	

	/**
	 * Test to see if a balance object is made when we have a chequing account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceChequingTest() {
		createAccountChequingTest();
		createNewAccountWithOpeningBalanceHelper();
	}
		
	/**
	 * Test to see if a balance object is made when we have a saving account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceSavingTest() {
		createAccountSavingTest();
		createNewAccountWithOpeningBalanceHelper();
	}
	
	/**
	 * Test to see if a balance object is made when we have a RRSP investment account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceRRSPTest() {
		createAccountRRSPInvestmentTest();
		createNewAccountWithOpeningBalanceHelper();
	}
	
	/**
	 * Test to see if a balance object is made when we have a Other investment account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceOtherTest() {
		createAccountOtherInvestmentTest();
		createNewAccountWithOpeningBalanceHelper();
	}
	
	/**
	 * Test to see if a balance object is made when we have a cash account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceCashTest() {
		createAccountCashTest();
		createNewAccountWithOpeningBalanceHelper();
	}
	
	/**
	 * Test to see if a balance object is made when we have a debts account
	 */
	@Test
	public void createNewAccountWithOpeningBalanceDebitTest() {
		createAccountDebtsTest();
		createNewAccountWithOpeningBalanceHelper();
	}
	
	
}





