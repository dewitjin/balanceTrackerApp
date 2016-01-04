/**
 *Project: balanceTrackerBetaV1
 *File: SortByBankPrefix.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.utilities;

import java.util.Comparator;
import balanceTrackerBetaV1.data.Balance;

/**
 * Creates a class that implements comparator to sort balances by their bank prefix, 
 * uses the compareTo method
 * @author Dewi Tjin 
 *
 */
public class SortByBankPrefix implements Comparator<Balance> {

	@Override
	public int compare(Balance balanceOne, Balance balanceTwo) {
		return balanceOne.getBank().getPrefix().compareTo(balanceTwo.getBank().getPrefix());		
	}
}
