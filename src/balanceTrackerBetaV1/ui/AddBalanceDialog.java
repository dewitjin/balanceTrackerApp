/**
 *Project: balanceTrackerBetaV1
 *File: AddBalanceDialog.java
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
import balanceTrackerBetaV1.database.dao.BalanceDao;
import net.miginfocom.swing.MigLayout;

/**
 * Purpose is to add a new balance to table (basically it updates an account
 * with a new balance) - dialog is almost the same as AddAccountDialog but
 * some fields are not editable 
 * TODO: check for null length == 0 and show error
 * message to user to have to input something
 * 
 * @author Dewi Tjin
 *
 */
public class AddBalanceDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldDate;
	private JTextField textFieldPrefix;
	private JTextField textFieldBankName;
	private JTextField textFieldBalance;
	private JRadioButton rdbtnChequing;
	private JRadioButton rdbtnSaving;
	private JRadioButton rdbtnRrspInvestments;
	private JRadioButton rdbtnOtherInvestments;
	private JRadioButton rdbtnDebt;
	private JTextField textFieldAccountName;
	private JTextArea txtrExtraNotes;

	private static final Logger LOG = LogManager
			.getLogger(AddBalanceDialog.class);
	private JRadioButton rdbtnCash;

	/**
	 * Create the dialog. The dialog has the main text fields of account and
	 * bank data visible but not editable. 
	 * TODO: make this modal or not modal
	 * depending on JList dialog? Modal dialog box — A dialog box that blocks
	 * input to some other top-level windows NOTE: if you create another account
	 * type you have to make sure you add the checkCheckBox() method here so
	 * that the dialog will have that type checked if the account being passed
	 * in was checked with the new type
	 */
	public AddBalanceDialog(Account accountSelect, String txtrExtraNotesForCash) {

		setBounds(100, 100, 725, 341);
		setTitle("ADD A NEW BALANCE");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setLocationRelativeTo(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("",
				"[36px,grow][92px,grow][73px][117px][63px,grow][]",
				"[16px][16px][23px][][][][grow]"));
		{
			JLabel lblDate = new JLabel("Date");
			lblDate.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblDate, "cell 0 0,alignx trailing");
		}
		{
			textFieldDate = new JTextField();
			contentPanel.add(textFieldDate, "cell 1 0 4 1,growx");
			textFieldDate.setColumns(10);
			// output current local date in the textFieldDate
			LocalDate date = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy/MM/dd");
			textFieldDate.setText(date.format(formatter));

		}
		{
			JLabel lblPrefix = new JLabel("Prefix");
			lblPrefix.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblPrefix, "cell 0 1,alignx trailing");
		}
		{
			textFieldPrefix = new JTextField();
			textFieldPrefix.setEnabled(false);
			textFieldPrefix.setEditable(false);
			contentPanel.add(textFieldPrefix, "cell 1 1,growx");
			textFieldPrefix.setColumns(10);
		}
		{
			{
				JLabel lblBankName = new JLabel("Bank Name");
				contentPanel.add(lblBankName, "cell 2 1,alignx trailing");
			}
		}
		textFieldBankName = new JTextField();
		textFieldBankName.setEnabled(false);
		contentPanel.add(textFieldBankName, "cell 3 1 2 1,growx");
		textFieldBankName.setColumns(10);
		{
			JLabel lblType = new JLabel("Type");
			lblType.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblType, "cell 0 2,alignx trailing");
		}
		{
			rdbtnChequing = new JRadioButton("Chequing");
			rdbtnChequing.setEnabled(false);
			checkCheckBox(Account.Types.CHEQUING.getAccountType(),
					rdbtnChequing, accountSelect);
			contentPanel.add(rdbtnChequing, "cell 1 2,alignx left");
		}
		{
			rdbtnSaving = new JRadioButton("Saving");
			rdbtnSaving.setEnabled(false);
			checkCheckBox(Account.Types.SAVING.getAccountType(), rdbtnSaving,
					accountSelect);
			contentPanel.add(rdbtnSaving, "cell 2 2,alignx left");
		}
		{
			rdbtnRrspInvestments = new JRadioButton("RRSP Investments");
			rdbtnRrspInvestments.setEnabled(false);
			checkCheckBox(Account.Types.RRSP_INVESTMENTS.getAccountType(),
					rdbtnRrspInvestments, accountSelect);
			contentPanel.add(rdbtnRrspInvestments, "cell 3 2,alignx left");
		}
		{
			rdbtnOtherInvestments = new JRadioButton("Other Investments");
			rdbtnOtherInvestments.setEnabled(false);
			checkCheckBox(Account.Types.OTHER_INVESTMENTS.getAccountType(),
					rdbtnOtherInvestments, accountSelect);
			contentPanel.add(rdbtnOtherInvestments, "cell 4 2,alignx left");
		}
		{
			rdbtnDebt = new JRadioButton("Credit Cards etc.");
			rdbtnDebt.setEnabled(false);
			checkCheckBox(Account.Types.DEBITS.getAccountType(), rdbtnDebt,
					accountSelect);
			contentPanel.add(rdbtnDebt, "cell 1 3,alignx left");
		}
		{
			rdbtnCash = new JRadioButton("Cash");
			rdbtnCash.setEnabled(false);
			checkCheckBox(Account.Types.CASH.getAccountType(), rdbtnCash,
					accountSelect);
			contentPanel.add(rdbtnCash, "cell 2 3");
		}
		// grouped radio buttons
		groupRadioButtons();
		{
			JLabel lblAccountName = new JLabel("Account Name");
			lblAccountName.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblAccountName, "cell 0 4,alignx trailing");
		}
		{
			textFieldAccountName = new JTextField();
			textFieldAccountName.setEnabled(false);
			textFieldAccountName.setEditable(false);
			contentPanel.add(textFieldAccountName, "cell 1 4 4 1,growx");
			textFieldAccountName.setColumns(10);
		}
		{
			JLabel lblBalance = new JLabel("Balance");
			lblBalance.setAlignmentX(Component.CENTER_ALIGNMENT);
			contentPanel.add(lblBalance, "cell 0 5,alignx trailing");
		}
		{
			textFieldBalance = new JTextField();
			contentPanel.add(textFieldBalance, "cell 1 5 4 1,growx");
			// set a flag to change text color
			// because selectAccount variable being passed into this constructor
			// will either
			// be selected or not as a debt, this is how we would change the
			// text, not
			// by adding a listening and revalidating/repainting the panel
			if (rdbtnDebt.isSelected() == true) {
				textFieldBalance.setForeground(Color.RED);
				contentPanel.revalidate();
				contentPanel.repaint();
			}
			textFieldBalance.setColumns(10);
		}
		{
			txtrExtraNotes = new JTextArea();
			if (txtrExtraNotesForCash != null) {
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
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {							
							// code needs to be a try catch in order for
							// errors to be CAUGHT 
							// we don't need to add bank and account data to the
							// bank/account tables again because
							// we got the selectAccount object from looking it
							// up in the tables;
							// we just need to make a balance object and send it
							// to the balance table
							setAllErrorMessages();
							BalanceDao.getTheInstance().update(
									createNewAccountWithOpeningBalance());
							// this is to test what is actually inside the db
							// BalanceDao.getTheinstance().getAllBalance();
							JOptionPane.showMessageDialog(null,
									"Your account has been updated");
							AddBalanceDialog.this.dispose();
						} catch (SQLException e1) {
							LOG.error(e1.getMessage());
						} catch (Exception e1) {
							LOG.error(e1.getMessage());
						}
						;
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
						AddBalanceDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
			// has to be run after all the items are made
			setUneditableTexyFields(accountSelect);
		}
	}

	/**
	 * Grouped the radio buttons together NOTE: when adding another type to the
	 * account, you have to add the type to the group too
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
	 */
	public void setAllErrorMessages() {
		try {
			Float.parseFloat(textFieldBalance.getText().trim());
			JOptionPane.showMessageDialog(null, "Balance added");
			// dispose old dialog and creates a new blank one
			AddBalanceDialog.this.dispose();
		} catch (NumberFormatException e1) {
			JOptionPane.showMessageDialog(null, "Balance needs to be a number");
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Select the checkbox that came with the selectAccount object
	 */
	public void checkCheckBox(String type, JRadioButton selectRadio,
			Account accountSelect) {
		if (accountSelect.getType().equalsIgnoreCase(type)) {
			selectRadio.setSelected(true);
		}
	}

	/**
	 * Method to set all the uneditable textfields from the account that was
	 * passed to this class
	 * 
	 * @param accountToAddABalanceTo
	 */
	public void setUneditableTexyFields(Account accountToAddABalanceTo) {
		textFieldPrefix.setText(accountToAddABalanceTo.getBank().getPrefix());
		textFieldBankName.setText(accountToAddABalanceTo.getBank().getName());
		textFieldAccountName.setText(accountToAddABalanceTo.getName());
	}

	/**
	 * Create a new bank to be put in bankdao table
	 * 
	 * @return
	 */
	public Bank createNewBank() {
		Bank bank = new Bank(textFieldPrefix.getText().trim(),
				textFieldBankName.getText().trim());
		return bank;
	}

	/**
	 * Create a new account NOTE: when adding new account types, you have to
	 * append another "else if" statement to build another account object
	 * 
	 * @return
	 */
	public Account createNewAccount() {
		Account account = null; // if null throw an error
		Bank bank = createNewBank();
		if (rdbtnChequing.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.CHEQUING.getAccountType());
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
					Account.Types.DEBITS.getAccountType()); // this was missing
		} else if (rdbtnCash.isSelected() == true) {
			account = new Account(textFieldAccountName.getText().trim(), bank,
					Account.Types.CASH.getAccountType());
		} else {
			// throw error?
		}
		// LOG.debug("createNewAccount()" + account.toString());
		return account;
	}

	/**
	 * Create a balance object to represent a new balance row with the updated
	 * balance info and the account/bank data that was passed to this class. 
	 * Then send this new balance object to the balanceDao to store in BalanceDao
	 * table 
	 * NOTE: technically we aren't creating a new bank or account object
	 * because those textfields are not editable, but in order for a new balance
	 * to be created we need a way to grab those data again
	 */
	public Balance createNewAccountWithOpeningBalance() {
		Bank bank = createNewBank();
		Account account = createNewAccount();
		String dateString = textFieldDate.getText().trim();
		int year = Integer.parseInt(dateString.substring(0, 4).trim());
		int month = Integer.parseInt(dateString.substring(5, 7).trim());
		int dayOfMonth = Integer.parseInt(dateString.substring(8, 10).trim());

		if (rdbtnCash.isSelected() == true) {
			AddAccountDialog obj = new AddAccountDialog(null, true);  //the only reason we created and addaccountdialog object here to use the createBalance method
			float balanceValue = obj.createBalance(textFieldBalance.getText()
					.trim()); // this method will take care of cash balances and coverting them properly
			Balance balance = new Balance(bank, account, year, month,
					dayOfMonth, balanceValue, txtrExtraNotes.getText().trim());
			// LOG.debug(balance.toString());
			return balance;
		} else {
			AddAccountDialog obj = new AddAccountDialog(null, false);
			float balanceValue = obj.createBalance(textFieldBalance.getText()
					.trim()); // this method will take care of cash balances
								// etc.
			Balance balance = new Balance(bank, account, year, month,
					dayOfMonth, balanceValue, txtrExtraNotes.getText().trim());
			// LOG.debug(balance.toString());
			return balance;
		}
	}
}
