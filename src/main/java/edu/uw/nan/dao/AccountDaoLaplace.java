package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
	
	public static final String OUTPUT_FILE_NAME = "%s/%s.txt";
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
	public void deleteAccount(String accountName ) throws AccountException {
		File f = new File(String.format(OUTPUT_FILE_NAME, ACCOUNTS_FOLDER, accountName) );
		if (f.exists() ) {
			f.delete();
		} else {
			logger.info("Unable to delete account "+ accountName);
			throw new AccountException( "Unable to delete account "+ accountName);
		} 

	}

	@Override
	public Account getAccount(String accountName) {
//		BeanFactory bean = new FileSystemXmlApplicationContext(APPLICATION_CONTEXT_FILE_NAME);
//		Account account = null;
//		
//		File file = new File(String.format(OUTPUT_FILE_NAME, ACCOUNTS_FOLDER, accountName ));
//		if ( file.exists() ) {
//			account = bean.getBean(Account.class);
//			try {
//				FileInputStream fis = new FileInputStream(file);
//				DataInputStream din = new DataInputStream(fis);
//				din.readUTF();
//				
//				}
//				
//		
//				
//			
//		}
		return null;
	}

	@Override
	public void reset() throws AccountException {
		File dir = new File(ACCOUNTS_FOLDER);
		if ( dir.exists() && dir.isDirectory() ) {
			for ( File f : dir.listFiles() ) {
				f.delete();
			}
			dir.delete();
		}

	}

	@Override
	public void setAccount(Account account) throws AccountException {
		
//		try {
//			FileOutputStream fos = new FileOutputStream(String.format(OUTPUT_FILE_NAME, account.getName()));
//			
//		}
//
//	}
//	
//	private void addEntry( DataOutputStream data, String name, String Value) throws IOException {
		
		
	}
	
	private static void write( final OutputStream out, final Account account ) throws AccountException {
		try { 
			final DataOutputStream dos = new DataOutputStream(out);
			dos.writeUTF(account.getName());
			dos.writeInt(account.getBalance());
			dos.writeUTF(account.getFullName());
			dos.writeUTF(account.getPhone());
			dos.writeUTF(account.getEmail());
			writeByteArray(dos,account.getPasswordHash());
			
		} catch ( IOException e) {
			
		}
	}
	
	private static void writeByteArray( DataOutputStream out, final byte[] bytes ) {
		final int length = bytes == null ? -1 : bytes.length;
		for ( int i = 0; i< length; i++ ) {
			try {
				out.writeByte(bytes[i]);
			} catch ( IOException e ) {
				logger.info("Unable to write hash", e);
			}
		}
	}

}
