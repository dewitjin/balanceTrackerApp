/**
 *Project: balanceTrackerBetaV1
 *File: Balance.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The Balance class creates a balance object to be stored in the 
 * balance table in the database.
 * Note: balances are stored in float types; this may lead to slight
 * inaccuracies when dealing with bigger numbers; however, for the
 * purpose of this project float worked well.
 * 
 * @author Dewi Tjin
 *
 */
public class Balance {
	
	private int key; //this is to get the right key that was incremented
	private Bank bank;
	private Account account;
	private LocalDate date;
	private int year;
	private int month;
	private int dayOfMonth;
	private float amount;	//maybe big decimal??
	private String extraNotes;
	
	/**
	 * In order to get the database started, the key is set to 0
	 * so that the primary key (PK) will increment properly.
	 */
	public Balance(){
		key = 0;
	}	
	/**
	 * Method checks if the reference key or PK key is greater than 0,
	 * if it is then the balance already exists inside the table
	 * @return
	 */
	public boolean existsInDatabase() {
		return (key > 0);
	}
	/**
	 * @param referenceKey
	 * @param bank
	 * @param account
	 * @param year
	 * @param month
	 * @param dayOfMonth
	 * @param amount
	 * @param extraNotes
	 */
	public Balance(Bank bank, Account account, int year,
			int month, int dayOfMonth, float amount, String extraNotes) {
		this.bank = bank;
		this.account = account;
		this.year = year;
		this.month = month;
		this.dayOfMonth = dayOfMonth;
		this.amount = amount;
		this.extraNotes = extraNotes;
		setDate(LocalDate.of(year, month, dayOfMonth));
	}
	
	/**
	 * @return the date
	 */
	public LocalDate getDate() {
		return date;
	}
	
	/**
	 * @param date the date to set
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	/**
	 * Gets the date created from the constructor and formats it to yyyy/MM/dd
	 * Note: need to test.
	 */
	public String getDateFormatted(){
		LocalDate date = this.getDate();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
		return date.format(formatter);
	}
		
	/**
	 * When we want to know the key value or the PK of the balance,
	 * we use this method
	 * @return the key
	 */
	public int getKey() {
		//System.out.println("GET key " + key);
		return key; 
	}
	
	/**
	 * sets the key
	 * @param key
	 */
	public void setKey(int key) {
		this.key = key;
	}
	 
	/**
	 * @return the bank
	 */
	public Bank getBank() {
		return bank;
	}
	/**
	 * @param bank the bank to set
	 */
	public void setBank(Bank bank) {
		this.bank = bank;
	}
	/**
	 * @return the account
	 */
	public Account getAccount() {
		return account;
	}
	/**
	 * @param account the account to set
	 */
	public void setAccount(Account account) {
		this.account = account;
	}
	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}
	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	/**
	 * @return the month
	 */
	public int getMonth() {
		return month;
	}
	/**
	 * @param month the month to set
	 */
	public void setMonth(int month) {
		this.month = month;
	}
	/**
	 * @return the day
	 */
	public int getDayOfMonth() {
		return dayOfMonth;
	}
	/**
	 * @param day the day to set
	 */
	public void setDayOfMonth(int day) {
		this.dayOfMonth = day;
	}
	/**
	 * @return the amount
	 */
	public float getAmount() {
		return amount;
	}
	/**
	 * @param amount the amount to set
	 */
	public void setAmount(float amount) {
		this.amount = amount;
	}
	/**
	 * @return the extraNotes
	 */
	public String getExtraNotes() {
		return extraNotes;
	}
	/**
	 * @param extraNotes the extraNotes to set
	 */
	public void setExtraNotes(String extraNotes) {
		this.extraNotes = extraNotes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "+\n" +
				"Balance [bank="  + bank                  + "\n"
				+ ", account="    + account               + "\n"
				+ ", date="       + "getDateFormatted()"  + "\n"
				+ ", year="       + year                  + "\n"
				+ ", month="      + month                 + "\n"
				+ ", dayOfMonth=" + dayOfMonth            + "\n"
				+ ", amount="     + amount                + "\n"
				+ ", extraNotes=" + extraNotes            + "]";
	}	
}
