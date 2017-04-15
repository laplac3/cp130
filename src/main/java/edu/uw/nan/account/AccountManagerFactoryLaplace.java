package edu.uw.nan.account;

import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.AccountManagerFactory;
import edu.uw.ext.framework.dao.AccountDao;

public class AccountManagerFactoryLaplace implements AccountManagerFactory {

	@Override
	public AccountManager newAccountManager(AccountDao dao) {
		AccountManager accountManager = new AccountManagerLaplace(dao);
		return accountManager;
	}
	

}
