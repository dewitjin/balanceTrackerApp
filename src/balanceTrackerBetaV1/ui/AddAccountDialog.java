/**
 *Project: balanceTrackerBetaV1
 *File: AddAccountDialog.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.data.Balance;
import balanceTrackerBetaV1.data.Bank;
import balanceTrackerBetaV1.database.dao.AccountDao;
import balanceTrackerBetaV1.database.dao.BalanceDao;
import balanceTrackerBetaV1.database.dao.BankDao;
import balanceTrackerBetaV1.utilities.ApplicationException;
import net.miginfocom.swing.MigLayout;

/**
 * Main purpose is to add a new account data and send the data to the
 * Balance Table 
 * NOTE: In order to create a balance object, a bank and account
 * object has to be made. After the two objects are made, they are also sent to
 * the Bank and Account tables to keep track of them. 
 * TODO: txtrExtraNotes has no character restrains - depending on the screen size, 
 * the alignment of the text file reports produced could be messed up 
 * if the content is too long.
 * 
 * @author Dewi Tjin
 *
 */
public class AddAccountDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = new JPanel();
	private JTextField textFieldDate;
	private JTextField textFieldPrefix;
	private JTextField textFieldBankName;
	private JTextField textFieldBalance;
	private JRadioButton rdbtnChequing;
	private JRadioButton rdbtnSaving;
	private JRadioButton rdbtnRrspInvestments;
	private JRadioButton rdbtnOtherInvestments;
	private JRadioButton rdbtnDebt;
	private JRadioButton rdbtnCash;
	private JTextField textFieldAccountName;
	private JTextArea txtrExtraNotes;

	private static final Logger LOG = LogManager
			.getLogger(AddAccountDialog.class);
	

	/**
	 * Create the dialog. 
	 * 
	 * NOTE: Reading an Exchange Rate: If the USD/CAD exchange
	 * rate is 1.0950, that means it costs 1.0950 Canadian dollars for 1 U.S.
	 * dollar. The first currency listed (USD) always stands for one unit of
	 * that currency; the exchange rate shows how much of the second currency
	 * (CAD) is needed to purchase that one unit of the first (USD). 
	 * Note: use boolean instead of Boolean for isCash because it
	 * forces the variable to be either true, false, or null
	 * TODO: for better user experience store
	 * textfield data somewhere before opening the JList filled with rates. 
	 * Right now when the rate JList is called, all the data in the textfield is gone. 
	 * NOTE: if tracking cash, the user does not have an option on prefix name, in order
	 * for us to update the account later, we are using prefix data to know what 
	 * foreign currency was stored
	 */
	@SuppressWarnings("deprecation")
	public AddAccountDialog(String txtrExtraNotesForCash, boolean isCash) {

		setBounds(100, 100, 752, 361);
		setTitle("ADD NEW ACCOUNT");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("",
				"[36px,grow][92px,grow][73px][117px][63px,grow][]",
				"[][16px][16px][23px][][][grow]"));
		{
			JLabel lblDate = new JLabel("Date");
			lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblDate, "cell 0 0,alignx trailing");
		}
		{
			// output current local date in the textFieldDate
			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy/MM/dd");
			textFieldDate = new JTextField();
			contentPanel.add(textFieldDate, "cell 1 0,growx");
			textFieldDate.setColumns(10);
			textFieldDate.setText(date.format(formatter));
		}

		{
			JLabel lblPrefix = new JLabel("Prefix");
			lblPrefix.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblPrefix, "cell 0 1,alignx trailing");
		}
		{
			textFieldPrefix = new JTextField();
			contentPanel.add(textFieldPrefix, "cell 1 1,growx");
			textFieldPrefix.setColumns(10);
		}
		{
			JLabel lblBankName = new JLabel("Bank Name");
			contentPanel.add(lblBankName, "cell 3 1,alignx trailing");
		}
		{
			textFieldBankName = new JTextField();
			contentPanel.add(textFieldBankName, "cell 4 1,growx");
			textFieldBankName.setColumns(10);
		}
		{
			JLabel lblType = new JLabel("Type");
			lblType.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblType, "cell 0 2,alignx trailing");
		}
		// TODO: find a better way to disable the other buttons after the user
		// returns from the JList filled with rates
		// right now all the radio buttons need to be created first in order for
		// modification to be executed or else a null error being thrown
		// NOTE: for clarity, I have left the initialization of the radio
		// buttons commented within the blocks;
		// WindowBuilder in Eclipse generates code blocks such as below in order
		// to keep code separated, but I need the radio buttons created sooner than the
		// blocks
		rdbtnCash = new JRadioButton("Cash");
		rdbtnChequing = new JRadioButton("Chequing");
		rdbtnSaving = new JRadioButton("Saving");
		rdbtnRrspInvestments = new JRadioButton("RRSP Investments");
		rdbtnOtherInvestments = new JRadioButton("Other Investments");
		rdbtnDebt = new JRadioButton("Credit Cards etc.");

		{
			// this is saying when you create a dialog, and isCash is not yet
			// selected then open up the exchange rate dialog when it is selected.
			// AFTER you choose an exchange rate THEN isCash is true so you
			// shouldn't be able to open up the JList rate again
			if (isCash == false) {
				rdbtnCash.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AddAccountDialog.this.dispose();
						try {
							ExchangeRateJListDialog exchangeRateJListDialog = new ExchangeRateJListDialog(
									null);
							exchangeRateJListDialog
									.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
							exchangeRateJListDialog.setVisible(true);
						} catch (ApplicationException e1) {
							LOG.error(e1.getMessage());
						}
					}
				});
			}
			contentPanel.add(rdbtnCash, "cell 2 3");
		}
		{
			// rdbtnChequing = new JRadioButton("Chequing");
			if (isCash == true) {
				rdbtnCash.setSelected(true);
				rdbtnChequing.setEnabled(false);
				rdbtnSaving.setEnabled(false);
				rdbtnRrspInvestments.setEnabled(false);
				rdbtnOtherInvestments.setEnabled(false);
				rdbtnDebt.setEnabled(false);
				// the rest of the time you can name the prefix whatever you
				// want, but CASH is different because this is the way we know
				// what currency it was before we converted it to Canadian
				if (txtrExtraNotesForCash != null) {
					textFieldPrefix.setText(txtrExtraNotesForCash.substring(4,
							7).trim()
							+ "-" + "CASH");
					textFieldPrefix.enable(false);
				}
			} else {
				rdbtnChequing.setSelected(true); // setting this to true so that
													// at least one button is
													// selected from the start,
													// if not an error will be
													// thrown
			}
			contentPanel.add(rdbtnChequing, "cell 1 2,alignx left");
		}
		{
			// rdbtnSaving = new JRadioButton("Saving");
			contentPanel.add(rdbtnSaving, "cell 2 2,alignx left");
		}
		{
			// rdbtnRrspInvestments = new JRadioButton("RRSP Investments");
			contentPanel.add(rdbtnRrspInvestments, "cell 3 2,alignx left");
		}
		{
			// rdbtnOtherInvestments = new JRadioButton("Other Investments");
			contentPanel.add(rdbtnOtherInvestments, "cell 4 2,alignx left");
		}
		{
			// rdbtnDebt = new JRadioButton("Credit Cards etc.");
			rdbtnDebt.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// turns textFieldBalance text to red, don't check if the
					// checkbox is selected,
					// we have to add a listener and then repaint the panel
					textFieldBalance.setForeground(Color.RED);
					contentPanel.revalidate();
					contentPanel.repaint();
				}
			});
			contentPanel.add(rdbtnDebt, "cell 1 3,alignx left");
		}
		// grouped radio buttons after you add all the buttons onto the panel
		groupRadioButtons();

		{
			JLabel lblAccountName = new JLabel("Account Name");
			lblAccountName.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAccountName, "cell 0 4,alignx trailing");
		}
		{
			textFieldAccountName = new JTextField();
			contentPanel.add(textFieldAccountName, "cell 1 4 4 1,growx");
			textFieldAccountName.setColumns(10);
		}
		{
			JLabel lblOpeningBalance = new JLabel("Opening Balance");
			lblOpeningBalance.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblOpeningBalance, "cell 0 5,alignx trailing");
		}
		{
			textFieldBalance = new JTextField();
			contentPanel.add(textFieldBalance, "cell 1 5 4 1,growx");
			textFieldBalance.setColumns(10);
		}
		{
			txtrExtraNotes = new JTextArea();
			if (isCash == true) {
				txtrExtraNotes.setText(txtrExtraNotesForCash);
			} else {
				txtrExtraNotes.setText("--");
			}
			contentPanel.add(txtrExtraNotes, "cell 0 6 5 1,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnAddMore = new JButton("Add more");
				btnAddMore.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setAllErrorMessages(); // if there are no errors then
												// create the balance object
					}
				});
				buttonPane.add(btnAddMore);
			}
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setAllErrorMessages(); // if there are no errors then
												// create the balance object
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						// close this dialog
						AddAccountDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Grouped the radio buttons together
	 */
	public void groupRadioButtons() {
		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(rdbtnChequing);
		group.add(rdbtnSaving);
		group.add(rdbtnRrspInvestments);
		group.add(rdbtnOtherInvestments);
		group.add(rdbtnDebt);
		group.add(rdbtnCash);
	}

	/**
	 * Checks that the balance field is a number first 
	 * Checks textfield for missing info 
	 * Dispose old dialog and opens up a new blank one 
	 * Note: txtrExtraNotes is optional, don't need to have info in it
	 */
	public void setAllErrorMessages() {
		try {
			Float.parseFloat(textFieldBalance.getText().trim());
			// this needs to be in the try catch block so that the
			// NumberFormatException is caught properly
			if (textFieldDate.getText().length() == 0
					|| textFieldPrefix.getText().length() == 0
					|| textFieldBankName.getText().length() == 0
					|| textFieldBalance.getText().length() == 0
					|| textFieldAccountName.getText().length() == 0) {
				JOptionPane.showMessageDialog(null,
						"All fields must have input", "Missing Info Warning",
						JOptionPane.WARNING_MESSAGE);
				// focus on textfield with missing info??
			} else {
				sendForInputToTables();
				JOptionPane
						.showMessageDialog(
								null,
								"Your account has been added to the database, press add more to continue or quit.");
				// dispose old dialog and creates a new blank one, dispose()
				AddAccountDialog.this.dispose();
				AddAccountDialog dialog = new AddAccountDialog(null, false);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true); // make sure this is set to true
			}
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Balance needs to be a number",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * After clicking on the OK or ADD MORE button, a balance object is created
	 * and send to the balance table, and errors message are shown if there are
	 * missing information
	 */
	public void sendForInputToTables() {
		try {
			Bank bank = createNewBank();
			Account account = createNewAccount();
			Balance balance = createNewAccountWithOpeningBalance();
			// add bank and account data too
			BankDao.getTheInstance().add(bank);
			AccountDao.getTheInstance().add(account);
			BalanceDao.getTheInstance().update(balance);
			// this is to test what is actually inside the db
			// BalanceDao.getTheinstance().getAllBalance();
		} catch (SQLException e1) {
			LOG.error(e1.getMessage());
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}

	}

	/**
	 * Create a new bank to be put in bankDao table
	 * 
	 * @return Bank
	 */
	public Bank createNewBank() {
		Bank bank = new Bank(textFieldPrefix.getText().trim(), textFieldBankName
				.getText().trim());
		LOG.debug("createNewBank() " + bank.toString());
		return bank;
	}

	/**
	 * Create a new account 
	 * Note: if in the future there is a need to add more
	 * radio button options, we need to add more if else statements here, to
	 * capture the selection of the new radio button
	 * 
	 * @return account
	 */
	public Account createNewAccount() {
		Account account = null; // if null throw an error
		Bank bank = createNewBank();

		if (rdbtnChequing.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.CHEQUING.getAccountType());
			LOG.debug("createNewAccount()" + account.toString());
		} else if (rdbtnSaving.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.SAVING.getAccountType());
		} else if (rdbtnRrspInvestments.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.RRSP_INVESTMENTS.getAccountType());
		} else if (rdbtnOtherInvestments.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.OTHER_INVESTMENTS.getAccountType());
		} else if (rdbtnDebt.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.DEBITS.getAccountType());
		} else if (rdbtnCash.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.CASH.getAccountType());
		} else {
			// add more choices of account types and currently add a message for
			// one of them needs to be selected
			JOptionPane.showMessageDialog(null, "Must choose an account type",
					"Missing Info Warning", JOptionPane.WARNING_MESSAGE); 
		}
		LOG.debug("createNewAccount()" + account.toString());
		return account;
	}

	/**
	 * Create a balance object to represent a new account with the opening
	 * balance information. Then send this new balance object to the balanceDao
	 * to store in BalanceDao table 
	 * NOTE: if the cash radio button is selected
	 * then we need to convert the balance with the foreign exchange rate. This
	 * conversion will give us the USD amount, then we need to convert this
	 * amount to CAD because I am storing all my balances in CAD.
	 */
	public Balance createNewAccountWithOpeningBalance() {
		Bank bank = createNewBank();
		Account account = createNewAccount();
		String dateString = textFieldDate.getText().trim();

		int year = Integer.parseInt(dateString.substring(0, 4).trim());
		int month = Integer.parseInt(dateString.substring(5, 7).trim());
		int dayOfMonth = Integer.parseInt(dateString.substring(8, 10).trim());

		Balance balance = new Balance(bank, account, year, month, dayOfMonth,
				createBalance(textFieldBalance.getText().trim()),
				txtrExtraNotes.getText().trim());
		LOG.debug(balance.toString());
		return balance;
	}

	/**
	 * Creates a balance and checks if the cash radio button is selected; if it
	 * is then extra calculation will have to take be executed 
	 * NOTE: made this into a method so we can use it for the UpdateAccountJListDialog too.
	 */
	public float createBalance(String balanceInput) {
		@SuppressWarnings("unused")
		float balanceAmount;
		Double currency_rate_selected = new Double(
				ExchangeRateJListDialog.currency_rate_selected);
		Double exchangeRateCAD_USD = new Double(
				ExchangeRateJListDialog.exchangeRateCAD_USD);

		if (rdbtnCash.isSelected() == true) {
			return balanceAmount = (Float.parseFloat(balanceInput) / currency_rate_selected
					.floatValue()) * exchangeRateCAD_USD.floatValue();
		} else {
			return balanceAmount = Float.parseFloat(balanceInput);
		}
	}
}
