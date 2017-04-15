package edu.uw.nan.account;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;

public class AccountFactoryLaplace implements AccountFactory {
	private final Logger logger = LoggerFactory.getLogger(AccountFactoryLaplace.class);
	@Override
	public Account newAccount(String accountName, byte[] hashPassword, int intBalance) {
		Preferences perfs = Preferences.systemNodeForPackage(edu.uw.ext.framework.account.Account.class);
		AccountLaplace account = null;
		try {
			if ( accountName.length() < perfs.getInt("minLength", 8) ) {
				logger.warn(String.format("Failed to create account for %s due the length of the name being to short.", accountName));
				return account;
			}
			
			if (intBalance < perfs.getInt("minAccountBalance", 0)) {
				logger.warn(String.format("Failed to create accountfor %s due to the initial balance = %d", accountName, intBalance ));
				return account;
			}
			
			account = new AccountLaplace();
			account.setName(accountName);
			account.setPasswordHash(hashPassword);
			account.setBalance(intBalance);
			
			logger.info(String.format("Created account: name=%s, balance=%d", accountName, intBalance));
		} catch (AccountException ex) {
			logger.error("Unable to create an account", ex);
		}
		return account;
	}

}
