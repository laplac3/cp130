package edu.uw.nan.dao;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.ext.framework.dao.DaoFactory;
import edu.uw.ext.framework.dao.DaoFactoryException;

public class JsonDaoFactory implements DaoFactory {

	public JsonDaoFactory() {
		
	}
	@Override
	public AccountDao getAccountDao() throws DaoFactoryException {
		try {
			return new JsonAccountDao();
		} catch ( final AccountException e ) {
			throw new DaoFactoryException( "Failed to create new JsonAccountDao", e );
		}
	}

}
