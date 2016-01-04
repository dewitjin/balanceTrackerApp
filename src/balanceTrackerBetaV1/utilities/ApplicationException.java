/**
 *Project: balanceTrackerBetaV1
 *File: ApplicationException.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.utilities;

/**
 * A class that extends the Exception class 
 * @author Dewi Tjin
 *
 */

@SuppressWarnings("serial")
public class ApplicationException extends Exception {

	/**
	 * Creates object with a specified message
	 * @param message
	 */
	public ApplicationException(String message){
		super(message);
	}
	
	public ApplicationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ApplicationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApplicationException(Throwable cause) {
		super(cause);
	}
}
