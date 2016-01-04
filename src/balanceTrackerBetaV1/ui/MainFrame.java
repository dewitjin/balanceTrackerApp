/**
 *Project: balanceTrackerBetaV1
 *File: MainFrame.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */

package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import balanceTrackerBetaV1.data.Account;  // note remember to import the right data - there's a main packgae that I haven't deleted
import balanceTrackerBetaV1.data.Account.Types;
import balanceTrackerBetaV1.data.Balance;
import balanceTrackerBetaV1.data.Bank;
import balanceTrackerBetaV1.database.Database;
import balanceTrackerBetaV1.database.dao.AccountDao;
import balanceTrackerBetaV1.database.dao.BalanceDao;
import balanceTrackerBetaV1.database.dao.BankDao;
import balanceTrackerBetaV1.utilities.ApplicationException;
import balanceTrackerBetaV1.utilities.SortByBankPrefix;

import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Builds the mainframe structure, starts the database and builds the tables
 * using init() TODO: right now if an account/bank is updated with new data, the
 * balance table doesn't get automatically updated so if I decide to change the
 * name of an account in the future, the future balance input for that account
 * will be different, even though the account should be the same NOTE: there was
 * a variable being printed out on the console that I didn't know where it was
 * coming from; until I realize it was from a System.out.println() instead of
 * LOG.debug statement because a log statement would show from what class the
 * variable came from //TODO: make jar into a image...
 *
 * @author Dewi Tjin
 *
 */


public class MainFrame extends JFrame {

	private static Logger LOG = LogManager.getLogger(MainFrame.class.getName()); //is this the right params??
	private JPanel contentPane;
	private static final String DB_PROPERTIES_FILENAME = "db.properties";
	public static BalanceDao balanceDao;
	public static AccountDao accountDao;
	public static BankDao bankDao;
	private static List<Account> allAccounts;
	private static List<Bank> allBanks; //haven't done anything with this

	//NOTE: the spaces in between the %s symbols in string format counts as a character so BECAREFUL
	public static final String HEADER = String.format("%-10s %-10s   %-10s  %-20s  %-30s  %-10s  %-500s", "PK KEY", "DATE", "PREFIX", "ACCOUNT TYPE", "ACCOUNT NAME", "BALANCE", "EXTRA NOTES");
	public static final String REPORT_FORMAT =        "%-10s %-4s/%-2s/%-2s   %-10s  %-20s  %-30s  %-10.2f  %-1000s"; //for the line that outputs accounts that are not debits
	public static final String REPORT_FORMAT_DEBIT =  "%-10s %-4s/%-2s/%-2s   %-10s  %-20s  %-30s  -%-9.2f  %-1000s";
	public static final String TOTAL_FORMAT =                                           "%90s = %-20.2f"; //the total was incorrect because I added s instead of f
	public static final String DASHES = new String(new char[170]).replace(
			"\0", "-") + "\n";

	public static float fullReportsTotalBalance;
	public static float fullReportsWithOneDateTotalBalance;
	/**
	 * Create the frame.
	 * TODO: after every 57 characters append \n and then start next letter after padding 108 blank spaces
	 */
	public MainFrame() {

		init();
		//testingDbContents(); //UNCOMMENT THIS FOR TESTING
		//testingExchangeRateRetrival(){}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnLogin = new JMenu("Login");
		mnLogin.setMnemonic(KeyEvent.VK_L);
		menuBar.add(mnLogin);

		JMenuItem mntmAddNewAccount = new JMenuItem("Add new account");
		mntmAddNewAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//opens add account dialog
				try {
					AddAccountDialog dialog = new AddAccountDialog(null, false);
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception ex) {
					LOG.error(ex.getMessage());
				}
			}
		});
		mntmAddNewAccount.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1,ActionEvent.ALT_MASK )); //for some reason if I
		//put keyEvent.VK_N it won't work here... test again later
		mnLogin.add(mntmAddNewAccount);

		JMenuItem mntmUpdateAccounts = new JMenuItem("Update accounts");
		mntmUpdateAccounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//open jlist of accounts avaiable to update
				//got to Accounts table and grab all accounts first
				UpdateAccountJListDialog dialog;
				try {
					dialog = new UpdateAccountJListDialog(accountDao.getAllAccounts());
					dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					dialog.setVisible(true);
				} catch (Exception e1) {
					LOG.error(e1.getMessage());
				}

			}
		});
		mntmUpdateAccounts.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2,ActionEvent.ALT_MASK ));
		mnLogin.add(mntmUpdateAccounts);

//		add cash radio button option instead of this
//		JMenuItem mntmUpdateCash = new JMenuItem("Update cash");
//		mntmUpdateCash.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3,ActionEvent.ALT_MASK ));
//		mnLogin.add(mntmUpdateCash);

		JMenuItem mntmQuit = new JMenuItem("Quit");
		mntmQuit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		KeyStroke f1KeyStroke = KeyStroke.getKeyStroke("F1");
		mntmQuit.setAccelerator(f1KeyStroke);
		mnLogin.add(mntmQuit);

		JMenu mnGenerateReports = new JMenu("Generate Reports");
		mnGenerateReports.setMnemonic(KeyEvent.VK_R);
		menuBar.add(mnGenerateReports);

		JMenuItem mntmFullReport = new JMenuItem("Full report");
		mntmFullReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//show EVERYTHING in the balance table in the database
				generateReport("fullReports");
				JOptionPane.showMessageDialog(null, "Report has been generated");
			}
		});
		mntmFullReport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F,ActionEvent.ALT_MASK ));
		mnGenerateReports.add(mntmFullReport);

		JMenuItem mntmByTodaysDate = new JMenuItem("By Today's Date");
		mntmByTodaysDate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//show everything in the balance table in the database that has today's date
				generateReport("fullReportsWithOneDate");
				JOptionPane.showMessageDialog(null, "Report has been generated");
			}
		});
		mntmByTodaysDate.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B,ActionEvent.ALT_MASK ));
		mnGenerateReports.add(mntmByTodaysDate);

		JMenuItem mntmByDates = new JMenuItem("By dates");
		mntmByDates.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DateRangeDialog drb = new DateRangeDialog();
				drb.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				drb.setVisible(true);
			}
		});
		mntmByDates.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D,ActionEvent.ALT_MASK ));
		mnGenerateReports.add(mntmByDates);

//		NOTE: taking this out for now
//		JMenuItem mntmByAccount = new JMenuItem("By account");
//		mntmByAccount.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.ALT_MASK ));
//		mnGenerateReports.add(mntmByAccount);
//
		JMenu mnAbout = new JMenu("About");
		mnAbout.setMnemonic(KeyEvent.VK_A);
		menuBar.add(mnAbout);

//		NOTE: taking this out for now
//		JMenuItem mntmFullReportFromLast = new JMenuItem("Full Report from last login");
//		mnAbout.add(mntmFullReportFromLast);

		JMenuItem mntmAboutThisApp = new JMenuItem("About this App");
		mntmAboutThisApp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "© Copyright 2016 DEWI Y. TJIN All Rights Reserved \n" +
						"Special THANKS to Matthew Lawarence who helped me figure stuff out."
						);
			}
		});
		mnAbout.add(mntmAboutThisApp);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}

	/**
	 * Loads the database property file and starts the database,
	 * and then start the each dao: BankDao, BalanceDao, AccountDao
	 * NOTE: init() is important because it resets static variable reference key
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws ApplicationException
	 */
	private void init(){
		File file = new File(DB_PROPERTIES_FILENAME);
		Properties dbProperties = new Properties();
		if (!file.exists()) {
			LOG.error(String.format(
					"Program cannot start because %s cannot be found.",
					DB_PROPERTIES_FILENAME));
			System.exit(-1);
		}
		try {
			dbProperties.load(new FileInputStream(DB_PROPERTIES_FILENAME));
			Database.init(dbProperties);
		} catch (IOException e) {
			LOG.debug(e.getMessage());
		}

		try {
			if(!Database.tableExists(BankDao.TABLE_NAME_BANKS)){
				//BankDao.init();
				BankDao.getTheInstance().create();
			}else{
				//prints info/debug if the table already exist
			}
			if(!Database.tableExists(AccountDao.TABLE_NAME_ACCOUNTS)){
				//AccountDao.init();
				AccountDao.getTheInstance().create();
			}
			if(!Database.tableExists(BalanceDao.TABLE_NAME_BALANCES)){
				BalanceDao.getTheInstance().create();
			}
			//reset static variable reference key
			BalanceDao.init();
			accountDao = AccountDao.getTheInstance();
			bankDao = BankDao.getTheInstance();
			balanceDao = BalanceDao.getTheInstance();
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}

	}

	/**
	 * Generates a reports.
	 * in the fullReport directory: report will show everything in the balance table
	 * in the fullReportWithOneDate directory: report will show all the balances with the date
	 * the report was generated on
	 * NOTE: if adding more account types in add account dialog option, make sure to update the total
	 * formula below
	 */
	public void generateReport(String directoryName){
		try {
			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd");
			String FILE_NAME = date.format(formatter);

			fullReportsTotalBalance = balanceDao.getTotalChequingAmount() + balanceDao.getTotalSavingAmount() +
					balanceDao.getTotalRRSP_InvestmentAmount() + balanceDao.getTotalOthersAmount()
					+ balanceDao.getCashTotalAmount() - balanceDao.getTotalDebitAmount(); //add everything AND subtract debts

			fullReportsWithOneDateTotalBalance = balanceDao.getTotalChequingAmount(date) + balanceDao.getTotalSavingAmount(date) +
					balanceDao.getTotalRRSP_InvestmentAmount(date) + balanceDao.getTotalOthersAmount(date)
					+ balanceDao.getCashTotalAmount(date) - balanceDao.getTotalDebitAmount(date); //add everything AND subtract debts

			File report = new File("reports");
			File filepath = new File(report + "/" + directoryName + "/" + FILE_NAME + ".txt");
			File directory = new File(filepath.getParentFile().getAbsolutePath());
			directory.mkdirs(); // creates the directory if the folder
								// isn't there, does nothing if it is
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath)); // name of file not directory??
			writer.newLine();
			writer.write(HEADER);
			writer.newLine();
			writer.write(DASHES);

			List<Balance> allBalances = BalanceDao.getTheInstance().getAllBalance();
			Collections.sort(allBalances, new SortByBankPrefix());
			// if we want to reverse Collections.reverse(allBalances);

			//gets all balances with one date
			List<Balance> allBalancesWithOneDate = BalanceDao.getTheInstance().getAllBalance(date);
			Collections.sort(allBalancesWithOneDate, new SortByBankPrefix());

			if(directoryName.equalsIgnoreCase("fullReports")){
				writeBalanceReportLine(allBalances, writer);
				writer.newLine();
				writer.write(DASHES);
				writer.write(String.format(TOTAL_FORMAT,"TOTAL NET", fullReportsTotalBalance));
			}else if(directoryName.equalsIgnoreCase("fullReportsWithOneDate")){
				writeBalanceReportLine(allBalancesWithOneDate, writer);
				writer.newLine();
				writer.write(DASHES);
				writer.write(String.format(TOTAL_FORMAT,"TOTAL NET", fullReportsWithOneDateTotalBalance));
				//TODO: open the file right after
			}
			writer.flush();
			writer.close();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Generate report method helper : gets the line in the report
	 * @param balances
	 * @param writer
	 */
	public static void writeBalanceReportLine(List<Balance> balances, BufferedWriter writer){
		for (Balance balance : balances) {
			try {
				if(balance.getAccount().getType().equals(Account.Types.DEBITS.getAccountType())){
					writer.write(String.format(REPORT_FORMAT_DEBIT,
							balance.getKey(),
							balance.getYear(), balance.getMonth(),
							balance.getDayOfMonth(),
							balance.getBank().getPrefix(),
							balance.getAccount().getType(),
							balance.getAccount().getName(),
							balance.getAmount(),  //here since it is a debit balance, the amount will start with a "-" sybmol, formatting by the REPORT_FORMAT_DEBIT
							balance.getExtraNotes()));
				}else{
					writer.write(String.format(REPORT_FORMAT,
							balance.getKey(),
							balance.getYear(),
							balance.getMonth(),
							balance.getDayOfMonth(),
							balance.getBank().getPrefix(),
							balance.getAccount().getType(),
							balance.getAccount().getName(),
							balance.getAmount(),
							balance.getExtraNotes()));
				}
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}
		}
	}




















	/**
	 * This method is used just for testing.
	 */
	private void testingDbContents(){
		Bank td = new Bank("TD", "TD Bank");
		Bank hsbc = new Bank("HSBC", "Hong Kong Shanghai Bank");

		Account s = new Account("savings", td, Types.SAVING.getAccountType());
		Account c = new Account("chequing", td, Types.CHEQUING.getAccountType());
		Account h = new Account("hsbc savings", hsbc, Types.SAVING.getAccountType());

		Balance a = new Balance(td, s, 2015, 03, 23, 23.34f, "extra notes");
		Balance b = new Balance(td, c, 2015, 03, 24, 23.34f, "extra notes BBB");

		try {
			//banlanceDao.add(a);
			//banlanceDao.add(b);
//			accountDao.add(s);
//			bankDao.add(td);
//			accountDao.add(h);
//			bankDao.getAllBanks();
			accountDao.getAllAccounts();
			try {
				BalanceDao.getTheInstance().getAllBalance();
			} catch (Exception e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}
		} catch (Exception e) {
			LOG.debug(e.getMessage());
		}
	}

	/**
	 * This method is just for testing.
	 */
	public void testingExchangeRateRetrival(){
		try {
			//note: trying to use json-simple-1.1.jar found on Internet for json parsing and apache commons
			//there was a certificate problem when I had https so I just changed it to http
			//this still returns an uncomplete list though - missing bracket at the end..
		    URL myURL = new URL("http://openexchangerates.org/api/latest.json");
		    URLConnection myURLConnection = myURL.openConnection();

		    myURLConnection.connect();
		    InputStream stream = myURLConnection.getInputStream();
		    byte[] data = new byte[stream.available()];
		    stream.read(data);
		    stream.close();
		    String source = new String(data);

		    System.out.println(source);

		    try {

		    	LocalDate startDate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy-MM-dd");
				String FILE_NAME = startDate.format(formatter);
				File report = new File("reports");
				File filepath = new File(report + "/" + "exchangeRate" + "/" + FILE_NAME + ".txt");

//				File directory = new File(filepath.getParentFile().getAbsolutePath());
//				directory.mkdirs();
//				BufferedWriter writer = new BufferedWriter(new FileWriter(filepath)); // name of file not directory??
//				writer.newLine();
//				writer.write(source);
//				writer.flush();
//				writer.close();

				FileReader reader = new FileReader(filepath);
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

				String base = (String) jsonObject.get("base");
				//System.out.println("BASE: " + base);

				Long timestamp = (Long) jsonObject.get("timestamp");
				//System.out.println("timestamp: " + timestamp);

				 // get an array from the JSON object
				//rates is actually another JSONObject when you look at the file... it returns an Object but you can make another JSONObject
				JSONObject obj= (JSONObject) jsonObject.get("rates");
				System.out.println("object: " + obj.get("AED")); //this is where you would be the foreign currency name

				//this prints out the list in sorted order alphabetical order
				SortedSet<String> keys = new TreeSet<String>(obj.keySet());
				for (String key : keys) {
				   Object value = obj.get(key);
				   //if statements double checks that it is a number
				   if(value instanceof Number){
					   Double valueAsADouble = ((Number)value).doubleValue(); //we have to do this because some of he values were integer numbers storing as a long, this makes everything into a double
				   }
				   System.out.println(key + " " + value);
				}

			} catch (Exception e1) {
				LOG.error(e1.getMessage());
			}

		} catch (MalformedURLException e) {
		    LOG.error(e.getMessage());
		}
		catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
}
