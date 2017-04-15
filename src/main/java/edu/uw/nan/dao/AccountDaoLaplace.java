package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.dao.AccountDao;

public class AccountDaoLaplace implements AccountDao {
    private static final Logger logger =
            LoggerFactory.getLogger(AccountDaoLaplace.class);
	private static final  String ACCOUNTS_FOLDER = "accounts";
	private static final int BUFF_SIZE = 32*1024;
	private static final String encoding = "UTF-8";
	
	public static final String NAME ="name";
	public static final String BALANCE = "balance";
	public static final String FULL_NAME = "fullName";
	public static final String EMAIL = "email";
	public static final String PASSWORD_HASH = "passwordHash";
	
	public static final String STREET_ADDRESS = "streetAddress";
	public static final String CITY = "city";
	public static final String STATE = "state";
	public static final String ZIP_CODE = "zipCode";
	
	public static final String ISSUER ="issuer";
	public static final String TYPE = "type";
	public static final String HOLDER="holder";
	public static final String ACCOUNT_NUMBER ="accountNumber";
	public static final String EXPIRATION_DATE= "expirationDate";
	
	public static final String ACCOUNT = "account";
	public static final String ADDRESS = "address";
	public static final String CREDIT_CARD = "creditCard";
	public static final String ACCOUNT_NAME = "accountName";
	
	public static final String OUTPUT_FILE_NAME = "%s/%s.zip";
	public static final String APPLICATION_CONTEXT_FILE_NAME = "context.xml";
	
	public AccountDaoLaplace() {
		final File dir = new File(ACCOUNTS_FOLDER);
		if ( !dir.exists())
			dir.mkdirs();
	}
	@Override
	public void close() throws AccountException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAccount(String arg0) throws AccountException {
		// TODO Auto-generated method stub

	}

	@Override
	public Account getAccount(String accountName) {
		BeanFactory bean = new FileSystemXmlApplicationContext(APPLICATION_CONTEXT_FILE_NAME);
		Account account = null;
		
		File file = new File(String.format(OUTPUT_FILE_NAME, ACCOUNTS_FOLDER, accountName ));
		if ( file.exists() ) {
			account = bean.getBean(Account.class);
			try {
				FileInputStream fis = new FileInputStream(file);
				DataInputStream din = new DataInputStream(fis);
				din.readUTF();
				
				}
				
		
				
			
		}
		return null;
	}

	@Override
	public void reset() throws AccountException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAccount(Account account) throws AccountException {
		


	}

}
