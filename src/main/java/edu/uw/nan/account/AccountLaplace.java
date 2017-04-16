package edu.uw.nan.account;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.order.Order;

/**
 * @author Neil Nevit
 * A pure JavaBean representation of an account.
 */
@SuppressWarnings("serial")
public class AccountLaplace implements Account {

	private final Logger logger = LoggerFactory.getLogger(AccountLaplace.class);
	/**
	 * Name of the account holder.
	 */
	private String acctName;
	/**
	 * Account balance.
	 */
	private int balance;
	/**
	 * Full name of account holder.
	 */
	private String fullName;
	/**
	 * Address of account holder.
	 */
	private Address address;
	/**
	 * Phone number of account holder.
	 */
	private String phone;
	/**
	 * Email address for account holder.
	 */
	private String email;
	/**
	 * Credit card for account holder.
	 */
	private CreditCard creditCard;
	/**
	 * Hash of account holder's password.
	 */
	private byte[] passwordHash;
	/**
	 * Account Manager.
	 */
	private AccountManager accountManager = null;
	
	/**
	 * Account constructor.
	 */
	public AccountLaplace(){
		
	}
	/**
	 * Get the account name.
	 * @return the name of the account.
	 */
	public String getName() {
		return acctName;
	}
	/**
	 * Sets the account name. This operation is not generally used but is provided for JavaBean conformance.
	 * @param acctName - the value to be set for the account name
	 */
	public void setName(final String acctName) throws AccountException {
		Preferences perfs = Preferences.userNodeForPackage(Account.class);
		if ( acctName.length() < perfs.getInt("minLength", 8)) {
			throw new AccountException("Account name is to short, " + acctName );
		}
		this.acctName = acctName;
	}
	/**
	 * Gets the account balance, in cents.
	 * @return the current balance of the account.
	 */
	public int getBalance() {
		return balance;
	}
	/**
	 * Sets the account balance.
	 * @param balance - the value to set the balance to in cents
	 */
	public void setBalance(int balance) {
		Preferences perfs = Preferences.userNodeForPackage(Account.class);
		if ( balance < perfs.getInt("minAccountBalance", 0)) {
			
		}
		this.balance = balance;
	}
	/**
	 * Gets the full name of the account holder.
	 * @return the account holders full name.
	 */
	public String getFullName() {
		return fullName;
	}
	/**
	 * Sets the full name of the account holder.
	 * @param fullName - the account holders full name.
	 */
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	/**
	 * Gets the account address.
	 * @return the accounts address.
	 */
	public Address getAddress() {
		return address;
	}
	/**
	 * Sets the account address.
	 * @param address - the address for the account.
	 */
	public void setAddress(Address address) {
		
		this.address = address;
	}
	/**
	 * Gets the phone number.
	 * @return the phone number
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * Sets the account phone number.
	 * @param phone - value for the account phone number.
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * Gets the email address.
	 * @return the email address
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * Sets the account email address.
	 * @param email - the email address
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	/**
	 * Gets the account credit card.
	 * @return the credit card.
	 */
	public CreditCard getCreditCard() {
		return creditCard;
	}
	/**
	 * Sets the account credit card.
	 * @param card - the value to be set for the credit card
	 */
	public void setCreditCard(CreditCard card) {
		this.creditCard = card;
	}
	/**
	 * Gets the hashed password.
	 * @return the hashed password.
	 */
	public byte[] getPasswordHash() {
		return passwordHash;
	}
	/**
	 * Sets the hashed password.
	 * @param passwordHash - the value to be st for the password hash
	 */
	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}
	/**
	 * Getter for account manager.
	 * @return return the account manager.
	 */
	public AccountManager getAccountManager() {
		return accountManager;
	}
	/**
	 * Sets the account manager responsible for persisting/managing this account. 
	 * This may be invoked exactly once on any given account, any subsequent invocations should be ignored.
	 * The account manager member should not be serialized with implementing class object.
	 * 
	 */
	public void registerAccountManager(AccountManager m) {
		if ( this.accountManager == null) {
			this.accountManager = m;
		} else {
			logger.info(String.format("AccountManager is already set to %s.", this.accountManager));
		}
		
	}

	/**
	 * Incorporates the effect of an order in the balance.
	 * @param order - the order to be reflected in the account
	 * @param executionPrice - the price the order was executed at
	 */
	@Override
	public void reflectOrder(Order order, int executionPrice) {
		if ( order.isBuyOrder()) {
			balance -= order.valueOfOrder(executionPrice);
		} else {
			balance += order.valueOfOrder(executionPrice);
		}
		
	}

	
	
}
