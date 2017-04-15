package edu.uw.nan.account;

import edu.uw.ext.framework.account.Address;

@SuppressWarnings("serial")
public class AddressLaplace implements Address {


	private String streetAddress;
	private String city;
	private String state;
	private String zipCode;
	
	@Override
	public String getCity() {
		return this.city;
	}

	@Override
	public String getState() {
		return this.state;
	}

	@Override
	public String getStreetAddress() {
		return this.streetAddress;
	}

	@Override
	public String getZipCode() {
		return this.zipCode;
	}

	@Override
	public void setCity(final String city) {
		this.city = city == null ? "" : city;
	}

	@Override
	public void setState(final String state) {
		this.state = state == null ? "" : state;
		
	}

	@Override
	public void setStreetAddress(final String streetAddress) {
		this.streetAddress = streetAddress == null ? "" : streetAddress;
		
	}

	@Override
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode == null ? "" : zipCode;
		
	}
	
}
