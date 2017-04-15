package edu.uw.nan.account;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.*;
import edu.uw.ext.framework.order.Order;

@SuppressWarnings("serial")
public class AccountLaplace implements Account {

	private final Logger logger = LoggerFactory.getLogger(AccountLaplace.class);
	/**
	 * Name of the account holder.
	 */
	private String name;
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
	private AccountManager accountManager = null;
	
	public AccountLaplace(){
		
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) throws AccountException {
		Preferences perfs = Preferences.userNodeForPackage(Account.class);
		if ( name.length() < perfs.getInt("minLength", 8)) {
			throw new AccountException("Account name is to short, " + name );
		}
		this.name = name;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		Preferences perfs = Preferences.userNodeForPackage(Account.class);
		if ( balance < perfs.getInt("minAccountBalance", 0)) {
			
		}
		this.balance = balance;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		
		this.address = address;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard card) {
		this.creditCard = card;
	}

	public byte[] getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(byte[] passwordHash) {
		this.passwordHash = passwordHash;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public void registerAccountManager(AccountManager accountManager) {
		if ( this.accountManager == null) {
			this.accountManager = accountManager;
		} else {
			logger.info(String.format("AccountManager is already set to %s.", this.accountManager));
		}
		
	}


	@Override
	public void reflectOrder(Order order, int executionPrice) {
		if ( order.isBuyOrder()) {
			balance -= order.valueOfOrder(executionPrice);
		} else {
			balance += order.valueOfOrder(executionPrice);
		}
		
	}

	
	
}
