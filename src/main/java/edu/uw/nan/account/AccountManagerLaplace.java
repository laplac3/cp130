package edu.uw.nan.account;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.dao.AccountDao;


import edu.uw.nan.dao.FileAccountDaoLaplace;

/**
 * @author Neil Nevitt
 * Manages basic account operations; create, delete, authentication and persistence.
 */
public class AccountManagerLaplace implements AccountManager {
 
	private final Logger logger = LoggerFactory.getLogger(AccountManagerLaplace.class);
	
	private final String ENCODING = "ISO-8859-1";
	private final String ALGORITHIM = "SHA1";
	/**
	 * The account factory.
	 */
	private AccountFactory accountFactory;
	/**
	 * The data access object.
	 */
	private AccountDao dao;
	
	/**
	 * Constructor for the AccountManagerLaplace.
	 * @param dao - the data access.
	 */
	public AccountManagerLaplace(final AccountDao dao) {
		this.dao = dao;
		try ( ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
			accountFactory = appContext.getBean(AccountFactory.class);
		} catch ( final BeansException e) {
			logger.error("Unable to create account Manager", e);;
		}
	}
	

	/**
	 * Release any resources used by the AccountManager implementation. Once closed further operations on the AccountManager may fail.
	 * @throws AccountException - if error occurs accessing accounts.
	 */
	@Override
	public void close() throws AccountException {
		dao.close();
		dao = null;
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
	public synchronized Account createAccount(final String accountName, final String password, int balance) throws AccountException {
		
		if ( dao.getAccount(accountName) == null ) {
			final byte[] passwordHash = hashPassword(password);
			final Account account = accountFactory.newAccount(accountName, passwordHash, balance);
			account.registerAccountManager(this);
			persist(account);
			return account;
		} else {
			throw new AccountException("account name is already in use.");
		}
	}
	
	private byte[] hashPassword(String password) throws AccountException {
		
		try {
			MessageDigest mess = MessageDigest.getInstance(ALGORITHIM);
			mess.update(password.getBytes(ENCODING));
			return mess.digest();
		} catch ( final NoSuchAlgorithmException ex ) {
			throw new AccountException("Unable to find Algorithm.",ex);
		} catch ( final UnsupportedEncodingException e) {
			throw new AccountException(String.format("Unable to find character encoding.",ENCODING),e);
		}
	}

	/**
	 * Remove the account.
	 * @param accountName - the name of the account to remove
	 * @throws AccountException - if operation fails
	 */
	@Override
	public synchronized void deleteAccount(final String accountName ) throws AccountException {
		final Account account = dao.getAccount(accountName);
		if ( account != null ) {
			dao.deleteAccount(accountName);
		}
	}
	/**
	 * Lookup an account based on account name.
	 * @param accountName - the name of the desired account.
	 * @return the account if located otherwise null.
	 * @throws AccountException - if operation fails
	 */
	@Override
	public synchronized Account getAccount(final String accountName) throws AccountException {
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
	public synchronized void persist(final Account account) throws AccountException {
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
	public synchronized boolean validateLogin(final String accountName, final String password) throws AccountException {
		boolean valid = false;
		final Account account = getAccount(accountName);
		if (account != null ) {
			final byte[] passwordHash = hashPassword(password);
			valid = MessageDigest.isEqual(account.getPasswordHash(), passwordHash);
		}
		return valid;
	}

}
