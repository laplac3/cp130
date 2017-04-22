package edu.uw.nan.dao;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


import org.springframework.context.support.ClassPathXmlApplicationContext;


import edu.uw.ext.framework.account.AccountException;
import edu.uw.ext.framework.account.Address;


public class AddressSer {
	

	
	public static Address read(FileInputStream in) throws AccountException {
		final DataInputStream din = new DataInputStream(in);
		try ( ClassPathXmlApplicationContext appContext = new ClassPathXmlApplicationContext("context.xml")) {
			final Address address = appContext.getBean(Address.class);
			address.setCity(SerUtil.readString(din));
			address.setState(SerUtil.readString(din));
			address.setStreetAddress(SerUtil.readString(din));
			address.setZipCode(SerUtil.readString(din));
			return address;
		} catch (IOException e) {
			throw new AccountException("Unable to set address for account.", e);
		}
		 
	}

	public static void write(FileOutputStream out, Address address) throws AccountException {
		try {
			final DataOutputStream dos =  new DataOutputStream(out);
			SerUtil.writeString(dos, address.getCity());
			SerUtil.writeString(dos, address.getState());
			SerUtil.writeString(dos, address.getStreetAddress());
			SerUtil.writeString(dos, address.getZipCode());
			dos.flush();
		} catch (IOException ex ) {
			throw new AccountException("Failed to write address data.", ex);
		}
		
	}
 
}
