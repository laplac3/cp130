package edu.uw.nan.account;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;

/**
 * @author Neil Nevitt.
 * Creates account managers.
 *
 */ 
public class AccountManagerFactoryLaplace implements AccountManagerFactory {
	/**
	 * Instantiates a new account manager instance.
	 * @param dao - the data access object to be used by the account manager
	 * @return a newly instantiated account manager
	 */
	@Override
	public AccountManager newAccountManager(AccountDao dao) {
		AccountManager accountManager = new AccountManagerLaplace(dao);
		return accountManager; 
	}
	

}
