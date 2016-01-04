/**
 *Project: balanceTrackerBetaV1
 *File: LoginDialog.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextArea;

public class LoginDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldUsername;
	private JPasswordField passwordField;

	private static final Logger LOG = LogManager.getLogger(LoginDialog.class);

	/**
	 * Create the dialog.
	 */
	public LoginDialog() {
		setBounds(100, 100, 450, 300);
		setTitle("Beta Balance Tracker - BBT (not bubble milk tea)");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[][grow][grow]", "[][][][][grow]"));
		{
			JLabel lblUsername = new JLabel("USERNAME:  ");
			contentPanel.add(lblUsername, "flowx,cell 1 1,alignx trailing");
		}
		{
			JLabel lblPassword = new JLabel("PASSWORD:  ");
			contentPanel.add(lblPassword, "flowx,cell 1 2,alignx trailing");
		}
		{
			JTextArea textAreaMessage = new JTextArea();
			textAreaMessage.setEditable(false);
			textAreaMessage.setLineWrap(true);
			textAreaMessage.setWrapStyleWord(true);
			textAreaMessage
					.setText("BBT is the ultimate App used to track your financial balances. "
							+ "Start by creating your accounts, updating your balances, and "
							+ "creating text reports for your records.");
			// textAreaMessage.setWrapStyleWord(true);
			contentPanel.add(textAreaMessage, "cell 1 4 2 1,grow");
		}
		{
			textFieldUsername = new JTextField();
			contentPanel.add(textFieldUsername, "cell 1 1,growx");
			textFieldUsername.setColumns(10);
		}
		{
			passwordField = new JPasswordField();
			contentPanel.add(passwordField, "cell 1 2,growx");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@SuppressWarnings("deprecation")
					public void actionPerformed(ActionEvent e) {
						if (Main.LOGIN_USERNAME
								.equalsIgnoreCase(textFieldUsername.getText())
								&& Main.LOGIN_PASSWORD
										.equalsIgnoreCase(passwordField
												.getText())) {
							// MainFrame opens
							EventQueue.invokeLater(new Runnable() {
								public void run() {
									try {
										MainFrame frame = new MainFrame();
										frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
										frame.setVisible(true);
									} catch (Exception e) {
										LOG.error(e.getMessage());
									}
								}
							});
							LoginDialog.this.dispose();
						} else {
							JOptionPane.showMessageDialog(null,
									"Incorrect login.");
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
						System.exit(0);
						LoginDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
