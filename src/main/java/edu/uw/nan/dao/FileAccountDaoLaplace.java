package edu.uw.nan.dao;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;
import edu.uw.ext.framework.account.CreditCard;
import edu.uw.ext.framework.dao.AccountDao;

public class FileAccountDaoLaplace implements AccountDao {
    private static final Logger logger =
            LoggerFactory.getLogger(FileAccountDaoLaplace.class);
	private static final String ACCOUNT_FILENAME = "account.dat";
	private static final String ADDRESS_FILENAME = "address.dat";
	private static final String CREDITCARD_FILENAME = "creditcard.dat";
	private static final File accountsDir = new File("target", "accounts");
	public static final String APPLICATION_CONTEXT_FILE_NAME = "context.xml";
	private static final String CREDITCARD_NAME = null;
	
	public FileAccountDaoLaplace() throws AccountException {
		 
	}
	@Override
	public void close() throws AccountException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAccount(String accountName ) throws AccountException {
		deleteFile(new File(accountsDir, accountName));

	}

	@Override
	public Account getAccount(String accountName) {
		FileInputStream in = null;
		Account account = null;
		final File accountDir = new File(accountsDir, accountName);
		
		if ( accountDir.exists() && accountDir.isDirectory() ) {
			
			try {
				File inFile = new File(accountDir,ACCOUNT_FILENAME);
				in = new FileInputStream(inFile);
				account = AccountSer.read(in);
				in.close();
				
				
				inFile = new File(accountDir, ADDRESS_FILENAME);
				if (inFile.exists()) {
					in = new FileInputStream(inFile);
					final Address address = AddressSer.read(in);
					in.close();
					account.setAddress(address);
				}
					

				inFile = new File(accountDir, CREDITCARD_FILENAME);
				if (inFile.exists()) {
					in = new FileInputStream(inFile);
					final CreditCard creditCard = CreditCardSer.read(in);
					in.close();
					account.setCreditCard(creditCard);
				
				}
				
				} catch ( final IOException e ) {
					logger.warn("Unable to access or read account data %s", accountName , e);
				} catch ( final AccountException aex ) {
					logger.warn("Unable to process account %s.", accountName, aex);
				} finally {
					if ( in != null ) {
						try {
							in.close();
						} catch ( IOException e ) {
							logger.warn("Attempt to close stream failed.", e );
						}
					}
				}
				
		
				
			
		}
		return account;
	}

	@Override
	public void reset() throws AccountException {
		
		deleteFile(accountsDir);

	}

	@Override
	public void setAccount(Account account) throws AccountException {
		FileOutputStream out = null;
		try {
			final File accountDir = new File( accountsDir, account.getName());
			
			final Address address = account.getAddress();
			final CreditCard creditCard = account.getCreditCard();
			
			deleteFile(accountDir);
			if (!accountDir.exists() ) {
				final boolean success = accountDir.mkdirs();
				if (!success ) {
					throw new AccountException( String.format("Unable to creat account directory, %s", account.getName()));
				}
			}
			
			File outFile = new File(accountDir, ACCOUNT_FILENAME);
			out = new FileOutputStream(outFile);
			AccountSer.write(out, account);
			out.close();
			
			if (address != null ) {
				outFile = new File(accountDir, ADDRESS_FILENAME);
				out = new FileOutputStream(outFile);
				AddressSer.write(out, address);
				out.close();
			}
			
			if ( creditCard != null ) {
				outFile = new File(accountDir, CREDITCARD_NAME);
				out = new FileOutputStream(outFile);
				CreditCardSer.write(out, creditCard);
				out.close();
			}
		} catch ( final IOException e ) {
			throw new AccountException("Unable to stroe account(s).", e);
		} finally {
			if ( out != null ) {
				try {
					out.close();
				} catch (IOException e ) {
					logger.warn("unable to close out stream.", e);
				
				}
			}
		}

		
		
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
			logger.warn("File deletion failed for " + file.getAbsolutePath());
		}
	}

}
