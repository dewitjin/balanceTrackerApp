/**
 *Project: balanceTrackerBetaV1
 *File: DateRangeDialog.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Balance;
import balanceTrackerBetaV1.utilities.ApplicationException;
import balanceTrackerBetaV1.utilities.SortByBankPrefix;
import net.miginfocom.swing.MigLayout;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * This class creates a dialog that takes two dates to generate a report.
 * 
 * @author Dewi Tjin
 *
 */
public class DateRangeDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldStartDate;
	private JTextField textFieldEndDate;
	private static LocalDate startDate;
	private static LocalDate endDate;

	private static final Logger LOG = LogManager
			.getLogger(DateRangeDialog.class);

	/**
	 * Create the dialog. Takes two date inputs, uses it to grab data from the
	 * balance table, and generates a report with balances made between those
	 * dates
	 */
	public DateRangeDialog() {
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "[][][][][grow]", "[][][]"));
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JLabel lblStartingDate = new JLabel("Start Date");
				lblStartingDate.setAlignmentX(Component.CENTER_ALIGNMENT);
				contentPanel.add(lblStartingDate, "cell 0 2,alignx trailing");
			}
			{
				textFieldStartDate = new JTextField();
				contentPanel.add(textFieldStartDate, "cell 1 2,growx");
				textFieldStartDate.setColumns(10);
				textFieldStartDate.setText("YYYY/MM/DD");
			}
			{
				JLabel lblEndDate = new JLabel("End Date");
				lblEndDate.setAlignmentX(0.5f);
				contentPanel.add(lblEndDate, "cell 3 2,alignx trailing");
			}
			{
				// output current local date in the textFieldDate
				LocalDate date = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter
						.ofPattern("yyyy/MM/dd");
				textFieldEndDate = new JTextField();
				textFieldEndDate.setText(date.format(formatter));
				textFieldEndDate.setColumns(10);
				contentPanel.add(textFieldEndDate, "cell 4 2,growx");
			}

			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							generateDateRangeReport();
							DateRangeDialog.this.dispose();
						} catch (ApplicationException e1) {
							LOG.error(e1.getMessage());
						}
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
						DateRangeDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * Generates a report based on two dates. Send the two dates to the
	 * balanceDao and use a generateReport method to get the balances that needs
	 * to be printed
	 */
	public void generateDateRangeReport() throws ApplicationException {
		try {
			startDate = datesSplitHelper(textFieldStartDate.getText().trim());
			endDate = datesSplitHelper(textFieldEndDate.getText().trim());
			if (startDate.isEqual(endDate)) {
				throw new ApplicationException("Can't input the same date");
			}
			List<Balance> balances = MainFrame.balanceDao.getAllBalances(
					startDate, endDate);
			generateReport("dateRangeReports", balances);
			JOptionPane.showMessageDialog(null, "Your report was generated");
		} catch (ApplicationException e) { // find specific exception
			LOG.error(e.getMessage());
			JOptionPane.showMessageDialog(null, "Can't input the same date");
		} catch (Exception e) { // find specific exception
			LOG.error(e.getMessage());
			JOptionPane
					.showMessageDialog(
							null,
							"Did you leave the slashes in? \n"
									+ " Start date has to be earlier than end date too.");
		}
	}

	/**
	 * This is a helper method to take the textfield input and parse it out to
	 * make a year, month, and day creating a LocalDate
	 * 
	 * @param textfield
	 * @return LocalDate
	 */
	public LocalDate datesSplitHelper(String textFieldInput) {
		String dateString = textFieldInput;
		int year = Integer.parseInt(dateString.substring(0, 4).trim());
		int month = Integer.parseInt(dateString.substring(5, 7).trim());
		int dayOfMonth = Integer.parseInt(dateString.substring(8, 10).trim());
		LocalDate date = LocalDate.of(year, month, dayOfMonth);
		LOG.debug("date " + date);
		return date;
	}

	/**
	 * Generates a date range reports.
	 * 
	 * @param directoryName
	 * @param balances
	 */
	public void generateReport(String directoryName,
			List<Balance> balancesByRange) {
		try {

			float dateRangeTotal = getTheDateRangeTotal();
			DateTimeFormatter formatter = DateTimeFormatter
					.ofPattern("yyyy-MM-dd");
			String FILE_NAME = startDate.format(formatter) + "--TO--"
					+ endDate.format(formatter);
			File report = new File("reports");
			File filepath = new File(report + "/" + directoryName + "/"
					+ FILE_NAME + ".txt");
			File directory = new File(filepath.getParentFile()
					.getAbsolutePath());
			directory.mkdirs();
			BufferedWriter writer = new BufferedWriter(new FileWriter(filepath)); // name
																					// of
																					// file
																					// not
																					// directory??
			writer.newLine();
			writer.write(MainFrame.HEADER);
			writer.newLine();
			writer.write(MainFrame.DASHES);
			Collections.sort(balancesByRange, new SortByBankPrefix());
			MainFrame.writeBalanceReportLine(balancesByRange, writer);
			writer.newLine();
			writer.write(MainFrame.DASHES);
			writer.write(String.format(MainFrame.TOTAL_FORMAT, "TOTAL NET",
					dateRangeTotal));
			writer.flush();
			writer.close();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	/**
	 * Helper method to get the total of for the date range report NOTE: total
	 * was returning incorrect numbers because the total coming out from the
	 * database was not rounded. When printing debug messages format the
	 * variable as a float and also format the output in the report as a float
	 * i.e. MainFrame.TOTAL_FORMAT
	 * 
	 * @return dateRangeTotal
	 */
	public float getTheDateRangeTotal() {
		float total = 0f;
		float startDateTotalBalance = getTotal(startDate);
		float endDateTotalBalance = getTotal(endDate);
		// LOG.debug(String.format(" START %-20.2f", startDateTotalBalance));
		// LOG.debug(String.format(" START as normal float" +
		// startDateTotalBalance));
		// LOG.debug(String.format(" end %-20.2f", endDateTotalBalance));
		total = startDateTotalBalance + endDateTotalBalance;
		LOG.debug(String.format(" total %-20.2f", total));
		return total;
	}

	/**
	 * Helper method to get the total by passing in a date
	 * 
	 * @param date
	 *            Note: when adding a new type in the add account checkbox
	 *            include a getTotal method for the new type here too
	 */
	public float getTotal(LocalDate date) {
		float total = 0F;
		try {
			total = MainFrame.balanceDao.getTotalChequingAmount(date)
					+ MainFrame.balanceDao.getTotalSavingAmount(date)
					+ MainFrame.balanceDao.getTotalRRSP_InvestmentAmount(date)
					+ MainFrame.balanceDao.getTotalOthersAmount(date)
					+ MainFrame.balanceDao.getCashTotalAmount(date)
					- MainFrame.balanceDao.getTotalDebitAmount(date);
			LOG.debug(total);
			// don't think you need this here return total;
		} catch (SQLException e) {
			LOG.error(e.getMessage());
		}
		return total;
	}
}
