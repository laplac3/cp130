package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import org.springframework.beans.BeansException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.Account;
import edu.uw.ext.framework.account.AccountException;
import edu.uw.nan.dao.SerUtil;

public class AccountSer {
	
	private AccountSer() {
		
	}
	
	public static void write(FileOutputStream out, Account account) throws AccountException {
		try {
			final DataOutputStream dos = new DataOutputStream(out);
			dos.writeUTF(account.getName());
			SerUtil.writeByteArray(dos, account.getPasswordHash());
			dos.writeInt(account.getBalance());
			SerUtil.writeString(dos,account.getFullName());
			SerUtil.writeString(dos,account.getPhone());
			SerUtil.writeString(dos, account.getEmail());
			dos.flush();
		} catch (final IOException e ) {
			throw new AccountException("Failed to write account data", e);
		} 
		 
	}
	
	public static Account read( final InputStream in ) throws AccountException {
		final DataInputStream din = new DataInputStream(in);
		try ( ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
			final Account account = appContext.getBean(Account.class);
			account.setName(din.readUTF());
			account.setPasswordHash(SerUtil.readByteArray(din));
			account.setBalance(din.readInt());
			account.setFullName(SerUtil.readString(din));
			account.setPhone(SerUtil.readString(din));
			account.setEmail(SerUtil.readString(din));
			return account;
		} catch ( final BeansException ex ) {
			throw new AccountException("Unable to create account instance." , ex );
		} catch ( final IOException ex ) {
			throw new AccountException("Unable to read persisted account data.", ex);
		}
		
		
		
	}
	


}
