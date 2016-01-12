/**
 *Project: balanceTrackerBetaV1
 *File: Account.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.data;

import balanceTrackerBetaV1.data.Bank;

/**
 * This class creates the Account class, choices of accounts are currently:
 * chequing; saving; rrspInvestment(including RRSP mutual fund and RRSP TFSA);
 * otherInvestments(stocks etc); credits/debts and cash - listed as enums.
 * NOTE: if addition to account types were added, add an enum option below and update
 * the radio button setting in createNewAccount() in AddAccountDialog class, and the getTotal()
 * in DateRangeDialog class and create the balanceDao getTotal method too.
 * NOTE: the toString() of this class is different from the Eclipse default
 * toString because it is formatted to align properly in the JList created in
 * UpdateAccountJListDialog class, JList calls the toString() by default to show
 * what the list elements will look like
 * 
 * @author Dewi Tjin
 *
 */
public class Account {

	private String name;
	private Bank bank;
	private String type;

	public Account() {
		// default;
	}

	/**
	 * Constructor for an account
	 * 
	 * @param name
	 * @param bank
	 * @param accountType
	 */
	public Account(String name, Bank bank, String type) {
		this.name = name;
		this.bank = bank;
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}

	/**
	 * @param bank
	 *            the bank to set
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() This method formats the string so that
	 * the list in the update account JList will be formatted when users choose
	 * to view account list and update balances using updateAccountJListDialog
	 */
	@Override
	public String toString() {
		return String.format("%-20s %-30s %-30s %-20s", bank.getPrefix(), type,
				name, bank.getName());

	}

	/**
	 * Inner class for account types - enum class
	 * NOTE: if you change the enum values etc. the old balances that were made already will
	 * not automatically be updated. You would need to drop the database and start fresh
	 * to reflect name changes.
	 * @author Dewi Tjin
	 *
	 */
	public enum Types {
		CHEQUING("CHEQUING"), SAVING("SAVING"), RRSP_INVESTMENTS(
				"RRSP_INVESTMENTS"), OTHER_INVESTMENTS("OTHER_INVESTMENTS"), DEBITS(
				"DEBT (credit etc.)"), CASH("CASH");

		// field has to be final
		private final String accountType;

		// constructor has no access modifier - enum rulea
		Types(String accountType) {
			this.accountType = accountType;
		}

		public String getAccountType() {
			return accountType;
		}
	}
	
	/**
	 * Implemented in order to perform JUnit testing assertEquals correctly
	 * TODO: Note: look in AddAccountDialogTest class
	 */
	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (!(o instanceof Account)) {
			return false;
		}
		Account account = (Account) o;

		boolean result = (this.name.equalsIgnoreCase(account.name)
				&& this.bank.equals(account.bank) && this.type
				.equalsIgnoreCase(account.type));

		return result;
	}
}
