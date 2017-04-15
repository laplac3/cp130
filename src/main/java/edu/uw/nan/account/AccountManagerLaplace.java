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
	
	@Override
	public void close() throws AccountException {
		dao.close();
		
	}

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

	@Override
	public void deleteAccount(final String accountName ) throws AccountException {
		dao.deleteAccount(accountName);
		
	}

	@Override
	public Account getAccount(final String accountName) throws AccountException {
		Account account = dao.getAccount(accountName);
		if ( account != null ) {
			account.registerAccountManager(this);
		}
		return account;
	}

	@Override
	public void persist(final Account account) throws AccountException {
		dao.setAccount(account);
		
	}

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
