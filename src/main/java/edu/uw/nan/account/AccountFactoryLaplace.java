package edu.uw.nan.account;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountFactory;

/**
 * @author Neil Nevitt
 * Factory for creation of accounts.
 */
public final class AccountFactoryLaplace implements AccountFactory {
	private final Logger logger = LoggerFactory.getLogger(AccountFactoryLaplace.class);
	
	public AccountFactoryLaplace() {
		
	}
	
	/**
	* Instantiates a new account instance.
	* @param accountName - the account name.
	* @param hashedPassword - the password hash
	* @param initialBalance - the balance.
	* @return Returns the newly instantiated account, or null if unable to instantiate the account.
	*/
	@Override
	public Account newAccount(String accountName, byte[] hashPassword, int intBalance) {
		
		AccountLaplace account = null;
		try {
			account = new AccountLaplace( accountName, hashPassword, intBalance);
			if ( logger.isInfoEnabled() ) {
				logger.info(String.format("Created account: %s with balance of %d.", accountName, intBalance));
				
			}
		} catch (AccountException ex) {
			final String msg = String.format("Account createion failed for %s due to a balance of %d.", accountName, intBalance);
			logger.warn(msg, ex);
		} 
		return account;
	}

}
