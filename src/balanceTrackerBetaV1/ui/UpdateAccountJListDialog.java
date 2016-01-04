/**
 *Project: balanceTrackerBetaV1
 *File: UpdateAccountJListDialog.java
 *Date: Jan 1, 2016
 *Time: 5:41:58 PM
 */
package balanceTrackerBetaV1.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

import javax.swing.JList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import balanceTrackerBetaV1.data.Account;
import balanceTrackerBetaV1.utilities.SortByBankPrefixWithAccountObjects;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * This class shows in a list what accounts are available to create balance
 * objects in. You can either select list element, double click on element, or
 * select list element and then press okay to start the add balance dialog.
 * NOTE: placing a closed status on an account does not remove the account on the JLIST
 * because we want to still see the account on the list BUT it will add a CLOSED status
 * NOTE: then we update the account tables to reflect the closed status too, the bank tables can be left alone
 * 
 * @author Dewi Tjin
 *
 */
public class UpdateAccountJListDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final Font FIXEDFONT = new Font("Monospaced", Font.PLAIN, 12);
	private final JPanel contentPanel = new JPanel();
	@SuppressWarnings("unused")
	private List<Account> allAccountsToView = new ArrayList<Account>();
	private SortedListModel accountSortedModel = new SortedListModel();
	private JList<Account> listOfAccounts = new JList<Account>();
		
	private static final Logger LOG = LogManager
			.getLogger(UpdateAccountJListDialog.class);

	/**
	 * Create dialog that has a list of accounts available to update, when you
	 * double click on a list element, select and press enter, or select and
	 * press okay you will be able to update the balance.
	 * 
	 * @param account
	 */
	public UpdateAccountJListDialog(List<Account> allAccounts) {
		allAccountsToView = allAccounts;
		setBounds(100, 100, 788, 487);
		setTitle("VIEW ALL EXISINT ACCOUNTS");
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		contentPanel.setLayout(new MigLayout("", "[grow]", "[grow]"));
		//sorts out by  bank prefix for readability 
		for (Account account : allAccounts) {
			try {
				accountSortedModel.add(account); 
			} catch (Exception e) {
				LOG.error(e.getMessage());
			}
		}	    
		{									
			listOfAccounts.setModel(accountSortedModel);			
			listOfAccounts.setFont(FIXEDFONT); 
			listOfAccounts.setSelectedIndex(0); //this selects the first element on the list, preventing the null error when the dialog first opens
			listOfAccounts.addKeyListener(new KeyAdapter() {
				@Override
				public void keyReleased(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						getClickAndEnterActions();
					}
				}
			});

			listOfAccounts.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						getClickAndEnterActions();
					}
				}
			});	
			JScrollPane scrollPane = new JScrollPane(listOfAccounts);
			// instead of this we had the scrollbar contentPanel.add(listOfAccounts, "cell 0 0,grow");
			contentPanel.add(scrollPane, "cell 0 0,grow");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						openAddBalanceDialog();
					}
				});
				{
					JButton btnPlaceAClosed = new JButton("Place a CLOSED status ");
					btnPlaceAClosed.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							addsClosedStatusToAccounts();
						}
					});
					buttonPane.add(btnPlaceAClosed);
				}
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						UpdateAccountJListDialog.this.dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	/**
	 * This helper method disables account updates that are closed and enables 
	 * account updates for others.
	 */
	public void getClickAndEnterActions() {
		try{
			if(listOfAccounts.getSelectedValue().getName().trim().length() <= 9){					
				openAddBalanceDialog(); //this takes care of account names that are less than 9 letters
			}else if (listOfAccounts.getSelectedValue().getName().substring(0, 9)
					.equalsIgnoreCase("ClosedOn-") && listOfAccounts.getSelectedValue().getName().trim().length() >= 9 ) {
				JOptionPane.showMessageDialog(null,
						"This account is now CLOSED. \n" + "Can not update"); //this is if the nine letters are ClosedOn-
			}else{
				openAddBalanceDialog();
			}			
		}catch(Exception e){
			LOG.error(e.getMessage());
		}
	}
	/**
	 * This method finds the account data that was selected in the list, then
	 * pass that data into a add balance dialog
	 */
	public void openAddBalanceDialog() {
		try {
			Account selectAccount = listOfAccounts.getSelectedValue();
			if(selectAccount == null){
				JOptionPane.showMessageDialog(null, "Please select and account");
			}
			
			if(selectAccount.getType().equalsIgnoreCase(Account.Types.CASH.getAccountType())){
				// pass this account to add balance dialog to start the dialog, and look up the current foreign rate first 
				ExchangeRateJListDialog dialog = new ExchangeRateJListDialog(selectAccount.getBank().getPrefix().substring(0, 3), selectAccount);
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setLocation(800, 50);;
				dialog.setVisible(true);
				//AddBalanceDialog addBalanceDialog = new AddBalanceDialog(selectAccount);
			}else{
				// pass this account to add balance dialog to start the dialog
				AddBalanceDialog addBalanceDialog = new AddBalanceDialog(selectAccount, null);
				addBalanceDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				addBalanceDialog.setVisible(true);	
			}
						
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}
	
	/**
	 * This method finds the account data that was selected in the list, then goes
	 * into the balance and account table and changes the account name to PREPEND the words
	 * ClosedOn dash and "the day it was closed" and the account name (readByAccountAndUpdate())
	 * Note: this method will leave the bank table alone because other accounts with 
	 * the same bank might be open so we don't need to place a bank close status
	 */
	public void addsClosedStatusToAccounts() {
		Account selectAccount = null;
		try {
			LocalDate date = LocalDate.now();
			selectAccount = listOfAccounts.getSelectedValue();
			MainFrame.balanceDao.readByAccountAndUpdate(selectAccount, date);
			//need to change the account table too so that the JList gets updated properly
			MainFrame.accountDao.readByAccountAndUpdate(selectAccount, date);
			UpdateAccountJListDialog.this.dispose();
			JOptionPane.showMessageDialog(null, "This account is now CLOSED. \n" +
			"You will see old balances updated with the CLOSED status");
			//TODO: find a better way to re-open the JList Dialog to show that the account has now have the closed status??
			try {
				UpdateAccountJListDialog dialog = new UpdateAccountJListDialog(MainFrame.accountDao.getAllAccounts());	
				dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				dialog.setVisible(true);
			} catch (Exception e1) {
				LOG.error(e1.getMessage());
			}
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
		}
	}

	
	/**
	 * Inner class to sort list in model
	 * In order to customize the code for this App, instead of just creating
	 * a plain treeSet constructor, I had to pass a class that implements Comparator and takes Account as objects.
	 * The code from the URL was comparing Strings, but I needed the model to hold Account objects
	 * so I could pass Account objects to the other dialogs. The SortByBankPrefixWithAccountObjects class
	 * compares the bank name prefix (String).
	 * @author used: http://www.java2s.com/Tutorials/Java/Swing/JList/Create_a_sorted_List_Model_for_JList_in_Java.htm
	 *
	 */
	public class SortedListModel extends AbstractListModel<Account>{

		SortedSet<Account> model;
		public SortedListModel() {
			model = new TreeSet(new SortByBankPrefixWithAccountObjects());
		}
		  
		@Override
		public int getSize() {
			return model.size();
		}

		@Override
		public Account getElementAt(int index) {
			return (Account)model.toArray()[index];
		}
		public void add(Object element) {
		    if (model.add((Account)element)) {
		      fireContentsChanged(this, 0, getSize());
		    }
		}
		//extra methods that don't really need to be here... but was from the URL, might use for future
		public void addAll(Object elements[]) {
		    Collection c = Arrays.asList(elements);
		    model.addAll(c);
		    fireContentsChanged(this, 0, getSize());
		  }

		  public void clear() {
		    model.clear();
		    fireContentsChanged(this, 0, getSize());
		  }

		  public boolean contains(Object element) {
		    return model.contains(element);
		  }

		  public Object firstElement() {
		    return model.first();
		  }

		  public Iterator iterator() {
		    return model.iterator();
		  }

		  public Object lastElement() {
		    return model.last();
		  }

		  public boolean removeElement(Object element) {
		    boolean removed = model.remove(element);
		    if (removed) {
		      fireContentsChanged(this, 0, getSize());
		    }
		    return removed;
		  }
	}
}

//NOTE: if I wanted to create a model based on the DefaultListModel class it would look something like this
//private DefaultListModel<Account> accountModel = new DefaultListModel<Account>();
//for (Account account : allAccounts) {
//	try {
//		accountModel.addElement(account);
//	} catch (Exception e) {
//		LOG.error(e.getMessage());
//	}
//}
//pass model into list so we can see the account to_string
//listOfAccounts = new JList<Account>(accountModel); this is what you would do with the defaultListModel
