package edu.uw.nan.account;

import edu.uw.ext.framework.account.Address;

@SuppressWarnings("serial")
public class AddressLaplace implements Address {


	/**
	 * Street address.
	 */
	private String streetAddress;
	/**
	 * The city.
	 */
	private String city;
	/**
	 * The state.
	 */
	private String state;
	/**
	 * The zip code.
	 */
	private String zipCode;
	/**
	 * Gets the city.
	 * @return the city.
	 */
	@Override
	public String getCity() {
		return this.city;
	}
	/**
	 * Gets the State.
	 * @return the state.
	 */
	@Override
	public String getState() {
		return this.state;
	}
	/**
	 * Get Street address.
	 * @return the street address.
	 */
	@Override
	public String getStreetAddress() {
		return this.streetAddress;
	}
	/**
	 * Get the zip code
	 * @return the zip code.
	 */
	@Override
	public String getZipCode() {
		return this.zipCode;
	}
	/**
	 * Sets the city.
	 * @param city - city to set.
	 */
	@Override
	public void setCity(final String city) {
		this.city = city == null ? "" : city;
	}
	/**
	 * Sets the state.
	 * @param state - the state to set.
	 */
	@Override
	public void setState(final String state) {
		this.state = state == null ? "" : state;
		
	}
	/**
	 * Sets the street address.
	 * @param streetAddress - the street address to set.
	 */
	@Override
	public void setStreetAddress(final String streetAddress) {
		this.streetAddress = streetAddress == null ? "" : streetAddress;
		
	}
	/**
	 * Sets the zip code.
	 * @param zipCode - the zip code to set.
	 */
	@Override
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode == null ? "" : zipCode;
		
	}
	
}
