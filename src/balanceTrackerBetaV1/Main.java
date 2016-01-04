/**
 *Project: balanceTrackerBetaV1
 *File: Main.java
 *Date: Jan 1, 2016
 *Time: 3:32:57 PM
 */
package balanceTrackerBetaV1;

import javax.swing.JDialog;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.ui.LoginDialog;

/**
 * Note: when using log4j2 straight from download, you will need to run
 * -Dlog4j.configurationFile=log_config.xml in VM or terminal to start log,
 * works with the log_config.xml file 
 * NOTE: instead of having to set the system property with the above configurations before 
 * running the App every time, I made a folder call resources then added the
 * class path by changing property setting (one of the options after right clicking), 
 * then insert a configuration file call log4j2.xml 
 * NOTE: the file has to be called log4j2.xml in order for the logger to work properly
 * 
 * TODO: before making jar file after any modifications, turn log debug/trace off and 
 * just run error mode - to do this we go to log.properties and add ERROR instead of debug or
 * trace. Then in log4j2.xml change root level to <Root level="error">.
 * 
 * TODO: delete APP ID in ExchangeRateJListDialog.class
 * 
 * @author Dewi Tjin
 *
 */
public class Main {

	private static final Logger LOG = LogManager.getLogger(Main.class);

	public static final String LOGIN_USERNAME = "";
	public static final String LOGIN_PASSWORD = "";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// start with LoginDialog
		try {
			setLook();
			LoginDialog dialog = new LoginDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			LOG.debug(e.getMessage());
		}
	}

	/**
	 * Setting the look and feel of UI
	 */
	public static void setLook() {
		try {
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (Exception e) {
			LOG.debug(e.getMessage());
			;
		}
	}

}
