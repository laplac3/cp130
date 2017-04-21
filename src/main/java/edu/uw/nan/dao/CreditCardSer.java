package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.CreditCard;

public class CreditCardSer {

	
	public static CreditCard read(FileInputStream in) throws AccountException {
		final DataInputStream din = new DataInputStream(in);
		try ( ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
			final CreditCard credit = appContext.getBean(CreditCard.class);
			credit.setAccountNumber(SerUtil.readString(din));
			credit.setExpirationDate(SerUtil.readString(din));
			credit.setHolder(SerUtil.readString(din));
			credit.setIssuer(SerUtil.readString(din));
			credit.setType(SerUtil.readString(din));
			return credit;
		} catch (IOException e) {
			throw new AccountException("Unable to set address for account.", e);
		}
		
	} 

	public static void write(FileOutputStream out, CreditCard creditCard) throws AccountException {
		
		try {
			final DataOutputStream dos = new DataOutputStream(out);
			SerUtil.writeString(dos, creditCard.getAccountNumber());
			SerUtil.writeString(dos, creditCard.getExpirationDate());
			SerUtil.writeString(dos, creditCard.getHolder());
			SerUtil.writeString(dos, creditCard.getIssuer());
			SerUtil.writeString(dos, creditCard.getType());
			dos.flush();
		} catch ( IOException e ) {
			throw new AccountException("Unable to write creditcard data.", e);
		}
	}

}