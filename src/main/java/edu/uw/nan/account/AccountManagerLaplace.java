package edu.uw.nan.account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.nan.dao.AccountDaoLaplace;
import edu.uw.nan.dao.DaoFactoryLaplace;

/**
 * @author Neil Nevitt
 * Manages basic account operations; create, delete, authentication and persistence.
 */
public class AccountManagerLaplace implements AccountManager {

	private final Logger logger = LoggerFactory.getLogger(AccountManagerLaplace.class);
	/**
	 * The account factory.
	 */
	private final AccountFactory accountFactory;
	/**
	 * The data access object.
	 */
	private final AccountDao dao;
	
	/**
	 * Constructor for the AccountManagerLaplace.
	 * @param dao - the data access.
	 */
	public AccountManagerLaplace(final AccountDao dao) {
		this.dao = dao;
		accountFactory = new AccountFactoryLaplace();
	}
	
	/**
	 * Constructor for the AccountManagerLaplace.
	 */
	public AccountManagerLaplace() {
		this.dao = new AccountDaoLaplace();
		accountFactory = new AccountFactoryLaplace();
	}
	/**
	 * Release any resources used by the AccountManager implementation. Once closed further operations on the AccountManager may fail.
	 * @throws AccountException - if error occurs accessing accounts.
	 */
	@Override
	public void close() throws AccountException {
		dao.close();
		
	}
	/**
	 * Creates an account. The creation process should include persisting the account and setting the account manager reference 
	 * (through the Account registerAccountManager operation).
	 * @param accountName - the name for account to add.
	 * @param password - the password used to gain access to the account
	 * @param balance - the initial balance of the account
	 * @return the newly created account
	 * @throws AccountException - if operation fails
	 */
	@Override
	public Account createAccount(final String accountName, final String password, int balance) throws AccountException {
		Account account = null;
		account = dao.getAccount(accountName);
		if ( account != null ) {
			throw new AccountException(String.format("Account %s already exists", accountName));
		} try {
			MessageDigest mess = MessageDigest.getInstance("SHA1");
			mess.update(password.getBytes());
			
			account= accountFactory.newAccount(accountName, mess.digest(), balance);
			account.registerAccountManager(this);
			dao.setAccount(account);
		} catch (NoSuchAlgorithmException ex ) {
			logger.error("Unable to create SHA1 hash for this password", ex);
		}
		return account;
	}
	/**
	 * Remove the account.
	 * @param accountName - the name of the account to remove
	 * @throws AccountException - if operation fails
	 */
	@Override
	public void deleteAccount(final String accountName ) throws AccountException {
		dao.deleteAccount(accountName);
		
	}
	/**
	 * Lookup an account based on account name.
	 * @param accountName - the name of the desired account.
	 * @return the account if located otherwise null.
	 * @throws AccountException - if operation fails
	 */
	@Override
	public Account getAccount(final String accountName) throws AccountException {
		Account account = dao.getAccount(accountName);
		if ( account != null ) {
			account.registerAccountManager(this);
		}
		return account;
	}
	/**
	 * Used to persist an account.
	 * @param account - the account to persist
	 * @throws AccountException - if operation fails
	 */
	@Override
	public void persist(final Account account) throws AccountException {
		dao.setAccount(account);
		
	}
	/**
	 * Check whether a login is valid. An account must exist with the account name and the password must match.
	 * @param accountName - name of account the password is to be validated for.
	 * @param password - password is to be validated
	 * @return true if password is valid for account identified by accountName
	 * @throws AccountException - if error occurs accessing accounts
	 */
	@Override
	public boolean validateLogin(final String accountName, final String password) throws AccountException {
		boolean isPasswordMatch = false;
		try {
			MessageDigest mess = MessageDigest.getInstance("SHA1");
			mess.update(password.getBytes());
			
			Account account = dao.getAccount(accountName);
			if ( account != null ) {
				isPasswordMatch = Arrays.equals(account.getPasswordHash(), mess.digest());
			}
		} catch ( NoSuchAlgorithmException ex ) {
			logger.error("Unable to create a message digest for this password", ex);
			throw new AccountException(ex.getMessage());
		}
		return isPasswordMatch;
	}

}
