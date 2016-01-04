/**
 *Project: balanceTrackerBetaV1
 *File: ExchangeRateJListDialog.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.Position;

import net.miginfocom.swing.MigLayout;

import javax.swing.JList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.utilities.ApplicationException;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * This class populates the JList with a list of available currency and rates
 * that cash balances can be stored in, right now I am testing the
 * https://openexchangerates
 * .org/api/latest.json with my app_id as "". The free version of the API, stores the
 * base currency only in USD so I have to do some extra calculations to change
 * the foreign balances into CANADIAN DOLLARS. NOTE: users are supposed to enter
 * the foreign amount in their foreign currency value. An information panel will
 * remind users to do so, but the money being stored will changed to Canadian
 * dollar. When the user clicks on okay in the AddAccountDialog, there will also
 * be an information dialog to show the conversion value.
 *
 * @author Dewi Tjin
 *
 */
public class ExchangeRateJListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static Logger LOG = LogManager.getLogger(MainFrame.class.getName()); //is it getName or just class?
	private final JPanel contentPanel = new JPanel();
	private DefaultListModel<String> exchangeRateModel;
	private File filepath; // the filePath to where to rates were going to be printed
	private ArrayList<String> exchangeLineContent;
	// NOTE: there was a certificate problem when I had https so I just changed it to http
	private static final String URL = "http://openexchangerates.org/api/latest.json";

	private JList<String> listExchangeRateChoices;
	public static String foreign_currency_name_selected;
	public static double currency_rate_selected;
	public static double exchangeRateCAD_USD; // changed to float before calculations, API uses double
	public static String txtrExtraNotesForCash;
	private String fcName;
	private Account selectAccount;

	/**
	 * Create the dialog.
	 *
	 * @param foreignCurrencyName
	 * @param account
	 *            (using an optional parameter here because the only time I need
	 *            to pass an account is when I am updating a cash account and we
	 *            need to check the exchange rate first, after the rate is
	 *            checked then we open a add balance dialog with the new rate to
	 *            populate the textarea)
	 * @throws ApplicationException
	 */
	public ExchangeRateJListDialog(String foreignCurrencyName,
			Object... account) throws ApplicationException {
		setBounds(100, 100, 450, 300);
		setTitle("View Current Exchange Rates");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		fcName = foreignCurrencyName;
		if (account.length > 0) {
			if (!(account[0] instanceof Account)) {
				throw new ApplicationException("Object is not an account.");
			}
			selectAccount = (Account) account[0];
		}
		{
			getExchangeRates();// loads up list model
			listExchangeRateChoices = new JList<String>(exchangeRateModel);
			JScrollPane scrollPane = new JScrollPane(listExchangeRateChoices);
			listExchangeRateChoices.setSelectedIndex(0); //preventing the null error when the dialog first opens
			// if this is being passed from the AddBalanceDialog when cash is
			// selected then find the foreign currency rate and select it
			if (fcName != null) {
				int index = listExchangeRateChoices.getNextMatch(fcName, 0,
						Position.Bias.Forward);
				if (index != -1) {
					listExchangeRateChoices.setSelectedIndex(index);
					listExchangeRateChoices.ensureIndexIsVisible(index);
				}
			}

			listExchangeRateChoices.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						getClickAndEnterActions();
					}
				}
			});

			listExchangeRateChoices.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						getClickAndEnterActions();
					}
				}
			});
			contentPanel.add(scrollPane, "cell 0 0,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						ExchangeRateJListDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * This method sets the currency name and rate to the AddAccountDialog
	 * variables. It will also update textarea with the name and rate as notes.
	 * Note: before I had the AddAccountDialog panel and txrtExtraNotes as
	 * public and static. But it can't be static because then all the dialogs
	 * will be the same.. causing duplicate PK keys error.
	 */
	public void getClickAndEnterActions() {

		foreign_currency_name_selected = listExchangeRateChoices
				.getSelectedValue().toString().substring(0, 3);
		currency_rate_selected = Float.parseFloat(listExchangeRateChoices
				.getSelectedValue()
				.toString()
				.substring(
						4,
						listExchangeRateChoices.getSelectedValue().toString()
								.length() - 1));
		txtrExtraNotesForCash = "USD/" + foreign_currency_name_selected + " "
				+ currency_rate_selected + " and CAD/USD is "
				+ exchangeRateCAD_USD;

		JOptionPane
				.showMessageDialog(
						null,
						"Enter the balance in the foreign currency you have chosen.  \n"
								+ "The actual balance that will be stored, however, will be converted to CANADIAN dollars.");

		if (fcName != null) {
			AddBalanceDialog addBalanceDialog = new AddBalanceDialog(
					selectAccount, txtrExtraNotesForCash);
			addBalanceDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			addBalanceDialog.setVisible(true);
			ExchangeRateJListDialog.this.dispose();
		} else {
			AddAccountDialog dialog = new AddAccountDialog(
					txtrExtraNotesForCash, true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
			ExchangeRateJListDialog.this.dispose();
		}
	}

	/**
	 * This method goes to the openexchangerates.org and grabs the latest
	 * exchange rate conversion NOTE: the base dollar is US (free version) so we
	 * have to do extra calculations to make sure we can store all values in CAD
	 * dollars. NOTE: right now I am creating a txt file with the json data that
	 * comes back from the API. In the future, instead of grabbing information
	 * from the site we can just read from a txt file to keep it simple. If
	 * users only keep a small number of currencies at a time, it might make
	 * more sense to just read from a txt file instead of grabbing 156 plus
	 * rates that are useless. But then the user would have to manually update
	 * the txt file as well as balances. NOTE: file will be stored in a folder
	 * called exchangeRate NOTE: using json-simple-1.1.jar found on Internet for
	 * json parsing and apache commons TODO: only get the exchange rate if there
	 * isn't already a text file with the current's date??
	 */
	public void getExchangeRates() {
		exchangeLineContent = new ArrayList<String>();
		try {
			URL myURL = new URL(URL);
			URLConnection myURLConnection = myURL.openConnection();
			myURLConnection.connect();
			// note sure why there is a bracket after try...
			try (BufferedReader reader = new BufferedReader(
					new InputStreamReader(myURLConnection.getInputStream()))) {
				String line = null;
				while ((line = reader.readLine()) != null) {
					exchangeLineContent.add(line);
				}
				writeExchangeRateToFile();
			} catch (IOException e) {
				LOG.error(e.getMessage());
			}

			try {
				Double valueAsADouble = 0.0;
				FileReader readFromFile = new FileReader(filepath);
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser
						.parse(readFromFile);

				// rates actually returns an Object but you can make another
				// JSONObject
				JSONObject obj = (JSONObject) jsonObject.get("rates");
				// System.out.println("object: " + obj.get("CAD")); //this how
				// you would get the a specific foreign currency name - this
				// might be something that I would use if I only wanted to load
				// a small amount of currencies in the JList
				exchangeRateCAD_USD = (Double) obj.get("CAD");
				exchangeRateModel = new DefaultListModel<String>();
				// using SortedSet listed the elements in the model in
				// ALPHABETIC order
				@SuppressWarnings("unchecked")
				SortedSet<String> keys = new TreeSet<String>(obj.keySet());
				for (String key : keys) {
					Object value = obj.get(key);
					// if statements double checks that it is a number
					if (value instanceof Number) {
						//API Json returns both double and long, this makes everything double
						valueAsADouble = ((Number) value).doubleValue();
						exchangeRateModel
								.addElement(key + " " + valueAsADouble);
					}
				}

			} catch (ParseException e) {
				LOG.error(e.getMessage());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	/**
	 * Creates a txt file and writes the latest exchange rate to the file NOTE:
	 * this is just an extra function so that users can have a back up of
	 * historical exchange rates NOTE: in the future, I want to disable the
	 * write to file function if the txt file with today's date is already there
	 * - it makes more send to just write it once every day..but right now it
	 * writes/update every time we choose a currency
	 */
	public void writeExchangeRateToFile() {
		LocalDate startDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String FILE_NAME = startDate.format(formatter);
		File report = new File("reports");
		filepath = new File(report + "/" + "exchangeRate" + "/" + FILE_NAME
				+ ".txt");
		// creates a file just to store - not sure if I am going to keep this
		// for future
		File directory = new File(filepath.getParentFile().getAbsolutePath());
		directory.mkdirs();

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(filepath));
			writer.newLine();
			for (String eachLine : exchangeLineContent) {
				writer.write(eachLine + "\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
	}
}
