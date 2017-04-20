package edu.uw.nan.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

/**
 * @author Neil Nevitt
 * Creates DAO objects.
 *
 */
public class FileDaoFactoryLaplace implements DaoFactory {
	/**
	 * Instantiates a new AccountDao object.
	 * @return a newly instantiated account DAO object
	 * @throws DaoFactoryException - if unable to instantiate the DAO object
	 * @see AccountDao
	 */
	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		try {
			return new FileAccountDaoLaplace();
		} catch ( final AccountException e) {
			throw new DaoFactoryException("Instantiation of FileAccountDao failed.", e);
		}
	}

}
