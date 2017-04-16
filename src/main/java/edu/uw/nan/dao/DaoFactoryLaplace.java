package edu.uw.nan.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * @author Neil Nevitt
 * Creates DAO objects.
 *
 */
public class DaoFactoryLaplace implements DaoFactory {
	/**
	 * Instantiates a new AccountDao object.
	 * @return a newly instantiated account DAO object
	 * @throws DaoFactoryException - if unable to instantiate the DAO object
	 * @see AccountDao
	 */
	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		AccountDao accountDao = new AccountDaoLaplace();
		return accountDao;
	}

}
