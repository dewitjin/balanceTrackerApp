/**
 *Project: balanceTrackerBetaV1
 *File: SortByBankPrefixWithAccountObjects.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.utilities;

import java.util.Comparator;

import balanceTrackerBetaV1.data.Account;

/**
 * Creates a class that implements comparator to sort accounts by their bank prefix, 
 * uses the compareTo method
 * @author Dewi Tjin
 *
 */
public class SortByBankPrefixWithAccountObjects implements Comparator<Account> {

	@Override
	public int compare(Account one, Account two) {
		return one.getBank().getPrefix().compareTo(two.getBank().getPrefix());		
	}
}
