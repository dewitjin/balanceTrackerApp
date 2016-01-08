/**
 /**
 *Project: balanceTrackerBetaV1
 *File: Bank.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.data;

/**
 * 
 * The bank class stores the name and prefix of banks.
 * NOTE: the prefix of Cash types are set by the AddAccountDialog class
 * to the acronym of foreign currencies dash and the word CASH (i.e. USD-CASH);
 * It is recommended that you don't change this prefix setting because 
 * the AddBalanceDialog class will use the prefix acronym to locate the 
 * foreign currency that was used to input the balance into the database and
 * use the data to convert the new balance to CAD dollars again by getting the 
 * new exchange rate based on the currency used in the AddAccountDialog class.
 * 
 * @author Dewi Tjin
 *
 */
public class Bank {
	private String name;
	private String prefix;
	
	public Bank(){
		//default;
	}
	/**
	 * @param name
	 * @param prefix
	 */
	public Bank(String prefix, String name) {
		this.prefix = prefix;
		this.name = name;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the prefix
	 */
	public String getPrefix() {
		return prefix;
	}
	/**
	 * @param prefix the prefix to set
	 */
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		return "Bank [prefix=" + prefix + " name=" + name + "]";
	}

}
