package edu.uw.nan.dao;

import java.io.File;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.AccountManager;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;
import edu.uw.nan.account.AccountLaplace;
import edu.uw.nan.account.AccountManagerLaplace;
import edu.uw.nan.account.AddressLaplace;
import edu.uw.nan.account.CreditCardLaplace;


public final class JsonAccountDao implements AccountDao {

	private static final Logger logger = LoggerFactory.getLogger(JsonAccountDao.class);
	
	private static final String JSON_FILENAME = "%s.json";
	private static final File accountsDir = new File("target", "accounts");

	private final ObjectMapper mapper;
	
	public JsonAccountDao() throws AccountException {
		final SimpleModule module = new SimpleModule();
		module.addAbstractTypeMapping(Account.class, AccountLaplace.class);
		module.addAbstractTypeMapping(Address.class, AddressLaplace.class);
		module.addAbstractTypeMapping(CreditCard.class, CreditCardLaplace.class);
		mapper = new ObjectMapper();
		mapper.registerModule(module);
	}
	
	
	@Override
	public void close() throws AccountException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAccount(String accountName ) throws AccountException {
		
		String accountFileName = String.format(JSON_FILENAME, accountName);
		File file = new File(accountsDir, accountFileName);
		if( file.exists() && !file.delete()) {
			logger.warn(String.format("File deletion,", file.getAbsolutePath()  ));
		}
	}
	@Override
	public void reset() throws AccountException {
		deleteFile(accountsDir);
	}
	
	private void deleteFile(final File file) {
		if (file.exists()) {
			if (file.isDirectory() ) {
				final File[] files = file.listFiles();
				for ( File f : files ) {
					deleteFile(f);
				}
			
			}
		}
		if (!file.delete() ) {
			logger.warn(String.format("File deletion failed for, %s ", file.getAbsolutePath()));
		}
	}

	@Override
	public Account getAccount(String accountName) {
		Account account = null;
		String accountFileName = String.format(JSON_FILENAME, accountName);
		
		if ( accountsDir.exists() && accountsDir.isDirectory() ) {
			
			try {
				final File inf = new File(accountsDir, accountFileName);
				account = mapper.readValue(inf, Account.class);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.warn("Cannot read account from file for" + accountName,e);
			}
		}
		return account;
	}
	
	@Override
	public void setAccount(Account account ) throws AccountException {
		
		try {
			String accountFileName = String.format(JSON_FILENAME, account.getName());
			final File outFile = new File( accountsDir, accountFileName);
			
			if (!accountsDir.exists() ) {
				final boolean success = accountsDir.mkdirs();
				if (!success ) {
					throw new AccountException( String.format("Unable to create account directory, %s",
							accountsDir.getAbsolutePath()));
				}
			}
			if (outFile.exists() ) {
				boolean delete = outFile.delete();
				if ( !delete ) {
					logger.warn(String.format("Unable to delete file for %s", 
							accountsDir.getAbsolutePath()));
				}
			}
			
			mapper.writerWithDefaultPrettyPrinter().writeValue(outFile, account);
		} catch ( final IOException e ) {
			throw new AccountException("Unable to store account data", e); 
		}
	}

}
