package edu.uw.nan.dao;

import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

public class DaoFactoryLaplace implements DaoFactory {

	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		AccountDao accountDao = new AccountDaoLaplace();
		return accountDao;
	}

}
